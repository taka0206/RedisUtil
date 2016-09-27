package jp.co.softbank.RedisUtil;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

public class RedisUtilMain implements ActionListener {

	private JFrame mainFrame;
	private Container contentPane;
	private JPanel topPane;
	
	private JLabel connectLabel;
	private JTextField connectField;
	private JLabel portLabel;
	private JTextField portField;
	private JLabel authLabel;
	private JTextField authField;
	private JLabel keyLabel;
	private JTextField keyField;
	private JPanel westPane;
	private JLabel westLabel;
	private JComboBox<Object> combo;
	
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JPanel buttonPane;
	private JButton keyAllButton;
	private JButton selectButton;
	private JButton addButton;
	private JButton deleteButton;
	
	public RedisUtilMain() {
		
		mainFrame = new JFrame("Redisユーティリティ");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(640, 400);
		mainFrame.setLocationRelativeTo(null);
		
		contentPane = mainFrame.getContentPane();
		
		// NORTH
		topPane = new JPanel();
		topPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		topPane.setLayout(new GridLayout(4, 4));
		connectLabel = new JLabel("接続:");
		connectField = new JTextField();
		portLabel = new JLabel("ポート:");
		portField = new JTextField();
		authLabel = new JLabel("認証:");
		authField = new JTextField();
		keyLabel = new JLabel("キーを入力:");
		keyField = new JTextField();
		
		topPane.add(connectLabel);
		topPane.add(connectField);
		
		topPane.add(portLabel);
		topPane.add(portField);
		topPane.add(authLabel);
		topPane.add(authField);
		topPane.add(keyLabel);
		topPane.add(keyField);
		
		// WEST
		westPane = new JPanel();
		westPane.setLayout(new GridLayout(10, 1));
		westLabel = new JLabel("データ型選択");
		String[] comboData = {"文字列", "リスト", "集合", "ハッシュ", "ソート済み集合"};
		combo = new JComboBox<Object>(comboData);
		westPane.add(westLabel);
		westPane.add(combo);
		
		// CENTER
		textArea = new JTextArea();
		textArea.setFont(new Font("MS Gothic", Font.PLAIN, 14));
		scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		
		// SOUTH
		keyAllButton = new JButton("キー全検索");
		selectButton = new JButton("検索");
		addButton = new JButton("登録");
		deleteButton = new JButton("削除");
		keyAllButton.addActionListener(this);
		selectButton.addActionListener(this);
		addButton.addActionListener(this);
		deleteButton.addActionListener(this);
		
		buttonPane = new JPanel();
		buttonPane.add(keyAllButton);
		buttonPane.add(selectButton);
		buttonPane.add(addButton);
		buttonPane.add(deleteButton);
		
		contentPane.add(topPane, BorderLayout.NORTH);
		// contentPane.add(westPane, BorderLayout.WEST);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.SOUTH);
		
		// 初期値設定
		Path path = FileSystems.getDefault().getPath(
				System.getProperty("user.home") + 
				File.separator + 
				".redisutil_conf.properties");
		try {
			List<String> fileData = Files.readAllLines(path);
			for (String data : fileData) {
				String[] tmp = data.split(",");
				connectField.setText(tmp[0]);
				portField.setText(tmp[1]);
				if (tmp.length == 3) {
					authField.setText(tmp[2]);
				}
			}	
		} catch (Exception e) {
			textArea.setText("設定ファイルが見つかりません.初期値をロードします");
			// 初期値設定
			connectField.setText("127.0.0.1");
			portField.setText("6379");
		}
		mainFrame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new RedisUtilMain();
	}
	
	
	public void actionPerformed(ActionEvent e) {
		try {
			StringBuilder sb = new StringBuilder();
			Path path = FileSystems.getDefault().getPath(
					System.getProperty("user.home") + 
					File.separator + 
					".redisutil_conf.properties");
			System.out.println(path);
			String address = connectField.getText();
			if (!StringUtils.isBlank(address)) {
				address = address.trim();
				sb.append(address);
			} else {
				textArea.setText(null);
				textArea.append("接続先を設定して下さい");
				return;
			}
			String strPort = portField.getText();
			if (!StringUtils.isBlank(strPort)) {
				strPort = strPort.trim();
				sb.append("," + strPort);
			} else {
				textArea.setText(null);
				textArea.append("ポートを設定して下さい");
			}

			int port = Integer.parseInt(strPort);
			Jedis jedis = new Jedis(address, port, 1000);
			jedis.connect();
			String auth = authField.getText();
			if (StringUtils.isNotBlank(auth)) {
				auth = auth.trim();
				sb.append("," + auth);
				jedis.auth(auth);
			}
			Files.write(path, sb.toString().getBytes("UTF-8"));
			if (e.getSource() == keyAllButton) {
				textArea.setText(null);
				Set<String> keys = jedis.keys("*");
				List<String> sortKeyList = new ArrayList<String>(keys);
				Collections.sort(sortKeyList);
				for (String key : sortKeyList) {
					textArea.append(key);
					textArea.append(System.lineSeparator());
				}
			}
			if (e.getSource() == selectButton) {
				textArea.setText(null);
				String key = keyField.getText();
				if (!StringUtils.isBlank(key)) {
					key = key.trim();
				}
				String keyType = jedis.type(key);
				if ("string".equals(keyType)) {
					String value = jedis.get(key);
					textArea.append(value);
				} else if ("list".equals(keyType)) {
					List<String> valueList = jedis.lrange(key, 0, -1);
					for (String value : valueList) {
						textArea.append(value);
					}
				} else if ("set".equals(keyType)) {
					Set<String> valueSet = jedis.smembers(key);
					for (String value : valueSet) {
						textArea.append(value);
					}
				} else if ("zset".equals(keyType)) {
					Set<String> valueSet = jedis.zrange(key, 0, -1);
					for (String value : valueSet) {
						textArea.append(value);
					}
				} else if ("hash".equals(keyType)) {
					System.out.println("hash");
					textArea.append("+++++　キー有効期限  +++++");
					textArea.append(System.lineSeparator());
					textArea.append(jedis.ttl(key).toString());
					textArea.append(System.lineSeparator());
					textArea.append(System.lineSeparator());
					Map<String, String> valueMap =  jedis.hgetAll(key);
					Stack<String> preKey = new Stack<String>();
					for (Entry<String, String> entry : valueMap.entrySet()) {
						System.out.println(entry);
						if ( preKey.isEmpty() || !preKey.pop().equals(entry.getKey())) {
							textArea.append("+++++ ハッシュフィールド +++++");
							textArea.append(System.lineSeparator());
							textArea.append(entry.getKey());
							textArea.append(System.lineSeparator());
							textArea.append(System.lineSeparator());
						}
						textArea.append("----- ハッシュ値 -----");
						textArea.append(System.lineSeparator());
						String entryValue = entry.getValue();
						entryValue = entryValue.trim();
						if (entryValue.startsWith("[") && entryValue.endsWith("]")) {
							entryValue = "{" + "'" + entry.getKey() + "' : " + entryValue + "}";
						}
						JSONObject jsonObject = JSONObject.fromObject(entryValue);
						textArea.append(jsonObject.toString(4));
						// textArea.append(entry.getValue());
						// textArea.append(System.lineSeparator());
						textArea.append(System.lineSeparator());
						textArea.append(System.lineSeparator());
						preKey.push(entry.getKey());
					}
				} else {
					textArea.setText(null);
					textArea.append("不明な型です");
				}
			}
			if (e.getSource() == addButton) {
				textArea.setText("登録ボタンは利用出来ません");
				return;
//				String key = keyField.getText();
//				if (StringUtils.isNotBlank(key)) {
//					key = key.trim();
//				}
//				String value = textArea.getText();
//				jedis.hset("testHash", "authInfo", "{\"test\":\"test\",\"test2\":\"test2\"}");
//				jedis.hset("testHash", "userInfo", "{\"test3\":\"test3\"}");
//				jedis.hset("testHash", "setterInfo", "{\"test4\":\"test4\",\"test5\":\"test5\",\"test6\":\"test6\"}");
				// jedis.set(key, value);
			}
			if (e.getSource() == deleteButton) {
				String key = keyField.getText();
				if (StringUtils.isNotBlank(key)) {
					key = key.trim();
				}
				jedis.del(key);
			}
			jedis.disconnect();
		} catch (Exception exp) {
			textArea.setText(null);
			textArea.append(exp.getMessage());
		}
	}
}
