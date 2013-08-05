package com.mba.view;

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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.mba.manageactivity.SysApplication;
import cn.wiz.sdk.api.WizAsyncAction;
import cn.wiz.sdk.api.WizObject;
import cn.wiz.sdk.api.WizAsyncAction.WizAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAsyncActionThread;
import cn.wiz.sdk.api.WizObject.WizDeletedGUID;
import cn.wiz.sdk.api.WizObject.WizDocument;
import cn.wiz.sdk.db.WizDatabase;
import cn.wiz.sdk.settings.WizAccountSettings;
import cn.wiz.sdk.util.WizMisc;

import com.example.skymba.R;
import com.mba.account.AccountStatus;
import com.mba.adapter.SkyMBADocumentsListAdapter;
import com.mba.json.JsonURL;
import com.mba.json.ProcessJson;
import com.mba.serilizable.SerialDocument;

public class SkyMBAFavoriteList extends Activity{

		private TextView favorite_list;
		private ListView docList;
	    private String mKbGuid=null;
	    private String mTagId=null;
	    private SkyMBAFavoriteAdapter mAdapter;
		private String mTagName=null;
		private TextView noFavorite=null;
		private ArrayList guidlist=null;
		private ArrayList<String>listCato;
		private int readClasses[]=null;
		private int size=0;
		private RelativeLayout pBarLayout = null;
		private ArrayList<Integer>kbGuids=new ArrayList<Integer>();
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
		private String[]tagIdTexts={"管理","创业","营销","人力","销售","金融","财务","案例","养生","运动","品酒"};
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.favorite_list);
			SysApplication.getInstance().addActivity(this); 
			docList=(ListView)this.findViewById(R.id.docList);
			noFavorite=(TextView)this.findViewById(R.id.noFavorite);
			if(!AccountStatus.verifyLogin(getApplicationContext())){
				docList.setVisibility(View.GONE);
				noFavorite.setVisibility(View.VISIBLE);
			}else{
				init();
				addListener();
			}
		
			
		}
		
		
		public void addListener() {
			docList.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					docList.setSelected(true);
					ViewHolder viewHolder=(ViewHolder)view.getTag();
					viewHolder.docTitle.setTextColor(Color.argb(255,153,153,153));
					viewHolder.docAbstract.setTextColor(Color.argb(255,153,153,153));
					int bagOrLife=mAdapter.getGuid(position);
					if(bagOrLife==0){
						mKbGuid=SkyMBALoading.KBGUID_BAG;
					}else if(bagOrLife==1){
						mKbGuid=SkyMBALoading.KBGUDI_LIFE;
					}
					Intent intent = new Intent();
					intent.setClass(SkyMBAFavoriteList.this,
							SkyMBAViewDocument.class);
					WizObject.WizDocument document = (WizObject.WizDocument) docList.getAdapter().getItem(position);
					
					guidlist=mAdapter.getGuidList();
					kbGuids=mAdapter.getKbGuidList();
					
					  SerialDocument doc=new SerialDocument(position,document.guid,document.title,mKbGuid,null,null,true);
					intent.putStringArrayListExtra("guidlist", guidlist);
					intent.putIntegerArrayListExtra("kbguidlist", kbGuids);
					intent.putExtra("doc", doc);
					SkyMBAFavoriteList.this.startActivity(intent);
					
				}});
			
			
		}
		
		public  void init() {
			
			favorite_list=(TextView)this.findViewById(R.id.catoName);
			
			docList.setSelector(R.drawable.listview_selector);
			
			pBarLayout=(RelativeLayout)this.findViewById(R.id.pBarLayout);
			
			if(mAdapter==null){
				mAdapter=new SkyMBAFavoriteAdapter(this,WizAccountSettings.getUserId(this));	
			}
			docList.setAdapter(mAdapter);
			
		
			
		}
		class SkyMBAFavoriteAdapter extends BaseAdapter {
			private ArrayList<WizDocument> listDocs = new ArrayList<WizDocument>();//必须初始化！！

			private Context context;
			private String tagId;
			private LayoutInflater inflater;
			private WizDatabase wizDatabase;
			private String url;
			private String urlMain = "http://mba.trends-china.com/service.ashx?";
			private JSONObject object = null;
			private String mUserId;
			private WizDatabase dbBag;
			private WizDatabase dbLife;
			private ArrayList<Integer> kbGuids=new ArrayList<Integer>();
		    private String mKbGuid;
		    private ArrayList listReadOnes=null;
		    public  boolean IS_NULL=false;
		    private String topicids=null;

			protected JSONObject objectLike=null;

			protected ArrayList<HashMap<String, String>> listLikeNums=new ArrayList<HashMap<String, String>>();

			protected int likesize;
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
					int number=listDocs.size();
					return number;	
		     	}

			@Override
			public Object getItem(int position) {
				
					return listDocs.get(position);
				
			}
			public int getGuid(int position){
				return kbGuids.get(position);
			}
			
			private void setReadId(int position, int id) {
				if(size!=0){
				readClasses[position]=id;
				}
			}
			public int getReadClass(int position){
				return readClasses[position];
			}
			
			@Override
			public long getItemId(int position) {

				return position;
			}
			public ArrayList<String> getGuidList(){
				ArrayList<String> list=new ArrayList<String>();
				for(int j=0;j<listDocs.size();j++){
				   WizDocument doc=listDocs.get(j);
				   String guid=doc.guid;
				   list.add(guid);
				}
				return list;
			}
			
		public ArrayList<Integer> getKbGuidList(){
				return kbGuids;
			}
			
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder = null;
//				if (convertView == null) {
					viewHolder = new ViewHolder();
					convertView = inflater.inflate(
							R.layout.favorlist_item, null);
					viewHolder.docTitle = (TextView) convertView
							.findViewById(R.id.title);
					viewHolder.readClass=(ImageView)convertView.findViewById(R.id.readClassImg);
					viewHolder.docAbstract = (TextView) convertView
							.findViewById(R.id.abs);
