package com.ineutech.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import cn.ineutech.tpms.tcp.TPMSConsts;
import cn.ineutech.tpms.tcp.server.SessionManager;
import cn.ineutech.tpms.tcp.vo.Session;

import com.ineutech.dao.DeviceDao;
import com.ineutech.entity.DeviceData;
import com.ineutech.entity.Hard;
import com.ineutech.entity.TestInfo;
import com.ineutech.entity.TestScorePackage;
import com.ineutech.entity.TestScoreVO;
import com.ineutech.entity.TestUser;
import com.ineutech.service.DeviceService;

public class DeviceServiceImpl implements DeviceService {
	private SessionManager sessionManager = SessionManager.getInstance();
	@Resource
	private DeviceDao deviceDao;

	@Override
	public int saveData(TestInfo testInfo, Session session, TestScoreVO data) {
		data.setDeviceStatus(TestUser.DEVICE_STATUS_SCORE);
		
		if (session.getScore()==null||data.getScore().intValue()!=session.getScore().intValue()) {
			deviceDao.updScoreToNow(data);
		}
		
//		if (TPMSConsts.PERIOD_SCORE_START.equals(testInfo.getPeriod())) {
			deviceDao.saveData(data);
//		}
		
		return 1;
	}

	@Override
	public TestUser getUser(TestUser user) {
		return deviceDao.getUser(user);
	}

	@Override
	public int saveHardInfo(Hard hard) {
		return deviceDao.saveHardInfo(hard);
	}

	@Override
	public int updEle(Hard hard) {
		return deviceDao.updEle(hard);
	}

	@Override
	public Hard getByMac(String mac) {
		return deviceDao.getByMac(mac);
	}

	@Override
	public int updateDeviceStatus(TestUser testUser) {
		return deviceDao.updateDeviceStatus(testUser);
	}

	@Override
	public List<TestUser> getTestUserFromBind(int testId) {
		return deviceDao.getTestUserFromBind(testId);
	}

	@Override
	public TestUser getUserFromNow(String mac) {
		return deviceDao.getUserFromNow(mac);
	}

	@Override
	public List<Hard> getTestDevice(int testId) {
		return deviceDao.getTestDevice(testId);
	}

	@Override
	public Object saveNowInfo(int testId) {
		return deviceDao.saveNowInfo(testId);
	}

	@Override
	public TestInfo getTestInfoById(int testId) {
		return deviceDao.getTestInfoById(testId);
	}

	@Override
	public int savePackageData(List<TestScorePackage> data) {
		return deviceDao.savePackageData(data);
	}

	@Override
	public int updScoreAndStatus(TestScoreVO data) {
		return deviceDao.updScoreAndStatus(data);
	}

	@Override
	public long saveBrainData(Session session, DeviceData data) {
		
		// 处理测试在正常状态下(非暂停)的数据
		if (sessionManager.getTestInfo() != null
				&& session.getTestUser() != null
				&& TPMSConsts.STATUS_BACK.equals(sessionManager.getTestInfo().getStatus())) {
			if (TPMSConsts.PERIOD_BEFORE.equals(sessionManager.getTestInfo().getPeriod()) 
					&& (session.getThreshold() == null || data.getAttention() > session
							.getThreshold())) {
				// 更新脑电阈值
				TestUser user = new TestUser();
				user.setHumanId(session.getTestUser().getHumanId());
				user.setThreshold(data.getAttention());
				deviceDao.updThreshold(user);
			}

			deviceDao.updBrainInNow(data);
			// 只有开始视频测试时记录脑电历史值
			if (TPMSConsts.PERIOD_SCORE_START.equals(sessionManager.getTestInfo().getPeriod())) {
				return deviceDao.saveBrainData(data);
			}
		}
		return 0;
	}

	@Override
	public TestUser getUser(int testId, String mac) {
		TestUser param = new TestUser();
		param.setTestId(testId);
		param.setMac(mac);
		return deviceDao.getUser(param);
	}

	@Override
	public int saveDialTotalScore() {
		if (sessionManager.getTestInfo()!=null) {
			return deviceDao.saveDialTotalScore(sessionManager.getTestInfo().getTestId());
		}
		return 0;
	}

	@Override
	public int updBrainInNow(DeviceData data) {
		return deviceDao.updBrainInNow(data);
	}

	@Override
	public Hard getUserBrain(int humanId) {
		
		return deviceDao.getUserBrain(new TestUser(sessionManager.getTestInfo().getTestId(), humanId, null, null));
	}

	@Override
	public Integer userBindBrain(int humanId, Hard hard) {
		TestUser user = new TestUser(sessionManager.getTestInfo().getTestId(), humanId, hard.getHardId(),hard.getMac());
		TestUser bindUser = deviceDao.getUser(user);
		if (bindUser!=null) {
			//该脑电已绑定到其他人员,不能重复绑定
			return 2;
		}else {
			int ret = deviceDao.updUserBrain(user);
			if (ret == 1) {
				Session session = sessionManager.findBrainByMac(hard.getMac());
				if (session!=null) {
					session.setTestUser(user);
				}
			}
			
			return 1;
		}
	}

	@Override
	public int removeBind(int humanId) {
		Hard hard = deviceDao.getUserBrain(new TestUser(sessionManager.getTestInfo().getTestId(), humanId, null, null));
		if (hard != null && StringUtils.isNotEmpty(hard.getMac())) {
			int ret = deviceDao.updUserBrain(new TestUser(sessionManager.getTestInfo().getTestId(), humanId, null, null));
			if (ret == 1) {
				Session session = sessionManager.findBrainByMac(hard.getMac());
				if (session!=null) {
					session.setTestUser(null);
				}
				return 1;
			}
		}
		
		return 0;
	}

	@Override
	public Hard getBrainByNo(int no) {
		return deviceDao.getBrainByNo(no);
	}

}
