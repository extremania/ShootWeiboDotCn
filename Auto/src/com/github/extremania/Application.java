package com.github.extremania;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;

public class Application extends JPanel implements ActionListener {

	private static final long serialVersionUID = -7908488548387382792L;

	private JTextArea ta_log, ta_diaosi, ta_filter;

	private JTextArea ta_time = null;
	private JTextArea ta_follow = null;
	private JButton btn_work = null;
	private JButton btn_follow = null;

	// private Map<String, Integer> diaoSiMap;
	private final Set<String> sikwans = new HashSet<String>();
	private Collection<String> filters;

	private void initDiaoSiMap() {
		File diaoshifile = new File("G:\\pic\\kwan.txt");
		if (!diaoshifile.exists()) {
			diaoshifile = new File("C:\\pic\\kwan.txt");
		}
		List<String> fileLines = Utils.parseFile(diaoshifile);
		for (String fileLine : fileLines) {
			String url = StringUtils.splitByWholeSeparator(fileLine, " ")[0];
			String sikwan = StringUtils.trim(StringUtils.replace(url, "http://weibo.com/", ""));
			sikwans.add(sikwan);
			System.out.println(sikwans);
		}

		// diaoSiMap = new LinkedHashMap<String, Integer>();
		// String text = ta_diaosi.getText();
		// if (StringUtils.isBlank(text)) {
		// ta_log.append("屌丝不能为空" + "\n");
		// return;
		// }
		// String[] parts = StringUtils.split(text, ",");
		// try {
		// for (String part : parts) {
		// String[] ps = StringUtils.split(part, "-");
		// diaoSiMap.put(ps[0], Integer.valueOf(ps[1]));
		// }
		// } catch (Exception e) {
		// ta_log.append("屌丝输入不合法" + "\n");
		// }
	}

	private void initFilters() {
		filters = new ArrayList<String>();
		String text = ta_filter.getText();
		if (StringUtils.isNotBlank(text)) {
			filters = Arrays.asList(text.split(","));
		}
	}

	private void initTime() {
		try {
			time = Integer.valueOf(ta_time.getText());
		} catch (Exception e) {
			ta_log.append("转发时间输入不合法" + "\n");
		}
	}

	private void initFollow() {
		try {
			follow = Integer.valueOf(ta_follow.getText());
		} catch (Exception e) {
			ta_log.append("取消关注输入不合法" + "\n");
		}
	}

	private Integer time = 60;
	private Integer follow = 100;

	@Override
	public void actionPerformed(ActionEvent e) {
		ta_log.setText("================================\n");
		ta_log.append("欢迎使用自动屎棍机\n");
		ta_log.append("================================\n");
		if (e.getSource() == btn_work) {

			initDiaoSiMap();
			initFilters();
			initTime();

			for (int i = 0; i < checkboxList.size(); i++) {
				JCheckBox checkbox = checkboxList.get(i);
				if (checkbox.isSelected()) {
					String user = userList.get(i);
					String password = Utils.USER.get(user);

					Zrufer zrufer = new Zrufer();
					// zrufer.setDiaoSiMap(diaoSiMap);
					List<String> sikwanList = new ArrayList<String>(sikwans);
					Collections.shuffle(sikwanList);
					List<String> subskwans = sikwanList.subList(0, 30);
					zrufer.setSikwans(new ArrayList<String>(subskwans));
					zrufer.setLogger(ta_log);
					zrufer.setUsername(user);
					zrufer.setPassword(password);
					zrufer.setTime(time);
					zrufer.setCheckBox(checkbox);
					zrufer.setAtOther(cb_atOther.isSelected());
					zrufer.setYc(cb_yc.isSelected());
					zrufer.setOnlyYc(cb_oyc.isSelected());
					zrufer.setAttact(cb_att.isSelected());
					zrufer.setFilters(filters);
					zrufer.start();
				}
			}

		} else if (e.getSource() == btn_follow) {

			initFollow();

			for (int i = 0; i < checkboxList.size(); i++) {
				JCheckBox checkbox = checkboxList.get(i);
				if (checkbox.isSelected()) {
					String user = userList.get(i);
					String password = Utils.USER.get(user);

					Delter delter = new Delter();
					delter.setLogger(ta_log);
					delter.setUsername(user);
					delter.setPassword(password);
					delter.setFollow(follow);
					delter.setCheckBox(checkbox);
					delter.start();
				}
			}
		}
	}

