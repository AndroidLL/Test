package com.example.login.activity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.R;
import com.example.login.common.CommonUtils;
import com.example.login.database.DBHelper;
import com.example.login.entity.CardNum;
import com.example.login.entity.GlobalDataPacket;
import com.example.login.entity.Mode;
import com.example.login.entity.UnitNode;

public class showModelActivity extends AdAbstractActivity 
{
	private ListView listView;
	
	protected LinearLayout model;
	private ArrayList<Mode> modeData = new ArrayList<Mode>();
	private Intent  intent;
	List<UnitNode> mDataset;
	UnitNode curretNode;
	private MyModeAdapter mModeAdapter;
	private DBHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		
		model = (LinearLayout)layoutInflater.inflate(R.layout.activity_model, null);
		mRootView.addView(model,FF);
		
		initComponent();
	}


	private void initComponent() 
	{
	setTextForBackBtn(getString(R.string.app_back));
	setTextForTitle(getString(R.string.activity_title_show_model));
	listView= (ListView)findViewById(R.id.listview);

	intent = getIntent();
	modeData = (ArrayList<Mode>)intent.getSerializableExtra("modeData");
	Log.e("", " /////"+modeData.size());
	
	mModeAdapter = new MyModeAdapter();
	listView.setAdapter(mModeAdapter);
	
	dbHelper = new DBHelper(this);
	mDataset = dbHelper.queryAllUnitNodes();
	curretNode = mDataset.get(0);// null;
	Log.e("moyimoyimoyimoyi",""+mDataset.size());
	}

	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
		case R.id.title_btn4:
			finish();
			break;

		default:
			break;
		}
	}

	private class MyModeAdapter extends BaseAdapter 
	{
		
		@Override
		public int getCount() 
		{
			if(modeData != null)
			{
				//填充条的数目
				return modeData.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) 
		{
			return position;
		}

		@Override
		public long getItemId(int position) 
		{
			return 0;
		}

		private class ViewHolder
		{
			 /*String sceneName;
			 int indexScene;
			 int valid;*/
			 TextView modelName;
		}
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			final ViewHolder viewHolder;
			if(convertView == null)
			{
				convertView = LayoutInflater.from(showModelActivity.this).inflate(R.layout.model_item, null);

				viewHolder = new ViewHolder();
				viewHolder.modelName = (TextView) convertView.findViewById(R.id.model_item_tv);
			
				//viewHolder.indexScene = position;
				convertView.setTag(viewHolder);
				
			}
			else
			{
				viewHolder = (ViewHolder)convertView.getTag();
			}
			
			Mode currentMode = modeData.get(position);
			
			viewHolder.modelName.setText(currentMode.sceneName);
			Log.e("", ""+currentMode.sceneName);
			
			listView.setOnItemClickListener(new OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id)
				{
				
					final int m = listView.getPositionForView(view);
				
					Log.e("", "dddddd"+m);
					
						new Thread(new Runnable()
						{
							@Override
							public void run() 
							{
								byte[] data = null;
								try 
								{
									
									data = CommonUtils.sendMode(CommonUtils.getPktSn(),m,CommonUtils.getLocalIp(),curretNode.ipAddr);
									
									Log.e("", "param = "+m+curretNode.ipAddr);
								
								} catch (UnknownHostException e) 
								{
									e.printStackTrace();
								}
								try {
									DatagramPacket datagramPacket = new DatagramPacket(data, data.length,InetAddress.getByName(curretNode.ipAddr), 8300);
									DatagramSocket datagramSocket = new DatagramSocket();
									datagramSocket.send(datagramPacket);
									Log.e("aaaaaaa", "MODEMODEMODE");
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
					String string = modeData.get(position).sceneName.toString();
				Toast.makeText(showModelActivity.this, "已经变更为"+string, Toast.LENGTH_SHORT).show();
				}
					
				
			});
		
			return convertView;
		}

	
	}
}