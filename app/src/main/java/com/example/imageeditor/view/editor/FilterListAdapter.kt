package com.example.imageeditor.view.editor

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.imageeditor.model.FilterList
import com.example.imageeditor.R



class FilterListAdapter(private  val context: Context, private val list: List<FilterList>, private val listener: OnClickListener) :
    RecyclerView.Adapter<FilterListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val title: TextView = itemView.findViewById(R.id.title)
        val layout: CardView = itemView.findViewById(R.id.itemLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        val bitmap = BitmapFactory.decodeResource(context.resources,item.image)
        holder.imageView.setImageBitmap(bitmap)
        holder.title.text=item.title.name
        holder.layout.setOnClickListener { listener.onItemClick(item) }
    }

    override fun getItemCount(): Int {
       return list.size
    }

    interface OnClickListener{
        fun onItemClick(item: FilterList)
    }


}

