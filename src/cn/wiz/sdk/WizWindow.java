package cn.wiz.sdk;

import com.example.skymba.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

//import cn.wiz.sdk.widget.WizColorPickerDialog;

public class WizWindow {

	public static Dialog createAlertDialog(Context ctx, int titleId, String[] itemsArray,
			int index, android.content.DialogInterface.OnClickListener onSelect,
			android.content.DialogInterface.OnClickListener onCancelClick) {

		return new AlertDialog.Builder(ctx).setTitle(ctx.getString(titleId))
				.setSingleChoiceItems(itemsArray, index, onSelect)
				.setNegativeButton(R.string.cancel, onCancelClick).create();
	}

	public static Dialog createAlertDialog(Context ctx, String title, String[] itemsArray,
			int index, android.content.DialogInterface.OnClickListener onSelect,
			android.content.DialogInterface.OnClickListener onCancelClick) {

		return new AlertDialog.Builder(ctx).setTitle(title)
				.setSingleChoiceItems(itemsArray, index, onSelect)
				.setNegativeButton(R.string.cancel, onCancelClick).create();
	}

	public static Dialog createAlertDialog(Context ctx, int titleId, View view,
			String[] itemsArray, boolean[] flags,
			DialogInterface.OnMultiChoiceClickListener onSelect,
			android.content.DialogInterface.OnClickListener onOKClick,
			android.content.DialogInterface.OnClickListener onCancelClick) {

		return new AlertDialog.Builder(ctx).setTitle(ctx.getString(titleId)).setView(view)
				.setMultiChoiceItems(itemsArray, flags, onSelect)
				.setPositiveButton(R.string.ok, onOKClick)
				.setNegativeButton(R.string.cancel, onCancelClick).create();
	}

	public static Dialog createAlertDialog(Context ctx, int titleId, String message,
			String onOKtext, String onCancelText,
			android.content.DialogInterface.OnClickListener onOKClick,
			android.content.DialogInterface.OnClickListener onCancelClick) {
		return new AlertDialog.Builder(ctx).setIcon(R.drawable.alert_dialog_icon)
				.setTitle(ctx.getString(titleId)).setMessage(message)
				.setPositiveButton(onOKtext, onOKClick)
				.setNegativeButton(onCancelText, onCancelClick).create();
	}

	public static Dialog createAlertDialog(Context ctx, int titleId, String message,
			android.content.DialogInterface.OnClickListener onOKClick, int onOkId,
			android.content.DialogInterface.OnClickListener onCancelClick, int onCancelId) {
		return new AlertDialog.Builder(ctx).setIcon(R.drawable.alert_dialog_icon)
				.setTitle(ctx.getString(titleId)).setMessage(message)
				.setPositiveButton(onOkId == 0 ? R.string.ok : onOkId, onOKClick)
				.setNegativeButton(onCancelId == 0 ? R.string.cancel : onCancelId, onCancelClick)
				.create();
	}

	public static Dialog createAlertDialog(Context ctx, int titleId, String message,
			android.content.DialogInterface.OnClickListener onOKClick) {
		return new AlertDialog.Builder(ctx).setIcon(R.drawable.alert_dialog_icon)
				.setTitle(ctx.getString(titleId)).setMessage(message)
				.setPositiveButton(R.string.ok, onOKClick).create();
	}

	public static Dialog createAlertDialog(Context ctx, int titleId, String message,
			android.content.DialogInterface.OnClickListener onOKClick, boolean isOk) {
		return new AlertDialog.Builder(ctx).setIcon(R.drawable.alert_dialog_icon)
				.setTitle(ctx.getString(titleId)).setMessage(message)
				.setPositiveButton(isOk ? R.string.ok : R.string.dialog_close, onOKClick).create();
	}

	public static Dialog createAlertDialog(Context ctx, int titleId, boolean showIcon,
			String message, android.content.DialogInterface.OnClickListener onOKClick) {
		if (showIcon) {

			return new AlertDialog.Builder(ctx).setIcon(R.drawable.alert_dialog_icon)
					.setTitle(ctx.getString(titleId)).setMessage(message)
					.setPositiveButton(R.string.dialog_close, onOKClick).create();
		} else {

			return new AlertDialog.Builder(ctx).setTitle(ctx.getString(titleId))
					.setMessage(message).setPositiveButton(R.string.dialog_close, onOKClick)
					.create();
		}
	}

	public static Dialog createAlertDialog(Context ctx, int titleId, View view,
			android.content.DialogInterface.OnClickListener onOKClick,
			android.content.DialogInterface.OnClickListener onCancelClick) {
		return new AlertDialog.Builder(ctx).setIcon(R.drawable.alert_dialog_icon)
				.setTitle(ctx.getString(titleId)).setView(view)
				.setPositiveButton(R.string.ok, onOKClick)
				.setNegativeButton(R.string.cancel, onCancelClick).create();
	}

