package com.nhb.dtu.protocol;

import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;

public class RecloseCount extends AbstractDevice {

	public RecloseCount(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter, ReceiptDevice receiptDevice) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		this.receiptDevice = receiptDevice;
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
		senddata[1] = 0x06;
		senddata[2] = 0x10;
		senddata[3] = 0x02;
		senddata[4] = 0x00;
		senddata[5] = 0x00;
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
		return true;
	}

	@Override
	public void handleResult() {

	}

}
