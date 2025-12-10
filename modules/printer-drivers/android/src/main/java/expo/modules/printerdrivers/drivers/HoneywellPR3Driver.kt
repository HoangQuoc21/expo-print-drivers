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
import expo.modules.printerdrivers.utils.helpers.CommonHelper
import honeywell.printer.DocumentLP
import java.io.File
import androidx.core.graphics.createBitmap

class HoneywellPR3Driver(bluetoothService: BluetoothService, context: Context) :
    BaseDriver(bluetoothService, context) {
    override var driverName = "HoneywellPR3Driver"
    override var printerPageWidth: Int = 53
    override var separateLineLength: Int = 72
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
            textSize = if (doubleFontSize) 32f else 16f
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

    override fun addBitmapToBuffer(fileName: String) {
        val docLP = DocumentLP("!")

        try {
            val qrImagePath = "${context.cacheDir}/$fileName"
            val imageFile = File(qrImagePath)

            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(qrImagePath)
                if (bitmap != null) {
                    docLP.writeImage(bitmap, imageHeadWidth)
                    bitmap.recycle()
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

    override fun addLineFeedToBuffer(lineNumber: Int) {
        for (i in 1..lineNumber) {
            buffer.put(PR3Command.NEW_LINE)
        }
    }

    override fun giayBaoTienNuocNongThon(jsonData: ReadableMap) {

//        addSeparateLineToBuffer()
//        addAlignedStringToBuffer("Gã vội vã bước nhanh qua phố xá, dưới bóng trời chớm nở những giấc mơ.\n")
//        addAlignedStringToBuffer(
//            "Gã vội vã bước nhanh qua phố xá, dưới bóng trời chớm nở những giấc mơ.\n",
//            WoosimCmd.ALIGN_CENTER
//        )
//        addAlignedStringToBuffer(
//            "Gã vội vã bước nhanh qua phố xá, dưới bóng trời chớm nở những giấc mơ.\n",
//            WoosimCmd.ALIGN_RIGHT
//        )
        // addSeparateLineToBuffer()

        addAlignedStringToBuffer("The quick brown fox jumps over the lazy dog.\n")
        addAlignedStringToBuffer(
            "The quick brown fox jumps over the lazy dog.\n",
            WoosimCmd.ALIGN_CENTER,
            true,
        )
        addAlignedStringToBuffer(
            "The quick brown fox jumps over the lazy dog.\n",
            WoosimCmd.ALIGN_RIGHT,
            doubleFontSize = true
        )
        addAlignedStringToBuffer(
            "The quick brown fox jumps over the lazy dog.\n",
            WoosimCmd.ALIGN_RIGHT,
            bold = true,
            doubleFontSize = true,
        )

        addAlignedStringToBuffer("Gã vội vã bước nhanh qua phố xá, dưới bóng trời chớm nở những giấc mơ.\n")
        addAlignedStringToBuffer(
            "Gã vội vã bước nhanh qua phố xá, dưới bóng trời chớm nở những giấc mơ.\n",
            WoosimCmd.ALIGN_CENTER,
            true,
        )
        addAlignedStringToBuffer(
            "Gã vội vã bước nhanh qua phố xá, dưới bóng trời chớm nở những giấc mơ.\n",
            WoosimCmd.ALIGN_RIGHT,
            doubleFontSize = true
        )
        addAlignedStringToBuffer(
            "Gã vội vã bước nhanh qua phố xá, dưới bóng trời chớm nở những giấc mơ.\n",
            WoosimCmd.ALIGN_RIGHT,
            bold = true,
            doubleFontSize = true,
        )

//        buffer.put(PR3Command.BOLD_ON)
//        buffer.put("Dòng chữ đậm\n".toByteArray())
//        buffer.put(PR3Command.BOLD_OFF)
//
//        buffer.put(PR3Command.DOUBLE_WIDE_ON)
//        buffer.put("Dòng chữ gấp đôi chiều rộng\n".toByteArray())
//        buffer.put(PR3Command.DOUBLE_WIDE_OFF)
//
//        buffer.put(PR3Command.DOUBLE_HIGH_ON)
//        buffer.put("Dòng chữ gấp đôi chiều cao\n".toByteArray())
//        buffer.put(PR3Command.DOUBLE_HIGH_OFF)
//
//        buffer.put(PR3Command.DOUBLE_WH_ON)
//        buffer.put("Dòng chữ gấp đôi chiều cao và chiều rộng\n".toByteArray())
//        buffer.put(PR3Command.DOUBLE_WH_OFF)
//
//        buffer.put("Dòng chữ bình thường\n".toByteArray())
//
//        addBitmapToBuffer("ma_qr.png")

        addSeparateLineToBuffer()

        addLineFeedToBuffer(2)
    }
}
