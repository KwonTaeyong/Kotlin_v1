package com.example.vphipda.view.fragment.sample

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
import com.example.vphipda.adapter.SampleOrderListAdapter
import com.example.vphipda.model.Prod
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.CDVPLANT
import com.example.vphipda.model.appData.Companion.SampleNoio
import com.example.vphipda.model.appData.Companion.SampleText
import com.example.vphipda.model.appData.Companion.SampleType
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.fragment_sample_order_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SampleOrderListFragment : Fragment(), SampleOrderListAdapter.OnSampleClickedListener {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController

    lateinit var sampleOrderListAdapter: SampleOrderListAdapter
    var sampleArray = ArrayList<SampleData>()

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
        return inflater.inflate(R.layout.fragment_sample_order_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        sampleOrderList_tx_top.text = SampleText

        sampleArray.clear()
        sampleOrderListAdapter = SampleOrderListAdapter(sampleArray, this)
        sampleOrderList_rv.adapter = sampleOrderListAdapter
        sampleOrderListAdapter.notifyDataSetChanged()
        setSampleArray()


        // 홈 버튼
        sampleOrderList_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }



    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setSampleArray() {

        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(SampleApiService::class.java)
        val callGet = api.get_sample(appData.SampleType)

        callGet.enqueue(object : Callback<GetSampleData> {
            override fun onResponse(
                call: Call<GetSampleData>,
                response: Response<GetSampleData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when {
                    !datalist.isNullOrEmpty() -> {
                        sampleArray.apply {
                            for(i in datalist.indices){
                                val Y = datalist[i].DT_IO.substring(0..3)
                                val M = datalist[i].DT_IO.substring(4..5)
                                val D = datalist[i].DT_IO.substring(6..7)
                                val dateForm = "$Y.$M.$D"

                                add(
                                    SampleData(
                                        datalist[i].NO_IO,
                                        dateForm,
                                        datalist[i].CD_PLANT,
                                        datalist[i].NM_KOR,
                                        datalist[i].DC_RMK,
                                        datalist[i].CD_VPLANT
                                    )
                                )
                            }
                        }
                        sampleOrderListAdapter.notifyDataSetChanged()
                    }
                    else -> {
                        AlertMessage("", "지시 내용이 없습니다.")
                    }
                }
            }

            override fun onFailure(call: Call<GetSampleData>, t: Throwable) {
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
            SampleOrderListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onSampleClick(sampleData: SampleData) {
        SampleNoio = sampleData.NO_IO
        CDVPLANT = sampleData.CD_VPLANT
        when(SampleType) {
            "get_atin" -> {
                navController.navigate(R.id.action_sampleOrderListFragment_to_sampleScanInFragment) // 계정대체 입고 / 반출 수량 업 // 구매 반품
            }

            "get_sample" -> {
                navController.navigate(R.id.action_sampleOrderListFragment_to_sampleScanOutFragment) // 계정대체 출고 / 반출 수량 다운 // 구매 반품
            }

            "get_nmreturn" -> {
                navController.navigate(R.id.action_sampleOrderListFragment_to_sampleScanInFragment) // 판매 반품 / 반입 수량 업 // 계정대체 입고
            }

            "get_pcreturn" -> {
                navController.navigate(R.id.action_sampleOrderListFragment_to_sampleScanOutFragment) // 구매 반품 / 반출 수량 다운 // 구매 반품
            }
        }
    }
}