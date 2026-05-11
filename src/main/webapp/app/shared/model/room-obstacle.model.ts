import { IDesignSession } from 'app/shared/model/design-session.model';
import { RoomObstacleType } from 'app/shared/model/enumerations/room-obstacle-type.model';

export interface IRoomObstacle {
  id?: number;
  obstacleType?: keyof typeof RoomObstacleType;
  label?: string | null;
  xMm?: number;
  yMm?: number | null;
  zMm?: number | null;
  widthMm?: number | null;
  heightMm?: number | null;
  depthMm?: number | null;
  notes?: string | null;
  session?: IDesignSession;
}

export const defaultValue: Readonly<IRoomObstacle> = {};
