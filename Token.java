public class Token {
    private String simbolo;
	private String lexema;
	private int linha;

	public Token(String simbolo, String lexema, int linha) {
		this.simbolo = simbolo;
		this.lexema = lexema;
		this.linha = linha;
	}

    // Sprograma
	public String getSimbolo() {
		return simbolo;
	}

    // programa
	public String getLexema() {
		return lexema;
	}

	public int getLinha() {
		return linha;
	}
}
