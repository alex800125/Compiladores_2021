
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Interface extends JFrame {

    private JButton     importar;
    private JButton     tokens;
    private JTextArea   numerolinha;
    private JTextArea   areacodigo;
    private JTextArea   console;
    private JLabel      tituloconsole;
    private JTable      simbolos;
    private JScrollPane painelpilha;

    
    public Interface(){
        super("Analizador Lexico");
        setLayout(null);
        
        importar = new JButton("Importar");
        importar.setBounds(50, 20, 100, 30);
        add(importar);
        
        tokens = new JButton("Gerar Tokens");
        tokens.setBounds(200, 20, 150, 30);
        add(tokens);
        
        numerolinha = new JTextArea();
        numerolinha.setBackground(Color.LIGHT_GRAY);
        numerolinha.setEditable(false);
        add(numerolinha);
        
        JScrollPane poslinha = new JScrollPane(numerolinha);
        poslinha.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        poslinha.setBounds(45, 80, 25, 650);
        add(poslinha);
        
        areacodigo = new JTextArea();
        add(areacodigo);

        JScrollPane posacodigo = new JScrollPane(areacodigo);
        posacodigo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        posacodigo.setBounds(70, 80, 500, 650);
        posacodigo.getVerticalScrollBar().setModel(poslinha.getVerticalScrollBar().getModel());
        add(posacodigo);
        
        tituloconsole = new JLabel("Console:");
        tituloconsole.setBounds(70, 740, 100, 35);
        tituloconsole.setForeground(Color.BLACK);
	    add(tituloconsole);
        
        console = new JTextArea();
        console.setEditable(false);
	    add(console);
        
        JScrollPane posainfo = new JScrollPane(console);
        posainfo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        posainfo.setBounds(70, 775, 850, 150);
        add(posainfo);    
        
        simbolos = new JTable();
        simbolos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Lexema", "Simbolo"})
            {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        
        simbolos.getTableHeader().setResizingAllowed(false);
        simbolos.getTableHeader().setReorderingAllowed(false);  
        painelpilha = new JScrollPane(simbolos);
        painelpilha.setBounds(600,80,320,650);
        simbolos.setPreferredScrollableViewportSize(simbolos.getPreferredSize());
        simbolos.setFillsViewportHeight(true);
        add(painelpilha);
        
        ImportArquivo botaoimport = new ImportArquivo();
        importar.addActionListener(botaoimport);
        
        RodaLexico botaotokens = new RodaLexico();
        tokens.addActionListener(botaotokens);
        
        DigitarCod digitarcodigo = new DigitarCod();
        areacodigo.addKeyListener(digitarcodigo);
        
    }

    private class ImportArquivo implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            try {
                JFileChooser file = new JFileChooser("D:\\Faculdade/S10/"); 
                file.setFileSelectionMode(JFileChooser.FILES_ONLY);
                file.showOpenDialog(null);
                File arquivo = file.getSelectedFile();
                FileReader arq = new FileReader(arquivo);
                BufferedReader lerArq = new BufferedReader(arq);
                areacodigo.read(lerArq, null);
                lerArq.close();
                EnumerarLinhas();
            }catch (IOException e) {
                System.err.printf("Erro na abertura do arquivo: %s.\n",e.getMessage());
                console.setText("Erro na abertura do arquivo.");
            }
        }
    }

    private class RodaLexico implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == tokens) {
                String msgconsole;
                Lexico geratoken = new Lexico(areacodigo.getText());
                msgconsole = geratoken.Executar();
                console.setText(msgconsole);
            }
        }
    }

    private void EnumerarLinhas() {
        int i = 1, indicechar = 0;
        String codigo = areacodigo.getText();
        String linhabarra = "";

        if (indicechar < codigo.length()) {
            linhabarra = "1\n";
        }

        while (indicechar < codigo.length()) {
            if(codigo.charAt(indicechar) == '\n') {
                i++;
                linhabarra = linhabarra + String.valueOf(i) + '\n';
            }
            indicechar++;
        }
        numerolinha.setText(linhabarra);
    }

    private class DigitarCod implements KeyListener {
        
        @Override
	    public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                EnumerarLinhas();
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_PAGE_UP && e.getKeyCode() != KeyEvent.VK_PAGE_DOWN) {
		        EnumerarLinhas();
            }     
        }

        @Override
        public void keyTyped(KeyEvent e) { 
            
        }
    }

}
