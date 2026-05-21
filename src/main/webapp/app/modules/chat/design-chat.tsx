import './design-chat.scss';

import React, { FormEvent, useEffect, useMemo, useRef, useState } from 'react';
import { Alert, Button, Form, Spinner } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCheck, faImage, faPaperPlane, faRotateRight, faXmark } from '@fortawesome/free-solid-svg-icons';

import {
  analyzeSketch,
  ChatMessageView,
  ChatSession,
  generateVisualConcept,
  getCatalogStyles,
  resumeChatSession,
  sendChatMessage,
  SketchCabinetCandidate,
  SketchExtractionResponse,
  SketchField,
  SketchMeasurement,
  SketchObstacleCandidate,
  SketchWallCandidate,
  SketchZoneCandidate,
  startChatSession,
} from 'app/shared/api/design-chat-api';
import { ICatalogStyle } from 'app/shared/model/catalog-style.model';
import { Link, useSearchParams } from 'react-router';

const STORAGE_KEY = 'kalitron.designChat.sessionCode';
const ACCEPTED_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/webp'];
const MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;

interface SelectedReferenceImage {
  fileName: string;
  mimeType: string;
  sizeBytes: number;
  base64: string;
  previewUrl: string;
}

type SelectedSketchImage = SelectedReferenceImage;

interface SketchReviewWall {
  wallCode: string;
  wallConfidence: string;
  length: string;
  lengthConfidence: string;
  height: string;
  heightConfidence: string;
  angleDeg: string;
  angleConfidence: string;
}

interface SketchReviewZone {
  zoneCode: string;
  zoneConfidence: string;
  zoneType: string;
  typeConfidence: string;
  wallCode: string;
  wallConfidence: string;
  x: string;
  xConfidence: string;
  width: string;
  widthConfidence: string;
}

interface SketchReviewObstacle {
  obstacleType: string;
  typeConfidence: string;
  label: string;
  labelConfidence: string;
  wallCode: string;
  wallConfidence: string;
  x: string;
  xConfidence: string;
  width: string;
  widthConfidence: string;
}

interface SketchReviewCabinet {
  candidateCode: string;
  category: string;
  categoryConfidence: string;
  label: string;
  labelConfidence: string;
  wallCode: string;
  wallConfidence: string;
  x: string;
  xConfidence: string;
  width: string;
  widthConfidence: string;
  height: string;
  heightConfidence: string;
  depth: string;
  depthConfidence: string;
  doors: string;
  doorsConfidence: string;
  drawers: string;
  drawersConfidence: string;
}

interface SketchReviewState {
  projectType: string;
  projectTypeConfidence: string;
  layout: string;
  layoutConfidence: string;
  unit: string;
  unitConfidence: string;
  walls: SketchReviewWall[];
  zones: SketchReviewZone[];
  obstacles: SketchReviewObstacle[];
  cabinets: SketchReviewCabinet[];
  missingInfo: string[];
  questions: string[];
  warnings: string[];
}

const formatPriceRange = (priceRange?: string | null) => {
  if (!priceRange) {
    return 'Precio por definir';
  }

  const normalizedPriceRange = priceRange.toLowerCase();
  if (normalizedPriceRange === 'economico') {
    return 'Precio economico';
  }
  if (normalizedPriceRange === 'medio') {
    return 'Precio medio';
  }
  if (normalizedPriceRange === 'premium') {
    return 'Precio premium';
  }

  return `Precio ${priceRange}`;
};

const readImageAsDataUrl = (file: File): Promise<string> =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      if (typeof reader.result === 'string') {
        resolve(reader.result);
        return;
      }
      reject(new Error('Image reader did not return a data URL.'));
    };
    reader.onerror = () => reject(reader.error);
    reader.readAsDataURL(file);
  });

const toBase64Payload = (dataUrl: string) => dataUrl.split(',')[1] ?? dataUrl;

const formatSketchValue = (value?: string | null) => value?.replaceAll('_', ' ').toLowerCase() ?? 'no detectado';

const sketchCountLabel = (count: number, singular: string, plural: string) => `${count} ${count === 1 ? singular : plural}`;

const fieldValue = <T,>(field?: SketchField<T> | null, fallback = '') => `${field?.value ?? fallback}`;

const fieldConfidence = <T,>(field?: SketchField<T> | null) => field?.confidence ?? 'MISSING';

const measurementValue = (measurement?: SketchMeasurement | null) => `${measurement?.value ?? ''}`;

const measurementConfidence = (measurement?: SketchMeasurement | null) => measurement?.confidence ?? 'MISSING';

