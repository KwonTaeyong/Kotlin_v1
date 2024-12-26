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
import com.example.vphipda.adapter.PalletDetailListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.dialog_end_packing_ly.view.*
import kotlinx.android.synthetic.main.fragment_outprod_o_case_list_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodOCaseListDetailFragment : Fragment(), PalletDetailListAdapter.PalletDetailDataClickedListener, PalletDetailListAdapter.PalletDetailDataLongClickedListener {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var palletDetailListAdapter: PalletDetailListAdapter
    var palletdetailArray = ArrayList<PalletDetail>()
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
        return inflater.inflate(R.layout.fragment_outprod_o_case_list_detail, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        imm = navController.context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?


        // 대포장 케이스 번호 지정
        outprodOCaseListDetail_tx_rack2.text = appData.PmsNum


        //홈 버튼
        outprodOCaseListDetail_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }


        //리사이클 뷰
        palletdetailArray.clear()
        palletDetailListAdapter = PalletDetailListAdapter(palletdetailArray, this, this)
        outprodOCaseListDetail_rv.adapter = palletDetailListAdapter
        palletDetailListAdapter.notifyDataSetChanged()

        setpalletdetailArray()



        outprodOCaseListDetail_bt_in.setOnClickListener {
            val builder = AlertDialog.Builder(navController.context)
            var v1 = layoutInflater.inflate(R.layout.dialog_end_packing_ly, null)
            builder.setView(v1)
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        var condition1 = v1.end_packing_et_w.text.isNullOrBlank()
                        var condition2 = v1.end_packing_et_z.text.isNullOrBlank()
                        var condition3 = v1.end_packing_et_x.text.isNullOrBlank()
                        var condition4 = v1.end_packing_et_y.text.isNullOrBlank()

                        var gw:String = "0" // 실측 중량
                        var Lz :String = "0" // 높이
                        var Lx:String = "0" // 가로
                        var Ly:String = "0" // 세로


                        if (!condition1) {
                            gw = v1.end_packing_et_w.text.toString() // 실측 중량
                        }

                        if (!condition2) {
                            Lz = v1.end_packing_et_z.text.toString() // 높이
                        }

                        if (!condition3) {
                            Lx = v1.end_packing_et_x.text.toString() // 가로
                        }

                        if (!condition4) {
                            Ly = v1.end_packing_et_y.text.toString() // 세로
                        }



                        hideKeyboard(v1)
                        cls_case(appData.PmsNum, gw, Lx, Ly, Lz)

                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                        hideKeyboard(v1)
                    })

            // 다이얼로그를 띄워주기
            builder.show()
            v1.end_packing_et_w.requestFocus()
        }

    }

    private fun cls_case(case:String, gw:String, x:String, y:String, z:String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.cls_case("cls_pms", case, gw, x, y, z)

        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();

        callGet.enqueue(object : Callback<GetClsCase> {
            override fun onResponse(
                call: Call<GetClsCase>,
                response: Response<GetClsCase>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message

                when(state) {
                    1 -> {
                        progressDialog.dismiss();
                        navController.navigate(R.id.action_outprodOCaseListFragment)
                        AlertMessage("완포장 처리 완료", "Pms : ${case}\n실측 중량 : ${gw}\n높이 : ${z}\n가로 : ${x}\n 세로 : ${y}")
                    }
                    0 -> {
                        progressDialog.dismiss();
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetClsCase>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", appData.AlertF)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun delPk(cd_item:String, case:String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.del_pk("del_pms", cd_item, case)

        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();

        callGet.enqueue(object : Callback<GetDelPkitem> {
            override fun onResponse(
                call: Call<GetDelPkitem>,
                response: Response<GetDelPkitem>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message

                when(state) {
                    1 -> {
                        setpalletdetailArray()
                        progressDialog.dismiss();
                        AlertMessage("삭제 성공", "품목코드: ${cd_item}")
                    }
                    else -> {
                        progressDialog.dismiss();
                        AlertMessage("", message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetDelPkitem>, t: Throwable) {
                progressDialog.dismiss();
                AlertMessage("", appData.AlertF)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setpalletdetailArray() {
        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.getPalletDetail("get_pms_detail", appData.PmsNum)

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
                    }
                    else -> {
                        palletdetailArray.clear()
                        palletDetailListAdapter.notifyDataSetChanged()
                        Toast.makeText(navController.context, "저장된 품목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<GetPalletDetail>, t: Throwable) {
                Toast.makeText(navController.context, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
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

    fun showKeyboard(view: View) {
        imm?.showSoftInput(view, 0)
    }

    fun hideKeyboard(view: View) {
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {



        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodOCaseListDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun ontemplcClick(item: PalletDetail) {

    }

    override fun onPalletLongClick(item: PalletDetail): Boolean {
        val builder = AlertDialog.Builder(navController.context)
        builder.setTitle("삭제 하시겠습니까?")
            .setMessage("품목코드: ${item.CD_ITEM}\n수량: ${item.QT}\n")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, id ->
                    delPk(item.CD_ITEM, appData.PmsNum)

                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, id ->

                })
        // 다이얼로그를 띄워주기
        builder.show()

        return true
    }
}