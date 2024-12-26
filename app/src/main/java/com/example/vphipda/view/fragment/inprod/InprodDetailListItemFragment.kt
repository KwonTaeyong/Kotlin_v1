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
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.adapter.ProdDetailListItemAdapter
import com.example.vphipda.model.ProdDetailItem
import com.example.vphipda.model.appData
import com.example.vphipda.network.GetDelRcvLog
import com.example.vphipda.network.GetRcvLog
import com.example.vphipda.network.InprodApiService
import kotlinx.android.synthetic.main.fragment_inprod_detail_list_item.*
import kotlinx.android.synthetic.main.fragment_inprod_detail_list_item.pordDetailList_rv
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class InprodDetailListItemFragment : Fragment(), ProdDetailListItemAdapter.OnProdDetailClickedListener {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var no_io: String
    lateinit var cd_item: String


    lateinit var prodDetailListItemAdapter: ProdDetailListItemAdapter
    var prodItemArray = ArrayList<ProdDetailItem>()

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
        return inflater.inflate(R.layout.fragment_inprod_detail_list_item, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        no_io = arguments?.getString("no_io").toString()
        cd_item = arguments?.getString("cd_item").toString()
        inprodDetailListItem_tx_top.text = no_io


        prodItemArray.clear()
        prodDetailListItemAdapter = ProdDetailListItemAdapter(prodItemArray, this)
        pordDetailList_rv.adapter = prodDetailListItemAdapter
        prodDetailListItemAdapter.notifyDataSetChanged()
        setprodDetailItemArray(cd_item)
        
        //홈 버튼
        inprodDetailListItem_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }


    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InprodDetailListItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setprodDetailItemArray(cd_item:String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InprodApiService::class.java)
        val callGet = api.get_rcv_log("get_rcv_log", no_io, cd_item)

        callGet.enqueue(object : Callback<GetRcvLog> {
            override fun onResponse(
                call: Call<GetRcvLog>,
                response: Response<GetRcvLog>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data
                when (state) {
                    1 -> {

                        if (!datalist.isNullOrEmpty()) {
                            prodItemArray.clear()
                            prodItemArray.apply {
                                for(i in datalist.indices){
                                    add(ProdDetailItem(datalist[i].CD_LC, datalist[i].CD_ITEM, datalist[i].QT_LC, datalist[i].DTS_LC))
                                }
                            }
                            prodDetailListItemAdapter.notifyDataSetChanged()
                        } else {
                            prodItemArray.clear()
                            prodDetailListItemAdapter.notifyDataSetChanged()
                            AlertMessage("", "목록이 없습니다.")

                        }
                    }

                    0 -> {
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetRcvLog>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun delRcvLog(cd_item:String, dts_lc:String, cd_lc:String, qt_lc:String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InprodApiService::class.java)
        val callGet = api.del_rcv_log("del_rcv_log", no_io, cd_item, dts_lc, cd_lc, qt_lc)


        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();

        callGet.enqueue(object : Callback<GetDelRcvLog> {
            override fun onResponse(
                call: Call<GetDelRcvLog>,
                response: Response<GetDelRcvLog>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message

                when (state) {
                    1 -> {
                        setprodDetailItemArray(cd_item)
                        progressDialog.dismiss();
                        AlertMessage("삭제 성공", "품목 : ${cd_item}\n렉 코드 : ${cd_lc}")
                    }

                    0 -> {
                        progressDialog.dismiss();
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetDelRcvLog>, t: Throwable) {
                progressDialog.dismiss();
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



    override fun prodDetailClick(item: ProdDetailItem): Boolean {
        val builder = AlertDialog.Builder(navController.context)
        builder.setTitle("삭제 하시겠습니까?")
            .setMessage("저장 위치: ${item.CD_LC}\n품목 코드: ${item.CD_ITEM}\n수량: ${item.QT_LC}\n입력시간:\n ${item.DTS_LC}")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, id ->
                    delRcvLog(item.CD_ITEM, item.DTS_LC, item.CD_LC, item.QT_LC.toString())
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, id ->

                })
        // 다이얼로그를 띄워주기
        builder.show()

        return true

    }
}