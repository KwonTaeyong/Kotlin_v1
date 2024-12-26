package com.example.vphipda.view.fragment.inven

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
import com.example.vphipda.adapter.InvenListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.AlertE
import com.example.vphipda.model.appData.Companion.AlertF
import com.example.vphipda.model.appData.Companion.AlertS
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.dialog_end_packing_ly.view.*
import kotlinx.android.synthetic.main.dialog_new_prod_ly.view.*
import kotlinx.android.synthetic.main.fragment_inven.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class InvenFragment : Fragment(), InvenListAdapter.OnInvenitemClickedListener, TextWatcher {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var invenListAdapter: InvenListAdapter
    var stateIndex: Int = 0 // 0 : 렉스캔 || 1 : 품목 스캔 || 2: 수량 입력
    var imm : InputMethodManager? = null
    var invenArray = ArrayList<invenitem>()
    var invenArray2 = ArrayList<invenitem>()
    private var NO_CNT : String? = ""
    private var cd_lc : String? = ""
    private var cd_item : String? = ""
    private var qt_cnt : String? = ""

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

        return inflater.inflate(R.layout.fragment_inven, container, false)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateIndex = 0
        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?


        // 실사 코드
        getNoCnt()


        //리사이클 뷰
        invenArray.clear()
        invenListAdapter = InvenListAdapter(invenArray, this)
        inven_rv.adapter = invenListAdapter
        invenListAdapter.notifyDataSetChanged()


        inven_ed_scan.requestFocus()
        hideKeyboard(view)
        inven_ed_scan.addTextChangedListener(this)

        // 홈버튼
        inven_home_btn.setOnClickListener {
            navController.navigate(com.example.vphipda.R.id.action_home)
        }

        // 품목 리셋
        inven_ly_item.setOnClickListener {

            when(stateIndex) {

                0 -> {
                    AlertMessage("", "렉 지정 후 설정 가능 합니다.")
                }

                else -> {
                    hideKeyboard(view)
                    stateIndex = 1
                    inven_tx_qt.clearFocus()
                    inven_tx_qt.isEnabled = false
                    inven_tx_cdItem.text = ""
                    inven_tx_nmItem.text = ""
                    inven_ed_scan.isEnabled = true
                    inven_ed_scan.requestFocus()
                }
            }
        }

        inven_ly_item.setOnLongClickListener {

            when(stateIndex) {

                0 -> {
                    AlertMessage("", "렉 지정 후 설정 가능 합니다.")
                }
                else -> {
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
            }
            return@setOnLongClickListener(true)
        }


        //리셋 버튼
        inven_reset_btn.setOnClickListener {
            stateIndex = 0
            hideKeyboard(view)
            inven_tx_qt.clearFocus()
            inven_tx_qt.isEnabled = false
            inven_tx_qt.text.clear()
            inven_tx_cdItem.text = ""
            inven_tx_nmItem.text = ""
            inven_ed_scan.isEnabled = true
            inven_tx_rack.text = ""
            inven_ed_scan.requestFocus()

            invenArray.clear()
            invenListAdapter.notifyDataSetChanged()
        }

        // 수량 입력 완료 버튼
        inven_tx_qt.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == 5 || actionId == 6) {
                if (!inven_tx_qt.text.toString().isBlank()) {
                    hideKeyboard(view)
                    cd_lc = inven_tx_rack.text.toString()
                    cd_item = inven_tx_cdItem.text.toString()
                    qt_cnt = inven_tx_qt.text.toString()
                    postInsQtcnt(NO_CNT!!, cd_lc!!, cd_item!!, qt_cnt!!)

                } else {
                    AlertMessage("", "수량을 입력해주세요.")
                    stateIndex = 2
                    inven_tx_qt.requestFocus()
                }
                handled = true
            }
            handled
        }
        inven_tx_qt.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                showKeyboard(v)

            }
        }


    }



    fun hideKeyboard(view: View) {

        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(view: View) {
        imm?.showSoftInput(view, 0)
    }


    private fun getNoCnt() {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InvenApiService::class.java)
        val callGet = api.chk_qtcnt("chk_qtcnt")
        callGet.enqueue(object : Callback<GetChkQtcnt> {
            override fun onResponse(
                call: Call<GetChkQtcnt>,
                response: Response<GetChkQtcnt>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when (state) {
                    1 -> {
                        NO_CNT = datalist?.get(0)?.get("NO_CNT")

                    }
                    0 -> {
                        AlertMessage("", message.toString())
                    }
                }

            }

            override fun onFailure(call: Call<GetChkQtcnt>, t: Throwable) {
                Toast.makeText(navController.context, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun postInsQtcnt(no_cnt:String, cd_lc:String, cd_item: String, qt_cnt:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InvenApiService::class.java)
        val callGet = api.ins_qtcnt("ins_qtcnt", no_cnt, cd_lc, cd_item, qt_cnt)

        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();


        callGet.enqueue(object : Callback<GetInsQtcnt> {
            override fun onResponse(
                call: Call<GetInsQtcnt>,
                response: Response<GetInsQtcnt>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                when (state) {
                    1 -> {
                        progressDialog.dismiss();
                        stateIndex = 1
                        getQtcnt(NO_CNT!!, cd_lc)
                        inven_tx_qt.clearFocus()
                        inven_tx_qt.isEnabled = false
                        inven_tx_qt.text.clear()
                        inven_tx_cdItem.text = ""
                        inven_tx_nmItem.text = ""
                        inven_ed_scan.isEnabled = true
                        inven_ed_scan.requestFocus()
                        AlertMessage("입력 성공", "품목 : ${cd_item}\n수량 : ${qt_cnt}")
                    }
                    0 -> {
                        progressDialog.dismiss();
                        stateIndex = 2
                        AlertMessage("", message.toString())
                    }
                }

            }

            override fun onFailure(call: Call<GetInsQtcnt>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", AlertF)
            }
        })
    }

    private fun getQtcnt(no_cnt: String, cd_lc: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InvenApiService::class.java)
        val callGet = api.get_qtcnt("get_qtcnt", no_cnt, cd_lc)
        callGet.enqueue(object : Callback<GetQtcnt> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<GetQtcnt>,
                response: Response<GetQtcnt>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when (state) {
                    1 -> {
                        invenArray.clear()
                        when {
                            !datalist.isNullOrEmpty() -> {
                                invenArray.apply {
                                    for(i in datalist.indices){
                                        add(
                                            invenitem(
                                                datalist[i].NO_CNT,
                                                datalist[i].CD_LC,
                                                datalist[i].CD_ITEM,
                                                datalist[i].QT_CNT,
                                                datalist[i].DTS_CNT,
                                            )
                                        )
                                    }
                                }

                            }
                        }
                        invenListAdapter.notifyDataSetChanged()
                    }
                    0 -> {

                        when(stateIndex){
                            1 -> {
                                stateIndex = 0
                                inven_tx_rack.text = ""
                                AlertMessage("", message.toString())
                            }
                            else -> {
                                AlertMessage("", message.toString())
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetQtcnt>, t: Throwable) {
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
                        inven_tx_cdItem.text = datalist?.get("CD_ITEM")
                        inven_tx_nmItem.text = datalist?.get("STND_ITEM")
                        stateIndex = 2
                        inven_ed_scan.isEnabled = false
                        inven_ed_scan.text.clear()
                        inven_tx_qt.isEnabled = true
                        inven_tx_qt.requestFocus()
                    }
                    0 -> {
                        inven_ed_scan.text.clear()
                        AlertMessage("", message.toString())
                    }
                }

            }

            override fun onFailure(call: Call<SearchItemData>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })
    }


    private fun postDelQtcnt(no_cnt:String, cd_lc:String, cd_item: String, dts_cnt:String, qt_cnt: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InvenApiService::class.java)
        val callGet = api.del_qtcnt("del_qtcnt", no_cnt, cd_lc, cd_item, dts_cnt)

        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();


        callGet.enqueue(object : Callback<GetDelQtcnt> {
            override fun onResponse(
                call: Call<GetDelQtcnt>,
                response: Response<GetDelQtcnt>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                when (state) {
                    1 -> {
                        getQtcnt(NO_CNT!!, cd_lc)
                        progressDialog.dismiss();
                        AlertMessage("삭제 성공", "품목 : ${cd_item}\n수량 : ${qt_cnt}")
                    }
                    0 -> {
                        progressDialog.dismiss();
                        AlertMessage("", message.toString())
                    }
                }

            }

            override fun onFailure(call: Call<GetDelQtcnt>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", AlertF)
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
            InvenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun oninvenClick(item: invenitem): Boolean {


        val builder = AlertDialog.Builder(navController.context)
        builder.setTitle("삭제 하시겠습니까?")
            .setMessage("품목코드: ${item.CD_ITEM}\n수량: ${item.QT_CNT}\n")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, id ->
                    postDelQtcnt(NO_CNT!!, item.CD_LC, item.CD_ITEM, item.DTS_CNT, item.QT_CNT.toString())
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, id ->

                })
        // 다이얼로그를 띄워주기
        builder.show()

        return true
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(!s.isNullOrBlank()) {

            when (stateIndex) {
                0 -> {
                    inven_tx_rack.text = s
                    stateIndex = 1
                    getQtcnt(NO_CNT!!, s.toString())
                    inven_ed_scan.text.clear()
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
}