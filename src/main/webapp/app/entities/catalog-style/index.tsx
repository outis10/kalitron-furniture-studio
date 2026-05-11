import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CatalogStyle from './catalog-style';
import CatalogStyleDeleteDialog from './catalog-style-delete-dialog';
import CatalogStyleDetail from './catalog-style-detail';
import CatalogStyleUpdate from './catalog-style-update';

const CatalogStyleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CatalogStyle />} />
    <Route path="new" element={<CatalogStyleUpdate />} />
    <Route path=":id">
      <Route index element={<CatalogStyleDetail />} />
      <Route path="edit" element={<CatalogStyleUpdate />} />
      <Route path="delete" element={<CatalogStyleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CatalogStyleRoutes;
