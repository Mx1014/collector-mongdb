package com.nhb.dtu.protocol;

import java.util.Date;
import java.util.UUID;

import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.entity.DataTemperature;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.service.data.DataTemperatureDService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

public class Temperature extends AbstractDevice {

	Double tempA = (double) 0;
	Double tempB = (double) 0;
	Double tempC = (double) 0;

	public Temperature(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		buildWritingFrames();
	}

	@Override
	public void buildWritingFrames() {
		writingFrames.add(makeFrame());
	}

	private byte[] makeFrame() {
		int[] senddata = new int[8];
		int[] crc = new int[2];
		senddata[0] = Integer.parseInt(receiptMeter.getMeterNo());
		senddata[1] = 0x03;
		senddata[2] = 0x00;
		senddata[3] = 0x00;
		senddata[4] = 0x00;
		senddata[5] = 0x06;

		crc = CRC.calculateCRC(senddata, 6);
		senddata[6] = crc[0];
		senddata[7] = crc[1];

		byte[] frame = new byte[8];
		for (int i = 0; i < 8; i++)
			frame[i] = (byte) senddata[i];
		return frame;
	}

	@Override
	public boolean analyzeFrame(byte[] frame) {
		int[] data = new int[frame.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = frame[i] & 0xFF;
		}

		if (data[2] != 0x0C) {
			return false;
		}

		tempA = (double) (data[6] - 0x23);
		tempB = (double) (data[8] - 0x23);
		tempC = (double) (data[10] - 0x23);

		return true;
	}

	@Override
	public void handleResult() {
		ReceiptDevice receiptDevice = getReceiptDeviceService().findByMeterIdAndCircuitNo(receiptMeter.getId(), "1");
		DataTemperature dataTemperature = new DataTemperature();
		Date nowTime = new Date();
		dataTemperature.setId(UUID.randomUUID());
		dataTemperature.setDeviceId(receiptDevice.getId());
		dataTemperature.setReadTime(nowTime);
		dataTemperature.setTemperatureA(tempA);
		dataTemperature.setTemperatureB(tempB);
		dataTemperature.setTemperatureC(tempC);
		getDataTemperatureDService().save(dataTemperature);

	}

	private ReceiptDeviceService getReceiptDeviceService() {
		return SpringContextHolder.getBean("receiptDeviceService");
	}

	private DataTemperatureDService getDataTemperatureDService() {
		return SpringContextHolder.getBean("dataTemperatureDService");
	}

}
