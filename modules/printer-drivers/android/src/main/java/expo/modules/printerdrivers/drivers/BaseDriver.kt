package expo.modules.printerdrivers.drivers

import com.facebook.react.bridge.ReadableMap
import expo.modules.printerdrivers.bluetoothService.BluetoothService
import expo.modules.printerdrivers.utils.constants.MemoryUnit.KiB
import java.nio.ByteBuffer

abstract class BaseDriver (private val bluetoothService: BluetoothService) {
    var buffer: ByteBuffer = ByteBuffer.allocate(50 * KiB)
    abstract var driverName: String


    protected fun clearBuffer() {
        buffer.clear()
    }
    protected fun sendPrintData() {
        bluetoothService.write(buffer.array())
    }
    abstract fun initPrinter()
    abstract fun giayBaoTienNuocNongThon(jsonData: ReadableMap)
}