package Task2;

import javax.swing.*;
import java.awt.*;

public class GuessNumber extends  JFrame {
    private JPanel panel;
    private JLabel inputLabel;
    private JTextField inputNumb;
    private JLabel outputLabel;
    private JButton okButton;
    private JLabel attemptLabel;
    private JLabel attemptNo;
    private JButton clearButton;
    private  int attempt;
    private final int actualNumb;

    private int getRandomNumber(int lowerBound,int upperBound){
        int range=upperBound-lowerBound+1;
        return (int)(Math.random()*range+lowerBound);
    }

    public GuessNumber(int lowerRange,int upperRange){
        setVisible(true);
        setSize(500,400);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setContentPane(panel);

        try{panel.add(attemptLabel);}catch (Exception ignored){}
        attemptLabel.setBounds(300,30,120,30);
        attemptLabel.setForeground(Color.RED);
        attemptLabel.setFont(new Font("Serif",Font.ITALIC,20));

        try{panel.add(attemptNo);}catch (Exception ignored){}
        attemptNo.setBounds(420,30,50,30);
        attemptNo.setForeground(Color.black);
        attemptNo.setFont(new Font("Serif",Font.ITALIC,25));

        try{panel.add(inputLabel);}catch (Exception ignored){}
        inputLabel.setBounds(50,180,150,50);
        inputLabel.setForeground(Color.blue);
        inputLabel.setFont(new Font("Serif",Font.ITALIC,20));

        try{panel.add(inputNumb);}catch (Exception ignored){}
        inputNumb.setBounds(230,180,80,40);

        try{panel.add(outputLabel);}catch (Exception ignored){}
        outputLabel.setBounds(100,230,300,80);
        outputLabel.setForeground(Color.magenta);
        outputLabel.setFont(new Font("Serif",Font.ITALIC,18));

        try{panel.add(clearButton);}catch (Exception ignored){}
        clearButton.setBounds(300,300,80,40);
        try{clearButton.setFocusable(false);}catch (Exception ignored){}
        clearButton.setBackground(Color.red);
        clearButton.setFont(new Font("Serif",Font.ITALIC,20));
        clearButton.addActionListener(e -> {
            inputNumb.setText("");
            outputLabel.setText("");
        });

        try{panel.add(okButton);}catch (Exception ignored){}
        okButton.setBounds(400,300,80,40);
        okButton.setBackground(Color.green);
        okButton.setFont(new Font("Serif",Font.ITALIC,20));
        try{okButton.setFocusable(false);}catch (Exception ignored){}

        actualNumb=getRandomNumber(lowerRange,upperRange);
       // System.out.println("Number:"+actualNumb);
        okButton.addActionListener(e -> {
            int guessNumb=Integer.parseInt(inputNumb.getText());
            if(actualNumb==guessNumb){
                outputLabel.setText("WoW ,You Won!");
                attemptNo.setText(Integer.toString(++attempt));
                okButton.setEnabled(false);
                clearButton.setEnabled(false);
            }
            else if(actualNumb>guessNumb){
                outputLabel.setText("Ha Ha! My number is larger");
                attemptNo.setText(Integer.toString(++attempt));
            }
            else {
                outputLabel.setText("Ha Ha! My number is smaller");
                attemptNo.setText(Integer.toString(++attempt));
            }
            if(attempt==5&&actualNumb!=guessNumb){
                outputLabel.setText("Attempts Exceeded! Number:"+actualNumb);
                okButton.setEnabled(false);
                clearButton.setEnabled(false);
            }
        });

    }
}
