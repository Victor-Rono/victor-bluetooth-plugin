import { WebPlugin } from '@capacitor/core';

import type { BluetoothPluginPlugin } from './definitions';

export class BluetoothPluginWeb
  extends WebPlugin
  implements BluetoothPluginPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async load(): Promise<any> {
    console.log('load');
    return 'loading';

  }

  async startClassicScan(): Promise<any> {
    console.log('load');
    return 'loading';

  }

  async connectToDevice(): Promise<any> {
    console.log('load');
    return 'loading';

  }

  async manageConnectedSocket(): Promise<any> {
    console.log('load');
    return 'loading';

  }
}
