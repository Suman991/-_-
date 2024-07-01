package LibraryManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Banner extends JFrame {
    private JFrame frame;
    private JPanel panel;
    private JLabel l1;
    private  JButton adminButton,userButton;

    public  Banner(){
        frame=new JFrame();
        setSize(400,200);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        panel=new JPanel();
        panel.setSize(400,200);
        panel.setBackground(Color.ORANGE);
        panel.setLayout(null);
        setContentPane(panel);

        l1=new JLabel("Select User mode");
        l1.setBounds(150,20,200,80);
        l1.setFont(new Font("Serif",Font.ITALIC,20));
        panel.add(l1);

        adminButton=new JButton("ADMIN");
        adminButton.setBounds(100,100,100,30);
        panel.add(adminButton);
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Admin();   // Admin login panel will be opened
                dispose();
            }
        });

        userButton=new JButton("USER");
        userButton.setBounds(250,100,100,30);
        panel.add(userButton);
        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });





    }
}
