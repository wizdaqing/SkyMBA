package com.mba.adapter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import cn.wiz.sdk.api.WizASXmlRpcServer;
import cn.wiz.sdk.api.WizAsyncAction;
import cn.wiz.sdk.api.WizObject;
import cn.wiz.sdk.api.WizAsyncAction.WizAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAsyncActionThread;
import cn.wiz.sdk.api.WizObject.WizDeletedGUID;
import cn.wiz.sdk.api.WizObject.WizDocument;
import cn.wiz.sdk.api.WizObject.WizKb;
import cn.wiz.sdk.db.WizDatabase;
import cn.wiz.sdk.settings.WizAccountSettings;
import cn.wiz.sdk.util.WizMisc;
import cn.wiz.sdk.util.WizMisc.MD5Util;


import com.example.skymba.R;
import com.mba.account.AccountStatus;
import com.mba.json.JsonURL;
import com.mba.json.ProcessJson;

import com.mba.view.ReadClasses;
import com.mba.view.SkyMBALoading;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * @author Z-L
 *
 */
public class SkyMBADocumentsListAdapter extends BaseAdapter {
	private ArrayList<WizDocument> listDocs = new ArrayList<WizDocument>();//必须初始化！！
	public static HashMap<Integer, Boolean> isSelected;
	public static HashMap<Integer, Double> scrollY;
	private Context context;
	private String tagId;
	private LayoutInflater inflater;
	private WizDatabase wizDatabase;
	private String url;
	private String urlMain = "http://mba.trends-china.com/service.ashx?";
	private JSONObject object = null;
	private JSONObject objectLike=null;
	private String mUserId;
    private String mKbGuid;
    private ArrayList<String> listReadOnes=new ArrayList<String>();
    private ArrayList<HashMap<String, String>> listLikeNums=new ArrayList<HashMap<String, String>>();
    private ArrayList<String> listLocalLikes=new ArrayList<String>();
    private int[] readClasses;
    private int size=0;
    private int likesize=0;
    String topicids=null;
    

	private int[] areRead=null;
	@Override
	public int getCount() {
			int number=listDocs.size();
			
			return number;	
			

	}
    
	@Override
	public Object getItem(int position) {
		
			return listDocs.get(position);
		
	}
	@Override
	public long getItemId(int position) {

		return position;
	}
	public int getReadClass(int position){
		return readClasses[position];
	}
	
	
	public ArrayList getGuidList(){
		ArrayList list=new ArrayList();
		for(int j=0;j<listDocs.size();j++){
		   WizDocument doc=listDocs.get(j);
		   String guid=doc.guid;
		   list.add(guid);
		}
		return list;
	}
	public void refreshListView(){
		listDocs.clear();
		wizDatabase = WizDatabase.getDb(context, SkyMBALoading.DEFAULT_USERID, mKbGuid);
		listDocs = wizDatabase.getDocumentsByTag(tagId);
		this.notifyDataSetChanged();
	}
	
   public void syncRefresh(){
	   wizDatabase = WizDatabase.getDb(context, SkyMBALoading.DEFAULT_USERID, mKbGuid);
	   listDocs = wizDatabase.getDocumentsByTag(tagId);
	   size=listDocs.size();
	  if(size!=0){
		readClasses=new int [size];
	   for(int j=0;j<size;j++){
			isSelected.put(j, false);
			scrollY.put(j,0.0);
		}	
		}
		setAllLike();
		
   }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
//		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.document_list_item, null);
			viewHolder.docTitle = (TextView) convertView
					.findViewById(R.id.title);
			viewHolder.docAbstract = (TextView) convertView
					.findViewById(R.id.abs);
			viewHolder.readClass=(ImageView)convertView.findViewById(R.id.readClassImg);
			viewHolder.keywordOne = (TextView) convertView
					.findViewById(R.id.keyword_01);
			viewHolder.keywordTwo = (TextView) convertView
					.findViewById(R.id.keyword_02);
			viewHolder.keywordThree = (TextView) convertView
					.findViewById(R.id.keyword_03);
			viewHolder.likeNum=(TextView)convertView.findViewById(R.id.like_number);
			convertView.setTag(viewHolder);
//			convertView.setTag(position);

//		} else {
			viewHolder = (ViewHolder) convertView.getTag();
//		}
		Object obj = listDocs.get(position);
		if (obj == null)
			return null;

		WizDocument curDocument = (WizDocument) obj;
		setData(viewHolder, curDocument,position);
		
		viewHolder.likeNum.setText(getLikeNum(listLikeNums, curDocument.guid));

