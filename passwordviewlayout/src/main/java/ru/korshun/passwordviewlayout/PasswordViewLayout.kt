package ru.korshun.passwordviewlayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.fragment.app.FragmentActivity
import ru.korshun.passwordviewlayout.action.OnEnterPasswordListener
import ru.korshun.passwordviewlayout.databinding.KeyboardLayoutBinding
import ru.korshun.passwordviewlayout.exception.NoBiometryException
import ru.korshun.passwordviewlayout.exception.PasswordLengthException
import ru.korshun.passwordviewlayout.utils.intOrString
import java.util.concurrent.Executor

class PasswordViewLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs), View.OnClickListener {

    private var onEnterPasswordListener : OnEnterPasswordListener? = null

    private val minPasswordLength = 4
    private val maxPasswordLength = 8

    private val mBinding = KeyboardLayoutBinding.inflate(
        LayoutInflater.from(context), this, true)
    private var mPasswordLength = minPasswordLength
    private var mOpenBiometryOnLoad = true
    private val mIndicationButtonsList : MutableList<PasswordIndicatorButton> = ArrayList()
    private val mPassword : MutableList<Int> = ArrayList()

    private val mBiometricManager =  BiometricManager.from(context)
    private lateinit var mPromptInfo: PromptInfo
    private lateinit var mExecutor: Executor
    private lateinit var mBiometricPrompt: BiometricPrompt
    private var mAuthenticationCallback : BiometricPrompt.AuthenticationCallback? = null

    @get:DrawableRes
    @DrawableRes
    var ibBackgroundInactive = R.drawable.round_button_inactive
        private set
    @get:DrawableRes
    @DrawableRes
    var ibBackgroundActive = R.drawable.round_button_active
    @get:DrawableRes
    @DrawableRes
    var ibBackgroundError = R.drawable.round_button_error

    init {

        context.obtainStyledAttributes(attrs,
            R.styleable.PasswordViewLayout).let {
            mPasswordLength = it.getInt(
                R.styleable.PasswordViewLayout_passwordLength, minPasswordLength)
            mOpenBiometryOnLoad = it.getBoolean(
                R.styleable.PasswordViewLayout_openBiometryOnLoad, true)
            it.recycle()
        }

        if (mPasswordLength < minPasswordLength || mPasswordLength > maxPasswordLength)
            throw PasswordLengthException(resources.getString(R.string.password_length_error))

        inflate(context, R.layout.keyboard_layout, this)

        initIndicationButtons(attrs)
        initButtonsClickListeners()

        if (!checkBiometry())
            mBinding.fingerprintButton.visibility = View.INVISIBLE


    }

    override fun onClick(v: View) {
        var number = -1

        when(v.id) {
            R.id.keyboard_button_0,
            R.id.keyboard_button_1,
            R.id.keyboard_button_2,
            R.id.keyboard_button_3,
            R.id.keyboard_button_4,
            R.id.keyboard_button_5,
            R.id.keyboard_button_6,
            R.id.keyboard_button_7,
            R.id.keyboard_button_8,
            R.id.keyboard_button_9 -> {
                number = (v as AppCompatButton).text.toString().intOrString() }
            R.id.del_number_button -> delLastNumber()
            R.id.fingerprint_button -> openBiometryDialog()
        }

        if (number != -1)
            addNumber(number)
    }

    private fun addNumber(number:Int) {
        if (mPassword.size == mPasswordLength)
            return

        mPassword.add(mPassword.size, number)
        mIndicationButtonsList[mPassword.size - 1].setBackgroundResource(ibBackgroundActive)

        if (mPassword.size == mPasswordLength)
            onEnterPasswordListener?.getPassword(
                mPassword.joinToString (
                    prefix = "",
                    separator = "",
                    postfix = ""
                )
            )
    }

    private fun delLastNumber() {
        if (mPassword.size == 0)
            return

        mPassword.removeLast()
        mIndicationButtonsList[mPassword.size].setBackgroundResource(ibBackgroundInactive)
    }

    private fun initIndicationButtons(attrs: AttributeSet?) {
        val indicationLayout = mBinding.indicationContainer

        for (x in 1..mPasswordLength) {
            val indicationButton = PasswordIndicatorButton(context, attrs)

            val params = LayoutParams(context, attrs)
            params.setMargins(6)
            indicationButton.setBackgroundResource(ibBackgroundInactive)

            indicationLayout.addView(indicationButton, params)
            mIndicationButtonsList.add(indicationButton)
        }
    }

    private fun initButtonsClickListeners() {
        mBinding.keyboardButton0.setOnClickListener(this)
        mBinding.keyboardButton1.setOnClickListener(this)
        mBinding.keyboardButton2.setOnClickListener(this)
        mBinding.keyboardButton3.setOnClickListener(this)
        mBinding.keyboardButton4.setOnClickListener(this)
        mBinding.keyboardButton5.setOnClickListener(this)
        mBinding.keyboardButton6.setOnClickListener(this)
        mBinding.keyboardButton7.setOnClickListener(this)
        mBinding.keyboardButton8.setOnClickListener(this)
        mBinding.keyboardButton9.setOnClickListener(this)
        mBinding.fingerprintButton.setOnClickListener(this)
        mBinding.delNumberButton.setOnClickListener(this)
    }

    private fun initBiometry() {
        mExecutor = ContextCompat.getMainExecutor(context)
        mPromptInfo = PromptInfo.Builder()
            .setTitle(resources.getString(R.string.biometric_dialog_title))
            .setDescription(resources.getString(R.string.biometric_dialog_subtitle))
            .setNegativeButtonText(resources.getString(R.string.biometric_enter_pass))
            .setConfirmationRequired(true)
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
        mBiometricPrompt = BiometricPrompt(context as FragmentActivity,
            mExecutor, mAuthenticationCallback!!)

        if (mOpenBiometryOnLoad)
            openBiometryDialog()
    }

    private fun checkBiometry() : Boolean {
        return when (mBiometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    private fun openBiometryDialog() {
        mBiometricPrompt.authenticate(mPromptInfo)
    }

    fun setOnEnterPasswordListener(l: OnEnterPasswordListener) {
        onEnterPasswordListener = l
    }

    fun setAuthenticationCallback(callback: BiometricPrompt.AuthenticationCallback) {
        if (checkBiometry()) {
            mAuthenticationCallback = callback
            initBiometry()
        } else {
            throw NoBiometryException(resources.getString(R.string.no_biometry_error))
        }
    }

    fun clearPassword() {
        mPassword.clear()

        for (x in 0 until mPasswordLength)
            mIndicationButtonsList[x].setBackgroundResource(ibBackgroundInactive)
    }

    fun setError() {
        for (x in 0 until mPasswordLength)
            mIndicationButtonsList[x].setBackgroundResource(ibBackgroundError)
    }

}