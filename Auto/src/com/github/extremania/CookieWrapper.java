package com.github.extremania;

import java.util.List;

import org.apache.http.cookie.Cookie;

public class CookieWrapper {

	private List<Cookie> cookies;

	private String gsid;

	// private String rand;

	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public String getGsid() {
		return gsid;
	}

	public void setGsid(String gsid) {
		this.gsid = gsid;
	}

	// public String getRand() {
	// return rand;
	// }

	// public void setRand(String rand) {
	// this.rand = rand;
	// }

}
