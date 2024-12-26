package com.example.vphipda.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.model.ProdItem


class ProdDetailListAdapter(var prodItemData: ArrayList<ProdItem>, var onProdOneClickedListener: OnProdOneClickedListener) : RecyclerView.Adapter<ProdDetailListAdapter.ViewHolder>()  {



    interface OnProdOneClickedListener {
        fun onProdOneClick(item : ProdItem)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdDetailListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_prod_detail_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdDetailListAdapter.ViewHolder, position: Int) {
        val item = prodItemData[position]
        holder.CD_ITEM.text = prodItemData[position].CD_ITEM
        holder.QT_IO.text = prodItemData[position].QT_IO.toString()
        holder.QT_LC.text = prodItemData[position].QT_LC.toString()

        if(prodItemData[position].QT_IO.toString() == prodItemData[position].QT_LC.toString()) {
            holder.QT_LC.setTextColor(Color.rgb(51,153,51))
        } else {
            holder.QT_LC.setTextColor(Color.RED)
        }

        holder.itemView.setOnClickListener {
            onProdOneClickedListener.onProdOneClick(item)
        }



    }

    override fun getItemCount(): Int {
        return prodItemData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val CD_ITEM : TextView = itemView.findViewById(R.id.rv_tv_val_CD_ITEM)
        val QT_IO : TextView = itemView.findViewById(R.id.rv_tv_val_QT_IO)
        val QT_LC : TextView = itemView.findViewById(R.id.rv_tv_val_QT_LC)
    }
}





    

