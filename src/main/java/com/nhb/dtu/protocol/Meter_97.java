package com.nhb.dtu.protocol;

import java.util.Date;
import java.util.UUID;

import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.config.ConfigBean;
import com.nhb.dtu.entity.DataElectricity;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.enums.ElectricityType;
import com.nhb.dtu.service.data.DataElectricityService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

public class Meter_97 extends AbstractDevice {
	private boolean rateFromDataBaseEnabled = ConfigBean.getRateFromDataBaseEnabled();
	private int[] address = new int[6]; // 0为最高位

	private double currentEnergy = 0; // 当前组合有功总电能

	private double Ubb = 1;// 电压变比

	private double Ibb = 1;// 电流变比

	public Meter_97(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		for (int i = 0; i < address.length; i++) {
			address[i] = Integer.parseInt(receiptMeter.getMeterNo().substring(2 * i, 2 * i + 2), 16);
		}
		buildWritingFrames();
	}

	@Override
	public void buildWritingFrames() {
		writingFrames.add(makeFrame(new int[] { 0x90, 0x10 }));
	}

	private byte[] makeFrame(int[] ident) { // 0为高位
		byte[] senddata = null;

		if (ident.length != 2)
			return senddata;

		int[] send = new int[14];

		send[0] = 0x68;
		send[1] = address[5];
		send[2] = address[4];
		send[3] = address[3];
		send[4] = address[2];
		send[5] = address[1];
		send[6] = address[0];
		send[7] = 0x68;
		send[8] = 0x01; // 请求读电能表数据
		send[9] = 0x02;
		send[10] = ident[1] + 0x33;
		send[11] = ident[0] + 0x33;
		send[12] = calculateCS(send, 12);
		send[13] = 0x16;

		senddata = new byte[14];
		for (int i = 0; i < 14; i++) {
			senddata[i] = (byte) send[i];
		}

		return senddata;
	}

	@Override
	public boolean analyzeFrame(byte[] frame) {
		int[] data = new int[frame.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = frame[i] & 0xFF;
		}

		int num = 0;
		while (num < data.length && data[num] != 0x68) {
			num++;
		}

		int[] data2 = new int[data.length - num];
		for (int i = 0; i < data2.length; i++) {
			data2[i] = data[i + num];
		}

		if (!isValid(data2)) {
			return false;
		}

		int dataLen = data2[9];
		int[] validData = new int[dataLen];
		for (int i = 0; i < dataLen; i++) {
			if (data2[i + 10] < 0x33)
				validData[i] = data2[i + 10] + 0xFF - 0x33;
			else
				validData[i] = data2[i + 10] - 0x33;
		}
		// 正向有功电能
		if (validData[1] == 0x90 && validData[0] == 0x10) {
			currentEnergy = Double.parseDouble(parseData(validData));
		}
		return true;
	}

	private boolean isValid(int[] data) {
		if (data.length < 12)
			return false;

		if (data[0] != 0x68 || data[7] != 0x68 || data[data.length - 1] != 0x16)
			return false;

		int identLength = data[9];
		if ((identLength + 12) != data.length)
			return false;

		int cs = calculateCS(data, data.length - 2);
		if (cs == data[data.length - 2])
			return true;
		else
			return false;
	}

	private int calculateCS(int[] data, int length) {
		int sum = 0;

		for (int i = 0; i < length; i++) {
			sum += data[i];
		}

		sum = sum % 256;

		return sum;
	}

	private String parseData(int[] validData) {
		StringBuilder builder = new StringBuilder();
		for (int i = 5; i > 1; i--) {
			if (validData[i] < 0x10) {
				builder.append('0');
			}
			builder.append(Integer.toHexString(validData[i]));
			if (i == 3) {
				builder.append('.');
			}
		}
		return builder.toString();
	}

	@Override
	public void handleResult() {
		ReceiptDevice receiptDevice = getReceiptDeviceService().findByMeterIdAndCircuitNo(receiptMeter.getId(), "1");
		if (rateFromDataBaseEnabled) {
			if (receiptDevice == null) {
				return;
			}
			if (receiptDevice.getVoltageRatio() != null) {
				Ubb = receiptDevice.getVoltageRatio();
			}
			if (receiptDevice.getCurrentRatio() != null) {
				Ibb = receiptDevice.getCurrentRatio();
			}
		}
		resultMutiplyRate();
		Date now = new Date();
		DataElectricity dataElectricity = new DataElectricity();
		dataElectricity.setId(UUID.randomUUID());
		dataElectricity.setDeviceId(
				receiptCollector.getCollectorNo() + receiptMeter.getMeterNo() + receiptDevice.getCircuitNo());
		dataElectricity.setReadTime(now);
		dataElectricity.setElectricityType(ElectricityType.ENERGY.name());
		dataElectricity.setKwh(currentEnergy);
		getDataElectricityService().save(dataElectricity);
	}

	private void resultMutiplyRate() {
		currentEnergy *= Ubb * Ibb;
	}

	private ReceiptDeviceService getReceiptDeviceService() {
		return SpringContextHolder.getBean("receiptDeviceService");
	}

	private DataElectricityService getDataElectricityService() {
		return SpringContextHolder.getBean("dataElectricityService");
	}
}
