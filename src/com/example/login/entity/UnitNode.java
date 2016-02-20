package com.example.login.entity;

import android.widget.Button;

public class UnitNode {
	/*
	 * ID 4 name 12 IpAddr 4 GateWay 4 inDoorServ 4 centerServ 4 roomNum 4
	 * config 4 macAddr 6 rev1 2 Us485Addr 1 UsCanAddr 1 S315mType 1 rev2 1
	 * TnetAddr 2 port 2 softversion 4 hwversion 4
	 */
	public int id;
	public String name;
	public String ipAddr;
	public String subMask;
	public String gateWay;
	public String inDoorServ;
	public String centerServ;
	public String roomNum;
	public int config;
	public String macAddr;
	public int rev1;
	public int us485Addr;
	public int uscanAddr;
	public int s315mType;
	public int rev2;
	public int tnetAddr;
	public int port;
	public String uID;
	public String softVersion;
	public String hwVersion;
	public Button pBtn;

}
