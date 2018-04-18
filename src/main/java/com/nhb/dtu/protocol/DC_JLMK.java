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

/**
 * 
 * @ClassName: DC_JLMK
 * @Description: 直流六路计量模块
 * @author XS guo
 * @date 2018年1月5日 下午3:01:41
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DC_JLMK extends AbstractDevice {

	public DC_JLMK(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		buildWritingFrames();
	}

	private double[] voltage;

	private double[] current;

	private double[] kw;

	private double[] kwh;

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
		data[6] = 0x44;
		// cid2
		data[7] = 0x38;
		data[8] = 0x35;
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

		voltage = new double[6];
		current = new double[6];
		kw = new double[6];
		kwh = new double[6];

		int index = 0;
		for (int i = 0; i < 6; i++) {
			voltage[i] = bytes2int(data[index + 25], data[index + 26], data[index + 27], data[index + 28],
					data[index + 29], data[index + 30], data[index + 31], data[index + 32]) / 100.0;
			current[i] = bytes2int(data[index + 33], data[index + 34], data[index + 35], data[index + 36],
					data[index + 37], data[index + 38], data[index + 39], data[index + 40]) / 100.0;
			kw[i] = bytes2int(data[index + 41], data[index + 42], data[index + 43], data[index + 44], data[index + 45],
					data[index + 46], data[index + 47], data[index + 48]) / 100.0;
			kwh[i] = bytes2int(data[index + 49], data[index + 50], data[index + 51], data[index + 52], data[index + 53],
					data[index + 54], data[index + 55], data[index + 56]) / 100.0;
			index += 32;
		}

		return true;
	}

	public static int bytes2int(int data0, int data1, int data2, int data3, int data4, int data5, int data6,
			int data7) {
		int i = 0;
		i |= Integer.parseInt((char) data0 + "" + (char) data1, 16);
		i <<= 8;
		i |= Integer.parseInt((char) data2 + "" + (char) data3, 16);
		i <<= 8;
		i |= Integer.parseInt((char) data4 + "" + (char) data5, 16);
		i <<= 8;
		i |= Integer.parseInt((char) data6 + "" + (char) data7, 16);
		return i;
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
			dataElectricity.setVoltage(voltage[i]);
			dataElectricity.setCurrent(current[i]);
			dataElectricity.setKw(kw[i]);
			dataElectricity.setKwh(kwh[i]);
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
