package com.nhb.dtu.protocol;

public class CRC {

	public static int[] calculateCRC(int[] data, int length) {
		int[] temdata = new int[2]; // temdata[0]低位

		int xda, xdapoly;
		int i, j, xdabit;
		xda = 0xFFFF;
		xdapoly = 0xA001;
		for (i = 0; i < length; i++) {
			xda ^= data[i];
			for (j = 0; j < 8; j++) {
				xdabit = (int) (xda & 0x01);
				xda >>= 1;
				if (xdabit == 1)
					xda ^= xdapoly;
			}
		}

		temdata[0] = (int) (xda & 0xFF);
		temdata[1] = (int) (xda >> 8);
		return temdata;
	}

	public static boolean isValid(int[] buf) {
		if (buf.length < 5)
			return false; // 最小长度

		int UserDataLen = buf[2];
		if (buf.length != UserDataLen + 5)
			return false;

		int[] crc = calculateCRC(buf, UserDataLen + 3);
		if (buf[buf.length - 1] == crc[1] && buf[buf.length - 2] == crc[0])
			return true;
		else
			return false;
	}

	public static boolean isValid2(int[] buf) {
		int[] crc = calculateCRC(buf, buf.length - 2);
		if (buf[buf.length - 1] == crc[1] && buf[buf.length - 2] == crc[0])
			return true;
		else
			return false;
	}

	public static int CRC_XModem(byte[] bytes) {
		int crc = 0x00; // initial value
		int polynomial = 0x1021;
		for (int index = 0; index < bytes.length; index++) {
			byte b = bytes[index];
			for (int i = 0; i < 8; i++) {
				boolean bit = ((b >> (7 - i) & 1) == 1);
				boolean c15 = ((crc >> 15 & 1) == 1);
				crc <<= 1;
				if (c15 ^ bit)
					crc ^= polynomial;
			}
		}
		crc &= 0xffff;
		return crc;
	}
}
