package com.example.vphipda.view.fragment.inprod

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.adapter.ProdDetailListAdapter
import com.example.vphipda.model.ProdItem
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.AlertF
import com.example.vphipda.network.InprodApiService
import com.example.vphipda.network.getReceivingDetailData
import com.example.vphipda.network.loadingClsData
import kotlinx.android.synthetic.main.fragment_inpord_detail_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class InpordDetailListFragment : Fragment(),  ProdDetailListAdapter.OnProdOneClickedListener {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var no_io: String

    lateinit var prodDetailListAdapter: ProdDetailListAdapter
    var prodItemArray = ArrayList<ProdItem>()

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

        return inflater.inflate(R.layout.fragment_inpord_detail_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        no_io = arguments?.getString("no_io").toString()
        prodDetailList_tx_top.text = no_io

        prodItemArray.clear()
        prodDetailListAdapter = ProdDetailListAdapter(prodItemArray, this)
        pordDetailList_rv.adapter = prodDetailListAdapter
        prodDetailListAdapter.notifyDataSetChanged()
        setprodItemArray()

        prodDetailList_bt_back.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("NO_ID", no_io)
            navController.navigate(R.id.action_inprod_detail, bundle)
        }

        prodDetailList_bt_cls.setOnClickListener {
            loadingCls()
        }

        prodDetailList_home_btn.setOnClickListener{
            navController.navigate(R.id.action_home)
        }

    }

    private fun loadingCls() {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InprodApiService::class.java)
        val callGet = api.loadingCls("cls_rcv", no_io)

        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();

        callGet.enqueue(object : Callback<loadingClsData> {
            override fun onResponse(
                call: Call<loadingClsData>,
                response: Response<loadingClsData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                when (state) {
                    1 -> {
                        progressDialog.dismiss();
                        navController.navigate(R.id.action_inprod)
                    }

                    0 -> {
                        progressDialog.dismiss();
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<loadingClsData>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", AlertF)
            }
        })

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setprodItemArray() {

        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InprodApiService::class.java)
        val callGet = api.getReceivingDetail("get_rcv_detail", no_io)

        callGet.enqueue(object : Callback<getReceivingDetailData> {
            override fun onResponse(
                call: Call<getReceivingDetailData>,
                response: Response<getReceivingDetailData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when (state) {
                    1 -> {
                        if (!datalist.isNullOrEmpty()) {
                            prodItemArray.apply {
                                for(i in datalist.indices){
                                    add(ProdItem(datalist[i].CD_ITEM, datalist[i].QT_IO, datalist[i].QT_LC))
                                }
                            }
                            prodDetailListAdapter.notifyDataSetChanged()
                        }
                    }

                    0 -> {
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<getReceivingDetailData>, t: Throwable) {
                AlertMessage("", AlertF)
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
            InpordDetailListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onProdOneClick(item: ProdItem) {
        val bundle = Bundle()
        bundle.putString("no_io", no_io)
        bundle.putString("cd_item", item.CD_ITEM)
        navController.navigate(R.id.action_inpordDetailListFragment_to_inprodDetailListItemFragment, bundle)
    }
}