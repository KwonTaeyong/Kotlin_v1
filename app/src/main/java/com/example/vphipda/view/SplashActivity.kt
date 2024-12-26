package com.example.vphipda.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.vphipda.MainActivity
import com.example.vphipda.R


class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashActivity"
        private const val SPLASH_SECOND: Long = 2000
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )


        toMainActivity()

    }

    private fun toMainActivity(){
        Handler(Looper.getMainLooper()).postDelayed(
            {
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, SPLASH_SECOND
        )
    }


}





    

