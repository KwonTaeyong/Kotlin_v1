package com.example.vphipda.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.network.invenitem


class InvenListAdapter(var moveWHData: ArrayList<invenitem>, var onInvenitemClickedListener: OnInvenitemClickedListener) : RecyclerView.Adapter<InvenListAdapter.ViewHolder>() {




    interface OnInvenitemClickedListener {
        fun oninvenClick(item : invenitem): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_inven, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = moveWHData[position]
        holder.CD_ITEM.text = moveWHData[position].CD_ITEM
        holder.QT.text = moveWHData[position].QT_CNT.toString()

        holder.itemView.setOnLongClickListener {
            onInvenitemClickedListener.oninvenClick(item)
        }


    }

    override fun getItemCount(): Int {
        return moveWHData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val CD_ITEM : TextView = itemView.findViewById(R.id.rv_inven_val_CD_ITEM)
        val QT : TextView = itemView.findViewById(R.id.rv_inven_val_QT_LD)
    }
}





    

