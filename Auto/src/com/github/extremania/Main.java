package com.github.extremania;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Main extends JPanel implements ActionListener {

	private static final long serialVersionUID = 5134599829021811385L;

	private Set<String> tags = new HashSet<String>();

	private final Map<String, String> commentMap = new HashMap<String, String>();

	private String gsid = "3_58a253919e7f199246e43a921eb9b49b825f19a0e265";

	private int page = 10;

	private String diaosi = "nekochinson";

	private String uid = null;

	private String st = null;

	private JButton btn;
	private JTextArea text1, text2, text3, text4;
	private JRadioButton radioButton;
	private JComboBox<?> comboBox;

	public Main() {
		super(new GridBagLayout());
		init();
	}

	public static void main(String[] args) throws Exception {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("自动屎棍机");

		Toolkit kit = frame.getToolkit();
		Dimension winSize = kit.getScreenSize();

		frame.setBounds(winSize.width / 4, winSize.height / 6, winSize.width / 2, winSize.height / 2);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add contents to the window.
		frame.add(new Main());

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	private static final Map<String, String> gsidMap = new HashMap<>();
	static {
		// TODO: put GSID map here: key - username, value - gsid
	}

	@SuppressWarnings("unchecked")
	public void init() {
		this.setSize(800, 800);
		Label label1 = new Label("GSID");
		text1 = new JTextArea(1, 90);
		text1.setText(gsid);
		String[] gsids = { "Netiod", "facingl", "hardaa", "alcanta0" };
		comboBox = new JComboBox(gsids);
		ItemListener lis = new ItemListener() {
			@SuppressWarnings("rawtypes")
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					JComboBox jcb = (JComboBox) e.getSource();
					String selText = gsidMap.get(jcb.getSelectedItem());
					text1.setText(selText);
				} else {

				}
			}
		};
		comboBox.addItemListener(lis);

		Label label2 = new Label("user(如果有u，需要填u，如“u/1911012410”)");
		text2 = new JTextArea(1, 25);
		radioButton = new JRadioButton("UID");
		Label label3 = new Label("页数");
		text3 = new JTextArea(1, 25);
		btn = new JButton("Work");
		text4 = new JTextArea(20, 90);
		text4.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(this.text4);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;

		this.add(label1, c);
		this.add(text1, c);
		this.add(comboBox, c);
		this.add(label2, c);
		this.add(text2, c);
		this.add(radioButton, c);
		this.add(label3, c);
		this.add(text3, c);
		this.add(btn, c);
		this.add(scrollPane, c);
		btn.addActionListener(this);

	}

	public void getStm() {
		String[] men = {
				// TODO: user names
		};
		String[] gsids = {
				// TODO: gsids
		};

		for (int i = 0; i < men.length; i++) {
			try {
				String doc = getDocument("http://weibo.cn/?s2w=login&gsid=" + gsids[i] + "&vt=4", null);
				int stex = doc.indexOf("st=");
				st = doc.substring(stex + 3, stex + 7);
				text4.append(men[i] + ":::::::::::::::::::::::::" + st + "\n");
			} catch (Exception e) {
				text4.append(" failed to get st");
			}
		}

	}

	public void getAtt() {
		String param = "mp=535&page=10";
		String doc = getDocument("http://weibo.cn/1345057570/fans", param);
	}

	public void go() throws Exception {
		// getAtt();
		// if(1==1){
		// return;
		// }
		text4.setText("");
		getStm();
		// if(1==1)return;
		text4.append("\nstart work:" + gsid + " " + diaosi + "\n");
		tags = new HashSet<String>();

		if (gsid == null || diaosi == null) {
			text4.setText("Invalid input: gsid or diaosi is null.");
			return;
		}

		for (int i = 1; i <= page; i++) {
			String urlStr = "http://weibo.cn/" + diaosi + "?page=" + i + "&gsid=" + gsid + "&vt=4&lret=1";
			getTags(urlStr);
			text4.append(tags.size() + " weibos\n");
		}

		// int totalWeibo = 0;

		Set<String> newTags = new HashSet<String>();

		for (String tag : tags) {

			String secureId = getSecureId(tag);
			String comment = commentMap.get(tag);
			if (comment == null) {
				comment = "";
			}
			comment = comment.replace("&nbsp;", "");
			if (secureId != null && !secureId.equals(uid)) {

				newTags.add(tag + "#/#" + secureId + "#/#" + comment);

				// String content = generateCommont();
				// attention(secureId);
				// post(content, tag, secureId);
				// text4.append(content + "===>" + secureId + "#" + tag + "\n");
				// totalWeibo++;
				// // break;
				// Thread.sleep(10000);
			}
		}

		text4.append("tags:\n");
		for (String tag : newTags) {
			text4.append(tag + "#,#");
		}
		text4.append("\n");

		// text4.append("Total commented " + totalWeibo + " weibos\n");
		text4.append("Finished");
	}

	private void attention(String sk) {
		String urlStr = "http://weibo.cn/attention/add?st=" + st + "&uid=" + sk + "&rl=1&vt=4&gsid=" + gsid;
		getDocument(urlStr, null);
	}

	private void post(String content, String tag, String sid) {
		String urlStr = " http://weibo.cn/comments/addcomment?vt=4&gsid=" + gsid;
		String param = "content=" + content + "&id=" + tag + "&rl=1&rt=评论并转发&srcuid=" + sid;
		getDocument(urlStr, param);
	}

	// http://weibo.cn/comment/yetbe7Ysa?rl=0&vt=5&gsid=3_58a253919e7f199246e43a921eb9b49b825f19a0e265&vt=4
	// http://weibo.cn/comment/yeCXtjIAr?rl=0&vt=4&gsid=3_58a253919e7f199246e43a921eb9b49b825f19a0e265&st=06ea

	// private String lastComment = "";

	private String getSecureId(String tag) {
		String urlStr = "http://weibo.cn/comment/" + tag + "?rl=0&vt=4&gsid=" + gsid + "&st=" + st;
		String doc = getDocument(urlStr, null);
		int index = doc.indexOf("srcuid");
		// int index2 = doc.indexOf("转发理由");
		// int index3 = doc.indexOf("<", index2 + 12);
		int indexe = doc.indexOf("alt=\"V\"");
		if (indexe < 0) {
			return null;
		}
		String secureId = null;
		try {
			if (index > 0) {
				secureId = doc.substring(index + 15, index + 25);
			}
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
			secureId = null;
			// lastComment = "";
		}
		return secureId;
	}

	private String getDocument(String urlStr, String param) {
		StringBuffer document = new StringBuffer();
		try {
			URL url = new URL(urlStr);
			// System.out.println(url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; iCafeMedia; QQDownload 677;  Embedded Web Browser from: http://bsalsa.com/; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 1.1.4322)");

			if (param != null) {
				conn.setDoInput(true);
				BufferedOutputStream hurlBufOus = new BufferedOutputStream(conn.getOutputStream());
				hurlBufOus.write(param.getBytes("UTF-8"));
				hurlBufOus.flush();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				document.append(line + " ");
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			text4.append("Exception: " + e);
		}

		String doc = document.toString();

		System.out.println("doc:" + doc);

		if (uid == null) {
			int uidex = doc.indexOf("/operation?");
			if (uidex > 0) {
				uid = doc.substring(uidex - 10, uidex);
			}
			text4.append("get uid:" + uid + "\n");
		}
		if (st == null) {
			// System.out.println(doc);
			int stex = doc.indexOf("st=");
			if (stex > 0) {
				st = doc.substring(stex + 3, stex + 7);
			}
			text4.append("get st:" + st + "\n");
		}

		return doc;
	}

	private void getTags(String urlStr) {
		System.out.println("urlStr:" + urlStr);
		String doc = getDocument(urlStr, null);
		int index = doc.indexOf("comment");

		// int awarex = doc.indexOf("奖");
		while (index >= 0) {
			String tag = doc.substring(index + 8, index + 8 + 9);
			// System.out.println(tag);
			tags.add(tag);
			doc = doc.substring(doc.indexOf(tag) + 9);
			index = doc.indexOf("comment");
			int index2 = doc.indexOf("转发理由");
			int index3 = doc.indexOf("<", index2 + 12);
			if (index2 > 0 && index3 > 0) {
				// String ccc = doc.substring(index2 + 12, index3+1);
				commentMap.put(tag, doc.substring(index2 + 12, index3));
			}

			// if (index2 > 0 && index3 > 0) {
			// // System.out.println("index2:"+index2+" index3:"+index3);
			// lastComment = doc.substring(index2 + 12, index3);
			// // System.out.println(comment);
			// }
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn) {
			Thread t = new Thread() {
				@Override
				public void run() {
					gsid = text1.getText();
					diaosi = text2.getText();
					if (radioButton.isSelected()) {
						uid = diaosi;
					}
					try {
						page = Integer.parseInt(text3.getText());
						go();
					} catch (Exception e1) {
						e1.printStackTrace();
						text4.setText("Exception: " + e1);
					}
				}
			};
			t.start();
		}

	}

}
