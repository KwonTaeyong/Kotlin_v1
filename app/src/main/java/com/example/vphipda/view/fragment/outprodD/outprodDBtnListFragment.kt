package com.example.vphipda.view.fragment.outprodD

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import kotlinx.android.synthetic.main.fragment_outprod_d_btn_list.*



// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class outprodDBtnListFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_outprod_d_btn_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)


        // 홈버튼
        outprodDBtnList_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        // 보관 지시 버튼
        outprodDBtnList_bt_save.setOnClickListener {
            navController.navigate(
                R.id.action_outprodDBtnListFragment_to_outprodDSaveScanFragment
            )
        }

        // 출고 지시 버튼
        outprodDBtnList_bt_out.setOnClickListener {
            navController.navigate(
                R.id.action_outprodDBtnListFragment_to_outprodDOutOrderListFragment
            )
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            outprodDBtnListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}