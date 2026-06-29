package com.dp.plat.param;

import com.dp.plat.data.bean.OrderTodoBean;

public class OrderTodoParam {
	private OrderTodoBean orderTodoBean;
	private DisplayParam displayParam;
	public OrderTodoBean getOrderTodoBean() {
		return orderTodoBean;
	}
	public void setOrderTodoBean(OrderTodoBean orderTodoBean) {
		this.orderTodoBean = orderTodoBean;
	}
	public DisplayParam getDisplayParam() {
		return displayParam;
	}
	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}
}
