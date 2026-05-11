import dayjs from 'dayjs';

import { FinishType } from 'app/shared/model/enumerations/finish-type.model';
import { KitchenLayout } from 'app/shared/model/enumerations/kitchen-layout.model';
import { IMaterial } from 'app/shared/model/material.model';

export interface IKitchenSpec {
  id?: number;
  layout?: keyof typeof KitchenLayout;
  totalWidthMm?: number | null;
  totalHeightMm?: number | null;
  totalDepthMm?: number | null;
  style?: string;
  primaryFinish?: keyof typeof FinishType;
  handleType?: string | null;
  countertopMaterial?: string | null;
  sinkPosition?: string | null;
  confirmedByClient?: boolean;
  extractedJson?: string | null;
  confirmedAt?: dayjs.Dayjs | null;
  primaryMaterial?: IMaterial;
}

export const defaultValue: Readonly<IKitchenSpec> = {
  confirmedByClient: false,
};
