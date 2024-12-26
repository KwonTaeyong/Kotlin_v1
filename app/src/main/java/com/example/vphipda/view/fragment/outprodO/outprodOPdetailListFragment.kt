package com.example.vphipda.view.fragment.outprodO

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.example.vphipda.adapter.PalletListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.AlertF
import com.example.vphipda.model.appData.Companion.CaseNum
import com.example.vphipda.model.appData.Companion.PackingOrderCode
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.dialog_new_packing_ly.view.*
import kotlinx.android.synthetic.main.fragment_outprod_o_pdetail_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodOPdetailListFragment : Fragment(), PalletListAdapter.PkwoListClickedListener, PalletListAdapter.PalletLongClickedListener, TextWatcher, PalletListAdapter.PalletBtnClickedListener {

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
        return inflater.inflate(R.layout.fragment_outprod_o_pdetail_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        palletArray.clear()
        CaseNum = ""
        palletListAdapter = PalletListAdapter(palletArray, this, this, this)
        outprodOPdetailList_rv.adapter = palletListAdapter
        palletListAdapter.notifyDataSetChanged()

        setpalletArray()


        outprodOPdetailList_ed_scan.addTextChangedListener(this)
        outprodOPdetailList_ed_scan.requestFocus()

        //홈 버튼
        outprodOPdetailList_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        // 지시 목록
        outprodOPdetailList_list.setOnClickListener {
            navController.navigate(R.id.action_outprodOpackingListFragment)
        }
        
        
        // 대포장 케이스 추가
        outprodOPdetailList_bt_add.setOnClickListener {

            val builder = AlertDialog.Builder(navController.context)
            builder.setTitle("")
                .setMessage("대포장 케이스 추가 하시겠습니까?")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        var no_so = PackingOrderCode
                        ins_pallet(no_so)
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }


        

    }


    private fun ins_pallet(no_so: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.ins_pallet("ins_pallet", no_so)

        callGet.enqueue(object : Callback<GetInsPallet> {
            override fun onResponse(
                call: Call<GetInsPallet>,
                response: Response<GetInsPallet>
            ) {
                val state = response.body()?.state
                val message = response.body()?.message
                when (state) {
                    1 -> {
                        setpalletArray()
                        AlertMessage("생성 완료", "수주 번호 : ${no_so}")
                    }
                    else -> {
                        AlertMessage("", message.toString())
                    }
                }
            }
            override fun onFailure(call: Call<GetInsPallet>, t: Throwable) {
                AlertMessage("", AlertF)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setpalletArray() {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.getPallet("get_pallet", PackingOrderCode)

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
                        Toast.makeText(navController.context, "생성된 대포장 케이스가 없습니다.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<GetPallet>, t: Throwable) {
                AlertMessage("", AlertF)
            }
        })

    }

    private fun del_pallet(case:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.del_pallet("del_pallet", case)
        callGet.enqueue(object : Callback<GetDelPallet> {
            override fun onResponse(
                call: Call<GetDelPallet>,
                response: Response<GetDelPallet>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                when (state) {
                    1 -> {
                        setpalletArray()
                        AlertMessage("삭제 성공", "케이스 번호 : ${case}")
                    }
                    0 -> {
                        AlertMessage("", message.toString())
                    }
                }

            }

            override fun onFailure(call: Call<GetDelPallet>, t: Throwable) {
                AlertMessage("", AlertF)
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodOPdetailListFragment().apply {
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

    fun hideKeyboard(view: View) {
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun pkwoClick(pallet: Pallet) {
        CaseNum = pallet.P_PVLHCASE
        navController.navigate(R.id.action_outprodOPdetailListFragment_to_outprodOPdetailScanFragment)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        if(!s.isNullOrBlank()) {

            for (i in palletArray.indices) {
                var caseCode = palletArray[i].P_PVLHCASE
                var scanCode = s.toString()
                when {
                    caseCode == scanCode -> {
                        CaseNum = scanCode
                        navController.navigate(R.id.action_outprodOPdetailListFragment_to_outprodOPdetailScanFragment)
                        break
                    }
                }
            }
            outprodOPdetailList_ed_scan.text.clear()
        }
    }

    override fun afterTextChanged(s: Editable?) {
        
    }

    override fun palletlongClick(item: Pallet): Boolean {

        val builder = AlertDialog.Builder(navController.context)
        builder.setTitle("삭제 하시겠습니까?")
            .setMessage("CASE: ${item.P_PVLHCASE}")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, id ->
                    del_pallet(item.P_PVLHCASE)
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, id ->

                })
        // 다이얼로그를 띄워주기
        builder.show()
        return true
    }

    override fun palletBtnClick(item: Pallet) {

    }

}