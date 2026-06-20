import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
// ================= DATABASE IMPORTS =================
// These allow Java to connect to MySQL database
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;

public class JavaPOSMain extends JFrame {
    // ================= DATABASE CONNECTION OBJECT =================
Connection con;
// ================= DATABASE CONNECTION DETAILS =================
// These variables are used to connect Java to MySQL (WAMP)
String url = "jdbc:mysql://localhost:3306/urban_bites";
String user = "root";
String password = "";

    DefaultTableModel model;
    JTable table;

    JTextField txtSubtotal, txtTax, txtTotal, txtCash, txtChange;
    JLabel lblStatus;
    JComboBox<String> paymentMethod;

    Map<String, Integer> stockMap = new HashMap<>();

    double dailySales = 0;
    int receiptNo = 1001;
    
  
    public JavaPOSMain() {

        setTitle("URBAN BITES ZW");
        setSize(1450, 820);
        setLayout(null);
        getContentPane().setBackground(new Color(18,18,18));

        // ================= LEFT PANEL =================
        JPanel leftPanel = new JPanel();
        leftPanel.setBounds(10,10,760,760);
        leftPanel.setBackground(new Color(25,25,25));
        leftPanel.setLayout(new GridLayout(5,3,12,12));

        addProduct(leftPanel,"CheeseCake","CheeseCake.png",6.0,15);
        addProduct(leftPanel,"Chicken","Chicken.png",7.0,20);
        addProduct(leftPanel,"Chocolate Cake","ChocolateCake.png",6.5,15);

        addProduct(leftPanel,"Cocktail","Cocktail.png",4.5,25);
        addProduct(leftPanel,"Coffee","Coffee.png",2.5,30);
        addProduct(leftPanel,"Coke","Coke.png",2.0,40);

        addProduct(leftPanel,"Fresh Chips","FreshChips.png",3.0,30);
        addProduct(leftPanel,"Hotdog","Hotdog.png",4.0,25);
        addProduct(leftPanel,"Noodles","Noodles.png",5.5,20);

        addProduct(leftPanel,"Pizza","Pizza.png",8.5,15);
        addProduct(leftPanel,"Tacos","Tacos.png",4.5,20);
        addProduct(leftPanel,"Ice Cream","Icecream.png",3.0,25);

        addProduct(leftPanel,"Wine","Wine.png",6.0,18);
        addProduct(leftPanel,"Wraps","Wraps.png",5.0,20);
        addProduct(leftPanel,"Burger","Burger.png",5.0,20);

        add(leftPanel);

        // ================= RIGHT PANEL =================
        JPanel rightPanel = new JPanel();
        rightPanel.setBounds(780,10,640,760);
        rightPanel.setBackground(new Color(30,30,30));
        rightPanel.setLayout(null);
        add(rightPanel);

        JLabel title = new JLabel("URBAN BITES ZW",SwingConstants.CENTER);
        title.setBounds(20,10,600,40);
        title.setForeground(new Color(0,255,180));
        title.setFont(new Font("Segoe UI",Font.BOLD,28));
        rightPanel.add(title);

        // ================= TABLE =================
        model = new DefaultTableModel(new Object[]{"ITEM","QTY","PRICE"},0);
        table = new JTable(model);

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI",Font.BOLD,15));
        table.setBackground(new Color(40,40,40));
        table.setForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0,200,150));
        header.setForeground(Color.BLACK);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20,70,600,330);
        rightPanel.add(sp);

        // ================= TOTALS =================
        JPanel totals = new JPanel();
        totals.setBounds(20,420,600,160);
        totals.setBackground(new Color(30,30,30));
        totals.setLayout(null);

        txtSubtotal = createField();
        txtTax = createField();
        txtTotal = createField();
        txtCash = createField();
        txtChange = createField();

        int y = 10;

        totals.add(createLabel("Subtotal")).setBounds(10,y,100,20);
        txtSubtotal.setBounds(10,y+20,150,25);
        totals.add(txtSubtotal);
        y += 40;

        totals.add(createLabel("Tax")).setBounds(10,y,100,20);
        txtTax.setBounds(10,y+20,150,25);
        totals.add(txtTax);
        y += 40;

        totals.add(createLabel("Total")).setBounds(10,y,100,20);
        txtTotal.setBounds(10,y+20,150,25);
        totals.add(txtTotal);
        y += 40;

        totals.add(createLabel("Cash")).setBounds(200,y-120,100,20);
        txtCash.setBounds(200,y-100,150,25);
        totals.add(txtCash);

        totals.add(createLabel("Change")).setBounds(200,y-60,100,20);
        txtChange.setBounds(200,y-40,150,25);
        totals.add(txtChange);

        paymentMethod = new JComboBox<>(new String[]{"Cash","Card","Ecocash"});
        paymentMethod.setBounds(380,40,180,30);
        paymentMethod.setBackground(Color.BLACK);
        paymentMethod.setForeground(Color.WHITE);

        JLabel payLabel = createLabel("Payment Method");
        payLabel.setBounds(380,10,200,20);

        totals.add(payLabel);
        totals.add(paymentMethod);

        rightPanel.add(totals);

        // ================= STATUS =================
        lblStatus = new JLabel("STATUS : WAITING",SwingConstants.CENTER);
        lblStatus.setBounds(20,590,600,30);
        lblStatus.setForeground(Color.YELLOW);
        lblStatus.setFont(new Font("Segoe UI",Font.BOLD,18));
        rightPanel.add(lblStatus);

        // ================= BUTTONS =================
        JButton pay = createButton("PAY",new Color(0,200,83));
        JButton reset = createButton("RESET",new Color(220,53,69));
        JButton receipt = createButton("RECEIPT",new Color(33,150,243));
        JButton orderSummary = createButton("ORDER SUMMARY",new Color(156,39,176));
        JButton exit = createButton("EXIT",new Color(255,111,0));

        pay.setBounds(20,650,100,50);
        reset.setBounds(130,650,100,50);
        receipt.setBounds(240,650,120,50);
        orderSummary.setBounds(370,650,150,50);
        exit.setBounds(530,650,90,50);
        

        rightPanel.add(pay);
        rightPanel.add(reset);
        rightPanel.add(receipt);
        rightPanel.add(orderSummary);
        rightPanel.add(exit);

        pay.addActionListener(e -> processPayment());
        reset.addActionListener(e -> clearSystem());
        receipt.addActionListener(e -> generateReceipt());
        orderSummary.addActionListener(e -> showOrderSummary());
        exit.addActionListener(e -> System.exit(0));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ================= PRODUCT (FIXED IMAGE LOADING) =================
    private void addProduct(JPanel panel, String name, String imageName, double price, int stock) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(45,45,45));

        JLabel imageLabel = new JLabel("",SwingConstants.CENTER);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/image/" + imageName));
            Image img = icon.getImage().getScaledInstance(200,140,Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            imageLabel.setText("NO IMAGE");
            imageLabel.setForeground(Color.WHITE);
        }

        JLabel info = new JLabel(name + " $" + price,SwingConstants.CENTER);
        info.setForeground(Color.WHITE);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                model.addRow(new Object[]{name,1,"$"+price});
                calculateTotals();
            }
        });

        card.add(info,BorderLayout.NORTH);
        card.add(imageLabel,BorderLayout.CENTER);

        panel.add(card);
    }
    // ================= SAVE EACH ITEM SOLD =================
