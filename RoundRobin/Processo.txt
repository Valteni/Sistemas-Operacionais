public class Processo {
        String pid;
        int chegada;
        int burst;
        int prioridade;
        int tempoRestante;
        int tempoFinal;
        int tempoEspera;
        int tempoRetorno;

        Processo(String pid, int chegada, int burst, int prioridade) {
            this.pid = pid;
            this.chegada = chegada;
            this.burst = burst;
            this.prioridade = prioridade;
            this.tempoRestante = burst;
         }
}
