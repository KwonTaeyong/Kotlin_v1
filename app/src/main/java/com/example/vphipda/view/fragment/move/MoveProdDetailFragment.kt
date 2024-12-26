package com.example.vphipda.view.fragment.move

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
import com.example.vphipda.adapter.MoveProdDetailAdapter
import com.example.vphipda.model.MoveDetail
import com.example.vphipda.model.appData
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.fragment_move_prod_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MoveProdDetailFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var no_io: String? = null
    private var scanItem: String? = ""
    lateinit var navController: NavController
    lateinit var moveProdDetailAdapter: MoveProdDetailAdapter
    var moveDetailArray = ArrayList<MoveDetail>()

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
        return inflater.inflate(R.layout.fragment_move_prod_detail, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        no_io = arguments?.getString("no_io")
        moveProdDetail_tx_main.text = no_io

        // 아답터
        moveDetailArray.clear()
        moveProdDetailAdapter = MoveProdDetailAdapter(moveDetailArray)
        movingList_rv.adapter = moveProdDetailAdapter
        moveProdDetailAdapter.notifyDataSetChanged()
        setmoveDetailArray(no_io.toString())



        // 홈 버튼
        moveProdDetail_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        // 반출 버튼
        moveProdDetail_bt_out.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("no_io", no_io)
            bundle.putString("main_tx", "반출")
            bundle.putString("state", "out")
            navController.navigate(R.id.action_moveProdDetailFragment_to_moveProdScanFragment, bundle)
        }

        // 저장 버튼
        moveProdDetail_bt_save.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("no_io", no_io)
            bundle.putString("main_tx", "저장")
            bundle.putString("state", "save")
            navController.navigate(R.id.action_moveProdDetailFragment_to_moveProdScanFragment, bundle)
        }

        moveProdDetail_bt_scan.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("no_io", no_io)
            bundle.putString("main_tx", "스캔")
            bundle.putString("state", "scan")
            navController.navigate(R.id.action_moveProdDetailFragment_to_moveProdScanFragment, bundle)


        }

    }

    private fun mpio_cls(no_io: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MoveApiService::class.java)
        val callGet = api.mpioCls("mpio_cls", no_io)

        callGet.enqueue(object : Callback<mpioClsData> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<mpioClsData>,
                response: Response<mpioClsData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                when (state) {
                    1 -> {
                        Toast.makeText(navController.context, message, Toast.LENGTH_SHORT).show()
                        navController.navigate(R.id.action_moveProd)
                    }
                    0 -> {
                        if (!message.isNullOrBlank()){
                            Toast.makeText(navController.context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<mpioClsData>, t: Throwable) {
                Toast.makeText(navController.context, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun setmoveDetailArray(no_io:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MoveApiService::class.java)
        val callGet = api.getMpioDetail("get_mpio_detail", no_io)

        callGet.enqueue(object : Callback<getMpioDetailData> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<getMpioDetailData>,
                response: Response<getMpioDetailData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when (state) {
                    1 -> {
                        if (!datalist.isNullOrEmpty()) {
                            moveDetailArray.clear()
                            moveDetailArray.apply {
                                for(i in datalist.indices){
                                    add(MoveDetail(datalist[i].CD_ITEM, datalist[i].QT_IO, datalist[i].QT_LC))
                                }
                            }
                            moveProdDetailAdapter.notifyDataSetChanged()
                        }
                    }

                    0 -> {
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<getMpioDetailData>, t: Throwable) {
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
            MoveProdDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}