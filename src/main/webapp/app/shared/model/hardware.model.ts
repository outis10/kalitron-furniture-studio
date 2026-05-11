import { HardwareType } from 'app/shared/model/enumerations/hardware-type.model';

export interface IHardware {
  id?: number;
  code?: string;
  name?: string;
  hardwareType?: keyof typeof HardwareType;
  unitCostMxn?: number;
  supplierName?: string | null;
  isActive?: boolean;
  notes?: string | null;
}

export const defaultValue: Readonly<IHardware> = {
  isActive: false,
};
