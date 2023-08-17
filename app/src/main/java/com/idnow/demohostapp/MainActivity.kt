package com.idnow.demohostapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import de.idnow.sdk.IDnowSDK

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_ASK_PERMISSIONS = 46825
    private var oldTokenTextLength = 0

    lateinit var startButton: Button
    lateinit var identEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (IDnowSDK.getInstance().isSessionActive) {
            Log.d("StartActivity", "Bringing Video Activity to front")
            IDnowSDK.getInstance().bringUpActiveSession(this, intent)
            finish()
        }
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_main)
        checkForRuntimePermissions()

        startButton = findViewById(R.id.startButton)
        identEditText = findViewById(R.id.identEditText)


        // dynamically check if the entered text is a valid phone number
        identEditText.addTextChangedListener(getIdentWatcher())

        startButton.setOnClickListener {
            val sdk = IDnowSDK.getInstance()
            sdk.initialize(this@MainActivity, "")
            IDnowSDK.setShowVideoOverviewCheck(false, this)
            IDnowSDK.setShowErrorSuccessScreen(false, this)
            IDnowSDK.setShowDialogsWithIcon(false, this)
            sdk.start(IDnowSDK.getTransactionToken())

            IDnowSDK.setHeaderBolded(true)
            IDnowSDK.setCheckboxesOrientationTop(true)
            IDnowSDK.setAGBInOneLine(true)
            IDnowSDK.setCheckScreenBoxConsentRequired(false)
            IDnowSDK.setCheckScreenBoxPhoneNumberRequired(false)
            IDnowSDK.setCheckScreenBoxDocumentRequired(false)
            IDnowSDK.setSquareCheckboxForm()
            IDnowSDK.setFullSizeModalSmsWindow(false)
            IDnowSDK.setTransparentBackgroundModalSmsWindow(true)
            IDnowSDK.setWhiteAgentThumbnailBackground(true)
            IDnowSDK.setCheckScreenLinesLong(true)
            IDnowSDK.setCustomiseCallQualityCheckScreen(true)


            IDnowSDK.getInstance().start(identEditText.text.toString())
        }
    }

    private fun getIdentWatcher() = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.toString() != "") {
                if (!startButton.isEnabled) {
                    startButton.isEnabled = true
                }

                if ((identEditText.text.length == 3 && oldTokenTextLength != 4 || identEditText.text.length == 10) && oldTokenTextLength != 11) {
                    identEditText.setText("$s-")
                    identEditText.setSelection(identEditText.text.length)
                }
            } else {
                startButton.isEnabled = false
            }
            updateOldTokenLength()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }

    private fun updateOldTokenLength() {
        oldTokenTextLength = identEditText.text.length
    }

    private fun checkForRuntimePermissions() {
        val permissionsList: List<String> = ArrayList()
        if (permissionsList.isNotEmpty()) {
            requestPermissions(permissionsList.toTypedArray(), REQUEST_CODE_ASK_PERMISSIONS)
            return
        }
    }
}