		return convertView;
	}
	
	public void setData(ViewHolder viewHolder, WizDocument curDocument,int position) {
		String guid=curDocument.guid;
		
		String[] title = getStrSeqs(curDocument.title, "@");
		int lengthTitle = title.length;
		
		if (title != null) {
			if (lengthTitle >= 1) {
				viewHolder.docTitle.setText(title[0]);
				
				if(getItemViewType(position)==1||isSelected.get(position)){
					viewHolder.docTitle.setTextColor(Color.argb(255,153,153,153));
					viewHolder.docAbstract.setTextColor(Color.argb(255,153,153,153));
				}
			}
			if (lengthTitle >= 2) {
				viewHolder.docAbstract.setText(title [1]);
			}
			if(lengthTitle>=3){
				int readId=Integer.parseInt(title[2]);
				setReadId(position,readId);
			    viewHolder.readClass.getDrawable().setLevel(readId);	
			}
		}

		String[] keywords = getStrSeqs(curDocument.keywords, "；");
		
		
		if (!keywords[0].equals("")) {
			//去除空串
			String keywordsReal[]=new String[7];
			int i=0;
			for(int j=0;j<keywords.length;j++){
				String word=keywords[j];
				if(!word.equals("")){
					keywordsReal[i++]=word;
				}
			}
			
			int lengthKeywords = i;
			if (lengthKeywords >= 1) {
				viewHolder.keywordOne.setVisibility(View.VISIBLE);
				viewHolder.keywordOne.setText(keywordsReal[0]);
				
			}
			if (lengthKeywords >= 2) {
				viewHolder.keywordTwo.setVisibility(View.VISIBLE);
				viewHolder.keywordTwo.setText(keywordsReal[1]);
			}
			if (lengthKeywords >= 3) {
				viewHolder.keywordThree.setVisibility(View.VISIBLE);
				viewHolder.keywordThree.setText(keywordsReal[2]);
			}
		}
		
	}

	private void setReadId(int position, int readId) {
		if(size!=0){
		readClasses[position]=readId;
		}
	}

	public String[] getStrSeqs(String strSeq, String charSeq) {

		String strSeqs[] = strSeq.split(charSeq); // 暂用全角
		return strSeqs;
	}
	//从本地数据库取收藏列表
	
	public boolean isRead(ArrayList list,String docId){
		for(int j=0;j<list.size();j++){
			if(docId.equals(list.get(j))){
				return true;
			}
		}
				
		return false;
			
	}
	
	public String getLikeNum(ArrayList<HashMap<String,String>> list,String docId){
		HashMap<String, String> map=null;
		String topicid=null;
		for(int j=0;j<list.size();j++){
			map=list.get(j);
			topicid=map.get("topicid");
			if(docId.equals(topicid)){
				int length=topicid.length();
				 int realLike=Integer.parseInt(map.get("likeCount"));
				 int virturlLike=Integer.parseInt(topicid.substring(length-2,length-1),16)*16+
						 Integer.parseInt(topicid.substring(length-1,length),16);
						
				 return realLike+virturlLike+"";
			}
		
		
		}
		return "0";
	}
	
		
   
	@Override
	public int getItemViewType(int position) {
		WizDocument doc=listDocs.get(position);
	
		if(listReadOnes!=null){
			if(isRead(listReadOnes,doc.guid)){
				System.out.println("title-->"+doc.title+",position-->"+position);
				return 1;
			}
		}
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		if(listDocs.size()!=0){
			return listDocs.size();
		}
		return 1;
	}

	public SkyMBADocumentsListAdapter(final Context context,String
			mUserId,String mKbGuid, String tagId) {
		this.context = context;
		this.tagId = tagId;		
		this.inflater = LayoutInflater.from(context);
		this.mUserId=mUserId;
		this.mKbGuid=mKbGuid;
		wizDatabase = WizDatabase.getDb(context, SkyMBALoading.DEFAULT_USERID, mKbGuid);
		listDocs = wizDatabase.getDocumentsByTag(tagId);
		size=listDocs.size();	
		isSelected=new HashMap<Integer, Boolean>();
		scrollY=new HashMap<Integer,Double>();
		for(int j=0;j<size;j++){
			isSelected.put(j, false);
			scrollY.put(j,0.0);
		}	

		if(size!=0){
		readClasses=new int [size];
		areRead=new int [size];
		}
		updateRead();
		setAllLike();
		}
	
	@SuppressWarnings("null")
	public void setAllLike(){
				if(WizMisc.isNetworkAvailable(context)){//联网，本地有账号，已阅读可查看
			// 网络连接获取数据：

			WizAsyncAction.startAsyncAction(null, new WizAction() {

				@Override
				public Object work(WizAsyncActionThread thread,
						Object actionData) throws Exception {
					StringBuilder sb=new StringBuilder();
					
					for(int j=0;j<size;j++){
						WizDocument doc=listDocs.get(j);
						sb.append(doc.guid);
						sb.append(",");
					}
					sb.toString();
					int dot=sb.lastIndexOf(",");
					topicids=sb.substring(0, dot);
					String resultLike = null;
					DefaultHttpClient httpClient = new DefaultHttpClient();

					String urlLike = urlMain + "comm=getlisttopicinfo&ctype=1&&topicids="+topicids;
					HttpGet requestLike = new HttpGet(urlLike);
					HttpResponse responseLike;
					try {
						responseLike = httpClient.execute(requestLike);
						resultLike = EntityUtils.toString(responseLike.getEntity());
						objectLike = new JSONObject(resultLike);

					} catch (ClientProtocolException e) {

						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					} catch (JSONException e) {

						e.printStackTrace();
					}

					return objectLike;
				}

				@Override
				public void onBegin(Object actionData) {

				}

				@Override
				public void onEnd(Object actionData, Object ret) {
					listLikeNums= ProcessJson.doParseGetLikeNums(objectLike);
					likesize=size;
					
					SkyMBADocumentsListAdapter.this.notifyDataSetChanged();//重点！！
//					updateLocalLike(listLikeNums);
				}

				@Override
				public void onException(Object actionData, Exception e) {
					e.printStackTrace();

				}

				@Override
				public void onStatus(Object actionData, String status,
						int arg1, int arg2, Object obj) {

				}

			});
		}else{
//			setLocalLikes();//无网，取本地/
		}
	}
	public void setLocalLikes(){
		
		String sql="SELECT LIKE FROM WIZ_DOCUMENT WHERE DOCUMENT_TAG_GUIDS = '"+tagId+"'";
		listLocalLikes=wizDatabase.sqlToStringArray(sql, 0);
		
	}
		
	public void updateLocalLike(ArrayList<HashMap<String, String>> likeNums){
		int likesize=likeNums.size();
		for(int j=0;j<likesize;j++)
		{   
			HashMap<String,String> map=likeNums.get(j);
			String sql = "UPDATE WIZ_DOCUMENT SET LIKE = '"+map.get("likeCount")+"' WHERE DOCUMENT_GUID = '" + map.get("topicid") + "'";
			wizDatabase.execSql(sql);
			
		}// 字符串型数据加''
				
	}
    public void getReadListFromLocal(){
    	String sql="SELECT DOCUMENT_GUID FROM WIZ_DOCUMENT WHERE READ = 1 AND DOCUMENT_TAG_GUIDS = '"+tagId+"'";
    	listReadOnes=wizDatabase.sqlToStringArray(sql, 0);
    	for(int j=0;j<listReadOnes.size();j++){
    		System.out.println("已经阅读"+listReadOnes.get(j));
    	}
    	
    }
    public void updateRead(){
    	if(WizMisc.isNetworkAvailable(context)&&AccountStatus.verifyLogin(context)){//联网，本地有账号，已阅读可查看
			JsonURL jsonURL = new JsonURL("getread", mUserId, null,tagId,null); 
			url = jsonURL.urlGetReadOnes;
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
					listReadOnes= ProcessJson.doParseGetMyReadOnes(object);
					
					
					SkyMBADocumentsListAdapter.this.notifyDataSetChanged();//重点！！
				}

				@Override
				public void onException(Object actionData, Exception e) {
					e.printStackTrace();

				}

				@Override
				public void onStatus(Object actionData, String status,
						int arg1, int arg2, Object obj) {

				}

			});
		}
    	else {//没联网，并且登陆，取本地
    		getReadListFromLocal();
    	}	
    	
    }
	


	public String[] getTitleKeywords(ViewHolder viewHolder) {
		String s[] = { (String) viewHolder.docTitle.getText(),
				(String) viewHolder.docAbstract.getText(),
				(String) viewHolder.keywordOne.getText(),
				(String) viewHolder.keywordTwo.getText(),
				(String) viewHolder.keywordThree.getText() };
		return s;

	}

	public class ViewHolder {
		public TextView docTitle;
		public TextView docAbstract;
		public ImageView readClass;
		public TextView keywordOne;
		public TextView keywordTwo;
		public TextView keywordThree;
		public TextView likeNum;

	}

}
