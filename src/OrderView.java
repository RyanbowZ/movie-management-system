import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class OrderView extends JFrame implements ActionListener{

    private JButton btnAdd = new JButton("Add Tickets");
    private JButton btnPay = new JButton("Finish and pay");

    private DefaultTableModel items = new DefaultTableModel(); // store information for the table!

    private JTable tblItems = new JTable(items);
    private JLabel labTotal = new JLabel("Add a ticket to compute price");

    private Order order = null;

    public OrderView() {

        this.setTitle("Order View");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setSize(400, 600);


        items.addColumn("Movie ID");
        items.addColumn("Movie Name");
        items.addColumn("Price");
        items.addColumn("Quantity");
        items.addColumn("Cost");

        JPanel panelOrder = new JPanel();
        panelOrder.setPreferredSize(new Dimension(400, 450));
        panelOrder.setLayout(new BoxLayout(panelOrder, BoxLayout.PAGE_AXIS));
        tblItems.setBounds(0, 0, 400, 350);
        panelOrder.add(tblItems.getTableHeader());
        panelOrder.add(tblItems);
        panelOrder.add(labTotal);
        tblItems.setFillsViewportHeight(true);
        this.getContentPane().add(panelOrder);

        JPanel panelButton = new JPanel();
        panelButton.setPreferredSize(new Dimension(400, 100));
        panelButton.add(btnAdd);
        panelButton.add(btnPay);
        this.getContentPane().add(panelButton);

        this.getBtnAdd().addActionListener(this);
        this.getBtnPay().addActionListener(this);

        order = new Order();

    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnPay() {
        return btnPay;
    }

    public JLabel getLabTotal() {
        return labTotal;
    }

    public void addRow(Object[] row) {
        items.addRow(row);
    }

    public Order getOrder() {
        return order;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.getBtnAdd()) {
            try {
                this.addProduct();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        else
        if (e.getSource() == this.getBtnPay()) {
            try {
                this.makeOrder();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void makeOrder() throws Exception {
        //JOptionPane.showMessageDialog(null, "This function is being implemented!");
        Socket socket = Application.getInstance().getSocket();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        for (OrderLine orderline:order.getLines()) {
            int productID = orderline.getProductID();
            double orderQuantity = orderline.getQuantity();
            out.println("localhost:7777/product/load?productID="+productID);
            String serverAnswer = in.readLine();
            String[] answer = serverAnswer.split("  ");
            Product product = new Product();
            product.setProductID(productID);
            product.setName(answer[0]);
            product.setPrice(Double.parseDouble(answer[1]));
            product.setQuantity(Double.parseDouble(answer[2]));
            product.setProductID(Integer.parseInt(answer[3]));
            product.setCategoryID(Integer.parseInt(answer[4]));
            if(product.getQuantity() - orderQuantity<0){//examine whether the number of products is enough
                JOptionPane.showMessageDialog(null, "The available seats are not enough!");
                return;
            }
            double currentQuantity = product.getQuantity()-orderQuantity;
            product.setQuantity(currentQuantity);
            out.println("localhost:7777/product/update?productID="+productID+"&productName="+product.getName()+"&productPrice="+product.getPrice()
                    +"&productQuantity="+product.getQuantity()+"&categoryID="+product.getCategoryID());
            serverAnswer = in.readLine();

//          Product product = Application.getInstance().getDataAdapter().loadProduct(orderline.getProductID());
//            Application.getInstance().getDataAdapter().saveProduct(product); // and save this product back
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        int oid=(int) (new Date().getTime()/1000);
        System.out.println("oid:"+oid);
        order.setOrderID(oid);
        order.setBuyerID(Application.getInstance().getCurrentUser().getUserID());
        order.setDate(dtf.format(now));
        out.println("localhost:7777/order/save?orderID="+order.getOrderID()+"&date="+order.getDate()+"&totalCost="+order.getTotalCost()
                +"&buyerID="+order.getBuyerID());
//        out.println("SaveOrder  "+order.getOrderID()+"  "+order.getDate()+"  "+
//                order.getTotalCost()+"  "+order.getBuyerID());
        String serverAnswer = in.readLine();
        String[] answer = serverAnswer.split("  ");
        System.out.println(answer[0]);

//        Application.getInstance().getDataAdapter().saveOrder(order);
        Application.getInstance().getPaymentView().setVisible(true);
        //JOptionPane.showMessageDialog(null, "You have successfully submitted the order!");
    }

    private void addProduct() throws Exception {
        // Check whether the id for product is an integer.
        int id;
        try {
            id = Integer.parseInt(JOptionPane.showInputDialog("Enter movieID: "));
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "This movieID is not valid!");
            return;
        }
//        Product product = Application.getInstance().getDataAdapter().loadProduct(id);
        Socket socket = Application.getInstance().getSocket();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Add Product. ID = " + id);
        out.println("localhost:7777/product/load?productID="+id);
        String serverAnswer = in.readLine();
        Product product = null;
        if(serverAnswer.equals("Invalid Product")) {
            JOptionPane.showMessageDialog(null, "This Product does not exist!");
        } else {
            System.out.println("Product Detail: " + id+ " "+ serverAnswer);
            String[] answer = serverAnswer.split("  ");
            product = new Product();
            product.setProductID(id);
            product.setName(answer[0]);
            product.setPrice(Double.parseDouble(answer[1]));
            product.setQuantity(Double.parseDouble(answer[2]));
        }
        if (product == null) {
            JOptionPane.showMessageDialog(null, "This movie does not exist!");
            return;
        }
        double quantity;
        // Check whether the quantity for product is double.
        try {
            quantity = Double.parseDouble(JOptionPane.showInputDialog(null, "Enter quantity: "));
        }catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "This quantity is not valid!");
            return;
        }

        if (quantity < 0 || quantity > product.getQuantity()) {
            JOptionPane.showMessageDialog(null, "This quantity is not valid!");
            return;
        }

        OrderLine line = new OrderLine();
        line.setProductID(product.getProductID());
        line.setQuantity(quantity);
        line.setCost(quantity * product.getPrice());
        order.getLines().add(line);
        order.setTotalCost(order.getTotalCost() + line.getCost());

        Object[] row = new Object[5];
        row[0] = line.getProductID();
        row[1] = product.getName();
        row[2] = product.getPrice();
        row[3] = line.getQuantity();
        row[4] = line.getCost();

        this.addRow(row);
        this.getLabTotal().setText("Subtotal: $" + String.format("%.2f ",order.getTotalCost())+"  Total (including tax): $" + String.format("%.2f",order.getTotalCost()*1.08));
        this.invalidate();
    }
}
