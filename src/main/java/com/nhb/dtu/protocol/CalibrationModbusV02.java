package com.nhb.dtu.protocol;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.nhb.dtu.base.AbstractDevice;

public class CalibrationModbusV02 extends AbstractDevice {

	@Override
	public void buildWritingFrames() {
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
		String date = format.format(new Date());
		int[] data = new int[15];
		data[0] = 0x00;
		data[1] = 0x10;
		data[2] = 0x00;
		data[3] = 0x01;
		data[4] = 0x00;
		data[5] = 0x03;
		data[6] = 0x06;
		data[7] = Integer.parseInt(date.substring(0, 2), 16);
		data[8] = Integer.parseInt(date.substring(2, 4), 16);
		data[9] = Integer.parseInt(date.substring(4, 6), 16);
		data[10] = Integer.parseInt(date.substring(6, 8), 16);
		data[11] = Integer.parseInt(date.substring(8, 10), 16);
		data[12] = Integer.parseInt(date.substring(10, 12), 16);
		int[] crc = CRC.calculateCRC(data, 13);
		data[13] = crc[0];
		data[14] = crc[1];
		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	@Override
	public boolean analyzeFrame(byte[] frame) {
		return true;
	}

	@Override
	public void handleResult() {
		//
	}

}
