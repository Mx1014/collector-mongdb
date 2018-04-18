package com.nhb.dtu.protocol;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.entity.DataElectricity;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.service.data.DataElectricityService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

public class AC_JLMK extends AbstractDevice {

	public AC_JLMK(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		buildWritingFrames();
	}

	private double[] voltageAB;

	private double[] voltageBC;

	private double[] voltageCA;

	private double[] voltageA;

	private double[] voltageB;

	private double[] voltageC;

	private double[] currentA;

	private double[] currentB;

	private double[] currentC;

	// private double[] zeroCurrent;

	private double[] powerFactor;

	private double[] frequency;

	private double[] kw;

	private double[] kwA;

	private double[] kwB;

	private double[] kwC;

	private double[] kvar;

	private double[] kvarA;

	private double[] kvarB;

	private double[] kvarC;

	// 总有功电能
	private double[] kwh;

	// 正向有功电能
	private double[] kwhForward;

	// 反向有功电能
	private double[] kwhReverse;

	@Override
	public void buildWritingFrames() {

		int[] data = new int[20];
		// 起始位置SOI 1位
		data[0] = 0x7E;
		// 通讯版本号 2位
		data[1] = 0x31;
		data[2] = 0x30;
		// 表地址 2位
		String meterNo = Integer.toHexString(Integer.parseInt(receiptMeter.getMeterNo())).toUpperCase();
		if (meterNo.length() < 2) {
			meterNo = "0" + meterNo;
		}
		char[] meterChar = meterNo.toCharArray();
		data[3] = (byte) meterChar[0] & 0xFF;
		data[4] = (byte) meterChar[1] & 0xFF;
		// cid1
		data[5] = 0x32;
		data[6] = 0x43;
		// cid2
		data[7] = 0x34;
		data[8] = 0x31;
		// length 校验
		Integer lenId = Integer.parseInt("02", 16);
		int[] lchlSum = Verify.lchlSum(lenId);
		data[9] = lchlSum[0] & 0xFF;
		data[10] = lchlSum[1] & 0xFF;
		data[11] = lchlSum[2] & 0xFF;
		data[12] = lchlSum[3] & 0xFF;
		// data info
		data[13] = 0x46;
		data[14] = 0x46;
		// CHKSUM 校验和码
		int[] chkSum = Verify.chkSum(data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7] + data[8]
				+ data[9] + data[10] + data[11] + data[12] + data[13] + data[14]);
		data[15] = chkSum[0];
		data[16] = chkSum[1];
		data[17] = chkSum[2];
		data[18] = chkSum[3];

