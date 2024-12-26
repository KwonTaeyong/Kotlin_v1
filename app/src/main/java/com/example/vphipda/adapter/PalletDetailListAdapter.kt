package com.example.vphipda.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.network.PalletDetail


class PalletDetailListAdapter(var templcData: ArrayList<PalletDetail>, var onTemplcDataClickedListener: PalletDetailDataClickedListener, var onPalletLongClickedListener: PalletDetailDataLongClickedListener) : RecyclerView.Adapter<PalletDetailListAdapter.ViewHolder>() {




    interface PalletDetailDataClickedListener {
        fun ontemplcClick(item: PalletDetail)
    }

    interface PalletDetailDataLongClickedListener {
        fun onPalletLongClick(item: PalletDetail):Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_templc, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = templcData[position]
        holder.CD_ITEM.text = templcData[position].CD_ITEM
        holder.QT.text = templcData[position].QT.toString()

        holder.itemView.setOnClickListener {
            onTemplcDataClickedListener.ontemplcClick(item)
        }

        holder.itemView.setOnLongClickListener{
            onPalletLongClickedListener.onPalletLongClick(item)
        }


    }

    override fun getItemCount(): Int {
        return templcData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val CD_ITEM : TextView = itemView.findViewById(R.id.rv_templc_val_CD_ITEM)
        val QT : TextView = itemView.findViewById(R.id.rv_templc_val_QT_LD)
    }
}





    

