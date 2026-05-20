import './design-chat.scss';

import React, { FormEvent, useEffect, useMemo, useState } from 'react';
import { Alert, Button, Form, Spinner } from 'react-bootstrap';
import { Link, useParams } from 'react-router';

import {
  MeasuredKitchenLayout,
  MeasuredLayout,
  MeasuredWallSegment,
  getMeasuredLayout,
  saveMeasuredLayout,
} from 'app/shared/api/design-chat-api';

const layoutOptions: { value: MeasuredKitchenLayout; label: string; wallCount: number }[] = [
  { value: 'LINEAR', label: 'Lineal', wallCount: 1 },
  { value: 'L_SHAPE', label: 'En L', wallCount: 2 },
  { value: 'U_SHAPE', label: 'En U', wallCount: 3 },
  { value: 'ISLAND', label: 'Con isla', wallCount: 1 },
];

const wallCodeForIndex = (index: number) => String.fromCharCode(65 + index);
const mmToCm = (value?: number | null) => (value ? Math.round(value / 10) : '');
const cmToMm = (value: string) => Math.round(Number(value || 0) * 10);

const buildWalls = (layout: MeasuredKitchenLayout, roomHeightMm = 2400, existingWalls: MeasuredWallSegment[] = []) => {
  const wallCount = layoutOptions.find(option => option.value === layout)?.wallCount ?? 1;
  return Array.from({ length: wallCount }, (_, index) => {
    const existingWall = existingWalls[index];
    return {
      wallCode: existingWall?.wallCode ?? wallCodeForIndex(index),
      lengthMm: existingWall?.lengthMm ?? 3000,
      heightMm: existingWall?.heightMm ?? roomHeightMm,
      angleDeg: existingWall?.angleDeg ?? (layout === 'L_SHAPE' && index === 1 ? 90 : layout === 'U_SHAPE' && index === 2 ? 180 : 0),
      startXMm: existingWall?.startXMm ?? null,
      startYMm: existingWall?.startYMm ?? null,
      sortOrder: index + 1,
    };
  });
};

const buildDefaultLayout = (sessionId: number): MeasuredLayout => ({
  sessionId,
  layout: 'L_SHAPE',
  roomHeightMm: 2400,
  defaultBaseDepthMm: 600,
  defaultUpperDepthMm: 350,
  walls: buildWalls('L_SHAPE'),
  zones: [],
  obstacles: [],
  notes: '',
});

const buildPath = (layout: MeasuredKitchenLayout, walls: MeasuredWallSegment[]) => {
  const lengths = walls.map(wall => Math.max(40, Math.min(240, wall.lengthMm / 20)));
  if (layout === 'LINEAR' || layout === 'ISLAND') {
    return `M 36 128 H ${36 + (lengths[0] ?? 150)}`;
  }
  if (layout === 'L_SHAPE') {
    return `M 42 56 V 180 H ${42 + (lengths[0] ?? 150)}`;
  }
  return `M 42 56 V 180 H ${42 + (lengths[1] ?? 150)} V 56`;
};

