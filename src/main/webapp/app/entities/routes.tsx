import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Hardware from './hardware';
import CabinetTemplate from './cabinet-template';
import Cabinet from './cabinet';
import CabinetPart from './cabinet-part';
import CatalogStyle from './catalog-style';
import ChatMessage from './chat-message';
import DesignArtifact from './design-artifact';
import DesignImage from './design-image';
import DesignSession from './design-session';
import GenerationJob from './generation-job';
import KitchenSpec from './kitchen-spec';
import Material from './material';
import Quote from './quote';
import QuoteItem from './quote-item';
import RoomObstacle from './room-obstacle';
import RoomWall from './room-wall';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="/design-session/*" element={<DesignSession />} />
        <Route path="/chat-message/*" element={<ChatMessage />} />
        <Route path="/kitchen-spec/*" element={<KitchenSpec />} />
        <Route path="/room-wall/*" element={<RoomWall />} />
        <Route path="/room-obstacle/*" element={<RoomObstacle />} />
        <Route path="/design-image/*" element={<DesignImage />} />
        <Route path="/design-artifact/*" element={<DesignArtifact />} />
        <Route path="/generation-job/*" element={<GenerationJob />} />
        <Route path="/catalog-style/*" element={<CatalogStyle />} />
        <Route path="/material/*" element={<Material />} />
        <Route path="/hardware/*" element={<Hardware />} />
        <Route path="/cabinet-template/*" element={<CabinetTemplate />} />
        <Route path="/cabinet/*" element={<Cabinet />} />
        <Route path="/cabinet-part/*" element={<CabinetPart />} />
        <Route path="/quote/*" element={<Quote />} />
        <Route path="/quote-item/*" element={<QuoteItem />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
