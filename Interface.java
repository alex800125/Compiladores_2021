import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.*;
import java.util.Vector;

public class Interface extends JFrame {

    private JButton importar;
    private JButton tokens;
    private JTextArea numeroLinha;
    private JTextArea areaCodigo;
    private JTextArea console;
    private JLabel tituloConsole;

    public Interface() {
        super("Analizador Lexico");
        setLayout(null);

        importar = new JButton("Importar");
        importar.setBounds(50, 20, 100, 30);
        add(importar);

        tokens = new JButton("Rodar");
        tokens.setBounds(200, 20, 150, 30);
        add(tokens);

        numeroLinha = new JTextArea();
        numeroLinha.setBackground(Color.LIGHT_GRAY);
        numeroLinha.setEditable(false);
        add(numeroLinha);

        JScrollPane posLinha = new JScrollPane(numeroLinha);
        posLinha.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        posLinha.setBounds(45, 80, 25, 650);
        add(posLinha);

        areaCodigo = new JTextArea();
        add(areaCodigo);

        JScrollPane posacodigo = new JScrollPane(areaCodigo);
        posacodigo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        posacodigo.setBounds(70, 80, 850, 650);
        posacodigo.getVerticalScrollBar().setModel(posLinha.getVerticalScrollBar().getModel());
        add(posacodigo);

        tituloConsole = new JLabel("Console:");
        tituloConsole.setBounds(70, 740, 100, 35);
        tituloConsole.setForeground(Color.BLACK);
        add(tituloConsole);

        console = new JTextArea();
        console.setEditable(false);
        add(console);

        JScrollPane posainfo = new JScrollPane(console);
        posainfo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        posainfo.setBounds(70, 775, 850, 150);
        add(posainfo);

        ImportArquivo botaoImport = new ImportArquivo();
        importar.addActionListener(botaoImport);

        RodaSintatico botaoTokens = new RodaSintatico();
        tokens.addActionListener(botaoTokens);

        DigitarCod digitarCodigo = new DigitarCod();
        areaCodigo.addKeyListener(digitarCodigo);

    }

    private class ImportArquivo implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            try {
                // JFileChooser file = new
                // JFileChooser("D:\\RTL/Compiladores_2021/Testes/Sintatico");
                JFileChooser file = new JFileChooser(
                        "C:\\Users/alex8/Documents/Compiladores/Compiladores_2021/Testes/Sintatico/Teste_professor");
                file.setFileSelectionMode(JFileChooser.FILES_ONLY);
                file.showOpenDialog(null);
                File arquivo = file.getSelectedFile();
                FileReader arq = new FileReader(arquivo);
                BufferedReader lerArq = new BufferedReader(arq);
                areaCodigo.read(lerArq, null);
                lerArq.close();
                EnumerarLinhas();
            } catch (IOException e) {
                System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
                console.setText("Erro na abertura do arquivo.");
            }
        }
    }

    private class RodaSintatico implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == tokens) {
                Sintatico sintatico = new Sintatico(areaCodigo.getText());
                console.setText(sintatico.getMessage());
            }
        }
    }

    private void EnumerarLinhas() {
        int i = 1, indiceChar = 0;
        String codigo = areaCodigo.getText();
        String linhaBarra = "";

        if (indiceChar < codigo.length()) {
            linhaBarra = "1\n";
        }

        while (indiceChar < codigo.length()) {
            if (codigo.charAt(indiceChar) == '\n') {
                i++;
                linhaBarra = linhaBarra + String.valueOf(i) + '\n';
            }
            indiceChar++;
        }
        numeroLinha.setText(linhaBarra);
    }

    private class DigitarCod implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                EnumerarLinhas();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN
                    && e.getKeyCode() != KeyEvent.VK_PAGE_UP && e.getKeyCode() != KeyEvent.VK_PAGE_DOWN) {
                EnumerarLinhas();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }
    }

}
