package com.example.vphipda.view.fragment.outprodO

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
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
import com.example.vphipda.adapter.PalletListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.CaseNum
import com.example.vphipda.model.appData.Companion.PackingOrderCode
import com.example.vphipda.model.appData.Companion.PmsNum
import com.example.vphipda.model.appData.Companion.ScanCodeItem
import com.example.vphipda.model.appData.Companion.ScanCodeQt
import com.example.vphipda.model.appData.Companion.ScanCodeRack
import com.example.vphipda.model.appData.Companion.ScanCodeState
import com.example.vphipda.network.GetInsPkitem
import com.example.vphipda.network.GetPallet
import com.example.vphipda.network.Pallet
import com.example.vphipda.network.outprodApiService
import kotlinx.android.synthetic.main.fragment_outprod_o_pdetail_list.*
import kotlinx.android.synthetic.main.fragment_outprod_o_pdetail_scan.*
import kotlinx.android.synthetic.main.fragment_outprod_o_pms_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodOPmsListFragment : Fragment(), PalletListAdapter.PkwoListClickedListener, PalletListAdapter.PalletLongClickedListener, PalletListAdapter.PalletBtnClickedListener {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var palletListAdapter: PalletListAdapter
    var imm : InputMethodManager? = null
    var palletArray = ArrayList<Pallet>()


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
        return inflater.inflate(R.layout.fragment_outprod_o_pms_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        palletArray.clear()
        CaseNum = ""
        PmsNum = ""
        palletListAdapter = PalletListAdapter(palletArray, this, this, this)
        outprodOPmsList_rv.adapter = palletListAdapter
        palletListAdapter.notifyDataSetChanged()
        setpalletArray()

        //홈 버튼
        outprodOPmsList_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        // 지시 목록
        outprodOPmsList_list.setOnClickListener {
            navController.navigate(R.id.action_outprodOItemScanFragment)
        }


        // 대포장 케이스 추가
        outprodOPmsList_bt_add.setOnClickListener {
            when(ScanCodeState) {
                1 -> {
                    AlertMessage("", "품목 스캔 후 사용 가능합니다.")
                }
                0 -> {
                    val builder = AlertDialog.Builder(navController.context)
                    builder.setTitle("")
                        .setMessage("PMS 추가 하시겠습니까?\n수주번호:${PackingOrderCode}\n렉:${ScanCodeRack}\n품목:${ScanCodeItem}\n수량:${ScanCodeQt}")
                        .setPositiveButton("확인",
                            DialogInterface.OnClickListener { dialog, id ->
                                ins_pkitem("", ScanCodeRack, ScanCodeItem, ScanCodeQt)
                            })
                        .setNegativeButton("취소",
                            DialogInterface.OnClickListener { dialog, id ->

                            })
                    // 다이얼로그를 띄워주기
                    builder.show()
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setpalletArray() {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.getPallet("get_pms", PackingOrderCode)

        callGet.enqueue(object : Callback<GetPallet> {
            override fun onResponse(
                call: Call<GetPallet>,
                response: Response<GetPallet>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when {
                    !datalist.isNullOrEmpty() -> {
                        palletArray.clear()
                        palletArray.apply {
                            for(i in datalist.indices){
                                add(
                                    Pallet(datalist[i].NO_PMS,
                                        datalist[i].P_PVLHCASE,
                                        datalist[i].NO_SO
                                    )
                                )
                            }
                        }
                        palletListAdapter.notifyDataSetChanged()
                    }
                    else -> {
                        palletArray.clear()
                        palletListAdapter.notifyDataSetChanged()
                        Toast.makeText(navController.context, "생성된 케이스가 없습니다.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<GetPallet>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })
    }


    private fun ins_pkitem(pmsNum:String, rack:String, item:String, qt:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.ins_pkitem("ins_pms", PackingOrderCode, item, pmsNum, qt, rack)


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
                        progressDialog.dismiss();
                        if (pmsNum.isBlank()) {
                            AlertMessage("입력 완료", "PMS:신규 생성\n반출 렉 : ${rack}\n품목 : ${item}\n수량 : ${qt}")
                        } else {
                            AlertMessage("입력 완료", "PMS:${pmsNum}\n반출 렉 : ${rack}\n품목 : ${item}\n수량 : ${qt}")
                        }
                        navController.navigate(R.id.action_outprodOItemScanFragment)
                    }
                    else -> {
                        progressDialog.dismiss();
                        AlertMessage("", message.toString())
                    }
                }
            }
            override fun onFailure(call: Call<GetInsPkitem>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", appData.AlertF)
            }
        })
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodOPmsListFragment().apply {
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

    override fun pkwoClick(pallet: Pallet) {
        PmsNum = pallet.NO_PMS
        CaseNum = pallet.P_PVLHCASE
        navController.navigate(R.id.action_outprodOPmsListFragment_to_outprodOPmsListDetailFragment)
    }

    override fun palletlongClick(item: Pallet): Boolean {


    return true
    }

    override fun palletBtnClick(item: Pallet) {
        val builder = AlertDialog.Builder(navController.context)
            builder.setTitle("")
                .setMessage("해당 품목을 추가 하시겠습니까?\n수주번호:${PackingOrderCode}\n렉:${ScanCodeRack}\n품목:${ScanCodeItem}\n수량:${ScanCodeQt}\npms: ${item.NO_PMS}\n항목: ${item.P_PVLHCASE}")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        ins_pkitem(item.NO_PMS, ScanCodeRack, ScanCodeItem, ScanCodeQt)
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }
    }