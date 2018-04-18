package com.nhb.dtu.protocol;

public class Test {

	public static int bytes2int2(int[] data) {
		int i = 0;
		i |= Integer.parseInt((char) data[0] + "" + (char) data[1], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[2] + "" + (char) data[3], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[4] + "" + (char) data[5], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[6] + "" + (char) data[7], 16);
		return i;
	}

	public static int bytes2int(byte[] data) {
		int i = 0;
		i |= Integer.parseInt((char) data[0] + "" + (char) data[1], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[2] + "" + (char) data[3], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[4] + "" + (char) data[5], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[6] + "" + (char) data[7], 16);
		return i;
	}

	public static long bytes2long(byte[] data) {
		long i = 0;
		i |= Integer.parseInt((char) data[0] + "" + (char) data[1], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[2] + "" + (char) data[3], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[4] + "" + (char) data[5], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[6] + "" + (char) data[7], 16);
		return i;
	}

	public static float bytes2float(byte[] data) {
		int i = 0;
		i |= Integer.parseInt((char) data[6] + "" + (char) data[7], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[4] + "" + (char) data[5], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[2] + "" + (char) data[3], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[0] + "" + (char) data[1], 16);
		return Float.intBitsToFloat(i);
	}

	public static double bytes2double(byte[] data) {
		long i = 0;
		i |= Integer.parseInt((char) data[14] + "" + (char) data[15], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[12] + "" + (char) data[13], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[10] + "" + (char) data[11], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[8] + "" + (char) data[9], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[6] + "" + (char) data[7], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[4] + "" + (char) data[5], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[2] + "" + (char) data[3], 16);
		i <<= 8;
		i |= Integer.parseInt((char) data[0] + "" + (char) data[1], 16);
		return Double.longBitsToDouble(i);
	}

	private static double[] voltage;

	private static double[] current;

	private static double[] kw;

	private static double[] kwh;

	public static void main(String[] args) {

		byte[] data = { 0x7e, 0x31, 0x30, 0x30, 0x31, 0x32, 0x44, 0x30, 0x30, 0x38, 0x30, 0x43, 0x43, 0x30, 0x31, 0x20,
				0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x30, 0x36, 0x30, 0x30, 0x30, 0x30, 0x31, 0x32, 0x35, 0x43,
				0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
				0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x31, 0x32, 0x35, 0x43, 0x30, 0x30,
				0x30, 0x30, 0x30, 0x33, 0x42, 0x37, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x32, 0x43, 0x30, 0x30, 0x30,
				0x30, 0x30, 0x39, 0x42, 0x41, 0x30, 0x30, 0x30, 0x30, 0x31, 0x32, 0x35, 0x43, 0x30, 0x30, 0x30, 0x30,
				0x30, 0x34, 0x31, 0x42, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x33, 0x31, 0x30, 0x30, 0x30, 0x30, 0x30,
				0x41, 0x37, 0x43, 0x30, 0x30, 0x30, 0x30, 0x31, 0x32, 0x35, 0x43, 0x30, 0x30, 0x30, 0x30, 0x30, 0x33,
				0x44, 0x42, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x32, 0x45, 0x30, 0x30, 0x30, 0x30, 0x30, 0x41, 0x31,
				0x30, 0x30, 0x30, 0x30, 0x30, 0x31, 0x32, 0x35, 0x44, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
				0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
				0x30, 0x30, 0x30, 0x31, 0x32, 0x35, 0x43, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
				0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x44, 0x36, 0x31,
				0x42, 0x0d, 0x16 };

		analyzeFrame(data);
	}

	public static boolean analyzeFrame(byte[] frame) {
		int[] data = new int[frame.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = frame[i] & 0xFF;
		}
		// 解析 RTN 为 00 ，返回数据成功
		String rtn = (char) data[7] + "" + (char) data[8];
		if (!rtn.equals("00")) {
			return false;
		}

		voltage = new double[6];
		current = new double[6];
		kw = new double[6];
		kwh = new double[6];

		int index = 0;
		for (int i = 0; i < 6; i++) {
			voltage[i] = bytes2int222(data[index + 25], data[index + 26], data[index + 27], data[index + 28],
					data[index + 29], data[index + 30], data[index + 31], data[index + 32]) / 100.0;
			current[i] = bytes2int222(data[index + 33], data[index + 34], data[index + 35], data[index + 36],
					data[index + 37], data[index + 38], data[index + 39], data[index + 40]) / 100.0;
			kw[i] = bytes2int222(data[index + 41], data[index + 42], data[index + 43], data[index + 44],
					data[index + 45], data[index + 46], data[index + 47], data[index + 48]) / 100.0;
			kwh[i] = bytes2int222(data[index + 49], data[index + 50], data[index + 51], data[index + 52],
					data[index + 53], data[index + 54], data[index + 55], data[index + 56]) / 100.0;
			index += 32;

			System.out.println("voltage" + voltage[i]);
			System.out.println("current" + current[i]);
			System.out.println("kw" + kw[i]);
			System.out.println("kwh" + kwh[i]);
		}
		return true;

	}

	public static long bytes2int222(int data0, int data1, int data2, int data3, int data4, int data5, int data6,
			int data7) {
		return (Integer.parseInt((char) data0 + "" + (char) data1 + "" + (char) data2 + "" + (char) data3 + ""
				+ (char) data4 + "" + (char) data5 + "" + (char) data6 + "" + (char) data7, 16));
	}

	public static long bytes2int(int data0, int data1, int data2, int data3, int data4, int data5, int data6,
			int data7) {
		long i = 0;
		i |= Integer.parseInt((char) data0 + "" + (char) data1, 16);
		i <<= 8;
		i |= Integer.parseInt((char) data2 + "" + (char) data3, 16);
		i <<= 8;
		i |= Integer.parseInt((char) data4 + "" + (char) data5, 16);
		i <<= 8;
		i |= Integer.parseInt((char) data6 + "" + (char) data7, 16);
		return i;
	}
}