package com.example.login.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.login.database.DBUser.User;
import com.example.login.entity.UnitNode;

/**
 * ������ݿ⸨����
 * 
 */
public class DBHelper
{
	public static final int DB_VERSION = 2;
	public static final String DB_NAME = "example3.db";
	private static final String TABLE_NAME_UNIT_NODE = "my_node3";
	public static final String USER_TABLE_NAME = "user3_table";
	public static final String[] USER_COLS = { User.USERNAME, User.PASSWORD,
			User.ISSAVED, User.ISFLAG };
	private SQLiteDatabase db;
	private DBOpenHelper dbOpenHelper;

	public DBHelper(Context context) 
	{
		this.dbOpenHelper = new DBOpenHelper(context);
		establishDb();
	}

	/**
	 * ����ݿ�
	 */
	private void establishDb() 
	{
		if (this.db == null) 
		{
			this.db = this.dbOpenHelper.getWritableDatabase();
		}
	}

	/**
	 * ����һ���¼
	 * 
	 * @param map
	 *            Ҫ����ļ�¼
	 * @param tableName
	 *            ����
	 * @return �����¼��id -1��ʾ���벻�ɹ�
	 */
	public long insertOrUpdate(String userName, String password, int isSaved, int isFlag) 
	{
		boolean isUpdate = false;
		String[] usernames = queryAllUserName();
		
		for (int i = 0; i < usernames.length; i++) 
		{
			
			if (userName.equals(usernames[i])) 
			{
				
				isUpdate = true;
				break;
				
			}
		}
		long id = -1;
		
		if (isUpdate) 
		{
			
			id = update(userName, password, isSaved, isFlag);
			
		} else 
		{
			if (db != null)
			{
				ContentValues values = new ContentValues();
				values.put(User.USERNAME, userName);
				values.put(User.PASSWORD, password);
				values.put(User.ISSAVED, isSaved);
				values.put(User.ISFLAG, isFlag);
				id = db.insert(USER_TABLE_NAME, null, values);
			}
		}
		return id;
	}
	
	public void updateFlag()
	{
	
		try
		{
			
			establishDb();
			db.execSQL(" update " + USER_TABLE_NAME + " set " + User.ISFLAG + " = " +  0 ); 	
		}finally
		{
			;//cleanup();
		}
		
	}
	
	public void updateCurrentFlag(String userName)
	{
		
		
			establishDb();
			db.execSQL(" update " + USER_TABLE_NAME + " set " + User.ISFLAG + " = " +  1  
			+ " where " + User.USERNAME + " = '" + userName + "' ");
			Log.e("<><><>", "flag"+" update " + USER_TABLE_NAME + " set " + User.ISFLAG + " = " +  1  
					+ " where " + User.USERNAME + " = '" + userName + "' ");
			
	}
		
		

	public int queryIsFlag(String username)
	{
		String sql = " select * from " + USER_TABLE_NAME + " where "
				+ User.USERNAME + " = '" + username + "'";
		Cursor cursor = db.rawQuery(sql, null);
		int i = 0 ;
		if (cursor.getCount() > 0) 
		{
			cursor.moveToFirst();
			i = cursor.getInt(cursor.getColumnIndex(User.ISFLAG));
			
			Log.e("真OOOOOOO", "sfsfdfsfsd"+i);
			Log.e("真OOOOOOO", "sfsfdfsfsd"+" select * from " + USER_TABLE_NAME + " where "
					+ User.USERNAME + " = '" + username + "'");
		}

		return i;
	}
	
	/**
	 * ɾ��һ���¼
	 * 
	 * @param userName
	 *            �û���
	 * @param tableName
	 *            ����
	 * @return ɾ���¼��id -1��ʾɾ��ɹ�
	 */
	public long delete(String userName) 
	{
		// long id = db.delete(USER_TABLE_NAME, userName, USER_COLS);
		// return id;
		long id = db.delete(USER_TABLE_NAME, User.USERNAME + " = '" + userName
				+ "'", null);
		return id;
	}

