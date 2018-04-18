package com.nhb.dtu.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The persistent class for the collector_status database table.
 * 
 */
@Document(collection = "collector_status")
public class CollectorStatus {

	@Id
	@Field("collector_id")
	private String collectorId;

	@Field("collector_no")
	private String collectorNo;

	@Field("collector_name")
	private String collectorName;

	@Field("collector_ip")
	private String collectorIp;

	@Field("collector_port")
	private Integer collectorPort;

	@Field("server_ip")
	private String serverIp;

	@Field("server_port")
	private Integer serverPort;

	@Field("active_time")
	private Date activeTime;

	public String getCollectorId() {
		return this.collectorId;
	}

	public void setCollectorId(String collectorId) {
		this.collectorId = collectorId;
	}

	public String getCollectorNo() {
		return collectorNo;
	}

	public void setCollectorNo(String collectorNo) {
		this.collectorNo = collectorNo;
	}

	public String getCollectorName() {
		return collectorName;
	}

	public void setCollectorName(String collectorName) {
		this.collectorName = collectorName;
	}

	public String getCollectorIp() {
		return this.collectorIp;
	}

	public void setCollectorIp(String collectorIp) {
		this.collectorIp = collectorIp;
	}

	public Integer getCollectorPort() {
		return collectorPort;
	}

	public void setCollectorPort(Integer collectorPort) {
		this.collectorPort = collectorPort;
	}

	public String getServerIp() {
		return this.serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public Integer getServerPort() {
		return this.serverPort;
	}

	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}

	public Date getActiveTime() {
		return this.activeTime;
	}

	public void setActiveTime(Date activeTime) {
		this.activeTime = activeTime;
	}
}
