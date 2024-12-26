package com.example.vphipda.view.fragment.sample

import android.app.ProgressDialog
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
import com.example.vphipda.network.InprodApiService
import com.example.vphipda.network.MoveApiService
import com.example.vphipda.network.SearchItemData
import com.example.vphipda.network.reqMvoutData
import kotlinx.android.synthetic.main.fragment_sample_scan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SampleScanFragment : Fragment(), TextWatcher {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    private var stateIndex: Int = 0 // 0 :반출 렉 스캔 || 1 : 품목 스캔 || 2: 수량 입력 || 3: 샘플 처리 완료
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
        return inflater.inflate(R.layout.fragment_sample_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stateIndex = 0
        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?


        sampleScan_ed_scan.requestFocus()
        hideKeyboard(view)
        sampleScan_ed_scan.addTextChangedListener(this)

        // 홈 버튼
        sampleScan_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        // 수량 입력
        sampleScan_tx_qt.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId== EditorInfo.IME_ACTION_DONE) {

                if (!sampleScan_tx_qt.text.toString().isBlank()) {


                    stateIndex = 3
                    sampleScan_ly_state.text = "완료 버튼 입력"
                    hideKeyboard(v)
                    sampleScan_tx_qt.clearFocus()
                    sampleScan_tx_qt.isEnabled = false
                    sampleScan_ed_scan.isEnabled = true
                    sampleScan_ed_scan.requestFocus()


                } else{
                    Toast.makeText(navController.context, "수량을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    stateIndex = 2
                    sampleScan_ly_state.text = "수량 입력"
                    sampleScan_tx_qt.requestFocus()
                }
                handled = true
            }
            handled
        }

        sampleScan_tx_qt.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                showKeyboard(v)
            }
        }



        // 반출 렉 스캔으로
        sampleScan_ly_rack.setOnClickListener {
            sampleScan_tx_rack.text = ""
            sampleScan_tx_cdItem.text = ""
            sampleScan_tx_nmItem.text = ""
            sampleScan_tx_qt.text.clear()
            stateIndex = 0
            sampleScan_ly_state.text = "반출 렉 스캔"
            sampleScan_tx_qt.isEnabled = false
            sampleScan_ed_scan.isEnabled = true
            sampleScan_ed_scan.requestFocus()
            hideKeyboard(it)
        }

        //품목 스캔으로
        sampleScan_ly_item.setOnClickListener {
            stateIndex = 1
            hideKeyboard(view)
            sampleScan_tx_cdItem.text = ""
            sampleScan_tx_nmItem.text = ""
            sampleScan_tx_qt.text.clear()
            sampleScan_ly_state.text = "품목 스캔"
            sampleScan_tx_qt.isEnabled = false
            sampleScan_ed_scan.isEnabled = true
            sampleScan_ed_scan.requestFocus()

        }


        // A 가상 창고 버튼
        sampleScan_bt_move_A.setOnClickListener {
            val scan_tx = "AV000001"
            when(stateIndex) {

                0 -> {
                    sampleScan_tx_rack.text = scan_tx
                    stateIndex = 1
                    sampleScan_ly_state.text = "품목 스캔"
                }

                else -> {
                    Toast.makeText(navController.context, "렉 스캔 상태에서 선택 가능 합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // B 가상 창고 버튼
        sampleScan_bt_move_B.setOnClickListener {
            val scan_tx = "BV000001"
            when(stateIndex) {

                0 -> {
                    sampleScan_tx_rack.text = scan_tx
                    stateIndex = 1
                    sampleScan_ly_state.text = "품목 스캔"
                }

                else -> {
                    Toast.makeText(navController.context, "렉 스캔 상태에서 선택 가능 합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 샘플 처리 완료
        sampleScan_bt_post.setOnClickListener {
            val scan_tx = "PV000001"
            when(stateIndex) {
                3 -> {
                    reqMvout(scan_tx)

                }

                else -> {
                    Toast.makeText(navController.context, "완료 버튼 입력에서 선택 가능 합니다.", Toast.LENGTH_SHORT).show()
                }
            }


        }

    }

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SampleScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun reqMvout(cd_lc2:String) {

        val cd_lc = sampleScan_tx_rack.text.toString()
        val cd_item = sampleScan_tx_cdItem.text.toString()
        val qt_ld = sampleScan_tx_qt.text.toString()

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

                        sampleScan_tx_rack.text = ""
                        sampleScan_tx_cdItem.text = ""
                        sampleScan_tx_nmItem.text = ""
                        sampleScan_tx_qt.text.clear()
                        stateIndex = 0
                        sampleScan_ly_state.text = "반출 렉 스캔"
                        sampleScan_ed_scan.text.clear()
                        sampleScan_ed_scan.requestFocus()
                        sampleScan_tx_qt.isEnabled = false
                        Toast.makeText(navController.context, message, Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss();
                    }
                    else -> {
                        sampleScan_tx_rack.text = ""
                        sampleScan_tx_cdItem.text = ""
                        sampleScan_tx_nmItem.text = ""
                        sampleScan_tx_qt.text.clear()
                        stateIndex = 0
                        sampleScan_ly_state.text = "반출 렉 스캔"
                        sampleScan_ed_scan.text.clear()
                        sampleScan_ed_scan.requestFocus()
                        sampleScan_tx_qt.isEnabled = false
                        Toast.makeText(navController.context, message, Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss();
                    }
                }
            }
            override fun onFailure(call: Call<reqMvoutData>, t: Throwable) {
                progressDialog.dismiss();
                Toast.makeText(navController.context, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
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
                        sampleScan_tx_cdItem.text = datalist?.get("CD_ITEM")
                        sampleScan_tx_nmItem.text = datalist?.get("STND_ITEM")
                        stateIndex = 2
                        sampleScan_ly_state.text = "수량 입력"
                        sampleScan_tx_qt.isEnabled = true
                        sampleScan_ed_scan.text.clear()
                        sampleScan_ed_scan.isEnabled = false
                        sampleScan_tx_qt.requestFocus()

                    }
                    else -> {

                        sampleScan_tx_rack.text = ""
                        sampleScan_tx_cdItem.text = ""
                        sampleScan_tx_nmItem.text = ""
                        sampleScan_tx_qt.text.clear()
                        stateIndex = 0
                        sampleScan_ly_state.text = "반출 렉 스캔"
                        sampleScan_ed_scan.text.clear()
                        sampleScan_tx_qt.isEnabled = false
                        Toast.makeText(navController.context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<SearchItemData>, t: Throwable) {
                Toast.makeText(navController.context, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun hideKeyboard(view: View) {
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(view: View) {
        imm?.showSoftInput(view, 0)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(!s.isNullOrBlank()) {
            when(stateIndex) {
                0 -> {
                    sampleScan_tx_rack.text = s.toString()
                    stateIndex = 1
                    sampleScan_ly_state.text = "품목 스캔"
                    sampleScan_ed_scan.text.clear()
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