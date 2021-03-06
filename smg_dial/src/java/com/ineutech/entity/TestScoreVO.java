package com.ineutech.entity;

import java.util.Date;

/**
 * 
 * @name: TestScoreVO 
 * @description: 硬件上传数据VO
 * @date 2018年2月1日 下午4:16:23
 * @author liululu
 */
public class TestScoreVO {

	private Integer id;
	private String mac;// 设备MAC
	private Date createTime;// 时间戳

	private String caijiTime;
	private Integer score;// 值
	private Integer hardElec;// 电量

	private Integer testId;// 测试id
	private Integer hardId;// 拨盘id
	private Integer humanId;// 人员id
	private Integer seatNo;// 座位号
	private String userGroup;// 分组

	private Integer deviceStatus;// 设备状态

	private String insertTime;

	public TestScoreVO() {
	}

	public TestScoreVO(String mac, Date createTime, String insertTime,
			String caijiTime, Integer score, Integer testId,
			Integer hardId, Integer humanId, Integer seatNo, String userGroup) {
		this.mac = mac;
		this.createTime = createTime;
		this.insertTime = insertTime;
		this.caijiTime = caijiTime;
		this.score = score;
		this.testId = testId;
		this.hardId = hardId;
		this.humanId = humanId;
		this.seatNo = seatNo;
		this.userGroup = userGroup;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCaijiTime() {
		return caijiTime;
	}

	public void setCaijiTime(String caijiTime) {
		this.caijiTime = caijiTime;
	}

	public Integer getHardElec() {
		return hardElec;
	}

	public void setHardElec(Integer hardElec) {
		this.hardElec = hardElec;
	}

	public Integer getTestId() {
		return testId;
	}

	public void setTestId(Integer testId) {
		this.testId = testId;
	}

	public Integer getHardId() {
		return hardId;
	}

	public void setHardId(Integer hardId) {
		this.hardId = hardId;
	}

	public Integer getHumanId() {
		return humanId;
	}

	public void setHumanId(Integer humanId) {
		this.humanId = humanId;
	}

	public Integer getSeatNo() {
		return seatNo;
	}

	public void setSeatNo(Integer seatNo) {
		this.seatNo = seatNo;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public Integer getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(Integer deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

}
