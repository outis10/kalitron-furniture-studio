import React, { useEffect, useRef, useState } from 'react';
import { Button } from 'react-bootstrap';

type GoogleCredentialResponse = {
  credential?: string;
};

type GooglePromptMomentNotification = {
  isNotDisplayed?: () => boolean;
  isSkippedMoment?: () => boolean;
};

type GoogleAccountsIdConfig = {
  client_id: string;
  callback: (response: GoogleCredentialResponse) => void;
  auto_select?: boolean;
  use_fedcm_for_prompt?: boolean;
  ux_mode?: 'popup' | 'redirect';
};

declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (config: GoogleAccountsIdConfig) => void;
          prompt: (callback?: (notification: GooglePromptMomentNotification) => void) => void;
        };
      };
    };
  }
}

interface GoogleAuthButtonProps {
  clientId: string;
  label: string;
  onCredential: (response: GoogleCredentialResponse) => Promise<void> | void;
  onUnavailable?: () => void;
  disabled?: boolean;
  className?: string;
}

const GoogleAuthButton = ({ clientId, label, onCredential, onUnavailable, disabled = false, className }: GoogleAuthButtonProps) => {
  const [isReady, setIsReady] = useState(false);
  const isInitializedRef = useRef(false);

  useEffect(() => {
    let cancelled = false;

    const initializeGoogle = () => {
      if (cancelled || isInitializedRef.current || !window.google?.accounts?.id) {
        return Boolean(window.google?.accounts?.id);
      }

      window.google.accounts.id.initialize({
        client_id: clientId,
        callback(response) {
          void onCredential(response);
        },
        auto_select: false,
        use_fedcm_for_prompt: true,
        ux_mode: 'popup',
      });

      isInitializedRef.current = true;
      setIsReady(true);
      return true;
    };

    if (initializeGoogle()) {
      return () => {
        cancelled = true;
      };
    }

    const intervalId = window.setInterval(() => {
      if (initializeGoogle()) {
        window.clearInterval(intervalId);
      }
    }, 250);

    return () => {
      cancelled = true;
      window.clearInterval(intervalId);
    };
  }, [clientId, onCredential]);

  const handleClick = () => {
    if (!window.google?.accounts?.id || !isReady) {
      onUnavailable?.();
      return;
    }

    window.google.accounts.id.prompt((notification?: GooglePromptMomentNotification) => {
      if (!notification) return;
      if (notification.isNotDisplayed?.() || notification.isSkippedMoment?.()) {
        onUnavailable?.();
      }
    });
  };

  return (
    <Button
      type="button"
      variant="light"
      className={`google-auth-button d-flex align-items-center gap-2 ${className ?? ''}`.trim()}
      onClick={handleClick}
      disabled={disabled || !isReady}
    >
      <img src="content/images/google-g-logo.svg" alt="" aria-hidden="true" width="18" height="18" />
      <span>{label}</span>
    </Button>
  );
};

export default GoogleAuthButton;
