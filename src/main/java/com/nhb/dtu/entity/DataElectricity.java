package com.nhb.dtu.entity;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The persistent class for the data_electricity database table.
 * 
 */
@Document(collection = "data_electricity")
public class DataElectricity{

	@Id
	@Field("id")
	private UUID id;

	@Field("current")
	private Double current;

	@Field("current_a")
	private Double currentA;

	@Field("current_b")
	private Double currentB;

	@Field("current_c")
	private Double currentC;

	@Field("electricity_type")
	private String electricityType;

	@Field("frequency")
	private Double frequency;

	@Field("kva")
	private Double kva;

	@Field("kva_a")
	private Double kvaA;

	@Field("kva_b")
	private Double kvaB;

	@Field("kva_c")
	private Double kvaC;

	@Field("kvar")
	private Double kvar;

	@Field("kvar_a")
	private Double kvarA;

	@Field("kvar_b")
	private Double kvarB;

	@Field("kvar_c")
	private Double kvarC;

	@Field("kvarh1")
	private Double kvarh1;

	@Field("kvarh2")
	private Double kvarh2;

	@Field("kw")
	private Double kw;

	@Field("kw_a")
	private Double kwA;

	@Field("kw_b")
	private Double kwB;

	@Field("kw_c")
	private Double kwC;

	@Field("kwh")
	private Double kwh;

	@Field("kwh_forward")
	private Double kwhForward;

	@Field("kwh_reverse")
	private Double kwhReverse;

	@Field("power_factor")
	private Double powerFactor;

	@Field("power_factor_a")
	private Double powerFactorA;

	@Field("power_factor_b")
	private Double powerFactorB;

	@Field("power_factor_c")
	private Double powerFactorC;

	@Field("read_time")
	private Date readTime;

	@Field("voltage")
	private Double voltage;

	@Field("voltage_a")
	private Double voltageA;

	@Field("voltage_a_b")
	private Double voltageAB;

	@Field("voltage_b")
	private Double voltageB;

	@Field("voltage_b_c")
	private Double voltageBC;

	@Field("voltage_c")
	private Double voltageC;

	@Field("voltage_c_a")
	private Double voltageCA;

	@Field("device_id")
	private String deviceId;

	public DataElectricity() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Double getCurrent() {
		return current;
	}

	public void setCurrent(Double current) {
		this.current = current;
	}

	public Double getCurrentA() {
		return this.currentA;
	}

	public void setCurrentA(Double currentA) {
		this.currentA = currentA;
	}

	public Double getCurrentB() {
		return this.currentB;
	}

	public void setCurrentB(Double currentB) {
		this.currentB = currentB;
	}

	public Double getCurrentC() {
		return this.currentC;
	}

	public void setCurrentC(Double currentC) {
		this.currentC = currentC;
	}

	public String getElectricityType() {
		return electricityType;
	}

	public void setElectricityType(String electricityType) {
		this.electricityType = electricityType;
	}

	public Double getFrequency() {
		return this.frequency;
	}

	public void setFrequency(Double frequency) {
		this.frequency = frequency;
	}

	public Double getKva() {
		return this.kva;
	}

	public void setKva(Double kva) {
		this.kva = kva;
	}

	public Double getKvaA() {
		return this.kvaA;
	}

	public void setKvaA(Double kvaA) {
		this.kvaA = kvaA;
	}

	public Double getKvaB() {
		return this.kvaB;
	}

	public void setKvaB(Double kvaB) {
		this.kvaB = kvaB;
	}

	public Double getKvaC() {
		return this.kvaC;
	}

	public void setKvaC(Double kvaC) {
		this.kvaC = kvaC;
	}

	public Double getKvar() {
		return this.kvar;
	}

	public void setKvar(Double kvar) {
		this.kvar = kvar;
	}

	public Double getKvarA() {
		return this.kvarA;
	}

	public void setKvarA(Double kvarA) {
		this.kvarA = kvarA;
	}

	public Double getKvarB() {
		return this.kvarB;
	}

	public void setKvarB(Double kvarB) {
		this.kvarB = kvarB;
	}

	public Double getKvarC() {
		return this.kvarC;
	}

	public void setKvarC(Double kvarC) {
		this.kvarC = kvarC;
	}

	public Double getKvarh1() {
		return this.kvarh1;
	}

	public void setKvarh1(Double kvarh1) {
		this.kvarh1 = kvarh1;
	}

	public Double getKvarh2() {
		return this.kvarh2;
	}

	public void setKvarh2(Double kvarh2) {
		this.kvarh2 = kvarh2;
	}

	public Double getKw() {
		return this.kw;
	}

	public void setKw(Double kw) {
		this.kw = kw;
	}

	public Double getKwA() {
		return this.kwA;
	}

	public void setKwA(Double kwA) {
		this.kwA = kwA;
	}

	public Double getKwB() {
		return this.kwB;
	}

	public void setKwB(Double kwB) {
		this.kwB = kwB;
	}

	public Double getKwC() {
		return this.kwC;
	}

	public void setKwC(Double kwC) {
		this.kwC = kwC;
	}

	public Double getKwh() {
		return this.kwh;
	}

	public void setKwh(Double kwh) {
		this.kwh = kwh;
	}

	public Double getKwhForward() {
		return this.kwhForward;
	}

	public void setKwhForward(Double kwhForward) {
		this.kwhForward = kwhForward;
	}

	public Double getKwhReverse() {
		return this.kwhReverse;
	}

	public void setKwhReverse(Double kwhReverse) {
		this.kwhReverse = kwhReverse;
	}

	public Double getPowerFactor() {
		return this.powerFactor;
	}

	public void setPowerFactor(Double powerFactor) {
		this.powerFactor = powerFactor;
	}

	public Double getPowerFactorA() {
		return this.powerFactorA;
	}

	public void setPowerFactorA(Double powerFactorA) {
		this.powerFactorA = powerFactorA;
	}

	public Double getPowerFactorB() {
		return this.powerFactorB;
	}

	public void setPowerFactorB(Double powerFactorB) {
		this.powerFactorB = powerFactorB;
	}

	public Double getPowerFactorC() {
		return this.powerFactorC;
	}

	public void setPowerFactorC(Double powerFactorC) {
		this.powerFactorC = powerFactorC;
	}

	public Date getReadTime() {
		return this.readTime;
	}

	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}

	public Double getVoltage() {
		return voltage;
	}

	public void setVoltage(Double voltage) {
		this.voltage = voltage;
	}

	public Double getVoltageA() {
		return this.voltageA;
	}

	public void setVoltageA(Double voltageA) {
		this.voltageA = voltageA;
	}

	public Double getVoltageAB() {
		return this.voltageAB;
	}

	public void setVoltageAB(Double voltageAB) {
		this.voltageAB = voltageAB;
	}

	public Double getVoltageB() {
		return this.voltageB;
	}

	public void setVoltageB(Double voltageB) {
		this.voltageB = voltageB;
	}

	public Double getVoltageBC() {
		return this.voltageBC;
	}

	public void setVoltageBC(Double voltageBC) {
		this.voltageBC = voltageBC;
	}

	public Double getVoltageC() {
		return this.voltageC;
	}

	public void setVoltageC(Double voltageC) {
		this.voltageC = voltageC;
	}

	public Double getVoltageCA() {
		return this.voltageCA;
	}

	public void setVoltageCA(Double voltageCA) {
		this.voltageCA = voltageCA;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}
