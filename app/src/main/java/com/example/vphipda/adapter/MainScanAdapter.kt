package com.example.vphipda.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.network.SearchCodeData


class MainScanAdapter(var mainScanData: ArrayList<SearchCodeData>) : RecyclerView.Adapter<MainScanAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainScanAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_mainscan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainScanAdapter.ViewHolder, position: Int) {
        val item = mainScanData[position]
        holder.CD_ITEM.text = mainScanData[position].CD_ITEM
        holder.CD_LC.text = mainScanData[position].CD_LC
        holder.QT.text = mainScanData[position].QT.toString()


    }

    override fun getItemCount(): Int {
        return mainScanData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val CD_ITEM : TextView = itemView.findViewById(R.id.rv_MainScan_th_cdItem)
        val CD_LC : TextView = itemView.findViewById(R.id.rv_MainScan_th_rack)
        val QT : TextView = itemView.findViewById(R.id.rv_MainScan_th_qt)
    }
}





    

