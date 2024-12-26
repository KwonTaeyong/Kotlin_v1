package com.example.vphipda.view.fragment.inprod


import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.ActivF
import com.example.vphipda.model.appData.Companion.AlertF
import com.example.vphipda.network.InprodApiService
import com.example.vphipda.network.LoadingData
import com.example.vphipda.network.SearchItemData
import kotlinx.android.synthetic.main.dialog_new_prod_ly.view.*
import kotlinx.android.synthetic.main.fragment_inprod_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class InprodDetailFragment : Fragment(), TextWatcher {

    private var param1: String? = null
    private var param2: String? = null

    var stateIndex: Int = 0 // 0 : 품목 스캔 || 1 : 수량 입력 || 2: 렉 스캔
    lateinit var navController: NavController
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
        return inflater.inflate(com.example.vphipda.R.layout.fragment_inprod_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ActivF = "inpord"
        stateIndex = 0
        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        prodDetail_tx_top.text = arguments?.getString("NO_ID")



        prodDetail_ed_scan.requestFocus()
        hideKeyboard(view)

        prodDetail_ed_scan.addTextChangedListener(this)


        // 수량 입력 완료 버튼
        prodDetail_tx_qt.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            println(actionId)
            println(EditorInfo.IME_ACTION_DONE)
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                if (!prodDetail_tx_qt.text.toString().isBlank()) {
                    stateIndex = 2
                    prodDetail_ly_state.text = "렉 스캔"
                    hideKeyboard(v)
                    prodDetail_tx_qt.clearFocus()
                    prodDetail_tx_qt.isEnabled  = false
                    prodDetail_ed_scan.isEnabled = true
                    prodDetail_ed_scan.requestFocus()
                } else {
                    AlertMessage("", "수량을 입력해주세요.")
                    stateIndex = 1
                    prodDetail_ly_state.text = "수량 입력"
                    prodDetail_tx_qt.requestFocus()
                }
                handled = true
            }
            handled
        }


        // 품목 모달
        prodDetail_ly_item.setOnLongClickListener {

            when(stateIndex) {
                0 -> {
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
                    AlertMessage("", "품목 스캔 상태에서 설정 가능 합니다..")
                }
            }
            return@setOnLongClickListener(true)
        }


        //수량 포커스

        prodDetail_tx_qt.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                showKeyboard(v)
            }
        }


        // 품목 재설정
        prodDetail_ly_item.setOnClickListener {
            stateIndex = 0
            prodDetail_ly_state.text = "품목 스캔"
            prodDetail_tx_cdItem.text = ""
            prodDetail_tx_nmItem.text = ""
            prodDetail_tx_qt.text.clear()
            prodDetail_tx_qt.isEnabled = false
            prodDetail_ed_scan.isEnabled = true
            prodDetail_ed_scan.requestFocus()
            hideKeyboard(it)
        }

        // 입고 목록
        prodDetail_bt_back.setOnClickListener {
            stateIndex = 100
            navController.navigate(com.example.vphipda.R.id.action_inprod)
        }
        // 저장 현황
        prodDetail_bt_list.setOnClickListener {
            stateIndex = 100
            val bundle = Bundle()
            bundle.putString("no_io", prodDetail_tx_top.text.toString())
            navController.navigate(
                com.example.vphipda.R.id.action_inprodDetailFragment_to_inpordDetailListFragment,
                bundle
            )
        }

        // 홈버튼
        home_btn.setOnClickListener {
            navController.navigate(com.example.vphipda.R.id.action_home)
        }

        // 가상창고 버튼
        prodDetail_bt_move.setOnClickListener {
            val vr_tx = "VV"

            when (stateIndex) {
                2 -> {
                    loading(vr_tx)
                }
                else -> {
                    AlertMessage("", "렉 스캔 상태에서 사용 가능합니다.")
                }
            }
        }

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
                        prodDetail_tx_cdItem.text = datalist?.get("CD_ITEM")
                        prodDetail_tx_nmItem.text = datalist?.get("STND_ITEM")
                        stateIndex = 1
                        prodDetail_ly_state.text = "수량 입력"
                        prodDetail_ed_scan.isEnabled = false
                        prodDetail_ed_scan.text.clear()
                        prodDetail_tx_qt.isEnabled = true
                        prodDetail_tx_qt.requestFocus()
                    }
                    0 -> {
                        prodDetail_ed_scan.text.clear()
                        AlertMessage("", message.toString())
                    }
                }

            }

            override fun onFailure(call: Call<SearchItemData>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })
    }


    private fun loading(scan_tx: String) {
        val no_io = prodDetail_tx_top.text.toString()
        val cd_item = prodDetail_tx_cdItem.text.toString()
        val cd_lc = scan_tx
        val qt_lc = prodDetail_tx_qt.text.toString()


        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InprodApiService::class.java)
        val callGet = api.loading("ins_rcv", no_io, cd_item, cd_lc, qt_lc)

        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();

        callGet.enqueue(object : Callback<LoadingData> {
            override fun onResponse(
                call: Call<LoadingData>,
                response: Response<LoadingData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message

                when (state) {
                    1 -> {
                        progressDialog.dismiss();
                        prodDetail_tx_cdItem.text = ""
                        prodDetail_tx_nmItem.text = ""
                        prodDetail_tx_qt.text.clear()
                        prodDetail_tx_qt.isEnabled = false
                        prodDetail_ed_scan.text.clear()
                        prodDetail_ed_scan.isEnabled = true
                        prodDetail_ed_scan.requestFocus()
                        stateIndex = 0
                        prodDetail_ly_state.text = "품목 스캔"
                        AlertMessage("입력 성공", "품목 : ${cd_item}\n수량 : ${qt_lc} ")
                    }
                    else -> {
                        progressDialog.dismiss();
                        prodDetail_ed_scan.text.clear()
                        prodDetail_ed_scan.requestFocus()
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<LoadingData>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", AlertF)
            }
        })
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InprodDetailFragment().apply {
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

            when (stateIndex) {
                0 -> {
                    search_item(s.toString())

                }
                2 -> {
                    loading(s.toString())
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }


}


