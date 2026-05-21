import './design-chat.scss';

import React, { FormEvent, useEffect, useMemo, useState } from 'react';
import { Alert, Button, Form, Spinner } from 'react-bootstrap';
import { Link, useParams } from 'react-router';

import {
  CabinetCategory,
  CabinetPlan,
  CabinetPlanItem,
  LayoutObstacle,
  LayoutObstacleType,
  LayoutZone,
  MeasuredKitchenLayout,
  MeasuredLayout,
  MeasuredWallSegment,
  generateCabinetPlan,
  getCabinetPlan,
  getMeasuredLayout,
  saveCabinetPlan,
  saveMeasuredLayout,
} from 'app/shared/api/design-chat-api';

const layoutOptions: { value: MeasuredKitchenLayout; label: string; wallCount: number }[] = [
  { value: 'LINEAR', label: 'Lineal', wallCount: 1 },
  { value: 'L_SHAPE', label: 'En L', wallCount: 2 },
  { value: 'U_SHAPE', label: 'En U', wallCount: 3 },
  { value: 'ISLAND', label: 'Con isla', wallCount: 1 },
];

const zoneOptions = [
  { value: 'SINK', label: 'Tarja' },
  { value: 'RANGE', label: 'Estufa' },
  { value: 'REFRIGERATOR', label: 'Refrigerador' },
  { value: 'DISHWASHER', label: 'Lavavajillas' },
  { value: 'PANTRY', label: 'Despensa' },
];

const obstacleOptions: { value: LayoutObstacleType; label: string }[] = [
  { value: 'WINDOW', label: 'Ventana' },
  { value: 'DOOR', label: 'Puerta' },
  { value: 'COLUMN', label: 'Columna' },
  { value: 'OUTLET', label: 'Contacto' },
  { value: 'WATER', label: 'Agua' },
  { value: 'GAS', label: 'Gas' },
  { value: 'DRAIN', label: 'Drenaje' },
  { value: 'RANGE_HOOD', label: 'Campana' },
  { value: 'APPLIANCE', label: 'Electrodoméstico' },
  { value: 'OTHER', label: 'Otro' },
];

const cabinetCategoryOptions: { value: CabinetCategory; label: string }[] = [
  { value: 'LOWER', label: 'Base puertas' },
  { value: 'DRAWER_BASE', label: 'Base cajones' },
  { value: 'SINK', label: 'Base tarja' },
  { value: 'APPLIANCE', label: 'Electrodoméstico' },
  { value: 'UPPER', label: 'Superior' },
  { value: 'TALL', label: 'Torre' },
  { value: 'FILLER', label: 'Relleno' },
  { value: 'PANEL', label: 'Panel' },
  { value: 'CORNER', label: 'Esquinero' },
  { value: 'ISLAND', label: 'Isla' },
];

const wallCodeForIndex = (index: number) => String.fromCharCode(65 + index);
const mmToCm = (value?: number | null) => (value ? Math.round(value / 10) : '');
const cmToMm = (value: string) => Math.round(Number(value || 0) * 10);
const clampPreviewX = (wall: MeasuredWallSegment | undefined, xMm?: number | null) => {
  if (!wall?.lengthMm) {
    return 0;
  }
  return Math.max(0, Math.min(1, (xMm ?? 0) / wall.lengthMm));
};

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

const previewPoint = (layout: MeasuredKitchenLayout, walls: MeasuredWallSegment[], wallCode: string, xMm?: number | null) => {
  const wallIndex = Math.max(
    0,
    walls.findIndex(wall => wall.wallCode === wallCode),
  );
  const wall = walls[wallIndex];
  const ratio = clampPreviewX(wall, xMm);
  if (layout === 'LINEAR' || layout === 'ISLAND') {
    return { x: 36 + ratio * 180, y: 128 };
  }
  if (layout === 'L_SHAPE') {
    return wallIndex === 0 ? { x: 42 + ratio * 180, y: 180 } : { x: 42, y: 180 - ratio * 124 };
  }
  if (wallIndex === 0) {
    return { x: 42, y: 180 - ratio * 124 };
  }
  if (wallIndex === 1) {
    return { x: 42 + ratio * 180, y: 180 };
  }
  return { x: 222, y: 180 - ratio * 124 };
};

