import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DesignSession from './design-session';
import DesignSessionDeleteDialog from './design-session-delete-dialog';
import DesignSessionDetail from './design-session-detail';
import DesignSessionUpdate from './design-session-update';

const DesignSessionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DesignSession />} />
    <Route path="new" element={<DesignSessionUpdate />} />
    <Route path=":id">
      <Route index element={<DesignSessionDetail />} />
      <Route path="edit" element={<DesignSessionUpdate />} />
      <Route path="delete" element={<DesignSessionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DesignSessionRoutes;
