package com.example.login.entity;

public class GlobalDataPacket 
{

	public int pktType;
	public int isAck;
	public int pktSn;
	public byte[] dstAddr;
	public byte[] srcAddr;
	public int globalCmd;
	public int globalCmdHandlerResult;
	public int reserve;// 字节对齐保留
	public int subCmd;
	public int param;
	public int dataLen;
	public byte[] uID;// 节点的UID
	public byte[] globalData;
	public int pktCrc;

	public GlobalDataPacket() 
	{
	}

	public GlobalDataPacket(int pktType,// 包类型
			int isAck,// 是否应答包
			int pktSn,// 序列号
			byte[] dstAddr,// 目的地址
			byte[] srcAddr,// 源地址
			int globalCmd,// 命令类型
			int globalCmdHandlerResult, int reserve,// 字节对齐保留
			int subCmd,// 子命令
			int param,// 命令参数
			int dataLen,// 数据区长度
			int pktCrc) {// 此包校验和

		this.pktType = pktType;
		this.isAck = isAck;
		this.pktSn = pktSn;
		this.dstAddr = dstAddr;
		this.srcAddr = srcAddr;
		this.globalCmd = globalCmd;
		this.globalCmdHandlerResult = globalCmdHandlerResult;
		this.reserve = reserve;
		this.subCmd = subCmd;
		this.param = param;
		this.dataLen = dataLen;
		this.pktCrc = pktCrc;

	}

	public byte[] getData() 
	{
		byte[] data = new byte[38 + globalData.length];
		// pktType
		data[0] = (byte) (this.pktType & 0xff);

		// isAck
		data[1] = (byte) (this.isAck & 0xff);

		// pktSn
		data[2] = (byte) (this.pktSn & 0xff);
		data[3] = (byte) (this.pktSn << 8 & 0xff);

		// dstAddr
		data[4] = this.dstAddr[0];
		data[5] = this.dstAddr[1];
		data[6] = this.dstAddr[2];
		data[7] = this.dstAddr[3];

		// srcAddr
		data[8] = this.srcAddr[0];
		data[9] = this.srcAddr[1];
		data[10] = this.srcAddr[2];
		data[11] = this.srcAddr[3];

		// cmd
		data[12] = (byte) (this.globalCmd & 0xff);

		// global cmd handler result
		data[13] = (byte) (this.globalCmdHandlerResult & 0xff);

		// reserve
		data[14] = (byte) (this.reserve & 0xff);
		data[15] = (byte) (this.reserve << 8 & 0xff);

		// subCmd
		data[16] = (byte) (this.subCmd & 0xff);
		data[17] = (byte) (this.subCmd << 8 & 0xff);
		data[18] = (byte) (this.subCmd << 16 & 0xff);
		data[19] = (byte) (this.subCmd << 24 & 0xff);

		// param
		data[20] = (byte) (this.param & 0xff);
		data[21] = (byte) (this.param << 8 & 0xff);
		data[22] = (byte) (this.param << 16 & 0xff);
		data[23] = (byte) (this.param << 24 & 0xff);

		for (int i = 0; i < 12; i++) 
		{
			data[24 + i] = uID[i];
		}

		// dataLength
		data[36] = (byte) (this.dataLen & 0xff);
		data[37] = (byte) (this.dataLen << 8 & 0xff);

		for (int i = 0; i < this.dataLen; i++)
		{
			data[38 + i] = globalData[i];
		}

		return data;

	}

	// 包的类型
	public static enum PKT_TYPE 
	{
		PT_TCP(0), PT_U485(1), PT_CAN_US(2), PT_UDP(3);
		private int value;

		PKT_TYPE(int value) 
		{
			this.value = value;
		}

		public int getValue() 
		{
			return value;
		}
	};

	// 是否是应答包
	public static enum IS_ACK
	{
		NOT_ACK(0), IS_ACK(1);
		private int value;

		IS_ACK(int value) 
		{
			this.value = value;
		}

		public int getValue() 
		{
			return this.value;
		}

	}

