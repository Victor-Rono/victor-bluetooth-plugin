import { registerPlugin } from '@capacitor/core';

import type { BluetoothPluginPlugin } from './definitions';

const BluetoothPlugin = registerPlugin<BluetoothPluginPlugin>(
  'BluetoothPlugin',
  {
    web: () => import('./web').then(m => new m.BluetoothPluginWeb()),
  },
);

export * from './definitions';
export { BluetoothPlugin };
