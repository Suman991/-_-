package LibraryManagement;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Admin extends JFrame {
    private JFrame frame;
    private JLabel l1,l2,msg;
    private  JTextField idTf;
    private JPasswordField passTf;
    private JButton b1;
    private Connection con;

    private void JDBC(){
        String url = "jdbc:mysql://localhost:3306/db1";
        String user = "root";
        String password = "admin@2003";
        try{con = DriverManager.getConnection(url, user, password);} catch (Exception ignored){}
        if(con!=null)
            System.out.println("Connection established");
    }

    public Admin(){
        JDBC();
        frame=new JFrame();
        setSize(400,200);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        l1=new JLabel("ID");
        l1.setBounds(30,30,30,30);
        add(l1);

        idTf=new JTextField();
        idTf.setBounds(150,30,100,30);
        add(idTf);

        l2=new JLabel("password");
        l2.setBounds(30,80,100,30);
        add(l2);

        passTf=new JPasswordField();
        passTf.setBounds(150,80,100,30);
        add(passTf);

        msg=new JLabel();
        msg.setBounds(50,120,150,30);
        add(msg);

        b1=new JButton("SIGN IN");
        b1.setBounds(300,120,80,30);
        add(b1);
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                msg.setText(null);
               if(isSignedIn()){
                   new AdminsEnd();   // Admin's operation panel will be opened
                   dispose();
               }
               else
                   msg.setText("Invalid Id or password!");
            }
        });

        setVisible(true);
    }

    boolean isSignedIn(){
        String pass=null;
        String query="select * from admin where id=?";
        try{
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,idTf.getText());
            ResultSet rs=ps.executeQuery();
            while(rs.next())
                pass=rs.getString(2);
        }catch (Exception e){System.out.println(e.getMessage());}
        return passTf.getText().equals(pass);
    }

}
