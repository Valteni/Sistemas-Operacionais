import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulador {

    public static void main(String[] args) {
        List<Processo> processos = new ArrayList<>();
        processos.add(new Processo(1, 5));
        processos.add(new Processo(2, 4));
        processos.add(new Processo(3, 2));
        processos.add(new Processo(4, 5));
        processos.add(new Processo(5, 8));
        processos.add(new Processo(6, 3));
        processos.add(new Processo(7, 5));
        processos.add(new Processo(8, 8));
        processos.add(new Processo(9, 2));
        processos.add(new Processo(10, 6));

        String[] algoritmos = {"First Fit", "Next Fit", "Best Fit", "Worst Fit", "Quick Fit"};

        for (String algoritmo : algoritmos) {
            simular(algoritmo, processos);
        }
    }

    public static void simular(String nomeAlgoritmo, List<Processo> processos) {
        System.out.println("\n=======================================================");
        System.out.println("INICIANDO SIMULACAO PARA O ALGORITMO: " + nomeAlgoritmo);
        System.out.println("=======================================================");
        
        GerenciadorDeMemoria gerenciador = new GerenciadorDeMemoria(32);
        Random random = new Random();

        for (int i = 1; i <= 30; i++) {
            System.out.println("\n--- Operacao " + i + " ---");

            Processo processoSorteado = processos.get(random.nextInt(processos.size()));
            
            if (gerenciador.estaAlocado(processoSorteado.id)) {
                gerenciador.desalocar(processoSorteado.id);
                System.out.println("-> Processo " + processoSorteado + " (tamanho " + processoSorteado.tamanho + ") foi DESALOCADO.");
            } else {
                boolean alocado = false;
                switch (nomeAlgoritmo) {
                    case "First Fit":
                        alocado = gerenciador.alocarFirstFit(processoSorteado);
                        break;
                    case "Next Fit":
                        alocado = gerenciador.alocarNextFit(processoSorteado);
                        break;
                    case "Best Fit":
                        alocado = gerenciador.alocarBestFit(processoSorteado);
                        break;
                    case "Worst Fit":
                        alocado = gerenciador.alocarWorstFit(processoSorteado);
                        break;
                    case "Quick Fit":
                        alocado = gerenciador.alocarQuickFit(processoSorteado);
                        break;
                }
                
                if (alocado) {
                    System.out.println("-> Processo " + processoSorteado + " (tamanho " + processoSorteado.tamanho + ") foi ALOCADO.");
                } else {
                    System.out.println("-> FALHA AO ALOCAR: Nao ha espaco para o Processo " + processoSorteado + " (tamanho " + processoSorteado.tamanho + ").");
                }
            }
            gerenciador.imprimirMapaDeBits();
        }
    }
}