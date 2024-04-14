package Task2;

import javax.swing.*;
import java.awt.*;

public class Input extends  JFrame {
    private JPanel panel;
    private JButton b1;
    private JLabel l1;
    private JLabel l2;
    private JTextField upperRange;
    private JTextField lowerRange;
    private JLabel output;

    public Input(){
        setVisible(true);
        setSize(400,300);
        setLayout(null);
        setResizable(false);
        setTitle("Select Range");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setContentPane(panel);
        //panel.setBackground(Color.CYAN);

        try{panel.add(l2);}catch (Exception ignored){}
        l2.setBounds(10,50,180,40);
        l2.setForeground(Color.blue);
        l2.setFont(new Font("Serif",Font.ITALIC,20));

        try{panel.add(lowerRange);}catch (Exception ignored){}
        lowerRange.setBounds(170,50,80,40);

        try{panel.add(l1);}catch (Exception ignored){}
        l1.setBounds(10,120,180,40);
        l1.setForeground(Color.blue);
        l1.setFont(new Font("Serif",Font.ITALIC,20));

        try{panel.add(upperRange);}catch (Exception ignored){}
        upperRange.setBounds(170,120,80,40);

        try{panel.add(output);}catch (Exception ignored){}
        output.setBounds(20,180,200,40);
        output.setForeground(Color.red);
        output.setFont(new Font("Serif",Font.ITALIC,15));

        try{panel.add(b1);}catch (Exception ignored){}
        b1.setBounds(300,200,80,40);
        b1.setBackground(Color.green);
        b1.setFont(new Font("Serif",Font.ITALIC,20));
        try{b1.setFocusable(false);}catch (Exception ignored){}
        b1.addActionListener(e -> {
            if(lowerRange.getText().isBlank()||upperRange.getText().isBlank())
                output.setText("Please Enter Valid Limits");
            GuessNumber ob = new GuessNumber(
                        Integer.parseInt(lowerRange.getText()),
                        Integer.parseInt(upperRange.getText())
            );
            dispose();
        });
    }
}
