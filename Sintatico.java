
import java.util.ArrayList;
import java.util.List;
import Exception.LexicoException;
import Exception.SemanticoException;
import Exception.SintaticoException;

public class Sintatico {

    /**Declaração de todas a variaveis que serão usadas no decorrer do programa */
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

    /**Inicializa as flags de procedimento e função, instancia o léxico, semantico e gerador de código. 
     * Chama a função analisadorsintático() que faz a verificação inteira se o código está certo.*/
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

    /**O analisador sintático é basicamente igual ao passado na apostila, porém depois do simbolo programa
     * criamos o código de start e damos o ALLOC de uma posição  na pilha que será usada para o retorno das
     * funções. Depois do ponto final pedimos mais um token ao léxico, se vier um simbolo de vazio fazemos os
     * tratamentos de criar o arquivo do código, desalocar a posição 1 da pilha e exibimos o que a compilação
     *  foi um sucesso.*/
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

    /**Pega um token, chama as funções de analisa etapa de variaveis, subrotinas e comandos, sucessivamente 
     *nesta ordem, como mostrado na apostila*/
    public void analisaBloco() throws SintaticoException, LexicoException, SemanticoException {
        getToken();
        analisaEtVariaveis();
        analisaSubrotinas();
        analisaComandos();

        /**verifica se há espaços alocados ainda, se sim, desaloca-os
        verifica tambem procedimentos e funçoes em aberto e fecha-os*/
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

    /**Faz a verificação sintática se tudo está certo*/
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

        /**adiciona na lista quantas variaveis foram alocadas e gera o codigo do alloc das posições variaveis*/
        varalloc.add(contvariavel);
        contvariavel = 0;
        if (varalloc.get(varalloc.size() - 1) > 0) {
            geracod.criaCodigo("ALLOC", varalloc.get(varalloc.size() - 1));
        }
    }

    /**Além da verificação sintática, esse procedimento verifica se ao declarar uma variavel não há
     * outra com o nome igual através da chamada de um procedimento do semantico procuraVariavelIgual(),
     *  se não tiver conflito é feita a inserção na tabela de simbolos por outra chamada de procedimento 
     * do semantico insereTabela(), após isto é atualizada o valor da posicao e o contador de variaveis.*/
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

    /**Verifica se é inteiro ou booleano o tipo das variaveis e chama a função do semanco inserirTipoVariavel()
     * que vai na tabela de simbolos inserir o tipo. Caso o simbolo que vier for diferente das duas opções ditas
     * é apresentado uma mensagem de erro.*/
    private void analisaTipo() throws SintaticoException, LexicoException, SemanticoException {
        if (!(token.getSimbolo().equals(Constantes.INTEIRO_SIMBOLO))
                && !(token.getSimbolo().equals(Constantes.BOOLEANO_SIMBOLO))) {
            throw new SintaticoException("Inteiro ou Booleano", token.getLexema(), token.getLinha());
        } else {
            semantico.inserirTipoVariavel(token.getLexema());
        }
        getToken();
    }

    /**Procedimento exatamente igual ao da apostila, onde criamos um rótulo auxiliar para guardar o  valor que 
     * utilizaremos após o while que verifica se há rotinas dentro de rotinas, o auxrot é essencial para a geração 
     * de código. Utilizamos a variavel flag  para gerar o código caso o procedimento entre no 
     * primeiro if. De acordo com o simbolo que verifica se é função ou procedimento é feita a chamada de um
     * procedimento que passa a tratar eles.*/
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

    /**Exatamente igual a apostila, neste procedimento não manipulamos nada e não chamamos nenhuma função do semantico,
     * apenas verificamos se o código obdece a sintática que estabelecemos.*/
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

    /**De acordo com o simbolo lido, vemos que verificação sintática precisamos faze, se é atribuição de procedimento,
     * se, enquantom leia, escreva ou se vai começar outro bloco de comandos.*/
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

    /**Verifica se é uma atribuição ou chamada de procedimento que virá, no caso do primeiro, guardamos o token anterior 
     * no auxtok para saber posteriormente a quem vamos atribuir algo, é feita a chamada de uma funçao do semantico que
     * verificará se o token anterior existe na tabela de simbolos.*/
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

