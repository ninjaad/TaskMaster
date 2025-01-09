package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Handle "Already have an account?" text click
        binding.textView.setOnClickListener {
            navigateToLogin()
        }

        // Handle Sign Up button click
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passET.text.toString().trim()
            val confirmPass = binding.confirmPassEt.text.toString().trim()

            if (validateInputs(email, pass, confirmPass)) {
                performSignUp(email, pass)
            }
        }
    }

    private fun validateInputs(email: String, pass: String, confirmPass: String): Boolean {
        return when {
            email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty() -> {
                showToast("Empty Fields Are not Allowed!")
                false
            }
            pass != confirmPass -> {
                showToast("Passwords do not match")
                false
            }
            pass.length < 6 -> {
                showToast("Password must be at least 6 characters")
                false
            }
            else -> true
        }
    }

    private fun performSignUp(email: String, password: String) {
        binding.button.isEnabled = false // Prevent multiple clicks

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign out immediately after account creation
                    firebaseAuth.signOut()
                    showToast("Account created successfully! Please login.")
                    navigateToLogin()
                } else {
                    showToast(task.exception?.message ?: "Signup failed")
                    binding.button.isEnabled = true
                }
            }
            .addOnFailureListener { e ->
                showToast(e.message ?: "Signup failed")
                binding.button.isEnabled = true
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        // Remove these flags as they might interfere with the back stack
        // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}