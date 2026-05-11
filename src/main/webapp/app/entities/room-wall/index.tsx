import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import RoomWall from './room-wall';
import RoomWallDeleteDialog from './room-wall-delete-dialog';
import RoomWallDetail from './room-wall-detail';
import RoomWallUpdate from './room-wall-update';

const RoomWallRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<RoomWall />} />
    <Route path="new" element={<RoomWallUpdate />} />
    <Route path=":id">
      <Route index element={<RoomWallDetail />} />
      <Route path="edit" element={<RoomWallUpdate />} />
      <Route path="delete" element={<RoomWallDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RoomWallRoutes;
