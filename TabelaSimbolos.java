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
    }

    // Atribui a uma função o tipo definido na declaração.
    public void inserirTipoFuncao(String tipo) {
        Simbolos simbol = pilhaSimbolos.get(pilhaSimbolos.size()-1);

        if(simbol.getTipo().equals(Constantes.FUNCAO_LEXEMA) && simbol.getBooleanoOuInteiro()==null){
            pilhaSimbolos.get(pilhaSimbolos.size()-1).setBooleanoOuInteiro(tipo);
        }
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
        int i;
        for (i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)) {
                if(token.getLexema().equals(pilhaSimbolos.get(i).getLexema())){
                    return true;
                }
            }
            else{
                break;
            }
        }

        for(int j=i;j>=0;j--){
            if((pilhaSimbolos.get(i).getTipo().equals(Constantes.PROCEDIMENTO_LEXEMA)) || (pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA))){
                if(token.getLexema().equals(pilhaSimbolos.get(i).getLexema())){
                    return true;
                }
            }
        }
        
        return nomePrograma(token.getLexema());
    }

    private boolean nomePrograma(String lexema) {
        if(lexema.equals(pilhaSimbolos.get(0).getLexema())){
            return true;
        }
        return false;
    }

    public boolean procuraVariavel(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if(pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)){
                if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                    return true;
                }
            }
        }
        return nomePrograma(lexema);
    }

    public boolean procuraFuncao(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if(pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA)){
                if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                    return true;
                }
            }
        }
        return nomePrograma(lexema);
    }

    public boolean procuraProcedimento(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if(pilhaSimbolos.get(i).getTipo().equals(Constantes.PROCEDIMENTO_LEXEMA)){
                if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                    return true;
                }
            }
        }
        return nomePrograma(lexema);
    }

    // Função/procedimento com mesmo nome
    public boolean procuraFuncaoProcedimentoIgual(Token token) {
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

    public String getSimboloLex(int indice) {
        return pilhaSimbolos.get(indice).getLexema();
    }

    public int procurarPosicaoVariavel(String variavel) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if(pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA)){
                if (pilhaSimbolos.get(i).getLexema().equals(variavel)) {
                    return pilhaSimbolos.get(i).getPosicao();
                }
            }
        }
        return -1;
    }

    // procura por um simbolo especifico e retorna sua posição na pilha
    public int procurarLexema(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if(pilhaSimbolos.get(i).getTipo().equals(Constantes.VAR_LEXEMA) || pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA)){
                if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                    return i;
                }
            }
        }
        return -1;
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

    // Recebe o nome da função ou do procedimento e procura o seu rotulo
    public int procurarRotuloFuncao(String lexema) {
        for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
            if(pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA)){
                if (pilhaSimbolos.get(i).getLexema().equals(lexema)) {
                    return pilhaSimbolos.get(i).getRotulo();
                }
            }
        }
        return -1;
    }

    public int procurarRotuloProcedimento(String lexema) {
        for(int i = (pilhaSimbolos.size()-1);i>=0;i--){
            if(pilhaSimbolos.get(i).getTipo().equals(Constantes.PROCEDIMENTO_LEXEMA)){
                if(pilhaSimbolos.get(i).getLexema().equals(lexema)){
                    return pilhaSimbolos.get(i).getRotulo();
                }
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
                && (pilhaSimbolos.get(indice).getBooleanoOuInteiro().equals(Constantes.INTEIRO_LEXEMA)
                        || pilhaSimbolos.get(indice).getBooleanoOuInteiro().equals(Constantes.BOOLEANO_LEXEMA));
    }

    // Remove as variaveis declaradas dentro de uma função/procedimento.
    public void limparNivel() {
        for (int i = (pilhaSimbolos.size() - 1); i > 0; i--) {
            if (pilhaSimbolos.get(i).getTipo().equals(Constantes.FUNCAO_LEXEMA)
                    || pilhaSimbolos.get(i).getTipo().equals(Constantes.PROCEDIMENTO_LEXEMA)) {
                if(pilhaSimbolos.get(i).naoestafechado()){
                    pilhaSimbolos.get(i).setaFechado(true);
                    break;
                }
                else{
                    pilhaSimbolos.remove(i);
                }
            } else {
                pilhaSimbolos.remove(i);
            }
        }
    }

    // Classe Simbolos, é uma classe usada para cada elemento da pilha
    private class Simbolos {
        String lexema; // lexema do simbolo atual
        String tipo; // é o tipo do simbolo: nome do programa, variavel, função ou procedimento
        String booleanoOuInteiro; // se for variavel ou função, tem que ser do tipo booleano ou inteiro
        int rotulo; // usado pra identificar funções e procedimentos
        int posicao; // usado pra identificar variaveis
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
