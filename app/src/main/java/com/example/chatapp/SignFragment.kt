package com.example.chatapp

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatapp.databinding.FragmentSignBinding
import com.google.firebase.auth.FirebaseAuth

class SignFragment : Fragment(R.layout.fragment_sign) {
    private lateinit var binding: FragmentSignBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""
    private var isSignIn = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignBinding.bind(view)
        progressDialog = ProgressDialog(requireContext())
        firebaseAuth = FirebaseAuth.getInstance()

        checkUser()

        dialog()

        initListener()

        createAccount()

    }

    private fun checkUser() {
        if (isSignIn) {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                Toast.makeText(requireContext(), "You are already register!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signFragment_to_mainFragment)
            }
        }
    }

    private fun initListener() {
        if (isSignIn) {
            binding.btnLogin.setOnClickListener {
                validateDate()
            }
        }
    }

    private fun validateDate() {
        if (isSignIn) {
            email = binding.etEmail.text.toString()
            password = binding.etPassword.text.toString()

            if (Patterns.EMAIL_ADDRESS.matcher(email).matches().not()) {
                binding.etEmail.error = "Invalid email format"
            } else if (TextUtils.isEmpty(password)) {
                binding.etPassword.error = "Please enter your password"
            } else {
                firebaselogin()
            }
        }
    }

    private fun firebaselogin() {
        if (isSignIn) {
            progressDialog.show()
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    val firebaseUser = firebaseAuth.currentUser
                    val email = firebaseUser!!.email
                    findNavController().navigate(R.id.action_signFragment_to_mainFragment)
                    Log.d("TTTT", "email: $email")
                }
                .addOnFailureListener {
                    Log.d("TTTT", "Failure")
                    progressDialog.dismiss()
                }
        }
    }

    private fun dialog() {
        if (isSignIn) {
            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Loggin in...")
            progressDialog.setCanceledOnTouchOutside(false)
        }
    }

    private fun createAccount(){
        binding.btnSignUp.setOnClickListener {
            isSignIn = false
            if (isSignIn.not()) binding.btnLogin.visibility = View.GONE
            if (isSignIn.not()) {
                email = binding.etEmail.text.toString()
                password = binding.etPassword.text.toString()

                if (Patterns.EMAIL_ADDRESS.matcher(email).matches().not()) {
                    binding.etEmail.error = "Invalid email format"
                } else if (TextUtils.isEmpty(password)) {
                    binding.etPassword.error = "Please enter your password"
                } else {
                    firebaseCreateAccount()
                }
            }
        }
    }

    private fun firebaseCreateAccount() {
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                findNavController().navigate(R.id.action_signFragment_to_mainFragment)
                Log.d("TTTT", "email: $email")
            }
            .addOnFailureListener {
                Log.d("TTTT", "Failure")
                progressDialog.dismiss()
            }
    }
}