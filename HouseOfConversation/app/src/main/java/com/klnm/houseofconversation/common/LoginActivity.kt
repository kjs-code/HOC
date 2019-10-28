package com.klnm.houseofconversation.common

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.klnm.houseofconversation.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        loginBtn.setOnClickListener {
            val id = inputId.text
            val pw = inputPw.text

            id?.let {
                pw.let {

                }
            }.let {
                Toast.makeText(this, "ID를 입력하세요", Toast.LENGTH_LONG).show()
            }

        }

        joinMemBtn.setOnClickListener {
            startActivity<JoinMemberActivity>()
        }

    }

}
