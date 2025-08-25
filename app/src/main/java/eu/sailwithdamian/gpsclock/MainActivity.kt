package eu.sailwithdamian.gpsclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import eu.sailwithdamian.gpsclock.ui.ClockScreen
import eu.sailwithdamian.gpsclock.ui.ClockTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : ComponentActivity() {
    private var lastGpsTime: Long = 0
    private var currentOffsetFromSystemTime: Long = 0

    private var nmeaStreamWorker: Job? = null
    private var tickerWorker: Job? = null

    private val dateFormatter = SimpleDateFormat("HH:mm:ss", Locale.UK)

    private var correctedTimeText = mutableStateOf("")
    private var infoText = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")

        setContent {
            ClockTheme {
                ClockScreen(
                    correctedTimeText = correctedTimeText,
                    infoText = infoText,
                )
            }
        }

        tickerWorker = displayTickerWorker()
        nmeaStreamWorker = nmeaReceiverWorker()
    }

    override fun onDestroy() {
        tickerWorker?.cancel()
        nmeaStreamWorker?.cancel()
        super.onDestroy()
    }

    private fun displayTickerWorker(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                var currentTime = System.currentTimeMillis()
                correctedTimeText.value =
                    dateFormatter.format(Date(currentTime + currentOffsetFromSystemTime))

                if (lastGpsTime + 10000 < currentTime) {
                    infoText.value = "No GPS Feed"
                }
                delay(1000L)
            }
        }
    }

    private fun nmeaReceiverWorker(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            val socket = DatagramSocket(1456)
            socket.broadcast = true

            val buffer = ByteArray(512)
            val receivedData = StringBuilder()
            while (isActive) {
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)
                receivedData.append(String(packet.data, 0, packet.length))

                while (true) {
                    val lineIdx = receivedData.indexOf("\n")
                    if (lineIdx == -1) break

                    val line = receivedData.substring(0, lineIdx).trim()
                    handleNmeaMessage(line)

                    receivedData.delete(0, lineIdx + 1)
                }
            }
        }
    }

    private fun handleNmeaMessage(line: String) {
        if (line.length < 6) return

        if ("GGA" == line.substring(3, 6)) {
            val fields = line.split(",")
            if (fields.size >= 2 && fields[1].length >= 8) {
                val hh = fields[1].substring(0, 2).toLong()
                val mm = fields[1].substring(2, 4).toLong()
                val ss = fields[1].substring(4, 6).toLong()
                val ms = fields[1].substring(7).toLong()

                val midNightEpoch =
                    LocalDate.now(ZoneId.of("UTC")).atStartOfDay(ZoneId.of("UTC")).toInstant()
                        .toEpochMilli()
                val systemTime = System.currentTimeMillis()
                val gpsTime = midNightEpoch + ((3600000 * hh) + (60000 * mm) + (1000 * ss) + ms)

                currentOffsetFromSystemTime = systemTime - gpsTime
                lastGpsTime = System.currentTimeMillis()

                infoText.value = String.format(
                    Locale.UK,
                    "S: %s G: %s O: %d ms",
                    dateFormatter.format(systemTime),
                    dateFormatter.format(gpsTime),
                    currentOffsetFromSystemTime
                )
            }
        }
    }
}