		// 结束码 1位
		data[19] = 0x0D;

		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);

	}

	@Override
	public boolean analyzeFrame(byte[] frame) {
		int[] data = new int[frame.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = frame[i] & 0xFF;
		}
		// 解析 RTN 为 00 ，返回数据成功
		String rtn = (char) data[7] + "" + (char) data[8];
		if (!rtn.equals("00")) {
			return false;
		}

		// 从 15 16 是 数据
		voltageAB = new double[4];

		voltageBC = new double[4];

		voltageCA = new double[4];

		voltageA = new double[4];

		voltageB = new double[4];

		voltageC = new double[4];

		currentA = new double[4];

		currentB = new double[4];

		currentC = new double[4];

		// zeroCurrent = new double[4];

		powerFactor = new double[4];

		frequency = new double[4];

		kw = new double[4];

		kwA = new double[4];

		kwB = new double[4];

		kwC = new double[4];

		kvar = new double[4];

		kvarA = new double[4];

		kvarB = new double[4];

		kvarC = new double[4];

		// 总有功电能
		kwh = new double[4];

		// 正向有功电能
		kwhForward = new double[4];

		// 反向有功电能
		kwhReverse = new double[4];

		int index = 0;
		for (int i = 0; i < 4; i++) {
			voltageAB[i] = getDouble(frame, 15 + index + 2);
			voltageBC[i] = getDouble(frame, 23 + index + 2);
			voltageCA[i] = getDouble(frame, 31 + index + 2);
			voltageA[i] = getDouble(frame, 39 + index + 2);
			voltageB[i] = getDouble(frame, 47 + index + 2);
			voltageC[i] = getDouble(frame, 55 + index + 2);
			currentA[i] = getDouble(frame, 63 + index + 2);
			currentB[i] = getDouble(frame, 71 + index + 2);
			currentC[i] = getDouble(frame, 79 + index + 2);
			powerFactor[i] = getDouble(frame, 95 + index + 2);
			frequency[i] = getDouble(frame, 103 + index + 2);
			kw[i] = getDouble(frame, index + 2 + 113);
			kwA[i] = getDouble(frame, index + 2 + 121);
			kwB[i] = getDouble(frame, index + 2 + 129);
			kwC[i] = getDouble(frame, index + 2 + 137);
			kvar[i] = getDouble(frame, index + 2 + 145);
			kvarA[i] = getDouble(frame, index + 2 + 153);
			kvarB[i] = getDouble(frame, index + 2 + 161);
			kvarC[i] = getDouble(frame, index + 2 + 169);
			// 总有功电能
			kwh[i] = getDouble(frame, index + 2 + 177);
			// 正向有功电能
			kwhForward[i] = getDouble(frame, index + 2 + 193);
			// 反向有功电能
			kwhReverse[i] = getDouble(frame, index + 2 + 217);
			index += 210;
		}

		return true;
	}

	// 从byte数组的index处的连续8个字节获得一个double
	public static double getDouble(byte[] arr, int index) {
		return (getLong(arr, index));
	}

	// 从byte数组的index处的连续8个字节获得一个long
	public static double getLong(byte[] arr, int index) {
		return Float.intBitsToFloat((Integer.parseInt(((char) arr[index + 6]) + "" + ((char) arr[index + 7]), 16) << 24)
								+ (Integer.parseInt(((char) arr[index + 4]) + "" + ((char) arr[index + 5]), 16) << 16)
								+ (Integer.parseInt(((char) arr[index + 2]) + "" + ((char) arr[index + 3]), 16) << 8)
								+ (Integer.parseInt(((char) arr[index + 0]) + "" + ((char) arr[index + 1]), 16)));
	}

	@Override
	public void handleResult() {
		List<ReceiptDevice> devices = getReceiptDeviceService().findByMeterId(receiptMeter.getId());
		DataElectricity dataElectricity = null;
		Date nowTime = new Date();
		for (int i = 0; i < devices.size(); i++) {
			dataElectricity = new DataElectricity();
			dataElectricity.setId(UUID.randomUUID());
			dataElectricity.setDeviceId(devices.get(i).getId());
			dataElectricity.setReadTime(nowTime);
			dataElectricity.setVoltageAB(voltageAB[i]);
			dataElectricity.setVoltageBC(voltageBC[i]);
			dataElectricity.setVoltageCA(voltageCA[i]);
			dataElectricity.setVoltageA(voltageA[i]);
			dataElectricity.setVoltageB(voltageB[i]);
			dataElectricity.setVoltageC(voltageC[i]);
			dataElectricity.setCurrentA(currentA[i]);
			dataElectricity.setCurrentB(currentB[i]);
			dataElectricity.setCurrentC(currentC[i]);
			dataElectricity.setPowerFactor(powerFactor[i]);
			dataElectricity.setFrequency(frequency[i]);
			dataElectricity.setKw(kw[i]);
			dataElectricity.setKwA(kwA[i]);
			dataElectricity.setKwB(kwB[i]);
			dataElectricity.setKwC(kwC[i]);
			dataElectricity.setKvar(kvar[i]);
			dataElectricity.setKvarA(kvarA[i]);
			dataElectricity.setKvarB(kvarB[i]);
			dataElectricity.setKvarC(kvarC[i]);
			dataElectricity.setKwh(kwh[i]);
			dataElectricity.setKwhForward(kwhForward[i]);
			dataElectricity.setKwhReverse(kwhReverse[i]);
			getDataElectricityService().save(dataElectricity);
		}

	}

	private DataElectricityService getDataElectricityService() {
		return SpringContextHolder.getBean(DataElectricityService.class);
	}

	private ReceiptDeviceService getReceiptDeviceService() {
		return SpringContextHolder.getBean(ReceiptDeviceService.class);
	}

}
