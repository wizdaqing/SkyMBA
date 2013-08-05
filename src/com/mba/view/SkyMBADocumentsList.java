package com.mba.view;

import java.net.MalformedURLException;
import java.util.ArrayList;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcFault;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.mba.manageactivity.SysApplication;
import cn.wiz.sdk.api.WizAsyncAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAsyncActionThread;
import cn.wiz.sdk.api.WizEventsCenter;
import cn.wiz.sdk.api.WizEventsCenter.WizDatabaseEventsListener;
import cn.wiz.sdk.api.WizEventsCenter.WizDatabaseObjectType;
import cn.wiz.sdk.api.WizEventsCenter.WizSyncKbEventsListener;
import cn.wiz.sdk.api.WizEventsCenter.WizSyncKbStep;
import cn.wiz.sdk.api.WizObject.WizDocument;
import cn.wiz.sdk.api.WizObject.WizKb;
import cn.wiz.sdk.api.WizObject.WizObjectBase;
import cn.wiz.sdk.api.WizASXmlRpcServer;
import cn.wiz.sdk.api.WizObject;
import cn.wiz.sdk.db.WizDatabase;
import cn.wiz.sdk.settings.WizAccountSettings;
import cn.wiz.sdk.util.WizMisc;

import com.example.skymba.R;
import com.mba.adapter.SkyMBADocumentsListAdapter.ViewHolder;
import com.mba.adapter.SkyMBADocumentsListAdapter;
import com.mba.pullrefresh.PullToRefreshListView;
import com.mba.pullrefresh.PullToRefreshListView.OnRefreshListener;
import com.mba.serilizable.SerialDocument;

