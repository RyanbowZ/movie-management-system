import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainScreen extends JFrame {
    private JButton btnSell = new JButton("Movie List");
    private JButton btnBuy = new JButton("Buy Ticket");
    private JButton btnHis = new JButton("Rate Movie");
    private JButton btnRecom = new JButton("Best Movies");



    public MainScreen() {
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 300);

    }

    public void addInfo(){
        btnSell.setPreferredSize(new Dimension(150, 50));
        btnBuy.setPreferredSize(new Dimension(150, 50));
        btnHis.setPreferredSize(new Dimension(150, 50));
        btnRecom.setPreferredSize(new Dimension(150, 50));

        JLabel title = new JLabel("Movie Ticket Ordering System");
        title.setFont(new Font("Sans Serif", Font.BOLD, 24));
        JPanel panelTitle = new JPanel();
        panelTitle.add(title);
        User currentUser=Application.getInstance().getCurrentUser();

        panelTitle.add(new JLabel ("User: "+currentUser.getFullName()+"   Usertype: "+currentUser.getUsertypeStr()));

        this.getContentPane().add(panelTitle);

        JPanel panelButton = new JPanel();
        panelButton.add(btnSell);
        panelButton.add(btnBuy);
        panelButton.add(btnHis);
        panelButton.add(btnRecom);

        this.getContentPane().add(panelButton);

        btnBuy.addActionListener(new ActionListener() { // when controller is simple, we can declare it on the fly
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().getOrderView().setVisible(true);            }
        });


        btnSell.addActionListener(new ActionListener() { // when controller is simple, we can declare it on the fly
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().getMovieView().setVisible(true);
                try {
                    loadMovieList();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        btnHis.addActionListener(new ActionListener() { // when controller is simple, we can declare it on the fly
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().getRateView().setVisible(true);
            }
        });
        btnRecom.addActionListener(new ActionListener() { // when controller is simple, we can declare it on the fly
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().getRecomView().setVisible(true);
            }
        });
    }

    public void loadMovieList() throws Exception {
        Socket socket = Application.getInstance().getSocket();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Send Http request: localhost:7777/product/loadList");
        out.println("localhost:7777/product/loadList");
        String serverAnswer = in.readLine().trim();
//        System.out.println(serverAnswer);
        String[] answer = serverAnswer.split("\\+");
        for (int i = 0; i<answer.length; i+=4) {
            Object[] row = new Object[4];
            row[0] = answer[i];
            row[1] = answer[i+1];
            row[2] = answer[i+2];
            row[3] = answer[i+3];
            Application.getInstance().getMovieView().addRow(row);
        }

    }


}
