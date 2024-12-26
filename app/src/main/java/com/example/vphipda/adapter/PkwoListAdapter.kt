package com.example.vphipda.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.network.Pkwo

class PkwoListAdapter(var pkwoData: ArrayList<Pkwo>, var pkwoListClickedListener: PkwoListAdapter.PkwoListClickedListener) : RecyclerView.Adapter<PkwoListAdapter.ViewHolder>()  {

    interface PkwoListClickedListener {
        fun pkwoClick(pkwo : Pkwo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PkwoListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_outprod_o_packing_order_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PkwoListAdapter.ViewHolder, position: Int) {
        val item = pkwoData[position]
        holder.NM_KOR.text = pkwoData[position].NM_KOR
        holder.LN_PARTNER.text = pkwoData[position].LN_PARTNER
        holder.NO_IO.text = pkwoData[position].NO_SO
        holder.itemView.setOnClickListener{
            pkwoListClickedListener.pkwoClick(item)
        }
    }

    override fun getItemCount(): Int {
        return pkwoData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val NM_KOR : TextView = itemView.findViewById(R.id.pkwo_list_tx_nm_kor)
        val LN_PARTNER : TextView = itemView.findViewById(R.id.pkwo_list_tx_ln_partner)
        val NO_IO : TextView = itemView.findViewById(R.id.pkwo_list_tx_no_io)
    }
}





    

