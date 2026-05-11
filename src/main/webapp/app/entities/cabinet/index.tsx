import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Cabinet from './cabinet';
import CabinetDeleteDialog from './cabinet-delete-dialog';
import CabinetDetail from './cabinet-detail';
import CabinetUpdate from './cabinet-update';

const CabinetRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Cabinet />} />
    <Route path="new" element={<CabinetUpdate />} />
    <Route path=":id">
      <Route index element={<CabinetDetail />} />
      <Route path="edit" element={<CabinetUpdate />} />
      <Route path="delete" element={<CabinetDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CabinetRoutes;
