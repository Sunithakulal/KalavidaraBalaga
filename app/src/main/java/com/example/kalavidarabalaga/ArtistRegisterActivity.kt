package com.example.kalavidarabalaga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ArtistRegisterActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()

        // CHECK EDIT MODE
        val id = intent.getStringExtra("id")

        // MAIN LAYOUT
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 100, 50, 50)
        }

        // TITLE
        val title = TextView(this).apply {

            text =
                if (id == null)
                    "🎭 Add Artist"
                else
                    "✏️ Edit Artist"

            textSize = 22f
        }

        // INPUT FIELDS
        val name = EditText(this).apply {
            hint = "Artist Name"
        }

        val phone = EditText(this).apply {
            hint = "Phone Number"
        }

        val skill = EditText(this).apply {
            hint = "Art Form / Skill"
        }

        val district = EditText(this).apply {
            hint = "District"
        }

        val equipment = EditText(this).apply {
            hint = "Equipment"
        }

        val location = EditText(this).apply {
            hint = "Location"
        }

        val imageUrl = EditText(this).apply {
            hint = "Image URL"
        }

        // SAVE BUTTON
        val saveBtn = Button(this).apply {

            text =
                if (id == null)
                    "Save Artist"
                else
                    "Update Artist"
        }

        // ADD VIEWS
        layout.addView(title)
        layout.addView(name)
        layout.addView(phone)
        layout.addView(skill)
        layout.addView(district)
        layout.addView(equipment)
        layout.addView(location)
        layout.addView(imageUrl)
        layout.addView(saveBtn)

        setContentView(layout)

        // PREFILL DATA FOR EDIT
        name.setText(intent.getStringExtra("name"))
        phone.setText(intent.getStringExtra("phone"))
        skill.setText(intent.getStringExtra("skill"))
        district.setText(intent.getStringExtra("district"))
        equipment.setText(intent.getStringExtra("equipment"))
        location.setText(intent.getStringExtra("location"))
        imageUrl.setText(intent.getStringExtra("imageUrl"))

        // SAVE / UPDATE BUTTON
        saveBtn.setOnClickListener {

            val artistData = hashMapOf(

                "name" to name.text.toString(),
                "phone" to phone.text.toString(),
                "skill" to skill.text.toString(),
                "district" to district.text.toString(),
                "equipment" to equipment.text.toString(),
                "location" to location.text.toString(),
                "imageUrl" to imageUrl.text.toString()
            )

            val ref = db.collection("artists")

            // ADD NEW ARTIST
            if (id == null) {

                ref.add(artistData)

                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Artist Added Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this,
                                MainActivity::class.java
                            )
                        )

                        finish()
                    }

                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Failed To Add Artist",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            } else {

                // UPDATE EXISTING ARTIST
                ref.document(id)
                    .update(artistData as Map<String, Any>)

                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Artist Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this,
                                MainActivity::class.java
                            )
                        )

                        finish()
                    }

                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Failed To Update Artist",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }
}