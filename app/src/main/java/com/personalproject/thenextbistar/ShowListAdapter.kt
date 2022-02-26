package com.personalproject.thenextbistar;

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

val fbStorage = FirebaseStorage.getInstance()

class ShowListAdapter(private val context : Context, private val showListItems : List<LiveShowItem>) : RecyclerView.Adapter<ShowListAdapter.ViewHolder>() {

    private val handler = Handler(Looper.getMainLooper()!!)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowListAdapter.ViewHolder {
        val showView = LayoutInflater.from(context).inflate(R.layout.item_show, parent, false)
        showView.isClickable = true
        showView.isFocusable = true

        return ViewHolder(showView)
    }

    override fun onBindViewHolder(holder: ShowListAdapter.ViewHolder, position: Int) {
        val item = showListItems[position]
        holder.tvName.text = item.name
        holder.tvDesc.text = item.description
        holder.tvResult.text = "..."
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowActivity::class.java)
            intent.putExtra("showId", item.key)
            context.startActivity(intent)
        }

        val maxDownload: Long = 1024 * 1024; // Megabyte
        fbStorage.getReference(item.internalName).child("avatar.png").getBytes(maxDownload).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            holder.ivAvatar.setImageBitmap(bitmap)
        }

        val r = object : Runnable {
            override fun run() {
                holder.tvResult.text = calcResult(item)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(r)
    }

    override fun getItemCount(): Int = showListItems.size

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
        val tvResult: TextView = itemView.findViewById(R.id.tvResult)
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
    }

}
