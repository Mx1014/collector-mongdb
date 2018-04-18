package com.nhb.dtu.protocol;

import java.util.Date;
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

/**
 * 
 * @ClassName: NewElecOperaStatus
 * @Description: 智能电操（新）- 读取状态
 * @author XS guo
 * @date 2017年9月1日 下午2:04:34
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class NewElecOperaStatus extends AbstractDevice {

	private String status;

	public NewElecOperaStatus(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter,
			ReceiptDevice receiptDevice) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		this.receiptDevice = receiptDevice;
		buildWritingFrames();
	}

	public NewElecOperaStatus(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
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
		senddata[1] = 0x03;
		senddata[2] = 0x10;
		senddata[3] = 0x01;
		senddata[4] = 0x00;
		senddata[5] = 0x01;

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
		if (data[2] != 0x02) {
			return false;
		}

		if (data[4] == 0x00) {
			status = SwitchStatusEnum.NONE.getKey();
		} else if (data[4] == 0x01) {
			status = SwitchStatusEnum.ON.getKey();
		} else if (data[4] == 0x02) {
			status = SwitchStatusEnum.OFF.getKey();
		} else if (data[4] == 0x03) {
			status = SwitchStatusEnum.BROKEN.getKey();
		}
		return true;
	}

	@Override
	public void handleResult() {
		if (null == receiptDevice) {
			receiptDevice = getReceiptDeviceService().findByMeterIdAndCircuitNo(receiptMeter.getId(), "1");
		}
		Date nowTime = new Date();
		DataSwitch dataSwitch = new DataSwitch();
		dataSwitch.setId(UUID.randomUUID());
		dataSwitch.setStatus(status);
		dataSwitch.setReadTime(nowTime);
		dataSwitch.setDeviceId(receiptDevice.getId());
		getDataSwitchService().save(dataSwitch);

		SwitchStatus switchStatus = getSwitchStatusService().findById(receiptDevice.getId());

		if (null == switchStatus) {
			switchStatus = new SwitchStatus();
			switchStatus.setDeviceId(receiptDevice.getId());
		}
		switchStatus.setReadTime(nowTime);
		switchStatus.setStatus(status);
		getSwitchStatusService().save(switchStatus);

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
