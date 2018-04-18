package com.nhb.dtu.protocol;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.entity.DataSwitch;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.entity.SwitchStatus;
import com.nhb.dtu.enums.SwitchStatusEnum;
import com.nhb.dtu.service.data.DataSwitchService;
import com.nhb.dtu.service.data.SwitchStatusService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

public class AC_JLMK_STATUS extends AbstractDevice {

	public AC_JLMK_STATUS(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		buildWritingFrames();
	}

	private String status;

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
		data[7] = 0x39;
		data[8] = 0x33;
		// length 校验
		Integer lenId = Integer.parseInt("02", 16);
		int[] lchlSum = Verify.lchlSum(lenId);
		data[9] = lchlSum[0] & 0xFF;
		data[10] = lchlSum[1] & 0xFF;
		data[11] = lchlSum[2] & 0xFF;
		data[12] = lchlSum[3] & 0xFF;
		// data info
		data[13] = 0x44;
		data[14] = 0x31;
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
		// TODO length、lchsum检验
		// 解析 RTN 为 00 ，返回数据成功
		String rtn = (char) data[7] + "" + (char) data[8];
		if (!rtn.equals("00")) {
			return false;
		}
		String readStatus = (char) data[13] + "" + (char) data[14];
		if (readStatus.equals("01")) {
			status = SwitchStatusEnum.ON.getKey();
		} else if (readStatus.equals("00")) {
			status = SwitchStatusEnum.OFF.getKey();
		}
		return true;

	}

	@Override
	public void handleResult() {
		List<ReceiptDevice> devices = getReceiptDeviceService().findByMeterId(receiptMeter.getId());
		for(ReceiptDevice device : devices) {
			Date nowTime = new Date();
			DataSwitch dataSwitch = new DataSwitch();
			dataSwitch.setId(UUID.randomUUID());
			dataSwitch.setStatus(status);
			dataSwitch.setReadTime(nowTime);
			dataSwitch.setDeviceId(device.getId());
			getDataSwitchService().save(dataSwitch);
			
			SwitchStatus switchStatus = getSwitchStatusService().findById(device.getId());
			
			if (null == switchStatus) {
				switchStatus = new SwitchStatus();
				switchStatus.setDeviceId(device.getId());
			}
			switchStatus.setReadTime(nowTime);
			switchStatus.setStatus(status);
			getSwitchStatusService().save(switchStatus);
			
		}
	}

	private ReceiptDeviceService getReceiptDeviceService() {
		return SpringContextHolder.getBean("receiptDeviceService");
	}

	private DataSwitchService getDataSwitchService() {
		return SpringContextHolder.getBean("dataSwitchService");
	}

	private SwitchStatusService getSwitchStatusService() {
		return SpringContextHolder.getBean("switchStatusService");
	}

}
