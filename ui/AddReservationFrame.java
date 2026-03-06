package oceanviewresort.ui;

import oceanviewresort.client.ApiClient;
import oceanviewresort.server.JsonUtil;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AddReservationFrame extends JFrame {

    private JTextField txtGuestName, txtAddress, txtContact;
    private JTextField txtCheckIn, txtCheckOut;
    private JComboBox<String> cmbRooms;
    private JLabel lblRoomPrice, lblTotalAmount, lblRoomDesc;
    private JButton btnSave, btnClear, btnClose;

    // Parallel lists to match combobox index
    private final List<String> roomNumbers = new ArrayList<>();
    private final List<String> roomTypes   = new ArrayList<>();
    private final List<Double> roomPrices  = new ArrayList<>();

    public AddReservationFrame(MainMenuFrame parent) {
        initComponents();
        loadRoomsFromApi();
    }

    private void initComponents() {
        setTitle("Add New Reservation");
        setSize(510, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(13, 71, 161));
        headerPanel.setBorder(
            BorderFactory.createEmptyBorder(12, 10, 12, 10));
        JLabel lbl = new JLabel("Add New Reservation");
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        headerPanel.add(lbl);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(
            BorderFactory.createEmptyBorder(15, 25, 10, 25));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        txtGuestName = new JTextField(20);
        txtAddress   = new JTextField(20);
        txtContact   = new JTextField(20);
        cmbRooms     = new JComboBox<>();
        lblRoomPrice = new JLabel("LKR 0.00");
        lblRoomPrice.setFont(new Font("Arial", Font.BOLD, 12));
        lblRoomPrice.setForeground(new Color(13, 71, 161));
        lblRoomDesc  = new JLabel("-");
        lblRoomDesc.setFont(new Font("Arial", Font.ITALIC, 11));
        lblRoomDesc.setForeground(Color.GRAY);
        txtCheckIn   = new JTextField(20);
        txtCheckOut  = new JTextField(20);
        lblTotalAmount = new JLabel("LKR 0.00");
        lblTotalAmount.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalAmount.setForeground(new Color(27, 94, 32));

        String[] labels = {
            "Guest Name:", "Address:", "Contact Number:",
            "Select Room:", "Price / Night:", "Room Info:",
            "Check-In (yyyy-MM-dd):", "Check-Out (yyyy-MM-dd):",
            "Total Amount (LKR):"
        };
        JComponent[] fields = {
            txtGuestName, txtAddress, txtContact,
            cmbRooms, lblRoomPrice, lblRoomDesc,
            txtCheckIn, txtCheckOut, lblTotalAmount
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]);
            l.setFont(new Font("Arial", Font.BOLD, 12));
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.38;
            formPanel.add(l, gbc);
            gbc.gridx = 1; gbc.weightx = 0.62;
            formPanel.add(fields[i], gbc);
        }

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(
            FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);

        btnSave  = makeBtn("Save Reservation",
            new Color(13, 71, 161));
        btnClear = makeBtn("Clear",
            new Color(245, 124, 0));
        btnClose = makeBtn("Close",
            new Color(198, 40, 40));
        btnPanel.add(btnSave);
        btnPanel.add(btnClear);
        btnPanel.add(btnClose);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel,   BorderLayout.CENTER);
        mainPanel.add(btnPanel,    BorderLayout.SOUTH);
        add(mainPanel);

        cmbRooms.addActionListener(e -> updateRoomInfo());
        txtCheckIn.addFocusListener(
            new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent e) {
                    calculateTotal();
                }
            });
        txtCheckOut.addFocusListener(
            new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent e) {
                    calculateTotal();
                }
            });
        btnSave.addActionListener(e -> saveReservation());
        btnClear.addActionListener(e -> clearForm());
        btnClose.addActionListener(e -> dispose());
    }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(155, 36));
        return b;
    }

    // Load available rooms via REST API
    private void loadRoomsFromApi() {
        SwingWorker<String, Void> w = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return ApiClient.getAvailableRooms();
            }
            @Override
            protected void done() {
                try {
                    String json = get();
                    parseAndPopulateRooms(json);
                } catch (Exception ex) {
                    cmbRooms.addItem("Error loading rooms");
                }
            }
        };
        w.execute();
    }

    // Parse JSON array of rooms and fill combobox
    private void parseAndPopulateRooms(String json) {
        roomNumbers.clear();
        roomTypes.clear();
        roomPrices.clear();
        cmbRooms.removeAllItems();

        if (json == null || json.equals("[]")) {
            cmbRooms.addItem("No rooms available");
            return;
        }

        // Split JSON array into objects
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]"))
            json = json.substring(0, json.length() - 1);

        // Each room object is {...}
        int depth = 0, start = 0;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') { if (depth++ == 0) start = i; }
            else if (c == '}') {
                if (--depth == 0) {
                    String obj = json.substring(start, i + 1);
                    String num  = JsonUtil.parseField(
                        obj, "roomNumber");
                    String type = JsonUtil.parseField(
                        obj, "roomType");
                    String price = JsonUtil.parseField(
                        obj, "pricePerNight");
                    if (num != null) {
                        roomNumbers.add(num);
                        roomTypes.add(type);
                        roomPrices.add(Double.parseDouble(price));
                        cmbRooms.addItem("Room " + num +
                            " - " + type +
                            "  [LKR " + price + "]");
                    }
                }
            }
        }

        if (cmbRooms.getItemCount() > 0) {
            cmbRooms.setSelectedIndex(0);
            updateRoomInfo();
        }
    }

    private void updateRoomInfo() {
        int idx = cmbRooms.getSelectedIndex();
        if (idx >= 0 && idx < roomPrices.size()) {
            lblRoomPrice.setText("LKR " +
                String.format("%.2f", roomPrices.get(idx)) +
                " per night");
            lblRoomDesc.setText(
                getDesc(roomTypes.get(idx)));
            calculateTotal();
        }
    }

    private String getDesc(String type) {
        if (type == null) return "-";
        switch (type) {
            case "Standard":
                return "Garden view. WiFi, TV, AC, Hot Water.";
            case "Deluxe":
                return "Sea view balcony. WiFi, Smart TV, Mini Bar.";
            case "Suite":
                return "Ocean panorama. Jacuzzi, Butler, Breakfast.";
            default: return "-";
        }
    }

    private void calculateTotal() {
        try {
            int idx = cmbRooms.getSelectedIndex();
            if (idx < 0 || idx >= roomPrices.size()) return;

            String ci = txtCheckIn.getText().trim();
            String co = txtCheckOut.getText().trim();
            if (ci.isEmpty() || co.isEmpty()) return;

            java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date d1 = sdf.parse(ci);
            java.util.Date d2 = sdf.parse(co);
            long nights =
                (d2.getTime() - d1.getTime()) / (86400000L);

            if (nights <= 0) {
                lblTotalAmount.setText("Invalid date range!");
                lblTotalAmount.setForeground(Color.RED);
                return;
            }

            double total = nights * roomPrices.get(idx);
            lblTotalAmount.setText("LKR " +
                String.format("%.2f", total) +
                "  (" + nights + " night/s)");
            lblTotalAmount.setForeground(new Color(27, 94, 32));
        } catch (Exception ex) {
            // Silent while typing
        }
    }

    private void saveReservation() {
        String name    = txtGuestName.getText().trim();
        String address = txtAddress.getText().trim();
        String contact = txtContact.getText().trim();
        String ci      = txtCheckIn.getText().trim();
        String co      = txtCheckOut.getText().trim();

        if (name.isEmpty() || address.isEmpty() ||
            contact.isEmpty() || ci.isEmpty() || co.isEmpty()) {
            warn("Please fill in all fields.");
            return;
        }
        if (!contact.matches("\\d{10}")) {
            warn("Contact number must be exactly 10 digits.");
            return;
        }

        int idx = cmbRooms.getSelectedIndex();
        if (idx < 0 || idx >= roomNumbers.size()) {
            warn("Please select a valid room.");
            return;
        }

        String rNum  = roomNumbers.get(idx);
        String rType = roomTypes.get(idx);

        btnSave.setEnabled(false);

        SwingWorker<String, Void> w = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                // ← REST API call to backend
                return ApiClient.createReservation(
                    name, address, contact,
                    rNum, rType, ci, co);
            }
            @Override
            protected void done() {
                try {
                    String resp = get();
                    if (ApiClient.isSuccess(resp)) {
                        String resNum = JsonUtil.parseField(
                            resp, "reservationNumber");
                        String total = JsonUtil.parseField(
                            resp, "totalAmount");
                        JOptionPane.showMessageDialog(
                            AddReservationFrame.this,
                            "Reservation saved!\n" +
                            "Number: " + resNum + "\n" +
                            "Total: LKR " + total,
                            "Confirmed",
                            JOptionPane.INFORMATION_MESSAGE);
                        clearForm();
                        loadRoomsFromApi();
                    } else {
                        warn(ApiClient.getError(resp));
                    }
                } catch (Exception ex) {
                    warn("Error: " + ex.getMessage());
                } finally {
                    btnSave.setEnabled(true);
                }
            }
        };
        w.execute();
    }

    private void clearForm() {
        txtGuestName.setText("");
        txtAddress.setText("");
        txtContact.setText("");
        txtCheckIn.setText("");
        txtCheckOut.setText("");
        lblTotalAmount.setText("LKR 0.00");
        lblTotalAmount.setForeground(new Color(27, 94, 32));
        if (cmbRooms.getItemCount() > 0)
            cmbRooms.setSelectedIndex(0);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg,
            "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
}
