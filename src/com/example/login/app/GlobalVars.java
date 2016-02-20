package com.example.login.app;

import android.content.Context;

public class GlobalVars {
	private static Context context;

	public static void init(Context c) {
		context = c;
	}

	public static Context getContext() {
		return context;
	}
}
