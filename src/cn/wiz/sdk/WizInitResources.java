package cn.wiz.sdk;

import cn.wiz.sdk.settings.WizSystemSettings;

import com.example.skymba.R;

import android.app.Activity;

public class WizInitResources extends Thread {

	private Activity mContext;

	public WizInitResources(Activity ctx) {
		this.mContext = ctx;
	}

	@Override
	public void run() {
		super.run();
		createDefaultEditPath();
		insert2html();
		setAbsImgLength();
		// optEmail();
		// fbEmailSubuect();
	}

	void insert2html() {
		String defValue = mContext.getString(R.string.message_add_text_to_html);
		WizSystemSettings.setTextOfInsert2html(mContext, defValue);
	}

//	void optEmail() {
//		String defValue = mContext.getString(R.string.message_email_select);
//		WizSystemSettings.setTextOfEmailSelect(mContext, defValue);
//	}
//
//	void fbEmailSubuect() {
//		String defValue = mContext.getString(R.string.message_email_subject);
//		defValue = mContext.getString(R.string.message_email_thanks, defValue);
//		WizSystemSettings.setTextOfEmailSubuectfb(mContext, defValue);
//	}

	void setAbsImgLength() {
		int length = (int) (WizSystemSettings.getScreenDensity(mContext) * WizSystemSettings.DEFAULT_ABS_IMG_LENGTH);
		WizSystemSettings.setDefaultAbsImgLength(mContext, length);
	}

	void createDefaultEditPath() {
		// String mDefaultDir;
		// WizFile.getEditDefaultNotePath(mContext, mDefaultDir);
		// WizFile.getEditDefaultImgPath(mContext, mDefaultDir);
	}
}
