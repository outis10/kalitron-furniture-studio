import dayjs from 'dayjs';

import { IDesignSession } from 'app/shared/model/design-session.model';
import { ImageType } from 'app/shared/model/enumerations/image-type.model';
import { ISiteMeasurement } from 'app/shared/model/site-measurement.model';

export interface IDesignImage {
  id?: number;
  imageType?: keyof typeof ImageType;
  fileName?: string;
  filePath?: string;
  imageDataBase64?: string | null;
  mimeType?: string | null;
  fileSizeKb?: number | null;
  widthPx?: number | null;
  heightPx?: number | null;
  isActive?: boolean;
  uploadedAt?: dayjs.Dayjs;
  description?: string | null;
  wallCode?: string | null;
  photoUuid?: string | null;
  sha256?: string | null;
  siteMeasurement?: ISiteMeasurement | null;
  session?: IDesignSession;
}

export const defaultValue: Readonly<IDesignImage> = {
  isActive: false,
};
