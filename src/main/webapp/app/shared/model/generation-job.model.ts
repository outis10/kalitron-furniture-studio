import dayjs from 'dayjs';

import { IDesignArtifact } from 'app/shared/model/design-artifact.model';
import { IDesignSession } from 'app/shared/model/design-session.model';
import { GenerationJobStatus } from 'app/shared/model/enumerations/generation-job-status.model';
import { GenerationJobType } from 'app/shared/model/enumerations/generation-job-type.model';

export interface IGenerationJob {
  id?: number;
  jobType?: keyof typeof GenerationJobType;
  status?: keyof typeof GenerationJobStatus;
  inputJson?: string | null;
  outputJson?: string | null;
  errorMessage?: string | null;
  createdAt?: dayjs.Dayjs;
  startedAt?: dayjs.Dayjs | null;
  finishedAt?: dayjs.Dayjs | null;
  artifact?: IDesignArtifact | null;
  session?: IDesignSession;
}

export const defaultValue: Readonly<IGenerationJob> = {};
