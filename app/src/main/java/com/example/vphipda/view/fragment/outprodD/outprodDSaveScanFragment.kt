package com.example.vphipda.view.fragment.outprodD

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
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.adapter.TemplcListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.AlertF
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.dialog_new_prod_ly.view.*
import kotlinx.android.synthetic.main.fragment_outprod_d_save_scan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodDSaveScanFragment : Fragment(), TemplcListAdapter.OnTemplcDataClickedListener,
    TextWatcher {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var templcListAdapter: TemplcListAdapter
    var stateIndex: Int = 0 // 0 : 렉스캔 || 1 : 품목 스캔 || 2: 수량 입력 || 3: 버튼 입력
    var imm : InputMethodManager? = null

    var outProdScanArray = ArrayList<TemplcData>()
    var outProdScanArray2 = ArrayList<TemplcData>()

    private var cd_lc : String? = ""
    private var cd_item : String? = ""
    private var qt_lc : String? = ""
    private var no_rcv : String? = ""


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
        return inflater.inflate(R.layout.fragment_outprod_d_save_scan, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateIndex = 0
        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?



        //리사이클 뷰
        outProdScanArray.clear()
        templcListAdapter = TemplcListAdapter(outProdScanArray, this)
        outprodDSaveScan_rv.adapter = templcListAdapter
        templcListAdapter.notifyDataSetChanged()






        outprodDSaveScan_ed_scan.requestFocus()
        hideKeyboard(view)
        outprodDSaveScan_ed_scan.addTextChangedListener(this)


        outprodDSaveScan_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }


        // 품목 리셋
        outprodDSaveScan_ly_item.setOnClickListener {

            when(stateIndex) {

                0 -> {

                }

                else -> {
                    hideKeyboard(view)
                    stateIndex = 1
                    outprodDSaveScan_tx_qt.clearFocus()
                    outprodDSaveScan_tx_qt.text.clear()
                    outprodDSaveScan_tx_qt.isEnabled = false
                    outprodDSaveScan_tx_cdItem.text = ""
                    outprodDSaveScan_tx_nmItem.text = ""
                    outprodDSaveScan_ed_scan.isEnabled = true
                    outprodDSaveScan_ed_scan.requestFocus()
                }
            }
        }


        //품목 모달
        outprodDSaveScan_ly_item.setOnLongClickListener {
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
        outprodDSaveScan_reset_btn.setOnClickListener {
            stateIndex = 0
            hideKeyboard(view)
            outprodDSaveScan_tx_qt.clearFocus()
            outprodDSaveScan_tx_qt.isEnabled = false
            outprodDSaveScan_tx_qt.text.clear()
            outprodDSaveScan_tx_cdItem.text = ""
            outprodDSaveScan_tx_nmItem.text = ""
            outprodDSaveScan_ed_scan.isEnabled = true
            outprodDSaveScan_tx_rack.text = ""
            outprodDSaveScan_ed_scan.requestFocus()

            outProdScanArray.clear()
            templcListAdapter.notifyDataSetChanged()
        }

        //반입 버튼
        outprodDSaveScan_bt_in.setOnClickListener {

            when(stateIndex){
                3 -> {
                    stateIndex = 1

                    cd_lc = outprodDSaveScan_tx_rack.text.toString()
                    cd_item = outprodDSaveScan_tx_cdItem.text.toString()
                    qt_lc = outprodDSaveScan_tx_qt.text.toString()
                    postInsTempqtlc(cd_lc!!, cd_item!!, qt_lc!!)
                }

                else -> {
                    AlertMessage("", "수량 입력까지 완료 후 시도 하세요.")
                }

            }

        }

        //반출 버튼
        outprodDSaveScan_bt_out.setOnClickListener {
            when(stateIndex) {
                3 -> {

                    stateIndex = 1
                    cd_lc = outprodDSaveScan_tx_rack.text.toString()
                    cd_item = outprodDSaveScan_tx_cdItem.text.toString()
                    qt_lc = outprodDSaveScan_tx_qt.text.toString()
                    postDelTempqtlc(cd_lc!!, cd_item!!, qt_lc!!)

                }

                else -> {
                    AlertMessage("", "수량 입력까지 완료 후 시도 하세요.")
                }
            }

        }

        // 수량 입력 완료 버튼
        outprodDSaveScan_tx_qt.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == 5 || actionId == 6) {
                if (!outprodDSaveScan_tx_qt.text.toString().isBlank()) {
                    hideKeyboard(view)
                    stateIndex = 3
                    outprodDSaveScan_tx_qt.clearFocus()
                    outprodDSaveScan_tx_qt.isEnabled = false


                } else {
                    AlertMessage("", "수량을 입력해주세요.")
                    stateIndex = 2
                    outprodDSaveScan_tx_qt.requestFocus()
                }
                handled = true
            }
            handled
        }
        outprodDSaveScan_tx_qt.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                showKeyboard(v)
            }
        }
        
    }


    fun hideKeyboard(view: View) {
        outprodDSaveScan_tb_lay.isVisible = true
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(view: View) {
        outprodDSaveScan_tb_lay.isVisible = false
        imm?.showSoftInput(view, 0)
    }
    
    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodDSaveScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun postInsTempqtlc(cd_lc:String, cd_item: String, qt_lc:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.ins_tempqtlc("ins_tempqtlc",cd_item, qt_lc, cd_lc)

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();


        callGet.enqueue(object : Callback<GetInsTempqtlc> {
            override fun onResponse(
                call: Call<GetInsTempqtlc>,
                response: Response<GetInsTempqtlc>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                when (state) {
                    1 -> {
                        stateIndex = 1
                        getTemplcS(cd_lc)
                        outprodDSaveScan_tx_qt.clearFocus()
                        outprodDSaveScan_tx_qt.isEnabled = false
                        outprodDSaveScan_tx_qt.text.clear()
                        outprodDSaveScan_tx_cdItem.text = ""
                        outprodDSaveScan_tx_nmItem.text = ""
                        outprodDSaveScan_ed_scan.isEnabled = true
                        outprodDSaveScan_ed_scan.requestFocus()
                        progressDialog.dismiss();
                        AlertMessage("반입 성공", "반입 렉 : ${cd_lc}\n품목 : ${cd_item}\n 수량 : ${qt_lc}")

                    }
                    0 -> {
                        stateIndex = 2
                        progressDialog.dismiss();
                        AlertMessage("", message.toString())
                    }
                }

            }

            override fun onFailure(call: Call<GetInsTempqtlc>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", AlertF)
            }
        })
    }


    private fun postDelTempqtlc(cd_lc:String, cd_item: String, qt_lc:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.ins_tempqtlc("del_tempqtlc",cd_item, qt_lc, cd_lc)

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();

        callGet.enqueue(object : Callback<GetInsTempqtlc> {
            override fun onResponse(
                call: Call<GetInsTempqtlc>,
                response: Response<GetInsTempqtlc>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                when (state) {
                    1 -> {
                        stateIndex = 1
                        getTemplcS(cd_lc)
                        outprodDSaveScan_tx_qt.clearFocus()
                        outprodDSaveScan_tx_qt.isEnabled = false
                        outprodDSaveScan_tx_qt.text.clear()
                        outprodDSaveScan_tx_cdItem.text = ""
                        outprodDSaveScan_tx_nmItem.text = ""
                        outprodDSaveScan_ed_scan.isEnabled = true
                        outprodDSaveScan_ed_scan.requestFocus()
                        progressDialog.dismiss();
                        AlertMessage("반출 성공", "반출 렉 : ${cd_lc}\n품목 : ${cd_item}\n 수량 : ${qt_lc}")
                    }
                    0 -> {
                        progressDialog.dismiss();
                        stateIndex = 2
                        AlertMessage("", message.toString())
                    }
                }

            }

            override fun onFailure(call: Call<GetInsTempqtlc>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", AlertF)
            }
        })
    }


    private fun getTemplcS(cd_lc: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.getTemplc("get_templc", cd_lc)
        callGet.enqueue(object : Callback<getTemplcData> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<getTemplcData>,
                response: Response<getTemplcData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when (state) {
                    1 -> {
                        when {
                            !datalist.isNullOrEmpty() -> {
                                outProdScanArray.clear()
                                outProdScanArray.apply {
                                    for(i in datalist.indices){
                                        add(
                                            TemplcData(
                                                datalist[i].CD_ITEM,
                                                datalist[i].QT_LC,
                                            )
                                        )
                                    }
                                }
                                templcListAdapter.notifyDataSetChanged()
                            }
                            else -> {
                                outProdScanArray.clear()
                                templcListAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    0 -> {
                        when(stateIndex){
                            1 -> {
                                stateIndex = 0
                                outprodDSaveScan_tx_rack.text = ""
                                AlertMessage("", message.toString())
                            }
                            else -> {
                                AlertMessage("", message.toString())
                            }
                        }
                    }
                }

            }

            override fun onFailure(call: Call<getTemplcData>, t: Throwable) {
                stateIndex = 0
                outprodDSaveScan_tx_rack.text = ""
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
                        outprodDSaveScan_tx_cdItem.text = datalist?.get("CD_ITEM")
                        outprodDSaveScan_tx_nmItem.text = datalist?.get("STND_ITEM")
                        stateIndex = 2
                        outprodDSaveScan_ed_scan.isEnabled = false
                        outprodDSaveScan_ed_scan.text.clear()
                        outprodDSaveScan_tx_qt.isEnabled = true
                        outprodDSaveScan_tx_qt.requestFocus()
                    }
                    0 -> {
                        outprodDSaveScan_ed_scan.text.clear()
                        AlertMessage("", message.toString())
                    }
                }

            }

            override fun onFailure(call: Call<SearchItemData>, t: Throwable) {
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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!s.isNullOrBlank()) {
            when (stateIndex) {
                0 -> {
                    outprodDSaveScan_tx_rack.text = s
                    stateIndex = 1
                    getTemplcS(s.toString())
                    outprodDSaveScan_ed_scan.text.clear()
                }

                1 -> {
                    search_item(s.toString())
                }

                2 -> {

                }

            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }



    override fun ontemplcClick(item: TemplcData): Boolean {
        return true
    }
}