	// 命令类型
	public static enum GLOBAL_CMD 
	{
		GC_Null(0), GC_GetSysParam(1), GC_SetSysParam(2), GC_SaveSysParam(3), GC_StartAddTerminal(
				4), GC_StopAddTerminal(5), GC_DeleteTerminal(6), GC_IrRecv(7), GC_IrSend(
				8), GC_IrSaveCurr(9), GC_IrSave(10), GC_IrReadByIdx(11), GC_IrReadByFlashAdd(
				12), GC_IrRemove(13), GC_IrClean(14), GC_S315mRecv(15), GC_S315mSend(
				16), GC_S315mSaveCurr(17), GC_S315mSave(18), GC_S315mReadByIdx(
				19), GC_S315mReadByFlashAddr(20), GC_S315mRemove(21), GC_S315mClean(
				22), GC_SetSysInfoTxt(23), GC_SetSysInfoImg(24), GC_PublicSysInfo(
				25), GC_Hello(26), GC_SetAirCmds(27), GC_ExeAirCmds(28), GC_ExeTvCmds(
				29), GC_ExeTvUPCmds(30), GC_ExeLgtCmds(31), GC_ExeCurtCmds(32), GC_ExeLockCmds(
				33), GC_ExeValveCmds(34), GC_ExeOtherCmds(35), GC_QueryDev(36), GC_QueryUserDev(
				37), GC_QueryRooms(38), GC_tellRooms(39), GC_Transmit(40), GC_readDevInfo(
				41), GC_DeleteDev(42), GC_AddDev(43), GC_EditDev(44), GC_SetDevScene(
				45);

		private int value;

		GLOBAL_CMD(int value) 
		{
			this.value = value;
		}

		public int getValue() 
		{
			return this.value;
		}
	};

	public static enum GLOBAL_CMD_HANDLER_RESULT 
	{
		GCHR_SUCCESS(0), GCHR_FAILD(1), GCHR_PARAM_ERROR(2), GCHR_RES_NULL(3), GCHR_RES_UNENOUGH(
				4);

		private int value;

		GLOBAL_CMD_HANDLER_RESULT(int value) 
		{
			this.value = value;
		}

		public int getValue()
		{
			return this.value;
		}
	}

	public static enum SUB_CMD 
	{
		SPN_Null(0), SPN_ID(1), SPN_Name(2), SPN_IpAddr(3), SPN_MacAddr(4), SPN_SubMask(
				5), SPN_Gataway(6), SPN_inDoorServ(7), SPN_centerServ(8), SPN_roomNum(
				9), SPN_config(10), SPN_Port(11), SPN_Us485Addr(12), SPN_UsCanAddr(
				13), SPN_TnetAddr(14), SPN_S315mType(15), SPN_TnetDeviceAddrList(
				16), SPN_IrTypeList(17), SPN_IrTypeNamePtr(18), SPN_IrKeyNamePtr(
				19), SPN_S315mKeyNamePtr(20), SPN_VendorIrInfo(21), SPN_SoftVer(
				22), SPN_HwVer(23), SPN_ChkSum(24), SPN_SavAirCmdsStart(25), SPN_SavAirCmdsData(
				26), SPN_SavAirCmdsEnd(27), SPN_SavAirCmdsNoSpace(28), SPN_noAirBrandModel(
				29), SPN_airCmdsSaveErr(30), SPN_queryAirDev(31), SPN_queryAirDevInfo(
				32), SPN_setHelloParam(33), SPN_askUpRoomInfo(34), SPN_tellUpRoomInfo(
				35), SPN_transmitAirCmd(36), SPN_ackTransAirCmd(37), SPN_Total(
				38);

		private int value;

		SUB_CMD(int value) 
		{
			this.value = value;
		}

		public int getValue() 
		{
			return this.value;
		}
	}

	public static enum E_WARE_TYPE 
	{
		e_ware_airCond(0), e_ware_tv(1), e_ware_tvUP(2), e_ware_light(3), e_ware_curtain(
				4), e_ware_lock(5), e_ware_valve(6), e_ware_projector(7), // 投影仪
		e_ware_center_ctrl(8), e_ware_other(9), e_ware_sm(10);// 湿度控制

