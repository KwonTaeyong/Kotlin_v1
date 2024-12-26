package com.example.vphipda.view.fragment.outprodO

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.model.appData
import kotlinx.android.synthetic.main.fragment_outprod_o_btn_list.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodOBtnListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
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
        return inflater.inflate(R.layout.fragment_outprod_o_btn_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        //홈버튼
        outprodOBtnList_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        //패킹 지시
        outprodOBtnList_bt_packing.setOnClickListener {
            navController.navigate(R.id.action_outprodOBtnListFragment_to_outprodOPackingListFragment)
        }

        // 출고 지시
        outprodOBtnList_bt_out.setOnClickListener {
            navController.navigate(R.id.action_outprodOBtnListFragment_to_outprodOEndScanFragment)
        }

        // 대포장 목록
        outprodOBtnList_bt_case_list.setOnClickListener {
            navController.navigate(R.id.action_outprodOBtnListFragment_to_outprodOCaseListFragment)
        }


    }

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodOBtnListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}