import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RoundRobin {
    public static void main(String[] args) {
        Queue<Processo> processos = new LinkedList<>();
        processos.add(new Processo("P1", 0, 5, 0));
        processos.add(new Processo("P2", 2, 3, 0));
        processos.add(new Processo("P3", 4, 8, 0));
        processos.add(new Processo("P4", 5, 6, 0));
        processos.add(new Processo("P5",11, 8, 0 ));

        int quantum = 4;
        executarRoundRobin(processos, quantum);
    }

    public static void executarRoundRobin(Queue<Processo> processos, int quantum) {
        int tempo = 0;
        Queue<Processo> fila = new LinkedList<>();
        List<String> ordemExecucao = new ArrayList<>();
        List<Processo> finalizados = new ArrayList<>();

        while (!processos.isEmpty() || !fila.isEmpty()) {

            while (!processos.isEmpty() && processos.peek().chegada <= tempo) {
                fila.add(processos.poll());
            }
 
            if (!fila.isEmpty()) {
                Processo p = fila.poll();
                ordemExecucao.add(p.pid);

                int execucao = Math.min(quantum, p.tempoRestante);
                tempo += execucao;
                p.tempoRestante -= execucao;


                while (!processos.isEmpty() && processos.peek().chegada <= tempo) {
                    fila.add(processos.poll());
                }

                if (p.tempoRestante > 0) {
                    fila.add(p);
                } else {
                    p.tempoFinal = tempo;
                    p.tempoRetorno = p.tempoFinal - p.chegada;
                    p.tempoEspera = p.tempoRetorno - p.burst;
                    finalizados.add(p);
                }
            } else {
                tempo++;
            }
        }


        double mediaEspera = finalizados.stream().mapToInt(p -> p.tempoEspera).average().orElse(0);
        double mediaRetorno = finalizados.stream().mapToInt(p -> p.tempoRetorno).average().orElse(0);


        StringBuilder saida = new StringBuilder();
        saida.append("Ordem de Execução: ").append(String.join(" → ", ordemExecucao)).append("\n\n");

        saida.append(String.format("%-10s %-15s %-15s\n", "Processo", "Tempo de Espera", "Tempo de Retorno"));
        for (Processo p : finalizados) {
            saida.append(String.format("%-10s %-15d %-15d\n", p.pid, p.tempoEspera, p.tempoRetorno));
        }

        saida.append("\nTempo Médio de Espera: ").append(String.format("%.2f", mediaEspera)).append("\n");
        saida.append("Tempo Médio de Retorno: ").append(String.format("%.2f", mediaRetorno)).append("\n");

        System.out.println(saida);

        try (FileWriter writer = new FileWriter("resultados.txt")) {
            writer.write(saida.toString());
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
}
