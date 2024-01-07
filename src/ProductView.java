import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ProductView extends JFrame implements ActionListener{
    private JTextField txtProductID  = new JTextField(10);
    private JTextField txtProductName  = new JTextField(30);
    private JTextField txtProductPrice  = new JTextField(10);
    private JTextField txtProductQuantity  = new JTextField(10);
    private JTextField txtCategoryID  = new JTextField(10);

    private JButton btnLoad = new JButton("Load Movie Info");
    private JButton btnSave = new JButton("Save Movie Info");

    public ProductView() {
        this.setTitle("Manage Movie Information");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        this.setSize(500, 200);

        JPanel panelButton = new JPanel();
        panelButton.add(btnLoad);
        panelButton.add(btnSave);
        this.getContentPane().add(panelButton);

        JPanel panelProductID = new JPanel();
        panelProductID.add(new JLabel("Movie ID: "));
        panelProductID.add(txtProductID);
        txtProductID.setHorizontalAlignment(JTextField.RIGHT);
        panelProductID.add(new JLabel("Category ID: "));
        panelProductID.add(txtCategoryID);
        txtCategoryID.setHorizontalAlignment(JTextField.RIGHT);
        this.getContentPane().add(panelProductID);

        JPanel panelProductName = new JPanel();
        panelProductName.add(new JLabel("Movie Name: "));
        panelProductName.add(txtProductName);
        this.getContentPane().add(panelProductName);

        JPanel panelProductInfo = new JPanel();
        panelProductInfo.add(new JLabel("Price: "));
        panelProductInfo.add(txtProductPrice);
        txtProductPrice.setHorizontalAlignment(JTextField.RIGHT);

        panelProductInfo.add(new JLabel("Quantity: "));
        panelProductInfo.add(txtProductQuantity);
        txtProductQuantity.setHorizontalAlignment(JTextField.RIGHT);

        this.getContentPane().add(panelProductInfo);

        this.getBtnLoad().addActionListener(this);
        this.getBtnSave().addActionListener(this);

    }

    public JButton getBtnLoad() {
        return btnLoad;
    }

    public JButton getBtnSave() {
        return btnSave;
    }

    public JTextField getTxtProductID() {
        return txtProductID;
    }

    public JTextField getTxtProductName() {
        return txtProductName;
    }

    public JTextField getTxtProductPrice() {
        return txtProductPrice;
    }

    public JTextField getTxtProductQuantity() {
        return txtProductQuantity;
    }

    public JTextField getTxtCategoryID() {
        return txtCategoryID;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.getBtnLoad()) {
            try {
                this.loadProduct();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        else
        if (e.getSource() == this.getBtnSave()) {
            try {
                this.saveProduct();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void saveProduct() throws Exception{
        Socket socket = Application.getInstance().getSocket();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        int productID;
        try {
            productID = Integer.parseInt(this.getTxtProductID().getText());
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid movie ID! Please provide a valid movie ID!");
            return;
        }

        double productPrice;
        try {
            productPrice = Double.parseDouble(this.getTxtProductPrice().getText());
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid movie price! Please provide a movie product price!");
            return;
        }
        // Check the product price should not be negative
        if (productPrice < 0 ) {
            JOptionPane.showMessageDialog(null, "This movie price is not valid! Please provide a positive price!");
            return;
        }

        double productQuantity;
        try {
            productQuantity = Double.parseDouble(this.getTxtProductQuantity().getText());
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid product quantity! Please provide a valid movie quantity!");
            return;
        }
        // Check the product quantity should not be negative
        if (productQuantity < 0 ) {
            JOptionPane.showMessageDialog(null, "This movie quantity is not valid! Please provide a positive quantity!");
            return;
        }

        String productName = this.getTxtProductName().getText().trim();

        if (productName.length() == 0) {
            JOptionPane.showMessageDialog(null, "Invalid movie name! Please provide a non-empty movie name!");
            return;
        }

        int categoryID;
        try {
            categoryID = Integer.parseInt(this.getTxtCategoryID().getText());
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid category ID! Please provide a valid category ID!");
            return;
        }
        if(categoryID<0||categoryID>4){
            JOptionPane.showMessageDialog(null, "2Invalid category ID! Please provide a valid category ID!");
            return;
        }

        // Done all validations! Make an object for this product!

        Product product = new Product();
        product.setProductID(productID);
        product.setSellerID(Application.getInstance().getCurrentUser().getUserID());
        product.setName(productName);
        product.setPrice(productPrice);
        product.setQuantity(productQuantity);
        product.setCategoryID(categoryID);

        // Store the product to the database
        out.println("localhost:7777/product/update?productID="+productID+"&productName="+product.getName()+"&productPrice="+product.getPrice()
                +"&productQuantity="+product.getQuantity()+"&categoryID="+product.getCategoryID());
//        out.println("UpdateProduct  "+productID+"  "+product.getName()+"  "+product.getPrice()+"  "+product.getQuantity()
//                +"  "+product.getCategoryID());
        String serverAnswer = in.readLine();
        System.out.println(serverAnswer);
        JOptionPane.showMessageDialog(null, "Insert new or Update existing information successfully!");
    }

    private void loadProduct() throws IOException {
        Socket socket = Application.getInstance().getSocket();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        int productID = -1;
        try {
            productID = Integer.parseInt(this.getTxtProductID().getText());
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid movie ID! Please provide a valid movie ID!");
            return;
        }

        out.println("localhost:7777/product/load?productID="+productID);
        String serverAnswer = in.readLine();
        if (serverAnswer.equals("Invalid Product")) {
            JOptionPane.showMessageDialog(null, "This movie ID does not exist in the database!");
            return;
        }
        String[] answer = serverAnswer.split("  ");
        Product product = new Product();
        product.setProductID(productID);
        product.setName(answer[0]);
        product.setPrice(Double.parseDouble(answer[1]));
        product.setQuantity(Double.parseDouble(answer[2]));
        product.setProductID(Integer.parseInt(answer[3]));
        product.setCategoryID(Integer.parseInt(answer[4]));
//        Product product = Application.getInstance().getDataAdapter().loadProduct(productID);
        System.out.println("Load Product Success");
        JOptionPane.showMessageDialog(null, "Successful query! The information is listed on screen!");

        this.getTxtProductID().setText(String.valueOf(product.getProductID()));
        this.getTxtProductName().setText(product.getName());
        this.getTxtProductPrice().setText(String.valueOf(product.getPrice()));
        this.getTxtProductQuantity().setText(String.valueOf(product.getQuantity()));
        this.getTxtCategoryID().setText(String.valueOf(product.getCategoryID()));
    }
}
