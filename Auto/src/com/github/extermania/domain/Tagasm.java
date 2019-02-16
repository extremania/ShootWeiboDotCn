package com.github.extermania.domain;

public class Tagasm {
	private final boolean isVip;
	private final String tag;
	private final String reason;
	private final String topic;
	private final String uid;
	private final String title;

	public Tagasm(boolean isVip, String tag, String reason, String topic,
			String uid, String title) {
		super();
		this.isVip = isVip;
		this.tag = tag;
		this.reason = reason;
		this.topic = topic;

		this.uid = uid;
		this.title = title;
	}

	public String getUid() {
		return uid;
	}

	public boolean isVip() {
		return isVip;
	}

	public String getTag() {
		return tag;
	}

	public String getReason() {
		return reason;
	}

	public String getTopic() {
		return topic;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return "Tagasm [isVip=" + isVip + ", reason=" + reason + ", tag=" + tag
				+ ", title=" + title + ", topic=" + topic + ", uid=" + uid
				+ "]";
	}

}