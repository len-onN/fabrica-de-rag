# Ideias e Evoluções Pós-MVP (Roadmap Futuro)

Este documento centraliza as funcionalidades, conectores e ideias arquiteturais que foram discutidos ou mapeados durante o design do sistema, mas que deliberadamente ficaram de fora do escopo do MVP inicial (Produto Mínimo Viável) para mantermos o foco na entrega de valor e simplicidade operacional.

Sempre que novas sugestões brilhantes (mas não bloqueantes para a versão 1.0) surgirem, elas devem ser centralizadas aqui.

## 1. Identidade, Segurança e Acesso
- **Autenticação OAuth2 / OIDC**: Integração com provedores externos de identidade (Google, Facebook, Microsoft/Azure AD, GitHub) para login corporativo e social, reduzindo o atrito do login puramente local.
- **Convites e Recuperação de Senha**: Implementar o disparo de e-mails via SMTP/Serviço Externo para envio de convites de participação em *Workspaces* e links de "Esqueci minha senha". (No MVP, resets são operacionais via banco e não há convites dinâmicos).
- **MFA (Autenticação Multifator)**: Suporte a TOTP/Tokens (Google Authenticator, Authy) para segurança adicional nas contas dos usuários.
- **Service Accounts Completas**: Gerenciamento de credenciais de máquina parrudas para sistemas externos consumirem a Fábrica de RAG, indo além das API Keys baseadas em capabilities planejadas para o MVP.
- **RLS (Row-Level Security)**: Hardening da base de dados PostgreSQL utilizando políticas RLS por *workspace* para isolamento arquitetural profundo.
- **Session Store Distribuída**: Migrar a leitura das sessões (Opaque Tokens) do banco PostgreSQL para um Redis/Memcached em um eventual cenário de alta disponibilidade.

## 2. Modelos, Ingestão e RAG
- **Busca Multimodal e Embeddings Visuais**: Suporte nativo para gerar embeddings diretamente de imagens e gráficos e buscar por similaridade visual pura (O MVP foca em extrair *texto derivado* das imagens via Vision LLM e buscar apenas esse texto).
- **Reranking Algorítmico**: Aplicação de *Cross-Encoders* para reordenar os resultados brutos retornados pelo Qdrant antes da montagem final do contexto do LLM, melhorando drasticamente a precisão em bases imensas.
- **Outras Fontes (Além do PDF)**: Suporte nativo para Word, Excel, Markdown, sites da web (scrapers), conectores de Notion, Jira e Confluence (veja também `docs/fontes-e-integracoes-futuras.md`).
- **Conectores de Vector Stores Externos**: Permitir plugar a Fábrica de RAG em bancos vetoriais gerenciados de terceiros, como Pinecone, Weaviate ou pgvector (veja `docs/conectores-vector-store.md`).

## 3. Integração com Agentes (MCP)
- **Ações Autônomas Mutantes (Auto-Agent)**: Permitir que agentes e clientes do *Model Context Protocol* executem configurações, exclusões de dados sensíveis ou modificações na base sem exigir aprovação humana (No MVP, ferramentas destrutivas exigem confirmação explícita na interface do usuário local).
- **MCP Server Remoto Seguro**: Exposição de conectores MCP via HTTP remoto obrigatoriamente sob TLS robusto e mTLS. O foco primário do MVP é stdio ou rede loopback.

## 4. Infraestrutura e Observabilidade
- **Storage Distribuído (Object Storage)**: Transição do armazenamento bruto de PDFs do *Filesystem* local da máquina para provedores S3 / MinIO (ideal para orquestração em Kubernetes no futuro).
- **Telemetria Remota Opcional (Opt-in)**: Capacidade de enviar agregações anônimas de métricas sobre qualidade do RAG para os mantenedores da Fábrica (O MVP possui o analytics estritamente contido e rodando localmente na máquina do cliente, sem comunicação para fora).
- **Audit Trail Imutável**: Um registro de auditoria completo para ações de *Compliance* exportável e protegido contra tampering.
