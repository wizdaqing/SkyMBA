package cn.wiz.sdk;

import android.content.Context;
import com.example.skymba.R;
import cn.wiz.sdk.api.WizObject.WizKb;
import cn.wiz.sdk.api.WizStrings;
import cn.wiz.sdk.api.WizStrings.WizStringId;
import cn.wiz.sdk.api.WizStrings.WizStringsBase;

public class WizLocalize {

	static public void installLocalString(Context ctx) {
		WizStrings.setStrings(new WizLocalStrings(ctx));
	}

	static private class WizLocalStrings implements WizStringsBase {
		Context mContext;

		public WizLocalStrings(Context ctx) {
			mContext = ctx;
		}

		private int getResourceStringId(WizStringId id) {
			switch (id) {
			case SYNC_LOGIN:
				return R.string.signing_in;
			case SYNC_LOGOUT:
				return R.string.signing_out;
			case SYNC_DOWNLOADING_DELETED_GUIDS:
				return R.string.downloading_deleted_objects_log;
			case SYNC_DOWNLOADING_TAGS:
				return R.string.downloading_tags;
			case SYNC_DOWNLOADING_DOCUMENTS:
				return R.string.downloading_notes_list;
			case SYNC_DOWNLOADING_ATTACHMENTS:
				return R.string.downloading_attachments_list;
			case SYNC_UPLOADING_DELETED_GUIDS:
				return R.string.uploading_deleted_objects_log;
			case SYNC_UPLOADING_TAGS:
				return R.string.uploading_tags;
			case SYNC_UPLOADING_DOCUMENT:
				return R.string.uploading_note;
			case SYNC_UPLOADING_ATTACHMENT:
				return R.string.uploading_attachment;
			case SYNC_KB_BEGIN:
				return R.string.syncing_kb_begin;
			case SYNC_KB_END:
				return R.string.syncing_kb_end;
			case PERSONAL_KB_NAME:
				return R.string.personalNotes;
			case SYNC_CANCELED_NOT_WIFI:
				return R.string.sync_stop_because_wifi_disconnected;
			case FOLDER_MY_NOTES:
				return R.string.key_my_notes;
			case FOLDER_MY_DRAFTS:
				return R.string.key_my_drafts;
			case FOLDER_MY_EVENTS:
				return R.string.key_my_events;
			case FOLDER_MY_TASKS:
				return R.string.key_my_tasks;
			case FOLDER_MY_EMAILS:
				return R.string.key_my_emails;
			case FOLDER_MY_JOURNALS:
				return R.string.key_my_journals;
			case FOLDER_MY_MOBILES:
				return R.string.key_my_mobiles;
			case FOLDER_MY_STICKY_NOTES:
				return R.string.key_my_sticky_notes;
			case FOLDER_TASKS_INBOX:
				return R.string.key_inbox;
			case FOLDER_TASKS_COMPLETED:
				return R.string.key_completed;
			case SYNC_DOWNLOADING_NOTE:
				return R.string.downloading_note_ex;
			case SYNC_START:
				return R.string.syncing;
			case STOPPING_SYNC:
				return R.string.stopping_sync;
			case SYNC_DOWNLOADING_NOTES_DATA:
				return R.string.downloading_notes_data;
			}
			//
			return 0;
		}

		@Override
		public String getString(WizStringId id) {
			//
			int stringId = getResourceStringId(id);
			if (stringId == 0)
				return "";
			//
			return mContext.getString(stringId);
		}
	}

	// public static String getAlias(Context ctx, String showName) {
	//
	// if (showName.equals(WizKb.mGroupAttributeOfKbName))
	// return ctx.getString(R.string.mGroupAttributeOfKbName);
	// else if (showName.equals(WizKb.mGroupAttributeOfKbOwnerName))
	// return ctx.getString(R.string.mGroupAttributeOfKbOwnerName);
	// else if (showName.equals(WizKb.mGroupAttributeOfKbUserGroup))
	// return ctx.getString(R.string.mGroupAttributeOfKbUserGroup);
	// else if (showName.equals(WizKb.mGroupAttributeOfKbNote))
	// return ctx.getString(R.string.mGroupAttributeOfKbNote);
	// else if (showName.equals(WizKb.mGroupAttributeOfKbDateCreated))
	// return ctx.getString(R.string.mGroupAttributeOfKbDateCreated);
	// else if (showName.equals(WizKb.mGroupAttributeOfKbDateModified))
	// return ctx.getString(R.string.mGroupAttributeOfKbDateModified);
	// else if (showName.equals(WizKb.mKbUserGroupOwner))
	// return ctx.getString(R.string.mKbUserGroupOwner);
	// else if (showName.equals(WizKb.mKbUserGroupAdmin))
	// return ctx.getString(R.string.mKbUserGroupAdmin);
	// else if (showName.equals(WizKb.mKbUserGroupSuper))
	// return ctx.getString(R.string.mKbUserGroupSuper);
	// else if (showName.equals(WizKb.mKbUserGroupEditor))
	// return ctx.getString(R.string.mKbUserGroupEditor);
	// else if (showName.equals(WizKb.mKbUserGroupAuthor))
	// return ctx.getString(R.string.mKbUserGroupAuthor);
	// else if (showName.equals(WizKb.mKbUserGroupReader))
	// return ctx.getString(R.string.mKbUserGroupReader);
	// else if (showName.equals(WizKb.mKbUserGroupNone))
	// return ctx.getString(R.string.mKbUserGroupNone);
	// return showName;
	// }

}
