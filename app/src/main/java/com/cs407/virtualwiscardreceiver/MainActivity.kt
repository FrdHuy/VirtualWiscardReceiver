package com.cs407.virtualwiscardreceiver

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        statusTextView = findViewById(R.id.statusTextView)

        // Initialize NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            statusTextView.text = getString(R.string.nfc_not_supported)
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(
            this,
            PendingIntent.getActivity(
                this, 0,
                Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT
            ),
            null,
            null
        )
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // Handle NFC tag
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            tag?.let { processNfcMessage(it) }
        }
    }

    private fun processNfcMessage(tag: Tag) {
        // Simulated logic for receiving data
        // In a real scenario, you'd read from the NFC tag's NDEF message
        val receivedMessage = "PASS" // Replace this with actual data read logic
        if (receivedMessage == "PASS") {
            statusTextView.text = getString(R.string.access_granted)
            statusTextView.setTextColor(getColor(R.color.green))
        } else {
            statusTextView.text = getString(R.string.access_denied)
            statusTextView.setTextColor(getColor(R.color.red))
        }
    }
}
