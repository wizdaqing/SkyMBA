package com.mba.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import cn.mba.manageactivity.SysApplication;
import cn.wiz.sdk.WizWindow;
import cn.wiz.sdk.api.WizEventsCenter;
import cn.wiz.sdk.api.WizEventsCenter.WizDatabaseEventsListener;
import cn.wiz.sdk.api.WizEventsCenter.WizDatabaseObjectType;
import cn.wiz.sdk.api.WizEventsCenter.WizReadStausChangedListener;
import cn.wiz.sdk.api.WizLogger;
import cn.wiz.sdk.api.WizObject.WizObjectBase;
import cn.wiz.sdk.api.WizStatusCenter;
import cn.wiz.sdk.api.WizEventsCenter.WizSyncEventsListener;
import cn.wiz.sdk.api.WizObject.WizTag;
import cn.wiz.sdk.db.WizDatabase;
import cn.wiz.sdk.settings.WizAccountSettings;
import cn.wiz.sdk.settings.WizSystemSettings;
import cn.wiz.sdk.util.WizMisc;

import com.example.skymba.R;
import com.example.skymba.R.color;
import com.mba.account.AccountStatus;
import com.mba.adapter.SkyMBAGridViewAdapter;
import com.mba.mygridview.MyGridView;
//import com.mba.slipbutton.SlipButton;
//import com.mba.slipbutton.SlipButton.OnChangedListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

