package com.example.vphipda.view.fragment.outprodO

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
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.adapter.PalletDetailListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.AlertF
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.fragment_outprod_o_end_scan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class outprodOEndScanFragment : Fragment(), TextWatcher, PalletDetailListAdapter.PalletDetailDataClickedListener, PalletDetailListAdapter.PalletDetailDataLongClickedListener {

    private var param1: String? = null
    private var param2: String? = null
    var stateIndex: Int = 0 // 0 : pms 스캔 || 1 : rack 스캔
    var imm : InputMethodManager? = null
    lateinit var palletDetailListAdapter: PalletDetailListAdapter
    lateinit var navController: NavController
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
        return inflater.inflate(R.layout.fragment_outprod_o_end_scan, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        stateIndex = 0
        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?



        outprodOEndScan_ed_scan.requestFocus()
        hideKeyboard(view)
        outprodOEndScan_ed_scan.addTextChangedListener(this)


        // 홈버튼
        outprodOEndScan_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        //리사이클 뷰
        palletdetailArray.clear()
        palletDetailListAdapter = PalletDetailListAdapter(palletdetailArray, this, this)
        outprodOEndScan_rv.adapter = palletDetailListAdapter
        palletDetailListAdapter.notifyDataSetChanged()


        // PMS 스캔으로
        outprodOEndScan_ly_item.setOnClickListener {
            outprodOEndScan_ly_state.text = "PMS 스캔"
            outprodOEndScan_tx_packing.text = ""
            outprodOEndScan_tx_lc.text = ""
            stateIndex = 0
            outprodOEndScan_ed_scan.isEnabled = true
            outprodOEndScan_ed_scan.requestFocus()
            hideKeyboard(it)
        }

//        // 렉 스캔으로
//        outprodOEndScan_ly_lc.setOnClickListener {
//            when(stateIndex){
//                0 -> {
//                    Toast.makeText(navController.context, "PMS 스캔 후에 가능 합니다.", Toast.LENGTH_SHORT).show()
//                }
//                else -> {
//                    outprodOEndScan_ly_state.text = "이동 위치 스캔"
//                    outprodOEndScan_tx_lc.text = ""
//                    stateIndex = 1
//                    outprodOEndScan_ed_scan.isEnabled = true
//                    outprodOEndScan_ed_scan.requestFocus()
//                    hideKeyboard(it)
//                }
//            }
//        }


        // 완료 버튼
        outprodOEndScan_bt_in.setOnClickListener {
            when(stateIndex) {
                1 -> {
                    var case = outprodOEndScan_tx_packing.text.toString()
                    var cd_lc = "BP000001"


                    val builder = AlertDialog.Builder(navController.context)
                    builder
                        .setMessage("PMS : ${case}\n포트로 이동 처리 하시겠습니까?")
                        .setPositiveButton("확인",
                            DialogInterface.OnClickListener { dialog, id ->
                                go_port(case, cd_lc)
                            })
                        .setNegativeButton("취소",
                            DialogInterface.OnClickListener { dialog, id ->

                            })
                    // 다이얼로그를 띄워주기
                    builder.show()

                }
                else -> {
                    AlertMessage("", "이전 작업을 완료 해주세요.")
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun go_port(case: String, cd_lc: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.goPort("mv_pms", case, cd_lc)

        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();

        palletdetailArray.clear()
        palletDetailListAdapter.notifyDataSetChanged()

        callGet.enqueue(object : Callback<GetGoPort> {
            override fun onResponse(
                call: Call<GetGoPort>,
                response: Response<GetGoPort>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                when (state) {
                    1 -> {
                        progressDialog.dismiss();
                        outprodOEndScan_ly_state.text = "PMS 스캔"
                        outprodOEndScan_tx_packing.text = ""
                        outprodOEndScan_tx_lc.text = ""
                        stateIndex = 0
                        outprodOEndScan_ed_scan.isEnabled = true

                        outprodOEndScan_ed_scan.requestFocus()
                        AlertMessage("이동 완료", "Pms : ${case}\n위치 : ${cd_lc}")
                    }
                    else -> {
                        progressDialog.dismiss();
                        AlertMessage("", message.toString())
                    }
                }
            }
            override fun onFailure(call: Call<GetGoPort>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", AlertF)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setpalletdetailArray(Pms:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.getPalletDetail("get_pms_detail", Pms)

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
                        outprodOEndScan_tx_packing.text = Pms
                        stateIndex = 1
                        outprodOEndScan_ly_state.text = "이동 위치 스캔"
                    }
                    else -> {
                        palletdetailArray.clear()
                        palletDetailListAdapter.notifyDataSetChanged()
                        AlertMessage("", "저장된 품목이 없습니다.")
                    }
                }
            }

            override fun onFailure(call: Call<GetPalletDetail>, t: Throwable) {
                AlertMessage("", AlertF)
            }
        })
    }



    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodOEndScanFragment().apply {
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
                    setpalletdetailArray(s.toString())
                    outprodOEndScan_ed_scan.text.clear()
                }
                1 -> {
                    outprodOEndScan_tx_lc.text = s.toString()

                    var case = outprodOEndScan_tx_packing.text.toString()
                    var cd_lc = s.toString()

                    val builder = AlertDialog.Builder(navController.context)
                    builder
                        .setMessage("PMS : ${case}\n위치 : ${cd_lc}\n이동 처리 하시겠습니까?")
                        .setPositiveButton("확인",
                            DialogInterface.OnClickListener { dialog, id ->
                                go_port(case, cd_lc)
                            })
                        .setNegativeButton("취소",
                            DialogInterface.OnClickListener { dialog, id ->
                                outprodOEndScan_tx_lc.text = ""
                            })
                    // 다이얼로그를 띄워주기
                    builder.show()


                    outprodOEndScan_ed_scan.text.clear()
                }

            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun ontemplcClick(item: PalletDetail) {

    }

    override fun onPalletLongClick(item: PalletDetail): Boolean {
        return true
    }


}