package Task3.ATM;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;

public class Solution {
    private String card;
    private String pin;
    private double balance;
    public boolean loginStatus;
    int transactionLimit=5;
    private Connection con;
    private Scanner sc=new Scanner(System.in);

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


     void register(){
        System.out.println("<------- Register Yourself --------->");
        System.out.println();
        System.out.println("ENTER Your 12 DIGIT Card Number:");
        this.card =sc.next();
        System.out.println("SET Your 4 DIGIT PIN:");
        this.pin=sc.next();
        System.out.println();

        String query="insert into bank(card_no,pin_no) values(?,?);";
        try {
            if(card.length()!=12 && pin.length()!=4)
                throw new Exception("<----!!!! Invalid Card Number or Pin Number !!!!----->\n");
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, card);
            ps.setString(2,pin);
            ps.execute();
            System.out.println("You are Successfully Registered\n");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

     void login(){
        System.out.println("<--------- Please Login to Proceed ----------->");
        System.out.println();
        System.out.println("ENTER Your Card Number:");
        this.card =sc.next();
        System.out.println("ENTER Your 4 DIGIT PIN:");
        this.pin=sc.next();
        System.out.println();

        String query="select pin_no from bank where card_no=?;";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, card);
            ResultSet rs=ps.executeQuery();
            String pinNo =null;
            while (rs.next()){
                pinNo =rs.getString("pin_no");
            }
            if(Objects.equals(pinNo, pin)){
                loginStatus=true;
                System.out.println("Login Successful\n");
            }
            else
                System.out.println("<----!!!! Invalid Card Number or PIN Number !!!!----->\n");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    double getBalance(){
         double bal=0;
        String query="select balance from bank where card_no=?;";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, card);
            ResultSet rs=ps.executeQuery();
            while (rs.next()){
                bal=rs.getDouble("balance");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
         return  bal;
    }

    String getDateTime(){
        LocalDateTime d=LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
        return d.format(dtf);
    }

    void deposit(){

        balance=getBalance();  //cur bal fetched from DB
        System.out.println("Enter Deposit Amount:");
        int  amt=sc.nextInt();
            String query=" insert into pass_book(card_num, credit, date, cur_bal) values (?,?,?,?);";
            try {
                if(amt%100!=0)
                    throw new Exception("!!!! Amount must be Multiple of 100 !!!!!");
                balance+=amt;
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1,card);
                ps.setInt(2,amt);
                ps.setString(3,getDateTime());
                ps.setDouble(4,balance);
                ps.execute();

                String qry="update bank set balance=? where card_no=?";
                ps=con.prepareStatement(qry);
                ps.setDouble(1,balance);
                ps.setString(2,card);
                ps.execute();

                System.out.println("Amount Deposited to your Account");
                System.out.println("Current balance:"+balance+"\n");

                transactionLimit--;
                System.out.println("Remaining Limit:"+transactionLimit+"\n");
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
    }

    void withDraw(){

        balance=getBalance();  //cur bal fetched from DB
        System.out.println("Enter Amount:");
        int  amt=sc.nextInt();
        try {
            if(amt>balance)
                throw new Exception("<------!!!! Insufficient Balance !!!!------->");
            if(amt%100!=0)
                throw new Exception("!!!! Amount must be Multiple of 100 !!!!!");
            balance -= amt;

            String query=" insert into pass_book(card_num, debit, date, cur_bal) values (?,?,?,?);";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,card);
            ps.setInt(2,amt);
            ps.setString(3,getDateTime());
            ps.setDouble(4,balance);
            ps.execute();

            String qry="update bank set balance=? where card_no=?";
            ps=con.prepareStatement(qry);
            ps.setDouble(1,balance);
            ps.setString(2,card);
            ps.execute();

            transactionLimit--;
            System.out.println("Amount deducted from from Account");
            System.out.println("Current balance:"+balance+"\n");
            System.out.println("Remaining Limit:"+transactionLimit+"\n");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("Current balance:"+balance+"\n");
            System.out.println("Remaining Limit:"+transactionLimit+"\n");
        }
    }

    void transfer(){
        ///sc.nextLine();
        System.out.println("Enter Recipient's  Account No:");
        String acNo=sc.next();
        System.out.println("Enter Recipient's  IFSC code:");
        String ifscCode=sc.next();

        withDraw(); //need modification related to withdrawal amount
    }

    void transactionHistory(){

        String query="select * from pass_book where card_num=?";
        try {
            double drAmt,crAmt,bal;
            String dateTime;
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,card);
            ResultSet rs=ps.executeQuery();
            while (rs.next()){
                drAmt=rs.getInt(2);
                crAmt=rs.getInt(3);
                dateTime=rs.getString(4);
                bal=rs.getDouble(5);
                System.out.println(drAmt+" Dr || "+crAmt+" Cr || "+dateTime+" || "+bal);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}
