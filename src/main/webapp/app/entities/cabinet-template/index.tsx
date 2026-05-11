import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CabinetTemplate from './cabinet-template';
import CabinetTemplateDeleteDialog from './cabinet-template-delete-dialog';
import CabinetTemplateDetail from './cabinet-template-detail';
import CabinetTemplateUpdate from './cabinet-template-update';

const CabinetTemplateRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CabinetTemplate />} />
    <Route path="new" element={<CabinetTemplateUpdate />} />
    <Route path=":id">
      <Route index element={<CabinetTemplateDetail />} />
      <Route path="edit" element={<CabinetTemplateUpdate />} />
      <Route path="delete" element={<CabinetTemplateDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CabinetTemplateRoutes;
