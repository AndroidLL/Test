package com.example.login.activity;

import com.example.login.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class AdAbstractActivity extends Activity implements
		android.view.View.OnClickListener
		{

	protected LayoutParams FF = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.FILL_PARENT,
			LinearLayout.LayoutParams.FILL_PARENT);
	protected LayoutParams FW = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.FILL_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	
	protected LayoutInflater layoutInflater;
	protected LinearLayout mmTitle;
	protected LinearLayout mRootView;
	protected Button titleBackBtn;
	protected Button titleRightBtn;
	protected TextView titleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_mm_activity);
		
		SysApplication.getInstance().addActivity(this);
		
		layoutInflater = LayoutInflater.from(this);
		mRootView = (LinearLayout) findViewById(R.id.mm_root_view);
		mmTitle = (LinearLayout) layoutInflater.inflate(R.layout.ad_mm_title,
				null);
		mRootView.addView(mmTitle, FW);
		init();

	}

	private void init() 
	{
		
		titleBackBtn = (Button) findViewById(R.id.title_btn4);
		titleRightBtn = (Button) findViewById(R.id.title_btn1);
		titleTextView = (TextView) findViewById(R.id.title);
		titleBackBtn.setOnClickListener(this);
		titleRightBtn.setOnClickListener(this);
		
	}

	protected void setTextForBackBtn(String text) 
	{
		
		titleBackBtn.setText(text);
		titleBackBtn.setVisibility(View.VISIBLE);
		
	}

	protected void setTextForRightBtn(String text) 
	{
		
		titleRightBtn.setText(text);
		titleRightBtn.setVisibility(View.VISIBLE);
		
	}

	protected void setTextForTitle(String title) 
	{
		
		titleTextView.setText(title);
		
	}
}
