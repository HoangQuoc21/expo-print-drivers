package expo.modules.printerdrivers.drivers

import com.facebook.react.bridge.ReadableMap
import expo.modules.printerdrivers.bluetoothService.BluetoothService
import expo.modules.printerdrivers.utils.constants.EscPosCommand

class HoneywellPR3Driver(bluetoothService: BluetoothService) : BaseDriver(bluetoothService) {
    override var driverName = "HoneywellPR3Driver"

    override fun initPrinter() {
        buffer.put(EscPosCommand.INIT)
        // Add this for better special character support
        buffer.put(EscPosCommand.CODE_PAGE_CP437)
    }

    override fun giayBaoTienNuocNongThon(jsonData: ReadableMap) {
        clearBuffer()
        initPrinter()

        buffer.put("---------------------------------------.\n".toByteArray())
        buffer.put("The quick brown fox jumps over the lazy dog.\n".toByteArray())
        buffer.put("Gã vội vã bước nhanh qua phố xá.\n".toByteArray())
        buffer.put("---------------------------------------.\n".toByteArray())

        buffer.put(EscPosCommand.FEED_LINES(2))
        buffer.put(EscPosCommand.CUT_FULL)

        sendPrintData()
    }
}