public class SkyMBADocumentsList extends Activity implements
		WizSyncKbEventsListener {
	private TextView catoName;
	private ImageView personalCenter;
	private PullToRefreshListView refreshList;
	private String mKbGuid = null;
	private String mTagId = null;
	private SkyMBADocumentsListAdapter mAdapter;
	private String mTagName = null;
	final int GOTO_READ_DOC = 100;
	private boolean isFavorite = false;
	private WizSyncKb syncKB;
	private String token = null;
	private int syncState = 0;
	private RelativeLayout pBarLayout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.document_list);
		SysApplication.getInstance().addActivity(this);
		WizEventsCenter.addKbListener(this);
		init();

	}

	@Override
	protected void onDestroy() {
		WizEventsCenter.removeKbListener(this);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
			if (mKbGuid.equals(SkyMBALoading.KBGUID_BAG)) {
				setResult(RESULT_OK);
			} else {
				setResult(RESULT_CANCELED);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	static private <T> long getMaxVersion(ArrayList<T> arr,
			long currentMaxVersion) {
		long ret = currentMaxVersion;
		for (T elem : arr) {
			WizObjectBase obj = (WizObjectBase) elem;
			ret = Math.max(ret, obj.version);
		}
		//
		return ret;
	}

	public int doSync() throws Exception {
		// 1.先拿到token,必须先登录
		token = WizASXmlRpcServer.getToken(this, SkyMBALoading.DEFAULT_USERID,
				SkyMBALoading.DEFAULT_PASSWORD);
		if (TextUtils.isEmpty(token)) {
			return -1;
		}
		// 2.new一个WizKb,通过mKbGuid
		WizDatabase db = WizDatabase.getDb(this, SkyMBALoading.DEFAULT_USERID,
				mKbGuid);
		WizKb kb = db.getKbByGuid(mKbGuid);

		// 2.new一个WizSyncKb
		try {
			syncKB = new WizSyncKb(this, SkyMBALoading.DEFAULT_USERID, token,
					kb, false);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			syncKB.mKbServerVersion = syncKB.mServer.getVersions();
		} catch (XmlRpcException e1) {
			e1.printStackTrace();
		} catch (XmlRpcFault e1) {
			e1.printStackTrace();
		}
		if (syncKB.mKbLocalVersion.documentVersion >= syncKB.mKbServerVersion.documentVersion) {

			return 0;
		}

		long startVersion = syncKB.mKbLocalVersion.documentVersion;
		int count = 10;

		while (true) {
			ArrayList<WizDocument> documents = null;
			try {
				documents = syncKB.mServer
						.getDocuments(startVersion + 1, count);
				if (documents.size() > 0) {
					WizEventsCenter
							.sendSyncStatusMessage(documents.get(0).title);
					syncKB.mDatabase.saveServerDocuments(documents);
					startVersion = getMaxVersion(documents, startVersion);
					syncKB.mDatabase.setDocumentsVersion(startVersion);

					// 尽早通知笔记列表下载，更新界面，例如每下载50个就通知一次
					WizEventsCenter.sendSyncKbStepMessage(syncKB.mKb,
							WizSyncKbStep.AfterDownloadDocuments);
					return 1;
				} else {
					syncKB.mDatabase
							.setDocumentsVersion(syncKB.mKbServerVersion.documentVersion);
				}

			} catch (XmlRpcException e) {
				e.printStackTrace();
			} catch (XmlRpcFault e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mAdapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void addListener() {
		refreshList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ViewHolder viewHolder = (ViewHolder) view.getTag();
				viewHolder.docTitle.setTextColor(Color.argb(255, 153, 153, 153));
				viewHolder.docAbstract.setTextColor(Color.argb(255, 153, 153,
						153));
//				mAdapter.isSelected.put(position-1, true);
				String mCatoName = (String) catoName.getText();
				Intent intent = new Intent();
				intent.setClass(SkyMBADocumentsList.this,
						SkyMBAViewDocument.class);
				WizObject.WizDocument document = (WizObject.WizDocument) refreshList
						.getAdapter().getItem(position);
				@SuppressWarnings("unchecked")
				ArrayList<String> guidlist = mAdapter.getGuidList();
				SerialDocument doc = new SerialDocument(position,
						document.guid, document.title, mKbGuid, mCatoName,
						mTagId, isFavorite);
				int readid = mAdapter.getReadClass(position - 1);
				intent.putExtra("readid", readid);
				intent.putExtra("doc", doc);
				intent.putStringArrayListExtra("guidlist", guidlist);

				 SkyMBADocumentsList.this.startActivityForResult(intent, 100);

			}
		});

		personalCenter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SkyMBADocumentsList.this,
						SkyMBAPersonalCenter.class);
				startActivity(intent);
				SkyMBADocumentsList.this.finish();

			}
		});

	}

	public void init() {
		catoName = (TextView) this.findViewById(R.id.catoName);
		personalCenter = (ImageView) this.findViewById(R.id.personalCenterImg);
		pBarLayout = (RelativeLayout) this.findViewById(R.id.pBarLayout);
		refreshList = (PullToRefreshListView) this
				.findViewById(R.id.listViewOfDocs);
		refreshList.setVisibility(View.VISIBLE);

		refreshList.setSelector(R.drawable.listview_selector);
		refreshList.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {

				WizAsyncAction.startAsyncAction(null, new WizAction() {

					@Override
					public Object work(WizAsyncActionThread thread,
							Object actionData) throws Exception {
						syncState = doSync();
						return null;
					}

					@Override
					public void onBegin(Object actionData) {

					}

					@Override
					public void onEnd(Object actionData, Object ret) {
						refreshList.onRefreshComplete();
						if (syncState == 1) {
							mAdapter.refreshListView();
						} else if (syncState == -1) {
							Toast.makeText(SkyMBADocumentsList.this,
									R.string.no_network, Toast.LENGTH_SHORT)
									.show();
						}
						
					}

					@Override
					public void onException(Object actionData, Exception e) {

					}

					@Override
					public void onStatus(Object actionData, String status,
							int arg1, int arg2, Object obj) {

					}
				});
			}
		});
		Intent intent = getIntent();
		mKbGuid = intent.getStringExtra("mKbGuid");

		mTagId = intent.getStringExtra("mTagId");
		mTagName = intent.getStringExtra("mTagName");
		catoName.setText(mTagName);

		if (mAdapter == null) {

			mAdapter = new SkyMBADocumentsListAdapter(this,
					WizAccountSettings.getUserId(this), mKbGuid, mTagId);
		}

		refreshList.setAdapter(mAdapter);
		addListener();
		if (mAdapter.getCount() <= 0
				&& WizMisc.isNetworkAvailable(SkyMBADocumentsList.this)) {
			pBarLayout.setVisibility(View.VISIBLE);
		}
		if (mAdapter.getCount() < 5
				&& WizMisc.isNetworkAvailable(SkyMBADocumentsList.this)) {
			// pBar.setVisibility(View.VISIBLE);
			WizAsyncAction.startAsyncAction(null, new WizAction() {

				@Override
				public Object work(WizAsyncActionThread thread,
						Object actionData) throws Exception {
					syncState = doSync();
					return null;
				}

				@Override
				public void onBegin(Object actionData) {

				}

				@Override
				public void onEnd(Object actionData, Object ret) {
					if (syncState == 1) {
						mAdapter.refreshListView();
					} else if (syncState == -1) {
						Toast.makeText(SkyMBADocumentsList.this,
								R.string.no_network, Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onException(Object actionData, Exception e) {

				}

				@Override
				public void onStatus(Object actionData, String status,
						int arg1, int arg2, Object obj) {

				}
			});
		}

	}

	@Override
	public void onSyncKbBegin(WizKb kb, boolean uploadOnly) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSyncKbEnd(WizKb kb, boolean uploadOnly, boolean succeeded) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSyncKbProgress(WizKb kb, int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSyncKbStep(WizKb kb, WizSyncKbStep step) {
		if (pBarLayout.getVisibility() == View.VISIBLE) {

			pBarLayout.setVisibility(View.GONE);
		}
		mAdapter.syncRefresh();
	}
}