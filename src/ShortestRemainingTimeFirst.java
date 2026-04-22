import java.util.LinkedList;

public class ShortestRemainingTimeFirst implements Escalonador {

    @Override
    public Escalonamento escalonar(LinkedList<Processo> processos) {
        Escalonamento resultado = new Escalonamento();

        LinkedList<Processo> processosOrdenados = new LinkedList<>(processos);
        processosOrdenados.sort(
            (p1, p2) -> Integer.compare(p1.getBurstTotal(), p2.getBurstTotal())
        );

        for(int i = 0; i < processosOrdenados.size(); i++) {
            Processo atual = processosOrdenados.get(i);

            
        }

        return resultado;
    }
}