const reflowCabinetPositions = (cabinets: CabinetPlanItem[]) => {
  const sortedCabinets = [...cabinets].sort(
    (first, second) =>
      first.wallCode.localeCompare(second.wallCode) || first.xMm - second.xMm || (first.positionSeq ?? 0) - (second.positionSeq ?? 0),
  );
  const wallCursors = new Map<string, number>();
  return sortedCabinets.map((cabinet, index) => {
    const xMm = wallCursors.get(cabinet.wallCode) ?? 0;
    wallCursors.set(cabinet.wallCode, xMm + cabinet.widthMm);
    return {
      ...cabinet,
      xMm,
      positionSeq: index + 1,
    };
  });
};

const MeasuredLayoutPage = () => {
  const { sessionId: sessionIdParam = '' } = useParams();
  const sessionId = Number(sessionIdParam);
  const [layout, setLayout] = useState<MeasuredLayout | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isGeneratingPlan, setIsGeneratingPlan] = useState(false);
  const [isSavingPlan, setIsSavingPlan] = useState(false);
  const [cabinetPlan, setCabinetPlan] = useState<CabinetPlan | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [savedMessage, setSavedMessage] = useState<string | null>(null);

  useEffect(() => {
    if (!sessionId) {
      setError('La sesión de diseño no es válida.');
      setIsLoading(false);
      return;
    }

    Promise.allSettled([getMeasuredLayout(sessionId), getCabinetPlan(sessionId)])
      .then(([layoutResult, planResult]) => {
        if (layoutResult.status === 'fulfilled') {
          setLayout(layoutResult.value);
        } else {
          setLayout(buildDefaultLayout(sessionId));
        }
        if (planResult.status === 'fulfilled') {
          setCabinetPlan(planResult.value);
        }
      })
      .catch(() => setLayout(buildDefaultLayout(sessionId)))
      .finally(() => setIsLoading(false));
  }, [sessionId]);

  const previewPath = useMemo(() => (layout ? buildPath(layout.layout, layout.walls) : ''), [layout]);
  const cabinetCount = cabinetPlan?.cabinets.length ?? 0;
  const cabinetTotalWidthMm = useMemo(
    () => cabinetPlan?.cabinets.reduce((total, cabinet) => total + cabinet.widthMm, 0) ?? 0,
    [cabinetPlan],
  );

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

  const addZone = () => {
    setLayout(currentLayout => {
      if (!currentLayout) {
        return currentLayout;
      }
      const wallCode = currentLayout.walls[0]?.wallCode ?? 'A';
      const nextIndex = (currentLayout.zones?.length ?? 0) + 1;
      const nextZone: LayoutZone = {
        zoneCode: `ZONE-${nextIndex}`,
        zoneType: 'SINK',
        wallCode,
        xMm: 0,
        widthMm: 800,
      };
      return { ...currentLayout, zones: [...(currentLayout.zones ?? []), nextZone] };
    });
  };

  const updateZone = (index: number, nextZone: Partial<LayoutZone>) => {
    setLayout(currentLayout =>
      currentLayout
        ? {
            ...currentLayout,
            zones: (currentLayout.zones ?? []).map((zone, zoneIndex) => (zoneIndex === index ? { ...zone, ...nextZone } : zone)),
          }
        : currentLayout,
    );
  };

  const removeZone = (index: number) => {
    setLayout(currentLayout =>
      currentLayout
        ? { ...currentLayout, zones: (currentLayout.zones ?? []).filter((_zone, zoneIndex) => zoneIndex !== index) }
        : currentLayout,
    );
  };

  const addObstacle = () => {
    setLayout(currentLayout => {
      if (!currentLayout) {
        return currentLayout;
      }
      const wallCode = currentLayout.walls[0]?.wallCode ?? 'A';
      const nextObstacle: LayoutObstacle = {
        obstacleType: 'WINDOW',
        label: 'Ventana',
        wallCode,
        xMm: 0,
        zMm: 1100,
        widthMm: 900,
        heightMm: 700,
      };
      return { ...currentLayout, obstacles: [...(currentLayout.obstacles ?? []), nextObstacle] };
    });
  };

  const updateObstacle = (index: number, nextObstacle: Partial<LayoutObstacle>) => {
    setLayout(currentLayout =>
      currentLayout
        ? {
            ...currentLayout,
            obstacles: (currentLayout.obstacles ?? []).map((obstacle, obstacleIndex) =>
              obstacleIndex === index ? { ...obstacle, ...nextObstacle } : obstacle,
            ),
          }
        : currentLayout,
    );
  };

  const removeObstacle = (index: number) => {
    setLayout(currentLayout =>
      currentLayout
        ? { ...currentLayout, obstacles: (currentLayout.obstacles ?? []).filter((_obstacle, obstacleIndex) => obstacleIndex !== index) }
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

  const handleGenerateCabinetPlan = async () => {
    if (!layout) {
      return;
    }

    setError(null);
    setSavedMessage(null);
    setIsGeneratingPlan(true);
    try {
      await saveMeasuredLayout(sessionId, layout);
      const generatedPlan = await generateCabinetPlan(sessionId);
      setCabinetPlan(generatedPlan);
      setSavedMessage('Lista de muebles generada.');
    } catch {
      setError('No se pudo generar la lista de muebles. Revisa layout, zonas y obstáculos.');
    } finally {
      setIsGeneratingPlan(false);
    }
  };

  const updateCabinet = (index: number, nextCabinet: Partial<CabinetPlanItem>) => {
    setCabinetPlan(currentPlan => {
      if (!currentPlan) {
        return currentPlan;
      }
      const cabinets = currentPlan.cabinets.map((cabinet, cabinetIndex) =>
        cabinetIndex === index ? { ...cabinet, ...nextCabinet } : cabinet,
      );
      const reflowedCabinets = reflowCabinetPositions(cabinets);
      return {
        ...currentPlan,
        cabinets: reflowedCabinets,
        cabinetCount: reflowedCabinets.length,
        totalOccupiedLengthMm: reflowedCabinets.reduce((total, cabinet) => total + cabinet.widthMm, 0),
        validationMessages: [],
        valid: true,
      };
    });
  };

  const handleSaveCabinetPlan = async () => {
    if (!cabinetPlan) {
      return;
    }

    setError(null);
    setSavedMessage(null);
    setIsSavingPlan(true);
    try {
      const savedPlan = await saveCabinetPlan(sessionId, cabinetPlan);
      setCabinetPlan(savedPlan);
      if (savedPlan.valid) {
        setSavedMessage('Lista de muebles guardada.');
      } else {
        setError('La lista tiene errores de validación. Corrige los campos marcados antes de guardar.');
      }
    } catch {
      setError('No se pudo guardar la lista de muebles. Revisa los datos e intenta de nuevo.');
    } finally {
      setIsSavingPlan(false);
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

          <section className="measured-layout__fieldset" aria-label="Zonas funcionales">
            <div className="measured-layout__section-heading">
              <h2 className="h6 mb-0">Zonas</h2>
              <Button type="button" size="sm" variant="outline-primary" onClick={addZone}>
                Agregar zona
              </Button>
            </div>
            {(layout.zones ?? []).map((zone, index) => (
              <div className="measured-layout__row" key={`${zone.zoneCode}-${index}`}>
                <Form.Group controlId={`zone-type-${index}`}>
                  <Form.Label>Tipo</Form.Label>
                  <Form.Select value={zone.zoneType} onChange={event => updateZone(index, { zoneType: event.target.value })}>
                    {zoneOptions.map(option => (
                      <option value={option.value} key={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
                <Form.Group controlId={`zone-wall-${index}`}>
                  <Form.Label>Pared</Form.Label>
                  <Form.Select value={zone.wallCode} onChange={event => updateZone(index, { wallCode: event.target.value })}>
                    {layout.walls.map(wall => (
                      <option value={wall.wallCode} key={wall.wallCode}>
                        {wall.wallCode}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
                <Form.Group controlId={`zone-x-${index}`}>
                  <Form.Label>Inicio (cm)</Form.Label>
                  <Form.Control
                    type="number"
                    min={0}
                    value={mmToCm(zone.xMm)}
                    onChange={event => updateZone(index, { xMm: cmToMm(event.target.value) })}
                  />
                </Form.Group>
                <Form.Group controlId={`zone-width-${index}`}>
                  <Form.Label>Ancho (cm)</Form.Label>
                  <Form.Control
                    type="number"
                    min={1}
                    value={mmToCm(zone.widthMm)}
                    onChange={event => updateZone(index, { widthMm: cmToMm(event.target.value) })}
                  />
                </Form.Group>
                <Button type="button" variant="outline-danger" size="sm" onClick={() => removeZone(index)}>
                  Quitar
                </Button>
              </div>
            ))}
          </section>

          <section className="measured-layout__fieldset" aria-label="Obstáculos">
            <div className="measured-layout__section-heading">
              <h2 className="h6 mb-0">Obstáculos y servicios</h2>
              <Button type="button" size="sm" variant="outline-primary" onClick={addObstacle}>
                Agregar obstáculo
              </Button>
            </div>
            {(layout.obstacles ?? []).map((obstacle, index) => (
              <div className="measured-layout__row" key={`${obstacle.obstacleType}-${index}`}>
                <Form.Group controlId={`obstacle-type-${index}`}>
                  <Form.Label>Tipo</Form.Label>
                  <Form.Select
                    value={obstacle.obstacleType}
                    onChange={event => updateObstacle(index, { obstacleType: event.target.value as LayoutObstacleType })}
                  >
                    {obstacleOptions.map(option => (
                      <option value={option.value} key={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
                <Form.Group controlId={`obstacle-wall-${index}`}>
                  <Form.Label>Pared</Form.Label>
                  <Form.Select value={obstacle.wallCode} onChange={event => updateObstacle(index, { wallCode: event.target.value })}>
                    {layout.walls.map(wall => (
                      <option value={wall.wallCode} key={wall.wallCode}>
                        {wall.wallCode}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
                <Form.Group controlId={`obstacle-label-${index}`}>
                  <Form.Label>Etiqueta</Form.Label>
                  <Form.Control value={obstacle.label ?? ''} onChange={event => updateObstacle(index, { label: event.target.value })} />
                </Form.Group>
                <Form.Group controlId={`obstacle-x-${index}`}>
                  <Form.Label>Inicio (cm)</Form.Label>
                  <Form.Control
                    type="number"
                    min={0}
                    value={mmToCm(obstacle.xMm)}
                    onChange={event => updateObstacle(index, { xMm: cmToMm(event.target.value) })}
                  />
                </Form.Group>
                <Form.Group controlId={`obstacle-width-${index}`}>
                  <Form.Label>Ancho (cm)</Form.Label>
                  <Form.Control
                    type="number"
                    min={1}
                    value={mmToCm(obstacle.widthMm)}
                    onChange={event => updateObstacle(index, { widthMm: cmToMm(event.target.value) })}
                  />
                </Form.Group>
                <Form.Group controlId={`obstacle-height-${index}`}>
                  <Form.Label>Alto (cm)</Form.Label>
                  <Form.Control
                    type="number"
                    min={1}
                    value={mmToCm(obstacle.heightMm)}
                    onChange={event => updateObstacle(index, { heightMm: cmToMm(event.target.value) })}
                  />
                </Form.Group>
                <Form.Group controlId={`obstacle-z-${index}`}>
                  <Form.Label>Altura desde piso (cm)</Form.Label>
                  <Form.Control
                    type="number"
                    min={0}
                    value={mmToCm(obstacle.zMm)}
                    onChange={event => updateObstacle(index, { zMm: cmToMm(event.target.value) })}
                  />
                </Form.Group>
                <Button type="button" variant="outline-danger" size="sm" onClick={() => removeObstacle(index)}>
                  Quitar
                </Button>
              </div>
            ))}
          </section>

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
          <Button
            className="mt-3 ms-2"
            type="button"
            variant="primary"
            disabled={isSaving || isGeneratingPlan}
            onClick={handleGenerateCabinetPlan}
          >
            {isGeneratingPlan ? <Spinner size="sm" /> : 'Generar muebles'}
          </Button>
        </Form>

        <section className="measured-layout__preview" aria-label="Vista previa 2D del layout">
          <svg viewBox="0 0 320 240" role="img" aria-label="Preview de layout medido">
            <path d={previewPath} fill="none" stroke="#0f3d44" strokeLinecap="round" strokeLinejoin="round" strokeWidth="18" />
            {layout.layout === 'ISLAND' ? <rect x="138" y="98" width="62" height="44" rx="4" fill="#8aa6a9" /> : null}
            {(layout.zones ?? []).map((zone, index) => {
              const point = previewPoint(layout.layout, layout.walls, zone.wallCode, zone.xMm);
              return <circle cx={point.x} cy={point.y} r="8" fill="#2f80ed" key={`${zone.zoneCode}-${index}`} />;
            })}
            {(layout.obstacles ?? []).map((obstacle, index) => {
              const point = previewPoint(layout.layout, layout.walls, obstacle.wallCode, obstacle.xMm);
              return (
                <rect
                  x={point.x - 7}
                  y={point.y - 7}
                  width="14"
                  height="14"
                  rx="2"
                  fill="#e06f2f"
                  key={`${obstacle.obstacleType}-${index}`}
                />
              );
            })}
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
            <div>
              <dt>Zonas</dt>
              <dd>{layout.zones?.length ?? 0}</dd>
            </div>
            <div>
              <dt>Obstáculos</dt>
              <dd>{layout.obstacles?.length ?? 0}</dd>
            </div>
          </dl>
        </section>
      </div>

      {cabinetPlan ? (
        <section className="measured-layout__cabinet-plan" aria-label="Lista de muebles generada">
          <div className="measured-layout__section-heading">
            <div>
              <p className="design-chat__meta mb-1">Lista editable</p>
              <h2 className="h5 mb-0">
                {cabinetCount} muebles · {mmToCm(cabinetTotalWidthMm)} cm ocupados
              </h2>
            </div>
            <div className="measured-layout__actions">
              <strong>{cabinetPlan.valid ? 'Válida' : 'Revisar advertencias'}</strong>
              <Button type="button" variant="primary" disabled={isSavingPlan} onClick={handleSaveCabinetPlan}>
                {isSavingPlan ? <Spinner size="sm" /> : 'Guardar muebles'}
              </Button>
            </div>
          </div>
          {cabinetPlan.validationMessages.length > 0 ? (
            <div className="measured-layout__messages">
              {cabinetPlan.validationMessages.map(message => (
                <Alert variant={message.severity === 'ERROR' ? 'danger' : 'warning'} key={`${message.code}-${message.wallCode ?? ''}`}>
                  {message.message}
                </Alert>
              ))}
            </div>
          ) : null}
          <div className="measured-layout__cabinet-table-wrap">
            <table className="measured-layout__cabinet-table">
              <thead>
                <tr>
                  <th>Mueble</th>
                  <th>Tipo</th>
                  <th>Pared</th>
                  <th>X</th>
                  <th>Ancho</th>
                  <th>Alto</th>
                  <th>Fondo</th>
                  <th>Notas</th>
                </tr>
              </thead>
              <tbody>
                {cabinetPlan.cabinets.map((cabinet, index) => (
                  <tr key={cabinet.cabinetCode ?? `${cabinet.wallCode}-${cabinet.xMm}-${index}`}>
                    <td>
                      <Form.Control
                        aria-label={`Nombre de ${cabinet.cabinetCode}`}
                        value={cabinet.label}
                        maxLength={120}
                        onChange={event => updateCabinet(index, { label: event.target.value })}
                      />
                    </td>
                    <td>
                      <Form.Select
                        aria-label={`Tipo de ${cabinet.cabinetCode}`}
                        value={cabinet.category}
                        onChange={event => updateCabinet(index, { category: event.target.value as CabinetCategory })}
                      >
                        {cabinetCategoryOptions.map(option => (
                          <option value={option.value} key={option.value}>
                            {option.label}
                          </option>
                        ))}
                      </Form.Select>
                    </td>
                    <td>
                      <Form.Select
                        aria-label={`Pared de ${cabinet.cabinetCode}`}
                        value={cabinet.wallCode}
                        onChange={event => updateCabinet(index, { wallCode: event.target.value })}
                      >
                        {layout.walls.map(wall => (
                          <option value={wall.wallCode} key={wall.wallCode}>
                            {wall.wallCode}
                          </option>
                        ))}
                      </Form.Select>
                    </td>
                    <td>{mmToCm(cabinet.xMm)} cm</td>
                    <td>
                      <Form.Control
                        aria-label={`Ancho de ${cabinet.cabinetCode}`}
                        type="number"
                        min={1}
                        value={mmToCm(cabinet.widthMm)}
                        onChange={event => updateCabinet(index, { widthMm: cmToMm(event.target.value) })}
                      />
                    </td>
                    <td>
                      <Form.Control
                        aria-label={`Alto de ${cabinet.cabinetCode}`}
                        type="number"
                        min={1}
                        value={mmToCm(cabinet.heightMm)}
                        onChange={event => updateCabinet(index, { heightMm: cmToMm(event.target.value) })}
                      />
                    </td>
                    <td>
                      <Form.Control
                        aria-label={`Fondo de ${cabinet.cabinetCode}`}
                        type="number"
                        min={1}
                        value={mmToCm(cabinet.depthMm)}
                        onChange={event => updateCabinet(index, { depthMm: cmToMm(event.target.value) })}
                      />
                    </td>
                    <td>
                      <Form.Control
                        aria-label={`Notas de ${cabinet.cabinetCode}`}
                        value={cabinet.notes ?? ''}
                        maxLength={300}
                        onChange={event => updateCabinet(index, { notes: event.target.value })}
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      ) : null}
    </main>
  );
};

export default MeasuredLayoutPage;
