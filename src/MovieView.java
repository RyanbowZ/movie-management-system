import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class MovieView extends JFrame implements ActionListener{

    private JButton btnOK = new JButton("Exit");

    private DefaultTableModel items = new DefaultTableModel(); // store information for the table!

    private JTable tblItems = new JTable(items);


    public MovieView() {

        this.setTitle("Movie List");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setSize(400, 600);


        items.addColumn("Movie ID");
        items.addColumn("Movie Name");
        items.addColumn("Category");
        items.addColumn("Rate");



        JPanel panelMovie = new JPanel();
        panelMovie.setPreferredSize(new Dimension(400, 450));
        panelMovie.setLayout(new BoxLayout(panelMovie, BoxLayout.PAGE_AXIS));
        tblItems.setBounds(0, 0, 400, 350);
        panelMovie.add(tblItems.getTableHeader());
        panelMovie.add(tblItems);

        tblItems.setFillsViewportHeight(true);
        this.getContentPane().add(panelMovie);

        JPanel panelButton = new JPanel();
        panelButton.setPreferredSize(new Dimension(400, 100));
        panelButton.add(btnOK);
        this.getContentPane().add(panelButton);

        this.getBtnOK().addActionListener(this);




    }

    public JButton getBtnOK() {
        return btnOK;
    }


    public void addRow(Object[] row) {
        items.addRow(row);
    }



    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.getBtnOK()) {
            while(items.getRowCount()>0){
                items.removeRow(0);
            }
            this.setVisible(false);

        }

    }


}
