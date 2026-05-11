import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Hardware from './hardware';
import HardwareDeleteDialog from './hardware-delete-dialog';
import HardwareDetail from './hardware-detail';
import HardwareUpdate from './hardware-update';

const HardwareRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Hardware />} />
    <Route path="new" element={<HardwareUpdate />} />
    <Route path=":id">
      <Route index element={<HardwareDetail />} />
      <Route path="edit" element={<HardwareUpdate />} />
      <Route path="delete" element={<HardwareDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default HardwareRoutes;
