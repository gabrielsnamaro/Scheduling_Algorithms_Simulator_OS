import java.util.LinkedList;

public class App {
    public static void main(String[] args) {
        LinkedList<Processo> lista = Leitor.doArquivo();

        for(Processo processo : lista) {
            System.out.println(processo.toString());
        }
    }
}