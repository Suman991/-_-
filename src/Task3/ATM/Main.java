package Task3.ATM;
import java.util.Scanner;

public class Main {
    static Solution s1=new Solution();

    public static void main(String[] args) {
        getOption();     // Find out Login or Register  ??

        int ch;
        Scanner sc=new Scanner(System.in);
        while(true) {
            if(s1.transactionLimit==0){
                System.out.println("------!!!Daily Limit Exceeded!!!!-------\n");
                System.exit(0);
            }
            if(s1.loginStatus){
                System.out.println("""
                        -----------MENU BAR----------\s
                        ||   1->BalanceCheck       ||
                        ||   2->Deposit            ||
                        ||   3->Withdraw           ||\

                        ||   4->Fund Transfer      ||
                        ||   5->Transaction History||
                        ||   6->QUIT               ||
                        -----------------------------""");
                System.out.println();
                System.out.println("Enter Your Choice:");
                ch=sc.nextInt();
                switch (ch){
                    case 1:
                        System.out.println("Current Balance:"+s1.getBalance()+"\n");
                        break;
                    case 2:
                        s1.deposit();
                        break;
                    case 3:
                        s1.withDraw();
                        break;
                    case 4:
                       s1.transfer();
                        break;
                    case 5:
                        s1.transactionHistory();
                        break;
                    case 6:
                        System.out.println("Thank You for using our ATM");
                        System.exit(0);
                    case 7:
                        System.out.println("Invalid choice\n");
                }
            }
            else {
                getOption();
            }
        }
    }

    static void getOption(){
        Scanner sc=new Scanner(System.in);
        int ch=0;
        while(ch!=1&&ch!=2){
            System.out.println("<------Press------>  \n|| 1--> Login    || \n|| 2--> Register ||\n");
            System.out.println();
            System.out.println("Enter Your Choice:");
            ch=sc.nextInt();
            switch(ch){
                case 1:
                    s1.login();
                    break;
                case 2:
                    s1.register();
                    break;
                default:
                    System.out.println("Invalid Choice\n");
            }
        }
    }

}
