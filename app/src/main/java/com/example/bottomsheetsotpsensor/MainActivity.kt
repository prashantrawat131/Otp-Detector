package com.example.bottomsheetsotpsensor

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bottomsheetsotpsensor.databinding.ActivityMainBinding
import com.example.bottomsheetsotpsensor.databinding.ModalBottomSheetContentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var appSignature: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val modalBottomSheet = ModalBottomSheet()
        modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)

        askPermission()

        generateAppSignature()

        binding.phoneNumberEt.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                sendOtp()
            }
            false
        }

        binding.sendOtpButton.setOnClickListener{
            sendOtp()
        }
    }

    private fun generateAppSignature() {
        val appSignatureHelper = AppSignatureHelper(applicationContext)
        for (x in appSignatureHelper.getAppSignatures()!!) {
            Log.d("tagJi", "App signature: $x")
            appSignature = x
        }
    }

    private fun sendOtp() {
        try {
            val phoneNo = binding.phoneNumberEt.text.toString()

            val smsManager: SmsManager = SmsManager.getDefault()

            val otp = Random.nextInt(1000, 9999)
            val msg = "This is a sample otp message. Your otp is $otp \n\n\n\n $appSignature"

            smsManager.sendTextMessage(phoneNo, null, msg, null, null)
            Toast.makeText(
                applicationContext, "Message Sent", Toast.LENGTH_LONG
            ).show()
            Log.d("tagJi", "Message Sent")
        } catch (ex: Exception) {
            Toast.makeText(
                applicationContext, ex.message.toString(), Toast.LENGTH_LONG
            ).show()

            Log.d("tagJi", ex.message.toString())
            ex.printStackTrace()
        }
        startActivity(Intent(this, OtpVerifyActivity::class.java))
    }

    private fun askPermission() {
        if (checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(android.Manifest.permission.SEND_SMS), 10)
        }
    }
}

class ModalBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val sheetBinding = ModalBottomSheetContentBinding.inflate(
            inflater, container, false
        )

        sheetBinding.closeButton.setOnClickListener {
            dismiss()
        }


        sheetBinding.image.setOnClickListener {
            val anim = RotateAnimation(0f, 350f, 15f, 15f)
            anim.interpolator = LinearInterpolator()
            anim.repeatCount = 1
            anim.duration = 700

            sheetBinding.image.startAnimation(anim)
        }

        return sheetBinding.root
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}