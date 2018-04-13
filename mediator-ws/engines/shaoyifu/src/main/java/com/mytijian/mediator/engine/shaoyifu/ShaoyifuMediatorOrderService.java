package com.mytijian.mediator.engine.shaoyifu;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mytijian.mediator.order.base.service.impl.MediatorOrderServiceImpl;
import com.mytijian.mediator.order.dto.OrderDto;
import com.mytijian.offer.examitem.constant.enums.ExamItemToMealEnum;
import com.mytijian.offer.examitem.dto.ExamItemDto;
import com.mytijian.offer.examitem.service.ExamItemService;

@Service("shaoyifuMediatorOrderService")
public class ShaoyifuMediatorOrderService extends MediatorOrderServiceImpl {

	@Resource(name = "examItemService")
	private ExamItemService examItemService;

	/**
	 * 新增项目且是组项目，在服务端将单项Description设置未[IS_GROUP_ITEM].
	 * 在agent将单项中间表的itemType设置为hisid。
	 */
	@Override
	protected void handleOrderDto(OrderDto orderDto) {
		for (com.mytijian.resource.model.ExamItemSnap snap : orderDto
				.getExamItemSnapList()) {

			if (snap != null
					&& snap.getTypeToMeal() == ExamItemToMealEnum.addToMeal
							.getCode()) {
				ExamItemDto dto = examItemService.getExamItemByItemId(snap
						.getId());
				if (dto != null && dto.getExamItem().getGroupId() != null) {
					snap.setDescription("IS_GROUP_ITEM");
				}
			}
		}

	}

}
