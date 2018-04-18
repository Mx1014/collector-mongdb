package com.nhb.dtu.protocol;

import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;
import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.entity.SwitchStatus;
import com.nhb.dtu.enums.SwitchStatusEnum;
import com.nhb.dtu.feign.RemoteRtnService;
import com.nhb.dtu.service.data.SwitchStatusService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

/**
 * 
 * @ClassName: Meter_DTSY_SwitchOn
 * @Description: 预付费电表合闸指令
 * @author XS guo
 * @date 2017年8月4日 下午1:38:13
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class Meter_DTSY_SwitchOn extends AbstractDevice {

	private boolean result;

	private RemoteRtnService getRemoteRtnService() {
		return SpringContextHolder.getBean("remoteRtnService");
	}

	private String status;

	public Meter_DTSY_SwitchOn(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter,
			ReceiptDevice receiptDevice) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		this.receiptDevice = receiptDevice;
		buildWritingFrames();
	}

	@Override
	public void buildWritingFrames() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x05;
		data[2] = 0x00;
		data[3] = 0x00;
		data[4] = 0xff;
		data[5] = 0x00;
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
		int[] data = new int[frame.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = frame[i] & 0xFF;
		}

		if (data[4] != 0xff) {
			return false;
		}
		result = true;
		status = SwitchStatusEnum.ON.getKey();
		return true;
	}

	@Override
	public void handleResult() {
		SwitchStatus switchStatus = getSwitchStatusService().findById(receiptDevice.getId());
		Date nowTime = new Date();
		if (null == switchStatus) {
			switchStatus = new SwitchStatus();
			switchStatus.setDeviceId(receiptDevice.getId());
		}
		switchStatus.setReadTime(nowTime);
		switchStatus.setStatus(status);
		getSwitchStatusService().save(switchStatus);

		Map<String, Object> params = Maps.newHashMap();
		params.put("deviceId", receiptDevice.getId());
		params.put("name", receiptDevice.getName());
		params.put("result", result);
		params.put("type", SwitchStatusEnum.ON.getKey());
		getRemoteRtnService().remoteRtn(params);
	}

	private SwitchStatusService getSwitchStatusService() {
		return SpringContextHolder.getBean("switchStatusService");
	}
}
