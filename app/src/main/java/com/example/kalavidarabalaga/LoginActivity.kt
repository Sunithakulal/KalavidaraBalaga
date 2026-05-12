package com.example.kalavidarabalaga

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 200, 60, 60)
        }

        val email = EditText(this).apply { hint = "Email" }
        val password = EditText(this).apply { hint = "Password" }

        val loginBtn = Button(this).apply { text = "Login" }
        val signupBtn = Button(this).apply { text = "Signup" }

        layout.addView(email)
        layout.addView(password)
        layout.addView(loginBtn)
        layout.addView(signupBtn)

        setContentView(layout)

        loginBtn.setOnClickListener {

            auth.signInWithEmailAndPassword(
                email.text.toString(),
                password.text.toString()
            ).addOnSuccessListener {

                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }.addOnFailureListener {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }

        signupBtn.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}