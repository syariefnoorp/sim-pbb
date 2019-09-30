package com.example.syari.sim_pbb.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.list_faq.view.*

class FaqChildAdapter(val dataset : ArrayList<HashMap<String,String>>) : RecyclerView.Adapter<FaqChildAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.list_faq,p0,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tanyaFaq.text = dataset[position]["tanya"]
        holder.itemView.jawabFaq.text = dataset[position]["jawab"]
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}