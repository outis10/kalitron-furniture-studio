import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import RoomObstacle from './room-obstacle';
import RoomObstacleDeleteDialog from './room-obstacle-delete-dialog';
import RoomObstacleDetail from './room-obstacle-detail';
import RoomObstacleUpdate from './room-obstacle-update';

const RoomObstacleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<RoomObstacle />} />
    <Route path="new" element={<RoomObstacleUpdate />} />
    <Route path=":id">
      <Route index element={<RoomObstacleDetail />} />
      <Route path="edit" element={<RoomObstacleUpdate />} />
      <Route path="delete" element={<RoomObstacleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RoomObstacleRoutes;
