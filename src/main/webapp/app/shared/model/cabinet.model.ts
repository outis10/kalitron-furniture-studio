import { ICabinetTemplate } from 'app/shared/model/cabinet-template.model';
import { CabinetCategory } from 'app/shared/model/enumerations/cabinet-category.model';
import { FinishType } from 'app/shared/model/enumerations/finish-type.model';
import { IKitchenSpec } from 'app/shared/model/kitchen-spec.model';
import { IMaterial } from 'app/shared/model/material.model';

export interface ICabinet {
  id?: number;
  cabinetCode?: string;
  category?: keyof typeof CabinetCategory;
  label?: string;
  widthMm?: number;
  heightMm?: number;
  depthMm?: number;
  doors?: number;
  drawers?: number;
  shelves?: number | null;
  finish?: keyof typeof FinishType;
  positionX?: number | null;
  positionY?: number | null;
  positionZ?: number | null;
  rotationDeg?: number | null;
  positionSeq?: number | null;
  csvRowJson?: string | null;
  notes?: string | null;
  template?: ICabinetTemplate | null;
  material?: IMaterial;
  spec?: IKitchenSpec;
}

export const defaultValue: Readonly<ICabinet> = {};
