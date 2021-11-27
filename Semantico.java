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

    // insere um elemento na pilha de simbolos
    public void insereTabela(String lexema, String tipo, int rotulo, int posicao) {
        tabelaSimbolos.inserirPilhaSimbolos(lexema, tipo, rotulo, posicao);
    }

    // insere o tipo da funcao depois de declaradas
    public void inserirTipoFuncao(String tipo) {
        tabelaSimbolos.inserirTipoFuncao(tipo);
    }

    // insere o tipo da variavel depois de declaradas
    public void inserirTipoVariavel(String tipo) {
        tabelaSimbolos.inserirTipoVariavel(tipo);
    }

    public void procuraVariavel(Token token) throws SemanticoException{
        if(!(tabelaSimbolos.procuraVariavel(token.getLexema()))){
            throw new SemanticoException("Erro linha: " + token.getLinha() + "\nA variável" + token.getLexema() + "não foi definida");
        }
    }

    // função que verifica se uma variavel já foi declarada
    public void procuraVariavelIgual(Token token) throws SemanticoException {
        if (tabelaSimbolos.procuraVariavelIgual(token)) {
            throw new SemanticoException("Erro linha: " + token.getLinha() + " Variavel já existe.");
        }
    }

    // verifica se a variavel ou funcao existe
    public boolean procuraVariavelFuncao(Token token) throws SemanticoException {
        if (!(tabelaSimbolos.procuraVariavel(token.getLexema()) || tabelaSimbolos.procuraFuncao(token.getLexema()))) {
            throw new SemanticoException("Erro linha: " + token.getLinha() + "\nA variável ou função '"
                    + token.getLexema() + "' não foi definida");
        } else {
            return !tabelaSimbolos.procuraVariavel(token.getLexema());
        }
    }

    // função que verifica se uma funçao ou procedimento já foi declarado
    public void procuraFuncaoProcedimentoIgual(Token token) throws SemanticoException {
        if (tabelaSimbolos.procuraFuncaoProcedimentoIgual(token)) {
            throw new SemanticoException("Procedimento/Função já existente.");
        } else if (tabelaSimbolos.procuraNomePrograma(token)) {
            throw new SemanticoException("Procedimento/Função com mesmo nome do programa.");
        }
    }

    public void procuraProcedimentoMesmoNome(Token token) throws SemanticoException{
        if(tabelaSimbolos.procuraProcedimento(token.getLexema())){
            throw new SemanticoException("Erro linha: " + token.getLinha() + "\nJá existe um procedimento com o mesmo nome");
        }
    }

    public void procuraFuncaoMesmoNome(Token token) throws SemanticoException{
        if(tabelaSimbolos.procuraFuncao(token.getLexema())){
            throw new SemanticoException("Erro linha: " + token.getLinha() + "\nJá existe função com o mesmo nome");
        }
    }

    // essa função passa a expressão recebida para o formato pós-fixa
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

    // retorna a prioridade de cada operador, essa função auxilia na pós-fixa
    private int definePrioridaOperador(String operador) {
        if (null != operador) switch (operador) {
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

    // retorna se a expressão é do tipo inteiro ou booleano de acordo com os valores
    // 0=int, 1=boolean
    public String retornaTipoExpressao(String exp) throws SemanticoException {
        String tipo = separaPosFixaExp(exp);

        if (tipo == "0") {
            return Constantes.INTEIRO_LEXEMA;
        } else {
            return Constantes.BOOLEANO_LEXEMA;
        }
    }

    // procura posição da variavel
    public String posicaoVariavel(String var) {
        int pos = tabelaSimbolos.procurarPosicaoVariavel(var);
        return Integer.toString(pos);
    }

    // procura por simbolo especifico e retorna seu indice.
    public int procurarLexema(Token token) throws SemanticoException {
        int resultado = tabelaSimbolos.procurarLexema(token.getLexema());
        if (resultado >=0) {
            return resultado;
        }
        throw new SemanticoException(
            "Não foi encontrado um simbolo com esse nome: '" + token.getLexema() + "' Linha: " + linha);
    }

    // verifica se as expressoes são válidas e retorna o tipo de expressão final
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

    // formata a expressao de uma forma que facilite a geração de codigo
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


    public boolean procuraFuncaoProcedimento(String nomeFuncao) throws SemanticoException {
        boolean resultado = tabelaSimbolos
                .procuraFuncaoProcedimentoIgual(new Token(Constantes.FUNCAO_LEXEMA, nomeFuncao, linha));
        if (!resultado) {
            throw new SemanticoException(
                    "Não foi encontrado uma função com esse nome: '" + nomeFuncao + "' Linha: " + linha);
        }
        return resultado;
    }

    public int procurarRotulo(String nomeFuncaoOuProcedimento) throws SemanticoException {
        int resultado = tabelaSimbolos.procurarRotulo(nomeFuncaoOuProcedimento);

        if (resultado == -1) {
            throw new SemanticoException("Não foi encontrado uma função ou procedimento com esse nome: "
                    + nomeFuncaoOuProcedimento + " Linha: " + linha);
        }
        return resultado;
    }

    public int procuraRotuloProcedimento(Token token) throws SemanticoException{
        int rotres = tabelaSimbolos.procurarRotuloProcedimento(token.getLexema());
        
        if(rotres == -1){
            throw new SemanticoException("Erro linha: " + token.getLinha() + "\nProcedimento " + token.getLexema() + "não declarado");
        }
        else{
            return rotres;
        }     
    }

    public int procurarRotuloFuncao(Token token) throws SemanticoException{
        int rotres = tabelaSimbolos.procurarRotuloFuncao(token.getLexema());
        if(rotres == -1){
            throw new SemanticoException("Erro linha: " + token.getLinha() + "\nFunçao" + token.getLexema() + "não declarada");
        }
        else{
            return rotres;
        }
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

    // retorna qual o formato a operação deve ter de acordo com os operadores e o
    // operador
    private String retornaTipoOperacao(String tipo1, String tipo2, String operador) throws SemanticoException {
        // 0=inteiro, 1=booleano
        if (ehOperador(operador)) {
            if (ehOperadorMatematico(operador)) {
                if (tipo1 == "0" && tipo2 == "0") {
                    return "0";
                }
                throw new SemanticoException(
                        "Operações aritméticas (+ | - | * | div) devem ter duas variáveis inteiras.Linha: " + linha);
            } else if (ehOperadorRelacional(operador)) {
                if (tipo1 == "0" && tipo2 == "0") {
                    return "1";
                }
                throw new SemanticoException(
                        "Operações relacionais(!= | = | < | <= | > | >=) devem ter duas variáveis inteiras.Linha: "
                                + linha);
            } else {
                if (tipo1 == "1" && tipo2 == "1") {
                    return "1";
                }
                throw new SemanticoException(
                        "Operações lógicas (e | ou) devem envolver duas variáveis booleanas.Linha: " + linha);
            }
        } else {
            if (ehOperadorUnarioMat(operador)) {
                if (tipo1 == "0") {
                    return "0";
                }
                throw new SemanticoException(
                        "Operações com unários (+ | -) devem ser com variáveis inteiras.Linha: " + linha);
            } else {
                if (tipo1 == "1") {
                    return "1";
                }
                throw new SemanticoException(
                        "Operações com unário não devem ser com variáveis booleanas.Linha: " + linha);
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

    public void procurarFuncao(Token token) throws SemanticoException {
        if(!(tabelaSimbolos.procuraFuncao(token.getLexema()))){
            throw new SemanticoException("Erro linha: " + token.getLinha() + "\nFunçao" + token.getLexema() + "não declarada");
        }
    }

    public void procurarProcedimento(Token token) throws SemanticoException {
        if(!(tabelaSimbolos.procuraProcedimento(token.getLexema()))){
            throw new SemanticoException("Erro linha: " + token.getLinha() + "\nProcedimento" + token.getLexema() + "não declarado");
        }
    }

    // verifica quem chamou a função ou variavel
    public void quemChamo(String tipo, String chamou) throws SemanticoException {
        if (Constantes.SE_LEXEMA.equals(chamou) || Constantes.ENQUANTO_LEXEMA.equals(chamou)) {
            if (!(Constantes.BOOLEANO_LEXEMA.equals(tipo))) {
                throw new SemanticoException("Erro.\nA condição no '" + chamou + "não é booleana");
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

    public String getLexemaSimbolo(int indice) {
        return tabelaSimbolos.getSimboloLex(indice);
    }

    // chama a função da tabela de simbolos que limpa o nivel
    public void limpaNivelTabela() {
        tabelaSimbolos.limparNivel();
    }

    // funcao auto explicativa
    public void insereTokenFuncaoLista(Token token) {
        listaTokenFuncao.add(token);
    }

    public void limpaListaFuncao(){
        listaTokenFuncao.clear();
    }


    public void verificarListaFuncao(String label) {
        Token auxToken = null;

        boolean conditionalThenReturn = false;
        boolean conditionalElseReturn = false;
        int thenPosition = -1;
        int elsePosition = thenPosition;

        for (int i = 0; i < listaTokenFuncao.size(); i++) {
            if (Constantes.SE_SIMBOLO.equals(listaTokenFuncao.get(i).getSimbolo())
                    && listaTokenFuncao.get(i).getLexema().contains(label)) {
                listaTokenFuncao.remove(i);
                i--;
            } else if (Constantes.ENTAO_SIMBOLO.equals(listaTokenFuncao.get(i).getSimbolo())
                    && listaTokenFuncao.get(i).getLexema().contains(label)) {
                if (listaTokenFuncao.size() > (i + 1)) {
                    if (Constantes.IDENTIFICADOR_SIMBOLO.equals(listaTokenFuncao.get(i + 1).getSimbolo())) {
                        conditionalThenReturn = true;
                        auxToken = listaTokenFuncao.get(i + 1);
                    }
                } else {
                    linhaSemRetorno = listaTokenFuncao.get(i).getLinha();
                }
                thenPosition = i;
            } else if (Constantes.SENAO_SIMBOLO.equals(listaTokenFuncao.get(i).getSimbolo())
                    && listaTokenFuncao.get(i).getLexema().contains(label)) {
                if (listaTokenFuncao.size() > (i + 1)) {
                    if (Constantes.IDENTIFICADOR_SIMBOLO.equals(listaTokenFuncao.get(i + 1).getSimbolo())) {
                        conditionalElseReturn = true;
                        elsePosition = i + 1;
                        auxToken = listaTokenFuncao.get(i + 1);
                    }
                } else {
                    linhaSemRetorno = listaTokenFuncao.get(i).getLinha();
                    elsePosition = i;
                }
            }
        }
        if (elsePosition == (-1)) elsePosition = listaTokenFuncao.size() - 1;

        removeIf(elsePosition, thenPosition, (conditionalThenReturn && conditionalElseReturn), auxToken);
    }

    public boolean verificarSeFuncaoTemRetorno(String nameOfFunction) throws SemanticoException {
        int aux = 0;

        for (int i = 0; i < listaTokenFuncao.size(); i++) {
            if (nameOfFunction.equals(listaTokenFuncao.get(i).getLexema())) {
                aux++;
                if (aux == listaTokenFuncao.size()) {
                    return true;
                }
            }
        }

        error = true;
        if (linhaSemRetorno != 0) linha = linhaSemRetorno;

        throw new SemanticoException("Nem todos os caminhos da função possuem retorno." + "\nLinha: " + linha);
    }

    private void removeIf(int start, int end, boolean functionReturn, Token tokenFunction) {
        for (int i = start; i >= end; i--) {
            listaTokenFuncao.remove(i);
        }

        if (functionReturn && tokenFunction != null) {
            listaTokenFuncao.add(tokenFunction);
        }
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