	/**
	 * ����һ���¼
	 * 
	 * @param password2
	 * 
	 * @param
	 * 
	 * @param tableName
	 *            ����
	 * @return ���¹���¼��id -1��ʾ���²��ɹ�
	 */
	public long update(String username, String password, int isSaved, int isFlag)
	{
		ContentValues values = new ContentValues();
		values.put(User.USERNAME, username);
		values.put(User.PASSWORD, password);
		values.put(User.ISSAVED, isSaved);
		values.put(User.ISFLAG, isFlag);
		long id = db.update(USER_TABLE_NAME, values, User.USERNAME + " = '"
				+ username + "'", null);
		return id;
	}

	/**
	 * ����û����ѯ����
	 * 
	 * @param username
	 *            �û���
	 * @param tableName
	 *            ����
	 * @return Hashmap ��ѯ�ļ�¼
	 */
	public String queryPasswordByName(String username)
	{
		String sql = "select * from " + USER_TABLE_NAME + " where "
				+ User.USERNAME + " = '" + username + "'";
		Cursor cursor = db.rawQuery(sql, null);
		String password = "";
		if (cursor.getCount() > 0) 
		{
			cursor.moveToFirst();
			password = cursor.getString(cursor.getColumnIndex(User.PASSWORD));
		}

		return password;
	}

