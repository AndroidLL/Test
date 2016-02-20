package com.example.login.database;

import android.provider.BaseColumns;

/**
 * BaseColumns 提供独一无二的ID和分配行数
 */
public final class DBUser 
{

	public static final class User implements BaseColumns 
	{
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
		public static final String ISSAVED = "issaved";
		public static final String ISFLAG = "isflag";
	}

}
