package com.example.kalavidarabalaga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArtistAdapter

    private val artistList = ArrayList<Artist>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()

        // MAIN LAYOUT
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }

        // SEARCH BOX
        val search = EditText(this).apply {
            hint = "Search Artist"
        }

        // BUTTONS
        val addBtn = Button(this).apply {
            text = "➕ Add Artist"
        }

        val logoutBtn = Button(this).apply {
            text = "🚪 Logout"
        }

        // RECYCLER VIEW
        recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        // ADAPTER
        adapter = ArtistAdapter(artistList)
        recyclerView.adapter = adapter

        // ADD VIEWS
        layout.addView(search)
        layout.addView(addBtn)
        layout.addView(logoutBtn)
        layout.addView(recyclerView)

        setContentView(layout)

        // LOAD ARTISTS
        loadArtists()

        // ADD ARTIST BUTTON
        addBtn.setOnClickListener {

            startActivity(
                Intent(
                    this@MainActivity,
                    ArtistRegisterActivity::class.java
                )
            )
        }

        // LOGOUT BUTTON
        logoutBtn.setOnClickListener {

            FirebaseAuth.getInstance().signOut()

            startActivity(
                Intent(
                    this@MainActivity,
                    LoginActivity::class.java
                )
            )

            finish()
        }
    }

    // REFRESH WHEN RETURNING TO SCREEN
    override fun onResume() {
        super.onResume()
        loadArtists()
    }

    // LOAD DATA FROM FIRESTORE
    private fun loadArtists() {

        db.collection("artists")
            .get()

            .addOnSuccessListener { result ->

                artistList.clear()

                for (doc in result) {

                    val artist =
                        doc.toObject(Artist::class.java)
                            .copy(id = doc.id)

                    artistList.add(artist)
                }

                adapter.notifyDataSetChanged()
            }
    }
}