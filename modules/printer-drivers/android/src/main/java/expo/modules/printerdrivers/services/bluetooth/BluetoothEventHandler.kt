package expo.modules.printerdrivers.services.bluetooth

/**
 * Interface for handling Bluetooth events.
 */

interface BluetoothEventHandler {
    fun onDeviceConnected(deviceName: String, deviceAddress: String)
    fun onDeviceDisconnected()
    fun onConnectionFailed(error: String)
    fun onConnectionLost()
    fun onDataReceived(data: ByteArray)
}