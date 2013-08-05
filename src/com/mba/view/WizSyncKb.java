package com.mba.view;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcFault;

import android.content.Context;
import cn.wiz.sdk.api.WizEventsCenter;
import cn.wiz.sdk.api.WizKSXmlRpcServer;
import cn.wiz.sdk.api.WizSync;
import cn.wiz.sdk.api.WizEventsCenter.WizSyncKbStep;
import cn.wiz.sdk.api.WizObject.WizDocument;
import cn.wiz.sdk.api.WizObject.WizKb;
import cn.wiz.sdk.api.WizObject.WizKbVersion;
import cn.wiz.sdk.api.WizStrings.WizStringId;
import cn.wiz.sdk.api.WizXmlRpcServer.WizKeyValue;
import cn.wiz.sdk.db.WizDatabase;

public class WizSyncKb {

	public Context mContext;
	public String mUserId;
	public WizKb mKb;
	public WizKbVersion mKbServerVersion;
	public WizKbVersion mKbLocalVersion;
	public WizKSXmlRpcServer mServer;
	public boolean mUploadOnly;
	public WizDatabase mDatabase;
	public HashMap<String, WizKeyValue> mOldKeyValues;

	public WizSyncKb(Context ctx, String userId, String token, WizKb kb,
			boolean uploadOnly) throws MalformedURLException {
		mContext = ctx;
		mUserId = userId;
		mKb = kb;
		mServer = new WizKSXmlRpcServer(ctx, kb.kbDatabaseUrl, userId,
				token, kb.kbGuid);
		mUploadOnly = uploadOnly;
		mDatabase = WizDatabase.getDb(ctx, userId, kb.isPersonalKb() ? null
				: kb.kbGuid);
		mKbLocalVersion = mDatabase.getVersions();
	}
	
	
	
}
