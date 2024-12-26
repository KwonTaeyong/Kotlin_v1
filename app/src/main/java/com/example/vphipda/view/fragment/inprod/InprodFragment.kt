package com.example.vphipda.view.fragment.inprod

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.adapter.ProdListAdapter
import com.example.vphipda.model.Prod
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.BaseURL
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.fragment_inprod.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class InprodFragment : Fragment(), ProdListAdapter.OnProdClickedListener {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var prodListAdapter: ProdListAdapter
    var prodArray = ArrayList<Prod>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val clipboard = activity?.baseContext?.getSystemService(Context.CLIPBOARD_SERVICE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inprod, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        prodArray.clear()
        prodListAdapter = ProdListAdapter(prodArray, this)
        pordlist_rv.adapter = prodListAdapter
        prodListAdapter.notifyDataSetChanged()
        setprodArray()


        prod_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setprodArray() {

        val retrofit = Retrofit.Builder()
            .baseUrl(BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InprodApiService::class.java)
        val callGet = api.getReceiving("get_rcv")

        callGet.enqueue(object : Callback<GetReceivingData> {
            override fun onResponse(
                call: Call<GetReceivingData>,
                response: Response<GetReceivingData>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when {
                    !datalist.isNullOrEmpty() -> {
                        prodArray.apply {
                            for(i in datalist.indices){
                                val Y = datalist[i].DT_IO.substring(0..3)
                                val M = datalist[i].DT_IO.substring(4..5)
                                val D = datalist[i].DT_IO.substring(6..7)
                                val dateForm = "$Y.$M.$D"

                                add(Prod(dateForm,
                                    datalist[i].LN_PARTNER,
                                    datalist[i].NO_IO))
                            }
                        }
                        prodListAdapter.notifyDataSetChanged()
                    }
                    else -> {
                        AlertMessage("", "지시 내용이 없습니다.")
                    }
                }
            }

            override fun onFailure(call: Call<GetReceivingData>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InprodFragment().apply {
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

    override fun onprodClick(prod: Prod) {
        val bundle = Bundle()
        bundle.putString("NO_ID", prod.NO_IO)
        navController.navigate(R.id.action_inprodFragment_to_inprodDetailFragment, bundle)
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val inputMethodManager: InputMethodManager = activity?.baseContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

}


