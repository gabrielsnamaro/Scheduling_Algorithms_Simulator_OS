import java.util.LinkedList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        LinkedList<Processo> lista = Leitor.doArquivo();

        Escalonador fcfs = new FirstComeFirstServed(lista);

        fcfs.escalonar();
    }

    private static void printList(List<Processo> lista) {
        for(Processo p : lista) {
            System.out.println(p);
        }
    }
}