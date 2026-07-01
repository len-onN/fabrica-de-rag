import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
  ErrorCode,
  McpError,
} from "@modelcontextprotocol/sdk/types.js";
import { z } from "zod";
import { zodToJsonSchema } from "zod-to-json-schema";
import {
  listWorkspaces,
  searchChunks,
  getChunk,
  expandContext,
  askRag,
  trackEvent,
} from "./api";

const server = new Server(
  {
    name: "rag-creator-mcp",
    version: "1.0.0",
  },
  {
    capabilities: {
      tools: {},
    },
  }
);

// Define tool schemas
const listWorkspacesSchema = z.object({});

const searchChunksSchema = z.object({
  workspaceId: z.string().describe("ID do workspace"),
  collectionId: z.string().optional().describe("ID da coleção (opcional)"),
  query: z.string().describe("Busca semântica ou palavra-chave"),
  topK: z.number().max(12).default(8).describe("Número máximo de resultados"),
  filters: z.any().optional().describe("Filtros extras em JSON"),
});

const getChunkSchema = z.object({
  workspaceId: z.string().describe("ID do workspace"),
  collectionId: z.string().describe("ID da coleção"),
  chunkId: z.string().describe("ID do chunk"),
});

const expandContextSchema = z.object({
  workspaceId: z.string().describe("ID do workspace"),
  collectionId: z.string().optional().describe("ID da coleção (opcional)"),
  query: z.string().describe("Busca original que encontrou este contexto"),
  topK: z.number().max(12).default(8),
  tokenBudget: z.number().max(6000).default(5000),
});

const askRagSchema = z.object({
  workspaceId: z.string().describe("ID do workspace"),
  collectionId: z.string().optional().describe("ID da coleção (opcional)"),
  query: z.string().describe("Pergunta do usuário"),
  topK: z.number().max(12).default(8),
  policy: z.enum(["grounded_answer_policy_v1"]).default("grounded_answer_policy_v1"),
});

// Helper to convert Zod schema to JSON schema for MCP
function getJsonSchema(schema: any) {
  const jsonSchema = zodToJsonSchema(schema) as any;
  // MCP SDK expects shape without the $schema property
  delete jsonSchema.$schema;
  return jsonSchema;
}

server.setRequestHandler(ListToolsRequestSchema, async () => {
  return {
    tools: [
      {
        name: "list_workspaces",
        description: "Lista os workspaces aos quais este agente tem acesso.",
        inputSchema: getJsonSchema(listWorkspacesSchema),
      },
      {
        name: "search_chunks",
        description: "Busca fragmentos de conhecimento baseados em busca vetorial e similaridade semântica.",
        inputSchema: getJsonSchema(searchChunksSchema),
      },
      {
        name: "get_chunk",
        description: "Inspeciona todo o contexto visual, de tabelas ou de texto contido num chunk de conhecimento.",
        inputSchema: getJsonSchema(getChunkSchema),
      },
      {
        name: "expand_context",
        description: "Busca contexto amplo contendo o texto completo que rodeia o contexto referenciado num chunk ou que respeite os budgets do RAG.",
        inputSchema: getJsonSchema(expandContextSchema),
      },
      {
        name: "ask_rag",
        description: "Gera uma resposta fundamentada em dados das coleções RAG do workspace e as respectivas citações comprobatórias.",
        inputSchema: getJsonSchema(askRagSchema),
      },
    ],
  };
});

server.setRequestHandler(CallToolRequestSchema, async (request) => {
  const name = request.params.name;
  const args = request.params.arguments || {};
  let result;
  const workspaceId = args.workspaceId as string;

  trackEvent(workspaceId || "system", "mcp_tool_invoked", { tool: name, args });

  try {
    switch (name) {
      case "list_workspaces":
        result = await listWorkspaces();
        break;
      case "search_chunks":
        const searchArgs = searchChunksSchema.parse(args);
        result = await searchChunks(
          searchArgs.workspaceId,
          searchArgs.collectionId,
          searchArgs.query,
          searchArgs.topK,
          searchArgs.filters
        );
        break;
      case "get_chunk":
        const getArgs = getChunkSchema.parse(args);
        result = await getChunk(
          getArgs.workspaceId,
          getArgs.collectionId,
          getArgs.chunkId
        );
        break;
      case "expand_context":
        const expArgs = expandContextSchema.parse(args);
        result = await expandContext(
          expArgs.workspaceId,
          expArgs.collectionId,
          expArgs.query,
          expArgs.topK,
          expArgs.tokenBudget
        );
        break;
      case "ask_rag":
        const askArgs = askRagSchema.parse(args);
        result = await askRag(
          askArgs.workspaceId,
          askArgs.collectionId,
          askArgs.query,
          askArgs.topK,
          askArgs.policy
        );
        break;
      default:
        throw new McpError(ErrorCode.MethodNotFound, `Unknown tool: ${name}`);
    }

    return {
      content: [
        {
          type: "text",
          text: JSON.stringify(result, null, 2),
        },
      ],
    };
  } catch (error: any) {
    trackEvent(workspaceId || "system", "mcp_tool_failed", { tool: name, error: error.message }, error);
    
    // Instead of throwing and breaking Stdio, we return the error back to the LLM nicely.
    return {
      content: [
        {
          type: "text",
          text: `Error executing tool ${name}: ${error.message}\n${error.response?.data ? JSON.stringify(error.response.data) : ''}`,
        },
      ],
      isError: true,
    };
  }
});

async function main() {
  const transport = new StdioServerTransport();
  await server.connect(transport);
  console.error("RagCreator MCP Server running on stdio");
}

main().catch((error) => {
  console.error("Server error:", error);
  process.exit(1);
});
