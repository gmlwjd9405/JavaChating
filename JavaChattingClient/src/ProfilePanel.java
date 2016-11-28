//import java.awt.Color;
//import java.awt.Font;
//import java.awt.Graphics;
//
//import javax.swing.ImageIcon;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//
//public class ProfilePanel extends JPanel {
//	private ImageIcon profileImg;
//	private String charName;
//	
//	private JLabel [] idLabel;
//	private JLabel [] charNameLabel;
//	private JLabel [] levelLabel;
//	
//	public ProfilePanel(String id, int iconNum, int level) {
//		this.profileImg = new ImageIcon("res/char" + iconNum + ".png");
//		
//		switch (iconNum) {
//		case 1:
//			this.charName = "Shrek";
//			break;
//		case 2:
//			this.charName = "Ironman";
//			break;
//		case 3:
//			this.charName = "Captain America";
//			break;
//		case 4:
//			this.charName = "Batman";
//			break;
//		case 5:
//			this.charName = "Spiderman";
//			break;
//		}
//
//		setLayout(null);
//		setBackground(new Color(255, 230, 153));
//		
//		idLabel = new JLabel[2];
//		for(int i=0; i<idLabel.length; i++) {
//			idLabel[i] = new JLabel("Id:");
//		}
//		
//		idLabel[0].setFont(new Font("12롯데마트드림Light", Font.BOLD, 17));
//		idLabel[0].setBounds(160, 10, 50, 20);
//		add(idLabel[0]);
//		
//		idLabel[1].setFont(new Font("12롯데마트드림Light", Font.PLAIN, 15));
//		idLabel[1].setText(id);
//		idLabel[1].setBounds(160, 30, 100, 20);
//		add(idLabel[1]);
//		
//		charNameLabel = new JLabel[2];
//		for(int i=0; i<charNameLabel.length; i++) {
//			charNameLabel[i] = new JLabel("Char:");
//		}
//		
//		charNameLabel[0].setFont(new Font("12롯데마트드림Light", Font.BOLD, 17));
//		charNameLabel[0].setBounds(160, 70, 50, 20);
//		add(charNameLabel[0]);
//		
//		charNameLabel[1].setFont(new Font("12롯데마트드림Light", Font.PLAIN, 15));
//		charNameLabel[1].setText(charName);
//		charNameLabel[1].setBounds(160, 90, 150, 20);
//		add(charNameLabel[1]);
//		
//		levelLabel = new JLabel[2];
//		for(int i=0; i<levelLabel.length; i++) {
//			levelLabel[i] = new JLabel("Level:");
//		}
//		
//		levelLabel[0].setFont(new Font("12롯데마트드림Light", Font.BOLD, 17));
//		levelLabel[0].setBounds(160, 130, 50, 20);
//		add(levelLabel[0]);
//		
//		levelLabel[1].setFont(new Font("12롯데마트드림Light", Font.PLAIN, 15));
//		levelLabel[1].setText(Integer.toString(level));
//		levelLabel[1].setBounds(160, 150, 50, 20);
//		add(levelLabel[1]);			
//	}
//	
//	@Override
//	protected void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		g.drawImage(profileImg.getImage(), 5, 5, this);
//
//	}
//}
