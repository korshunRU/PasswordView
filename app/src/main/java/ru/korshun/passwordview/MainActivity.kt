package ru.korshun.passwordview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import ru.korshun.passwordviewlayout.PasswordViewLayout
import ru.korshun.passwordviewlayout.action.OnEnterPasswordListener


class MainActivity : AppCompatActivity(), OnEnterPasswordListener {

    private var mPasswordViewLayout : PasswordViewLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPasswordViewLayout = findViewById(R.id.passwordViewLayout)
        mPasswordViewLayout?.setOnEnterPasswordListener(this)

        val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int,
                                               errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                    errorCode != BiometricPrompt.ERROR_USER_CANCELED
                ) {
                    Toast.makeText(applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(applicationContext,
                    "Authentication succeeded!", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Authentication failed",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
        mPasswordViewLayout?.setAuthenticationCallback(authenticationCallback)


    }

    override fun getPassword(s: String) {
        Toast.makeText(this, s, LENGTH_LONG).show()
        mPasswordViewLayout?.setError()
    }


}