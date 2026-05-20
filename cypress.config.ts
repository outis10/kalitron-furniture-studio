import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: process.env.CYPRESS_BASE_URL ?? 'http://localhost:9000',
    defaultCommandTimeout: 10000,
    downloadsFolder: 'build/cypress/downloads',
    requestTimeout: 15000,
    responseTimeout: 15000,
    screenshotsFolder: 'build/cypress/screenshots',
    specPattern: 'cypress/e2e/**/*.cy.ts',
    supportFile: 'cypress/support/e2e.ts',
    video: false,
  },
});
