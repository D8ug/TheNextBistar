package com.personalproject.thenextbistar;

import com.google.firebase.database.DataSnapshot

data class LiveShowItem (
    val internalName: String,
    val name : String,
    val description: String,
    val timestamp: Long,
    val avatarLink: String,
    val key: String,
    val votes: Int
)

fun parseFromSnapshot(snapshot: DataSnapshot) : LiveShowItem {
    val internalName = snapshot.child("internal_name").getValue(String::class.java)!!
    val name = snapshot.child("name").getValue(String::class.java)!!
    val avatar = snapshot.child("avatar").getValue(String::class.java)!!
    val description = snapshot.child("description").getValue(String::class.java)!!
    val time = snapshot.child("time").getValue(Long::class.java)!!
    val votes = snapshot.child("votes").childrenCount

    return LiveShowItem(internalName, name, description, time, avatar, snapshot.key!!, votes.toInt())
}

