import dayjs from 'dayjs';

import { IDesignSession } from 'app/shared/model/design-session.model';
import { ImageType } from 'app/shared/model/enumerations/image-type.model';

export interface IDesignImage {
  id?: number;
  imageType?: keyof typeof ImageType;
  fileName?: string;
  filePath?: string;
  mimeType?: string | null;
  fileSizeKb?: number | null;
  widthPx?: number | null;
  heightPx?: number | null;
  isActive?: boolean;
  uploadedAt?: dayjs.Dayjs;
  description?: string | null;
  session?: IDesignSession;
}

export const defaultValue: Readonly<IDesignImage> = {
  isActive: false,
};
