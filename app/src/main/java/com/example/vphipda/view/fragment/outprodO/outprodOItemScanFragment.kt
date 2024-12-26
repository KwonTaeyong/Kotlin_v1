package com.example.vphipda.view.fragment.outprodO

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
import com.example.vphipda.adapter.OutProdListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.ScanCodeItem
import com.example.vphipda.model.appData.Companion.ScanCodeQt
import com.example.vphipda.model.appData.Companion.ScanCodeRack
import com.example.vphipda.model.appData.Companion.ScanCodeState
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.dialog_new_prod_ly.view.*
import kotlinx.android.synthetic.main.fragment_outprod_o_item_scan.*
import kotlinx.android.synthetic.main.fragment_outprod_o_pdetail_scan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodOItemScanFragment : Fragment(), OutProdListAdapter.OutProdListLongClickedListener, TextWatcher {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    var stateIndex: Int = 0 // 0 : 렉스캔  || 1 : 품목 스캔  || 2 : 수량 입력
    var imm : InputMethodManager? = null
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
        return inflater.inflate(R.layout.fragment_outprod_o_item_scan, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 네비 컨트롤러
        stateIndex = 0
        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?


        //공통 코드 초기화
        ScanCodeState = 0
        ScanCodeRack = ""
        ScanCodeItem = ""
        ScanCodeQt = ""

        // 지시서 리스트
        moveWHArray.clear()
        outProdListAdapter = OutProdListAdapter(moveWHArray, this)
        outprodOItemScan_rv.adapter = outProdListAdapter
        outProdListAdapter.notifyDataSetChanged()

        setPackingOrder()

        // 수주 번호 텍스트
        outprodOItemScan_tx_rack2.text = appData.PackingOrderCode

        outprodOItemScan_ed_scan.requestFocus()
        hideKeyboard(view)
        outprodOItemScan_ed_scan.addTextChangedListener(this)

        //홈 버튼
        outprodOItemScan_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        // 품목 리셋
        outprodOItemScan_ly_item.setOnClickListener {

            when (stateIndex) {

                0 -> {
                    AlertMessage("", "렉 스캔 이후 사용 가능합니다.")
                }

                1 -> {
                    hideKeyboard(view)
                    stateIndex = 1
                    outprodOItemScan_tx_qt.text.clear()
                    outprodOItemScan_tx_qt.clearFocus()
                    outprodOItemScan_tx_qt.isEnabled = false
                    outprodOItemScan_tx_cdItem.text = ""
                    outprodOItemScan_tx_nmItem.text = ""
                    outprodOItemScan_ly_state.text = "품목 스캔"
                    outprodOItemScan_ed_scan.isEnabled = true
                    outprodOItemScan_ed_scan.requestFocus()
                }

                2 -> {
                    hideKeyboard(view)
                    stateIndex = 1
                    outprodOItemScan_tx_qt.text.clear()
                    outprodOItemScan_tx_qt.clearFocus()
                    outprodOItemScan_tx_qt.isEnabled = false
                    outprodOItemScan_tx_cdItem.text = ""
                    outprodOItemScan_tx_nmItem.text = ""
                    outprodOItemScan_ly_state.text = "품목 스캔"
                    outprodOItemScan_ed_scan.isEnabled = true
                    outprodOItemScan_ed_scan.requestFocus()
                }
            }
        }


        //품목 모달
        outprodOItemScan_ly_item.setOnLongClickListener {
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

        //가상 창고
        outprodOItemScan_bt_avRack.setOnClickListener {
            when (stateIndex) {

                0 -> {
                    outprodOItemScan_tx_rack.text = "BV000001"
                    stateIndex = 1
                    outprodOItemScan_ly_state.text = "품목 스캔"
                }
                else -> {
                    AlertMessage("", "렉 스캔 상태에서 사용 가능합니다.")
                }

            }
        }


        //리셋 버튼
        outprodOItemScan_ly_rack.setOnClickListener {
            stateIndex = 0
            hideKeyboard(view)
            outprodOItemScan_ly_state.text = "렉 스캔"
            outprodOItemScan_tx_qt.clearFocus()
            outprodOItemScan_tx_qt.isEnabled = false
            outprodOItemScan_tx_qt.text.clear()
            outprodOItemScan_tx_cdItem.text = ""
            outprodOItemScan_tx_nmItem.text = ""
            outprodOItemScan_ed_scan.isEnabled = true
            outprodOItemScan_tx_rack.text = ""
            outprodOItemScan_ed_scan.requestFocus()

        }

        // 수량 입력 완료 버튼
        outprodOItemScan_tx_qt.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == 5 || actionId == 6) {
                if (!outprodOItemScan_tx_qt.text.toString().isBlank()) {
                    hideKeyboard(view)
                    var cd_lc = outprodOItemScan_tx_rack.text.toString()
                    var cd_item = outprodOItemScan_tx_cdItem.text.toString()
                    var qt_lc = outprodOItemScan_tx_qt.text.toString()

                    hideKeyboard(v)
//                    ins_pkitem(cd_item, qt_lc, cd_lc)
                    setScanCode(cd_lc, cd_item, qt_lc)



                } else {
                    AlertMessage("", "수량을 입력해주세요.")
                    stateIndex = 2
                    outprodOItemScan_tx_qt.requestFocus()
                }
                handled = true
            }
            handled
        }

        // 케이스 목록 이동 버튼
        outprodOItemScan_list_btn.setOnClickListener {
            ScanCodeState = 1
            navController.navigate(R.id.action_outprodOItemScanFragment_to_outprodOPmsListFragment)

        }



        //수량 포커스
        outprodOItemScan_tx_qt.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                showKeyboard(v)
            }
        }

        outprodOItemScan_ed_scan.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                hideKeyboard(v)
            }

        }


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

    fun showKeyboard(view: View) {
        imm?.showSoftInput(view, 0)
    }

    fun hideKeyboard(view: View) {
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setScanCode(rack:String, item:String, qt:String) {
        ScanCodeState = 0
        ScanCodeRack = rack
        ScanCodeItem = item
        ScanCodeQt = qt
        navController.navigate(R.id.action_outprodOItemScanFragment_to_outprodOPmsListFragment)
    }


    private fun setPackingOrder() {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.get_pkwo_so("get_pkwo_so", appData.PackingOrderCode)

        callGet.enqueue(object : Callback<GetPkwoSo> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<GetPkwoSo>,
                response: Response<GetPkwoSo>
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
                                    add(WolistDetail(datalist[i].CD_ITEM, datalist[i].QT_WO, datalist[i].QT_PK))
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

            override fun onFailure(call: Call<GetPkwoSo>, t: Throwable) {
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
                        outprodOItemScan_tx_cdItem.text = datalist?.get("CD_ITEM")
                        outprodOItemScan_tx_nmItem.text = datalist?.get("STND_ITEM")
                        stateIndex = 2
                        outprodOItemScan_tx_qt.isEnabled = true
                        outprodOItemScan_ed_scan.text.clear()
                        outprodOItemScan_ed_scan.isEnabled = false
                        outprodOItemScan_tx_qt.requestFocus()

                    }
                    else -> {

                        outprodOItemScan_ed_scan.text.clear()
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

        fun newInstance(param1: String, param2: String) =
            outprodOItemScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun outProdListLongClick(item: WolistDetail): Boolean {
        return true
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(!s.isNullOrBlank()) {
            when(stateIndex) {
                0 -> {
                    outprodOItemScan_tx_rack.text = s.toString()
                    stateIndex = 1
                    outprodOItemScan_ly_state.text = "품목 스캔"
                    outprodOItemScan_ed_scan.text.clear()
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