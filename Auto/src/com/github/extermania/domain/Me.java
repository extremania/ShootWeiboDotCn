package com.github.extermania.domain;

import java.util.List;

import org.apache.http.cookie.Cookie;

public class Me {

	private final String gsid;
	private final String uid;
	private final String st;
	private final String username;
	private final String password;
	private final int pint;
	private final List<Cookie> cookies;
	private int rl;

	public Me(String gsid, String uid, String st, String username,
			String password, int pint, List<Cookie> cookies, int rl) {
		super();
		this.gsid = gsid;
		this.uid = uid;
		this.st = st;
		this.username = username;
		this.password = password;
		this.pint = pint;
		this.cookies = cookies;
		this.rl = rl;
	}

	public int getPint() {
		return pint;
	}

	public String getGsid() {
		return gsid;
	}

	public String getUid() {
		return uid;
	}

	public String getSt() {
		return st;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public int getRl() {
		return rl;
	}

	public void setRl(int rl) {
		this.rl = rl;
	}

	@Override
	public String toString() {
		return "Me [cookies=" + cookies + ", gsid=" + gsid + ", password="
				+ password + ", pint=" + pint + ", rl=" + rl + ", st=" + st
				+ ", uid=" + uid + ", username=" + username + "]";
	}

}
