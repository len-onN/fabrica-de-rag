# Investigação e Fundação Estrutural

Fornece propostas arquiteturais definitivas para os 5 problemas operacionais mapeados durante o benchmark End-to-End, garantindo alinhamento com as ADRs (Decisões Arquiteturais) e Padrões de Projeto do sistema.

## Propostas Arquiteturais

### 1. Quebra de Contrato da API vs Rastreamento Assíncrono (O caso do `ingestRunId`)
**Problema:** O retorno da run de ingestão poluindo o `DocumentResponse`.
**Proposta (Alinhamento com ADR-022 e ADR-023):** 
Manteremos o `DocumentResponse` limpo e isolado de vazamentos do domínio de ingestão. Como a ingestão é assíncrona, a rota de Upload (`POST /documents`) continuará retornando o documento criado. Acompanhamento das execuções será feito implementando a nova rota de listagem com filtro:
`GET /api/v1/workspaces/{workspaceId}/ingest-runs?documentId={documentId}`.
Isso garante a coesão do DTO de documento e preserva a semântica REST.

### 2. Vazamento de Responsabilidade nos Controllers (UUID Interno vs ID Público)
**Problema:** Injeção excessiva de repositories nos Controllers para converter `wsp_123` para UUID.
**Proposta (Alinhamento com Padrões de SOLID/IoC):**
Implementar um mecanismo centralizado de resolução nativo do Spring Web. Vamos criar um `HandlerMethodArgumentResolver` customizado (ex: `PublicIdArgumentResolver`). Com isso, os controllers poderão receber o UUID interno ou até a própria entidade de domínio resolvida diretamente via anotação de injeção (ex: `@ResolvePublicId("workspaceId") UUID internalId`). Isso remove a responsabilidade repetitiva do Controller e mantém a tradução centralizada na borda (Infrastructure/Web layer).

### 3. Falsa Sensação de Segurança (O UUID Chumbado)
**Problema:** UUID mockado usado como autor contornava a segurança real causando falha de FK.
**Proposta (Alinhamento com ADR-024):**
Eliminar todos os mocks/hardcodes de segurança no código de produção. O autor logado deverá sempre ser obtido através da abstração do Spring Security: `@AuthenticationPrincipal CurrentActor currentActor` injetada diretamente nos endpoints. Os Controllers passarão o ID verdadeiro desse `CurrentActor` para os Services da camada de Application, assegurando que auditoria e regras de FK usem a identidade correta e ativa.

### 4. Fragilidade de Acoplamento de Infraestrutura (Rede Docker)
**Problema:** Hardcode de `http://localhost:8000` gerando `Connection Refused` dentro do Docker.
**Proposta (Alinhamento com ADR-019 e Estratégia E2E):**
Externar todas as URLs de serviços para variáveis de ambiente obrigatórias. 
No Spring (`application.yml`): `worker.url: ${WORKER_URL:http://localhost:8000}`.
No `infra/compose/compose.e2e.yml` e `compose.dev.yml`, definiremos a variável de ambiente para o serviço do backend apontando para a rede interna: `WORKER_URL=http://worker:8000`. Nenhuma chamada local fixa será mantida sem fallback via `env var`.

### 5. Falha Final Silenciosa no Pipeline Assíncrono (Worker)
**Problema:** O worker falhava e o Spring ficava esperando para sempre.
**Proposta (Alinhamento com ADR-023 e ADR-026):**
Implementar um _top-level exception handler_ (try/catch global) nas tarefas assíncronas do Worker (ex: função injetada nas `BackgroundTasks` do FastAPI). Ao capturar qualquer exceção não tratada, o Worker fará obrigatoriamente um callback de falha para a API: `PATCH /internal/v1/ingest-runs/{runId}` informando status `FAILED` e um erro seguro. Do lado do Spring, configuraremos um scheduler ou Job para marcar como "TIMEOUT/FAILED" execuções presas no mesmo estado além de um threshold razoável (ex: 2 horas), garantindo resiliência de duas vias.
