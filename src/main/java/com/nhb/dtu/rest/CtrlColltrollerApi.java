package com.nhb.dtu.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nhb.dtu.command.CommandRequest;
import com.nhb.dtu.controller.RemoteCtrl;
import com.nhb.dtu.entity.AirCtrlComm;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.enums.AirConditionCtrlEnum;
import com.nhb.dtu.enums.AirCtrlAlarmStatusEnum;
import com.nhb.dtu.enums.SwitchStatusEnum;
import com.nhb.dtu.service.data.AirCtrlCommService;
import com.nhb.dtu.service.meter.ReceiptMeterService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.common.RestResultDto;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("ctrl")
@Api(description = "空调控制器")
public class CtrlColltrollerApi {

	@Autowired
	private AirCtrlCommService airCtrlCommService;

	@Autowired
	private ReceiptMeterService receiptMeterService;

	@Autowired
	private ReceiptDeviceService receiptDeviceService;

	@Autowired
	private RemoteCtrl remoteCtrl;

	CommandRequest request;

	String type;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "on", method = { RequestMethod.POST })
	public RestResultDto turnOn(@RequestBody Map<String, Object> param) {
		String circuitId = String.valueOf(param.get("circuitId"));
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;
		try {
			AirCtrlComm comm = airCtrlCommService.findById(circuitId);
			if (null != comm) {
				if (comm.getStatus().equals(SwitchStatusEnum.ON.getKey())) {
					msg = "当前空调已在启动状态！";
				} else {
					type = AirConditionCtrlEnum.SWITCHON.getKey();
					request = new CommandRequest();
					request.setCircuitId(circuitId);
					request.setType(type);
					remoteCtrl.addCmdCommand(request);
					msg = "开机命令下发成功,请稍后查看最新状态！";
					data = true;
				}
			}
		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			data = null;
			msg = "获取回路id为" + circuitId + "空调控制器状态失败！";
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "off", method = { RequestMethod.POST })
	public RestResultDto turnOff(@RequestBody Map<String, Object> param) {
		String circuitId = String.valueOf(param.get("circuitId"));
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;
		try {
			AirCtrlComm comm = airCtrlCommService.findById(circuitId);
			if (null != comm) {
				if (comm.getStatus().equals(SwitchStatusEnum.OFF.getKey())) {
					msg = "当前空调已在关机状态！";
				} else {
					type = AirConditionCtrlEnum.SWITCHOFF.getKey();
					request = new CommandRequest();
					request.setCircuitId(circuitId);
					request.setType(type);
					remoteCtrl.addCmdCommand(request);
					msg = "关机命令下发成功,请稍后查看最新状态！";
					data = true;
				}
			}
		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			data = null;
			msg = "获取回路id为" + circuitId + "空调控制器状态失败！";
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "sendNewStatus", method = { RequestMethod.POST })
	public RestResultDto sendNewStatus(@RequestBody Map<String, Object> param) {
		String circuitId = String.valueOf(param.get("circuitId"));
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;
		try {
			AirCtrlComm comm = airCtrlCommService.findById(circuitId);
			if (null != comm) {
				type = AirConditionCtrlEnum.AIRPARAM.getKey();
				request = new CommandRequest();
				request.setCircuitId(circuitId);
				request.setType(type);
				remoteCtrl.addCmdCommand(request);
				msg = "获取状态命令下发成功,请稍后查询最新状态！";
				data = true;
			}
		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			data = null;
			msg = "获取回路id为" + circuitId + "空调控制器状态失败！";
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/getAirStatus", method = { RequestMethod.POST })
	public RestResultDto getAirStatus(@RequestBody Map<String, Object> param) throws Exception {
		String circuitId = String.valueOf(param.get("circuitId"));
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;
		try {
			AirCtrlComm comm = airCtrlCommService.findById(circuitId);
			if (null == comm) {
				msg = "回路id为" + circuitId + "的空调状态为空！";
			} else {
				msg = "回路id为" + circuitId + "空调控制器状态成功！";
				data = comm;
			}
		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			data = null;
			msg = "获取回路id为" + circuitId + "空调控制器状态失败！";
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("getAllAirCtrl")
	public RestResultDto getAllAirCtrl() {
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;
		try {
			List<Object> returnValue = Lists.newArrayList();
			Map<String, Object> mapValue = null;
			// 查询所有的空调控制器
			List<ReceiptMeter> meters = receiptMeterService.findMetersByType("Air_Ctrl");
			List<ReceiptDevice> circuits = receiptDeviceService.findAll();
			List<AirCtrlComm> comms = airCtrlCommService.findAll();
			String alarm = AirCtrlAlarmStatusEnum.NORMAL.getKey();
			if (CollectionUtils.isEmpty(meters) || CollectionUtils.isEmpty(circuits)) {
				msg = "未查询到可用的空调控制器！";
			} else {
				for (ReceiptMeter meter : meters) {
					for (ReceiptDevice circuit : circuits) {
						mapValue = Maps.newHashMap();
						if (circuit.getMeterId().equals(meter.getId())) {
							for (AirCtrlComm comm : comms) {
								if (comm.getCircuitId().equals(circuit.getId())) {
									mapValue.put("status", comm.getStatus());
									if (comm.getCurrsensorEx().equals(alarm) && comm.getDeliveryairEx().equals(alarm)
											&& comm.getHeatingEx().equals(alarm) && comm.getPowerEx().equals(alarm)
											&& comm.getRefrigerationEx().equals(alarm)
											&& comm.getReturnairEx().equals(alarm)) {
										mapValue.put("existAlarm", false);
									} else {
										mapValue.put("existAlarm", true);
									}
								}
							}
							mapValue.put("meterId", meter.getId());
							mapValue.put("circuitId", circuit.getId());
							mapValue.put("name", circuit.getName());
							returnValue.add(mapValue);
						}
					}
				}
			}
			data = returnValue;
			msg = "获取所有空调控制器成功！";

		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			data = null;
			msg = "获取空调控制器失败！";
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

}
