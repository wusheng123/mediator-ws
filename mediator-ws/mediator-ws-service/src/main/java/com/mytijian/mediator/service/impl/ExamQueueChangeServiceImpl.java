package com.mytijian.mediator.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.WriteResult;
import com.mytijian.account.model.Account;
import com.mytijian.account.model.ExamQueueInfo;
import com.mytijian.account.service.AccountService;
import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.api.service.ExamQueueChangeService;
import com.mytijian.message.constant.SmsMsgType;
import com.mytijian.message.model.SendInfo;
import com.mytijian.message.service.SendMessageService;
import com.mytijian.mongodb.MongoException;
import com.mytijian.mongodb.MyMongoTemplate;
import com.mytijian.order.service.MongoOrderService;
import com.mytijian.resource.model.RoomItem;
import com.mytijian.util.AssertUtil;
import com.mytijian.wx.model.WxBind;
import com.mytijian.wx.model.WxConfig;
import com.mytijian.wx.service.WxBindService;
import com.mytijian.wx.service.WxConfigService;

/**
 *
 * @author linzhihao
 */
@Deprecated
@Service("examQueueService")
public class ExamQueueChangeServiceImpl implements ExamQueueChangeService {
	
	private static Logger logger = LoggerFactory.getLogger(ExamQueueChangeServiceImpl.class);
	
	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;

	@Resource(name = "mongoClient")
	private MyMongoTemplate mongo;
	
	@Value("${queueMongoCol}")
	private String MONGOCOL;
	
	@Resource(name = "mongoOrderService")
	private MongoOrderService mongoOrderService;
	
	@Resource(name = "accountService")
	private AccountService accountService;
	
	@Resource(name = "wxBindService")
	private WxBindService wxBindService;
	
	@Resource(name = "wxConfigService")
	private WxConfigService wxConfigService;


	@Resource(name = "sendMessageService")
	private SendMessageService sendMessageService;
	
	/** 
	 * @see com.mytijian.mediator.api.service.ExamQueueChangeService#onChange(com.mytijian.account.model.ExamQueueInfo)
	 */
	@Override
	public void onChange(ExamQueueInfo person) {
		logger.debug("WS 收到数据 hospital {} -> {}", envKeeper.getHospital(), person);
		person.setHospitalId(envKeeper.getHospital());
		try {
			this.saveOrUpdatePerson(person);
			this.notifyUser(person);
		} catch (Exception e) {
			logger.error("ws onchange invoke error {}", e);
		}
	}

	private void notifyUser(ExamQueueInfo person) throws Exception {
		logger.debug("notify user -> {}", person);
		if (person.isChangeRoom()) {
			if (AssertUtil.areEquals(person.getStatus(), String.valueOf(ExamQueueInfo.STATUS_OVER))) {
				return;
			}
			logger.debug("roomid changed hisbm -> {}", person.getCode());
			Map<String, String> msg = new HashMap<>();
			msg.put("zone", person.getZone());
			msg.put("room", person.getRoom());
			if (AssertUtil.isNotEmpty(person.getRoomnum())) {
				msg.put("roomnum", " ("+person.getRoomnum()+")");
			}else {
				msg.put("roomnum", "");
			}
			msg.put("serial", person.getSerial());
			msg.put("minutes", person.getMinutes());
			if (AssertUtil.isNotEmpty(person.getRemark())) {
				msg.put("remark", "备注信息: "+person.getRemark()+"");
			} else {
				msg.put("remark", "");
			}
			@SuppressWarnings("rawtypes")
			Map order = getOrder(person.getCode());
			String orderId = String.valueOf(order.get("id"));
			msg.put("orderId", orderId);
			msg.put("first", "请准备前往下一个体检科室。");
			System.out.println("map -> "+msg);
			sendWx(person, msg, SmsMsgType.SMS_TEMPLATE_COED_ROOM_CHANGE);
		}
		
		if (person.isChangeStatus() &&
				AssertUtil.areEquals(person.getStatus(), String.valueOf(ExamQueueInfo.STATUS_OVER))) {
			Map<String, String> msg = new HashMap<String, String>();
			msg.put("first", "您的体检已经完成。");
			msg.put("name", person.getName());
			msg.put("status", person.getStatusstr());
			msg.put("remark", person.getZone()+"体检已完成。"+person.getRemark());
			sendWx(person, msg, SmsMsgType.SMS_TEMPLATE_COED_STATUS_CHANGE);
		}
	}
	
	
	private void sendWx(ExamQueueInfo person, Map<String, String> msg, String code) throws Exception {
		// 检查科室变化
		Account account = getAccount(person.getCode());
		if (account==null) {
			logger.debug("account is null hisbm -> {}", person.getCode());
			return;
		}
		WxConfig wxConfig = wxConfigService
				.findEnabledWxConfigByHospitalId(envKeeper.getHospital());
		if (wxConfig==null) {
			logger.debug("wxconfig is null hisbm -> {}, hospitalid -> {}", 
					person.getCode(), envKeeper.getHospital());
			return;
		}
		WxBind bind = wxBindService.getWxBindInfoByAccountId(wxConfig.getHid(), account.getId());
		if (bind==null) {
			logger.debug("wxbindinfo is null accountid->{}", account.getId());
			return;
		}
		SendInfo sendInfo = SendInfo.builder(envKeeper.getHospital(), code, msg)
				.weixinInfo(account.getId(), envKeeper.getHospital())
				.build();
		
		sendMessageService.send(sendInfo);
	}
	
