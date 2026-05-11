import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DesignImage from './design-image';
import DesignImageDeleteDialog from './design-image-delete-dialog';
import DesignImageDetail from './design-image-detail';
import DesignImageUpdate from './design-image-update';

const DesignImageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DesignImage />} />
    <Route path="new" element={<DesignImageUpdate />} />
    <Route path=":id">
      <Route index element={<DesignImageDetail />} />
      <Route path="edit" element={<DesignImageUpdate />} />
      <Route path="delete" element={<DesignImageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DesignImageRoutes;
