import Exception.SemanticoException;

public class Semantico {

    TabelaSimbolos tabelaSimbolos;

    public Semantico() {
        tabelaSimbolos = new TabelaSimbolos();
    }

    public void insereTabela(String lexema, String tipo, int label) {
        tabelaSimbolos.inserirPilhaSimbolos(lexema, tipo, label);
    }

    public void procuraVariavelIgual(Token token) throws SemanticoException {
        if (tabelaSimbolos.procuraVariavelIgual(token)) {
            throw new SemanticoException("Variavel já existente ou Procedimento/Função com mesmo nome.");
        }
    }

    public void procuraFuncaoProcedimentoIgual(Token token) throws SemanticoException {
        if (tabelaSimbolos.procuraFuncaoProcedimentoIgual(token, -1)) {
            throw new SemanticoException("Procedimento/Função já existente.");
        }
    }
}
