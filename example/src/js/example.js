import { BluetoothPlugin } from 'victor-bluetooth-plugin';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    BluetoothPlugin.echo({ value: inputValue })
}
