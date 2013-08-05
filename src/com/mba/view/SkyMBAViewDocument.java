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

import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcFault;

import cn.mba.manageactivity.SysApplication;
import cn.wiz.sdk.WizWindow;
import cn.wiz.sdk.api.WizASXmlRpcServer;
import cn.wiz.sdk.api.WizAsyncAction;
import cn.wiz.sdk.api.WizDocumentAbstractCache;
import cn.wiz.sdk.api.WizKSXmlRpcServer;
import cn.wiz.sdk.api.WizAsyncAction.WizAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAsyncActionThread;
import cn.wiz.sdk.api.WizAsyncAction.WizAsyncThread;
import cn.wiz.sdk.api.WizObject.WizCert;
import cn.wiz.sdk.api.WizObject.WizDataStatus;
import cn.wiz.sdk.api.WizObject.WizDocument;
import cn.wiz.sdk.api.WizObject.WizKb;
import cn.wiz.sdk.db.WizDatabase;  

import cn.wiz.sdk.settings.WizAccountSettings;
import cn.wiz.sdk.settings.WizSystemSettings;
import cn.wiz.sdk.util.FileUtil;

import cn.wiz.sdk.util.WebViewUtil;
import cn.wiz.sdk.util.WizMisc;

import cn.wiz.sdk.util.ZipUtil;
import cn.wiz.sdk.util.WizMisc.WizInvalidPasswordException;

