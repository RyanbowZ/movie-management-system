import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLEncoder;

import java.util.List;

public class PaymentView extends JFrame implements ActionListener{
    private JTextField txtAdds = new JTextField(20);
    private JTextField txtCard = new JTextField(20);
    private JButton    btnPay    = new JButton("Pay");
    private int orderID;

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public JButton getBtnPay() {
        return btnPay;
    }

    public JTextField getTxtAdds() {
        return txtAdds;
    }

    public JTextField getTxtCard() {
        return txtCard;
    }
    public PaymentView(){
        this.setSize(350, 150);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        this.getContentPane().add(new JLabel ("Payment Page"));

        JPanel main = new JPanel(new SpringLayout());
        main.add(new JLabel("Credit Card:"));
        main.add(txtCard);
        main.add(new JLabel("Addresses:"));
        main.add(txtAdds);

        SpringUtilities.makeCompactGrid(main,
                2, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

        this.getContentPane().add(main);
        this.getContentPane().add(btnPay);
        this.getBtnPay().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == getBtnPay()) {
            try {
                pay();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void pay() throws Exception {
        Socket socket = Application.getInstance().getSocket();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String creditcard = getTxtCard().getText().trim();
        String address = getTxtAdds().getText().trim();
        // check whether the username or password is empty
        if (creditcard.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter non-empty information!");
            return;
        }
        if (creditcard.length() < 5) {
            JOptionPane.showMessageDialog(null, "The credit card number is too short!");
            return;
        }

        User currentUser = Application.getInstance().getCurrentUser();
        Order currentOrder = Application.getInstance().getOrderView().getOrder();

        Payment payment = new Payment();

        payment.setAddress(address);
        payment.setCreditcard(creditcard);
        payment.setOrderID(currentOrder.getOrderID());
        out.println("localhost:7777/payment/save?address="+payment.getAddress()+"&creditCard="+payment.getCreditcard()+"&orderID="+payment.getOrderID());
//        out.println("SavePayment  "+payment.getAddress()+"  "+payment.getCreditcard()+"  "+
//                payment.getOrderID());
        String serverAnswer = in.readLine();
        String[] answer = serverAnswer.split("  ");
        System.out.println(answer[0]);
//        Application.getInstance().getDataAdapter().savePayment(payment);

        String receiptContent = "====================Receipt=====================\n" +
                "Customer ID: " + currentUser.getUserID() +
                "\nCustomer Name: " + currentUser.getFullName() +
                "\nCredit Card Number (last 4 digits): " + creditcard.substring(creditcard.length() - 4) +
                "\nShipping address: " + address +
                "\nOrder ID: " + currentOrder.getOrderID() +
                "\nOrder Time: " + currentOrder.getDate() +
                "\nOrder Cost: " + currentOrder.getTotalCost() + "\n------Purchase Details-----\n";
        for (OrderLine od : currentOrder.getLines()) {
            Product product = Application.getInstance().getDataAdapter().loadProduct(od.getProductID());
            receiptContent += ("Movie ID:" + od.getProductID() + "\t  Name:" + product.getName() + "\t  Price:" + product.getPrice() + "\t  Quantity:" + od.getQuantity() + "\t  Cost:" + od.getCost() + "\n");
        }
        out.println("localhost:7777/receipt/save?orderID="+payment.getOrderID()+"&receiptContent="+URLEncoder.encode(receiptContent, "UTF-8"));

//        serverAnswer = in.readLine();
//        answer = serverAnswer.split("  ");
//        System.out.println(answer[0]);

        JOptionPane.showMessageDialog(null, receiptContent);
        this.setVisible(false);
    }
}
