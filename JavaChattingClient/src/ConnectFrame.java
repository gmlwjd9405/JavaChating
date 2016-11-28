// Client.Java Java Chatting Client 의 Nicknam, IP, Port 번호 입력하고 접속하는 부분

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ConnectFrame extends JFrame {
	private JPanel contentPane;
	private JTextField tf_ID; // ID를 입력받을곳
	private JPasswordField tf_PW; // PW를 입력받을곳

	public ConnectFrame() { // 생성자
		init();
	}

	public void init() { // 화면 구성
	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 288, 392);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("ID");
		lblNewLabel.setBounds(53, 57, 90, 34);
		contentPane.add(lblNewLabel);

		tf_ID = new JTextField();
		tf_ID.setBounds(92, 64, 150, 21);
		contentPane.add(tf_ID);
		tf_ID.setColumns(10);
		
		JLabel lbPW = new JLabel("비밀번호");
		lbPW.setBounds(12, 111, 90, 34);
		contentPane.add(lbPW);

		tf_PW = new JPasswordField();
		tf_PW.setColumns(10);
		tf_PW.setBounds(92, 118, 150, 21);
		contentPane.add(tf_PW);
		
		JButton btnNewButton = new JButton("접    속");
		btnNewButton.setBounds(36, 266, 206, 52);
		contentPane.add(btnNewButton);
		
		ConnectAction action = new ConnectAction();
		btnNewButton.addActionListener(action);
		tf_PW.addActionListener(action);
		
	}
	
	class ConnectAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {		
			String _id = tf_ID.getText().trim(); // 공백이 있지 모르니 공백 제거 trim() 사용
			String _ip = "127.0.0.1";
			int _port = 30008;
			
			ChattingFrame view = new ChattingFrame(_id,_ip,_port);
			setVisible(false);		
		}
	}
	
}
