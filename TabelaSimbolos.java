import java.util.ArrayList;
import java.util.List;

public class TabelaSimbolos {
    private List<Simbolos> pilhaSimbolos;

    // Construtor da classe, inicia a pilha
    public TabelaSimbolos() {
        pilhaSimbolos = new ArrayList<Simbolos>();
    }

    /**
     * Função responsável por popular a pilha de simbolos, ela é unificada e chamada
     * apenas pelo Semantico.
     * 
     * @param lexema  recebe o lexema do token atual.
     * @param tipo    recebe o tipo, ou seja: nome do programa, variavel, função ou
     *                procedimento.
     * @param rotulo  caso seja uma função ou procedimento, recebe o contador
     *                rotulo, caso contrário, recebe -1. Usado na geração de código.
     * @param posicao caso seja uma variavel, recebe o contador de variaveis, caso
     *                contrário, recebe -1. Usado na geração de código.
     */
    public void inserirPilhaSimbolos(String lexema, String tipo, int rotulo, int posicao) {
        pilhaSimbolos.add(new Simbolos(lexema, tipo, rotulo, posicao));
    }

    /**
     * Atribui a uma função o tipo definido na declaração.
     * 
     * @param tipo recebe o lexema do 'booleano' ou 'inteiro'
     */
    public void inserirTipoFuncao(String tipo) {
        Simbolos simbol = pilhaSimbolos.get(pilhaSimbolos.size() - 1);

        if (simbol.getTipo().equals(Constantes.FUNCAO_LEXEMA) && simbol.getBooleanoOuInteiro() == null) {
            pilhaSimbolos.get(pilhaSimbolos.size() - 1).setBooleanoOuInteiro(tipo);
        }
    }

    /**
     * Atribui a uma variavel o tipo definido na declaração.
     * 
     * @param tipo recebe o lexema do 'booleano' ou 'inteiro'
     */
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

