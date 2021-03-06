package com.nhb.dtu.service.basic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.ModbusGericProtocol;
import com.nhb.dtu.mapper.basic.ModbusGericProtocolDao;

/**
 * 
 * @ClassName: ModbusGericProtocolService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author XS guo
 * @date 2017年6月30日 下午4:02:20
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
public class ModbusGericProtocolService {

	@Autowired
	private ModbusGericProtocolDao modbusGericProtocolDao;

	/**
	 * 
	 * @Title: findAll @Description: TODO(这里用一句话描述这个方法的作用) @return
	 *         List<ModbusGericProtocol> @throws
	 */
	public List<ModbusGericProtocol> findAll() {
		return (List<ModbusGericProtocol>) modbusGericProtocolDao.findAll();
	}

}
