import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CabinetPart from './cabinet-part';
import CabinetPartDeleteDialog from './cabinet-part-delete-dialog';
import CabinetPartDetail from './cabinet-part-detail';
import CabinetPartUpdate from './cabinet-part-update';

const CabinetPartRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CabinetPart />} />
    <Route path="new" element={<CabinetPartUpdate />} />
    <Route path=":id">
      <Route index element={<CabinetPartDetail />} />
      <Route path="edit" element={<CabinetPartUpdate />} />
      <Route path="delete" element={<CabinetPartDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CabinetPartRoutes;