	public Application() throws IOException {
		super(new GridBagLayout());
		init();
	}

	private final List<JCheckBox> checkboxList = new ArrayList<JCheckBox>();
	private final List<String> userList = new ArrayList<String>();
	private JCheckBox cb_atOther;
	private JCheckBox cb_yc;
	private JCheckBox cb_oyc;
	private JCheckBox cb_att;

	public void init() throws IOException {

		this.setSize(800, 800);

		Label l_time = new Label("转发时间（秒）");
		ta_time = new JTextArea(1, 2);
		ta_time.setText(time + "");

		Label l_diaosi = new Label("屌丝-转发数（如果有u，需要填u，如“u/1911012410”，用英文逗号隔开。）");
		Label l_diaosi2 = new Label("例如：qinyongdongdong-50,u/1961529147-10");

		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;

		ta_diaosi = new JTextArea(3, 2);
		ta_diaosi.setText("（不用再输入了）");
		ta_diaosi.setEditable(false);
		ta_diaosi.setLineWrap(true);

		Label l_filter = new Label("关键字过滤，用英文逗号隔开");
		ta_filter = new JTextArea(3, 2);
		ta_filter.setLineWrap(true);

		btn_work = new JButton("开始转屎棍");

		ta_log = new JTextArea(20, 75);
		ta_log.setEditable(false);
		ta_log.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(ta_log);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel panel_cb = new JPanel(new GridLayout(Utils.USER.size() / 3, 3));

		int i = 0;
		for (Map.Entry<String, String> entry : Utils.USER.entrySet()) {
			String key = entry.getKey();
			JCheckBox checkbox = new JCheckBox(key + "                           ");
			if (i++ < 7) {
				checkbox.setSelected(true);
			} else {
				checkbox.setSelected(false);
			}
			checkboxList.add(checkbox);
			userList.add(key);
			// this.add(checkbox, c);
			panel_cb.add(checkbox);
		}

		cb_atOther = new JCheckBox("@其他人");
		cb_yc = new JCheckBox("发原创");
		cb_oyc = new JCheckBox("只发原创");
		cb_att = new JCheckBox("攻击");
		cb_yc.setSelected(false);
		cb_att.setSelected(false);

		Label l_delfollow = new Label("删除关注");
		ta_follow = new JTextArea(1, 2);
		ta_follow.setText(follow + "");

		btn_follow = new JButton("开始删除关注");

		this.add(panel_cb, c);
		this.add(l_time, c);
		this.add(ta_time, c);
		this.add(cb_atOther, c);
		this.add(cb_yc, c);
		this.add(cb_oyc, c);
		this.add(cb_att, c);
		this.add(l_diaosi, c);
		this.add(l_diaosi2, c);
		this.add(ta_diaosi, c);
		this.add(l_filter, c);
		this.add(ta_filter, c);
		this.add(btn_work, c);
		this.add(l_delfollow, c);
		this.add(ta_follow, c);
		this.add(btn_follow, c);
		this.add(scrollPane, c);
		btn_work.addActionListener(this);
		btn_follow.addActionListener(this);
	}

	public static void main(String[] args) {
		// System.setProperty("http.proxyHost", "localhost");
		// System.setProperty("http.proxyPort", "8888");
		// System.setProperty("https.proxyHost", "localhost");
		// System.setProperty("https.proxyPort", "8888");

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		// String url = StringUtils.splitByWholeSeparator(
		// "http://weibo.com/beckyzouz 英子 ", " ")[0];
		// System.out.println(StringUtils.replace(url, "http://weibo.com/",
		// ""));
		// String sikwan = StringUtils.trim(StringUtils.stripStart(url,
		// "http://weibo.com/"));
		// System.out.println(sikwan);
	}

	private static void createAndShowGUI() throws IOException {
		// Create and set up the window.
		JFrame frame = new JFrame("自动微博机 - 高级版");

		Toolkit kit = frame.getToolkit();
		Dimension winSize = kit.getScreenSize();

		frame.setBounds(winSize.width / 4, winSize.height / 6, winSize.width / 2 + 200, winSize.height / 2);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		// Add contents to the window.
		frame.add(new Application());

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

}
