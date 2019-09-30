package com.example.syari.sim_pbb.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.support.design.animation.AnimationUtils.LINEAR_INTERPOLATOR
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.list_kategori.view.*

import java.util.HashMap

class FaqAdapter(val context: Context,val dataset: HashMap<String,ArrayList<HashMap<String,String>>>): RecyclerView.Adapter<FaqAdapter.ViewHolder>() {
    val kategori =  dataset.map { it.key }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.list_kategori,p0,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return kategori.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Log.e("error", "cek data : ${dataset[kategori[position]]?.size}")
        val adapter = FaqChildAdapter(dataset[kategori[position]]!!)
        holder.itemView.kategoriChild.layoutManager = LinearLayoutManager(context,LinearLayout.VERTICAL,false)
        holder.itemView.kategoriChild.adapter = adapter
        holder.itemView.textKategori.text = kategori[position]
        holder.itemView.containerFaq.setOnClickListener {
            if (holder.itemView.kategoriChild.visibility == View.GONE){
                holder.itemView.kategoriChild.visibility = View.VISIBLE
                createRotateAnimator(holder.itemView.dropdown, 180f, 0f).start()
            }else{
                holder.itemView.kategoriChild.visibility = View.GONE
                createRotateAnimator(holder.itemView.dropdown, 180f, 0f).start()
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun createRotateAnimator(target: View, from: Float, to: Float): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(target, "rotation", from, to)
        animator.duration = 300
        animator.interpolator = LINEAR_INTERPOLATOR
        return animator
    }
}
