package expo.modules.printerdrivers.drivers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.facebook.react.bridge.ReadableMap
import com.woosim.printer.WoosimCmd
import expo.modules.printerdrivers.services.bluetooth.BluetoothService
import expo.modules.printerdrivers.utils.constants.PR3Command
import honeywell.printer.DocumentLP
import java.io.File
import androidx.core.graphics.createBitmap
import expo.modules.printerdrivers.utils.constants.PrinterCharacter
import expo.modules.printerdrivers.utils.helpers.CommonHelper.getStringValueByKey

class HoneywellPR3Driver(bluetoothService: BluetoothService, context: Context) :
    BaseDriver(bluetoothService, context) {
    override var driverName = "HoneywellPR3Driver"
    override var printerPageWidth: Int = 44
    override var separateLineLength: Int = 44
    var imageHeadWidth: Int = 576 // in dots

    override fun initPrinter() {
        buffer.put(PR3Command.INIT)
    }

    private fun wrapTextToWidth(text: String, paint: Paint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        val words = text.split(" ")
        var currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val testWidth = paint.measureText(testLine)

            if (testWidth <= maxWidth) {
                currentLine = StringBuilder(testLine)
            } else {
                // Current line is full, save it and start new line
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.toString())
                    currentLine = StringBuilder(word)
                } else {
                    // Single word is too long, need to break it
                    lines.add(word)
                }
            }
        }

        // Add the last line
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }

        return lines
    }

    private fun createTextBitmap(
        text: String, align: Int, bold: Boolean, doubleFontSize: Boolean,
    ): Bitmap {
        // Configure paint
        val paint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = false // Sharper text for thermal printers
            textSize = if (doubleFontSize) 32f else 24f
            typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

        // Measure text metrics
        val fontMetrics = paint.fontMetrics
        val lineHeight = (fontMetrics.descent - fontMetrics.ascent).toInt()
        val textPadding = 4f

        // Calculate available width for text (with margins)
        val availableWidth = imageHeadWidth - (textPadding * 2)

        // Wrap text into multiple lines that fit within available width
        val wrappedLines = wrapTextToWidth(text, paint, availableWidth)

        // Calculate total bitmap height
        val totalHeight = (lineHeight * wrappedLines.size) + 8 // 4px padding top and bottom

        // Create bitmap
        val bitmap = createBitmap(imageHeadWidth, totalHeight)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        // Draw each line
        var currentY = -fontMetrics.ascent + 4f // Start position with top padding
        for (line in wrappedLines) {
            val lineWidth = paint.measureText(line)

            // Calculate x position based on alignment
            val x = when (align) {
                WoosimCmd.ALIGN_CENTER -> (imageHeadWidth - lineWidth) / 2
                WoosimCmd.ALIGN_RIGHT -> imageHeadWidth - lineWidth - textPadding
                else -> textPadding // Left with margin
            }

            canvas.drawText(line, x, currentY, paint)
            currentY += lineHeight
        }

        return bitmap
    }

    override fun addAlignedStringToBuffer(
        string: String, align: Int, bold: Boolean, doubleFontSize: Boolean
    ) {
        val text = string.trimEnd('\n')
        if (text.isEmpty()) return

        // Render text as bitmap with proper alignment
        val bitmap = createTextBitmap(text, align, bold, doubleFontSize)

        val docLP = DocumentLP("!")
        docLP.writeImage(bitmap, imageHeadWidth)
        buffer.put(docLP.documentData)

        bitmap.recycle()
    }

    override fun addBitmapToBuffer(fileName: String, align: Int) {
        val docLP = DocumentLP("!")

        try {
            val qrImagePath = "${context.cacheDir}/$fileName"
            val imageFile = File(qrImagePath)

            if (imageFile.exists()) {
                val originalBitmap = BitmapFactory.decodeFile(qrImagePath)
                if (originalBitmap != null) {
                    // Create aligned bitmap wrapper
                    val alignedBitmap = createAlignedBitmap(originalBitmap, align)
                    docLP.writeImage(alignedBitmap, imageHeadWidth)
                    alignedBitmap.recycle()
                    originalBitmap.recycle()
                } else {
                    docLP.writeText("ERROR: Failed to decode image")
                }
            } else {
                docLP.writeText("ERROR: $fileName not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            docLP.writeText("ERROR: ${e.message}")
        }

        buffer.put(docLP.documentData)
    }

    private fun createAlignedBitmap(originalBitmap: Bitmap, align: Int): Bitmap {
        // Create a full-width bitmap with white background
        val alignedBitmap = createBitmap(imageHeadWidth, originalBitmap.height)
        val canvas = Canvas(alignedBitmap)
        canvas.drawColor(Color.WHITE)

        // Calculate x position based on alignment
        val x = when (align) {
            WoosimCmd.ALIGN_CENTER -> ((imageHeadWidth - originalBitmap.width) / 2).toFloat()
            WoosimCmd.ALIGN_RIGHT -> (imageHeadWidth - originalBitmap.width).toFloat()
            else -> 0f // Left aligned
        }

        // Draw the original bitmap at the calculated position
        canvas.drawBitmap(originalBitmap, x, 0f, null)

        return alignedBitmap
    }

    override fun addLineFeedsToBuffer(lineNumber: Int) {
        for (i in 1..lineNumber) {
            buffer.put(PR3Command.NEW_LINE)
        }
    }

    override fun addTwoAlignedStringsToBuffer(
        leftString: String,
        rightString: String,
        leftBold: Boolean,
        rightBold: Boolean,
        leftDoubleHeight: Boolean,
        rightDoubleHeight: Boolean,
    ) {
        val leftText = leftString.trimEnd('\n')
        val rightText = rightString.trimEnd('\n')

        if (leftText.isEmpty() && rightText.isEmpty()) return

        // Create a combined bitmap with both texts
        val bitmap = createTwoAlignedTextBitmap(
            leftText, rightText, leftBold, rightBold, leftDoubleHeight, rightDoubleHeight
        )

        val docLP = DocumentLP("!")
        docLP.writeImage(bitmap, imageHeadWidth)
        buffer.put(docLP.documentData)

        bitmap.recycle()
    }

    private fun createTwoAlignedTextBitmap(
        leftText: String,
        rightText: String,
        leftBold: Boolean,
        rightBold: Boolean,
        leftDoubleSize: Boolean,
        rightDoubleSize: Boolean
    ): Bitmap {
        // Configure paints for left and right text
        val leftPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = false
            textSize = if (leftDoubleSize) 32f else 24f
            typeface = if (leftBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

        val rightPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = false
            textSize = if (rightDoubleSize) 32f else 24f
            typeface = if (rightBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

        // Calculate heights
        val leftHeight = leftPaint.fontMetrics.let { it.descent - it.ascent }
        val rightHeight = rightPaint.fontMetrics.let { it.descent - it.ascent }
        val maxHeight = maxOf(leftHeight, rightHeight).toInt() + 8

        val textPadding = 4f

        // Create bitmap
        val bitmap = createBitmap(imageHeadWidth, maxHeight)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        // Draw left text
        if (leftText.isNotEmpty()) {
            val leftY = -leftPaint.fontMetrics.ascent + 4f
            canvas.drawText(leftText, textPadding, leftY, leftPaint)
        }

        // Draw right text
        if (rightText.isNotEmpty()) {
            val rightWidth = rightPaint.measureText(rightText)
            val rightX = imageHeadWidth - rightWidth - textPadding
            val rightY = -rightPaint.fontMetrics.ascent + 4f
            canvas.drawText(rightText, rightX, rightY, rightPaint)
        }

        return bitmap
    }

    override fun addSeparateLineToBuffer() {
        addAlignedStringToBuffer("-".repeat(separateLineLength), WoosimCmd.ALIGN_CENTER)
    }

    override fun giayBaoTienNuocNongThon(jsonData: ReadableMap) {
        val tenCongTy = getStringValueByKey(jsonData, "tenCongTy")
        val tenPhieu = getStringValueByKey(jsonData, "tenPhieu")
        val ky = getStringValueByKey(jsonData, "ky")
        val tuNgay = getStringValueByKey(jsonData, "tuNgay")
        val denNgay = getStringValueByKey(jsonData, "denNgay")
        val mdb = getStringValueByKey(jsonData, "mdb")
        val mlt = getStringValueByKey(jsonData, "mlt")
        val khachHang = getStringValueByKey(jsonData, "khachHang")
        val soDienThoai = getStringValueByKey(jsonData, "soDienThoai")
        val diaChi = getStringValueByKey(jsonData, "diaChi")
        val giaBieu = getStringValueByKey(jsonData, "giaBieu")
        val dinhMuc = getStringValueByKey(jsonData, "dinhMuc")
        val chiSo = getStringValueByKey(jsonData, "chiSo")
        val tienNuoc = getStringValueByKey(jsonData, "tienNuoc")
        val tienKyMoi = getStringValueByKey(jsonData, "tienKyMoi")
        val nhanVien = getStringValueByKey(jsonData, "nhanVien")
        val dienThoaiNhanVien = getStringValueByKey(jsonData, "dienThoaiNhanVien")
        val maQR = getStringValueByKey(jsonData, "maQR")


        addSeparateLineToBuffer()
        addAlignedStringToBuffer(
            tenCongTy, WoosimCmd.ALIGN_CENTER, bold = true
        )
        addSeparateLineToBuffer()
        addAlignedStringToBuffer(
            tenPhieu, WoosimCmd.ALIGN_CENTER, bold = true, doubleFontSize = true
        )
        addAlignedStringToBuffer("KỲ: $ky", WoosimCmd.ALIGN_CENTER, bold = true)
        addAlignedStringToBuffer("$tuNgay - $denNgay", WoosimCmd.ALIGN_CENTER)
        addAlignedStringToBuffer("DB: $mdb - MLT: $mlt", bold = true)
        addAlignedStringToBuffer("KH: $khachHang", bold = true)
        addAlignedStringToBuffer("Điện thoại KH: $soDienThoai")
        addAlignedStringToBuffer("ĐC: $diaChi")
        addAlignedStringToBuffer("Giá biểu: $giaBieu - Định mức: $dinhMuc")
        addTwoAlignedStringsToBuffer(
            leftString = "Chỉ số lala",
            rightString = "$chiSo ${PrinterCharacter.M3}",
            rightBold = true
        )
        addTwoAlignedStringsToBuffer(
            leftString = "Tiền hehe",
            rightString = "$tienNuoc ${PrinterCharacter.VND}",
            rightBold = true
        )
        addAlignedStringToBuffer("-".repeat(10), WoosimCmd.ALIGN_RIGHT)
        addTwoAlignedStringsToBuffer(
            leftString = "Số tiền (kỳ mới)",
            rightString = "$tienKyMoi ${PrinterCharacter.VND}",
            rightBold = true
        )
        addSeparateLineToBuffer()
        addAlignedStringToBuffer("NV: $nhanVien", bold = true)
        addAlignedStringToBuffer("ĐT: $dienThoaiNhanVien", bold = true)
        addAlignedStringToBuffer("Sau 3 ngày làm việc, kể từ ngày ghi chỉ số nước, dữ liệu hoá đơn sẽ được cập nhật tại website:")
        addAlignedStringToBuffer("https://www.example.com", bold = true)
        addAlignedStringToBuffer("Quý khách vui lòng kiểm tra lại số điện thoại trên phiếu báo này và liên hệ đội làm giàu:")
        addAlignedStringToBuffer("(0123) 456789 để cập nhật lại nếu chưa chính xác.")
        addLineFeedsToBuffer()
        addAlignedStringToBuffer(
            "Quét mã QR để thanh toán MOMO", WoosimCmd.ALIGN_CENTER, bold = true
        )
        addBitmapToBuffer(maQR, WoosimCmd.ALIGN_CENTER)

        addLineFeedsToBuffer(2)
    }
}
