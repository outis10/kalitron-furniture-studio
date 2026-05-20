/// <reference types="cypress" />

beforeEach(() => {
  cy.clearAllLocalStorage();
  cy.clearAllSessionStorage();
});

afterEach(() => {
  cy.clearAllLocalStorage();
  cy.clearAllSessionStorage();
});