	public static Dialog createAlertDialog(Context ctx, int titleId, View view,
			android.content.DialogInterface.OnClickListener onOKClick, int okId,
			android.content.DialogInterface.OnClickListener onCancelClick, int cancelId) {
		return new AlertDialog.Builder(ctx).setIcon(R.drawable.alert_dialog_icon)
				.setTitle(ctx.getString(titleId)).setView(view)
				.setPositiveButton(okId == 0 ? R.string.ok : okId, onOKClick)
				.setNegativeButton(cancelId == 0 ? R.string.cancel : cancelId, onCancelClick)
				.create();
	}

	public static Dialog createAlertDialog(Context ctx, int titleId, String message,
			String okButtonText, android.content.DialogInterface.OnClickListener onOKClick,
			String cancelButtonText, android.content.DialogInterface.OnClickListener onCancelClick) {
		return new AlertDialog.Builder(ctx).setIcon(R.drawable.alert_dialog_icon)
				.setTitle(ctx.getString(titleId)).setMessage(message)
				.setPositiveButton(okButtonText, onOKClick)
				.setNegativeButton(cancelButtonText, onCancelClick).create();
	}

	public static Dialog createListDialog(Context ctx, String title, String[] list,
			android.content.DialogInterface.OnClickListener onListClick) {
		return new AlertDialog.Builder(ctx).setTitle(title).setItems(list, onListClick).create();
	}

	public static Dialog createListDialog(final Activity ctx, final int dialogId, String title,
			String[] list, android.content.DialogInterface.OnClickListener onListClick,
			String cancelButtonText, android.content.DialogInterface.OnClickListener onCancelClick) {
		Dialog dialog = new AlertDialog.Builder(ctx).setTitle(title).setItems(list, onListClick)
				.setNegativeButton(cancelButtonText, onCancelClick).create();
		dialog.setOnKeyListener(new DialogOnKeyListenter(ctx, dialogId));
		return dialog;
	}

	public static ProgressDialog createProgressDialog(Context ctx, int titleId, int messageId,
			boolean indeterminate, boolean cancelable) {
		return createProgressDialog(ctx, titleId, ctx.getResources().getString(messageId),
				indeterminate, cancelable);
	}

	public static ProgressDialog createProgressDialog(Context ctx, int titleId, String message,
			int buttonText, android.content.DialogInterface.OnClickListener onCancelClick,
			boolean indeterminate, boolean cancelable) {
		if (TextUtils.isEmpty(message)) {
			message = "...";
		}
		return createProgressDialog(ctx, titleId, message,
				ctx.getResources().getString(buttonText), onCancelClick, indeterminate, cancelable);
	}

	public static ProgressDialog createProgressDialog(Context ctx, int titleId, String message,
			boolean indeterminate, boolean cancelable) {
		String title = titleId == 0 ? "" : ctx.getString(titleId);
		ProgressDialog dialog = new ProgressDialog(ctx);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(cancelable);
		return dialog;
	}
	//no title progressDialog
	public static ProgressDialog createProgressDialog(Context ctx,int messageId,
			boolean indeterminate, boolean cancelable) {
		return createProgressDialog(ctx, ctx.getResources().getString(messageId),
				indeterminate, cancelable);
	}
	

	public static ProgressDialog createProgressDialog(Context ctx, String message,
			boolean indeterminate, boolean cancelable) {		
		ProgressDialog dialog = new ProgressDialog(ctx);		
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(cancelable);
		return dialog;
	}
	

//	public static WizColorPickerDialog createColorPickerDialog(Context ctx,
//			WizColorPickerDialog.OnColorChangedListener listener, int color) {
//		return new WizColorPickerDialog(ctx, listener, color);
//	}

	public static ProgressDialog createProgressDialog(Context ctx, int titleId, String message,
			String cancelButtonText, android.content.DialogInterface.OnClickListener onCancelClick,
			boolean indeterminate, boolean cancelable) {
		ProgressDialog dialog = new ProgressDialog(ctx);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setMessage(message);
		dialog.setTitle(ctx.getString(titleId));
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setButton(cancelButtonText, onCancelClick);
		return dialog;
	}

	public static void showException(Context ctx, Exception err) {
		Toast.makeText(ctx, err.getMessage(), Toast.LENGTH_LONG).show();
	}

	public static void showMessage(Context ctx, int strId) {
		Toast.makeText(ctx, ctx.getString(strId), Toast.LENGTH_SHORT).show();
	}
	
	public static void showMessage(Context ctx, int strId, int fillId) {
		String message = WizResources.getResString(ctx, strId, fillId);
		Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
	}

	public static void showMessage(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
	}
	public static void showShortMessage(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showMessage(Context ctx, int strId, boolean longTime) {
		Toast.makeText(ctx, ctx.getString(strId), longTime ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)
				.show();
	}

	public static void showMessage(Context ctx, int strId, String param1) {
		String str = ctx.getString(strId);
		str = str.replace("%1", param1);
		Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
	}

}