private void saveSaleItemsToDatabase() {

    try {
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/urban_bites",
                "root",
                ""
        );

        String sql = "INSERT INTO sale_items (receipt_no, item_name, qty, price) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(sql);

        for (int i = 0; i < model.getRowCount(); i++) {

            String item = model.getValueAt(i, 0).toString();
            int qty = Integer.parseInt(model.getValueAt(i, 1).toString());
            double price = Double.parseDouble(model.getValueAt(i, 2).toString().replace("$", ""));

            pst.setInt(1, receiptNo);
            pst.setString(2, item);
            pst.setInt(3, qty);
            pst.setDouble(4, price);

            pst.executeUpdate();
        }

        con.close();

        System.out.println("SALE ITEMS SAVED ✔");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "ITEM SAVE ERROR: " + e.getMessage());
    }
}
private void showOrderSummary() {

    try {

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No order found!");
            return;
        }

        StringBuilder summary = new StringBuilder();

        summary.append("===== ORDER SUMMARY =====\n");
        summary.append("Order No: ").append(receiptNo).append("\n\n");

        for (int i = 0; i < model.getRowCount(); i++) {

            String item = model.getValueAt(i, 0).toString();
            String qty = model.getValueAt(i, 1).toString();
            String price = model.getValueAt(i, 2).toString();

            summary.append(item)
                    .append(" x")
                    .append(qty)
                    .append(" - ")
                    .append(price)
                    .append("\n");
        }

        summary.append("\nSubtotal: ").append(txtSubtotal.getText());
        summary.append("\nTax: ").append(txtTax.getText());
        summary.append("\nTotal: ").append(txtTotal.getText());

        summary.append("\n\nSTATUS: PAID / COMPLETED");
        summary.append("\n========================");

        JTextArea area = new JTextArea(summary.toString());
        area.setFont(new Font("Monospaced", Font.BOLD, 14));

        JOptionPane.showMessageDialog(this,
                new JScrollPane(area),
                "ORDER SUMMARY",
                JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage());
    }
}

