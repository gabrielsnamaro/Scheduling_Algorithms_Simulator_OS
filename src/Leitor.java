import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class Leitor {
    private static final String NOME_ARQUIVO;

    static {
        NOME_ARQUIVO = "processos.txt";
    }

    public static LinkedList<Processo> doArquivo() {
        LinkedList<Processo> lista = new LinkedList<>();

        File arquivo = new File(NOME_ARQUIVO);

        Scanner leitor;

        try {
            leitor = new Scanner(arquivo);

            while(leitor.hasNextLine())
                lista.add(new Processo(leitor.nextLine()));

            leitor.close();
        } catch (FileNotFoundException e) {
            System.out.println("Deu pau");
        }

        return lista;
    }
}
