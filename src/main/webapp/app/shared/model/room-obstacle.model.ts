import { IDesignSession } from 'app/shared/model/design-session.model';
import { ApplianceType } from 'app/shared/model/enumerations/appliance-type.model';
import { RoomObstacleType } from 'app/shared/model/enumerations/room-obstacle-type.model';
import { ISiteMeasurement } from 'app/shared/model/site-measurement.model';

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
  wallCode?: string | null;
  croquisCode?: string | null;
  applianceType?: keyof typeof ApplianceType | null;
  siteMeasurement?: ISiteMeasurement | null;
  session?: IDesignSession;
}

export const defaultValue: Readonly<IRoomObstacle> = {};
