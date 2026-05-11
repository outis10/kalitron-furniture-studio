import dayjs from 'dayjs';

import { IDesignSession } from 'app/shared/model/design-session.model';
import { ArtifactType } from 'app/shared/model/enumerations/artifact-type.model';

export interface IDesignArtifact {
  id?: number;
  artifactType?: keyof typeof ArtifactType;
  fileName?: string;
  filePath?: string;
  mimeType?: string | null;
  fileSizeKb?: number | null;
  checksum?: string | null;
  metadataJson?: string | null;
  createdAt?: dayjs.Dayjs;
  session?: IDesignSession;
}

export const defaultValue: Readonly<IDesignArtifact> = {};
