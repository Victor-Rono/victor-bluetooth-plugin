// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "VictorBluetoothPlugin",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "VictorBluetoothPlugin",
            targets: ["BluetoothPluginPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "BluetoothPluginPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/BluetoothPluginPlugin"),
        .testTarget(
            name: "BluetoothPluginPluginTests",
            dependencies: ["BluetoothPluginPlugin"],
            path: "ios/Tests/BluetoothPluginPluginTests")
    ]
)