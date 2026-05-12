import axios from 'axios';

export type ChatRole = 'USER' | 'ASSISTANT' | 'SYSTEM';

export interface ChatMessageView {
  role: ChatRole;
  content: string;
  createdAt: string;
}

export interface ChatSession {
  sessionId: number;
  sessionCode: string;
  clientName: string;
  clientEmail: string;
  projectType: string;
  status: string;
  messages: ChatMessageView[];
}

export interface ChatResponse {
  sessionId: number;
  sessionCode: string;
  reply: string;
  specsReady: boolean;
  specsSummary?: Record<string, unknown> | null;
}

export interface StartChatSessionRequest {
  clientName: string;
  clientEmail: string;
}

export const startChatSession = async (request: StartChatSessionRequest): Promise<ChatSession> => {
  const response = await axios.post<ChatSession>('/api/chat/sessions', request);
  return response.data;
};

export const resumeChatSession = async (sessionCode: string): Promise<ChatSession> => {
  const response = await axios.get<ChatSession>(`/api/chat/sessions/${encodeURIComponent(sessionCode)}`);
  return response.data;
};

export const sendChatMessage = async (sessionId: number, message: string): Promise<ChatResponse> => {
  const response = await axios.post<ChatResponse>('/api/chat/message', { sessionId, message });
  return response.data;
};
