package com.personalproject.thenextbistar

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ShowActivity : AppCompatActivity() {

    private lateinit var tvName : TextView;
    private lateinit var tvDesc : TextView;
    private lateinit var tvResult : TextView;
    private lateinit var ivAvatar : ImageView;

    private lateinit var showId : String

    private var handler = Handler(Looper.getMainLooper()!!)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_show)

        tvName = findViewById(R.id.tvName)
        tvDesc = findViewById(R.id.tvDesc)
        tvResult = findViewById(R.id.tvResult)
        ivAvatar = findViewById(R.id.ivAvatar)

        showId = intent.extras?.getString("showId").toString()
        updateDisplay(showId)

        tvName.text = showId
    }

    private fun updateDisplay(showId: String) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = parseFromSnapshot(snapshot)
                tvName.text = item.name
                tvDesc.text = item.description
                tvResult.text = calcResult(item)

                val maxDownload: Long = 1024 * 1024; // Megabyte
                fbStorage.getReference(item.internalName).child("avatar.png").getBytes(maxDownload).addOnSuccessListener { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    ivAvatar.setImageBitmap(bitmap)
                }

                val r = object : Runnable {
                    override fun run() {
                        if(item.timestamp < Calendar.getInstance().timeInMillis / 1000) {
                            finish()
                            enterVoteActivity()
                            return;
                        }

                        tvResult.text = calcResult(item)

                        handler.postDelayed(this, 1000)
                    }
                }

                handler.post(r)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        FirebaseDatabase.getInstance().getReference("shows").child(showId).addListenerForSingleValueEvent(listener)
    }

    private fun enterVoteActivity() {
        val i = Intent(this, VoteActivity::class.java)
        i.putExtra("showId", showId)
        startActivity(i)
    }
}