import './design-chat.scss';

import React, { FormEvent, useEffect, useMemo, useRef, useState } from 'react';
import { Alert, Button, Form, Spinner } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCheck, faImage, faPaperPlane, faRotateRight, faXmark } from '@fortawesome/free-solid-svg-icons';

import {
  ChatMessageView,
  ChatSession,
  getCatalogStyles,
  resumeChatSession,
  sendChatMessage,
  startChatSession,
} from 'app/shared/api/design-chat-api';
import { ICatalogStyle } from 'app/shared/model/catalog-style.model';

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

const DesignChat = () => {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [clientName, setClientName] = useState('');
  const [clientEmail, setClientEmail] = useState('');
  const [session, setSession] = useState<ChatSession | null>(null);
  const [messages, setMessages] = useState<ChatMessageView[]>([]);
  const [draft, setDraft] = useState('');
  const [catalogStyles, setCatalogStyles] = useState<ICatalogStyle[]>([]);
  const [selectedStyle, setSelectedStyle] = useState<ICatalogStyle | null>(null);
  const [selectedReferenceImage, setSelectedReferenceImage] = useState<SelectedReferenceImage | null>(null);
  const [expandedStyleId, setExpandedStyleId] = useState<string | number | null>(null);
  const [styleSkipped, setStyleSkipped] = useState(false);
  const [isLoadingStyles, setIsLoadingStyles] = useState(false);
  const [isStarting, setIsStarting] = useState(false);
  const [isSending, setIsSending] = useState(false);
  const [isResuming, setIsResuming] = useState(false);
  const [isDraggingReferenceImage, setIsDraggingReferenceImage] = useState(false);
  const [error, setError] = useState<string | null>(null);

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
    const storedSessionCode = window.localStorage.getItem(STORAGE_KEY);
    if (!storedSessionCode) {
      return;
    }

    setIsResuming(true);
    resumeChatSession(storedSessionCode)
      .then(resumedSession => {
        setSession(resumedSession);
        setMessages(resumedSession.messages ?? []);
        if (resumedSession.selectedStyle) {
          setSelectedStyle({ name: resumedSession.selectedStyle, isActive: true });
          setStyleSkipped(false);
        }
      })
      .catch(() => {
        window.localStorage.removeItem(STORAGE_KEY);
      })
      .finally(() => setIsResuming(false));
  }, []);

  const canStart = useMemo(
    () => clientName.trim().length > 0 && clientEmail.trim().length > 0 && (!!selectedStyle || styleSkipped),
    [clientEmail, clientName, selectedStyle, styleSkipped],
  );
  const canSend = useMemo(
    () => (draft.trim().length > 0 || !!selectedReferenceImage) && !!session && !isSending,
    [draft, isSending, selectedReferenceImage, session],
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
    setError(null);
    setIsSending(true);
    setMessages(currentMessages => [...currentMessages, optimisticMessage]);

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
      setSession(currentSession => (currentSession ? { ...currentSession, sessionCode: response.sessionCode } : currentSession));
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
    setStyleSkipped(false);
    setError(null);
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
                }`}
                key={`${message.createdAt}-${index}`}
              >
                {message.imagePreviewUrl ? (
                  <img
                    className="design-chat__message-image"
                    src={message.imagePreviewUrl}
                    alt={message.imageFileName ?? 'Imagen de referencia'}
                  />
                ) : null}
                {message.content}
              </div>
            ))}
            {isSending && (
              <div className="design-chat__message design-chat__message--assistant">
                <Spinner size="sm" /> Pensando...
              </div>
            )}
          </div>

          {error && (
            <Alert variant="danger" className="m-3 mb-0">
              {error}
            </Alert>
          )}

          <Form className="design-chat__composer" onSubmit={handleSend}>
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
                disabled={!session || isSending}
                onChange={event => handleReferenceImage(event.target.files?.[0])}
                ref={fileInputRef}
                type="file"
              />
              <Button
                aria-label="Adjuntar imagen de referencia"
                disabled={!session || isSending}
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
                placeholder="Escribe sobre tu cocina, closet, estilo o medidas..."
                disabled={!session || isSending}
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
