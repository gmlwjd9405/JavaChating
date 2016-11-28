import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChattingPanel extends JPanel {
	private JLabel idLabel;
	private JLabel msgLabel;
	private int msgLabelWidth;
	
	public ChattingPanel(String id, String msg) {
		setSize(40, 40);
		setLayout(null);
		setBackground(Color.black);
		setOpaque(true);
		
		idLabel = new JLabel(id);
		idLabel.setFont(new Font("12롯데마트드림Light", Font.BOLD, 10));
		idLabel.setBounds(0, 0, 20, 20);
		add(idLabel);
		System.out.println("id 붙임");
		
		msgLabel = new JLabel(msg);
		msgLabel.setFont(new Font("12롯데마트드림Light", Font.PLAIN, 10));
		setMsgLabelWidth(msgLabel);
		msgLabel.setBounds(0, 20, 20, 20);
		add(msgLabel);
		System.out.println("msg붙임");
	}
	
	public ChattingPanel(String id, ImageIcon img) {

		
	}

	// Text의 크기에 맞게 Label의 사이즈 변경
	private void setMsgLabelWidth(JLabel label) {
		Font currentFont = label.getFont(); // text의 현재 폰트 객체
		FontMetrics fm = label.getFontMetrics(currentFont); // 폰트의 여러 속성을 가진 객체
		this.msgLabelWidth = fm.stringWidth(label.getText()) + 5; // 텍스트의 스트링 길이(픽셀)
		System.out.println("msgWidth : " + msgLabelWidth);
	}
}