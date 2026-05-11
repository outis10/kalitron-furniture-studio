import { IDesignSession } from 'app/shared/model/design-session.model';

export interface IRoomWall {
  id?: number;
  name?: string;
  lengthMm?: number;
  heightMm?: number | null;
  angleDeg?: number | null;
  positionX?: number | null;
  positionY?: number | null;
  sortOrder?: number | null;
  session?: IDesignSession;
}

export const defaultValue: Readonly<IRoomWall> = {};
