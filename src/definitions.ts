export interface BluetoothPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
