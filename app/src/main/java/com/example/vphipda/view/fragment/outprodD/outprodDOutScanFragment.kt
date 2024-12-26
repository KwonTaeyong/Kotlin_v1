package com.example.vphipda.view.fragment.outprodD

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
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.AlertF
import com.example.vphipda.model.appData.Companion.DataNoWo
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.dialog_new_prod_ly.view.*
import kotlinx.android.synthetic.main.fragment_outprod_d_out_scan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodDOutScanFragment : Fragment(), TextWatcher {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    var stateIndex: Int = 0 // 0 : 출고 렉 스캔 || 1 : 품목 스캔 || 2: 수량 입력 || 3: 완료 버튼
    var imm : InputMethodManager? = null

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
        return inflater.inflate(R.layout.fragment_outprod_d_out_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        stateIndex = 0
        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        //헤더
        outprodDOutScan_tx_main.text = DataNoWo

        outprodDOutScan_ed_scan.requestFocus()
        hideKeyboard(view)
        outprodDOutScan_ed_scan.addTextChangedListener(this)


        // 홈버튼
        outprodDOutScan_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        // 지시 목록
        outprodDOutScan_bt_back.setOnClickListener {
            navController.navigate(R.id.action_outprodDOutOrderList)
        }


        // 저장 현황
        outprodDOutScan_bt_list.setOnClickListener {
            navController.navigate(R.id.action_outprodDOutScanFragment_to_outprodDOutListFragment)
        }


        //품목 모달
        outprodDOutScan_ly_item.setOnLongClickListener {
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

        // 가상 창고 버튼
        outprodDOutScan_bt_avRack.setOnClickListener {
            when(stateIndex) {
                0 -> {
                    outprodDOutScan_tx_rack.text = "AV000001"
                    stateIndex = 1
                    outprodDOutScan_ly_state.text = "품목 스캔"
                    outprodDOutScan_ed_scan.text.clear()
                }
                else -> {
                    AlertMessage("", "렉 스캔 상태에서 가능 합니다.")
                }
            }
        }


        // 수량 입력
        outprodDOutScan_tx_qt.setOnEditorActionListener { v, actionId, event ->
            var handled = false

            if (actionId == EditorInfo.IME_ACTION_DONE) {

                if (!outprodDOutScan_tx_qt.text.toString().isBlank()) {

                    stateIndex = 3
                    outprodDOutScan_ly_state.text = "완료 버튼 입력"
                    hideKeyboard(v)
                    outprodDOutScan_tx_qt.clearFocus()
                    outprodDOutScan_tx_qt.isEnabled = false
                } else{
                    AlertMessage("", "수량을 입력해주세요.")
                    stateIndex = 2
                    outprodDOutScan_ly_state.text = "수량 입력"
                    outprodDOutScan_tx_qt.requestFocus()
                }
                handled = true
            }
            handled
        }

        //수량 포커스
        outprodDOutScan_tx_qt.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                showKeyboard(v)
            }
        }


        // 반출 렉 스캔으로
        outprodDOutScan_ly_rack.setOnClickListener {
            outprodDOutScan_tx_rack.text = ""
            outprodDOutScan_tx_cdItem.text = ""
            outprodDOutScan_tx_nmItem.text = ""
            outprodDOutScan_tx_qt.text.clear()
            stateIndex = 0
            outprodDOutScan_ly_state.text = "렉 스캔"
            outprodDOutScan_tx_qt.isEnabled = false
            outprodDOutScan_ed_scan.isEnabled = true
            outprodDOutScan_ed_scan.requestFocus()
            hideKeyboard(it)
        }

        // 품목 스캔으로
        outprodDOutScan_ly_item.setOnClickListener {
            when(stateIndex){
                0 -> {
                    AlertMessage("", "렉 스캔 후에 가능 합니다.")
                }
                else -> {
                    outprodDOutScan_tx_cdItem.text = ""
                    outprodDOutScan_tx_nmItem.text = ""
                    outprodDOutScan_tx_qt.text.clear()
                    stateIndex = 1
                    outprodDOutScan_ly_state.text = "품목 스캔"
                    outprodDOutScan_tx_qt.isEnabled = false
                    outprodDOutScan_ed_scan.isEnabled = true
                    outprodDOutScan_ed_scan.requestFocus()
                    hideKeyboard(it)
                }
            }
        }

        // 완료 버튼
        outprodDOutScan_bt_out.setOnClickListener {
            when(stateIndex) {
                3 -> {
                    var cd_item:String = outprodDOutScan_tx_cdItem.text.toString()
                    var cd_lc:String = outprodDOutScan_tx_rack.text.toString()
                    var qt:String = outprodDOutScan_tx_qt.text.toString()
                    outprodDOutScan_bt_out.isEnabled = false
                    ins_woout(DataNoWo, cd_item, cd_lc, qt)
                    hideKeyboard(view)
                }
                else -> {
                    AlertMessage("", "이전 작업을 완료 해주세요.")
                }

            }

        }

    }


    private fun ins_woout(no_wo: String, cd_item: String, cd_lc:String, qt:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.ins_woout("ins_woout", no_wo, cd_item, cd_lc, qt)


        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();

        callGet.enqueue(object : Callback<GetInsWoout> {
            override fun onResponse(
                call: Call<GetInsWoout>,
                response: Response<GetInsWoout>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                when (state) {
                    1 -> {
                        outprodDOutScan_tx_rack.text = ""
                        outprodDOutScan_tx_cdItem.text = ""
                        outprodDOutScan_tx_nmItem.text = ""
                        outprodDOutScan_tx_qt.text.clear()
                        stateIndex = 0
                        outprodDOutScan_ly_state.text = "렉 스캔"
                        outprodDOutScan_tx_qt.isEnabled = false
                        outprodDOutScan_ed_scan.isEnabled = true
                        outprodDOutScan_ed_scan.requestFocus()
                        progressDialog.dismiss();
                        outprodDOutScan_bt_out.isEnabled = true
                        AlertMessage("반출 성공", "반출 렉 : ${cd_lc}\n품목 : ${cd_item}\n수량 : ${qt}")
                    }
                    else -> {
                        progressDialog.dismiss();
                        outprodDOutScan_bt_out.isEnabled = true
                        AlertMessage("", message.toString())
                    }
                }
            }
            override fun onFailure(call: Call<GetInsWoout>, t: Throwable) {
                outprodDOutScan_bt_out.isEnabled = true
                progressDialog.dismiss();
                AlertMessage("", AlertF)
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
                        outprodDOutScan_tx_cdItem.text = datalist?.get("CD_ITEM")
                        outprodDOutScan_tx_nmItem.text = datalist?.get("STND_ITEM")
                        stateIndex = 2
                        outprodDOutScan_ly_state.text = "수량 입력"
                        outprodDOutScan_tx_qt.isEnabled = true
                        outprodDOutScan_ed_scan.text.clear()
                        outprodDOutScan_ed_scan.isEnabled = false
                        outprodDOutScan_tx_qt.requestFocus()

                    }
                    else -> {

                        outprodDOutScan_tx_cdItem.text = ""
                        outprodDOutScan_tx_nmItem.text = ""
                        outprodDOutScan_tx_qt.text.clear()
                        stateIndex = 1
                        outprodDOutScan_ly_state.text = "품목 스캔"
                        outprodDOutScan_tx_qt.isEnabled = false
                        outprodDOutScan_ed_scan.isEnabled = true
                        outprodDOutScan_ed_scan.text.clear()
                        outprodDOutScan_ed_scan.requestFocus()
                        AlertMessage("", message.toString())
                    }
                }
            }
            override fun onFailure(call: Call<SearchItemData>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodDOutScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun showKeyboard(view: View) {
        imm?.showSoftInput(view, 0)
    }

    fun hideKeyboard(view: View) {
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(!s.isNullOrBlank()) {
            when(stateIndex) {
                0 -> {
                    outprodDOutScan_tx_rack.text = s.toString()
                    stateIndex = 1
                    outprodDOutScan_ly_state.text = "품목 스캔"
                    outprodDOutScan_ed_scan.text.clear()
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