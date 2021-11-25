import java.util.ArrayList;
import java.util.List;

public class TabelaSimbolos {
    private List<Simbolos> pilhaSimbolos;

    // Construtor da classe, inicia a pilha
    public TabelaSimbolos() {
        pilhaSimbolos = new ArrayList<Simbolos>();
    }

    // Insere itens na pilha
    public void inserirPilhaSimbolos(String lexema, String tipo, int rotulo, int posicao) {
        pilhaSimbolos.add(new Simbolos(lexema, tipo, rotulo, posicao));
        exibirPilha();
    }

    // Atribui a uma função o tipo definido na declaração.
    public void inserirTipoFuncao(String tipo) {
        pilhaSimbolos.get(pilhaSimbolos.size() - 1).setBooleanoOuInteiro(tipo);
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

    public boolean procuraVariavel(Token token) {
        System.out.println("procuraVariavel - token.lexema = " + token.getLexema());
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getLexema().equals(token.getLexema())
                    && pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)) {
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

    public boolean procuraFuncao(String funcao) {
        System.out.println("procuraFuncao - token.lexema = " + funcao);
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            // só executo se não for uma variavel, caso seja eu apenas pulo ela.
            if (!pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)
                    && !pilhaSimbolos.get(i).getTipo().equals(Constantes.PROGRAMA_LEXEMA)
                    && !pilhaSimbolos.get(i).getTipo().equals(Constantes.PROCEDIMENTO_LEXEMA)) {
                if (pilhaSimbolos.get(i).getLexema().equals(funcao)) {
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

    public String getSimbolo(int indice) {
        return pilhaSimbolos.get(indice).getLexema();
    }

    public int procurarPosicaoVariavel(String variavel) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getLexema().equals(variavel)) {
                return pilhaSimbolos.get(i).getPosicao();
            }
        }
        return -1;
    }

    // procura por um simbolo especifico e retorna sua posição na pilha
    public int procurarLexema(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                return i;
            }
        }
        return -1;
    }

    // Recebe o nome da função ou do procedimento e procura o seu rotulo
    public int procurarRotulo(String funcaoOuProcedimento) {
        exibirPilha();
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

    public boolean ehFuncaoValida(int indice) {
        return pilhaSimbolos.get(indice).getTipo().equals(Constantes.FUNCAO_LEXEMA)
                && (pilhaSimbolos.get(indice).getBooleanoOuInteiro().equals(Constantes.BOOLEANO_LEXEMA)
                        || pilhaSimbolos.get(indice).getBooleanoOuInteiro().equals(Constantes.INTEIRO_LEXEMA));
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
        System.out.println("\nPILHA INICIO");
        for (int i = 0; i <= pilhaSimbolos.size() - 1; i++) {
            System.out.println("Pilha: lexema = '" + pilhaSimbolos.get(i).getLexema() + "' / tipo = '"
                    + pilhaSimbolos.get(i).getTipo() + "' / Booleano/Inteiro = '"
                    + pilhaSimbolos.get(i).getBooleanoOuInteiro() + "' / rotulo = '" + pilhaSimbolos.get(i).getRotulo()
                    + "'");
        }
        System.out.println("PILHA FIM\n");
    }

    // Classe Simbolos, é uma classe usada para cada elemento da pilha
    private class Simbolos {
        String lexema; // lexema do simbolo atual
        String tipo; // é o tipo do simbolo: nome do programa, variavel, função ou procedimento
        String booleanoOuInteiro; // se for variavel ou função, tem que ser do tipo booleano ou inteiro
        int rotulo; // usado pra identificar funções e procedimentos
        int posicao; // usado pra identificar variaveis

        public Simbolos(String lexema, String tipo, int rotulo, int posicao) {
            this.setLexema(lexema);
            this.setTipo(tipo);
            this.setRotulo(rotulo);
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

        public int getPosicao() {
            return posicao;
        }

        public void setPosicao(int posicao) {
            this.posicao = posicao;
        }
    }
}
