import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeradordeCodigo {

    private String codigo = ""; // é o código a ser gerado
    private int variavelmemoria = 0; // guarda a posição inicio dos allocs (se tiver mais de um)
    private List<Integer> variavelalloc = new ArrayList<Integer>(); // Lista de variavel que guarda todos allocs feitos

    // gera o código de acordo com os atributos já passados
    public void criaCodigo(String valor1, String valor2, String valor3) {
        codigo = codigo.concat(valor1 + " ").concat(valor2 + " ").concat(valor3 + "\r\n");
    }

    // Gera código de operadores aritméticos, relacionais e lógicos.
    // Recebe uma expressao que é splitada, onde cada termo da expressao vira um
    // código assembly.
    public void criaCodigo(String expposfix) {
        String[] aux = expposfix.split(" ");

        for (int i = 0; i < aux.length; i++) {
            if (aux[i].contains("p")) {
                String[] valor = aux[i].split("p");
                codigo = codigo.concat("LDV ").concat(valor[1]).concat("\r\n");
            } else if (aux[i].contains("funcao")) {
                String[] valor = aux[i].split("funcao");
                codigo = codigo.concat("CALL ").concat("L" + valor[1]).concat("\r\n");
            } else if (aux[i].equals("+")) {
                codigo = codigo.concat("ADD").concat("\r\n");
            } else if (aux[i].equals("-")) {
                codigo = codigo.concat("SUB").concat("\r\n");
            } else if (aux[i].equals("*")) {
                codigo = codigo.concat("MULT").concat("\r\n");
            } else if (aux[i].equals("div")) {
                codigo = codigo.concat("DIVI").concat("\r\n");
            } else if (aux[i].equals("e")) {
                codigo = codigo.concat("AND").concat("\r\n");
            } else if (aux[i].equals("ou")) {
                codigo = codigo.concat("OR").concat("\r\n");
            } else if (aux[i].equals("<")) {
                codigo = codigo.concat("CME").concat("\r\n");
            } else if (aux[i].equals(">")) {
                codigo = codigo.concat("CMA").concat("\r\n");
            } else if (aux[i].equals("=")) {
                codigo = codigo.concat("CEQ").concat("\r\n");
            } else if (aux[i].equals("!=")) {
                codigo = codigo.concat("CDIF").concat("\r\n");
            } else if (aux[i].equals("<=")) {
                codigo = codigo.concat("CMEQ").concat("\r\n");
            } else if (aux[i].equals(">=")) {
                codigo = codigo.concat("CMAQ").concat("\r\n");
            } else if (aux[i].equals("-u")) {
                codigo = codigo.concat("INV").concat("\r\n");
            } else if (aux[i].equals("+u")) {

            } else if (aux[i].equals("nao")) {
                codigo = codigo.concat("NEG").concat("\r\n");
            } else {
                if (aux[i].equals("verdadeiro")) {
                    codigo = codigo.concat("LDC").concat(" 1").concat("\r\n");
                } else if (aux[i].equals("falso")) {
                    codigo = codigo.concat("LDC").concat(" 0").concat("\r\n");
                } else if (aux[i].equals("")) {

                } else {
                    codigo = codigo.concat("LDC ").concat(aux[i]).concat("\r\n");
                }
            }
        }
    }

    // procedimento que gera o código do alloc e dalloc
    public void criaCodigo(String comando, int contvar) {
        if ("ALLOC".equals(comando)) {
            codigo = codigo.concat(comando + " ").concat(variavelmemoria + " ").concat(contvar + "\r\n");
            variavelmemoria = variavelmemoria + contvar;
            variavelalloc.add(contvar);
        } else {
            if (contvar == 0) {
                codigo = codigo.concat(comando + "\r\n");
            } else {
                int posicao = variavelalloc.size() - 1;
                int contvardalloc = variavelalloc.get(posicao);

                variavelmemoria = variavelmemoria - contvardalloc;
                codigo = codigo.concat(comando + " ").concat(variavelmemoria + " ").concat(contvardalloc + "\r\n");
                variavelalloc.remove(posicao);
            }
        }
    }

    // ao ser compilado o programa com sucesso é gerado o arquivo assembly .txt no
    // disco E:
    public void criaArquivo() {
        try {
            File directory = new File("E:\\", "cod_assembly.txt");
            directory.createNewFile();

            FileWriter file = new FileWriter(directory);
            file.write(codigo);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
