package com.nhb.dtu.protocol;

import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptMeter;

/**
 * 
 * @ClassName: Meter4_02
 * @Description: 三相四路表 抽象类
 * @author XS guo
 * @date 2017年8月30日 上午9:17:31
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public abstract class Meter4_02 extends AbstractDevice {

	private int index;

	protected String time;
	protected double frequency;
	protected double voltageA;
	protected double voltageB;
	protected double voltageC;
	protected double voltageAB;
	protected double voltageBC;
	protected double voltageAC;
	protected double oilCommonKwh;
	protected double oilCommonKwhForward;
	protected double oilCommonKwhReverse;
	protected double oilCommonkvarh1;
	protected double oilCommonkvarh2;
	protected double[] currentA = new double[4];
	protected double[] currentB = new double[4];
	protected double[] currentC = new double[4];
	protected double[] kva = new double[4];
	protected double[] kvaA = new double[4];
	protected double[] kvaB = new double[4];
	protected double[] kvaC = new double[4];
	protected double[] kw = new double[4];
	protected double[] kwA = new double[4];
	protected double[] kwB = new double[4];
	protected double[] kwC = new double[4];
	protected double[] kvar = new double[4];
	protected double[] kvarA = new double[4];
	protected double[] kvarB = new double[4];
	protected double[] kvarC = new double[4];
	protected double[] kwh = new double[4];
	protected double[] kwhForward = new double[4];
	protected double[] kwhReverse = new double[4];
	protected double[] kvarh1 = new double[4];
	protected double[] kvarh2 = new double[4];
	protected double[] powerFactor = new double[4];
	protected double[] powerFactorA = new double[4];
	protected double[] powerFactorB = new double[4];
	protected double[] powerFactorC = new double[4];

	protected double[] voltageSP = new double[12];
	protected double[] currentSP = new double[12];
	protected double[] kvaSP = new double[12];
	protected double[] kwSP = new double[12];
	protected double[] kvarSP = new double[12];
	protected double[] powerFactorSP = new double[12];
	protected double[] kwhSP = new double[12];
	protected double[] kwhForwardSP = new double[12];
	protected double[] kwhReverseSP = new double[12];
	protected double[] kvarh1SP = new double[12];
	protected double[] kvarh2SP = new double[12];

	protected double Ubb = 1;
	protected double Ibb = 1;

	public Meter4_02(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		buildWritingFrames();
	}

	@Override
	public void buildWritingFrames() {
		makeFrame1();
		makeFrame2();
		makeFrame3();
		makeFrame4();
		makeFrameRate();
		index = 0;
	}

	private void makeFrameRate() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x00;
		data[3] = 0x06;
		data[4] = 0x00;
		data[5] = 0x02;
		int[] crc = CRC.calculateCRC(data, 6);
		data[6] = crc[0];
		data[7] = crc[1];
		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	private void makeFrame1() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x10;
		data[3] = 0x00;
		data[4] = 0x00;
		data[5] = 0x42;
		int[] crc = CRC.calculateCRC(data, 6);
		data[6] = crc[0];
		data[7] = crc[1];
		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	private void makeFrame2() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x10;
		data[3] = 0x42;
		data[4] = 0x00;
		data[5] = 0x47;
		int[] crc = CRC.calculateCRC(data, 6);
		data[6] = crc[0];
		data[7] = crc[1];
		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	private void makeFrame3() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x50;
		data[3] = 0x00;
		data[4] = 0x00;
		data[5] = 0x3C;
		int[] crc = CRC.calculateCRC(data, 6);
		data[6] = crc[0];
		data[7] = crc[1];
		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	private void makeFrame4() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x50;
		data[3] = 0x3C;
		data[4] = 0x00;
		data[5] = 0x3C;
		int[] crc = CRC.calculateCRC(data, 6);
		data[6] = crc[0];
		data[7] = crc[1];
		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	@Override
	public boolean analyzeFrame(byte[] frame) {
		if (readingFrames.size() != 5) {
			return false;
		}
		int[] data = new int[frame.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = frame[i] & 0xFF;
		}
		if (!CRC.isValid(data)) {
			return false;
		}
		if (index == 0) {
			analyzeFrame1(data);
		}
		if (index == 1) {
			analyzeFrame2(data);
		}
		if (index == 2) {
			analyzeFrame3(data);
		}
		if (index == 3) {
			analyzeFrame4(data);
		}
		if (index == 4) {
			analyzeFrame5(data);
		}
		index++;
		return true;
	}

	private void analyzeFrame5(int[] data) {
		Ubb = data[3] * 256 + data[4];
		Ibb = data[5] * 256 + data[6];
	}

	private void analyzeFrame1(int[] data) {
		time = "20";
		for (int i = 5; i < 10; i++) {
			if (data[i] < 0x10)
				time += "0" + Integer.toHexString(data[i]);
			else
				time += Integer.toHexString(data[i]);
		}
		frequency = (data[11] * 256 + data[12]) / 100.0;
		voltageA = (data[13] * 256 + data[14]) / 10.0;
		voltageB = (data[15] * 256 + data[16]) / 10.0;
		voltageC = (data[17] * 256 + data[18]) / 10.0;
		for (int i = 0; i < 4; i++) {
			voltageSP[3 * i] = voltageA;
			voltageSP[3 * i + 1] = voltageB;
			voltageSP[3 * i + 2] = voltageC;
		}
		for (int i = 0; i < 2; i++) {
			currentA[i] = (data[58 * i + 19] * 256 + data[58 * i + 20]) / 100.0;
			currentB[i] = (data[58 * i + 21] * 256 + data[58 * i + 22]) / 100.0;
			currentC[i] = (data[58 * i + 23] * 256 + data[58 * i + 24]) / 100.0;
			kva[i] = (data[58 * i + 25] * 256 + data[58 * i + 26]) / 100.0;
			kvaA[i] = (data[58 * i + 27] * 256 + data[58 * i + 28]) / 100.0;
			kvaB[i] = (data[58 * i + 29] * 256 + data[58 * i + 30]) / 100.0;
			kvaC[i] = (data[58 * i + 31] * 256 + data[58 * i + 32]) / 100.0;
			kw[i] = (data[58 * i + 33] * 256 + data[58 * i + 34]) / 100.0;
			kwA[i] = (data[58 * i + 35] * 256 + data[58 * i + 36]) / 100.0;
			kwB[i] = (data[58 * i + 37] * 256 + data[58 * i + 38]) / 100.0;
			kwC[i] = (data[58 * i + 39] * 256 + data[58 * i + 40]) / 100.0;
			kvar[i] = (data[58 * i + 41] * 256 + data[58 * i + 42]) / 100.0;
			kvarA[i] = (data[58 * i + 43] * 256 + data[58 * i + 44]) / 100.0;
			kvarB[i] = (data[58 * i + 45] * 256 + data[58 * i + 46]) / 100.0;
			kvarC[i] = (data[58 * i + 47] * 256 + data[58 * i + 48]) / 100.0;
			kwh[i] = (data[58 * i + 49] * 256 * 256 * 256 + data[58 * i + 50] * 256 * 256 + data[58 * i + 51] * 256
					+ data[58 * i + 52]) / 100.0;
			kwhForward[i] = (data[58 * i + 53] * 256 * 256 * 256 + data[58 * i + 54] * 256 * 256
					+ data[58 * i + 55] * 256 + data[58 * i + 56]) / 100.0;
			kwhReverse[i] = (data[58 * i + 57] * 256 * 256 * 256 + data[58 * i + 58] * 256 * 256
					+ data[58 * i + 59] * 256 + data[58 * i + 60]) / 100.0;
			kvarh1[i] = (data[58 * i + 61] * 256 * 256 * 256 + data[58 * i + 62] * 256 * 256 + data[58 * i + 63] * 256
					+ data[58 * i + 64]) / 100.0;
			kvarh2[i] = (data[58 * i + 65] * 256 * 256 * 256 + data[58 * i + 66] * 256 * 256 + data[58 * i + 67] * 256
					+ data[58 * i + 68]) / 100.0;
			powerFactor[i] = (data[58 * i + 69] * 256 + data[58 * i + 70]) / 100.0;
			powerFactorA[i] = (data[58 * i + 71] * 256 + data[58 * i + 72]) / 100.0;
			powerFactorB[i] = (data[58 * i + 73] * 256 + data[58 * i + 74]) / 100.0;
			powerFactorC[i] = (data[58 * i + 75] * 256 + data[58 * i + 76]) / 100.0;

			currentSP[3 * i] = currentA[i];
			currentSP[3 * i + 1] = currentB[i];
			currentSP[3 * i + 2] = currentC[i];
			kvaSP[3 * i] = kvaA[i];
			kvaSP[3 * i + 1] = kvaB[i];
			kvaSP[3 * i + 2] = kvaC[i];
			kwSP[3 * i] = kwA[i];
			kwSP[3 * i + 1] = kwB[i];
			kwSP[3 * i + 2] = kwC[i];
			kvarSP[3 * i] = kvarA[i];
			kvarSP[3 * i + 1] = kvarB[i];
			kvarSP[3 * i + 2] = kvarC[i];
			powerFactorSP[3 * i] = powerFactorA[i];
			powerFactorSP[3 * i + 1] = powerFactorB[i];
			powerFactorSP[3 * i + 2] = powerFactorC[i];
		}
	}

	private void analyzeFrame2(int[] data) {
		for (int i = 0; i < 2; i++) {
			currentA[i + 2] = (data[58 * i + 3] * 256 + data[58 * i + 4]) / 100.0;
			currentB[i + 2] = (data[58 * i + 5] * 256 + data[58 * i + 6]) / 100.0;
			currentC[i + 2] = (data[58 * i + 7] * 256 + data[58 * i + 8]) / 100.0;
			kva[i + 2] = (data[58 * i + 9] * 256 + data[58 * i + 10]) / 100.0;
			kvaA[i + 2] = (data[58 * i + 11] * 256 + data[58 * i + 12]) / 100.0;
			kvaB[i + 2] = (data[58 * i + 13] * 256 + data[58 * i + 14]) / 100.0;
			kvaC[i + 2] = (data[58 * i + 15] * 256 + data[58 * i + 16]) / 100.0;
			kw[i + 2] = (data[58 * i + 17] * 256 + data[58 * i + 18]) / 100.0;
			kwA[i + 2] = (data[58 * i + 19] * 256 + data[58 * i + 20]) / 100.0;
			kwB[i + 2] = (data[58 * i + 21] * 256 + data[58 * i + 22]) / 100.0;
			kwC[i + 2] = (data[58 * i + 23] * 256 + data[58 * i + 24]) / 100.0;
			kvar[i + 2] = (data[58 * i + 25] * 256 + data[58 * i + 26]) / 100.0;
			kvarA[i + 2] = (data[58 * i + 27] * 256 + data[58 * i + 28]) / 100.0;
			kvarB[i + 2] = (data[58 * i + 29] * 256 + data[58 * i + 30]) / 100.0;
			kvarC[i + 2] = (data[58 * i + 31] * 256 + data[58 * i + 32]) / 100.0;
			kwh[i + 2] = (data[58 * i + 33] * 256 * 256 * 256 + data[58 * i + 34] * 256 * 256 + data[58 * i + 35] * 256
					+ data[58 * i + 36]) / 100.0;
			kwhForward[i + 2] = (data[58 * i + 37] * 256 * 256 * 256 + data[58 * i + 38] * 256 * 256
					+ data[58 * i + 39] * 256 + data[58 * i + 40]) / 100.0;
			kwhReverse[i + 2] = (data[58 * i + 41] * 256 * 256 * 256 + data[58 * i + 42] * 256 * 256
					+ data[58 * i + 43] * 256 + data[58 * i + 44]) / 100.0;
			kvarh1[i + 2] = (data[58 * i + 45] * 256 * 256 * 256 + data[58 * i + 46] * 256 * 256
					+ data[58 * i + 47] * 256 + data[58 * i + 48]) / 100.0;
			kvarh2[i + 2] = (data[58 * i + 49] * 256 * 256 * 256 + data[58 * i + 50] * 256 * 256
					+ data[58 * i + 51] * 256 + data[58 * i + 52]) / 100.0;
			powerFactor[i + 2] = (data[58 * i + 53] * 256 + data[58 * i + 54]) / 100.0;
			powerFactorA[i + 2] = (data[58 * i + 55] * 256 + data[58 * i + 56]) / 100.0;
			powerFactorB[i + 2] = (data[58 * i + 57] * 256 + data[58 * i + 58]) / 100.0;
			powerFactorC[i + 2] = (data[58 * i + 59] * 256 + data[58 * i + 60]) / 100.0;
			currentSP[3 * i + 6] = currentA[i + 2];
			currentSP[3 * i + 7] = currentB[i + 2];
			currentSP[3 * i + 8] = currentC[i + 2];
			kvaSP[3 * i + 6] = kvaA[i + 2];
			kvaSP[3 * i + 7] = kvaB[i + 2];
			kvaSP[3 * i + 8] = kvaC[i + 2];
			kwSP[3 * i + 6] = kwA[i + 2];
			kwSP[3 * i + 7] = kwB[i + 2];
			kwSP[3 * i + 8] = kwC[i + 2];
			kvarSP[3 * i + 6] = kvarA[i + 2];
			kvarSP[3 * i + 7] = kvarB[i + 2];
			kvarSP[3 * i + 8] = kvarC[i + 2];
			powerFactorSP[3 * i + 6] = powerFactorA[i + 2];
			powerFactorSP[3 * i + 7] = powerFactorB[i + 2];
			powerFactorSP[3 * i + 8] = powerFactorC[i + 2];
		}
		voltageAB = (data[119] * 256 + data[120]) / 10.0;
		voltageBC = (data[121] * 256 + data[122]) / 10.0;
		voltageAC = (data[123] * 256 + data[124]) / 10.0;
		oilCommonKwh = (data[125] * 256 * 256 * 256L + data[126] * 256 * 256 + data[127] * 256 + data[128]) / 100.0;
		oilCommonKwhForward = (data[129] * 256 * 256 * 256L + data[130] * 256 * 256 + data[131] * 256 + data[132])
				/ 100.0;
		oilCommonKwhReverse = (data[133] * 256 * 256 * 256L + data[134] * 256 * 256 + data[135] * 256 + data[136])
				/ 100.0;
		oilCommonkvarh1 = (data[137] * 256 * 256 * 256L + data[138] * 256 * 256 + data[139] * 256 + data[140]) / 100.0;
		oilCommonkvarh1 = (data[141] * 256 * 256 * 256L + data[142] * 256 * 256 + data[143] * 256 + data[144]) / 100.0;
	}

	private void analyzeFrame3(int[] data) {
		for (int i = 0; i < 6; i++) {
			kwhSP[i] = (data[20 * i + 3] * 256 * 256 * 256 + data[20 * i + 4] * 256 * 256 + data[20 * i + 5] * 256
					+ data[20 * i + 6]) / 100.0;
			kwhForwardSP[i] = (data[20 * i + 7] * 256 * 256 * 256 + data[20 * i + 8] * 256 * 256
					+ data[20 * i + 9] * 256 + data[20 * i + 10]) / 100.0;
			kwhReverseSP[i] = (data[20 * i + 11] * 256 * 256 * 256 + data[20 * i + 12] * 256 * 256
					+ data[20 * i + 13] * 256 + data[20 * i + 14]) / 100.0;
			kvarh1SP[i] = (data[20 * i + 15] * 256 * 256 * 256 + data[20 * i + 16] * 256 * 256 + data[20 * i + 17] * 256
					+ data[20 * i + 18]) / 100.0;
			kvarh2SP[i] = (data[20 * i + 19] * 256 * 256 * 256 + data[20 * i + 20] * 256 * 256 + data[20 * i + 21] * 256
					+ data[20 * i + 22]) / 100.0;
		}
	}

	private void analyzeFrame4(int[] data) {
		for (int i = 0; i < 6; i++) {
			kwhSP[i + 6] = (data[20 * i + 3] * 256 * 256 * 256 + data[20 * i + 4] * 256 * 256 + data[20 * i + 5] * 256
					+ data[20 * i + 6]) / 100.0;
			kwhForwardSP[i + 6] = (data[20 * i + 7] * 256 * 256 * 256 + data[20 * i + 8] * 256 * 256
					+ data[20 * i + 9] * 256 + data[20 * i + 10]) / 100.0;
			kwhReverseSP[i + 6] = (data[20 * i + 11] * 256 * 256 * 256 + data[20 * i + 12] * 256 * 256
					+ data[20 * i + 13] * 256 + data[20 * i + 14]) / 100.0;
			kvarh1SP[i + 6] = (data[20 * i + 15] * 256 * 256 * 256 + data[20 * i + 16] * 256 * 256
					+ data[20 * i + 17] * 256 + data[20 * i + 18]) / 100.0;
			kvarh2SP[i + 6] = (data[20 * i + 19] * 256 * 256 * 256 + data[20 * i + 20] * 256 * 256
					+ data[20 * i + 21] * 256 + data[20 * i + 22]) / 100.0;
		}
	}

}