    /**Aqui chamamos o analisaExpressão() e logo após fazemos manipulações chamando o semantico. 
     * Primeiro chamamos a função do semantico que passa a nossa lista de expressão para o formato pós fixa e 
     *guardamos na variavel aux. Em segundo chamamos o formata expressao que retorna para o novoexp um tipo 
     *de expressão com as posiçoes das variaveis e os simbolos para facilitar na geração de código. Em terceiro 
     *geramos o código da expressao. Em quarto chamamos a função que retorna o tipo da expressão, se é inteiro 
     *ou booleano. Em quinto chamamos a função quemChamo() que verifica se o tipo da expressao é o mesmo da variavel
     *que vai recebe-lo. Em sexto damos um clear na lista de expressão já que essa foi gerada.
     *Após tudo isso, incluimos o token de função numa lista que temos no semantico de acordo com uns critérios 
     *das flags que usamos. Por ultimo geramos o código de STR da função. */
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

    /**Chamamos o analisa expressao simples e depois se o simbolo for relacional, adicionamos na lista de expressao,
     * pegamos outro token e chamamos novamente a função. Igual ao que está na apostila. */
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

    /**Basicamente igual da apostila, só muda que dentro do if e do while estamos incluindo os tokens de mais, menos e ou
     * na lista de expressão. Então aqui ficamos num loop que verifica se os tokens são esses sinais e chama o analisaTermo(). */
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
    
    /**Igual a apostila, chama o analisa fator e enquanto os simbolos forem iguais a mult, div e E, ele vai pegando token, adicionando
     * na lista de expressao e chamando o analisaFator. */
    private void analisaTermo() throws SintaticoException, LexicoException, SemanticoException {
        analisaFator();
        while (token.getSimbolo().equals(Constantes.MULT_SIMBOLO) || token.getSimbolo().equals(Constantes.DIV_SIMBOLO)
                || token.getSimbolo().equals(Constantes.E_SIMBOLO)) {
            expressao.add(token);
            getToken();
            analisaFator();
        }
    }

    /**Aqui pega tudo que faz parte da  expressão e adiciona na lista de expressão, no caso de identificador é feita
     * a verificação para ver se existe e é valido. */
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

    /**Pega o lexema do indice que foi passado, verifica se função foi declarada e pega um token novo. */
    private void chamadaFuncao(int indice) throws SintaticoException, LexicoException, SemanticoException {
        String simbololexico = semantico.getLexemaSimbolo(indice);
        semantico.procurarFuncao(new Token("", simbololexico, token.getLinha()));
        getToken();
    }

    /**Verifica se procedimento existe e procura o rotulo dele através de chamadas do semantico, depois disso gera o código
     * CALL que vai para a parte desse procedimento. */
    private void chamadaProcedimento(Token auxtoken) throws SintaticoException, LexicoException, SemanticoException {
        semantico.procurarProcedimento(auxtoken);
        int rotres = semantico.procuraRotuloProcedimento(auxtoken);
        geracod.criaCodigo("CALL", "L" + rotres, "");
    }

    /**Igual da apostila, vai gerar o RD, fazer a verificação sintática, chamara a função que verifica se o identificador existe
     * que é o procuraVarivel() e criar o Código de STR que serve para saber o local que vamos guardar o valor de entrada do teclado,
     * por isso chamamos a funçao que procura a posicao da variavel. */
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

    /**Faz a verificação sintatica, no if do identificador verifica se é funçao ou variaavel através do procuraVariavelFunçao()
     * e guarda no auxiliar ehfuncao para fazer um if. Se for função, então chama o procurarRotuloFuncao() guarda o rotulo e
     * gera o código de CALL; se for variavel então chama o posicaoVariavel() e gera o LDV para carrega-la. Depois do fecha
     * parenteses é gerado o código de print PRN. */
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

