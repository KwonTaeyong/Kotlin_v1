package com.example.vphipda.view.fragment.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.vphipda.R
import com.example.vphipda.model.appData.Companion.An_ID
import com.example.vphipda.model.appData.Companion.AppVersion
import kotlinx.android.synthetic.main.fragment_android_info.*



private const val TAG = "ANDROIDINFO"
private const val Android_Id = "Android_Id"

@SuppressLint("StaticFieldLeak")
lateinit var navController: NavController


class AndroidInfo : Fragment() {
    private var android_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_android_info, container, false)
    }

    @SuppressLint("HardwareIds")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        tv_id.text = "앱 버전: ${AppVersion}\n\n기기 번호 :${An_ID}"

        back_btn.setOnClickListener{
            navController.navigate(R.id.mainFragment)
        }



    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            AndroidInfo().apply {
                arguments = Bundle().apply {
                    putString("tv_id", param1)
                }
            }
    }

}