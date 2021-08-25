public class Main {
    public static void main(String[] arg) throws Exception {

        String[] URLsFiles = {
            //"https://pastebin.com/raw/4xBbyy2x",//1
            //"https://pastebin.com/raw/zwRst6H3",//2
            //"https://pastebin.com/raw/ySWH89vr",//3
            "https://pastebin.com/raw/0ujyCqyC",//4
            //"https://pastebin.com/raw/rap3gMRm",//1
            //"https://pastebin.com/raw/DEgXfe5a",//2
            //"https://pastebin.com/raw/xED3vEjp",//3
            //"https://pastebin.com/raw/4zVr7EbT",//4
            //"https://pastebin.com/raw/33neFtaF",//5
            //"https://pastebin.com/raw/upnixbJQ",//6
            //"https://pastebin.com/raw/sDMPHGs1" //7
        };

        URLReader urlReader = new URLReader();
        urlReader.startFile(URLsFiles);
        
        Lexico lexico = new Lexico();
        lexico.analisadorLexical(urlReader);
    }
}
