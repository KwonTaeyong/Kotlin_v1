package com.example.vphipda.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.network.WolistDetail
import com.example.vphipda.network.invenitem


class OutProdListAdapter(var moveWHData: ArrayList<WolistDetail>, var outProdListLongClickedListener: OutProdListLongClickedListener) : RecyclerView.Adapter<OutProdListAdapter.ViewHolder>() {


    interface OutProdListLongClickedListener {
        fun outProdListLongClick(item : WolistDetail): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_prod_detail_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = moveWHData[position]
        holder.CD_ITEM.text = moveWHData[position].CD_ITEM
        holder.QT.text = moveWHData[position].QT_IO.toString()
        holder.QT2.text = moveWHData[position].QT_WO.toString()

        if(moveWHData[position].QT_IO.toString() == moveWHData[position].QT_WO.toString()) {
            holder.QT2.setTextColor(Color.rgb(51,153,51))
        } else {
            holder.QT2.setTextColor(Color.RED)
        }

        holder.itemView.setOnLongClickListener{
            outProdListLongClickedListener.outProdListLongClick(item)
        }

    }

    override fun getItemCount(): Int {
        return moveWHData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val CD_ITEM : TextView = itemView.findViewById(R.id.rv_tv_val_CD_ITEM)
        val QT : TextView = itemView.findViewById(R.id.rv_tv_val_QT_IO)
        val QT2 : TextView = itemView.findViewById(R.id.rv_tv_val_QT_LC)
    }
}





    

