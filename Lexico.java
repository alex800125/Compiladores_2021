import jdk.javadoc.internal.doclets.formats.html.SourceToHTMLConverter;

public class Lexico{

    // Variaveis globais
    Character caracter;
    private String codigo;
    private int indice = 0;
    private int linha = 1;
    private String linhaerro ="";

    public Lexico(String cod){    
        System.out.println("Lexico - iniciado");
        codigo = cod;
    }

    public String Executar(){
        char caracter;
        
        while(indice<codigo.length())
        {
            caracter = PegaCaracter();
            caracter = CharsIgnorados(caracter);
        }
        
        return "Fim da execução";
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

    private char PegaCaracter() {
        return codigo.charAt(indice);
    }

    private char CharsIgnorados(char carac) {
        while(carac == '{' || carac == ' ' || carac == '	' || carac == '\n' && indice<codigo.length()) {
            if(carac == '{'){
                while(carac != '}' && indice<codigo.length()-1){
                    indice++;
                    carac = PegaCaracter();
                    if(carac =='\n')
                    {
                        linha++;
                    }
                }
                if(carac == '}'){
                indice++;
                carac = PegaCaracter();
                }
                else{
                    linhaerro = "Erro na linha:" + linha;
                    indice=codigo.length();
                    return carac;
                }
            }
            if(carac == ' ' || carac == '	' && indice<codigo.length()-1){
                indice++;
                carac = PegaCaracter();
            }
            if(carac == '\n' && indice<codigo.length()-1){
                indice++;
                linha++;
                carac = PegaCaracter();
            }            
        }
        return carac;
    }


}