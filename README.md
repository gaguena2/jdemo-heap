# jdemo-heap

Aplicação Spring Boot para simular o consumo de heap em Java 17, permitindo testes de memória controlados e geração de **heap dumps** para estudo.

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
- JVM vai usar até 60% do limite do container (~307 MB)
- Heap é popular gradualmente
- Nenhum OOM esperado

## Teste alto: Popular 80% do heap
```bash
docker run --rm -m 512m \
  -e JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=80 -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/dumps" \
  -p 8080:8080 \
  jdemo-heap
```
- JVM vai usar até 80% do limite do container (~410 MB)
- Heap se aproxima do limite → útil para observar comportamento sob alta carga
- OOM ainda não deve ocorrer, mas o heap ficará quase cheio

## Teste extremo: Popular 100% do heap (simulação de OOM)
```bash
docker run --rm -m 512m \
  -e JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=100 -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/dumps" \
  -p 8080:8080 \
  jdemo-heap
```
- JVM tenta usar todo o limite do container (512 MB)
- OOM é esperado
- Heap dump é gerado automaticamente em /app/dumps para auditoria
- Se estiver no kubernetes Evc pode vez de gravar em /app/dumps dentro do container, você monta um volume persistente nesse caminho.

## Ex com 7008 MB :

| Teste   | Limits memory | MaxRAMPercentage | Heap máximo | Non-heap estimado | Overhead/threads/buffers | Margem restante | Observações                                                                             |
| ------- | ------------- | ---------------- | ----------- | ----------------- | ------------------------ | --------------- | --------------------------------------------------------------------------------------- |
| Seguro  | 7008 MB       | 60%              | 4205 MB     | 300 MB            | 300 MB                   | 1203 MB         | Heap popular gradualmente, sem risco de OOM                                             |
| Alto    | 7008 MB       | 80%              | 5606 MB     | 300 MB            | 400 MB                   | 702 MB          | Aproxima-se do limite, útil para testar comportamento sob carga                         |
| Extremo | 7008 MB       | 100%             | 7008 MB     | 300 MB            | 400 MB                   | 0 MB            | Simula OOM: heap consome todo limite do container, non-heap e overhead podem causar OOM |
