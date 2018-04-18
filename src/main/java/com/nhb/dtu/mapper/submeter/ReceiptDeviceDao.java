package com.nhb.dtu.mapper.submeter;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nhb.dtu.entity.ReceiptDevice;

public interface ReceiptDeviceDao extends MongoRepository<ReceiptDevice, String> {

	/**
	 * 
	 * @Title: findCircuitsByMeterId @Description: 根据电表id，查询该电表下所有回路信息 @return
	 *         List<ReceiptCircuit> @throws
	 */
	List<ReceiptDevice> findByMeterId(String meterId);

	/**
	 * 
	 * @Title: findCircuitByMeterIdAndCircuitNo @Description:
	 *         根据电表id和回路No查询单个回路信息 @return ReceiptCircuit @throws
	 */
	ReceiptDevice findByMeterIdAndCircuitNo(String meterId, String circuitNo);

}
