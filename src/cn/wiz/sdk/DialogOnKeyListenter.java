package cn.wiz.sdk;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;

public class DialogOnKeyListenter implements OnKeyListener {

	private Activity mContext;
	private int mDialogId;

	public DialogOnKeyListenter(Activity ctx, int dialogId) {
		mContext = ctx;
		mDialogId = dialogId;
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			mContext.removeDialog(mDialogId);

		return false;
	}

}
