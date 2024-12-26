package com.example.vphipda.view.fragment.move

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
import com.example.vphipda.adapter.MoveListAdapter
import com.example.vphipda.model.Move
import com.example.vphipda.model.appData
import com.example.vphipda.network.GetMpioData
import com.example.vphipda.network.MoveApiService
import kotlinx.android.synthetic.main.fragment_move_prod.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MoveProdFragment : Fragment(), MoveListAdapter.OnMoveClickedListener {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var moveListAdapter: MoveListAdapter
    var moveArray = ArrayList<Move>()

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
        return inflater.inflate(R.layout.fragment_move_prod, container, false)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        moveArray.clear()
        moveListAdapter = MoveListAdapter(moveArray, this)
        movelist_rv.adapter = moveListAdapter
        moveListAdapter.notifyDataSetChanged()

        setmrodArray()


        // 홈 버튼
        move_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

    }

    private fun setmrodArray() {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MoveApiService::class.java)
        val callGet = api.getMpio("get_mpio")

        callGet.enqueue(object : Callback<GetMpioData> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<GetMpioData>,
                response: Response<GetMpioData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when {
                    !datalist.isNullOrEmpty() -> {
                        moveArray.clear()
                        moveArray.apply {
                            for(i in datalist.indices){
                                val Y = datalist[i].DT_IO.substring(0..3)
                                val M = datalist[i].DT_IO.substring(4..5)
                                val D = datalist[i].DT_IO.substring(6..7)
                                val dateForm = "$Y.$M.$D"

                                add(Move(datalist[i].NO_IO ,dateForm, datalist[i].I_SL, datalist[i].O_SL))
                            }
                        }
                        moveListAdapter.notifyDataSetChanged()
                    }
                    else -> {
                        AlertMessage("", "해당 지시가 없습니다.")
                    }
                }
            }

            override fun onFailure(call: Call<GetMpioData>, t: Throwable) {
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
            MoveProdFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onmoveClick(move: Move) {
        val bundle = Bundle()
        bundle.putString("no_io", move.NO_IO)
        navController.navigate(R.id.action_moveProdFragment_to_moveProdDetailFragment, bundle)
    }
}