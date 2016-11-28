import java.awt.Image;
import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JTextArea;

public class ClientManager extends Thread {
	private JTextArea textArea; // Ŭ���̾�Ʈ �� ���� �޽��� ���
	
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private FileInputStream fis;
	private FileOutputStream fos;
	private ObjectOutputStream oos;
	private Socket user_socket;
	private Vector user_vc;
	
	private String receiveFilePath = "C:/Program Files/chat_server";

	public ClientManager(JTextArea textArea, Socket soc, Vector vc) { // �����ڸ޼ҵ�
		// �Ű������� �Ѿ�� �ڷ� ����
		this.textArea = textArea;
		this.user_socket = soc;
		this.user_vc = vc;
		userNetwork();
	}
	
	public void userNetwork() {
		try {
			is = user_socket.getInputStream();
			dis = new DataInputStream(is);
			os = user_socket.getOutputStream();
			dos = new DataOutputStream(os);
			String id = dis.readUTF(); // ������� �г��� �޴ºκ�
			textArea.append("ID " + id + " ����\n");
			textArea.setCaretPosition(textArea.getText().length());		
			sendString(id + "�� ȯ���մϴ�."); // ����� ����ڿ��� ���������� �˸�
		} catch (Exception e) {
			textArea.append("��Ʈ�� ���� ����\n");
			textArea.setCaretPosition(textArea.getText().length());
		}
	}

	public void InMessage(int iconNum, String id, int level, String msg) {
		textArea.append("[" + id + "] " + msg + "\n");
		textArea.setCaretPosition(textArea.getText().length());
		// ����� �޼��� ó��
		broadCastMsg(iconNum, id, level, msg);
	}
	
	public void receiveImage(int iconNum, String id, int level) {
		try {
			String receiveFileName;
			
			receiveFileName = dis.readUTF();
			System.out.println("���� �̹��� ���� �̸� : " + receiveFileName);

			long receiveFileSize = dis.readLong();
			System.out.println("���� �̹��� ���� ũ�� : " + receiveFileSize);

			int byteSize = 10000;
			byte[] ReceiveByteArrayToFile = new byte[byteSize];
			
			String saveFolder =  receiveFilePath;  //���        
	        File targetDir = new File(saveFolder);  
	        
	        if(!targetDir.exists()) { //���丮 ������ ����.
	         targetDir.mkdirs();
	        }
			
			fos = new FileOutputStream(receiveFilePath + "/" + receiveFileName);

			int n = 0;
			int count = 0;
			while (count < receiveFileSize) { 
				n = dis.read(ReceiveByteArrayToFile);
				fos.write(ReceiveByteArrayToFile, 0, n); 
				count += n;
				System.out.println("�̹��� ���� ������");
			}
			
			System.out.println(receiveFileSize + "bytes ũ���� �̹��� ���� ���� �Ϸ�!");
			
			fos.close();
			
			textArea.append("[" + id + "] " + receiveFileName + "\n");
			textArea.setCaretPosition(textArea.getText().length());
			
			broadCastImage(iconNum, id, level, receiveFileName);
			
		} catch (IOException e) {
			textArea.append("�̹��� ���� ���� ����!\n");
		}
	}
	
