package com.example.login.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.R;
import com.example.login.common.CommonUtils;
import com.example.login.common.Constants;
import com.example.login.database.DBHelper;
import com.example.login.entity.GlobalDataPacket;
import com.example.login.entity.UnitNode;
import com.example.login.entity.WareCurtain;
import com.example.login.entity.WareDev;
import com.example.login.entity.WareLight;
import com.example.login.entity.WareLock;
import com.example.login.entity.WareOther;
import com.example.login.entity.WareValve;
import com.example.login.entity.airCondDev;

public class Activity2 extends AdAbstractActivity implements
		OnCheckedChangeListener,
		android.widget.RadioGroup.OnCheckedChangeListener
		{
	private LinearLayout remoteControlLayout;
	private LinearLayout roomsLayout;

	private DBHelper dbHelper;
	int mDevType2Query;

	private MulticastSocket castClass;

	ExecutorService executorService;
	private LinearLayout.LayoutParams WW = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);

	private static final int MSG_UPDATE_LIGHT = 1001;
	private static final int MSG_UPDATE_CURTAIN = 1002;
	private static final int MSG_UPDATE_AIRCOND = 1003;

	private RadioButton lightRadioBtn;
	private RadioButton curtainRadioBtn;
	private RadioButton acRadioBtn;
	public RadioButton currRadioBtnSel;// 自定义的空按钮

	private TextView tempTv;// 温度
	private TextView destTempTv;// 湿度
	private TextView PM25Tv;// 颗粒

	private RadioGroup itemsLayout;

	private MyLightAdapter mLightAdapter;
	private MyCurtainAdapter mCurtainAdapter;// 窗帘

	private List<WareLight> mLightDataset;
	private List<WareCurtain> mCurtainDataset;
	private List<airCondDev> mAircondDataset;
	List<UnitNode> nodes;
	UnitNode curretNode;

	private GridView mGrid;
	private ScrollView acScrollView;

	private Handler mHandler = new Handler() 
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what)
			{
			case MSG_UPDATE_LIGHT:
				if (currRadioBtnSel == lightRadioBtn) 
				{
					mLightAdapter.notifyDataSetChanged();// 通知适配器改变
				}

				break;

			case MSG_UPDATE_CURTAIN:
				if (currRadioBtnSel == curtainRadioBtn) 
				{
					mCurtainAdapter.notifyDataSetChanged();
				}
				break;

			case MSG_UPDATE_AIRCOND:
				
				if(mAircondDataset.size() > 0)
				{
				airCondDev dev = mAircondDataset.get(0);
				
				String pm = "PM2.5值：" + dev.pm25 + "湿度:" + dev.humidity;
				PM25Tv.setText(pm);
				tempTv.setText("室温：" + Integer.toString(dev.tempReal));
				if (dev.pwrOn == 1) {
					findViewById(R.id.acPower).setPressed(true);

					findViewById(R.id.tempModeAuto).setPressed(false);
					findViewById(R.id.zhireBtn).setPressed(false);
					findViewById(R.id.zhilengBtn).setPressed(false);
					findViewById(R.id.choushiBtn).setPressed(false);

					switch (dev.modeSel) {
					case 0:
						findViewById(R.id.tempModeAuto).setPressed(true);
						break;
					case 1:// e_air_hot
						findViewById(R.id.zhireBtn).setPressed(true);
						break;
					case 2:// e_air_cool
						findViewById(R.id.zhilengBtn).setPressed(true);
						break;
					case 3:// e_air_dry
						findViewById(R.id.choushiBtn).setPressed(true);
						break;

					}
					findViewById(R.id.difengBtn).setPressed(false);
					findViewById(R.id.zhongfengBtn).setPressed(false);
					findViewById(R.id.gaofengBtn).setPressed(false);
					findViewById(R.id.windModeAuto).setPressed(false);

					switch (dev.spdSel) {
					case 2:
						findViewById(R.id.difengBtn).setPressed(true);
						break;
					case 3:// e_air_spdMid
						findViewById(R.id.zhongfengBtn).setPressed(true);
						break;
					case 4:// e_air_spdHigh
						findViewById(R.id.gaofengBtn).setPressed(true);
						break;
					case 5:// e_air_spdAuto
						findViewById(R.id.windModeAuto).setPressed(true);
						break;

					default:
						break;
					}
					switch (dev.tempSel) 
					{
					case 14:// e_air_temp14
						destTempTv.setText("14");
						break;
					case 15:// e_air_temp14
						destTempTv.setText("15");
						break;
					case 16:// e_air_temp14
						destTempTv.setText("16");
						break;
					case 17:// e_air_temp14
						destTempTv.setText("17");
						break;
					case 18:// e_air_temp14
						destTempTv.setText("18");
						break;
					case 19:// e_air_temp14
						destTempTv.setText("19");
						break;
					case 20:// e_air_temp14
						destTempTv.setText("20");
						break;
					case 21:// e_air_temp14
						destTempTv.setText("21");
						break;
					case 22:// e_air_temp14
						destTempTv.setText("22");
						break;
					case 23:// e_air_temp14
						destTempTv.setText("23");
						break;
					case 24:// e_air_temp14
						destTempTv.setText("24");
						break;
					case 25:// e_air_temp14
						destTempTv.setText("25");
						break;
					case 26:// e_air_temp14
						destTempTv.setText("26");
						break;
					case 27:// e_air_temp14
						destTempTv.setText("27");
						break;
					case 28:// e_air_temp14
						destTempTv.setText("28");
						break;
					case 29:// e_air_temp14
						destTempTv.setText("29");
						break;
					case 30:// e_air_temp14
						destTempTv.setText("30");
						break;
					default:
						destTempTv.setText(" ");
					}
				} else {
					findViewById(R.id.acPower).setPressed(false);
					findViewById(R.id.tempModeAuto).setPressed(false);
					findViewById(R.id.zhireBtn).setPressed(false);
					findViewById(R.id.zhilengBtn).setPressed(false);
					findViewById(R.id.choushiBtn).setPressed(false);
					findViewById(R.id.difengBtn).setPressed(false);
					findViewById(R.id.zhongfengBtn).setPressed(false);
					findViewById(R.id.gaofengBtn).setPressed(false);
					findViewById(R.id.windModeAuto).setPressed(false);
					destTempTv.setText(" ");
				}
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		SysApplication.getInstance().addActivity(this);
		
		remoteControlLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.function, null);
		mRootView.addView(remoteControlLayout, FF);

		initComponent();
	}

	private void initComponent() {
		setTextForTitle(getString(R.string.activity_title_remote_control));
		setTextForBackBtn(getString(R.string.app_back));

		lightRadioBtn = (RadioButton) findViewById(R.id.lightRadioBtn);
		curtainRadioBtn = (RadioButton) findViewById(R.id.curtainRadioBtn);
		acRadioBtn = (RadioButton) findViewById(R.id.acRadioBtn);
		currRadioBtnSel = lightRadioBtn;

		lightRadioBtn.setOnCheckedChangeListener(this);
		curtainRadioBtn.setOnCheckedChangeListener(this);
		acRadioBtn.setOnCheckedChangeListener(this);

		itemsLayout = (RadioGroup) findViewById(R.id.itemsLayout);
		itemsLayout.setOnCheckedChangeListener(this);

		roomsLayout = (LinearLayout) findViewById(R.id.rooms_layout);

		tempTv = (TextView) findViewById(R.id.tempTv);
		destTempTv = (TextView) findViewById(R.id.destTempTv);
		PM25Tv = (TextView) findViewById(R.id.tvPm25);

		Typeface font = Typeface.createFromAsset(getAssets(), "lcd2mono.ttf");// 自定义字体样式
		tempTv.setTypeface(font);
		destTempTv.setTypeface(font);

		dbHelper = new DBHelper(this);
		nodes = dbHelper.queryAllUnitNodes();
		
		curretNode = nodes.get(0);// null;

		roomsLayout.removeAllViews();// 清空所有的视图

		for (UnitNode node : nodes) {
			Button button = new Button(this);
			button.setText(node.name);
			node.pBtn = button;
			button.setMinWidth(80);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					for (int i = 0; i < roomsLayout.getChildCount(); i++) {
						View vChild = roomsLayout.getChildAt(i);
						if (vChild instanceof Button) {
							if (v != vChild) {
								((Button) vChild).setTextColor(getResources()
										.getColor(R.color.room_btn_text));
							}
						}
					}
					((Button) v).setTextColor(getResources().getColor(
							R.color.black));
					for (UnitNode node : nodes) {
						if (node.pBtn == ((Button) v)) {
							curretNode = node;
							break;
						}
					}
					if (curretNode == null) {

						return;
					} else {
						if (currRadioBtnSel == lightRadioBtn) {
							getNodeDevInfo(GlobalDataPacket.E_WARE_TYPE.e_ware_light
									.getValue());
						} else if (currRadioBtnSel == curtainRadioBtn) {
							getNodeDevInfo(GlobalDataPacket.E_WARE_TYPE.e_ware_curtain
									.getValue());
						}

						else if (currRadioBtnSel == acRadioBtn) {
							getNodeDevInfo(GlobalDataPacket.E_WARE_TYPE.e_ware_airCond
									.getValue());
						}
					}
				}
			});
			button.setTextSize(getResources().getDimension(
					R.dimen.LargerTextSize));
			button.setTextColor(getResources().getColor(R.color.room_btn_text));
			button.setBackgroundResource(R.drawable.room_btn);
			WW.setMargins(5, 5, 5, 5);
			roomsLayout.addView(button, WW);

		}
		mGrid = (GridView) findViewById(R.id.grid);

		acScrollView = (ScrollView) findViewById(R.id.acScrollView);
		findViewById(R.id.acPower).setOnClickListener(this);
		findViewById(R.id.difengBtn).setOnClickListener(this);
		findViewById(R.id.gaofengBtn).setOnClickListener(this);
		findViewById(R.id.zhongfengBtn).setOnClickListener(this);
		findViewById(R.id.windModeAuto).setOnClickListener(this);
		findViewById(R.id.tempDecrese).setOnClickListener(this);
		findViewById(R.id.tempIncrese).setOnClickListener(this);
		findViewById(R.id.zhireBtn).setOnClickListener(this);
		findViewById(R.id.zhilengBtn).setOnClickListener(this);
		findViewById(R.id.choushiBtn).setOnClickListener(this);
		findViewById(R.id.tempModeAuto).setOnClickListener(this);

		executorService = Executors.newSingleThreadExecutor();
		executorService.execute(new CastServer());

		mLightDataset = new ArrayList<WareLight>();
		mCurtainDataset = new ArrayList<WareCurtain>();

		mLightAdapter = new MyLightAdapter();// 新建个失配器放数据
		mCurtainAdapter = new MyCurtainAdapter();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void getNodeDevInfo(int devType) {
		mDevType2Query = devType;

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					queryByWareType(mDevType2Query);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	@Override
	protected void onPause() {
		super.onPause();
		executorService.shutdownNow();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_btn4:
			finish();
			break;

		case R.id.acPower:
			if (mAircondDataset.get(0).pwrOn == 0)// /电源开问题
			{
				new Thread(new Runnable() {

					@Override
					public void run() {
						sendAircondCtrlCmd(
								GlobalDataPacket.E_AIR_CMD.e_air_pwrOn
										.getValue(),
								mAircondDataset.get(0).modeSel);

					}
				}).start();
			} else {
				new Thread(new Runnable() {

					@Override
					public void run() {
						sendAircondCtrlCmd(
								GlobalDataPacket.E_AIR_CMD.e_air_pwrOff
										.getValue(),
								mAircondDataset.get(0).modeSel);
					}
				}).start();
			}
			break;

		case R.id.difengBtn:
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendAircondCtrlCmd(
							GlobalDataPacket.E_AIR_CMD.e_air_spdLow.getValue(),
							mAircondDataset.get(0).modeSel);
				}
			}).start();
			break;
		case R.id.gaofengBtn:
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendAircondCtrlCmd(
							GlobalDataPacket.E_AIR_CMD.e_air_spdHigh.getValue(),
							mAircondDataset.get(0).modeSel);
				}
			}).start();
			break;
		case R.id.zhongfengBtn:
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendAircondCtrlCmd(
							GlobalDataPacket.E_AIR_CMD.e_air_spdMid.getValue(),
							mAircondDataset.get(0).modeSel);
				}
			}).start();
			break;
		case R.id.windModeAuto:
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendAircondCtrlCmd(
							GlobalDataPacket.E_AIR_CMD.e_air_spdAuto.getValue(),
							mAircondDataset.get(0).modeSel);
				}
			}).start();
			break;
		case R.id.tempDecrese:// 降低温度
			new Thread(new Runnable() {
				@Override
				public void run() {
					int tempSel = mAircondDataset.get(0).tempSel;
					tempSel--;
					if (tempSel < GlobalDataPacket.E_AIR_CMD.e_air_temp14
							.getValue()) {
						tempSel = GlobalDataPacket.E_AIR_CMD.e_air_temp14
								.getValue();
					}
					sendAircondCtrlCmd(tempSel, mAircondDataset.get(0).modeSel);
				}
			}).start();
			break;
		case R.id.tempIncrese:// 增加温度
			new Thread(new Runnable() {
				@Override
				public void run() {
					int tempSel = mAircondDataset.get(0).tempSel;
					tempSel++;
					if (tempSel > GlobalDataPacket.E_AIR_CMD.e_air_temp30
							.getValue()) {
						tempSel = GlobalDataPacket.E_AIR_CMD.e_air_temp30
								.getValue();
					}
					sendAircondCtrlCmd(tempSel, mAircondDataset.get(0).modeSel);
				}
			}).start();
			break;
		case R.id.zhireBtn:// 制热
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendAircondCtrlCmd(
							GlobalDataPacket.E_AIR_CMD.e_air_pwrOn.getValue(),
							GlobalDataPacket.E_AIR_MODE.e_air_hot.getValue());
				}
			}).start();
			break;
		case R.id.zhilengBtn:// 制冷
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendAircondCtrlCmd(
							GlobalDataPacket.E_AIR_CMD.e_air_pwrOn.getValue(),
							GlobalDataPacket.E_AIR_MODE.e_air_cool.getValue());
				}
			}).start();
			break;
		case R.id.choushiBtn:// 抽湿
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendAircondCtrlCmd(
							GlobalDataPacket.E_AIR_CMD.e_air_pwrOn.getValue(),
							GlobalDataPacket.E_AIR_MODE.e_air_dry.getValue());
				}
			}).start();
			break;
		case R.id.tempModeAuto:// 自动
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendAircondCtrlCmd(
							GlobalDataPacket.E_AIR_CMD.e_air_pwrOn.getValue(),
							GlobalDataPacket.E_AIR_MODE.e_air_auto.getValue());
				}
			}).start();
			break;

		default:
			break;

		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.lightRadioBtn:
			mGrid.setNumColumns(3);// 设置列数
			mGrid.setAdapter(mLightAdapter);
			mGrid.setVisibility(View.VISIBLE);
			acScrollView.setVisibility(View.GONE);
			this.getNodeDevInfo(GlobalDataPacket.E_WARE_TYPE.e_ware_light
					.getValue());//
			currRadioBtnSel = lightRadioBtn;
			break;

		case R.id.curtainRadioBtn:
			mGrid.setNumColumns(1);
			mGrid.setAdapter(mCurtainAdapter);
			mGrid.setVisibility(View.VISIBLE);
			acScrollView.setVisibility(View.GONE);
			currRadioBtnSel = curtainRadioBtn;
			this.getNodeDevInfo(GlobalDataPacket.E_WARE_TYPE.e_ware_curtain
					.getValue());
			break;

		case R.id.acRadioBtn:
			mGrid.setVisibility(View.GONE);
			acScrollView.setVisibility(View.VISIBLE);
			currRadioBtnSel = acRadioBtn;
			this.getNodeDevInfo(GlobalDataPacket.E_WARE_TYPE.e_ware_airCond
					.getValue());
			break;

		}
	}

	private void queryByWareType(int wareType) throws IOException {
		if (curretNode == null) {
			Toast.makeText(this, "没节点", Toast.LENGTH_SHORT).show();
			return;
		}
		byte[] data = CommonUtils.queryByWareType(Constants.sn++, wareType,
				curretNode.ipAddr, CommonUtils.getLocalIp(), curretNode.uID);
		DatagramPacket packet = new DatagramPacket(data, data.length,
				InetAddress.getByName(curretNode.ipAddr), 8300);
		DatagramSocket socket = new DatagramSocket();
		socket.send(packet);
		socket = null;
	}

	private void sendAircondCtrlCmd(int cmd, int mode) {
		if (curretNode == null) {
			Toast.makeText(this, "当前没有节点", Toast.LENGTH_SHORT).show();
			return;
		}
		byte[] cmdBuff = new byte[2];
		cmdBuff[0] = (byte) (mode & 0xff);
		cmdBuff[1] = (byte) (cmd & 0xff);
		try {
			byte[] data = CommonUtils.preSendGlobalPkt(Constants.sn++,
					curretNode.ipAddr, CommonUtils.getLocalIp(),
					GlobalDataPacket.GLOBAL_CMD.GC_ExeAirCmds.getValue(),
					GlobalDataPacket.SUB_CMD.SPN_noAirBrandModel.getValue(), 0,
					cmdBuff, 2, curretNode.uID);

			DatagramPacket packet = new DatagramPacket(data, data.length,
					InetAddress.getByName(curretNode.ipAddr), 8300);
			// castClass.send(packet);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket = null;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private class CastServer implements Runnable {

		@Override
		public void run() {
			byte[] buffer = new byte[2048];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			int i = 0;
			try {
				castClass = new MulticastSocket(8300);
				while (i == 0) {
					castClass.receive(packet);
					i = packet.getLength();
					if (i > 0) {
						extractData(packet);
						i = 0;
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void extractData(DatagramPacket packet) {
		byte[] data = packet.getData();

		byte[] localData = new byte[packet.getLength() - 7];
		for (int i = 0; i < localData.length; i++) {
			localData[i] = data[i + 7];
		}

		int isAck = localData[1] & 0xff;

		int pktSn = localData[3] & 0xff;
		pktSn = CommonUtils
				.byteToInt(new byte[] { localData[2], localData[3] });

		String mDstAddr = (localData[4] & 0xff) + "." + (localData[5] & 0xff)
				+ "." + (localData[6] & 0xff) + "." + (localData[7] & 0xff);

		String mSrcAddr = (localData[8] & 0xff) + "." + (localData[9] & 0xff)
				+ "." + (localData[10] & 0xff) + "." + (localData[11] & 0xff);

		if (isAck == 0 && CommonUtils.getLocalIp().equals(mDstAddr)) {
			try {
				byte[] replyData = CommonUtils.reply(packet);
				DatagramPacket replyPacket = new DatagramPacket(replyData,
						replyData.length, InetAddress.getByName(mSrcAddr), 8300);
				CommonUtils.castUdpPacket(castClass, replyPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}

			int globalCmd = CommonUtils.byteToInt(new byte[] { localData[12] });

			int subCmd = CommonUtils.byteToInt(new byte[] { localData[16],
					localData[17], localData[18], localData[19] });

			int param = CommonUtils.byteToInt(new byte[] { localData[20],
					localData[21], localData[22], localData[23]

			});

			switch (globalCmd) {
			case 26:// GC_Hello
				if (subCmd == 10)// SPN_config
				{
					int datalen = CommonUtils.byteToInt(new byte[] {
							localData[36], localData[37], });
					byte[] nodeData = new byte[datalen];
					for (int i = 0; i < nodeData.length; i++) {
						nodeData[i] = localData[i + 38];
					}
					UnitNode node = CommonUtils.extractUnitNode(nodeData);

					if (node != null) {
						dbHelper.clearUnitNode();

						// 更新数据库
					}
					long id = dbHelper.insertUnitNode(node);

					return;
				}
				break;

			case 36:// GC_QueryDev
				if (subCmd == 32)// SPN_queryAirDevInfo
				{
					int datalen = CommonUtils.byteToInt(new byte[] {
							localData[36], localData[37] });
					byte[] devInfoData = new byte[datalen];
					for (int i = 0; i < devInfoData.length; i++) {
						devInfoData[i] = localData[i + 38];
					}
				} else if (subCmd == 38) {// SPN_Total
					// query devices by waretype
					int datalen = CommonUtils.byteToInt(new byte[] {
							localData[36], localData[37] });
					byte[] devInfoData = new byte[datalen];
					for (int i = 0; i < devInfoData.length; i++) {
						devInfoData[i] = localData[i + 38];
					}
					switch (param) {
					case 0:
						List<airCondDev> airconds = CommonUtils
								.extractAirconds(devInfoData);
						mAircondDataset = airconds;
						mHandler.sendEmptyMessage(MSG_UPDATE_AIRCOND);
						break;
					// e_ware_tv
					case 1:
						break;

					// e_ware_tvUP
					case 2:
						break;

					// e_ware_light
					case 3:
						Log.e("", "-------rev lgt into by type "
								+ devInfoData.length);
						List<WareLight> lights = CommonUtils
								.extractLights(devInfoData);
						mLightDataset = lights;
						// Message msg = mHandler.obtainMessage();
						// msg.what = MSG_UPDATE_LIGHT;
						// mHandler.sendMessage(msg);
						mHandler.sendEmptyMessage(MSG_UPDATE_LIGHT);
						break;

					// e_ware_curtain
					case 4:
						Log.e("", "-------rev curt into by type "
								+ devInfoData.length);
						List<WareCurtain> curtains = CommonUtils
								.extractCurtains(devInfoData);
						mCurtainDataset = curtains;
						mHandler.sendEmptyMessage(MSG_UPDATE_CURTAIN);
						break;

					// e_ware_projector
					case 7:
						break;
					// default:
					// break;
					}
				} else if (subCmd == 31) {// SPN_queryAirDev
					// query devices by waretype and id

					Log.e("",
							">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>QUERY_DEV_INFO By WARE_TYPE and ID>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
					int datalen = CommonUtils.byteToInt(new byte[] {
							localData[36], localData[37] });
					System.out.println("data length: " + datalen);
					byte[] devInfoData = new byte[datalen];
					for (int i = 0; i < devInfoData.length; i++) {
						devInfoData[i] = localData[i + 38];
					}
					switch (param) {
					// e_ware_airCond
					case 0:
						System.out.println("按类型查询空调");
						break;

					// e_ware_tv
					case 1:
						break;

					// e_ware_tvUP
					case 2:
						break;

					// e_ware_light
					case 3:
						System.out.println("按类型查询灯光");
						// extractLights(devInfoData);
						break;

					// e_ware_curtain
					case 4:
						break;

					// e_ware_projector
					case 7:
						break;

					// e_ware_musicSender
					case 8:
						break;

					// e_ware_musicPlayer
					case 9:
						break;

					// e_ware_other
					case 10:
						break;

					// e_ware_sm
					case 11:
						break;
					}

				}

				break;
			// lights relatived
			case 31:// GC_ExeLgtCmds
				Log.e("",
						">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>LIGHT CONTROL>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				int dataLen = CommonUtils.byteToInt(new byte[] { localData[36],
						localData[37] });
				byte[] devInfoData = new byte[dataLen];
				for (int i = 0; i < devInfoData.length; i++) 
				{
					devInfoData[i] = localData[i + 38];
				}

				Log.e("", ">>>>>>>>>>>>>> data leng: " + dataLen);

				List<WareLight> mLights = CommonUtils
						.extractLights(devInfoData);
				for (WareLight light : mLights) {
					for (WareLight ll : mLightDataset)
					{
						if (ll.dev.id == light.dev.id)
						{
							ll.onOff = light.onOff;
						}
					}
				}
				Message message = mHandler.obtainMessage();
				message.what = MSG_UPDATE_LIGHT;
				mHandler.sendMessage(message);

				break;
			// curtains relatived
			case 32:// GC_ExeCurtCmds大问题
				int dataLen1 = CommonUtils.byteToInt(new byte[] {
						localData[36], localData[37] });
				byte[] devInfoData1 = new byte[dataLen1];
				for (int i = 0; i < devInfoData1.length; i++) {
					devInfoData1[i] = localData[i + 38];
				}

				Log.e("", ">>>>>>>>>>>>>> data leng: " + dataLen1);

				List<WareCurtain> mCurtains = CommonUtils
						.extractCurtains(devInfoData1);
				for (WareCurtain cu : mCurtains) {
					Log.e("", ">>>>>>>>>>>> curtain onoff: " + cu.onOff);
					for (WareCurtain cc : mCurtainDataset) {
						if (cc.dev.id == cu.dev.id) {
							cc.onOff = cu.onOff;
						}
					}
				}
				Message messages = mHandler.obtainMessage();
				messages.what = MSG_UPDATE_CURTAIN;
				mHandler.sendMessage(messages);
				break;

			// airconds relatived
			case 28:// 大问题GC_ExeAirCmds
				if (subCmd == 0) {
					Log.e("",
							">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>AIRCOND CONTROL>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
					int dataLen11 = CommonUtils.byteToInt(new byte[] {
							localData[36], localData[37] });

					byte[] devInfoData11 = new byte[dataLen11];
					for (int i = 0; i < devInfoData11.length; i++) {
						devInfoData11[i] = localData[i + 38];
					}

					Log.e("", ">>>>>>>>>>>>>> data leng: " + dataLen11);

					List<airCondDev> mAirdevs = CommonUtils
							.extractAirconds(devInfoData11);
					for (airCondDev tmp : mAirdevs) {
						for (airCondDev tmp1 : mAircondDataset) {
							if (tmp.dev.id == tmp1.dev.id) {
								tmp1.modeSel = tmp.modeSel;
								tmp1.pwrOn = tmp.pwrOn;
								tmp1.spdSel = tmp.spdSel;
								tmp1.tempSel = tmp.tempSel;
							}
						}
					}
					Message message1 = mHandler.obtainMessage();
					message1.what = MSG_UPDATE_AIRCOND;
					mHandler.sendMessage(message1);
				}
				break;

			default:
				break;

			}
		}
	}

	// 自定义的适配器
	private class MyLightAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mLightDataset != null) {
				return mLightDataset.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		// 自定义的内部类避免多次的findviewbyID（这是很耗内存的）
		private class ViewHolder {
			WareDev dev;
			int onoff;
			int lmvalue;

			ImageView lightIv;
			TextView lightName;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(Activity2.this).inflate(
						R.layout.alarm_grid_item, null);
				viewHolder = new ViewHolder();
				viewHolder.lightIv = (ImageView) convertView
						.findViewById(R.id.alarm_grid_item_iv);
				viewHolder.lightName = (TextView) convertView
						.findViewById(R.id.alarm_grid_item_tv);

				convertView.setTag(viewHolder);// 设置一个标签方便直接调用

			} else {
				viewHolder = (ViewHolder) convertView.getTag();// 设置得到标签，可以直接用了
			}
			final WareLight currentLight = mLightDataset.get(position);// 在列表中得到一个位置，可以进行操作
			viewHolder.dev = currentLight.dev;
			viewHolder.onoff = currentLight.onOff;
			viewHolder.lmvalue = currentLight.lmValue;

			if (currentLight.onOff == 1)// 可能是自定义的1（默认的1代表通，0代表闭）
			{
				viewHolder.lightIv.setImageResource(R.drawable.light_on);
			} else {
				viewHolder.lightIv.setImageResource(R.drawable.light_off);
			}
			viewHolder.lightName.setText(currentLight.dev.name + "");

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								byte[] data;
								if (currentLight.onOff == 0) {
									data = CommonUtils.openLight(
											CommonUtils.getPktSn(),
											curretNode.ipAddr,
											CommonUtils.getLocalIp(),
											viewHolder.dev.id, curretNode.uID);
								} else {
									data = CommonUtils.closeLight(
											CommonUtils.getPktSn(),
											curretNode.ipAddr,
											CommonUtils.getLocalIp(),
											viewHolder.dev.id, curretNode.uID);
								}
								DatagramPacket packet = new DatagramPacket(
										data, data.length, InetAddress
												.getByName(curretNode.ipAddr),
										8300);
								// CommonUtils.castUdpPacket(castClass, packet);
								DatagramSocket socket = new DatagramSocket();
								socket.send(packet);
								socket = null;
							} catch (UnknownHostException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			});

			return convertView;
		}

	}

	private class MyCurtainAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mCurtainDataset != null) {
				return mCurtainDataset.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private class ViewHolder {
			ImageView curtainIv;
			TextView curtainTv;

			LinearLayout buttonLayout;

			Button openBtn;
			Button stopBtn;
			Button closeBtn;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(Activity2.this).inflate(
						R.layout.curtain_item, null);

				viewHolder = new ViewHolder();
				viewHolder.curtainIv = (ImageView) convertView
						.findViewById(R.id.alarm_grid_item_iv);
				viewHolder.curtainTv = (TextView) convertView
						.findViewById(R.id.alarm_grid_item_tv);
				viewHolder.openBtn = (Button) convertView
						.findViewById(R.id.curtainOpenBtn);
				viewHolder.closeBtn = (Button) convertView
						.findViewById(R.id.curtainCloseBtn);
				viewHolder.stopBtn = (Button) convertView
						.findViewById(R.id.curtainStopBtn);
				viewHolder.buttonLayout = (LinearLayout) convertView
						.findViewById(R.id.buttonLayout);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final WareCurtain curtain = mCurtainDataset.get(position);

			viewHolder.buttonLayout.setVisibility(View.VISIBLE);

			if (curtain.onOff == 2) // 你怎么知道要赋予2的？问题
			{
				viewHolder.curtainIv.setImageResource(R.drawable.curtain_close);
			} else {
				viewHolder.curtainIv.setImageResource(R.drawable.curtain_open);
			}
			viewHolder.curtainTv.setText(curtain.dev.name + "");

			viewHolder.openBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								byte[] data = CommonUtils.openCurtain(
										CommonUtils.getPktSn(),
										curretNode.ipAddr,
										CommonUtils.getLocalIp(),
										curtain.dev.id, curretNode.uID);
								DatagramPacket packet = new DatagramPacket(
										data, data.length, InetAddress
												.getByName(curretNode.ipAddr),
										8300);
								DatagramSocket sockTx = new DatagramSocket();
								sockTx.send(packet);
							} catch (UnknownHostException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			});
			viewHolder.closeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								byte[] data = CommonUtils.closeCurtain(
										CommonUtils.getPktSn(),
										curretNode.ipAddr,
										CommonUtils.getLocalIp(),
										curtain.dev.id, curretNode.uID);
								DatagramPacket packet = new DatagramPacket(
										data, data.length, InetAddress
												.getByName(curretNode.ipAddr),
										8300);
								DatagramSocket sockTx = new DatagramSocket();
								sockTx.send(packet);
							} catch (UnknownHostException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			});

			viewHolder.stopBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								byte[] data = CommonUtils.stopCurtain(
										CommonUtils.getPktSn(),
										curretNode.ipAddr,
										CommonUtils.getLocalIp(),
										curtain.dev.id, curretNode.uID);
								DatagramPacket packet = new DatagramPacket(
										data, data.length, InetAddress
												.getByName(curretNode.ipAddr),
										8300);
								DatagramSocket sockTx = new DatagramSocket();
								sockTx.send(packet);
							} catch (UnknownHostException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			});

			return convertView;
		}

	}
}
