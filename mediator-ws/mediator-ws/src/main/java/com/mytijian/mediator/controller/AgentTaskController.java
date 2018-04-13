package com.mytijian.mediator.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mytijian.mediator.api.model.MediatorAgentTaskModel;
import com.mytijian.mediator.api.service.MediatorAgentTaskService;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;

@Controller
public class AgentTaskController {

	private Logger logger = LoggerFactory.getLogger(AgentTaskController.class);
	
	@Resource(name = "mediatorAgentTaskService")
	private MediatorAgentTaskService mediatorAgentTaskService;
	
	@Resource(name = "hospitalService")
	private HospitalService hospitalService;
	
	/**
	 * 获取订单任务详细
	 * @param hospitalId
	 * @param status
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/task/getAgentTask", method = RequestMethod.GET, headers = "Accept=application/json")
	public ModelAndView getAgentTask(@RequestParam(value ="hospitalId", required = false) Integer hospitalId,
						 @RequestParam(value = "status", required = false) Integer status,
						 @RequestParam(value = "sign", required = true) String sign,
						 @RequestParam(value = "startIndex", required = false) Integer startIndex,
						 @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		
		if (StringUtils.isEmpty(sign) || !sign.equals("Mytijianws_2016")) {
			return null;
		}
		
		List<MediatorAgentTaskModel> mediatorAgentTaskModels = null;
		try {
			mediatorAgentTaskModels = mediatorAgentTaskService.getAgentTask(hospitalId, status, null, startIndex, pageSize);
		} catch (Exception e) {
			logger.error("AgentTaskController.getAgentTask exception!", e.getMessage());
		}
		// 返回部分字段
		ModelAndView mv = new ModelAndView();
		mv.setViewName("hospital_task");
		mv.addObject("agentTask", getSysAgentTask(mediatorAgentTaskModels));	
		return mv;
	}
	
	/**
	 * 更新订单任务
	 * @param hospitalId 医院编号
	 * @param taskCmd 任务类型
	 * @param openStatus 开启状态（1：开启， 非1关闭）
	 * method = RequestMethod.GET,
	 * @return
	 */
	@RequestMapping(value = "/task/updateAgentTask",  headers = "Accept=application/json")
	public String updateAgentTask(@RequestParam(value ="hospitalId", required = true) Integer hospitalId,
						 @RequestParam(value = "taskCmd", required = true) String taskCmd,
						 @RequestParam(value = "status", required = false) Integer status) {
		status = status == 1 ? 1 : 2;
		int count = mediatorAgentTaskService.updateAgentTaskByHsId(taskCmd, Integer.valueOf(hospitalId), Integer.valueOf(status));
		if (count == -1) {
			// TODO 医院不存在
		}
		return "redirect:/action/task/getAgentTask?sign=Mytijianws_2016";
	}
	
	private List<AgentTaskStatis> getSysAgentTask(List<MediatorAgentTaskModel> mediatorAgentTaskModels) {
		if (mediatorAgentTaskModels == null) {
			return null;
		}
		List<AgentTaskStatis> agentTasksSimple = new ArrayList<AgentTaskStatis>();
		Map<Object, AgentTaskStatis> map = new HashMap<>();
		mediatorAgentTaskModels.forEach(mediatorAgentTaskModel -> {
			int hid = mediatorAgentTaskModel.getHospitalId();
			AgentTaskStatis agentTaskStatis = new AgentTaskStatis();
			agentTaskStatis.setHospitalId(mediatorAgentTaskModel.getHospitalId());
			setCmd(agentTaskStatis, mediatorAgentTaskModel.getTaskCmd(), mediatorAgentTaskModel.getStatus());
			if (map.get(hid) == null) {
				map.put(hid, agentTaskStatis);
			} else {
				agentTaskStatis = map.get(hid);
				setCmd(agentTaskStatis, mediatorAgentTaskModel.getTaskCmd(), mediatorAgentTaskModel.getStatus());
				map.put(hid, agentTaskStatis);
			}
		});

		if (!map.values().isEmpty()) {
			agentTasksSimple = new ArrayList<>(map.values());
			agentTasksSimple.forEach(agentInfo->{
				Hospital hospital = hospitalService.getHospitalBaseInfoById(agentInfo.getHospitalId());
				agentInfo.setHospitalName(hospital.getName());
			});
		}
		
		return agentTasksSimple;
	}
	
