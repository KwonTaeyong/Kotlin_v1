package com.example.vphipda.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vphipda.R
import com.example.vphipda.network.SampleData


class SampleOrderListAdapter(var smapleData: ArrayList<SampleData>, var onSampleClickedListener: OnSampleClickedListener) : RecyclerView.Adapter<SampleOrderListAdapter.ViewHolder>() {

    interface OnSampleClickedListener {
        fun onSampleClick(sampleData: SampleData)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_sample, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = smapleData[position]
        holder.NO_IO.text = smapleData[position].NO_IO
        holder.DT_IO.text = smapleData[position].DT_IO
        holder.CD_PLANT.text = smapleData[position].CD_PLANT
        holder.NM_KOR.text = smapleData[position].NM_KOR
        holder.DC_RMK.text = smapleData[position].DC_RMK

        holder.itemView.setOnClickListener{
            onSampleClickedListener.onSampleClick(item)
        }
    }

    override fun getItemCount(): Int {
        return smapleData.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val NO_IO : TextView = itemView.findViewById(R.id.rv_sample_tx_no_io)
        val DT_IO : TextView = itemView.findViewById(R.id.rv_sample_tx_dt_io)
        val CD_PLANT : TextView = itemView.findViewById(R.id.rv_sample_tx_cd_plant)
        val NM_KOR : TextView = itemView.findViewById(R.id.rv_sample_tx_nm_kor)
        val DC_RMK : TextView = itemView.findViewById(R.id.rv_sample_tx_dc_rmk)
    }
}





    

