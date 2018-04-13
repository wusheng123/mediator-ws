<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
	String contexPath = request.getContextPath();
%>

<link rel="stylesheet" type="text/css" href="<%=contexPath%>/css/table_form.css" />
<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script type="text/javascript" src="<%=contexPath%>/artDialog/jquery.artDialog.js?skin=default"></script>
<script type="text/javascript">
	function setChecked(id, hid, taskCmd, status) {
		window.location.href="updateAgentTask?hospitalId="+hid+"&taskCmd="+taskCmd+"&status="+status;
	}
	
	function addHospitalTask() {
		art.dialog({
					title : '添加医院',
					width :'600',
					  opacity: .10,
					  lock : true,
					content :
							'<form action="updateAgentTask" method="post" name="addform">'
							+'<table class="table_form">'
							+'<tr><td>医院编号<input type="text" name="hospitalId" id="hospitalId"/></td></tr>'
							+'<tr><td><input type="hidden" name="taskCmd" id="taskCmd" value="order" />'
							+'<input type="hidden" name="status" id="status" value="2" /></td></tr>'
							+'<tr><td>医院名称<input type="text" name="hospitalName" value="..." readonly/></td></tr></table>'
							+'</form>',
					init : function() {
						var aa = 1;
					},		
					ok : function() {						
						$("form[name='addform']").submit();
					},
					cancelVal : '关闭',
					cancel : true
				});
	}

	