	private void setCmd(AgentTaskStatis agentTaskStatis, String cmd, Integer status) {
		switch (cmd) {
		case "orderdone":
			agentTaskStatis.setOrderdone(cmd);
			agentTaskStatis.setOrderdoneStatus(status);
			break;
		case "order":
			agentTaskStatis.setOrder(cmd);
			agentTaskStatis.setOrderStatus(status);
			break;

		case "examreport":
			agentTaskStatis.setExamreport(cmd);
			agentTaskStatis.setExamreportStatus(status);
			break;
		
		case "examitem":
			agentTaskStatis.setExamitem(cmd);
			agentTaskStatis.setExamitemStatus(status);
			break;
		
		case "company":
			agentTaskStatis.setCompany(cmd);
			agentTaskStatis.setCompanyStatus(status);
			break;
		
		case "immediateorder":
			agentTaskStatis.setImmediateorder(cmd);
			agentTaskStatis.setImmediateorderStatus(status);
			break;
		
		case "alarmOrder":
			agentTaskStatis.setAlarmOrder(cmd);
			agentTaskStatis.setAlarmOrderStatus(status);
			break;
		
		case "deleteOrder":
			agentTaskStatis.setDeleteOrder(cmd);
			agentTaskStatis.setDeleteOrderStatus(status);
			break;
		
		case "syncAll":
			agentTaskStatis.setSyncAll(cmd);
			agentTaskStatis.setSyncAllStatus(status);
			break;
		
		case "syncMytijian":
			agentTaskStatis.setSyncMytijian(cmd);
			agentTaskStatis.setSyncMytijianStatus(status);
			break;
		
		case "template":
			agentTaskStatis.setTemplate(cmd);
			agentTaskStatis.setTemplateStatus(status);
			break;
		
		case "INIT_CMD":
			agentTaskStatis.setINIT_CMD(cmd);
			agentTaskStatis.setINIT_CMDStatus(status);
			break;
		
		case "examqueue":
			agentTaskStatis.setExamqueue(cmd);
			agentTaskStatis.setExamitemStatus(status);
			break;

		case "meal":
			agentTaskStatis.setMeal(cmd);
			agentTaskStatis.setMealStatus(status);
			break;
		case "newCompany":
			agentTaskStatis.setNewCompany(cmd);
			agentTaskStatis.setNewCompanyStatus(status);
			break;
		case "orderPrint":
			agentTaskStatis.setOrderPrint(cmd);
			agentTaskStatis.setOrderPrintStatus(status);
			break;
		default:
			break;
		}
	}
	
