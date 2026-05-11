import React, { useEffect, useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router';
import axios from 'axios';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { login } from 'app/shared/reducers/authentication';

import LoginModal from './login-modal';

export const Login = () => {
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const loginError = useAppSelector(state => state.authentication.loginError);
  const showModalLogin = useAppSelector(state => state.authentication.showModalLogin);
  const [showModal, setShowModal] = useState(showModalLogin);
  const [googleClientId, setGoogleClientId] = useState('');
  const navigate = useNavigate();
  const pageLocation = useLocation();

  useEffect(() => {
    setShowModal(true);
    axios.get<{ clientId: string }>('/api/auth/google/client-id').then(res => {
      if (res.data?.clientId) setGoogleClientId(res.data.clientId);
    });
  }, []);

  const handleLogin = (username, password, rememberMe = false) => dispatch(login(username, password, rememberMe));

  const handleClose = () => {
    setShowModal(false);
    navigate('/');
  };

  const { from } = pageLocation.state || { from: { pathname: '/', search: pageLocation.search } };
  if (isAuthenticated) {
    return <Navigate to={from} replace />;
  }

  const redirectAfterLogin = from?.pathname ?? '/';
  return (
    <LoginModal
      showModal={showModal}
      handleLogin={handleLogin}
      handleClose={handleClose}
      loginError={loginError}
      redirectAfterLogin={redirectAfterLogin}
      googleClientId={googleClientId}
    />
  );
};

export default Login;
