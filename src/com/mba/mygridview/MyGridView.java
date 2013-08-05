package com.mba.mygridview;

import com.example.skymba.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.GridView;

public class MyGridView extends GridView{
	private Bitmap background;  
	private Context context;
	public MyGridView(Context context ) {
		super(context);
		this.context=context;
	}
	
   
    
    public MyGridView(Context context, AttributeSet attrs) {   
        super(context, attrs);   
        background = BitmapFactory.decodeResource(getResources(), R.drawable.row_bg5);   
    }

	@SuppressLint("NewApi")
	@Override
	protected void dispatchDraw(Canvas canvas) {
		int count = getChildCount();
		int top = count > 0 ? getChildAt(0).getTop() : 0;
		int backgroundWidth = background.getWidth();
		int backgroundHeight = background.getHeight();
 		int width = getWidth();
 		int height = getHeight();
		int countOut=0;
		int countIn=0;
		for (int y = top; y < height; y += backgroundHeight) {
			
			countOut++;
			for (int x = 0; x < width; x += backgroundWidth) {
				countIn++;
				canvas.drawBitmap(background, x,y, null);
			}
		}

		super.dispatchDraw(canvas);
	}
	

}
