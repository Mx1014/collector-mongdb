package com.nhb.dtu.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.service.collector.CollectorStatusService;
import com.nhb.dtu.service.collector.ReceiptCollectorService;
import com.nhb.dtu.service.data.SwitchStatusService;
import com.nhb.dtu.service.meter.ReceiptMeterService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.common.RestResultDto;

@RestController
@RequestMapping("dtu/collector")
public class CollectorController {

	@Autowired
	private ReceiptCollectorService receiptCollectorService;

	@Autowired
	private ReceiptMeterService receiptMeterService;

	@Autowired
	private ReceiptDeviceService receiptDeviceService;

	@Autowired
	private CollectorStatusService collectorStatusService;

	@Autowired
	private SwitchStatusService switchStatusService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public RestResultDto save(@RequestBody ReceiptCollector collector) {
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			ReceiptCollector receiptCollector = null;
			if (collector.getId() != null) {
				receiptCollector = receiptCollectorService.findById(collector.getId());
			}
			if (null != receiptCollector) {
				// 更新
				receiptCollectorService.save(collector);
				msg = "更新成功！";
				data = true;
			} else {
				// 新增
				ReceiptCollector collectorByNo = receiptCollectorService.findCollectorByNo(collector.getCollectorNo());
				if (null != collectorByNo) {
					msg = "保存失败，采集器编号重复！";
					data = false;
					result = RestResultDto.RESULT_FAIL;
				} else {
					receiptCollectorService.save(collector);
					msg = "collector保存成功！";
					data = true;
				}
			}
		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "collector保存失败";
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
		String collectorId = String.valueOf(param.get("collectorId"));
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;

		try {
			// 删除collector时 需要同时删除其下挂电表和回路
			List<ReceiptMeter> meters = receiptMeterService.findMetersByCollectorId(collectorId);
			if (!CollectionUtils.isEmpty(meters)) {
				// 防止 循环 查询，所以一次性查询出所有的 回路信息
				List<ReceiptDevice> circuits = receiptDeviceService.findAll();
				for (ReceiptMeter meter : meters) {
					if (!CollectionUtils.isEmpty(circuits)) {
						// 遍历circuit信息
						for (ReceiptDevice circuit : circuits) {
							// 判断回路是否属于 下挂的 meter中，如果属于就删除该回路
							if (circuit.getMeterId().equals(meter.getId())) {
								receiptDeviceService.delete(circuit.getId());
								switchStatusService.delete(circuit.getId());
							}
						}
					}
					// 删除 meter
					receiptMeterService.delete(meter.getId());
				}
			}
			// 删除 collector
			receiptCollectorService.delete(collectorId);
			collectorStatusService.delete(collectorId);
			msg = "collector删除成功！";
			data = true;
		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "collector删除失败";
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
			List<ReceiptCollector> list = receiptCollectorService.findAll();

			data = list;
			msg = "查询所有collector成功！";

		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "查询所有collector失败！";
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
