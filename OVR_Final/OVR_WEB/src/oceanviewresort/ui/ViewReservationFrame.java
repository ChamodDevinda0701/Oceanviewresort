// ============================================================
//  ViewReservationFrame.java
// ============================================================
package oceanviewresort.ui;

import oceanviewresort.client.ApiClient;
import oceanviewresort.server.JsonUtil;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ViewReservationFrame extends JFrame {

    private static final Color TEAL_DARK  = new Color(0,  77,  77);
    private static final Color TEAL_MID   = new Color(0, 110, 110);
    private static final Color TEAL_LIGHT = new Color(0, 150, 136);
    private static final Color GOLD       = new Color(212, 175,  55);
    private static final Color GOLD_LIGHT = new Color(255, 223, 100);
    private static final Color RED_ERR    = new Color(180,  30,  30);
    private static final Color BG         = new Color(245, 248, 248);

    private JTextField txtResNumber;
    private JTextArea  txtDetails;
    private JButton    btnSearch, btnClose;

    public ViewReservationFrame() { initComponents(); }

    private void initComponents() {
        setTitle("View Reservation Details");
        setSize(500, 510);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // Header
        root.add(buildHeader("🔍  View Reservation Details", TEAL_DARK), BorderLayout.NORTH);

        // Search bar
        JPanel search = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 14));
        search.setBackground(new Color(230, 245, 243));
        search.setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(180,220,215)));
        JLabel lbl = new JLabel("Reservation Number:");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(new Color(0,80,80));
        txtResNumber = styledField(18);
        btnSearch = tealBtn("Search", TEAL_DARK, 100);
        search.add(lbl); search.add(txtResNumber); search.add(btnSearch);

        // Details area
        txtDetails = new JTextArea();
        txtDetails.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtDetails.setEditable(false);
        txtDetails.setBackground(Color.WHITE);
        txtDetails.setForeground(new Color(20,40,40));
        txtDetails.setBorder(BorderFactory.createEmptyBorder(14,18,14,18));
        txtDetails.setText("\n  Enter a Reservation Number and click Search.");
        JScrollPane scroll = new JScrollPane(txtDetails);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200,225,220)));

        // Close
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btns.setBackground(new Color(238,245,245));
        btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(200,225,220)));
        btnClose = tealBtn("✕  Close", RED_ERR, 120);
        btns.add(btnClose);

        JPanel center = new JPanel(new BorderLayout(0,0));
        center.setBackground(BG);
        center.add(search, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        center.add(btns,   BorderLayout.SOUTH);

        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        btnSearch.addActionListener(e -> searchReservation());
        btnClose.addActionListener(e -> dispose());
        txtResNumber.addActionListener(e -> searchReservation());
    }

    private JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setBackground(new Color(240,248,248));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0,130,120),1),
            BorderFactory.createEmptyBorder(5,9,5,9)));
        return f;
    }

    private JButton tealBtn(String text, Color bg, int w) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif",Font.BOLD,12));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(w,36));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
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
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Georgia",Font.BOLD,17));
        lbl.setForeground(GOLD_LIGHT);
        h.add(lbl, BorderLayout.WEST);
        return h;
    }

    private void searchReservation() {
        String num = txtResNumber.getText().trim();
        if (num.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please enter a reservation number.","Required",JOptionPane.WARNING_MESSAGE);
            return;
        }
        btnSearch.setEnabled(false);
        txtDetails.setText("\n  Searching...");
        SwingWorker<String,Void> w = new SwingWorker<String,Void>() {
            @Override protected String doInBackground() { return ApiClient.getReservation(num); }
            @Override protected void done() {
                try {
                    String resp=get();
                    if ("error".equals(JsonUtil.parseField(resp,"status")))
                        txtDetails.setText("\n  " + ApiClient.getError(resp));
                    else displayReservation(resp);
                } catch(Exception ex) { txtDetails.setText("\n  Error: "+ex.getMessage()); }
                finally { btnSearch.setEnabled(true); }
            }
        };
        w.execute();
    }

    private void displayReservation(String json) {
        String resNum=JsonUtil.parseField(json,"reservationNumber");
        String guest=JsonUtil.parseField(json,"guestName");
        String address=JsonUtil.parseField(json,"address");
        String contact=JsonUtil.parseField(json,"contactNumber");
        String roomNum=JsonUtil.parseField(json,"roomNumber");
        String roomType=JsonUtil.parseField(json,"roomType");
        String checkIn=JsonUtil.parseField(json,"checkInDate");
        String checkOut=JsonUtil.parseField(json,"checkOutDate");
        String nights=JsonUtil.parseField(json,"numberOfNights");
        String total=JsonUtil.parseField(json,"totalAmount");
        StringBuilder sb=new StringBuilder("\n");
        sb.append("  ╔══════════════════════════════════════════╗\n");
        sb.append("  ║         RESERVATION DETAILS              ║\n");
        sb.append("  ╠══════════════════════════════════════════╣\n\n");
        sb.append("  Reservation No  : ").append(resNum).append("\n");
        sb.append("  Guest Name      : ").append(guest).append("\n");
        sb.append("  Address         : ").append(address).append("\n");
        sb.append("  Contact Number  : ").append(contact).append("\n\n");
        sb.append("  Room Number     : ").append(roomNum).append("\n");
        sb.append("  Room Type       : ").append(roomType).append("\n");
        sb.append("  Check-In Date   : ").append(checkIn).append("\n");
        sb.append("  Check-Out Date  : ").append(checkOut).append("\n");
        sb.append("  No. of Nights   : ").append(nights).append("\n\n");
        sb.append("  Total Amount    : LKR ").append(
            String.format("%,.2f",Double.parseDouble(total))).append("\n\n");
        sb.append("  ╚══════════════════════════════════════════╝\n");
        txtDetails.setText(sb.toString());
    }
}
