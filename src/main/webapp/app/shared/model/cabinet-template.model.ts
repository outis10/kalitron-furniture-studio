import { CabinetCategory } from 'app/shared/model/enumerations/cabinet-category.model';

export interface ICabinetTemplate {
  id?: number;
  code?: string;
  name?: string;
  category?: keyof typeof CabinetCategory;
  defaultWidthMm?: number | null;
  defaultHeightMm?: number | null;
  defaultDepthMm?: number | null;
  minWidthMm?: number | null;
  maxWidthMm?: number | null;
  supportsDoors?: boolean;
  supportsDrawers?: boolean;
  supportsShelves?: boolean;
  fusionTemplateName?: string | null;
  csvProfileJson?: string | null;
  isActive?: boolean;
  sortOrder?: number | null;
}

export const defaultValue: Readonly<ICabinetTemplate> = {
  supportsDoors: false,
  supportsDrawers: false,
  supportsShelves: false,
  isActive: false,
};
