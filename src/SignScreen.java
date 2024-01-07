import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Objects;

public class SignScreen extends JFrame implements ActionListener{
    private JTextField txtUserName = new JTextField(10);
    private JTextField txtPassword = new JTextField(10);
    private JTextField txtDisplayName = new JTextField(10);
    private JButton    btnSign    = new JButton("Sign Up");
    private JComboBox comboBox=new JComboBox();



    public JButton getBtnSign() {
        return btnSign;
    }

    public JTextField getTxtPassword() {
        return txtPassword;
    }

    public JTextField getTxtUserName() {
        return txtUserName;
    }

    public JTextField getTxtDisplayName() {
        return txtDisplayName;
    }

    public JComboBox getComboBox() {
        return comboBox;
    }

    private User user;

    public SignScreen() {


        this.setSize(350, 200);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        this.getContentPane().add(new JLabel ("Store Management System"));

        JPanel main = new JPanel(new SpringLayout());
        main.add(new JLabel("Username:"));
        main.add(txtUserName);
        main.add(new JLabel("Password:"));
        main.add(txtPassword);
        main.add(new JLabel("Display Name:"));
        main.add(txtDisplayName);

        SpringUtilities.makeCompactGrid(main,
                3, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

        this.getContentPane().add(main);
        JPanel usertype=new JPanel(new SpringLayout());
        usertype.setLayout(new FlowLayout(FlowLayout.LEFT,6,0));
        usertype.add(new JLabel("User Type:       "));


        comboBox.addItem("Audience");
        comboBox.addItem("Manager");
        comboBox.addItem("Other");

        usertype.add(comboBox);
        this.getContentPane().add(usertype);
        this.getContentPane().add(btnSign);
        this.getBtnSign().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==getBtnSign()){
            try {
                signUp();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void signUp() throws Exception {
        String username = getTxtUserName().getText().trim();
        String password = getTxtPassword().getText().trim();
        String displayname = getTxtDisplayName().getText().trim();
        int usertype=getComboBox().getSelectedIndex();

        // check whether the username or password is empty
        if(username.isEmpty()||password.isEmpty()){
            JOptionPane.showMessageDialog(null, "Please enter non-empty username or password!");
            return;
        }

        Socket socket = Application.getInstance().getSocket();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Send Http request: localhost:7777/sign?username="+username+"&password="+password);
        out.println("localhost:7777/sign?username="+username+"&password="+password+"&displayname="+displayname+"&usertype="+usertype);
        String serverAnswer = in.readLine();
        if (Objects.equals(serverAnswer, "Sign up Success")) {
            JOptionPane.showMessageDialog(null, "Successfully Registered an account!");
            this.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(null, "User exist. Please use different username.");
        }
    }
}
