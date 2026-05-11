import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import GenerationJob from './generation-job';
import GenerationJobDeleteDialog from './generation-job-delete-dialog';
import GenerationJobDetail from './generation-job-detail';
import GenerationJobUpdate from './generation-job-update';

const GenerationJobRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<GenerationJob />} />
    <Route path="new" element={<GenerationJobUpdate />} />
    <Route path=":id">
      <Route index element={<GenerationJobDetail />} />
      <Route path="edit" element={<GenerationJobUpdate />} />
      <Route path="delete" element={<GenerationJobDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default GenerationJobRoutes;
