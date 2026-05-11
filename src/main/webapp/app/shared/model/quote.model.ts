import dayjs from 'dayjs';

import { IDesignArtifact } from 'app/shared/model/design-artifact.model';
import { IDesignImage } from 'app/shared/model/design-image.model';
import { IDesignSession } from 'app/shared/model/design-session.model';
import { QuoteStatus } from 'app/shared/model/enumerations/quote-status.model';

export interface IQuote {
  id?: number;
  quoteNumber?: string;
  status?: keyof typeof QuoteStatus;
  subtotalMxn?: number;
  taxMxn?: number;
  totalMxn?: number;
  laborMxn?: number | null;
  validUntil?: dayjs.Dayjs | null;
  publicToken?: string | null;
  notes?: string | null;
  createdAt?: dayjs.Dayjs;
  sentAt?: dayjs.Dayjs | null;
  renderImage?: IDesignImage | null;
  pdfArtifact?: IDesignArtifact | null;
  session?: IDesignSession;
}

export const defaultValue: Readonly<IQuote> = {};
