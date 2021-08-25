public class Main {
    public static void main(String[] arg) throws Exception {
        String[] URLsFiles = {
            //"https://pastebin.com/raw/4xBbyy2x",
            //"https://pastebin.com/raw/zwRst6H3",
            // "https://pastebin.com/raw/ySWH89vr", 
             "https://pastebin.com/raw/0ujyCqyC"
            };

        URLReader urlReader = new URLReader();
        urlReader.startFile(URLsFiles);
        
        Lexico lexico = new Lexico();
        lexico.analisadorLexical(urlReader);
    }
}
