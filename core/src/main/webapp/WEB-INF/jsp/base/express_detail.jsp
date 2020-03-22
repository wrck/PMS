<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="box box-primary mb-0">
	<!-- /.box-header -->
	<div class="box-body">
		<c:if test="${empty expressList}">
			<table class="table table-bordered table-striped table-hover">
				<tr><td>无快递物流信息</td></tr>
			</table>
		</c:if>
		<c:if test="${!empty expressList}">
			<div class="pull-left pb-1" style="width:200px;">
				<label>快递单号：</label>
				<select onchange="changeExpress(this)">
				<c:forEach var="express" items="${expressList}" varStatus="status">
					<option value="${status.index}">${express.number}</option>
				</c:forEach>
				</select>
			</div>
			<c:forEach var="express" items="${expressList}" varStatus="status">
				<div id="expressInfo${status.index}" class="pull-left" style="display:none;">
					<label>快递公司：</label><span class="pr-1">${express.expName}</span>	
					<label>签收状态：</label>
					<c:if test="${express.isCheck == true}">
						<span class="pr-1">已签收</span>	
					</c:if>
					<c:if test="${express.isCheck == false}">
						<span class="pr-1">未签收</span>	
					</c:if>
					<label>收货人：</label><span class="pr-1">${express.receiveName}</span>
				</div>
				<table id="transitInfo${status.index}" class="table table-bordered table-striped table-hover detail" style="display:none;">
					<c:forEach var="item" items="${express.contexts}">
						<tr><td  class="text-center"> <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${item.time}" /></td><td>${item.context}</td></tr>
					</c:forEach>
				</table>
			</c:forEach>
		</c:if>
	</div>
	<!-- /.box-body -->
</div>
