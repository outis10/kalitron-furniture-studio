import { useCallback, useEffect, useState } from 'react';

import { MeasurerSummary, assignMeasurer, getAssignedMeasurerLogin, listAssignableMeasurers } from 'app/shared/api/measurer-api';

export const useMeasurerAssignment = (sessionId: number) => {
  const [measurers, setMeasurers] = useState<MeasurerSummary[]>([]);
  const [assignedLogin, setAssignedLogin] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [savedMessage, setSavedMessage] = useState<string | null>(null);

  useEffect(() => {
    if (!sessionId) {
      return;
    }
    setIsLoading(true);
    Promise.all([listAssignableMeasurers(), getAssignedMeasurerLogin(sessionId)])
      .then(([list, login]) => {
        setMeasurers(list);
        setAssignedLogin(login);
        setError(null);
      })
      .catch(() => setError('No se pudo cargar el medidor asignado.'))
      .finally(() => setIsLoading(false));
  }, [sessionId]);

  const assign = useCallback(
    async (login: string | null) => {
      setIsSaving(true);
      setSavedMessage(null);
      try {
        const result = await assignMeasurer(sessionId, login);
        setAssignedLogin(result.assignedMeasurer?.login ?? null);
        setError(null);
        setSavedMessage(result.assignedMeasurer ? 'Medidor asignado.' : 'Sesión sin medidor asignado.');
      } catch {
        // keep the previous value on failure
        setError('No se pudo guardar el medidor asignado. Intenta nuevamente.');
      } finally {
        setIsSaving(false);
      }
    },
    [sessionId],
  );

  return { measurers, assignedLogin, isLoading, isSaving, error, savedMessage, assign };
};
