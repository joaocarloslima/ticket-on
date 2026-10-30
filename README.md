# Microsserviços com Spring Cloud - Ticket

Projeto de uma arquitetura de microsserviços para compra de ingressos, construído com **Spring Boot** e **Spring Cloud** (Netflix Eureka + Spring Cloud Gateway). O objetivo é demonstrar descoberta de serviços, roteamento via API Gateway, tratamento de concorrência (race condition) na reserva de ingressos e resiliência (retry) no processamento de pagamento.

## Arquitetura

O sistema é composto por 4 serviços:

| Serviço | Porta | Responsabilidade |
|---|---|---|
| [`eureka`](eureka) | `8761` | Service Registry (Netflix Eureka Server) — permite que os demais serviços se registrem e se descubram entre si. |
| [`gateway`](gateway) | `8080` | API Gateway (Spring Cloud Gateway) — ponto único de entrada, roteando requisições `/tickets/**` para o `ticket-service` via balanceamento de carga (`lb://`). |
| [`ticket-service`](ticket-service) | `8083` | Gerencia a compra de ingressos. Usa H2 (em memória) e lock pessimista (`findByIdForUpdate`) para evitar que dois clientes comprem o mesmo ingresso simultaneamente (race condition). Ao confirmar uma compra, chama o `payment-service`. |
| [`payment-service`](payment-service) | `8081` | Processa o pagamento. Simula falhas aleatórias e usa `@Retryable` (Spring Resilience) para reexecutar a operação com backoff exponencial e jitter. |

Fluxo de uma compra:

1. O cliente chama `POST /tickets/purchase/{ticketId}` no **gateway**.
2. O **gateway** descobre o `ticket-service` via **Eureka** e roteia a chamada.
3. O **ticket-service** trava a linha do ingresso (`SELECT ... FOR UPDATE`), verifica disponibilidade e chama o **payment-service**.
4. O **payment-service** processa o pagamento, com retries automáticos em caso de falha.
5. Se o pagamento for confirmado, o `ticket-service` decrementa os ingressos disponíveis.

## Como rodar

Cada serviço é um projeto Gradle independente (usa o Gradle Wrapper, não é necessário instalar Gradle). É necessário **Java 25**.

Suba os serviços em ordem, em terminais separados, a partir da raiz de cada pasta:

```bash
# 1. Eureka (service registry)
cd eureka && ./gradlew bootRun

# 2. Payment Service
cd payment-service && ./gradlew bootRun

# 3. Ticket Service
cd ticket-service && ./gradlew bootRun

# 4. Gateway
cd gateway && ./gradlew bootRun
```

Aguarde cada serviço subir e se registrar no Eureka (painel disponível em `http://localhost:8761`) antes de iniciar o próximo, principalmente antes do `gateway`.

## Endpoints da API

Requisições de exemplo também estão disponíveis em [`api.http`](api.http).

### Comprar ingresso

Via gateway, roteado para o `ticket-service`:

```http
POST http://localhost:8080/tickets/purchase/{ticketId}
```

### Processar pagamento

Chamado internamente pelo `ticket-service`, mas também pode ser testado diretamente no `payment-service`:

```http
POST http://localhost:8081/payments
```

## Teste de carga (k6)

O script [`purchase.js`](purchase.js) simula compras concorrentes do mesmo ingresso para validar o tratamento de race condition no `ticket-service`.

Requer o [k6](https://k6.io/docs/get-started/installation/) instalado.

```bash
k6 run purchase.js
```

O script dispara 20 usuários virtuais fazendo `POST /tickets/purchase/1` através do gateway, verificando que cada compra retorna status `200`.

## Documentação

Documentação complementar para entender visualmente os conceitos abordados:

- [Monólito ou microsserviços](https://joaocarloslima.github.io/ticket-on/docs/monolito-vs-microservicos.html)
- [Race condition no ticket-service](https://joaocarloslima.github.io/ticket-on/docs/race-condition-ticket-service.html)
- [Retry no payment-service](https://joaocarloslima.github.io/ticket-on/docs/retry-payment-service.html)

