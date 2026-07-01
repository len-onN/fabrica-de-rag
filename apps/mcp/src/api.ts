import axios from 'axios';

const API_URL = process.env.RAG_API_URL || 'http://localhost:8080/api/v1';
const API_KEY = process.env.RAG_API_KEY;

if (!API_KEY) {
    console.error("Missing RAG_API_KEY environment variable. MCP server cannot authenticate.");
}

const client = axios.create({
    baseURL: API_URL,
    headers: {
        'Authorization': `Bearer ${API_KEY}`,
        'Content-Type': 'application/json'
    }
});

/**
 * Registra eventos de analytics assincronamente (fire-and-forget).
 */
export async function trackEvent(workspaceId: string, eventName: string, properties: any, error?: Error) {
    try {
        const payload = {
            eventVersion: "v1",
            eventName: eventName,
            origin: "mcp",
            retentionClass: "ANALYTICS",
            actor: {
                type: "AGENT",
                id: "mcp-server" // can be derived from identity later
            },
            correlationId: properties.correlationId || "mcp-" + Date.now(),
            occurredAt: new Date().toISOString(),
            properties: {
                ...properties,
                error: error ? error.message : undefined
            }
        };
        // F&F
        client.post(`/workspaces/${workspaceId}/analytics/events`, payload).catch(() => {});
    } catch (e) {
        // Ignore analytics failure
    }
}

export async function listWorkspaces() {
    // Calling the workspace endpoint
    const response = await client.get('/workspaces');
    return response.data;
}

export async function searchChunks(workspaceId: string, collectionId: string | undefined, query: string, topK: number, filters: any) {
    const payload = {
        collectionId,
        query,
        topK,
        filters
    };
    const response = await client.post(`/workspaces/${workspaceId}/rag/search`, payload);
    return response.data;
}

export async function getChunk(workspaceId: string, collectionId: string, chunkId: string) {
    const response = await client.get(`/workspaces/${workspaceId}/collections/${collectionId}/chunks/${chunkId}`);
    return response.data;
}

export async function expandContext(workspaceId: string, collectionId: string | undefined, query: string, topK: number, tokenBudget: number) {
    const payload = {
        collectionId,
        query,
        topK,
        tokenBudget,
        policy: "conservative" // standard policy for context expansion
    };
    const response = await client.post(`/workspaces/${workspaceId}/rag/context`, payload);
    return response.data;
}

export async function askRag(workspaceId: string, collectionId: string | undefined, query: string, topK: number, policy: string) {
    const payload = {
        collectionId,
        query,
        topK,
        policy,
        tokenBudget: 5000
    };
    const response = await client.post(`/workspaces/${workspaceId}/rag/ask`, payload);
    return response.data;
}
