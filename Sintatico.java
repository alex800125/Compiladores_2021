
import java.util.ArrayList;
import java.util.List;
import Exception.LexicoException;
import Exception.SemanticoException;
import Exception.SintaticoException;

public class Sintatico {

    private List<Token> expressao = new ArrayList<Token>();
    private List<Boolean> flagproc = new ArrayList<Boolean>();
    private List<Boolean> flagfunc = new ArrayList<Boolean>();
    private List<Integer> varalloc = new ArrayList<Integer>();
    private List<String> nomefunc = new ArrayList<String>();
    Token token = new Token("", "", 0);
    private Lexico lexico;
    private Semantico semantico;
    private GeradordeCodigo geracod;
    private String message = "";
    private int rotulo = 0;
    private int auxrotulo = 0;
    private int posicao = 0;
    private int contvariavel = 0;
    

    public Sintatico(String codigo) {
        flagproc.add(false);
        flagfunc.add(false);
        lexico = new Lexico(codigo);
        semantico = new Semantico();
        geracod = new GeradordeCodigo();
        try {
            analisadorSintatico();
        } catch (SintaticoException e) {
            token.getLinha();
            setMessage(e.getMessage());
        } catch (LexicoException e) {
            token.getLinha();
            setMessage(e.getMessage());
        } catch (SemanticoException e) {
            token.getLinha();
            setMessage(e.getMessage());
        }
    }

