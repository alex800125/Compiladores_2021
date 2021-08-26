import jdk.javadoc.internal.doclets.formats.html.SourceToHTMLConverter;

public class Lexico{

    // Variaveis globais
    Character caracter;
    private String codigo;

    public Lexico(String cod){    
        System.out.println("Lexico - iniciado");
        codigo = cod;
    }

    public String Executar(){
        return "Compilado com sucesso";
    }

    public void analisadorLexical(URLReader urlReader){
        System.out.println("Lexico - analisadorLexical");
        String linha= urlReader.pegarLinha();
        
        // colocar em um while, terminar depois
        //while (!linha.equals("-999")){
            pegaCaracter(linha);
        //}

        urlReader.fecharArquivo();
    }

    public Boolean pegaCaracter(String line){
        
        for (int i = 0; i < line.length(); i++) {
            System.out.println("Lexico - pegaCaracter = " + caracter);
            caracter = line.charAt(i);
        }
        return true;
    }


}