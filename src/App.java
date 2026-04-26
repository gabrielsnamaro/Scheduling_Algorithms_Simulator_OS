import java.util.LinkedList;

public class App {
    public static void main(String[] args) {
        LinkedList<Processo> lista = Leitor.doArquivo();
    
        Escalonador srtf = new ShortestRemainingTimeFirst();
        Escalonador fcfs = new FirstComeFirstServed();

        System.out.println("Shortest Remaining Time First" + srtf.escalonar(lista));
        System.out.println("First Come First Served" + fcfs.escalonar(lista));

    }
}