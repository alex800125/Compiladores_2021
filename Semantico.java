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
}
