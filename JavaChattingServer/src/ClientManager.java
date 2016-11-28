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
	private JTextArea textArea; // 클라이언트 및 서버 메시지 출력
	
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

	public ClientManager(JTextArea textArea, Socket soc, Vector vc) { // 생성자메소드
		// 매개변수로 넘어온 자료 저장
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
			String id = dis.readUTF(); // 사용자의 닉네임 받는부분
			textArea.append("ID " + id + " 접속\n");
			textArea.setCaretPosition(textArea.getText().length());		
			sendString(id + "님 환영합니다."); // 연결된 사용자에게 정상접속을 알림
		} catch (Exception e) {
			textArea.append("스트림 셋팅 에러\n");
			textArea.setCaretPosition(textArea.getText().length());
		}
	}

	public void InMessage(int iconNum, String id, int level, String msg) {
		textArea.append("[" + id + "] " + msg + "\n");
		textArea.setCaretPosition(textArea.getText().length());
		// 사용자 메세지 처리
		broadCastMsg(iconNum, id, level, msg);
	}
	
	public void receiveImage(int iconNum, String id, int level) {
		try {
			String receiveFileName;
			
			receiveFileName = dis.readUTF();
			System.out.println("받은 이미지 파일 이름 : " + receiveFileName);

			long receiveFileSize = dis.readLong();
			System.out.println("받은 이미지 파일 크기 : " + receiveFileSize);

			int byteSize = 10000;
			byte[] ReceiveByteArrayToFile = new byte[byteSize];
			
			String saveFolder =  receiveFilePath;  //경로        
	        File targetDir = new File(saveFolder);  
	        
	        if(!targetDir.exists()) { //디렉토리 없으면 생성.
	         targetDir.mkdirs();
	        }
			
			fos = new FileOutputStream(receiveFilePath + "/" + receiveFileName);

			int n = 0;
			int count = 0;
			while (count < receiveFileSize) { 
				n = dis.read(ReceiveByteArrayToFile);
				fos.write(ReceiveByteArrayToFile, 0, n); 
				count += n;
				System.out.println("이미지 파일 수신중");
			}
			
			System.out.println(receiveFileSize + "bytes 크기의 이미지 파일 수신 완료!");
			
			fos.close();
			
			textArea.append("[" + id + "] " + receiveFileName + "\n");
			textArea.setCaretPosition(textArea.getText().length());
			
			broadCastImage(iconNum, id, level, receiveFileName);
			
		} catch (IOException e) {
			textArea.append("이미지 파일 수신 에러!\n");
		}
	}
	
	public void receiveFile(int iconNum, String id, int level) {
		try {
			String receiveFileName;
			
			receiveFileName = dis.readUTF();
			System.out.println("받은 파일 이름 : " + receiveFileName);

			long receiveFileSize = dis.readLong();
			System.out.println("받은 파일 크기 : " + receiveFileSize);

			int byteSize = 10000;
			byte[] ReceiveByteArrayToFile = new byte[byteSize];
			
			String saveFolder =  receiveFilePath;  //경로        
	        File targetDir = new File(saveFolder);  
	        
	        if(!targetDir.exists()) { //디렉토리 없으면 생성.
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
			
			textArea.append("[" + id + "] " + receiveFileName + "\n");
			textArea.setCaretPosition(textArea.getText().length());
			
			broadCastFileInfo(iconNum, id, level, receiveFileName);
			
		} catch (IOException e) {
			textArea.append("파일 수신 에러!\n");
		}
	}
	
	public void sendFile(String fileName) {
		try {
			dos.writeUTF("/FILESAVE"); // 파일 전송임을 알림
			System.out.println("[FILESAVE] send");

			File sendFile = new File(receiveFilePath + "/" + fileName); // 파일 생성
			long fileSize = sendFile.length(); // 파일 크기 받아오기

			// DataOutputStream에 파일명 보내기
			dos.writeUTF(fileName); // Unicode Transformation Formats
			dos.flush();
			System.out.println("보낸 파일 이름 : " + fileName);

			// DataOutputStream에 파일크기 보내기
			dos.writeLong(fileSize);
			dos.flush();
			System.out.println("보낸 파일 크기 : " + fileSize);

			int byteSize = 10000;
			byte[] sendFileTobyteArray = new byte[byteSize]; // 바이트 배열 생성

			fis = new FileInputStream(sendFile); // 파일에서 읽어오기 위한 스트림 생성

			int n = 0;
			int count = 0;
			while (count < fileSize) {
				n = fis.read(sendFileTobyteArray);
				dos.write(sendFileTobyteArray, 0, n);
				count += n;
				System.out.println("파일 전송중");
			}

			System.out.println(fileSize + "bytes 크기의 파일 전송 완료!");

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
			textArea.append("메시지 송신 에러 발생\n");	
			textArea.setCaretPosition(textArea.getText().length());
		}
	}

	public void sendString(String str) {
		try {
			dos.writeUTF(str);
		} 
		catch (IOException e) {
			textArea.append("메시지 송신 에러 발생\n");	
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
			System.out.println("OriginalImageIcon 전송!");
			oos.writeObject(resizingImageIcon);
			System.out.println("ResizingImageIcon 전송!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendFileSize(long fileSize) {
		try {
			dos.writeLong(fileSize);
		} 
		catch (IOException e) {
			textArea.append("메시지 송신 에러 발생\n");	
			textArea.setCaretPosition(textArea.getText().length());
		}
	}

	public void run() { // 스레드 정의
		while (true) {
			try {
				// 사용자에게 받는 메세지
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
					user_vc.removeElement(this); // 에러가난 현재 객체를 벡터에서 지운다
					textArea.append(user_vc.size() +" : 현재 벡터에 담겨진 사용자 수\n");
					textArea.append("사용자 접속 끊어짐 자원 반납\n");
					textArea.setCaretPosition(textArea.getText().length());
					break;
				} catch (Exception ee) {
				
				}// catch문 끝
			}// 바깥 catch문끝
		}
	}// run메소드 끝
} // 내부 userinfo클래스끝

