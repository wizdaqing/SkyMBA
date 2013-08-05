package com.mba.dialog;

import android.app.Dialog;
import android.content.Context;

public class MyDialog extends Dialog {
	private int message;
	public MyDialog(Context context,int message) {
		super(context);
		this.message=message;
		this.setTitle(message);
		this.show();
		
	}
	
	

}
