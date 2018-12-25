package com.example.app.cbr.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.example.app.cbr.R
import com.example.app.cbr.model.Gejala

class ChecklistAdapter (val contex: Context, val gejala: List<Gejala>) : RecyclerView.Adapter<ChecklistAdapter.Holder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
        return Holder(LayoutInflater.from(contex).inflate(R.layout.checklist_list_item, p0, false))
    }

    override fun getItemCount(): Int = gejala.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.cbGejala.tag = position
        //check state object
        holder.cbGejala.isChecked = gejala[position].isSelected

        holder.cbGejala.setOnClickListener() {
            val pos = holder.cbGejala.tag as Int
            gejala[pos].isSelected = !gejala[pos].isSelected
        }

        //bind item to holder
        holder.bindItem(gejala[position])
    }

    fun getItem() = gejala

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        var tvName: TextView = view.findViewById(R.id.tvGejalaName)
        var cbGejala: CheckBox = view.findViewById(R.id.cbGejala)

        fun bindItem(gejala: Gejala){
            tvName.text = gejala.namaGejala
            cbGejala.text
        }
    }
}