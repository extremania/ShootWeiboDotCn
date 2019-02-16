package com.github.extremania;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.extermania.domain.Fan;
import com.github.extermania.domain.Me;
import com.github.extermania.domain.Tagasm;

public class Utils {

	private static final String USER_AGENT = "User-Agent";
	private static final String USER_AGENT_VAL = "Mozilla/5.0 (Windows NT 6.1; Trident/7.0; rv:11.0) like Gecko";
	private static final String REFERER = "Referer";
	private static final String REFERER_VAL = "http://weibo.cn/";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_TYPE_VAL = "application/x-www-form-urlencoded";
	private static final int TIMEOUT = 30000;

	private static final int MAX_DAY = 7;

	private static final String URL_LOGIN = "http://login.weibo.cn/login/";

	private static final Map<String, Integer> PINT_MAP = new HashMap<String, Integer>();
	static {

	}

	public static final Me getMe(String username, String password) throws IOException {
		CookieWrapper cookie = getCookies(username, password);

		boolean login = newlogin(cookie);
		if (!login) {
			return null;
		}

		String gsid = cookie.getGsid();
		if (gsid == null) {
			return null;
		}
		String stuidStr = getStuid(cookie);
		if (stuidStr == null) {
			return null;
		}
		String[] stuid = StringUtils.split(stuidStr, "@");
		String st = stuid[0];
		String uid = stuid[1];
		Integer pint = PINT_MAP.get(username);
		if (pint == null) {
			pint = 0;
		}
		Me me = new Me(gsid, uid, st, username, password, pint, cookie.getCookies(), 1);
		return me;
	}

	private static final List<String> COMMENTS = parseFile("comment.txt");
	private static final List<String> COMMENTS_2 = parseFile("comment2.txt");
	private static final List<String> EXPRESSIONS = parseFile("expression.txt");
	private static final List<String> FANS = parseFile("fan.txt");

	private static final Map<String, List<String>> DR_MAP = new HashMap<String, List<String>>();
	static {
		DR_MAP.put("", refineFans(0, "DR: "));
		DR_MAP.put("15893746171", refineFans(1, "DR: "));
		DR_MAP.put("13570018870", refineFans(2, "DR: "));
		DR_MAP.put("15914362557", refineFans(3, "DR: "));
		DR_MAP.put("15127264977", refineFans(4, "DR: "));
		DR_MAP.put("18346251360", refineFans(5, "DR: "));
		DR_MAP.put("13416275269", refineFans(6, "DR: "));
	}
	private static final Map<String, List<String>> NDR_MAP = new HashMap<String, List<String>>();
	static {
		NDR_MAP.put("", refineFans(0, "NDR: "));
		NDR_MAP.put("15893746171", refineFans(1, "NDR: "));
		NDR_MAP.put("13570018870", refineFans(2, "NDR: "));
		NDR_MAP.put("15914362557", refineFans(3, "NDR: "));
		NDR_MAP.put("15127264977", refineFans(4, "NDR: "));
		NDR_MAP.put("18346251360", refineFans(5, "NDR: "));
		NDR_MAP.put("13416275269", refineFans(6, "NDR: "));
	}

	private static final Set<String> allFans = new HashSet<String>();
	static {
		for (List<String> value : DR_MAP.values()) {
			allFans.addAll(value);
		}
		for (List<String> value : NDR_MAP.values()) {
			allFans.addAll(value);
		}
	}

	static {
		// System.out.println(DR_MAP.get("extremania@126.com"));
		// System.out.println(NDR_MAP.get("extremania@126.com"));
	}

	private static List<String> refineFans(int i, String key) {
		List<String> fans = parseFile("fan/" + i + ".txt");
		List<String> drFans = new ArrayList<String>();
		for (String fan : fans) {
			if (StringUtils.startsWith(fan, key)) {
				drFans.add(StringUtils.replace(fan, key, ""));
			}
		}
		return drFans;
	}

