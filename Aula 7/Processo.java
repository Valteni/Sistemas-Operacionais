public class Processo {
    final int id;
    final int tamanho;

    public Processo(int id, int tamanho) {
        this.id = id;
        this.tamanho = tamanho;
    }

    @Override
    public String toString() {
        return "P" + this.id;
    }
}