package LibraryManagement;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookPanel extends JPanel {

    private JTable bookTable;
    private DefaultTableModel bookModel;
    private JTextField idTf,titleTf,priceTf,authorTf,publisherTf,unitTf;
    private int tempBookId;
    private  BookCla bookObj;
    private Connection con=null;

    public BookPanel(Connection con){
        this.con=con;
    }

    void setBookPanel(){
        setLayout(new GridLayout(3,1));

        JPanel panel1 = new JPanel();   //for JLabel and JTextField
        JPanel panel2 = new JPanel();  // for JButton
        JPanel panel3 = new JPanel(); // for JTable

        panel1.setLayout(new GridLayout(6,2,5,5));

        add(panel1);
        add(panel2);
        add(panel3);

        JLabel idLabel = new JLabel("BOOK ID");
        panel1.add(idLabel);
        idTf=new JTextField();
        panel1.add(idTf);

        JLabel titleLabel = new JLabel("TITLE");
        panel1.add(titleLabel);
        titleTf=new JTextField();
        panel1.add(titleTf);

        JLabel priceLabel = new JLabel("PRICE");
        panel1.add(priceLabel);
        priceTf=new JTextField();
        panel1.add(priceTf);

        JLabel authorLabel = new JLabel("AUTHOR");
        panel1.add(authorLabel);
        authorTf=new JTextField();
        panel1.add(authorTf);

        JLabel publisherLabel = new JLabel("PUBLISHER");
        panel1.add(publisherLabel);
        publisherTf=new JTextField();
        panel1.add(publisherTf);

        JLabel unitLabel = new JLabel("UNIT");
        panel1.add(unitLabel);
        unitTf=new JTextField();
        panel1.add(unitTf);

        /*JTable creation */
        String []columnName={"BOOK ID","TITLE","PRICE","AUTHOR","PUBLISHER","UNIT"};
        Object [][] data={};
        bookModel =new DefaultTableModel(data,columnName);
        bookTable=new JTable(bookModel);
        //These two lines are important
        bookTable.setPreferredScrollableViewportSize(new Dimension(550,150));
        bookTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        panel3.add(scrollPane);

        /*Event handling for JTable*/
        bookTable.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int rowIndex=bookTable.getSelectedRow();
                //0 based counting
                tempBookId= (int) bookTable.getValueAt(rowIndex,0);  //storing BookId temporarily
                idTf.setText(bookTable.getValueAt(rowIndex,0).toString());
                titleTf.setText(bookTable.getValueAt(rowIndex,1).toString());
                priceTf.setText(bookTable.getValueAt(rowIndex,2).toString());
                authorTf.setText(bookTable.getValueAt(rowIndex,3).toString());
                publisherTf.setText(bookTable.getValueAt(rowIndex,4).toString());
                unitTf.setText(bookTable.getValueAt(rowIndex,5).toString());
            }
        });

        bookObj=new BookCla(); //object creation

        JButton storeButton = new JButton("STORE");
        storeButton.setFocusable(false);
        panel2.add(storeButton);
        storeButton.addActionListener(e -> insertBook());


        JButton displayButton = new JButton("DISPLAY");
        displayButton.setFocusable(false);
        panel2.add(displayButton);
        displayButton.addActionListener(e -> displayBook());


        JButton updateButton = new JButton("UPDATE");
        updateButton.setFocusable(false);
        panel2.add(updateButton);
        updateButton.addActionListener(e -> updateBook());


        JButton deleteButton = new JButton("DELETE");
        deleteButton.setFocusable(false);
        panel2.add(deleteButton);
        deleteButton.addActionListener(e -> deleteBook());

        JButton searchButton = new JButton("SEARCH");
        searchButton.setFocusable(false);
        panel2.add(searchButton);
        searchButton.addActionListener(e ->searchBook());

    }


    void resetBookFields(){
        /*Resetting Input Fields */
        idTf.setText(null);
        titleTf.setText(null);
        priceTf.setText(null);
        authorTf.setText(null);
        publisherTf.setText(null);
        unitTf.setText(null);
    }


    void insertBook(){
        if(
                idTf.getText().isEmpty()||titleTf.getText().isEmpty()||
                        priceTf.getText().isEmpty()||authorTf.getText().isEmpty()||
                        publisherTf.getText().isEmpty()
        ){
            JOptionPane.showMessageDialog(null,"Blank Field(s)","ERROR",JOptionPane.ERROR_MESSAGE);
        }
        else{
            int status = JOptionPane.showConfirmDialog(null, "Do You Want to Store?", "Confirm", JOptionPane.YES_NO_OPTION);
            if(JOptionPane.YES_OPTION==0){
                //inserting data into Objects from Input fields
                bookObj.setBookId(Integer.parseInt(idTf.getText()));
                bookObj.setTitle(titleTf.getText());
                bookObj.setPrice(Double.parseDouble(priceTf.getText()));
                bookObj.setAuthor(authorTf.getText());
                bookObj.setPublisher(publisherTf.getText());
                bookObj.setUnit(Integer.parseInt(unitTf.getText()));
                //inserting data into DataBases from Object
                String query = "insert into book values(?,?,?,?,?,?)";
                try {
                    PreparedStatement ps = con.prepareStatement(query);
                    //providing values in place of '?' in the  SQL query.
                    ps.setInt(1, bookObj.getBookId());   // 1 based counting.
                    ps.setString(2, bookObj.getTitle().toUpperCase());
                    ps.setDouble(3, bookObj.getPrice());
                    ps.setString(4, bookObj.getAuthor());
                    ps.setString(5, bookObj.getPublisher());
                    ps.setInt(6,bookObj.getUnit());

                    ps.execute();
                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }
            }
        }
        resetBookFields();
    }


    void displayBook(){
        /*Removing previously displayed row's items*/
        int countRow= bookModel.getRowCount();  // 0 based counting
        while(countRow!=0) {
            bookModel.removeRow(countRow-1);
            countRow--;
        }
        /*Displaying current row's items fetched from Data Base*/
        try {
            String query = "select * from book";        //SQL query
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] newRow={
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getInt(6)
                };
                bookModel.addRow(newRow);
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }



    void searchBook(){
        if(titleTf.getText().isBlank()){
            JOptionPane.showMessageDialog(null,"Blank Title","ERROR",JOptionPane.ERROR_MESSAGE);
        }else{
            /*Removing previously displayed row's items*/
            int countRow= bookModel.getRowCount();  // 0 based counting
            while(countRow!=0) {
                bookModel.removeRow(countRow-1);
                countRow--;
            }
            /*Displaying current row's items fetched from Data Base*/
            try {
                String query = "select * from book where title=?";        //SQL query
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1,titleTf.getText().toUpperCase());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Object[] newRow={
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getDouble(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getInt(6)
                    };
                    bookModel.addRow(newRow);
                }
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }

    }


    void updateBook(){
        if(bookTable.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(null,"No Row selected","ERROR",JOptionPane.ERROR_MESSAGE);
        }
        else {
            int status = JOptionPane.showConfirmDialog(null, "Do You Want to Update?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (status == JOptionPane.YES_OPTION) {
                //inserting updated data into object
                bookObj.setBookId(tempBookId);
                bookObj.setTitle(titleTf.getText());
                bookObj.setPrice(Double.parseDouble(priceTf.getText()));
                bookObj.setAuthor(authorTf.getText());
                bookObj.setPublisher(publisherTf.getText());
                bookObj.setUnit(Integer.parseInt(unitTf.getText()));
                //updating  "book" table in Data base
                String query = "update book set title=?,price=?,author=?,publisher=?,unit=? where bookid=?";
                try {
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, bookObj.getTitle().toUpperCase());  //1 based counting
                    ps.setDouble(2, bookObj.getPrice());
                    ps.setString(3, bookObj.getAuthor());
                    ps.setString(4, bookObj.getPublisher());
                    ps.setInt(5,bookObj.getUnit());
                    ps.setInt(6,bookObj.getBookId());

                    ps.execute();
                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }
            }
        }
        resetBookFields();
    }


    void deleteBook(){
        if(bookTable.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(null,"No Row selected","ERROR",JOptionPane.ERROR_MESSAGE);
        }
        else{
            int status=JOptionPane.showConfirmDialog(null,"Do You Want to Delete?","Confirm",JOptionPane.YES_NO_OPTION);
            if(status==JOptionPane.YES_OPTION){
                String query="delete from book where bookid=?";
                try {
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1,Integer.parseInt(idTf.getText()));
                    ps.execute();
                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }
            }
            resetBookFields();
        }
    }

}
