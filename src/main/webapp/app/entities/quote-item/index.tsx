import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import QuoteItem from './quote-item';
import QuoteItemDeleteDialog from './quote-item-delete-dialog';
import QuoteItemDetail from './quote-item-detail';
import QuoteItemUpdate from './quote-item-update';

const QuoteItemRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<QuoteItem />} />
    <Route path="new" element={<QuoteItemUpdate />} />
    <Route path=":id">
      <Route index element={<QuoteItemDetail />} />
      <Route path="edit" element={<QuoteItemUpdate />} />
      <Route path="delete" element={<QuoteItemDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default QuoteItemRoutes;
