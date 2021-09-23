import java.util.Vector;

public class Sintatico {

    private Vector<Token> Tokens = new Vector<Token>();
    private Lexico lexico;
    String resultado = "";
    Token token = new Token("", "", 0);

    public Sintatico(String codigo){
        lexico = new Lexico(codigo);
    }

    public void analisadorSintatico() {
        getToken();
        if (token.getSimbolo().equals(Constantes.PROGRAMA_SIMBOLO)) {
            getToken();
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                getToken();
                if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                    analisaBloco();
                    if (token.getSimbolo().equals(Constantes.PONTO_SIMBOLO)) {
                        getToken();
                        if (token.getSimbolo().equals(Constantes.FIM_SIMBOLO)) {
                            gerarListaToken();
                            resultado = "Compilação concluida com sucesso";
                        } else {
                            resultado = excecaoSintatico(Constantes.FIM_LEXEMA, token.getLexema(), token.getLinha());
                        }
                    } else {
                        resultado = excecaoSintatico(Constantes.PONTO_LEXEMA, token.getLexema(), token.getLinha());
                    }
                } else {
                    resultado = excecaoSintatico(Constantes.PONTO_VIRGULA_LEXEMA, token.getLexema(), token.getLinha());
                }
            } else {
                resultado = excecaoSintatico(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else {
            resultado = excecaoSintatico(Constantes.PROGRAMA_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    public void analisaBloco(){
        getToken();       
        analisaEtVariaveis();
        analisaSubrotinas();
        analisaComandos();
    }

    private void analisaEtVariaveis(){
        if (token.getSimbolo().equals(Constantes.VAR_SIMBOLO)) {
            getToken();
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)){
                    while (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                        analisaVariaveis();
                        if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)){
                            getToken();
                        } else {
                            resultado = excecaoSintatico(Constantes.PONTO_VIRGULA_LEXEMA, token.getLexema(), token.getLinha());
                        }
                    }
            } else {
                resultado = excecaoSintatico(Constantes.VAR_LEXEMA, token.getLexema(), token.getLinha());
            }
        }   
    }

