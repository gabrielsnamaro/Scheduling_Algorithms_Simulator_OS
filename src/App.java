import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        LinkedList<Processo> lista;

        int opcao;
        do {
            lista = Leitor.doArquivo();
            opcao = menu(teclado);

            try {
                executar(opcao, lista);
            } catch(IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

            esperar(teclado);
            limparTela();
        } while(opcao != 0);
    }

    private static int lerInteiro(String mensagem, Scanner teclado) {
        System.out.print(mensagem);
        return Integer.parseInt(teclado.nextLine());
    }

    private static int menu(Scanner teclado) {
        String menu = "************ ESCALONADOR SIMULADO ************\nAutoria: Gabriel Silva Neiva Amaro e Otávio Chaves Silva\nEngenharia de Software - 3º período\n\n* Opção 1: Listar processos do arquivo;\n* Opção 2: First Come First Served (FCFS);\n* Opção 3: Shortest Remaining Time First (SRTF);\n* Opção 4: Round Robin com predição;\n* Opção 5: Multi Level Queue (Round Robin e FCFS);\n* Opção 0: Encerrar.\n\nInsira sua opção: ";

        return lerInteiro(menu, teclado);
    }

    private static void executar(int opcao, LinkedList<Processo> processos) throws IllegalArgumentException {
        Escalonador escalonador;

        switch (opcao) {
            case 1:
                printList(processos);
                break;
            case 2:
                escalonador = new FirstComeFirstServed(processos);
                escalonador.escalonar();
                break;
            case 3:
                escalonador = new ShortestRemainingTimeFirst(processos);
                escalonador.escalonar();
                break;
            case 4:
                break;
            case 5:
                escalonador = new MultilevelQueue(processos);
                escalonador.escalonar();
                break;
            case 0:
                break;
            default:
                throw new IllegalArgumentException("Opção inválida!");
        }
    }

    private static void printList(List<Processo> lista) {
        for(Processo p : lista) {
            System.out.println(p);
        }
    }

    private static void esperar(Scanner teclado) {
        teclado.nextLine();
    }

    private static void limparTela() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }
}