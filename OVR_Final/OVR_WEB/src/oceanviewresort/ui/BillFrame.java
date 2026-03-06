package oceanviewresort.ui;

import oceanviewresort.client.ApiClient;
import oceanviewresort.server.JsonUtil;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BillFrame extends JFrame {

    private static final Color TEAL_DARK  = new Color(0,  77,  77);
    private static final Color TEAL_MID   = new Color(0, 110, 110);
    private static final Color GOLD       = new Color(212, 175,  55);
    private static final Color GOLD_LIGHT = new Color(255, 223, 100);
    private static final Color GREEN_DARK = new Color(27,  94,  32);
    private static final Color RED_ERR    = new Color(180,  30,  30);

    private JTextField txtResNumber;
    private JTextArea  txtBill;
    private JButton    btnGenerate, btnPrint, btnClose;

    public BillFrame() { initComponents(); }

    private void initComponents() {
        setTitle("Calculate & Print Bill");
        setSize(500, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245,248,248));

        // Header
        root.add(buildHeader("🧾  Calculate & Print Bill", GREEN_DARK), BorderLayout.NORTH);

        // Search
        JPanel search = new JPanel(new FlowLayout(FlowLayout.CENTER,10,14));
        search.setBackground(new Color(232,245,233));
        search.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(180,220,195)));
        JLabel lbl = new JLabel("Reservation Number:");
        lbl.setFont(new Font("SansSerif",Font.BOLD,12));
        lbl.setForeground(new Color(0,80,60));
        txtResNumber = styledField(18);
        btnGenerate = btn("Generate Bill", GREEN_DARK, 130);
        search.add(lbl); search.add(txtResNumber); search.add(btnGenerate);

        // Bill area
        txtBill = new JTextArea();
        txtBill.setFont(new Font("Courier New",Font.PLAIN,12));
        txtBill.setEditable(false);
        txtBill.setBackground(Color.WHITE);
        txtBill.setForeground(new Color(20,40,30));
        txtBill.setBorder(BorderFactory.createEmptyBorder(14,18,14,18));
        txtBill.setText("\n  Enter a reservation number to generate the bill.");
        JScrollPane scroll = new JScrollPane(txtBill);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(190,225,205)));

        // Buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.CENTER,12,10));
        btnBar.setBackground(new Color(238,245,238));
        btnBar.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(190,225,205)));
        btnPrint = btn("🖨  Print Bill", new Color(0,100,140), 130);
        btnPrint.setEnabled(false);
        btnClose = btn("✕  Close", RED_ERR, 110);
        btnBar.add(btnPrint); btnBar.add(btnClose);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(245,248,248));
        center.add(search, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        center.add(btnBar, BorderLayout.SOUTH);

        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        btnGenerate.addActionListener(e -> generateBill());
        btnPrint.addActionListener(e -> printBill());
        btnClose.addActionListener(e -> dispose());
        txtResNumber.addActionListener(e -> generateBill());
    }

    private JTextField styledField(int cols) {
        JTextField f=new JTextField(cols);
        f.setFont(new Font("SansSerif",Font.PLAIN,12));
        f.setBackground(new Color(240,248,248));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0,130,100),1),
            BorderFactory.createEmptyBorder(5,9,5,9)));
        return f;
    }

    private JButton btn(String t, Color bg, int w) {
        JButton b=new JButton(t);
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
        JLabel lbl=new JLabel(title);
        lbl.setFont(new Font("Georgia",Font.BOLD,17));
        lbl.setForeground(GOLD_LIGHT);
        h.add(lbl,BorderLayout.WEST);
        return h;
    }

    private void generateBill() {
        String num=txtResNumber.getText().trim();
        if (num.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please enter a reservation number.","Required",JOptionPane.WARNING_MESSAGE);
            return;
        }
        btnGenerate.setEnabled(false);
        txtBill.setText("\n  Fetching reservation...");
        SwingWorker<String,Void> w=new SwingWorker<String,Void>() {
            @Override protected String doInBackground() { return ApiClient.getReservation(num); }
            @Override protected void done() {
                try {
                    String resp=get();
                    if ("error".equals(JsonUtil.parseField(resp,"status"))) {
                        txtBill.setText("\n  "+ApiClient.getError(resp));
                        btnPrint.setEnabled(false);
                    } else { renderBill(resp); btnPrint.setEnabled(true); }
                } catch(Exception ex) { txtBill.setText("\n  Error: "+ex.getMessage()); }
                finally { btnGenerate.setEnabled(true); }
            }
        };
        w.execute();
    }

    private void renderBill(String json) {
        String resNum=JsonUtil.parseField(json,"reservationNumber");
        String guest=JsonUtil.parseField(json,"guestName");
        String contact=JsonUtil.parseField(json,"contactNumber");
        String address=JsonUtil.parseField(json,"address");
        String roomNum=JsonUtil.parseField(json,"roomNumber");
        String roomType=JsonUtil.parseField(json,"roomType");
        String checkIn=JsonUtil.parseField(json,"checkInDate");
        String checkOut=JsonUtil.parseField(json,"checkOutDate");
        long nights=Long.parseLong(JsonUtil.parseField(json,"numberOfNights"));
        double total=Double.parseDouble(JsonUtil.parseField(json,"totalAmount"));
        double ppn=total/nights;
        double tax=total*0.10;
        double grand=total+tax;
        String now=new SimpleDateFormat("dd MMM yyyy HH:mm").format(new Date());
        StringBuilder b=new StringBuilder("\n");
        b.append("  ╔══════════════════════════════════════════╗\n");
        b.append("  ║      OCEAN VIEW RESORT – GALLE           ║\n");
        b.append("  ║         Room Reservation Invoice          ║\n");
        b.append("  ╠══════════════════════════════════════════╣\n");
        b.append("  Bill Date      : ").append(now).append("\n");
        b.append("  Reservation No : ").append(resNum).append("\n");
        b.append("  ╠══════════════════════════════════════════╣\n");
        b.append("  GUEST INFORMATION\n");
        b.append("  ──────────────────────────────────────────\n");
        b.append("  Guest Name     : ").append(guest).append("\n");
        b.append("  Contact        : ").append(contact).append("\n");
        b.append("  Address        : ").append(address).append("\n");
        b.append("  ╠══════════════════════════════════════════╣\n");
        b.append("  BOOKING INFORMATION\n");
        b.append("  ──────────────────────────────────────────\n");
        b.append("  Room Number    : ").append(roomNum).append("\n");
        b.append("  Room Type      : ").append(roomType).append("\n");
        b.append("  Check-In       : ").append(checkIn).append("\n");
        b.append("  Check-Out      : ").append(checkOut).append("\n");
        b.append("  Duration       : ").append(nights).append(" night/s\n");
        b.append("  ╠══════════════════════════════════════════╣\n");
        b.append("  PAYMENT DETAILS\n");
        b.append("  ──────────────────────────────────────────\n");
        b.append("  Rate / Night   : LKR ").append(String.format("%,.2f",ppn)).append("\n");
        b.append("  Room Subtotal  : LKR ").append(String.format("%,.2f",total)).append("\n");
        b.append("  Tax (10%)      : LKR ").append(String.format("%,.2f",tax)).append("\n");
        b.append("  ╠══════════════════════════════════════════╣\n");
        b.append("  GRAND TOTAL    : LKR ").append(String.format("%,.2f",grand)).append("\n");
        b.append("  ╚══════════════════════════════════════════╝\n");
        b.append("    Thank you for choosing Ocean View Resort!\n");
        txtBill.setText(b.toString());
    }

    private void printBill() {
        try { txtBill.print(); }
        catch(Exception e) {
            JOptionPane.showMessageDialog(this,"Print failed: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
