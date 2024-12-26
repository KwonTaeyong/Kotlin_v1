package com.example.vphipda.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.model.appData.Companion.ScanCodeState
import com.example.vphipda.network.Pallet

class PalletListAdapter(
                        var palletData: ArrayList<Pallet>,
                        var pkwoListClickedListener: PalletListAdapter.PkwoListClickedListener,
                        var palletLongClickedListener: PalletListAdapter.PalletLongClickedListener,
                        var palletBtnClickedListener: PalletBtnClickedListener
                        ) : RecyclerView.Adapter<PalletListAdapter.ViewHolder>()  {

    interface PkwoListClickedListener {
        fun pkwoClick(pallet : Pallet)
    }

    interface PalletLongClickedListener {
        fun palletlongClick(item : Pallet): Boolean
    }

    interface PalletBtnClickedListener {
        fun palletBtnClick(item: Pallet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PalletListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_outprod_o_pdetail_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PalletListAdapter.ViewHolder, position: Int) {
        val item = palletData[position]
        holder.CD_LC.text = palletData[position].NO_PMS
        holder.P_PVLHCASE.text = palletData[position].P_PVLHCASE
        holder.NO_SO.text = palletData[position].NO_SO

        holder.itemView.setOnClickListener{
            pkwoListClickedListener.pkwoClick(item)
        }


        holder.itemView.setOnLongClickListener {
            palletLongClickedListener.palletlongClick(item)
        }


        holder.ADD_BTN.setOnClickListener {
            palletBtnClickedListener.palletBtnClick(item)
        }

        when(ScanCodeState) {
            1 -> {
                holder.ADD_BTN.isVisible = false
            }
        }


    }

    override fun getItemCount(): Int {
        return palletData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val CD_LC : TextView = itemView.findViewById(R.id.Pdetail_tx_p_pvlhcase)
        val P_PVLHCASE : TextView = itemView.findViewById(R.id.Pdetail_tx_cd_lc)
        var NO_SO : TextView = itemView.findViewById(R.id.Pdetail_tx_no_so)
        val ADD_BTN: Button = itemView.findViewById(R.id.rv_outprod_o_add_btn)
    }
}





    