	/** 
	 * @see com.mytijian.mediator.api.service.ExamQueueChangeService#clearState()
	 */
	@Override
	public void clearState() {
		logger.debug("ws 清除数据 hospital->{} ", envKeeper.getHospital());
		try {
			Query query = new Query();
			Criteria criteria = new Criteria("hospitalId").is(envKeeper.getHospital());
			query.addCriteria(criteria);
			WriteResult result = mongo.remove(query, MONGOCOL);
			int n = result.getN();
			logger.debug("ws 清除数据成功 hospital->{} 删除数据 {}",envKeeper.getHospital(),n);
		} catch (MongoException e) {
			logger.error("clear mongo person error {}", e);
		}
	}

	/** 
	 * @see com.mytijian.mediator.api.service.ExamQueueChangeService#getPersons()
	 */
	@Override
	public List<ExamQueueInfo> getPersons() {
		logger.debug("ws 同步数据 hospital-> {}", envKeeper.getHospital());
		Query query = new Query();
		Criteria criteria = new Criteria("hospitalId").is(envKeeper.getHospital());
		query.addCriteria(criteria);
		try {
			return mongo.find(query, ExamQueueInfo.class, MONGOCOL);
		} catch (MongoException e) {
			logger.error("get mongo person error {}", e);
		}
		return null;
	}
	
	private synchronized void saveOrUpdatePerson(final ExamQueueInfo person) throws MongoException {
		
		Query query = new Query();
		Criteria criteria = new Criteria("code").is(person.getCode())
							.and("hospitalId").is(envKeeper.getHospital());
		query.addCriteria(criteria);

		ExamQueueInfo mp = mongo.findOne(query, ExamQueueInfo.class, MONGOCOL);

		if (mp==null) {
			mongo.insert(person, MONGOCOL);
		} else {
			// 传过来的版本号大于mongo中的版本号的时候才进行操作
			// 否则认为是由于网络延迟或者其他原因导致的 不进行操作
			logger.debug("{} change to {}", mp.getVersion(), person.getVersion());
			Criteria removeCriteria = criteria.and("version").is(mp.getVersion());
			Query delete = new Query();
			delete.addCriteria(removeCriteria);
			mongo.remove(delete, MONGOCOL);
			mongo.insert(person, MONGOCOL);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Map getOrder(String hisbm) {
		List<Map> orders = mongoOrderService.getMongoOrder(String.format("{\"hisbm\":\"%s\"}", hisbm));
		Map order = null;
		if (orders.size()==0) {
			return null;
		}
		if (orders.size()>=1) {
			order = orders.get(0);
		}
		return order;
	}
	
	@SuppressWarnings("rawtypes")
	private Account getAccount(String hisbm) {
		Account result = null;
		
		Map order = getOrder(hisbm);
	
		Map account = (Map) order.get("account");
		Integer accountId =(Integer) account.get("_id");
		result = accountService.getAccountById(accountId);

		return result;
	}

	/** 
	 * @see com.mytijian.mediator.api.service.ExamQueueChangeService#syncHisAccountCode(java.lang.String, java.lang.String)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void syncHisAccountCode(String ordernum, String hisbm) {
		/*List<Map> mongolists = mongoOrderService.getMongoOrder(String.format("{\"orderNum\":\"%s\"}", ordernum));
		if (mongolists.size()==1) {
			Map order = mongolists.get(0);
			Integer id = Integer.valueOf(String.valueOf(order.get("id")));
			List<Integer> updateId = new ArrayList<>();
			updateId.add(id);
			Map<String, Object> setValueMap = new HashMap<>();
			setValueMap.put("hisbm", hisbm);
			mongoOrderService.updateMongoOrder(updateId, setValueMap);
		} else {
			throw new IllegalStateException("订单:"+ordernum+" 数据错误 应该有1条订单数据 但是查出了 "+mongolists.size()+" 条数据");
		}*/
	}

	/** 
	 * @see com.mytijian.mediator.api.service.ExamQueueChangeService#syncRoomItem(java.util.List)
	 */
	@Override
	public void syncRoomItem(List<RoomItem> rooms) {
		Map<String,Object> map = new HashMap<>();
		map.put("key", "rooms");
		map.put("roomitem", rooms);
		map.put("hospital", envKeeper.getHospital());
		
		Query query = new Query();
		Criteria criteria = new Criteria("key").is("rooms")
					.and("hospital").is(envKeeper.getHospital());
		
		query.addCriteria(criteria);
		try {
			mongo.remove(query, MONGOCOL);
			mongo.save(map, MONGOCOL);
		} catch (MongoException e) {
			logger.error("保存 room 列表失败", e);
		}
		
	}
}
