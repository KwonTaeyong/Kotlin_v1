package com.example.vphipda.view.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.model.appData.Companion.An_ID
import com.example.vphipda.model.appData.Companion.AppURL
import com.example.vphipda.model.appData.Companion.AppVersion
import com.example.vphipda.model.appData.Companion.BaseURL
import com.example.vphipda.network.BadgeData
import com.example.vphipda.network.GetVersion
import com.example.vphipda.network.InprodApiService
import kotlinx.android.synthetic.main.fragment_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MainFragment : Fragment(), View.OnClickListener {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    var mBackWait: Long = 0
    private var mWebView: WebView? = null


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
        return inflater.inflate(com.example.vphipda.R.layout.fragment_main, container, false)
    }

    @SuppressLint("HardwareIds")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        setBadge()
        getVersion()
        navController = Navigation.findNavController(view)


        println(An_ID)

//        android_info_btn.setOnClickListener(this)
        in_prod_btn.setOnClickListener(this)
        moveWH_prod_btn.setOnClickListener(this)
        move_prod_btn.setOnClickListener(this)
        android_sample_btn.setOnClickListener(this)
        out_prod_btn.setOnClickListener(this)
        worklist_btn.setOnClickListener(this)
        info_btn.setOnClickListener(this)
        inven_btn.setOnClickListener(this)
        scan_btn.setOnClickListener(this)





        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (System.currentTimeMillis() - mBackWait >= 2000) {
                mBackWait = System.currentTimeMillis()
                Toast.makeText(navController.context, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_LONG)
                    .show()
            } else {
                activity?.finish() //액티비티 종료
            }
        }
    }



    private fun setBadge() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InprodApiService::class.java)
        val callGet = api.Badge("get_badge")


        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("데이터 처리중입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();


        callGet.enqueue(object : Callback<BadgeData> {
            override fun onResponse(
                call: Call<BadgeData>,
                response: Response<BadgeData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when {
                    !datalist.isNullOrEmpty() -> {
                        for (item in datalist.keys) {
                            var getNum: Int? = datalist[item]
                            when (item) {
                                "rcv" -> {
                                    if (getNum != 0) {
                                        in_prod_btn_bg.text = getNum.toString()
                                        in_prod_btn_bg.isVisible = true
                                    }
                                }

                                "mp" -> {
                                    if (getNum != 0) {
                                        move_prod_btn_bg.text = getNum.toString()
                                        move_prod_btn_bg.isVisible = true
                                    }
                                }
                                "qtcnt" -> {

                                    if (getNum != 0) {
                                        inven_btn_bg.text = getNum.toString()
                                        inven_btn_bg.isVisible = true
                                    }
                                }
                                // 샘플 , 반품
                                "sample" -> {
                                    if (getNum != 0) {
                                        sample_btn_bg.text = getNum.toString()
                                        sample_btn_bg.isVisible = true
                                    }
                                }
                                // 국내
                                "do" -> {

                                    if (getNum != 0) {
                                        out_prod_btn_bg.text = getNum.toString()
                                        out_prod_btn_bg.isVisible = true
                                    }
                                }
                                // 해외
                                "os" -> {
                                    if (getNum != 0) {
                                        worklist_btn_bg.text = getNum.toString()
                                        worklist_btn_bg.isVisible = true
                                    }
                                }
                            }
                        }
                        progressDialog.dismiss();
                    }
                    else -> {
                        Toast.makeText(navController.context, message, Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss();
                    }
                }
            }

            override fun onFailure(call: Call<BadgeData>, t: Throwable) {
                Toast.makeText(navController.context, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss();
            }
        })
    }


    private fun getVersion() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InprodApiService::class.java)
        val callGet = api.get_version("get_version")


        // 프로그래스 바
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("버전 체크중 입니다...")
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal)
        progressDialog.setCancelable(false)
        progressDialog.show();


        callGet.enqueue(object : Callback<GetVersion> {
            override fun onResponse(
                call: Call<GetVersion>,
                response: Response<GetVersion>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val data = response.body()?.data
                when(state){
                    1-> {
                            when(data) {

                                AppVersion -> {

                                } else -> {

                                val builder = AlertDialog.Builder(navController.context)
                                builder.setTitle("신규 업데이트 확인")
                                    .setMessage("다운로드 받으시겠습니까?")
                                    .setPositiveButton("확인",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            if (data?.isBlank() != true) {
                                                val intent = Intent(Intent.ACTION_VIEW , Uri.parse("${AppURL}${data}.apk"))
                                                startActivity(intent)
                                            }
                                        })
                                    .setNegativeButton("취소",
                                        DialogInterface.OnClickListener { dialog, id ->

                                        })
                                // 다이얼로그를 띄워주기
                                builder.show()
                                }
                            }
                        progressDialog.dismiss();
                    }

                    else -> {
                        progressDialog.dismiss();
                        Toast.makeText(navController.context, message, Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onFailure(call: Call<GetVersion>, t: Throwable) {
                Toast.makeText(navController.context, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss();
            }
        })
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.in_prod_btn -> {
                navController.navigate(R.id.action_mainFragment_to_InprodFragment)
            }
            R.id.moveWH_prod_btn -> {
                navController.navigate(R.id.action_mainFragment_to_moveWHListFragment)
            }
            R.id.move_prod_btn -> {
                navController.navigate(R.id.action_mainFragment_to_moveProdFragment)
            }
            R.id.android_sample_btn -> {
                navController.navigate(R.id.action_mainFragment_to_sampleBtnListFragment)
            }
            R.id.out_prod_btn -> {
                navController.navigate(R.id.action_mainFragment_to_outprodDBtnListFragment)
            }
            R.id.worklist_btn -> {
                navController.navigate(R.id.action_mainFragment_to_outprodOBtnListFragment)
            }
            R.id.info_btn -> {
                navController.navigate(R.id.action_mainFragment_to_androidInfo)
            }
            R.id.inven_btn -> {

                when(inven_btn_bg.text) {
                    "1" -> {
                        navController.navigate(R.id.action_mainFragment_to_invenFragment)
                    } else -> {
                    Toast.makeText(navController.context, "재고 실사 기간이 아닙니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.scan_btn -> {
                navController.navigate(R.id.action_mainFragment_to_mainScanFragment)
            }
        }

    }
}