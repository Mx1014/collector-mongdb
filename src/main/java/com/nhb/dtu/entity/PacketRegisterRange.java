package com.nhb.dtu.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "packet_register_range")
public class PacketRegisterRange {

	@Id
	private Long id;

	@Field("protocol_id")
	private Long protocolId;

	@Field("startAt")
	private String startAt;

	@Field("endAt")
	private String endAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(Long protocolId) {
		this.protocolId = protocolId;
	}

	public String getStartAt() {
		return startAt;
	}

	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}

	public String getEndAt() {
		return endAt;
	}

	public void setEndAt(String endAt) {
		this.endAt = endAt;
	}
}
