import './design-chat.scss';

import React, { FormEvent, useEffect, useMemo, useState } from 'react';
import { Alert, Button, Form, Spinner } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPaperPlane, faRotateRight } from '@fortawesome/free-solid-svg-icons';

import { ChatMessageView, ChatSession, resumeChatSession, sendChatMessage, startChatSession } from 'app/shared/api/design-chat-api';

const STORAGE_KEY = 'kalitron.designChat.sessionCode';

const DesignChat = () => {
  const [clientName, setClientName] = useState('');
  const [clientEmail, setClientEmail] = useState('');
  const [session, setSession] = useState<ChatSession | null>(null);
  const [messages, setMessages] = useState<ChatMessageView[]>([]);
  const [draft, setDraft] = useState('');
  const [isStarting, setIsStarting] = useState(false);
  const [isSending, setIsSending] = useState(false);
  const [isResuming, setIsResuming] = useState(false);
  const [error, setError] = useState<string | null>(null);

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
      })
      .catch(() => {
        window.localStorage.removeItem(STORAGE_KEY);
      })
      .finally(() => setIsResuming(false));
  }, []);

  const canStart = useMemo(() => clientName.trim().length > 0 && clientEmail.trim().length > 0, [clientEmail, clientName]);
  const canSend = useMemo(() => draft.trim().length > 0 && !!session && !isSending, [draft, isSending, session]);

  const handleStart = async (event: FormEvent) => {
    event.preventDefault();
    if (!canStart) {
      return;
    }

    setError(null);
    setIsStarting(true);
    try {
      const startedSession = await startChatSession({ clientName: clientName.trim(), clientEmail: clientEmail.trim() });
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
    const optimisticMessage: ChatMessageView = {
      role: 'USER',
      content: messageText,
      createdAt: new Date().toISOString(),
    };

    setDraft('');
    setError(null);
    setIsSending(true);
    setMessages(currentMessages => [...currentMessages, optimisticMessage]);

    try {
      const response = await sendChatMessage(session.sessionId, messageText);
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
    setError(null);
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
              <Button type="button" variant="outline-secondary" size="sm" onClick={handleRestart}>
                <FontAwesomeIcon icon={faRotateRight} /> Nueva sesión
              </Button>
            </>
          ) : (
            <Form onSubmit={handleStart}>
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

        <section className="design-chat__conversation" aria-live="polite">
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
          </Form>
        </section>
      </div>
    </main>
  );
};

export default DesignChat;
