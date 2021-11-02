import Exception.SemanticoException;

public class Semantico {

    TabelaSimbolos tabelaSimbolos;

    public Semantico() {
        tabelaSimbolos = new TabelaSimbolos();
    }

    public void insereTabela(String lexema, String tipo, int posicao) {
        tabelaSimbolos.inserirPilhaSimbolos(lexema, tipo, posicao);
    }

    public void procuraItemIgual(Token token) throws SemanticoException {
        if (tabelaSimbolos.procuraItemIgual(token)) {
            throw new SemanticoException("Variavel, Função ou Procedimento já existente.");
        }
    }
}
