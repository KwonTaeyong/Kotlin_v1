package com.example.vphipda.view.fragment.sample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.model.appData.Companion.SampleText
import com.example.vphipda.model.appData.Companion.SampleType
import kotlinx.android.synthetic.main.fragment_sample_btn_list.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SampleBtnListFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_sample_btn_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        SampleType = ""
        SampleText = ""


        // 홈버튼
        sampleBtnList_home_btn.setOnClickListener {
            navController.navigate(R.id.action_home)
        }

        //계정대체입고
        sampleBtnList_bt.setOnClickListener {
            SampleType = "get_atin"
            SampleText = "계정대체입고"
            navController.navigate(R.id.action_sampleBtnListFragment_to_sampleOrderListFragment)
        }

        //계정대체출고
        sampleBtnList_bt2.setOnClickListener {
            SampleType = "get_sample"
            SampleText = "계정대체출고"
            navController.navigate(R.id.action_sampleBtnListFragment_to_sampleOrderListFragment)
        }

        //판매 반품
        sampleBtnList_bt3.setOnClickListener {
            SampleType = "get_nmreturn"
            SampleText = "판매 반품"
            navController.navigate(R.id.action_sampleBtnListFragment_to_sampleOrderListFragment)
        }

        //구매 반품
        sampleBtnList_bt4.setOnClickListener {
            SampleType = "get_pcreturn"
            SampleText = "구매 반품"
            navController.navigate(R.id.action_sampleBtnListFragment_to_sampleOrderListFragment)
        }


    }

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SampleBtnListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}