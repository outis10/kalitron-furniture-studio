import dayjs from 'dayjs';

import { IDesignSession } from 'app/shared/model/design-session.model';
import { ProjectType } from 'app/shared/model/enumerations/project-type.model';
import { SiteMeasurementStatus } from 'app/shared/model/enumerations/site-measurement-status.model';
import { IUser } from 'app/shared/model/user.model';

export interface ISiteMeasurement {
  id?: number;
  measurementUuid?: string;
  projectType?: keyof typeof ProjectType;
  revision?: number;
  status?: keyof typeof SiteMeasurementStatus;
  schemaVersion?: number;
  catalogVersion?: string;
  payload?: string;
  payloadSha256?: string;
  floorOutOfLevelMm?: number | null;
  floorOutOfLevelNote?: string | null;
  deviceId?: string | null;
  appVersion?: string | null;
  laserModel?: string | null;
  capturedAt?: dayjs.Dayjs | null;
  receivedAt?: dayjs.Dayjs;
  confirmedAt?: dayjs.Dayjs | null;
  session?: IDesignSession;
  measuredBy?: IUser | null;
}

export const defaultValue: Readonly<ISiteMeasurement> = {};
