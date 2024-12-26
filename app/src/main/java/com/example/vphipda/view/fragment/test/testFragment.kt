package com.example.vphipda.view.fragment.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.model.appData
import com.example.vphipda.networktest.ResultGetSearch
import com.example.vphipda.networktest.testApiService
import com.example.vphipda.networktest.testRetrofitService
import kotlinx.android.synthetic.main.fragment_test.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




/**
 * A simple [Fragment] subclass.
 * Use the [testFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class testFragment : Fragment(),View.OnClickListener, TextWatcher {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var testApiService: testApiService
    lateinit var navController: NavController

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
        return inflater.inflate(R.layout.fragment_test, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)


        test_ed_scanner.requestFocus()
        test_ed_scanner.addTextChangedListener(this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment testFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            testFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }



    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!s.isNullOrBlank()){


            var str_arr = s.split(" ") // 바코드 넘버
            val retrofit = Retrofit.Builder()
                .baseUrl(appData.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(com.example.vphipda.networktest.testApiService::class.java)
            val callGet = api.getMinuDustWeekFrcstDspth(str_arr[0])
            callGet.enqueue(object : Callback<ResultGetSearch>{

                override fun onResponse(
                    call: Call<ResultGetSearch>,
                    response: Response<ResultGetSearch>
                ) {
                    test_tv_pdname_val.text = response.body()?.result
                }

                override fun onFailure(call: Call<ResultGetSearch>, t: Throwable) {
                    test_tv_pdname_val.text = "서버와 연결할 수 없습니다."
                }
            })


            when {
                str_arr.size > 1 -> {
                    test_tv_prod_val.text = str_arr[0]
                    test_tv_qt_val.text = str_arr[1]
                }
                else -> {
                    test_tv_prod_val.text = str_arr[0]
                    test_tv_qt_val.text = ""
                }
            }


            test_tv_bc_val.text = s
            test_ed_scanner.text.clear()

        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

}