	private static final Properties CONFIG = getProperties("config.properties");
	private static final boolean __USE_EXPRESSION__ = BooleanUtils.toBoolean(CONFIG.getProperty("expression"));
	private static final int __RANDOM_FAN_NUM__ = Integer.valueOf(CONFIG.getProperty("random_fan"));
	private static final int __FAN_NUM__ = Integer.valueOf(CONFIG.getProperty("fan"));
	public static final Map<String, String> USER = new LinkedHashMap<String, String>();
	public static final Map<String, String> GISD = new LinkedHashMap<String, String>();
	static {
		List<String> users = parseFile("user.txt");
		for (String user : users) {
			String[] parts = StringUtils.split(user, ",");
			USER.put(parts[0], parts[1]);
			if (parts.length > 2) {
				GISD.put(parts[0], parts[2]);
			}
		}
	}
	public static final int __BIAS__ = Integer.valueOf(CONFIG.getProperty("bias"));

	private static final Random random = new Random();

	private static File[][] PIC_FILES;

	static {
		// URL PIC_URL_1 = Utils.class.getResource("/pic/1");
		// URL PIC_URL_2 = Utils.class.getResource("/pic/2");
		// URL PIC_URL_3 = Utils.class.getResource("/pic/3");
		// URL PIC_URL_4 = Utils.class.getResource("/pic/4");

		File PIC_DIR_1 = new File("G:\\pic\\1");
		File PIC_DIR_2 = new File("G:\\pic\\2");
		File PIC_DIR_3 = new File("G:\\pic\\3");
		File PIC_DIR_4 = new File("G:\\pic\\4");
		if (!PIC_DIR_1.exists()) {
			PIC_DIR_1 = new File("C:\\pic\\1");
			PIC_DIR_2 = new File("C:\\pic\\2");
			PIC_DIR_3 = new File("C:\\pic\\3");
			PIC_DIR_4 = new File("C:\\pic\\4");
		}
		File[] PIC_FILES_1 = PIC_DIR_1.listFiles();
		File[] PIC_FILES_2 = PIC_DIR_2.listFiles();
		File[] PIC_FILES_3 = PIC_DIR_3.listFiles();
		File[] PIC_FILES_4 = PIC_DIR_4.listFiles();
		PIC_FILES = new File[][] { PIC_FILES_1, PIC_FILES_2, PIC_FILES_3, PIC_FILES_4 };

	}

	public static synchronized String getComment(String user, String topic, String cp, List<Fan> fans) {
		String comment = COMMENTS.get(random.nextInt(COMMENTS.size()));
		String expression = "";
		if (random.nextBoolean()) {
			expression = __USE_EXPRESSION__ ? EXPRESSIONS.get(random.nextInt(EXPRESSIONS.size())) : "";
		}
		Collections.shuffle(fans);

		List<String> drs = DR_MAP.get(user);
		List<String> ndrs = NDR_MAP.get(user);
		if (drs == null) {
			drs = DR_MAP.get("");
		}
		if (ndrs == null) {
			ndrs = NDR_MAP.get("");
		}
		Collections.shuffle(drs);
		Collections.shuffle(ndrs);

		// Collections.shuffle(FANS);

		List<Fan> subFans = fans.size() > __RANDOM_FAN_NUM__ ? fans.subList(0, __RANDOM_FAN_NUM__) : fans;
		List<String> subFans2 = drs.size() > (__FAN_NUM__) - subFans.size()
				? drs.subList(0, (__FAN_NUM__) - subFans.size())
				: drs;
		// subFans2.add(ndrs.get(0));

		List<String> pp = new ArrayList<String>();

		for (Fan f : subFans) {
			pp.add("@" + f.getName());
		}
		for (String f : subFans2) {
			pp.add("@" + f);
		}
		Collections.shuffle(pp);

		if (StringUtils.length(cp) > 20) {
			cp = "";
		}

		return StringUtils.join(pp.iterator(), " ") + " " + topic + comment + " " + cp + expression;
	}

	public static synchronized String getComment2(List<Fan> fans) {
		String expression = "";
		if (random.nextBoolean()) {
			expression = __USE_EXPRESSION__ ? EXPRESSIONS.get(random.nextInt(EXPRESSIONS.size())) : "";
		}
		// String fan = "@" + fans.get(random.nextInt(fans.size())).getName();
		String comment = COMMENTS_2.get(random.nextInt(COMMENTS_2.size()));

		// return comment + expression + fan;
		return comment + expression;
	}

