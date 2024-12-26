package com.example.vphipda.view.fragment.move


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
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.AlertF
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.dialog_new_prod_ly.view.*
import kotlinx.android.synthetic.main.fragment_move_prod_scan.*
import kotlinx.android.synthetic.main.fragment_move_prod_scan.moveWHDetail_bt_list
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MoveProdScanFragment : Fragment(), TextWatcher {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var no_io: String
    lateinit var main_tx: String
    lateinit var state: String
    private var stateIndex: Int = 0 // 0 :반출 렉 스캔 || 1 : 품목 스캔 || 2: 수량 입력 || 3: 저장 렉 스캔
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
        return inflater.inflate(R.layout.fragment_move_prod_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateIndex = 0
        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        no_io = arguments?.getString("no_io").toString()
        main_tx = arguments?.getString("main_tx").toString()
        state = arguments?.getString("state").toString()
        moveProdScan_tx_main.text = main_tx
        moveProdScan_ed_scan.requestFocus()
        hideKeyboard(view)


        //스캔
        moveProdScan_ed_scan.addTextChangedListener(this)


        //홈 버튼
        moveProdScan_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        //목록으로 돌아가기
        moveWHDetail_bt_list.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("no_io", no_io)
            navController.navigate(R.id.action_moveProdDetail, bundle)
        }

        //품목 모달
        moveProdScan_ly_item.setOnLongClickListener {
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





        // 수량 입력
        moveProdScan_tx_qt.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId== EditorInfo.IME_ACTION_DONE) {

                if (!moveProdScan_tx_qt.text.toString().isBlank()) {
                    stateIndex = 3
                    moveProdScan_ly_state.text = "저장 렉 스캔"
                    hideKeyboard(v)
                    moveProdScan_tx_qt.clearFocus()
                    moveProdScan_ed_scan.isEnabled = true
                    moveProdScan_ed_scan.requestFocus()


                } else{
                    AlertMessage("", "수량을 입력해주세요.")
                    stateIndex = 2
                    moveProdScan_ly_state.text = "수량 입력"
                    moveProdScan_tx_qt.requestFocus()
                }
                handled = true
            }
            handled
        }

        moveProdScan_tx_qt.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                showKeyboard(v)
            }
        }

        //반출 렉 스캔으로
        moveProdScan_ly_rack.setOnClickListener {
            moveProdScan_tx_rack.text = ""
            moveProdScan_tx_cdItem.text = ""
            moveProdScan_tx_nmItem.text = ""
            moveProdScan_tx_qt.text.clear()
            stateIndex = 0
            moveProdScan_ly_state.text = "반출 렉 스캔"
            moveProdScan_tx_qt.isEnabled = false
            moveProdScan_ed_scan.isEnabled = true
            moveProdScan_ed_scan.requestFocus()
            hideKeyboard(it)
        }

        // 품목 스캔으로
        moveProdScan_ly_item.setOnClickListener {
            when(stateIndex) {
                0 -> {
                    AlertMessage("", "반출 렉 지정 후 설정 가능 합니다.")
                }
                else -> {
                    moveProdScan_tx_cdItem.text = ""
                    moveProdScan_tx_nmItem.text = ""
                    moveProdScan_tx_qt.text.clear()
                    stateIndex = 1
                    moveProdScan_ly_state.text = "품목 스캔"
                    moveProdScan_tx_qt.isEnabled = false
                    moveProdScan_ed_scan.isEnabled = true
                    moveProdScan_ed_scan.requestFocus()
                    hideKeyboard(it)
                }

            }

        }


        // 가상 창고 버튼
        moveProdScan_bt_move_A.setOnClickListener {
            val av_tx:String = "AV000001"
            when(stateIndex) {

                0 -> {
                    moveProdScan_tx_rack.text = av_tx
                    stateIndex = 1
                    moveProdScan_ly_state.text = "품목 스캔"
                }

                3 -> {
                    reqMp(av_tx)
                }

                else -> {
                    AlertMessage("", "렉 스캔 상태에서 선택 가능 합니다.")
                }
            }
        }

        // 가상 창고 버튼
        moveProdScan_bt_move_B.setOnClickListener {
            val bv_tx:String = "BV000001"
            when(stateIndex) {

                0 -> {
                    moveProdScan_tx_rack.text = bv_tx
                    stateIndex = 1
                    moveProdScan_ly_state.text = "품목 스캔"
                }

                3 -> {
                    reqMp(bv_tx)
                }

                else -> {
                    AlertMessage("", "렉 스캔 상태에서 선택 가능 합니다.")
            }
            }
        }


    }


    private fun reqMp(cd_lc2:String) {
        val cd_lc = moveProdScan_tx_rack.text.toString()
        val cd_item = moveProdScan_tx_cdItem.text.toString()
        val qt_ld = moveProdScan_tx_qt.text.toString()
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MoveApiService::class.java)
        val callGet = api.reqMp("req_mpio", no_io, cd_item, qt_ld, cd_lc, cd_lc2 )


        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();

        callGet.enqueue(object : Callback<reqMpData> {
            override fun onResponse(
                call: Call<reqMpData>,
                response: Response<reqMpData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message

                when (state) {
                    1 -> {
                        progressDialog.dismiss();
                        AlertMessage("저장 완료", "품목 : ${cd_item}\n 수량 : ${qt_ld}")
                        val bundle = Bundle()
                        bundle.putString("no_io", no_io)
                        navController.navigate(R.id.action_moveProdDetail, bundle)
                    }
                    else -> {
                        progressDialog.dismiss();
                        moveProdScan_ed_scan.text.clear()
                        moveProdScan_ed_scan.requestFocus()
                        AlertMessage("", message.toString())

                    }
                }
            }
            override fun onFailure(call: Call<reqMpData>, t: Throwable) {
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
                        moveProdScan_tx_cdItem.text = datalist?.get("CD_ITEM")
                        moveProdScan_tx_nmItem.text = datalist?.get("STND_ITEM")
                        stateIndex = 2
                        moveProdScan_ly_state.text = "수량 입력"
                        moveProdScan_tx_qt.isEnabled = true
                        moveProdScan_ed_scan.text.clear()
                        moveProdScan_ed_scan.isEnabled = false
                        moveProdScan_tx_qt.requestFocus()
                    }
                    else -> {
                        moveProdScan_tx_rack.text = ""
                        moveProdScan_tx_cdItem.text = ""
                        moveProdScan_tx_nmItem.text = ""
                        moveProdScan_tx_qt.text.clear()
                        stateIndex = 0
                        moveProdScan_ly_state.text = "반출 렉 스캔"
                        moveProdScan_ed_scan.text.clear()
                        moveProdScan_tx_qt.isEnabled = false
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
            MoveProdScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun hideKeyboard(view: View) {
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun showKeyboard(view: View) {
        imm?.showSoftInput(view, 0)
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
                    moveProdScan_tx_rack.text = s.toString()
                    stateIndex = 1
                    moveProdScan_ly_state.text = "품목 스캔"
                    moveProdScan_ed_scan.text.clear()
                }
                1 -> {
                    search_item(s.toString())
                }

                3 -> {
                    reqMp(s.toString())
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }
}