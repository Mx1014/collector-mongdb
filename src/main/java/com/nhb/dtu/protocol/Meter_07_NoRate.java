package com.nhb.dtu.protocol;

import java.util.Date;
import java.util.List;
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

public class Meter_07_NoRate extends AbstractDevice {
	private boolean rateFromDataBaseEnabled = ConfigBean.getRateFromDataBaseEnabled();
	private int[] address = new int[6]; // 0为最高位

	private double ratioV = 1;
	private double ratioA = 1;
	private double currentEnergy = 0; // 当前组合有功总电能
	private double voltA = 0;
	private double voltB = 0;
	private double voltC = 0;
	private double currentA = 0;
	private double currentB = 0;
	private double currentC = 0;
	private double pow = 0;
	private double powA = 0;
	private double powB = 0;
	private double powC = 0;
	private double kvar = 0;
	private double kvarA = 0;
	private double kvarB = 0;
	private double kvarC = 0;
	private double kva = 0;
	private double kvaA = 0;
	private double kvaB = 0;
	private double kvaC = 0;
	private double factor = 0;
	private double factorA = 0;
	private double factorB = 0;
	private double factorC = 0;

	public Meter_07_NoRate(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		for (int i = 0; i < address.length; i++) {
			address[i] = Integer.parseInt(receiptMeter.getMeterNo().substring(2 * i, 2 * i + 2), 16);
		}
		buildWritingFrames();
	}

	@Override
	public void buildWritingFrames() {
		writingFrames.add(makeFrame(new int[] { 0, 0, 0, 0 }));
		writingFrames.add(makeFrame(new int[] { 2, 1, 0xff, 0 }));
		writingFrames.add(makeFrame(new int[] { 2, 2, 0xff, 0 }));
		writingFrames.add(makeFrame(new int[] { 2, 3, 0xff, 0 }));
		writingFrames.add(makeFrame(new int[] { 2, 4, 0xff, 0 }));
		writingFrames.add(makeFrame(new int[] { 2, 5, 0xff, 0 }));
		writingFrames.add(makeFrame(new int[] { 2, 6, 0xff, 0 }));

	}

	private byte[] makeFrame(int[] ident) { // 0为高位
		byte[] senddata = null;

		if (ident.length != 4)
			return senddata;

		int[] send = new int[16];

		send[0] = 0x68;
		send[1] = address[5];
		send[2] = address[4];
		send[3] = address[3];
		send[4] = address[2];
		send[5] = address[1];
		send[6] = address[0];
		send[7] = 0x68;
		send[8] = 0x11; // 请求读电能表数据
		send[9] = 0x04;
		send[10] = ident[3] + 0x33;
		send[11] = ident[2] + 0x33;
		send[12] = ident[1] + 0x33;
		send[13] = ident[0] + 0x33;
		send[14] = calculateCS(send, 14);
		send[15] = 0x16;

		senddata = new byte[16];
		for (int i = 0; i < 16; i++) {
			senddata[i] = (byte) send[i];
		}

		return senddata;
	}

	@Override
	public boolean analyzeFrame(byte[] frame) {
		if (readingFrames.size() != 7) {
			return false;
		}

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

		if (data2[8] != 0x91) {
			return true;
		}
		int dataLen = data2[9];
		int[] validData = new int[dataLen];
		for (int i = 0; i < dataLen; i++) {
			if (data2[i + 10] < 0x33)
				validData[i] = data2[i + 10] + 0x01 + 0xFF - 0x33;
			else
				validData[i] = data2[i + 10] - 0x33;
		}
		// 组合有功总电能
		if (validData[3] == 0x00 && validData[2] == 0x00 && validData[1] == 0x00 && validData[0] == 0x00) {
			currentEnergy = Double.parseDouble(parseData(validData));
		} else if (validData[3] == 0x02 && validData[2] == 0x01 && validData[1] == 0xff && validData[0] == 0x00) {
			voltA = (hexToTenData(validData[4]) + hexToTenData(validData[5]) * 100) / 10.0;
			voltB = (hexToTenData(validData[6]) + hexToTenData(validData[7]) * 100) / 10.0;
			voltC = (hexToTenData(validData[8]) + hexToTenData(validData[9]) * 100) / 10.0;
		} else if (validData[3] == 0x02 && validData[2] == 0x02 && validData[1] == 0xff && validData[0] == 0x00) {
			currentA = (hexToTenData(validData[4]) + hexToTenData(validData[5]) * 100
					+ hexToTenData(validData[6]) * 10000) / 1000.0;
			currentB = (hexToTenData(validData[7]) + hexToTenData(validData[8]) * 100
					+ hexToTenData(validData[9]) * 10000) / 1000.0;
			currentC = (hexToTenData(validData[10]) + hexToTenData(validData[11]) * 100
					+ hexToTenData(validData[12]) * 10000) / 1000.0;
		} else if (validData[3] == 0x02 && validData[2] == 0x03 && validData[1] == 0xff && validData[0] == 0x00) {
			pow = (hexToTenData(validData[4]) + hexToTenData(validData[5]) * 100 + hexToTenData(validData[6]) * 10000)
					/ 10000.0;
			powA = (hexToTenData(validData[7]) + hexToTenData(validData[8]) * 100 + hexToTenData(validData[9]) * 10000)
					/ 10000.0;
			powB = (hexToTenData(validData[10]) + hexToTenData(validData[11]) * 100
					+ hexToTenData(validData[12]) * 10000) / 10000.0;
			powC = (hexToTenData(validData[13]) + hexToTenData(validData[14]) * 100
					+ hexToTenData(validData[15]) * 10000) / 10000.0;
		} else if (validData[3] == 0x02 && validData[2] == 0x04 && validData[1] == 0xff && validData[0] == 0x00) {
			kvar = (hexToTenData(validData[4]) + hexToTenData(validData[5]) * 100 + hexToTenData(validData[6]) * 10000)
					/ 10000.0;
			kvarA = (hexToTenData(validData[7]) + hexToTenData(validData[8]) * 100 + hexToTenData(validData[9]) * 10000)
					/ 10000.0;
			kvarB = (hexToTenData(validData[10]) + hexToTenData(validData[11]) * 100
					+ hexToTenData(validData[12]) * 10000) / 10000.0;
			kvarC = (hexToTenData(validData[13]) + hexToTenData(validData[14]) * 100
					+ hexToTenData(validData[15]) * 10000) / 10000.0;
		} else if (validData[3] == 0x02 && validData[2] == 0x05 && validData[1] == 0xff && validData[0] == 0x00) {
			kva = (hexToTenData(validData[4]) + hexToTenData(validData[5]) * 100 + hexToTenData(validData[6]) * 10000)
					/ 10000.0;
			kvaA = (hexToTenData(validData[7]) + hexToTenData(validData[8]) * 100 + hexToTenData(validData[9]) * 10000)
					/ 10000.0;
			kvaB = (hexToTenData(validData[10]) + hexToTenData(validData[11]) * 100
					+ hexToTenData(validData[12]) * 10000) / 10000.0;
			kvaC = (hexToTenData(validData[13]) + hexToTenData(validData[14]) * 100
					+ hexToTenData(validData[15]) * 10000) / 10000.0;
		} else if (validData[3] == 0x02 && validData[2] == 0x06 && validData[1] == 0xff && validData[0] == 0x00) {
			factor = (hexToTenData(validData[4]) + hexToTenData(validData[5]) * 100) / 1000.0;
			factorA = (hexToTenData(validData[6]) + hexToTenData(validData[7]) * 100) / 1000.0;
			factorB = (hexToTenData(validData[8]) + hexToTenData(validData[9]) * 100) / 1000.0;
			factorC = (hexToTenData(validData[10]) + hexToTenData(validData[11]) * 100) / 1000.0;
		}
		return true;
	}

