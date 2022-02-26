package com.personalproject.thenextbistar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {

    private lateinit var rvShowList: RecyclerView
//    private lateinit var showList : ListView<LiveShowItem>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalState.start()

        setContentView(R.layout.activity_main)

        rvShowList = findViewById(R.id.rvShowList)
        rvShowList.layoutManager = LinearLayoutManager(this)

        val fbStorage = FirebaseStorage.getInstance()
        val fbDb = FirebaseDatabase.getInstance()
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val voters = snapshot.childrenCount;
                Toast.makeText(this@MainActivity, "$voters voters!", Toast.LENGTH_LONG).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "${error.code} ${error.message} ${error.details}");
                Toast.makeText(this@MainActivity, "There was an error", Toast.LENGTH_LONG).show()
            }
        }

        val showsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val showList : ArrayList<LiveShowItem> = ArrayList();

                for(child in snapshot.children) {
                    Log.i("MainActivity", "Hi: ${child.key.toString()}")
                    val item = parseFromSnapshot(child)
                    showList.add(item)
                }

                rvShowList.adapter = ShowListAdapter(this@MainActivity, showList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        fbDb.getReference("shows").addValueEventListener(showsListener)

        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener(this) {
                Toast.makeText(this, "UID is ${auth.currentUser?.uid}", Toast.LENGTH_LONG).show()
                fbDb.getReference("users").child(auth.currentUser?.uid.toString()).setValue(auth.currentUser?.uid)
            }
            Toast.makeText(this, "Logging in!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Already logged in! UID is ${auth.currentUser?.uid}", Toast.LENGTH_LONG).show()
            fbDb.getReference("users").child(auth.currentUser?.uid.toString()).setValue(auth.currentUser?.uid)
        }

        fbDb.getReference("shows").child("0").child("votes").addValueEventListener(listener)
    }
}