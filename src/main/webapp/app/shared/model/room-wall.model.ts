import { IDesignSession } from 'app/shared/model/design-session.model';
import { ISiteMeasurement } from 'app/shared/model/site-measurement.model';

export interface IRoomWall {
  id?: number;
  name?: string;
  lengthMm?: number;
  heightMm?: number | null;
  angleDeg?: number | null;
  positionX?: number | null;
  positionY?: number | null;
  sortOrder?: number | null;
  lengthFloorMm?: number | null;
  length900Mm?: number | null;
  lengthCeilingMm?: number | null;
  outOfPlumbMm?: number | null;
  closingMm?: number | null;
  heightLeftMm?: number | null;
  heightRightMm?: number | null;
  siteMeasurement?: ISiteMeasurement | null;
  session?: IDesignSession;
}

export const defaultValue: Readonly<IRoomWall> = {};