// ================= UPDATE PRODUCT STOCK =================
private void updateStock() {

    try {
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/urban_bites",
                "root",
                ""
        );

        String sql = "UPDATE products SET stock = stock - ? WHERE name = ?";
        PreparedStatement pst = con.prepareStatement(sql);

        for (int i = 0; i < model.getRowCount(); i++) {

            String item = model.getValueAt(i, 0).toString();
            int qty = Integer.parseInt(model.getValueAt(i, 1).toString());

            pst.setInt(1, qty);
            pst.setString(2, item);

            pst.executeUpdate();
        }

        con.close();

        System.out.println("STOCK UPDATED ✔");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "STOCK ERROR: " + e.getMessage());
    }
}
private void showReport() {

    try {
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/urban_bites",
                "root",
                ""
        );

        String sql = "SELECT COUNT(*), SUM(total_amount) FROM sales";
        PreparedStatement pst = con.prepareStatement(sql);

        java.sql.ResultSet rs = pst.executeQuery();

        int count = 0;
        double total = 0;

        if (rs.next()) {
            count = rs.getInt(1);
            total = rs.getDouble(2);
        }

        JOptionPane.showMessageDialog(this,
                "SALES REPORT\n\n" +
                "Total Transactions: " + count + "\n" +
                "Total Revenue: $" + total
        );

        con.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "REPORT ERROR: " + e.getMessage());
    }
}

    // ================= PAYMENT =================
    private void processPayment() {

        try {
            double total = Double.parseDouble(txtTotal.getText().replace("$",""));
            String method = (String) paymentMethod.getSelectedItem();

            if(method.equals("Cash")) {

                openCashKeypad();

                double cash = Double.parseDouble(txtCash.getText());

                if(cash < total) {
                    lblStatus.setText("STATUS : NOT ENOUGH CASH");
                    lblStatus.setForeground(Color.RED);
                    return;
                }

                txtChange.setText("$" + String.format("%.2f", cash - total));
                JOptionPane.showMessageDialog(this,"PAYMENT SUCCESSFUL (CASH)");
            }

            else if(method.equals("Ecocash")) {

                String phone = JOptionPane.showInputDialog(this,"Enter Ecocash Number:");
                if(phone == null) return;

                JOptionPane.showMessageDialog(this,
                        "Processing payment from " + phone);

                txtCash.setText(txtTotal.getText());
                txtChange.setText("$0.00");

                JOptionPane.showMessageDialog(this,"PAYMENT SUCCESSFUL (ECOCASH)");
            }

            else if(method.equals("Card")) {

                String card = JOptionPane.showInputDialog(this,"Enter Card Number:");
                if(card == null) return;

                JOptionPane.showMessageDialog(this,
                        "Card ending " + card.substring(card.length()-4));

                txtCash.setText(txtTotal.getText());
                txtChange.setText("$0.00");

                JOptionPane.showMessageDialog(this,"PAYMENT SUCCESSFUL (CARD)");
            }

            lblStatus.setText("STATUS : PAID");
            lblStatus.setForeground(Color.GREEN);

            dailySales += total;
            JOptionPane.showMessageDialog(this,
        "🎉 ORDER SUCCESSFUL 🎉\n\n" +
        "Order #: " + receiptNo + "\n" +
        "Status: Being Prepared\n" +
        "Estimated Time: 10–15 minutes\n\n" +
        "Thank you for ordering!");
            // Save sale into database
          saveSaleToDatabase(total, method);
          JOptionPane.showMessageDialog(this,
        "ORDER #" + receiptNo + "\nEstimated time: 10–15 min");
          saveSaleItemsToDatabase();
          updateStock();

receiptNo++;
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,"ENTER VALID DETAILS");
        }
    }

    // ================= CASH KEYPAD =================
    private void openCashKeypad() {

        JDialog dialog = new JDialog(this,"Enter Cash",true);
        dialog.setSize(300,400);
        dialog.setLayout(new BorderLayout());

        JTextField display = new JTextField();
        display.setFont(new Font("Segoe UI",Font.BOLD,20));
        dialog.add(display,BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(4,3,10,10));

        String[] keys = {"7","8","9","4","5","6","1","2","3","0",".","OK"};

        for(String key: keys) {
            JButton btn = new JButton(key);

            btn.addActionListener(e -> {
                if(key.equals("OK")) {
                    txtCash.setText(display.getText());
                    dialog.dispose();
                } else {
                    display.setText(display.getText()+key);
                }
            });

            panel.add(btn);
        }

        dialog.add(panel,BorderLayout.CENTER);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ================= TOTALS =================
    private void calculateTotals() {

        double subtotal = 0;

        for(int i=0;i<model.getRowCount();i++) {
            subtotal += Double.parseDouble(model.getValueAt(i,2).toString().replace("$",""));
        }

        double tax = subtotal * 0.05;
        double total = subtotal + tax;

        txtSubtotal.setText("$"+String.format("%.2f",subtotal));
        txtTax.setText("$"+String.format("%.2f",tax));
        txtTotal.setText("$"+String.format("%.2f",total));
    }

    // ================= RESET =================
    private void clearSystem() {
        model.setRowCount(0);
        txtSubtotal.setText("");
        txtTax.setText("");
        txtTotal.setText("");
        txtCash.setText("");
        txtChange.setText("");
        lblStatus.setText("STATUS : WAITING");
        lblStatus.setForeground(Color.YELLOW);
    }
   // ================= SAVE SALE TO DATABASE =================
private void saveSaleToDatabase(double total, String method) {
   try {
    Class.forName("com.mysql.jdbc.Driver");

   Connection con = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/urban_bites?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
        "root",
        ""
);

    System.out.println("DATABASE CONNECTED SUCCESSFULLY");

} catch (Exception e) {
    JOptionPane.showMessageDialog(this,
            "Database Error: " + e.getMessage());
}
}
   // ================= RECEIPT  =================
