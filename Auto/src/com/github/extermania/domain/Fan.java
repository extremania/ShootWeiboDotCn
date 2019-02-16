package com.github.extermania.domain;

public class Fan {

	private final String name;
	private final int fans;
	private final String uid;
	private final String uid2;
	private final boolean isDr;
	private final boolean isV;
	private final boolean isHxgz;

	public Fan(String name, int fans, String uid, String uid2, boolean isDr,
			boolean isV, boolean isHxgz) {
		super();
		this.name = name;
		this.fans = fans;
		this.uid = uid;
		this.uid2 = uid2;
		this.isDr = isDr;
		this.isV = isV;
		this.isHxgz = isHxgz;
	}

	@Override
	public String toString() {
		return "Fan [fans=" + fans + ", isDr=" + isDr + ", isHxgz=" + isHxgz
				+ ", isV=" + isV + ", name=" + name + ", uid=" + uid
				+ ", uid2=" + uid2 + "]";
	}

	public String getUid2() {
		return uid2;
	}

	public String getName() {
		return name;
	}

	public int getFans() {
		return fans;
	}

	public String getUid() {
		return uid;
	}

	public boolean isDr() {
		return isDr;
	}

	public boolean isV() {
		return isV;
	}

	public boolean isHxgz() {
		return isHxgz;
	}

}
