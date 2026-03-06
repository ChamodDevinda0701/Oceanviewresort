package oceanviewresort.ui;

import oceanviewresort.client.ApiClient;
import oceanviewresort.server.JsonUtil;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;

public class AllReservationsFrame extends JFrame {

    private static final Color TEAL_DARK  = new Color(0,  77,  77);
    private static final Color TEAL_MID   = new Color(0, 110, 110);
    private static final Color TEAL_LIGHT = new Color(0, 150, 136);
    private static final Color GOLD       = new Color(212, 175,  55);
    private static final Color GOLD_LIGHT = new Color(255, 223, 100);
    private static final Color RED_ERR    = new Color(180,  30,  30);
    private static final Color GREEN_DARK = new Color(27,  94,  32);

    private JTable            table;
    private DefaultTableModel tableModel;
    private JButton           btnCancel, btnRefresh, btnClose;
    private JLabel            lblCount;

    public AllReservationsFrame() { initComponents(); loadReservations(); }

    private void initComponents() {
        setTitle("All Reservations");
        setSize(900, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245,248,248));

        // ── Header ───────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                GradientPaint gp=new GradientPaint(0,0,TEAL_DARK,getWidth(),0,TEAL_MID);
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(GOLD); g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(14,22,14,22));
        JLabel lblTitle = new JLabel("📋  All Reservations");
        lblTitle.setFont(new Font("Georgia",Font.BOLD,17));
        lblTitle.setForeground(GOLD_LIGHT);
        lblCount = new JLabel("Total: 0");
        lblCount.setFont(new Font("SansSerif",Font.PLAIN,11));
        lblCount.setForeground(new Color(180,220,210));
        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblCount, BorderLayout.EAST);

        // ── Table ─────────────────────────────────────────────
        String[] cols = {"Res. Number","Guest Name","Contact",
            "Room No.","Room Type","Check-In","Check-Out","Nights","Total (LKR)"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif",Font.PLAIN,12));
        table.setRowHeight(26);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(0,150,136,60));
        table.setSelectionForeground(new Color(0,60,60));

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
                if (!sel) {
                    setBackground(row%2==0 ? Color.WHITE : new Color(237,248,246));
                    setForeground(new Color(20,40,40));
                }
                return this;
            }
        });

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("SansSerif",Font.BOLD,11));
        th.setBackground(TEAL_DARK);
        th.setForeground(GOLD_LIGHT);
        th.setBorder(BorderFactory.createEmptyBorder());
        th.setPreferredSize(new Dimension(0,32));

        int[] widths={110,130,100,70,80,90,90,55,105};
        for (int i=0;i<widths.length;i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200,225,220)));
        scroll.getViewport().setBackground(Color.WHITE);

        // ── Buttons ───────────────────────────────────────────
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.CENTER,12,10));
        btnBar.setBackground(new Color(238,245,245));
        btnBar.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(200,225,220)));
        btnCancel  = btn("🗑  Cancel Selected", RED_ERR, 170);
        btnRefresh = btn("↺  Refresh", new Color(180,120,0), 120);
        btnClose   = btn("✕  Close", new Color(80,100,100), 110);
        btnBar.add(btnCancel); btnBar.add(btnRefresh); btnBar.add(btnClose);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(btnBar, BorderLayout.SOUTH);
        setContentPane(root);

        btnCancel.addActionListener(e -> cancelSelected());
        btnRefresh.addActionListener(e -> loadReservations());
        btnClose.addActionListener(e -> dispose());
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

    private void loadReservations() {
        tableModel.setRowCount(0);
        lblCount.setText("Loading...");
        SwingWorker<String,Void> w=new SwingWorker<String,Void>() {
            @Override protected String doInBackground() { return ApiClient.getAllReservations(); }
            @Override protected void done() {
                try { populateTable(get()); }
                catch(Exception ex) { lblCount.setText("Error loading data"); }
            }
        };
        w.execute();
    }

    private void populateTable(String json) {
        tableModel.setRowCount(0);
        if (json==null||json.equals("[]")) { lblCount.setText("Total: 0 reservations"); return; }
        json=json.trim();
        if (json.startsWith("[")) json=json.substring(1);
        if (json.endsWith("]")) json=json.substring(0,json.length()-1);
        int count=0,depth=0,start=0;
        for (int i=0;i<json.length();i++) {
            char c=json.charAt(i);
            if (c=='{') { if(depth++==0) start=i; }
            else if (c=='}') {
                if (--depth==0) {
                    String obj=json.substring(start,i+1);
                    String resNum=JsonUtil.parseField(obj,"reservationNumber");
                    if (resNum!=null) {
                        tableModel.addRow(new Object[]{
                            resNum,
                            JsonUtil.parseField(obj,"guestName"),
                            JsonUtil.parseField(obj,"contactNumber"),
                            JsonUtil.parseField(obj,"roomNumber"),
                            JsonUtil.parseField(obj,"roomType"),
                            JsonUtil.parseField(obj,"checkInDate"),
                            JsonUtil.parseField(obj,"checkOutDate"),
                            JsonUtil.parseField(obj,"numberOfNights"),
                            String.format("%,.2f",Double.parseDouble(
                                JsonUtil.parseField(obj,"totalAmount")))
                        });
                        count++;
                    }
                }
            }
        }
        lblCount.setText("Total: "+count+" reservation/s");
    }

    private void cancelSelected() {
        int row=table.getSelectedRow();
        if (row<0) {
            JOptionPane.showMessageDialog(this,"Please select a reservation to cancel.",
                "No Selection",JOptionPane.WARNING_MESSAGE); return;
        }
        String resNum=(String)tableModel.getValueAt(row,0);
        String guest=(String)tableModel.getValueAt(row,1);
        int ok=JOptionPane.showConfirmDialog(this,
            "Cancel reservation: "+resNum+"\nGuest: "+guest+"\n\nThis cannot be undone.",
            "Confirm Cancel",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        if (ok==JOptionPane.YES_OPTION) {
            btnCancel.setEnabled(false);
            SwingWorker<String,Void> w=new SwingWorker<String,Void>() {
                @Override protected String doInBackground() { return ApiClient.cancelReservation(resNum); }
                @Override protected void done() {
                    try {
                        String resp=get();
                        if (ApiClient.isSuccess(resp)) {
                            JOptionPane.showMessageDialog(AllReservationsFrame.this,
                                "✅  Reservation "+resNum+" cancelled successfully.",
                                "Cancelled",JOptionPane.INFORMATION_MESSAGE);
                            loadReservations();
                        } else {
                            JOptionPane.showMessageDialog(AllReservationsFrame.this,
                                ApiClient.getError(resp),"Error",JOptionPane.ERROR_MESSAGE);
                        }
                    } catch(Exception ex) {
                        JOptionPane.showMessageDialog(AllReservationsFrame.this,
                            "Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    } finally { btnCancel.setEnabled(true); }
                }
            };
            w.execute();
        }
    }
}
