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

export type SketchConfidence = 'HIGH' | 'MEDIUM' | 'LOW' | 'MISSING';

export interface SketchField<T> {
  value?: T | null;
  confidence?: SketchConfidence | null;
  sourceText?: string | null;
}

export interface SketchMeasurement {
  value?: number | null;
  unit?: string | null;
  confidence?: SketchConfidence | null;
  sourceText?: string | null;
}

export interface SketchWallCandidate {
  wallCode?: SketchField<string> | null;
  length?: SketchMeasurement | null;
  height?: SketchMeasurement | null;
  angleDeg?: SketchField<number> | null;
}

export interface SketchZoneCandidate {
  zoneCode?: SketchField<string> | null;
  zoneType?: SketchField<string> | null;
  wallCode?: SketchField<string> | null;
  x?: SketchMeasurement | null;
  width?: SketchMeasurement | null;
}

export interface SketchObstacleCandidate {
  obstacleType?: SketchField<string> | null;
  label?: SketchField<string> | null;
  wallCode?: SketchField<string> | null;
  x?: SketchMeasurement | null;
  width?: SketchMeasurement | null;
}

export interface SketchCabinetCandidate {
  candidateCode?: string | null;
  category?: SketchField<string> | null;
  label?: SketchField<string> | null;
  wallCode?: SketchField<string> | null;
  x?: SketchMeasurement | null;
  width?: SketchMeasurement | null;
  height?: SketchMeasurement | null;
  depth?: SketchMeasurement | null;
  doors?: SketchField<number> | null;
  drawers?: SketchField<number> | null;
}

export interface SketchMissingInfo {
  code?: string | null;
  message?: string | null;
  severity?: string | null;
}

export interface SketchExtractionResponse {
  schemaVersion: string;
  requestId?: string | null;
  sessionId: number;
  sessionCode: string;
  projectType?: SketchField<string> | null;
  layout?: SketchField<string> | null;
  unit?: SketchField<string> | null;
  walls?: SketchWallCandidate[] | null;
  zones?: SketchZoneCandidate[] | null;
  obstacles?: SketchObstacleCandidate[] | null;
  cabinetCandidates?: SketchCabinetCandidate[] | null;
  missingInfo?: SketchMissingInfo[] | null;
  questions?: string[] | null;
  warnings?: string[] | null;
  rawExtraction?: Record<string, unknown> | null;
}

export interface SketchAnalysisRequest extends SendChatImage {
  sessionId: number;
  projectTypeHint?: string | null;
  unitHint?: string | null;
  userPrompt?: string | null;
}

export const analyzeSketch = async (request: SketchAnalysisRequest): Promise<SketchExtractionResponse> => {
  const response = await axios.post<SketchExtractionResponse>('/api/chat/sketch-analysis', request);
  return response.data;
};

export type MeasuredKitchenLayout = 'LINEAR' | 'L_SHAPE' | 'U_SHAPE' | 'ISLAND';

export interface MeasuredWallSegment {
  wallCode: string;
  lengthMm: number;
  heightMm?: number | null;
  angleDeg?: number | null;
  startXMm?: number | null;
  startYMm?: number | null;
  sortOrder?: number | null;
}

export type LayoutObstacleType = 'WINDOW' | 'DOOR' | 'COLUMN' | 'OUTLET' | 'WATER' | 'GAS' | 'DRAIN' | 'RANGE_HOOD' | 'APPLIANCE' | 'OTHER';

export interface LayoutZone {
  zoneCode: string;
  zoneType: string;
  wallCode: string;
  xMm: number;
  yMm?: number | null;
  zMm?: number | null;
  widthMm?: number | null;
  heightMm?: number | null;
  depthMm?: number | null;
  clearanceLeftMm?: number | null;
  clearanceRightMm?: number | null;
  notes?: string | null;
}

export interface LayoutObstacle {
  obstacleType: LayoutObstacleType;
  label?: string | null;
  wallCode: string;
  xMm: number;
  yMm?: number | null;
  zMm?: number | null;
  widthMm?: number | null;
  heightMm?: number | null;
  depthMm?: number | null;
  notes?: string | null;
}

export interface MeasuredLayout {
  sessionId: number;
  layout: MeasuredKitchenLayout;
  roomHeightMm: number;
  defaultBaseDepthMm?: number | null;
  defaultUpperDepthMm?: number | null;
  walls: MeasuredWallSegment[];
  zones?: LayoutZone[];
  obstacles?: LayoutObstacle[];
  notes?: string | null;
}

export interface CabinetPlanItem {
  cabinetCode?: string | null;
  templateCode?: string | null;
  category: CabinetCategory;
  label: string;
  widthMm: number;
  heightMm: number;
  depthMm: number;
  doors?: number | null;
  drawers?: number | null;
  shelves?: number | null;
  finish?: string | null;
  wallCode: string;
  xMm: number;
  yMm: number;
  zMm: number;
  rotationDeg?: number | null;
  positionSeq?: number | null;
  materialCode?: string | null;
  notes?: string | null;
}

export type CabinetCategory = 'UPPER' | 'LOWER' | 'CORNER' | 'TALL' | 'SINK' | 'ISLAND' | 'DRAWER_BASE' | 'APPLIANCE' | 'FILLER' | 'PANEL';

export interface CabinetPlanValidationMessage {
  severity: 'INFO' | 'WARNING' | 'ERROR';
  code: string;
  message: string;
  wallCode?: string | null;
  cabinetCode?: string | null;
}

export interface CabinetPlan {
  sessionId: number;
  sessionCode: string;
  layout: MeasuredKitchenLayout;
  valid: boolean;
  cabinetCount: number;
  totalOccupiedLengthMm?: number | null;
  cabinets: CabinetPlanItem[];
  validationMessages: CabinetPlanValidationMessage[];
}

export const getMeasuredLayout = async (sessionId: number): Promise<MeasuredLayout> => {
  const response = await axios.get<MeasuredLayout>(`/api/design-sessions/${sessionId}/measured-layout`);
  return response.data;
};

export const saveMeasuredLayout = async (sessionId: number, layout: MeasuredLayout): Promise<MeasuredLayout> => {
  const response = await axios.put<MeasuredLayout>(`/api/design-sessions/${sessionId}/measured-layout`, layout);
  return response.data;
};

export const getCabinetPlan = async (sessionId: number): Promise<CabinetPlan> => {
  const response = await axios.get<CabinetPlan>(`/api/design-sessions/${sessionId}/cabinet-plan`);
  return response.data;
};

export const generateCabinetPlan = async (sessionId: number): Promise<CabinetPlan> => {
  const response = await axios.post<CabinetPlan>(`/api/design-sessions/${sessionId}/cabinet-plan`);
  return response.data;
};

export const saveCabinetPlan = async (sessionId: number, cabinetPlan: CabinetPlan): Promise<CabinetPlan> => {
  const response = await axios.put<CabinetPlan>(`/api/design-sessions/${sessionId}/cabinet-plan`, cabinetPlan);
  return response.data;
};
