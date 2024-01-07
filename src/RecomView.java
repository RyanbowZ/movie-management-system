import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RecomView extends JFrame implements ActionListener{
    private JTextField txtMovieType  = new JTextField(10);



    private JButton btnSubmit = new JButton("Submit");


    public RecomView() {
        this.setTitle("Best Movie");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        this.setSize(300, 150);


        JPanel panelMovieRate = new JPanel();
        panelMovieRate.add(new JLabel("Movie Type: "));
        panelMovieRate.add(txtMovieType);
        txtMovieType.setHorizontalAlignment(JTextField.RIGHT);
        SpringUtilities.makeCompactGrid(panelMovieRate,
                1, 2, //rows, cols
                6, 2,        //initX, initY
                6, 20);       //xPad, yPad
        this.getContentPane().add(panelMovieRate);


        JPanel panelButton = new JPanel();
        panelButton.add(btnSubmit);

        this.getContentPane().add(panelButton);


        this.getBtnSubmit().addActionListener(this);
    }

    public JTextField getTxtMovieType() {
        return txtMovieType;
    }



    public JButton getBtnSubmit() {
        return btnSubmit;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            recomMovie();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void recomMovie() throws Exception {
        Socket socket = Application.getInstance().getSocket();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String movieType;
        movieType = this.getTxtMovieType().getText();

        if(movieType == null) {
            JOptionPane.showMessageDialog(null, "Invalid movie ID! Please provide a valid movie ID!");
        }

        out.println("localhost:7777/recommend?movieType="+movieType);
        System.out.println("movietype="+movieType);
        //out.println("SaveReview  "+movieID+"  "+rate+"  "+review+"  "+userID);
        String serverAnswer = in.readLine();
        System.out.println(serverAnswer);
        String[] answer = serverAnswer.split("\\+");

        String movieContent = "====================Recommend Movie=====================\n" +
                "Movie ID: " + answer[0] +
                "\nMovie Name: " + answer[1] +
                "\nRate: " + answer[2] +
                "\nAudience Review: " + (answer.length>=4?answer[3]:"No available comments now");
        System.out.println(movieContent);
//        Application.getInstance().getDataAdapter().saveReview(movieID,rate,review);
        JOptionPane.showMessageDialog(null, movieContent);
        this.setVisible(false);
    }


}
