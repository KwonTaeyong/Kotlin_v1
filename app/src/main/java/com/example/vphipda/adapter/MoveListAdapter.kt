package com.example.vphipda.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.model.Move


class MoveListAdapter(var moveData: ArrayList<Move>, var onMoveClickedListener: OnMoveClickedListener) : RecyclerView.Adapter<MoveListAdapter.ViewHolder>() {

    interface OnMoveClickedListener {
        fun onmoveClick(move : Move)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_movelist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = moveData[position]
        holder.DT_IO.text = moveData[position].DT_IO
        holder.NO_IO.text = moveData[position].NO_IO
        holder.I_SL.text = moveData[position].I_SL
        holder.O_SL.text = moveData[position].O_SL




        holder.itemView.setOnClickListener{
            onMoveClickedListener.onmoveClick(item)
        }
    }

    override fun getItemCount(): Int {
        return moveData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val DT_IO : TextView = itemView.findViewById(R.id.movelist_tx_dt_io)
        val NO_IO : TextView = itemView.findViewById(R.id.movelist_tx_no_io)
        val I_SL : TextView = itemView.findViewById(R.id.movelist_tx_i_sl)
        val O_SL : TextView = itemView.findViewById(R.id.movelist_tx_o_sl)
    }
}





    