    /**
     * Procura variaveis com o mesmo nome.
     * 
     * @param token recebe o token e verifica o lexema a ser procurado.
     * @return caso encontre alguma variavel igual, retornamos 'true', caso
     *         contrário verificamos com o nome do programa.
     */
    public boolean procuraVariavelIgual(Token token) {
        int i;
        for (i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)) {
                if (token.getLexema().equals(pilhaSimbolos.get(i).getLexema())) {
                    return true;
                }
            } else {
                break;
            }
        }

        for (int j = i; j >= 0; j--) {
            if ((pilhaSimbolos.get(i).getTipo().equals(Constantes.PROCEDIMENTO_LEXEMA))
                    || (pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA))) {
                if (token.getLexema().equals(pilhaSimbolos.get(i).getLexema())) {
                    return true;
                }
            }
        }

        return nomePrograma(token.getLexema());
    }

    /**
     * verifica o nome do programa.
     * 
     * @param lexema recebe o lexema a ser procurado.
     * @return retornamos 'true' se o lexema recebido for igual ao nome do programa
     */
    private boolean nomePrograma(String lexema) {
        if (lexema.equals(pilhaSimbolos.get(0).getLexema())) {
            return true;
        }
        return false;
    }

    /**
     * Procura variaveis com o mesmo nome.
     * 
     * @param lexema recebe o lexema a ser procurado.
     * @return caso encontre alguma variavel igual, retornamos 'true', caso
     *         contrário verificamos com o nome do programa.
     */
    public boolean procuraVariavel(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)) {
                if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                    return true;
                }
            }
        }
        return nomePrograma(lexema);
    }

    /**
     * Procura função com o mesmo nome.
     * 
     * @param lexema recebe o lexema a ser procurado.
     * @return caso encontre uma função igual, retorna 'true', caso
     *         contrário verificamos com o nome do programa.
     */
    public boolean procuraFuncao(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA)) {
                if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                    return true;
                }
            }
        }
        return nomePrograma(lexema);
    }

    /**
     * Procura procedimento com o mesmo nome.
     * 
     * @param lexema recebe o lexema a ser procurado.
     * @return caso encontre um procedimento igual, retorna 'true', caso
     *         contrário verificamos com o nome do programa.
     */
    public boolean procuraProcedimento(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.PROCEDIMENTO_LEXEMA)) {
                if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                    return true;
                }
            }
        }
        return nomePrograma(lexema);
    }

    /**
     * Procura o tipo de uma variavel ou de uma função.
     * 
     * @param lexema recebe o lexema a ser procurado.
     * @return retorna se a variavel ou função é do tipo 'Inteiro' ou 'Booleano'.
     */
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

    /**
     * Retorna o lexema no indice passado.
     * 
     * @param indice representa posição na pilha
     * @return retorna o lexema desejado.
     */
    public String getSimboloLex(int indice) {
        return pilhaSimbolos.get(indice).getLexema();
    }

    /**
     * Retorna a posição que foi atribuido a essa variavel durante a criação
     * 
     * @param variavel recebe o nome da variavel.
     * @return retorna o valor que foi passado durante a criação.
     */
    public int procurarPosicaoVariavel(String variavel) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)) {
                if (pilhaSimbolos.get(i).getLexema().equals(variavel)) {
                    return pilhaSimbolos.get(i).getPosicao();
                }
            }
        }
        return -1;
    }

    /**
     * Procura por um simbolo especifico e retorna sua posição na pilha.
     * 
     * @param lexema recebe o lexema a ser procurado.
     * @return retorna a sua posição na pilha.
     */
    public int procurarLexema(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)
                    || pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA)) {
                if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Retorna o rotulo de uma função que foi atribuido durante sua criação.
     * 
     * @param lexema Recebe o nome da função.
     * @return retorna o rotulo encontrado ou -1 caso não encontre.
     */
    public int procurarRotuloFuncao(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA)) {
                if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                    return pilhaSimbolos.get(i).getRotulo();
                }
            }
        }
        return -1;
    }

    /**
     * Retorna o rotulo de um procedimento que foi atribuido durante sua criação.
     * 
     * @param lexema Recebe o nome do procedimento.
     * @return retorna o rotulo encontrado ou -1 caso não encontre.
     */
    public int procurarRotuloProcedimento(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.PROCEDIMENTO_LEXEMA)) {
                if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                    return pilhaSimbolos.get(i).getRotulo();
                }
            }
        }
        return -1;
    }

    /**
     * verifica se é uma função ou variavel
     * 
     * @param indice recebe a posição na pilha.
     * @return retorna 'true' se for uma funcao e tiver um tipo definido, retorna
     *         'false' se for uma variavel
     */
    public boolean ehFuncaoValida(int indice) {
        return pilhaSimbolos.get(indice).getTipo().equals(Constantes.FUNCAO_LEXEMA)
                && (pilhaSimbolos.get(indice).getBooleanoOuInteiro().equals(Constantes.INTEIRO_LEXEMA)
                        || pilhaSimbolos.get(indice).getBooleanoOuInteiro().equals(Constantes.BOOLEANO_LEXEMA));
    }

    /**
     * Quando uma função, procedimento ou o programa chega ao fim, é removido as
     * variaveis alocadas dentro desse trecho de código.
     */
    public void limparNivel() {
        for (int i = (pilhaSimbolos.size() - 1); i > 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA)
                    || pilhaSimbolos.get(i).getTipo().equals(Constantes.PROCEDIMENTO_LEXEMA)) {
                if (pilhaSimbolos.get(i).naoestafechado()) {
                    pilhaSimbolos.get(i).setaFechado(true);
                    break;
                } else {
                    pilhaSimbolos.remove(i);
                }
            } else {
                pilhaSimbolos.remove(i);
            }
        }
    }

    /**
     * Classe Simbolos, é usada para representar cada elemento da pilha.
     */
    private class Simbolos {
        String lexema; // lexema do token atual.
        String tipo; // tipo do simbolo: nome do programa, variavel, função ou procedimento.
        String booleanoOuInteiro; // se for variavel ou função, tem que ser do tipo booleano ou inteiro.
        int rotulo; // usado pra identificar posição de funções e procedimentos na geração de código.
        int posicao; // usado pra identificar a posição de variaveis na geração de código.
        private boolean fechado;

        public Simbolos(String lexema, String tipo, int rotulo, int posicao) {
            this.setLexema(lexema);
            this.setTipo(tipo);
            this.setRotulo(rotulo);
            this.setPosicao(posicao);
            this.fechado = false;
            this.booleanoOuInteiro = null;
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

        public boolean naoestafechado() {
            return !fechado;
        }

        public void setaFechado(boolean fechado) {
            this.fechado = fechado;
        }
    }
}
