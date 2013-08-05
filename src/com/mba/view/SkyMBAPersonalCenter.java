package com.mba.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import redstone.xmlrpc.XmlRpcFault;

import cn.mba.manageactivity.SysApplication;

import cn.wiz.sdk.WizWindow;
import cn.wiz.sdk.api.WizASXmlRpcServer;
import cn.wiz.sdk.api.WizAsyncAction;
import cn.wiz.sdk.api.WizLogger;
import cn.wiz.sdk.api.WizAsyncAction.WizAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAsyncActionThread;
import cn.wiz.sdk.api.WizObject.WizDocument;
import cn.wiz.sdk.api.WizObject.WizUser;
import cn.wiz.sdk.api.WizObject.WizUserInfo;
import cn.wiz.sdk.db.WizDatabase;
import cn.wiz.sdk.settings.WizAccountSettings;
import cn.wiz.sdk.settings.WizSystemSettings;
import cn.wiz.sdk.util.WizMisc;

import com.example.skymba.R;
import com.mba.account.AccountStatus;
import com.mba.json.JsonURL;
import com.mba.json.ProcessJson;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

@SuppressLint("ShowToast")
public class SkyMBAPersonalCenter extends Activity {
	private TextView personal_center;
	private ImageView logout;
	private TextView usernameView;

	// 注册和登陆
	private EditText acText;
	private EditText psText;
	private Button regBtn;
	private Button loginBtn;

	// 成绩单
	public static final int FOR_LOGIN=1;
	public static final int FOR_REG=2;

	private TextView creditScoreView;
	private TextView gradeView;
	private TextView levelView;
	// 单
	private TextView manageScoreView;
	private TextView marketScoreView;
	private TextView saleScoreView;
	private TextView financialScoreView;

	private TextView financeScoreView;
	private TextView humanResourceScoreView;
	private TextView exampleScoreView;
	private TextView ventureScoreView;

	private TextView myFavoriteView;
	private ImageView myFavoriteDetail;

	private ImageView offlineDownloadImg;
	private TextView offlineDownloadView;
	private ToggleButton offlineButton;

	private ImageView deleteOfflinesImg;
	private TextView deleteOfflinesView;

	private ImageView aboutImgView;
	private TextView aboutTextView;
	private ImageView aboutImgDetail;

	private ImageView suggestFeedbackImg;
	private TextView suggestFeedBackText;
	private ImageView suggestFeedbackDetail;

	private ImageView markImg;
	private TextView markText;
	private ImageView markDetail;

	private String username;
	private String password;

	private String url;
	private String urlUploadRead;
	private String urlMain = "http://mba.trends-china.com/service.ashx?";
	private JSONObject object;
	private JSONObject objectUploadRead;
	private long exitTime = 0;
	private Dialog waitDialog;
	private Dialog deleteOfflineDialog;
	private ArrayList<String> listFavor = null;
	private ArrayList<String> listLike = null;
	private RelativeLayout lay_favor;
	private RelativeLayout lay_download;
	private RelativeLayout lay_clear;
	private RelativeLayout lay_about;
	private RelativeLayout lay_suggest;
	private RelativeLayout lay_mark;
	private boolean mCopyAnonymousAccountData = false;
	private String curUserid = null;

