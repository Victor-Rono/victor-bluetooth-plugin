import Capacitor
import ExternalAccessory

@objc(BluetoothPlugin)
public class BluetoothPlugin: CAPPlugin, EAAccessoryDelegate, EASessionDelegate {
    var accessories: [EAAccessory] = []
    var session: EASession?
    var inputStream: InputStream?
    var outputStream: OutputStream?

    @objc func startClassicScan(_ call: CAPPluginCall) {
        EAAccessoryManager.shared().registerForLocalNotifications()
        self.accessories = EAAccessoryManager.shared().connectedAccessories
        for accessory in accessories {
            let ret = ["address": accessory.serialNumber, "name": accessory.name]
            self.notifyListeners("classicDeviceFound", data: ret)
        }
        call.resolve()
    }

    @objc func connectToDevice(_ call: CAPPluginCall) {
        guard let address = call.getString("address") else {
            call.reject("No device address provided")
            return
        }

        if let accessory = accessories.first(where: { $0.serialNumber == address }) {
            accessory.delegate = self
            if let protocolString = accessory.protocolStrings.first {
                self.session = EASession(accessory: accessory, forProtocol: protocolString)
                self.inputStream = self.session?.inputStream
                self.outputStream = self.session?.outputStream
                self.inputStream?.delegate = self
                self.outputStream?.delegate = self
                self.inputStream?.schedule(in: .current, forMode: .default)
                self.outputStream?.schedule(in: .current, forMode: .default)
                self.inputStream?.open()
                self.outputStream?.open()
                call.resolve()
            } else {
                call.reject("No supported protocol found")
            }
        } else {
            call.reject("Device not found")
        }
    }

    public func accessoryDidDisconnect(_ accessory: EAAccessory) {
        // Handle disconnection
    }

    public func stream(_ aStream: Stream, handle eventCode: Stream.Event) {
        switch eventCode {
        case .hasBytesAvailable:
            if aStream == inputStream {
                var buffer = [UInt8](repeating: 0, count: 1024)
                let bytesRead = inputStream?.read(&buffer, maxLength: buffer.count)
                if bytesRead! > 0 {
                    let receivedData = String(bytes: buffer, encoding: .utf8) ?? ""
                    let ret = ["data": receivedData]
                    self.notifyListeners("dataReceived", data: ret)
                }
            }
        case .endEncountered, .errorOccurred:
            // Handle stream errors or end
            break
        default:
            break
        }
    }
}
