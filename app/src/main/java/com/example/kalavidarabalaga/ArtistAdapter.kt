
package com.example.kalavidarabalaga

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class ArtistAdapter(
    private val artistList: ArrayList<Artist>
) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    class ArtistViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val imgArtist: ImageView =
            itemView.findViewById(R.id.imgArtist)

        val tvName: TextView =
            itemView.findViewById(R.id.tvName)

        val tvSkill: TextView =
            itemView.findViewById(R.id.tvSkill)

        val tvPhone: TextView =
            itemView.findViewById(R.id.tvPhone)

        val tvDistrict: TextView =
            itemView.findViewById(R.id.tvDistrict)

        val tvEquipment: TextView =
            itemView.findViewById(R.id.tvEquipment)

        val tvLocation: TextView =
            itemView.findViewById(R.id.tvLocation)

        val btnBook: Button =
            itemView.findViewById(R.id.btnBook)

        val btnCall: Button =
            itemView.findViewById(R.id.btnCall)

        val btnEdit: Button =
            itemView.findViewById(R.id.btnEdit)

        val btnDelete: Button =
            itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArtistViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artist, parent, false)

        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ArtistViewHolder,
        position: Int
    ) {

        val artist = artistList[position]
        val context = holder.itemView.context

        // TEXT
        holder.tvName.text = artist.name
        holder.tvSkill.text = "🎵 Skill: ${artist.skill}"
        holder.tvPhone.text = "📞 Phone: ${artist.phone}"
        holder.tvDistrict.text = "📍 District: ${artist.district}"
        holder.tvEquipment.text = "🥁 Equipment: ${artist.equipment}"
        holder.tvLocation.text = "📌 Location: ${artist.location}"

        // IMAGE
        Glide.with(context)
            .load(artist.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.imgArtist)

        // BOOK BUTTON
        holder.btnBook.setOnClickListener {

            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->

                    val selectedDate =
                        "$dayOfMonth-${month + 1}-$year"

                    checkBookingAndConfirm(
                        artist,
                        selectedDate,
                        context
                    )

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }

        // CALL BUTTON
        holder.btnCall.setOnClickListener {

            val intent = Intent(Intent.ACTION_DIAL)

            intent.data = Uri.parse("tel:${artist.phone}")

            context.startActivity(intent)
        }

        // EDIT BUTTON
        holder.btnEdit.setOnClickListener {

            val intent = Intent(
                context,
                ArtistRegisterActivity::class.java
            )

            intent.putExtra("id", artist.id)
            intent.putExtra("name", artist.name)
            intent.putExtra("phone", artist.phone)
            intent.putExtra("skill", artist.skill)
            intent.putExtra("district", artist.district)
            intent.putExtra("equipment", artist.equipment)
            intent.putExtra("location", artist.location)
            intent.putExtra("imageUrl", artist.imageUrl)

            context.startActivity(intent)
        }

        // DELETE BUTTON
        holder.btnDelete.setOnClickListener {

            AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Delete ${artist.name}?")

                .setPositiveButton("Yes") { _, _ ->

                    FirebaseFirestore.getInstance()
                        .collection("artists")
                        .document(artist.id)
                        .delete()

                        .addOnSuccessListener {

                            Toast.makeText(
                                context,
                                "Artist Deleted",
                                Toast.LENGTH_SHORT
                            ).show()

                            artistList.removeAt(position)

                            notifyItemRemoved(position)

                            notifyItemRangeChanged(
                                position,
                                artistList.size
                            )
                        }
                }

                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun checkBookingAndConfirm(
        artist: Artist,
        selectedDate: String,
        context: Context
    ) {

        FirebaseFirestore.getInstance()
            .collection("bookings")
            .whereEqualTo("artistId", artist.id)
            .whereEqualTo("bookingDate", selectedDate)
            .get()

            .addOnSuccessListener { documents ->

                if (!documents.isEmpty) {

                    Toast.makeText(
                        context,
                        "Artist already booked on this date",
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    AlertDialog.Builder(context)
                        .setTitle("Confirm Booking")
                        .setMessage(
                            "Book ${artist.name} on $selectedDate?"
                        )

                        .setPositiveButton("Confirm") { _, _ ->

                            saveBooking(
                                artist,
                                selectedDate,
                                context
                            )
                        }

                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
    }

    private fun saveBooking(
        artist: Artist,
        selectedDate: String,
        context: Context
    ) {

        val bookingData = hashMapOf(

            "artistId" to artist.id,
            "artistName" to artist.name,
            "artistPhone" to artist.phone,
            "artistSkill" to artist.skill,
            "bookingDate" to selectedDate,
            "bookedBy" to (
                    FirebaseAuth.getInstance()
                        .currentUser?.email ?: "Unknown User"
                    ),
            "status" to "Booked",
            "timestamp" to System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance()
            .collection("bookings")
            .add(bookingData)

            .addOnSuccessListener {

                Toast.makeText(
                    context,
                    "Booking Confirmed",
                    Toast.LENGTH_LONG
                ).show()
            }

            .addOnFailureListener {

                Toast.makeText(
                    context,
                    "Booking Failed",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    override fun getItemCount(): Int {

        return artistList.size
    }
}

