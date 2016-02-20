package com.example.login.activity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.R;
import com.example.login.common.CommonUtils;
import com.example.login.database.DBHelper;
import com.example.login.entity.Name;
import com.example.login.entity.UnitNode;

public class MainActivity extends Activity implements OnClickListener 
{
	private EditText mUserName;
	private EditText mPassword;
	private Button mLoginButton;
	private ImageButton mDropDown;
	private DBHelper dbHelper;
	private CheckBox mCheckBox;
	private PopupWindow popView;
	private MyAdapter dropDownAdapter;
	ExecutorService executorService;
	private MulticastSocket castClass;
	
	Context mContext;
	private int udpReSendCnt = 0;
	private int checkFlag;
	private int isFlag;
	private String userName = "";
	private String password = "";
	
	private boolean isRuning = false;

	private static final int MSG_UDP_TX_TIMOUT = 1000;
	private static final int MSG_UPDAT_LIST = 1001;
	private static final int MSG_REMINDER = 1002;
	private static final int MSG_TIME = 1004;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg)
		{
			
			switch (msg.what) 
			{
			
			case MSG_UDP_TX_TIMOUT:
				if (udpReSendCnt >= 6) 
				{
					Toast.makeText(mContext, "连接失败，请检查", Toast.LENGTH_SHORT)
							.show();
					udpReSendCnt = 0;
					setStopTime();
				} else 
				{
					obtainConnect();
					// 重发
					udpReSendCnt++;
				}

				break;

			case MSG_UPDAT_LIST:
				
				Destory();
			
				break;
			case MSG_REMINDER:// MSG_REMINDER
				Toast.makeText(MainActivity.this, "用户名或密码不正确",
						Toast.LENGTH_LONG).show();
				mUserName.setText("");
				mPassword.setText("");
				break;

			case MSG_TIME:
				setStopTime();
				break;

			}
		}

	};
	
	
	
	private void setStopTime()
	{
		isRuning=true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		SysApplication.getInstance().addActivity(this);
		
		setContentView(R.layout.activity_main);
		mContext = this;
		initWidget();
	}

	private void initWidget() 
	{
		
		dbHelper = new DBHelper(this);
		mUserName = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);
		mLoginButton = (Button) findViewById(R.id.login);
		mDropDown = (ImageButton) findViewById(R.id.dropdown_button);
		mCheckBox = (CheckBox) findViewById(R.id.remember);
		mLoginButton.setOnClickListener(this);
		mDropDown.setOnClickListener(this);
		initLoginUserName();

	}

	private void initLoginUserName() 
	{
		String[] usernames = dbHelper.queryAllUserName();
		Log.e("初始化", "有"+usernames.length);
		if (usernames.length > 1) 
		{
			
			isFlag = 1;
			String tempName = dbHelper.queryNameByFlag(isFlag);
			Log.e("","名字"+ tempName);
			
			mUserName.setText(tempName);
			mUserName.setSelection(tempName.length());
			String tempPwd = dbHelper.queryPasswordByName(tempName);
			checkFlag = dbHelper.queryIsSavedByName(tempName);

			if (checkFlag == 0) 
			{
				
				mCheckBox.setChecked(false);
				
			} else if (checkFlag == 1) 
			{
				
				mCheckBox.setChecked(true);
			
			}
			mPassword.setText(tempPwd);

		}else if(usernames.length == 1)
		{
			String tempName = usernames[0];
			mUserName.setText(tempName);
			mUserName.setSelection(tempName.length());
			String tempPwd = dbHelper.queryPasswordByName(tempName);
			checkFlag = dbHelper.queryIsSavedByName(tempName);

			if (checkFlag == 0) 
			{
				
				mCheckBox.setChecked(false);
				
			} else if (checkFlag == 1) 
			{
				
				mCheckBox.setChecked(true);
			
			}
			mPassword.setText(tempPwd);

		}else
		{
			mUserName.setText("");
			mPassword.setText("");
		}
		
		
		
		mUserName.addTextChangedListener(new TextWatcher() 
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) 
			{
				
				mPassword.setText("");
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) 
			{

			}

			@Override
			public void afterTextChanged(Editable s) 
			{
				
			}
		});
	}

	@Override
	public void onClick(View v) 
	{

		switch (v.getId())
		{
		case R.id.login:
			
			  userName = mUserName.getText().toString();
			  password = mPassword.getText().toString();
			
			if(userName.length()<10)
			{
				
				Toast.makeText(mContext, "请核对房间信息", Toast.LENGTH_SHORT).show();
			
			}else if(password.isEmpty())
			{
				
				Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
		
			}else if(userName.length()==10&&!password.isEmpty())
			{
			
			final Timer timer1 = new Timer();
			TimerTask timerTask1 = new TimerTask() 
			{
				@Override
				public void run() 
				{
					
					if (isRuning == false) 
					{
						Message message = Message.obtain();
						message.what = MSG_UDP_TX_TIMOUT;
						mHandler.sendMessage(message);
					
					}
					if (isRuning == true) 
					{
						timer1.cancel();
						isRuning = false;
					}
				}
			};
			timer1.schedule(timerTask1, 100, 500);

			//请求服务时用到
			Name.setName(userName);
			Log.e("wwwwwwwwwwwww", ">>>>>>>"+userName + "mima" +password);
			
			if (mCheckBox.isChecked() )
			{
				
				dbHelper.insertOrUpdate(userName, password, 1, 1);
				
			} else if(!mCheckBox.isChecked())
			{
				
				dbHelper.insertOrUpdate(userName, "", 0, 1);
				
			}else
			{
				mPassword.setText("");
				Toast.makeText(mContext, "正在连接...", Toast.LENGTH_SHORT).show();
			}
			}
			
			dbHelper.updateFlag();
			dbHelper.updateCurrentFlag(userName);
			int w  = dbHelper.queryIsFlag(userName);
			Log.e("OOOOOOO", "sfsfdfsfsd"+w);
				break;
			
		case R.id.dropdown_button:
			
			if (popView != null) 
			{
				if (!popView.isShowing()) 
				{
				
					popView.showAsDropDown(mUserName);
				
				} else 
				{
					
					popView.dismiss();
				
				}
			} else 
			{
				if (dbHelper.queryAllUserName().length > 0) 
				{

					initPopView(dbHelper.queryAllUserName());
				
					if (!popView.isShowing()) 
					{
					
						popView.showAsDropDown(mUserName);
					
					} else 
					{
					
						popView.dismiss();
					
					}
				} else 
				{
				
					Toast.makeText(this, "无记录", Toast.LENGTH_LONG).show();
			
				}

			}
			break;
		}
	}

	private void obtainConnect() 
	{
		
		Log.e("wwwwwwwwwwwww", "这次是"+userName +"mm"+password);
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{

				byte[] data = null;
				
				try 
				{
					data = CommonUtils.querAllUnitNodesname(
							new Random()
									.nextInt(com.example.login.common.Constants.sn++),
							userName, password, CommonUtils
									.getLocalIp(),
							"255.255.255.255");
					Log.e("wwwwwwwwwwwww", "打包"+password);
				} catch (UnknownHostException e1) 
				{
					e1.printStackTrace();
				}
				
				try {
					DatagramPacket packet = new DatagramPacket(
							data, data.length, InetAddress
									.getByName("255.255.255.255"),
							8300);
					DatagramSocket socket = new DatagramSocket();
					socket.setBroadcast(true);
					socket.send(packet);
				
					Log.e("<<<<<<<<<<<<<<<<", "duoshaociya ");
				} catch (UnknownHostException e) 
				{
					e.printStackTrace();
				} catch (SocketException e)
				{
					e.printStackTrace();
				} catch (IOException e) 
				{

					e.printStackTrace();
				}
			}
		}

		).start();
	}

	protected void Destory() 
	
	{
		
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(intent);
		this.finish();
		
		
	}
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	
		executorService = Executors.newSingleThreadExecutor();
		executorService.execute(new CastServer());
		
		/**
		 * 将ip改变为字符串例如："162.167.5.125"
		 */
		WifiManager wifi = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifi.getConnectionInfo();
		
		int ip = wifiInfo.getIpAddress();
		
		String ipStr = (ip & 0xff) + "." + ((ip >> 8) & 0xff) + "."
				+ ((ip >> 16) & 0xff) + "." + ((ip >> 24) & 0xff);

		CommonUtils.CommonUtils_init(ipStr);
		
		Toast.makeText(this, "ip: " + CommonUtils.getLocalIp(),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		executorService.shutdownNow();
	}

	private class CastServer implements Runnable 
	{

		@Override
		public void run() 
		{
			// Looper.prepare();
			byte[] buffer = new byte[2048];
			//
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			int i = 0;
			try {
				castClass = new MulticastSocket(8300);
				castClass.setBroadcast(true);
				
				while (true) 
				{
					castClass.receive(packet);
					i = packet.getLength();
					Log.e("err>>>>>>>>>>>>>>>", "rev " + i);
				
					if (i > 0) 
					{
						extractDate(packet);
						i = 0;
					}
				}
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
			// Looper.loop();
		}
	}

	public void extractDate(DatagramPacket packet)
	{
		byte[] data = packet.getData();
		byte[] localData = new byte[packet.getLength() - 7];
	
		for (int i = 0; i < localData.length; i++) 
		{
			
			localData[i] = data[i + 7];
		
		}
		
		int isAck = localData[1] & 0xff;
		
		int pktSn = CommonUtils.byteToInt(new byte[] { localData[2],
				localData[3] });
		
		String mDstAddr = (localData[4] & 0xff) + "." + (localData[5] & 0xff)
				+ "." + (localData[6] & 0xff) + "." + (localData[7] & 0xff);

		String mSrcAddr = (localData[8] & 0xff) + "." + (localData[9] & 0xff)
				+ "." + (localData[10] & 0xff) + "." + (localData[11] & 0xff);

		if (isAck == 0 && CommonUtils.getLocalIp().equals(mDstAddr)) 
		{
			try 
			{
				byte[] replyData = CommonUtils.reply(packet);
				DatagramPacket replyPacket = new DatagramPacket(replyData,
						replyData.length, InetAddress.getByName(mSrcAddr), 8300);
				CommonUtils.castUdpPacket(castClass, replyPacket);
			
			} catch (IOException e) 
			{
				e.printStackTrace();
			}

			int globalCmd = CommonUtils.byteToInt(new byte[] { localData[12] });

			int subCmd = CommonUtils.byteToInt(new byte[] { localData[16],
					localData[17], localData[18], localData[19] });

			int param = CommonUtils.byteToInt(new byte[] { localData[20],
					localData[21], localData[22], localData[23] });

			Log.e("", "--------> global cmd: " + globalCmd + ", subCmd: "
					+ subCmd + ", param: " + param);

			// GC_Hello
			switch (globalCmd) 
			{
			// GlobalDataPacket.GLOBAL_CMD.GC_Hello.getValue():
			case 26:
				
				// 取消计时
				Message messages = mHandler.obtainMessage();
				messages.what = MSG_TIME;
				mHandler.sendMessage(messages);
				
				// SPN_Port
				if (subCmd == 11) 
				{
					Message message = mHandler.obtainMessage();
					message.what = MSG_REMINDER;
					mHandler.sendMessage(message);
					return;

					// SPN_config
				} else if (subCmd == 10)
				{
					if (param == 0)
					{
						Log.e("", "--------ok: ");

						// 节点
						int datalen = CommonUtils.byteToInt(new byte[] {
								localData[36], localData[37] });

						byte[] nodeData = new byte[datalen];

						for (int i = 0; i < nodeData.length; i++)
						{
							
							nodeData[i] = localData[i + 38];
						
						}

						UnitNode node = CommonUtils.extractUnitNode(nodeData);
						
						
						long id = dbHelper.insertUnitNode(node);
						
						Message message = mHandler.obtainMessage();
						message.what = MSG_UPDAT_LIST;
						mHandler.sendMessage(message);
						
					return;
					}
				}
				break;
			}

		}
	}

	private void initPopView(String[] usernames)
	{
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < usernames.length; i++) 
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", usernames[i]);
			Log.e("", "这是"+usernames[i]+"个");
			map.put("drawable", R.drawable.xicon);
			list.add(map);
		}
		
		dropDownAdapter = new MyAdapter(this, list, R.layout.dropdown_item,
				new String[] { "name", "drawable" }, new int[] { R.id.textview,
						R.id.delete });
		
		ListView listView = new ListView(this);
		listView.setAdapter(dropDownAdapter);

		popView = new PopupWindow(listView, mUserName.getWidth(),
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		popView.setFocusable(true);
		popView.setOutsideTouchable(true);
		popView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.white));
		// popView.showAsDropDown(mUserName);
	}

	class MyAdapter extends SimpleAdapter 
	{

		private List<HashMap<String, Object>> data;

		public MyAdapter(Context context, List<HashMap<String, Object>> data,
				int resource, String[] from, int[] to) 
		{
			
			super(context, data, resource, from, to);
			
			this.data = data;
		}

		@Override
		public int getCount() 
		{
			
			return data.size();
		}

		@Override
		public Object getItem(int position)
		{
			
			return position;
		}

		@Override
		public long getItemId(int position) 
		{
			
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) 
		{
			System.out.println(position);
			ViewHolder holder;
			
			if (convertView == null)
			{
				holder = new ViewHolder();

				convertView = LayoutInflater.from(MainActivity.this).inflate(
						R.layout.dropdown_item, null);
				holder.btn = (ImageButton) convertView
						.findViewById(R.id.delete);
				holder.tv = (TextView) convertView.findViewById(R.id.textview);

				convertView.setTag(holder);
			} else 
			{
				
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.tv.setText(data.get(position).get("name").toString());

			holder.tv.setOnClickListener(new View.OnClickListener() 
			{

				@Override
				public void onClick(View v) 
				{
					
				
					String[] usernames = dbHelper.queryAllUserName();
					Log.e("<<><><><><>", "name" + usernames.length);

					mUserName.setText(usernames[position]);
					Log.e("<<><><><><>", "name" + usernames[position]);
					
					mPassword.setText(dbHelper
							.queryPasswordByName(usernames[position]));
					checkFlag = dbHelper.queryIsSavedByName(usernames[position]);

					if (checkFlag == 0) 
					{
						
						mCheckBox.setChecked(false);
						
					} else if (checkFlag == 1) 
					{
						
						mCheckBox.setChecked(true);
					
					}
					
					
					popView.dismiss();
					
				}
			});

			holder.btn.setOnClickListener(new View.OnClickListener() 
			{

				@Override
				public void onClick(View v) 
				{
					String[] usernames = dbHelper.queryAllUserName();

					if (usernames.length > 0) 
					{
						
						dbHelper.delete(usernames[position]);
						Toast.makeText(mContext, "删除了"+usernames[position],Toast.LENGTH_SHORT ).show();
						popView.dismiss();
					}
					
					String[] newusernames = dbHelper.queryAllUserName();

					if (newusernames.length > 0) 
					{
						
						initPopView(newusernames);
						popView.showAsDropDown(mUserName);
						Log.e("", "更新了没");
						
					} else 
					{
						
						popView.dismiss();
						popView = null;
						
					}
				}
			});
			return convertView;
		}
	}

	class ViewHolder
	{
		
		private TextView tv;
		private ImageButton btn;
		
	}

	@Override
	protected void onStop() 
	{
		
		super.onStop();
		dbHelper.cleanup();
		
	}

}
