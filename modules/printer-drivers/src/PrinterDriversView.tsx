import { requireNativeView } from 'expo';
import * as React from 'react';

import { PrinterDriversViewProps } from './PrinterDrivers.types';

const NativeView: React.ComponentType<PrinterDriversViewProps> =
  requireNativeView('PrinterDrivers');

export default function PrinterDriversView(props: PrinterDriversViewProps) {
  return <NativeView {...props} />;
}
