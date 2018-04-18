package com.nhb.dtu.dto;

import com.nhb.dtu.enums.ElectricityType;

public class ModbusGericProtocolDTO {

	private Long id;

	private String protocol;

	private ElectricityType electricityType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public ElectricityType getElectricityType() {
		return electricityType;
	}

	public void setElectricityType(ElectricityType electricityType) {
		this.electricityType = electricityType;
	}
}
