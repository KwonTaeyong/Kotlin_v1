package com.example.vphipda.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.model.MoveDetail


class MoveProdDetailAdapter(var moveDetailData: ArrayList<MoveDetail>) : RecyclerView.Adapter<MoveProdDetailAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_movedetail_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.CD_ITEM.text = moveDetailData[position].CD_ITEM
        holder.QT_IO.text = moveDetailData[position].QT_IO.toString()
        holder.QT_LC.text = moveDetailData[position].QT_LC.toString()


        if(moveDetailData[position].QT_IO == moveDetailData[position].QT_LC) {
            holder.QT_LC.setTextColor(Color.rgb(51,153,51))
        } else {
            holder.QT_LC.setTextColor(Color.RED)
        }


    }

    override fun getItemCount(): Int {
        return moveDetailData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val CD_ITEM : TextView = itemView.findViewById(R.id.moveProdDetail_th_cdItem)
        val QT_IO : TextView = itemView.findViewById(R.id.moveProdDetail_th_qt_io)
        val QT_LC : TextView = itemView.findViewById(R.id.moveProdDetail_th_QT_LC)
    }
}





    

