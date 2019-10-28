package com.klnm.houseofconversation

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.klnm.houseofconversation.common.LoginActivity
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity<LoginActivity>()

    }
}
