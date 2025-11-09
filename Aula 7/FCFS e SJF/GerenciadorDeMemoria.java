import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class Alocacao {
    int indiceInicio;
    int tamanho;

    Alocacao(int indiceInicio, int tamanho) {
        this.indiceInicio = indiceInicio;
        this.tamanho = tamanho;
    }
}

public class GerenciadorDeMemoria {
    private final int[] memoria;
    private final Map<Integer, Alocacao> processosAlocados;
    private int ultimoIndiceVerificado = 0;

    public GerenciadorDeMemoria(int tamanhoMemoria) {
        this.memoria = new int[tamanhoMemoria];
        Arrays.fill(this.memoria, 0);
        this.processosAlocados = new HashMap<>();
    }
    
    public boolean alocarFirstFit(Processo processo) {
        for (int i = 0; i <= memoria.length - processo.tamanho; i++) {
            if (verificarEspacoLivre(i, processo.tamanho)) {
                realizarAlocacao(processo, i);
                return true;
            }
        }
        return false;
    }
    
    public boolean alocarNextFit(Processo processo) {
        for (int i = ultimoIndiceVerificado; i <= memoria.length - processo.tamanho; i++) {
            if (verificarEspacoLivre(i, processo.tamanho)) {
                realizarAlocacao(processo, i);
                ultimoIndiceVerificado = i + processo.tamanho;
                return true;
            }
        }
        for (int i = 0; i < ultimoIndiceVerificado - processo.tamanho; i++) {
            if (verificarEspacoLivre(i, processo.tamanho)) {
                realizarAlocacao(processo, i);
                ultimoIndiceVerificado = i + processo.tamanho;
                return true;
            }
        }
        return false;
    }
    
    public boolean alocarBestFit(Processo processo) {
        int melhorIndice = -1;
        int menorTamanho = Integer.MAX_VALUE;

        int i = 0;
        while (i <= memoria.length - processo.tamanho) {
            if (memoria[i] == 0) {
                int tamanhoBuraco = contarTamanhoBuraco(i);
                if (tamanhoBuraco >= processo.tamanho && tamanhoBuraco < menorTamanho) {
                    menorTamanho = tamanhoBuraco;
                    melhorIndice = i;
                }
                i += tamanhoBuraco;
            } else {
                i++;
            }
        }

        if (melhorIndice != -1) {
            realizarAlocacao(processo, melhorIndice);
            return true;
        }
        return false;
    }
    
    public boolean alocarWorstFit(Processo processo) {
        int piorIndice = -1;
        int maiorTamanho = -1;

        int i = 0;
        while (i <= memoria.length - processo.tamanho) {
            if (memoria[i] == 0) {
                int tamanhoBuraco = contarTamanhoBuraco(i);
                if (tamanhoBuraco >= processo.tamanho && tamanhoBuraco > maiorTamanho) {
                    maiorTamanho = tamanhoBuraco;
                    piorIndice = i;
                }
                i += tamanhoBuraco;
            } else {
                i++;
            }
        }
        
        if (piorIndice != -1) {
            realizarAlocacao(processo, piorIndice);
            return true;
        }
        return false;
    }

    public boolean alocarQuickFit(Processo processo) {
        for (int i = 0; i <= memoria.length - processo.tamanho; i++) {
            if (memoria[i] == 0) {
                int tamanhoBuraco = contarTamanhoBuraco(i);
                if (tamanhoBuraco == processo.tamanho) {
                    realizarAlocacao(processo, i);
                    return true;
                }
                i += tamanhoBuraco -1;
            }
        }
        return alocarFirstFit(processo);
    }
    
    public boolean desalocar(int pid) {
        Alocacao aloc = processosAlocados.get(pid);
        if (aloc != null) {
            for (int i = 0; i < aloc.tamanho; i++) {
                memoria[aloc.indiceInicio + i] = 0;
            }
            processosAlocados.remove(pid);
            return true;
        }
        return false;
    }

    private void realizarAlocacao(Processo processo, int indiceInicio) {
        for (int i = 0; i < processo.tamanho; i++) {
            memoria[indiceInicio + i] = 1;
        }
        processosAlocados.put(processo.id, new Alocacao(indiceInicio, processo.tamanho));
    }
    
    private boolean verificarEspacoLivre(int inicio, int tamanho) {
        for (int i = 0; i < tamanho; i++) {
            if (memoria[inicio + i] == 1) {
                return false;
            }
        }
        return true;
    }
    
    private int contarTamanhoBuraco(int inicio) {
        int tamanho = 0;
        while (inicio + tamanho < memoria.length && memoria[inicio + tamanho] == 0) {
            tamanho++;
        }
        return tamanho;
    }
    
    public boolean estaAlocado(int pid) {
        return processosAlocados.containsKey(pid);
    }
    
    public void imprimirMapaDeBits() {
        System.out.print("Mapa de Bits: " + Arrays.toString(memoria));
        System.out.println();
    }
}