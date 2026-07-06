package com.dp.plat.core.pojo;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;

/**
 * 用户反馈
 *
 * @author trae
 *
 */
public class Feedback {
    private Long id;

    // 反馈类型（bug-缺陷，suggestion-建议，question-咨询）
    private String type;

    // 标题
    private String title;

    // 内容
    private String content;

    // 联系方式
    private String contact;

    // 状态（open-待处理，processing-处理中，resolved-已解决，closed-已关闭）
    private String status;

    // 创建人
    private String createUser;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    // 更新人
    private String updateUser;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取反馈类型
     *
     * @return type - 反馈类型（bug-缺陷，suggestion-建议，question-咨询）
     */
    public String getType() {
        return type;
    }

    /**
     * 设置反馈类型
     *
     * @param type 反馈类型（bug-缺陷，suggestion-建议，question-咨询）
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取标题
     *
     * @return title - 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取内容
     *
     * @return content - 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取联系方式
     *
     * @return contact - 联系方式
     */
    public String getContact() {
        return contact;
    }

    /**
     * 设置联系方式
     *
     * @param contact 联系方式
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * 获取状态
     *
     * @return status - 状态（open-待处理，processing-处理中，resolved-已解决，closed-已关闭）
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态（open-待处理，processing-处理中，resolved-已解决，closed-已关闭）
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取创建人
     *
     * @return createUser - 创建人
     */
    public String getCreateUser() {
        return createUser;
    }

    /**
     * 设置创建人
     *
     * @param createUser 创建人
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    /**
     * @return createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新人
     *
     * @return updateUser - 更新人
     */
    public String getUpdateUser() {
        return updateUser;
    }

    /**
     * 设置更新人
     *
     * @param updateUser 更新人
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    /**
     * @return updateTime
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
