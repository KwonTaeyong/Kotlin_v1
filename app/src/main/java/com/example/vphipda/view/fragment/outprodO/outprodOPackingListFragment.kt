package com.example.vphipda.view.fragment.outprodO

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.adapter.PkwoListAdapter
import com.example.vphipda.model.appData
import com.example.vphipda.model.appData.Companion.PackingOrderCode
import com.example.vphipda.network.*
import kotlinx.android.synthetic.main.fragment_outprod_o_packing_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodOPackingListFragment : Fragment(), PkwoListAdapter.PkwoListClickedListener {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var navController: NavController
    lateinit var pkwolistAdapter: PkwoListAdapter
    var pkwoArray = ArrayList<Pkwo>()


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
        return inflater.inflate(R.layout.fragment_outprod_o_packing_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        PackingOrderCode  = ""

        pkwoArray.clear()
        pkwolistAdapter = PkwoListAdapter(pkwoArray, this)
        outprodOPackingList_rv.adapter = pkwolistAdapter
        pkwolistAdapter.notifyDataSetChanged()
        setpkwoArray()

        // 홈버튼
        outprodOPackingList_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

    }



    @SuppressLint("NotifyDataSetChanged")
    private fun setpkwoArray() {

        val retrofit = Retrofit.Builder()
            .baseUrl(appData.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(outprodApiService::class.java)
        val callGet = api.getPkwo("get_pkwo")

        callGet.enqueue(object : Callback<GetPkwo> {
            override fun onResponse(
                call: Call<GetPkwo>,
                response: Response<GetPkwo>
            ) {

                val state = response.body()?.state
                val message = response.body()?.message
                val datalist = response.body()?.data

                when {
                    !datalist.isNullOrEmpty() -> {
                        pkwoArray.apply {
                            for(i in datalist.indices){

                                add(
                                    Pkwo(datalist[i].NO_SO,
                                    datalist[i].LN_PARTNER,
                                    datalist[i].NM_KOR
                                    )
                                )
                            }
                        }
                        pkwolistAdapter.notifyDataSetChanged()
                    }
                    else -> {
                        AlertMessage("", "지시 내용이 없습니다.")
                    }
                }
            }

            override fun onFailure(call: Call<GetPkwo>, t: Throwable) {
                AlertMessage("", appData.AlertF)
            }
        })

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodOPackingListFragment().apply {
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



    override fun pkwoClick(pkwo: Pkwo) {
        PackingOrderCode = pkwo.NO_SO
//        navController.navigate(R.id.action_outprodOPackingListFragment_to_outprodOPdetailListFragment)
        navController.navigate(R.id.action_outprodOPackingListFragment_to_outprodOItemScanFragment)
    }


}