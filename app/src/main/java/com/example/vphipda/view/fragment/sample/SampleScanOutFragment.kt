package com.example.vphipda.view.fragment.sample

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.adapter.ProdDetailListAdapter
import com.example.vphipda.model.ProdItem
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.CDVPLANT
import com.example.vphipda.model.appData.Companion.SampleType
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.dialog_new_prod_ly.view.*
import kotlinx.android.synthetic.main.fragment_move_w_h_detail.*
import kotlinx.android.synthetic.main.fragment_sample_scan_out.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SampleScanOutFragment : Fragment(), ProdDetailListAdapter.OnProdOneClickedListener,
    TextWatcher {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    var stateIndex: Int = 0 // 0 : 렉스캔 || 1 : 품목 스캔 || 2: 수량 입력
    var imm: InputMethodManager? = null
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sample_scan_out, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateIndex = 0
        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        //헤더 텍스트
        sampleScanOut_tx_main.text = appData.SampleText

        //홈버튼
        sampleScanOut_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }


        prodItemArray.clear()
        prodDetailListAdapter = ProdDetailListAdapter(prodItemArray, this)
        sampleScanOut_rv.adapter = prodDetailListAdapter
        prodDetailListAdapter.notifyDataSetChanged()
        setSampleDetailList()


        sampleScanOut_ed_scan.requestFocus()
        hideKeyboard(view)
        sampleScanOut_ed_scan.addTextChangedListener(this)

        // 품목 리셋
        sampleScanOut_ly_item.setOnClickListener {

            when (stateIndex) {

                0 -> {
                    AlertMessage("", "렉 스캔 이후 사용 가능합니다.")
                }

                1 -> {
                    hideKeyboard(view)
                    stateIndex = 1
                    sampleScanOut_tx_qt.text.clear()
                    sampleScanOut_tx_qt.clearFocus()
                    sampleScanOut_tx_qt.isEnabled = false
                    sampleScanOut_tx_cdItem.text = ""
                    sampleScanOut_tx_nmItem.text = ""
                    sampleScanOut_ly_state.text = "품목 스캔"
                    sampleScanOut_ed_scan.isEnabled = true
                    sampleScanOut_ed_scan.requestFocus()
                }

                2 -> {
                    hideKeyboard(view)
                    stateIndex = 1
                    sampleScanOut_tx_qt.text.clear()
                    sampleScanOut_tx_qt.clearFocus()
                    sampleScanOut_tx_qt.isEnabled = false
                    sampleScanOut_tx_cdItem.text = ""
                    sampleScanOut_tx_nmItem.text = ""
                    sampleScanOut_ly_state.text = "품목 스캔"
                    sampleScanOut_ed_scan.isEnabled = true
                    sampleScanOut_ed_scan.requestFocus()
                }
            }
        }

        //가상 창고
        sampleScanOut_bt_avRack.setOnClickListener {
            when (stateIndex) {

                0 -> {
                    sampleScanOut_tx_rack.text = CDVPLANT
                    stateIndex = 1
                    sampleScanOut_ly_state.text = "품목 스캔"
                }
                else -> {
                    AlertMessage("", "렉 스캔 상태에서 사용 가능합니다.")
                }

            }
        }

        //품목 모달
        sampleScanOut_ly_item.setOnLongClickListener {
            when(stateIndex) {

                1 -> {
                    val builder = AlertDialog.Builder(navController.context)
                    var v1 = layoutInflater.inflate(R.layout.dialog_new_prod_ly, null)
                    builder.setView(v1)
                        .setPositiveButton("확인",
                            DialogInterface.OnClickListener { dialog, id ->
                                var condition1 = v1.prod_et.text.isNullOrBlank()
                                var item_code:String = "" // 품목 코드
                                if (!condition1) {
                                    item_code = v1.prod_et.text.toString() // 품목 코드
                                    search_item(item_code)
                                } else {
                                    AlertMessage("", "품목 코드를 입력해 주세요.")
                                }

                                hideKeyboard(v1)

                            })
                        .setNegativeButton("취소",
                            DialogInterface.OnClickListener { dialog, id ->
                                hideKeyboard(v1)
                            })

                    // 다이얼로그를 띄워주기
                    builder.show()
                    v1.prod_et.requestFocus()

                }
                else -> {
                    AlertMessage("", "품목 스캔 상태에서 설정 가능합니다.")
                }
            }
            return@setOnLongClickListener(true)
        }


        //리셋 버튼
        sampleScanOut_ly_rack.setOnClickListener {
            stateIndex = 0
            hideKeyboard(view)
            sampleScanOut_ly_state.text = "렉 스캔"
            sampleScanOut_tx_qt.clearFocus()
            sampleScanOut_tx_qt.isEnabled = false
            sampleScanOut_tx_qt.text.clear()
            sampleScanOut_tx_cdItem.text = ""
            sampleScanOut_tx_nmItem.text = ""
            sampleScanOut_ed_scan.isEnabled = true
            sampleScanOut_tx_rack.text = ""
            sampleScanOut_ed_scan.requestFocus()

        }

        // 수량 입력 완료 버튼
        sampleScanOut_tx_qt.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == 5 || actionId == 6) {
                if (!sampleScanOut_tx_qt.text.toString().isBlank()) {
                    hideKeyboard(view)
                    var no_io = appData.SampleNoio
                    var cd_lc = sampleScanOut_tx_rack.text.toString()
                    var cd_item = sampleScanOut_tx_cdItem.text.toString()
                    var qt_lc = sampleScanOut_tx_qt.text.toString()

                    when(SampleType) {
                        "get_sample" -> {
                            ins_sample(no_io,  cd_item, qt_lc, cd_lc)
                            hideKeyboard(v)
                        }
                        "get_pcreturn" -> {
                            ins_pcreturn(no_io,  cd_item, qt_lc, cd_lc)
                            hideKeyboard(v)
                        }

                    }


                } else {
                    AlertMessage("", "수량을 입력해주세요.")
                    stateIndex = 2
                    sampleScanOut_tx_qt.requestFocus()
                }
                handled = true
            }
            handled
        }
        sampleScanOut_tx_qt.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showKeyboard(v)

            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setSampleDetailList() {

        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(SampleApiService::class.java)
        val callGet = api.get_sampleDetail("get_sample_detail", appData.SampleNoio)




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
                        prodItemArray.clear()
                        if (!datalist.isNullOrEmpty()) {
                            prodItemArray.apply {
                                for (i in datalist.indices) {
                                    add(
                                        ProdItem(
                                            datalist[i].CD_ITEM,
                                            datalist[i].QT_IO,
                                            datalist[i].QT_LC
                                        )
                                    )
                                }
                            }
                        }
                        prodDetailListAdapter.notifyDataSetChanged()
                    }

                    0 -> {
                        prodItemArray.clear()
                        prodDetailListAdapter.notifyDataSetChanged()
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<getReceivingDetailData>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })
    }


    private fun ins_sample(no_io:String, cd_item: String, qt:String, cd_lc:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(SampleApiService::class.java)
        val callGet = api.ins_sample("ins_sample", no_io, cd_item, qt, cd_lc)
//        품목스캔 후 수량 입력 들어갈때

        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();


        callGet.enqueue(object : Callback<SamplePostData> {
            override fun onResponse(
                call: Call<SamplePostData>,
                response: Response<SamplePostData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message

                when (state) {
                    1 -> {
                        progressDialog.dismiss();
                        stateIndex = 0
                        sampleScanOut_ly_state.text = "렉 스캔"
                        sampleScanOut_tx_qt.clearFocus()
                        sampleScanOut_tx_qt.isEnabled = false
                        sampleScanOut_tx_qt.text.clear()
                        sampleScanOut_tx_cdItem.text = ""
                        sampleScanOut_tx_nmItem.text = ""
                        sampleScanOut_ed_scan.isEnabled = true
                        sampleScanOut_tx_rack.text = ""
                        sampleScanOut_ed_scan.requestFocus()
                        AlertMessage("처리 완료", "지시 번호 : $no_io\n반출 렉 : $cd_lc\n품목 : $cd_item\n수량 : $qt")
                        setSampleDetailList()
                    }
                    0 -> {
                        progressDialog.dismiss();
                        AlertMessage("", message.toString())
                    }
                }

            }

            override fun onFailure(call: Call<SamplePostData>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", appData.AlertF)
            }
        })
    }


    private fun ins_pcreturn(no_io:String, cd_item: String, qt:String, cd_lc:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(SampleApiService::class.java)
        val callGet = api.ins_sample("ins_pcreturn", no_io, cd_item, qt, cd_lc)
//        품목스캔 후 수량 입력 들어갈때

        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();


        callGet.enqueue(object : Callback<SamplePostData> {
            override fun onResponse(
                call: Call<SamplePostData>,
                response: Response<SamplePostData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message

                when (state) {
                    1 -> {
                        progressDialog.dismiss();
                        stateIndex = 0
                        sampleScanOut_ly_state.text = "렉 스캔"
                        sampleScanOut_tx_qt.clearFocus()
                        sampleScanOut_tx_qt.isEnabled = false
                        sampleScanOut_tx_qt.text.clear()
                        sampleScanOut_tx_cdItem.text = ""
                        sampleScanOut_tx_nmItem.text = ""
                        sampleScanOut_ed_scan.isEnabled = true
                        sampleScanOut_tx_rack.text = ""
                        sampleScanOut_ed_scan.requestFocus()
                        AlertMessage("처리 완료", "지시 번호 : $no_io\n반출 렉 : $cd_lc\n품목 : $cd_item\n수량 : $qt")
                        setSampleDetailList()
                    }
                    0 -> {
                        progressDialog.dismiss();
                        AlertMessage("", message.toString())
                    }
                }

            }

            override fun onFailure(call: Call<SamplePostData>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", appData.AlertF)
            }
        })
    }


    private fun search_item(cd_item: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InprodApiService::class.java)
        val callGet = api.searchItem("search_item", cd_item)
