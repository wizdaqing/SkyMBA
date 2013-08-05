package com.mba.pullrefresh;
import java.util.Date;

import cn.wiz.sdk.WizWindow;
import cn.wiz.sdk.util.WizMisc;

import com.example.skymba.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author wiz_chentong
 *
 */
public class PullToRefreshListView extends ListView implements OnScrollListener {

	private LayoutInflater mInflater;
	private LinearLayout mHeadView;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;
	private TextView mTitle;
	private TextView mLastUpdate;
	
	private int mHeadContentHeight;
	
	private RotateAnimation mAnimation;
	private RotateAnimation mReverseAnimation;

	private PullToRefreshListViewState mState;//当前状态
	private boolean mIsRefreshable; //是否可以下拉刷新

	private OnRefreshListener mRefreshListener;
	/**
	 * RELEASE_TO_REFRESH : 松开刷新 已经达到刷新的距离<br>
	 * PULL_TO_REFRESH : 下拉刷新 还没有拉到能刷新的距离<br>
	 * REFRESHING : 正在刷新<br>
	 * DONE : 完成<br>
	 */
	private enum PullToRefreshListViewState{
		RELEASE_TO_REFRESH, PULL_TO_REFRESH, REFRESHING, DONE
	}
	public PullToRefreshListView(Context context) {
		super(context);
		init(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	private Context mContext;
	private void init(Context context) {
		mContext = context;
		setCacheColorHint(context.getResources().getColor(R.color.transparent_background));
		
		mInflater = LayoutInflater.from(context);

		mHeadView = (LinearLayout) mInflater.inflate(R.layout.pull_to_refresh_list_header, null);

		mArrowImageView = (ImageView) mHeadView.findViewById(R.id.pull_to_refresh_list_head_arrow);
		mArrowImageView.setMaxHeight(50);

		mProgressBar = (ProgressBar) mHeadView.findViewById(R.id.pull_to_refresh_list_head_progress_bar);
		mTitle = (TextView) mHeadView.findViewById(R.id.pull_to_refresh_list_head_title);
		mLastUpdate = (TextView) mHeadView.findViewById(R.id.pull_to_refresh_list_head_last_update);

		measureView(mHeadView);
		mHeadContentHeight = mHeadView.getMeasuredHeight();
		//设置headView距离屏幕的上边距
		mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
		mHeadView.invalidate();
		//为listView 指定顶部视图 headView
		mHeadView.setEnabled(false);
		addHeaderView(mHeadView);
		setOnScrollListener(this);

		mAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mAnimation.setInterpolator(new LinearInterpolator());
		mAnimation.setDuration(250);
		mAnimation.setFillAfter(true);
		mReverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseAnimation.setInterpolator(new LinearInterpolator());
		mReverseAnimation.setDuration(200);
		mReverseAnimation.setFillAfter(true);

		mState = PullToRefreshListViewState.DONE; //默认状态：完成
		mIsRefreshable = false; //默认不可下拉刷新
	}
	@SuppressWarnings("deprecation")
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
	
	private int mFirstItemIndex;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mFirstItemIndex = firstVisibleItem;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}
	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.mRefreshListener = refreshListener;
		mIsRefreshable = true;// 设置可以刷新
	}
	
	private int mStartY;
	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean mIsRecored;
	private boolean mIsBack;
	//实际的padding的距离与界面上手势偏移距离的比例
	private final static int RATIO = 3;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (mIsRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mFirstItemIndex == 0 && !mIsRecored) {
					mIsRecored = true;
					mStartY = (int) event.getY();
				}
				break;

			case MotionEvent.ACTION_UP:
				if(mState != PullToRefreshListViewState.REFRESHING) {
					switch (mState) {
					case PULL_TO_REFRESH:
						mState = PullToRefreshListViewState.DONE;
						//由下拉刷新状态到done状态
						changeHeaderViewByState();
						break;
					case RELEASE_TO_REFRESH:
						mState = PullToRefreshListViewState.REFRESHING;
						//由松开刷新状态到正在刷新状态
						changeHeaderViewByState();
						if(WizMisc.isNetworkAvailable(mContext)){
						onRefresh();
						}
						else {
							mState = PullToRefreshListViewState.DONE;
							//由松开刷新状态到正在刷新状态
							changeHeaderViewByState();
						  Toast toast= Toast.makeText(mContext, R.string.no_network, Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.TOP,0,WizMisc.dip2px(mContext, 48));
							toast.show();
							
							
							
						}
							
						break;
					default:
						break;
					}
				}
				mIsRecored = false;
				mIsBack = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int moveY = (int) event.getY();
				if(!mIsRecored && mFirstItemIndex == 0){
					mIsRecored = true;
					mStartY = moveY;
				}
				int y = moveY - mStartY;
				if(mIsRecored && mState != PullToRefreshListViewState.REFRESHING &&  y> 40){
					//保证在设置padding的过程中，当前的位置一直实在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
					//可以松手去刷新了
					if(mState == PullToRefreshListViewState.RELEASE_TO_REFRESH) {
						setSelection(0);//指定选定的位置 headView
						
						//往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if((y / RATIO < mHeadContentHeight ) && y > 0){
							//由松开刷新状态转变到下拉刷新状态
							mState = PullToRefreshListViewState.PULL_TO_REFRESH;
							changeHeaderViewByState();
						}
						//一下子推到顶了
						else if(y <= 0){
							//由松开刷新状态转变到done状态
							mState = PullToRefreshListViewState.DONE;
							changeHeaderViewByState();
						}
						//往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
						else {
							//不用进行特别的操作，只用更新paddingTop的值就行了
							mHeadView.setPadding(0, y / RATIO - mHeadContentHeight, 0, 0);
						}
					}
					
