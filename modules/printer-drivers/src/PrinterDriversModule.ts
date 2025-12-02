import { NativeModule, requireNativeModule } from 'expo';

import { PrinterDriversModuleEvents } from './PrinterDrivers.types';

declare class PrinterDriversModule extends NativeModule<PrinterDriversModuleEvents> {
  PI: number;
  hello(): string;
  setValueAsync(value: string): Promise<void>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<PrinterDriversModule>('PrinterDrivers');
