import java.util.ArrayList;
import java.util.List;

public class TabelaSimbolos {

    private List<Simbolos> pilhaSimbolos = new ArrayList<Simbolos>();

    // TODO Por ernquanto é apenas uma previa da Tabela de simbolos

    public void inserirPilhaSimbolos(String lexema, String tipo, int posicao) {

        pilhaSimbolos.add(new Simbolos(lexema, tipo, posicao));
    }

    private class Simbolos {
        String lexema;
        String tipo;
        int posicao;

        public Simbolos(String lexema, String tipo, int posicao) {
            this.setLexema(lexema);
            this.setTipo(tipo);
            this.setPosicao(posicao);
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

        public int getPosicao() {
            return posicao;
        }

        public void setPosicao(int posicao) {
            this.posicao = posicao;
        }
    }
}
