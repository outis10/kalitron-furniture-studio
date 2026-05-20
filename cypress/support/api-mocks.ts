/// <reference types="cypress" />

const testUser = {
  activated: true,
  authorities: ['ROLE_USER'],
  email: 'designer@example.com',
  firstName: 'Demo',
  langKey: 'es',
  lastName: 'Designer',
  login: 'user',
};

export const mockAuthenticatedUser = () => {
  cy.intercept('GET', '**/management/info', {
    activeProfiles: ['dev', 'api-docs'],
    'display-ribbon-on-profiles': 'dev',
  }).as('managementInfo');

  cy.intercept('GET', '**/api/auth/google/client-id', { clientId: '' }).as('googleClientId');
  cy.intercept('POST', '**/api/authenticate', {
    body: {},
    headers: {
      authorization: 'Bearer cypress-test-token',
    },
  }).as('authenticate');

  cy.intercept('GET', '**/api/account', req => {
    if (req.headers.authorization) {
      req.reply(testUser);
      return;
    }

    req.reply({ statusCode: 401, body: {} });
  }).as('account');
};
