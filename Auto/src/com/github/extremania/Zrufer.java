package com.github.extremania;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.JCheckBox;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;

import com.github.extermania.domain.Fan;
import com.github.extermania.domain.Me;
import com.github.extermania.domain.Tagasm;

public class Zrufer extends Thread {

	private JTextArea logger;

	private JCheckBox checkBox;

	// private Map<String, Integer> diaoSiMap;

	private List<String> sikwans;

	private Integer time;

	private static final String SP = "================================";

	private static final Random random = new Random();

	private String username;

	private String password;

	private boolean atOther;

	private boolean yc;

	private boolean onlyYc;

	private boolean attact;

	private Collection<String> filters;

	public void setSikwans(List<String> sikwans) {
		this.sikwans = sikwans;
	}

	public void setAttact(boolean attact) {
		this.attact = attact;
	}

	public void setAtOther(boolean atOther) {
		this.atOther = atOther;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setLogger(JTextArea logger) {
		this.logger = logger;
	}

	// public void setDiaoSiMap(Map<String, Integer> diaoSiMap) {
	// this.diaoSiMap = diaoSiMap;
	// }

	public void setTime(Integer time) {
		this.time = time;
	}

	public void setCheckBox(JCheckBox checkBox) {
		this.checkBox = checkBox;
	}

	public void setYc(boolean yc) {
		this.yc = yc;
	}

	public void setOnlyYc(boolean onlyYc) {
		this.onlyYc = onlyYc;
	}

	public void setFilters(Collection<String> filters) {
		this.filters = filters;
	}

	private int runCount = 1;

	private static final int MAX_TAG = 50; // TODO

	@Override
	public void run() {

		try {
			if (time == null || time <= 0) {
				return;
			}

			long startTime = System.currentTimeMillis();
			StringBuffer sb = new StringBuffer("");
			sb.append(username).append("---username: ").append(username)
					.append("\n");
			sb.append(username + "---password: ********" + "\n");
			sb.append(username + "---转发时间：" + time + "(" + Utils.__BIAS__ + ")"
					+ "\n");
			sb.append(username + "---屌丝个数：" + sikwans.size() + "\n");
			for (String sikwan : sikwans) {
				sb.append(username + "---屌丝: " + sikwan + "\n");
			}

			// for (Map.Entry<String, Integer> entry : diaoSiMap.entrySet()) {
			// sb.append(username + "---屌丝/微博数：" + entry.getKey() + "/"
			// + entry.getValue() + "\n");
			// }
			sb.append(SP + "\n");
			logger.append(sb.toString());

			Me me = null;
			try {
				me = Utils.getMe(username, password);
			} catch (Exception e) {
				e.printStackTrace();
				logger.append(username + "---登录失败: " + e + "\n" + SP + "\n");
			}
			if (me != null) {
				logger.append(username + "---登录成功\n" + me + "\n" + SP + "\n");
			} else {
				logger.append(username + "---登录失败（新浪抽风了，删除cookie，重启机器看看）\n"
						+ SP + "\n");
				checkBox.setText(username + "（渣丝渣）");
				return;
			}

			Utils.simpleGet("http://weibo.cn/msg/?unread=100", me, null);

			sb = new StringBuffer(SP + "\n" + username + "---预备粉丝：\n");
			List<Fan> rawfans = new ArrayList<Fan>();
			if (atOther) {
				for (int i = 1; i <= 50; i++) {
					try {
						rawfans.addAll(Utils
								.getFan(me, Utils.getFanPage(me), i));
					} catch (Exception e) {
						logger.append(username + "---发生错误：" + e + "\n" + SP
								+ "\n");
					}
					// System.out.println(i);
				}
			}

			List<Fan> fans = new ArrayList<Fan>();
			// List<Fan> drs = new ArrayList<Fan>();
			// List<Fan> ndrs = new ArrayList<Fan>();
			for (Fan fan : rawfans) {
				// if (fan.isHxgz()) {
				// if (fan.isDr()) {
				// drs.add(fan);
				// } else {
				// ndrs.add(fan);
				// }
				// }

				if (fan.isHxgz() && !fan.isV()) {
					fans.add(fan);
					sb.append("@" + fan.getName() + " ");
				}

			}
			// for (Fan fant : drs) {
			// System.out.println("DR: " + fant.getName());
			// }
			// for (Fan fant : ndrs) {
			// System.out.println("NDR: " + fant.getName());
			// }
			// if (1 == 1) {
			// return;
			// }
			sb.append("\n" + SP + "\n");
			logger.append(sb.toString());

			List<Tagasm> tagasms = new ArrayList<Tagasm>();
			if (!onlyYc) {
				Collections.shuffle(this.sikwans);
				int k = 0;
				for (String sikwan : sikwans) {
					logger.append(username + "---sikwan：" + sikwan + " " + k++
							+ "\n");
					if (tagasms.size() > MAX_TAG) {
						break;
					}
					// for (Map.Entry<String, Integer> entry :
					// diaoSiMap.entrySet()) {
					List<Tagasm> ts = Utils.getTags(me, sikwan, 3, logger);
					for (Tagasm t : ts) {
						boolean valid = true;
						for (String filter : filters) {
							if (t.getReason() != null
									&& t.getReason().contains(filter)) {
								valid = false;
								break;
							}
						}
						if (valid) {
							tagasms.add(t);
						} else {
							logger.append(username + "---过滤:" + t.getReason()
									+ "\n");
						}
					}

				}
			}
			logger.append(username + "---获得标签：" + tagasms.size() + "个\n" + SP
					+ "\n");
			Collections.shuffle(tagasms);

			int i = 0;
			checkBox.setText(username + "(" + i + "/" + tagasms.size() + ")");

			if (attact) {
				for (Tagasm tagasm : tagasms) {
					try {
						String content = StringUtils.join(filters.iterator(),
								",");
						Utils.comment(me, content, tagasm.getTag(), tagasm
								.getUid());
						logger.append(username + "---攻击：" + content + "\n" + SP
								+ "\n");
					} catch (Exception e) {
						logger.append(username + "---发生错误：" + e + "\n" + SP
								+ "\n");
					} finally {
						i++;
						checkBox.setText(username + "[" + runCount + "]" + "("
								+ i + "/" + tagasms.size() + ")");
						long sleep = getSleep();
						logger.append(username + "---下一轮：" + (sleep / 1000)
								+ "秒\n" + SP + "\n");
						Thread.sleep(sleep);
					}
				}
			} else if (onlyYc) {
				int ycCount = 0;
				while (true) {
					try {
						String content = Utils.getComment2(fans);
						Utils.sendmblog(me, content);
						logger.append(username + "---发表原创：" + content + "\n"
								+ SP + "\n");
					} catch (Exception e) {
						logger.append(username + "---发生错误：" + e + "\n" + SP
								+ "\n");
					} finally {
						long sleep = getSleep();
						logger.append(username + "---下一轮：" + (sleep / 1000)
								+ "秒\n" + SP + "\n");
						checkBox.setText(username + "(" + (++ycCount) + ")");
						Thread.sleep(sleep);
					}
				}
			} else {
				for (Tagasm tagasm : tagasms) {
					try {
						String uid = tagasm.getUid();
						Utils.add(me, uid);
						logger.append(username + "---关注：" + tagasm.getTitle()
								+ "(" + uid + ")" + "\n" + SP + "\n");
						String content = Utils.getComment(username, tagasm
								.getTopic(), tagasm.getReason(), fans);
						Utils.dort(me, content, tagasm.getTag());
						logger.append(username + "---转发：" + content + "\n" + SP
								+ "\n");
						Utils.attitude(me, tagasm.getTag());

						long sleep = getSleep();

						if (yc && random.nextBoolean()) {
							Thread.sleep(sleep);
							content = Utils.getComment2(fans);
							Utils.sendmblog(me, content);
							logger.append(username + "---发表原创：" + content
									+ "\n" + SP + "\n");
						}

					} catch (Exception e) {
						logger.append(username + "---发生错误：" + e + "\n" + SP
								+ "\n");
						e.printStackTrace();
					} finally {
						i++;
						checkBox.setText(username + "[" + runCount + "]" + "("
								+ i + "/" + tagasms.size() + ")");
						long sleep = getSleep();
						logger.append(username + "---下一轮：" + (sleep / 1000)
								+ "秒\n" + SP + "\n");
						Thread.sleep(sleep);
					}
				}

			}
			long spend = System.currentTimeMillis() - startTime;
			logger.append(username + "---完成，总耗时：" + (spend / 1000) + "秒" + SP
					+ "\n");
			checkBox.setText(username + "[" + runCount + "]" + "(搞掂)");
			// ==================================
			logger.append("准备下一波~~~~~~~~~~~~~~~~~~~~~~~");
			Thread.sleep(30000);
			runCount++;
			// XXX run();
		} catch (Exception e) {
			logger.append(username + "---出现错误！\n" + SP + "\n");
			logger.append(e + "\n");
			checkBox.setText(username + "（渣丝渣）");
			e.printStackTrace();
		}

	}

	private long getSleep() {
		long base = time * 1000;
		long bias = random.nextInt(Utils.__BIAS__ * 1000);
		return base + bias;
	}

};
