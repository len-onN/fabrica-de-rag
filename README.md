# Fabrica de RAG

Aplicacao web para criar, curar, testar e servir bancos de conhecimento vetoriais como RAGs, com foco em pesquisa, estudo, desenvolvimento assistido, controle de ingestao, rastreabilidade das fontes e uso por agentes via API e MCP.

Repositorio remoto planejado:

https://github.com/len-onN/fabrica-de-rag

## Estado atual

Este repositorio esta na fase de concepcao e planejamento. O objetivo agora e registrar a proposta, discutir as decisoes de arquitetura e transformar gradualmente essas decisoes em um plano tecnico para um MVP.

## Documentacao

- [Proposta principal](docs/proposta-principal.md)
- [Proposito e casos de uso](docs/proposito-e-casos-de-uso.md)
- [Features planejadas](docs/features.md)
- [MVP: interface e API](docs/mvp-interface-e-api.md)
- [Design visual](docs/design-visual.md)
- [Estrategia de base de conhecimento](docs/base-de-conhecimento.md)
- [Padroes de projeto e desenvolvimento](docs/padroes-de-projeto.md)
- [Git e commits](docs/git-e-commits.md)
- [Usuarios, workspaces e acesso](docs/usuarios-e-acesso.md)
- [Fontes e integracoes futuras](docs/fontes-e-integracoes-futuras.md)
- [Decisoes de arquitetura](docs/decisoes-arquiteturais.md)
- [Conectores de bancos vetoriais](docs/conectores-vector-store.md)
- [Analytics local de RAG](docs/analytics-local-rag.md)
- [Planejamento](docs/planejamento.md)

## Stacks escolhidas ate aqui

- Frontend: Angular moderno.
- Backend principal: Java com Spring Boot.
- Worker de ML/documentos: Python em container separado.
- Banco relacional: PostgreSQL.
- Banco vetorial inicial: Qdrant interno via Docker Compose.
- Fonte documental inicial: PDF.
- Conectores vetoriais futuros: Qdrant externo, Pinecone, Weaviate, Milvus e pgvector.
- Armazenamento de arquivos: filesystem local no desenvolvimento; MinIO/S3 como caminho natural para ambientes maiores.
- Integracao com agentes: API HTTP e MCP.
- Analytics local: eventos e auditoria para o usuario avaliar e melhorar seus proprios RAGs.
- Base de conhecimento do desenvolvimento: colecoes ativaveis por stack, fonte oficial/primaria e decisoes versionadas em ADRs.
- Usuarios e acesso: modo local com usuario unico, mas arquitetura preparada para workspaces, roles e deploy multiusuario.
- Design visual: minimalista, com modo claro/escuro, violeta como identidade e verde como sinal de confianca/sucesso.

## Principios do produto

- O usuario nao deve precisar editar vetores diretamente.
- A engenharia de conhecimento deve acontecer por ingestao, curadoria, relacoes, politicas de contexto e avaliacao.
- O sistema deve preservar proveniencia: documento, pagina fisica, pagina impressa, bbox, elemento, chunk e evidencia visual.
- PDF e imagem devem ser tratados como fontes estruturaveis, nao apenas como texto plano.
- A recuperacao vetorial deve encontrar ancoras; a montagem final do contexto deve ser controlada por politicas previsiveis.
- A geracao com LLM deve ampliar pesquisa, estudo e desenvolvimento sem romper o vinculo com fontes reais.
- O analytics local deve ajudar o usuario a entender qualidade, falhas, uso de contexto e comportamento de agentes sem enviar dados externos por padrao.
- O desenvolvimento do projeto deve ser guiado por uma base de conhecimento propria, com fontes confiaveis e ativacao contextual.
- O acesso a colecoes, documentos, agentes, analytics e configuracoes deve ser sempre escopado por workspace e permissao.
- O MVP deve focar em PDF, mas o modelo deve permitir expansao natural para novas fontes e conectores.
- A interface deve ser minimalista, densa e legivel, usando cor como sinal semantico e nao como decoracao excessiva.
