
// MainView.java : Java Chatting Client �� �ٽɺκ�
// read keyboard --> write to network (Thread �� ó��)
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
	private JTextField textField; // ���� �޼��� ���°�

	private int iconNum;
	private String id;
	private int level;
	private String ip;
	private int port;

	private ImageIcon icon;
	private JLabel iconLabel;
	
	private JButton ImageOpenBtn; // �̹���÷�ι�ư
	private JButton FileOpenBtn; // ����÷�ι�ư
	private JButton sendBtn; // ���۹�ư
	private JTextPane textArea; // �̸�Ƽ���̳� �̹��� �����ֱ� ���ؼ��� JtextPane�� ����Ѵ�.

	private Socket socket; // �������
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private FileInputStream fis;
	private FileOutputStream fos;
	private ObjectInputStream ois;
	
	private String receiveFilePath = "C:/Program Files/chat_client";

	public ChattingFrame(String id, String ip, int port) { // ������
		this.iconNum = (int) (Math.random() * 5 + 1);
		this.id = id;
		this.level = (int) (Math.random() * 30 + 1);
		this.ip = ip;
		this.port = port;
		init();
		start();
		//appendString("�Ű� ������ �Ѿ�� �� : " + id + " " + ip + " " + port + "\n");
		network();
	}

	public void network() { // ������ ����
		try {
			socket = new Socket(ip, port);
			if (socket != null) { // socket�� null���� �ƴҶ� ��! ����Ǿ�����
				Connection(); // ���� �޼ҵ带 ȣ��
			}
		} catch (UnknownHostException e) {

		} catch (IOException e) {
			// textArea.append("���� ���� ����!!\n");
			// appendMessage("���� ���� ����!!\n");
		}
	}

	public void Connection() { // ���� ���� �޼ҵ� ����κ�
		try { // ��Ʈ�� ����
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (IOException e) {
			// textArea.append("��Ʈ�� ���� ����!!\n");
			// appendMessage("��Ʈ�� ���� ����!!\n");
		}
		try {
			dos.writeUTF(id); // ���������� ����Ǹ� ���� id�� ����
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		Thread th = new Thread(new Runnable() { // �����带 ������ �����κ��� �޼����� ����
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
						// appendMessage("�޼��� ���� ����!!\n");
						// ������ ���� ��ſ� ������ ������ ��� ������ �ݴ´�
						try {
							os.close();
							is.close();
							dos.close();
							dis.close();
							socket.close();
							break; // ���� �߻��ϸ� while�� ����
						} catch (IOException e1) {
						}
					}
				} // while�� ��
			}// run�޼ҵ� ��
		});
		th.start();
	}

	// public void sendMessage(String str) { // ������ �޼����� ������ �޼ҵ�
	// try {
	// dos.writeUTF(str);
	// } catch (IOException e) {
	// // textArea.append("�޼��� �۽� ����!!\n");
	// //appendMessage("�޼��� �۽� ����!!\n");
	// }
	// }

	public void sendImage(String filePath) { // ������ ���������� ������ �޼ҵ�
		try {
			dos.writeUTF("/IMAGE"); // �̹��� ���� �������� �˸�
			dos.writeInt(this.iconNum);
			dos.writeUTF(this.id);
			dos.writeInt(this.level);

			File sendFile = new File(filePath); // ���� ����
			String sendFileName = sendFile.getName(); // �̹��� ���� �̸� �޾ƿ���
			long sendFileSize = sendFile.length(); // �̹��� ���� ũ�� �޾ƿ���

			// DataOutputStream�� ���ϸ� ������
			dos.writeUTF(sendFileName); // Unicode Transformation Formats
			dos.flush();
			System.out.println("���� �̹��� ���� �̸� : " + sendFileName);

			// DataOutputStream�� �̹��� ����ũ�� ������
			dos.writeLong(sendFileSize);
			dos.flush();
			System.out.println("���� �̹��� ���� ũ�� : " + sendFileSize);

			int byteSize = 10000;
			byte[] sendFileTobyteArray = new byte[byteSize]; // ����Ʈ �迭 ����

			fis = new FileInputStream(sendFile); // ���Ͽ��� �о���� ���� ��Ʈ�� ����

			int n = 0;
			int count = 0;
			while (count < sendFileSize) {
				n = fis.read(sendFileTobyteArray);
				dos.write(sendFileTobyteArray, 0, n);
				count += n;
				System.out.println("�̹��� ���� ������");
			}

			System.out.println(sendFileSize + "bytes ũ���� �̹��� ���� ���� �Ϸ�!");

			fis.close();

		} catch (IOException e) {
			// appendMessage("�̹��� ���� �۽� ����!!\n");
		}
	}

	public void sendFile(String filePath) { // ������ ������ ������ �޼ҵ�
		try {
			dos.writeUTF("/FILE"); // ���� �������� �˸�
			dos.writeInt(this.iconNum);
			dos.writeUTF(this.id);
			dos.writeInt(this.level);

			File sendFile = new File(filePath); // ���� ����
			String sendFileName = sendFile.getName(); // ���� �̸� �޾ƿ���
			long sendFileSize = sendFile.length(); // ���� ũ�� �޾ƿ���

			// DataOutputStream�� ���ϸ� ������
			dos.writeUTF(sendFileName); // Unicode Transformation Formats
			dos.flush();
			System.out.println("���� ���� �̸� : " + sendFileName);

			// DataOutputStream�� ����ũ�� ������
			dos.writeLong(sendFileSize);
			dos.flush();
			System.out.println("���� ���� ũ�� : " + sendFileSize);

			int byteSize = 10000;
			byte[] sendFileTobyteArray = new byte[byteSize]; // ����Ʈ �迭 ����

			fis = new FileInputStream(sendFile); // ���Ͽ��� �о���� ���� ��Ʈ�� ����

			int n = 0;
			int count = 0;
			while (count < sendFileSize) {
				n = fis.read(sendFileTobyteArray);
				dos.write(sendFileTobyteArray, 0, n);
				count += n;
				System.out.println("���� ������");
			}

			System.out.println(sendFileSize + "bytes ũ���� ���� ���� �Ϸ�!");

			fis.close();

		} catch (IOException e) {
			// appendMessage("���� �۽� ����!!\n");
		}
	}

	public void init() { // ȭ�鱸�� �޼ҵ�
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

		sendBtn = new JButton("����");
		sendBtn.setBounds(210, 312, 60, 42);
		contentPane.add(sendBtn);

		textArea.setEnabled(false); // ����ڰ� �������ϰ� ���´�
		setVisible(true);
	}

	public void start() { // �׼��̺�Ʈ ���� �޼ҵ�
		Myaction action = new Myaction();
		MyImageSendAction imageSendAction = new MyImageSendAction();
		MyFileSendAction fileSendAction = new MyFileSendAction();
		sendBtn.addActionListener(action); // ����Ŭ������ �׼� �����ʸ� ��ӹ��� Ŭ������
		textField.addActionListener(action);
		ImageOpenBtn.addActionListener(imageSendAction);
		FileOpenBtn.addActionListener(fileSendAction);
	}

	class Myaction implements ActionListener { // ����Ŭ������ �׼� �̺�Ʈ ó�� Ŭ����
		@Override
		public void actionPerformed(ActionEvent e) {
			// �׼� �̺�Ʈ�� sendBtn�϶� �Ǵ� textField ���� Enter key ġ��
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
				textField.setText(""); // �޼����� ������ ���� �޼��� ����â�� ����.
				textField.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
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
				JOptionPane.showMessageDialog(null, "������ �������� �ʾҽ��ϴ�", "���", JOptionPane.WARNING_MESSAGE);
				textField.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
				return;
			}

			String filePath = chooser.getSelectedFile().getPath();
			sendImage(filePath);
			textField.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
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
				JOptionPane.showMessageDialog(null, "������ �������� �ʾҽ��ϴ�", "���", JOptionPane.WARNING_MESSAGE);
				textField.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
				return;
			}

			String filePath = chooser.getSelectedFile().getPath();
			sendFile(filePath);
			textField.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
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
			System.out.println("ImageIcon ����!");

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
			System.out.println("FileInfo ����!");

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
			System.out.println("���� ���� �̸� : " + receiveFileName);

			long receiveFileSize = dis.readLong();
			System.out.println("���� ���� ũ�� : " + receiveFileSize);

			int byteSize = 10000;
			byte[] ReceiveByteArrayToFile = new byte[byteSize];

			String saveFolder = receiveFilePath; // ���
			File targetDir = new File(saveFolder);

			if (!targetDir.exists()) { // ���丮 ������ ����.
				targetDir.mkdirs();
			}

			fos = new FileOutputStream(receiveFilePath + "/" + receiveFileName);

			int n = 0;
			int count = 0;
			while (count < receiveFileSize) {
				n = dis.read(ReceiveByteArrayToFile);
				fos.write(ReceiveByteArrayToFile, 0, n);
				count += n;
				System.out.println("���� ������");
			}

			System.out.println(receiveFileSize + "bytes ũ���� ���� ���� �Ϸ�!");

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
