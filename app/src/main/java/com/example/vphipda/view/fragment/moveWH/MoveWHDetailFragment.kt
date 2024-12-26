package com.example.vphipda.view.fragment.moveWH


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
import com.example.vphipda.network.InprodApiService
import com.example.vphipda.network.MoveApiService
import com.example.vphipda.network.SearchItemData
import com.example.vphipda.network.reqMvoutData
import kotlinx.android.synthetic.main.dialog_new_prod_ly.view.*
import kotlinx.android.synthetic.main.fragment_move_w_h_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MoveWHDetailFragment : Fragment(), TextWatcher {


    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    var stateIndex: Int = 0 // 0 : 반출 렉 스캔 || 1 : 품목 스캔 || 2: 수량 입력 || 3: 저장 렉 스캔
    lateinit var sel_main_tx: String
    lateinit var sel_code: String
    private var scanItem: String? = ""
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
        return inflater.inflate(R.layout.fragment_move_w_h_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stateIndex = 0

        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        sel_main_tx = arguments?.getString("sel_main_tx").toString()
        sel_code = arguments?.getString("sel_code_tx").toString()
        moveWHDetail_tx_top.text = sel_main_tx


        moveWHDetail_ed_scan.requestFocus()
        hideKeyboard(view)
        moveWHDetail_ed_scan.addTextChangedListener(this)

        // 수량 입력 
        moveWHDetail_tx_qt.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            println(actionId)
            println(EditorInfo.IME_ACTION_DONE)
            if (actionId== EditorInfo.IME_ACTION_DONE) {

                if (!moveWHDetail_tx_qt.text.toString().isBlank()) {

                    stateIndex = 3
                    moveWHDetail_ly_state.text = "저장 렉 스캔"
                    hideKeyboard(v)
                    moveWHDetail_tx_qt.clearFocus()
                    moveWHDetail_tx_qt.isEnabled = false
                    moveWHDetail_ed_scan.isEnabled = true
                    moveWHDetail_ed_scan.requestFocus()
                } else{
                    AlertMessage("", "수량을 입력해주세요.")
                    stateIndex = 2
                    moveWHDetail_ly_state.text = "수량 입력"
                    moveWHDetail_tx_qt.requestFocus()
                }
                handled = true
            }
            handled
        }

        //수량 포커스
        moveWHDetail_tx_qt.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                showKeyboard(v)
            }
        }


        //품목 모달
        moveWHDetail_ly_item.setOnLongClickListener {
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


        // 반출 렉 스캔으로
        moveWHDetail_ly_rack.setOnClickListener {
            moveWHDetail_tx_rack.text = ""
            moveWHDetail_tx_cdItem.text = ""
            moveWHDetail_tx_nmItem.text = ""
            moveWHDetail_tx_qt.text.clear()
            stateIndex = 0
            moveWHDetail_ly_state.text = "반출 렉 스캔"
            moveWHDetail_tx_qt.isEnabled = false
            moveWHDetail_ed_scan.isEnabled = true
            moveWHDetail_ed_scan.requestFocus()
            hideKeyboard(it)
        }

        // 품목 스캔으로
        moveWHDetail_ly_item.setOnClickListener {

            when(stateIndex) {

                0 -> {
                    AlertMessage("", "반출 렉 스캔 후 설정 가능합니다.")
                }
                else -> {
                    moveWHDetail_tx_cdItem.text = ""
                    moveWHDetail_tx_nmItem.text = ""
                    moveWHDetail_tx_qt.text.clear()
                    stateIndex = 1
                    moveWHDetail_ly_state.text = "품목 스캔"
                    moveWHDetail_tx_qt.isEnabled = false
                    moveWHDetail_ed_scan.isEnabled = true
                    moveWHDetail_ed_scan.requestFocus()
                    hideKeyboard(it)
                }
            }
        }


        // 입고 가상 창고 버튼
        moveWHDetail_bt_inprod.setOnClickListener {
            val scan_tx = sel_code + "000001"
            when(stateIndex) {

                0 -> {
                    moveWHDetail_tx_rack.text = scan_tx
                    stateIndex = 1
                    moveWHDetail_ly_state.text = "품목 스캔"
                }

                else -> {
                    AlertMessage("", "렉 스캔 상태에서 선택 가능 합니다.")
                }
            }
        }

        // 입고 가상 창고 목록 버튼
        moveWHDetail_bt_inprod_list.setOnClickListener {
            val cd_lc = sel_code + "000001"
            val bundle = Bundle()
            bundle.putString("cd_lc", cd_lc)
            bundle.putString("sel_main_tx", sel_main_tx)
            bundle.putString("sel_sub_tx", "입고 가상 물품 리스트")
            navController.navigate(
                R.id.action_moveWHDetailFragment_to_moveWHFragment,
                bundle
            )
        }



        // 홈 버튼
        moveWHDetail_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }
    }


    private fun reqMvout(cd_lc2:String) {

        val cd_lc = moveWHDetail_tx_rack.text.toString()
        val cd_item = moveWHDetail_tx_cdItem.text.toString()
        val qt_ld = moveWHDetail_tx_qt.text.toString()

        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MoveApiService::class.java)
        val callGet = api.reqMvout("req_mpmv",  cd_item, qt_ld, cd_lc, cd_lc2)

        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();

        callGet.enqueue(object : Callback<reqMvoutData> {
            override fun onResponse(
                call: Call<reqMvoutData>,
                response: Response<reqMvoutData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message

                when (state) {
                    1 -> {
                        progressDialog.dismiss();
                        moveWHDetail_tx_rack.text = ""
                        moveWHDetail_tx_cdItem.text = ""
                        moveWHDetail_tx_nmItem.text = ""
                        moveWHDetail_tx_qt.text.clear()
                        stateIndex = 0
                        moveWHDetail_ly_state.text = "반출 렉 스캔"
                        moveWHDetail_ed_scan.text.clear()
                        moveWHDetail_tx_qt.isEnabled = false
                        AlertMessage("이동 완료", "반출 렉 : ${cd_lc}\n이동 렉 : ${cd_lc2}\n 품목 : ${cd_item}\n 수량 : ${qt_ld}")
                    }
                    else -> {
                        progressDialog.dismiss();
                        moveWHDetail_ed_scan.text.clear()
                        moveWHDetail_ed_scan.requestFocus()
                        AlertMessage("", message.toString())
                    }
                }
            }
            override fun onFailure(call: Call<reqMvoutData>, t: Throwable) {
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
                        moveWHDetail_tx_cdItem.text = datalist?.get("CD_ITEM")
                        moveWHDetail_tx_nmItem.text = datalist?.get("STND_ITEM")
                        stateIndex = 2
                        moveWHDetail_ly_state.text = "수량 입력"
                        moveWHDetail_tx_qt.isEnabled = true
                        moveWHDetail_ed_scan.text.clear()
                        moveWHDetail_ed_scan.isEnabled = false
                        moveWHDetail_tx_qt.requestFocus()

                    }
                    else -> {

                        moveWHDetail_tx_rack.text = ""
                        moveWHDetail_tx_cdItem.text = ""
                        moveWHDetail_tx_nmItem.text = ""
                        moveWHDetail_tx_qt.text.clear()
                        stateIndex = 0
                        moveWHDetail_ly_state.text = "반출 렉 스캔"
                        moveWHDetail_ed_scan.text.clear()
                        moveWHDetail_tx_qt.isEnabled = false
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
            MoveWHDetailFragment().apply {
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
                    moveWHDetail_tx_rack.text = s.toString()
                    stateIndex = 1
                    moveWHDetail_ly_state.text = "품목 스캔"
                    moveWHDetail_ed_scan.text.clear()
                }
                1 -> {
                    search_item(s.toString())
                }

                3 -> {
                    reqMvout(s.toString())
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

}