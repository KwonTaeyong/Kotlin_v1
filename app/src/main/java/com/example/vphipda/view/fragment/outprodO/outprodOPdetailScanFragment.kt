package com.example.vphipda.view.fragment.outprodO

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.print.PrintManager
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.adapter.PalletDetailListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.BaseURL
import com.example.vphipda.model.appData.Companion.CaseNum
import com.example.vphipda.model.appData.Companion.PackingOrderCode
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.dialog_end_packing_ly.view.*
import kotlinx.android.synthetic.main.dialog_new_prod_ly.view.*
import kotlinx.android.synthetic.main.fragment_outprod_o_pdetail_scan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodOPdetailScanFragment : Fragment(), TextWatcher, PalletDetailListAdapter.PalletDetailDataClickedListener, PalletDetailListAdapter.PalletDetailDataLongClickedListener {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var palletDetailListAdapter: PalletDetailListAdapter
    private var mWebView: WebView? = null
    var stateIndex: Int = 0 // 0 : 렉스캔  || 1 : 품목 스캔  || 2 : 수량 입력
    var imm : InputMethodManager? = null

    var palletdetailArray = ArrayList<PalletDetail>()

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
        return inflater.inflate(R.layout.fragment_outprod_o_pdetail_scan, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stateIndex = 0
        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // 대포장 케이스 번호 지정
        outprodOPdetailScan_tx_rack2.text = CaseNum

        // 패킹 리스트 출력
        outprodOPdetailScan_print_btn.setOnClickListener {
            doWebViewPrint(CaseNum)
        }

        outprodOPdetailScan_ed_scan.requestFocus()
        hideKeyboard(view)
        outprodOPdetailScan_ed_scan.addTextChangedListener(this)


        //홈 버튼
        outprodOPdetailScan_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }


        //리사이클 뷰
        palletdetailArray.clear()
        palletDetailListAdapter = PalletDetailListAdapter(palletdetailArray, this, this)
        outprodOPdetailScan_rv.adapter = palletDetailListAdapter
        palletDetailListAdapter.notifyDataSetChanged()
        setpalletdetailArray()



        // 품목 리셋
        outprodOPdetailScan_ly_item.setOnClickListener {

            when (stateIndex) {

                0 -> {
                    AlertMessage("", "렉 스캔 이후 사용 가능합니다.")
                }

                1 -> {
                    hideKeyboard(view)
                    stateIndex = 1
                    outprodOPdetailScan_tx_qt.text.clear()
                    outprodOPdetailScan_tx_qt.clearFocus()
                    outprodOPdetailScan_tx_qt.isEnabled = false
                    outprodOPdetailScan_tx_cdItem.text = ""
                    outprodOPdetailScan_tx_nmItem.text = ""
                    outprodOPdetailScan_ly_state.text = "품목 스캔"
                    outprodOPdetailScan_ed_scan.isEnabled = true
                    outprodOPdetailScan_ed_scan.requestFocus()
                }

                2 -> {
                    hideKeyboard(view)
                    stateIndex = 1
                    outprodOPdetailScan_tx_qt.text.clear()
                    outprodOPdetailScan_tx_qt.clearFocus()
                    outprodOPdetailScan_tx_qt.isEnabled = false
                    outprodOPdetailScan_tx_cdItem.text = ""
                    outprodOPdetailScan_tx_nmItem.text = ""
                    outprodOPdetailScan_ly_state.text = "품목 스캔"
                    outprodOPdetailScan_ed_scan.isEnabled = true
                    outprodOPdetailScan_ed_scan.requestFocus()
                }
            }
        }


        //품목 모달
        outprodOPdetailScan_ly_item.setOnLongClickListener {
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
        outprodOPdetailScan_bt_avRack.setOnClickListener {
            when (stateIndex) {

                0 -> {
                    outprodOPdetailScan_tx_rack.text = "BV000001"
                    stateIndex = 1
                    outprodOPdetailScan_ly_state.text = "품목 스캔"
                }
                else -> {
                    AlertMessage("", "렉 스캔 상태에서 사용 가능합니다.")
                }

            }
        }


        //리셋 버튼
        outprodOPdetailScan_ly_rack.setOnClickListener {
            stateIndex = 0
            hideKeyboard(view)
            outprodOPdetailScan_ly_state.text = "렉 스캔"
            outprodOPdetailScan_tx_qt.clearFocus()
            outprodOPdetailScan_tx_qt.isEnabled = false
            outprodOPdetailScan_tx_qt.text.clear()
            outprodOPdetailScan_tx_cdItem.text = ""
            outprodOPdetailScan_tx_nmItem.text = ""
            outprodOPdetailScan_ed_scan.isEnabled = true
            outprodOPdetailScan_tx_rack.text = ""
            outprodOPdetailScan_ed_scan.requestFocus()

        }

        // 수량 입력 완료 버튼
        outprodOPdetailScan_tx_qt.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == 5 || actionId == 6) {
                if (!outprodOPdetailScan_tx_qt.text.toString().isBlank()) {
                    hideKeyboard(view)
                    var cd_lc = outprodOPdetailScan_tx_rack.text.toString()
                    var cd_item = outprodOPdetailScan_tx_cdItem.text.toString()
                    var qt_lc = outprodOPdetailScan_tx_qt.text.toString()

                    hideKeyboard(v)
                    ins_pkitem(cd_item, qt_lc, cd_lc)



                } else {
                    AlertMessage("", "수량을 입력해주세요.")
                    stateIndex = 2
                    outprodOPdetailScan_tx_qt.requestFocus()
                }
                handled = true
            }
            handled
        }



        //수량 포커스
        outprodOPdetailScan_tx_qt.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                showKeyboard(v)
            }
        }

        outprodOPdetailScan_ed_scan.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                hideKeyboard(v)
            }

        }



        //완포장 지시
        outprodOPdetailScan_bt_in.setOnClickListener {
            val builder = AlertDialog.Builder(navController.context)
            var v1 = layoutInflater.inflate(R.layout.dialog_end_packing_ly, null)
            builder.setView(v1)
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        var condition1 = v1.end_packing_et_w.text.isNullOrBlank()
                        var condition2 = v1.end_packing_et_z.text.isNullOrBlank()
                        var condition3 = v1.end_packing_et_x.text.isNullOrBlank()
                        var condition4 = v1.end_packing_et_y.text.isNullOrBlank()

                        var gw:String = "0" // 실측 중량
                        var Lz :String = "0" // 높이
                        var Lx:String = "0" // 가로
                        var Ly:String = "0" // 세로


                        if (!condition1) {
                            gw = v1.end_packing_et_w.text.toString() // 실측 중량
                        }

                        if (!condition2) {
                            Lz = v1.end_packing_et_z.text.toString() // 높이
                        }

                        if (!condition3) {
                            Lx = v1.end_packing_et_x.text.toString() // 가로
                        }

                        if (!condition4) {
                            Ly = v1.end_packing_et_y.text.toString() // 세로
                        }



                        hideKeyboard(v1)
                        cls_case(CaseNum, gw, Lx, Ly, Lz)

                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                        hideKeyboard(v1)
                    })

            // 다이얼로그를 띄워주기
            builder.show()
            v1.end_packing_et_w.requestFocus()
        }

        // 스캔 으로
        outprodOPdetailScan_bt_scan.setOnClickListener {

            navController.navigate(R.id.action_outprodOPdetailScanFragment_to_outprodOPackingOrderFragment)
        }

    }

    private fun cls_case(case:String, gw:String, x:String, y:String, z:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.cls_case("cls_case", case, gw, x, y, z)

        callGet.enqueue(object : Callback<GetClsCase> {
            override fun onResponse(
                call: Call<GetClsCase>,
                response: Response<GetClsCase>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message

                when(state) {
                    1 -> {
                        navController.navigate(R.id.action_outprodOPdetailListFragment)
                        AlertMessage("완포장 처리 완료", "Case : ${case}\n실측 중량 : ${gw}\n높이 : ${z}\n가로 : ${x} 세로 : ${y}")
                    }
                    0 -> {
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetClsCase>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setpalletdetailArray() {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.getPalletDetail("get_pallet_detail", CaseNum)

        callGet.enqueue(object : Callback<GetPalletDetail> {
            override fun onResponse(
                call: Call<GetPalletDetail>,
                response: Response<GetPalletDetail>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when {
                    !datalist.isNullOrEmpty() -> {
                        palletdetailArray.clear()
                        palletdetailArray.apply {
                            for(i in datalist.indices){
                                add(
                                    PalletDetail(datalist[i].CD_ITEM,
                                                datalist[i].QT,
                                    )
                                )
                            }
                        }
                        palletDetailListAdapter.notifyDataSetChanged()
                    }
                    else -> {
                        palletdetailArray.clear()
                        palletDetailListAdapter.notifyDataSetChanged()
                        Toast.makeText(navController.context, "저장된 품목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<GetPalletDetail>, t: Throwable) {
                Toast.makeText(navController.context, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun ins_pkitem(cd_item: String, qt:String, cd_lc:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.ins_pkitem("ins_pkitem", PackingOrderCode, cd_item, CaseNum, qt, cd_lc)


        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();

        callGet.enqueue(object : Callback<GetInsPkitem> {
            override fun onResponse(
                call: Call<GetInsPkitem>,
                response: Response<GetInsPkitem>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                when (state) {
                    1 -> {


                        stateIndex = 0
                        progressDialog.dismiss();
                        outprodOPdetailScan_tb_lay.isVisible = true
                        outprodOPdetailScan_ly_state.text = "렉 스캔"
                        outprodOPdetailScan_tx_qt.clearFocus()
                        outprodOPdetailScan_tx_qt.isEnabled = false
                        outprodOPdetailScan_tx_qt.text.clear()
                        outprodOPdetailScan_tx_cdItem.text = ""
                        outprodOPdetailScan_tx_nmItem.text = ""
                        outprodOPdetailScan_ed_scan.isEnabled = true
                        outprodOPdetailScan_tx_rack.text = ""
                        outprodOPdetailScan_ed_scan.requestFocus()

                        setpalletdetailArray()
                        AlertMessage("입력 완료", "반출 렉 : ${cd_lc}\n품목 : ${cd_item}\n수량 : ${qt}")
                    }
                    else -> {
                        progressDialog.dismiss();
                        outprodOPdetailScan_tb_lay.isVisible = false
                        AlertMessage("", message.toString())
                    }
                }
            }
            override fun onFailure(call: Call<GetInsPkitem>, t: Throwable) {
                progressDialog.dismiss();
                outprodOPdetailScan_tb_lay.isVisible = false
                AlertMessage("", appData.AlertF)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun delPk(cd_item:String, case:String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.del_pk("del_pk", cd_item, case)

        callGet.enqueue(object : Callback<GetDelPkitem> {
            override fun onResponse(
                call: Call<GetDelPkitem>,
                response: Response<GetDelPkitem>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message

                when(state) {
                    1 -> {
                        setpalletdetailArray()
                        AlertMessage("삭제 성공", "품목코드: ${cd_item}")
                    }
                    else -> {
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetDelPkitem>, t: Throwable) {
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
                        outprodOPdetailScan_tx_cdItem.text = datalist?.get("CD_ITEM")
                        outprodOPdetailScan_tx_nmItem.text = datalist?.get("STND_ITEM")
                        stateIndex = 2
                        outprodOPdetailScan_tx_qt.isEnabled = true
                        outprodOPdetailScan_ed_scan.text.clear()
                        outprodOPdetailScan_ed_scan.isEnabled = false
                        outprodOPdetailScan_tx_qt.requestFocus()

                    }
                    else -> {

                        outprodOPdetailScan_ed_scan.text.clear()
                        AlertMessage("", message.toString())
                    }
                }
            }
            override fun onFailure(call: Call<SearchItemData>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })
    }


    private fun doWebViewPrint(CaseNum:String) {
        // Create a WebView object specifically for printing
        val webView = activity?.let { WebView(it) }
        webView?.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = false

            override fun onPageFinished(view: WebView, url: String) {
                createWebPrintJob(view)
                mWebView = null
            }
        }

        webView?.loadUrl("${BaseURL}AppPackingList/?caseNum=${CaseNum}")
        mWebView = webView
    }

    private fun createWebPrintJob(webView: WebView) {

        // Get a PrintManager instance
        (activity?.getSystemService(Context.PRINT_SERVICE) as? PrintManager)?.let { printManager ->

            val jobName = "${getString(R.string.app_name)} Document"

            // Get a print adapter instance
            val printAdapter = webView.createPrintDocumentAdapter(jobName)

            // Create a print job with name and adapter instance
            printManager.print(
                jobName,
                printAdapter,
                null
            ).also { printJob ->

                // Save the job object for later status checking

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

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodOPdetailScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun showKeyboard(view: View) {
        outprodOPdetailScan_tb_lay.isVisible = false
        imm?.showSoftInput(view, 0)
    }

    fun hideKeyboard(view: View) {
        outprodOPdetailScan_tb_lay.isVisible = true
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(!s.isNullOrBlank()) {
            when(stateIndex) {
                0 -> {
                    outprodOPdetailScan_tx_rack.text = s.toString()
                    stateIndex = 1
                    outprodOPdetailScan_ly_state.text = "품목 스캔"
                    outprodOPdetailScan_ed_scan.text.clear()
                }

                1 -> {
                    search_item(s.toString())
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun ontemplcClick(item: PalletDetail) {

    }

    override fun onPalletLongClick(item: PalletDetail): Boolean {
        val builder = AlertDialog.Builder(navController.context)
        builder.setTitle("삭제 하시겠습니까?")
            .setMessage("품목코드: ${item.CD_ITEM}\n수량: ${item.QT}\n")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, id ->
                    delPk(item.CD_ITEM, CaseNum)

                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, id ->

                })
        // 다이얼로그를 띄워주기
        builder.show()

        return true
    }
}