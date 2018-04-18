package com.nhb.dtu.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.enums.EnergyTypeEnum;
import com.nhb.dtu.enums.UnitTypeEnum;
import com.nhb.dtu.service.collector.ReceiptCollectorService;
import com.nhb.dtu.service.data.SwitchStatusService;
import com.nhb.dtu.service.meter.ReceiptMeterService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.common.RestResultDto;

@RestController
@RequestMapping("dtu/circuit")
public class CircuitController {

	@Autowired
	private ReceiptDeviceService receiptDeviceService;

	@Autowired
	private ReceiptMeterService receiptMeterService;

	@Autowired
	private ReceiptCollectorService receiptCollectorService;

	@Autowired
	private SwitchStatusService switchStatusService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public RestResultDto save(@RequestBody ReceiptDevice circuit) {
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			ReceiptMeter meter = receiptMeterService.findById(circuit.getMeterId());
			ReceiptCollector collector = receiptCollectorService.findById(meter.getCollectorId());
			circuit.setId(collector.getCollectorNo() + meter.getMeterNo() + circuit.getCircuitNo());
			receiptDeviceService.save(circuit);
			msg = "circuit保存成功！";
			data = true;
		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "circuit保存失败";
			data = false;
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public RestResultDto delete(@RequestBody Map<String, Object> param) {
		String circuitId = String.valueOf(param.get("circuitId"));
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			receiptDeviceService.delete(circuitId);
			switchStatusService.delete(circuitId);
			msg = "circuit删除成功！";
			data = true;
		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "circuit删除失败";
			data = false;
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "findAll", method = RequestMethod.POST)
	public RestResultDto findAll() {
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			List<ReceiptDevice> list = receiptDeviceService.findAll();
			data = list;
			msg = "查询所有circuit成功！";

		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "查询所有circuit失败！";
			data = null;
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "findByMeterId", method = RequestMethod.POST)
	public RestResultDto findByMeterId(@RequestBody Map<String, Object> param) {
		String meterId = String.valueOf(param.get("meterId"));
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			List<ReceiptDevice> list = receiptDeviceService.findByMeterId(meterId);
			data = list;
			msg = "查询所有circuit成功！";

		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "查询所有circuit失败！";
			data = null;
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "getUnitTypes", method = RequestMethod.POST)
	public RestResultDto getUnitTypes() {
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			List<Object> returnValue = Lists.newArrayList();
			Map<String, Object> oneMap = null;
			for (UnitTypeEnum entity : UnitTypeEnum.values()) {
				oneMap = Maps.newHashMap();
				oneMap.put("code", entity.getKey());
				oneMap.put("name", entity.getValue());
				returnValue.add(oneMap);
			}
			data = returnValue;
			msg = "查询所有单位类型成功！";

		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "查询所有单位类型失败！";
			data = null;
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "getEnergyTypes", method = RequestMethod.POST)
	public RestResultDto getEnergyTypes() {
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			List<Object> returnValue = Lists.newArrayList();
			Map<String, Object> oneMap = null;
			for (EnergyTypeEnum entity : EnergyTypeEnum.values()) {
				oneMap = Maps.newHashMap();
				oneMap.put("code", entity.getKey());
				oneMap.put("name", entity.getValue());
				returnValue.add(oneMap);
			}
			data = returnValue;
			msg = "查询所有能源类型成功！";

		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "查询所有能源类型失败！";
			data = null;
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

}