	public String queryNameByFlag(int flag)
	{
		//establishDb();
		String sql = " select * from " + USER_TABLE_NAME + " where "
				+User.ISFLAG + " =  " + flag ;
		Cursor cursor = db.rawQuery(sql, null);
		String name = "";
		
		if(cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			name = cursor.getString(cursor.getColumnIndex(User.USERNAME));
		}
		
		return name;
		
	}
	
	
	/**
	 * ��ס����ѡ����Ƿ�ѡ��
	 * 
	 * @param username
	 * @return
	 */
	public int queryIsSavedByName(String username) {
		String sql = "select * from " + USER_TABLE_NAME + " where "
				+ User.USERNAME + " = '" + username + "'";
		Cursor cursor = db.rawQuery(sql, null);
		int isSaved = 0;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			isSaved = cursor.getInt(cursor.getColumnIndex(User.ISSAVED));
		}
		return isSaved;
	}

	/**
	 * ��ѯ�����û���
	 * 
	 * @param tableName
	 *            ����
	 * @return ���м�¼��list����
	 */
	public String[] queryAllUserName() 
	{
		
		if (db != null) 
		{
			Cursor cursor = db.query(USER_TABLE_NAME, null, null, null, null,
					null, null);
			int count = cursor.getCount();
			String[] userNames = new String[count];
			if (count > 0) 
			{
				cursor.moveToFirst();
				for (int i = 0; i < count; i++) 
				{
					userNames[i] = cursor.getString(cursor
							.getColumnIndex(User.USERNAME));
					cursor.moveToNext();
				}
			}
			return userNames;
		} 
		else 
		{
			return new String[0];
		}

	}
	
	public String queryName()
	{
		
		/*String sql = " select * from " + USER_TABLE_NAME
				+ " where ip_addr = '" + ip + "'";*/
		Cursor cursor = null;
		String sql = "select" + User.USERNAME +"from" +USER_TABLE_NAME ;
		
		 cursor = db.rawQuery(sql, null);
		
		String username = "";
		if(cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			username = cursor.getString(cursor.getColumnIndex(User.USERNAME));
			Log.e("", "name"+ username);
		}
		
		return username;
	}

	/**
	 * �ر���ݿ�
	 */
	public void cleanup() {
		if (this.db != null) {
			this.db.close();
			this.db = null;
		}
	}

	/***
	 * 
	 * 创建节点表
	 */
	// 节点号
	public void createNodeTb() {
		/*
		 * "id", "name", "ip_addr", "sub_mask", "gate_way", "in_door_serv",
		 * "center_serv", "room_num", "node_uid"
		 */
		String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_UNIT_NODE
				+ " (" + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT," + "id"
				+ " INTEGER," + "name" + " TEXT," + "ip_addr" + " TEXT,"
				+ "sub_mask" + " TEXT," + "gate_way" + " TEXT,"
				+ "in_door_serv" + " TEXT," + "center_serv" + " TEXT,"
				+ "room_num" + " TEXT," + "node_uid" + " TEXT" + ")";
		Log.e("e/", ">>>>>>>>>>>>>>createNodeTb=" + sql);
		try {
			establishDb();
			db.execSQL(sql);
		} finally {
			cleanup();
		}
	}

	// 通过名字删除表
	public void delTbByName(String tbName) 
	{
		String sql = "DROP TABLE " + tbName;
		try {
			establishDb();
			db.execSQL(sql);
		} finally {
			cleanup();
		}
	}

	// 清除节点
	public void clearUnitNode() 
	{
		delTbByName(TABLE_NAME_UNIT_NODE);
		createNodeTb();
	}

	// 通过IP来更新节点名称
	public String queryUnitnodeNameByIp(String ip)
	{
		String name = null;
		Cursor cursor = null;
		createNodeTb();
		try {
			establishDb();
			cursor = db.query(TABLE_NAME_UNIT_NODE, new String[] { "name" },
					"ip_addr=?", new String[] { ip }, null, null, null);
			while (cursor.moveToNext()) {
				name = cursor.getString(cursor.getColumnIndex("name"));
			}

		} finally {
			cleanup();
		}
		return name;
	}

	// 通过IP来更新节点
	private int queryUnitNodeByIp(String ip) {
		String sql = " select * from " + TABLE_NAME_UNIT_NODE
				+ " where ip_addr = '" + ip + "'";
		Cursor cursor = null;
		createNodeTb();
		int ret = 0;
		// Log.e("err ", ">>>>>>>>>>>: " + sql );
		try {
			establishDb();
			cursor = db.rawQuery(sql, null);
			if (cursor != null) 
			{
				if (cursor.moveToFirst()) 
				{
					ret = cursor.getCount();
				}
				cursor.close();
			}
		} finally {
			cleanup();
		}
		return ret;
	}

	// 插入单元节点
	public long insertUnitNode(UnitNode node) {
		int cnt = queryUnitNodeByIp(node.ipAddr);
		if (cnt > 0) {
			Log.e("err ", ">>>>>>>>>>>>>>>>>>>>>>>>>>>> node cnt: " + cnt);
			return 0;
		}

		ContentValues values = new ContentValues();
		values.put("id", node.id);
		values.put("name", node.name);
		values.put("ip_addr", node.ipAddr);
		values.put("sub_mask", node.subMask);
		values.put("gate_way", node.gateWay);
		values.put("in_door_serv", node.inDoorServ);
		values.put("center_serv", node.centerServ);
		values.put("node_uid", node.uID);

		try {
			establishDb();
			return db.insert(TABLE_NAME_UNIT_NODE, null, values);
		} finally {
			cleanup();
		}
	}

	// 查询单元节点
	public List<UnitNode> queryAllUnitNodes() 
	{
		Cursor cursor = null;

		List<UnitNode> nodes = new ArrayList<UnitNode>();
		createNodeTb();
		
		try 
		{
			establishDb();

			cursor = db.query(TABLE_NAME_UNIT_NODE, new String[] { "id",
					"name", "ip_addr", "sub_mask", "gate_way", "in_door_serv",
					"center_serv", "room_num", "node_uid" }, null, null, null,
					null, "_id asc");
			
			if (cursor != null) 
			{
				while (cursor.moveToNext())
				{
					UnitNode node = new UnitNode();
					node.id = cursor.getInt(cursor.getColumnIndex("id"));
					node.name = cursor.getString(cursor.getColumnIndex("name"));
					node.ipAddr = cursor.getString(cursor
							.getColumnIndex("ip_addr"));
					node.subMask = cursor.getString(cursor
							.getColumnIndex("sub_mask"));
					node.gateWay = cursor.getString(cursor
							.getColumnIndex("in_door_serv"));
					node.centerServ = cursor.getString(cursor
							.getColumnIndex("center_serv"));
					node.roomNum = cursor.getString(cursor
							.getColumnIndex("room_num"));
					node.uID = cursor.getString(cursor
							.getColumnIndex("node_uid"));
					nodes.add(node);
				}
			}

		} finally 
		{
			cleanup();
			if (cursor != null) 
			{
				cursor.close();
			}
		}
		return nodes;
	}

	
	
	/**
	 * ��ݿ⸨����
	 */
	private static class DBOpenHelper extends SQLiteOpenHelper
	{

		public DBOpenHelper(Context context) 
		{
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL("create table " + USER_TABLE_NAME + " ("
					+ BaseColumns._ID + " integer primary key," + User.USERNAME
					+ " text, "  + User.PASSWORD + " text, "
					+ User.ISSAVED + " INTEGER," + User.ISFLAG + " INTEGER) ");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
			onCreate(db);
		}

	}

}
