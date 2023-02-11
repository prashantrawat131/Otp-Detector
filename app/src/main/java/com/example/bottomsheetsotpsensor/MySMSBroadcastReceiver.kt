package com.example.bottomsheetsotpsensor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * BroadcastReceiver to wait for SMS messages. This can be registered either
 * in the AndroidManifest or at runtime.  Should filter Intents on
 * SmsRetriever.SMS_RETRIEVED_ACTION.
 */
class MySMSBroadcastReceiver : BroadcastReceiver() {
    private var otpReceiveListener: OTPReceiveListener? = null
    fun init(otpReceiveListener: OTPReceiveListener?) {
        this.otpReceiveListener = otpReceiveListener
    }

    override fun onReceive(context: Context?, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            if (extras != null) {
                val status: Status? = extras[SmsRetriever.EXTRA_STATUS] as Status?
                if (status != null) when (status.getStatusCode()) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get SMS message contents
                        val message = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                        if (message != null) {
                            val pattern: Pattern = Pattern.compile("(\\d{4})")
                            //   \d is for a digit
                            //   {} is the number of digits here 4.
                            val matcher: Matcher = pattern.matcher(message)
                            var value = ""
                            if (matcher.find()) {
                                value = matcher.group(0) // 4 digit number
                                if (otpReceiveListener != null) otpReceiveListener!!.onOTPReceived(
                                    value
                                )
                            } else {
                                if (otpReceiveListener != null) otpReceiveListener!!.onOTPReceived(
                                    null
                                )
                            }
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> if (otpReceiveListener != null) otpReceiveListener!!.onOTPTimeOut()
                }
            }
        }
    }

    interface OTPReceiveListener {
        fun onOTPReceived(otp: String?)
        fun onOTPTimeOut()
    }
}