    /**Igual da apostila, onde é criado dois auxiliares de rotulo um para fazer o loop e outro escape no código assembly. 
     * Depois de gerar o primeiro label e incrementar o rótulo, chamamos o analisaExpressão() e logo após fazemos manipulações chamando o semantico. 
     * Primeiro chamamos a função do semantico que passa a nossa lista de expressão para o formato pós fixa e 
     *guardamos na variavel aux. Em segundo chamamos o formata expressao que retorna para o novoexp um tipo 
     *de expressão com as posiçoes das variaveis e os simbolos para facilitar na geração de código. Em terceiro 
     *geramos o código da expressao. Em quarto chamamos a função que retorna o tipo da expressão, se é inteiro 
     *ou booleano. Em quinto chamamos a função quemChamo() que verifica se o tipo da expressao é o mesmo da variavel
     *que vai recebe-lo. Em sexto damos um clear na lista de expressão já que essa foi gerada.
     *Dentro do if que compara token com simbolo de FAÇA é feita a manipulação de rótulos e geração de código para 
     *que sejá possivel o loop do enquanto.*/
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

    /**É criado os rotulos auxiliares para permitir o se e senao, depois fazemos a manipulação de uma flag que
     * serve para verificarmos retornos de funçoes e se estamos dentro de uma.
     * Após isto chamamos o analisaExpressão() e logo após fazemos manipulações chamando o semantico. 
     * Primeiro chamamos a função do semantico que passa a nossa lista de expressão para o formato pós fixa e 
     *guardamos na variavel aux. Em segundo chamamos o formata expressao que retorna para o novoexp um tipo 
     *de expressão com as posiçoes das variaveis e os simbolos para facilitar na geração de código. Em terceiro 
     *geramos o código da expressao. Em quarto chamamos a função que retorna o tipo da expressão, se é inteiro 
     *ou booleano. Em quinto chamamos a função quemChamo() que verifica se o tipo da expressao é o mesmo da variavel
     *que vai recebe-lo. Em sexto damos um clear na lista de expressão já que essa foi gerada.
     *Após isto verificamos o ENTAO e geramos os códigos de JMPF dele, depois fazemos novamente manipulação da nossa 
     *flag de função; se existir o SENÃO  geramos o código dele mexemos mais uma vez na flagfunc. */
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

    /**Colocamos true na lista de flag de procedimento para mostrarmos que estamos dentro dela, depois disso verificamos se o identificador
     * pode ser atribuido ao nome do procedimento através da chamada de função procuraProcedimentoMesmoNome(), se sim, então é inserida na tabela 
     * pela função insereTabela() e depois é gerado um label desse procedimento. Depois da incrementação do rótulo e a chamada do analisaBloco() 
     * chamamos o limpaNivelTabela() já que estamos saindo desse procedimento. Finalmente é gerado os DALLOCs referente as variaveis deste escopo,
     * para assim gerar o RETURN e dar um remove na flagproc que estamos saindo. */
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

    /**Colocamos true na lista de flag de função para mostrarmos que estamos dentro dela, depois disso verificamos se o identificador
     * pode ser atribuido ao nome da função através da chamada de função procuraFuncaoMesmoNome(), se sim, então é inserida na tabela 
     * pela função insereTabela() e depois é gerado um label dessa função. Depois de incrementar o rótulo e adicionar o lexema na nossa
     * lista de função, setamos a linha no semantico e fazemos as verifificações léxicas. Ao receber um inteiro ou booleano, chamamos a 
     * função do semantico inserirTipoFuncao() que vai inserir o tipo dessa função na tabela de simbolos. 
     * Depois que o analisa bloco termina limpamos o nivel desse escopo pelo limpaNivelTabela(), removemos ela da flagfunc, verificamos
     * se possui retorno pelo verificarSeFuncaoTemRetorno(), removemos o nome dela do nomefunc(), geramos o STR 0 que é a posição de
     * retorno, fazemos os DALLOCs de variaveis, geramos o RETURN e limpamos a limta de função. */
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

    //**Pega um token do lexico, onde chamamos a função getToken que nos retorna o token gerado */
    private void getToken() {
        token = lexico.getToken();
    }

    /**Getter da variavel message, que guarda a mensagem a ser exibida no console do compilador */
    public final String getMessage() {
        return this.message;
    }

    /**Setter da variavel message, que guarda a mensagem a ser exibida no console do compilador */
    public void setMessage(String message) {
        this.message = message;
    }

}