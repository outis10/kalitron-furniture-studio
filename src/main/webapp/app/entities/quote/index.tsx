import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Quote from './quote';
import QuoteDeleteDialog from './quote-delete-dialog';
import QuoteDetail from './quote-detail';
import QuoteUpdate from './quote-update';

const QuoteRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Quote />} />
    <Route path="new" element={<QuoteUpdate />} />
    <Route path=":id">
      <Route index element={<QuoteDetail />} />
      <Route path="edit" element={<QuoteUpdate />} />
      <Route path="delete" element={<QuoteDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default QuoteRoutes;
