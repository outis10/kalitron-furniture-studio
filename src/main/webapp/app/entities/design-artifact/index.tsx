import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DesignArtifact from './design-artifact';
import DesignArtifactDeleteDialog from './design-artifact-delete-dialog';
import DesignArtifactDetail from './design-artifact-detail';
import DesignArtifactUpdate from './design-artifact-update';

const DesignArtifactRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DesignArtifact />} />
    <Route path="new" element={<DesignArtifactUpdate />} />
    <Route path=":id">
      <Route index element={<DesignArtifactDetail />} />
      <Route path="edit" element={<DesignArtifactUpdate />} />
      <Route path="delete" element={<DesignArtifactDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DesignArtifactRoutes;
