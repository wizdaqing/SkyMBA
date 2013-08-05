package com.mba.adapter;

import com.example.skymba.R;
import com.mba.view.SkyMBALoading;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SkyMBAGridViewAdapter extends BaseAdapter {
	
 
	// 定义Context
	private Context mContext;
	private LayoutInflater inflater; 
	private int bagOrLife;
	
	private int mCount=0;
	// 定义整型数组 即图片源
	private Integer[] mImageBagIds = {R.drawable.manage_a,R.drawable.venture_a, 
			 R.drawable.marketing_a, R.drawable.hr_a,
			R.drawable.sell_a,  R.drawable.financial_a,R.drawable.finance_a,
			R.drawable.case_a};
	private Integer[] mImageLifeIds = { R.drawable.health_a, R.drawable.sport_a,
			R.drawable.wine_tasting_a };
//	private TextView textView;
	private int[] lastUpdateInBag={-1,-1,-1,-1,-1,-1,-1,-1};
	private int[]lastUpdateInLife={-1,-1,-1};
	

	public SkyMBAGridViewAdapter(Context c, int bagOrLife,int []lastUpdate) {
		mContext = c;
		this.bagOrLife = bagOrLife;
		this.inflater = LayoutInflater.from(c);
		
		if(lastUpdate!=null){
		for(int j=0;j<lastUpdate.length;j++){
			if(this.bagOrLife==1){
				this.lastUpdateInBag[j]=lastUpdate[j];
				}
			else if(this.bagOrLife==2){
				this.lastUpdateInLife[j]=lastUpdate[j];
				}
			}
		}
		}

	// 获取图片的个数
	public int getCount() {
		if (bagOrLife == SkyMBALoading.BAG) {
			return mImageBagIds.length;
		}
		return mImageLifeIds.length+5;
	}

	// 获取图片在库中的位置
	public Object getItem(int position) {
		return position;
	}

	// 获取图片ID
	public long getItemId(int position) {
		return position;
	}

	
	
	
	@Override
	public int getItemViewType(int position) {
		if(this.bagOrLife==1){
			return lastUpdateInBag[position];
		}else if(this.bagOrLife==2&&position<3){
			return lastUpdateInLife[position];
		}
		return -1;
	}

	@Override
	public int getViewTypeCount() {
		if(this.bagOrLife==1){
			return 8;
		}else if(this.bagOrLife==2){
			return 3;
		}
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView view=null;
		TextView textView = null;
		if(convertView==null){
			convertView=inflater.inflate(R.layout.shelf_item, null);
			view=(ImageView)convertView.findViewById(R.id.shelf_img);
			textView=(TextView)convertView.findViewById(R.id.updateView);
			int updateData=getItemViewType(position);
			if(updateData!=0&&updateData!=-1){
				if(updateData>=10){
					textView.setText("10+");
				}else{
				textView.setText(""+updateData);
				}
				textView.setVisibility(View.VISIBLE);
			}else{
				textView.setVisibility(View.INVISIBLE);//王鑫的手机这儿是空指针异常
			}
			
			if (bagOrLife == SkyMBALoading.BAG) {
				view.setImageResource(mImageBagIds[position]);
				view.setTag(position);
		} else {
			if(mImageLifeIds.length>position){
				view.setImageResource(mImageLifeIds[position]);
				int lifeTag = 8 + position;
				view.setTag(lifeTag);
			}
			else{
				view.setImageResource(mImageLifeIds[0]);
				view.setEnabled(false);
				view.setVisibility(View.INVISIBLE);
			}
			
			
		}
			
			
		}
		else{
			return convertView;
		}
		
		int height=convertView.getHeight();	
		Log.i("height", height+"");
		return convertView;

	}

}
