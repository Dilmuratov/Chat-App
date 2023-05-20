package com.example.chatapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.chatapp.databinding.FragmentSignBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class SignFragment : Fragment(R.layout.fragment_sign) {
    private lateinit var binding: FragmentSignBinding
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var enteredCode: String = ""
    lateinit var credential: PhoneAuthCredential
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignBinding.bind(view)
        auth = Firebase.auth

        binding.btnNext.setOnClickListener {
            Log.d("TAG", "Clicked")
            val phoneNumber =
                binding.etCountryCode.text.toString() + binding.etPhoneNumber.text.toString()
            sendVerificationCode(phoneNumber)
        }

        binding.etVerifyingCode.addTextChangedListener {
            val verCode = it.toString()
            if (verCode.length == 6) {
                val credential = PhoneAuthProvider.getCredential(
                    storedVerificationId ?: "", verCode
                )
                signInWithPhoneAuthCredential(credential)
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")

                    val user = task.result?.user

                    Toast.makeText(context, "Succesfull registered", Toast.LENGTH_SHORT).show()

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }

    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("TAG", "onVerificationCompleted: $credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w("TAG", "onVerificationFailed: $e")

        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d("TAG", "onCodeSent: $verificationId")

            storedVerificationId = verificationId
            resendToken = token
        }

        private fun verifyCode(verificationId: String) {
            enteredCode = "123456"
            credential = PhoneAuthProvider.getCredential(verificationId, enteredCode)
        }
    }
}