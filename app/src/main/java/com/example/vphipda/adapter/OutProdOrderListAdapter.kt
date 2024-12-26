package com.example.vphipda.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.network.Wolist

class OutProdOrderListAdapter(var wolistData: ArrayList<Wolist>, var outProdClickedListener: OutProdClickedListener) : RecyclerView.Adapter<OutProdOrderListAdapter.ViewHolder>()  {

    interface OutProdClickedListener {
        fun outprodClick(wolist : Wolist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutProdOrderListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_outprod_d_out_order_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: OutProdOrderListAdapter.ViewHolder, position: Int) {
        val item = wolistData[position]
        holder.DT_WL.text = wolistData[position].DT_WL
        holder.NO_WO.text = wolistData[position].NO_WO
        holder.itemView.setOnClickListener{
            outProdClickedListener.outprodClick(item)
        }
    }

    override fun getItemCount(): Int {
        return wolistData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val DT_WL : TextView = itemView.findViewById(R.id.out_order_list_tx_dt_io)
        val NO_WO : TextView = itemView.findViewById(R.id.out_order_list_tx_no_io)
    }
}





    

