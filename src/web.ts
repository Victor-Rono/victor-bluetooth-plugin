import { WebPlugin } from '@capacitor/core';

import type { BluetoothPluginPlugin } from './definitions';

export class BluetoothPluginWeb
  extends WebPlugin
  implements BluetoothPluginPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
