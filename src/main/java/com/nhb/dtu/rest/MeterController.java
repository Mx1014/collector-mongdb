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
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.enums.MeterTypeEnum;
import com.nhb.dtu.enums.ProtrocolTypeEnum;
import com.nhb.dtu.service.meter.ReceiptMeterService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.common.RestResultDto;

@RestController
@RequestMapping("dtu/meter")
public class MeterController {

	@Autowired
	private ReceiptMeterService receiptMeterService;

	@Autowired
	private ReceiptDeviceService receiptDeviceService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public RestResultDto save(@RequestBody ReceiptMeter collector) {
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			receiptMeterService.save(collector);
			msg = "meter保存成功！";
			data = true;
		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "meter保存失败";
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
		String meterId = String.valueOf(param.get("meterId"));
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			// 删除 meter 的同时，需要删除 其下属的 Device
			List<ReceiptDevice> devices = receiptDeviceService.findByMeterId(meterId);
			if (!CollectionUtils.isEmpty(devices)) {
				for (ReceiptDevice device : devices) {
					receiptDeviceService.delete(device.getId());
				}
			}
			receiptMeterService.delete(meterId);
			msg = "meter删除成功！";
			data = true;
		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "meter删除失败";
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
			List<ReceiptMeter> list = receiptMeterService.findAll();
			data = list;
			msg = "查询所有meter成功！";

		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "查询所有meter失败！";
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
	@RequestMapping(value = "findByCollId", method = RequestMethod.POST)
	public RestResultDto findByCollId(@RequestBody Map<String, Object> param) {
		String collectorId = String.valueOf(param.get("collectorId"));
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			List<ReceiptMeter> list = receiptMeterService.findMetersByCollectorId(collectorId);
			data = list;
			msg = "查询所有meter成功！";

		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "查询所有meter失败！";
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
	@RequestMapping(value = "getProtrocolTypes", method = RequestMethod.POST)
	public RestResultDto getProtrocolTypes() {
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			List<Object> returnValue = Lists.newArrayList();
			Map<String, Object> oneMap = null;
			for (ProtrocolTypeEnum entity : ProtrocolTypeEnum.values()) {
				oneMap = Maps.newHashMap();
				oneMap.put("code", entity.getKey());
				oneMap.put("name", entity.getValue());
				returnValue.add(oneMap);
			}
			data = returnValue;
			msg = "查询所有协议类型成功！";

		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "查询所有协议类型失败！";
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
	@RequestMapping(value = "getMeterTypes", method = RequestMethod.POST)
	public RestResultDto getMeterTypes() {
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			List<Object> returnValue = Lists.newArrayList();
			Map<String, Object> oneMap = null;
			for (MeterTypeEnum entity : MeterTypeEnum.values()) {
				oneMap = Maps.newHashMap();
				oneMap.put("code", entity.getKey());
				oneMap.put("name", entity.getValue());
				returnValue.add(oneMap);
			}
			data = returnValue;
			msg = "查询所有电表类型成功！";

		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "查询所有电表类型失败！";
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
