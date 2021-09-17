import javax.swing.JFrame;

public class Main {
    public static void main(String[] arg) throws Exception {
        Interface analisador = new Interface();
        analisador.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        analisador.setSize(1000, 1000);
        analisador.setResizable(false);
        analisador.setVisible(true);
    }

}