const MeasuredLayoutPage = () => {
  const { sessionId: sessionIdParam = '' } = useParams();
  const sessionId = Number(sessionIdParam);
  const [layout, setLayout] = useState<MeasuredLayout | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [savedMessage, setSavedMessage] = useState<string | null>(null);

  useEffect(() => {
    if (!sessionId) {
      setError('La sesión de diseño no es válida.');
      setIsLoading(false);
      return;
    }

    getMeasuredLayout(sessionId)
      .then(savedLayout => setLayout(savedLayout))
      .catch(() => setLayout(buildDefaultLayout(sessionId)))
      .finally(() => setIsLoading(false));
  }, [sessionId]);

  const previewPath = useMemo(() => (layout ? buildPath(layout.layout, layout.walls) : ''), [layout]);

  const updateLayoutType = (nextLayout: MeasuredKitchenLayout) => {
    setLayout(currentLayout =>
      currentLayout
        ? {
            ...currentLayout,
            layout: nextLayout,
            walls: buildWalls(nextLayout, currentLayout.roomHeightMm, currentLayout.walls),
          }
        : currentLayout,
    );
  };

  const updateRoomHeight = (heightCm: string) => {
    const roomHeightMm = cmToMm(heightCm);
    setLayout(currentLayout =>
      currentLayout
        ? {
            ...currentLayout,
            roomHeightMm,
            walls: currentLayout.walls.map(wall => ({ ...wall, heightMm: roomHeightMm })),
          }
        : currentLayout,
    );
  };

  const updateWallLength = (index: number, lengthCm: string) => {
    setLayout(currentLayout =>
      currentLayout
        ? {
            ...currentLayout,
            walls: currentLayout.walls.map((wall, wallIndex) =>
              wallIndex === index ? { ...wall, lengthMm: cmToMm(lengthCm), sortOrder: wallIndex + 1 } : wall,
            ),
          }
        : currentLayout,
    );
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (!layout) {
      return;
    }

    setError(null);
    setSavedMessage(null);
    setIsSaving(true);
    try {
      const savedLayout = await saveMeasuredLayout(sessionId, layout);
      setLayout(savedLayout);
      setSavedMessage('Layout medido guardado.');
    } catch {
      setError('No se pudo guardar el layout medido. Revisa las medidas e intenta de nuevo.');
    } finally {
      setIsSaving(false);
    }
  };

  if (isLoading) {
    return (
      <main className="measured-layout">
        <div className="measured-layout__empty">
          <Spinner size="sm" /> Cargando layout...
        </div>
      </main>
    );
  }

  if (!layout) {
    return (
      <main className="measured-layout">
        <Alert variant="danger">{error}</Alert>
        <Button as={Link as any} to="/design-sessions">
          Volver a sesiones
        </Button>
      </main>
    );
  }

  return (
    <main className="measured-layout">
      <header className="measured-layout__header">
        <div>
          <p className="design-chat__meta mb-1">Layout medido</p>
          <h1 className="h3 mb-0">Captura de espacio</h1>
        </div>
        <Button as={Link as any} to="/design-sessions" variant="outline-secondary">
          Sesiones
        </Button>
      </header>

      {error ? <Alert variant="danger">{error}</Alert> : null}
      {savedMessage ? <Alert variant="success">{savedMessage}</Alert> : null}

      <div className="measured-layout__shell">
        <Form className="measured-layout__form" onSubmit={handleSubmit}>
          <Form.Group className="mb-3" controlId="layoutType">
            <Form.Label>Tipo de layout</Form.Label>
            <Form.Select value={layout.layout} onChange={event => updateLayoutType(event.target.value as MeasuredKitchenLayout)}>
              {layoutOptions.map(option => (
                <option value={option.value} key={option.value}>
                  {option.label}
                </option>
              ))}
            </Form.Select>
          </Form.Group>

          <Form.Group className="mb-3" controlId="roomHeightCm">
            <Form.Label>Altura del espacio (cm)</Form.Label>
            <Form.Control
              min={1}
              type="number"
              value={mmToCm(layout.roomHeightMm)}
              onChange={event => updateRoomHeight(event.target.value)}
              required
            />
          </Form.Group>

          <div className="measured-layout__wall-list">
            {layout.walls.map((wall, index) => (
              <Form.Group controlId={`wall-${wall.wallCode}`} key={wall.wallCode}>
                <Form.Label>Pared {wall.wallCode} largo (cm)</Form.Label>
                <Form.Control
                  min={1}
                  type="number"
                  value={mmToCm(wall.lengthMm)}
                  onChange={event => updateWallLength(index, event.target.value)}
                  required
                />
              </Form.Group>
            ))}
          </div>

          <Form.Group className="mt-3" controlId="layoutNotes">
            <Form.Label>Notas</Form.Label>
            <Form.Control
              as="textarea"
              maxLength={1000}
              rows={3}
              value={layout.notes ?? ''}
              onChange={event =>
                setLayout(currentLayout => (currentLayout ? { ...currentLayout, notes: event.target.value } : currentLayout))
              }
            />
          </Form.Group>

          <Button className="mt-3" type="submit" disabled={isSaving}>
            {isSaving ? <Spinner size="sm" /> : 'Guardar layout'}
          </Button>
        </Form>

        <section className="measured-layout__preview" aria-label="Vista previa 2D del layout">
          <svg viewBox="0 0 320 240" role="img" aria-label="Preview de layout medido">
            <path d={previewPath} fill="none" stroke="#0f3d44" strokeLinecap="round" strokeLinejoin="round" strokeWidth="18" />
            {layout.layout === 'ISLAND' ? <rect x="138" y="98" width="62" height="44" rx="4" fill="#8aa6a9" /> : null}
          </svg>
          <dl>
            <div>
              <dt>Layout</dt>
              <dd>{layoutOptions.find(option => option.value === layout.layout)?.label}</dd>
            </div>
            <div>
              <dt>Paredes</dt>
              <dd>{layout.walls.length}</dd>
            </div>
            <div>
              <dt>Altura</dt>
              <dd>{mmToCm(layout.roomHeightMm)} cm</dd>
            </div>
          </dl>
        </section>
      </div>
    </main>
  );
};

export default MeasuredLayoutPage;