	public class AgentTaskStatis extends MediatorAgentTaskModel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3107669372694240441L;
		private String orderdone; //=syncOrderDoneTask 回单 ok
		private Integer orderdoneStatus;
		private String order; //=syncOrderTask 订单同步  ok
		private Integer orderStatus;
		private String examreport;//=syncExamReportTask 
		private Integer examreportStatus;
		private String examitem;//=syncExamItemTask 单项同步 ok
		private Integer examitemStatus;
		private String onereport;//=syncOneExamReportTask 
		private Integer onereportStatus;
		private String company; //=syncExamCompanyTask 单位同步（old）
		private Integer companyStatus;
		private String immediateorder;//=syncImmediateOrderTask 即时订单任务 ok
		private Integer immediateorderStatus;
		private String alarmOrder;//=syncAlarmOrderTask 告警订单 ok
		private Integer alarmOrderStatus;
		private String deleteOrder;//=syncDeletedOrderTask 删除订单 ok
		private Integer deleteOrderStatus;
		private String syncAll;//=syncAllExamReportTask 全量同步
		private Integer syncAllStatus;
		private String syncMytijian;//=syncMytijianExamReportTask 增量同步
		private Integer syncMytijianStatus;
		private String template;//=syncTplInfoTask
		private Integer templateStatus;
		private String INIT_CMD;//=template 初始化
		private Integer INIT_CMDStatus;
		private String examqueue;//=examQueueTask 体检队列
		private Integer examqueueStatus;
		private String hospitalName;
		private String meal; // 套餐
		private Integer mealStatus;
		private String newCompany; // 新单位同步
		private Integer newCompanyStatus;
		private String orderPrint; // 打印同步
		private Integer orderPrintStatus;
		public String getOrderdone() {
			return orderdone;
		}
		public void setOrderdone(String orderdone) {
			this.orderdone = orderdone;
		}
		public String getOrder() {
			return order;
		}
		public void setOrder(String order) {
			this.order = order;
		}
		public String getExamreport() {
			return examreport;
		}
		public void setExamreport(String examreport) {
			this.examreport = examreport;
		}
		public String getExamitem() {
			return examitem;
		}
		public void setExamitem(String examitem) {
			this.examitem = examitem;
		}
		public String getOnereport() {
			return onereport;
		}
		public void setOnereport(String onereport) {
			this.onereport = onereport;
		}
		public String getCompany() {
			return company;
		}
		public void setCompany(String company) {
			this.company = company;
		}
		public String getImmediateorder() {
			return immediateorder;
		}
		public void setImmediateorder(String immediateorder) {
			this.immediateorder = immediateorder;
		}
		public String getAlarmOrder() {
			return alarmOrder;
		}
		public void setAlarmOrder(String alarmOrder) {
			this.alarmOrder = alarmOrder;
		}
		public String getDeleteOrder() {
			return deleteOrder;
		}
		public void setDeleteOrder(String deleteOrder) {
			this.deleteOrder = deleteOrder;
		}
		public String getSyncAll() {
			return syncAll;
		}
		public void setSyncAll(String syncAll) {
			this.syncAll = syncAll;
		}
		public String getSyncMytijian() {
			return syncMytijian;
		}
		public void setSyncMytijian(String syncMytijian) {
			this.syncMytijian = syncMytijian;
		}
		public String getTemplate() {
			return template;
		}
		public void setTemplate(String template) {
			this.template = template;
		}
		public String getINIT_CMD() {
			return INIT_CMD;
		}
		public void setINIT_CMD(String iNIT_CMD) {
			INIT_CMD = iNIT_CMD;
		}
		public String getExamqueue() {
			return examqueue;
		}
		public void setExamqueue(String examqueue) {
			this.examqueue = examqueue;
		}
		public Integer getOrderdoneStatus() {
			return orderdoneStatus;
		}
		public void setOrderdoneStatus(Integer orderdoneStatus) {
			this.orderdoneStatus = orderdoneStatus;
		}
		public Integer getOrderStatus() {
			return orderStatus;
		}
		public void setOrderStatus(Integer orderStatus) {
			this.orderStatus = orderStatus;
		}
		public Integer getExamreportStatus() {
			return examreportStatus;
		}
		public void setExamreportStatus(Integer examreportStatus) {
			this.examreportStatus = examreportStatus;
		}
		public Integer getExamitemStatus() {
			return examitemStatus;
		}
		public void setExamitemStatus(Integer examitemStatus) {
			this.examitemStatus = examitemStatus;
		}
		public Integer getOnereportStatus() {
			return onereportStatus;
		}
		public void setOnereportStatus(Integer onereportStatus) {
			this.onereportStatus = onereportStatus;
		}
		public Integer getCompanyStatus() {
			return companyStatus;
		}
		public void setCompanyStatus(Integer companyStatus) {
			this.companyStatus = companyStatus;
		}
		public Integer getImmediateorderStatus() {
			return immediateorderStatus;
		}
		public void setImmediateorderStatus(Integer immediateorderStatus) {
			this.immediateorderStatus = immediateorderStatus;
		}
		public Integer getAlarmOrderStatus() {
			return alarmOrderStatus;
		}
		public void setAlarmOrderStatus(Integer alarmOrderStatus) {
			this.alarmOrderStatus = alarmOrderStatus;
		}
		public Integer getDeleteOrderStatus() {
			return deleteOrderStatus;
		}
		public void setDeleteOrderStatus(Integer deleteOrderStatus) {
			this.deleteOrderStatus = deleteOrderStatus;
		}
		public Integer getSyncAllStatus() {
			return syncAllStatus;
		}
		public void setSyncAllStatus(Integer syncAllStatus) {
			this.syncAllStatus = syncAllStatus;
		}
		public Integer getSyncMytijianStatus() {
			return syncMytijianStatus;
		}
		public void setSyncMytijianStatus(Integer syncMytijianStatus) {
			this.syncMytijianStatus = syncMytijianStatus;
		}
		public Integer getTemplateStatus() {
			return templateStatus;
		}
		public void setTemplateStatus(Integer templateStatus) {
			this.templateStatus = templateStatus;
		}
		public Integer getINIT_CMDStatus() {
			return INIT_CMDStatus;
		}
		public void setINIT_CMDStatus(Integer iNIT_CMDStatus) {
			INIT_CMDStatus = iNIT_CMDStatus;
		}
		public Integer getExamqueueStatus() {
			return examqueueStatus;
		}
		public void setExamqueueStatus(Integer examqueueStatus) {
			this.examqueueStatus = examqueueStatus;
		}
		public String getHospitalName() {
			return hospitalName;
		}
		public void setHospitalName(String hospitalName) {
			this.hospitalName = hospitalName;
		}
		public String getMeal() {
			return meal;
		}
		public void setMeal(String meal) {
			this.meal = meal;
		}
		public Integer getMealStatus() {
			return mealStatus;
		}
		public void setMealStatus(Integer mealStatus) {
			this.mealStatus = mealStatus;
		}
		public String getNewCompany() {
			return newCompany;
		}
		public void setNewCompany(String newCompany) {
			this.newCompany = newCompany;
		}
		public Integer getNewCompanyStatus() {
			return newCompanyStatus;
		}
		public void setNewCompanyStatus(Integer newCompanyStatus) {
			this.newCompanyStatus = newCompanyStatus;
		}
		public String getOrderPrint() {
			return orderPrint;
		}
		public void setOrderPrint(String orderPrint) {
			this.orderPrint = orderPrint;
		}
		public Integer getOrderPrintStatus() {
			return orderPrintStatus;
		}
		public void setOrderPrintStatus(Integer orderPrintStatus) {
			this.orderPrintStatus = orderPrintStatus;
		}
	}
	
}
