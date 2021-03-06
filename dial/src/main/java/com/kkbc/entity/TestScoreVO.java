package com.kkbc.entity;

import java.util.Date;

/**
 * 硬件上传数据VO
 * @author liululu
 *
 */
public class TestScoreVO {
	private int id;
	private String mac;//设备MAC
	private Date create_time;//时间戳
	
	private String caiji_time;
	private int model;//模式: 1拨盘/2赞踩/3打分
	private int score;//值
	private int hard_elec;//电量
	
	private int test_id;//测试id
	private int hard_id;//拨盘id
	private int human_id;//人员id
	private Integer seat_no;//座位号
	private String user_group;//分组
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public int getModel() {
		return model;
	}
	public void setModel(int model) {
		this.model = model;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getHard_elec() {
		return hard_elec;
	}
	public void setHard_elec(int hard_elec) {
		this.hard_elec = hard_elec;
	}
	public int getTest_id() {
		return test_id;
	}
	public void setTest_id(int test_id) {
		this.test_id = test_id;
	}
	public int getHard_id() {
		return hard_id;
	}
	public void setHard_id(int hard_id) {
		this.hard_id = hard_id;
	}
	public int getHuman_id() {
		return human_id;
	}
	public void setHuman_id(int human_id) {
		this.human_id = human_id;
	}
	public Integer getSeat_no() {
		return seat_no;
	}
	public void setSeat_no(Integer seat_no) {
		this.seat_no = seat_no;
	}
	public String getUser_group() {
		return user_group;
	}
	public void setUser_group(String user_group) {
		this.user_group = user_group;
	}
	public String getCaiji_time() {
		return caiji_time;
	}
	public void setCaiji_time(String caiji_time) {
		this.caiji_time = caiji_time;
	}

}
