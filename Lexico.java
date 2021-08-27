import java.util.Vector;

public class Lexico {

    // Variaveis globais
    Character caracter;
    private String codigo;
    private int indice = 0;
    private int linha = 1;
    private String linhaerro = "";
    private Vector<Token> Tokens = new Vector<Token>();

    public Lexico(String cod) {
        System.out.println("Lexico - iniciado");
        codigo = cod;
    }

    public String analisadorLexical() {
        System.out.println("Lexico - analisadorLexical indice = " + indice + " codigo.length() = " + codigo.length());
        char caracter;

        while (indice < codigo.length()) {
            caracter = pegaCaracter();
            // System.out.println("Lexico - caracter = " + caracter);
            caracter = charsIgnorados(caracter);

            // Token token = PegaToken(caracter);
            Token token = TrataIdentificadorPalavraReservada(caracter);
            Tokens.add(token);
        }

        printarTokens();
        System.out.println("Lexico - Fim da execução");
        return "Fim da execução";
    }

    private char pegaCaracter() {
        char atualChar = ' ';
        if (codigo.length() > indice) {
            atualChar = codigo.charAt(indice);
            indice++;
        }
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

    private Token PegaToken(char carac) {

        if (Character.isDigit(carac)) {
            return TrataDigito(carac);
        } else if (Character.isLetter(carac)) {
            return TrataIdentificadorPalavraReservada(carac);
        } else if (carac == ':') {
            return TrataAtribuicao(carac);
        } else if (carac == '+' || carac == '-' || carac == '*') {
            return TrataOperadorAritmetico(carac);
        } else if (carac == '<' || carac == '>' || carac == '=' || carac == '!') {
            return TrataOperadorRelacional(carac);
        } else if (carac == ';' || carac == ',' || carac == '(' || carac == ')' || carac == '.') {
            return TrataPontuacao(carac);
        } else {
            linhaerro = "Erro linha:" + linha;
            indice = codigo.length();
            return new Token("", "", linha);
        }
    }

    private Token TrataDigito(char carac) {
        return new Token("", "", linha);
    }

    private Token TrataIdentificadorPalavraReservada(char carac) {
        String palavra = "";
        Token token;

        while (Character.isDigit(carac) || Character.isLetter(carac)) {
            System.out.println("Lexico - WHILE palavra = " + palavra);
            palavra = palavra + Character.toString(carac);
            carac = pegaCaracter();
        }
        System.out.println("Lexico - TrataIdentificadorPalavraReservada = " + palavra);

        switch (palavra) {
            case Constantes.PROGRAMA_LEXEMA:
                token = new Token(Constantes.PROGRAMA_SIMBOLO, palavra, linha);
                break;
            case Constantes.INICIO_LEXEMA:
                token = new Token(Constantes.INICIO_SIMBOLO, palavra, linha);
                break;
            case Constantes.FIM_LEXEMA:
                token = new Token(Constantes.FIM_SIMBOLO, palavra, linha);
                break;
            case Constantes.PROCEDIMENTO_LEXEMA:
                token = new Token(Constantes.PROCEDIMENTO_SIMBOLO, palavra, linha);
                break;
            case Constantes.FUNCAO_LEXEMA:
                token = new Token(Constantes.FUNCAO_SIMBOLO, palavra, linha);
                break;
            case Constantes.SE_LEXEMA:
                token = new Token(Constantes.SE_SIMBOLO, palavra, linha);
                break;
            case Constantes.ENTAO_LEXEMA:
                token = new Token(Constantes.ENTAO_SIMBOLO, palavra, linha);
                break;
            case Constantes.SENAO_LEXEMA:
                token = new Token(Constantes.SENAO_SIMBOLO, palavra, linha);
                break;
            case Constantes.ENQUANTO_LEXEMA:
                token = new Token(Constantes.ENQUANTO_SIMBOLO, palavra, linha);
                break;
            case Constantes.FACA_LEXEMA:
                token = new Token(Constantes.FACA_SIMBOLO, palavra, linha);
                break;
            case Constantes.ATRIBUICAO_LEXEMA:
                token = new Token(Constantes.ATRIBUICAO_SIMBOLO, palavra, linha);
                break;
            case Constantes.ESCREVA_LEXEMA:
                token = new Token(Constantes.ESCREVA_SIMBOLO, palavra, linha);
                break;
            case Constantes.LEIA_LEXEMA:
                token = new Token(Constantes.LEIA_SIMBOLO, palavra, linha);
                break;
            case Constantes.VAR_LEXEMA:
                token = new Token(Constantes.VAR_SIMBOLO, palavra, linha);
                break;
            case Constantes.INTEIRO_LEXEMA:
                token = new Token(Constantes.INICIO_SIMBOLO, palavra, linha);
                break;
            case Constantes.BOOLEANO_LEXEMA:
                token = new Token(Constantes.BOOLEANO_SIMBOLO, palavra, linha);
                break;
            case Constantes.NUMERO_LEXEMA:
                token = new Token(Constantes.NUMERO_SIMBOLO, palavra, linha);
                break;
            case Constantes.PONTO_LEXEMA:
                token = new Token(Constantes.PONTO_SIMBOLO, palavra, linha);
                break;
            case Constantes.PONTO_VIRGULA_LEXEMA:
                token = new Token(Constantes.PONTO_VIRGULA_SIMBOLO, palavra, linha);
                break;
            case Constantes.VIRGULA_LEXEMA:
                token = new Token(Constantes.VIRGULA_SIMBOLO, palavra, linha);
                break;
            case Constantes.ABRE_PARENTESES_LEXEMA:
                token = new Token(Constantes.ABRE_PARENTESES_SIMBOLO, palavra, linha);
                break;
            case Constantes.FECHA_PARENTESES_LEXEMA:
                token = new Token(Constantes.FECHA_PARENTESES_SIMBOLO, palavra, linha);
                break;
            case Constantes.MAIOR_LEXEMA:
                token = new Token(Constantes.MAIOR_SIMBOLO, palavra, linha);
                break;
            case Constantes.MAIOR_IGUAL_LEXEMA:
                token = new Token(Constantes.MAIOR_IGUAL_SIMBOLO, palavra, linha);
                break;
            case Constantes.IGUAL_LEXEMA:
                token = new Token(Constantes.IGUAL_SIMBOLO, palavra, linha);
                break;
            case Constantes.MENOR_LEXEMA:
                token = new Token(Constantes.MENOR_SIMBOLO, palavra, linha);
                break;
            case Constantes.MENOR_IGUAL_LEXEMA:
                token = new Token(Constantes.MENOR_IGUAL_SIMBOLO, palavra, linha);
                break;
            case Constantes.DIFERENTE_LEXEMA:
                token = new Token(Constantes.DIFERENTE_SIMBOLO, palavra, linha);
                break;
            case Constantes.MAIS_LEXEMA:
                token = new Token(Constantes.MAIS_SIMBOLO, palavra, linha);
                break;
            case Constantes.MENOS_LEXEMA:
                token = new Token(Constantes.MENOS_SIMBOLO, palavra, linha);
                break;
            case Constantes.MULT_LEXEMA:
                token = new Token(Constantes.MULT_SIMBOLO, palavra, linha);
                break;
            case Constantes.DIV_LEXEMA:
                token = new Token(Constantes.DIV_SIMBOLO, palavra, linha);
                break;
            case Constantes.E_LEXEMA:
                token = new Token(Constantes.E_SIMBOLO, palavra, linha);
                break;
            case Constantes.OU_LEXEMA:
                token = new Token(Constantes.OU_SIMBOLO, palavra, linha);
                break;
            case Constantes.NAO_LEXEMA:
                token = new Token(Constantes.NAO_SIMBOLO, palavra, linha);
                break;
            case Constantes.DOIS_PONTOS_LEXEMA:
                token = new Token(Constantes.DOIS_PONTOS_SIMBOLO, palavra, linha);
                break;
            case Constantes.VERDADEIRO_LEXEMA:
                token = new Token(Constantes.VERDADEIRO_SIMBOLO, palavra, linha);
                break;
            default:
                token = new Token(Constantes.IDENTIFICADOR_SIMBOLO, palavra, linha);
                break;
        }
        return token;
    }

    private Token EscolhaIdentificador(String palavra) {
        return new Token("", "", linha);
    }

    private Token TrataAtribuicao(char carac) {
        return new Token("", "", linha);
    }

    private Token TrataOperadorRelacional(char carac) {
        return new Token("", "", linha);
    }

    private Token TrataPontuacao(char carac) {
        return new Token("", "", linha);
    }

    private Token TrataOperadorAritmetico(char carac) {
        return new Token("", "", linha);
    }

    private void printarTokens() {
        int i = 0;
        while (Tokens.size() > i) {
            System.out.println("Lexico - printarTokens Simbolo = " + Tokens.elementAt(i).getSimbolo() + " Lexema = " + Tokens.elementAt(i).getLexema());
            i++;
        }
    }
}