	public void receiveFile(int iconNum, String id, int level) {
		try {
			String receiveFileName;
			
			receiveFileName = dis.readUTF();
			System.out.println("���� ���� �̸� : " + receiveFileName);

			long receiveFileSize = dis.readLong();
			System.out.println("���� ���� ũ�� : " + receiveFileSize);

			int byteSize = 10000;
			byte[] ReceiveByteArrayToFile = new byte[byteSize];
			
			String saveFolder =  receiveFilePath;  //���        
	        File targetDir = new File(saveFolder);  
	        
	        if(!targetDir.exists()) { //���丮 ������ ����.
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
			
			textArea.append("[" + id + "] " + receiveFileName + "\n");
			textArea.setCaretPosition(textArea.getText().length());
			
			broadCastFileInfo(iconNum, id, level, receiveFileName);
			
		} catch (IOException e) {
			textArea.append("���� ���� ����!\n");
		}
	}
	
	public void sendFile(String fileName) {
		try {
			dos.writeUTF("/FILESAVE"); // ���� �������� �˸�
			System.out.println("[FILESAVE] send");

			File sendFile = new File(receiveFilePath + "/" + fileName); // ���� ����
			long fileSize = sendFile.length(); // ���� ũ�� �޾ƿ���

			// DataOutputStream�� ���ϸ� ������
			dos.writeUTF(fileName); // Unicode Transformation Formats
			dos.flush();
			System.out.println("���� ���� �̸� : " + fileName);

			// DataOutputStream�� ����ũ�� ������
			dos.writeLong(fileSize);
			dos.flush();
			System.out.println("���� ���� ũ�� : " + fileSize);

			int byteSize = 10000;
			byte[] sendFileTobyteArray = new byte[byteSize]; // ����Ʈ �迭 ����

			fis = new FileInputStream(sendFile); // ���Ͽ��� �о���� ���� ��Ʈ�� ����

			int n = 0;
			int count = 0;
			while (count < fileSize) {
				n = fis.read(sendFileTobyteArray);
				dos.write(sendFileTobyteArray, 0, n);
				count += n;
				System.out.println("���� ������");
			}

			System.out.println(fileSize + "bytes ũ���� ���� ���� �Ϸ�!");

			fis.close();

		} catch (IOException e) {
			
		}
	}

	public void broadCastMsg(int iconNum, String id, int level, String msg) {
		for (int i = 0; i < user_vc.size(); i++) {
			ClientManager imsi = (ClientManager) user_vc.elementAt(i);
			imsi.sendString("/MSG");
			imsi.sendInt(iconNum);
			imsi.sendString(id);
			imsi.sendInt(level);
			imsi.sendString(msg);
		}
	}
	
	public void broadCastFileSize(long fileSize) {
		for (int i = 0; i < user_vc.size(); i++) {
			ClientManager imsi = (ClientManager) user_vc.elementAt(i);
			imsi.sendFileSize(fileSize);
		}
	}
	
	public void broadCastImage(int iconNum, String id, int level, String filename) {
		for (int i = 0; i < user_vc.size(); i++) {
			ClientManager imsi = (ClientManager) user_vc.elementAt(i);
			imsi.sendString("/IMAGE");
			imsi.sendInt(iconNum);
			imsi.sendString(id);
			imsi.sendInt(level);
			imsi.sendString(filename);
			System.out.println("id : " + id);
			imsi.sendImage(filename);
		}
	}
	
	public void broadCastFileInfo(int iconNum, String id, int level, String filename) {
		for (int i = 0; i < user_vc.size(); i++) {
			ClientManager imsi = (ClientManager) user_vc.elementAt(i);
			imsi.sendString("/FILE");
			imsi.sendInt(iconNum);
			imsi.sendString(id);
			imsi.sendInt(level);
			System.out.println("id : " + id);
			imsi.sendString(filename);
		}
	}
	
	public void sendInt(int iconNum) {
		try {
			dos.writeInt(iconNum);
		} 
		catch (IOException e) {
			textArea.append("�޽��� �۽� ���� �߻�\n");	
			textArea.setCaretPosition(textArea.getText().length());
		}
	}

	public void sendString(String str) {
		try {
			dos.writeUTF(str);
		} 
		catch (IOException e) {
			textArea.append("�޽��� �۽� ���� �߻�\n");	
			textArea.setCaretPosition(textArea.getText().length());
		}
	}
	
	public void sendImage(String fineName) {
		Image originalImage = Toolkit.getDefaultToolkit().getImage(receiveFilePath + "/" + fineName); // ImageIcon
		ImageIcon originalImageIcon = new ImageIcon(originalImage);
		Image resizingImage = originalImage.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH); // resize 
		ImageIcon resizingImageIcon = new ImageIcon(resizingImage);
		
		try {
			oos = new ObjectOutputStream(os);
			oos.writeObject(originalImageIcon);
			System.out.println("OriginalImageIcon ����!");
			oos.writeObject(resizingImageIcon);
			System.out.println("ResizingImageIcon ����!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendFileSize(long fileSize) {
		try {
			dos.writeLong(fileSize);
		} 
		catch (IOException e) {
			textArea.append("�޽��� �۽� ���� �߻�\n");	
			textArea.setCaretPosition(textArea.getText().length());
		}
	}

	public void run() { // ������ ����
		while (true) {
			try {
				// ����ڿ��� �޴� �޼���
				String cmd = dis.readUTF();
				if (cmd.equals("/IMAGE")) {
					int iconNum = dis.readInt();
					String id = dis.readUTF();
					int level = dis.readInt();
					receiveImage(iconNum, id, level);
				}
				else if (cmd.equals("/FILESAVE")) {
					String fileName = dis.readUTF();
					sendFile(fileName);
				}
				else if (cmd.equals("/FILE")) {
					int iconNum = dis.readInt();
					String id = dis.readUTF();
					int level = dis.readInt();
					receiveFile(iconNum, id, level);
				}
				else if (cmd.equals("/MSG")) {
					int iconNum = dis.readInt();
					String id = dis.readUTF();
					int level = dis.readInt();
					String msg = dis.readUTF();
					InMessage(iconNum, id, level, msg);
				}
			} 
			catch (IOException e) {
				try {
					dos.close();
					dis.close();
					user_socket.close();
					user_vc.removeElement(this); // �������� ���� ��ü�� ���Ϳ��� �����
					textArea.append(user_vc.size() +" : ���� ���Ϳ� ����� ����� ��\n");
					textArea.append("����� ���� ������ �ڿ� �ݳ�\n");
					textArea.setCaretPosition(textArea.getText().length());
					break;
				} catch (Exception ee) {
				
				}// catch�� ��
			}// �ٱ� catch����
		}
	}// run�޼ҵ� ��
} // ���� userinfoŬ������

