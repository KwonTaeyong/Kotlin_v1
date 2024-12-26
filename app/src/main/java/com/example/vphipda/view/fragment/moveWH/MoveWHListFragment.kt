package com.example.vphipda.view.fragment.moveWH

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import kotlinx.android.synthetic.main.fragment_move_w_h_list.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MoveWHListFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_move_w_h_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        // 홈버튼
        home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        moveWHList_bt_A.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("sel_main_tx", "A동 창고내 이동")
            bundle.putString("sel_code_tx", "AV")
            navController.navigate(
                R.id.action_moveWHListFragment_to_moveWHDetailFragment,
                bundle
            )
        }


        moveWHList_bt_B.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("sel_main_tx", "B동 창고내 이동")
            bundle.putString("sel_code_tx", "BV")
            navController.navigate(
                R.id.action_moveWHListFragment_to_moveWHDetailFragment,
                bundle
            )
        }


    }

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MoveWHListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}