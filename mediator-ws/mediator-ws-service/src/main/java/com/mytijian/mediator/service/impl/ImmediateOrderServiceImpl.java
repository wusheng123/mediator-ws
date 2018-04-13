package com.mytijian.mediator.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mytijian.mediator.api.service.ImmediateOrderService;

@Service("immediateOrderService")
public class ImmediateOrderServiceImpl implements ImmediateOrderService {

	@Value("${orderExportPageSzie}")
	private Integer orderExportPageSzie;

	private Map<Integer, BlockingQueue<Integer>> map = new ConcurrentHashMap<Integer, BlockingQueue<Integer>>();

	@Override
	public void addOrder(Integer hospitalId, Integer orderId) throws InterruptedException {

		if (map.containsKey(hospitalId)) {
			BlockingQueue<Integer> queue = map.get(hospitalId);
			queue.put(orderId);

		} else {
			LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
			queue.put(orderId);
			map.put(hospitalId, queue);

		}
	}

	@Override
	public List<Integer> getOrder(Integer hospitalId) {

		BlockingQueue<Integer> queue = map.get(hospitalId);
		if (queue != null && queue.size() > 0) {
			List<Integer> list = new ArrayList<Integer>();
			Integer orderId = null;
			// 分页
			while (list.size() < orderExportPageSzie
					&& (orderId = queue.poll()) != null) {
				list.add(orderId);
			}
			return list;
		} else {
			return Collections.emptyList();
		}

	}
	
}
