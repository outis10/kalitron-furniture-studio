import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/design-session">
        <Translate contentKey="global.menu.entities.designSession" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/chat-message">
        <Translate contentKey="global.menu.entities.chatMessage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/kitchen-spec">
        <Translate contentKey="global.menu.entities.kitchenSpec" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/room-wall">
        <Translate contentKey="global.menu.entities.roomWall" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/room-obstacle">
        <Translate contentKey="global.menu.entities.roomObstacle" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/design-image">
        <Translate contentKey="global.menu.entities.designImage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/design-artifact">
        <Translate contentKey="global.menu.entities.designArtifact" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/generation-job">
        <Translate contentKey="global.menu.entities.generationJob" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/catalog-style">
        <Translate contentKey="global.menu.entities.catalogStyle" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/material">
        <Translate contentKey="global.menu.entities.material" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/hardware">
        <Translate contentKey="global.menu.entities.hardware" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/cabinet-template">
        <Translate contentKey="global.menu.entities.cabinetTemplate" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/cabinet">
        <Translate contentKey="global.menu.entities.cabinet" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/cabinet-part">
        <Translate contentKey="global.menu.entities.cabinetPart" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/quote">
        <Translate contentKey="global.menu.entities.quote" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/quote-item">
        <Translate contentKey="global.menu.entities.quoteItem" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
