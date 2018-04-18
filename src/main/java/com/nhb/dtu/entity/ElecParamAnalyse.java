package com.nhb.dtu.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "elec_param_analyse")
public class ElecParamAnalyse {

	@Id
	private Long id;

	@Field("packet_id")
	private Long packetId;

	@Field("loop_no")
	private Long loopNo;

	@Field("elec_param")
	private String elecParam;

	@Field("startAt")
	private String startAt;

	@Field("register_count")
	private int registerCount;

	@Field("calculate_str")
	private String calculateStr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPacketId() {
		return packetId;
	}

	public void setPacketId(Long packetId) {
		this.packetId = packetId;
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
