package com.example.vphipda.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.model.MoveWH


class MoveWHListAdapter(var moveWHData: ArrayList<MoveWH>) : RecyclerView.Adapter<MoveWHListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_movewh_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.CD_ITEM.text = moveWHData[position].CD_ITEM
        holder.QT.text = moveWHData[position].QT_LC.toString()
    }

    override fun getItemCount(): Int {
        return moveWHData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val CD_ITEM : TextView = itemView.findViewById(R.id.rv_move_val_CD_ITEM)
        val QT : TextView = itemView.findViewById(R.id.rv_move_val_QT_LD)
    }
}





    