	private int hexToTenData(int data) {
		return Integer.parseInt(Integer.toHexString(data));
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
		for (int i = 7; i > 3; i--) {
			if (validData[i] < 0x10) {
				builder.append('0');
			}
			builder.append(Integer.toHexString(validData[i]));
			if (i == 5) {
				builder.append('.');
			}
		}
		return builder.toString();
	}

	@Override
	public void handleResult() {
		Date now = new Date();
		List<ReceiptDevice> receiptDeviceList = getReceiptDeviceService().findByMeterId(receiptMeter.getId());
		for (ReceiptDevice receiptDevice : receiptDeviceList) {
			if (rateFromDataBaseEnabled) {
				if (receiptDevice.getVoltageRatio() != null && receiptDevice.getVoltageRatio() != 0) {
					ratioV = receiptDevice.getVoltageRatio();
				}
				if (receiptDevice.getCurrentRatio() != null && receiptDevice.getCurrentRatio() != 0) {
					ratioA = receiptDevice.getCurrentRatio();
				}
			}
			resultMultipyRate();
			DataElectricity dataElectricity = new DataElectricity();
			dataElectricity.setId(UUID.randomUUID());
			dataElectricity.setDeviceId(receiptDevice.getId());
			dataElectricity.setReadTime(now);
			dataElectricity.setElectricityType(ElectricityType.AC_THREE.name());
			dataElectricity.setKwh(currentEnergy);
			dataElectricity.setCurrentA(currentA);
			dataElectricity.setCurrentB(currentB);
			dataElectricity.setCurrentC(currentC);
			dataElectricity.setVoltageA(voltA);
			dataElectricity.setVoltageB(voltB);
			dataElectricity.setVoltageC(voltC);
			dataElectricity.setPowerFactor(factor);
			dataElectricity.setPowerFactorA(factorA);
			dataElectricity.setPowerFactorB(factorB);
			dataElectricity.setPowerFactorC(factorC);
			dataElectricity.setKw(pow);
			dataElectricity.setKwA(powA);
			dataElectricity.setKwB(powB);
			dataElectricity.setKwC(powC);
			dataElectricity.setKva(kva);
			dataElectricity.setKvaA(kvaA);
			dataElectricity.setKvaB(kvaB);
			dataElectricity.setKvaC(kvaC);
			dataElectricity.setKvar(kvar);
			dataElectricity.setKvarA(kvarA);
			dataElectricity.setKvarB(kvarB);
			dataElectricity.setKvarC(kvarC);
			getDataElectricityService().save(dataElectricity);
		}
	}

	private void resultMultipyRate() {
		currentEnergy *= ratioV * ratioA;
		voltA *= ratioV;
		voltB *= ratioV;
		voltC *= ratioV;
		currentA *= ratioA;
		currentB *= ratioA;
		currentC *= ratioA;
		pow *= ratioV * ratioA;
		powA *= ratioV * ratioA;
		powB *= ratioV * ratioA;
		powC *= ratioV * ratioA;
		kvar *= ratioV * ratioA;
		kvarA *= ratioV * ratioA;
		kvarB *= ratioV * ratioA;
		kvarC *= ratioV * ratioA;
		kva *= ratioV * ratioA;
		kvaA *= ratioV * ratioA;
		kvaB *= ratioV * ratioA;
		kvaC *= ratioV * ratioA;
	}

	private ReceiptDeviceService getReceiptDeviceService() {
		return SpringContextHolder.getBean("receiptDeviceService");
	}

	private DataElectricityService getDataElectricityService() {
		return SpringContextHolder.getBean("dataElectricityService");
	}

}
