package com.example.bottomsheetsotpsensor

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bottomsheetsotpsensor.databinding.ActivityOtpVerifyBinding
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task


class OtpVerifyActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpVerifyBinding
    lateinit var mySMSBroadcastReceiver: MySMSBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startSMSRetrieverClient()

        mySMSBroadcastReceiver = MySMSBroadcastReceiver();
        registerReceiver(mySMSBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
        mySMSBroadcastReceiver.init(object : MySMSBroadcastReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                Log.d("tagJi", "Otp: $otp")
                binding.otpEt.setText("$otp")
            }

            override fun onOTPTimeOut() {
                Log.d("tagJi", "Time out for otp")
            }
        })
    }

    private fun startSMSRetrieverClient() {
        val client = SmsRetriever.getClient(this)
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener { aVoid ->
            Log.d("tagJi", "Sms retriever started")
        }
        task.addOnFailureListener { e ->
            Log.d("tagJi", "Sms retriever failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mySMSBroadcastReceiver != null)
            unregisterReceiver(mySMSBroadcastReceiver);
    }
}