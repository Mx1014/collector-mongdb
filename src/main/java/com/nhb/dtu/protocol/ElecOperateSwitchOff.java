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
 * @ClassName: ElecOperateSwitchOff
 * @Description: 智能电操 拉闸命令
 * @author XS guo
 * @date 2017年8月25日 上午11:03:39
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ElecOperateSwitchOff extends AbstractDevice {

	private RemoteRtnService getRemoteRtnService() {
		return SpringContextHolder.getBean(RemoteRtnService.class);
	}

	private boolean result;
	private String status;

	public ElecOperateSwitchOff(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter,
			ReceiptDevice receiptDevice) {
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
		senddata[3] = 0x00;
		senddata[4] = 0x00;
		senddata[5] = 0x02;

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
		int[] data = new int[frame.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = frame[i] & 0xFF;
		}
		// 返回字符 判断为02 则操作成功
		Integer rtn = (Integer) data[5];
		if (rtn != 02) {
			return false;
		}
		result = true;
		status = SwitchStatusEnum.OFF.getKey();
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

		// 如果 路径不为空，则需要 向 平台进行推送 消息
		Map<String, Object> params = Maps.newHashMap();
		params.put("deviceId", receiptDevice.getId());
		params.put("name", receiptDevice.getName());
		params.put("result", result);
		params.put("type", SwitchStatusEnum.OFF.getKey());
		getRemoteRtnService().remoteRtn(params);
	}

	private SwitchStatusService getSwitchStatusService() {
		return SpringContextHolder.getBean("switchStatusService");
	}

}
