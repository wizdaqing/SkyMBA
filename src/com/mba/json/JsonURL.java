package com.mba.json;

public class JsonURL {
	private String comm;
	private String userid;
	private String topicid;
	private String tagid;
	private String topicids;
	public String urlGetUserInfo;
	public String urlGetFavorite;
	public String urlAddFavorite;
	public String urlAddLike;
	public String urlAddReadOnce;
	public String urlGetLike;
	public String urlGetReadOnes;
	public String urlSyncLocalFavor;

	public JsonURL(String comm, String userid, String topicid, String tagid,
			String topicids) {
		this.comm = comm;
		this.userid = userid;
		this.topicid = topicid;
		this.tagid = tagid;
		this.topicids = topicids;
		urlGetUserInfo = "comm=" + this.comm + "&ctype=1&userid=" + this.userid;
		urlGetFavorite = "comm=" + this.comm + "&ctype=1&userid=" + this.userid
				+ "&num=1";
		urlAddFavorite = "comm=" + this.comm + "&ctype=1&userid=" + this.userid
				+ "&topicid=" + this.topicid;
		urlAddLike = "comm=" + this.comm + "&ctype=1&userid=" + this.userid
				+ "&topicid=" + this.topicid + "&tagid=" + this.tagid;
		urlAddReadOnce = "comm=" + this.comm + "&ctype=1&userid=" + this.userid
				+ "&topicid=" + this.topicid + "&tagid=" + this.tagid;
		urlGetLike = "comm=" + this.comm + "&ctype=1&topicid=" + this.topicid;
		urlSyncLocalFavor = "comm=" + this.comm + "&ctype=1&userid="
				+ this.userid + "&topicids=" + this.topicids;
		urlGetReadOnes = "comm=" + this.comm + "&ctype=1&userid=" + this.userid
				+ "&num=1" + "&tag=" + this.tagid;
	}

}
