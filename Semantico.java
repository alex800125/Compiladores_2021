import Exception.SemanticoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Semantico {

    TabelaSimbolos tabelaSimbolos;

    // possui a lista de tokens de um função (Tokens: Se, Senão, Então)
    private ArrayList<Token> listaTokenFuncao = new ArrayList<Token>();
    private int linhaSemRetorno;
    private boolean error;
    private int linha;

    // cria uma instancia da tabela de simbolos
    public Semantico() {
        tabelaSimbolos = new TabelaSimbolos();
    }

    /**
     * Função responsável por inserir o simbolo na pilha de simbolos.
     * Chama uma função na tabela de simbolos.
     * É chamado em 4 pontos do Sintatico: inicio do programa, declaração de função,
     * declaração de procedimento e declaração de variavel.
     */
    public void insereTabela(String lexema, String tipo, int rotulo, int posicao) {
        tabelaSimbolos.inserirPilhaSimbolos(lexema, tipo, rotulo, posicao);
    }

    /**
     * Atribui a uma função o tipo definido na declaração.
     */
    public void inserirTipoFuncao(String tipo) {
        tabelaSimbolos.inserirTipoFuncao(tipo);
    }

    /**
     * Atribui a uma variavel o tipo definido na declaração.
     */
    public void inserirTipoVariavel(String tipo) {
        tabelaSimbolos.inserirTipoVariavel(tipo);
    }

    public void procuraVariavel(Token token) throws SemanticoException {
        if (!(tabelaSimbolos.procuraVariavel(token.getLexema()))) {
            throw new SemanticoException(
                    "Erro linha: " + token.getLinha() + "\nA variável" + token.getLexema() + "não foi definida");
        }
    }

    /**
     * verifica se uma variavel já foi declarada.
     * 
     * @param token recebe o token e verifica o lexema a ser procurado.
     * @throws SemanticoException gera uma exceção semantica caso uma variavel igual
     *                            seja encontrada ou seja o mesmo nome do programa.
     */
    public void procuraVariavelIgual(Token token) throws SemanticoException {
        if (tabelaSimbolos.procuraVariavelIgual(token)) {
            throw new SemanticoException("Erro linha: " + token.getLinha() + " Variavel já existe.");
        }
    }

    /**
     * Procura variavel e função com o mesmo nome.
     */
    public boolean procuraVariavelFuncao(Token token) throws SemanticoException {
        if (!(tabelaSimbolos.procuraVariavel(token.getLexema()) || tabelaSimbolos.procuraFuncao(token.getLexema()))) {
            throw new SemanticoException("Erro linha: " + token.getLinha() + "\nA variável ou função '"
                    + token.getLexema() + "' não foi definida");
        } else {
            return !tabelaSimbolos.procuraVariavel(token.getLexema());
        }
    }

    /**
     * Procura procedimento com o mesmo nome.
     */
    public void procuraProcedimentoMesmoNome(Token token) throws SemanticoException {
        if (tabelaSimbolos.procuraProcedimento(token.getLexema())) {
            throw new SemanticoException(
                    "Erro linha: " + token.getLinha() + "\nJá existe um procedimento com o mesmo nome");
        }
    }

    /**
     * Procura função com o mesmo nome.
     */
    public void procuraFuncaoMesmoNome(Token token) throws SemanticoException {
        if (tabelaSimbolos.procuraFuncao(token.getLexema())) {
            throw new SemanticoException("Erro linha: " + token.getLinha() + "\nJá existe função com o mesmo nome");
        }
    }

    /**
     * Procura função com o mesmo nome.
     */
    public void procurarFuncao(Token token) throws SemanticoException {
        if (!(tabelaSimbolos.procuraFuncao(token.getLexema()))) {
            throw new SemanticoException(
                    "Erro linha: " + token.getLinha() + "\nFunçao" + token.getLexema() + "não declarada");
        }
    }

    /**
     * Procura procedimento com o mesmo nome.
     */
    public void procurarProcedimento(Token token) throws SemanticoException {
        if (!(tabelaSimbolos.procuraProcedimento(token.getLexema()))) {
            throw new SemanticoException(
                    "Erro linha: " + token.getLinha() + "\nProcedimento" + token.getLexema() + "não declarado");
        }
    }

    // procura posição da variavel
    public String posicaoVariavel(String var) {
        int pos = tabelaSimbolos.procurarPosicaoVariavel(var);
        return Integer.toString(pos);
    }

    /**
     * Retorna o rotulo de um procedimento que foi atribuido durante sua criação.
     */
    public int procuraRotuloProcedimento(Token token) throws SemanticoException {
        int rotres = tabelaSimbolos.procurarRotuloProcedimento(token.getLexema());

        if (rotres == -1) {
            throw new SemanticoException(
                    "Erro linha: " + token.getLinha() + "\nProcedimento " + token.getLexema() + "não declarado");
        } else {
            return rotres;
        }
    }

    /**
     * Retorna o rotulo de uma função que foi atribuido durante sua criação.
     */
    public int procurarRotuloFuncao(Token token) throws SemanticoException {
        int rotres = tabelaSimbolos.procurarRotuloFuncao(token.getLexema());
        if (rotres == -1) {
            throw new SemanticoException(
                    "Erro linha: " + token.getLinha() + "\nFunçao" + token.getLexema() + "não declarada");
        } else {
            return rotres;
        }
    }

    // procura por simbolo especifico e retorna seu indice.
    public int procurarLexema(Token token) throws SemanticoException {
        int resultado = tabelaSimbolos.procurarLexema(token.getLexema());
        if (resultado >= 0) {
            return resultado;
        }
        throw new SemanticoException(
                "Não foi encontrado um simbolo com esse nome: '" + token.getLexema() + "' Linha: " + linha);
    }

    /**
     * Essa função transforma uma expressão infixa para uma expressão pos-fixa
     * Para Identificadores(caracteres), Numeros e Verdadeiro ou falso:
     * São escritos na String Saida da esquerda para a Direita, ou seja na mesma
     * sequencia de entrada
     * 
     * Para os operadores usamos o recurso de Pilha
     * A inserção operadores na Pilha se da:
     * O último operador a entrar é o primeiro a sair (política LIFO)
     * Quando um operador de maior Prioridade é lido, este ocupa o topo da pilha
     * O topo da pilha permanece na pilha até aparecer na entrada um operador de
     * prioridade menor ou igual.
     * Assim este operador (menos prioritario) deve ser escrito na String Saida
     * 
     * @param exp Recebe uma expressão
     * @return retorna a String saida para o Sintatico
     */
    public String expressaoParaPosFixa(List<Token> exp) {
        List<String> pilha = new ArrayList<String>();
        String saida = "";

        for (int i = 0; i < exp.size(); i++) {
            String aux = exp.get(i).getLexema();
            this.linha = exp.get(i).getLinha();

            if (Constantes.NUMERO_SIMBOLO.equals(exp.get(i).getSimbolo())
                    || Constantes.IDENTIFICADOR_SIMBOLO.equals(exp.get(i).getSimbolo())
                    || Constantes.VERDADEIRO_SIMBOLO.equals(exp.get(i).getSimbolo())
                    || Constantes.FALSO_SIMBOLO.equals(exp.get(i).getSimbolo())) {
                saida = saida.concat(aux + " ");
            } else if (Constantes.ABRE_PARENTESES_SIMBOLO.equals(exp.get(i).getSimbolo())) {
                pilha.add(aux);
            } else if (Constantes.FECHA_PARENTESES_SIMBOLO.equals(exp.get(i).getSimbolo())) {
                int top = pilha.size() - 1;
                while (!(Constantes.ABRE_PARENTESES_LEXEMA.equals(pilha.get(top)))) {
                    saida = saida.concat(pilha.get(top) + " ");
                    pilha.remove(top);
                    top--;
                }
                pilha.remove(top);
            } else {
                if (pilha.isEmpty()) {
                    pilha.add(aux);
                } else {
                    int operador, operadorpilha;
                    int toppilha = pilha.size() - 1;
                    do {
                        operador = definePrioridaOperador(aux);
                        operadorpilha = definePrioridaOperador(pilha.get(toppilha));
                        if (operadorpilha >= operador) {
                            saida = saida.concat(pilha.get(toppilha) + " ");
                            pilha.remove(toppilha);
                            toppilha--;
                        }
                    } while (operadorpilha >= operador && !(pilha.isEmpty()));

                    if (operadorpilha < operador || pilha.isEmpty()) {
                        pilha.add(aux);
                    }
                }
            }
        }

        int toppilha = pilha.size() - 1;
        if (!pilha.isEmpty()) {
            for (int i = toppilha; i >= 0; i--) {
                saida = saida.concat(pilha.get(i) + " ");
                pilha.remove(i);
            }
        }

        return saida;
    }

    /**
     * Esta Função tem como objetivo retornar a prioridade de um operador
     * Acontece uma comparação onde cada operador é transformado em numero inteiro
     * Quanto maior seu numero mair sua prioridade
     * 
     * @param operador Operador enviado atraves da Função Pos-fixa
     * @return Retorna um valor inteiro, sua prioridade
     */
    private int definePrioridaOperador(String operador) {
        if (null != operador)
            switch (operador) {
                case Constantes.MAIS_UNARIO:
                case Constantes.MENOS_UNARIO:
                case Constantes.NAO_LEXEMA:
                    return 5;
                case Constantes.MULT_LEXEMA:
                case Constantes.DIV_LEXEMA:
                    return 4;
                case Constantes.MAIS_LEXEMA:
                case Constantes.MENOS_LEXEMA:
                    return 3;
                case Constantes.MAIOR_LEXEMA:
                case Constantes.MENOR_LEXEMA:
                case Constantes.MAIOR_IGUAL_LEXEMA:
                case Constantes.MENOR_IGUAL_LEXEMA:
                case Constantes.IGUAL_LEXEMA:
                case Constantes.DIFERENTE_LEXEMA:
                    return 2;
                case Constantes.E_LEXEMA:
                    return 1;
                case Constantes.OU_LEXEMA:
                    return 0;
                default:
                    break;
            }
        return -1;
    }

    /**
     * Essa função serve para verifica o tipo de uma expressão
     *
     * @param exp O sintatico manda uma expressão em notação pos-fixa
     * @return Retorna 0 para Inteiro / Retorna 1 para Booleano
     * @throws SemanticoException
     */
    public String retornaTipoExpressao(String exp) throws SemanticoException {
        String tipo = separaPosFixaExp(exp);

        if (tipo == "0") {
            return Constantes.INTEIRO_LEXEMA;
        } else {
            return Constantes.BOOLEANO_LEXEMA;
        }
    }

    /**
     * Essa função tem como objetivo verificar se a toda expressão pertence ao mesmo
     * tipo
     * No primeiro for:
     * Fazemos a verificacao dos Identificadores(caracteres), Numeros e Verdadeiro
     * ou falso
     * Escrevemos 0 para Identificadores (do tipo Inteiro) e numeros
     * Escrevemos 1 para Identificadores (do tipo booleano) e verdadeiro ou falso
     * 
     * No Segundo for:
     * No primeiro if:
     * Fazemos a verificacao dos operadores e dos tipos setados no primeiro for
     * Como a expressão veio em notação pos-fixa sabemos que ao achar um operador
     * (lendo da esquerda para a direita)
     * o anterior a ele (j - 1) sera um tipo e tambem que o (j - 2) sera outro tipo
     * Assim sabemos que a cada tres posições do explist teremos um retorno de seu
     * tipo
     * No segundo if:
     * serve para verificamos operadores Unario
     * Assim sabemos que a cada 2 posições do explist teremos um retorno de seu tipo
     * 
     * Desse modo escrevemos, no explist, esse tipo para analisar com o restante da
     * Expressão
     * no final explit tera somete 1 posição
     * 
     * @param exp Tem como Parametro uma expressão em notação pos-fixa
     * @return Retornamos a unica posição de explist que contem o tipo da expressão
     * @throws SemanticoException
     */
    private String separaPosFixaExp(String exp) throws SemanticoException {
        String[] aux = exp.split(" ");
        List<String> explist = new ArrayList<String>(Arrays.asList(aux));

        for (int i = 0; i < explist.size(); i++) {
            String parc = explist.get(i);
            if (!(ehOperador(parc)) && !(ehOperadorUnario(parc))) {
                if (Constantes.INTEIRO_LEXEMA.equals(tabelaSimbolos.procurarTipoVariavelFuncao(parc))) {
                    explist.set(i, "0");
                } else if (Constantes.BOOLEANO_LEXEMA.equals(tabelaSimbolos.procurarTipoVariavelFuncao(parc))) {
                    explist.set(i, "1");
                } else if (Constantes.VERDADEIRO_LEXEMA.equals(parc) || Constantes.FALSO_LEXEMA.equals(parc)) {
                    explist.set(i, "1");
                } else {
                    explist.set(i, "0");
                }
            }
        }

        for (int j = 0; j < explist.size(); j++) {
            if (ehOperador(explist.get(j))) {
                String operacao = retornaTipoOperacao(explist.get(j - 2), explist.get(j - 1), explist.get(j));
                explist.remove(j);
                explist.remove(j - 1);
                explist.remove(j - 2);
                explist.add(j - 2, operacao);
                j = 0;
            } else if (ehOperadorUnario(explist.get(j))) {
                String operacao = retornaTipoOperacao(explist.get(j - 1), null, explist.get(j));

                explist.remove(j);
                explist.remove(j - 1);
                explist.add(j - 1, operacao);
                j = 0;
            }
        }
        return explist.get(0);
    }

    /**
     * É uma função que auxilia a geração de codigo
     * Ela é importante para saber qual a posição de variaveis e qual o rotulo das
     * funções
     * Para isso ela consulta a tabela de Simbolo
     * Para Var:
     * Ela escreve, em nova novoexp, a letra 'p' e sua posição
     * O p serve para escrever LDV Posição
     * Para Funçôes:
     * Ela escreve, em nova novoexp, a letra 'funcao', sua posição e 'p0'
     * O p0 serve para escrevermos LDV 0 depois de um Call L Rotulo
     * 
     * @param exp Tem como Parametro uma expressão em notação pos-fixa
     * @return retorna a String novoexp para o Sintatico
     * @throws SemanticoException
     */
    public String formataExpressao(String exp) throws SemanticoException {
        String[] aux = exp.split(" ");
        String novoexp = "";
        int auxposicao;

        for (int i = 0; i < aux.length; i++) {
            if (!tabelaSimbolos.procuraFuncao(aux[i])) { // Passar string para token e chamar a funçao de procurar
                auxposicao = tabelaSimbolos.procurarPosicaoVariavel(aux[i]);
                if (auxposicao != -1) {
                    novoexp = novoexp.concat("p" + auxposicao + " ");
                } else {
                    novoexp = novoexp.concat(aux[i] + " ");
                }
            } else {
                int rotres = tabelaSimbolos.procurarRotuloFuncao(aux[i]);
                novoexp = novoexp.concat("funcao" + rotres + " p0 ");
            }
        }
        return novoexp;
    }

    // verifica se é algum tipo de operador
    private boolean ehOperador(String simbolo) {
        if (Constantes.MULT_LEXEMA.equals(simbolo) || Constantes.DIV_LEXEMA.equals(simbolo)
                || Constantes.MAIS_LEXEMA.equals(simbolo) || Constantes.MENOS_LEXEMA.equals(simbolo)
                || Constantes.MAIOR_LEXEMA.equals(simbolo) || Constantes.MENOR_LEXEMA.equals(simbolo)
                || Constantes.MAIOR_IGUAL_LEXEMA.equals(simbolo) || Constantes.MENOR_IGUAL_LEXEMA.equals(simbolo)
                || Constantes.IGUAL_LEXEMA.equals(simbolo) || Constantes.DIFERENTE_LEXEMA.equals(simbolo)
                || Constantes.E_LEXEMA.equals(simbolo) || Constantes.OU_LEXEMA.equals(simbolo)) {
            return true;
        }
        return false;
    }

    // verifica se é operador unário e operador not
    private boolean ehOperadorUnario(String simbolo) {
        if (Constantes.MAIS_UNARIO.equals(simbolo) || Constantes.MENOS_UNARIO.equals(simbolo)
                || Constantes.NAO_LEXEMA.equals(simbolo)) {
            return true;
        }
        return false;
    }

    /**
     * Verifica e retorna o formato que a operação deve ter de acordo com os
     * operadores e o operador:
     * 0 = inteiro
     * 1 = booleano
     * Um operador matematico deve apenas se envolver com variaveis do tipo inteiro
     * e operadores lógicos devem apenas se envolver com variaveis booleanas.
     * 
     * @param tipo1    primeiro operador
     * @param tipo2    segundo operador
     * @param operador operador
     * @return retorna o resultado de acordo com a operação
     * @throws SemanticoException em caso de alguma operação não tiver o operador
     *                            correto, uma exceção é gerada.
     */
    private String retornaTipoOperacao(String tipo1, String tipo2, String operador) throws SemanticoException {
        if (ehOperador(operador)) {
            if (ehOperadorMatematico(operador)) {
                // + | - | * | div
                if (tipo1 == "0" && tipo2 == "0") {
                    return "0";
                }
                throw new SemanticoException(
                        "Operações aritméticas (+ | - | * | div) devem ter duas variáveis inteiras.Linha: " + linha);
            } else if (ehOperadorRelacional(operador)) {
                // != | = | < | <= | > | >=
                if (tipo1 == "0" && tipo2 == "0") {
                    return "1";
                }
                throw new SemanticoException(
                        "Operações relacionais(!= | = | < | <= | > | >=) devem ter duas variáveis inteiras.Linha: "
                                + linha);
            } else {
                // e | ou
                if (tipo1 == "1" && tipo2 == "1") {
                    return "1";
                }
                throw new SemanticoException(
                        "Operações lógicas (e | ou) devem envolver duas variáveis booleanas.Linha: " + linha);
            }
        } else {
            if (ehOperadorUnarioMat(operador)) {
                // +u | -u
                if (tipo1 == "0") {
                    return "0";
                }
                throw new SemanticoException(
                        "Operações com unários (+ | -) devem ser com variáveis inteiras.Linha: " + linha);
            } else {
                // tipo1 tem que ser um booleano
                if (tipo1 == "1") {
                    return "1";
                }
                throw new SemanticoException("Apenas um numero não pode ser uma operação.Linha: " + linha);
            }
        }
    }

    // verifica se é operador unário
    private boolean ehOperadorUnarioMat(String simbolo) {
        if (Constantes.MAIS_UNARIO.equals(simbolo) || Constantes.MENOS_UNARIO.equals(simbolo)) {
            return true;
        }
        return false;
    }

    // verifica se é operador aritmetico
    private boolean ehOperadorMatematico(String simbolo) {
        if (Constantes.MULT_LEXEMA.equals(simbolo) || Constantes.DIV_LEXEMA.equals(simbolo)
                || Constantes.MAIS_LEXEMA.equals(simbolo) || Constantes.MENOS_LEXEMA.equals(simbolo)) {
            return true;
        }
        return false;
    }

    // verifica se a função é valida
    public boolean ehFuncaoValida(int indice) {
        return tabelaSimbolos.ehFuncaoValida(indice);

    }

    // verifica se é operador relacional
    private boolean ehOperadorRelacional(String simbolo) {
        if (Constantes.MAIOR_LEXEMA.equals(simbolo) || Constantes.MENOR_LEXEMA.equals(simbolo)
                || Constantes.MAIOR_IGUAL_LEXEMA.equals(simbolo) || Constantes.MENOR_IGUAL_LEXEMA.equals(simbolo)
                || Constantes.IGUAL_LEXEMA.equals(simbolo) || Constantes.DIFERENTE_LEXEMA.equals(simbolo)) {
            return true;
        }
        return false;
    }

    /**
     * Retorna o lexema no indice passado.
     */
    public String getLexemaSimbolo(int indice) {
        return tabelaSimbolos.getSimboloLex(indice);
    }

    /**
     * Quando uma função, procedimento ou o programa chega ao fim, é removido as
     * variaveis alocadas dentro desse trecho de código.
     */
    public void limpaNivelTabela() {
        tabelaSimbolos.limparNivel();
    }

    // funcao auto explicativa
    public void insereTokenFuncaoLista(Token token) {
        listaTokenFuncao.add(token);
    }

    public void limpaListaFuncao() {
        listaTokenFuncao.clear();
    }

    /**
     * verifica quem está utilizando a expressão.
     * No caso de ter sido chamado por um comando SE ou ENQUANTO, devemos
     * obrigatoriamente ter um retorno do tipo booleano.
     * Caso tenha sido chamado em uma atribuição, devemos verificar o tipo da
     * variavel (inteiro ou booleano) e ver se é compativel com a resposta que está
     * sendo recebido.
     * 
     * @param tipo   recebe o tipo do retorno.
     * @param chamou recebe quem está recebendo a expressão (SE, ENQUANTO ou
     *               variavel).
     * @throws SemanticoException gera uma exceção caso quem chamou não aceite o
     *                            tipo retornado pela expressão.
     */
    public void quemChamo(String tipo, String chamou) throws SemanticoException {
        if (Constantes.SE_LEXEMA.equals(chamou) || Constantes.ENQUANTO_LEXEMA.equals(chamou)) {
            if (!(Constantes.BOOLEANO_LEXEMA.equals(tipo))) {
                throw new SemanticoException("Erro.\nA condição no '" + chamou + "' não é booleana");
            }
        } else {
            String tipochamou = tabelaSimbolos.procurarTipoVariavelFuncao(chamou);

            if (!(tipo.equals(tipochamou))) {
                throw new SemanticoException(
                        "A expressão do tipo '" + tipo + "' é incompatível com a variável/função do tipo '" + tipochamou
                                + "', por isso não é possível fazer a atribuição");
            }
        }
    }

    /**
     * Durante a execução da função 'analisaSe()' do sintatico, nós vamos inserindo
     * na lista 'listaTokenFuncao' os itens (SE, ENTAO, SENAO), após chegarmos ao
     * final, nós chamamos a função 'verificarListaFuncao()' para validarmos se esse
     * IF esta funcionando corretamente e por fim, retiramos ele da lista.
     * Se tudo ocorrer certo, nós retiramos a inserções feitas.
     * 
     * @param label passamos a label da atual função.
     */
    public void verificarListaFuncao(String label) {
        Token auxToken = null;

        boolean condicionalRetornoEntao = false;
        boolean conditionalRetornoSenao = false;
        int posicaoEntao = -1;
        int posicaoSenao = -1;

        for (int i = 0; i < listaTokenFuncao.size(); i++) {
            if (Constantes.SE_SIMBOLO.equals(listaTokenFuncao.get(i).getSimbolo())
                    && listaTokenFuncao.get(i).getLexema().contains(label)) {
                listaTokenFuncao.remove(i);
                i--;
            } else if (Constantes.ENTAO_SIMBOLO.equals(listaTokenFuncao.get(i).getSimbolo())
                    && listaTokenFuncao.get(i).getLexema().contains(label)) {
                if (listaTokenFuncao.size() > (i + 1)) {
                    if (Constantes.IDENTIFICADOR_SIMBOLO.equals(listaTokenFuncao.get(i + 1).getSimbolo())) {
                        condicionalRetornoEntao = true;
                        auxToken = listaTokenFuncao.get(i + 1);
                    }
                } else {
                    linhaSemRetorno = listaTokenFuncao.get(i).getLinha();
                }
                posicaoEntao = i;
            } else if (Constantes.SENAO_SIMBOLO.equals(listaTokenFuncao.get(i).getSimbolo())
                    && listaTokenFuncao.get(i).getLexema().contains(label)) {
                if (listaTokenFuncao.size() > (i + 1)) {
                    if (Constantes.IDENTIFICADOR_SIMBOLO.equals(listaTokenFuncao.get(i + 1).getSimbolo())) {
                        conditionalRetornoSenao = true;
                        posicaoSenao = i + 1;
                        auxToken = listaTokenFuncao.get(i + 1);
                    }
                } else {
                    linhaSemRetorno = listaTokenFuncao.get(i).getLinha();
                    posicaoSenao = i;
                }
            }
        }

        if (posicaoSenao == (-1))
            posicaoSenao = listaTokenFuncao.size() - 1;

        removeIf(posicaoSenao, posicaoEntao, (condicionalRetornoEntao && conditionalRetornoSenao), auxToken);
    }

    /**
     * Chamamos essa função ao final da 'verificarListaFuncao()' apenas. Aqui nós
     * retiramos os itens (SE, ENTAO, SENAO) inseridos na lista.
     * 
     * @param comeco           começamos pelo senao
     * @param fim              terminamos no entao
     * @param funcaoTemRetorno se caso houver um retorno no meio do entao ou senao
     * @param token            token referente a função atual
     */
    private void removeIf(int comeco, int fim, boolean funcaoTemRetorno, Token token) {
        for (int i = comeco; i >= fim; i--) {
            listaTokenFuncao.remove(i);
        }

        if (funcaoTemRetorno && token != null) {
            listaTokenFuncao.add(token);
        }
    }

    /**
     * Chamada ao fim da execução de uma função, verificamos ao final de tudo se há
     * apenas retornos para função escrita. Se houver mais itens além do retorno de
     * função, teremos uma exceção que algum caminho não tem retorno
     * 
     * @param nomeFuncao nome da atual função
     * @return retorna o resultado, 'true' se tudo estiver certo
     * @throws SemanticoException geramos uma exceção caso não tivermos apenas
     *                            retornos de funções na lista
     */
    public boolean verificarSeFuncaoTemRetorno(String nomeFuncao) throws SemanticoException {
        int aux = 0;

        for (int i = 0; i < listaTokenFuncao.size(); i++) {
            if (nomeFuncao.equals(listaTokenFuncao.get(i).getLexema())) {
                aux++;
                if (aux == listaTokenFuncao.size()) {
                    return true;
                }
            }
        }

        error = true;
        if (linhaSemRetorno != 0)
            linha = linhaSemRetorno;

        throw new SemanticoException("Nem todos os caminhos da função possuem retorno." + "\nLinha: " + linha);
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getLinha() {
        return linha;
    }

    public boolean haErro() {
        return error;
    }
}