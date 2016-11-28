
// MainView.java : Java Chatting Client 의 핵심부분
// read keyboard --> write to network (Thread 로 처리)
// read network --> write to textArea

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class ChattingFrame extends JFrame {
	private JPanel contentPane;
	private JTextField textField; // 보낼 메세지 쓰는곳

	private int iconNum;
	private String id;
	private int level;
	private String ip;
	private int port;

	private ImageIcon icon;
	private JLabel iconLabel;
	
	private JButton ImageOpenBtn; // 이미지첨부버튼
	private JButton FileOpenBtn; // 파일첨부버튼
	private JButton sendBtn; // 전송버튼
	private JTextPane textArea; // 이모티콘이나 이미지 보여주기 위해서는 JtextPane을 사용한다.

	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private FileInputStream fis;
	private FileOutputStream fos;
	private ObjectInputStream ois;
	
	private String receiveFilePath = "C:/Program Files/chat_client";

	public ChattingFrame(String id, String ip, int port) { // 생성자
		this.iconNum = (int) (Math.random() * 5 + 1);
		this.id = id;
		this.level = (int) (Math.random() * 30 + 1);
		this.ip = ip;
		this.port = port;
		init();
		start();
		//appendString("매개 변수로 넘어온 값 : " + id + " " + ip + " " + port + "\n");
		network();
	}

	public void network() { // 서버에 접속
		try {
			socket = new Socket(ip, port);
			if (socket != null) { // socket이 null값이 아닐때 즉! 연결되었을때
				Connection(); // 연결 메소드를 호출
			}
		} catch (UnknownHostException e) {

		} catch (IOException e) {
			// textArea.append("소켓 접속 에러!!\n");
			// appendMessage("소켓 접속 에러!!\n");
		}
	}

	public void Connection() { // 실직 적인 메소드 연결부분
		try { // 스트림 설정
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (IOException e) {
			// textArea.append("스트림 설정 에러!!\n");
			// appendMessage("스트림 설정 에러!!\n");
		}
		try {
			dos.writeUTF(id); // 정상적으로 연결되면 나의 id를 전송
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		Thread th = new Thread(new Runnable() { // 스레드를 돌려서 서버로부터 메세지를 수신
			@Override
			public void run() {
				while (true) {
					try {
						String cmd = dis.readUTF();
						System.out.println("cmd : " + cmd);
						if (cmd.equals("/IMAGE")) {
							int iconNum = dis.readInt();
							String id = dis.readUTF();
							int level = dis.readInt();
							String fileName = dis.readUTF();
							appendImage(fileName, iconNum, id, level);
						} else if (cmd.equals("/FILESAVE")) {
							saveFile();
						} else if (cmd.equals("/FILE")) {
							int iconNum = dis.readInt();
							String id = dis.readUTF();
							int level = dis.readInt();
							appendFileInfo(iconNum, id, level);
						} else if (cmd.equals("/MSG")) {
							int iconNum = dis.readInt();
							String id = dis.readUTF();
							int level = dis.readInt();
							String msg = dis.readUTF();
							appendMessage(iconNum, id, level, msg);
						} else {
							appendString(cmd);
						}

					} catch (IOException e) {
						// appendMessage("메세지 수신 에러!!\n");
						// 서버와 소켓 통신에 문제가 생겼을 경우 소켓을 닫는다
						try {
							os.close();
							is.close();
							dos.close();
							dis.close();
							socket.close();
							break; // 에러 발생하면 while문 종료
						} catch (IOException e1) {
						}
					}
				} // while문 끝
			}// run메소드 끝
		});
		th.start();
	}

	// public void sendMessage(String str) { // 서버로 메세지를 보내는 메소드
	// try {
	// dos.writeUTF(str);
	// } catch (IOException e) {
	// // textArea.append("메세지 송신 에러!!\n");
	// //appendMessage("메세지 송신 에러!!\n");
	// }
	// }

	public void sendImage(String filePath) { // 서버로 사진파일을 보내는 메소드
		try {
			dos.writeUTF("/IMAGE"); // 이미지 파일 전송임을 알림
			dos.writeInt(this.iconNum);
			dos.writeUTF(this.id);
			dos.writeInt(this.level);

			File sendFile = new File(filePath); // 파일 생성
			String sendFileName = sendFile.getName(); // 이미지 파일 이름 받아오기
			long sendFileSize = sendFile.length(); // 이미지 파일 크기 받아오기

			// DataOutputStream에 파일명 보내기
			dos.writeUTF(sendFileName); // Unicode Transformation Formats
			dos.flush();
			System.out.println("보낸 이미지 파일 이름 : " + sendFileName);

			// DataOutputStream에 이미지 파일크기 보내기
			dos.writeLong(sendFileSize);
			dos.flush();
			System.out.println("보낸 이미지 파일 크기 : " + sendFileSize);

			int byteSize = 10000;
			byte[] sendFileTobyteArray = new byte[byteSize]; // 바이트 배열 생성

			fis = new FileInputStream(sendFile); // 파일에서 읽어오기 위한 스트림 생성

			int n = 0;
			int count = 0;
			while (count < sendFileSize) {
				n = fis.read(sendFileTobyteArray);
				dos.write(sendFileTobyteArray, 0, n);
				count += n;
				System.out.println("이미지 파일 전송중");
			}

			System.out.println(sendFileSize + "bytes 크기의 이미지 파일 전송 완료!");

			fis.close();

		} catch (IOException e) {
			// appendMessage("이미지 파일 송신 에러!!\n");
		}
	}

	public void sendFile(String filePath) { // 서버로 파일을 보내는 메소드
		try {
			dos.writeUTF("/FILE"); // 파일 전송임을 알림
			dos.writeInt(this.iconNum);
			dos.writeUTF(this.id);
			dos.writeInt(this.level);

			File sendFile = new File(filePath); // 파일 생성
			String sendFileName = sendFile.getName(); // 파일 이름 받아오기
			long sendFileSize = sendFile.length(); // 파일 크기 받아오기

			// DataOutputStream에 파일명 보내기
			dos.writeUTF(sendFileName); // Unicode Transformation Formats
			dos.flush();
			System.out.println("보낸 파일 이름 : " + sendFileName);

			// DataOutputStream에 파일크기 보내기
			dos.writeLong(sendFileSize);
			dos.flush();
			System.out.println("보낸 파일 크기 : " + sendFileSize);

			int byteSize = 10000;
			byte[] sendFileTobyteArray = new byte[byteSize]; // 바이트 배열 생성

			fis = new FileInputStream(sendFile); // 파일에서 읽어오기 위한 스트림 생성

			int n = 0;
			int count = 0;
			while (count < sendFileSize) {
				n = fis.read(sendFileTobyteArray);
				dos.write(sendFileTobyteArray, 0, n);
				count += n;
				System.out.println("파일 전송중");
			}

			System.out.println(sendFileSize + "bytes 크기의 파일 전송 완료!");

			fis.close();

		} catch (IOException e) {
			// appendMessage("파일 송신 에러!!\n");
		}
	}

	public void init() { // 화면구성 메소드
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 288, 392);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 272, 302);
		contentPane.add(scrollPane);
		textArea = new JTextPane();
		scrollPane.setViewportView(textArea);
		textArea.setDisabledTextColor(new Color(0, 0, 0));

		textField = new JTextField();
		textField.setBounds(0, 312, 100, 42);
		contentPane.add(textField);
		textField.setColumns(10);

		ImageOpenBtn = new JButton(new ImageIcon("res/ImageOpen.png"));
		ImageOpenBtn.setBorderPainted(false);
		ImageOpenBtn.setContentAreaFilled(false);
		ImageOpenBtn.setFocusPainted(false);
		ImageOpenBtn.setBounds(103, 312, 50, 42);
		contentPane.add(ImageOpenBtn);

		FileOpenBtn = new JButton(new ImageIcon("res/FileOpen.png"));
		FileOpenBtn.setBorderPainted(false);
		FileOpenBtn.setContentAreaFilled(false);
		FileOpenBtn.setFocusPainted(false);
		FileOpenBtn.setBounds(156, 312, 50, 42);
		contentPane.add(FileOpenBtn);

		sendBtn = new JButton("전송");
		sendBtn.setBounds(210, 312, 60, 42);
		contentPane.add(sendBtn);

		textArea.setEnabled(false); // 사용자가 수정못하게 막는다
		setVisible(true);
	}

	public void start() { // 액션이벤트 지정 메소드
		Myaction action = new Myaction();
		MyImageSendAction imageSendAction = new MyImageSendAction();
		MyFileSendAction fileSendAction = new MyFileSendAction();
		sendBtn.addActionListener(action); // 내부클래스로 액션 리스너를 상속받은 클래스로
		textField.addActionListener(action);
		ImageOpenBtn.addActionListener(imageSendAction);
		FileOpenBtn.addActionListener(fileSendAction);
	}

	class Myaction implements ActionListener { // 내부클래스로 액션 이벤트 처리 클래스
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == sendBtn || e.getSource() == textField) {
				if (textField.getText().length() != 0) {
					try {
						dos.writeUTF("/MSG");
						dos.writeInt(iconNum);
						dos.writeUTF(id);
						dos.writeInt(level);
						String msg = textField.getText();
						dos.writeUTF(msg);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				textField.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				textField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
			}
		}
	}

	class MyImageSendAction implements ActionListener {
		JFileChooser chooser;

		MyImageSendAction() {
			chooser = new JFileChooser();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
			chooser.setFileFilter(filter);

			int ret = chooser.showOpenDialog(null);
			if (ret != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다", "경고", JOptionPane.WARNING_MESSAGE);
				textField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				return;
			}

			String filePath = chooser.getSelectedFile().getPath();
			sendImage(filePath);
			textField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
		}
	}

	class MyFileSendAction implements ActionListener {
		JFileChooser chooser;

		MyFileSendAction() {
			chooser = new JFileChooser();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int ret = chooser.showOpenDialog(null);
			if (ret != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다", "경고", JOptionPane.WARNING_MESSAGE);
				textField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				return;
			}

			String filePath = chooser.getSelectedFile().getPath();
			sendFile(filePath);
			textField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
		}
	}
	
	public void appendString(String str) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.replaceSelection(str + "\n");
	}

	public void appendComponent(Component c) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.insertComponent(c);
	}

	public void appendMessage(int iconNum, String id, int level, String msg) {
		icon = new ImageIcon("res/icon" + iconNum + ".png");
		iconLabel = new JLabel(icon);
		appendComponent(iconLabel);
		iconLabel.addMouseListener(new MyProfileClickListener(iconNum, id, level));

		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.replaceSelection("[" + id + "] " + msg + "\n");

		if (id.equals(this.id)) {
			SimpleAttributeSet attribs = new SimpleAttributeSet();
			StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
			textArea.setParagraphAttributes(attribs, true);
		}
	}

	public void appendImage(String fileName, int iconNum, String id, int level) {
		icon = new ImageIcon("res/icon" + iconNum + ".png");
		iconLabel = new JLabel(icon);
		appendComponent(iconLabel);
		iconLabel.addMouseListener(new MyProfileClickListener(iconNum, id, level));

		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.replaceSelection("[" + id + "] ");
		try {
			ois = new ObjectInputStream(is);
			ImageIcon receiveOriginalImage = (ImageIcon) ois.readObject();
			ImageIcon receiveResizingImage = (ImageIcon) ois.readObject();
			JLabel resizingImageLabel = new JLabel(receiveResizingImage);
			resizingImageLabel.addMouseListener(new MyImageClickListener(receiveOriginalImage));
			System.out.println("ImageIcon 수신!");

			appendComponent(resizingImageLabel);
			JButton fileSaveBtn = new JButton(new ImageIcon("res/FileSave.png"));
			fileSaveBtn.addMouseListener(new MyFileSavaBtnClickListener(dos, fileName));
			fileSaveBtn.setBorderPainted(false);
			fileSaveBtn.setFocusPainted(false);
			fileSaveBtn.setContentAreaFilled(false);
			appendComponent(fileSaveBtn);

			len = textArea.getDocument().getLength();
			textArea.setCaretPosition(len);
			textArea.replaceSelection("\n");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (id.equals(this.id)) {
			SimpleAttributeSet attribs = new SimpleAttributeSet();
			StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
			textArea.setParagraphAttributes(attribs, true);
		}
	}

	public void appendFileInfo(int iconNum, String id, int level) {
		icon = new ImageIcon("res/icon" + iconNum + ".png");
		iconLabel = new JLabel(icon);
		appendComponent(iconLabel);
		iconLabel.addMouseListener(new MyProfileClickListener(iconNum, id, level));

		try {
			String fileName = dis.readUTF();
			System.out.println("FileInfo 수신!");

			int len = textArea.getDocument().getLength();
			textArea.setCaretPosition(len);
			textArea.replaceSelection("[" + id + "] " + fileName);
			
			JButton fileSaveBtn = new JButton(new ImageIcon("res/FileSave.png"));
			fileSaveBtn.addMouseListener(new MyFileSavaBtnClickListener(dos, fileName));
			fileSaveBtn.setBorderPainted(false);
			fileSaveBtn.setFocusPainted(false);
			fileSaveBtn.setContentAreaFilled(false);
			appendComponent(fileSaveBtn);

			len = textArea.getDocument().getLength();
			textArea.setCaretPosition(len);
			textArea.replaceSelection("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (id.equals(this.id)) {
			SimpleAttributeSet attribs = new SimpleAttributeSet();
			StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
			textArea.setParagraphAttributes(attribs, true);
		}
	}

	public void saveFile() {
		try {
			String receiveFileName;

			receiveFileName = dis.readUTF();
			System.out.println("받은 파일 이름 : " + receiveFileName);

			long receiveFileSize = dis.readLong();
			System.out.println("받은 파일 크기 : " + receiveFileSize);

			int byteSize = 10000;
			byte[] ReceiveByteArrayToFile = new byte[byteSize];

			String saveFolder = receiveFilePath; // 경로
			File targetDir = new File(saveFolder);

			if (!targetDir.exists()) { // 디렉토리 없으면 생성.
				targetDir.mkdirs();
			}

			fos = new FileOutputStream(receiveFilePath + "/" + receiveFileName);

			int n = 0;
			int count = 0;
			while (count < receiveFileSize) {
				n = dis.read(ReceiveByteArrayToFile);
				fos.write(ReceiveByteArrayToFile, 0, n);
				count += n;
				System.out.println("파일 수신중");
			}

			System.out.println(receiveFileSize + "bytes 크기의 파일 수신 완료!");

			fos.close();

		} catch (IOException e) {

		}
	}
}

class MyImageClickListener extends MouseAdapter {
	private ImageIcon imageIcon;

	public MyImageClickListener(ImageIcon imageIcon) {
		this.imageIcon = imageIcon;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		new OriginalImageDialog(imageIcon);
	}
}

class MyProfileClickListener extends MouseAdapter {
	private ImageIcon profileImg;
	private String id;
	private String charName;
	private int level;

	public MyProfileClickListener(int iconNum, String id, int level) {
		this.profileImg = new ImageIcon("res/char" + iconNum + ".png");

		switch (iconNum) {
		case 1:
			this.charName = "Shrek";
			break;
		case 2:
			this.charName = "Ironman";
			break;
		case 3:
			this.charName = "Captain America";
			break;
		case 4:
			this.charName = "Batman";
			break;
		case 5:
			this.charName = "Spiderman";
			break;
		}
		
		this.id = id;
		this.level = level;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		new ProfileDialog(profileImg, id, charName, level);
	}
}

class MyFileSavaBtnClickListener extends MouseAdapter {
	private DataOutputStream dos;
	private String fileName;

	public MyFileSavaBtnClickListener(DataOutputStream dos, String fileName) {
		this.dos = dos;
		this.fileName = fileName;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			dos.writeUTF("/FILESAVE");
			dos.writeUTF(fileName);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
