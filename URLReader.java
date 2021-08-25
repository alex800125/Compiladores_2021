// Original: Adapted from the 1997 Java Tutorial by Fred Swartz 
//           to use Scanner classes.  April 2007
// Description: Demo reading web page text lines by printing them.

import java.net.*;
import java.io.*;
import java.util.Scanner;

class URLReader {
    public static void main(String[] args) {
        
        for (int i = 0; i < args.length; i++) {

            System.out.println(" ****************************** ");
            System.out.println("****" + "Entrando no Arquivo " + i + " ****");
            System.out.println(" ****************************** ");
            Lexico lexico = new Lexico();
            try {
                URL url = new URL(args[i]);
                Scanner in = new Scanner(url.openStream());

                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    // System.out.println(line); // Just print it.
                    
                    lexico.analisadorLexical(line);
                }
                in.close();

            } catch (MalformedURLException me) {
                System.err.println(me);

            } catch (IOException ioe) {
                System.err.println(ioe);
            }
            System.out.println(" ****************************** ");
            System.out.println("****" + "Saindo do Arquivo " + i + "*****");
            System.out.println(" ****************************** ");
        }
    }
}
