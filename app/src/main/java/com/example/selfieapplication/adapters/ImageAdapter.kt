package com.example.selfieapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.selfieapplication.R
import com.example.selfieapplication.models.Image
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_image_adapter.view.*

class ImageAdapter(var mContext: Context, var mList: ArrayList<Image>):RecyclerView.Adapter<ImageAdapter.MyImageViewHolder>() {
    inner class MyImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(image: Image){
            Picasso.get().load(image.imageUrl).into(itemView.row_image_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyImageViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.row_image_adapter, parent, false)
        return MyImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyImageViewHolder, position: Int) {
        val image = mList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}