		private int value;

		private E_WARE_TYPE(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return this.value;
		}
	}

	public static enum E_LGT_CMD
	{
		e_lgt_offOn(0), e_lgt_onOff(1), e_lgt_dark(2), e_lgt_bright(3), e_lgt_cmd_total(
				4);

		private int value;

		E_LGT_CMD(int value) 
		{
			this.value = value;
		}

		public int getValue() 
		{
			return this.value;
		}
	}

	public static enum E_AIR_CMD 
	{
		e_air_pwrOn(0), e_air_pwrOff(1), e_air_spdLow(2), e_air_spdMid(3), e_air_spdHigh(
				4), e_air_spdAuto(5), e_air_drctUpDn1(6), // 上下摇摆
		e_air_drctUpDn2(7), e_air_drctUpDn3(8), e_air_drctUpDnAuto(9), e_air_drctLfRt1(
				10), // 左右摇摆
		e_air_drctLfRt2(11), e_air_drctLfRt3(12), e_air_drctLfRtAuto(13), e_air_temp14(
				14), e_air_temp15(15), e_air_temp16(16), e_air_temp17(17), e_air_temp18(
				18), e_air_temp19(19), e_air_temp20(20), e_air_temp21(21), e_air_temp22(
				22), e_air_temp23(23), e_air_temp24(24), e_air_temp25(25), e_air_temp26(
				26), e_air_temp27(27), e_air_temp28(28), e_air_temp29(29), e_air_temp30(
				30), e_air_cmd_total(31);

		private int value;

		E_AIR_CMD(int value)
		{
			this.value = value;
		}

		public int getValue() 
		{
			return this.value;
		}
	}

	public static enum E_AIR_MODE 
	{
		e_air_auto(0), e_air_hot(1), e_air_cool(2), e_air_dry(3), e_air_wind(4), e_air_mode_total(
				5);

		private int value;

		E_AIR_MODE(int value) 
		{
			this.value = value;
		}

		public int getValue() 
		{
			return this.value;
		}
	}

	public static enum E_TV_CMD 
	{
		e_tv_offOn(0), e_tv_mute(1), e_tv_numTvAv(2), e_tv_num1(3), e_tv_num2(4), e_tv_num3(
				5), e_tv_num4(6), e_tv_num5(7), e_tv_num6(8), e_tv_num7(9), e_tv_num8(
				10), e_tv_num9(11), e_tv_numMenu(12), e_tv_numUp(13), e_tv_num0(
				14), e_tv_numLf(15), e_tv_enter(16), e_tv_numRt(17), e_tv_numRet(
				18), e_tv_numDn(19), e_tv_numLookBack(20), e_tv_cmd_total(21);

		private int value;

		E_TV_CMD(int value) 
		{
			this.value = value;
		}

		public int getValue()
		{
			return this.value;
		}
	}

	public static enum E_TVUP_CMD 
	{
		e_tvUP_offOn(0), e_tvUP_mute(1), e_tvUP_numPg(2), e_tvUP_num1(3), e_tvUP_num2(
				4), e_tvUP_num3(5), e_tvUP_num4(6), e_tvUP_num5(7), e_tvUP_num6(
				8), e_tvUP_num7(9), e_tvUP_num8(10), e_tvUP_num9(11), e_tvUP_numDemand(
				12), e_tvUP_numUp(13), e_tvUP_num0(14), e_tvUP_numLf(15), e_tvUP_enter(
				16), e_tvUP_numRt(17), e_tvUP_numInteract(18), e_tvUP_numDn(19), e_tvUP_numBack(
				20), e_tvUP_numVInc(21), e_tvUP_numInfo(22), e_tvUP_numPInc(23), e_tvUP_numVDec(
				24), e_tvUP_numLive(25), e_tvUP_numPDec(26), e_tvUP_cmd_total(
				27);

		private int value;

		E_TVUP_CMD(int value) 
		{
			this.value = value;
		}

		public int getValue() 
		{
			return this.value;
		}
	}
}
