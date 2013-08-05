package com.mba.view;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import cn.mba.manageactivity.SysApplication;
import cn.wiz.sdk.WizWindow;
import cn.wiz.sdk.api.WizEventsCenter;
import cn.wiz.sdk.api.WizAsyncAction.WizAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAsyncActionThread;
import cn.wiz.sdk.api.WizEventsCenter.WizSyncEventsListener;
import cn.wiz.sdk.api.WizEventsCenter.WizSyncKbEventsListener;
import cn.wiz.sdk.api.WizEventsCenter.WizSyncKbStep;
import cn.wiz.sdk.api.WizObject.WizDocument;
import cn.wiz.sdk.api.WizObject.WizKb;
import cn.wiz.sdk.api.WizObject.WizTag;
import cn.wiz.sdk.api.WizAsyncAction;
import cn.wiz.sdk.api.WizLogger;
import cn.wiz.sdk.api.WizStatusCenter;
import cn.wiz.sdk.api.WizSync;
import cn.wiz.sdk.db.WizDatabase;

import cn.wiz.sdk.settings.WizAccountSettings;
import cn.wiz.sdk.settings.WizSystemSettings;
import cn.wiz.sdk.util.FileUtil;
import cn.wiz.sdk.util.WizMisc;


import com.example.skymba.R;

import com.mba.json.JsonURL;
import com.mba.json.ProcessJson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

public class SkyMBALoading extends Activity implements Runnable{
	
	static public final String DEFAULT_USERID="AIRMBA@163.com";
	static public final String DEFAULT_PASSWORD="iwom123456";
	static public final String KBGUID_BAG="44bdd495-2e90-4581-afb2-b154809c4cd8";
	static public final String KBGUDI_LIFE="008e0ca8-7f8d-44db-aa6a-9db4ce21c3cc";
	static public final int BAG=1;
	static public final int LIFE=2;
	
	static public final String FAVORITE="FAVORITE";
	static public final String LIKE="LIKE";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.sky_mba_loading);
		SysApplication.getInstance().addActivity(this); 		
		Thread t=new Thread(this);
		t.start();
	
	}

	@Override
	public void run() {
		try {
			Thread.sleep(2000);
			Intent intent=new Intent(SkyMBALoading.this,SkyMBAHome.class);
			intent.putExtra("fromLoading", "fromLoading");
			startActivity(intent);
			this.finish();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
	
}
