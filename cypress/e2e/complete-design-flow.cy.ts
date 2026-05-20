/// <reference types="cypress" />

import { mockAuthenticatedUser } from '../support/api-mocks';

const sessionCode = 'KD-2026-019';
const conceptImageUrl = `data:image/svg+xml;utf8,${encodeURIComponent(`
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 960 540">
    <rect width="960" height="540" fill="#f8fafc"/>
    <rect x="80" y="120" width="800" height="300" rx="16" fill="#ffffff" stroke="#cbd5e1" stroke-width="8"/>
    <rect x="120" y="160" width="170" height="210" rx="8" fill="#f1f5f9"/>
    <rect x="320" y="160" width="170" height="210" rx="8" fill="#e2e8f0"/>
    <rect x="520" y="160" width="170" height="210" rx="8" fill="#f1f5f9"/>
    <rect x="720" y="160" width="110" height="210" rx="8" fill="#e2e8f0"/>
    <rect x="120" y="385" width="710" height="18" fill="#ffffff"/>
    <text x="480" y="80" text-anchor="middle" font-family="Arial" font-size="34" fill="#0f172a">Mocked matte white L kitchen</text>
  </svg>
`)}`;

const specsSummary = `Resumen de diseño:

• Tipo de proyecto: Cocina
• Estilo: Moderno Blanco
• Dimensiones: 300 cm x 250 cm
• Distribución: En L
• Material: Melamina
• Acabado/Color: Blanco mate`;

const catalogStyles = [
  {
    id: 1,
    code: 'MODERNO_BLANCO',
    name: 'Moderno Blanco',
    style: 'moderno',
    description: 'Frentes blancos limpios y superficies lisas.',
    thumbnailPath: '/content/images/moderno-blanco.jpg',
    priceRange: 'MEDIO',
    sortOrder: 1,
    isActive: true,
  },
];

describe('complete AI design flow', () => {
  it('creates a mocked visual concept and opens a public proposal page', () => {
    mockAuthenticatedUser();

    cy.intercept('GET', '**/api/catalog-styles', catalogStyles).as('catalogStyles');
    cy.intercept('POST', '**/api/chat/sessions', req => {
      expect(req.body).to.include({
        clientName: 'Cliente Cypress',
        clientEmail: 'cliente.cypress@example.com',
        selectedStyle: 'Moderno Blanco',
      });
      req.reply({
        sessionId: 1901,
        sessionCode,
        clientName: 'Cliente Cypress',
        clientEmail: 'cliente.cypress@example.com',
        selectedStyle: 'Moderno Blanco',
        projectType: 'KITCHEN',
        status: 'CHATTING',
        messages: [
          {
            role: 'ASSISTANT',
            content: 'Hola Cliente Cypress, soy tu diseñador IA de Kalitron. ¿Tu proyecto es cocina, closet o ambos?',
            createdAt: '2026-05-20T12:00:00Z',
          },
        ],
      });
    }).as('startSession');

    let chatMessageCount = 0;
    cy.intercept('POST', '**/api/chat/message', req => {
      chatMessageCount += 1;

      if (chatMessageCount === 1) {
        expect(req.body.message).to.equal('I want an L-shaped kitchen in matte white');
        req.reply({
          sessionId: 1901,
          sessionCode,
          reply: 'Perfecto. ¿Qué medidas aproximadas tiene tu cocina? ¿Confirmas que será en L?',
          specsReady: false,
        });
        return;
      }

      expect(req.body.message).to.equal('Si, cocina en L de 300 cm x 250 cm');
      req.reply({
        sessionId: 1901,
        sessionCode,
        reply: `${specsSummary}\n\n¿Es correcto este resumen?`,
        specsReady: true,
        specsSummary: {},
      });
    }).as('chatMessage');

    cy.intercept('POST', '**/api/chat/visual-concept', req => {
      expect(req.body).to.include({
        sessionId: 1901,
        layout: 'L',
        finish: 'blanco mate',
      });
      req.reply({
        sessionId: 1901,
        sessionCode,
        imageUrl: conceptImageUrl,
        promptUsed: 'mocked Cypress visual prompt',
        pipeline: 'mocked',
        badge: 'Mocked AI Gateway',
      });
    }).as('visualConcept');

    cy.intercept('GET', `**/api/public/proposals/${sessionCode}`, {
      sessionId: 1901,
      sessionCode,
      clientName: 'Cliente Cypress',
      projectType: 'KITCHEN',
      status: 'VISUAL_GENERATED',
      selectedStyle: 'Moderno Blanco',
      updatedAt: '2026-05-20T12:05:00Z',
      renderImageUrl: conceptImageUrl,
      renderBadge: 'Mocked AI Gateway',
      specsSummary,
      cabinetCount: 6,
    }).as('proposal');

    cy.visit('/design-chat');
    cy.get('[data-cy="username"]').type('user');
    cy.get('[data-cy="password"]').type('user');
    cy.get('[data-cy="submit"]').click();
    cy.wait('@authenticate');
    cy.contains('Diseño IA');

    cy.wait('@catalogStyles');
    cy.contains('[role="button"]', 'Moderno Blanco').click();
    cy.get('#clientName').type('Cliente Cypress');
    cy.get('#clientEmail').type('cliente.cypress@example.com');
    cy.contains('button', 'Iniciar sesión').click();
    cy.wait('@startSession');
    cy.contains(sessionCode);

    cy.get('textarea').type('I want an L-shaped kitchen in matte white');
    cy.get('[aria-label="Enviar mensaje"]').click();
    cy.wait('@chatMessage');
    cy.contains('¿Confirmas que será en L?');

    cy.get('textarea').type('Si, cocina en L de 300 cm x 250 cm');
    cy.get('[aria-label="Enviar mensaje"]').click();
    cy.wait('@chatMessage');
    cy.contains('¿Es correcto este resumen?');

    cy.get('#visualLayout').select('En L');
    cy.get('#visualFinish').select('Blanco mate');
    cy.contains('button', 'Generar concepto').click();
    cy.wait('@visualConcept');
    cy.contains('Concepto visual generado.');
    cy.contains('Mocked AI Gateway');

    cy.contains('a', 'Ver propuesta').click();
    cy.wait('@proposal');
    cy.location('pathname').should('eq', `/proposal/${sessionCode}`);
    cy.contains(`Cocina ${sessionCode}`);
    cy.contains('Gabinetes');
    cy.contains('6');
    cy.contains('Blanco mate');
    cy.get('.design-proposal__render img').should('have.attr', 'src', conceptImageUrl);
  });
});
