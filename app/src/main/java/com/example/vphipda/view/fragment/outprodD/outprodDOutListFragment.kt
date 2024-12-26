package com.example.vphipda.view.fragment.outprodD

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
import com.example.vphipda.adapter.OutProdListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.AlertF
import com.example.vphipda.model.appData.Companion.DataNoWo
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.fragment_outprod_d_out_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodDOutListFragment : Fragment(), OutProdListAdapter.OutProdListLongClickedListener {
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
        return inflater.inflate(R.layout.fragment_outprod_d_out_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        moveWHArray.clear()
        outProdListAdapter = OutProdListAdapter(moveWHArray, this)
        outprodDOutList_rv.adapter = outProdListAdapter
        outProdListAdapter.notifyDataSetChanged()


        setoutListArray()

        //홈 버튼
        outprodDOutList_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }
        
        // 스캔으로
        outprodDOutList_bt_scan.setOnClickListener { 
            navController.navigate(R.id.action_outprodDOutScan)
        }
        
        // 출고 마감
        outprodDOutList_bt_end.setOnClickListener {
            cls_wo()
        }


    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodDOutListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun cls_wo() {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.cls_wo("cls_wo", DataNoWo)

        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();

        callGet.enqueue(object : Callback<GetClsWo> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<GetClsWo>,
                response: Response<GetClsWo>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message

                when (state) {
                    1 -> {
                        progressDialog.dismiss();
                        navController.navigate(R.id.action_outprodDOutOrderList)
                        Toast.makeText(navController.context, "출고 마감 완료", Toast.LENGTH_LONG).show()
                    }

                    0 -> {
                        progressDialog.dismiss();
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetClsWo>, t: Throwable) {
                progressDialog.dismiss();
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



    private fun setoutListArray() {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.getWolistDetail("get_wolist_detail", DataNoWo)

        callGet.enqueue(object : Callback<GetWolistDetail> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<GetWolistDetail>,
                response: Response<GetWolistDetail>
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
                                    add(WolistDetail(datalist[i].CD_ITEM, datalist[i].QT_IO, datalist[i].QT_WO))
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

            override fun onFailure(call: Call<GetWolistDetail>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })
    }


    private fun del_wo(cd_item:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.del_wo("del_wo", DataNoWo, cd_item)

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();


        callGet.enqueue(object : Callback<GetDelWo> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<GetDelWo>,
                response: Response<GetDelWo>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message

                when (state) {
                    1 -> {
                        progressDialog.dismiss();
                        setoutListArray()
                        AlertMessage("삭제 성공", "지시 번호 : ${DataNoWo}\n품목 코드 : ${cd_item}")
                    }

                    0 -> {
                        progressDialog.dismiss();
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetDelWo>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", appData.AlertF)
            }
        })
    }

    override fun outProdListLongClick(item: WolistDetail): Boolean {

        val builder = AlertDialog.Builder(navController.context)
        builder.setTitle("삭제 하시겠습니까?")
            .setMessage("지시 번호 : ${DataNoWo}\n품목코드: ${item.CD_ITEM}")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, id ->
                    del_wo(item.CD_ITEM)
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, id ->

                })
        // 다이얼로그를 띄워주기
        builder.show()


        return true
    }
}