    public void analisadorSintatico() throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        if (token.getSimbolo().equals(Constantes.PROGRAMA_SIMBOLO)) {
            geracod.criaCodigo("START", "", "");
            varalloc.add(1);
            posicao++;
            geracod.criaCodigo("ALLOC", varalloc.get(varalloc.size() - 1));
            getToken();
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                semantico.insereTabela(token.getLexema(), Constantes.PROGRAMA_LEXEMA, -1, -1);
                getToken();
                if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                    analisaBloco();
                    if (token.getSimbolo().equals(Constantes.PONTO_SIMBOLO)) {
                        getToken();
                        if (token.getSimbolo().equals(Constantes.VAZIO_SIMBOLO)) {
                            posicao = posicao - varalloc.get(varalloc.size() - 1);
                            geracod.criaCodigo("DALLOC", -1);
                            varalloc.remove(varalloc.size() - 1);
                            geracod.criaCodigo("HLT", "", "");
                            geracod.criaArquivo();
                            message = "Compilação concluida com sucesso";
                            semantico.limpaNivelTabela();
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

        // verifica se há espaços alocados ainda, se sim, desaloca-os
        // verifica tambem se procedimentos e funçoes em abertos e fecha-os se
        if (varalloc.size() > 0 && (!flagproc.get(flagproc.size() - 1)) && (!flagfunc.get(flagfunc.size() - 1))) {
            if (varalloc.get(varalloc.size() - 1) > 0) {
                posicao = posicao - varalloc.get(varalloc.size() - 1);
                geracod.criaCodigo("DALLOC", -1);
                varalloc.remove(varalloc.size() - 1);
            } else {
                varalloc.remove(varalloc.size() - 1);
            }
        }
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

        // adiciona na lista quantas variaveis foram alocadas
        // gera o codigo do alloc das posições variaveis
        varalloc.add(contvariavel);
        contvariavel = 0;
        if (varalloc.get(varalloc.size() - 1) > 0) {
            geracod.criaCodigo("ALLOC", varalloc.get(varalloc.size() - 1));
        }
    }

    private void analisaVariaveis() throws SintaticoException, LexicoException, SemanticoException {
        do {
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                semantico.procuraVariavelIgual(token);
                semantico.insereTabela(token.getLexema(), Constantes.VAR_LEXEMA, -1, posicao);
                contvariavel++;
                posicao++;
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

    private void analisaTipo() throws SintaticoException, LexicoException, SemanticoException {
        if (!(token.getSimbolo().equals(Constantes.INTEIRO_SIMBOLO))
                && !(token.getSimbolo().equals(Constantes.BOOLEANO_SIMBOLO))) {
            throw new SintaticoException("Inteiro ou Booleano", token.getLexema(), token.getLinha());
        } else {
            semantico.inserirTipoVariavel(token.getLexema());
        }
        getToken();
    }

    private void analisaSubrotinas() throws SintaticoException, LexicoException, SemanticoException {
        int auxrot = 0, flag = 0;

        if (token.getSimbolo() == Constantes.FUNCAO_SIMBOLO || token.getSimbolo() == Constantes.PROCEDIMENTO_SIMBOLO) {
            auxrot = rotulo;
            geracod.criaCodigo("JMP", "L" + rotulo, "");
            rotulo++;
            flag = 1;
        }

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
        if (flag == 1) {
            geracod.criaCodigo("L" + auxrot, "NULL", "");
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
        Token auxtok = token;
        getToken();
        if (token.getSimbolo().equals(Constantes.ATRIBUICAO_SIMBOLO)) {
            semantico.procuraVariavelFuncao(auxtok);
            analisaAtribuicao(auxtok);
        } else {
            chamadaProcedimento(auxtok);
        }
    }

    private void analisaAtribuicao(Token auxtok) throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        analisaExpressao();

        String aux = semantico.expressaoParaPosFixa(expressao);
        String novoexp = semantico.formataExpressao(aux);
        geracod.criaCodigo(novoexp);

        String tipo = semantico.retornaTipoExpressao(aux);
        semantico.quemChamo(tipo, auxtok.getLexema());
        expressao.clear();

        if (flagfunc.get(flagfunc.size() - 1) && (nomefunc.get(nomefunc.size() - 1)).equals(auxtok.getLexema())) {
            semantico.insereTokenFuncaoLista(auxtok);
        }

        if (nomefunc.size() > 0) {
            if (!((nomefunc.get(nomefunc.size() - 1)).equals(auxtok.getLexema()))) {
                geracod.criaCodigo("STR", semantico.posicaoVariavel(auxtok.getLexema()), "");
            }
        } else {
            geracod.criaCodigo("STR", semantico.posicaoVariavel(auxtok.getLexema()), "");
        }
    }

    private void analisaExpressao() throws SintaticoException, LexicoException, SemanticoException {
        analisaExpressaoSimples();
        if (token.getSimbolo().equals(Constantes.MAIOR_SIMBOLO)
                || token.getSimbolo().equals(Constantes.MAIOR_IGUAL_SIMBOLO)
                || token.getSimbolo().equals(Constantes.IGUAL_SIMBOLO)
                || token.getSimbolo().equals(Constantes.MENOR_SIMBOLO)
                || token.getSimbolo().equals(Constantes.MENOR_IGUAL_SIMBOLO)
                || token.getSimbolo().equals(Constantes.DIFERENTE_SIMBOLO)) {
            expressao.add(token);
            getToken();
            analisaExpressaoSimples();
        }
    }

    private void analisaExpressaoSimples() throws SintaticoException, LexicoException, SemanticoException {
        if (token.getSimbolo().equals(Constantes.MAIS_SIMBOLO) || token.getSimbolo().equals(Constantes.MENOS_SIMBOLO)) {
            Token aux = new Token(token.getSimbolo(), token.getLexema() + "u", token.getLinha());
            expressao.add(aux);
            getToken();
        }
        analisaTermo();
        while (token.getSimbolo().equals(Constantes.MAIS_SIMBOLO) || token.getSimbolo().equals(Constantes.MENOS_SIMBOLO)
                || token.getSimbolo().equals(Constantes.OU_SIMBOLO)) {
            expressao.add(token);
            getToken();
            analisaTermo();
        }
    }

    private void analisaTermo() throws SintaticoException, LexicoException, SemanticoException {
        analisaFator();
        while (token.getSimbolo().equals(Constantes.MULT_SIMBOLO) || token.getSimbolo().equals(Constantes.DIV_SIMBOLO)
                || token.getSimbolo().equals(Constantes.E_SIMBOLO)) {
            expressao.add(token);
            getToken();
            analisaFator();
        }
    }

    private void analisaFator() throws SintaticoException, LexicoException, SemanticoException {
        if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
            int indice = semantico.procurarLexema(token);
            if (semantico.ehFuncaoValida(indice)) {
                expressao.add(token);
                chamadaFuncao(indice);
            } else {
                expressao.add(token);
                getToken();
            }
        } else if (token.getSimbolo().equals(Constantes.NUMERO_SIMBOLO)) {
            expressao.add(token);
            getToken();
        } else if (token.getSimbolo().equals(Constantes.NAO_SIMBOLO)) {
            expressao.add(token);
            getToken();
            analisaFator();
        } else if (token.getSimbolo().equals(Constantes.ABRE_PARENTESES_SIMBOLO)) {
            expressao.add(token);
            getToken();
            analisaExpressao();
            if (token.getSimbolo().equals(Constantes.FECHA_PARENTESES_SIMBOLO)) {
                expressao.add(token);
                getToken();
            } else {
                throw new SintaticoException(Constantes.FECHA_PARENTESES_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else if (token.getSimbolo().equals(Constantes.VERDADEIRO_LEXEMA)
                || token.getSimbolo().equals(Constantes.FALSO_SIMBOLO)) {
            expressao.add(token);
            getToken();
        } else {
            throw new SintaticoException("Expressão incompleta", token.getLexema(),
                    token.getLinha());
        }
    }

    private void chamadaFuncao(int indice) throws SintaticoException, LexicoException, SemanticoException {
        String simbololexico = semantico.getLexemaSimbolo(indice);
        semantico.procurarFuncao(new Token("", simbololexico, token.getLinha()));
        getToken();
    }

    private void chamadaProcedimento(Token auxtoken) throws SintaticoException, LexicoException, SemanticoException {
        semantico.procurarProcedimento(auxtoken);
        int rotres = semantico.procuraRotuloProcedimento(auxtoken);
        geracod.criaCodigo("CALL", "L" + rotres, "");
    }

    private void analisaLeia() throws SintaticoException, LexicoException, SemanticoException {
        geracod.criaCodigo("RD", "", "");
        getToken();
        if (token.getSimbolo().equals(Constantes.ABRE_PARENTESES_SIMBOLO)) {
            getToken();
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                semantico.procuraVariavel(token);
                geracod.criaCodigo("STR", semantico.posicaoVariavel(token.getLexema()), "");
                getToken();
                if (token.getSimbolo().equals(Constantes.FECHA_PARENTESES_SIMBOLO)) {
                    getToken();
                } else {
                    throw new SintaticoException(Constantes.FECHA_PARENTESES_LEXEMA, token.getLexema(), token.getLinha());
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
                boolean ehfuncao = semantico.procuraVariavelFuncao(token);
                if (ehfuncao) {
                    int rotuloresult = semantico.procurarRotuloFuncao(token);
                    geracod.criaCodigo("CALL", "L" + rotuloresult, "");
                } else {
                    String posicaovar = semantico.posicaoVariavel(token.getLexema());
                    geracod.criaCodigo("LDV", posicaovar, "");
                }
                getToken();
                if (token.getSimbolo().equals(Constantes.FECHA_PARENTESES_SIMBOLO)) {
                    geracod.criaCodigo("PRN", "", "");
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
        int auxrotulo1, auxrotulo2;
        auxrotulo1 = rotulo;
        geracod.criaCodigo("L" + rotulo, "NULL", "");
        rotulo++;

        getToken();
        analisaExpressao();

        String auxexpressao = semantico.expressaoParaPosFixa(expressao);
        String novoexp = semantico.formataExpressao(auxexpressao);
        geracod.criaCodigo(novoexp);
        String tipoexpressao = semantico.retornaTipoExpressao(auxexpressao);
        semantico.quemChamo(tipoexpressao, Constantes.ENQUANTO_LEXEMA);
        expressao.clear();

        if (token.getSimbolo().equals(Constantes.FACA_SIMBOLO)) {
            auxrotulo2 = rotulo;
            geracod.criaCodigo("JMPF", "L" + rotulo, "");
            rotulo++;
            getToken();
            analisaComandoSimples();
            geracod.criaCodigo("JMP", "L" + auxrotulo1, "");
            geracod.criaCodigo("L" + auxrotulo2, "NULL", "");
        } else {
            throw new SintaticoException(Constantes.FACA_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaSe() throws SintaticoException, LexicoException, SemanticoException {
        int auxrotulo1, auxrotulo2;
        auxrotulo++;

        if (flagfunc.get(flagfunc.size() - 1)) {
            semantico.insereTokenFuncaoLista(
                    new Token(token.getSimbolo(), token.getLexema() + auxrotulo, token.getLinha()));
        }

        getToken();
        analisaExpressao();

        String auxexpressao = semantico.expressaoParaPosFixa(expressao);
        String novoexp = semantico.formataExpressao(auxexpressao);
        geracod.criaCodigo(novoexp);
        String tipoexpressao = semantico.retornaTipoExpressao(auxexpressao);
        semantico.quemChamo(tipoexpressao, Constantes.SE_LEXEMA);
        expressao.clear();

        if (token.getSimbolo().equals(Constantes.ENTAO_SIMBOLO)) {
            auxrotulo1 = rotulo;
            geracod.criaCodigo("JMPF", "L" + rotulo, "");
            rotulo++;

            if (flagfunc.get(flagfunc.size() - 1)) {
                semantico.insereTokenFuncaoLista(
                        new Token(token.getSimbolo(), token.getLexema() + auxrotulo, token.getLinha()));
            }

            getToken();
            analisaComandoSimples();

            if (token.getSimbolo().equals(Constantes.SENAO_SIMBOLO)) {
                auxrotulo2 = rotulo;
                geracod.criaCodigo("JMP", "L" + rotulo, "");
                rotulo++;

                geracod.criaCodigo("L" + auxrotulo1, "NULL", "");

                if (flagfunc.get(flagfunc.size() - 1)) {
                    semantico.insereTokenFuncaoLista(
                            new Token(token.getSimbolo(), token.getLexema() + auxrotulo, token.getLinha()));
                }

                getToken();
                analisaComandoSimples();

                geracod.criaCodigo("L" + auxrotulo2, "NULL", "");
            } else {
                geracod.criaCodigo("L" + auxrotulo1, "NULL", "");
            }
        } else {
            throw new SintaticoException(Constantes.ENTAO_LEXEMA, token.getLexema(), token.getLinha());
        }

        if (flagfunc.get(flagfunc.size() - 1)) {
            semantico.verificarListaFuncao(String.valueOf(auxrotulo));
        }
        auxrotulo--;
    }

    private void analisaDeclaracaoProcedimento() throws SintaticoException, LexicoException, SemanticoException {
        flagproc.add(true);
        getToken();
        if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
            semantico.procuraProcedimentoMesmoNome(token);
            semantico.insereTabela(token.getLexema(), Constantes.PROCEDIMENTO_LEXEMA, rotulo, -1);
            geracod.criaCodigo("L" + rotulo, "NULL", "");
            rotulo++;
            getToken();
            if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                analisaBloco();
            } else {
                throw new SintaticoException(Constantes.PONTO_VIRGULA_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else {
            throw new SintaticoException(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
        }

        semantico.limpaNivelTabela();

        if (varalloc.get(varalloc.size() - 1) > 0) {
            posicao = posicao - varalloc.get(varalloc.size() - 1);
            geracod.criaCodigo("DALLOC", -1);
            varalloc.remove(varalloc.size() - 1);
        } else {
            varalloc.remove(varalloc.size() - 1);
        }

        geracod.criaCodigo("RETURN", "", "");
        flagproc.remove(flagproc.size() - 1);
    }

    private void analisaDeclaracaoFuncao() throws SintaticoException, LexicoException, SemanticoException {
        flagfunc.add(true);
        getToken();
        if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
            semantico.procuraFuncaoMesmoNome(token);
            semantico.insereTabela(token.getLexema(), Constantes.FUNCAO_LEXEMA, rotulo, -1);
            geracod.criaCodigo("L" + rotulo, "NULL", "");
            rotulo++;
            nomefunc.add(token.getLexema());
            semantico.setLinha(token.getLinha());
            getToken();
            if (token.getSimbolo().equals(Constantes.DOIS_PONTOS_SIMBOLO)) {
                getToken();
                if (token.getSimbolo().equals(Constantes.INTEIRO_SIMBOLO)
                        || token.getSimbolo().equals(Constantes.BOOLEANO_SIMBOLO)) {
                    if (token.getSimbolo().equals(Constantes.INTEIRO_SIMBOLO)) {
                        semantico.inserirTipoFuncao(Constantes.INTEIRO_LEXEMA);
                    } else {
                        semantico.inserirTipoFuncao(Constantes.BOOLEANO_LEXEMA);
                    }
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

        semantico.limpaNivelTabela();
        flagfunc.remove(flagfunc.size() - 1);
        semantico.verificarSeFuncaoTemRetorno(nomefunc.get(nomefunc.size() - 1));
        nomefunc.remove(nomefunc.size() - 1);
        geracod.criaCodigo("STR", "0", "");
        if (varalloc.get(varalloc.size() - 1) > 0) {
            posicao = posicao - varalloc.get(varalloc.size() - 1);
            geracod.criaCodigo("DALLOC", -1);
            varalloc.remove(varalloc.size() - 1);
        }
        geracod.criaCodigo("RETURN", "", "");

        semantico.limpaListaFuncao();
    }

    // Pega o proximo token a ser usado
    private void getToken() {
        token = lexico.getToken();
    }


    public final String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}