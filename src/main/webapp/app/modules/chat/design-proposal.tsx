import './design-chat.scss';

import React, { useEffect, useMemo, useState } from 'react';
import { Alert, Button, Spinner } from 'react-bootstrap';
import { Link, useParams } from 'react-router';

import { DesignProposal, getDesignProposal } from 'app/shared/api/design-chat-api';

const formatDate = (value?: string) => {
  if (!value) {
    return 'Sin fecha';
  }
  return new Intl.DateTimeFormat('es-MX', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value));
};

const formatProjectType = (projectType?: string) => {
  if (projectType === 'KITCHEN') {
    return 'Cocina';
  }
  if (projectType === 'CLOSET') {
    return 'Closet';
  }
  if (projectType === 'BOTH') {
    return 'Cocina y closet';
  }
  return projectType ?? 'Proyecto';
};

const DesignProposalPage = () => {
  const { sessionCode = '' } = useParams();
  const [proposal, setProposal] = useState<DesignProposal | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!sessionCode) {
      setError('La liga de propuesta no es válida.');
      setIsLoading(false);
      return;
    }

    getDesignProposal(sessionCode)
      .then(setProposal)
      .catch(() => setError('No se encontró la propuesta de diseño.'))
      .finally(() => setIsLoading(false));
  }, [sessionCode]);

  const shareUrl = useMemo(() => window.location.href, []);
  const whatsappUrl = useMemo(() => {
    const text = proposal
      ? `Propuesta de diseño ${proposal.sessionCode} - ${formatProjectType(proposal.projectType)}: ${shareUrl}`
      : shareUrl;
    return `https://wa.me/?text=${encodeURIComponent(text)}`;
  }, [proposal, shareUrl]);

  if (isLoading) {
    return (
      <main className="design-proposal">
        <div className="design-proposal__empty">
          <Spinner size="sm" /> Cargando propuesta...
        </div>
      </main>
    );
  }

  if (error || !proposal) {
    return (
      <main className="design-proposal">
        <Alert variant="danger">{error}</Alert>
        <Button as={Link as any} to="/design-chat">
          Iniciar diseño
        </Button>
      </main>
    );
  }

  return (
    <main className="design-proposal">
      <header className="design-proposal__hero">
        <div>
          <p className="design-proposal__eyebrow">Propuesta de diseño</p>
          <h1>
            {formatProjectType(proposal.projectType)} {proposal.sessionCode}
          </h1>
          <p className="design-proposal__meta">
            Cliente: {proposal.clientName} · Actualizada {formatDate(proposal.updatedAt)}
          </p>
        </div>
        <div className="design-proposal__actions">
          <Button type="button" variant="outline-secondary" onClick={() => window.print()}>
            Descargar PDF
          </Button>
          <Button as="a" href={whatsappUrl} target="_blank" rel="noreferrer" variant="success">
            Compartir WhatsApp
          </Button>
        </div>
      </header>

      <section className="design-proposal__render" aria-label="Concepto visual generado">
        {proposal.renderBadge ? <span className="design-chat__image-badge">{proposal.renderBadge}</span> : null}
        {proposal.renderImageUrl ? (
          <img src={proposal.renderImageUrl} alt={`Concepto visual ${proposal.sessionCode}`} />
        ) : (
          <div className="design-proposal__empty">Aún no hay concepto visual generado.</div>
        )}
      </section>

      <section className="design-proposal__details" aria-label="Resumen de propuesta">
        <article>
          <h2>Resumen de diseño</h2>
          <p>{proposal.specsSummary ?? 'Resumen pendiente de confirmar.'}</p>
        </article>
        <aside>
          <h2>Datos clave</h2>
          <dl>
            <div>
              <dt>Estilo</dt>
              <dd>{proposal.selectedStyle ?? 'Por definir'}</dd>
            </div>
            <div>
              <dt>Gabinetes</dt>
              <dd>{proposal.cabinetCount}</dd>
            </div>
            <div>
              <dt>Estado</dt>
              <dd>{proposal.status}</dd>
            </div>
          </dl>
        </aside>
      </section>

      <section className="design-proposal__next">
        <h2>Siguientes pasos</h2>
        <ol>
          <li>Validar medidas finales en sitio.</li>
          <li>Confirmar materiales, cubierta, herrajes y acabado.</li>
          <li>Preparar cotización y archivos de fabricación.</li>
        </ol>
      </section>
    </main>
  );
};

export default DesignProposalPage;
