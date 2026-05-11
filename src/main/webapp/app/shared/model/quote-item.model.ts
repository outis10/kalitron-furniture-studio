import { ICabinet } from 'app/shared/model/cabinet.model';
import { IHardware } from 'app/shared/model/hardware.model';
import { IQuote } from 'app/shared/model/quote.model';

export interface IQuoteItem {
  id?: number;
  description?: string;
  quantity?: number;
  unitPriceMxn?: number;
  totalMxn?: number;
  category?: string | null;
  notes?: string | null;
  cabinet?: ICabinet | null;
  hardware?: IHardware | null;
  quote?: IQuote;
}

export const defaultValue: Readonly<IQuoteItem> = {};