					//还没有到达显示松开刷新的时候，DONE或者是PULL_TO_REFRESH状态
					if(mState == PullToRefreshListViewState.PULL_TO_REFRESH){
						setSelection(0);
						
						//下拉到可以进入RELEASE_TO_REFRESH的状体
						if(y / RATIO >= mHeadContentHeight ){
							//由DONE或者下拉刷新状态 转变到松开刷新
							mState = PullToRefreshListViewState.RELEASE_TO_REFRESH;
							mIsBack = true;
							changeHeaderViewByState();
						}
						//上推到顶了
						else if(y <= 0){
							//由DONE或者下拉刷新状态转变到DONE状态
							mState = PullToRefreshListViewState.DONE;
							changeHeaderViewByState();
						}
					}
					
					//DONE状态下
					if(mState == PullToRefreshListViewState.DONE) {
						if(y > 0){
							//由DONE状态转变到下拉刷新状态
							mState = PullToRefreshListViewState.PULL_TO_REFRESH;
							changeHeaderViewByState();
						}
					}
					//更新headView的size
					if(mState == PullToRefreshListViewState.PULL_TO_REFRESH) {
						mHeadView.setPadding(0, -1 * mHeadContentHeight + y / RATIO, 0, 0);
						
					}
				}
				break;
			}

		}
		return super.onTouchEvent(event);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (mState) {
		case RELEASE_TO_REFRESH://当前状态，松开刷新
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
			mTitle.setVisibility(View.VISIBLE);
			mLastUpdate.setVisibility(View.VISIBLE);
			
			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(mAnimation);
			
			mTitle.setText(R.string.release_to_refresh);
			break;
		case PULL_TO_REFRESH://当前状态，下拉刷新
			mProgressBar.setVisibility(View.GONE);
			mTitle.setVisibility(View.VISIBLE);
			mLastUpdate.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.VISIBLE);
			//是由RELEASE_TO_REFRESH状态转变来的
			if(mIsBack){
				mIsBack = false;
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mReverseAnimation);
				
				mTitle.setText(R.string.pull_to_refresh);
			} else {
				mTitle.setText(R.string.pull_to_refresh);
			}
			break;
		case REFRESHING://当前状态，正在刷新
			mHeadView.setPadding(0, 0, 0, 0);
			TranslateAnimation t = new TranslateAnimation(0, 0, mArrowImageView.getBottom(), 0);
			t.setDuration(250);
			t.setFillAfter(true);
			this.startAnimation(t);
			mProgressBar.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.GONE);
			mTitle.setText(R.string.updating);
			mLastUpdate.setVisibility(View.VISIBLE);
			break;	
		case DONE://当前状态，done
			TranslateAnimation t1 = new TranslateAnimation(0, 0, mHeadView.getBottom(), 0);
			t1.setDuration(250);
			t1.setFillAfter(true);
			this.startAnimation(t1);
			mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
			mHeadView.invalidate();
			mProgressBar.setVisibility(View.GONE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setImageResource(R.drawable.goicon);
			mTitle.setText(R.string.pull_to_refresh);
			mLastUpdate.setVisibility(View.VISIBLE);
			break;	
		}
	}
	private void onRefresh() {
		if(mRefreshListener != null){
			mRefreshListener.onRefresh();
		}
	}
	//做最后刷新数据的时间
	//HeadView状态改变时所对应的操作
	@SuppressWarnings("deprecation")
	public void onRefreshComplete(){
		mState = PullToRefreshListViewState.DONE;
		mLastUpdate.setText(mContext.getString(R.string.last_updated) + isFirstRefresh(getContext()));
		changeHeaderViewByState();
	}
	@SuppressWarnings("deprecation")
	@Override
	public void setAdapter(ListAdapter adapter) {
		mLastUpdate.setText(mContext.getString(R.string.last_updated) + isFirstRefresh(getContext()));
		super.setAdapter(adapter);
	}
	public String isFirstRefresh(Context mContext){
		Boolean isFirstRefresh = false;
		SharedPreferences pref = mContext.getSharedPreferences("myActivityName", 0);
		Editor editor=pref.edit();
		//取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		isFirstRefresh = pref.getBoolean("isFirstRefresh", true);
		String  firstRefresh="无记录";
		String last_update=null;
		if(isFirstRefresh){
			editor.putString("last_update", new Date().toLocaleString());
			editor.putBoolean("isFirstRefresh", false);
			editor.commit();
			return firstRefresh;
			
		}
		else{
			last_update=pref.getString("last_update", "啊");
			editor.putString("last_update", new Date().toLocaleString());
			editor.commit();
			return last_update;
		 
		}
	}
}
