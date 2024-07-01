package LibraryManagement;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class AdminsEnd extends JFrame {

    private  JFrame frame=new JFrame();
    private  JTabbedPane pane=new JTabbedPane();
    private Connection con=null;

    private void JDBC(){
        String url = "jdbc:mysql://localhost:3306/db1";
        String user = "root";
        String password = "admin@2003";
        try{con = DriverManager.getConnection(url, user, password);} catch (Exception ignored){}
        if(con!=null)
            System.out.println("Connection established");
    }

    //Constructor
    public AdminsEnd(){
        JDBC();
        setSize(650,650);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setComponents();
        setVisible(true);
    }

    void setComponents(){

        pane.setBounds(10,10,600,600);
        add(pane);

        BookPanel book=new BookPanel(con);
        book.setBookPanel();
        pane.add("BOOK",book);

        UserPanel user=new UserPanel(con);
        user.setUserPanel();
        pane.add("USER", user);

        IssueReturnPanel isr=new IssueReturnPanel(con);
        isr.setIssueReturnPanel();
        pane.add("ISSUE || RETURN",isr);

    }


}
