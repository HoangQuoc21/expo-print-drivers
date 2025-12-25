import Foundation

protocol BaseDriver {
    var buffer: NSMutableData { get set }
    var driverName: String { get }
    var printerPageWidth: Int { get }
    var seperatorLineHeight: Int { get }

    func initPrinter()

    func addAlignedStringToBuffer(string: String)

    func addBitmapToBuffer(fileName: String)

    func addLineFeedsToBuffer(lineNumber: Int)

    func addTwoAlignedStringsToBuffer()

    func addThreeAlignedStringsToBuffer()

    func addSeparateLineToBuffer()

    func clearBuffer()

    func sendPrintData()

    func giayBaoTienNuocBenThanh()
}
