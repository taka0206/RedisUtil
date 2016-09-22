package jp.co.softbank.RedisUtil;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class RedisUtilMain implements ActionListener {

	private JFrame mainFrame;
	private Container contentPane;
	private JPanel topPane;
	private JLabel topLabel;
	private JTextField textField;
	private JPanel westPane;
	private JLabel westLabel;
	private JComboBox<Object> combo;
	
	private JLabel centerLabel;
	private JLabel centerLabel2;
	private JTextField keyField;
	private JTextField fieldKeyField;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JPanel buttonPane;
	private JButton test1Button;
	private JButton test2Button;
	
	public RedisUtilMain() {
		
		mainFrame = new JFrame("Redisユーティリティ");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(640, 400);
		mainFrame.setLocationRelativeTo(null);
		
		contentPane = mainFrame.getContentPane();
		
		// NORTH
		topPane = new JPanel();
		topPane.setLayout(new GridLayout(3, 2));
		topLabel = new JLabel("キーを入力:");
		textField = new JTextField();
		centerLabel = new JLabel("キー");
		keyField = new JTextField();
		centerLabel2 = new JLabel("フィールドキー");
		fieldKeyField = new JTextField();
		
		topPane.add(topLabel);
		topPane.add(textField);
		topPane.add(centerLabel);
		topPane.add(keyField);
		topPane.add(centerLabel2);
		topPane.add(fieldKeyField);
		
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
		scrollPane = new JScrollPane(textArea);
		
		// SOUTH
		test1Button = new JButton("検索");
		test2Button = new JButton("登録");
		test1Button.addActionListener(this);
		test2Button.addActionListener(this);
		
		buttonPane = new JPanel();
		buttonPane.add(test1Button);
		buttonPane.add(test2Button);
		
		contentPane.add(topPane, BorderLayout.NORTH);
		contentPane.add(westPane, BorderLayout.WEST);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.SOUTH);
		
		mainFrame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new RedisUtilMain();
	}
	
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == test1Button) {
			System.out.println("test1Button called");
		}
		if (e.getSource() == test2Button) {
			System.out.println("test2Button called");
		}
		

	}

}
