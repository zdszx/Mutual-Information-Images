package cake.main;

import cake.screens.MainScreen;
import static cake.screens.MainScreen.LaunchExe;


public class Cake {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainScreen main = new MainScreen();
        main.setVisible(true);
        main.setResizable(false);
    
    }
}
