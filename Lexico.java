public class Lexico {

    // Variaveis globais
    Character caracter;
    private String codigo;
    private int indice = 0;
    private int linha = 1;
    private String linhaerro = "";

    public Lexico(String cod) {
        System.out.println("Lexico - iniciado");
        codigo = cod;
    }

    public String analisadorLexical() {
        System.out.println("Lexico - analisadorLexical indice = " + indice + " codigo.length() = " + codigo.length());
        char caracter;

        while (indice < codigo.length()) {
            caracter = pegaCaracter();
            System.out.println("Lexico - caracter = " + caracter);
            caracter = charsIgnorados(caracter);
        }
        System.out.println("Lexico - Fim da execução");
        return "Fim da execução";
    }

    public Boolean pegaCaracter(String line) {

        for (int i = 0; i < line.length(); i++) {
            System.out.println("Lexico - pegaCaracter = " + caracter);
            caracter = line.charAt(i);
        }
        return true;
    }

    private char pegaCaracter() {
        char atualChar = codigo.charAt(indice);
        indice++;
        return atualChar;
    }

    private char charsIgnorados(char carac) {
        while (carac == '{' || carac == ' ' || carac == '	' || carac == '\n' && indice < codigo.length()) {
            if (carac == '{') {
                while (carac != '}' && indice < codigo.length() - 1) {
                    indice++;
                    carac = pegaCaracter();
                    if (carac == '\n') {
                        linha++;
                    }
                }
                if (carac == '}') {
                    indice++;
                    carac = pegaCaracter();
                } else {
                    linhaerro = "Erro na linha:" + linha;
                    indice = codigo.length();
                    return carac;
                }
            }
            if (carac == ' ' || carac == '	' && indice < codigo.length() - 1) {
                indice++;
                carac = pegaCaracter();
            }
            if (carac == '\n' && indice < codigo.length() - 1) {
                indice++;
                linha++;
                carac = pegaCaracter();
            }
        }
        return carac;
    }

}