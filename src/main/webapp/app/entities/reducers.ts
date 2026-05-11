import hardware from 'app/entities/hardware/hardware.reducer';
import cabinetTemplate from 'app/entities/cabinet-template/cabinet-template.reducer';
import cabinet from 'app/entities/cabinet/cabinet.reducer';
import cabinetPart from 'app/entities/cabinet-part/cabinet-part.reducer';
import catalogStyle from 'app/entities/catalog-style/catalog-style.reducer';
import chatMessage from 'app/entities/chat-message/chat-message.reducer';
import designArtifact from 'app/entities/design-artifact/design-artifact.reducer';
import designImage from 'app/entities/design-image/design-image.reducer';
import designSession from 'app/entities/design-session/design-session.reducer';
import generationJob from 'app/entities/generation-job/generation-job.reducer';
import kitchenSpec from 'app/entities/kitchen-spec/kitchen-spec.reducer';
import material from 'app/entities/material/material.reducer';
import quote from 'app/entities/quote/quote.reducer';
import quoteItem from 'app/entities/quote-item/quote-item.reducer';
import roomObstacle from 'app/entities/room-obstacle/room-obstacle.reducer';
import roomWall from 'app/entities/room-wall/room-wall.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  designSession,
  chatMessage,
  kitchenSpec,
  roomWall,
  roomObstacle,
  designImage,
  designArtifact,
  generationJob,
  catalogStyle,
  material,
  hardware,
  cabinetTemplate,
  cabinet,
  cabinetPart,
  quote,
  quoteItem,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