//					viewHolder.readClasses=(TextView)convertView.findViewById(R.id.read_class);
					viewHolder.keywordOne = (TextView) convertView
							.findViewById(R.id.keyword_01);
					viewHolder.keywordTwo = (TextView) convertView
							.findViewById(R.id.keyword_02);
					viewHolder.keywordThree = (TextView) convertView
							.findViewById(R.id.keyword_03);
					viewHolder.likeNum=(TextView)convertView.findViewById(R.id.like_number);
					convertView.setTag(viewHolder);

//				} else {
					viewHolder = (ViewHolder) convertView.getTag();
//				}
				Object obj = listDocs.get(position);
				if (obj == null)
					return null;

				WizDocument curDocument = (WizDocument) obj;
				
				setData(viewHolder, curDocument,position);
				viewHolder.likeNum.setText(getLikeNum(listLikeNums, curDocument.guid));
				return convertView;
			}
			public String getLikeNum(ArrayList<HashMap<String,String>> list,String docId){
				HashMap<String, String> map=null;
				String topicid=null;
				for(int j=0;j<list.size();j++){
					map=list.get(j);
					topicid=map.get("topicid");
					if(docId.equals(topicid)){
						return map.get("likeCount");
					}
				
				
				}
				return "0";
			}
			public void setAllLike(){
				if(WizMisc.isNetworkAvailable(SkyMBAFavoriteList.this)){//联网，本地有账号，已阅读可查看
			// 网络连接获取数据：

			WizAsyncAction.startAsyncAction(null, new WizAction() {

				@Override
				public Object work(WizAsyncActionThread thread,
						Object actionData) throws Exception {
					StringBuilder sb=new StringBuilder();
					
					for(int j=0;j<listDocs.size();j++){
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
					pBarLayout.setVisibility(View.GONE);
					SkyMBAFavoriteAdapter.this.notifyDataSetChanged();//重点！！
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
			
		}
	}
	
			public void resetLike(){
				StringBuilder sb=new StringBuilder();
				
				for(int j=0;j<listDocs.size();j++){
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
				listLikeNums= ProcessJson.doParseGetLikeNums(objectLike);
				likesize=size;
				
				SkyMBAFavoriteAdapter.this.notifyDataSetChanged();//重点！！
			}
			public void setData(ViewHolder viewHolder, WizDocument curDocument,int position) {
//				for(int j=0;j<11;j++){
//					if(listCato.get(position).equals(tagIds[j])){
//						viewHolder.docCatogery.setText(tagIdTexts[j]);
//					}
//				}
				
				String guid=curDocument.guid;
				String[] title = getStrSeqs(curDocument.title, "@");
				int lengthTitle = title.length;
				
				if (title != null) {
					if (lengthTitle >= 1) {
						viewHolder.docTitle.setText(title[0]);
						
					}
					if (lengthTitle >= 2) {
						viewHolder.docAbstract.setText(title[1]);
					}
					if(lengthTitle>=3){
						int readId=Integer.parseInt(title[2]);
						setReadId(position, readId);
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

			public String[] getStrSeqs(String strSeq, String charSeq) {

				String strSeqs[] = strSeq.split(charSeq); // 暂用全角
				return strSeqs;
			}

		    public ArrayList<WizDocument> getFavorListFromLocal(boolean isLocal){
		    	final WizDatabase dbBag =  WizDatabase.getDb(context,
						SkyMBALoading.DEFAULT_USERID, SkyMBALoading.KBGUID_BAG);
				final WizDatabase dbLife = WizDatabase.getDb(context, SkyMBALoading.DEFAULT_USERID,
						SkyMBALoading.KBGUDI_LIFE);
				String sql=null;
				String sqlCato=null;
				ArrayList<WizDocument>listTemp=new ArrayList<WizDocument>();
				if(isLocal){
					 sql="SELECT DOCUMENT_GUID FROM WIZ_DOCUMENT WHERE FAVORITE = 1 ";
			    	
			    	 sqlCato="SELECT DOCUMENT_TAG_GUIDS FROM WIZ_DOCUMENT WHERE FAVORITE = 1 ";
				}else {
		    	 sql="SELECT DOCUMENT_GUID FROM WIZ_DOCUMENT WHERE FAVORITE = 1 OR FAVORITE = 2";
		    	
		    	 sqlCato="SELECT DOCUMENT_TAG_GUIDS FROM WIZ_DOCUMENT WHERE FAVORITE = 1 OR FAVORITE = 2";
				}
		    	ArrayList<String>list1=new ArrayList<String>();
				list1=dbBag.sqlToStringArray(sql,0);
				listCato=new ArrayList<String>();
				listCato.addAll(dbBag.sqlToStringArray(sqlCato, 0));
				listCato.addAll(dbLife.sqlToStringArray(sqlCato, 0));
				ArrayList<String>list2=new ArrayList<String>();
				list2=dbLife.sqlToStringArray(sql,0);
				int size1=list1.size();
				int size2=list2.size();
				int kbSize=size1+size2;
				int j=0;
			
				int favorsize=0;
				for(j=0;j<size1;j++){
					WizDocument doc=dbBag.getDocumentByGuid(list1.get(j));
					if(doc!=null){
						if(isLocal){
							listTemp.add(doc);
						}else {
						listDocs.add(doc);
						}
						kbGuids.add(0);
						favorsize++;
					}
					
					
					
				}
				
				
				for(int m=0;m<size2;m++){
					WizDocument doc=dbLife.getDocumentByGuid(list2.get(m));
					if(doc!=null){
						if(isLocal){
							listTemp.add(doc);
						}else{
							listDocs.add(doc);
						}
						kbGuids.add(1);
						favorsize++;
					}
								
				}
				if(isLocal){
					size=listTemp.size();
				}else{
				size=listDocs.size();
				}
				if(size==0){
					docList.setVisibility(View.GONE);
					noFavorite.setVisibility(View.VISIBLE);
					return null;
				}else{
					readClasses=new int[size];
					docList.setVisibility(View.VISIBLE);
					noFavorite.setVisibility(View.GONE);
					return listTemp;
					
				}
				
			
				
			
		    	 
		    }
			public SkyMBAFavoriteAdapter(final Context context,String
					mUserId) {
				this.context = context;
				this.inflater = LayoutInflater.from(context);
				this.mUserId=mUserId;
				
					if(WizMisc.isNetworkAvailable(context))
					{	
						pBarLayout.setVisibility(View.VISIBLE);
						final WizDatabase dbBag = WizDatabase.getDb(context, SkyMBALoading.DEFAULT_USERID,
								SkyMBALoading.KBGUID_BAG);
						final WizDatabase dbLife = WizDatabase.getDb(context, SkyMBALoading.DEFAULT_USERID,
								SkyMBALoading.KBGUDI_LIFE);
					JsonURL jsonURL = new JsonURL("getFavorite", mUserId, null,null,null);
					url = jsonURL.urlGetFavorite;
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
							ArrayList list = ProcessJson.doParseGetMyFavorite(object);
							listCato=new ArrayList<String>();
							for (int m = 0; m < list.size(); m++) {
								String guid = (String) list.get(m);
								WizDocument doc = dbBag.getDocumentByGuid(guid);
								
								if (doc != null) {
								listDocs.add(doc);
								kbGuids.add(0);
								listCato.add(doc.tagGUIDs);
								} else {
								doc = dbLife.getDocumentByGuid(guid);
								if (doc != null) {
								listDocs.add(doc);
								kbGuids.add(1);
								listCato.add(doc.tagGUIDs);
								}
								}
								}
							ArrayList<WizDocument> listTemp=getFavorListFromLocal(true);
							if(listTemp!=null){
							listDocs.addAll(getFavorListFromLocal(true));
							}
							size=listDocs.size();
							if(size==0){
									pBarLayout.setVisibility(View.GONE);
									docList.setVisibility(View.GONE);
									noFavorite.setVisibility(View.VISIBLE);
								}else{
									readClasses=new int[size];
									docList.setVisibility(View.VISIBLE);
									noFavorite.setVisibility(View.GONE);
									setAllLike();
									
								}

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

						
				else{
					getFavorListFromLocal(false);//从本地数据库取收藏列表
				}
					
		}

}
		public ArrayList<String> getFavorList(WizDatabase db){
			String sql="SELECT DOCUMENT_GUID FROM WIZ_DOCUMENT WHERE FAVORITE = 1 OR FAVORITE = 2 ";
			return db.sqlToStringArray(sql, 0);
		}


			public String[] getTitleKeywords(ViewHolder viewHolder) {
				String s[] = { (String) viewHolder.docTitle.getText(),
						(String) viewHolder.docAbstract.getText(),
						(String) viewHolder.keywordOne.getText(),
						(String) viewHolder.keywordTwo.getText(),
						(String) viewHolder.keywordThree.getText() };
				return s;

			}

		 class ViewHolder {
			public TextView docTitle;
			public ImageView readClass;
			public TextView docAbstract;
			public TextView keywordOne;
			public TextView keywordTwo;
			public TextView keywordThree;
			public TextView likeNum;

		}
		
}
