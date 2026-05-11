import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import KitchenSpec from './kitchen-spec';
import KitchenSpecDeleteDialog from './kitchen-spec-delete-dialog';
import KitchenSpecDetail from './kitchen-spec-detail';
import KitchenSpecUpdate from './kitchen-spec-update';

const KitchenSpecRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<KitchenSpec />} />
    <Route path="new" element={<KitchenSpecUpdate />} />
    <Route path=":id">
      <Route index element={<KitchenSpecDetail />} />
      <Route path="edit" element={<KitchenSpecUpdate />} />
      <Route path="delete" element={<KitchenSpecDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default KitchenSpecRoutes;
