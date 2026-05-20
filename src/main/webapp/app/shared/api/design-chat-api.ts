import axios from 'axios';

import { ICatalogStyle } from 'app/shared/model/catalog-style.model';

export type ChatRole = 'USER' | 'ASSISTANT' | 'SYSTEM';

export interface ChatMessageView {
  role: ChatRole;
  content: string;
  createdAt: string;
  imagePreviewUrl?: string;
  imageFileName?: string;
  imageBadge?: string;
}

export interface ChatSession {
  sessionId: number;
  sessionCode: string;
  clientName: string;
  clientEmail: string;
  selectedStyle?: string | null;
  projectType: string;
  status: string;
  messages: ChatMessageView[];
}

export interface ChatSessionSummary {
  sessionId: number;
  sessionCode: string;
  clientName: string;
  projectType: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  generatedConceptCount: number;
}

export interface DesignProposal {
  sessionId: number;
  sessionCode: string;
  clientName: string;
  projectType: string;
  status: string;
  selectedStyle?: string | null;
  updatedAt: string;
  renderImageUrl?: string | null;
  renderBadge?: string | null;
  specsSummary?: string | null;
  cabinetCount: number;
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
  selectedStyle?: string | null;
}

export const startChatSession = async (request: StartChatSessionRequest): Promise<ChatSession> => {
  const response = await axios.post<ChatSession>('/api/chat/sessions', request);
  return response.data;
};

export const resumeChatSession = async (sessionCode: string): Promise<ChatSession> => {
  const response = await axios.get<ChatSession>(`/api/chat/sessions/${encodeURIComponent(sessionCode)}`);
  return response.data;
};

export const listChatSessions = async (): Promise<ChatSessionSummary[]> => {
  const response = await axios.get<ChatSessionSummary[]>('/api/chat/sessions');
  return response.data;
};

export const getDesignProposal = async (sessionCode: string): Promise<DesignProposal> => {
  const response = await axios.get<DesignProposal>(`/api/public/proposals/${encodeURIComponent(sessionCode)}`);
  return response.data;
};

export interface SendChatImage {
  imageBase64: string;
  imageFileName: string;
  imageMimeType: string;
  imageSizeBytes: number;
}

export const sendChatMessage = async (
  sessionId: number,
  message: string,
  selectedStyle?: string | null,
  image?: SendChatImage | null,
): Promise<ChatResponse> => {
  const response = await axios.post<ChatResponse>('/api/chat/message', { sessionId, message, selectedStyle, ...image });
  return response.data;
};

export const getCatalogStyles = async (): Promise<ICatalogStyle[]> => {
  const response = await axios.get<ICatalogStyle[]>('/api/catalog-styles');
  return response.data;
};

export interface VisualConceptRequest {
  sessionId: number;
  style?: string | null;
  layout?: string | null;
  finish?: string | null;
  clientImageBase64?: string | null;
  visualInstructions?: string | null;
}

export interface VisualConceptResponse {
  sessionId: number;
  sessionCode: string;
  imageUrl: string;
  promptUsed?: string | null;
  pipeline: string;
  badge: string;
}

export const generateVisualConcept = async (request: VisualConceptRequest): Promise<VisualConceptResponse> => {
  const response = await axios.post<VisualConceptResponse>('/api/chat/visual-concept', request);
  return response.data;
};
