package com.example.vphipda.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.model.Prod


class ProdListAdapter(var prodData: ArrayList<Prod>, var onProdClickedListener: OnProdClickedListener) : RecyclerView.Adapter<ProdListAdapter.ViewHolder>() {

    interface OnProdClickedListener {
        fun onprodClick(prod : Prod)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_prodlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = prodData[position]
        holder.DT_ID.text = prodData[position].DT_IO
        holder.LN_PARTNER.text = prodData[position].LN_PARTNER
        holder.NO_IO.text = prodData[position].NO_IO
        holder.itemView.setOnClickListener{
            onProdClickedListener.onprodClick(item)
        }
    }

    override fun getItemCount(): Int {
        return prodData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val DT_ID : TextView = itemView.findViewById(R.id.prodlist_tx_DT_ID)
        val LN_PARTNER : TextView = itemView.findViewById(R.id.prodlist_tx_LN_PARTNER)
        val NO_IO : TextView = itemView.findViewById(R.id.prodlist_tx_NO_IO)
    }
}





    

