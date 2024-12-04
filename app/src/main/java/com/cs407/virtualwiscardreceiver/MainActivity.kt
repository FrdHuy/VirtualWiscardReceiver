package com.cs407.virtualwiscardreceiver

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var pendingIntent: PendingIntent
    private lateinit var intentFiltersArray: Array<IntentFilter>
    private lateinit var techListsArray: Array<Array<String>>

    private lateinit var resultTextView: TextView

    companion object {
        private const val TAG = "ReceiverActivity"
        private const val AID = "F0010203040506"
        private val SELECT_APDU = buildSelectApdu(AID)

        private fun buildSelectApdu(aid: String): ByteArray {
            return hexStringToByteArray("00A40400" + String.format("%02X", aid.length / 2) + aid)
        }

        private fun hexStringToByteArray(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                        + Character.digit(s[i + 1], 16)).toByte()
                i += 2
            }
            return data
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout
        setContentView(R.layout.activity_main)

        // Get the UI components
        resultTextView = findViewById(R.id.resultTextView)
        resultTextView.text = "Waiting for NFC tag scan..."

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            resultTextView.text = "NFC is not supported on this device"
            return
        }

        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val ndef = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        intentFiltersArray = arrayOf(ndef)
        techListsArray = arrayOf(arrayOf(IsoDep::class.java.name))
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            tag?.let {
                communicateWithTag(it)
            }
        }
    }

    private fun communicateWithTag(tag: Tag) {
        try {
            val isoDep = IsoDep.get(tag)
            isoDep.connect()

            // Send SELECT AID command
            val response = isoDep.transceive(SELECT_APDU)
            Log.d(TAG, "Response: ${response.joinToString(" ") { String.format("%02X", it) }}")

            // Parse the response
            val responseData = response.copyOfRange(0, response.size - 2)
            val statusWord = response.copyOfRange(response.size - 2, response.size)

            val result = String(responseData, Charsets.UTF_8)
            if (statusWord.contentEquals(byteArrayOf(0x90.toByte(), 0x00.toByte()))) {
                // Success status word
                if (result == "PASS") {
                    resultTextView.text = "Access Granted"
                    resultTextView.setBackgroundColor(android.graphics.Color.GREEN)
                } else if (result == "FAIL") {
                    resultTextView.text = "Access Denied"
                    resultTextView.setBackgroundColor(android.graphics.Color.RED)
                } else {
                    resultTextView.text = "Unknown Response: $result"
                    resultTextView.setBackgroundColor(android.graphics.Color.YELLOW)
                }
            } else {
                resultTextView.text = "Communication Error"
                resultTextView.setBackgroundColor(android.graphics.Color.GRAY)
            }

            isoDep.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error communicating with tag", e)
            resultTextView.text = "Error: ${e.message}"
        }
    }
}
