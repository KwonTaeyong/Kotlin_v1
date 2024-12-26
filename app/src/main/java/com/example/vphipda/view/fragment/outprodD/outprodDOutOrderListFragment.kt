package com.example.vphipda.view.fragment.outprodD

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.adapter.OutProdOrderListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.DataNoWo
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.fragment_outprod_d_out_order_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodDOutOrderListFragment : Fragment(), OutProdOrderListAdapter.OutProdClickedListener {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var outProdOrderListAdapter: OutProdOrderListAdapter
    var wolistArray = ArrayList<Wolist>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_outprod_d_out_order_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        wolistArray.clear()
        outProdOrderListAdapter = OutProdOrderListAdapter(wolistArray, this)
        outprodDOutOrderList_rv.adapter = outProdOrderListAdapter
        outProdOrderListAdapter.notifyDataSetChanged()

        DataNoWo = ""
        setprodArray()


        //홈 버튼
        outprodDOutOrderList_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }




    }




    @SuppressLint("NotifyDataSetChanged")
    private fun setprodArray() {

        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.getWolist("get_wolist")

        callGet.enqueue(object : Callback<GetWolist> {
            override fun onResponse(
                call: Call<GetWolist>,
                response: Response<GetWolist>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when {
                    !datalist.isNullOrEmpty() -> {
                        wolistArray.clear()
                        wolistArray.apply {
                            for(i in datalist.indices){
                                val Y = datalist[i].DT_WL.substring(0..3)
                                val M = datalist[i].DT_WL.substring(4..5)
                                val D = datalist[i].DT_WL.substring(6..7)
                                val dateForm2 = "$Y.$M.$D"
                                add(
                                    Wolist(
                                    datalist[i].NO_WO,
                                        dateForm2)
                                )
                            }
                        }
                        outProdOrderListAdapter.notifyDataSetChanged()
                    }
                    else -> {
                        AlertMessage("", "지시 내용이 없습니다.")
                    }
                }
            }

            override fun onFailure(call: Call<GetWolist>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })
    }


    fun AlertMessage(titleM:String, bodyM:String) {
        val builder = AlertDialog.Builder(navController.context)
        builder.setTitle(titleM)
            .setMessage(bodyM)
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, id ->

                })
        // 다이얼로그를 띄워주기
        builder.show()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodDOutOrderListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun outprodClick(wolist: Wolist) {
        DataNoWo = wolist.NO_WO
        navController.navigate(R.id.action_outprodDOutOrderListFragment_to_outprodDOutScanFragment)
    }
}