package com.example.vphipda.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.model.ProdDetailItem



class ProdDetailListItemAdapter(var prodItemData: ArrayList<ProdDetailItem>, var onProdDetailClickedListener: OnProdDetailClickedListener) : RecyclerView.Adapter<ProdDetailListItemAdapter.ViewHolder>()  {

    interface OnProdDetailClickedListener {
        fun prodDetailClick(item : ProdDetailItem): Boolean
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdDetailListItemAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_prod_detail_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdDetailListItemAdapter.ViewHolder, position: Int) {
        val item = prodItemData[position]
        holder.CD_ITEM.text = prodItemData[position].CD_ITEM
        holder.CD_LC.text = prodItemData[position].CD_LC
        holder.QT_LC.text = prodItemData[position].QT_LC.toString()


        holder.itemView.setOnLongClickListener {
            onProdDetailClickedListener.prodDetailClick(item)
        }

    }

    override fun getItemCount(): Int {
        return prodItemData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val CD_ITEM : TextView = itemView.findViewById(R.id.rv_tv_val_CD_ITEM)
        val CD_LC : TextView = itemView.findViewById(R.id.rv_tv_val_LC)
        val QT_LC : TextView = itemView.findViewById(R.id.rv_tv_val_QT_LC)
    }
}





    

