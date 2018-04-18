package com.nhb.dtu.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "receipt_device")
public class ReceiptDevice {

	@Id
	private String id;

	@Field("collector_no")
	private String collectorNo;

	@Field("meter_no")
	private String meterNo;

	@Field("meter_id")
	private String meterId;

	@Field("circuit_no")
	private String circuitNo;

	@Field("name")
	private String name;

	@Field("power_type")
	private String powerType;

	@Field("unit")
	private String unit;

	@Field("location")
	private String location;

	/**
	 * 电压变比
	 */
	@Field("voltage_ratio")
	private Double voltageRatio;

	/**
	 * 电流变比
	 */
	@Field("current_ratio")
	private Double currentRatio;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCollectorNo() {
		return collectorNo;
	}

	public void setCollectorNo(String collectorNo) {
		this.collectorNo = collectorNo;
	}

	public String getMeterNo() {
		return meterNo;
	}

	public void setMeterNo(String meterNo) {
		this.meterNo = meterNo;
	}

	public String getMeterId() {
		return meterId;
	}

	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}

	public String getCircuitNo() {
		return circuitNo;
	}

	public void setCircuitNo(String circuitNo) {
		this.circuitNo = circuitNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPowerType() {
		return powerType;
	}

	public void setPowerType(String powerType) {
		this.powerType = powerType;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getVoltageRatio() {
		return voltageRatio;
	}

	public void setVoltageRatio(Double voltageRatio) {
		this.voltageRatio = voltageRatio;
	}

	public Double getCurrentRatio() {
		return currentRatio;
	}

	public void setCurrentRatio(Double currentRatio) {
		this.currentRatio = currentRatio;
	}

}