	private static Properties getProperties(String fileName) {
		Properties prop = new Properties();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream("/" + fileName)));
			prop.load(br);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return prop;
	}

	public static List<String> parseFile(File file) {
		List<String> lines = new ArrayList<String>();

		String line = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return lines;
	}

	public static List<String> parseFile(String fileName) {
		List<String> lines = new ArrayList<String>();

		String line = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream("/" + fileName)));
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return lines;
	}

	public static void add(Me me, String uid) {
		attention(me, uid, "add");
	}

	public static void del(Me me, String uid) {
		attention(me, uid, "del");
	}

	private static final int MAXIMUM_PAGE = 10;

	public static List<Tagasm> getTags(Me me, String diaosi, int number, JTextArea ta) {
		List<Tagasm> tagasms = new ArrayList<Tagasm>();
		for (int i = 1; i <= MAXIMUM_PAGE; i++) {
			try {
				String url = "http://weibo.cn/" + diaosi + "?page=" + i + "&gsid=" + me.getGsid() + "&vt=4&lret=1";
				List<Tagasm> tags = getTags(url, me);
				if (ta != null) {
					for (Tagasm tagasm : tags) {
						ta.append(tagasm + "\n");
					}
				}
				tagasms.addAll(tags);
			} catch (Exception e) {
				e.printStackTrace();
				ta.append(me.getUsername() + "---发生错误：" + e + "\n");
			}
			if (tagasms.size() >= number) {
				break;
			}
		}
		return tagasms;
	}

	private static final String YEAR = (new SimpleDateFormat("yyyy")).format(new Date());

	public static List<Tagasm> getTags(String url, Me me) {
		List<Tagasm> tagasms = new ArrayList<Tagasm>();

		DateFormat format = new SimpleDateFormat("yyyyMM月dd日");

		Document doc = simpleGet(url, me, null);

		String docStr = doc.toString();

		boolean isGd = docStr.indexOf("广东") > 0;

		int idx = docStr.indexOf("uid=");
		String tempUid = "";
		if (idx > 0) {
			tempUid = docStr.substring(idx + 4, idx + 14);
		}

		Elements elements = doc.select("div[class=c]");
		if (elements != null) {
			for (Element element : elements.toArray(new Element[elements.size()])) {

				if (!isGd && tagasms.size() > 0) {
					return tagasms;
				}

				String string = element.toString();
				// System.out.println("===========================");
				// System.out.println(string);
				// System.out.println("===========================");
				boolean isVip = string.indexOf("alt=\"V\"") > 0;
				String tag = "";
				String reason = "";
				String topic = "";

				boolean atSomebody = string.indexOf("@") > 0;
				boolean isToday = string.indexOf("今天") > 0 || string.indexOf("分钟前") > 0;

				int tInd = string.indexOf("<span class=\"ct\">");
				String tStr = string.substring(tInd + 17, tInd + 23);
				Date tDate = null;
				try {
					if (tStr.indexOf("月") > 0 && tStr.indexOf("日") > 0) {
						System.out.println(YEAR + tStr);
						tDate = format.parse(YEAR + tStr);
						tDate = DateUtils.addDays(tDate, MAX_DAY);
						// System.out.println(format.format(tDate));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

				boolean badDate = (tDate != null && tDate.getTime() < (new Date()).getTime());
				if (badDate) {
					// System.out.println("badDate:" + format.format(tDate));
					return tagasms;
				}

				boolean validDate = isToday || !badDate;

				System.out.println("tDate: " + tDate);
				System.out.println("validDate: " + validDate + " isToday: " + isToday);

				int index1 = string.indexOf("comment");
				int index2 = string.indexOf("转发理由");
				int index3 = string.indexOf("http://weibo.cn/attitude", index2 + 12);
				int index4 = string.indexOf("#");
				int index5 = string.indexOf("#", index4 + 1);
				if (index4 > 0 && index5 > 0) {
					topic = string.substring(index4, index5 + 1);
					if (topic.startsWith("#cmtfrm")) {
						topic = "";
					}
				}
				boolean danger = false;
				if (string.contains("谢谢") || string.contains("恭喜")) {
					danger = true;
				}
				if (index1 > 0 && index2 > 0 && index3 > 0) {
					tag = string.substring(index1 + 8, index1 + 8 + 9);
					String[] parts = delHTMLTag(string.substring(index2 + 12, index3)).split("[\\r\\n]+");
					_REMOVE: for (String part : parts) {
						for (String r : REMOVABLE) {
							if (part.contains(r)) {
								continue _REMOVE;
							}
						}
						reason = reason + part;
					}
					reason = StringEscapeUtils.unescapeHtml3(reason);
				}

				if (isVip) {
					try {
						String url2 = "/follow?";
						int idx1 = string.indexOf("href=\"");
						String substring = string.substring(idx1);
						int idx2 = substring.indexOf("\">");
						String iurl = substring.substring(6, idx2);

						iurl = StringUtils.replace(iurl, "&amp;", "&");
						Document idoc = simpleGet(iurl, null, null);
						String iString = idoc.toString();
						int idx3 = iString.indexOf(url2);
						// System.out.println("iString:" + iString);
						String uid = iString.substring(idx3 - 10, idx3);
						int idx4 = iString.indexOf("<title>");
						int idx5 = iString.indexOf("</title>");
						String title = iString.substring(idx4 + 7, idx5);

						reason = StringUtils.remove(reason, topic);
						Tagasm tagasm = new Tagasm(isVip, tag, reason, topic, uid, title);
						if (!StringUtils.equals(uid, tempUid) && atSomebody && validDate && !danger) {
							tagasms.add(tagasm);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		return tagasms;
	}

	private static final String[] REMOVABLE = { "@", "<a href=", "英子", "蚊滋", "晕晕炖炖", "葵花", "小nat", "珊珊", "薇薇" };

	public static String delHTMLTag(String htmlStr) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
		String regEx_html = "<[^>]+>";

		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll("");

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll("");

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll("");

		htmlStr = StringUtils.replace(htmlStr, "&middot;", "·");

		return htmlStr.trim();
	}

	private static void setHeader(HttpPost post) {
		post.setHeader(USER_AGENT, USER_AGENT_VAL);
		post.setHeader(REFERER, REFERER_VAL);
		post.setHeader(CONTENT_TYPE, CONTENT_TYPE_VAL);

	}

	private static void setHeader2(HttpPost post) {
		post.setHeader(USER_AGENT, USER_AGENT_VAL);
		post.setHeader("Referer", "http://weibo.cn/mblog/sendmblog");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("Content-Type", "multipart/form-data; boundary=---------------------------7dda23b2c0236");
	}

	public static void superMode(Me me) throws Exception {
		String url = "http://weibo.cn/mblog/sendmblog?st=" + me.getSt() + "&vt=4" /*
																					 * + "&gsid=" + me.getGsid()
																					 */;
		HttpPost post = new HttpPost(url);
		setHeader(post);

		MultipartEntity multiEntity = new MultipartEntity();
		multiEntity.addPart("composer", new StringBody("高级", Charset.forName("UTF-8")));
		multiEntity.addPart("content", new StringBody("1", Charset.forName("UTF-8")));
		multiEntity.addPart("rl", new StringBody(String.valueOf(me.getRl())));
		post.setEntity(multiEntity);

		DefaultHttpClient client = new DefaultHttpClient();

		List<String> cookieStrs = new ArrayList<String>();
		for (Cookie cookie : me.getCookies()) {
			client.getCookieStore().addCookie(cookie);
			String cookieStr = cookie.getName() + "=" + cookie.getValue();
			cookieStrs.add(cookieStr);
		}
		// post.setHeader("Referer", "http://weibo.cn/?pos=65&s2w=admin&vt=4");
		post.setHeader("Host", "weibo.cn");
		post.setHeader("Cookie", StringUtils.join(cookieStrs, "; "));
		try {
			HttpResponse resp = client.execute(post);
			System.out.println(resp);
			System.out.println(convertStreamToString(resp.getEntity().getContent()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		post.abort();

	}

	public static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "/n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static void sendmblog(Me me, String content) throws Exception {
		// System.setProperty("http.proxyHost", "localhost");
		// System.setProperty("http.proxyPort", "8888");
		// System.setProperty("https.proxyHost", "localhost");
		// System.setProperty("https.proxyPort", "8888");
		// superMode(me);

		String url = "http://weibo.cn/mblog/sendmblog?st=" + me.getSt();
		// + "&vt=4" + "&rl=1"/*
		// * + "&gsid=" + me.getGsid()
		// */;

		HttpPost post = new HttpPost(url);
		setHeader(post);

		// MultipartEntity multiEntity = new MultipartEntity();
		// multiEntity.addPart("content", new StringBody(content, Charset
		// .forName("UTF-8")));
		// multiEntity.addPart("visible", new StringBody("0"));
		// ContentBody contentBody = new FileBody(PIC_FILES[me.getPint()][random
		// .nextInt(PIC_FILES[me.getPint()].length)],
		// "application/octet-stream");
		// multiEntity.addPart("pic", contentBody);
		//
		// post.setEntity(multiEntity);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("content", content));
		nvps.add(new BasicNameValuePair("rl", "0"));
		post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

		DefaultHttpClient client = new DefaultHttpClient();
		// HttpHost proxy = new HttpHost("localhost", 8888);
		// client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		// proxy);
		List<String> cookieStrs = new ArrayList<String>();
		for (Cookie cookie : me.getCookies()) {
			client.getCookieStore().addCookie(cookie);
			String cookieStr = cookie.getName() + "=" + cookie.getValue();
			cookieStrs.add(cookieStr);
		}
		// post.setHeader("Referer", "http://weibo.cn/?pos=65&s2w=admin&vt=4");
		post.setHeader("Host", "weibo.cn");
		post.setHeader("Cookie", StringUtils.join(cookieStrs, "; "));
		// me.setRl(me.getRl() + 1);
		try {
			HttpResponse resp = client.execute(post);
			System.out.println(resp);
			System.out.println(convertStreamToString(resp.getEntity().getContent()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		post.abort();

	}

	public static void attitude(Me me, String id) {
		String url = "http://weibo.cn/attitude/" + id + "/add?uid=" + me.getUid() + "&rl=0&st=" + me.getSt();
		simpleGet(url, me, null);
	}

	public static void comment(Me me, String content, String id, String srcuid) throws Exception {
		String url = "http://weibo.cn/comments/addcomment?vt=4&gsid=" + me.getGsid() + "&st=" + me.getSt();

		Map<String, String> data = new HashMap<String, String>();
		data.put("rl", "1");
		data.put("content", content);
		data.put("id", id);
		data.put("srcuid", srcuid);

		Connection conn = getPostConnection(url, me.getCookies(), data);
		conn.get();
	}

	public static void dort(Me me, String content, String id) throws Exception {

		String url = "http://weibo.cn/repost/dort/" + id + "?vt=4&gid=10001&gsid=" + me.getGsid() + "&st=" + me.getSt();

		Map<String, String> data = new HashMap<String, String>();
		data.put("rl", "0");
		data.put("content", content);
		data.put("act", "dort");
		data.put("id", id);
		data.put("rtrootcomment", "on");

		Connection conn = getPostConnection(url, me.getCookies(), data);
		conn.get();
	}

	public static Connection getPostConnection(String url, List<Cookie> cookies, Map<String, String> data) {

		Map<String, String> cookieMap = new HashMap<String, String>();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookieMap.put(cookie.getName(), cookie.getValue());
			}
		}

		Connection conn = Jsoup.connect(url) //
				.userAgent(USER_AGENT_VAL) //
				.referrer(REFERER_VAL)//
				.cookies(cookieMap)//
				.method(Method.POST) //
				.timeout(TIMEOUT) //
				.ignoreContentType(true);

		if (data != null) {
			for (Map.Entry<String, String> dataEntry : data.entrySet()) {
				conn.data(dataEntry.getKey(), dataEntry.getValue());
			}
		}

		return conn;
	}

	public static List<Fan> getFan(Me me, int total, int page) throws Exception {

		List<Fan> fanList = new ArrayList<Fan>();

		String url = "http://weibo.cn/" + me.getUid() + "/fans?vt=4&gsid=" + me.getGsid() + "&st=" + me.getSt();

		Map<String, String> data = new HashMap<String, String>();
		data.put("mp", String.valueOf(total));
		data.put("page", String.valueOf(page));

		Connection conn = getPostConnection(url, me.getCookies(), data);
		Document doc = conn.get();
		fanList.addAll(getFan(doc));

		return fanList;
	}

	private static List<Fan> getFan(Document doc) {
		List<Fan> fanList = new ArrayList<Fan>();
		try {
			String url = "http://weibo.cn/";
			String url2 = "sinaimg.cn/";
			Elements elements = doc.select("tbody");
			for (Element element : elements.toArray(new Element[elements.size()])) {
				try {
					String string = element.toString();
					int idx1 = string.indexOf(url);
					int idx2 = string.indexOf("?");
					String uid = string.substring(idx1 + url.length(), idx2);
					idx1 = string.indexOf(url2);
					String uid2 = string.substring(idx1 + url2.length(), idx1 + url2.length() + 10);
					boolean isDr = StringUtils.contains(string, "alt=\"达人\"");
					boolean isV = StringUtils.contains(string, "alt=\"V\"");
					boolean isHxgz = StringUtils.contains(string, "相互关注");
					string = delHTMLTag(string);
					String[] fs = StringUtils.splitByWholeSeparator(string, "粉丝");
					String name = fs[0];
					name = StringUtils.remove(name, "&nbsp;(备注)");

					int idx = fs[1].indexOf("人");
					if (idx > 0) {
						String fans = fs[1].substring(0, idx);
						Fan fan = new Fan(name, Integer.valueOf(fans), uid, uid2, isDr, isV, isHxgz);
						fanList.add(fan);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fanList;

	}

	public static Document simpleGet(String url, Me me, CookieWrapper cookieWrapper) {
		try {
			List<Cookie> cookies = null;
			if (cookieWrapper != null) {
				cookies = cookieWrapper.getCookies();
			} else if (me != null) {
				cookies = me.getCookies();
			}
			if (cookies != null) {
				Map<String, String> cookieMap = new HashMap<String, String>();
				for (Cookie cookie : cookies) {
					cookieMap.put(cookie.getName(), cookie.getValue());
				}
				Response response = Jsoup.connect(url).header(USER_AGENT, USER_AGENT_VAL).cookies(cookieMap).execute();
				Map<String, String> newCookies = response.cookies();
				if (newCookies != null) {
					for (Map.Entry<String, String> entry : newCookies.entrySet()) {
						BasicClientCookie newCookie = new BasicClientCookie(entry.getKey(), entry.getValue());
						newCookie.setDomain(".weibo.cn");
						newCookie.setExpiryDate(new Date(new Date().getTime() + 2400000));
						newCookie.setPath("/");
						// setSecure(boolean)
						// setValue(String)
						// setVersion(int)
						if (me != null) {
							me.getCookies().add(newCookie);
						}
						cookieMap.put(entry.getKey(), entry.getValue());
					}
				}
				return Jsoup.connect(url).header(USER_AGENT, USER_AGENT_VAL).cookies(cookieMap).get();
			} else {
				return Jsoup.connect(url).header(USER_AGENT, USER_AGENT_VAL).get();
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void attention(Me me, String uid, String type) {
		String url = "http://weibo.cn/attention/" + type + "?st=" + me.getSt() + "&uid=" + uid + "&rl=0&vt=4&gsid="
				+ me.getGsid();

		System.out.println(url);

		if ("del".equals(type)) {
			url = url + "&act=delc";
		}

		simpleGet(url, me, null);
	}

	public static int getFanPage(Me me) {
		String url = "http://weibo.cn/" + me.getUid() + "/fans?vt=4&gsid=" + me.getGsid();
		Document doc = simpleGet(url, me, null);
		if (doc == null) {
			return 0;
		}
		Element element = doc.select("input[name=mp]").first();
		String page = element.attr("value");
		return Integer.valueOf(page);
	}

	public static String getStuid(CookieWrapper cookie) {
		String gsid = cookie.getGsid();

		if (gsid == null) {
			throw new IllegalArgumentException("gsid is null");
		}
		// wait a wait, don't call so quick
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String url = "http://weibo.cn/at/weibo?rl=1&vt=4";
		Document doc = simpleGet(url, null, cookie);
		System.out.println(doc);
		if (doc == null || doc.toString().indexOf("@提到我的") < 0) {
			return null;
		}

		String docString = doc.toString();
		int fuidInx = docString.indexOf("fuid=");
		String uid = docString.substring(fuidInx + 5, fuidInx + 15);
		int stIdx = docString.indexOf("st=");
		String st = docString.substring(stIdx + 3, stIdx + 9);

		return st + "@" + uid;
	}

	public static CookieWrapper getCookies(String username, String password) throws IOException {
		// String getgsid = null;// GISD.get(username);

		/*
		 * if (getgsid != null) { return getgsid; }
		 */

		Document doc = simpleGet(URL_LOGIN, null, null);
		if (doc == null) {
			return null;
		}

		Element form = null, pwnd = null, vk = null;
		String rand = null, spwnd = null, svk = null;
		form = doc.select("form[method=post]").first();
		pwnd = doc.select("input[type=password]").first();
		vk = doc.select("input[name=vk]").first();
		if (form == null || pwnd == null || vk == null) {
			return null;
		}

		rand = form.attr("action");
		spwnd = pwnd.attr("name");
		svk = vk.attr("value");
		if (rand == null || spwnd == null || svk == null) {
			return null;
		}

		CookieWrapper cookie = new CookieWrapper();

		try {
			String url = "http://login.weibo.cn/login/" + rand;
			HttpPost post = new HttpPost(url);
			setHeader(post);

			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("mobile", username));
			qparams.add(new BasicNameValuePair(spwnd, password));
			qparams.add(new BasicNameValuePair("remember", "on"));
			qparams.add(new BasicNameValuePair("backURL", "http://weibo.cn/dpool/ttt/home.php"));
			qparams.add(new BasicNameValuePair("backTitle", "新浪微博"));
			qparams.add(new BasicNameValuePair("vk", svk));
			qparams.add(new BasicNameValuePair("submit", "登录"));
			UrlEncodedFormEntity params = new UrlEncodedFormEntity(qparams, "UTF-8");
			post.setEntity(params);
			DefaultHttpClient client = new DefaultHttpClient();
			// for (Cookie cookie : me.getCookies()) {
			// client.getCookieStore().addCookie(cookie);
			// }
			/* HttpResponse resp = */client.execute(post);
			post.abort();

			List<Cookie> cookies = client.getCookieStore().getCookies();
			cookie.setCookies(cookies);
			for (Cookie cooky : cookies) {
				if ("gsid_CTandWM".equals(cooky.getName())) {
					String gsid = cooky.getValue();
					cookie.setGsid(gsid);
				}
			}
			// BasicClientCookie newCookie = new BasicClientCookie("SUHB",
			// "0d4-37_5HUzbmg");
			// newCookie.setDomain(".weibo.cn");
			// newCookie.setExpiryDate(new Date(new Date().getTime() +
			// 2400000));
			// newCookie.setPath("/");
			// cookies.add(newCookie);

			return cookie;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean newlogin(CookieWrapper cookie) {
		Document doc = simpleGet("http://newlogin.sina.cn/crossDomain/?g=" + cookie.getGsid() + "&t="
				+ (new Date()).getTime() + "&m=ce01&r=&u=http%3A%2F%2Fweibo.cn%2F%3Fs2w%3Dlogin%26gsid%3D4"
				+ cookie.getGsid() + "%26vt%3D4&cross=1&vt=4", null, cookie);
		String docString = doc.toString();
		return docString.contains("成功");
	}

	private static int getFollowPage(Me me) {
		String url = "http://weibo.cn/" + me.getUid() + "/follow?vt=4&gsid=" + me.getGsid();
		Document doc = simpleGet(url, me, null);
		Element input = doc.select("input[name=mp]").first();
		String pageStr = input.attr("value");
		int page = Integer.parseInt(pageStr);
		return page;
	}

	public static List<String> getFollows(Me me, int total) throws Exception {

		String url = "http://weibo.cn/" + me.getUid() + "/follow?vt=4&gsid=" + me.getGsid() + "&st=" + me.getSt();

		List<String> uids = new ArrayList<String>();
		int pageTotal = getFollowPage(me);
		int page = pageTotal;
		while (uids.size() < total && page > 1) {

			Map<String, String> data = new HashMap<String, String>();
			data.put("mp", String.valueOf(pageTotal));
			data.put("page", String.valueOf(page));

			Connection conn = getPostConnection(url, me.getCookies(), data);
			Document doc = conn.get();

			uids.addAll(getFollow(doc));

			page--;

		}
		return uids;
	}

	private static List<String> getFollow(Document doc) {
		List<String> followList = new ArrayList<String>();
		Elements elements = doc.select("tbody");
		for (Element element : elements.toArray(new Element[elements.size()])) {
			try {
				String string = element.toString();
				boolean majia = false;
				for (String fan : allFans) {
					if (StringUtils.contains(string, fan)) {
						majia = true;
						break;
					}
				}
				if (!majia && !StringUtils.contains(string, "相互关注")) {
					int idx = string.indexOf("sinaimg.cn/");
					String uid = string.substring(idx + 11, idx + 21);
					followList.add(uid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return followList;
	}

	public static void main(String[] args) throws Exception {
		Me me = getMe("", // TODO: test user
				"" // TODO: test password
		);
		List<String> list = getFollows(me, 10);
		for (String str : list) {
			del(me, str);
		}

	}
}
