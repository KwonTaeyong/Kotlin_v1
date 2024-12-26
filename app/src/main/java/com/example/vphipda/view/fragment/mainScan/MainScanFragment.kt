package com.example.vphipda.view.fragment.mainScan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.adapter.MainScanAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.dialog_new_prod_ly.view.*
import kotlinx.android.synthetic.main.dialog_new_rack_ly.view.*
import kotlinx.android.synthetic.main.fragment_main_scan.*
import kotlinx.android.synthetic.main.fragment_outprod_o_pdetail_scan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MainScanFragment : Fragment(), TextWatcher {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    var imm : InputMethodManager? = null
    lateinit var mainScanAdapter: MainScanAdapter


    var searchCodeArray = ArrayList<SearchCodeData>()

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
        return inflater.inflate(R.layout.fragment_main_scan, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?



        //홈 버튼
        MainScan_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }


        MainScan_ed_scan.requestFocus()
        hideKeyboard(view)
        MainScan_ed_scan.addTextChangedListener(this)


        //리사이클 뷰
        searchCodeArray.clear()
        mainScanAdapter = MainScanAdapter(searchCodeArray)
        MainScan_rv.adapter = mainScanAdapter
        mainScanAdapter.notifyDataSetChanged()

        //렉 지정 모달

        MainScan_ly_rack.setOnLongClickListener {
            val builder = AlertDialog.Builder(navController.context)
            var v1 = layoutInflater.inflate(R.layout.dialog_new_rack_ly, null)
            builder.setView(v1)
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        var condition1 = v1.rack_et.text.isNullOrBlank()
                        var item_code:String = "" // 품목 코드
                        if (!condition1) {
                            item_code = v1.rack_et.text.toString() // 품목 코드
                            MainScan_tx_rack.text = item_code
                            setSearchCodeArray(item_code)
                        } else {
                            AlertMessage("", "렉 코드를 입력해 주세요.")
                        }

                        hideKeyboard(v1)

                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                        hideKeyboard(v1)
                    })

            // 다이얼로그를 띄워주기
            builder.show()
            v1.rack_et.requestFocus()

            return@setOnLongClickListener(true)
        }

        //a 가상 창고 버튼
        MainScan_bt_avRack.setOnClickListener {
            val av_code = "AV000001"
            MainScan_tx_rack.text = av_code
            setSearchCodeArray(av_code)
        }

        //b 가상 창고 버튼
        MainScan_bt_bvRack.setOnClickListener {
            val av_code = "BV000001"
            MainScan_tx_rack.text = av_code
            setSearchCodeArray(av_code)
        }

        // 포트 버튼
        MainScan_bt_port.setOnClickListener {
            val av_code = "BP000001"
            MainScan_tx_rack.text = av_code
            setSearchCodeArray(av_code)
        }

        // 샘플
        MainScan_bt_sample.setOnClickListener {
            val av_code = "PV000001"
            MainScan_tx_rack.text = av_code
            setSearchCodeArray(av_code)
        }



    }

    fun showKeyboard(view: View) {
        imm?.showSoftInput(view, 0)
    }

    fun hideKeyboard(view: View) {
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setSearchCodeArray(Scode:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InprodApiService::class.java)
        val callGet = api.search_code("search_code", Scode)

        callGet.enqueue(object : Callback<GetSearchCode> {
            override fun onResponse(
                call: Call<GetSearchCode>,
                response: Response<GetSearchCode>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data


                when(state) {
                    1 -> {
                        when {
                            !datalist.isNullOrEmpty() -> {
                                searchCodeArray.clear()
                                searchCodeArray.apply {
                                    for(i in datalist.indices){
                                        add(
                                            SearchCodeData(datalist[i].CD_LC,
                                                datalist[i].CD_ITEM,
                                                datalist[i].QT
                                            )
                                        )
                                    }
                                }
                                mainScanAdapter.notifyDataSetChanged()
                            }
                            else -> {
                                searchCodeArray.clear()
                                mainScanAdapter.notifyDataSetChanged()
                                AlertMessage("", "저장된 품목이 없습니다.")
                            }
                        }
                    }
                    else -> {
                        searchCodeArray.clear()
                        mainScanAdapter.notifyDataSetChanged()
                        AlertMessage("", message.toString())
                    }

                }
            }

            override fun onFailure(call: Call<GetSearchCode>, t: Throwable) {
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
            MainScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(!s.isNullOrBlank()) {
                MainScan_tx_rack.text = s.toString()
                setSearchCodeArray(s.toString())
                MainScan_ed_scan.text.clear()
            }
        }

    override fun afterTextChanged(s: Editable?) {

    }
}
