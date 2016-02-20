package com.example.login.activity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.R;
import com.example.login.common.CommonUtils;
import com.example.login.database.DBHelper;
import com.example.login.entity.Name;
import com.example.login.entity.Service;
import com.example.login.entity.UnitNode;

public class showDemandService extends AdAbstractActivity 
{

	private ListView listView;
	private LinearLayout layout;
	private Intent intent;

	List<UnitNode> mDataset;

	private MyServAdapter mServAdapter;
	private ArrayList<Service> servicesData = new ArrayList<Service>();

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);

		layout = (LinearLayout) layoutInflater.inflate(
				R.layout.activity_service, null);
		mRootView.addView(layout, FF);

		initComponent();
	}

	private void initComponent() 
	{
		setTextForBackBtn("返回");
		setTextForTitle("请求服务");
		listView = (ListView) findViewById(R.id.listview);

		intent = getIntent();
		servicesData = (ArrayList<Service>) intent
				.getSerializableExtra("service");
		Log.e("",
				"ppppppp" + servicesData.get(0).serv_name
						+ servicesData.get(1).serv_name);
		mServAdapter = new MyServAdapter();
		listView.setAdapter(mServAdapter);

		

	}

	@Override
	protected void onResume() 
	{
		
		super.onResume();
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

	private class MyServAdapter extends BaseAdapter 
	{

		@Override
		public int getCount() 
		{
			if (servicesData != null) 
			{
				return servicesData.size();
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
			int serv_id;
			TextView name;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final ViewHolder viewHolder;

			if (convertView == null)
			{
				convertView = LayoutInflater.from(showDemandService.this)
						.inflate(R.layout.service_item, null);

				viewHolder = new ViewHolder();
				viewHolder.name = (TextView) convertView
						.findViewById(R.id.serv_item_tv);

				convertView.setTag(viewHolder);

			} else 
			{
				viewHolder = (ViewHolder) convertView.getTag();
			}

			Service currentService = servicesData.get(position);

			viewHolder.name.setText(currentService.serv_name);
			Log.e("", "" + currentService.serv_name);

			listView.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id)
				{

					final int m = servicesData.get(position).serv_id;
					Log.e("", "dddddd" + m);
					
					//查询name
					String usernames = Name.getName();
					Log.e("", "name是：" + usernames);
					 final String b = usernames.substring(0, 4);
					Log.e("", "b是：" + b);
					final String u = usernames.substring(4, 6);
					Log.e("", "u是：" + u);
					final String l = usernames.substring(6, 8);
					Log.e("", "u是：" + l);
					final String r = usernames.substring(8, 10);
					Log.e("", "u是：" + r);
					
					new Thread(new Runnable() 
					{
						@Override
						public void run()
						{
							byte[] data = null;
							
							try 
							{
								data = CommonUtils.sendService(CommonUtils.getPktSn(), m, CommonUtils.getLocalIp(), CommonUtils.getServIp(), b, u, l, r);
								
								/*data = CommonUtils.sendService(m, CommonUtils.getPktSn(), u, l, r,
										
										CommonUtils.getLocalIp(), curretNode.ipAddr,
										b);*/
								Log.e("", ""+m+u+l+r+b+ CommonUtils.getServIp());
							} catch (UnknownHostException e1) 
							{
								e1.printStackTrace();
							}
							Log.e("", "param = " + m);
							try {
							
								DatagramPacket datagramPacket = new DatagramPacket(data,
										data.length,
										InetAddress.getByName(CommonUtils.getServIp()), 8300);
								DatagramSocket datagramSocket = new DatagramSocket();
								datagramSocket.setBroadcast(true);
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
					
					
					
					String string = servicesData.get(position).serv_name
							.toString();
					Toast.makeText(showDemandService.this, "已经变更为" + string,
							Toast.LENGTH_SHORT).show();
				}

			});

			return convertView;
		}

	}


}
