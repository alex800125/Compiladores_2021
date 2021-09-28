package Exception;

public class SintaticoException extends Exception {
    public SintaticoException(String message) {
        super(message);
    }

    public SintaticoException(String tokenesperado, String tokenencontrado, int linha) {
        super("Token esperado: " + tokenesperado + "\nEncontrado: " + tokenencontrado + "\nErro linha: " + linha);
    }

    public SintaticoException(String mensagem, int linha) {
        super("" + mensagem + "\nErro linha: " + linha);
    }
}
