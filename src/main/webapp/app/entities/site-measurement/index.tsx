import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SiteMeasurement from './site-measurement';
import SiteMeasurementDeleteDialog from './site-measurement-delete-dialog';
import SiteMeasurementDetail from './site-measurement-detail';
import SiteMeasurementUpdate from './site-measurement-update';

const SiteMeasurementRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SiteMeasurement />} />
    <Route path="new" element={<SiteMeasurementUpdate />} />
    <Route path=":id">
      <Route index element={<SiteMeasurementDetail />} />
      <Route path="edit" element={<SiteMeasurementUpdate />} />
      <Route path="delete" element={<SiteMeasurementDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SiteMeasurementRoutes;
