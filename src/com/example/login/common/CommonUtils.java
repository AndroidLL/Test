package com.example.login.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.Log;

import com.example.login.entity.CardNum;
import com.example.login.entity.GlobalDataPacket;
import com.example.login.entity.Mode;
import com.example.login.entity.Service;
import com.example.login.entity.UnitNode;
import com.example.login.entity.WareCurtain;
import com.example.login.entity.WareDev;
import com.example.login.entity.WareLight;
import com.example.login.entity.WareLock;
import com.example.login.entity.WareOther;
import com.example.login.entity.WareValve;
import com.example.login.entity.airCondDev;

public class CommonUtils
{
	// 远程地址
	public static String mLocalAddr = "";
	public static String mRemoteAddr = "";
	public static String mRemoteIp = "";
	public static String mLocalIp = "";
	private static String mServIp = "";

	public static void setRemoteAddr(String addr) 
	{
		mRemoteAddr = addr;
	}

	public static void setRemoteIp(String ip) 
	{
		mRemoteIp = ip;
	}

	// byte 转换为Bit
	public static String byteToBit(byte b) 
	{
		return "" + ((b >> 7) & 0x1) + ((b >> 6) & 0x1) + ((b >> 5) & 0x1)
				+ ((b >> 4) & 0x1) + ((b >> 3) & 0x1) + ((b >> 2) & 0x1)
				+ ((b >> 1) & 0x1) + ((b >> 0) & 0x1);
	}
	/**
	 * cast byte array into byte value
	 * @param value
	 * @return
	 */
	
	public static byte byteToByte( byte [] value)
	{
		int inValue = value.length;
		
		byte sum = (byte)(value[inValue-1] - 0x30);
		byte tmp = 10;
		
		for (int i = inValue-2 ;i>=0 ;i--)
		{
			sum += (value[i] -0x30)*tmp;
					tmp*=10;
		}
		return sum;
	}
	
	// 设置是否是应答包
	public static boolean isAsk(String str) 
	{
		String s1 = str.substring(str.length() - 2, str.length() - 1);
		String s2 = str.substring(str.length() - 1, str.length());

		return ("0".equals(s1) && "1".equals(s2)) ? true : false;
	}

	// 设置是否要回应
	public static boolean isReply(String str) 
	{
		String s1 = str.substring(str.length() - 2, str.length() - 1);
		String s2 = str.substring(str.length() - 1, str.length());

		return ("1".equals(s1) && "0".equals(s2)) ? true : false;
	}

	public static void CommonUtils_init(String ipAddr) 
	{
		/*
		 * 初始化本机地址
		 */
		CommonUtils.mLocalIp = ipAddr;
	}
	
	public static void CommonUtils_sevInit(String servIp)
	{
		CommonUtils.mServIp = servIp; 
	}
	
	public static String getServIp()
	{
		return mServIp;
	}
	
	public static String getLocalIp() 
	{
		return mLocalIp;
	}

	public static String getLocalAddr()
	{
		// return "S0001010802000000000";
		return mLocalAddr;
	}

	public static String getRemoteAddr() 
	{
		// return "M0001010";
		return mRemoteAddr;
	}

	public static String getRemoteIp() 
	{
		return mRemoteIp;
	}

	public static List<WareLock> extractLocks(byte[] data) 
	{

		int count = 0;
		int lock_size = 36;// lock结构体字节数
		if (data != null) 
		{
			count = data.length / lock_size;
		}

		List<WareLock> locks = new ArrayList<WareLock>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) 
		{
			WareLock lock = new WareLock();
			byte[] lockData = new byte[24];
			for (int m = 0; m < 24; m++) 
			{
				lockData[m] = localData[i * lock_size + m];
			}

			WareDev dev = new WareDev();
			dev.type = byteToInt(new byte[] { lockData[0] });
			dev.id = byteToInt(new byte[] { lockData[1] });
			dev.isValid = byteToInt(new byte[] { lockData[2], lockData[3] });

			if (dev.isValid != 1)
				continue;

			byte[] namebyte = new byte[12];
			for (int m = 0; m < 12; m++) 
			{
				namebyte[m] = lockData[m + 4];
			}
			
			dev.name = getStr(namebyte);
			dev.tNetAddr = byteToInt(new byte[] { lockData[16], lockData[17] });
			dev.rev = byteToInt(new byte[] { lockData[18] });
			dev.scene = byteToInt(new byte[] { lockData[19] });

			lock.dev = dev;
			lock.statusLatch = byteToInt(new byte[] { localData[i * lock_size
					+ 24] });
			lock.statusCounter = byteToInt(new byte[] { localData[i * lock_size
					+ 26] });
			lock.statusDoor = byteToInt(new byte[] { localData[i * lock_size
					+ 25] });

			locks.add(lock);
		}

