package com.dp.plat.activiti.unifytask.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.dp.plat.activiti.unifytask.entity.UnifyTask;


/**
 *
 * @author w02611
 */
public class SeeyonTask extends UnifyTask {
	/**
	 * <b>必填</b>，第三方待办主键（保证唯一）
	 */
	@NotNull(message = "第三方待办主键不能为空")
	private String taskId;
	/**
	 * <b>必填</b>，为第三方配置的系统注册编码
	 */
	@NotNull(message = "第三方配置的系统注册编码不能为空")
	private String registerCode;
	/**
	 * <b>必填</b>，待办标题
	 */
	@NotNull(message = "待办标题不能为空")
	private String title;
	/**
	 * <b>非必填</b>，第三方待办发起人主键（保证唯一）
	 */
	private String thirdSenderId;
	/**
	 * <b>必填</b>，第三方待办发起人姓名
	 */
	@NotNull(message = "第三方待办发起人姓名不能为空")
	private String senderName;
	/**
	 * <b>必填</b>，第三方待办接收人主键（保证唯一)
	 */
	@NotNull(message = "第三方待办接收人主键不能为空")
	private String thirdReceiverId;
	/**
	 * <b>必填</b>，待办创建时间（格式：yyyy-MM-dd HH:mm:ss）
	 */
	@NotNull(message = "待办创建时间不能为空")
	private Date creationDate;
	/**
	 * <b>必填</b>，状态：0:未办理；1:已办理
	 */
	@NotNull(message = "状态不能为空")
	private String state;
	/**
	 * <b>非必填</b>，处理后状态：0/1/2/3同意已办/不同意已办/取消/驳回
	 */
	private String subState;
	/**
	 * <b>非必填</b>，原生应用下载地址（仅3和6类型可选）
	 */
	private String content;
	/**
	 * <b>非必填</b>，PC端穿透链接
	 */
	private String url;
	/**
	 * <b>非必填</b>，移动端穿透链接
	 */
	private String h5url;
	/**
	 * <b>非必填</b>，原生应用穿透命令，穿透命令需要按这个顺序：iphone|ipad|android|wp
	 */
	private String appParam;
	/**
	 * <b>免绑定必填字段</b>， 登录名称/人员编码/手机号/电子邮件
	 */
	private String noneBindingSender;
	/**
	 * <b>免绑定必填字段</b>， 登录名称/人员编码/手机号/电子邮件
	 */
	private String noneBindingReceiver;
	
	/**
	 * <b>必填</b>，第三方待办主键（保证唯一）
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}
	/**
	 * <b>必填</b>，第三方待办主键（保证唯一）
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	/**
	 * <b>必填</b>，为第三方配置的系统注册编码
	 * @return the registerCode
	 */
	public String getRegisterCode() {
		return registerCode;
	}
	/**
	 * <b>必填</b>，为第三方配置的系统注册编码
	 * @param registerCode the registerCode to set
	 */
	public void setRegisterCode(String registerCode) {
		this.registerCode = registerCode;
	}
	/**
	 * <b>必填</b>，待办标题
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * <b>必填</b>，待办标题
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * <b>非必填</b>，第三方待办发起人主键（保证唯一）
	 * @return the thirdSenderId
	 */
	public String getThirdSenderId() {
		return thirdSenderId;
	}
	/**
	 * <b>非必填</b>，第三方待办发起人主键（保证唯一）
	 * @param thirdSenderId the thirdSenderId to set
	 */
	public void setThirdSenderId(String thirdSenderId) {
		this.thirdSenderId = thirdSenderId;
	}
	/**
	 * <b>必填</b>，第三方待办发起人姓名
	 * @return the senderName
	 */
	public String getSenderName() {
		return senderName;
	}
	/**
	 * <b>必填</b>，第三方待办发起人姓名
	 * @param senderName the senderName to set
	 */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	/**
	 * <b>必填</b>，第三方待办接收人主键（保证唯一)
	 * @return the thirdReceiverId
	 */
	public String getThirdReceiverId() {
		return thirdReceiverId;
	}
	/**
	 * <b>必填</b>，第三方待办接收人主键（保证唯一)
	 * @param thirdReceiverId the thirdReceiverId to set
	 */
	public void setThirdReceiverId(String thirdReceiverId) {
		this.thirdReceiverId = thirdReceiverId;
	}
	/**
	 * <b>必填</b>，待办创建时间（格式：yyyy-MM-dd HH:mm:ss）
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	/**
	 * <b>必填</b>，待办创建时间（格式：yyyy-MM-dd HH:mm:ss）
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * <b>必填</b>，状态：0:未办理；1:已办理
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * <b>必填</b>，状态：0:未办理；1:已办理
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * <b>非必填</b>，处理后状态：0/1/2/3同意已办/不同意已办/取消/驳回
	 * @return the subState
	 */
	public String getSubState() {
		return subState;
	}
	/**
	 * <b>非必填</b>，处理后状态：0/1/2/3同意已办/不同意已办/取消/驳回
	 * @param subState the subState to set
	 */
	public void setSubState(String subState) {
		this.subState = subState;
	}
	/**
	 * <b>非必填</b>，原生应用下载地址（仅3和6类型可选）
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * <b>非必填</b>，原生应用下载地址（仅3和6类型可选）
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * <b>非必填</b>，PC端穿透链接
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * <b>非必填</b>，PC端穿透链接
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * <b>非必填</b>，移动端穿透链接
	 * @return the h5url
	 */
	public String getH5url() {
		return h5url;
	}
	/**
	 * <b>非必填</b>，移动端穿透链接
	 * @param h5url the h5url to set
	 */
	public void setH5url(String h5url) {
		this.h5url = h5url;
	}
	/**
	 * <b>非必填</b>，原生应用穿透命令，穿透命令需要按这个顺序：iphone|ipad|android|wp
	 * @return the appParam
	 */
	public String getAppParam() {
		return appParam;
	}
	/**
	 * <b>非必填</b>，原生应用穿透命令，穿透命令需要按这个顺序：iphone|ipad|android|wp
	 * @param appParam the appParam to set
	 */
	public void setAppParam(String appParam) {
		this.appParam = appParam;
	}
	/**
	 * <b>免绑定必填字段</b>， 登录名称/人员编码/手机号/电子邮件
	 * @return the noneBindingSender
	 */
	public String getNoneBindingSender() {
		return noneBindingSender;
	}
	/**
	 * <b>免绑定必填字段</b>， 登录名称/人员编码/手机号/电子邮件
	 * @param noneBindingSender the noneBindingSender to set
	 */
	public void setNoneBindingSender(String noneBindingSender) {
		this.noneBindingSender = noneBindingSender;
	}
	/**
	 * <b>免绑定必填字段</b>， 登录名称/人员编码/手机号/电子邮件
	 * @return the noneBindingReceiver
	 */
	public String getNoneBindingReceiver() {
		return noneBindingReceiver;
	}
	/**
	 * <b>免绑定必填字段</b>， 登录名称/人员编码/手机号/电子邮件
	 * @param noneBindingReceiver the noneBindingReceiver to set
	 */
	public void setNoneBindingReceiver(String noneBindingReceiver) {
		this.noneBindingReceiver = noneBindingReceiver;
	}


}