const toSketchReview = (extraction: SketchExtractionResponse): SketchReviewState => ({
  projectType: fieldValue(extraction.projectType),
  projectTypeConfidence: fieldConfidence(extraction.projectType),
  layout: fieldValue(extraction.layout),
  layoutConfidence: fieldConfidence(extraction.layout),
  unit: fieldValue(extraction.unit),
  unitConfidence: fieldConfidence(extraction.unit),
  walls: (extraction.walls ?? []).map((wall: SketchWallCandidate, index) => ({
    wallCode: fieldValue(wall.wallCode, `A-${index + 1}`),
    wallConfidence: fieldConfidence(wall.wallCode),
    length: measurementValue(wall.length),
    lengthConfidence: measurementConfidence(wall.length),
    height: measurementValue(wall.height),
    heightConfidence: measurementConfidence(wall.height),
    angleDeg: fieldValue(wall.angleDeg, '0'),
    angleConfidence: fieldConfidence(wall.angleDeg),
  })),
  zones: (extraction.zones ?? []).map((zone: SketchZoneCandidate, index) => ({
    zoneCode: fieldValue(zone.zoneCode, `ZONE-${index + 1}`),
    zoneConfidence: fieldConfidence(zone.zoneCode),
    zoneType: fieldValue(zone.zoneType),
    typeConfidence: fieldConfidence(zone.zoneType),
    wallCode: fieldValue(zone.wallCode),
    wallConfidence: fieldConfidence(zone.wallCode),
    x: measurementValue(zone.x),
    xConfidence: measurementConfidence(zone.x),
    width: measurementValue(zone.width),
    widthConfidence: measurementConfidence(zone.width),
  })),
  obstacles: (extraction.obstacles ?? []).map((obstacle: SketchObstacleCandidate) => ({
    obstacleType: fieldValue(obstacle.obstacleType),
    typeConfidence: fieldConfidence(obstacle.obstacleType),
    label: fieldValue(obstacle.label),
    labelConfidence: fieldConfidence(obstacle.label),
    wallCode: fieldValue(obstacle.wallCode),
    wallConfidence: fieldConfidence(obstacle.wallCode),
    x: measurementValue(obstacle.x),
    xConfidence: measurementConfidence(obstacle.x),
    width: measurementValue(obstacle.width),
    widthConfidence: measurementConfidence(obstacle.width),
  })),
  cabinets: (extraction.cabinetCandidates ?? []).map((cabinet: SketchCabinetCandidate, index) => ({
    candidateCode: cabinet.candidateCode ?? `CAB-${index + 1}`,
    category: fieldValue(cabinet.category),
    categoryConfidence: fieldConfidence(cabinet.category),
    label: fieldValue(cabinet.label),
    labelConfidence: fieldConfidence(cabinet.label),
    wallCode: fieldValue(cabinet.wallCode),
    wallConfidence: fieldConfidence(cabinet.wallCode),
    x: measurementValue(cabinet.x),
    xConfidence: measurementConfidence(cabinet.x),
    width: measurementValue(cabinet.width),
    widthConfidence: measurementConfidence(cabinet.width),
    height: measurementValue(cabinet.height),
    heightConfidence: measurementConfidence(cabinet.height),
    depth: measurementValue(cabinet.depth),
    depthConfidence: measurementConfidence(cabinet.depth),
    doors: fieldValue(cabinet.doors),
    doorsConfidence: fieldConfidence(cabinet.doors),
    drawers: fieldValue(cabinet.drawers),
    drawersConfidence: fieldConfidence(cabinet.drawers),
  })),
  missingInfo: (extraction.missingInfo ?? []).map(item => item.message).filter((message): message is string => !!message),
  questions: extraction.questions ?? [],
  warnings: extraction.warnings ?? [],
});

const confidenceClass = (confidence?: string | null) => `design-chat__confidence--${(confidence ?? 'MISSING').toLowerCase()}`;

