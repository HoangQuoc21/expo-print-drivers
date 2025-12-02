import { registerWebModule, NativeModule } from 'expo';

import { ChangeEventPayload } from './PrinterDrivers.types';

type PrinterDriversModuleEvents = {
  onChange: (params: ChangeEventPayload) => void;
}

class PrinterDriversModule extends NativeModule<PrinterDriversModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit('onChange', { value });
  }
  hello() {
    return 'Hello world! 👋';
  }
};

export default registerWebModule(PrinterDriversModule, 'PrinterDriversModule');
