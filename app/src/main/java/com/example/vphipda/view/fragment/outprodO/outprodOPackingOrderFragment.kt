package com.example.vphipda.view.fragment.outprodO

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.adapter.OutProdListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.network.GetPkwoSo
import com.example.vphipda.network.WolistDetail
import com.example.vphipda.network.outprodApiService
import kotlinx.android.synthetic.main.fragment_outprod_o_packing_order.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodOPackingOrderFragment : Fragment(), OutProdListAdapter.OutProdListLongClickedListener {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var outProdListAdapter: OutProdListAdapter
    var moveWHArray = ArrayList<WolistDetail>()

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
        return inflater.inflate(R.layout.fragment_outprod_o_packing_order, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)


        moveWHArray.clear()
        outProdListAdapter = OutProdListAdapter(moveWHArray, this)
        outprodOPackingOrder_rv.adapter = outProdListAdapter
        outProdListAdapter.notifyDataSetChanged()

        setPackingOrder()

        // 홈 버튼
        outprodOPackingOrder_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        // 스캔 버튼
        outprodOPackingOrder_bt_scan.setOnClickListener {
            navController.navigate(R.id.action_outprodOPdetailScanFragment)
        }

        // 목록 버튼
        outprodOPackingOrder_bt_list.setOnClickListener {
            navController.navigate(R.id.action_outprodOPdetailListFragment)
        }

    }

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodOPackingOrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setPackingOrder() {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.get_pkwo_so("get_pkwo_so", appData.PackingOrderCode)

        callGet.enqueue(object : Callback<GetPkwoSo> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<GetPkwoSo>,
                response: Response<GetPkwoSo>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when (state) {
                    1 -> {
                        if (!datalist.isNullOrEmpty()) {
                            moveWHArray.clear()
                            moveWHArray.apply {
                                for(i in datalist.indices){
                                    add(WolistDetail(datalist[i].CD_ITEM, datalist[i].QT_WO, datalist[i].QT_PK))
                                }
                            }
                            outProdListAdapter.notifyDataSetChanged()
                        }
                    }

                    0 -> {
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetPkwoSo>, t: Throwable) {
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

    override fun outProdListLongClick(item: WolistDetail): Boolean {

        return true
    }
}