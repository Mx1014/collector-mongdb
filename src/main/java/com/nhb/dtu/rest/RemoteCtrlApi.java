package com.nhb.dtu.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nhb.dtu.command.CommandRequest;
import com.nhb.dtu.controller.RemoteCtrl;
import com.nhb.dtu.entity.SwitchStatus;
import com.nhb.dtu.service.data.SwitchStatusService;
import com.nhb.utils.nhb_utils.common.RestResultDto;

/**
 * 
 * @ClassName: RemoteCtrlApi
 * @Description: 远程控制Api
 * @author XS guo
 * @date 2017年8月8日 下午7:10:40
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@RestController
@RequestMapping("remote/ctrl")
public class RemoteCtrlApi {

	@Autowired
	private RemoteCtrl remoteCtrl;

	@Autowired
	private SwitchStatusService switchStatusService;

	CommandRequest request;

	/**
	 * @Title: turnswitch
	 * @Description: 开关命令
	 * @return RestResultDto
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "turnswitch", method = { RequestMethod.POST })
	public RestResultDto turnswitch(@RequestBody Map<String, Object> param) {
		String circuitId = String.valueOf(param.get("deviceId"));
		String type = String.valueOf(param.get("type"));
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;
		try {
			SwitchStatus switchStatus = switchStatusService.findById(circuitId);
			if (null != switchStatus) {
				request = new CommandRequest();
				request.setCircuitId(circuitId);
				request.setType(type);
				remoteCtrl.addCmdCommand(request);
				msg = "远程控制命令已下发！";
				data = true;
			}
		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			data = false;
			msg = "远程控制命令下发失败！";
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

	/**
	 * @Title: setRecloseCount
	 * @Description: 设置重合闸次数
	 * @return RestResultDto
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "setRecloseCount", method = { RequestMethod.POST })
	public RestResultDto setRecloseCount(@RequestBody Map<String, Object> param) {
		String circuitId = String.valueOf(param.get("deviceId"));
		String type = String.valueOf(param.get("type"));
		RestResultDto resultDto = new RestResultDto();
		Integer result = RestResultDto.RESULT_SUCC;
		String msg = null;
		Object data = null;
		String exception = null;
		try {
			request = new CommandRequest();
			request.setCircuitId(circuitId);
			request.setType(type);
			remoteCtrl.addCmdCommand(request);
			msg = "远程控制命令已下发！";
			data = true;
		} catch (Exception e) {
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			data = false;
			msg = "远程控制命令下发失败！";
		} finally {
			resultDto.setData(data);
			resultDto.setException(exception);
			resultDto.setMsg(msg);
			resultDto.setResult(result);
		}
		return resultDto;
	}

}