//        품목스캔 후 수량 입력 들어갈때
        callGet.enqueue(object : Callback<SearchItemData> {
            override fun onResponse(
                call: Call<SearchItemData>,
                response: Response<SearchItemData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when (state) {
                    1 -> {
                        sampleScanOut_tx_cdItem.text = datalist?.get("CD_ITEM")
                        sampleScanOut_tx_nmItem.text = datalist?.get("STND_ITEM")
                        stateIndex = 2
                        sampleScanOut_ed_scan.isEnabled = false
                        sampleScanOut_ed_scan.text.clear()
                        sampleScanOut_ly_state.text = "수량 입력"
                        sampleScanOut_tx_qt.isEnabled = true
                        sampleScanOut_tx_qt.requestFocus()
                    }
                    0 -> {
                        sampleScanOut_ed_scan.text.clear()
                        AlertMessage("", message.toString())
                    }
                }

            }

            override fun onFailure(call: Call<SearchItemData>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })
    }


    fun hideKeyboard(view: View) {

        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(view: View) {
        imm?.showSoftInput(view, 0)
    }

    fun AlertMessage(titleM: String, bodyM: String) {
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
            SampleScanOutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onProdOneClick(item: ProdItem) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!s.isNullOrBlank()) {

            when (stateIndex) {
                0 -> {
                    sampleScanOut_tx_rack.text = s
                    stateIndex = 1
                    sampleScanOut_ly_state.text = "품목 스캔"
                    sampleScanOut_ed_scan.text.clear()
                }

                1 -> {
                    search_item(s.toString())
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }
}