import java.net.*;
import java.io.*;
import java.util.Scanner;

class URLReader {

    Scanner in;
    int i = 0;

    public void startFile(String[] files) {
        System.out.println("URLReader - Abrindo o Arquivo");

        try {
            URL url = new URL(files[i]);
            in = new Scanner(url.openStream());
        } catch (MalformedURLException me) {
            System.err.println(me);
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    public String pegarLinha(){
        String line = "-999";

        if (in.hasNextLine()) {
             line = in.nextLine();
        } else {
            // System.out.println("URLReader - fim do arquivo");
        }

        return line;
    }

    public void fecharArquivo(){
        System.out.println("URLReader - Saindo do Arquivo");
        in.close();
    }
}
