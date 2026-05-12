package com.example.kalavidarabalaga

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 200, 60, 60)
        }

        val email = EditText(this).apply { hint = "Email" }
        val password = EditText(this).apply { hint = "Password" }
        val btn = Button(this).apply { text = "Signup" }

        layout.addView(email)
        layout.addView(password)
        layout.addView(btn)

        setContentView(layout)

        btn.setOnClickListener {

            auth.createUserWithEmailAndPassword(
                email.text.toString(),
                password.text.toString()
            ).addOnSuccessListener {

                val uid = auth.currentUser!!.uid

                val user = hashMapOf(
                    "email" to email.text.toString(),
                    "role" to "artist"
                )

                db.collection("users").document(uid).set(user)

                Toast.makeText(this, "Signup Success", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()

            }.addOnFailureListener {
                Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}