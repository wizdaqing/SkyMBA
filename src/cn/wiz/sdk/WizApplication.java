package cn.wiz.sdk;

import cn.wiz.sdk.api.WizEventsCenter;
import cn.wiz.sdk.api.WizLogger;
import android.app.Application;
import android.annotation.SuppressLint;
import java.lang.Thread.UncaughtExceptionHandler;
import android.content.Context;

public class WizApplication extends Application {

	private boolean need2Exit;

	public void onCreate() {
		need2Exit = false;
		//
		/*
		 * 初始化消息中心的handler
		 */
		WizEventsCenter.init();
		//
		CrashHandler crashHandler = CrashHandler.getInstance();  
		crashHandler.init(getApplicationContext());  
	}

	public void setNeed2Exit(boolean bool) {
		need2Exit = bool;
	}

	public boolean isNeed2Exit() {
		return need2Exit;
	}
	

	/**
	 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
	 * 
	 * @author user
	 * 
	 */
	static public class CrashHandler implements UncaughtExceptionHandler {
		
		public static final String TAG = "CrashHandler";
		
		//系统默认的UncaughtException处理类 
		private Thread.UncaughtExceptionHandler mDefaultHandler;
		//CrashHandler实例
		private static CrashHandler INSTANCE = new CrashHandler();
		//程序的Context对象
		private Context mContext;

		//用于格式化日期,作为日志文件名的一部分
		@SuppressLint("SimpleDateFormat")
		/** 保证只有一个CrashHandler实例 */
		private CrashHandler() {
		}

		/** 获取CrashHandler实例 ,单例模式 */
		public static CrashHandler getInstance() {
			return INSTANCE;
		}

		/**
		 * 初始化
		 * 
		 * @param context
		 */
		public void init(Context context) {
			mContext = context;
			//获取系统默认的UncaughtException处理器
			mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
			//设置该CrashHandler为程序的默认处理器
			Thread.setDefaultUncaughtExceptionHandler(this);
		}

		/**
		 * 当UncaughtException发生时会转入该函数来处理
		 */
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			if (!handleException(ex) && mDefaultHandler != null) {
				//如果用户没有处理则让系统默认的异常处理器来处理
				mDefaultHandler.uncaughtException(thread, ex);
			} else {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				//退出程序
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(1);
			}
		}

		/**
		 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
		 * 
		 * @param ex
		 * @return true:如果处理了该异常信息;否则返回false.
		 */
		private boolean handleException(Throwable ex) {
			if (ex == null) {
				return false;
			}
			WizLogger.logException(mContext, ex);
			return true;
		}
	}
}