import axios from 'axios';

// E12 #116 — site measurer assignment.

export interface MeasurerSummary {
  login: string;
  firstName?: string | null;
  lastName?: string | null;
}

export interface MeasurerAssignment {
  sessionId: number;
  assignedMeasurer: MeasurerSummary | null;
}

export const listAssignableMeasurers = async (): Promise<MeasurerSummary[]> => {
  const response = await axios.get<MeasurerSummary[]>('api/measurers');
  return response.data;
};

export const getAssignedMeasurerLogin = async (sessionId: number): Promise<string | null> => {
  const response = await axios.get<{ assignedMeasurer?: { login?: string } | null }>(`api/design-sessions/${sessionId}`);
  return response.data.assignedMeasurer?.login ?? null;
};

export const assignMeasurer = async (sessionId: number, userLogin: string | null): Promise<MeasurerAssignment> => {
  const response = await axios.put<MeasurerAssignment>(`api/design-sessions/${sessionId}/assigned-measurer`, { userLogin });
  return response.data;
};

export const measurerDisplayName = (measurer: MeasurerSummary) =>
  [measurer.firstName, measurer.lastName].filter(Boolean).join(' ') || measurer.login;
