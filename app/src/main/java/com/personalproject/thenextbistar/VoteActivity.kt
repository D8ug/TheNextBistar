package com.personalproject.thenextbistar

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class VoteActivity : AppCompatActivity() {

    private lateinit var tvPercent : TextView
    private lateinit var ivPos : ImageView
    private lateinit var ivNeg : ImageView
    private lateinit var pbPercent : ProgressBar

    private lateinit var showId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote)

        showId = intent.extras?.getString("showId")!!
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = parseFromSnapshot(snapshot)
                setPercent(item.votes / GlobalState.numUsers.toFloat())


            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }
        }

        val imgListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = parseFromSnapshot(snapshot)
                val maxDownload: Long = 1024 * 1024; // Megabyte
                fbStorage.getReference(item.avatarLink).child("pos.png").getBytes(maxDownload).addOnSuccessListener { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    ivPos.setImageBitmap(bitmap)
                }

                fbStorage.getReference(item.avatarLink).child("neg.png").getBytes(maxDownload).addOnSuccessListener { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    ivNeg.setImageBitmap(bitmap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }
        }


        FirebaseDatabase.getInstance().getReference("shows").child(showId).addValueEventListener(listener)
        FirebaseDatabase.getInstance().getReference("shows").child(showId).addListenerForSingleValueEvent(imgListener)

        tvPercent = findViewById(R.id.tvPercent)
        ivPos = findViewById(R.id.ivPos)
        ivNeg = findViewById(R.id.ivNeg)
        pbPercent = findViewById(R.id.pbPercent)

        setPercent(0.75f)
    }

    fun addPos(v: View) {
        val id = FirebaseAuth.getInstance().currentUser?.uid.toString()

        Toast.makeText(this, "Pos!", Toast.LENGTH_SHORT).show()
        FirebaseDatabase.getInstance().getReference("shows").child(showId).child("votes").child(id).setValue(id)

        lockUI(true)
    }

    private fun lockUI(positive: Boolean) {
        ivNeg.alpha = if(positive) 0.25f else 0.75f
        ivPos.alpha = if(positive) 0.75f else 0.25f

        ivNeg.isClickable = false
        ivPos.isClickable = false
    }

    fun addNeg(v: View) {
        Toast.makeText(this, "Neg!", Toast.LENGTH_SHORT).show()

        lockUI(false)
    }

    fun setPercent(percent: Float) {
        val percentInt = (percent * 100).toInt()
        tvPercent.text = "$percentInt%"
        pbPercent.progress = percentInt
    }
}