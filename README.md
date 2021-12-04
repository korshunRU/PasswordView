# Password Keyboard


### Aviable options:
```app:passwordLength``` - length of your password (from 4 to 8 characters ,default 4)\
```app:openBiometryOnLoad``` - If true - opens biometry dialog on load (default true)\

### How to use
Add this code to your layout
```xml
    <ru.korshun.passwordviewlayout.PasswordViewLayout
        android:id="@+id/passwordViewLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:passwordLength="6"/>
```
Your Activity or Fragment must implement OnEnterPasswordListener interface:
```kotlin
    override fun getPassword(s: String) {
        // gets a password after input
        // your logic here
    }
```
Send listener:
```kotlin
mPasswordViewLayout?.setOnEnterPasswordListener(this)
```
Declare biometry authentication callback and send it:
```kotlin
        val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int,
                                               errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)          
                    Toast.makeText(applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
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
```
