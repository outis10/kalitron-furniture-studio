import { ICabinet } from 'app/shared/model/cabinet.model';
import { IMaterial } from 'app/shared/model/material.model';

export interface ICabinetPart {
  id?: number;
  partCode?: string;
  name?: string;
  widthMm?: number;
  heightMm?: number;
  thicknessMm?: number;
  quantity?: number;
  edgeBanding?: string | null;
  grainDirection?: string | null;
  cncOperation?: string | null;
  notes?: string | null;
  material?: IMaterial;
  cabinet?: ICabinet;
}

export const defaultValue: Readonly<ICabinetPart> = {};
