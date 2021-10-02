import java.util.Vector;

public class Lexico {

    // Variaveis globais
    private String codigo;
    private int indice = 0;
    private int linha = 1;
    private boolean erro = false;
    private String linhaerro = "";
    private Vector<Token> Tokens = new Vector<Token>();

    public Lexico(String cod) {
        codigo = cod;
    }

    public Token getToken() {
        Token token;
        char caracter;
        if (indice < codigo.length()) {
            caracter = pegaCaracter();
            caracter = charsIgnorados(caracter);
            token = PegaToken(caracter);
            return token;
        } else {
            return new Token(Constantes.VAZIO_SIMBOLO, Constantes.VAZIO_LEXEMA, -1);
        }
    }
    
    private char pegaCaracter() {
        return codigo.charAt(indice);
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
                if (carac == '}' && indice < codigo.length() - 1) {
                    indice++;
                    carac = pegaCaracter();
                } else {
                    erro = true;
                    linhaerro = "Erro na linha:" + linha;
                    indice = codigo.length();
                    return ' ';
                }
            }
            if (indice == codigo.length() - 1) {
                return carac;
            }
            if (carac == ' ' && indice < codigo.length() - 1) {
                indice++;
                carac = pegaCaracter();

            }
            if (carac == '	' && indice < codigo.length() - 1) {
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
            erro = true;
            indice = codigo.length();
            if (!(carac == ' ' || carac == '	' || carac == '\n')) {
                linhaerro = "Erro na linha:" + linha;
            }
            return new Token(Constantes.VAZIO_SIMBOLO, Constantes.VAZIO_LEXEMA, linha);
        }
    }

    private Token TrataDigito(char carac) {
        String numero = "";
        numero += carac;

        while (Character.isDigit(carac) && indice < codigo.length() - 1) {
            indice++;
            carac = pegaCaracter();
            if (Character.isDigit(carac)) {
                numero += carac;
            }
        }
        if (!Character.isDigit(carac)) {
            return new Token(Constantes.NUMERO_SIMBOLO, numero, linha);
        } else {
            indice++;
            return new Token(Constantes.NUMERO_SIMBOLO, numero, linha);
        }
    }

    private Token TrataIdentificadorPalavraReservada(char carac) {
        String palavra = "";
        palavra += carac;

        while ((Character.isDigit(carac) || Character.isLetter(carac) || carac == '_')
                && indice < codigo.length() - 1) {
            indice++;
            carac = pegaCaracter();
            if (Character.isDigit(carac) || Character.isLetter(carac) || carac == '_') {
                palavra += carac;
            }
        }
        if (Character.isDigit(carac) || Character.isLetter(carac) || carac == '_') {
            indice++;
        }
        return EscolhaIdentificador(palavra);
    }

    private Token EscolhaIdentificador(String palavra) {
        switch (palavra) {
            case Constantes.PROGRAMA_LEXEMA:
                return new Token(Constantes.PROGRAMA_SIMBOLO, palavra, linha);
            case Constantes.SE_LEXEMA:
                return new Token(Constantes.SE_SIMBOLO, palavra, linha);
            case Constantes.ENTAO_LEXEMA:
                return new Token(Constantes.ENTAO_SIMBOLO, palavra, linha);
            case Constantes.SENAO_LEXEMA:
                return new Token(Constantes.SENAO_SIMBOLO, palavra, linha);
            case Constantes.ENQUANTO_LEXEMA:
                return new Token(Constantes.ENQUANTO_SIMBOLO, palavra, linha);
            case Constantes.FACA_LEXEMA:
                return new Token(Constantes.FACA_SIMBOLO, palavra, linha);
            case Constantes.INICIO_LEXEMA:
                return new Token(Constantes.INICIO_SIMBOLO, palavra, linha);
            case Constantes.FIM_LEXEMA:
                return new Token(Constantes.FIM_SIMBOLO, palavra, linha);
            case Constantes.ESCREVA_LEXEMA:
                return new Token(Constantes.ESCREVA_SIMBOLO, palavra, linha);
            case Constantes.LEIA_LEXEMA:
                return new Token(Constantes.LEIA_SIMBOLO, palavra, linha);
            case Constantes.VAR_LEXEMA:
                return new Token(Constantes.VAR_SIMBOLO, palavra, linha);
            case Constantes.INTEIRO_LEXEMA:
                return new Token(Constantes.INTEIRO_SIMBOLO, palavra, linha);
            case Constantes.BOOLEANO_LEXEMA:
                return new Token(Constantes.BOOLEANO_SIMBOLO, palavra, linha);
            case Constantes.VERDADEIRO_LEXEMA:
                return new Token(Constantes.VERDADEIRO_SIMBOLO, palavra, linha);
            case Constantes.FALSO_LEXEMA:
                return new Token(Constantes.FALSO_SIMBOLO, palavra, linha);
            case Constantes.PROCEDIMENTO_LEXEMA:
                return new Token(Constantes.PROCEDIMENTO_SIMBOLO, palavra, linha);
            case Constantes.FUNCAO_LEXEMA:
                return new Token(Constantes.FUNCAO_SIMBOLO, palavra, linha);
            case Constantes.DIV_LEXEMA:
                return new Token(Constantes.DIV_SIMBOLO, palavra, linha);
            case Constantes.E_LEXEMA:
                return new Token(Constantes.E_SIMBOLO, palavra, linha);
            case Constantes.OU_LEXEMA:
                return new Token(Constantes.OU_SIMBOLO, palavra, linha);
            case Constantes.NAO_LEXEMA:
                return new Token(Constantes.NAO_SIMBOLO, palavra, linha);
            default:
                return new Token(Constantes.IDENTIFICADOR_SIMBOLO, palavra, linha);
        }
    }

    private Token TrataAtribuicao(char carac) {

        if (indice < codigo.length() - 1) {
            indice++;
            carac = pegaCaracter();
            if (carac == '=' && indice < codigo.length()) {
                indice++;
                return new Token(Constantes.ATRIBUICAO_SIMBOLO, Constantes.ATRIBUICAO_LEXEMA, linha);
            }
        } else {
            indice++;
            return new Token(Constantes.DOIS_PONTOS_SIMBOLO, Constantes.DOIS_PONTOS_LEXEMA, linha);
        }
        return new Token(Constantes.DOIS_PONTOS_SIMBOLO, Constantes.DOIS_PONTOS_LEXEMA, linha);
    }

    private Token TrataOperadorRelacional(char carac) {
        String op = "";
        op += carac;

        if (carac == '<') {
            indice++;
            if (indice < codigo.length()) {
                carac = pegaCaracter();
            }
            if (carac == '=') {
                op += carac;
                indice++;
                return new Token(Constantes.MENOR_IGUAL_SIMBOLO, Constantes.MENOR_IGUAL_LEXEMA, linha);
            } else {
                return new Token(Constantes.MENOR_SIMBOLO, Constantes.MENOR_LEXEMA, linha);
            }
        } else if (carac == '>') {
            indice++;
            if (indice < codigo.length()) {
                carac = pegaCaracter();
            }
            if (carac == '=') {
                op += carac;
                indice++;
                return new Token(Constantes.MAIOR_IGUAL_SIMBOLO, Constantes.MAIOR_IGUAL_LEXEMA, linha);
            } else {
                return new Token(Constantes.MAIOR_SIMBOLO, Constantes.MAIOR_LEXEMA, linha);
            }
        } else if (carac == '=') {
            indice++;
            return new Token(Constantes.IGUAL_SIMBOLO, Constantes.IGUAL_LEXEMA, linha);
        } else if (carac == '!') {
            indice++;
            if (indice < codigo.length()) {
                carac = pegaCaracter();
            } else {
                erro = true;
                linhaerro = "Erro na linha:" + linha + " operador: " + op;
                indice = codigo.length();
                return new Token(Constantes.VAZIO_SIMBOLO, Constantes.VAZIO_LEXEMA, linha);
            }
            if (carac == '=') {
                op += carac;
                indice++;
                return new Token(Constantes.DIFERENTE_SIMBOLO, Constantes.DIFERENTE_LEXEMA, linha);
            } else {
                erro = true;
                linhaerro = "Erro na linha:" + linha + " operador: " + op;
                indice = codigo.length();
                return new Token(Constantes.VAZIO_SIMBOLO, Constantes.VAZIO_LEXEMA, linha);
            }
        }
        erro = true;
        linhaerro = "Erro na linha:" + linha + " operador: " + op;
        indice = codigo.length();
        return new Token(Constantes.VAZIO_SIMBOLO, Constantes.VAZIO_LEXEMA, linha);
    }

    private Token TrataPontuacao(char carac) {
        indice++;
        switch (carac) {
            case ';':
                return new Token(Constantes.PONTO_VIRGULA_SIMBOLO, Constantes.PONTO_VIRGULA_LEXEMA, linha);
            case ',':
                return new Token(Constantes.VIRGULA_SIMBOLO, Constantes.VIRGULA_LEXEMA, linha);
            case '(':
                return new Token(Constantes.ABRE_PARENTESES_SIMBOLO, Constantes.ABRE_PARENTESES_LEXEMA, linha);
            case ')':
                return new Token(Constantes.FECHA_PARENTESES_SIMBOLO, Constantes.FECHA_PARENTESES_LEXEMA, linha);
            default:
                return new Token(Constantes.PONTO_SIMBOLO, Constantes.PONTO_LEXEMA, linha);
        }
    }

    private Token TrataOperadorAritmetico(char carac) {
        indice++;
        switch (carac) {
            case '+':
                return new Token(Constantes.MAIS_SIMBOLO, Constantes.MAIS_LEXEMA, linha);
            case '-':
                return new Token(Constantes.MENOS_SIMBOLO, Constantes.MENOS_LEXEMA, linha);
            default:
                return new Token(Constantes.MULT_SIMBOLO, Constantes.MULT_LEXEMA, linha);
        }
    }

    public Vector<Token> PegaVetor() {
        return Tokens;
    }

    public final int getLine() {
        return linha;
    }
}