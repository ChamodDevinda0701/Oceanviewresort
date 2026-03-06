package oceanviewresort.ui;

import oceanviewresort.client.ApiClient;
import oceanviewresort.server.JsonUtil;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    // ── Palette ──────────────────────────────────────────────
    private static final Color TEAL_DARK  = new Color(0,  77,  77);
    private static final Color TEAL_MID   = new Color(0, 110, 110);
    private static final Color TEAL_LIGHT = new Color(0, 150, 136);
    private static final Color GOLD       = new Color(212, 175,  55);
    private static final Color GOLD_LIGHT = new Color(255, 223, 100);
    private static final Color CREAM      = new Color(253, 250, 242);
    private static final Color WHITE      = Color.WHITE;
    private static final Color TEXT_DARK  = new Color(20,  40,  40);
    private static final Color TEXT_MID   = new Color(80, 110, 110);
    private static final Color RED_ERR    = new Color(180,  30,  30);

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin, btnExit;
    private JLabel         lblStatus;

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Ocean View Resort");
        setSize(460, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildMainPanel());
    }

    private JPanel buildMainPanel() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Background gradient
                GradientPaint gp = new GradientPaint(
                    0, 0, TEAL_DARK,
                    0, getHeight(), new Color(0, 50, 50));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative wave arc top-right
                g2.setColor(new Color(255,255,255, 15));
                g2.fillOval(260, -80, 280, 280);
                g2.setColor(new Color(255,255,255, 8));
                g2.fillOval(300, -40, 220, 220);
                // Gold accent line bottom
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(40, getHeight()-2, getWidth()-40, getHeight()-2);
            }
        };

        // ── TOP: Brand section ───────────────────────────────
        JPanel brand = new JPanel(new GridLayout(4, 1, 0, 2)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(38, 30, 20, 30));

        // Gold divider top
        JLabel divTop = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
            }
        };
        divTop.setPreferredSize(new Dimension(0, 10));
        divTop.setOpaque(false);

        JLabel lblResort = new JLabel("OCEAN VIEW RESORT", SwingConstants.CENTER);
        lblResort.setFont(loadFont(22f, Font.BOLD));
        lblResort.setForeground(GOLD_LIGHT);

        JLabel lblTagline = new JLabel("Room Reservation System", SwingConstants.CENTER);
        lblTagline.setFont(new Font("Georgia", Font.ITALIC, 13));
        lblTagline.setForeground(new Color(180, 220, 210));

        JLabel lblLocation = new JLabel("✦  Galle, Sri Lanka  ✦", SwingConstants.CENTER);
        lblLocation.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblLocation.setForeground(new Color(130, 180, 170));

        brand.add(divTop);
        brand.add(lblResort);
        brand.add(lblTagline);
        brand.add(lblLocation);

        // ── CENTER: Card panel ───────────────────────────────
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Top gold stripe
                g2.setColor(GOLD);
                g2.fillRoundRect(0, 0, getWidth(), 5, 4, 4);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 25, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 4, 8, 4);
        g.gridwidth = 2;

        // Staff login heading
        JLabel lblLogin = new JLabel("Staff Login", SwingConstants.CENTER);
        lblLogin.setFont(new Font("Georgia", Font.BOLD, 20));
        lblLogin.setForeground(TEAL_DARK);
        g.gridx = 0; g.gridy = 0;
        card.add(lblLogin, g);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 180));
        g.gridy = 1; g.insets = new Insets(0, 0, 12, 0);
        card.add(sep, g);

        g.insets = new Insets(7, 4, 7, 4);
        g.gridwidth = 1;

        // Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblUser.setForeground(TEXT_MID);
        g.gridx = 0; g.gridy = 2; g.weightx = 0.3;
        card.add(lblUser, g);

        txtUsername = styledField(18);
        g.gridx = 1; g.weightx = 0.7;
        card.add(txtUsername, g);

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPass.setForeground(TEXT_MID);
        g.gridx = 0; g.gridy = 3; g.weightx = 0.3;
        card.add(lblPass, g);

        txtPassword = new JPasswordField(18);
        styleComponent(txtPassword);
        g.gridx = 1; g.weightx = 0.7;
        card.add(txtPassword, g);

        // Status label
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStatus.setForeground(RED_ERR);
        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        card.add(lblStatus, g);

        // Buttons row
        JPanel btns = new JPanel(new GridLayout(1, 2, 12, 0));
        btns.setOpaque(false);
        btnLogin = goldButton("Login");
        btnExit  = outlineButton("Exit");
        btns.add(btnLogin);
        btns.add(btnExit);
        g.gridy = 5; g.insets = new Insets(4, 4, 4, 4);
        card.add(btns, g);

        // Hint
        JLabel lblHint = new JLabel("Default: admin  /  admin123", SwingConstants.CENTER);
        lblHint.setFont(new Font("SansSerif", Font.ITALIC, 10));
        lblHint.setForeground(TEXT_MID);
        g.gridy = 6; g.insets = new Insets(6, 4, 0, 4);
        card.add(lblHint, g);

        // ── Wrap card with margins ───────────────────────────
        JPanel cardWrap = new JPanel(new BorderLayout());
        cardWrap.setOpaque(false);
        cardWrap.setBorder(BorderFactory.createEmptyBorder(10, 35, 35, 35));
        cardWrap.add(card, BorderLayout.CENTER);

        // ── Footer ───────────────────────────────────────────
        JLabel footer = new JLabel("Ocean View Resort  ·  REST API  ·  Port 8080",
            SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 9));
        footer.setForeground(new Color(100, 150, 145));
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        root.add(brand,    BorderLayout.NORTH);
        root.add(cardWrap, BorderLayout.CENTER);
        root.add(footer,   BorderLayout.SOUTH);

        // ── Events ───────────────────────────────────────────
        btnLogin.addActionListener(e -> handleLogin());
        btnExit.addActionListener(e -> System.exit(0));
        txtPassword.addActionListener(e -> handleLogin());

        return root;
    }

    // ── Styled components ────────────────────────────────────
    private JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        styleComponent(f);
        return f;
    }

    private void styleComponent(JComponent c) {
        c.setFont(new Font("SansSerif", Font.PLAIN, 13));
        c.setBackground(new Color(240, 248, 248));
        c.setForeground(TEXT_DARK);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 130, 120), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }

    private JButton goldButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, GOLD, 0, getHeight(), new Color(180, 145, 20));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(getModel().isPressed() ?
                    new Color(0,0,0,30) : new Color(255,255,255,20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(TEAL_DARK);
                g2.setFont(getFont());
                g2.drawString(getText(), x, y);
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(120, 40));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton outlineButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ?
                    new Color(180, 30, 30, 50) : new Color(180, 30, 30, 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(RED_ERR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(RED_ERR);
                g2.setFont(getFont());
                g2.drawString(getText(), x, y);
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(120, 40));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private Font loadFont(float size, int style) {
        return new Font("Georgia", style, (int) size);
    }

    // ── Login logic (unchanged) ───────────────────────────────
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setForeground(RED_ERR);
            lblStatus.setText("Please enter username and password.");
            return;
        }

        btnLogin.setEnabled(false);
        lblStatus.setForeground(TEAL_MID);
        lblStatus.setText("Connecting...");

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override protected String doInBackground() {
                return ApiClient.login(username, password);
            }
            @Override protected void done() {
                try {
                    String response = get();
                    if (ApiClient.isSuccess(response)) {
                        String fullName = JsonUtil.parseField(response, "fullName");
                        String uname    = JsonUtil.parseField(response, "username");
                        lblStatus.setForeground(TEAL_LIGHT);
                        lblStatus.setText("Welcome, " + fullName + "!");
                        Timer t = new Timer(700, ev -> {
                            dispose();
                            new MainMenuFrame(fullName, uname).setVisible(true);
                        });
                        t.setRepeats(false);
                        t.start();
                    } else {
                        lblStatus.setForeground(RED_ERR);
                        lblStatus.setText(ApiClient.getError(response));
                        txtPassword.setText("");
                        btnLogin.setEnabled(true);
                    }
                } catch (Exception ex) {
                    lblStatus.setForeground(RED_ERR);
                    lblStatus.setText("Server error: " + ex.getMessage());
                    btnLogin.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
