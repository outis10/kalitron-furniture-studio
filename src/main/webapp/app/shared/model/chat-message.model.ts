import dayjs from 'dayjs';

import { IDesignSession } from 'app/shared/model/design-session.model';
import { MessageRole } from 'app/shared/model/enumerations/message-role.model';

export interface IChatMessage {
  id?: number;
  role?: keyof typeof MessageRole;
  content?: string;
  tokenCount?: number | null;
  createdAt?: dayjs.Dayjs;
  session?: IDesignSession;
}

export const defaultValue: Readonly<IChatMessage> = {};
