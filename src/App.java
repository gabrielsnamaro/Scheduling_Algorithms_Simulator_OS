import java.util.LinkedList;

public class App {
    public static void main(String[] args) {
        LinkedList<Processo> lista = Leitor.doArquivo();
    
        Escalonador escalonador = new ShortestRemainingTimeFirst();

        System.out.println(escalonador.escalonar(lista));
    }
}