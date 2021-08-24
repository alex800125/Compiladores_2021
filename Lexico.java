package Compiladores_2021;

import java.io.IOException;
import java.util.Scanner;
import java.io.FileReader;

public class Lexico{

    // Variaveis globais
    Scanner arquivo;
    String palavra;

    public Lexico(){
        System.out.println("LEXICO - iniciado");   

        String path = "C:\\Users\\alex8\\Documents\\Compiladores\\Compiladores_2021\\Testes\\teste.txt";

        try {
            arquivo = new Scanner(new FileReader(path));
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }
    }

    public void analisadorLexical(){
        System.out.println("LEXICO - analisadorLexical"); 

        while (pegarPalavra()){
            System.out.println("LEXICO - palavra = " + palavra);

            // implementar a partir daqui
        }

        arquivo.close();
    }

    public boolean pegarPalavra(){
        // System.out.println("LEXICO - pegarPalavra"); 

        boolean resultado = false;

        if (arquivo.hasNext()){
            resultado = true;
            palavra = arquivo.next();
        } 

        return resultado;
    }
}