package com.personalproject.thenextbistar

import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object GlobalState {
    fun start() {
        if(initialized) return

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                numUsers = snapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                // Sadge
                // Bruv
            }
        }
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(listener)

        initialized = true;
    }

    var initialized: Boolean = false
    var numUsers: Int = 0
}