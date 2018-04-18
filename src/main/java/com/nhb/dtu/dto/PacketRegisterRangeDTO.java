package com.nhb.dtu.dto;

public class PacketRegisterRangeDTO
{

	private Long id;

	private ModbusGericProtocolDTO modbusGericProtocol;

	private String startAt;

	private String endAt;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public ModbusGericProtocolDTO getModbusGericProtocol()
	{
		return modbusGericProtocol;
	}

	public void setModbusGericProtocol(ModbusGericProtocolDTO modbusGericProtocol)
	{
		this.modbusGericProtocol = modbusGericProtocol;
	}

	public String getStartAt()
	{
		return startAt;
	}

	public void setStartAt(String startAt)
	{
		this.startAt = startAt;
	}

	public String getEndAt()
	{
		return endAt;
	}

	public void setEndAt(String endAt)
	{
		this.endAt = endAt;
	}
}
