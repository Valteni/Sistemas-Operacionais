#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define NUM_PROCESSOS 10
#define QUANTUM 1000

#define PRONTO 0
#define EXECUTANDO 1
#define BLOQUEADO 2
#define TERMINADO 3

#define CHANCE_IO 1
#define CHANCE_DESBLOQUEIO 30

int tempo_total_necessario[NUM_PROCESSOS];
int tp[NUM_PROCESSOS];
int cp[NUM_PROCESSOS];
int estado[NUM_PROCESSOS];
int nes[NUM_PROCESSOS];
int n_cpu[NUM_PROCESSOS];

const char* estado_para_string(int estado_num) {
    if (estado_num == PRONTO) return "PRONTO";
    if (estado_num == EXECUTANDO) return "EXECUTANDO";
    if (estado_num == BLOQUEADO) return "BLOQUEADO";
    if (estado_num == TERMINADO) return "TERMINADO";
    return "DESCONHECIDO";
}

void imprimir_tabela_console() {
    printf("\n======================== TABELA DE PROCESSOS (SNAPSHOT) =========================\n");
    printf("%-5s | %-12s | %-20s | %-10s | %-10s | %-10s\n", "PID", "ESTADO", "TEMPO PROC.", "CP", "E/S", "CPU BURSTS");
    printf("---------------------------------------------------------------------------------\n");
    for (int i = 0; i < NUM_PROCESSOS; i++) {
        printf("%-5d | %-12s | %-6d / %-11d | %-10d | %-10d | %-10d\n",
                i,
                estado_para_string(estado[i]),
                tp[i],
                tempo_total_necessario[i],
                cp[i],
                nes[i],
                n_cpu[i]);
    }
    printf("=================================================================================\n\n");
}

void salvar_tabela_txt() {
    FILE* f = fopen("Tabela_Processos.txt", "w");
    if (f == NULL) {
        printf("ERRO: Nao foi possivel criar o arquivo Tabela_Processos.txt\n");
        return;
    }

    fprintf(f, "================================ TABELA DE PROCESSOS =================================\n");
    fprintf(f, "%-5s | %-12s | %-20s | %-10s | %-10s | %-10s\n", "PID", "ESTADO", "TEMPO PROC.", "CP", "E/S", "CPU BURSTS");
    fprintf(f, "--------------------------------------------------------------------------------------\n");

    for (int i = 0; i < NUM_PROCESSOS; i++) {
        fprintf(f, "%-5d | %-12s | %-6d / %-11d | %-10d | %-10d | %-10d\n",
                i,
                estado_para_string(estado[i]),
                tp[i],
                tempo_total_necessario[i],
                cp[i],
                nes[i],
                n_cpu[i]);
    }

    fclose(f);
    printf("   -> Tabela de Processos salva em 'Tabela_Processos.txt'\n\n");
}

int main() {
    srand(time(NULL));

    int salvar_em_arquivo = 0;
    printf("Deseja salvar a Tabela de Processos em 'Tabela_Processos.txt' a cada passo? (1 para Sim, 0 para Nao): ");
    scanf("%d", &salvar_em_arquivo);

    int tempos_iniciais[NUM_PROCESSOS] = {10000, 5000, 7000, 3000, 3000, 8000, 2000, 5000, 4000, 10000};
    for (int i = 0; i < NUM_PROCESSOS; i++) {
        tempo_total_necessario[i] = tempos_iniciais[i];
        tp[i] = 0;
        cp[i] = 1;
        estado[i] = PRONTO;
        nes[i] = 0;
        n_cpu[i] = 0;
    }
    printf("\n>>> Simulacao iniciada.\n");
    imprimir_tabela_console();
    if (salvar_em_arquivo) {
        salvar_tabela_txt();
    }

    int processos_terminados = 0;
    int pid_atual = 0;

    while (processos_terminados < NUM_PROCESSOS) {
        
        if (estado[pid_atual] == TERMINADO) {
            pid_atual = (pid_atual + 1) % NUM_PROCESSOS;
            continue;
        }

        if (estado[pid_atual] == BLOQUEADO) {
            if ((rand() % 100) < CHANCE_DESBLOQUEIO) {
                estado[pid_atual] = PRONTO;
                printf(">>> PID %d: MUDANCA DE ESTADO: BLOQUEADO -> PRONTO\n", pid_atual);
            }
            pid_atual = (pid_atual + 1) % NUM_PROCESSOS;
            continue;
        }

        if (estado[pid_atual] == PRONTO) {
            estado[pid_atual] = EXECUTANDO;
            n_cpu[pid_atual]++;
            printf("\n>>> PID %d: ENTRANDO NA CPU: PRONTO -> EXECUTANDO (Quantum: %d)\n", pid_atual, QUANTUM);

            int io_ocorreu = 0;

            for (int ciclo = 0; ciclo < QUANTUM; ciclo++) {
                if (tp[pid_atual] >= tempo_total_necessario[pid_atual]) {
                    break;
                }
                tp[pid_atual]++;
                cp[pid_atual] = tp[pid_atual] + 1;
                if ((rand() % 100) < CHANCE_IO) {
                    nes[pid_atual]++;
                    estado[pid_atual] = BLOQUEADO;
                    io_ocorreu = 1;
                    
                    printf("\n--- Troca de Contexto: PID %d | EXECUTANDO -> BLOQUEADO (E/S) ---\n", pid_atual);
                    imprimir_tabela_console();
                    if (salvar_em_arquivo) {
                        salvar_tabela_txt();
                    }
                    break;
                }
            }

            if (tp[pid_atual] >= tempo_total_necessario[pid_atual]) {
                estado[pid_atual] = TERMINADO;
                processos_terminados++;
                
                printf("\n#############################################################\n");
                printf("############ PROCESSO %d TERMINADO ############\n", pid_atual);
                printf("--- Estado Final ---\n");
                imprimir_tabela_console();
                if (salvar_em_arquivo) {
                    salvar_tabela_txt();
                }
                printf("#############################################################\n\n");

            } else if (!io_ocorreu) {
                estado[pid_atual] = PRONTO;
                
                printf("\n--- Troca de Contexto: PID %d | EXECUTANDO -> PRONTO (Quantum Expirado) ---\n", pid_atual);
                imprimir_tabela_console();
                if (salvar_em_arquivo) {
                    salvar_tabela_txt();
                }
            }
        }
        
        pid_atual = (pid_atual + 1) % NUM_PROCESSOS;
    }

    printf("\nTODOS OS PROCESSOS FORAM TERMINADOS. SIMULACAO CONCLUIDA.\n");
    return 0;
}
