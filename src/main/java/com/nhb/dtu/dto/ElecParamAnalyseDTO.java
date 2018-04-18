package com.nhb.dtu.dto;

public class ElecParamAnalyseDTO {

	private Long id;

	private PacketRegisterRangeDTO packetRegisterRange;

	private Long loopNo;

	private String elecParam;

	private String startAt;

	private int registerCount;

	private String calculateStr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PacketRegisterRangeDTO getPacketRegisterRange() {
		return packetRegisterRange;
	}

	public void setPacketRegisterRange(PacketRegisterRangeDTO packetRegisterRange) {
		this.packetRegisterRange = packetRegisterRange;
	}

	public Long getLoopNo() {
		return loopNo;
	}

	public void setLoopNo(Long loopNo) {
		this.loopNo = loopNo;
	}

	public String getElecParam() {
		return elecParam;
	}

	public void setElecParam(String elecParam) {
		this.elecParam = elecParam;
	}

	public String getStartAt() {
		return startAt;
	}

	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}

	public int getRegisterCount() {
		return registerCount;
	}

	public void setRegisterCount(int registerCount) {
		this.registerCount = registerCount;
	}

	public String getCalculateStr() {
		return calculateStr;
	}

	public void setCalculateStr(String calculateStr) {
		this.calculateStr = calculateStr;
	}
}
