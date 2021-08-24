//package Compiladores_2021;

import java.io.IOException;
import java.util.Scanner;

public class Lexico{

    // Variaveis globais
    String palavra;

    public Lexico(){    
        System.out.println(" ****************************** ");
        System.out.println("       " + "LEXICO - iniciado" + "       ");
        System.out.println(" ****************************** ");
 
    }

    public void analisadorLexical(String line){
        // System.out.println(" ****************************** ");
        // System.out.println("   " + "LEXICO - analisadorLexica" + "   ");
        // System.out.println(" ****************************** ");

        System.out.println("LEXICO - analisadorLexica ........" + line);
        limparLinha(line);
    }

    public Boolean limparLinha(String line){
        // System.out.println("LEXICO - limparLinhaBoolean"); 
        String[] parts = line.split(" ");
        for (int i = 0; i < parts.length - 1; i++) {
            palavra = parts[i];
            //Logica para limpar a palavra
        }
        return true;
    }
}