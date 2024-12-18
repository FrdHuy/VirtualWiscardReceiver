package com.cs407.virtualwiscardreceiver

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var statusTextView: TextView
    private lateinit var testPassButton: Button
    private lateinit var testFailButton: Button
    private lateinit var receiveSignalButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        statusTextView = findViewById(R.id.statusTextView)
        testPassButton = findViewById(R.id.testPassButton)
        testFailButton = findViewById(R.id.testFailButton)
        receiveSignalButton = findViewById(R.id.receiveSignalButton)

        // Initialize NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.nfc_not_supported), Toast.LENGTH_LONG).show()
            statusTextView.text = getString(R.string.nfc_not_supported)
        }

        // Simulate PASS button click
        testPassButton.setOnClickListener {
            simulateNfcMessage("PASS")
        }

        // Simulate FAIL button click
        testFailButton.setOnClickListener {
            simulateNfcMessage("FAIL")
        }

        // Prepare to receive NFC signal
        receiveSignalButton.setOnClickListener {
            if (nfcAdapter?.isEnabled == true) {
                Toast.makeText(this, "Ready to receive NFC signal", Toast.LENGTH_SHORT).show()
                statusTextView.text = getString(R.string.waiting_for_signal)
            } else {
                Toast.makeText(this, "Please enable NFC to use this feature", Toast.LENGTH_LONG).show()
            }
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

        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            Log.d("NFC Test", "NFC tag discovered")
            tag?.let {
                // Simulate NFC signal handling (can replace with actual logic)
                val simulatedMessage = "PASS" // Replace with actual NFC message data
                processNfcMessage(simulatedMessage)
            }
        }
    }

    // Process the NFC message
    private fun processNfcMessage(message: String) {
        val rootView = findViewById<View>(R.id.rootLayout)
        runOnUiThread {
            if (message == "PASS") {
                Log.d("NFC Test", "Access Granted")
                statusTextView.text = getString(R.string.access_granted)
                rootView.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            } else {
                Log.d("NFC Test", "Access Denied")
                statusTextView.text = getString(R.string.access_denied)
                rootView.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            }
        }
    }

    // Simulate NFC message
    private fun simulateNfcMessage(message: String) {
        processNfcMessage(message)
    }
}
