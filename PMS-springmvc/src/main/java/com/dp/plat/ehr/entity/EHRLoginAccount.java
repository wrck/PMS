package com.dp.plat.ehr.entity;

import javax.persistence.Id;

public class EHRLoginAccount {

	@Id
    private Integer id;

    private String title;

    private String account;

    private Integer empID;

    private String workNo;

    private String name;

    private Integer isDisabled;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return empID
     */
    public Integer getEmpID() {
        return empID;
    }

    /**
     * @param empID
     */
    public void setEmpID(Integer empID) {
        this.empID = empID;
    }

    /**
     * @return workNo
     */
    public String getWorkNo() {
        return workNo;
    }

    /**
     * @param workNo
     */
    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return isDisabled
     */
    public Integer getIsDisabled() {
        return isDisabled;
    }

    /**
     * @param isDisabled
     */
    public void setIsDisabled(Integer isDisabled) {
        this.isDisabled = isDisabled;
    }

}