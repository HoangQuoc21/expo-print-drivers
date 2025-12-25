import PrinterDriversModule from "./src/PrinterDriversModule";

export * from "./src/PrinterDrivers.types";
export * from "./src/hooks";

export const BluetoothService = {
  getState: () => PrinterDriversModule.getBluetoothState(),
  isAvailable: () => PrinterDriversModule.isBluetoothAvailable(),
  isEnabled: () => PrinterDriversModule.isBluetoothEnabled(),
  async getPairedDevices() {
    return await PrinterDriversModule.getPairedDevices();
  },
  async connect(address: string, secure: boolean) {
    return await PrinterDriversModule.connect(address, secure);
  },
  async disconnect() {
    return await PrinterDriversModule.disconnect();
  },
};

export const TicketPrinter = {
  giayBaoTienNuocBenThanh(printerType: string, jsonData: object) {
    PrinterDriversModule.giayBaoTienNuocBenThanh(printerType, jsonData);
  },
};

export const PrinterType = PrinterDriversModule.PrinterType;
