package com.example.vphipda.view.fragment.outprodO

import android.app.AlertDialog
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
import com.example.vphipda.model.appData.Companion.PackingOrderCode
import com.example.vphipda.network.GetUpdPkwo
import com.example.vphipda.network.InprodApiService
import com.example.vphipda.network.SearchItemData
import com.example.vphipda.network.outprodApiService
import kotlinx.android.synthetic.main.fragment_outprod_o_packing_scan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodOPackingScanFragment : Fragment(), TextWatcher {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    var stateIndex: Int = 0 // 0 : 입고 렉 스캔 || 1 : 품목 스캔 || 2: 수량 입력 || 3: 완료 버튼
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
        return inflater.inflate(R.layout.fragment_outprod_o_packing_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stateIndex = 0
        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        outprodOPackingScan_tx_main.text = PackingOrderCode

        outprodOPackingScan_ed_scan.requestFocus()
        hideKeyboard(view)
        outprodOPackingScan_ed_scan.addTextChangedListener(this)

        // 홈버튼
        outprodOPackingScan_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        // 지시 목록
        outprodOPackingScan_bt_back.setOnClickListener {
            navController.navigate(R.id.action_outprodOpackingListFragment)
        }


        //대포장 목록
        outprodOPackingDetail_bt_list.setOnClickListener {
            navController.navigate(R.id.action_outprodOPdetailListFragment)
        }

        // 수량 입력
        outprodOPackingScan_tx_qt.setOnEditorActionListener { v, actionId, event ->
            var handled = false

            if (actionId == EditorInfo.IME_ACTION_DONE) {

                if (!outprodOPackingScan_tx_qt.text.toString().isBlank()) {

                    stateIndex = 3
                    outprodOPackingScan_ly_state.text = "완료 버튼 입력"
                    hideKeyboard(v)
                    outprodOPackingScan_tx_qt.clearFocus()
                    outprodOPackingScan_tx_qt.isEnabled = false
                } else{
                    AlertMessage("", "수량을 입력해주세요.")
                    stateIndex = 2
                    outprodOPackingScan_ly_state.text = "수량 입력"
                    outprodOPackingScan_tx_qt.requestFocus()
                }
                handled = true
            }
            handled
        }

        //수량 포커스
        outprodOPackingScan_tx_qt.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                showKeyboard(v)
            }
        }


        // 반출 렉 스캔으로
        outprodOPackingScan_ly_rack.setOnClickListener {
            outprodOPackingScan_tx_rack.text = ""
            outprodOPackingScan_tx_cdItem.text = ""
            outprodOPackingScan_tx_nmItem.text = ""
            outprodOPackingScan_tx_qt.text.clear()
            stateIndex = 0
            outprodOPackingScan_ly_state.text = "렉 스캔"
            outprodOPackingScan_tx_qt.isEnabled = false
            outprodOPackingScan_ed_scan.isEnabled = true
            outprodOPackingScan_ed_scan.requestFocus()
            hideKeyboard(it)
        }

        // 품목 스캔으로
        outprodOPackingScan_ly_item.setOnClickListener {
            when(stateIndex){
                0 -> {
                    AlertMessage("", "렉 스캔 후에 가능 합니다.")
                }
                else -> {
                    outprodOPackingScan_tx_cdItem.text = ""
                    outprodOPackingScan_tx_nmItem.text = ""
                    outprodOPackingScan_tx_qt.text.clear()
                    stateIndex = 1
                    outprodOPackingScan_ly_state.text = "품목 스캔"
                    outprodOPackingScan_tx_qt.isEnabled = false
                    outprodOPackingScan_ed_scan.isEnabled = true
                    outprodOPackingScan_ed_scan.requestFocus()
                    hideKeyboard(it)
                }
            }
        }
        
        // 완료 버튼 
        outprodOPackingScan_bt_out.setOnClickListener { 
            when(stateIndex) {
                3 -> {

                    var cd_item:String = outprodOPackingScan_tx_cdItem.text.toString()
                    var cd_lc:String = outprodOPackingScan_tx_rack.text.toString()
                    var qt:String = outprodOPackingScan_tx_qt.text.toString()
                    upd_pkwo(PackingOrderCode, cd_item, cd_lc, qt)
                    hideKeyboard(view)
                }
                else -> {
                    AlertMessage("", "이전 작업을 완료 해주세요.")
                }

            }
            
        }

    }

    private fun upd_pkwo(no_so: String, cd_item: String, cd_lc:String, qt:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.upd_pkwo("upd_pkwo", no_so, cd_item, cd_lc, qt)

        callGet.enqueue(object : Callback<GetUpdPkwo> {
            override fun onResponse(
                call: Call<GetUpdPkwo>,
                response: Response<GetUpdPkwo>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                when (state) {
                    1 -> {
                        outprodOPackingScan_tx_rack.text = ""
                        outprodOPackingScan_tx_cdItem.text = ""
                        outprodOPackingScan_tx_nmItem.text = ""
                        outprodOPackingScan_tx_qt.text.clear()
                        stateIndex = 0
                        outprodOPackingScan_ly_state.text = "렉 스캔"
                        outprodOPackingScan_tx_qt.isEnabled = false
                        outprodOPackingScan_ed_scan.isEnabled = true
                        outprodOPackingScan_ed_scan.requestFocus()
                        AlertMessage("반출 성공", "반출 렉 : ${cd_lc}\n품목 : ${cd_item}\n수량 : ${qt}")
                    }
                    else -> {
                        AlertMessage("", message.toString())
                    }
                }
            }
            override fun onFailure(call: Call<GetUpdPkwo>, t: Throwable) {
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
                        outprodOPackingScan_tx_cdItem.text = datalist?.get("CD_ITEM")
                        outprodOPackingScan_tx_nmItem.text = datalist?.get("STND_ITEM")
                        stateIndex = 2
                        outprodOPackingScan_ly_state.text = "수량 입력"
                        outprodOPackingScan_tx_qt.isEnabled = true
                        outprodOPackingScan_ed_scan.text.clear()
                        outprodOPackingScan_ed_scan.isEnabled = false
                        outprodOPackingScan_tx_qt.requestFocus()

                    }
                    else -> {

                        outprodOPackingScan_tx_cdItem.text = ""
                        outprodOPackingScan_tx_nmItem.text = ""
                        outprodOPackingScan_tx_qt.text.clear()
                        stateIndex = 1
                        outprodOPackingScan_ly_state.text = "품목 스캔"
                        outprodOPackingScan_tx_qt.isEnabled = false
                        outprodOPackingScan_ed_scan.isEnabled = true
                        outprodOPackingScan_ed_scan.text.clear()
                        outprodOPackingScan_ed_scan.requestFocus()
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
            outprodOPackingScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(!s.isNullOrBlank()) {
            when(stateIndex) {
                0 -> {
                    outprodOPackingScan_tx_rack.text = s.toString()
                    stateIndex = 1
                    outprodOPackingScan_ly_state.text = "품목 스캔"
                    outprodOPackingScan_ed_scan.text.clear()
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