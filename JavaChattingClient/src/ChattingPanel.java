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
		idLabel.setFont(new Font("12�Ե���Ʈ�帲Light", Font.BOLD, 10));
		idLabel.setBounds(0, 0, 20, 20);
		add(idLabel);
		System.out.println("id ����");
		
		msgLabel = new JLabel(msg);
		msgLabel.setFont(new Font("12�Ե���Ʈ�帲Light", Font.PLAIN, 10));
		setMsgLabelWidth(msgLabel);
		msgLabel.setBounds(0, 20, 20, 20);
		add(msgLabel);
		System.out.println("msg����");
	}
	
	public ChattingPanel(String id, ImageIcon img) {

		
	}

	// Text�� ũ�⿡ �°� Label�� ������ ����
	private void setMsgLabelWidth(JLabel label) {
		Font currentFont = label.getFont(); // text�� ���� ��Ʈ ��ü
		FontMetrics fm = label.getFontMetrics(currentFont); // ��Ʈ�� ���� �Ӽ��� ���� ��ü
		this.msgLabelWidth = fm.stringWidth(label.getText()) + 5; // �ؽ�Ʈ�� ��Ʈ�� ����(�ȼ�)
		System.out.println("msgWidth : " + msgLabelWidth);
	}
}