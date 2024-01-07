import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RateView extends JFrame implements ActionListener{
    private JTextField txtMovieID  = new JTextField(10);
    private JTextField txtMovieRate  = new JTextField(10);
    private JTextField txtMovieReview  = new JTextField(10);


    private JButton btnSubmit = new JButton("Submit");


    public RateView() {
        this.setTitle("Rate Movies");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        this.setSize(500, 300);


        JPanel panelMovieRate = new JPanel();
        panelMovieRate.add(new JLabel("Movie ID: "));
        panelMovieRate.add(txtMovieID);
        txtMovieID.setHorizontalAlignment(JTextField.RIGHT);
        panelMovieRate.add(new JLabel("Rate(1~5): "));
        panelMovieRate.add(txtMovieRate);
        txtMovieRate.setHorizontalAlignment(JTextField.RIGHT);
        this.getContentPane().add(panelMovieRate);

        JPanel panelMovieReview = new JPanel(new SpringLayout());
        panelMovieReview.add(new JLabel("Review: "));
        panelMovieReview.add(txtMovieReview);
        SpringUtilities.makeCompactGrid(panelMovieReview,
                1, 2, //rows, cols
                6, 2,        //initX, initY
                6, 20);       //xPad, yPad
        this.getContentPane().add(panelMovieReview);

        JPanel panelButton = new JPanel();
        panelButton.add(btnSubmit);

        this.getContentPane().add(panelButton);


        this.getBtnSubmit().addActionListener(this);
    }

    public JTextField getTxtMovieID() {
        return txtMovieID;
    }

    public JTextField getTxtMovieRate() {
        return txtMovieRate;
    }

    public JTextField getTxtMovieReview() {
        return txtMovieReview;
    }

    public JButton getBtnSubmit() {
        return btnSubmit;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            rateMovie();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void rateMovie() throws Exception {
        Socket socket = Application.getInstance().getSocket();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        int movieID;
        try {
            movieID = Integer.parseInt(this.getTxtMovieID().getText());
        }
        catch (NumberFormatException s) {
            JOptionPane.showMessageDialog(null, "Invalid movie ID! Please provide a valid movie ID!");
            return;
        }

        int rate;
        try {
            rate = Integer.parseInt(this.getTxtMovieRate().getText());
        }
        catch (NumberFormatException s) {
            JOptionPane.showMessageDialog(null, "Please input integer rate!");
            return;
        }
        if(rate<1||rate>5){
            JOptionPane.showMessageDialog(null, "The range of rate must be within 1~5!");
            return;
        }
        int userID = Application.getInstance().getCurrentUser().getUserID();
        String review =this.getTxtMovieReview().getText();
        out.println("localhost:7777/review/save?movieID="+movieID+"&rate="+rate+"&review="+review
                +"&userID="+userID);
        //out.println("SaveReview  "+movieID+"  "+rate+"  "+review+"  "+userID);
        String serverAnswer = in.readLine();
        String[] answer = serverAnswer.split("  ");
        System.out.println(answer[0]);
//        Application.getInstance().getDataAdapter().saveReview(movieID,rate,review);
        JOptionPane.showMessageDialog(null, "Thank you for your feedback!");
        this.setVisible(false);
    }


}
