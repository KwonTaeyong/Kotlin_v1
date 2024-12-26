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
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.adapter.PalletListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.network.GetPallet
import com.example.vphipda.network.Pallet
import com.example.vphipda.network.outprodApiService
import kotlinx.android.synthetic.main.fragment_outprod_o_case_list.*
import kotlinx.android.synthetic.main.fragment_outprod_o_pdetail_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class outprodOCaseListFragment : Fragment(), PalletListAdapter.PkwoListClickedListener, PalletListAdapter.PalletLongClickedListener, TextWatcher, PalletListAdapter.PalletBtnClickedListener {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var palletListAdapter: PalletListAdapter
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
        return inflater.inflate(R.layout.fragment_outprod_o_case_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        appData.ScanCodeState = 1
        navController = Navigation.findNavController(view)
        palletArray.clear()
        appData.CaseNum = ""
        appData.PmsNum = ""
        palletListAdapter = PalletListAdapter(palletArray, this, this, this)
        outprodOCaseList_rv.adapter = palletListAdapter
        palletListAdapter.notifyDataSetChanged()


        outprodOCaseList_ed_scan.addTextChangedListener(this)
        outprodOCaseList_ed_scan.requestFocus()
        setpalletArray()

        //홈 버튼
        outprodOCaseList_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setpalletArray() {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.getPallet("get_pms", "")

        callGet.enqueue(object : Callback<GetPallet> {
            override fun onResponse(
                call: Call<GetPallet>,
                response: Response<GetPallet>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data
                println(datalist)
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

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodOCaseListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun pkwoClick(pallet: Pallet) {
            appData.PmsNum = pallet.NO_PMS
            appData.CaseNum = pallet.P_PVLHCASE
            navController.navigate(R.id.action_outprodOCaseListFragment_to_outprodOCaseListDetailFragment)
    }

    override fun palletlongClick(item: Pallet): Boolean {
        return true
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
                        appData.CaseNum = scanCode
                        navController.navigate(R.id.action_outprodOCaseListFragment_to_outprodOCaseListDetailFragment)
                        break
                    }
                }
            }
            outprodOCaseList_ed_scan.text.clear()
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun palletBtnClick(item: Pallet) {

    }

}