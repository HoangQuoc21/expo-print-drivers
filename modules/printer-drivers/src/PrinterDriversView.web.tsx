import * as React from 'react';

import { PrinterDriversViewProps } from './PrinterDrivers.types';

export default function PrinterDriversView(props: PrinterDriversViewProps) {
  return (
    <div>
      <iframe
        style={{ flex: 1 }}
        src={props.url}
        onLoad={() => props.onLoad({ nativeEvent: { url: props.url } })}
      />
    </div>
  );
}
