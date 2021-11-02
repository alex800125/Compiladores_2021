import java.util.ArrayList;
import java.util.List;

public class TabelaSimbolos {
    private List<Simbolos> pilhaSimbolos;

    public TabelaSimbolos() {
        pilhaSimbolos = new ArrayList<Simbolos>();
    }
    // TODO Por enquanto é apenas uma previa da Tabela de simbolos

    public void inserirPilhaSimbolos(String lexema, String tipo, int posicao) {
        pilhaSimbolos.add(new Simbolos(lexema, tipo, posicao));
        exibirPilha();
    }

    public boolean procuraItemIgual(Token token) {
        // TODO verificar se esta correto o funcionamento, nesse caso não poderá ter um
        // função/procedimento ou variavel com mesmo nome
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            System.out.println("procuraItemIgual - pilhaSimbolos.lexema = " + pilhaSimbolos.get(i).getLexema()
                    + " token.getLexema() = " + token.getLexema());
            if (pilhaSimbolos.get(i).getLexema().equals(token.getLexema())) {
                return true;
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
