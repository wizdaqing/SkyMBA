package com.mba.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.wiz.sdk.api.WizAsyncAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAsyncActionThread;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ProcessJson {

	// �û��ĳɼ�����Ϣ
	@SuppressLint("UseSparseArrays")
	public static HashMap<Integer, String> doParseGetUserInfo(
			JSONObject jsonObject) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {
				String credit = jsonObject.getString("credit");
				String grades = jsonObject.getString("grades");
				map.put(1, credit);
				map.put(2, grades);
				JSONArray array = jsonObject.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject jsonItem = array.getJSONObject(i);
					String count = jsonItem.getString("Count");
					map.put(i + 3, count);
				}
				return map;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}
	
	public static ArrayList<HashMap<String, String>> doParseGetLikeNums(JSONObject jsonObject){
		ArrayList<HashMap<String, String>> likeNums=new ArrayList<HashMap<String, String>>();
		String status;
		try {
			status = jsonObject.getString("status");
			if(status.equals("1")){
				JSONArray array=jsonObject.getJSONArray("data");
				for(int j=0;j<array.length();j++){
					JSONObject jsonItem=array.getJSONObject(j);
					HashMap<String, String> map=new HashMap<String, String>();
					String topicid=jsonItem.getString("topicid");
					String likeCount=jsonItem.getString("FavourableCount");
					map.put("topicid", topicid);
					map.put("likeCount", likeCount);
					likeNums.add(map);
					
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return likeNums;
		
			
		
	}

	// �ղ�
	public static int doParseGetFavorite(JSONObject jsonObject) {

		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {
				return 1;
			}

		} catch (JSONException e) {

			e.printStackTrace();
			return -1;
		}

		return 0;

	}
	
	public static int doParseAddReadNew(JSONObject jsonObject) {

		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {
				return 1;
			}else if(status.equals("-2")){
				return -2;
			}

		} catch (JSONException e) {

			e.printStackTrace();
			return -1;
		}

		return 0;

	}
	//ȡ���ղ�
	public static int doParseDelFavorite(JSONObject jsonObject) {

		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {
				return 1;
			}

		} catch (JSONException e) {

			e.printStackTrace();
			return -1;
		}

		return 0;

	}

	// ��ú���

	public static int doParseGetLike(JSONObject jsonObject) {

		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {
				String FavourableCount = jsonObject
						.getString("FavourableCount");
				return (Integer.parseInt(FavourableCount));

			}

		} catch (JSONException e) {
			//
			e.printStackTrace();
			return -1;
		}
		return 0;

	}

	// �ղ��б�

	public static ArrayList<String> doParseGetMyFavorite(JSONObject jsonObject) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {
				JSONArray array = jsonObject.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					String guid = (String) array.get(i);
					list.add(guid);
				}
				return list;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	// �Ѿ��Ķ��б�
	public static ArrayList<String> doParseGetMyReadOnes(JSONObject jsonObject) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {
				JSONArray array = jsonObject.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					String guid = (String) array.get(i);
					list.add(guid);
				}
				return list;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	// ���һ���Ķ�����

	public static int doParseIsRead(JSONObject jsonObject) {

		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {

				return 1;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;

	}

	// ͬ�������ղ���������

	public static int doSyncLocalFavor(JSONObject jsonObject) {

		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {

				return 1;
			}

		} catch (Exception e) {
			e.printStackTrace();// �Ƿ��׳��쳣
			return -1;
		}
		return 0;

	}
	
	public static int doUploadRead(JSONObject jsonObject) {

		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {

				return 1;
			}

		} catch (Exception e) {
			e.printStackTrace();// �Ƿ��׳��쳣
			return -1;
		}
		return 0;

	}

	// �������
	public static int doSubmitSuggest(JSONObject jsonObject) {

		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {

				return 1;
			}

		} catch (Exception e) {
			e.printStackTrace();// �Ƿ��׳��쳣
			return -1;
		}
		return 0;

	}
	
	public static int doSubmitCreditRead(JSONObject jsonObject) {

		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {

				return 1;
			}

		} catch (Exception e) {
			e.printStackTrace();// �Ƿ��׳��쳣
			return -1;
		}
		return 0;

	}

	// �Ƿ��ղ�
	public static int doIsFavored(JSONObject jsonObject) {

		try {
			String Count = jsonObject.getString("Count");
			if (Count.equals("0")) {

				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();// �Ƿ��׳��쳣
			return -1;
		}
		return 1;

	}
	

	// �Ƿ����
	public static int doIsLiked(JSONObject jsonObject) {

		try {
			String Count = jsonObject.getString("Count");
			if (Count.equals("0")) {

				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();// �Ƿ��׳��쳣
			return -1;
		}
		return 1;

	}

}
