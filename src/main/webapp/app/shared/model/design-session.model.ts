import dayjs from 'dayjs';

import { ICatalogStyle } from 'app/shared/model/catalog-style.model';
import { ProjectType } from 'app/shared/model/enumerations/project-type.model';
import { SessionStatus } from 'app/shared/model/enumerations/session-status.model';
import { IKitchenSpec } from 'app/shared/model/kitchen-spec.model';

export interface IDesignSession {
  id?: number;
  sessionCode?: string;
  projectType?: keyof typeof ProjectType;
  status?: keyof typeof SessionStatus;
  clientName?: string;
  clientEmail?: string | null;
  clientPhone?: string | null;
  selectedStyle?: string | null;
  notes?: string | null;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs;
  spec?: IKitchenSpec | null;
  catalogStyle?: ICatalogStyle | null;
}

export const defaultValue: Readonly<IDesignSession> = {};