		return locks;
	}

	public static List<WareValve> extractValves(byte[] data) 
	{

		int count = 0;
		int dev_size = 28;// valve结构体字节数
		if (data != null) 
		{
			count = data.length / dev_size;
		}

		List<WareValve> valves = new ArrayList<WareValve>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) 
		{
			WareValve valve = new WareValve();
			byte[] lockData = new byte[24];
			for (int m = 0; m < 24; m++) 
			{
				lockData[m] = localData[i * dev_size + m];
			}

			WareDev dev = new WareDev();
			dev.type = byteToInt(new byte[] { lockData[0] });
			dev.id = byteToInt(new byte[] { lockData[1] });
			dev.isValid = byteToInt(new byte[] { lockData[2], lockData[3] });

			if (dev.isValid != 1)
				continue;

			byte[] namebyte = new byte[12];
			for (int m = 0; m < 12; m++)
			{
				namebyte[m] = lockData[m + 4];
			}
			dev.name = getStr(namebyte);
			dev.tNetAddr = byteToInt(new byte[] { lockData[16], lockData[17] });
			dev.rev = byteToInt(new byte[] { lockData[18] });
			dev.scene = byteToInt(new byte[] { lockData[19] });

			valve.dev = dev;
			valve.statusLatch = byteToInt(new byte[] { localData[i * dev_size
					+ 24] });

			valves.add(valve);
		}

		return valves;
	}

	/**
	 * get curtains
	 * 
	 * @param data
	 * @return
	 */
	public static List<WareCurtain> extractCurtains(byte[] data) {

		int count = 0;
		int dev_size = 32;
		if (data != null) {
			count = data.length / dev_size;
		}

		List<WareCurtain> curtains = new ArrayList<WareCurtain>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) {
			WareCurtain curtain = new WareCurtain();
			byte[] curtData = new byte[24];
			for (int m = 0; m < 24; m++) {
				curtData[m] = localData[i * dev_size + m];
			}

			WareDev dev = new WareDev();
			dev.type = byteToInt(new byte[] { curtData[0] });
			dev.id = byteToInt(new byte[] { curtData[1] });
			dev.isValid = byteToInt(new byte[] { curtData[2], curtData[3] });

			if (dev.isValid != 1)
				continue;

			byte[] namebyte = new byte[12];
			for (int m = 0; m < 12; m++) {
				namebyte[m] = curtData[m + 4];
			}
			dev.name = getStr(namebyte);
			dev.tNetAddr = byteToInt(new byte[] { curtData[16], curtData[17] });
			dev.rev = byteToInt(new byte[] { curtData[18] });
			dev.scene = byteToInt(new byte[] { curtData[19] });

			curtain.dev = dev;
			curtain.onOff = byteToInt(new byte[] {
					localData[i * dev_size + 24], localData[i * dev_size + 25] });

			curtains.add(curtain);
		}

		return curtains;
	}

	public static List<WareOther> extractOthers(byte[] data) {
		int count = 0;
		int structSize = 244;// 244为C结构体中的WARE_DEV_OTHER字节数
		if (data != null) {
			count = (data.length) / structSize;
		}
		List<WareOther> others = new ArrayList<WareOther>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) {
			WareOther other = new WareOther();
			other.keyName = new byte[216];
			byte[] devData = new byte[structSize];
			for (int m = 0; m < structSize; m++) {
				devData[m] = localData[i * structSize + m];
			}

			WareDev dev = new WareDev();
			dev.type = byteToInt(new byte[] { devData[0] });
			dev.id = byteToInt(new byte[] { devData[1] });
			dev.isValid = byteToInt(new byte[] { devData[2], devData[3] });

			if (dev.isValid != 1) {
				continue;
			}

			byte[] namebyte = new byte[12];
			int nameLen = 0;
			for (int m = 0; m < 12; m++) {
				if (devData[m + 4] == 0) {
					// 后面是空
					nameLen = m;
					break;
				}
				namebyte[m] = devData[m + 4];
			}

			try {
				dev.name = new String(namebyte, 0, nameLen, "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			dev.tNetAddr = byteToInt(new byte[] { devData[16], devData[17] });
			dev.rev = byteToInt(new byte[] { devData[18] });
			dev.scene = byteToInt(new byte[] { devData[19] });

			other.dev = dev;

			other.type = byteToInt(new byte[] { localData[i * structSize + 24] });
			for (int m = 0; m < 216; m++) {
				other.keyName[m] = localData[i * structSize + 28 + m];
			}

			others.add(other);
		}

		return others;
	}

	/**
	 * Extract light data
	 * 
	 * @param data
	 */
	public static List<WareLight> extractLights(byte[] data) {

		int count = 0;
		if (data != null) {
			count = (data.length) / 40;
			Log.e("qqqqqqqqqqqq", ""+data.length);
		}
		List<WareLight> lights = new ArrayList<WareLight>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) {
			WareLight light = new WareLight();
			byte[] lightData = new byte[40];
			for (int m = 0; m < 40; m++) {
				lightData[m] = localData[i * 40 + m];
			}

			WareDev dev = new WareDev();
			dev.type = byteToInt(new byte[] { lightData[0] });
			dev.id = byteToInt(new byte[] { lightData[1] });
			dev.isValid = byteToInt(new byte[] { lightData[2], lightData[3] });

			if (dev.isValid != 1) {
				continue;
			}

			byte[] namebyte = new byte[12];
			int nameLen = 0;
			for (int m = 0; m < 12; m++) {
				if (lightData[m + 4] == 0) {
					// 后面是空
					nameLen = m;
					break;
				}
				namebyte[m] = lightData[m + 4];
			}

			try {

				dev.name = new String(namebyte, 0, nameLen, "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			dev.tNetAddr = byteToInt(new byte[] { lightData[16], lightData[17] });
			dev.rev = byteToInt(new byte[] { lightData[18] });
			dev.scene = byteToInt(new byte[] { lightData[19] });

			light.dev = dev;

			light.onOff = byteToInt(new byte[] { localData[i * 40 + 24 + 0],
					localData[i * 40 + 24 + 1] });
			light.lmValue = byteToInt(new byte[] { localData[i * 40 + 26 + 0],
					localData[i * 40 + 26 + 1] });
			light.scene = byteToInt(new byte[] { localData[i * 40 + 28 + 0],
					localData[i * 40 + 28 + 1], localData[i * 40 + 28 + 2],
					localData[i * 40 + 28 + 3], });

			lights.add(light);
		}

		return lights;
	}

	/**
	 * Query all Uint Nodes
	 * 
	 * @param pktSn
	 * @param srcIp
	 * @param dstIp
	 * @return
	 * @throws UnknownHostException
	 */
	public static byte[] queryAllUnitNodes(int pktSn, String srcIp, String dstIp)
			throws UnknownHostException {
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();// 类型是UDP
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();// 不是应答包
		dataPacket.pktSn = pktSn;

		dataPacket.dstAddr = InetAddress.getByName(dstIp).getAddress();// 目的地址
		dataPacket.srcAddr = InetAddress.getByName(srcIp).getAddress();// 源地址

		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_Hello.getValue();// 父命令hello
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;// 字节对齐保留

		dataPacket.subCmd = GlobalDataPacket.SUB_CMD.SPN_Null.getValue();// 子命令是SPN_Null

		dataPacket.param = 0;// 命令参数
		dataPacket.uID = new byte[12];// 节点的UID
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = (byte) (0xff);
		}

		dataPacket.dataLen = 1024;// 数据长度1k

		dataPacket.globalData = new byte[1024];// 数据
		for (int i = 0; i < 1024; i++) {
			dataPacket.globalData[i] = (byte) (0xff);
		}

		dataPacket.pktCrc = 0;// 此包校验和

		byte[] data = dataPacket.getData().clone();// 数据结构

		byte[] sendData = new byte[7 + data.length];// 包头
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}

		return sendData;
	}

	/**
	 * Open curtain
	 * 
	 * @param pktSn
	 * @param dstAddr
	 * @param srcAddr
	 * @param curtainId
	 * @throws UnknownHostException
	 */
	public static byte[] openCurtain(int pktSn, String dstAddr, String srcAddr,
			int curtainId, String uID) throws UnknownHostException {
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;

		dataPacket.dstAddr = InetAddress.getByName(dstAddr).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcAddr).getAddress();

		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_ExeCurtCmds
				.getValue();
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;

		// dataPacket.subCmd =
		// GlobalDataPacket.E_LGT_CMD.e_lgt_offOn.getValue();
		dataPacket.subCmd = 1;

		dataPacket.param = curtainId;
		dataPacket.uID = new byte[12];
		byte[] pUid = hexStringToBytes(uID);
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = pUid[i];
		}

		dataPacket.dataLen = 2;

		dataPacket.globalData = new byte[] { (byte) (0 & 0xff),
				(byte) (1 & 0xff) };

		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}

		return sendData;

	}

	/**
	 * Close Curtain
	 * 
	 * @param pktSn
	 * @param dstAddr
	 * @param srcAddr
	 * @param cutainId
	 * @throws UnknownHostException
	 */
	public static byte[] closeCurtain(int pktSn, String dstAddr,
			String srcAddr, int cutainId, String uID)
			throws UnknownHostException {
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;

		dataPacket.dstAddr = InetAddress.getByName(dstAddr).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcAddr).getAddress();

		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_ExeCurtCmds
				.getValue();
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;

		// dataPacket.subCmd =
		// GlobalDataPacket.E_LGT_CMD.e_lgt_offOn.getValue();
		dataPacket.subCmd = 2;

		dataPacket.param = cutainId;
		dataPacket.uID = new byte[12];
		byte[] pUid = hexStringToBytes(uID);
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = pUid[i];
		}

		dataPacket.dataLen = 2;

		dataPacket.globalData = new byte[] { (byte) (0 & 0xff),
				(byte) (2 & 0xff) };

		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}

		return sendData;

	}

	/**
	 * Stop curtain
	 * 
	 * @param pktSn
	 * @param dstAddr
	 * @param srcAddr
	 * @param cutainId
	 * @return
	 * @throws UnknownHostException
	 */
	public static byte[] stopCurtain(int pktSn, String dstAddr, String srcAddr,
			int cutainId, String uID) throws UnknownHostException {
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;

		dataPacket.dstAddr = InetAddress.getByName(dstAddr).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcAddr).getAddress();

		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_ExeCurtCmds
				.getValue();
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;

		// dataPacket.subCmd =
		// GlobalDataPacket.E_LGT_CMD.e_lgt_offOn.getValue();
		dataPacket.subCmd = 0;

		dataPacket.param = cutainId;
		dataPacket.uID = new byte[12];
		byte[] pUid = hexStringToBytes(uID);
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = pUid[i];
		}

		dataPacket.dataLen = 2;

		dataPacket.globalData = new byte[] { (byte) (0 & 0xff),
				(byte) (0 & 0xff) };

		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}

		return sendData;

	}

	/**
	 * Open light
	 * 
	 * @param pktSn
	 * @param dstAddr
	 * @param srcAddr
	 * @param lightId
	 * @throws UnknownHostException
	 */
	public static byte[] openLight(int pktSn, String dstAddr, String srcAddr,
			int lightId, String uID) throws UnknownHostException {
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;

		dataPacket.dstAddr = InetAddress.getByName(dstAddr).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcAddr).getAddress();

		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_ExeLgtCmds
				.getValue();
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;

		dataPacket.subCmd = GlobalDataPacket.E_LGT_CMD.e_lgt_offOn.getValue();

		dataPacket.param = lightId;
		dataPacket.uID = new byte[12];
		byte[] pUid = hexStringToBytes(uID);
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = pUid[i];
		}

		dataPacket.dataLen = 1024;

		dataPacket.globalData = new byte[1024];
		for (int i = 0; i < 1024; i++) {
			dataPacket.globalData[i] = (byte) (0xff);
		}

		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}

		return sendData;
	}

	/**
	 * Close light
	 * 
	 * @param pktSn
	 * @param dstAddr
	 * @param srcAddr
	 * @param lightId
	 * @throws UnknownHostException
	 */
	public static byte[] closeLight(int pktSn, String dstAddr, String srcAddr,
			int lightId, String uID) throws UnknownHostException {
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;

		dataPacket.dstAddr = InetAddress.getByName(dstAddr).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcAddr).getAddress();

		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_ExeLgtCmds
				.getValue();
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;

		dataPacket.subCmd = GlobalDataPacket.E_LGT_CMD.e_lgt_onOff.getValue();

		dataPacket.param = lightId;
		dataPacket.uID = new byte[12];
		byte[] pUid = hexStringToBytes(uID);
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = pUid[i];
		}

		dataPacket.dataLen = 1024;

		dataPacket.globalData = new byte[1024];
		for (int i = 0; i < 1024; i++) {
			dataPacket.globalData[i] = (byte) (0xff);
		}

		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}

		return sendData;
	}

	public static byte[] ctrlLock(boolean bOpen, int pktSn, String dstAddr,
			String srcAddr, int devId, String uID) throws UnknownHostException {
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;

		dataPacket.dstAddr = InetAddress.getByName(dstAddr).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcAddr).getAddress();

		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_ExeLockCmds
				.getValue();
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;
		/*
		 * typedef struct t_cmd_lock{ u8 devID; u8 rev1; u16
		 * devUP433Addr;//设备所属节点的433地址 u16 retAddr; u16 rev2; u32 retIp; u8
		 * statusLatch;//锁舌状态 0关闭 1开启 u8 statusDoor;//门吸合状态 0关闭 1开启 u8
		 * statusCounter;//反锁状态 0关闭 1开启 u8 statusRev;//状态 0关闭 1开启 u8 cmdType;//
		 * 0: 开关锁舌 1: 编辑板载卡号用于开门 u8 cmdSub;//子命令 用于锁舌时: 0 关闭1开启 //// 针对卡号操作 0:
		 * 增加 1:删除 2:清空 u16 len;//附加的卡号数据长度 }CMD_LOCK;
		 * 
		 * CMD_LOCK cmd; cmd.cmdType = 0; cmd.devID = pDev->devHead.id;
		 * cmd.cmdSub = !(pDev->statusLatch);
		 */
		dataPacket.subCmd = 0;
		dataPacket.param = devId;
		dataPacket.uID = new byte[12];
		byte[] pUid = hexStringToBytes(uID);
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = pUid[i];
		}
		dataPacket.dataLen = 20;// sizeof(CMD_LOCK)

		dataPacket.globalData = new byte[20];
		dataPacket.globalData[0] = (byte) (devId & 0x000000ff);
		dataPacket.globalData[16] = 0;
		if (bOpen)
			dataPacket.globalData[17] = 1;
		else
			dataPacket.globalData[17] = 0;
		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}

		return sendData;

	}

	public static byte[] ctrlValve(boolean bOpen, int pktSn, String dstAddr,
			String srcAddr, int devId, String uID) throws UnknownHostException {
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;

		dataPacket.dstAddr = InetAddress.getByName(dstAddr).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcAddr).getAddress();

		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_ExeValveCmds
				.getValue();
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;
		/*
		 * typedef struct t_cmd_valve{ u8 devID; u8 rev1; u16
		 * devUP433Addr;//设备所属节点的433地址 u16 retAddr; u16 rev2; u32 retIp; u8
		 * statusLatch;//状态 0关闭 1开启 u8 cmd;// 0关闭 1开启 u16 rev; }CMD_VALVE;
		 * 
		 * CMD_VALVE cmd; cmd.devID = pDev->devHead.id; cmd.cmd =
		 * !(pParent->m_devValve.statusLatch);
		 */
		dataPacket.subCmd = 0;
		dataPacket.param = devId;
		dataPacket.uID = new byte[12];
		byte[] pUid = hexStringToBytes(uID);
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = pUid[i];
		}
		dataPacket.dataLen = 16;// sizeof(CMD_VALVE)

		dataPacket.globalData = new byte[16];
		dataPacket.globalData[0] = (byte) (devId & 0x000000ff);
		if (bOpen)
			dataPacket.globalData[13] = 1;
		else
			dataPacket.globalData[13] = 0;
		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}

		return sendData;

	}

	public static byte[] sendOtherDevCmd(int pktSn, String dstAddr,
			String srcAddr, int devId, byte cmd, String uID)
			throws UnknownHostException {
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;

		dataPacket.dstAddr = InetAddress.getByName(dstAddr).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcAddr).getAddress();

		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_ExeOtherCmds
				.getValue();
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;
		dataPacket.subCmd = 9;// e_ware_other

		dataPacket.param = devId;
		dataPacket.uID = new byte[12];
		byte[] pUid = hexStringToBytes(uID);
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = pUid[i];
		}

		dataPacket.dataLen = 2;

		dataPacket.globalData = new byte[2];//
		dataPacket.globalData[0] = cmd;
		dataPacket.globalData[1] = 0;
		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}

		return sendData;

	}

	public static List<airCondDev> extractAirconds(byte[] data) 
	{

		int count = 0;
		int air_size = 188;// 结构体大小
		
		if (data != null) 
		{
			count = (data.length) / air_size;
		}
		
		List<airCondDev> airDevs = new ArrayList<airCondDev>();

		byte[] localData = data.clone();
		
		for (int i = 0; i < count; i++) 
		{
			airCondDev airDev = new airCondDev();
			byte[] devData = new byte[air_size];
			
			for (int m = 0; m < air_size; m++) 
			{
				devData[m] = localData[i * air_size + m];
			}

			WareDev dev = new WareDev();
			dev.type = byteToInt(new byte[] { devData[0] });
			dev.id = byteToInt(new byte[] { devData[1] });
			dev.isValid = byteToInt(new byte[] { devData[2], devData[3] });

			if (dev.isValid != 1) 
			{
				continue;
			}

			byte[] namebyte = new byte[12];
			int nameLen = 0;
			
			for (int m = 0; m < 12; m++) 
			
			{
				if (devData[m + 4] == 0) 
				{
					// 后面是空
					nameLen = m;
					break;
				}
				namebyte[m] = devData[m + 4];
			}

			try
			{
				dev.name = new String(namebyte, 0, nameLen, "GB2312");
				// dev.name = new String(namebyte, "GB2312");
			} catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			}
			
			dev.tNetAddr = byteToInt(new byte[] { devData[16], devData[17] });
			dev.rev = byteToInt(new byte[] { devData[18] });
			dev.scene = byteToInt(new byte[] { devData[19] });

			airDev.dev = dev;

			airDev.pwrOn = byteToInt(new byte[] { localData[i * air_size + 44
					+ 0] });
			airDev.tempSel = byteToInt(new byte[] { localData[i * air_size + 44
					+ 1] });
			airDev.modeSel = byteToInt(new byte[] { localData[i * air_size + 44
					+ 2] });
			airDev.spdSel = byteToInt(new byte[] { localData[i * air_size + 44
					+ 3] });
			airDev.tempReal = byteToInt(new byte[] { localData[i * air_size
					+ 50] });
			airDev.pm25 = byteToInt(new byte[] { localData[i * air_size + 52],
					localData[i * air_size + 53] });
			airDev.humidity = byteToInt(new byte[] {
					localData[i * air_size + 54], localData[i * air_size + 55] });

			airDevs.add(airDev);
		}

		return airDevs;
	}

	/**
	 * Query devices by device waretyp
	 * 
	 * @param pktSn
	 * @param wareType
	 * @param dstIp
	 * @param srcIp
	 */
	public static byte[] queryByWareType(int pktSn, int wareType, String dstIp,
			String srcIp, String uID) {
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;
		try {
			dataPacket.dstAddr = InetAddress.getByName(dstIp).getAddress();
			dataPacket.srcAddr = InetAddress.getByName(srcIp).getAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_QueryDev
				.getValue();
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;// 字节对齐保留

		dataPacket.subCmd = GlobalDataPacket.SUB_CMD.SPN_queryAirDev.getValue();
		dataPacket.param = 1;

		dataPacket.uID = new byte[12];
		byte[] pUid = hexStringToBytes(uID);
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = pUid[i];
		}

		dataPacket.dataLen = 1;
		dataPacket.globalData = new byte[] { (byte) (wareType & 0xff) };

		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);
		for (int i = 0; i < data.length; i++) {
			sendData[i + 7] = data[i];
		}

		return sendData;
	}

	// 得到系列号
	public static int getPktSn() {
		return new Random().nextInt(com.example.login.common.Constants.sn++);
	}

	// 准备发送GlobalPkt
	public static byte[] preSendGlobalPkt(int pktSn, String dstAddr,
			String srcAddr, int cmd, int subCmd, int param, byte[] buffer,
			int len, String uID) throws UnknownHostException {
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;

		dataPacket.dstAddr = InetAddress.getByName(dstAddr).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcAddr).getAddress();

		dataPacket.globalCmd = cmd;
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;

		dataPacket.subCmd = subCmd;

		dataPacket.param = param;
		dataPacket.uID = new byte[12];
		byte[] pUid = hexStringToBytes(uID);
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = pUid[i];
		}

		dataPacket.dataLen = len;

		dataPacket.globalData = new byte[1024];

		for (int i = 0; i < 1024; i++) {
			if (i < len) {
				dataPacket.globalData[i] = buffer[i];
			} else {
				dataPacket.globalData[i] = (byte) (0xff);
			}
		}

		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}

		return sendData;
	}

	// 查询卡号
	public static byte[] queryCardNumber(int pktSn, String srcIP, String desIp)
			throws UnknownHostException {

		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;

		dataPacket.dstAddr = InetAddress.getByName(desIp).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcIP).getAddress();

		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_Hello.getValue();
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();
		dataPacket.reserve = 0;
		dataPacket.subCmd = GlobalDataPacket.SUB_CMD.SPN_Null.getValue();
		dataPacket.param = 0;
		dataPacket.uID = new byte[12];
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = (byte) (0xff);
		}
		dataPacket.dataLen = 0;
		dataPacket.globalData = new byte[1];
		dataPacket.globalData[0] = (byte) (0xff);

		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData().clone();
		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);
		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}
		return sendData;

	}

	// 查询所有的单元楼号
	public static byte[] querAllUnitNodesname(int pktSn, String room,
			String pass, String srcIp, String dstIp)
			throws UnknownHostException {
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;

		dataPacket.dstAddr = InetAddress.getByName(dstIp).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcIp).getAddress();
		Log.e("err>>>", srcIp + dstIp);

		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_Hello.getValue();
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;
		dataPacket.subCmd = GlobalDataPacket.SUB_CMD.SPN_Null.getValue();

		dataPacket.param = 0;
		dataPacket.uID = new byte[12];
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = (byte) (0xff);
		}
		dataPacket.dataLen = 24;
		dataPacket.globalData = new byte[24];
		for (int i = 0; i < 24; i++) {
			dataPacket.globalData[i] = (byte) (0);
		}
		byte[] pName = room.getBytes();
		for (int i = 0; i < pName.length; i++) {
			dataPacket.globalData[i] = pName[i];
		}
		byte[] pPass = pass.getBytes();
		Log.e("密码", pass + " " + pPass.length);
		for (int i = 0; i < pPass.length; i++) {
			dataPacket.globalData[i + 12] = pPass[i];
		}

		dataPacket.pktCrc = 0;
		// 加上xxxcid
		byte[] data = dataPacket.getData().clone();
		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}

		return sendData;

	}

	// 取出节点号
	public static UnitNode extractUnitNode(byte[] data) {

		byte[] localData = data.clone();

		int id = (localData[0] & 0xff) | (localData[1] << 8 & 0xff)
				| (localData[2] << 16 & 0xff) | (localData[3] << 24 & 0xff);

		byte[] namebyte = new byte[12];
		int nameLen = 0;
		for (int i = 0; i < 12; i++) {
			if (localData[i + 4] == 0)// 问题
			{
				nameLen = i;
				break;
			}
			namebyte[i] = localData[i + 4];
		}
		String name = "";
		try {
			name = new String(namebyte, 0, nameLen, "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		StringBuilder ipBuilder = new StringBuilder();
		ipBuilder.append((data[0 + 16] & 0xff) + "." + (data[1 + 16] & 0xff)
				+ "." + (data[2 + 16] & 0xff) + "." + (data[3 + 16] & 0xff));
		String ipAddr = ipBuilder.toString();

		StringBuilder subMaskBuilder = new StringBuilder();
		subMaskBuilder.append((data[0 + 20] & 0xff) + "."
				+ (data[1 + 20] & 0xff) + "." + (data[2 + 20] & 0xff) + "."
				+ (data[3 + 20] & 0xff));
		String subMask = subMaskBuilder.toString();

		StringBuilder gatewayBuilder = new StringBuilder();
		gatewayBuilder.append((data[0 + 24] & 0xff) + "."
				+ (data[1 + 24] & 0xff) + "." + (data[2 + 24] & 0xff) + "."
				+ (data[3 + 24] & 0xff));
		String gateway = gatewayBuilder.toString();

		StringBuilder indoorServBuilder = new StringBuilder();
		indoorServBuilder.append((data[0 + 28] & 0xff) + "."
				+ (data[1 + 28] & 0xff) + "." + (data[2 + 28] & 0xff) + "."
				+ (data[3 + 28] & 0xff));
		String indoorServ = indoorServBuilder.toString();

		StringBuilder centerServBuilder = new StringBuilder();
		centerServBuilder.append((data[0 + 32] & 0xff) + "."
				+ (data[1 + 32] & 0xff) + "." + (data[2 + 32] & 0xff) + "."
				+ (data[3 + 32] & 0xff));
		String centerServ = centerServBuilder.toString();

		StringBuilder roomNumBuilder = new StringBuilder();
		roomNumBuilder.append(data[0 + 36] & 0xff);
		roomNumBuilder.append(data[1 + 36] & 0xff);
		roomNumBuilder.append(data[2 + 36] & 0xff);
		roomNumBuilder.append(data[3 + 36] & 0xff);
		String roomNum = roomNumBuilder.toString();

		byte[] idBuf = new byte[12];
		for (int i = 0; i < 12; i++) {
			idBuf[i] = data[60 + i];
		}
		String devUid = printHexString(idBuf);

		// Log.e(TAG, "id: " + id + ", name: " + name + ", ipAddr: " + ipAddr +
		// ", subMask: " + subMask + ", gateway: " + gateway + ", indoorServ: "
		// + indoorServ + ", centerServ: " + centerServ + ", roomNum: " +
		// roomNum);

		UnitNode node = new UnitNode();
		node.id = id;
		node.name = name;
		node.ipAddr = ipAddr;
		node.subMask = subMask;
		node.gateWay = gateway;
		node.inDoorServ = indoorServ;
		node.centerServ = centerServ;
		node.roomNum = roomNum;
		node.uID = devUid;

		return node;

	}

	private static String printHexString(byte[] b) 
	{
		String a = "";
		
		for (int i = 0; i < b.length; i++) 
		{
			String hex = Integer.toHexString(b[i] & 0xFF);
			
			if (hex.length() == 1) 
			{
				hex = '0' + hex;
			}

			a = a + hex;
		}

		return a;
	}

	// 处理节点ID用到的
	private static byte[] hexStringToBytes(String hexString)
	{
		if (hexString == null || hexString.equals("")) 
		{
			return null;
		}
		
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		
		for (int i = 0; i < length; i++) 
		{
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}

	/**
	 * Get str from byte
	 * 
	 * @param data
	 * @return
	 */
	public static String getStr(byte[] data) 
	{
		int dataLen = 0;
		
		for (int i = 0; i < data.length; i++) 
		{
			if (data[i] == 0)
			{
				dataLen = i;
				break;
			}
		}
		
		try 
		{
			return new String(data, 0, dataLen, "GB2312");
		} catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		return "";
	}

	// char变成byte
	private static int charToByte(char c) 
	{
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * cast byte array into int value
	 * 
	 * @param value
	 * @return
	 */
	public static int byteToInt(byte[] value) 
	{
		int length = value.length;
		int intValue = value[length - 1] & 0xff;

		for (int i = length - 2; i >= 0; i--) 
		{
			intValue = (intValue << 8 * (length - i - 1)) | (value[i] & 0xff);
		}

		return intValue;
	}

	/**
	 * Rely Ack Packet
	 * 
	 * @param packet
	 */
	public static byte[] reply(DatagramPacket packet) {

		byte[] receivedData = packet.getData();

		byte[] data = new byte[packet.getLength()];

		for (int i = 0; i < data.length; i++) {
			data[i] = receivedData[i];
		}

		// is ack
		data[1 + 7] = (byte) (1 & 0xff);

		// dst addr
		int[] dstAddr = new int[4];
		dstAddr[0] = data[11] & 0xff;
		dstAddr[1] = data[12] & 0xff;
		dstAddr[2] = data[13] & 0xff;
		dstAddr[3] = data[14] & 0xff;

		// src addr
		int[] srcAddr = new int[4];
		srcAddr[0] = data[15] & 0xff;
		srcAddr[1] = data[16] & 0xff;
		srcAddr[2] = data[17] & 0xff;
		srcAddr[3] = data[18] & 0xff;

		data[11] = (byte) (srcAddr[0] & 0xff);
		data[12] = (byte) (srcAddr[1] & 0xff);
		data[13] = (byte) (srcAddr[2] & 0xff);
		data[14] = (byte) (srcAddr[3] & 0xff);

		data[15] = (byte) (dstAddr[0] & 0xff);
		data[16] = (byte) (dstAddr[1] & 0xff);
		data[17] = (byte) (dstAddr[2] & 0xff);
		data[18] = (byte) (dstAddr[3] & 0xff);

		return data;
	}

	/**
	 * Broacast UDP packet.
	 * 
	 * @param castClass
	 * @param packet
	 * @throws IOException
	 */
	public static void castUdpPacket(MulticastSocket castClass,
			DatagramPacket packet) throws IOException 
			{
		castClass.send(packet);
	}

	public static CardNum extractCardNum(byte[] cardNumData)
	{
		byte[] localData = cardNumData.clone();

		int id = (localData[0] & 0xff) | (localData[1] << 8 & 0xff)
				| (localData[2] << 16 & 0xff) | (localData[3] << 24 & 0xff);
		

		byte[] namebyte = new byte[12];
		int nameLen = 0;
		for (int i = 0; i < 12; i++)
		{
			if (localData[i + 4] == 0)
			{
				nameLen = i;
				break;
			}
			namebyte[i] = localData[i + 4];
		}
		String name = "";
		try {
			name = new String(namebyte, 0, nameLen, "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		StringBuilder ipBuilder = new StringBuilder();
		ipBuilder.append((cardNumData[0 + 16] & 0xff) + "."
				+ (cardNumData[1 + 16] & 0xff) + "."
				+ (cardNumData[2 + 16] & 0xff) + "."
				+ (cardNumData[3 + 16] & 0xff));
		String ipAddr = ipBuilder.toString();

		StringBuilder subMaskBuilder = new StringBuilder();
		subMaskBuilder.append((cardNumData[0 + 20] & 0xff) + "."
				+ (cardNumData[1 + 20] & 0xff) + "."
				+ (cardNumData[2 + 20] & 0xff) + "."
				+ (cardNumData[3 + 20] & 0xff));
		String subMask = subMaskBuilder.toString();

		StringBuilder gatewayBuilder = new StringBuilder();
		gatewayBuilder.append((cardNumData[0 + 24] & 0xff) + "."
				+ (cardNumData[1 + 24] & 0xff) + "."
				+ (cardNumData[2 + 24] & 0xff) + "."
				+ (cardNumData[3 + 24] & 0xff));
		String gateway = gatewayBuilder.toString();

		StringBuilder indoorServBuilder = new StringBuilder();
		indoorServBuilder.append((cardNumData[0 + 28] & 0xff) + "."
				+ (cardNumData[1 + 28] & 0xff) + "."
				+ (cardNumData[2 + 28] & 0xff) + "."
				+ (cardNumData[3 + 28] & 0xff));
		String indoorServ = indoorServBuilder.toString();

		StringBuilder centerServBuilder = new StringBuilder();
		centerServBuilder.append((cardNumData[0 + 32] & 0xff) + "."
				+ (cardNumData[1 + 32] & 0xff) + "."
				+ (cardNumData[2 + 32] & 0xff) + "."
				+ (cardNumData[3 + 32] & 0xff));
		String centerServ = centerServBuilder.toString();

		StringBuilder roomNumBuilder = new StringBuilder();
		roomNumBuilder.append(cardNumData[0 + 36] & 0xff);
		roomNumBuilder.append(cardNumData[1 + 36] & 0xff);
		roomNumBuilder.append(cardNumData[2 + 36] & 0xff);
		roomNumBuilder.append(cardNumData[3 + 36] & 0xff);
		String roomNum = roomNumBuilder.toString();

		int cardPow = byteToInt(new byte[] {localData[55] });

		byte[] idBuf = new byte[12];
		for (int i = 0; i < 12; i++) {
			idBuf[i] = cardNumData[65 + i];
		}
		String devUid = printHexString(idBuf);
		
		CardNum cardNum = new CardNum();
		cardNum.id = id;
		cardNum.name = name;
		cardNum.ipAddr = ipAddr;
		cardNum.subMask = subMask;
		cardNum.gateWay = gateway;
		cardNum.inDoorServ = indoorServ;
		cardNum.centerServ = centerServ;
		cardNum.roomNum = roomNum;
		cardNum.cardPow = cardPow;
		cardNum.uID = devUid;
		return cardNum;
	}

	public static byte[] queryModel(int pktSn, String srcIp, String dstIp) throws UnknownHostException
	
	{
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;
		
		dataPacket.dstAddr = InetAddress.getByName(dstIp).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcIp).getAddress();
		
		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_SetDevScene .getValue();
		
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;//字节对齐保留

		dataPacket.subCmd = GlobalDataPacket.SUB_CMD.SPN_queryAirDevInfo.getValue();//子命令是SPN_Null

		dataPacket.param = 0;//命令参数
		dataPacket.uID = new byte[12];//节点的UID
		for (int i = 0; i < 12; i++) {
			dataPacket.uID[i] = (byte) (0xff);
		}
		dataPacket.dataLen = 0;
		dataPacket.globalData = new byte[1];
		dataPacket.globalData[0] = (byte) (0xff);

		dataPacket.pktCrc = 0;// 此包校验和

		byte[] data = dataPacket.getData().clone();//数据结构

		byte[] sendData = new byte[7 + data.length];//包头
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++) {
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) {
			sendData[i] = data[i - 7];
		}
		return sendData;
	}

	public static ArrayList<Mode> extractModel(byte[] devInfoData) 
	{
		
		Log.e("ooooooooooo",""+devInfoData.length);
		ArrayList<Mode> modes = new ArrayList<Mode>();
			
		byte[] localData = devInfoData.clone();
		
			byte[][]namebyte = new byte[32][12];
			
			for(int m = 0; m < 32; m++)
			{
				Mode mode = new Mode();
				for (int n = 0; n<12; n++)
				{
					namebyte[m][n] = localData[12*m+n];
				}
				
				try 
				{
					mode.sceneName = new String(namebyte[m], 0, 12, "GB2312");
					
				} 
				catch(UnsupportedEncodingException e) 
				{
					e.printStackTrace();
				}
				
				mode.indexScene = m;
				mode.valid = byteToInt(new byte[] {localData[384+m]});
				
				if(mode.valid==1)
				{
					modes.add(mode);
					Log.e("dds","/////"+mode.indexScene);
				}

			}
			
		return modes;
	}

	public static byte[] sendMode(int pktSn,int m, String srcIp, String dstIp) throws UnknownHostException 
	{
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;
		
		dataPacket.dstAddr = InetAddress.getByName(dstIp).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcIp).getAddress();
		
		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_SetDevScene .getValue();
		
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;

		dataPacket.subCmd = GlobalDataPacket.SUB_CMD.SPN_tellUpRoomInfo.getValue();
		
		dataPacket.param = m ;//命令参数
		
		dataPacket.uID = new byte[12];
		for (int i = 0; i < 12; i++)
		{
			dataPacket.uID[i] = (byte) (0xff);
		}
		dataPacket.dataLen = 0;
		dataPacket.globalData = new byte[1];
		dataPacket.globalData[0] = (byte) (0xff);

		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData().clone();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++)
		{
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) 
		{
			sendData[i] = data[i - 7];
		}
		return sendData;
	}

	public static byte[] DemandService(int pktSn, String srcIp, String dstIp) throws UnknownHostException {
		
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;
		
		dataPacket.dstAddr = InetAddress.getByName(dstIp).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(srcIp).getAddress();
		
		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_SetSysInfoImg .getValue();
		
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;

		dataPacket.subCmd = GlobalDataPacket.SUB_CMD.SPN_config.getValue();
		
		dataPacket.param = 0 ;//命令参数
		
		dataPacket.uID = new byte[12];
		for (int i = 0; i < 12; i++)
		{
			dataPacket.uID[i] = (byte) (0xff);
		}
		dataPacket.dataLen = 0;
		dataPacket.globalData = new byte[1];
		dataPacket.globalData[0] = (byte) (0xff);

		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData().clone();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++)
		{
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) 
		{
			sendData[i] = data[i - 7];
		}
		return sendData;
	}

	public static ArrayList<Service> extractService(byte[] devInfoData) 
	{
		
		Log.e("", ""+devInfoData.length);
		ArrayList<Service> services = new ArrayList<Service>();
		
		byte[] localData = devInfoData.clone();
		
			byte[][]namebyte = new byte[16][32];
			
			for(int m = 0; m < 16; m++)
			{
				Service service = new Service();
				
				for (int n = 0; n<32; n++)
				{
					namebyte[m][n] = localData[32*m+n];
				}
				
				try 
				{
					 service.serv_name= new String(namebyte[m], 0, 32, "GB2312");
					
					 Log.e("dds","/////"+service.serv_name);
				} 
				catch(UnsupportedEncodingException e) 
				{
					e.printStackTrace();
				}
				
				service.serv_id = localData[512+m];
				service.bVaild = byteToInt(new byte[] {localData[528+m]});
				Log.e("dds","/////"+service.bVaild);
				
				 if(service.bVaild == 1)
				{
					services.add(service);
					
				}

			}
			
		return services;
		
	}

	public static byte[] sendService(int pktSn, int m, String localIp,
			String ipAddr, String b,String u,String l,String r ) throws UnknownHostException 
	{
		GlobalDataPacket dataPacket = new GlobalDataPacket();
		dataPacket.pktType = GlobalDataPacket.PKT_TYPE.PT_UDP.getValue();
		dataPacket.isAck = GlobalDataPacket.IS_ACK.NOT_ACK.getValue();
		dataPacket.pktSn = pktSn;
		
		dataPacket.dstAddr = InetAddress.getByName(ipAddr).getAddress();
		dataPacket.srcAddr = InetAddress.getByName(localIp).getAddress();
		
		dataPacket.globalCmd = GlobalDataPacket.GLOBAL_CMD.GC_SetSysInfoImg .getValue();
		
		dataPacket.globalCmdHandlerResult = GlobalDataPacket.GLOBAL_CMD_HANDLER_RESULT.GCHR_RES_NULL
				.getValue();

		dataPacket.reserve = 0;

		dataPacket.subCmd = GlobalDataPacket.SUB_CMD.SPN_roomNum.getValue();
		
		dataPacket.param = m ;//命令参数
		
		dataPacket.uID = new byte[12];
		for (int i = 0; i < 12; i++)
		{
			dataPacket.uID[i] = (byte) (0xff);
		}
		
		dataPacket.dataLen = 4;
		dataPacket.globalData = new byte[4];
		dataPacket.globalData[0] =byteToByte(b.getBytes());
		dataPacket.globalData[1] =byteToByte(u.getBytes()); 
		dataPacket.globalData[2] =byteToByte(l.getBytes()); 
		dataPacket.globalData[3] =byteToByte(r.getBytes()); 
		Log.e("", "xxxx"+dataPacket.globalData[0]);
		Log.e("", "xxxx"+dataPacket.globalData[1]);
		Log.e("", "xxxx"+dataPacket.globalData[2]);
		Log.e("", "xxxx"+dataPacket.globalData[3]);
		
		dataPacket.pktCrc = 0;

		byte[] data = dataPacket.getData();

		byte[] sendData = new byte[7 + data.length];
		byte[] header = "XXXCID".getBytes();
		for (int i = 0; i < 6; i++)
		{
			sendData[i] = header[i];
		}
		sendData[6] = (byte) (0xff);

		for (int i = 7; i < sendData.length; i++) 
		{
			sendData[i] = data[i - 7];
		}
		return sendData;
	}

	
}