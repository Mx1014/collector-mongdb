package com.nhb.dtu.service.collector;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.mapper.collector.ReceiptCollectorDao;

/**
 * @ClassName:ReceiptService
 * @Function: 回单管理
 * @Date: May 25, 2017 5:03:07 PM
 * @author sunlei
 * @version
 * @param <D>
 * @param <T>
 * @since JDK 1.8
 * @see
 */
@Service
public class ReceiptCollectorService {
	@Autowired
	private ReceiptCollectorDao receiptCollectorDao;

	public ReceiptCollector findCollectorByNo(String collectorNo) {
		return receiptCollectorDao.findByCollectorNo(collectorNo);
	}

	public ReceiptCollector save(ReceiptCollector collector) {
		return receiptCollectorDao.save(collector);
	}

	public void delete(String id) {
		receiptCollectorDao.deleteById(id);
	}

	public List<ReceiptCollector> findAll() {
		return (List<ReceiptCollector>) receiptCollectorDao.findAll();
	}

	public ReceiptCollector findById(String collectorId) {
		if (receiptCollectorDao.findById(collectorId).equals(Optional.empty())) {
			return null;
		}
		return receiptCollectorDao.findById(collectorId).get();
	}
}
