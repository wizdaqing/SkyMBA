package com.mba.serilizable;

import java.io.Serializable;


public class SerialDocument implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private int position;
		private String guid;
		private String title;
		private String kbGuid;
		private String mCatoName;
		private String mTagId;
		private boolean favorite;
		
		public SerialDocument(int position,String guid,String title,String kbGuid,String mCatoName,String mTagId,boolean favorite){
			this.setPosition(position);
			this.guid=guid;
			this.title=title;
			this.kbGuid=kbGuid;
			this.mCatoName=mCatoName;
			this.mTagId=mTagId;
			this.favorite=favorite;
		}
		
		public String getGuid() {
			return guid;
		}
		public void setGuid(String guid) {
			this.guid = guid;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getKbGuid() {
			return kbGuid;
		}
		public void setKbGuid(String kbGuid) {
			this.kbGuid = kbGuid;
		}
		public String getmCatoName() {
			return mCatoName;
		}
		public void setmCatoName(String mCatoName) {
			this.mCatoName = mCatoName;
		}
		public String getmTagId() {
			return mTagId;
		}
		public void setmTagId(String mTagId) {
			this.mTagId = mTagId;
		}

		public void setFavorite(boolean favorite) {
			this.favorite = favorite;
		}

		public boolean getFavorite() {
			return favorite;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		public int getPosition() {
			return position;
		}
		
		
}
