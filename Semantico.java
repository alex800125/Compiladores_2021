import Exception.SemanticoException;
import java.util.Vector;

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
}
