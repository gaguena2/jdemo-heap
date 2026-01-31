# jdemo-heap

Aplicação Spring Boot para simular o consumo de heap em Java, permitindo testes de memória controlados e geração de **heap dumps** para auditoria.

## Objetivo

- Popular o heap da JVM até uma porcentagem configurável do máximo disponível (`MaxRAMPercentage`).  
- Simular cenários de **uso normal**, **uso alto** e **OOM controlado**.  
- Gerar **heap dumps** para análise e auditoria.  
- Integrar com Docker para testes isolados ou Kubernetes.

---

## Como funciona

- O código principal (`FullHeap.toFill`) calcula a memória alvo a ser usada baseado em **uma porcentagem do heap máximo**.  
- Parâmetros importantes da JVM configuráveis via variável de ambiente `JAVA_OPTS`:

| Parâmetro | Descrição |
|-----------|-----------|
| `-XX:+UseG1GC` | Garbage collector moderno |
| `-XX:MaxRAMPercentage=<valor>` | Percentual do limite de memória do container que a JVM pode usar |
| `-XX:+ExitOnOutOfMemoryError` | Faz a JVM sair ao estourar heap |
| `-XX:+HeapDumpOnOutOfMemoryError` | Gera heap dump ao estourar heap |
| `-XX:HeapDumpPath=<path>` | Caminho para armazenar heap dumps |

---

## Build da imagem Docker

```bash
docker build -t jdemo-heap .
