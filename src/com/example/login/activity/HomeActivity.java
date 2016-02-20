package com.example.login.activity;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.login.R;
import com.example.login.common.CommonUtils;
import com.example.login.database.DBHelper;
import com.example.login.entity.CardNum;
import com.example.login.entity.Flag;
import com.example.login.entity.Mode;
import com.example.login.entity.Service;
import com.example.login.entity.UnitNode;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HomeActivity extends AdAbstractActivity 
{
	ExecutorService executorService;
	private MulticastSocket castClass;
	private LinearLayout homeLayout;
	private LinearLayout panel;
	private LinearLayout panel2;
	private LinearLayout panel3;
	
	public DBHelper dbHelper;
	
	
	public  String serviceIp = null ;
	public List<UnitNode> mDataset;
	private ArrayList<Mode> mModeData = new ArrayList<Mode>();
	private ArrayList<Service> mServ_Data = new ArrayList<Service>();
	private long waitTime = 0;
	
	public boolean isRuning = false;
	public static long lastClickTime;
	
	private static final int MSG_UPDAT_CARDPOW = 1000;
	private static final int MSG_NO_RESPONSE = 1001;
	private static final int MSG_SHOW_SERVICE = 1004;
	private static final int MSG_SHOWE_MODE = 1003;	
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{
			case MSG_UPDAT_CARDPOW:
				
				switch (Flag.getFlag()) 
				{
				case 1:
					showActivity2();
					break;
				case 2:
					sendServicePacket();
					break;
				case 3:
					sendModelPacket();
					break;

				default:
					break;
				}
				
				break;
				
			case MSG_NO_RESPONSE:
				
				notice();
				
				break;
				
			case MSG_SHOW_SERVICE:
				
				long time = System.currentTimeMillis();  
				long timeD = time - lastClickTime; 
				
				if(timeD>=1000)
				{
				showDemandService();
				}
				
				lastClickTime = System.currentTimeMillis();
				break;
				
			case MSG_SHOWE_MODE:
			
					long mode_time = System.currentTimeMillis();  
					long mode_timeD = mode_time - lastClickTime;
					
					if(mode_timeD>=1000)
					{
						showModelActivity();
					}
					
					lastClickTime = System.currentTimeMillis();
					
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
		
		homeLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.activity_home, null);
		mRootView.addView(homeLayout);
		
		initComponent();
		
	}


	@Override
	protected void onResume() 
	{
		executorService = Executors.newSingleThreadExecutor();
		executorService.execute(new CastServer());
		super.onResume();
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		executorService.shutdownNow();
	}

	private void initComponent() {
		setTextForTitle("智能手机终端");
		panel = (LinearLayout) findViewById(R.id.panel);
		panel2 = (LinearLayout) findViewById(R.id.panel2);
		panel3 = (LinearLayout) findViewById(R.id.panel3);

		panel.setOnClickListener(this);
		panel2.setOnClickListener(this);
		panel3.setOnClickListener(this);
	
	}

	private class CastServer implements Runnable 
	{

		@Override
		public void run() 
		{
			byte[] buffer = new byte[2048];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			int i = 0;
			try 
			{
				
				castClass = new MulticastSocket(8300);
				castClass.setBroadcast(true);

				while (true) 
				{
					castClass.receive(packet);
					i = packet.getLength();
					if (i > 0) 
					{
						Log.e("err>>>>>>>>>>>>>>>", "收到包了");
						extractDate(packet);
						i = 0;
						
					}
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
		private void extractDate(DatagramPacket packet) 
		{
			byte[] data = packet.getData();
			byte[] localData = new byte[packet.getLength() - 7];

			for (int i = 0; i < localData.length; i++)
			{
				localData[i] = data[i + 7];
			}
			int isAck = localData[1] & 0xff;
			int pktSn = CommonUtils.byteToInt(new byte[] 
					{ 
					localData[2],
					localData[3]
					}
			);

			String mDstAddr = (localData[4] & 0xff) + "."
					+ (localData[5] & 0xff) + "." + (localData[6] & 0xff) + "."
					+ (localData[7] & 0xff);

			
			String mSrcAddr = (localData[8] & 0xff) + "."
					+ (localData[9] & 0xff) + "." + (localData[10] & 0xff)
					+ "." + (localData[11] & 0xff);

			if (isAck == 0 && CommonUtils.getLocalIp().equals(mDstAddr)) 
			{
				try 
				{
					byte[] replyData = CommonUtils.reply(packet);
					DatagramPacket replyPacket = new DatagramPacket(replyData,
							replyData.length, InetAddress.getByName(mSrcAddr),
							8300);

					CommonUtils.castUdpPacket(castClass, replyPacket);
					Log.e("<<<<<<<<<<<", "发Ack包");

				} catch (IOException e) {
					e.printStackTrace();
				}
				
				int globalCmd = CommonUtils.byteToInt(new byte[] { localData[12] });

				int subCmd = CommonUtils.byteToInt(new byte[] { localData[16],
						localData[17], localData[18], localData[19] });

				int param = CommonUtils.byteToInt(new byte[] { localData[20],
						localData[21], localData[22], localData[23] });

				switch (globalCmd) 
				{
				case 26:
					
					if (subCmd == 10 && param == 0) 
					{
						int datalen = CommonUtils.byteToInt(new byte[] 
								{
								localData[36], localData[37] });

						byte[] cardNumData = new byte[datalen];

						for (int i = 0; i < cardNumData.length; i++) 
						{
							cardNumData[i] = localData[i + 38];
						}
						CardNum cardnum = CommonUtils.extractCardNum(cardNumData);
						
						serviceIp = cardnum.centerServ;
						
						CommonUtils.CommonUtils_sevInit(serviceIp);
						Log.e("", "ppppp"+serviceIp);
						
						if(cardnum.cardPow == 1)
							{
							
							Message message = mHandler.obtainMessage();
							message.what = MSG_UPDAT_CARDPOW;
							mHandler.sendMessage(message);
							Log.e("有卡", "ppppp");
							
							}
						
						else if(cardnum.cardPow == 0)
							{
								
								Message message=mHandler.obtainMessage();
								message.what=MSG_NO_RESPONSE;
								mHandler.sendMessage(message);
								Log.e("无卡", "pppp");
								
							}
					}
					break;
					
				case 45:
					
					if(subCmd == 32 && param == 1)
					{
						int datalen = CommonUtils.byteToInt(new byte[]{localData[36],localData[37]});
						byte[] devInfoData = new byte [datalen];
						
						for(int i = 0;i<devInfoData.length;i++)
						{
							devInfoData[i] = localData[38+i];
						}
						
						mModeData= CommonUtils.extractModel(devInfoData);
						Log.e(",,,,,,,,,", "-------rev mode "+mModeData.size());
						
						Message message = Message.obtain();
						message.what = MSG_SHOWE_MODE;
						mHandler.sendMessage(message);
					
					}	
					
					break;
					
				case 24:
					
					if(subCmd == 0 && param == 0)
					{
						int datalen = CommonUtils.byteToInt(new byte[]{localData[36],localData[37]});
						byte[] devInfoData = new byte [datalen];
						
						for(int i = 0; i< devInfoData.length; i++)
						{
							devInfoData[i] = localData[38+i];
						}
						mServ_Data = CommonUtils.extractService(devInfoData);
						
						Log.e("", "hhhhh"+mServ_Data.size()+"fff"+CommonUtils.extractService(devInfoData));
						
						Message message = Message.obtain();
						message.what = MSG_SHOW_SERVICE;
						mHandler.sendMessage(message);
					}
					break;
				default:
					break;
				}
			}

		}

	

	@Override
	public void onClick(View v)
	{
		switch (v.getId()) 
		{
	
		case R.id.panel:
			
			queryCard();
			
			Flag.setFlag(1);
			
			
			break;
		case R.id.panel2:
			
			queryCard();
			Flag.setFlag(2);
		
			break;
		case R.id.panel3:
			
			queryCard();
			Flag.setFlag(3);
		
			
			break;
		default:
			break;
		}
	}


	private void notice() 
	{
		
		Toast.makeText(this, "请插卡", Toast.LENGTH_SHORT).show();
		
	}

	private void showDemandService()
	{
		
		Intent intent = new Intent();
		intent.setClass(HomeActivity.this, showDemandService.class);
		intent.putExtra("service", (Serializable)mServ_Data);
		startActivity(intent);
		
	}

	private void showModelActivity() 
	{
		
		Intent intent = new Intent();
		intent.setClass(HomeActivity.this, showModelActivity.class);
		intent.putExtra("modeData", (Serializable)mModeData);
		startActivity(intent);
		
	}

	private void showActivity2() 
	{
		
		Intent intent = new Intent();
		intent.setClass(HomeActivity.this, Activity2.class);
		startActivity(intent);
		
	}

	private void queryCard() 
	{
		dbHelper = new DBHelper(this);
		mDataset = dbHelper.queryAllUnitNodes();
		//TimerTask timerTask  = new TimerTask() 
		{
			
			new Thread(new Runnable() 
			{
				
				@Override
				public void run() 
				{
					
					byte[] data = null;
					
					try
					{
						data = CommonUtils.queryCardNumber(
								new Random()
								.nextInt(com.example.login.common.Constants.sn++),
								CommonUtils.getLocalIp(),
								mDataset.get(0).ipAddr);
					} catch (UnknownHostException e1) 
					{
						e1.printStackTrace();
					}
					
					try 
					{
						
						DatagramPacket packet = new DatagramPacket(
								data, data.length,
								InetAddress.getByName(mDataset
										.get(0).ipAddr), 8300);
						DatagramSocket socket = new DatagramSocket();
						socket.setBroadcast(true);
						socket.send(packet);
						
						Log.e("", "CARDNUMBER发了没");
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
			}).start();
			//@Override
		//	public void run() 
			{
				

				
				
				
			}
		};

		
		/*final Timer timer = new Timer();
		timer.schedule(timerTask, 100, 30000);	*/
	}
	
	private void sendServicePacket() 
	{
		
		new Thread(new Runnable() 
		{
			
			byte []data = null;
			@Override
			public void run() 
			{
				
				try 
				{
					
					data = CommonUtils.DemandService(CommonUtils.getPktSn(),CommonUtils.getLocalIp(),
							serviceIp);
					Log.e("OOOOOOO", ""+serviceIp+"  这个是  "+CommonUtils.getLocalIp());
				
				} catch (UnknownHostException e)
				{
					
					e.printStackTrace();
				}
				try 
				{
					
					DatagramPacket packet = new DatagramPacket(
							data, data.length, InetAddress
									.getByName(serviceIp),
							8300);
					Log.e("这个是", ""+InetAddress.getByName(serviceIp));
					DatagramSocket socket = new DatagramSocket();
					socket.setBroadcast(true);
					socket.send(packet);
					Log.e("", "发送服务");
					
				} catch (SocketException e) 
				{
					
					e.printStackTrace();
					
				} catch (UnknownHostException e) 
				{
					
					e.printStackTrace();
					
				} catch (IOException e) 
				{
					
					e.printStackTrace();
					
				}
			}
		}).start();
	}
	
	private void sendModelPacket() 
	{
		
		new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				byte[]data = null;
				
				try 
				{
					data = CommonUtils.queryModel(CommonUtils.getPktSn(),
					CommonUtils.getLocalIp(),
					"255.255.255.255");
				} catch (UnknownHostException e) 
				{
					e.printStackTrace();
				}
				
				try 
				{
					DatagramPacket packet = new DatagramPacket(
							data, data.length, InetAddress
									.getByName("255.255.255.255"),
							8300);
					
					DatagramSocket socket = new DatagramSocket();
					socket.setBroadcast(true);
					socket.send(packet);
					Log.e("ddd","d");
					
				} catch (SocketException e) 
				{
					
					e.printStackTrace();
					
				} catch (UnknownHostException e) 
				{
					
					e.printStackTrace();
					
				} catch (IOException e) 
				{
					
					e.printStackTrace();
					
				}
			}
		}).start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			
			if(System.currentTimeMillis()- waitTime > 2000)
			{
				
				Toast.makeText(getApplicationContext(),
						"再按一次退出"+getPackageManager().getApplicationLabel(getApplicationInfo()), 
						Toast.LENGTH_SHORT).show();
				
				waitTime = System.currentTimeMillis();
			}
			else
			{
				
				SysApplication.getInstance().exit();
				
			}
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

}