const DesignChat = () => {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const sketchInputRef = useRef<HTMLInputElement>(null);
  const [clientName, setClientName] = useState('');
  const [clientEmail, setClientEmail] = useState('');
  const [session, setSession] = useState<ChatSession | null>(null);
  const [messages, setMessages] = useState<ChatMessageView[]>([]);
  const [draft, setDraft] = useState('');
  const [catalogStyles, setCatalogStyles] = useState<ICatalogStyle[]>([]);
  const [selectedStyle, setSelectedStyle] = useState<ICatalogStyle | null>(null);
  const [selectedReferenceImage, setSelectedReferenceImage] = useState<SelectedReferenceImage | null>(null);
  const [selectedSketchImage, setSelectedSketchImage] = useState<SelectedSketchImage | null>(null);
  const [sketchReview, setSketchReview] = useState<SketchReviewState | null>(null);
  const [isSketchReviewConfirmed, setIsSketchReviewConfirmed] = useState(false);
  const [referenceImageBase64, setReferenceImageBase64] = useState<string | null>(null);
  const [visualStyle, setVisualStyle] = useState('');
  const [visualLayout, setVisualLayout] = useState('');
  const [visualFinish, setVisualFinish] = useState('');
  const [hasGeneratedConcept, setHasGeneratedConcept] = useState(false);
  const [expandedStyleId, setExpandedStyleId] = useState<string | number | null>(null);
  const [styleSkipped, setStyleSkipped] = useState(false);
  const [isLoadingStyles, setIsLoadingStyles] = useState(false);
  const [isStarting, setIsStarting] = useState(false);
  const [isSending, setIsSending] = useState(false);
  const [isAnalyzingSketch, setIsAnalyzingSketch] = useState(false);
  const [isGeneratingConcept, setIsGeneratingConcept] = useState(false);
  const [isResuming, setIsResuming] = useState(false);
  const [isDraggingReferenceImage, setIsDraggingReferenceImage] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searchParams] = useSearchParams();

  useEffect(() => {
    setIsLoadingStyles(true);
    getCatalogStyles()
      .then(styles => {
        setCatalogStyles(
          styles
            .filter(style => style.isActive)
            .sort((left, right) => (left.sortOrder ?? Number.MAX_SAFE_INTEGER) - (right.sortOrder ?? Number.MAX_SAFE_INTEGER)),
        );
      })
      .catch(() => {
        setError('No se pudieron cargar los estilos de catálogo.');
      })
      .finally(() => setIsLoadingStyles(false));
  }, []);

  useEffect(() => {
    const querySessionCode = searchParams.get('sessionCode');
    const storedSessionCode = querySessionCode || window.localStorage.getItem(STORAGE_KEY);
    if (!storedSessionCode) {
      return;
    }

    setIsResuming(true);
    resumeChatSession(storedSessionCode)
      .then(resumedSession => {
        setSession(resumedSession);
        setMessages(resumedSession.messages ?? []);
        window.localStorage.setItem(STORAGE_KEY, resumedSession.sessionCode);
        if (resumedSession.selectedStyle) {
          setSelectedStyle({ name: resumedSession.selectedStyle, isActive: true });
          setStyleSkipped(false);
        }
      })
      .catch(() => {
        window.localStorage.removeItem(STORAGE_KEY);
      })
      .finally(() => setIsResuming(false));
  }, [searchParams]);

  const canStart = useMemo(
    () => clientName.trim().length > 0 && clientEmail.trim().length > 0 && (!!selectedStyle || styleSkipped),
    [clientEmail, clientName, selectedStyle, styleSkipped],
  );
  const canSend = useMemo(
    () => (draft.trim().length > 0 || !!selectedReferenceImage) && !!session && !isSending && !isGeneratingConcept && !isAnalyzingSketch,
    [draft, isAnalyzingSketch, isGeneratingConcept, isSending, selectedReferenceImage, session],
  );
  const canAnalyzeSketch = useMemo(
    () => !!session && !!selectedSketchImage && !isAnalyzingSketch && !isSending && !isGeneratingConcept,
    [isAnalyzingSketch, isGeneratingConcept, isSending, selectedSketchImage, session],
  );
  const canGenerateConcept = useMemo(
    () => !!session && ['SPECS_READY', 'VISUAL_GENERATED'].includes(session.status) && !isGeneratingConcept,
    [isGeneratingConcept, session],
  );

  const handleStart = async (event: FormEvent) => {
    event.preventDefault();
    if (!canStart) {
      return;
    }

    setError(null);
    setIsStarting(true);
    try {
      const startedSession = await startChatSession({
        clientName: clientName.trim(),
        clientEmail: clientEmail.trim(),
        selectedStyle: selectedStyle?.name ?? null,
      });
      setSession(startedSession);
      setMessages(startedSession.messages ?? []);
      window.localStorage.setItem(STORAGE_KEY, startedSession.sessionCode);
    } catch {
      setError('No se pudo iniciar la sesión. Revisa tus datos e intenta de nuevo.');
    } finally {
      setIsStarting(false);
    }
  };

  const handleSend = async (event: FormEvent) => {
    event.preventDefault();
    if (!canSend || !session) {
      return;
    }

    const messageText = draft.trim();
    const imageToSend = selectedReferenceImage;
    const optimisticMessage: ChatMessageView = {
      role: 'USER',
      content: messageText || 'Imagen de referencia adjunta.',
      createdAt: new Date().toISOString(),
      imageFileName: imageToSend?.fileName,
      imagePreviewUrl: imageToSend?.previewUrl,
    };

    setDraft('');
    setSelectedReferenceImage(null);
    if (imageToSend?.base64) {
      setReferenceImageBase64(imageToSend.base64);
    }
    setError(null);
    setMessages(currentMessages => [...currentMessages, optimisticMessage]);

    if (canGenerateConcept && (messageText || imageToSend)) {
      await handleGenerateConcept(messageText || null, imageToSend);
      return;
    }

    setIsSending(true);

    try {
      const response = await sendChatMessage(
        session.sessionId,
        messageText,
        selectedStyle?.name ?? session.selectedStyle ?? null,
        imageToSend
          ? {
              imageBase64: imageToSend.base64,
              imageFileName: imageToSend.fileName,
              imageMimeType: imageToSend.mimeType,
              imageSizeBytes: imageToSend.sizeBytes,
            }
          : null,
      );
      setMessages(currentMessages => [
        ...currentMessages,
        {
          role: 'ASSISTANT',
          content: response.reply,
          createdAt: new Date().toISOString(),
        },
      ]);
      setSession(currentSession =>
        currentSession
          ? {
              ...currentSession,
              sessionCode: response.sessionCode,
              status: response.specsReady ? 'SPECS_READY' : currentSession.status,
            }
          : currentSession,
      );
    } catch {
      setSelectedReferenceImage(imageToSend);
      setError('No se pudo enviar el mensaje. Intenta nuevamente.');
    } finally {
      setIsSending(false);
    }
  };

  const handleRestart = () => {
    window.localStorage.removeItem(STORAGE_KEY);
    setSession(null);
    setMessages([]);
    setDraft('');
    setSelectedStyle(null);
    setSelectedReferenceImage(null);
    setSelectedSketchImage(null);
    setSketchReview(null);
    setIsSketchReviewConfirmed(false);
    setReferenceImageBase64(null);
    setHasGeneratedConcept(false);
    setStyleSkipped(false);
    setError(null);
  };

  const handleGenerateConcept = async (visualInstructions?: string | null, referenceImageOverride?: SelectedReferenceImage | null) => {
    if (!canGenerateConcept || !session) {
      return;
    }

    setError(null);
    setIsGeneratingConcept(true);
    try {
      const concept = await generateVisualConcept({
        sessionId: session.sessionId,
        style: visualStyle || null,
        layout: visualLayout || null,
        finish: visualFinish || null,
        clientImageBase64: referenceImageOverride?.base64 || selectedReferenceImage?.base64 || referenceImageBase64 || null,
        visualInstructions: visualInstructions || null,
      });
      setMessages(currentMessages => [
        ...currentMessages,
        {
          role: 'ASSISTANT',
          content: 'Concepto visual generado.',
          createdAt: new Date().toISOString(),
          imagePreviewUrl: concept.imageUrl,
          imageFileName: concept.pipeline,
          imageBadge: concept.badge,
        },
      ]);
      setHasGeneratedConcept(true);
      setSession(currentSession =>
        currentSession ? { ...currentSession, sessionCode: concept.sessionCode, status: 'VISUAL_GENERATED' } : currentSession,
      );
    } catch {
      setError('No se pudo generar el concepto visual. Verifica que el AI Gateway y ComfyUI estén disponibles.');
    } finally {
      setIsGeneratingConcept(false);
    }
  };

  const handleReferenceImage = async (file: File | undefined) => {
    if (!file) {
      return;
    }
    if (!ACCEPTED_IMAGE_TYPES.includes(file.type)) {
      setError('La imagen debe ser JPG, PNG o WebP.');
      return;
    }
    if (file.size > MAX_IMAGE_SIZE_BYTES) {
      setError('La imagen debe pesar 5MB o menos.');
      return;
    }

    try {
      const dataUrl = await readImageAsDataUrl(file);
      setSelectedReferenceImage({
        fileName: file.name,
        mimeType: file.type,
        sizeBytes: file.size,
        base64: toBase64Payload(dataUrl),
        previewUrl: dataUrl,
      });
      setError(null);
    } catch {
      setError('No se pudo leer la imagen seleccionada.');
    }
  };

  const handleSketchImage = async (file: File | undefined) => {
    if (!file) {
      return;
    }
    if (!ACCEPTED_IMAGE_TYPES.includes(file.type)) {
      setError('El boceto debe ser JPG, PNG o WebP.');
      return;
    }
    if (file.size > MAX_IMAGE_SIZE_BYTES) {
      setError('El boceto debe pesar 5MB o menos.');
      return;
    }

    try {
      const dataUrl = await readImageAsDataUrl(file);
      setSelectedSketchImage({
        fileName: file.name,
        mimeType: file.type,
        sizeBytes: file.size,
        base64: toBase64Payload(dataUrl),
        previewUrl: dataUrl,
      });
      setError(null);
    } catch {
      setError('No se pudo leer el boceto seleccionado.');
    }
  };

  const summarizeSketchExtraction = (extraction: Awaited<ReturnType<typeof analyzeSketch>>) => {
    const wallCount = extraction.walls?.length ?? 0;
    const zoneCount = extraction.zones?.length ?? 0;
    const cabinetCount = extraction.cabinetCandidates?.length ?? 0;
    const missingInfo = extraction.missingInfo?.map(item => item.message).filter(Boolean) ?? [];
    const questions = extraction.questions ?? [];
    const warnings = extraction.warnings ?? [];

    const lines = [
      'Boceto analizado como borrador, no como plano confirmado.',
      '',
      `Tipo detectado: ${formatSketchValue(extraction.projectType?.value)} (${extraction.projectType?.confidence ?? 'sin confianza'})`,
      `Layout detectado: ${formatSketchValue(extraction.layout?.value)} (${extraction.layout?.confidence ?? 'sin confianza'})`,
      `Elementos: ${sketchCountLabel(wallCount, 'pared', 'paredes')}, ${sketchCountLabel(zoneCount, 'zona', 'zonas')}, ${sketchCountLabel(cabinetCount, 'mueble candidato', 'muebles candidatos')}.`,
    ];

    if (missingInfo.length > 0) {
      lines.push('', 'Falta confirmar:', ...missingInfo.slice(0, 3).map(item => `- ${item}`));
    }

    if (questions.length > 0) {
      lines.push('', 'Preguntas sugeridas:', ...questions.slice(0, 2).map(item => `- ${item}`));
    }

    if (warnings.length > 0) {
      lines.push('', 'Advertencias:', ...warnings.slice(0, 2).map(item => `- ${item}`));
    }

    return lines.join('\n');
  };

  const handleAnalyzeSketch = async () => {
    if (!session || !selectedSketchImage || !canAnalyzeSketch) {
      return;
    }

    const sketchToAnalyze = selectedSketchImage;
    const sketchPrompt = draft.trim();
    setError(null);
    setIsAnalyzingSketch(true);
    setDraft('');
    setMessages(currentMessages => [
      ...currentMessages,
      {
        role: 'USER',
        content: sketchPrompt
          ? `Boceto para extracción de layout y muebles. Nota: ${sketchPrompt}`
          : 'Boceto para extracción de layout y muebles. Requiere revisión antes de guardar.',
        createdAt: new Date().toISOString(),
        imageFileName: sketchToAnalyze.fileName,
        imagePreviewUrl: sketchToAnalyze.previewUrl,
      },
    ]);

    try {
      const extraction = await analyzeSketch({
        sessionId: session.sessionId,
        imageBase64: sketchToAnalyze.base64,
        imageFileName: sketchToAnalyze.fileName,
        imageMimeType: sketchToAnalyze.mimeType,
        imageSizeBytes: sketchToAnalyze.sizeBytes,
        projectTypeHint: session.projectType,
        unitHint: 'MM',
        userPrompt: sketchPrompt || null,
      });
      setMessages(currentMessages => [
        ...currentMessages,
        {
          role: 'ASSISTANT',
          content: summarizeSketchExtraction(extraction),
          createdAt: new Date().toISOString(),
        },
      ]);
      setSketchReview(toSketchReview(extraction));
      setIsSketchReviewConfirmed(false);
      setSelectedSketchImage(null);
    } catch {
      setDraft(sketchPrompt);
      setError('No se pudo analizar el boceto. Verifica que el AI Gateway esté disponible e intenta de nuevo.');
    } finally {
      setIsAnalyzingSketch(false);
    }
  };

  const updateSketchReview = <K extends keyof SketchReviewState>(field: K, value: SketchReviewState[K]) => {
    setSketchReview(currentReview => (currentReview ? { ...currentReview, [field]: value } : currentReview));
    setIsSketchReviewConfirmed(false);
  };

  const updateSketchReviewItem = <K extends 'walls' | 'zones' | 'obstacles' | 'cabinets'>(
    collection: K,
    index: number,
    field: keyof SketchReviewState[K][number],
    value: string,
  ) => {
    setSketchReview(currentReview => {
      if (!currentReview) {
        return currentReview;
      }
      const updatedCollection = [...currentReview[collection]];
      updatedCollection[index] = { ...updatedCollection[index], [field]: value };
      return { ...currentReview, [collection]: updatedCollection };
    });
    setIsSketchReviewConfirmed(false);
  };

  const handleConfirmSketchReview = () => {
    if (!sketchReview) {
      return;
    }
    setIsSketchReviewConfirmed(true);
    setMessages(currentMessages => [
      ...currentMessages,
      {
        role: 'ASSISTANT',
        content: 'Extracción revisada y confirmada como borrador. Aún no se ha guardado como layout medido ni como plan de muebles.',
        createdAt: new Date().toISOString(),
      },
    ]);
  };

  const handleRejectSketchReview = () => {
    setSketchReview(null);
    setIsSketchReviewConfirmed(false);
    setMessages(currentMessages => [
      ...currentMessages,
      {
        role: 'ASSISTANT',
        content: 'Extracción descartada. Puedes capturar el layout manualmente desde Layout medido.',
        createdAt: new Date().toISOString(),
      },
    ]);
  };

  const handleDragReferenceImage = (event: React.DragEvent<HTMLElement>) => {
    event.preventDefault();
    event.stopPropagation();
    if (!session || isSending) {
      return;
    }
    setIsDraggingReferenceImage(true);
  };

  const handleLeaveReferenceImage = (event: React.DragEvent<HTMLElement>) => {
    event.preventDefault();
    event.stopPropagation();
    if (!event.currentTarget.contains(event.relatedTarget as Node | null)) {
      setIsDraggingReferenceImage(false);
    }
  };

  const handleDropReferenceImage = (event: React.DragEvent<HTMLElement>) => {
    event.preventDefault();
    event.stopPropagation();
    setIsDraggingReferenceImage(false);
    if (!session || isSending) {
      return;
    }
    handleReferenceImage(event.dataTransfer.files[0]);
  };

  const handleSelectStyle = (style: ICatalogStyle) => {
    setSelectedStyle(style);
    setStyleSkipped(false);
  };

  const handleStyleCardKeyDown = (event: React.KeyboardEvent<HTMLElement>, style: ICatalogStyle) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      handleSelectStyle(style);
    }
  };

  const handleSkipStyle = () => {
    setSelectedStyle(null);
    setStyleSkipped(true);
  };

  const getStyleKey = (style: ICatalogStyle) => style.id ?? style.name ?? 'style';

  const renderConfidenceBadge = (confidence: string) => (
    <span className={`design-chat__confidence ${confidenceClass(confidence)}`}>{confidence.toLowerCase()}</span>
  );

  const renderReviewInput = (
    label: string,
    value: string,
    confidence: string,
    onChange: (value: string) => void,
    inputMode?: React.HTMLAttributes<HTMLInputElement>['inputMode'],
  ) => (
    <Form.Group className={`design-chat__review-field ${confidenceClass(confidence)}`}>
      <div className="design-chat__review-label">
        <Form.Label>{label}</Form.Label>
        {renderConfidenceBadge(confidence)}
      </div>
      <Form.Control size="sm" value={value} onChange={event => onChange(event.target.value)} inputMode={inputMode} />
    </Form.Group>
  );

  const renderSketchReview = () => {
    if (!sketchReview) {
      return null;
    }

    return (
      <section className="design-chat__review" aria-label="Revisión de extracción de boceto">
        <div className="design-chat__review-header">
          <div>
            <h3 className="h6 mb-1">Revisión del boceto</h3>
            <p className="design-chat__meta mb-0">Corrige los datos antes de usarlos como layout o muebles. Nada se guarda todavía.</p>
          </div>
          <span className={`design-chat__review-status ${isSketchReviewConfirmed ? 'design-chat__review-status--confirmed' : ''}`}>
            {isSketchReviewConfirmed ? 'Confirmado como borrador' : 'Pendiente de confirmar'}
          </span>
        </div>

        <div className="design-chat__review-grid">
          {renderReviewInput('Proyecto', sketchReview.projectType, sketchReview.projectTypeConfidence, value =>
            updateSketchReview('projectType', value),
          )}
          {renderReviewInput('Layout', sketchReview.layout, sketchReview.layoutConfidence, value => updateSketchReview('layout', value))}
          {renderReviewInput('Unidad', sketchReview.unit, sketchReview.unitConfidence, value => updateSketchReview('unit', value))}
        </div>

        <section className="design-chat__review-section">
          <h4>Paredes</h4>
          {sketchReview.walls.length === 0 ? <p className="design-chat__meta mb-0">No se detectaron paredes.</p> : null}
          {sketchReview.walls.map((wall, index) => (
            <div className="design-chat__review-card" key={`wall-${index}`}>
              {renderReviewInput('Código', wall.wallCode, wall.wallConfidence, value =>
                updateSketchReviewItem('walls', index, 'wallCode', value),
              )}
              {renderReviewInput('Largo mm', wall.length, wall.lengthConfidence, value =>
                updateSketchReviewItem('walls', index, 'length', value),
              )}
              {renderReviewInput('Alto mm', wall.height, wall.heightConfidence, value =>
                updateSketchReviewItem('walls', index, 'height', value),
              )}
              {renderReviewInput('Ángulo', wall.angleDeg, wall.angleConfidence, value =>
                updateSketchReviewItem('walls', index, 'angleDeg', value),
              )}
            </div>
          ))}
        </section>

        <section className="design-chat__review-section">
          <h4>Zonas</h4>
          {sketchReview.zones.length === 0 ? <p className="design-chat__meta mb-0">No se detectaron zonas.</p> : null}
          {sketchReview.zones.map((zone, index) => (
            <div className="design-chat__review-card" key={`zone-${index}`}>
              {renderReviewInput('Código', zone.zoneCode, zone.zoneConfidence, value =>
                updateSketchReviewItem('zones', index, 'zoneCode', value),
              )}
              {renderReviewInput('Tipo', zone.zoneType, zone.typeConfidence, value =>
                updateSketchReviewItem('zones', index, 'zoneType', value),
              )}
              {renderReviewInput('Pared', zone.wallCode, zone.wallConfidence, value =>
                updateSketchReviewItem('zones', index, 'wallCode', value),
              )}
              {renderReviewInput('X mm', zone.x, zone.xConfidence, value => updateSketchReviewItem('zones', index, 'x', value))}
              {renderReviewInput('Ancho mm', zone.width, zone.widthConfidence, value =>
                updateSketchReviewItem('zones', index, 'width', value),
              )}
            </div>
          ))}
        </section>

        <section className="design-chat__review-section">
          <h4>Obstáculos</h4>
          {sketchReview.obstacles.length === 0 ? <p className="design-chat__meta mb-0">No se detectaron obstáculos.</p> : null}
          {sketchReview.obstacles.map((obstacle, index) => (
            <div className="design-chat__review-card" key={`obstacle-${index}`}>
              {renderReviewInput('Tipo', obstacle.obstacleType, obstacle.typeConfidence, value =>
                updateSketchReviewItem('obstacles', index, 'obstacleType', value),
              )}
              {renderReviewInput('Etiqueta', obstacle.label, obstacle.labelConfidence, value =>
                updateSketchReviewItem('obstacles', index, 'label', value),
              )}
              {renderReviewInput('Pared', obstacle.wallCode, obstacle.wallConfidence, value =>
                updateSketchReviewItem('obstacles', index, 'wallCode', value),
              )}
              {renderReviewInput('X mm', obstacle.x, obstacle.xConfidence, value => updateSketchReviewItem('obstacles', index, 'x', value))}
              {renderReviewInput('Ancho mm', obstacle.width, obstacle.widthConfidence, value =>
                updateSketchReviewItem('obstacles', index, 'width', value),
              )}
            </div>
          ))}
        </section>

        <section className="design-chat__review-section">
          <h4>Muebles candidatos</h4>
          {sketchReview.cabinets.length === 0 ? <p className="design-chat__meta mb-0">No se detectaron muebles.</p> : null}
          {sketchReview.cabinets.map((cabinet, index) => (
            <div className="design-chat__review-card" key={cabinet.candidateCode || `cabinet-${index}`}>
              {renderReviewInput('Código', cabinet.candidateCode, 'HIGH', value =>
                updateSketchReviewItem('cabinets', index, 'candidateCode', value),
              )}
              {renderReviewInput('Tipo', cabinet.category, cabinet.categoryConfidence, value =>
                updateSketchReviewItem('cabinets', index, 'category', value),
              )}
              {renderReviewInput('Etiqueta', cabinet.label, cabinet.labelConfidence, value =>
                updateSketchReviewItem('cabinets', index, 'label', value),
              )}
              {renderReviewInput('Pared', cabinet.wallCode, cabinet.wallConfidence, value =>
                updateSketchReviewItem('cabinets', index, 'wallCode', value),
              )}
              {renderReviewInput('X mm', cabinet.x, cabinet.xConfidence, value => updateSketchReviewItem('cabinets', index, 'x', value))}
              {renderReviewInput('Ancho mm', cabinet.width, cabinet.widthConfidence, value =>
                updateSketchReviewItem('cabinets', index, 'width', value),
              )}
              {renderReviewInput('Alto mm', cabinet.height, cabinet.heightConfidence, value =>
                updateSketchReviewItem('cabinets', index, 'height', value),
              )}
              {renderReviewInput('Fondo mm', cabinet.depth, cabinet.depthConfidence, value =>
                updateSketchReviewItem('cabinets', index, 'depth', value),
              )}
            </div>
          ))}
        </section>

        {sketchReview.missingInfo.length > 0 || sketchReview.questions.length > 0 || sketchReview.warnings.length > 0 ? (
          <section className="design-chat__review-section">
            <h4>Notas de revisión</h4>
            {[...sketchReview.missingInfo, ...sketchReview.questions, ...sketchReview.warnings].slice(0, 8).map((item, index) => (
              <Alert className="mb-2" key={`${item}-${index}`} variant="warning">
                {item}
              </Alert>
            ))}
          </section>
        ) : null}

        <div className="design-chat__review-actions">
          <Button onClick={handleConfirmSketchReview} type="button" disabled={isSketchReviewConfirmed}>
            Confirmar extracción
          </Button>
          <Button onClick={handleRejectSketchReview} type="button" variant="outline-secondary">
            Descartar extracción
          </Button>
          {session ? (
            <Button as={Link as any} to={`/design-layout/${session.sessionId}`} type="button" variant="outline-primary">
              Captura manual
            </Button>
          ) : null}
        </div>
      </section>
    );
  };

  return (
    <main className="design-chat">
      <div className="design-chat__shell">
        <aside className="design-chat__panel">
          <h1 className="h4 mb-3">Diseño IA</h1>
          <p className="design-chat__meta mb-4">Sesión guiada para convertir una idea inicial en especificaciones de diseño.</p>

          {session ? (
            <>
              <div className="mb-3">
                <div className="design-chat__meta">Código de sesión</div>
                <strong>{session.sessionCode}</strong>
              </div>
              <div className="mb-3">
                <div className="design-chat__meta">Cliente</div>
                <strong>{session.clientName}</strong>
              </div>
              <div className="mb-3">
                <div className="design-chat__meta">Estilo</div>
                <strong>{selectedStyle?.name ?? session.selectedStyle ?? 'Sin estilo seleccionado'}</strong>
              </div>
              <Button type="button" variant="outline-secondary" size="sm" onClick={handleRestart}>
                <FontAwesomeIcon icon={faRotateRight} /> Nueva sesión
              </Button>
              <Button as={Link as any} to={`/design-layout/${session.sessionId}`} className="ms-2" variant="outline-primary" size="sm">
                Layout medido
              </Button>
              <section className="design-chat__sketch-panel" aria-label="Boceto para extracción">
                <div>
                  <h2 className="h6 mb-1">Boceto</h2>
                  <p className="design-chat__meta mb-2">Sube una foto del dibujo. Se analizará como borrador, no como plano confirmado.</p>
                </div>
                {selectedSketchImage ? (
                  <div className="design-chat__sketch-preview">
                    <img src={selectedSketchImage.previewUrl} alt={selectedSketchImage.fileName} />
                    <span>{selectedSketchImage.fileName}</span>
                    <Button
                      aria-label="Quitar boceto"
                      disabled={isAnalyzingSketch}
                      onClick={() => setSelectedSketchImage(null)}
                      size="sm"
                      type="button"
                      variant="link"
                    >
                      <FontAwesomeIcon icon={faXmark} />
                    </Button>
                  </div>
                ) : null}
                <input
                  accept={ACCEPTED_IMAGE_TYPES.join(',')}
                  className="d-none"
                  disabled={!session || isAnalyzingSketch}
                  onChange={event => handleSketchImage(event.target.files?.[0])}
                  ref={sketchInputRef}
                  type="file"
                />
                <div className="design-chat__sketch-actions">
                  <Button
                    disabled={isAnalyzingSketch}
                    onClick={() => sketchInputRef.current?.click()}
                    type="button"
                    variant="outline-secondary"
                    size="sm"
                  >
                    <FontAwesomeIcon icon={faImage} /> Subir boceto
                  </Button>
                  <Button disabled={!canAnalyzeSketch} onClick={handleAnalyzeSketch} type="button" size="sm">
                    {isAnalyzingSketch ? <Spinner size="sm" /> : 'Analizar'}
                  </Button>
                </div>
              </section>
            </>
          ) : (
            <Form onSubmit={handleStart}>
              <section className="design-chat__styles" aria-label="Seleccionar estilo">
                <div className="d-flex align-items-center justify-content-between gap-2 mb-2">
                  <h2 className="h6 mb-0">Estilo visual</h2>
                  <Button type="button" variant={styleSkipped ? 'secondary' : 'outline-secondary'} size="sm" onClick={handleSkipStyle}>
                    Saltar
                  </Button>
                </div>
                {isLoadingStyles ? (
                  <div className="design-chat__meta mb-3">
                    <Spinner size="sm" /> Cargando estilos...
                  </div>
                ) : (
                  <div className="design-chat__style-grid">
                    {catalogStyles.map(style => (
                      <article
                        aria-label={`Seleccionar estilo ${style.name}`}
                        aria-pressed={selectedStyle?.id === style.id}
                        className={`design-chat__style-card ${selectedStyle?.id === style.id ? 'design-chat__style-card--selected' : ''}`}
                        key={getStyleKey(style)}
                        onClick={() => handleSelectStyle(style)}
                        onKeyDown={event => handleStyleCardKeyDown(event, style)}
                        role="button"
                        tabIndex={0}
                      >
                        {selectedStyle?.id === style.id ? (
                          <span className="design-chat__style-selected-indicator" aria-label="Estilo seleccionado">
                            <FontAwesomeIcon icon={faCheck} />
                          </span>
                        ) : null}
                        <span
                          className={`design-chat__style-thumb design-chat__style-thumb--${style.style ?? 'default'}`}
                          style={{
                            backgroundImage: `linear-gradient(135deg, rgba(255,255,255,0.34), rgba(0,0,0,0.16)), url(${style.thumbnailPath})`,
                          }}
                        />
                        <span className="design-chat__style-body">
                          <span className="design-chat__style-title">{style.name}</span>
                          <span className="design-chat__style-price">{formatPriceRange(style.priceRange)}</span>
                          <span
                            className={`design-chat__style-description ${
                              expandedStyleId === getStyleKey(style) ? 'design-chat__style-description--expanded' : ''
                            }`}
                            title={style.description ?? undefined}
                          >
                            {style.description}
                          </span>
                          <span className="design-chat__style-actions">
                            <Button
                              onClick={event => {
                                event.stopPropagation();
                                setExpandedStyleId(currentStyleId => (currentStyleId === getStyleKey(style) ? null : getStyleKey(style)));
                              }}
                              size="sm"
                              type="button"
                              variant="link"
                            >
                              {expandedStyleId === getStyleKey(style) ? 'Menos' : 'Ver más'}
                            </Button>
                          </span>
                        </span>
                      </article>
                    ))}
                  </div>
                )}
              </section>
              <Form.Group className="mb-3" controlId="clientName">
                <Form.Label>Nombre</Form.Label>
                <Form.Control value={clientName} onChange={event => setClientName(event.target.value)} maxLength={120} required />
              </Form.Group>
              <Form.Group className="mb-3" controlId="clientEmail">
                <Form.Label>Email</Form.Label>
                <Form.Control
                  type="email"
                  value={clientEmail}
                  onChange={event => setClientEmail(event.target.value)}
                  maxLength={120}
                  required
                />
              </Form.Group>
              <Button type="submit" disabled={!canStart || isStarting}>
                {isStarting ? <Spinner size="sm" /> : 'Iniciar sesión'}
              </Button>
            </Form>
          )}
        </aside>

        <section
          className={`design-chat__conversation ${isDraggingReferenceImage ? 'design-chat__conversation--dragging' : ''}`}
          aria-live="polite"
          onDragEnter={handleDragReferenceImage}
          onDragLeave={handleLeaveReferenceImage}
          onDragOver={handleDragReferenceImage}
          onDrop={handleDropReferenceImage}
        >
          {isDraggingReferenceImage ? <div className="design-chat__drop-zone">Suelta la imagen de referencia</div> : null}
          <header className="design-chat__header">
            <h2 className="h5 mb-1">Conversación de diseño</h2>
            <div className="design-chat__meta">
              {isResuming ? 'Restaurando sesión...' : session ? 'Describe tu espacio y preferencias.' : 'Inicia una sesión para comenzar.'}
            </div>
          </header>

          <div className="design-chat__messages">
            {messages.length === 0 && (
              <div className="design-chat__message design-chat__message--assistant">
                Comparte tus datos para abrir una sesión de diseño.
              </div>
            )}
            {messages.map((message, index) => (
              <div
                className={`design-chat__message ${
                  message.role === 'USER' ? 'design-chat__message--user' : 'design-chat__message--assistant'
                } ${message.imageBadge ? 'design-chat__message--concept' : ''}`}
                key={`${message.createdAt}-${index}`}
              >
                {message.imagePreviewUrl ? (
                  <>
                    {message.imageBadge ? <span className="design-chat__image-badge">{message.imageBadge}</span> : null}
                    <img
                      className={`design-chat__message-image ${message.imageBadge ? 'design-chat__message-image--concept' : ''}`}
                      src={message.imagePreviewUrl}
                      alt={message.imageFileName ?? 'Imagen de referencia'}
                    />
                  </>
                ) : null}
                {message.content}
              </div>
            ))}
            {isSending && (
              <div className="design-chat__message design-chat__message--assistant">
                <Spinner size="sm" /> Pensando...
              </div>
            )}
            {isGeneratingConcept && (
              <div className="design-chat__message design-chat__message--assistant">
                <Spinner size="sm" /> Generando concepto visual... tiempo estimado 45-60s.
              </div>
            )}
            {isAnalyzingSketch && (
              <div className="design-chat__message design-chat__message--assistant">
                <Spinner size="sm" /> Analizando boceto...
              </div>
            )}
          </div>

          {error && (
            <Alert variant="danger" className="m-3 mb-0">
              {error}
            </Alert>
          )}

          {renderSketchReview()}

          <Form className="design-chat__composer" onSubmit={handleSend}>
            {canGenerateConcept ? (
              <section className="design-chat__visual-controls" aria-label="Generar concepto visual">
                <div className="design-chat__visual-grid">
                  <Form.Group controlId="visualStyle">
                    <Form.Label>Estilo</Form.Label>
                    <Form.Select value={visualStyle} onChange={event => setVisualStyle(event.target.value)}>
                      <option value="">Usar resumen confirmado</option>
                      <option value="moderno">Moderno</option>
                      <option value="minimalista">Minimalista</option>
                      <option value="rustico">Rústico</option>
                      <option value="clasico">Clásico</option>
                      <option value="industrial">Industrial</option>
                    </Form.Select>
                  </Form.Group>
                  <Form.Group controlId="visualLayout">
                    <Form.Label>Layout</Form.Label>
                    <Form.Select value={visualLayout} onChange={event => setVisualLayout(event.target.value)}>
                      <option value="">Usar resumen confirmado</option>
                      <option value="lineal">Lineal</option>
                      <option value="L">En L</option>
                      <option value="U">En U</option>
                      <option value="isla">Con isla</option>
                    </Form.Select>
                  </Form.Group>
                  <Form.Group controlId="visualFinish">
                    <Form.Label>Acabado</Form.Label>
                    <Form.Select value={visualFinish} onChange={event => setVisualFinish(event.target.value)}>
                      <option value="">Usar resumen confirmado</option>
                      <option value="blanco mate">Blanco mate</option>
                      <option value="blanco brillante">Blanco brillante</option>
                      <option value="negro opaco">Negro opaco</option>
                      <option value="madera natural">Madera natural</option>
                      <option value="gris mate">Gris mate</option>
                    </Form.Select>
                  </Form.Group>
                </div>
                <Button type="button" onClick={() => handleGenerateConcept()} disabled={isGeneratingConcept}>
                  {isGeneratingConcept ? <Spinner size="sm" /> : hasGeneratedConcept ? 'Regenerar concepto' : 'Generar concepto'}
                </Button>
                {session?.status === 'VISUAL_GENERATED' ? (
                  <Button as={Link as any} to={`/proposal/${session.sessionCode}`} variant="outline-primary">
                    Ver propuesta
                  </Button>
                ) : null}
              </section>
            ) : null}
            {selectedReferenceImage ? (
              <div className="design-chat__attachment-preview">
                <img src={selectedReferenceImage.previewUrl} alt={selectedReferenceImage.fileName} />
                <span>{selectedReferenceImage.fileName}</span>
                <Button
                  aria-label="Quitar imagen de referencia"
                  disabled={isSending}
                  onClick={() => setSelectedReferenceImage(null)}
                  size="sm"
                  type="button"
                  variant="link"
                >
                  <FontAwesomeIcon icon={faXmark} />
                </Button>
              </div>
            ) : null}
            <div className="design-chat__composer-row">
              <input
                accept={ACCEPTED_IMAGE_TYPES.join(',')}
                className="d-none"
                disabled={!session || isSending || isAnalyzingSketch}
                onChange={event => handleReferenceImage(event.target.files?.[0])}
                ref={fileInputRef}
                type="file"
              />
              <Button
                aria-label="Adjuntar imagen de referencia"
                disabled={!session || isSending || isAnalyzingSketch}
                onClick={() => fileInputRef.current?.click()}
                type="button"
                variant="outline-secondary"
              >
                <FontAwesomeIcon icon={faImage} />
              </Button>
              <Form.Control
                as="textarea"
                value={draft}
                onChange={event => setDraft(event.target.value)}
                placeholder={
                  canGenerateConcept
                    ? 'Escribe ajustes para regenerar: colores, cubierta, puertas, materiales...'
                    : 'Escribe sobre tu cocina, closet, estilo o medidas...'
                }
                disabled={!session || isSending || isGeneratingConcept || isAnalyzingSketch}
                maxLength={4000}
              />
              <Button type="submit" disabled={!canSend} aria-label="Enviar mensaje">
                <FontAwesomeIcon icon={faPaperPlane} />
              </Button>
            </div>
          </Form>
        </section>
      </div>
    </main>
  );
};

export default DesignChat;
