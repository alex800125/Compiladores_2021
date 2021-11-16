import java.util.ArrayList;
import java.util.List;

public class TabelaSimbolos {
    private List<Simbolos> pilhaSimbolos;

    // Construtor da classe, inicia a pilha
    public TabelaSimbolos() {
        pilhaSimbolos = new ArrayList<Simbolos>();
    }

    // Insere itens na pilha
    public void inserirPilhaSimbolos(String lexema, String tipo, int rotulo) {
        pilhaSimbolos.add(new Simbolos(lexema, tipo, rotulo));
        exibirPilha();
    }

    // Atribui a uma função o tipo definido na declaração.
    public void inserirTipoFuncao(String tipo) {
        pilhaSimbolos.get(pilhaSimbolos.size() - 1).setBooleanoOuInteiro(tipo);
        exibirPilha();
    }

    // Atribui a uma variavel o tipo definido na declaração.
    public void inserirTipoVariavel(String tipo) {
        for (int i = (pilhaSimbolos.size() - 1); i > 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)) {
                if (pilhaSimbolos.get(i).getBooleanoOuInteiro() == null) {
                    pilhaSimbolos.get(i).setBooleanoOuInteiro(tipo);
                }
            } else {
                break;
            }
        }

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
                    || pilhaSimbolos.get(i).getTipo().equals(Constantes.PROCEDIMENTO_LEXEMA)
                    || pilhaSimbolos.get(i).getTipo().equals(Constantes.PROGRAMA_LEXEMA)) {
                return false;
            }
            if (pilhaSimbolos.get(i).getLexema().equals(token.getLexema())) {
                return true;
            }
        }
        return false;
    }

    // Função/procedimento com mesmo nome
    public boolean procuraFuncaoProcedimentoIgual(Token token) {
        System.out.println("procuraFuncaoProcedimentoIgual - token.lexema = " + token.getLexema());
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            // só executo se não for uma variavel, caso seja eu apenas pulo ela.
            if (!pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)
                    && !pilhaSimbolos.get(i).getTipo().equals(Constantes.PROGRAMA_LEXEMA)) {
                if (pilhaSimbolos.get(i).getLexema().equals(token.getLexema())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Retorna o tipo de uma variavel ou função (só é elegivel para esses dois
    // metodos)
    public String procurarTipoVariavelFuncao(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)
                    || pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA)) {
                if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                    return pilhaSimbolos.get(i).getBooleanoOuInteiro();
                }
            }
        }
        return null;
    }

    public int procurarPosicaoVariavel(String a) {
        // TODO desenvolver
        return 1;
    }

    // Recebe o nome da função ou do procedimento e procura o seu rotulo
    public int procurarRotulo(String funcaoOuProcedimento) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getLexema().equals(funcaoOuProcedimento)) {
                return pilhaSimbolos.get(i).getRotulo();
            }
        }
        return -1;
    }

    // Verifica se o token tem o mesmo nome do programa
    public boolean procuraNomePrograma(Token token) {
        return pilhaSimbolos.get(0).getLexema().equals(token.getLexema());
    }

    // Remove as variaveis declaradas dentro de uma função/procedimento.
    public void limparNivel() {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA)
                    || pilhaSimbolos.get(i).getTipo().equals(Constantes.PROCEDIMENTO_LEXEMA)) {
                break;
            } else {
                pilhaSimbolos.remove(i);
            }
        }
    }

    // Usado pra debug
    private void exibirPilha() {
        System.out.println("PILHA INICIO");
        for (int i = 0; i <= pilhaSimbolos.size() - 1; i++) {
            System.out.println("Pilha: lexema = '" + pilhaSimbolos.get(i).getLexema() + "' / tipo = '"
                    + pilhaSimbolos.get(i).getTipo() + "' / Booleano/Inteiro = '"
                    + pilhaSimbolos.get(i).getBooleanoOuInteiro() + "' / rotulo = '" + pilhaSimbolos.get(i).getRotulo()
                    + "'");
        }
        System.out.println("PILHA FIM");
    }

    // Classe Simbolos, é uma classe usada para cada elemento da pilha
    private class Simbolos {
        String lexema;
        String tipo;
        String booleanoOuInteiro;
        int rotulo;

        public Simbolos(String lexema, String tipo, int rotulo) {
            this.setLexema(lexema);
            this.setTipo(tipo);
            this.setRotulo(rotulo);
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

        public String getBooleanoOuInteiro() {
            return booleanoOuInteiro;
        }

        public void setBooleanoOuInteiro(String booleanoOuInteiro) {
            this.booleanoOuInteiro = booleanoOuInteiro;
        }

        public int getRotulo() {
            return rotulo;
        }

        public void setRotulo(int rotulo) {
            this.rotulo = rotulo;
        }
    }
}
