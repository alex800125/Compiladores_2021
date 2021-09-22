import java.util.Vector;

public class Sintatico {

    private Vector<Token> Tokens = new Vector<Token>();
    Lexico lexico;
    Token token = new Token("", "", 0);

    public String analisadorSintatico(String areaCodigo) {
        String resultado = "";
        lexico = new Lexico(areaCodigo);

        getToken();
        if (token.getSimbolo().equals(Constantes.PROGRAMA_SIMBOLO)) {
            getToken();
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                getToken();
                if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                    // analisaBloco();
                    if (token.getSimbolo().equals(Constantes.PONTO_SIMBOLO)) {
                        getToken();
                        if (token.getSimbolo().equals(Constantes.FIM_SIMBOLO)) {
                            gerarListaToken();
                            resultado = "Compilação concluida com sucesso";
                        } else {
                            resultado = excecaoSintatico(Constantes.FIM_LEXEMA, token.getLexema(), token.getLinha());
                        }
                    } else {
                        resultado = excecaoSintatico(Constantes.PONTO_LEXEMA, token.getLexema(), token.getLinha());
                    }
                } else {
                    resultado = excecaoSintatico(Constantes.PONTO_VIRGULA_LEXEMA, token.getLexema(), token.getLinha());
                }
            } else {
                resultado = excecaoSintatico(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else {
            resultado = excecaoSintatico(Constantes.PROGRAMA_LEXEMA, token.getLexema(), token.getLinha());
        }

        return resultado;
    }

    // TODO Inserir as demais classes do sintatico

    // Pega o proximo token a ser usado
    private void getToken() {
        token = lexico.getToken();
    }

    // Quando encontrarmos um erro, pra facilitar geramos essa mensagem, mas o certo
    // é gerar um Throw.
    public String excecaoSintatico(String LexemaEsperado, String LexemaEncontrado, int linha) {
        return ("Erro na linha: " + linha + " | Simbolo esperado: '" + LexemaEsperado + "' | Simbolo encontrado: '"
                + LexemaEncontrado + "'");
    }

    // função que percorre toda o codigo e joga os tokens numa lista
    private void gerarListaToken() {
        while (token.getLinha() != -1) {
            getToken();

            if (token.getLinha() != -1) {
                Tokens.add(token);
            }
        }
    }

    public Vector<Token> PegaVetor() {
        return Tokens;
    }
}
