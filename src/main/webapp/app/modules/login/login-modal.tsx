import React from 'react';
import { Alert, Button, Col, Form, Modal, ModalBody, ModalFooter, ModalHeader, Row } from 'react-bootstrap';
import { Storage, Translate, ValidatedField, translate } from 'react-jhipster';
import { Link } from 'react-router';
import axios from 'axios';

import { type FieldError, useForm } from 'react-hook-form';
import GoogleAuthButton from 'app/shared/components/GoogleAuthButton';

const AUTH_TOKEN_KEY = 'jhi-authenticationToken';
const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID ?? '';

export interface ILoginModalProps {
  showModal: boolean;
  loginError: boolean;
  handleLogin: (username: string, password: string, rememberMe: boolean) => void;
  handleClose: () => void;
  redirectAfterLogin?: string;
}

const LoginModal = (props: ILoginModalProps) => {
  const login = ({ username, password, rememberMe }) => {
    props.handleLogin(username, password, rememberMe);
  };

  const handleGoogleSuccess = async (credentialResponse: { credential?: string }) => {
    try {
      const response = await axios.post('/api/auth/google', { credential: credentialResponse.credential });
      const { token } = response.data;
      Storage.local.set(AUTH_TOKEN_KEY, token);
      window.location.assign(props.redirectAfterLogin || '/');
    } catch {
      // silent — the button will remain in its error state
    }
  };

  const {
    handleSubmit,
    register,
    formState: { errors, touchedFields },
  } = useForm({ mode: 'onTouched' });

  const { loginError, handleClose } = props;

  const handleLoginSubmit = e => {
    handleSubmit(login)(e);
  };

  return (
    <Modal show={props.showModal} onHide={handleClose} backdrop="static" id="login-page" autoFocus={false}>
      <Form onSubmit={handleLoginSubmit}>
        <ModalHeader id="login-title" data-cy="loginTitle" closeButton>
          <Translate contentKey="login.title">Sign in</Translate>
        </ModalHeader>
        <ModalBody>
          <Row>
            <Col md="12">
              {loginError && (
                <Alert variant="danger" data-cy="loginError">
                  <Translate contentKey="login.messages.error.authentication">
                    <strong>Failed to sign in!</strong> Please check your credentials and try again.
                  </Translate>
                </Alert>
              )}
            </Col>
            <Col md="12">
              <ValidatedField
                name="username"
                label={translate('global.form.username.label')}
                placeholder={translate('global.form.username.placeholder')}
                required
                autoFocus
                data-cy="username"
                validate={{ required: 'Username cannot be empty!' }}
                register={register}
                error={errors.username as FieldError}
                isTouched={touchedFields.username}
              />
              <ValidatedField
                name="password"
                type="password"
                label={translate('login.form.password')}
                placeholder={translate('login.form.password.placeholder')}
                required
                data-cy="password"
                validate={{ required: 'Password cannot be empty!' }}
                register={register}
                error={errors.password as FieldError}
                isTouched={touchedFields.password}
              />
              <ValidatedField
                name="rememberMe"
                type="checkbox"
                check
                label={translate('login.form.rememberme')}
                value={true}
                register={register}
              />
            </Col>
          </Row>
          <div className="mt-1">&nbsp;</div>
          <Alert variant="warning">
            <Link to="/account/reset/request" data-cy="forgetYourPasswordSelector">
              <Translate contentKey="login.password.forgot">Did you forget your password?</Translate>
            </Link>
          </Alert>
          <Alert variant="warning">
            <span>
              <Translate contentKey="global.messages.info.register.noaccount">You don&apos;t have an account yet?</Translate>
            </span>{' '}
            <Link to="/account/register">
              <Translate contentKey="global.messages.info.register.link">Register a new account</Translate>
            </Link>
          </Alert>
        </ModalBody>
        <ModalFooter className="flex-column w-100">
          <div className="d-flex w-100 gap-2">
            <Button variant="secondary" className="w-100" onClick={handleClose} tabIndex={1}>
              <Translate contentKey="entity.action.cancel">Cancel</Translate>
            </Button>
            <Button variant="primary" className="w-100" type="submit" data-cy="submit">
              <Translate contentKey="login.form.button">Sign in</Translate>
            </Button>
          </div>
          {GOOGLE_CLIENT_ID && (
            <>
              <div className="w-100 text-center my-2">
                <span className="text-muted small">
                  <Translate contentKey="login.or">or</Translate>
                </span>
              </div>
              <div className="w-100 d-flex justify-content-center" data-cy="googleLogin">
                <GoogleAuthButton
                  clientId={GOOGLE_CLIENT_ID}
                  label={translate('login.google', undefined, 'Continue with Google')}
                  onCredential={handleGoogleSuccess}
                  onUnavailable={() => undefined}
                />
              </div>
            </>
          )}
        </ModalFooter>
      </Form>
    </Modal>
  );
};

export default LoginModal;
