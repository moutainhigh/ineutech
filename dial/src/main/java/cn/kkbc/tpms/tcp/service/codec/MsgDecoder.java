package cn.kkbc.tpms.tcp.service.codec;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.kkbc.entity.TestUser;

import cn.kkbc.tpms.tcp.TPMSConsts;
import cn.kkbc.tpms.tcp.util.BitOperator;
import cn.kkbc.tpms.tcp.util.BmpFileUtil;
import cn.kkbc.tpms.tcp.util.HexStringUtils;
import cn.kkbc.tpms.tcp.vo.PackageData;

public class MsgDecoder {

	private static final Logger log = LoggerFactory.getLogger(MsgDecoder.class);

	private BitOperator bitOperator;

	public MsgDecoder() {
		this.bitOperator = new BitOperator();
	}
	
	public List<PackageData> dataToPackageData(byte[] data){
		int index=getIndex(data, TPMSConsts.HEAD_ARRAY, 0);
		if (index==-1) {
			log.info("数据包头不匹配");
			return null;
		}
		List<PackageData> packageDatas=new ArrayList<PackageData>();
		
		
		byte[] two=new byte[2];
		byte[] one=new byte[1];
		PackageData packageData=null;
		for (int i = index; i < data.length; i++) {
			packageData=new PackageData();
			
			//数据包 包头，2bytes
			System.arraycopy(data, i, two, 0, 2);
			packageData.setHead(bitOperator.byteToInteger(two));
			
			//数据类型
			System.arraycopy(data, i+2, one, 0, 1);
			packageData.setDataType(bitOperator.byteToInteger(one));
			
			//命令字节
			System.arraycopy(data, i+3, one, 0, 1);
			packageData.setCommand(bitOperator.byteToInteger(one));
			
			//数据长度
			System.arraycopy(data, i+4, two, 0, 2);
			packageData.setDataLen(bitOperator.byteToInteger(two));
			
			//数据
			byte[] data_temp=new byte[packageData.getDataLen()];
			System.arraycopy(data, i+6, data_temp, 0, packageData.getDataLen());
			packageData.setData(data_temp);
			
			//校验位
			System.arraycopy(data, i+6+packageData.getDataLen(), one, 0, 1);
			packageData.setCheckSum(bitOperator.byteToInteger(one));
			
			packageDatas.add(packageData);
			
			int temp=getIndex(data, TPMSConsts.HEAD_ARRAY, i+6+packageData.getDataLen()+1);
			if (temp==-1) {
				break;
			}else {
				i=temp-1;
			}
		}
		
		
		return packageDatas;
	}
	
	public static byte[] getByteFromToEnd(byte[] data,int startIndex,int endIndex){
		byte[] ret=new byte[endIndex-startIndex];
		if (startIndex>data.length||endIndex>data.length) {
			return null;
		}
		for (int i = startIndex; i < endIndex; i++) {
			ret[i-startIndex]=data[i];
		}
		
		return ret;
	}
	
	/**
	 * 从src中获取data第一次出现的索引
	 * @param src
	 * @param data
	 * @param startIndex
	 * @return
	 */
	public int getIndex(byte[] src,byte[] data,int startIndex){
		if (src==null||data==null) {
			return -1;
		}
		
		for (int i=startIndex;i<src.length;i++) {
			boolean flag=true;
			for (int j = 0; j < data.length; j++) {
				if (i+j>=src.length||(i+j<src.length&&data[j]!=src[i+j])) {
					flag=false;
					break;
				}
			}
			if (flag) {
				return i;
			}
		}
		
		return -1;
	}
	
	public Date byteToDate(byte[] time){
		if (time==null||time.length<8) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
//		cal.setFirstDayOfWeek(Calendar.MONDAY);
        
        cal.set(Calendar.HOUR_OF_DAY,time[0]);//时
        cal.set(Calendar.MINUTE,time[1]);//分
        cal.set(Calendar.SECOND,time[2]);//秒
        
        cal.set(Calendar.DAY_OF_WEEK, time[4]+1);
        
    	cal.set(Calendar.YEAR,time[7]+2000);      //年
		cal.set(Calendar.MONTH,time[5]-1);//月
        cal.set(Calendar.DATE,time[6]);       //日
        return cal.getTime();
		
	}
	
