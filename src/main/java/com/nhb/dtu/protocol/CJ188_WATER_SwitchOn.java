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
 * @ClassName: CJ188_WATER_SwitchOn
 * @Description: 188协议水表 开阀
 * @author XS guo
 * @date 2017年8月7日 下午6:51:38
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CJ188_WATER_SwitchOn extends AbstractDevice {

	private boolean result;

	private RemoteRtnService getRemoteRtnService() {
		return SpringContextHolder.getBean("remoteRtnService");
	}

	private int[] address = new int[7]; // 0为最高位

	public CJ188_WATER_SwitchOn(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter,
			ReceiptDevice receiptDevice) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		this.receiptDevice = receiptDevice;
		for (int i = 0; i < address.length; i++) {
			address[i] = Integer.parseInt(receiptMeter.getMeterNo().substring(2 * i, 2 * i + 2), 16);
		}
		buildWritingFrames();
	}

	@Override
	public void buildWritingFrames() {

		int[] data = new int[17];
		// 起始帧
		data[0] = 0x68;
		// 气表 为30
		data[1] = 0x30;
		// 表号 14位
		data[2] = address[6];
		data[3] = address[5];
		data[4] = address[4];
		data[5] = address[3];
		data[6] = address[2];
		data[7] = address[1];
		data[8] = address[0];
		// 控制码 写入数据
		data[9] = 0x04;
		// 数据长度
		data[10] = 0x04;
		// 数据标识
		data[11] = 0xA0;
		data[12] = 0x17;
		// SER序列号
		data[13] = 0x00;
		// 开阀
		data[14] = 0x55;
		// CS 校验和
		data[15] = Verify.csSum(data[0] + data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7] + data[8]
				+ data[9] + data[10] + data[11] + data[12] + data[13] + data[14]);
		// 结束帧
		data[16] = 0x16;

		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);

	}

	@Override
	public boolean analyzeFrame(byte[] frame) {
		// 去除 2个 FE 和 包头包尾
		int[] data = new int[frame.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = frame[i] & 0xFF;
		}

		if (data[13] == 0x00) {
			result = true;
		}

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
		switchStatus.setStatus(SwitchStatusEnum.ON.getKey());
		getSwitchStatusService().save(switchStatus);

		// 返回消息 开阀成功
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
