package oceanviewresort.ui;

import oceanviewresort.model.Reservation;
import oceanviewresort.observer.NotificationService;
import oceanviewresort.observer.ReservationObserver;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MainMenuFrame extends JFrame implements ReservationObserver {

    private static final Color TEAL_DARK  = new Color(0,  77,  77);
    private static final Color TEAL_MID   = new Color(0, 110, 110);
    private static final Color TEAL_LIGHT = new Color(0, 150, 136);
    private static final Color GOLD       = new Color(212, 175,  55);
    private static final Color GOLD_LIGHT = new Color(255, 223, 100);
    private static final Color CREAM      = new Color(253, 250, 242);
    private static final Color RED_ERR    = new Color(180,  30,  30);

    private final String fullName;
    private final String username;
    private JLabel lblNotification;

    // Menu items: {label, icon-char, bg color}
    private static final Object[][] MENU_ITEMS = {
        {"Add New Reservation",       "＋", new Color(0, 100, 100)},
        {"View Reservation Details",  "🔍", new Color(0,  90,  90)},
        {"Calculate & Print Bill",    "🧾", new Color(27, 94,  32)},
        {"View All Reservations",     "📋", new Color(0,  90,  90)},
        {"Help / User Guide",         "❓", new Color(40, 100, 60)},
        {"Logout",                    "⏻",  new Color(140, 20, 20)},
    };

    public MainMenuFrame(String fullName, String username) {
        this.fullName = fullName;
        this.username = username;
        NotificationService.getInstance().addObserver(this);
        initComponents();
    }

    private void initComponents() {
        setTitle("Ocean View Resort – Main Menu");
        setSize(520, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(245, 248, 248));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // ── HEADER ───────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, TEAL_DARK, getWidth(), 0, TEAL_MID);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // decorative circle
                g2.setColor(new Color(255,255,255,12));
                g2.fillOval(380, -30, 180, 180);
                // gold bottom border
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        JPanel headerLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        headerLeft.setOpaque(false);
        JLabel lblTitle = new JLabel("OCEAN VIEW RESORT");
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 19));
        lblTitle.setForeground(GOLD_LIGHT);
        JLabel lblSub = new JLabel("Room Reservation System");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSub.setForeground(new Color(160, 210, 200));
        headerLeft.add(lblTitle);
        headerLeft.add(lblSub);

        JPanel headerRight = new JPanel(new GridLayout(2, 1, 0, 2));
        headerRight.setOpaque(false);
        JLabel lblStaff = new JLabel("Staff: " + fullName, SwingConstants.RIGHT);
        lblStaff.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblStaff.setForeground(GOLD_LIGHT);
        JLabel lblTime = new JLabel(new java.text.SimpleDateFormat(
            "dd MMM yyyy").format(new java.util.Date()), SwingConstants.RIGHT);
        lblTime.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblTime.setForeground(new Color(160, 210, 200));
        headerRight.add(lblStaff);
        headerRight.add(lblTime);

        header.add(headerLeft,  BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);

        // ── NOTIFICATION BAR ─────────────────────────────────
        lblNotification = new JLabel("  ✦  Ready — Select an option below",
            SwingConstants.LEFT) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(220, 245, 235));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(TEAL_LIGHT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                super.paintComponent(g);
            }
        };
        lblNotification.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblNotification.setForeground(new Color(0, 100, 80));
        lblNotification.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        lblNotification.setOpaque(false);

        // ── MENU BUTTONS ─────────────────────────────────────
        JPanel menuPanel = new JPanel(new GridLayout(6, 1, 0, 8));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        for (int i = 0; i < MENU_ITEMS.length; i++) {
            final int idx = i;
            String label  = (String) MENU_ITEMS[i][0];
            String icon   = (String) MENU_ITEMS[i][1];
            Color  bg     = (Color)  MENU_ITEMS[i][2];

            JButton btn = new JButton() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                    Color base = bg;
                    Color hover = base.brighter();
                    g2.setColor(getModel().isRollover() ? hover : base);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    // left gold accent bar
                    g2.setColor(GOLD);
                    g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
                    // icon
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
                    g2.setColor(GOLD_LIGHT);
                    g2.drawString(icon, 18, getHeight()/2 + 6);
                    // label
                    g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, 48, getHeight()/2 + 5);
                    // arrow
                    g2.setColor(new Color(255,255,255,100));
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    g2.drawString("›", getWidth()-24, getHeight()/2 + 5);
                }
            };
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(0, 54));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> handleMenu(idx));
            menuPanel.add(btn);
        }

        // ── FOOTER ───────────────────────────────────────────
        JLabel footer = new JLabel(
            "Ocean View Resort  ·  Galle, Sri Lanka  ·  API: localhost:8080",
            SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 9));
        footer.setForeground(new Color(130, 160, 155));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 225, 220)),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(lblNotification, BorderLayout.NORTH);
        center.add(menuPanel,       BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void handleMenu(int choice) {
        switch (choice) {
            case 0: new AddReservationFrame(this).setVisible(true); break;
            case 1: new ViewReservationFrame().setVisible(true);    break;
            case 2: new BillFrame().setVisible(true);               break;
            case 3: new AllReservationsFrame().setVisible(true);    break;
            case 4: new HelpFrame().setVisible(true);               break;
            case 5:
                int ok = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Logout", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    NotificationService.getInstance().removeObserver(this);
                    dispose();
                    new LoginFrame().setVisible(true);
                }
                break;
        }
    }

    public void showNotification(String msg) {
        SwingUtilities.invokeLater(() -> {
            lblNotification.setText("  ✦  " + msg);
            Timer t = new Timer(6000, e ->
                lblNotification.setText("  ✦  Ready — Select an option below"));
            t.setRepeats(false); t.start();
        });
    }

    @Override
    public void onReservationAdded(Reservation r) {
        showNotification("NEW BOOKING: " + r.getReservationNumber() +
            " for " + r.getGuestName());
    }

    @Override
    public void onReservationCancelled(String number) {
        showNotification("CANCELLED: Reservation " + number + " removed.");
    }
}