	public static void main(String[] args) {
		MsgDecoder decoder=new MsgDecoder();
		byte[] a={(byte) 0xF5,0x5F,0x03,0x02,0x00,0x00,0x05,(byte) 0xF5,0x5F,0x02,0x09,0x00,0x06,(byte) 0xA0,0x20,(byte) 0xA6,0x10,(byte) 0x85,0x14,0x20};
		byte[] d={(byte) 0xF5,0x5F,0x02,0x17,0x00,0x0E,0x00,0x32,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,(byte) 0x85,0x14,0x03,0x00,(byte) 0xF5};
		byte[] c={(byte) 0xF5,0x5F,0x03,0x11,0x00,0x00,0x14};
		List<PackageData> packageDatas=decoder.dataToPackageData(c);
		System.out.println(packageDatas.size());
		BitOperator bitOperator=new BitOperator();
		for (PackageData packageData : packageDatas) {
//			System.out.println(bitOperator.byteToInteger(packageData.getData()));
		}
		
		byte[] b={0x0D,0x24,0x32,0x00,0x04,0x02,0x09,0x11};
		System.out.println(decoder.byteToDate(b).toString());
		
		TestUser user1=new TestUser();
//		user1.setBmpFile("11.bmp");
		user1.setMac("aaa");
		
		TestUser user2=new TestUser();
//		user2.setBmpFile("11.bmp");
		user2.setMac("aaa");
		
		List<TestUser> testUsers=new ArrayList<TestUser>();
		testUsers.add(user1);
		testUsers.add(user2);
		System.out.println(JSON.toJSONString(testUsers));
		
		BmpFileUtil util=new BmpFileUtil();
		String baseSrc="C:\\Users\\liululu\\Desktop\\硬件\\拨盘\\投票器功能样机演示资料\\bmp\\";
		byte zimoUtil[]=util.getZimo(baseSrc+"11.bmp");
		byte zimo[]={(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFD,(byte)0xFD,0x05,0x55,0x55,0x54,0x55,0x55,0x05,(byte)0xFD,(byte)0xFD,(byte)0xFF,0x77,0x76,0x6E,0x6E,0x5E,0x02,0x7E,0x5E,0x5E,0x6E,0x77,(byte)0xFF,(byte)0xFD,(byte)0xFD,0x05,0x55,0x55,0x54,0x55,0x55,0x05,(byte)0xFD,(byte)0xFD,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xEE,(byte)0xE1,0x0F,(byte)0xE0,(byte)0xFB,(byte)0xC6,(byte)0xEE,0x00,(byte)0xEE,(byte)0xEE,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xF7,(byte)0xEE,(byte)0xD6,0x18,(byte)0xDC,(byte)0xDA,(byte)0xDA,(byte)0xD6,(byte)0xCE,(byte)0xFE,(byte)0xFF,(byte)0xFF,(byte)0xEF,(byte)0xDF,(byte)0x80,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xEF,(byte)0xDF,(byte)0x80,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xCF,(byte)0xBF,(byte)0xBC,(byte)0xBB,(byte)0xC7,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xEF,(byte)0xDF,0x3F,(byte)0xDF,(byte)0xEF,0x0F,0x6F,0x6F,0x6F,0x6F,(byte)0xEF,(byte)0xFF,(byte)0xFF,0x0F,(byte)0xDF,(byte)0xDF,(byte)0xDF,(byte)0xDF,(byte)0xDF,(byte)0xDF,(byte)0xDF,0x0F,(byte)0xFF,(byte)0xFF,(byte)0xEF,(byte)0xDF,0x3F,(byte)0xDF,(byte)0xEF,0x0F,0x6F,0x6F,0x6F,0x6F,(byte)0xEF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xEF,0x5F,(byte)0xBF,0x5F,(byte)0xEF,(byte)0xEF,(byte)0xEF,0x0F,(byte)0xEF,(byte)0xEF,(byte)0xEF,(byte)0xFF,0x7F,0x7F,(byte)0xFF,0x0F,(byte)0xDF,(byte)0xDF,(byte)0xDF,(byte)0xDF,(byte)0xDF,(byte)0xDF,0x0F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,0x3F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,0x3F,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xBF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
		
		for (int i = 0; i < zimoUtil.length; i++) {
			if (zimoUtil[i]!=zimo[i]) {
				System.out.println("不相等"+zimoUtil[i]);
			}
		}
		
		
		
		
	}

}