import com.example.skymba.R;
import com.mba.account.AccountStatus;
import com.mba.adapter.SkyMBADocumentsListAdapter;
import com.mba.json.JsonURL;
import com.mba.json.ProcessJson;
import com.mba.serilizable.SerialDocument;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ResourceAsColor")
public class SkyMBAViewDocument extends Activity implements OnGestureListener{
	private TextView catoView;
	private TextView titleView;
	private TextView keyword_01View;
	private TextView keyword_02View;
	private TextView keyword_03View;
	//private WebView mWebView;
	private static MyWebview mWebView;
	private ImageView favorImg;
	private ImageView favorImg2;
	private ImageView likeImg;
	private ImageView likeImg2;
	private TextView likeView;
	private ImageView pre_page;
	private ImageView next_page;
	private String mDocumentGUID;
	private String title;
	private String mKbGuid;
	private String mUserId;
	private String mTagId;
	private WizDocument mDocument;
	private boolean mDestroyed = false;
	private String mDocumentUrl;
	private String mCertPassword = "";
	private String mPassword;
	private String url = null;
	private String urlMain = "http://mba.trends-china.com/service.ashx?";
	private JSONObject object = null;
	private JSONObject objectReadId=null;
	private WizDatabase db;
	private String mCatoName = null;
	private LinearLayout lay_up;
	private LinearLayout lay_down;
	private boolean isFavorite=false;
	static final String SQL_UPDATE = "update WIZ_DOCUMENT SET";
    private JSONObject objectGetFavor=null;
    private JSONObject objectGetLike=null;
    private JSONObject objectGetCreditRead=null;
    private JSONObject objectSetLike=null;
    private int localLike=0;
    private int localFavored;
	private Handler handler;
	private Handler timeHandler;
	private double time;
	private Button read;
	boolean read_to_bottom=false;
	boolean added=false;
	boolean scroll_to_bottom=false;
	private ArrayList<String>guidlist;
	private int position;
	private int prepos;
	private int nextpos;
	private int docLength;
	private GestureDetector detector;
	private ScrollView scrollView;
	int index = 0; 
	private ArrayList<Integer> kbguidlist=null;
	private int readid;
	private ImageView creditRead;
	private long firstTime;
	private long clickTime;
	private RelativeLayout pBar;
	private boolean effectRead=false;
//	private HashMap<integer, Boolean> isS
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.document);
		SysApplication.getInstance().addActivity(this);
		creditRead=(ImageView)this.findViewById(R.id.read_to_bottom);
		detector=new GestureDetector(this);
		scrollView=(ScrollView)this.findViewById(R.id.scrollView1);
		pBar=(RelativeLayout)this.findViewById(R.id.nodocument_pBar);
		
		 
        scrollView.setOnTouchListener(new OnTouchListener() {  

            @Override  
            public boolean onTouch(View v, MotionEvent event) {  
                // TODO Auto-generated method stub  
  
                switch (event.getAction()) {  
                    case MotionEvent.ACTION_DOWN :  
 
                        break;  
                    case MotionEvent.ACTION_MOVE :  
                        index++;  
                        break;  
                    default :  
                        break;  
                }  
               if (event.getAction() == MotionEvent.ACTION_UP &&  index > 0) {  
                    index = 0;  
                    View view = ((ScrollView) v).getChildAt(0);  
                    if (view.getMeasuredHeight() <= v.getScrollY() + v.getHeight()) {  
                         System.out.println("底部"); 
                         scroll_to_bottom=true;
                    }  
                }  
               return false;  
           }  
        }); 

		mWebView=new MyWebview(this);
		iniWebView();
		LinearLayout lay=(LinearLayout)this.findViewById(R.id.lay_doc);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//		params.setMargins(0, 0, 0, 0);// 通过自定义坐标来放置你的控件
		lay.addView(mWebView,params);	
		catoView = (TextView) this.findViewById(R.id.catoName);
		titleView = (TextView) this.findViewById(R.id.title);
		keyword_01View = (TextView) this.findViewById(R.id.keyword_01);
		keyword_02View = (TextView) this.findViewById(R.id.keyword_02);
		keyword_03View = (TextView) this.findViewById(R.id.keyword_03);
		favorImg = (ImageView) this.findViewById(R.id.favorImg);// 未收藏
		favorImg2 = (ImageView) this.findViewById(R.id.favorImg2);// 已收藏
		likeImg = (ImageView) this.findViewById(R.id.likeImg);
		likeImg2 = (ImageView) this.findViewById(R.id.likeImg2);
		pre_page=(ImageView)this.findViewById(R.id.pre_page);
		next_page=(ImageView)this.findViewById(R.id.next_page);
		lay_up = (LinearLayout) this.findViewById(R.id.layout_up_catoName);
		lay_down = (LinearLayout) this.findViewById(R.id.lay_down);
		
		getIntentData();
		
		handler=new Handler();  
		startViewDocument();
		
	}
	
	


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
			 if(!effectRead){
				 SkyMBADocumentsListAdapter.scrollY.put(position, (double) scrollView.getScaleY());
			 }else{
				 SkyMBADocumentsListAdapter.scrollY.put(position-1, 0.0);
			 }
			setResult(RESULT_OK);
							
					}
		//返回键
	
		return super.onKeyDown(keyCode, event);
	}
	
	public class MyWebview extends WebView {

		public MyWebview(Context context) {
	        super(context);
	    }
	        
	    @SuppressLint("NewApi")
		@Override
	    public boolean onTouchEvent(MotionEvent event) {
	    	return detector.onTouchEvent(event);
	    }

	}
    
	Runnable   runnableUi=new  Runnable(){  
        @Override  
        public void run() {  
        	noNetworkImg();
        	
        }  
          
    }; 
    
    Runnable runnablePBar=new Runnable() {
		
		@Override
		public void run() {
			pBar.setVisibility(View.VISIBLE);
		}
	};



	public int dip2px(Context context, double d) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (d * scale + 0.5f);
	}
	OnClickListener fullScreenListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (lay_up.getVisibility() == View.VISIBLE) {//
				lay_up.setVisibility(View.GONE);
				lay_down.setVisibility(View.GONE);
			}

			else {

				lay_up.setVisibility(View.VISIBLE);
				lay_down.setVisibility(View.VISIBLE);
			}

		}

	};
	private static AlertDialog alertDialog;

	private void iniWebView() {

		mWebView.setWebViewClient(new WizViewDocumentWebViewClient());

		//
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setAllowFileAccess(true);
		if (WizSystemSettings.isAutoAdaptsScreen(this))
			webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		
		mWebView.addJavascriptInterface(this, "WIZSHELL");
		mWebView.addJavascriptInterface(this, "imgOnClick");
		
		
		WebViewUtil.setZoomControlGone(this, mWebView);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setSupportZoom(false);


	}

	private class WizViewDocumentWebViewClient extends WebViewClient {

		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			return super.shouldOverrideUrlLoading(view, url);
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			injectCss(mWebView, "file:///android_asset/mba.css");
			
        	
			super.onPageStarted(view, url, favicon);
		}
		@Override
	        public void onPageFinished(WebView view, String url) {
				preAddListener();
				mWebView.loadUrl("javascript:window.demo.getLength(document.body.innerText.length);");
				scrollView.scrollTo(0, 0);
				System.out.println("文章长度:"+docLength);
	        	firstTime=System.currentTimeMillis();//文章加载完的时间，记为开始阅读时间。
	        	super.onPageFinished(view, url);
	        }
		

		
	}
	public void injectCss(WebView webView ,String cssUrl){
		String scriptLine = "{ var e = document.createElement('link'); e.rel='stylesheet'; e.type='text/css'; e.href='%1'; document.head.appendChild(e); }";
		String line = scriptLine.replace("%1", cssUrl);
		String scriptText = "javascript: " + line;
		webView.loadUrl(scriptText);
	}
	public void injectJs(WebView webView, String jsUrl) {
		String scriptLine = "{ var e = document.createElement('script'); e.type='text/javascript'; e.charset='utf-8'; e.src='%1'; document.body.appendChild(e); }";
		String line = scriptLine.replace("%1", url);
		String scriptText = "javascript: " + line;
		webView.loadUrl(scriptText);
	}
	
	protected void updateLikeToLocal(int likeScore) {
		String sqlQuery = "SELECT LIKE_SCORE FROM WIZ_DOCUMENT WHERE DOCUMENT_GUID = "
			+ "'" + mDocumentGUID + "'";
	WizDatabase db = WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID,
			mKbGuid);
	int likeScoreLocal = db.sqlToInt(sqlQuery, 0, -1);// index
	if (likeScore!=likeScoreLocal) {
		String sqlUpdate="UPDATE WIZ_DOCUMENT SET LIKE_SCORE ="+likeScore+"  WHERE DOCUMENT_GUID = "
			+ "'" + mDocumentGUID + "'";
		db.execSql(sqlUpdate);
		
	}
			
	}

	public String[] getStrSeqs(String strSeq, String charSeq) {

		String strSeqs[] = strSeq.split(charSeq); // 暂用全角
		return strSeqs;
	}
	
	public void noNetworkImg(){
		pBar.setVisibility(View.GONE);
		favorImg.setVisibility(View.VISIBLE);
		likeImg.setVisibility(View.VISIBLE);
		if(mKbGuid.equals(SkyMBALoading.KBGUID_BAG)){
			creditRead.setVisibility(View.VISIBLE);
			creditRead.getDrawable().setLevel(1);
		}else{
		creditRead.setVisibility(View.GONE);
		}
		next_page.setVisibility(View.VISIBLE);
		mWebView.loadUrl("file:///android_asset/index.html");
//		Toast.makeText(SkyMBAViewDocument.this, R.string.no_network, Toast.LENGTH_SHORT).show();
	}

	void startViewDocument() {
		favorImg.setEnabled(false);
		likeImg.setEnabled(false);
		creditRead.setEnabled(false);
		favorImg.setVisibility(View.VISIBLE);
		likeImg.setVisibility(View.VISIBLE);
		if(mKbGuid.equals(SkyMBALoading.KBGUID_BAG)){
			creditRead.setVisibility(View.VISIBLE);
			creditRead.getDrawable().setLevel(1);
		}else{
		creditRead.setVisibility(View.GONE);
		}
		next_page.setVisibility(View.VISIBLE);
		db = WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID, mKbGuid);
		mDocument = db.getDocumentByGuid(mDocumentGUID);
		mCatoName=CatoAndKb.getCatoByTagid(mDocument.tagGUIDs);
		catoView.setText(mCatoName);
		WizAsyncAction.startAsyncAction(null, new WizAction() {

			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {

				thread.sendStatusMessage(null, STATUS_SHOW_POPUP_WINDOW, 0,
						null);
				//

				for (int i = 0; i < 2; i++) {
					if (WizDataStatus.DOWNLOADDATA == mDocument
							.getDocumentStatus(SkyMBAViewDocument.this,
									SkyMBALoading.DEFAULT_USERID)) {
						if(!WizMisc.isNetworkAvailable(SkyMBAViewDocument.this)){
//							noNetworkImg();
							handler.post(runnableUi); 
							
						}else{
							handler.post(runnablePBar);
							thread.sendStatusMessage(SkyMBAViewDocument.this
								.getString(R.string.downloading_note),
								STATUS_SET_STATUS_TEXT, 0, null);
						//
						downloadData(db, thread);
						}
					}
					//
					if (mDestroyed)
						return Boolean.valueOf(false);
					//
					if (WizDataStatus.DECRYPTIONDATA == mDocument
							.getDocumentStatus(SkyMBAViewDocument.this,
									SkyMBALoading.DEFAULT_USERID)) {
						thread.sendStatusMessage(SkyMBAViewDocument.this
								.getString(R.string.decrypting_note),
								STATUS_SET_STATUS_TEXT, 0, null);
						if (!decryptData(db, thread)) {
							thread.sendStatusMessage(SkyMBAViewDocument.this
									.getString(R.string.invalid_password),
									STATUS_SET_STATUS_TEXT, 0, null);
							return Boolean.valueOf(false);
						}
					} else {
						if (WizDataStatus.UNZIPDATA == mDocument
								.getDocumentStatus(SkyMBAViewDocument.this,
										SkyMBALoading.DEFAULT_USERID)) {
							thread.sendStatusMessage(SkyMBAViewDocument.this
									.getString(R.string.uncompressing_note),
									STATUS_SET_STATUS_TEXT, 0, null);
							if (!unzipData()) {
								String fileName = mDocument.getZipFileName(
										SkyMBAViewDocument.this,
										SkyMBALoading.DEFAULT_USERID);
								FileUtil.deleteFile(fileName);
								continue;
							}
						}
					}
					//

					//
					if (WizDataStatus.VIEWDATA != mDocument.getDocumentStatus(
							SkyMBAViewDocument.this,
							SkyMBALoading.DEFAULT_USERID)) {
//						throw new Exception("Can't init note data");
						throw new Exception(getString(R.string.no_network));
					}

					//
					String documentFile = mDocument
							.getNoteFileName(SkyMBAViewDocument.this);
					FileUtil.reSaveFileToUTF8(documentFile);
					// WizJSAction.injectViewNoteJS2Html(documentFile);

					//
					return Boolean.valueOf(true);
				}
				//
//				if (mDestroyed)
//					return Boolean.valueOf(false);
				//
//				throw new Exception(SkyMBAViewDocument.this
//						.getString(R.string.wiz_exception_download_part_error));
				return  Boolean.valueOf(true);
			}

			@Override
			public void onBegin(Object actionData) {
				if (mDestroyed)
					return;
				Log.i("begin", "begin");
			}

			@Override
			public void onEnd(Object actionData, Object ret) {
				if (mDestroyed)
					return;
				Log.i("onEnd", "onEnd");
				Boolean b = (Boolean) ret;
				if (b.booleanValue()) {
					viewDocument();
				}
			}

			@Override
			public void onException(Object actionData, Exception e) {
				Log.i("onException", "onException");

				if (mDestroyed)
					return;
				hideStatus();
				if (e instanceof XmlRpcException) {
					WizWindow.showMessage(SkyMBAViewDocument.this,
							getString(R.string.no_network));

				} else {
					WizWindow.showMessage(SkyMBAViewDocument.this,
							e.getMessage());
				}

			}

			//
			final int STATUS_SHOW_POPUP_WINDOW = 1;
			final int STATUS_HIDE_POPUP_WINDOW = 2;
			final int STATUS_SET_STATUS_TEXT = 3;
			final int STATUS_ENTER_CERT_PASSWORD = 5;
			final int STATUS_INVALID_PASSWORD = 11;

			@Override
			public void onStatus(Object actionData, String status, int arg1,
					int arg2, Object obj) {
				if (mDestroyed)
					return;
				//
				if (STATUS_SHOW_POPUP_WINDOW == arg1) {
					showStatus();
				} else if (STATUS_HIDE_POPUP_WINDOW == arg1) {
					hideStatus();
				} else if (STATUS_SET_STATUS_TEXT == arg1) {
					setStatusText(status);
				} else if (STATUS_ENTER_CERT_PASSWORD == arg1) {
					LayoutInflater factory = LayoutInflater
							.from(SkyMBAViewDocument.this);
					View view = factory.inflate(R.layout.wiz_aes_query_key,
							null);
					final EditText keyEdit = (EditText) view
							.findViewById(R.id.wizAesGetKeyDialogQueryKey);
					final CheckBox rememberBox = (CheckBox) view
							.findViewById(R.id.checkBoxRememberPassword);
					TextView hint = (TextView) view
							.findViewById(R.id.wizAesGetKeyDialogHint);
					String hintText = status;
					hintText = getString(R.string.wiz_aes_hint, status);
					hint.setText(hintText);
					//

					final Thread thread = (Thread) obj;
					//
					WizWindow
							.createAlertDialog(
									SkyMBAViewDocument.this,
									R.string.message_enter_password,
									view,
									new android.content.DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											mCertPassword = keyEdit.getText()
													.toString();
											if (rememberBox.isChecked()) {
												WizAccountSettings
														.setCertPassword(mCertPassword);
											} else {
												WizAccountSettings
														.setCertPassword("");
											}

											synchronized (thread) {
												thread.notify();
											}
										}
									},
									new android.content.DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											//
											mCertPassword = "";
											synchronized (thread) {
												thread.notify();
											}
										}
									}).show();
					return;
				} else if (STATUS_INVALID_PASSWORD == arg1) {
					WizWindow.showMessage(SkyMBAViewDocument.this,
							SkyMBAViewDocument.this
									.getString(R.string.invalid_password));
				}
			}

			//
			private void downloadData(WizDatabase db, WizAsyncThread thread)
					throws XmlRpcException, XmlRpcFault, IOException, Exception {
				//
				WizKb kb = db.getKbByGuid(mKbGuid);

  				String token = WizASXmlRpcServer.getToken(
						SkyMBAViewDocument.this, SkyMBALoading.DEFAULT_USERID,
						mPassword);
				if (TextUtils.isEmpty(token)){
					handler.post(runnableUi);  
					throw new Exception(getString(R.string.no_doc_located));
				}					
				boolean ret = false;	
				WizKSXmlRpcServer ks = new WizKSXmlRpcServer(
						SkyMBAViewDocument.this, kb.kbDatabaseUrl,
						SkyMBALoading.DEFAULT_USERID, token, kb.kbGuid);
				//
				db.onBeforeDownloadDocument(mDocument);
				File destFile = mDocument.getZipFile(SkyMBAViewDocument.this,
						SkyMBALoading.DEFAULT_USERID);
				ks.downloadDocument(mDocument.guid, destFile);// 出现exception
				db.onDocumentDownloaded(mDocument);

				WizDocumentAbstractCache.forceUpdateAbstract(
						SkyMBALoading.DEFAULT_USERID, mDocument.guid);
				//
				ret = true; 
			}

			//
			private int index = 0;

			// /
			private boolean decryptData(WizDatabase db, WizAsyncThread thread)
					throws Exception {
				//
				WizCert cert = WizDatabase.getDb(SkyMBAViewDocument.this,
						SkyMBALoading.DEFAULT_USERID, mKbGuid).getCert();
				//
				if (TextUtils.isEmpty(cert.e)
						|| TextUtils.isEmpty(cert.encryptedD)
						|| TextUtils.isEmpty(cert.n)) {

					WizASXmlRpcServer as = new WizASXmlRpcServer(
							SkyMBAViewDocument.this,
							SkyMBALoading.DEFAULT_USERID);
					cert = as.getCert(mPassword);
					db.saveCert(cert);
				}
				//
				//
				while (true) {
					if (TextUtils.isEmpty(mCertPassword)) {
						String hint = cert.hint;
						if (hint == null) {
							hint = "";
						}
						//
						synchronized (thread) {
							mCertPassword = "";
							if (index == 1) {
								index = 3;
								mCertPassword = "22";
							}
							//
							thread.sendStatusMessage(hint,
									STATUS_ENTER_CERT_PASSWORD, 0, thread);
							//
							index++;
							//
							try {
								thread.wait(1000 * 60 * 3); // 绛��3���
							} catch (Exception e) {
								return false;
							}
						}

						//
						if (TextUtils.isEmpty(mCertPassword)) {
							return false;
						}
					}
					//

					try {
						if (WizMisc.decryptAndUnzipDocument(
								SkyMBAViewDocument.this,
								SkyMBALoading.DEFAULT_USERID, mDocument,
								mCertPassword, cert))
							return true;
						//
						throw new Exception(
								SkyMBAViewDocument.this
										.getString(R.string.error_message_file_not_fount));
					} catch (WizInvalidPasswordException e) {
						thread.sendStatusMessage(null, STATUS_INVALID_PASSWORD,
								0, null); // retry
						//
						WizAccountSettings.setCertPassword("");
						mCertPassword = "";
					}
				}
			}

			private boolean unzipData() {
				//
				try {
					return ZipUtil.unZipData(mDocument.getZipFileName(
							SkyMBAViewDocument.this,
							SkyMBALoading.DEFAULT_USERID), mDocument
							.getNotePatth(SkyMBAViewDocument.this), "");
				} catch (Exception e) {
					return false;
				}
			}

			//
			private void showStatus() {
				// showLoading(true);
			}

			private void hideStatus() {
				// showLoading(false);
			}

			private void setStatusText(String text) {
				// setLoadingText(text);
			}

		});

	}


	public void preAddListener(){
		

		next_page.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(position==guidlist.size()){
        			Toast.makeText(SkyMBAViewDocument.this, R.string.last_document, Toast.LENGTH_SHORT).show();
        		}else{
        			String nextguid=guidlist.get(position);
        			if(isFavorite){
        				mKbGuid=CatoAndKb.getKbguidById(kbguidlist.get(position));
        			}
        			
        			position=position+1;
        			mDocumentGUID=nextguid;
        			        			
        			favorImg.setVisibility(View.VISIBLE);
        			favorImg2.setVisibility(View.GONE);
        			likeImg.setVisibility(View.VISIBLE);
        			likeImg2.setVisibility(View.GONE);
        			if(mKbGuid.equals(SkyMBALoading.KBGUID_BAG)){
        				creditRead.getDrawable().setLevel(1);
        				creditRead.setVisibility(View.VISIBLE);
        				
        			}else{
        				creditRead.setVisibility(View.GONE);
        			}
        			
        			startViewDocument();
        		}

			}
		});
		
	}
	public void showDialog(int choiceid,int ok,int cancle){
		  AlertDialog.Builder builder = new AlertDialog.Builder(SkyMBAViewDocument.this);  
		 alertDialog=builder.setMessage(choiceid)  
			       .setCancelable(false)  
			       .setPositiveButton(ok, new DialogInterface.OnClickListener() {  
			           public void onClick(DialogInterface dialog, int id) {  
			        	     dialog.cancel();
			        	     Intent intent=new Intent(SkyMBAViewDocument.this,SkyMBAPersonalCenter.class);
			        	     startActivity(intent);
			                SkyMBAViewDocument.this.finish();  
			           }  
			       })  
			       .setNegativeButton(cancle, new DialogInterface.OnClickListener() {  
			           public void onClick(DialogInterface dialog, int id) {  
			                dialog.cancel();  
			           }  
			       }).create();
		
		alertDialog.show();
		
		

	}

	

	public void addListener() {
		
		favorImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//联网并登陆 可以加
				//没联网，登陆，可以本地加
				//没联网，没登陆,提示当前无账号
				//联网，没登陆，提示当前没账号
				favorImg.setEnabled(false);
				boolean isConnect=WizMisc.isNetworkAvailable(SkyMBAViewDocument.this);
				boolean isAccount=false;
				if(AccountStatus.verifyLogin(SkyMBAViewDocument.this)){
					isAccount=true;
				}
				
				if(!AccountStatus.verifyReg(SkyMBAViewDocument.this)){
					showDialog(R.string.never_reg_no_favor,R.string.register_now,R.string.reg_later);
				}
				else if(!isAccount){
					showDialog(R.string.no_account_no_favor,R.string.login_now,R.string.login_later);
	                

				}else 
					//else 即为有账号
				{
					if(isConnect){
					addFavorToServer();
					}else if(!isConnect){
						addFavorToLocal();
					}
				}
				
				favorImg.setEnabled(true);
				
				

			}
		});
		favorImg2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//取消收藏
				favorImg2.setEnabled(false);
				boolean isConnect=WizMisc.isNetworkAvailable(SkyMBAViewDocument.this);
				boolean isAccount=false;
				if(WizAccountSettings.getUserId(SkyMBAViewDocument.this) != SkyMBALoading.DEFAULT_USERID
						&& WizAccountSettings.getUserId(SkyMBAViewDocument.this) != null&&!WizAccountSettings.getUserId(SkyMBAViewDocument.this).equals("")){
					isAccount=true;
				}
				if(!isAccount){
					showDialog(R.string.no_account_no_favor,R.string.login_now,R.string.login_later);
	                
				}else 
					//else 即为有账号
				{
					if(isConnect){
						cancleFavorToServer();
					}else if(!isConnect){
						 cancleFavorToLocal();
					}
				}
				favorImg2.setEnabled(true);
			

			}

			
		});

		likeImg.setOnClickListener(new OnClickListener() { 

			@Override
			public void onClick(View v) {
				likeImg.setEnabled(false);
				boolean isConnect=WizMisc.isNetworkAvailable(SkyMBAViewDocument.this);
				boolean isAccount=false;
				if(AccountStatus.verifyLogin(SkyMBAViewDocument.this)){
					isAccount=true;
				}
				 
				if(!AccountStatus.verifyReg(SkyMBAViewDocument.this)){
					showDialog(R.string.never_reg_no_like,R.string.register_now,R.string.reg_later);
	                
				}
				else if(!isAccount){
					showDialog(R.string.no_account_no_like,R.string.login_now,R.string.login_later);
	                
				}else 
					//else 即为有账号
				{
					if(isConnect){
						addToLike();
					}else {
						Toast.makeText(SkyMBAViewDocument.this, R.string.error_message_no_network, Toast.LENGTH_SHORT).show();
					}
				}
				likeImg.setEnabled(true);
				
				
			}
		});
		likeImg2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				likeImg2.setEnabled(false);
				Toast.makeText(SkyMBAViewDocument.this, R.string.have_liked, Toast.LENGTH_SHORT).show();
				likeImg2.setEnabled(true);
				
			}
		});
		
		creditRead.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				showCreditDialog(1);
				    creditRead.setEnabled(false);
				    if(creditRead.getDrawable().getLevel()==2){
				    	WizWindow.showMessage(SkyMBAViewDocument.this, R.string.credit_readed);
				    	creditRead.setEnabled(true);
				    }
				    else {
					boolean isConnect=WizMisc.isNetworkAvailable(SkyMBAViewDocument.this);
					boolean isAccount=false;
					if(AccountStatus.verifyLogin(SkyMBAViewDocument.this)){
						isAccount=true;
					}
					 
					if(!AccountStatus.verifyReg(SkyMBAViewDocument.this)){
						showDialog(R.string.never_account_no_credit_read,R.string.register_now,R.string.reg_later);
		                
					}
					else if(!isAccount){
						showDialog(R.string.no_account_no_credit_read,R.string.login_now,R.string.login_later);
		                
					}else 
						//else 即为有账号
					{
//						if(isConnect){
							clickTime=System.currentTimeMillis();
							if(!scroll_to_bottom){
				        		showToast(R.string.doc_not_end);
				        	}
							else {//读完
								//判断阅读速度
								int readVel=(int) ((clickTime-firstTime)/1000);
								if(readVel>ReadClasses.getVelLimit(readid)){
									showToast(R.string.time_not_enough);
								}else
									
									{
									if(isConnect){
								
										WizAsyncAction.startAsyncAction(null, new WizAction() {

			        					@Override
			        					public Object work(WizAsyncActionThread thread, Object actionData)
			        							throws Exception {
			        						String resultReadId = null;
			        						DefaultHttpClient httpClientReadId = new DefaultHttpClient();

			        						String urlFullReadId ="http://mba.trends-china.com/service.ashx?comm=addreadnew&ctype=1&userid="+mUserId+"&topicid="+mDocumentGUID+"&tagid="+mTagId+"&labid="+readid;
			        						HttpGet requestReadId = new HttpGet(urlFullReadId);
			        						HttpResponse responseReadId;
			        						try {
			        							responseReadId = httpClientReadId.execute(requestReadId);
			        							resultReadId = EntityUtils.toString(responseReadId.getEntity());
			        							objectReadId = new JSONObject(resultReadId);

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
			        						int success = 0;
			        						success = ProcessJson.doParseAddReadNew(objectReadId);
			        						switch(success){
			        						case 0:
			        						showToast(R.string.add_read_failed);break;
			        						case 1:
			        						creditRead.getDrawable().setLevel(2);
			        						creditRead.setEnabled(false);
			        						 showCreditDialog(ReadClasses.getCredit(readid));			        						 
			    	        				updateSql("CREDIT_READ",2,mDocumentGUID);
			    	        				break;
			    	        				case -2:
			    	        				showToast(R.string.add_read_again);break;
			        						case -1:
			        						showToast(R.string.no_network);break;
			        						}
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
								else {
									creditRead.getDrawable().setLevel(2);
		    						creditRead.setEnabled(false);
		    						showCreditDialog(ReadClasses.getCredit(readid));			        						 
			        				updateSql("CREDIT_READ",1,mDocumentGUID);
								
							}
									}
						}
					}
					creditRead.setEnabled(true);
					
				}}
			
		});
		
		
	}
	protected void cancleFavorToServer() {
		

		WizAsyncAction.startAsyncAction(null, new WizAction() {

			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {
				favorImg2.setEnabled(false);
				String result = null;
				DefaultHttpClient httpClient = new DefaultHttpClient();
				
				String urlFull = urlMain + "comm=delFavorite&ctype=1&userid="+mUserId+"&topicid="+mDocumentGUID;
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

				return null;
			}

			@Override
			public void onBegin(Object actionData) {

			}

			@Override
			public void onEnd(Object actionData, Object ret) {
				int success = 0; 
				success = ProcessJson.doParseDelFavorite(object);
				switch (success) {
				case 1:
					favorImg2.setVisibility(View.GONE);
					favorImg.setVisibility(View.VISIBLE);
			
//					Toast.makeText(SkyMBAViewDocument.this, R.string.cancle_favor_success, Toast.LENGTH_SHORT).show();			
					
					
					updateSql("FAVORITE", 0, mDocumentGUID);
					break;


				}
				favorImg2.setEnabled(true);
			}

			@Override
			public void onException(Object actionData, Exception e) {

				Toast.makeText(SkyMBAViewDocument.this, R.string.error_message_no_network, Toast.LENGTH_SHORT).show();	
			}

			@Override
			public void onStatus(Object actionData, String status, int arg1,
					int arg2, Object obj) {

			}

		});
	}

	void cancleFavorToLocal(){
		String sql = "UPDATE WIZ_DOCUMENT SET FAVORITE = 0  WHERE DOCUMENT_GUID = "
			+ "'" + mDocumentGUID + "'";
	WizDatabase db = WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID,
			mKbGuid);
	if(db.execSql(sql)){
		favorImg2.setVisibility(View.GONE);
		favorImg.setVisibility(View.VISIBLE);
//		Toast.makeText(this, R.string.cancle_local_favor_success, Toast.LENGTH_SHORT).show();
	}
	
	
	}
	public void addLikeOnLocal() {
		String sql = "SELECT LIKE FROM WIZ_DOCUMENT WHERE DOCUMENT_GUID = "
				+ "'" + mDocumentGUID + "'";
		WizDatabase db = WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID,
				mKbGuid);
		int favor = db.sqlToInt(sql, 0, -1);// index
		if (favor == 1) {
			
			Toast.makeText(this, R.string.have_liked, Toast.LENGTH_SHORT).show();
		} else {
			
			Toast.makeText(this, R.string.can_not_choose_to_like, Toast.LENGTH_SHORT).show();
		}
	}

	public void addFavorToLocal() {
		String sql = "SELECT FAVORITE FROM WIZ_DOCUMENT WHERE DOCUMENT_GUID = "
				+ "'" + mDocumentGUID + "'";
		WizDatabase db = WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID,
				mKbGuid);
		int favor = db.sqlToInt(sql, 0, -1);// index
		if (favor == 2) {
//			Toast.makeText(this, R.string.have_favorited, Toast.LENGTH_SHORT).show();
		//	WizWindow.showMessage(this, R.string.have_favorited);// 已收藏
		} else if (favor == 1) {
//			Toast.makeText(this, R.string.local_favored, Toast.LENGTH_SHORT).show();
			//WizWindow.showMessage(this, R.string.local_favored);// 本地已收藏
		} else {
			if (updateSql("FAVORITE", 1, mDocumentGUID)) {
				favorImg.setVisibility(View.GONE);
				favorImg2.setVisibility(View.VISIBLE);
//				Toast.makeText(this, R.string.favorite_success_local, Toast.LENGTH_SHORT).show();
			}
		}

	}
	

	public void addToLike() {
		JsonURL jsonURL = new JsonURL("addFavourable", mUserId, mDocumentGUID,
				null, null);
		url = jsonURL.urlAddLike;
		// 网络连接获取数据：

		WizAsyncAction.startAsyncAction(null, new WizAction() {

			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {
				likeImg.setEnabled(false);
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

				return null;
			}

			@Override
			public void onBegin(Object actionData) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onEnd(Object actionData, Object ret) {
				int success = 0;
				success = ProcessJson.doParseGetFavorite(object);
				switch (success) {
				case 1:
					likeImg.setVisibility(View.GONE);
					likeImg2.setVisibility(View.VISIBLE);
//					MyDialog dialog = new MyDialog(SkyMBAViewDocument.this,
//							R.string.like_success);		
//					Toast.makeText(SkyMBAViewDocument.this, R.string.like_success, Toast.LENGTH_SHORT).show();
					updateSql("LIKE", 1, mDocumentGUID);
					break;
				case 0:
//					MyDialog dialog2 = new MyDialog(SkyMBAViewDocument.this,
//							R.string.have_liked);
					Toast.makeText(SkyMBAViewDocument.this, R.string.have_liked, Toast.LENGTH_SHORT).show();					
					
					break;

				}
				likeImg.setEnabled(true);
			}

			@Override
			public void onException(Object actionData, Exception e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatus(Object actionData, String status, int arg1,
					int arg2, Object obj) {
				// TODO Auto-generated method stub

			}

		});

	}

	

	public void getIntentData() {
		Intent intent = this.getIntent();
		guidlist=intent.getStringArrayListExtra("guidlist");
		readid=intent.getIntExtra("readid", readid);
		SerialDocument doc=(SerialDocument) intent.getSerializableExtra("doc");
		position=doc.getPosition();
		mDocumentGUID=doc.getGuid();
		title=doc.getTitle();
		mKbGuid=doc.getKbGuid();
		mCatoName=doc.getmCatoName();
		mTagId=doc.getmTagId();
		isFavorite=doc.getFavorite();
		if(isFavorite){
			kbguidlist=intent.getIntegerArrayListExtra("kbguidlist");
		}
		mUserId = WizAccountSettings.getUserId(this);
		mPassword = SkyMBALoading.DEFAULT_PASSWORD;
		// 在document界面，不需要分类名

	}

	public void addFavorToServer() {

		JsonURL jsonURL = new JsonURL("addFavorite", mUserId, mDocumentGUID,
				null, null);
		url = jsonURL.urlAddFavorite;

		WizAsyncAction.startAsyncAction(null, new WizAction() {

			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {
				favorImg.setEnabled(false);
				favorImg2.setEnabled(false);
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

				return null;
			}

			@Override
			public void onBegin(Object actionData) {

			}

			@Override
			public void onEnd(Object actionData, Object ret) {
				int success = 0;
				success = ProcessJson.doParseGetFavorite(object);
				switch (success) {
				case 1:
					favorImg.setVisibility(View.GONE);
					favorImg2.setVisibility(View.VISIBLE);
					
//					Toast.makeText(SkyMBAViewDocument.this, R.string.favorite_success, Toast.LENGTH_SHORT).show();					
					
					
					updateSql("FAVORITE", 2, mDocumentGUID);
					break;
				case 0:
					Toast.makeText(SkyMBAViewDocument.this, R.string.have_favorited, Toast.LENGTH_SHORT).show();					
					break;

				}
				favorImg.setEnabled(true);
				favorImg2.setEnabled(true);
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

	public boolean updateSql(String index, int num, String guid) {
		String sql = "UPDATE WIZ_DOCUMENT SET " + index + " = " + num
				+ " WHERE DOCUMENT_GUID =" + " " + "'" + guid + "'";// 字符串型数据加''
		WizDatabase db = WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID,
				mKbGuid);
		
		db.execSql(sql);
		String sql2 = "SELECT FAVORITE FROM WIZ_DOCUMENT WHERE DOCUMENT_GUID = "
			+ "'" + guid + "'";
	
	int favor = db.sqlToInt(sql, 0, -1);// index
		
		return db.execSql(sql);

	}
	public void updateIsRead(){
		String sqlQuery = "SELECT READ FROM WIZ_DOCUMENT WHERE DOCUMENT_GUID = "
			+ "'" + mDocumentGUID + "'";
	WizDatabase db = WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID,
			mKbGuid);
	int read = db.sqlToInt(sqlQuery, 0, -1);// index
	if (read!=1) {
		String sqlUpdate="UPDATE WIZ_DOCUMENT SET READ = 1 WHERE DOCUMENT_GUID = "
			+ "'" + mDocumentGUID + "'";
		db.execSql(sqlUpdate);
		
	}
		
	}
	public void showToast(int resId){
	  Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}
	 @SuppressLint("ShowToast")
	private int localCreditRead;
	private void viewDocument() {
		pBar.setVisibility(View.GONE);
		favorImg.setEnabled(true);
		likeImg.setEnabled(true);
		creditRead.setEnabled(true);
		SkyMBADocumentsListAdapter.isSelected.put(position-1, true);
		addReadOnce();
		boolean isLogin=AccountStatus.verifyLogin(SkyMBAViewDocument.this);
		String documentFile;
		try {
			documentFile = mDocument.getNoteFileName(this);
			mDocumentUrl = "file://" + documentFile;
			WebSettings settings=mWebView.getSettings();
			settings.setJavaScriptEnabled(true);
			mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
			mWebView.loadUrl(mDocumentUrl);
		} catch (Exception e) {
			pBar.setVisibility(View.GONE);
			WizWindow.showException(this, e);
		}
	
		if (WizMisc.isNetworkAvailable(this)) {
			if(isFavorite==true){
				favorImg2.setVisibility(View.VISIBLE);
				favorImg.setVisibility(View.GONE);
				
			}else{
				if(isLogin){
					
					isLocalFavored(mDocumentGUID);
					isFavored();
				}
				else {
					favorImg.setVisibility(View.VISIBLE);
					favorImg2.setVisibility(View.GONE);
				}
				
			}
			if(isLogin){
				isLocalLiked(mDocumentGUID);
				isLiked();
			}
			else{
				likeImg.setVisibility(View.VISIBLE);
				likeImg2.setVisibility(View.GONE);
			}
			if(mKbGuid.equals(SkyMBALoading.KBGUID_BAG)){
				creditRead.getDrawable().setLevel(1);
				creditRead.setVisibility(View.VISIBLE);
				if(isLogin){
					
					isLocalCreditReaded(mDocumentGUID);
					isCreditReaded();//缺少一个接口
			}
			 
			
			}else {
				creditRead.setVisibility(View.GONE);
			}
			
			
			//isLiked();是否好评的接口
			
			

		} else {//未联网
			
			if(isFavorite==true){
				favorImg2.setVisibility(View.VISIBLE);
				favorImg.setVisibility(View.GONE);
				
			}else {
				
				if(isLogin){
				isLocalFavored(mDocumentGUID);
							}
				else {
					favorImg.setVisibility(View.VISIBLE);
					favorImg2.setVisibility(View.GONE);
							}
			}	
			if(isLogin){
				isLocalLiked(mDocumentGUID);
//				setLocalLike();
				
			}else{
				likeImg.setVisibility(View.VISIBLE);
				likeImg2.setVisibility(View.GONE);
//				likeView.setText("0");
			}
			if(mKbGuid.equals(SkyMBALoading.KBGUID_BAG)){
				if(isLogin){
					isLocalCreditReaded(mDocumentGUID);
			}
			
					creditRead.setVisibility(View.VISIBLE);
				
			}
			else {
				creditRead.setVisibility(View.GONE);
			}
						
		}
		next_page.setVisibility(View.VISIBLE);
		addListener();
		updateIsRead();

	}
	


	private void setLocalLike() {
    	String sqlQuery = "SELECT LIKE_SCORE FROM WIZ_DOCUMENT WHERE DOCUMENT_GUID = "
			+ "'" + mDocumentGUID + "'";
	WizDatabase db = WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID,
			mKbGuid);
	int likeScore = db.sqlToInt(sqlQuery, 0, -1);// index
	if (likeScore!=-1) {
//		likeView.setText(""+likeScore);
		}
	
	}

	void  isLiked(){

		// 网络连接获取数据：

		WizAsyncAction.startAsyncAction(null, new WizAction() {

			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {
				String resultGetLike = null;
				DefaultHttpClient httpClient = new DefaultHttpClient();
				
				String urlGetLike = urlMain +"comm=gettopicfavourable&ctype=1&userid=" + mUserId + "&topicid="
				+ mDocumentGUID ;
				HttpGet requestGetLike = new HttpGet(urlGetLike);
				HttpResponse responseGetLike;
				try {
					responseGetLike = httpClient.execute(requestGetLike);
					resultGetLike = EntityUtils.toString(responseGetLike.getEntity());
					objectGetLike = new JSONObject(resultGetLike);

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
				int like = ProcessJson.doIsLiked(objectGetLike);
				
				switch (like) {
				case 1:
					likeImg2.setVisibility(View.VISIBLE);
					likeImg.setVisibility(View.GONE);
					
					break;
				case 0:
					likeImg.setVisibility(View.VISIBLE);
					break;
				case -1:
					likeImg.setVisibility(View.VISIBLE);
					break;
				}
				if(like!=localLike&&like!=-1){			
					updateSql("LIKE",like,mDocumentGUID);
				}
				

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
	void isCreditReaded(){
		WizAsyncAction.startAsyncAction(null, new WizAction() {

			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {
				String resultGetCreditRead = null;
				DefaultHttpClient httpClient = new DefaultHttpClient();
				
				String urlGetCreditRead = urlMain +"comm=getreadnew&ctype=1&topicid=" + mDocumentGUID + "&userid="
				+ mUserId ;
				HttpGet requestGetCreditRead = new HttpGet(urlGetCreditRead);
				HttpResponse responseGetCreditRead;
				try {
					responseGetCreditRead = httpClient.execute(requestGetCreditRead);
					resultGetCreditRead = EntityUtils.toString(responseGetCreditRead.getEntity());
					objectGetCreditRead = new JSONObject(resultGetCreditRead);

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
				int creditRead = ProcessJson.doSubmitCreditRead(objectGetCreditRead);
				if(creditRead==1)	{
	
					SkyMBAViewDocument.this.creditRead.getDrawable().setLevel(2);
					updateSql("CREDIT_READ",creditRead,mDocumentGUID);
				}else{
					SkyMBAViewDocument.this.creditRead.getDrawable().setLevel(1);
					SkyMBAViewDocument.this.creditRead.setEnabled(true);
				}
					
				
				

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
	void isFavored() {

		
		// 网络连接获取数据：

		WizAsyncAction.startAsyncAction(null, new WizAction() {

			

			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {
				String resultGetFavor = null;
				DefaultHttpClient httpClient = new DefaultHttpClient();
			
				String urlGetFavor = urlMain +"comm=gettopicfavorite&ctype=1&userid=" + mUserId + "&topicid="
				+ mDocumentGUID ;
				HttpGet requestGetFavor = new HttpGet(urlGetFavor);
				HttpResponse responseGetFavor;
				try {
					responseGetFavor = httpClient.execute(requestGetFavor);
					resultGetFavor = EntityUtils.toString(responseGetFavor.getEntity());					
					objectGetFavor = new JSONObject(resultGetFavor);

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
				int favored = ProcessJson.doIsFavored(objectGetFavor);
				switch (favored) {
				case 1:
					favorImg.setVisibility(View.GONE);
					favorImg2.setVisibility(View.VISIBLE);
					break;
				case 0:
					favorImg.setVisibility(View.VISIBLE);
					break;
				case -1:
					favorImg.setVisibility(View.VISIBLE);
					break;
				}
				if(favored!=localFavored&&favored!=-1){
					updateSql("FAVORITE",favored,mDocumentGUID);
				}
				

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

	void isLocalFavored(String guid) {
		String sql = "SELECT FAVORITE FROM WIZ_DOCUMENT WHERE DOCUMENT_GUID = "
				+ "'" + mDocumentGUID + "'";
		WizDatabase db = WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID,
				mKbGuid);
		localFavored = db.sqlToInt(sql, 0, -1);// index
		if (localFavored == 1 || localFavored == 2) {
			favorImg2.setVisibility(View.VISIBLE);
			favorImg.setVisibility(View.GONE);
		} else {
			favorImg.setVisibility(View.VISIBLE);
		}

	}
	void isLocalLiked(String guid){
		String sql = "SELECT LIKE FROM WIZ_DOCUMENT WHERE DOCUMENT_GUID = "
			+ "'" + mDocumentGUID + "'";
	WizDatabase db = WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID,
			mKbGuid);
	localLike = db.sqlToInt(sql, 0, -1);// index
	if (localLike == 1) {
		likeImg2.setVisibility(View.VISIBLE);
		likeImg.setVisibility(View.GONE);
	} else {
		likeImg.setVisibility(View.VISIBLE);
	}
		
	}
	
	void isLocalCreditReaded(String guid){
		String sql = "SELECT CREDIT_READ FROM WIZ_DOCUMENT WHERE DOCUMENT_GUID = "
			+ "'" + mDocumentGUID + "'";
		WizDatabase db = WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID,
				mKbGuid);
		localCreditRead = db.sqlToInt(sql, 0, -1);// index
		if (localCreditRead == 1||localCreditRead == 2) {
			creditRead.getDrawable().setLevel(2);
		} 	
	}

	

	public void addReadOnce() {

		// 网络连接获取数据：

		WizAsyncAction.startAsyncAction(null, new WizAction() {

			private HttpResponse responseAddRead;

			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {
				String resultAddRead = null;
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String urlAddRead =urlMain+"comm=addRead&ctype=1&userid="+mUserId+"&topicid="+mDocumentGUID+"&tagid="+mTagId;
				HttpGet requestAddRead = new HttpGet(urlAddRead);
				try {
					responseAddRead = httpClient.execute(requestAddRead);
					resultAddRead = EntityUtils.toString(responseAddRead.getEntity());
					System.out.println("result="+resultAddRead);
				}
				
				catch (ClientProtocolException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

				return null;
			}

			@Override
			public void onBegin(Object actionData) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onEnd(Object actionData, Object ret) {

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
	
	  final class DemoJavaScriptInterface {
		  DemoJavaScriptInterface(){
		  }
		  public void getLength(int length){
			 docLength=length;
		  }
		  
	  }
	  
	  public void showCreditDialog(int credit){
		//获取LayoutInflater对象，该对象能把XML文件转换为与之一直的View对象
		    effectRead=true;
			LayoutInflater inflater = getLayoutInflater();
			//根据指定的布局文件创建一个具有层级关系的View对象
			//第二个参数为View对象的根节点，即LinearLayout的ID
			View layout = inflater.inflate(R.layout.credit_toast, (ViewGroup) findViewById(R.id.layout_toast_root));
			TextView text = (TextView) layout.findViewById(R.id.credit_up);
			text.setText(""+credit);

			AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SkyMBAViewDocument.this, R.style.dialog)); 
			AlertDialog alertDialogCredit=builder.create();
			alertDialogCredit.setView(layout, 0, 0, 0, 0);
			alertDialogCredit.show();
			TimeCount timer = new TimeCount(2000, 2000,alertDialogCredit);//具体时间自定
			timer.start();

	  }
	  public static class TimeCount extends CountDownTimer 
		{    
		  private AlertDialog alert;
		    public TimeCount(long millisInFuture, long countDownInterval,AlertDialog alert) 
		    {
		        super(millisInFuture, countDownInterval);
		        this.alert=alert;
		    }
		    
		    @Override
		    public void onFinish() {
		    	alert.dismiss();//alertDialog是你的对话框
		    }
			@Override
			public void onTick(long millisUntilFinished) {
				
			}
		}
	

	@Override
	public boolean onDown(MotionEvent e) {

		System.out.println("onDown");
		return false;
	}


	@Override
	public void onShowPress(MotionEvent e) {
		System.out.println("onShowPress");
	}


	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		System.out.println("onSingleTapUp");
		return false;
	}


	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		System.out.println("scrolling");

			
		
		return false;
	}


	@Override
	public void onLongPress(MotionEvent e) {
		System.out.println("onLongPress");
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
				return false;
	}

}