</script>
</head>
<body>
	<div class="pad-lr-10" style="margin-top: 10px;">
		<h2 align="center">订单任务统计明细</h2>
		<div align="right">
				<tr align="right">
				<td><input type="button" value="添加医院"  onclick="return addHospitalTask()" align="right"/></td>
			</tr>
		</div>
		<form method="post" action="/task/updateAgentTask" enctype="multipart/form-data">
			<div class="table-list" align="center">
			
				<table style="width: 100%; cellspacing: 0;">
					<thead>
						<th align="center">医院编号</th>
						<th align="center">医院名称</th>
						<th align="center">订单同步任务</th>
						<th align="center">即时订单任务</th>
						<th align="center">告警订单任务</th>
						<th align="center">删除订单任务</th>
						<th align="center">回单任务</th>
						<th align="center">单项同步任务</th>
						<!-- <th align="center">老单位同步任务</th> -->
						<th align="center">新单位同步任务</th>
						<!-- 体检报告全量同步 -->
						<th align="center">全量报告</th>
						<th align="center">增量报告</th>
						<th align="center">套餐同步</th>
						<th align="center">打印同步</th>
					</thead>
					
					<c:forEach var="agent" items="${agentTask}" varStatus="c">
						<td align="center">${agent.hospitalId}</td>
						<td align="center">${agent.hospitalName}</td>
						<td align="center"><c:choose>
							<c:when test="${agent.order !=null && agent.orderStatus==1}">
									<input type="checkbox" checked="checked" name="taskCmd" id="${agent.hospitalId}_${agent.order}" onclick="setChecked('${agent.hospitalId}_${agent.order}', ${agent.hospitalId}, 'order', 2)"/><font color="red">开启</font>
				       		</c:when>
				       		<c:when test="${agent.order !=null && agent.orderStatus==2}">
									<input type="checkbox" name="taskCmd" id="${agent.hospitalId}_${agent.order}" onclick="setChecked('${agent.hospitalId}_${agent.order}', ${agent.hospitalId}, 'order', 1)"/><font color="green">关闭</font>
				       		</c:when>
							<c:otherwise>
									<input type="checkbox" name="taskCmd" id="${agent.hospitalId}_${agent.order}"  onchange="setChecked('${agent.hospitalId}_${agent.order}', ${agent.hospitalId}, 'order', 1)"/><font color="green">关闭</font>
				       		</c:otherwise>
							</c:choose></td>
						<td align="center"><c:choose>
							<c:when test="${agent.immediateorder !=null && agent.immediateorderStatus==1}">
									<input type="checkbox" checked="checked" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.immediateorder}', ${agent.hospitalId}, 'immediateorder', 2)"/><font color="red">开启</font>
				       		</c:when>
				       		<c:when test="${agent.immediateorder !=null && agent.immediateorderStatus==2}">
									<input type="checkbox"  name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.immediateorder}', ${agent.hospitalId}, 'immediateorder', 1)"/><font color="green">关闭</font>
				       		</c:when>
								<c:otherwise>
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.immediateorder}', ${agent.hospitalId}, 'immediateorder', 1)"/><font color="green">关闭</font>
				       		</c:otherwise>
							</c:choose></td>
						<td align="center"><c:choose>
								<c:when test="${agent.alarmOrder!=null && agent.alarmOrderStatus==1}">
									<input type="checkbox" checked="checked" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.alarmOrder}', ${agent.hospitalId}, 'alarmOrder', 2)"/><font color="red">开启</font>
				       		</c:when>
				       			<c:when test="${agent.alarmOrder!=null && agent.alarmOrderStatus==2}">
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.alarmOrder}', ${agent.hospitalId}, 'alarmOrder', 1)"/><font color="green">关闭</font>
				       		</c:when>
								<c:otherwise>
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.alarmOrder}', ${agent.hospitalId}, 'alarmOrder', 1)"/><font color="green">关闭</font>
				       		</c:otherwise>
							</c:choose></td>
						<td align="center"><c:choose>
							<c:when test="${agent.deleteOrder!=null && agent.deleteOrderStatus==1}">
									<input type="checkbox" checked="checked" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.deleteOrder}', ${agent.hospitalId}, 'deleteOrder', 2)"/><font color="red">开启</font>
				       		</c:when>
				       		<c:when test="${agent.deleteOrder!=null && agent.deleteOrderStatus==2}">
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.deleteOrder}', ${agent.hospitalId}, 'deleteOrder', 1)"/><font color="green">关闭</font>
				       		</c:when>
								<c:otherwise>
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.deleteOrder}', ${agent.hospitalId}, 'deleteOrder', 1)"/><font color="green">关闭</font>
				       		</c:otherwise>
							</c:choose></td>
						<td align="center"><c:choose>
								<c:when test="${agent.orderdone !=null && agent.orderdoneStatus==1}">
									<input type="checkbox" checked="checked" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.orderdone}', ${agent.hospitalId}, 'orderdone', 2)"/><font color="red">开启</font>
				       		</c:when>
				       		<c:when test="${agent.orderdone !=null && agent.orderdoneStatus==2}">
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.orderdone}', ${agent.hospitalId}, 'orderdone', 1)"/><font color="green">关闭</font>
				       		</c:when>
								<c:otherwise>
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.orderdone}', ${agent.hospitalId}, 'orderdone', 1)"/><font color="green">关闭</font>
				       		</c:otherwise>
							</c:choose></td>
						<td align="center"><c:choose>
								<c:when test="${agent.examitem !=null && agent.examitemStatus==1}">
									<input type="checkbox" checked="checked" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.examitem}', ${agent.hospitalId}, 'examitem', 2)"/><font color="red">开启</font>
				       		</c:when>
				       		<c:when test="${agent.examitem !=null && agent.examitemStatus==2}">
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.examitem}', ${agent.hospitalId}, 'examitem', 1)"/><font color="green">关闭</font>
				       		</c:when>
								<c:otherwise>
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.examitem}', ${agent.hospitalId}, 'examitem', 1)"/><font color="green">关闭</font>
				       		</c:otherwise>
							</c:choose></td>
						<td align="center"><c:choose>
							<c:when test="${agent.newCompany !=null && agent.newCompanyStatus==1}">
									<input type="checkbox" checked="checked" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.examitem}', ${agent.hospitalId}, 'newCompany', 2)"/><font color="red">开启</font>
				       		</c:when>
				       		<c:when test="${agent.newCompany !=null && agent.newCompanyStatus==2}">
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.newCompany}', ${agent.hospitalId}, 'newCompany', 1)"/><font color="green">关闭</font>
				       		</c:when>
								<c:otherwise>
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.newCompany}', ${agent.hospitalId}, 'newCompany', 1)"/><font color="green">关闭</font>
				       		</c:otherwise>
							</c:choose></td>
						<td align="center"><c:choose>
							<c:when test="${agent.syncAll !=null && agent.syncAllStatus==1}">
									<input type="checkbox" checked="checked" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.syncAll}', ${agent.hospitalId}, 'syncAll', 2)"/><font color="red">开启</font>
				       		</c:when>
				       		<c:when test="${agent.syncAll !=null && agent.syncAllStatus==2}">
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.syncAll}', ${agent.hospitalId}, 'syncAll', 1)"/><font color="green">关闭</font>
				       		</c:when>
							<c:otherwise>
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.syncAll}', ${agent.hospitalId}, 'syncAll', 1)"/><font color="green">关闭</font>
				       		</c:otherwise>
							</c:choose></td>
						<td align="center"><c:choose>
								<c:when test="${agent.syncMytijian !=null && agent.syncMytijianStatus==1}">
									<input type="checkbox" checked="checked" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.syncMytijian}', ${agent.hospitalId}, 'syncMytijian', 2)"/><font color="red">开启</font>
				       		</c:when>
				       		<c:when test="${agent.syncMytijian !=null && agent.syncMytijianStatus==2}">
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.syncMytijian}', ${agent.hospitalId}, 'syncMytijian', 1)"/><font color="green">关闭</font>
				       		</c:when>
								<c:otherwise>
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.syncMytijian}', ${agent.hospitalId}, 'syncMytijian', 1)"/><font color="green">关闭</font>
				       		</c:otherwise>
							</c:choose></td>
						<td align="center"><c:choose>
								<c:when test="${agent.meal !=null && agent.mealStatus==1}">
									<input type="checkbox" checked="checked" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.meal}', ${agent.hospitalId}, 'meal', 2)"/><font color="red">开启</font>
				       		</c:when>
				       		<c:when test="${agent.meal !=null && agent.mealStatus==2}">
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.meal}', ${agent.hospitalId}, 'meal', 1)"/><font color="green">关闭</font>
				       		</c:when>
								<c:otherwise>
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.meal}', ${agent.hospitalId}, 'meal', 1)"/><font color="green">关闭</font>
				       		</c:otherwise>
							</c:choose></td>
						<td align="center"><c:choose>
								<c:when test="${agent.orderPrint !=null && agent.orderPrintStatus==1}">
									<input type="checkbox" checked="checked" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.orderPrint}', ${agent.hospitalId}, 'orderPrint', 2)"/><font color="red">开启</font>
				       		</c:when>
				       		<c:when test="${agent.orderPrint !=null && agent.orderPrintStatus==2}">
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.orderPrint}', ${agent.hospitalId}, 'orderPrint', 1)"/><font color="green">关闭</font>
				       		</c:when>
								<c:otherwise>
									<input type="checkbox" name="taskCmd" onclick="setChecked('${agent.hospitalId}_${agent.orderPrint}', ${agent.hospitalId}, 'orderPrint', 1)"/><font color="green">关闭</font>
				       		</c:otherwise>
							</c:choose></td>	
						<c:if test="${c.count % 13 == 0}" />
						<tr />
					</c:forEach>
				</table>
			</div>
		</form>
	</div>
</body>

</html>