import java.util.LinkedList;

public class FirstComeFirstServed implements Escalonador {
    @Override
    public Escalonamento escalonar(LinkedList<Processo> processos) {
        Escalonamento resultado = new Escalonamento();

        LinkedList<Processo> processosOrdenados = new LinkedList<>(processos);
        processosOrdenados.sort(
            (p1, p2) -> Integer.compare(p1.getInstanteChegada(), p2.getInstanteChegada())
        );

        for(int i = 0; i < processosOrdenados.size(); i++) {
            Processo atual = processosOrdenados.get(i);
            if (!atual.emEspera(atual.getBurstTotal())) {
                atual.decrementarBurst();
            } else {
                // TODO definir logica de IO e decremento
            }


        }
        return resultado;
    }
}
