package LibraryManagement;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;


class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

    private String datePattern = "dd/MM/yyyy";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }
        return "";
    }
}


public class IssueReturnPanel extends JPanel {

    private JTextField libIdTf,bookIdTf;
    private JDatePickerImpl issuDate,retDate;
    private JTable logTable;
    private DefaultTableModel logModel;
    private Connection con;


    public IssueReturnPanel(Connection con){
        this.con=con;
    }

    void setIssueReturnPanel(){

        setLayout(new GridLayout(3,1));

        JPanel panel1 = new JPanel();   //for JLabel and JTextField
        JPanel panel2 = new JPanel();  // for JButton
        JPanel panel3 = new JPanel(); // for JTable

        panel1.setLayout(new GridLayout(4,2,5,5));

        add(panel1);
        add(panel2);
        add(panel3);

        JLabel libIdLabel = new JLabel("LIBRARY ID");
        panel1.add(libIdLabel);
        libIdTf=new JTextField();
        panel1.add(libIdTf);

        JLabel bookIdLabel = new JLabel("BOOK ID");
        panel1.add(bookIdLabel);
        bookIdTf=new JTextField();
        panel1.add(bookIdTf);

       /*Date Model creation  for issue date*/
        UtilDateModel issuModel = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl issuDatePanel = new JDatePanelImpl(issuModel,p);
        issuDate= new JDatePickerImpl(issuDatePanel,new DateLabelFormatter());

        JLabel issuDateLabel = new JLabel("ISSUE DATE");
        panel1.add(issuDateLabel);
        panel1.add(issuDate);

        /*Date Model creation for return date*/
        UtilDateModel retModel = new UtilDateModel();
        JDatePanelImpl retDatePanel = new JDatePanelImpl(retModel,p);
        retDate= new JDatePickerImpl(retDatePanel,new DateLabelFormatter());

        JLabel retDateLabel = new JLabel("RETURN DATE");
        panel1.add(retDateLabel);
        panel1.add(retDate);


        /*JTable creation */
        String []columnName={"USER ID","LIBRARY ID","BOOK ID","ISSUE DATE","RETURN DATE"};
        Object [][] data={};
        logModel =new DefaultTableModel(data,columnName);
        logTable=new JTable(logModel);
        //These two lines are important
        logTable.setPreferredScrollableViewportSize(new Dimension(550,150));
        logTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(logTable);
        panel3.add(scrollPane);


        JButton issueButton = new JButton("ISSUE");
        issueButton.setFocusable(false);
        panel2.add(issueButton);
        issueButton.addActionListener(e -> issueBook());

        JButton returnButton = new JButton("RETURN");
        returnButton.setFocusable(false);
        panel2.add(returnButton);
        returnButton.addActionListener(e -> returnBook());

        JButton logButton = new JButton("Log");
        logButton.setFocusable(false);
        panel2.add(logButton);
        logButton.addActionListener(e -> LogBook());

    }

