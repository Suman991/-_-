package Task3.ATM;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Solution {
    private int id;
    private int pin;
    private double balance;
    private String tranHistory;
    public boolean loginStatus;
    int transactionLimit=5;
    private Connection con;

    private void JDBC(){
        String url = "jdbc:mysql://localhost:3306/db1";
        String user = "root";
        String password = "admin@2003";
        try{con = DriverManager.getConnection(url, user, password);} catch (Exception e){
            System.out.println(e.getMessage());
        }
        if(con!=null)
            System.out.println("Connection established\n");
    }
    Solution(){
        JDBC();
    }
    Scanner sc=new Scanner(System.in);
     void register(){
        System.out.println("<------- USER CREATION--------->");
        System.out.println();
        System.out.println("ENTER Your 6 DIGIT Id:");
        this.id=sc.nextInt();
        System.out.println("SET Your 4 DIGIT PIN:");
        this.pin=sc.nextInt();
        System.out.println();

        String query="insert into bank(id,password) values(?,?);";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,id);
            ps.setInt(2,pin);
            ps.execute();
            System.out.println("You are Successfully Registered\n");
        }catch (Exception e){
            System.out.println("!!!!! Id Already Registered!!!!!!\n");
            System.out.println(e.getMessage());
        }
    }

     void login(){
        System.out.println("<--------- USER VERIFICATION----------->");
        System.out.println();
        System.out.println("ENTER Your Id:");
        this.id=sc.nextInt();
        System.out.println("ENTER Your 4 DIGIT PIN:");
        this.pin=sc.nextInt();
        System.out.println();

        String query="select password from bank where id=?;";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();
            int pass=0;
            while (rs.next()){
                pass=rs.getInt("password");
            }
            if(pass==pin){
                loginStatus=true;
                System.out.println("You are Successfully Login\n");
            }
            else
                System.out.println("<----!!!! Invalid ID or PIN !!!!----->\n");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    double getBalance(){
         double bal=0;
        String query="select balance from bank where id=?;";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();
            while (rs.next()){
                bal=rs.getDouble("balance");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
         return  bal;
    }

    void deposit(){
        transactionLimit--;
        balance=getBalance();  //cur bal fetched from DB
        System.out.println("Enter Deposit Amount:");
        double amt=sc.nextDouble();
        balance+=amt;

        LocalDateTime d=LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
        String dT=d.format(dtf);
        StringBuilder bf=new StringBuilder();
        bf.append(amt).append("Rs--Cr--").append(dT).append(" || ");

        //for concat function set DEFAULT value=" " for thc column "history"
        String query=" update bank set balance=?, history=concat(history,?) where id=?;";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDouble(1,balance);
            ps.setString(2,bf.toString());
            ps.setInt(3,id);
            ps.execute();
            System.out.println("Amount Deposited to your Account");
            System.out.println("Current balance:"+balance+"\n");
            System.out.println("Remaining Limit:"+transactionLimit+"\n");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    void withDraw(){
        transactionLimit--;
        balance=getBalance();  //cur bal fetched from DB
        System.out.println("Enter Amount:");
        double amt=sc.nextDouble();

        LocalDateTime d=LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
        String dT=d.format(dtf);
        StringBuilder bf=new StringBuilder();
        bf.append(amt).append("Rs--Dr--").append(dT).append(" || ");

        try {
            if(amt>balance)
                throw new Exception("<------!!!! INSUFFICIENT BALANCE!!!!------->");
            balance -= amt;
            String query=" update bank set balance=?,history=concat(history,?) where id=?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDouble(1,balance);
            ps.setString(2,bf.toString());
            ps.setInt(3,id);
            ps.execute();
            System.out.println("Amount deducted  from your Account");
            System.out.println("Current balance:"+balance+"\n");
            System.out.println("Remaining Limit:"+transactionLimit+"\n");
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("Current balance:"+balance+"\n");
            System.out.println("Remaining Limit:"+transactionLimit+"\n");
        }
    }

    void transfer(){
        System.out.println("Enter Recipient's  Account No:");
        String acNo=sc.next();
        System.out.println("Enter Recipient's  IFSC code:");
        String ifsc=sc.next();
        withDraw();
    }

    void transactionHistory(){
        String query="select history from bank where id=?;";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();
            while (rs.next()){
               tranHistory=rs.getString("history");
            }
            System.out.println(tranHistory+"\n");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}
