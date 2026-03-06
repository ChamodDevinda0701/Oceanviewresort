package oceanviewresort.ui;

import oceanviewresort.client.ApiClient;
import oceanviewresort.server.JsonUtil;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class AddReservationFrame extends JFrame {

    private static final Color TEAL_DARK  = new Color(0,  77,  77);
    private static final Color TEAL_MID   = new Color(0, 110, 110);
    private static final Color TEAL_LIGHT = new Color(0, 150, 136);
    private static final Color GOLD       = new Color(212, 175,  55);
    private static final Color GOLD_LIGHT = new Color(255, 223, 100);
    private static final Color GREEN_DARK = new Color(27,  94,  32);
    private static final Color RED_ERR    = new Color(180,  30,  30);
    private static final Color BG         = new Color(245, 248, 248);
    private static final Color FIELD_BG   = new Color(240, 248, 248);

    private JTextField  txtGuestName, txtAddress, txtContact;
    private JTextField  txtCheckIn,   txtCheckOut;
    private JComboBox<String> cmbRooms;
    private JLabel      lblRoomPrice, lblTotalAmount, lblRoomDesc;
    private JButton     btnSave, btnClear, btnClose;

    private final List<String> roomNumbers = new ArrayList<>();
    private final List<String> roomTypes   = new ArrayList<>();
    private final List<Double> roomPrices  = new ArrayList<>();

    public AddReservationFrame(MainMenuFrame parent) {
        initComponents();
        loadRoomsFromApi();
    }

    private void initComponents() {
        setTitle("Add New Reservation");
        setSize(530, 610);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // ── HEADER ───────────────────────────────────────────
        root.add(buildHeader("＋  Add New Reservation", TEAL_DARK), BorderLayout.NORTH);

        // ── FORM ─────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, GOLD),
            BorderFactory.createEmptyBorder(18, 28, 12, 28)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        txtGuestName = field();
        txtAddress   = field();
        txtContact   = field();
        cmbRooms     = new JComboBox<>();
        styleCombo(cmbRooms);

        lblRoomPrice = infoLabel("LKR 0.00", TEAL_MID);
        lblRoomDesc  = infoLabel("—", Color.GRAY);
        txtCheckIn   = field();
        txtCheckOut  = field();
        lblTotalAmount = infoLabel("LKR 0.00", GREEN_DARK);
        lblTotalAmount.setFont(new Font("Georgia", Font.BOLD, 15));

        // Section: Guest Info
        addSectionTitle(form, gbc, 0, "GUEST INFORMATION");
        addRow(form, gbc, 1, "Guest Name",          txtGuestName);
        addRow(form, gbc, 2, "Address",             txtAddress);
        addRow(form, gbc, 3, "Contact Number",      txtContact);

        // Section: Room
        addSectionTitle(form, gbc, 4, "ROOM SELECTION");
        addRow(form, gbc, 5, "Available Room",      cmbRooms);
        addRow(form, gbc, 6, "Price / Night",       lblRoomPrice);
        addRow(form, gbc, 7, "Room Description",    lblRoomDesc);

        // Section: Dates
        addSectionTitle(form, gbc, 8, "BOOKING DATES");
        addRow(form, gbc, 9,  "Check-In  (yyyy-MM-dd)",  txtCheckIn);
        addRow(form, gbc, 10, "Check-Out (yyyy-MM-dd)",  txtCheckOut);
        addRow(form, gbc, 11, "Total Amount (LKR)",      lblTotalAmount);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        // ── BUTTONS ───────────────────────────────────────────
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        btnBar.setBackground(new Color(238, 245, 245));
        btnBar.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, new Color(200, 225, 220)));

        btnSave  = tealButton("💾  Save Reservation", TEAL_DARK, 170);
        btnClear = tealButton("↺  Clear", new Color(180, 120, 0), 110);
        btnClose = tealButton("✕  Close", RED_ERR, 110);
        btnBar.add(btnSave);
        btnBar.add(btnClear);
        btnBar.add(btnClose);

        root.add(scroll, BorderLayout.CENTER);
        root.add(btnBar, BorderLayout.SOUTH);
        setContentPane(root);

        // ── Events ───────────────────────────────────────────
        cmbRooms.addActionListener(e -> updateRoomInfo());
        txtCheckIn.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { calculateTotal(); }
        });
        txtCheckOut.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { calculateTotal(); }
        });
        btnSave.addActionListener(e -> saveReservation());
        btnClear.addActionListener(e -> clearForm());
        btnClose.addActionListener(e -> dispose());
    }

    private void addSectionTitle(JPanel p, GridBagConstraints g, int row, String title) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        g.insets = new Insets(14, 4, 4, 4);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(TEAL_LIGHT);
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
            new Color(TEAL_LIGHT.getRed(), TEAL_LIGHT.getGreen(), TEAL_LIGHT.getBlue(), 80)));
        p.add(lbl, g);
        g.gridwidth = 1;
        g.insets = new Insets(6, 4, 6, 4);
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(new Color(80, 110, 110));
        g.gridx = 0; g.gridy = row; g.weightx = 0.36;
        p.add(lbl, g);
        g.gridx = 1; g.weightx = 0.64;
        p.add(field, g);
    }

    private JTextField field() {
        JTextField f = new JTextField(18);
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setBackground(FIELD_BG);
        f.setForeground(new Color(20, 40, 40));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 130, 120), 1),
            BorderFactory.createEmptyBorder(5, 9, 5, 9)));
        return f;
    }

    private void styleCombo(JComboBox<String> c) {
        c.setFont(new Font("SansSerif", Font.PLAIN, 12));
        c.setBackground(FIELD_BG);
        c.setBorder(BorderFactory.createLineBorder(new Color(0, 130, 120), 1));
    }

    private JLabel infoLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(color);
        return l;
    }

    private JButton tealButton(String text, Color bg, int width) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(width, 38));
        b.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return b;
    }

    private JPanel buildHeader(String title, Color bg) {
        JPanel h = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0,0,bg,getWidth(),0,bg.brighter());
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
            }
        };
        h.setOpaque(false);
        h.setBorder(BorderFactory.createEmptyBorder(14,22,14,22));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Georgia", Font.BOLD, 17));
        lbl.setForeground(GOLD_LIGHT);
        h.add(lbl, BorderLayout.WEST);
        return h;
    }

    // ── Logic methods (unchanged from original) ──────────────
    private void loadRoomsFromApi() {
        SwingWorker<String, Void> w = new SwingWorker<String, Void>() {
            @Override protected String doInBackground() { return ApiClient.getAvailableRooms(); }
            @Override protected void done() {
                try { parseAndPopulateRooms(get()); }
                catch (Exception ex) { cmbRooms.addItem("Error loading rooms"); }
            }
        };
        w.execute();
    }

    private void parseAndPopulateRooms(String json) {
        roomNumbers.clear(); roomTypes.clear(); roomPrices.clear();
        cmbRooms.removeAllItems();
        if (json == null || json.equals("[]")) { cmbRooms.addItem("No rooms available"); return; }
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]"))  json = json.substring(0, json.length()-1);
        int depth=0,start=0;
        for (int i=0;i<json.length();i++) {
            char c=json.charAt(i);
            if (c=='{') { if(depth++==0) start=i; }
            else if (c=='}') {
                if (--depth==0) {
                    String obj=json.substring(start,i+1);
                    String num=JsonUtil.parseField(obj,"roomNumber");
                    String type=JsonUtil.parseField(obj,"roomType");
                    String price=JsonUtil.parseField(obj,"pricePerNight");
                    if (num!=null) {
                        roomNumbers.add(num); roomTypes.add(type);
                        roomPrices.add(Double.parseDouble(price));
                        cmbRooms.addItem("Room "+num+" – "+type+" · LKR "+price);
                    }
                }
            }
        }
        if (cmbRooms.getItemCount()>0) { cmbRooms.setSelectedIndex(0); updateRoomInfo(); }
    }

    private void updateRoomInfo() {
        int idx=cmbRooms.getSelectedIndex();
        if (idx>=0&&idx<roomPrices.size()) {
            lblRoomPrice.setText("LKR "+String.format("%.2f",roomPrices.get(idx))+" per night");
            lblRoomDesc.setText(getDesc(roomTypes.get(idx)));
            calculateTotal();
        }
    }

    private String getDesc(String type) {
        if (type==null) return "—";
        switch(type) {
            case "Standard": return "Garden view. WiFi, TV, AC, Hot Water.";
            case "Deluxe":   return "Sea view balcony. WiFi, Smart TV, Mini Bar.";
            case "Suite":    return "Ocean panorama. Jacuzzi, Butler, Breakfast.";
            default: return "—";
        }
    }

    private void calculateTotal() {
        try {
            int idx=cmbRooms.getSelectedIndex();
            if (idx<0||idx>=roomPrices.size()) return;
            String ci=txtCheckIn.getText().trim();
            String co=txtCheckOut.getText().trim();
            if (ci.isEmpty()||co.isEmpty()) return;
            java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date d1=sdf.parse(ci);
            java.util.Date d2=sdf.parse(co);
            long nights=(d2.getTime()-d1.getTime())/86400000L;
            if (nights<=0) {
                lblTotalAmount.setText("Invalid date range!");
                lblTotalAmount.setForeground(RED_ERR);
                return;
            }
            double total=nights*roomPrices.get(idx);
            lblTotalAmount.setText("LKR "+String.format("%.2f",total)+"  ("+nights+" night/s)");
            lblTotalAmount.setForeground(GREEN_DARK);
        } catch(Exception ex) {}
    }

    private void saveReservation() {
        String name=txtGuestName.getText().trim();
        String address=txtAddress.getText().trim();
        String contact=txtContact.getText().trim();
        String ci=txtCheckIn.getText().trim();
        String co=txtCheckOut.getText().trim();
        if (name.isEmpty()||address.isEmpty()||contact.isEmpty()||ci.isEmpty()||co.isEmpty()) {
            warn("Please fill in all fields."); return;
        }
        if (!contact.matches("\\d{10}")) { warn("Contact number must be exactly 10 digits."); return; }
        int idx=cmbRooms.getSelectedIndex();
        if (idx<0||idx>=roomNumbers.size()) { warn("Please select a valid room."); return; }
        String rNum=roomNumbers.get(idx); String rType=roomTypes.get(idx);
        btnSave.setEnabled(false);
        SwingWorker<String,Void> w=new SwingWorker<String,Void>() {
            @Override protected String doInBackground() {
                return ApiClient.createReservation(name,address,contact,rNum,rType,ci,co);
            }
            @Override protected void done() {
                try {
                    String resp=get();
                    if (ApiClient.isSuccess(resp)) {
                        String resNum=JsonUtil.parseField(resp,"reservationNumber");
                        String total=JsonUtil.parseField(resp,"totalAmount");
                        JOptionPane.showMessageDialog(AddReservationFrame.this,
                            "✅  Reservation saved!\nNumber: "+resNum+"\nTotal: LKR "+total,
                            "Confirmed", JOptionPane.INFORMATION_MESSAGE);
                        clearForm(); loadRoomsFromApi();
                    } else { warn(ApiClient.getError(resp)); }
                } catch(Exception ex) { warn("Error: "+ex.getMessage()); }
                finally { btnSave.setEnabled(true); }
            }
        };
        w.execute();
    }

    private void clearForm() {
        txtGuestName.setText(""); txtAddress.setText("");
        txtContact.setText(""); txtCheckIn.setText(""); txtCheckOut.setText("");
        lblTotalAmount.setText("LKR 0.00"); lblTotalAmount.setForeground(GREEN_DARK);
        if (cmbRooms.getItemCount()>0) cmbRooms.setSelectedIndex(0);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
}
