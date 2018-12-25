package com.example.app.cbr.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.app.cbr.R
import kotlinx.android.synthetic.main.solusi_list_item.view.*

class GejalaAdapter(val items: MutableList<String>?, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        p0.itemView.tvSolusiAdapter.text = items?.get(position)
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.solusi_list_item, p0 , false))
    }

    override fun getItemCount(): Int = items?.size!!

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvGejala = view.tvSolusiAdapter
    }
}