	private JSONObject objectFavorList;
	private JSONObject objectLikeList;
	private Matrix matrix;
	private boolean isLogin = false;
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
	private ArrayList<String> listReadOnes;
	private Handler dialogHandler;
	private String userGuid = null;
	private WizDatabase dbBag=null;
	private WizDatabase dbLife=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_center);
		SysApplication.getInstance().addActivity(this);

		init();
		initData();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(SkyMBAPersonalCenter.this,
					SkyMBAHome.class);
			startActivity(intent);
			this.finish();
			return true;

		}
		return super.onKeyDown(keyCode, event);

	}
	
	public ArrayList<String> getFavorList(WizDatabase db){
		String sql="SELECT DOCUMENT_GUID FROM WIZ_DOCUMENT WHERE FAVORITE = 1 OR FAVORITE = 2 ";
		return db.sqlToStringArray(sql, 0);
	}
	public void clearDb(){
		String defaultUserid = SkyMBALoading.DEFAULT_USERID;
		WizDatabase db1 = WizDatabase.getDb(
				SkyMBAPersonalCenter.this, defaultUserid,
				SkyMBALoading.KBGUID_BAG);
		WizDatabase db2 = WizDatabase.getDb(
				SkyMBAPersonalCenter.this, defaultUserid,
				SkyMBALoading.KBGUDI_LIFE);
		clear(db1);
		clear(db2);
	}
	public boolean clear(WizDatabase db) {
		String sql = "UPDATE WIZ_DOCUMENT SET CREDIT_READ = 0 , FAVORITE = 0 , LIKE = 0 , READ = 0 , LIKE_SCORE = 0";// 字符串型数据加''
		return db.execSql(sql);

	}
	public void showDialog(int choiceid, int ok, int cancle) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SkyMBAPersonalCenter.this);
		builder.setMessage(choiceid)
				.setCancelable(false)
				.setPositiveButton(ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						String topicidsForFavor = getTopicidsForUpdate("FAVORITE");
						String topicidsForCreditRead=getTopicidsForUpdate("CREDIT_READ");
						if(topicidsForFavor!=null){
							uploadFavor(topicidsForFavor,"addfavoritelist");
						}
//						if(topicidsForCreditRead!=null){
//							uploadFavor(topicidsForCreditRead,"addnewreadlist");
//						}
												
						//注销账号，清除数据库，不传favor
						AccountStatus.setAccountStatus(
								SkyMBAPersonalCenter.this, 0);//注销
						clearDb();
						psText.setText("");
						creditScoreView.setText("0");
						gradeView.setText("0");
						initData();
						
						
					}
				})
				.setNegativeButton(cancle,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).create().show();
	}

	public void addListener() {

		logout.setOnClickListener(new OnClickListener() {
			// 注销账号

			@Override
			public void onClick(View v) {
				if (!AccountStatus.verifyLogin((SkyMBAPersonalCenter.this))) {
					Toast.makeText(SkyMBAPersonalCenter.this,
							R.string.no_account_now, Toast.LENGTH_SHORT).show();
				} else{
					showDialog(R.string.choice_of_logout, R.string.sure,
							R.string.cancle);
				
				}
			}
		});

		offlineButton.setOnCheckedChangeListener(listenerToggle);

		lay_favor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!WizAccountSettings.getUserId(SkyMBAPersonalCenter.this)
						.equals("")) {
					Intent intent = new Intent();
					intent.setClass(SkyMBAPersonalCenter.this,
							SkyMBAFavoriteList.class);
					intent.putExtra("myfavorite", "myfavorite");
					startActivity(intent);

				} else if (WizAccountSettings.getUserId(
						SkyMBAPersonalCenter.this).equals("")) {

					Toast.makeText(SkyMBAPersonalCenter.this,
							R.string.check_favored_files_when_unlogin,
							Toast.LENGTH_LONG).show();
				}

			}
		});

		lay_clear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!WizAccountSettings.getUserId(SkyMBAPersonalCenter.this)
						.equals("")
						&& WizMisc
								.isNetworkAvailable(SkyMBAPersonalCenter.this)) {
					Toast.makeText(SkyMBAPersonalCenter.this,
							R.string.start_delete_offline, Toast.LENGTH_LONG)
							.show();
					startDeleteFiles();
				} else if (!WizMisc
						.isNetworkAvailable(SkyMBAPersonalCenter.this)) {
					Toast.makeText(SkyMBAPersonalCenter.this,
							R.string.start_delete_offline, Toast.LENGTH_LONG)
							.show();
					ArrayList<String> list=getFavorList(dbBag);
					list.addAll(getFavorList(dbLife));
					deleteFiles(list);
					Toast.makeText(SkyMBAPersonalCenter.this,
							R.string.delete_offline_success, Toast.LENGTH_SHORT).show();
			
				} else if (WizAccountSettings.getUserId(
						SkyMBAPersonalCenter.this).equals("")) {

					Toast.makeText(SkyMBAPersonalCenter.this,
							R.string.delete_offline_when_unlogin,
							Toast.LENGTH_LONG).show();
				}

			}
		});
		lay_about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SkyMBAPersonalCenter.this, SkyMBAAbout.class);
				startActivity(intent);
			}
		});
		lay_suggest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!WizMisc.isNetworkAvailable(SkyMBAPersonalCenter.this)) {
					Toast.makeText(SkyMBAPersonalCenter.this,
							R.string.submit_suggest_when_no_network,
							Toast.LENGTH_LONG).show();
				} else {

					Intent intent = new Intent();
					intent.setClass(SkyMBAPersonalCenter.this,
							SkyMBASuggestFeedback.class);
					startActivity(intent);
				}
			}
		});
		loginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String userId = getUserId();
				if (!TextUtils.isEmpty(userId)) {
					serverVerifyUser(userId);
				} else {
					WizWindow.showMessage(SkyMBAPersonalCenter.this,
							R.string.error_message_null_user_name, false);
				}
			}
		});

		regBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				serverVerifyUser();
			}
		});

	}

	// 登陆成功，跳转到个人中心界面
	public String getTopicidsForUpdate(String column) {
		final WizDatabase dbBag = WizDatabase.getDb(SkyMBAPersonalCenter.this,
				SkyMBALoading.DEFAULT_USERID, SkyMBALoading.KBGUID_BAG);
		final WizDatabase dbLife = WizDatabase.getDb(SkyMBAPersonalCenter.this,
				SkyMBALoading.DEFAULT_PASSWORD, SkyMBALoading.KBGUDI_LIFE);
		String sql = "SELECT DOCUMENT_GUID FROM WIZ_DOCUMENT WHERE"+ column+" = 1 ";
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> list2 = new ArrayList<String>();
		list = dbBag.sqlToStringArray(sql, 0);
		String topicids = null;
		StringBuilder sb = new StringBuilder();
		int size = list.size();
		if (size > 0) {
			for (int j = 0; j < size; j++) {
				sb.append(list.get(j));
				sb.append(":");
			}
			list2 = dbLife.sqlToStringArray(sql, 0);
			int size2 = list2.size();
			for (int j = 0; j < size2; j++) {
				sb.append(list2.get(j));
				sb.append(":");
			}
			sb.toString();
			int dot = sb.lastIndexOf(":");
			topicids = sb.substring(0, dot);// 得到拼接的topicids
		}
		return topicids;
	}

	// 登陆成功，跳转到个人中心界面
	public String getTopicidsNa() {
		final WizDatabase dbBag = WizDatabase.getDb(SkyMBAPersonalCenter.this,
				SkyMBALoading.DEFAULT_USERID, SkyMBALoading.KBGUID_BAG);
		final WizDatabase dbLife = WizDatabase.getDb(SkyMBAPersonalCenter.this,
				SkyMBALoading.DEFAULT_PASSWORD, SkyMBALoading.KBGUDI_LIFE);
		ArrayList<String> list = new ArrayList<String>();
		String topicids = null;
		StringBuilder sb = new StringBuilder();

		for (int m = 0; m < 8; m++) {
			String forSqlBag = "SELECT DOCUMENT_GUID FROM WIZ_DOCUMENT WHERE READ = 1 AND DOCUMENT_TAG_GUIDS = '"
					+ tagIds[m] + "'";
			ArrayList<String> listTemp = dbBag.sqlToStringArray(forSqlBag, 0);
			int sizeTemp = listTemp.size();
			if (sizeTemp != 0) {

				for (int j = 0; j < sizeTemp; j++) {
					sb.append(listTemp.get(j));
					sb.append(",");
				}
				int dot = sb.lastIndexOf(",");
				sb.replace(dot, dot + 1, "");
				sb.append(";" + tagIds[m] + ":");
			}
		}

		for (int m = 0; m < 3; m++) {
			String forSqlLife = "SELECT DOCUMENT_GUID FROM WIZ_DOCUMENT WHERE READ = 1 AND DOCUMENT_TAG_GUIDS = '"
					+ tagIds[m + 8] + "'";
			ArrayList<String> listTemp = dbLife.sqlToStringArray(forSqlLife, 0);
			int sizeTemp = listTemp.size();
			if (sizeTemp != 0) {
				for (int j = 0; j < sizeTemp; j++) {
					sb.append(listTemp.get(j));
					sb.append(",");
				}
				int dot = sb.lastIndexOf(",");
				sb.replace(dot, dot + 1, "");
				sb.append(";" + tagIds[m + 8] + ":");

			}
		}
		if (sb.length() != 0) {
			int dot = sb.lastIndexOf(":");
			topicids = sb.substring(0, dot);// 得到拼接的topicids
		} else
			topicids = null;

		return topicids;
	}

	public void uploadReadInfo(final String userid, final String password,final int forWhat) {

		String topicids = getTopicidsNa();
		if (topicids != null) {// null用= ！=来判断，""用.equals来判断

			urlUploadRead = urlMain + "comm=addreadlist&ctype=1&userid="
					+ userid + "&topicids=" + topicids;
			// 网络连接获取数据：

			WizAsyncAction.startAsyncAction(null, new WizAction() {

				@Override
				public Object work(WizAsyncActionThread thread,
						Object actionData) throws Exception {
					String resultUploadRead = null;
					DefaultHttpClient httpClientUploadRead = new DefaultHttpClient();

					HttpGet requestUploadRead = new HttpGet(urlUploadRead);
					HttpResponse responseUploadRead;
					try {
						responseUploadRead = httpClientUploadRead
								.execute(requestUploadRead);
						resultUploadRead = EntityUtils
								.toString(responseUploadRead.getEntity());
						objectUploadRead = new JSONObject(resultUploadRead);

					} catch (ClientProtocolException e) {

						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					} catch (JSONException e) {

						e.printStackTrace();
					}

					return objectUploadRead;
				}

				@Override
				public void onBegin(Object actionData) {

				}

				@Override
				public void onEnd(Object actionData, Object ret) {

					int success = ProcessJson.doUploadRead(objectUploadRead);
					System.out.println("是否上传成功"+success);
					if(forWhat==SkyMBAPersonalCenter.this.FOR_REG)
					{
					WizAccountSettings.addAccount(SkyMBAPersonalCenter.this,
							userid, password, userGuid);
					}
					WizSystemSettings.setDefaultUserId(
							SkyMBAPersonalCenter.this, userid);
					AccountStatus
							.setAccountStatus(SkyMBAPersonalCenter.this, 1);
					
					initData();
					waitDialog.cancel();
					

				}

				@Override
				public void onException(Object actionData, Exception e) {
					waitDialog.cancel();
					e.printStackTrace();
					// jump();//联网失败,也跳转，界面友好

				}

				@Override
				public void onStatus(Object actionData, String status,
						int arg1, int arg2, Object obj) {

				}

			});
		}
		if(forWhat==SkyMBAPersonalCenter.this.FOR_REG)
		{
		WizAccountSettings.addAccount(SkyMBAPersonalCenter.this, userid,
				password, userGuid);
		}
		WizSystemSettings.setDefaultUserId(SkyMBAPersonalCenter.this, userid);
		AccountStatus.setAccountStatus(SkyMBAPersonalCenter.this, 1);
		initData();
		waitDialog.cancel();

	}
	
	

	void startCreateAccount() {
		class CreateAccountData {
			String userId;
			String password;
			boolean copyAnonymousAccountData;

			//
			public CreateAccountData(String userId, String password,
					boolean copyAnonymousAccountData) {
				this.userId = userId;
				this.password = password;
				this.copyAnonymousAccountData = copyAnonymousAccountData;
			}
		}
		//
		CreateAccountData loginData = new CreateAccountData(getUserId(),
				getPassword(), mCopyAnonymousAccountData);

		WizAsyncAction.startAsyncAction(loginData, new WizAction() {
			// private Dialog waitDialog;

			@Override
			public void onBegin(Object actionData) {
				waitDialog = getWaitDialog(R.string.creating_account);
				waitDialog.show();

				//
			}
			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {

				CreateAccountData createData = (CreateAccountData) actionData;
				WizASXmlRpcServer as = new WizASXmlRpcServer(
						SkyMBAPersonalCenter.this, createData.userId);
				String[] inviteCodes = { "afae0a1a", "ae54537f", "a150a2d2" };
				int inviteCodePosition = (int) (Math.random() * 1000)
						% inviteCodes.length;
				String inviteCode = inviteCodes[inviteCodePosition];

				String returnMesaage = as.createAccount(createData.password,
						inviteCode);

				WizUserInfo mUserInfo = WizAccountSettings.userLogin(SkyMBAPersonalCenter.this, createData.userId,
						createData.password);
				userGuid = mUserInfo.userGuid;
				if (createData.copyAnonymousAccountData) {
					WizDatabase.copyAnonymousData(SkyMBAPersonalCenter.this,
							createData.userId);
				}

				return returnMesaage;
			}

			@Override
			public void onEnd(Object actionData, Object ret) {
				WizLogger.logAction(SkyMBAPersonalCenter.this, "signin");

				CreateAccountData createData = (CreateAccountData) actionData;

				uploadReadInfo(createData.userId, createData.password,SkyMBAPersonalCenter.FOR_REG);
			

			}

			@Override
			public void onException(Object actionData, Exception e) {
				waitDialog.cancel();// 空指针错误！气死我了
				Log.i("exception", e.getMessage());
				if (e instanceof XmlRpcFault) {
					XmlRpcFault fault = (XmlRpcFault) e;
					if (fault.getErrorCode() == 301) {

						WizWindow.showMessage(SkyMBAPersonalCenter.this,
								R.string.error_message_invalid_password);
					} else if (fault.getErrorCode() == 310) {

						WizWindow.showMessage(SkyMBAPersonalCenter.this,
								R.string.email_have_reged);
					}
				} else {

					WizWindow.showMessage(SkyMBAPersonalCenter.this,
							R.string.error_message_no_network);
				}
				psText.setText("");
			}

			@Override
			public void onStatus(Object actionData, String status, int arg1,
					int arg2, Object obj) {
			}

			
		});
	}

	public void serverVerifyUser() {
		String userId = getUserId();
		String password = getPassword();

		if (TextUtils.isEmpty(userId)) {
			WizWindow.showMessage(this, R.string.error_message_null_user_name,
					false);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			WizWindow.showMessage(this, R.string.error_message_null_password,
					false);
			return;
		}

		startCreateAccount();

	}

	private void serverVerifyUser(String userId) {
		String password = getPassword();
		if (TextUtils.isEmpty(userId)) {
			WizWindow.showMessage(this, R.string.error_message_null_user_name,
					false);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			WizWindow.showMessage(this, R.string.error_message_null_password,
					false);
			return;
		}
		startloginAccount();
	}

	void startloginAccount() {
		class LoginData {
			String userId;
			String password;

			public LoginData(String userId, String password) {
				this.userId = userId;
				this.password = password;
			}
		}
		LoginData loginData = new LoginData(getUserId(), getPassword());

		WizAsyncAction.startAsyncAction(loginData, new WizAction() {

//			private Dialog waitDialog=null;

			@Override
			public void onBegin(Object actionData) {
				waitDialog = getWaitDialog(R.string.logining);//waitDialog为progressDialog，可以跟进去看
				waitDialog.show();

			}

			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {
				// 清空当前数据库的两个字段

				LoginData loginData = (LoginData) actionData;//actionData为登陆的账号LoginData

				WizASXmlRpcServer as = new WizASXmlRpcServer(
						SkyMBAPersonalCenter.this, loginData.userId);
				
				try {
					as.clientLogin(loginData.password);
					WizUserInfo mUserInfo=(WizUserInfo)WizAccountSettings.userLogin(SkyMBAPersonalCenter.this, loginData.userId, loginData.password);
					userGuid=mUserInfo.userGuid;

				} finally {

					as.clientLogout();

				}

				return null;
			}

			@Override
			public void onEnd(Object actionData, Object ret) {
				//来到end说明 登陆成功 否则去了onException 另 若在onEnd里再出现exception则要自己处理，而不能再抛出，因为此时 onException检测不到
				WizLogger.logAction(SkyMBAPersonalCenter.this, "signin");
				LoginData loginData = (LoginData) actionData;
				uploadReadInfo(loginData.userId,loginData.password,SkyMBAPersonalCenter.FOR_LOGIN);
				
			}

			@Override
			public void onException(Object actionData, Exception e) {
				waitDialog.cancel();

				if (e instanceof XmlRpcFault) {
					XmlRpcFault fault = (XmlRpcFault) e;
					if(fault.getErrorCode()==314){
						WizWindow.showMessage(SkyMBAPersonalCenter.this,
								R.string.error_message_no_such_username);
						acText.setText("");
					}
					else if (fault.getErrorCode() == 301) {

						WizWindow.showMessage(SkyMBAPersonalCenter.this,
								R.string.error_message_invalid_password);
					} else {

						WizWindow.showMessage(SkyMBAPersonalCenter.this,
								e.getMessage());
					}
					psText.setText("");
				} else {

					WizWindow.showMessage(SkyMBAPersonalCenter.this,
							R.string.error_message_no_network);
				}
			

			}

			@Override
			public void onStatus(Object actionData, String status, int arg1,
					int arg2, Object obj) {
			}

		});
	}
	public void uploadFavor(String topicids,String comm) {
			JsonURL jsonURL = new JsonURL(comm,
					WizAccountSettings.getUserId(SkyMBAPersonalCenter.this),
					null, null, topicids);
			url = jsonURL.urlSyncLocalFavor;

			// 网络连接获取数据：

			WizAsyncAction.startAsyncAction(null, new WizAction() {

				@Override
				public Object work(WizAsyncActionThread thread,
						Object actionData) throws Exception {
					String result = null;
					DefaultHttpClient httpClient = new DefaultHttpClient();

					String urlFull = urlMain + url;
					HttpGet request = new HttpGet(urlFull);
					HttpResponse response;
					try {
						response = httpClient.execute(request);
						result = EntityUtils.toString(response.getEntity());
						object = new JSONObject(result);

					} catch (ClientProtocolException e) {

						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					} catch (JSONException e) {

						e.printStackTrace();
					}

					return object;
				}

				@Override
				public void onBegin(Object actionData) {

				}

				@Override
				public void onEnd(Object actionData, Object ret) {
					int success = ProcessJson.doSyncLocalFavor(object);
//					AccountStatus.setAccountStatus(
//							SkyMBAPersonalCenter.this, 0);//注销
//					clearDb();//已经clear掉，不需要从1置为2
//					initData();//更新数据

					

				}

				@Override
				public void onException(Object actionData, Exception e) {
					e.printStackTrace();
					// jump();//联网失败,也跳转，界面友好

				}

				@Override
				public void onStatus(Object actionData, String status,
						int arg1, int arg2, Object obj) {

				}

			});

		

	}

	

	String getUserId() {
		return ((EditText) this.findViewById(R.id.edit_username_or_email))
				.getText().toString();
	}

	String getPassword() {
		return ((EditText) this.findViewById(R.id.edit_password)).getText()
				.toString();
	}

	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();

	}

	public boolean isFavor(String filename, ArrayList<String> list) {
		for (int j = 0; j < list.size(); j++) {
			String fname = list.get(j);
			if (filename.equals(list.get(j))) {
				return true;
			}
		}

		return false;

	}

	public void startDeleteFiles() {
		String mUserId = WizAccountSettings.getUserId(this);
		JsonURL jsonURL = new JsonURL("getFavorite", mUserId, null, null, null);
		url = jsonURL.urlGetFavorite;
		// 网络连接获取数据：

		WizAsyncAction.startAsyncAction(null, new WizAction() {

			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {
				String result = null;
				DefaultHttpClient httpClient = new DefaultHttpClient();

				String urlFull = urlMain + url;
				HttpGet request = new HttpGet(urlFull);
				HttpResponse response;
				try {
					response = httpClient.execute(request);
					result = EntityUtils.toString(response.getEntity());
					object = new JSONObject(result);

				} catch (ClientProtocolException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				} catch (JSONException e) {

					e.printStackTrace();
				}

				return object;
			}

			@Override
			public void onBegin(Object actionData) {
			}

			@Override
			public void onEnd(Object actionData, Object ret) {
				ArrayList<String> list = ProcessJson
						.doParseGetMyFavorite(object);
				list.addAll(getFavorList(dbBag));
				list.addAll(getFavorList(dbLife));
				deleteFiles(list);
				Toast.makeText(SkyMBAPersonalCenter.this,
						R.string.delete_offline_success, Toast.LENGTH_SHORT).show();
		

			}
			@Override
			public void onException(Object actionData, Exception e) {
				e.printStackTrace();

			}

			@Override
			public void onStatus(Object actionData, String status, int arg1,
					int arg2, Object obj) {

			}

		});
	}

	// Java文件操作 获取文件扩展名

	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	// Java文件操作 获取不带扩展名的文件名

	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	public boolean updateServerChanged(String guid, WizDatabase db) {
		String sql = "UPDATE WIZ_DOCUMENT SET SERVER_CHANGED = 1 WHERE DOCUMENT_GUID ="
				+ " " + "'" + guid + "'";// 字符串型数据加''
		return db.execSql(sql);

	}

	public void deleteFiles(ArrayList<String> list) {
		String sdPath = getSDPath();
		String accountPath = sdPath + "/wiznote/"
				+ SkyMBALoading.DEFAULT_USERID;
		File[] files = new File(accountPath).listFiles();//
		String filename = null;
		String fileguid = null;
		if (files != null) {

			for (File f : files) {

				filename = f.getName();

				if (filename.endsWith(".ziw")) {
					fileguid = getFileNameNoEx(filename);
					if (!isFavor(fileguid, list)) {
						try {

							f.delete();
							fileguid = getFileNameNoEx(filename);
							WizDocument doc = dbBag.getDocumentByGuid(fileguid);
							if (doc != null) {
								updateServerChanged(fileguid, dbBag);
							} else {
								updateServerChanged(fileguid, dbLife);
							}
							// 修改server_changed

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}

				}

	}

	public void initData() {
		String curUsername = null;
		// WizSystemSettings.setDefaultUserId(this,
		// SkyMBALoading.DEFAULT_USERID);//回头注掉
		personal_center.setText(R.string.personal_center);
		curUsername = WizAccountSettings.getUserId(this);
		acText.setText(curUsername);
		isLogin = AccountStatus.verifyLogin(this);
		if (isLogin) {
			// 登陆注册框置为gone,头像置为visible
			this.findViewById(R.id.logAndRegLinear).setVisibility(View.GONE);
			this.findViewById(R.id.photoRelative).setVisibility(View.VISIBLE);
			String username = modifyUsername(curUsername);
			usernameView.setText(username);

		}else{
			this.findViewById(R.id.logAndRegLinear).setVisibility(View.VISIBLE);
			this.findViewById(R.id.photoRelative).setVisibility(View.GONE);
			usernameView.setText("");
			
		}
		if (isLogin && WizMisc.isNetworkAvailable(this)) {
			initGrade();
		} else {
			// 从本地取数据
			int[] tagReadNum = new int[8];
			for (int j = 0; j < 8; j++) {
				tagReadNum[j] = setTagReadNum(tagIds[j]);

			}
			manageScoreView.setText(tagReadNum[0] + "");
			marketScoreView.setText(tagReadNum[2] + "");
			saleScoreView.setText(tagReadNum[4] + "");
			financeScoreView.setText(tagReadNum[6] + "");
			humanResourceScoreView.setText(tagReadNum[3] + "");
			exampleScoreView.setText(tagReadNum[7] + "");
			financialScoreView.setText(tagReadNum[5] + "");
			ventureScoreView.setText(tagReadNum[1] + "");

		}

		addListener();
	}

	public int setTagReadNum(String tagId) {
		final WizDatabase db = WizDatabase.getDb(this,
				SkyMBALoading.DEFAULT_USERID, SkyMBALoading.KBGUID_BAG);

		String sql = "SELECT COUNT(*) FROM WIZ_DOCUMENT WHERE READ = 1 AND DOCUMENT_TAG_GUIDS = '"
				+ tagId + "'";
		int num = db.sqlToInt(sql, 0, 0);
		return num;

	}

	private String modifyUsername(String curUsername) {
		String[] username = curUsername.split("@");
		return username[0];
	}

	private Dialog getWaitDialog(int stringId) {
		return WizWindow.createProgressDialog(this, stringId, true, false);

	}

	public void init() {
		dbBag = WizDatabase.getDb(SkyMBAPersonalCenter.this,
				SkyMBALoading.DEFAULT_USERID, SkyMBALoading.KBGUID_BAG);
		dbLife = WizDatabase.getDb(SkyMBAPersonalCenter.this,
				SkyMBALoading.DEFAULT_PASSWORD, SkyMBALoading.KBGUDI_LIFE);

		personal_center = (TextView) this.findViewById(R.id.personal_center);
		acText = (EditText) this.findViewById(R.id.edit_username_or_email);
		psText = (EditText) this.findViewById(R.id.edit_password);
		regBtn = (Button) this.findViewById(R.id.regBtn);
		loginBtn = (Button) this.findViewById(R.id.loginBtn);
		usernameView = (TextView) this.findViewById(R.id.username);
		logout = (ImageView) this.findViewById(R.id.logout);
		creditScoreView = (TextView) this.findViewById(R.id.number_credit);
		gradeView = (TextView) this.findViewById(R.id.number_level);
		manageScoreView = (TextView) this.findViewById(R.id.manageScore);
		marketScoreView = (TextView) this.findViewById(R.id.marketScore);
		saleScoreView = (TextView) this.findViewById(R.id.saleScore);
		financialScoreView = (TextView) this.findViewById(R.id.financialScore);
		financeScoreView = (TextView) this.findViewById(R.id.financeScore);
		humanResourceScoreView = (TextView) this.findViewById(R.id.hrScore);
		exampleScoreView = (TextView) this.findViewById(R.id.exampleScore);
		ventureScoreView = (TextView) this.findViewById(R.id.ventureScore);
		offlineButton = (ToggleButton) this
				.findViewById(R.id.expand_list_download);
		myFavoriteDetail = (ImageView) this
				.findViewById(R.id.expand_list_favor);
		deleteOfflinesView = (TextView) this.findViewById(R.id.delete_offline);
		deleteOfflinesImg = (ImageView) this.findViewById(R.id.clear);
		aboutImgDetail = (ImageView) this.findViewById(R.id.expand_list_about);

		lay_favor = (RelativeLayout) this.findViewById(R.id.lay_favor);
		lay_download = (RelativeLayout) this.findViewById(R.id.lay_download);
		lay_clear = (RelativeLayout) this.findViewById(R.id.lay_clear);
		lay_about = (RelativeLayout) this.findViewById(R.id.lay_about);
		lay_suggest = (RelativeLayout) this.findViewById(R.id.lay_suggest);
		// lay_mark = (RelativeLayout) this.findViewById(R.id.lay_mark);

		suggestFeedbackDetail = (ImageView) this
				.findViewById(R.id.expand_list_suggest);

		// markDetail = (ImageView) this.findViewById(R.id.expand_list_mark);

		String downloadType = WizSystemSettings.getGroupDownLoadDataType(this);
		if (downloadType.equals("1")) {
			offlineButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.offline_on));
			offlineButton.setChecked(true);
		} else {
			offlineButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.offline_off));
			offlineButton.setChecked(false);
		}

	}

	public void initGrade() {

		String mUserId = WizAccountSettings.getUserId(this);
		JsonURL jsonURL = new JsonURL("getuser", mUserId, null, null, null);
		url = jsonURL.urlGetUserInfo;

		// 网络连接获取数据：

		WizAsyncAction.startAsyncAction(null, new WizAction() {

			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {
				String result = null;
				DefaultHttpClient httpClient = new DefaultHttpClient();

				String urlFull = urlMain + url;
				HttpGet request = new HttpGet(urlFull);
				HttpResponse response;
				try {
					response = httpClient.execute(request);
					result = EntityUtils.toString(response.getEntity());
					Log.i("result", result);
					System.out.println("result：" + result);
					object = new JSONObject(result);

				} catch (ClientProtocolException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				} catch (JSONException e) {

					e.printStackTrace();
				}

				return null;
			}

			@Override
			public void onBegin(Object actionData) {

			}

			@Override
			public void onEnd(Object actionData, Object ret) {
				HashMap<Integer, String> map = new HashMap<Integer, String>();
				map = ProcessJson.doParseGetUserInfo(object);
				// 8个顺序，重新调
				creditScoreView.setText(map.get(1));
				gradeView.setText(map.get(2));
				manageScoreView.setText(map.get(3));
				marketScoreView.setText(map.get(4));
				saleScoreView.setText(map.get(5));
				financeScoreView.setText(map.get(6));
				humanResourceScoreView.setText(map.get(7));
				exampleScoreView.setText(map.get(8));
				financialScoreView.setText(map.get(9));
				ventureScoreView.setText(map.get(10));
				addListener();

			}

			@Override
			public void onException(Object actionData, Exception e) {

			}

			@Override
			public void onStatus(Object actionData, String status, int arg1,
					int arg2, Object obj) {

			}

		});

	}

	OnCheckedChangeListener listenerToggle = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {

			ToggleButton btn = (ToggleButton) buttonView;
			if (isChecked) {
				btn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.offline_on));
				WizSystemSettings.setGroupDownLoadDataType(
						SkyMBAPersonalCenter.this,
						WizSystemSettings.downloadTypeOfRecent);
				Toast.makeText(SkyMBAPersonalCenter.this, R.string.offline_on,
						Toast.LENGTH_SHORT).show();
			} else {
				btn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.offline_off));
				WizSystemSettings.setGroupDownLoadDataType(
						SkyMBAPersonalCenter.this,
						WizSystemSettings.downloadTypeOfNull);
				Toast.makeText(SkyMBAPersonalCenter.this, R.string.offline_off,
						Toast.LENGTH_SHORT).show();
				WizSystemSettings
						.getGroupDownLoadDataType(SkyMBAPersonalCenter.this);
			}

		}

	};

}