private void generateReceipt() {
    try {

        StringBuilder receiptText = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        receiptText.append("====================================\n");
        receiptText.append("          URBAN BITES ZW\n");
        receiptText.append("====================================\n");
        receiptText.append("Receipt No : ").append(receiptNo).append("\n");
        receiptText.append("Date : ").append(sdf.format(new Date())).append("\n");
        receiptText.append("====================================\n\n");

        receiptText.append(String.format("%-15s %-5s %-8s\n",
                "ITEM", "QTY", "PRICE"));

        receiptText.append("------------------------------------\n");

        for (int i = 0; i < model.getRowCount(); i++) {

            String item = model.getValueAt(i, 0).toString();
            String qty = model.getValueAt(i, 1).toString();
            String price = model.getValueAt(i, 2).toString();

            receiptText.append(String.format("%-15s %-5s %-8s\n",
                    item, qty, price));
        }

        receiptText.append("\n====================================\n");

        receiptText.append("Subtotal : ").append(txtSubtotal.getText()).append("\n");
        receiptText.append("Tax (5%) : ").append(txtTax.getText()).append("\n");
        receiptText.append("Total    : ").append(txtTotal.getText()).append("\n");
        receiptText.append("Cash     : $").append(txtCash.getText()).append("\n");
        receiptText.append("Change   : ").append(txtChange.getText()).append("\n");

        receiptText.append("====================================\n");
        receiptText.append("      THANK YOU FOR YOUR ORDER\n");
        receiptText.append("====================================\n");

        JTextArea printArea = new JTextArea(receiptText.toString());
        printArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        int option = JOptionPane.showConfirmDialog(
                this,
                new JScrollPane(printArea),
                "PRINT RECEIPT?",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                printArea.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "No printer found. Showing receipt only.");
            }
        }

        receiptNo++;

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                "ERROR GENERATING RECEIPT");
    }
}

    // ================= HELPERS =================
    private JTextField createField() {
        JTextField t = new JTextField();
        t.setBackground(new Color(18,18,18));
        t.setForeground(Color.WHITE);
        return t;
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        return l;
    }

    private JButton createButton(String text, Color c) {
        JButton b = new JButton(text);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        return b;
    }
    // ================= DATABASE CONNECTION =================
private void connectDatabase() {
    try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    System.out.println("DRIVER LOADED");
} catch (Exception e) {
    System.out.println("DRIVER FAILED: " + e);
}
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JavaPOSMain().setVisible(true));
    }
}