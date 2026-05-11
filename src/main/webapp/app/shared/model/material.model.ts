import { MaterialKind } from 'app/shared/model/enumerations/material-kind.model';

export interface IMaterial {
  id?: number;
  code?: string;
  name?: string;
  materialKind?: keyof typeof MaterialKind;
  thicknessMm?: number | null;
  sheetWidthMm?: number | null;
  sheetHeightMm?: number | null;
  costPerSheetMxn?: number | null;
  costPerSquareMeterMxn?: number | null;
  supplierName?: string | null;
  isActive?: boolean;
  notes?: string | null;
}

export const defaultValue: Readonly<IMaterial> = {
  isActive: false,
};
