import Exception.SemanticoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.List;

public class Semantico {

    TabelaSimbolos tabelaSimbolos;

    private int linha;

    public Semantico() {
        tabelaSimbolos = new TabelaSimbolos();
    }

    public void insereTabela(String lexema, String tipo, int label) {
        tabelaSimbolos.inserirPilhaSimbolos(lexema, tipo, label);
    }

    public void inserirTipoFuncao(String tipo) {
        tabelaSimbolos.inserirTipoFuncao(tipo);
    }

    public void inserirTipoVariavel(String tipo) {
        tabelaSimbolos.inserirTipoVariavel(tipo);
    }

    public void procuraVariavelIgual(Token token) throws SemanticoException {
        if (tabelaSimbolos.procuraVariavelIgual(token)) {
            throw new SemanticoException("Variavel já existente.");
        } else if (tabelaSimbolos.procuraFuncaoProcedimentoIgual(token)) {
            throw new SemanticoException("Variavel com Procedimento/Função com mesmo nome.");
        } else if (tabelaSimbolos.procuraNomePrograma(token)) {
            throw new SemanticoException("Variavel com mesmo nome do programa.");
        }
    }

    public void procuraFuncaoProcedimentoIgual(Token token) throws SemanticoException {
        if (tabelaSimbolos.procuraFuncaoProcedimentoIgual(token)) {
            throw new SemanticoException("Procedimento/Função já existente.");
        } else if (tabelaSimbolos.procuraNomePrograma(token)) {
            throw new SemanticoException("Procedimento/Função com mesmo nome do programa.");
        }
    }

    public String expressaoParaPosFixa(Vector<Token> exp) {
        Vector<String> pilha = new Vector<String>();
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

    public String retornaTipoExpressao(String exp) {
        String tipo = separaPosFixaExp(exp);

        if (tipo == "0") {
            return Constantes.INTEIRO_LEXEMA;
        } else {
            return Constantes.BOOLEANO_LEXEMA;
        }
    }

    private String separaPosFixaExp(String exp) {
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

    public String formataExpressao(String exp) {
        String[] aux = exp.split(" ");
        String novoexp = "";
        int auxposicao;

        for (int i = 0; i < aux.length; i++) {
            if (!tabelaSimbolos.procurarFuncao(aux[i])) { // Passar string para token e chamar a funçao de procurar
                auxposicao = tabelaSimbolos.procurarPosicaoVariavel(aux[i]);
                if (auxposicao != -1) {
                    novoexp = novoexp.concat("p" + auxposicao + " ");
                } else {
                    novoexp = novoexp.concat(aux[i] + " ");
                }
            } else {
                int rotres = tabelaSimbolos.procurarRotuloFuncao(aux[i]);
                novoexp = novoexp.concat("funcao" + rotres + " ");
            }
        }
        return novoexp;
    }

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

    private boolean ehOperadorUnario(String simbolo) {
        if (Constantes.MAIS_UNARIO.equals(simbolo) || Constantes.MENOS_UNARIO.equals(simbolo)
                || Constantes.NAO_LEXEMA.equals(simbolo)) {
            return true;
        }
        return false;
    }

    private String retornaTipoOperacao(String tipo1, String tipo2, String operador) throws SemanticoException {
        // 0=inteiro, 1=booleano
        if (ehOperador(operador)) {
            if (ehOperadorMatematico(operador)) {
                if (tipo1 == "0" && tipo2 == "0") {
                    return "0";
                }
                throw new SemanticoException("Operações aritméticas (+ | - | * | div) devem ter duas variáveis inteiras.Linha: " + linha);
            } else if (ehOperadorRelacional(operador)) {
                if (tipo1 == "0" && tipo2 == "0") {
                    return "1";
                }
                throw new SemanticoException("Operações relacionais(!= | = | < | <= | > | >=) devem ter duas variáveis inteiras.Linha: " + linha);
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

    private boolean ehOperadorUnarioMat(String simbolo){
        if(Constantes.MAIS_UNARIO.equals(simbolo) || Constantes.MENOS_UNARIO.equals(simbolo)){
            return true;
        }
        return false;
    }

    private boolean ehOperadorMatematico(String simbolo) {
        if(Constantes.MULT_LEXEMA.equals(simbolo) || Constantes.DIV_LEXEMA.equals(simbolo) 
                || Constantes.MAIS_LEXEMA.equals(simbolo) || Constantes.MENOS_LEXEMA.equals(simbolo)){
            return true;
        }
        return false;
    }
    
    private boolean ehOperadorRelacional(String simbolo) {
        if (Constantes.MAIOR_LEXEMA.equals(simbolo) || Constantes.MENOR_LEXEMA.equals(simbolo) 
                || Constantes.MAIOR_IGUAL_LEXEMA.equals(simbolo) || Constantes.MENOR_IGUAL_LEXEMA.equals(simbolo) 
                || Constantes.IGUAL_LEXEMA.equals(simbolo)|| Constantes.DIFERENTE_LEXEMA.equals(simbolo)) {
            return true;
        }
        return false;
    }

}
