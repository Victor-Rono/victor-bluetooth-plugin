export interface BluetoothPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  load(): any,
  startClassicScan(options: any): any,
  connectToDevice(options: any): any,
  manageConnectedSocket(options: any): any,

}