    void issueBook(){

        if(libIdTf.getText().isEmpty()||bookIdTf.getText().isEmpty())
            JOptionPane.showMessageDialog(null,"Empty fields","ERROR",JOptionPane.ERROR_MESSAGE);
        else if(isIssued()){
            JOptionPane.showMessageDialog(null,"1 Copy Issued ","WARNING",JOptionPane.WARNING_MESSAGE);
        }
        else{
            int status = JOptionPane.showConfirmDialog(null, "Do You Want to Issue?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (status == JOptionPane.YES_OPTION){
                //libraryId authentication
                String userId="0";
                try {
                    String query = "select * from user where l_id=?";        //SQL query
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1, Integer.parseInt(libIdTf.getText()));
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        userId=rs.getString(1);
                    }
                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }

                if(userId.equals("0"))   //
                    JOptionPane.showMessageDialog(null,"Invalid Library Id","WARNING",JOptionPane.WARNING_MESSAGE);
                else {
                    int unit=0;
                    //fetching  bookUnit from "book" table in database
                    try {
                        String query = "select * from book where bookid=?";        //SQL query
                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setInt(1, Integer.parseInt(bookIdTf.getText()));
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            unit=rs.getInt(6);
                        }
                    }catch (Exception ex){
                        System.out.println(ex.getMessage());
                    }

                    if(unit==0)   // no book  or  invalid bookId
                        JOptionPane.showMessageDialog(null,"Book not available","WARNING",JOptionPane.WARNING_MESSAGE);
                    else{
                        //updating "book" table in database
                        try {
                            String query = "update book set unit=? where bookid=?";
                            PreparedStatement ps = con.prepareStatement(query);
                            ps.setInt(1,(unit-1));
                            ps.setInt(2, Integer.parseInt(bookIdTf.getText()));
                            ps.execute();
                        }catch (Exception ex){
                            System.out.println(ex.getMessage());
                        }
                        //formatting date
                        Date isDt=(Date) issuDate.getModel().getValue();
                        Date retDt =(Date) retDate.getModel().getValue();
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

                        //insert Entry into "book_log" table in database
                        try{
                            String query="insert into log_book values(?,?,?,?,?)";
                            PreparedStatement ps=con.prepareStatement(query);
                            ps.setString(1,userId);
                            ps.setInt(2, Integer.parseInt(libIdTf.getText()));
                            ps.setInt(3, Integer.parseInt(bookIdTf.getText()));
                            ps.setString(4,dateFormat.format(isDt));
                            ps.setString(5,dateFormat.format(retDt));
                            ps.execute();
                        }catch(Exception ex){
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            }
        }
    }

    boolean isIssued(){
        String userId="0";
        try {
            String query = "select * from log_book where l_id=? and b_id=?";        //SQL query
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(libIdTf.getText()));
            ps.setInt(2,Integer.parseInt(bookIdTf.getText()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                userId =rs.getString(1);
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return !userId.equals("0");
    }


    void returnBook() {
        if (libIdTf.getText().isEmpty() || bookIdTf.getText().isEmpty())
            JOptionPane.showMessageDialog(null, "Empty fields", "ERROR", JOptionPane.ERROR_MESSAGE);
        else {
            int status = JOptionPane.showConfirmDialog(null, "Do You Want to Return?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (status == JOptionPane.YES_OPTION){
                //  libraryId , bookId  authentication
                String userId="0";
                try {
                    String query = "select * from log_book where l_id=? and b_id=?";        //SQL query
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1, Integer.parseInt(libIdTf.getText()));
                    ps.setInt(2,Integer.parseInt(bookIdTf.getText()));
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        userId =rs.getString(1);
                    }
                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }
                if(userId.equals("0"))
                    JOptionPane.showMessageDialog(null,"Invalid Library Id  ||  Book Id","WARNING",JOptionPane.WARNING_MESSAGE);
                else{
                    int unit=0;
                    //fetching  bookUnit from "book" table in database
                    try {
                        String query = "select * from book where bookid=?";        //SQL query
                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setInt(1, Integer.parseInt(bookIdTf.getText()));
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            unit=rs.getInt(6);
                        }
                    }catch (Exception ex){
                        System.out.println(ex.getMessage());
                    }

                    //updating "book" table in database
                    try {
                        String query = "update book set unit=? where bookid=?";
                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setInt(1,(unit+1));
                        ps.setInt(2, Integer.parseInt(bookIdTf.getText()));
                        ps.execute();
                    }catch (Exception ex){
                        System.out.println(ex.getMessage());
                    }
                    //delete Entry from "book_log" table in database
                    try{
                        String query = "delete from log_book where l_id=? and b_id=?";        //SQL query
                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setInt(1, Integer.parseInt(libIdTf.getText()));
                        ps.setInt(2,Integer.parseInt(bookIdTf.getText()));
                        ps.execute();
                    }catch (Exception ex){
                        System.out.println(ex.getMessage());
                    }
                }
            }
        }
    }



    void LogBook(){
        /*Removing previously displayed row's items*/
        int countRow= logModel.getRowCount();  // 0 based counting
        while(countRow!=0) {
            logModel.removeRow(countRow-1);
            countRow--;
        }
        /*Displaying current row's items fetched from Data Base*/
        try {
            String query = "select * from log_book";        //SQL query
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] newRow={
                        rs.getString(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getString(4),
                        rs.getString(5)
                };
                logModel.addRow(newRow);
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}
