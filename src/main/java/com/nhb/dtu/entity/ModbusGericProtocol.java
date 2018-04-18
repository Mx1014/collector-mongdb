package com.nhb.dtu.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "modbus_generic_protocol")
public class ModbusGericProtocol {

	@Id
	private Long id;

	@Field("protocol")
	private String protocol;

	@Field("electricity_type")
	private String electricityType;

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

	public String getElectricityType() {
		return electricityType;
	}

	public void setElectricityType(String electricityType) {
		this.electricityType = electricityType;
	}

}