@SuppressLint({ "SdCardPath", "NewApi" })
public class SkyMBAHome extends Activity implements WizSyncEventsListener,WizDatabaseEventsListener
//  implements OnChangedListener
  {
	private ImageView imgToggle;
	private ImageView personalCenter;
	private MyGridView gridViewBag;
	private MyGridView gridViewLife;
	private SkyMBAGridViewAdapter adapterBag;
	private SkyMBAGridViewAdapter adapterLife;
	private String mKbGuid;
	private Dialog waitDialog;
	private static Boolean isExit = false;
	final int TO_DOC_LIST=1001;
	private RelativeLayout topHome;
	private int lastUpdateBag[]=new int[8];
	private int lastUpdateLife[]=new int[3];
	
	private static final String DATA_PATH = "/mnt/sdcard/";
    

	private String[] catoNames = { "管理", "创业", "营销", "人力", "销售", "金融", "财务",
			"案例", "养生", "运动", "品酒" };
	private String[] tagIds = { "0a78b0d5-da7b-45be-bb66-8c62ef4fa7b4",
			"566f1469-80b2-4b68-a6b0-adfca48bd6f3",
			"8c642f8c-584b-4b16-b315-e537b6e4c429",
			"c721f718-0e2d-4a92-89a7-916e72690cd8",
			"c01f51b9-c489-49be-bd7d-412fe041593c",
			"80ca4ee9-eb75-48d4-9051-a78469f1970a",
			"d5f53fcb-5688-4667-a68d-d8603a644d37",
			"cb5288ca-9b46-4645-8cef-d68fea08e730",
			"4d0dca24-a720-44bd-97fe-a22c764b640a",
			"bfa489bb-d7cd-4947-b61c-7e0892c2d19f",
			"f544fa53-b201-4cd4-9119-115837af5071" };
	private int[] imgResId={R.drawable.manage_press,R.drawable.venture_press,R.drawable.marketing_press,R.drawable.hr_press,
			R.drawable.sell_press,R.drawable.financial_press,R.drawable.finance_press,
			R.drawable.case_press,R.drawable.health_press,R.drawable.sport_press,R.drawable.wine_tasting_press};
	
	private String url=null;
	private String urlMain="http://mba.trends-china.com/service.ashx?";
	private JSONObject object;
	public	  static  int ACCOUNT_STATUS=-1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		SysApplication.getInstance().addActivity(this);
		DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    int widthScreen = dm.widthPixels;
	    int heightScreen = dm.heightPixels;
		ini();
		
		Intent intent=this.getIntent();
		String data=intent.getStringExtra("fromLoading");
		if(data!=null)
		{
			 if(isFirst(this)){		//新建一个字段在com.example.skymba_preferences.xml中命名为account_status
					WizSystemSettings.setGroupDownLoadDataType(this, WizSystemSettings.downloadTypeOfNull);
					createCols();
					createPrefeName();
			     	}
			 if(WizMisc.isNetworkAvailable(this)){
				    waitDialog = getWaitDialog();
//					waitDialog.show();
//					WizEventsCenter.init();
//					WizLogger.init();		
				    WizEventsCenter.addDatabaseListener(this);
			        WizEventsCenter.addSyncListener(this);
			    	WizLogger.logActionOneDay(this, "start_mba");
//			    	String password = WizAccountSettings.getAccountPasswordByUserId(this, SkyMBALoading.DEFAULT_USERID);
					WizStatusCenter.startAllThreads(this, SkyMBALoading.DEFAULT_USERID, SkyMBALoading.DEFAULT_PASSWORD);
			 }
				
				
		}
		
		addListener();
		
		
		
		

	}
	
    public void createPrefeName(){
    	SharedPreferences pref = this.getSharedPreferences("myActivityName", 0);
    	ACCOUNT_STATUS=-1;
    	Editor editor = pref.edit();
		editor.putInt("account_status", -1);
		editor.commit();
    }
    public int getWindowWidthPixels(){

		DisplayMetrics metrics =new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
    }
	public  void createCols(){
		  WizDatabase  db1=WizDatabase.getDb(this,SkyMBALoading.DEFAULT_USERID, SkyMBALoading.KBGUID_BAG);
		   //四个字段：收藏，好评，阅读，好评数
		  db1.addTableColumn(WizDatabase.mTableNameOfDocument, "FAVORITE", WizDatabase.TableColumnType.COLUMNTYPEINT);
		   db1.addTableColumn(WizDatabase.mTableNameOfDocument, "LIKE", WizDatabase.TableColumnType.COLUMNTYPEINT);
		   db1.addTableColumn(WizDatabase.mTableNameOfDocument, "READ", WizDatabase.TableColumnType.COLUMNTYPEINT);
		   db1.addTableColumn(WizDatabase.mTableNameOfDocument, "LIKE_SCORE", WizDatabase.TableColumnType.COLUMNTYPEINT);
		   db1.addTableColumn(WizDatabase.mTableNameOfDocument, "CREDIT_READ", WizDatabase.TableColumnType.COLUMNTYPEINT);
			  
		   
		   WizDatabase db2=WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID, SkyMBALoading.KBGUDI_LIFE);
		   db2.addTableColumn(WizDatabase.mTableNameOfDocument, "FAVORITE", WizDatabase.TableColumnType.COLUMNTYPEINT);					  
		   db2.addTableColumn(WizDatabase.mTableNameOfDocument, "LIKE", WizDatabase.TableColumnType.COLUMNTYPEINT);
		   db2.addTableColumn(WizDatabase.mTableNameOfDocument, "READ", WizDatabase.TableColumnType.COLUMNTYPEINT);
		   db2.addTableColumn(WizDatabase.mTableNameOfDocument, "LIKE_SCORE", WizDatabase.TableColumnType.COLUMNTYPEINT);
			  
	}



    

	
	public boolean isFirst(Context mContext){
		Boolean isFirstIn = false;
		SharedPreferences pref = mContext.getSharedPreferences("myActivityName", 0);
		//取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		isFirstIn = pref.getBoolean("isFirstIn", true);
		
		return isFirstIn;
	}
	public void setNotFirst(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences("myActivityName", 0);
		Editor editor = pref.edit();
		editor.putBoolean("isFirstIn", false);
		editor.commit();
		
	}

	
	
	
	
	
	

	
	
	@Override
	protected void onDestroy() {
		WizEventsCenter.removeSyncListener(this);	
		WizEventsCenter.removeDatabaseListener(this);
		super.onDestroy();
	}
	// 增加退出提示
    public void getHeight(){
    	
            
             
     
    }
	public void ini() {
		 final float scale = this.getResources().getDisplayMetrics().density;  
		 float topHeight=60*scale + 0.5f; 
		int width=this.getWindowManager().getDefaultDisplay().getWidth();
		int height= this.getWindowManager().getDefaultDisplay().getHeight();
		
		Log.i("h", topHeight+","+width+","+height);
		gridViewBag = (MyGridView) this.findViewById(R.id.gridViewBag);
		gridViewLife = (MyGridView) this.findViewById(R.id.gridViewLife);
		personalCenter = (ImageView) this.findViewById(R.id.personalCenterImg);
		imgToggle=(ImageView)this.findViewById(R.id.BagOrLifeToggle);
		
		if (AccountStatus.verifyLogin(this)) {
			personalCenter
					.setImageDrawable(getResources().getDrawable(R.drawable.user_login));
		}else{
			personalCenter
			.setImageDrawable(getResources().getDrawable(R.drawable.user_unlogin));
		}
		  personalCenter.setVisibility(View.VISIBLE);
		

		imgToggle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int level=imgToggle.getDrawable().getLevel();
				if(level==1||level==0){
					imgToggle.getDrawable().setLevel(2);
					mKbGuid=SkyMBALoading.KBGUDI_LIFE;
					gridViewLife.setVisibility(View.VISIBLE);
					gridViewBag.setVisibility(View.GONE);
				}else{
					imgToggle.getDrawable().setLevel(1);
					mKbGuid=SkyMBALoading.KBGUID_BAG;
					gridViewBag.setVisibility(View.VISIBLE);
					gridViewLife.setVisibility(View.GONE);
				}
			}
		});
			

		mKbGuid = SkyMBALoading.KBGUID_BAG;
		if (adapterBag == null) { 
			adapterBag = new SkyMBAGridViewAdapter(this, SkyMBALoading.BAG,null);
			
			
		}
		if (adapterLife == null) {
			adapterLife = new SkyMBAGridViewAdapter(this, SkyMBALoading.LIFE,null);
		}


		gridViewBag.setAdapter(adapterBag);
		gridViewBag.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridViewLife.setAdapter(adapterLife);
		gridViewLife.setSelector(new ColorDrawable(Color.TRANSPARENT));
		

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case 100:
			
			if(resultCode==RESULT_OK){
				updateNum(1);
			}else if(resultCode==RESULT_CANCELED) {
				updateNum(2);
			}
		break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	public void addListener(){
		OnItemClickListener listenerItem = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LinearLayout shelfLay = (LinearLayout) view;
				ImageView img=(ImageView)shelfLay.findViewById(R.id.shelf_img);
				TextView txt=(TextView)shelfLay.findViewById(R.id.updateView);
				
				int mTagSeq = Integer.parseInt(img.getTag().toString());
				String mTagId = tagIds[mTagSeq];
				String mTagName = catoNames[mTagSeq];
				WizDatabase db= WizDatabase.getDb(
						SkyMBAHome.this,
						SkyMBALoading.DEFAULT_USERID,
						mKbGuid);

				db.setAllUnreadDocumentsReadByTagId(mTagId);
				
				Intent intent = new Intent();
				intent.setClass(SkyMBAHome.this, SkyMBADocumentsList.class);
				intent.putExtra("mTagId", mTagId);
				intent.putExtra("mTagName", mTagName);
				intent.putExtra("mKbGuid", mKbGuid);
				startActivityForResult(intent, 100);//request_code=100
				


			}

		};
		
		gridViewBag.setOnItemClickListener(listenerItem);
		gridViewLife.setOnItemClickListener(listenerItem);
		 personalCenter.setOnClickListener(new OnClickListener(){
				
			 @Override
			 public void onClick(View v) {
			 Intent intent=new Intent(SkyMBAHome.this,
			 SkyMBAPersonalCenter.class);
			
			 startActivity(intent);
			 SkyMBAHome.this.finish();
			
			 }});
	}
	// 重写按下“后退”键时所做的操作


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Timer tExit = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				isExit = false;
			}
		};

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isExit == false) {
				isExit = true;
				Toast.makeText(this, "再按一次后退键退出应用程序", Toast.LENGTH_SHORT)
						.show();
				// 定义计划任务，根据参数的不同可以完成以下种类的工作：
				// 在固定时间执行某任务，在固定时间开始重复执行某任务，重复时间间隔可控，在延迟多久后执行某任务，在延迟多久后重复执行某任务，重复时间间隔可控
				// 在上面已定义
				task=null;
				task=new TimerTask(){
					@Override 
					public void run() {
						isExit = false;
					}
				};
				tExit.schedule(task, 2000);
			} else {
				finish();
				System.exit(0);
			}
		}
		return true;

	}

	@Override
	public void onSyncBegin() {
		
		System.out.println("sync_begin");
	}
	
	public void updateNum(int choice){
		WizDatabase db1 = WizDatabase.getDb(
				SkyMBAHome.this,
				SkyMBALoading.DEFAULT_USERID,
				SkyMBALoading.KBGUID_BAG);
		WizDatabase db2 = WizDatabase.getDb(
				SkyMBAHome.this,
				SkyMBALoading.DEFAULT_USERID,
				SkyMBALoading.KBGUDI_LIFE);
		if(choice==1){
		for(int j=0;j<8;j++){
			lastUpdateBag[j]=db1.getUnreadDocumentsCountByTag(CatoAndKb.tagIds[j]);
				System.out.println("tag--"+j+",更新-->"+lastUpdateBag[j]);
			}
		adapterBag = new SkyMBAGridViewAdapter(this, SkyMBALoading.BAG,lastUpdateBag);
		gridViewBag.setAdapter(adapterBag);
		}else{
		for(int j=0;j<3;j++){
			lastUpdateLife[j]=db2.getUnreadDocumentsCountByTag(CatoAndKb.tagIds[j+8]);
			}
	
		
		adapterLife = new SkyMBAGridViewAdapter(this, SkyMBALoading.LIFE,lastUpdateLife);
		
		gridViewLife.setAdapter(adapterLife);
		}
	}
	public void markRead(){
		if(isFirst(SkyMBAHome.this)){//第一次进来，不显示未读数目，故全部设为“已读”
			WizDatabase db1 = WizDatabase.getDb(
					SkyMBAHome.this,
					SkyMBALoading.DEFAULT_USERID,
					SkyMBALoading.KBGUID_BAG);
			WizDatabase db2 = WizDatabase.getDb(
					SkyMBAHome.this,
					SkyMBALoading.DEFAULT_USERID,
					SkyMBALoading.KBGUDI_LIFE);
			db1.setAllUnreadDocumentsReaded();
			db2.setAllUnreadDocumentsReaded();
			setNotFirst(this);
		}
	}

	@Override
	public void onSyncEnd(boolean succeeded) {
	
		 markRead();
		if(succeeded){
			//收藏与服务器同步
			
			
			addListener();
			
		}
		
	}

	@Override
	public void onSyncProgress(int progress) {
		System.out.println("sync_progress");
	}

	@Override
	public void onSyncStatus(String status) {
		System.out.println("sync_status");
	}

	@Override
	public void onSyncException(Exception e) {
		
		
	}
	
	private Dialog getWaitDialog() {
	
	//挺好，无标题的进度提示可用。	
return WizWindow.createProgressDialog(this,		
				R.string.loading_later, true, false);
	}
	
	
	
	
	
	@Override
	public void onDatabaseRefreshObject(WizDatabase db,
			WizDatabaseObjectType type) {
		if(!isFirst(SkyMBAHome.this)){
		if(type.equals(WizDatabaseObjectType.DocumentsCount)){//如果笔记数目发生改变
			WizDatabase db1 = WizDatabase.getDb(
					SkyMBAHome.this,
					SkyMBALoading.DEFAULT_USERID,
					SkyMBALoading.KBGUID_BAG);
			WizDatabase db2 = WizDatabase.getDb(
					SkyMBAHome.this,
					SkyMBALoading.DEFAULT_USERID,
					SkyMBALoading.KBGUDI_LIFE);
			
			for(int j=0;j<8;j++){
				lastUpdateBag[j]=db1.getUnreadDocumentsCountByTag(CatoAndKb.tagIds[j]);
				}
			for(int j=0;j<3;j++){
				lastUpdateLife[j]=db2.getUnreadDocumentsCountByTag(CatoAndKb.tagIds[j+8]);
				}
			}
			adapterBag = new SkyMBAGridViewAdapter(this, SkyMBALoading.BAG,lastUpdateBag);
			adapterLife = new SkyMBAGridViewAdapter(this, SkyMBALoading.LIFE,lastUpdateLife);
			gridViewBag.setAdapter(adapterBag);
			gridViewLife.setAdapter(adapterLife);
			
			
		}}
	}


