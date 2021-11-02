import java.util.Vector;
import Exception.LexicoException;
import Exception.SemanticoException;
import Exception.SintaticoException;

public class Sintatico {

    private Vector<Token> Tokens = new Vector<Token>();
    private String message = "";
    private int linhaerro;
    private Lexico lexico;
    private Semantico semantico;
    private int label = 0; 
    Token token = new Token("", "", 0);

    public Sintatico(String codigo) {
        lexico = new Lexico(codigo);
        semantico = new Semantico();
        try {
            analisadorSintatico();
        } catch (SintaticoException e) {
            linhaerro = token.getLinha();
            setMessage(e.getMessage());
        } catch (LexicoException e) {
            linhaerro = token.getLinha();
            setMessage(e.getMessage());
        } catch (SemanticoException e) {
            linhaerro = token.getLinha();
            setMessage(e.getMessage());
        }
    }

    public void analisadorSintatico() throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        if (token.getSimbolo().equals(Constantes.PROGRAMA_SIMBOLO)) {
            getToken();
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                getToken();
                if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                    analisaBloco();
                    if (token.getSimbolo().equals(Constantes.PONTO_SIMBOLO)) {
                        getToken();
                        if (token.getSimbolo().equals(Constantes.VAZIO_SIMBOLO)) {
                            message = "Compilação concluida com sucesso";
                        } else {
                            message = "Trecho de código inesperado após final do programa na linha: "
                                    + lexico.getLine();
                        }
                    } else {
                        throw new SintaticoException(Constantes.PONTO_LEXEMA, token.getLexema(), token.getLinha());
                    }
                } else {
                    throw new SintaticoException(Constantes.PONTO_VIRGULA_LEXEMA, token.getLexema(), token.getLinha());
                }
            } else {
                throw new SintaticoException(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else {
            throw new SintaticoException(Constantes.PROGRAMA_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    public void analisaBloco() throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        analisaEtVariaveis();
        analisaSubrotinas();
        analisaComandos();
    }

    private void analisaEtVariaveis() throws SintaticoException, LexicoException, SemanticoException {
        if (token.getSimbolo().equals(Constantes.VAR_SIMBOLO)) {
            getToken();
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                while (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                    analisaVariaveis();
                    if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                        getToken();
                    } else {
                        throw new SintaticoException(Constantes.PONTO_VIRGULA_LEXEMA, token.getLexema(),
                                token.getLinha());
                    }
                }
            } else {
                throw new SintaticoException(Constantes.VAR_LEXEMA, token.getLexema(), token.getLinha());
            }
        }
    }

    private void analisaVariaveis() throws SintaticoException, LexicoException, SemanticoException {
        do {
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                semantico.procuraVariavelIgual(token);
                semantico.insereTabela(token.getLexema(), Constantes.VAR_LEXEMA, label);
                getToken();
                if (token.getSimbolo().equals(Constantes.VIRGULA_SIMBOLO)
                        || token.getSimbolo().equals(Constantes.DOIS_PONTOS_SIMBOLO)) {
                    if (token.getSimbolo().equals(Constantes.VIRGULA_SIMBOLO)) {
                        getToken();
                        if (token.getSimbolo().equals(Constantes.DOIS_PONTOS_SIMBOLO)) {
                            throw new SintaticoException(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(),
                                    token.getLinha());
                        }
                    }
                } else {
                    throw new SintaticoException("Virgula ou Dois Pontos", token.getLexema(), token.getLinha());
                }
            } else {
                throw new SintaticoException(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
            }
        } while (!(token.getSimbolo().equals(Constantes.DOIS_PONTOS_SIMBOLO)));
        getToken();
        analisaTipo();
    }

    private void analisaSubrotinas() throws SintaticoException, LexicoException, SemanticoException {
        while (token.getSimbolo().equals(Constantes.PROCEDIMENTO_SIMBOLO)
                || token.getSimbolo().equals(Constantes.FUNCAO_SIMBOLO)) {

            if (token.getSimbolo().equals(Constantes.PROCEDIMENTO_SIMBOLO)) {
                analisaDeclaracaoProcedimento();
            } else {
                analisaDeclaracaoFuncao();
            }
            if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                getToken();
            } else {
                throw new SintaticoException(Constantes.PONTO_VIRGULA_LEXEMA, token.getLexema(), token.getLinha());
            }
        }
    }

    private void analisaComandos() throws SintaticoException, LexicoException, SemanticoException {
        if (token.getSimbolo().equals(Constantes.INICIO_SIMBOLO)) {
            getToken();
            analisaComandoSimples();
            while (!(token.getSimbolo().equals(Constantes.FIM_SIMBOLO))) {
                if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                    getToken();
                    if (!(token.getSimbolo().equals(Constantes.FIM_SIMBOLO))) {
                        analisaComandoSimples();
                    }
                } else {
                    throw new SintaticoException(Constantes.PONTO_VIRGULA_LEXEMA, token.getLexema(), token.getLinha());
                }
            }
            getToken();
        } else {
            throw new SintaticoException(Constantes.INICIO_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaComandoSimples() throws SintaticoException, LexicoException, SemanticoException {
        if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
            analisaAtribChprocedimento();
        } else if (token.getSimbolo().equals(Constantes.SE_SIMBOLO)) {
            analisaSe();
        } else if (token.getSimbolo().equals(Constantes.ENQUANTO_SIMBOLO)) {
            analisaEnquanto();
        } else if (token.getSimbolo().equals(Constantes.LEIA_SIMBOLO)) {
            analisaLeia();
        } else if (token.getSimbolo().equals(Constantes.ESCREVA_SIMBOLO)) {
            analisaEscreva();
        } else {
            analisaComandos();
        }
    }

    private void analisaAtribChprocedimento() throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        if (token.getSimbolo().equals(Constantes.ATRIBUICAO_SIMBOLO)) {
            analisaAtribuicao();
        } else {
            chamadaProcedimento();
        }
    }

    private void analisaAtribuicao() throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        analisaExpressao();

    }

    private void analisaExpressao() throws SintaticoException, LexicoException, SemanticoException {
        analisaExpressaoSimples();
        if (token.getSimbolo().equals(Constantes.MAIOR_SIMBOLO)
                || token.getSimbolo().equals(Constantes.MAIOR_IGUAL_SIMBOLO)
                || token.getSimbolo().equals(Constantes.IGUAL_SIMBOLO)
                || token.getSimbolo().equals(Constantes.MENOR_SIMBOLO)
                || token.getSimbolo().equals(Constantes.MENOR_IGUAL_SIMBOLO)
                || token.getSimbolo().equals(Constantes.DIFERENTE_SIMBOLO)) {
            getToken();
            analisaExpressaoSimples();
        }
    }

    private void analisaExpressaoSimples() throws SintaticoException, LexicoException, SemanticoException {
        if (token.getSimbolo().equals(Constantes.MAIS_SIMBOLO) || token.getSimbolo().equals(Constantes.MENOS_SIMBOLO)) {
            getToken();
        }
        analisaTermo();
        while (token.getSimbolo().equals(Constantes.MAIS_SIMBOLO) || token.getSimbolo().equals(Constantes.MENOS_SIMBOLO)
                || token.getSimbolo().equals(Constantes.OU_SIMBOLO)) {
            getToken();
            analisaTermo();
        }
    }

    private void analisaTermo() throws SintaticoException, LexicoException, SemanticoException {
        analisaFator();
        while (token.getSimbolo().equals(Constantes.MULT_SIMBOLO) || token.getSimbolo().equals(Constantes.DIV_SIMBOLO)
                || token.getSimbolo().equals(Constantes.E_SIMBOLO)) {
            getToken();
            analisaFator();
        }
    }

    private void analisaFator() throws SintaticoException, LexicoException, SemanticoException {
        if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
            chamadaFuncao();
            getToken();

        } else if (token.getSimbolo().equals(Constantes.NUMERO_SIMBOLO)) {
            getToken();
        } else if (token.getSimbolo().equals(Constantes.NAO_SIMBOLO)) {
            getToken();
            analisaFator();
        } else if (token.getSimbolo().equals(Constantes.ABRE_PARENTESES_SIMBOLO)) {
            getToken();
            analisaExpressao();
            if (token.getSimbolo().equals(Constantes.FECHA_PARENTESES_SIMBOLO)) {
                getToken();
            } else {
                throw new SintaticoException(Constantes.FECHA_PARENTESES_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else if (token.getSimbolo().equals(Constantes.VERDADEIRO_LEXEMA)
                || token.getSimbolo().equals(Constantes.FALSO_SIMBOLO)) {
            getToken();
        } else {
            throw new SintaticoException("Identificador, Número ou Expressão para comparar", token.getLexema(), token.getLinha());
        }
    }

    private void chamadaFuncao() throws SintaticoException, LexicoException, SemanticoException {
    }

    private void chamadaProcedimento() throws SintaticoException, LexicoException, SemanticoException {
    }

    private void analisaLeia() throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        if (token.getSimbolo().equals(Constantes.ABRE_PARENTESES_SIMBOLO)) {
            getToken();
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                getToken();
                if (token.getSimbolo().equals(Constantes.FECHA_PARENTESES_SIMBOLO)) {
                    getToken();
                } else {
                    throw new SintaticoException(Constantes.FECHA_PARENTESES_LEXEMA, token.getLexema(),
                            token.getLinha());
                }
            } else {
                throw new SintaticoException(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else {
            throw new SintaticoException(Constantes.ABRE_PARENTESES_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaEscreva() throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        if (token.getSimbolo().equals(Constantes.ABRE_PARENTESES_SIMBOLO)) {
            getToken();
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                getToken();
                if (token.getSimbolo().equals(Constantes.FECHA_PARENTESES_SIMBOLO)) {
                    getToken();
                } else {
                    throw new SintaticoException(Constantes.FECHA_PARENTESES_LEXEMA, token.getLexema(),
                            token.getLinha());
                }
            } else {
                throw new SintaticoException(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else {
            throw new SintaticoException(Constantes.ABRE_PARENTESES_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaEnquanto() throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        analisaExpressao();
        if (token.getSimbolo().equals(Constantes.FACA_SIMBOLO)) {
            getToken();
            analisaComandoSimples();
        } else {
            throw new SintaticoException(Constantes.FACA_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaSe() throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        analisaExpressao();
        if (token.getSimbolo().equals(Constantes.ENTAO_SIMBOLO)) {
            getToken();
            analisaComandoSimples();
            if (token.getSimbolo().equals(Constantes.SENAO_SIMBOLO)) {
                getToken();
                analisaComandoSimples();
            }
        } else {
            throw new SintaticoException(Constantes.ENTAO_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaDeclaracaoProcedimento() throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
            semantico.procuraFuncaoProcedimentoIgual(token);
            semantico.insereTabela(token.getLexema(), Constantes.PROCEDIMENTO_LEXEMA, label);
            getToken();
            if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                analisaBloco();
            } else {
                throw new SintaticoException(Constantes.PONTO_VIRGULA_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else {
            throw new SintaticoException(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaDeclaracaoFuncao() throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
            semantico.procuraFuncaoProcedimentoIgual(token);
            semantico.insereTabela(token.getLexema(), Constantes.FUNCAO_LEXEMA, label);
            getToken();
            if (token.getSimbolo().equals(Constantes.DOIS_PONTOS_SIMBOLO)) {
                getToken();
                if (token.getSimbolo().equals(Constantes.INTEIRO_SIMBOLO)
                        || token.getSimbolo().equals(Constantes.BOOLEANO_SIMBOLO)) {
                    getToken();
                    if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                        analisaBloco();
                    }
                } else {
                    throw new SintaticoException("Inteiro ou Booleano", token.getLexema(), token.getLinha());
                }
            } else {
                throw new SintaticoException(Constantes.DOIS_PONTOS_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else {
            throw new SintaticoException(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaTipo() throws SintaticoException, LexicoException, SemanticoException {
        if (!(token.getSimbolo().equals(Constantes.INTEIRO_SIMBOLO))
                && !(token.getSimbolo().equals(Constantes.BOOLEANO_SIMBOLO))) {
            throw new SintaticoException("Inteiro ou Booleano", token.getLexema(), token.getLinha());
        }
        getToken();
    }

    // TODO Inserir as demais classes do sintatico

    // Pega o proximo token a ser usado
    private void getToken() {
        token = lexico.getToken();
        //System.out.println(
               // "lexema = " + token.getLexema() + " simbolo = " + token.getSimbolo() + " linha = " + token.getLinha());
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

    public final String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Vector<Token> PegaVetor() {
        return Tokens;
    }
}
