package com.lynxsolutions.utils;

import java.io.Serializable;

import android.net.Uri;

public class Note implements Serializable {
	private String id;
	private String title;
	// private String subTitle;
	private String type;

	private String tag;
	private String uri;
	private String notificationDate;

	public enum TYPE {
		IMAGE, TEXT, VIDEO, VOICE
	}

	public enum TAG {
		ALL, HOME, WORK
	}

	public Note() {

	}

	public Note(String id, String title, String type, String tag, String uri,
			String date) {
		super();
		this.id = id;
		this.title = title;
		this.type = type;
		this.tag = tag;
		this.uri = uri;
		this.notificationDate = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// public String getSubTitle() {
	// return subTitle;
	// }
	//
	// public void setSubTitle(String subTitle) {
	// this.subTitle = subTitle;
	// }

	public String getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(String notificationDate) {
		this.notificationDate = notificationDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
