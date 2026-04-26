import java.util.LinkedList;

public class ShortestRemainingTimeFirst extends Escalonador {

    @Override
    public Escalonamento escalonar(LinkedList<Processo> processos) {
        Escalonamento resultado = new Escalonamento();

        LinkedList<Processo> processosOrdenados = new LinkedList<>(processos);
        processosOrdenados.sort(
            (p1, p2) -> Integer.compare(p1.getBurstReal(), p2.getBurstReal())
        );

        for(int i = 0; i < processosOrdenados.size(); i++) {
            Processo atual = processosOrdenados.get(i);

            
        }

        return resultado;
    }
}
