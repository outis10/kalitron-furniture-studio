import './design-chat.scss';

import React, { useEffect, useState } from 'react';
import { Alert, Button, Spinner } from 'react-bootstrap';
import { Link } from 'react-router';

import { ChatSessionSummary, listChatSessions } from 'app/shared/api/design-chat-api';

const formatDate = (value?: string) => {
  if (!value) {
    return 'Sin fecha';
  }

  return new Intl.DateTimeFormat('es-MX', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value));
};

const formatProjectType = (projectType: string) => {
  if (projectType === 'KITCHEN') {
    return 'Cocina';
  }
  if (projectType === 'CLOSET') {
    return 'Closet';
  }
  if (projectType === 'BOTH') {
    return 'Cocina y closet';
  }
  return projectType;
};

const formatStatus = (status: string) =>
  status
    .toLowerCase()
    .split('_')
    .map(part => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ');

const DesignSessions = () => {
  const [sessions, setSessions] = useState<ChatSessionSummary[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    listChatSessions()
      .then(setSessions)
      .catch(() => setError('No se pudieron cargar tus sesiones. Intenta nuevamente.'))
      .finally(() => setIsLoading(false));
  }, []);

  return (
    <main className="design-sessions">
      <header className="design-sessions__header">
        <div>
          <h1 className="h3 mb-2">Mis sesiones</h1>
          <p className="design-sessions__meta">Continúa conversaciones de diseño y revisa conceptos generados.</p>
        </div>
        <Button as={Link as any} to="/design-chat?new=true">
          Nueva sesión
        </Button>
      </header>

      {error ? <Alert variant="danger">{error}</Alert> : null}

      {isLoading ? (
        <div className="design-sessions__empty">
          <Spinner size="sm" /> Cargando sesiones...
        </div>
      ) : sessions.length === 0 ? (
        <section className="design-sessions__empty">
          <h2 className="h5">Todavía no hay sesiones</h2>
          <p className="design-sessions__meta">Crea una sesión para empezar a diseñar con IA.</p>
          <Button as={Link as any} to="/design-chat?new=true">
            Iniciar diseño
          </Button>
        </section>
      ) : (
        <section className="design-sessions__grid" aria-label="Sesiones de diseño">
          {sessions.map(session => (
            <article className="design-sessions__card" key={session.sessionId}>
              <div className="design-sessions__card-header">
                <div>
                  <h2 className="design-sessions__code">{session.sessionCode}</h2>
                  <p className="design-sessions__meta">{session.clientName}</p>
                </div>
                <span className="design-sessions__status">{formatStatus(session.status)}</span>
              </div>
              <div>
                <p className="design-sessions__meta mb-1">Proyecto</p>
                <strong>{formatProjectType(session.projectType)}</strong>
              </div>
              <div>
                <p className="design-sessions__meta mb-1">Última actualización</p>
                <strong>{formatDate(session.updatedAt)}</strong>
              </div>
              <p className="design-sessions__meta mb-0">
                {session.generatedConceptCount === 1 ? '1 concepto generado' : `${session.generatedConceptCount} conceptos generados`}
              </p>
              <Button as={Link as any} to={`/design-chat?sessionCode=${encodeURIComponent(session.sessionCode)}`} variant="outline-primary">
                Reabrir chat
              </Button>
              <Button as={Link as any} to={`/design-layout/${session.sessionId}`} variant="outline-primary">
                Layout medido
              </Button>
              <Button as={Link as any} to={`/proposal/${encodeURIComponent(session.sessionCode)}`} variant="outline-secondary">
                Ver propuesta
              </Button>
            </article>
          ))}
        </section>
      )}
    </main>
  );
};

export default DesignSessions;
