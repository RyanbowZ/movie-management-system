import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginScreen extends JFrame implements ActionListener{
    private JTextField txtUserName = new JTextField(10);
    private JTextField txtPassword = new JTextField(10);
    private JButton    btnLogin    = new JButton("Login");
    private JButton    btnSign    = new JButton("Sign Up");

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JButton getBtnSign() {
        return btnSign;
    }

    public JTextField getTxtPassword() {
        return txtPassword;
    }

    public JTextField getTxtUserName() {
        return txtUserName;
    }

    private User user;

    public LoginScreen() {


        this.setSize(350, 150);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        this.getContentPane().add(new JLabel ("Movie Ticket Management System"));

        JPanel main = new JPanel(new SpringLayout());
        main.add(new JLabel("Username:"));
        main.add(txtUserName);
        main.add(new JLabel("Password:"));
        main.add(txtPassword);

        SpringUtilities.makeCompactGrid(main,
                2, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

        this.getContentPane().add(main);
        JPanel button = new JPanel(new SpringLayout());

        button.add(btnLogin);

        button.add(btnSign);
        button.setLayout(new FlowLayout(2,10,5));

        this.getContentPane().add(button);

        this.getBtnLogin().addActionListener(this);
        this.getBtnSign().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String serverHostname = new String("127.0.0.1");
        int portNumber = 7777;

        //System.out.println("Attemping to connect to host " + serverHostname + " on port " + portNumber);

        if (e.getSource() == getBtnLogin()) {
            try {
                login();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
//            String username = getTxtUserName().getText().trim();
//            String password = getTxtPassword().getText().trim();
//            // check whether the username or password is empty
//            if(username.isEmpty()||password.isEmpty()){
//                JOptionPane.showMessageDialog(null, "Please enter non-empty username or password!");
//                return;
//            }
//            System.out.println("Login with username = " + username + " and password = " + password);
//            user = Application.getInstance().getDataAdapter().loadUser(username, password);
//
//            if (user == null) {
//                JOptionPane.showMessageDialog(null, "This user does not exist!");
//            }
//            else {
//                Application.getInstance().setCurrentUser(user);
//                this.setVisible(false);
//                if(user.getUsertype()==0) {
//                    //Application.getInstance().getMainScreen().setFullname(user.getFullName());
//                    Application.getInstance().getMainScreen().addInfo();
//                    Application.getInstance().getMainScreen().setVisible(true);
//                }
//                else if(user.getUsertype()==1){
//
//                    Application.getInstance().getManagerScreen().addInfo();
//                    Application.getInstance().getManagerScreen().setVisible(true);
//                }
//
//            }
        }
        else if(e.getSource()==getBtnSign()){
            Application.getInstance().getSignScreen().setVisible(true);
        }
    }

    public void login() throws Exception {
        String serverHostname = new String("127.0.0.1");
        int portNumber = 7777;


//        Socket socket = new Socket(serverHostname, portNumber);
        Socket socket = Application.getInstance().getSocket();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String username = getTxtUserName().getText().trim();
        String password = getTxtPassword().getText().trim();
        if(username.equals("")) {
            JOptionPane.showMessageDialog(null, "Username should not be empty!");
            return;
        }
        if(password.equals("")) {
            JOptionPane.showMessageDialog(null, "Password should not be empty!");
            return;
        }
        System.out.println("Send Http request: localhost:7777/login?username="+username+"&password="+password);

        out.println("localhost:7777/login?username="+username+"&password="+password);
        String serverAnswer = in.readLine();
        if(serverAnswer.equals("Invalid User")) {
            JOptionPane.showMessageDialog(null, "This user does not exist!");
        } else {
            //System.out.println("LOGIN Success.");
            System.out.println("Get Http response: Login with username = " + username + " and password = " + password);
            System.out.println("serverAnswer = "+serverAnswer);
            String[] answer = serverAnswer.split("  ");
            User user = new User();
            user.setUserID(Integer.parseInt(answer[0]));
            user.setUsername(answer[1]);
            user.setPassword(answer[2]);
            user.setFullName(answer[3]);
            user.setUsertype(Integer.parseInt(answer[4]));
            Application.getInstance().setCurrentUser(user);
            System.out.println("usertype="+user.getUsertype());
            setVisible(false);
            if (user.getUsertype() == 0) {
                Application.getInstance().getMainScreen().addInfo();
                Application.getInstance().getMainScreen().setVisible(true);
            } else if (user.getUsertype() == 1) {

                Application.getInstance().getManagerScreen().addInfo();
                Application.getInstance().getManagerScreen().setVisible(true);
            }

        }



    }
}
