package com.example.vphipda.view.fragment.moveWH

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
import com.example.vphipda.adapter.MoveWHListAdapter
import com.example.vphipda.model.MoveWH
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.BaseURL
import com.example.vphipda.network.GetMovingData
import com.example.vphipda.network.MoveApiService
import kotlinx.android.synthetic.main.fragment_inpord_detail_list.*
import kotlinx.android.synthetic.main.fragment_inprod_detail.*
import kotlinx.android.synthetic.main.fragment_move_w_h.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MoveWHFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var cd_lc: String
    lateinit var sel_main_tx: String
    lateinit var sel_sub_tx: String


    lateinit var moveWHListAdapter: MoveWHListAdapter
    var moveWHArray = ArrayList<MoveWH>()

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
        return inflater.inflate(R.layout.fragment_move_w_h, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        cd_lc = arguments?.getString("cd_lc").toString()
        sel_main_tx = arguments?.getString("sel_main_tx").toString()
        sel_sub_tx = arguments?.getString("sel_sub_tx").toString()

        moveWH_tx_main.text = sel_main_tx
        moveWH_tx_table_nm.text = sel_sub_tx

        moveWHArray.clear()
        moveWHListAdapter = MoveWHListAdapter(moveWHArray)
        movingList_rv.adapter = moveWHListAdapter
        moveWHListAdapter.notifyDataSetChanged()
        setmoveWHArray()


        moveWH_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

    }

    private fun setmoveWHArray() {

        val retrofit = Retrofit.Builder()
            .baseUrl(BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MoveApiService::class.java)
        val callGet = api.getMoving("get_qtlc", cd_lc)

        callGet.enqueue(object : Callback<GetMovingData> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<GetMovingData>,
                response: Response<GetMovingData>
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
                                    add(MoveWH(datalist[i].CD_ITEM, datalist[i].QT_LC))
                                }
                            }
                            moveWHListAdapter.notifyDataSetChanged()
                        }
                    }

                    0 -> {
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetMovingData>, t: Throwable) {
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
            MoveWHFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}