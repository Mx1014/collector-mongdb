package com.nhb.dtu.base;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.enums.ProtrocolTypeEnum;

public abstract class AbstractDevice implements Device {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected ReceiptCollector receiptCollector;

	protected ReceiptMeter receiptMeter;

	protected ReceiptDevice receiptDevice;// 远程操控使用

	protected List<byte[]> readingFrames = new ArrayList<byte[]>();
	protected List<byte[]> writingFrames = new ArrayList<byte[]>();

	private static final long TIME_OUT = 60000; // 3 * 60 * 1000
	private long writingTime;
	private int retry = 0; // total = retry + 1
	private boolean success;

	private boolean ignoreResponse;

	@Override
	public void processReadingFrame(byte[] readingFrame) {
		// 长度 小于 包头包尾长度 9，不需要解析
		if (readingFrame.length < 9) {
			return;
		}
		// 去除 包头 包尾 和 188 协议设备 前缀 fe
		int counter = 0;
		// 先去包头包尾
		byte[] data = new byte[readingFrame.length - 9];
		for (int i = 0; i < data.length; i++) {
			data[i] = readingFrame[i + 8];
		}
		// 判断 开头fe的 字符全部去掉
		for (int i = 0; i < data.length; i++) {
			int fe = data[i] & 0xff;
			if (fe == 0xfe) {
				counter++;
			} else { // 当不是fe 后直接跳过
				break;
			}
		}
		// 新的数组
		byte[] destFrame = new byte[data.length - counter];
		System.arraycopy(data, counter, destFrame, 0, destFrame.length);

		if (!isPacketBelongToMeter(destFrame)) {
			return;
		}
		try {
			readingFrames.add(destFrame);
			if (!writingFrames.isEmpty()) {
				return;
			}
			for (byte[] frame : readingFrames) {
				if (!analyzeFrame(frame)) {
					return;
				}
			}
			handleResult();
			success = true;
		} catch (Exception ex) {
			logger.error("unknow Exception", ex);
		} finally {
			writingTime = 0;
		}
	}

	/**
	 * Do the receive packet belong to current Meter ?
	 * 
	 * @param the
	 *            receive packet
	 * @return Yes retrun true,No return false
	 */
	private boolean isPacketBelongToMeter(byte[] msg) {
		try {
			long meterNo = 0L;
			if (receiptMeter.getProtocolType().equals(ProtrocolTypeEnum.AIR_CTRL.getKey())
					|| receiptMeter.getProtocolType().equals(ProtrocolTypeEnum.DC_JLMK.getKey())
					|| receiptMeter.getProtocolType().equals(ProtrocolTypeEnum.AC_JLMK.getKey())) {
				meterNo = getCtrlNo(msg);
			} else if (receiptMeter.getProtocolType().equals(ProtrocolTypeEnum.CJ188_GAS.getKey())
					|| receiptMeter.getProtocolType().equals(ProtrocolTypeEnum.CJ188_WATER.getKey())) {
				meterNo = get188No(msg);
			} else if (receiptMeter.getProtocolType().equals(ProtrocolTypeEnum.DLT645_2007.getKey())) {
				meterNo = get07MeterNo(msg);
			} else {
				meterNo = doGetMeterNo(msg);
			}
			if (meterNo == Long.parseLong(receiptMeter.getMeterNo())) {
				return true;
			}
		} catch (Exception ex) {
			logger.error("Exception in isPacketBelongToMeter：", ex);
		}
		return false;
	}

	private long get07MeterNo(byte[] msg) {
		String addr = "";
		for (int i = 6; i > 0; i--) {
			String temp = Integer.toHexString(msg[i] & 0xff);
			if (temp.length() < 2) {
				temp = "0" + temp;
			}
			addr += temp;
		}
		return Long.parseLong(addr);
	}

	private long get188No(byte[] msg) {
		if (msg.length < 16) {
			return 0;
		}
		String addr = "";
		for (int i = 8; i > 1; i--) {
			String temp = Integer.toHexString(msg[i] & 0xff);
			if (temp.length() < 2) {
				temp = "0" + temp;
			}
			addr += temp;
		}
		return Long.parseLong(addr);
	}

	private long getCtrlNo(byte[] msg) {
		char a = (char) (msg[3] & 0xff);
		char b = (char) (msg[4] & 0xff);
		String meterNo = String.valueOf(a) + String.valueOf(b);
		return Long.parseLong(meterNo);
	}

	/**
	 * Get Device Address
	 * 
	 * @param the
	 *            receive packet
	 * @return meterNO
	 */
	private long doGetMeterNo(byte[] msg) throws Exception {
		int meterNo = msg[0] & 0xff;
		return meterNo;
	}

	@Override
	public byte[] nextWritingFrame() {
		if (System.currentTimeMillis() - writingTime < TIME_OUT) {
			return null;
		}
		if (success || ((writingFrames.isEmpty() || writingTime > 0) && retry == 0)) {
			return null;
		}
		if (writingFrames.isEmpty() || writingTime > 0) {
			retry = retry - 1;
			readingFrames.clear();
			writingFrames.clear();
			buildWritingFrames();
		}
		writingTime = System.currentTimeMillis();
		return writingFrames.remove(0);
	}

	@Override
	public boolean isComplete() {
		if (System.currentTimeMillis() - writingTime < TIME_OUT) {
			return false;
		}
		if (success || ((writingFrames.isEmpty() || writingTime > 0) && retry == 0)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isIgnoreResponse() {
		return ignoreResponse;
	}

	public void setIgnoreResponse(boolean ignoreResponse) {
		this.ignoreResponse = ignoreResponse;
	}

	public ReceiptCollector getReceiptCollector() {
		return receiptCollector;
	}

	public void setReceiptCollector(ReceiptCollector receiptCollector) {
		this.receiptCollector = receiptCollector;
	}

	public ReceiptMeter getReceiptMeter() {
		return receiptMeter;
	}

	public void setReceiptMeter(ReceiptMeter receiptMeter) {
		this.receiptMeter = receiptMeter;
	}

	public abstract void buildWritingFrames();

	public abstract boolean analyzeFrame(byte[] frame);

	public abstract void handleResult();

}
