package com.github.extremania;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JTextArea;

import com.github.extermania.domain.Me;

public class Delter extends Thread {

	private Integer follow;

	private JTextArea logger;

	private JCheckBox checkBox;

	private String username;

	private String password;

	public void setFollow(Integer follow) {
		this.follow = follow;
	}

	public void setLogger(JTextArea logger) {
		this.logger = logger;
	}

	public void setCheckBox(JCheckBox checkBox) {
		this.checkBox = checkBox;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private static final String SP = "================================";

	@Override
	public void run() {

		try {
			if (follow == null || follow <= 0) {
				return;
			}
			Me me = null;
			try {
				me = Utils.getMe(username, password);
			} catch (Exception e) {
				logger.append(username + "---µÇÂ¼Ê§°Ü: " + e + "\n" + SP + "\n");
			}
			if (me != null) {
				logger.append(username + "---µÇÂ¼³É¹¦\n" + me + "\n" + SP + "\n");
			} else {
				logger.append(username + "---µÇÂ¼Ê§°Ü£¨ÐÂÀË³é·çÁË£¬É¾³ýcookie£¬ÖØÆô»úÆ÷¿´¿´£©\n"
						+ SP + "\n");
				checkBox.setText(username + "£¨ÔüË¿Ôü£©");
				return;
			}
			List<String> follows = Utils.getFollows(me, follow);
			int i = 0;
			checkBox.setText(username + "(" + i + "/" + follows.size() + ")");
			for (String follow : follows) {
				try {
					Utils.del(me, follow);
					logger.append(username + "---É¾³ý:" + follow + "\n");
				} catch (Exception e) {
					logger.append(username + "---³öÏÖ´íÎó£¡\n" + SP + "\n");
				} finally {
					i++;
					checkBox.setText(username + "(" + i + "/" + follows.size()
							+ ")");
				}
			}
			checkBox.setText(username + "(¸ãµà)");
		} catch (Exception e) {
			logger.append(username + "---³öÏÖ´íÎó£¡\n" + SP + "\n");
			logger.append(e + "\n");
			checkBox.setText(username + "£¨ÔüË¿Ôü£©");
			e.printStackTrace();
		}
	}

}
