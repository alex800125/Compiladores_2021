import java.util.ArrayList;
import java.util.List;

public class TabelaSimbolos {
    private List<Simbolos> pilhaSimbolos;

    public TabelaSimbolos() {
        pilhaSimbolos = new ArrayList<Simbolos>();
    }

    public void inserirPilhaSimbolos(String lexema, String tipo, int label) {
        pilhaSimbolos.add(new Simbolos(lexema, tipo, label));
        exibirPilha();
    }

    // variavel com mesmo nome
    public boolean procuraVariavelIgual(Token token) {
        System.out.println("procuraVariavelIgual - token.lexema = " + token.getLexema());
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            // Caso seja uma função ou procedimento, chegamos ao topo da função ou
            // procedimento atual, daqui pra cima não importa se exista uma variavel de
            // mesmo nome.
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA)
                    || pilhaSimbolos.get(i).getTipo().equals(Constantes.PROCEDIMENTO_LEXEMA)) {
                return procuraFuncaoProcedimentoIgual(token, i);
            }
            if (pilhaSimbolos.get(i).getLexema().equals(token.getLexema())) {
                return true;
            }
        }
        return false;
    }

    // função/procedimento com mesmo nome
    public boolean procuraFuncaoProcedimentoIgual(Token token, int posicao) {
        System.out.println("procuraFuncaoProcedimentoIgual - token.lexema = " + token.getLexema());
        // caso tenha sido chamado pelo procuraVariavelIgual(), ele começa da posição
        // atual.
        if (posicao == -1) {
            posicao = (pilhaSimbolos.size() - 1);
        }
        for (int i = posicao; i >= 0; i--) {
            // só executo se não for uma variavel, caso seja eu apenas pulo ela.
            if (!pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)) {
                if (pilhaSimbolos.get(i).getLexema().equals(token.getLexema())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Usado pra debug
    private void exibirPilha() {
        System.out.println("PILHA INICIO");
        for (int i = 0; i <= pilhaSimbolos.size() - 1; i++) {
            System.out.println("Pilha - lexema = " + pilhaSimbolos.get(i).getLexema() + " simbolo = "
                    + pilhaSimbolos.get(i).getTipo() + " label = " + pilhaSimbolos.get(i).getLabel());
        }
        System.out.println("PILHA FIM");
    }

    private class Simbolos {
        String lexema;
        String tipo;
        int label;

        public Simbolos(String lexema, String tipo, int label) {
            this.setLexema(lexema);
            this.setTipo(tipo);
            this.setLabel(label);
        }

        public String getLexema() {
            return lexema;
        }

        public void setLexema(String lexema) {
            this.lexema = lexema;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public int getLabel() {
            return label;
        }

        public void setLabel(int posicao) {
            this.label = posicao;
        }
    }
}
