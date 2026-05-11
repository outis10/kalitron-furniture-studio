import { FinishType } from 'app/shared/model/enumerations/finish-type.model';

export interface ICatalogStyle {
  id?: number;
  name?: string;
  description?: string | null;
  thumbnailPath?: string;
  style?: string | null;
  primaryFinish?: keyof typeof FinishType | null;
  priceRange?: string | null;
  isActive?: boolean;
  sortOrder?: number | null;
}

export const defaultValue: Readonly<ICatalogStyle> = {
  isActive: false,
};
