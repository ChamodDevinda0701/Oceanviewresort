package oceanviewresort.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class HelpFrame extends JFrame {

    private static final Color TEAL_DARK  = new Color(0,  77,  77);
    private static final Color TEAL_MID   = new Color(0, 110, 110);
    private static final Color GOLD       = new Color(212, 175,  55);
    private static final Color GOLD_LIGHT = new Color(255, 223, 100);
    private static final Color GREEN_DARK = new Color(27,  94,  32);

    public HelpFrame() { initComponents(); }

    private void initComponents() {
        setTitle("Help – Ocean View Resort");
        setSize(560, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245,248,248));

        // Header
        root.add(buildHeader("❓  Help & User Guide", TEAL_DARK), BorderLayout.NORTH);

        // Help content
        JTextArea txt = new JTextArea();
        txt.setFont(new Font("SansSerif",Font.PLAIN,12));
        txt.setEditable(false);
        txt.setBackground(Color.WHITE);
        txt.setForeground(new Color(20,40,40));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setBorder(BorderFactory.createEmptyBorder(16,22,16,22));
        txt.setText(
"OCEAN VIEW RESORT – ROOM RESERVATION SYSTEM\n" +
"User Guide for Staff Members\n" +
"━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +

"1.  LOGIN\n" +
"    Use your staff username and password to log in.\n" +
"    Default credentials:  admin  /  admin123\n" +
"    Contact IT if your account is locked.\n\n" +

"2.  ADD NEW RESERVATION\n" +
"    ▸ Fill in all guest details completely.\n" +
"    ▸ Select an available room from the dropdown.\n" +
"    ▸ Enter dates in format: yyyy-MM-dd\n" +
"      Example: 2026-03-25\n" +
"    ▸ Contact number must be exactly 10 digits.\n" +
"    ▸ Total is calculated automatically.\n" +
"    ▸ Click 'Save Reservation' to confirm.\n\n" +

"3.  VIEW RESERVATION DETAILS\n" +
"    ▸ Enter the reservation number (e.g. RES-12345).\n" +
"    ▸ Click Search to view full booking details.\n\n" +

"4.  CALCULATE & PRINT BILL\n" +
"    ▸ Enter the reservation number.\n" +
"    ▸ Click 'Generate Bill' to view the invoice.\n" +
"    ▸ Click 'Print Bill' to send to your printer.\n" +
"    ▸ Note: A 10% tax is applied to all bills.\n\n" +

"5.  VIEW ALL RESERVATIONS\n" +
"    ▸ Shows all reservations in a table.\n" +
"    ▸ Select a row and click 'Cancel Selected'\n" +
"      to cancel a booking (room is freed).\n\n" +

"6.  ROOM TYPES & NIGHTLY RATES\n" +
"    ┌─────────────────┬──────────────────┐\n" +
"    │  Standard Room  │  LKR  5,000/night│\n" +
"    │  Deluxe Room    │  LKR  9,500/night│\n" +
"    │  Suite          │  LKR 18,000/night│\n" +
"    └─────────────────┴──────────────────┘\n\n" +

"7.  LIVE NOTIFICATIONS (Observer Pattern)\n" +
"    The main menu bar updates in real-time\n" +
"    whenever a booking is added or cancelled.\n\n" +

"━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
"For technical support:\n" +
"IT Department  |  Ocean View Resort, Galle, Sri Lanka"
        );

        JScrollPane scroll = new JScrollPane(txt);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200,225,220)));

        // Close button
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnBar.setBackground(new Color(238,245,245));
        btnBar.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(200,225,220)));
        JButton btnClose = new JButton("✕  Close");
        btnClose.setBackground(TEAL_DARK);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("SansSerif",Font.BOLD,12));
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setPreferredSize(new Dimension(120,36));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        btnBar.add(btnClose);

        root.add(scroll,  BorderLayout.CENTER);
        root.add(btnBar,  BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildHeader(String title, Color bg) {
        JPanel h = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                GradientPaint gp=new GradientPaint(0,0,bg,getWidth(),0,bg.brighter());
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(GOLD); g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
            }
        };
        h.setOpaque(false);
        h.setBorder(BorderFactory.createEmptyBorder(14,22,14,22));
        JLabel lbl=new JLabel(title);
        lbl.setFont(new Font("Georgia",Font.BOLD,17));
        lbl.setForeground(GOLD_LIGHT);
        h.add(lbl,BorderLayout.WEST);
        return h;
    }
}
