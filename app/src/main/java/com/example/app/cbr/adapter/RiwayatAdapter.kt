package com.example.app.cbr.adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.app.cbr.DetailActivity
import com.example.app.cbr.R
import com.example.app.cbr.model.AppDatabase
import com.example.app.cbr.model.GejalaSolusi
import com.example.app.cbr.model.Konsultasi
import kotlinx.android.synthetic.main.konsultasi_list_item.view.*

class RiwayatAdapter(val items: List<Konsultasi>?, val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        p0.itemView.tvNama.text = items?.get(position)?.namaAnak
        p0.itemView.tvSolusi.text = items?.get(position)?.tanggal.toString()
        p0.itemView.tvGejala.text = items?.get(position)?.idKonsultasi.toString()
        p0.itemView.linearRecycler.setOnClickListener() {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("id", p0.itemView.tvGejala.text.toString())
            startActivity(context, intent, intent.extras)
        }
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.konsultasi_list_item, p0 , false))
    }

    override fun getItemCount(): Int = items?.size!!


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvNama = view.tvNama
        val tvSolusi = view.tvSolusi
        val tvGejala = view.tvGejala
        val linearRecycler = view.linearRecycler
    }
}