    private void analisaVariaveis(){
        do {
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                getToken();
                if (token.getSimbolo().equals(Constantes.VIRGULA_SIMBOLO) || token.getSimbolo().equals(Constantes.DOIS_PONTOS_SIMBOLO)) {
                    if (token.getSimbolo().equals(Constantes.VIRGULA_SIMBOLO)) {
                        getToken();
                        if (token.getSimbolo().equals(Constantes.DOIS_PONTOS_SIMBOLO)) {
                            resultado = excecaoSintatico(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
                        }
                    }
                } else {
                    resultado = excecaoSintatico("Virgula ou Dois Pontos", token.getLexema(), token.getLinha());
                }
            } else {
                resultado = excecaoSintatico(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
            }
        } while (!(token.getSimbolo().equals(Constantes.VIRGULA_SIMBOLO)));
        getToken();
        analisaTipo();      
    }

    private void analisaSubrotinas(){
        int flag = 0;
        
        if (token.getSimbolo().equals(Constantes.PROCEDIMENTO_SIMBOLO) || token.getSimbolo().equals(Constantes.FUNCAO_SIMBOLO)){
            flag = 1;
        }

        while (token.getSimbolo().equals(Constantes.PROCEDIMENTO_SIMBOLO) || token.getSimbolo().equals(Constantes.FUNCAO_SIMBOLO)){

                if (token.getSimbolo().equals(Constantes.PROCEDIMENTO_SIMBOLO) ) {
                    analisaDeclaracaoProcedimento();
                } else {
                    analisaDeclaracaoFuncao();
                }
                if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO) ) {
                    getToken();
                } else {
                    resultado = excecaoSintatico(Constantes.PONTO_VIRGULA_LEXEMA, token.getLexema(), token.getLinha());
                }
        }
        if(flag==1){
            //gerar codigo
        }

    }

    private void analisaComandos(){
        if (token.getSimbolo().equals(Constantes.INICIO_SIMBOLO)) {
                getToken();
                analisaComandoSimples();
                while (!(token.getSimbolo().equals(Constantes.FIM_SIMBOLO))) {
                        if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                            getToken();
                            if (token.getSimbolo().equals(Constantes.FIM_SIMBOLO)) {
                                analisaComandoSimples();
                            }
                        } else {
                            resultado = excecaoSintatico(Constantes.PONTO_VIRGULA_LEXEMA, token.getLexema(), token.getLinha());
                        }
                }
                getToken();
        } else {
            resultado = excecaoSintatico(Constantes.INICIO_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaComandoSimples() {
        if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
            analisaAtribChprocedimento();
        } else if (token.getSimbolo().equals(Constantes.SE_SIMBOLO)) {
            analisaSe();           
        } else if (token.getSimbolo().equals(Constantes.ENQUANTO_SIMBOLO)) {
            analisaEnquanto();
        } else if (token.getSimbolo().equals(Constantes.LEIA_SIMBOLO)) {
            analisaLeia();
        } else if (token.getSimbolo().equals(Constantes.ESCREVA_SIMBOLO)) {
            analisaEscreva();
        } else {
            analisaComandos();
        }
    }

    private void analisaAtribChprocedimento() {
        getToken();                
        if (token.getSimbolo().equals(Constantes.ATRIBUICAO_SIMBOLO)) {
            analisaAtribuicao();
        } else {
            chamadaProcedimento();
        }
    }

    private void analisaAtribuicao(){
        getToken(); 
        analisaExpressao();

    }

    private void analisaExpressao(){
        analisaExpressaoSimples();
        if (token.getSimbolo().equals(Constantes.MAIOR_SIMBOLO) || token.getSimbolo().equals(Constantes.MAIOR_IGUAL_SIMBOLO) || token.getSimbolo().equals(Constantes.IGUAL_SIMBOLO) || token.getSimbolo().equals(Constantes.MENOR_SIMBOLO) || token.getSimbolo().equals(Constantes.MENOR_IGUAL_SIMBOLO) || token.getSimbolo().equals(Constantes.DIFERENTE_SIMBOLO)) {
            getToken();
            analisaExpressaoSimples();
        }
    }

    private void analisaExpressaoSimples(){
        if (token.getSimbolo().equals(Constantes.MAIS_SIMBOLO) || token.getSimbolo().equals(Constantes.MENOS_SIMBOLO)) {
            getToken();
        }
        analisaTermo();
        while (token.getSimbolo().equals(Constantes.MAIS_SIMBOLO) || token.getSimbolo().equals(Constantes.MENOS_SIMBOLO) || token.getSimbolo().equals(Constantes.OU_SIMBOLO)) {
            getToken();
            analisaTermo();
        }
    }
    
    private void analisaTermo(){
        analisaFator();
        while (token.getSimbolo().equals(Constantes.MULT_SIMBOLO) || token.getSimbolo().equals(Constantes.DIV_SIMBOLO) || token.getSimbolo().equals(Constantes.E_SIMBOLO)) {
            getToken();
            analisaFator();
        }
    }
    
    private void analisaFator(){
        if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
            chamadaFuncao();
            getToken();
            
        } else if (token.getSimbolo().equals(Constantes.NUMERO_SIMBOLO)) {
            getToken();
        } else if (token.getSimbolo().equals(Constantes.NAO_SIMBOLO)) {
            getToken();
            analisaFator();
        } else if (token.getSimbolo().equals(Constantes.ABRE_PARENTESES_SIMBOLO)) {
            getToken();
            analisaExpressao();
            if (token.getSimbolo().equals(Constantes.FECHA_PARENTESES_SIMBOLO)) {
                getToken();
            } else {
                resultado = excecaoSintatico(Constantes.FECHA_PARENTESES_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else if (token.getSimbolo().equals(Constantes.VERDADEIRO_LEXEMA) || token.getSimbolo().equals(Constantes.FALSO_SIMBOLO)) {
            getToken();
        } else {
            resultado = excecaoSintatico("Verdadeiro ou Falso", token.getLexema(), token.getLinha());
        }
    }

    private void chamadaFuncao(){
    }

    private void chamadaProcedimento(){
    }

    private void analisaLeia(){
        getToken();
        if (token.getSimbolo().equals(Constantes.ABRE_PARENTESES_SIMBOLO)) {
            getToken();
            if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {
                getToken();
                    if (token.getSimbolo().equals(Constantes.FECHA_PARENTESES_SIMBOLO)) {
                        getToken();
                    } else {
                        resultado = excecaoSintatico(Constantes.FECHA_PARENTESES_LEXEMA, token.getLexema(), token.getLinha());
                    }
            } else {
                resultado = excecaoSintatico(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else {
            resultado = excecaoSintatico(Constantes.ABRE_PARENTESES_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaEscreva(){
        getToken();        
    if (token.getSimbolo().equals(Constantes.ABRE_PARENTESES_SIMBOLO)) {
        getToken();
        if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {              
            getToken();                  
                if (token.getSimbolo().equals(Constantes.FECHA_PARENTESES_SIMBOLO)) {
                    getToken();
                } else {
                    resultado = excecaoSintatico(Constantes.FECHA_PARENTESES_LEXEMA, token.getLexema(), token.getLinha());
                }
            } else {
                resultado = excecaoSintatico(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else {
            resultado = excecaoSintatico(Constantes.ABRE_PARENTESES_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaEnquanto(){  
        getToken();
        analisaExpressao();
        if (token.getSimbolo().equals(Constantes.FACA_SIMBOLO)) {
            getToken();
            analisaComandoSimples();
        } else {
            resultado = excecaoSintatico(Constantes.FACA_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaSe(){
        getToken();
        analisaExpressao();   
        if (token.getSimbolo().equals(Constantes.ENTAO_SIMBOLO)) {
            getToken();
            analisaComandoSimples();
            if (token.getSimbolo().equals(Constantes.SENAO_SIMBOLO)) {
                getToken();
                analisaComandoSimples();
            }
        } 
        else {
            resultado = excecaoSintatico(Constantes.ENTAO_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaDeclaracaoProcedimento(){
        getToken();
        if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {        
            getToken();
            if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                analisaBloco();
            } else {
                resultado = excecaoSintatico(Constantes.PONTO_VIRGULA_LEXEMA, token.getLexema(), token.getLinha());
            }
        } else {
            resultado = excecaoSintatico(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaDeclaracaoFuncao(){
        getToken();
        if (token.getSimbolo().equals(Constantes.IDENTIFICADOR_SIMBOLO)) {                 
            getToken();
                if (token.getSimbolo().equals(Constantes.DOIS_PONTOS_SIMBOLO)) {
                    getToken();
                    if (token.getSimbolo().equals(Constantes.INTEIRO_SIMBOLO) || token.getSimbolo().equals(Constantes.BOOLEANO_SIMBOLO)) {
                        getToken();
                        if (token.getSimbolo().equals(Constantes.PONTO_VIRGULA_SIMBOLO)) {
                            analisaBloco();
                        }
                        } else {
                            resultado = excecaoSintatico("Inteiro ou Booleano", token.getLexema(), token.getLinha()); 
                        }
                } else {
                    resultado = excecaoSintatico(Constantes.DOIS_PONTOS_LEXEMA, token.getLexema(), token.getLinha());
                }
        } else {
            resultado = excecaoSintatico(Constantes.IDENTIFICADOR_LEXEMA, token.getLexema(), token.getLinha());
        }
    }

    private void analisaTipo(){
        if (!(token.getSimbolo().equals(Constantes.INTEIRO_SIMBOLO)) && !(token.getSimbolo().equals(Constantes.BOOLEANO_SIMBOLO))) {
            resultado = excecaoSintatico("Inteiro ou Booleano", token.getLexema(), token.getLinha());;
        }
        getToken();
  }

    // TODO Inserir as demais classes do sintatico

    // Pega o proximo token a ser usado
    private void getToken() {
        token = lexico.getToken();
        System.out.println(token.getLexema());
    }

    // Quando encontrarmos um erro, pra facilitar geramos essa mensagem, mas o certo
    // é gerar um Throw.
    public String excecaoSintatico(String LexemaEsperado, String LexemaEncontrado, int linha) {
        return ("Erro na linha: " + linha + " | Simbolo esperado: '" + LexemaEsperado + "' | Simbolo encontrado: '"
                + LexemaEncontrado + "'");
    }

    // função que percorre toda o codigo e joga os tokens numa lista
    private void gerarListaToken() {
        while (token.getLinha() != -1) {
            getToken();

            if (token.getLinha() != -1) {
                Tokens.add(token);
            }
        }
    }

    public String getResultado(){
        return resultado;
    }

    public Vector<Token> PegaVetor() {
        return Tokens;
    }
}
