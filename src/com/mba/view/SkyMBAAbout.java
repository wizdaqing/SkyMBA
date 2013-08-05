package com.mba.view;

import cn.mba.manageactivity.SysApplication;

import com.example.skymba.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SkyMBAAbout extends Activity {
	RelativeLayout about=null;
	ImageView imageView1=null;
	TextView textView1=null;
	TextView textView2=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		
		SysApplication.getInstance().addActivity(this); 
		imageView1=new ImageView(this);
		textView1=new TextView(this);
		textView1.setTextSize(20);
		String tmp= "不忘初心，方得始终";
	    SpannableString sp =  new  SpannableString("不忘初心，方得始终");   
	       //设置斜体   
	      sp.setSpan( new StyleSpan(android.graphics.Typeface.BOLD_ITALIC),  0 ,  tmp.length() , Spannable.SPAN_EXCLUSIVE_INCLUSIVE);   			
	     textView1.setText(sp);
	     textView1.setTextSize(18);
	     textView1.setTextColor(Color.WHITE);
	     textView1.setGravity(Gravity.CENTER);
	     textView2=new TextView(this);
	     textView2.setText("version 1.0");
	     textView2.setTextSize(14);
	     textView2.setGravity(Gravity.CENTER);
	     textView2.setTextColor(Color.WHITE);
	     
		imageView1.setBackgroundResource(R.drawable.about_icon);
		about=(RelativeLayout)this.findViewById(R.id.rela_about);
		   DisplayMetrics metric = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(metric);
	        int width = metric.widthPixels;  // 屏幕宽度（像素）
	        int height=metric.heightPixels;
	        int realWidth=width/7*2;
	    RelativeLayout.LayoutParams mParams=new RelativeLayout.LayoutParams(realWidth, realWidth);
	    mParams.topMargin=height/5;
	    mParams.bottomMargin=height/10;
		mParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		about.addView(imageView1, mParams);
		int height2=realWidth/3;
		 Bitmap background2 = BitmapFactory.decodeResource(getResources(), R.drawable.about_center); 
		int bgWidth= background2.getWidth();
		int bgHeight=background2.getHeight();
		 float scale=bgHeight/height2;
		int width2=(int) (bgWidth/scale);
		RelativeLayout.LayoutParams mParams2=new RelativeLayout.LayoutParams(width2,height2 );
		mParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mParams2.topMargin=height/5+realWidth+realWidth/4;
		about.addView(textView1,mParams2);
		
		RelativeLayout.LayoutParams mParams3=new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
		mParams3.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mParams3.topMargin=height/5*4;
		about.addView(textView2,mParams3);
	}

}
