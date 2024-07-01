package LibraryManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class UserPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel userModel;
    private JTextField idTf,nameTf,rollTf;
    private  JComboBox deptBox;
    private Connection con=null;
    private  String tempDept,tempId;
    public UserPanel(Connection con){
        this.con=con;
    }

    void setUserPanel(){

        setLayout(new GridLayout(3,1));

        JPanel panel1 = new JPanel();   //for JLabel and JTextField
        JPanel panel2 = new JPanel();  // for JButton
        JPanel panel3 = new JPanel(); // for JTable

        panel1.setLayout(new GridLayout(5,2,5,5));

        add(panel1);
        add(panel2);
        add(panel3);

        JLabel idLabel = new JLabel("ID");
        panel1.add(idLabel);
        idTf=new JTextField();
        panel1.add(idTf);

        JLabel nameLabel = new JLabel("NAME");
        panel1.add(nameLabel);
        nameTf=new JTextField();
        panel1.add(nameTf);

        JLabel deptLabel = new JLabel("DEPARTMENT");
        panel1.add(deptLabel);
        //dropdown box
        String []items={
                        "SELECT DEPARTMENT","BCA","PHYSICS","CHEMISTRY","MATHEMATICS",
                        "NUTRITION","HISTORY","GEOGRAPHY","MICROBIOLOGY","ZOOLOGY"
                        };
        deptBox=new JComboBox(items);
        panel1.add(deptBox);
        deptBox.setSelectedItem("SELECT DEPARTMENT");
        deptBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempDept= Objects.requireNonNull(deptBox.getSelectedItem()).toString();
            }
        });

        JLabel rollLabel = new JLabel("ROLL NO");
        panel1.add(rollLabel);
        rollTf=new JTextField();
        panel1.add(rollTf);


        /*JTable creation */
        String []columnName={"USER ID","NAME","DEPARTMENT","ROLL NO","LIBRARY ID"};
        Object [][] data={};
        userModel =new DefaultTableModel(data,columnName);
        userTable=new JTable(userModel);
        //These two lines are important
        userTable.setPreferredScrollableViewportSize(new Dimension(550,150));
        userTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel3.add(scrollPane);


        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int selectedRowIndex=userTable.getSelectedRow();
                //0 based counting
                tempId=userTable.getValueAt(selectedRowIndex,0).toString();  //storing id temporarily
                idTf.setText(tempId);
                nameTf.setText(userTable.getValueAt(selectedRowIndex,1).toString());
                deptBox.setSelectedItem(userTable.getValueAt(selectedRowIndex,2).toString());
                rollTf.setText(userTable.getValueAt(selectedRowIndex,3).toString());
            }
        });


        JButton storeButton = new JButton("STORE");
        storeButton.setFocusable(false);
        panel2.add(storeButton);
        storeButton.addActionListener(e -> insertUser());


        JButton displayButton = new JButton("DISPLAY");
        displayButton.setFocusable(false);
        panel2.add(displayButton);
        displayButton.addActionListener(e -> displayUser());


        JButton updateButton = new JButton("UPDATE");
        updateButton.setFocusable(false);
        panel2.add(updateButton);
        updateButton.addActionListener(e -> updateUser());


        JButton deleteButton = new JButton("DELETE");
        deleteButton.setFocusable(false);
        panel2.add(deleteButton);
        deleteButton.addActionListener(e -> deleteUser());

        JButton searchButton = new JButton("SEARCH");
        searchButton.setFocusable(false);
        panel2.add(searchButton);
        searchButton.addActionListener(e ->searchUser());


    }

    void resetFields(){
        idTf.setText(null);
        nameTf.setText(null);
        deptBox.setSelectedItem("SELECT DEPARTMENT");
        rollTf.setText(null);
    }

    void insertUser(){
        if( idTf.getText().isEmpty()||nameTf.getText().isEmpty()
                || deptBox.getSelectedIndex()==0||rollTf.getText().isEmpty()
        ) {
            JOptionPane.showMessageDialog(null,"Blank Field(s)","ERROR",JOptionPane.ERROR_MESSAGE);
        }else{
            int status = JOptionPane.showConfirmDialog(null, "Do You Want to Store?", "Confirm", JOptionPane.YES_NO_OPTION);
            if(JOptionPane.YES_OPTION==0){
                String query="insert into user(u_id,u_name,u_dept,u_roll) values(?,?,?,?)";
                try{
                    PreparedStatement ps=con.prepareStatement(query);
                    ps.setString(1,idTf.getText());
                    ps.setString(2,nameTf.getText());
                    ps.setString(3, Objects.requireNonNull(deptBox.getSelectedItem()).toString());
                    ps.setInt(4,Integer.parseInt(rollTf.getText()));
                    ps.execute();
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }
        resetFields();
    }


    void displayUser(){
        /*Removing previously displayed row's items*/
        int countRow= userModel.getRowCount();  // 0 based counting
        while(countRow!=0) {
            userModel.removeRow(countRow-1);
            countRow--;
        }
        /*Displaying current row's items fetched from Data Base*/
        try {
            String query = "select * from user";        //SQL query
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            rs.next();
            while (rs.next()) {
                Object[] newRow={
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5)
                };
                userModel.addRow(newRow);
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }


    void deleteUser(){
        if(userTable.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(null,"No Row selected","ERROR",JOptionPane.ERROR_MESSAGE);
        }
        else {
            int status = JOptionPane.showConfirmDialog(null, "Do You Want to Delete?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (status == JOptionPane.YES_OPTION) {
                String query = "delete from user where u_id=?";
                try {
                    PreparedStatement ps = con.prepareStatement(query);
                    System.out.println(idTf.getText());
                    ps.setString(1,idTf.getText());
                    ps.execute();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        resetFields();
    }

    void updateUser(){
        if(userTable.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(null,"No Row selected","ERROR",JOptionPane.ERROR_MESSAGE);
        }else {
            int status = JOptionPane.showConfirmDialog(null, "Do You Want to Update?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (status == JOptionPane.YES_OPTION){
                try{
                    String query="update user set  u_name=?,u_dept=?,u_roll=? where u_id=?";
                    PreparedStatement ps=con.prepareStatement(query);
                    ps.setString(1,nameTf.getText());
                    ps.setString(2, Objects.requireNonNull(deptBox.getSelectedItem()).toString());
                    ps.setInt(3,Integer.parseInt(rollTf.getText()));
                    ps.setString(4,tempId);  // don't take idTf.getText() .as user may change idTf
                    ps.execute();
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }
        resetFields();
    }

    void  searchUser(){

    }

}
