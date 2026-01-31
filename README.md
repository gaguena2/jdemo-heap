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
```

| Teste | MaxRAMPercentage | Limite do container | Objetivo |
|-------|-----------------|------------------|----------|
| Seguro | 60% | 512 MB | Popular heap sem OOM |
| Alto   | 80% | 512 MB | Popular até quase OOM |
| Extremo| 100%| 512 MB | Simular OOM + gerar heap dump |

----
## Teste seguro: Popular 60% do heap
```bash
docker run --rm -m 512m \
  -e JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=60 -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/dumps" \
  -p 8080:8080 \
  jdemo-heap
```
## Teste alto: Popular 80% do heap
docker run --rm -m 512m \
  -e JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=80 -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/dumps" \
  -p 8080:8080 \
  jdemo-heap
```
## Teste extremo: Popular 100% do heap (simulação de OOM)
docker run --rm -m 512m \
  -e JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=100 -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/dumps" \
  -p 8080:8080 \
  jdemo-heap
```
