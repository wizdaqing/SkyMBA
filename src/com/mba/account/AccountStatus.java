package com.mba.account;

import cn.wiz.sdk.settings.WizAccountSettings;

import com.mba.view.SkyMBALoading;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AccountStatus {
	
	
	public  static  void setAccountStatus(Context context,int account_status){
		SharedPreferences pref = context.getSharedPreferences("myActivityName", 0);
		Editor editor = pref.edit(); 
		editor.putInt("account_status", account_status);
		editor.commit();
		int status=pref.getInt("account_status", -1);
		System.out.println(status);
	}
	
	
	public  static  int getAccountStatus(Context context){
		SharedPreferences pref = context.getSharedPreferences("myActivityName", 0);
		return pref.getInt("account_status", -1);
		
	}
	
	public static boolean verifyLogin(Context context){
		String curUsername=WizAccountSettings.getUserId(context);
		int account_status=getAccountStatus(context);
		if(curUsername != SkyMBALoading.DEFAULT_USERID && curUsername != null
				&& !curUsername.equals("")&&account_status==1){
			return true;
		}
			return false;
	}
	
	public static boolean verifyReg(Context context){
		String curUsername=WizAccountSettings.getUserId(context);
		int account_status=getAccountStatus(context);
		if(curUsername == null
				||curUsername.equals("")&&account_status==-1){
			return false;
		}
		
		return true;
		
	}
}
