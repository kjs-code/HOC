package com.klnm.houseofconversation.common

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.klnm.houseofconversation.R
import kotlinx.android.synthetic.main.activity_join_member.*
import kotlinx.android.synthetic.main.activity_join_member.joinMemBtn
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject


class JoinMemberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_member)

        val jObj = JSONObject()
        val upload = DoDataUpload()
        var idCheck = false

        idCheckBtn.setOnClickListener {
            val id = inputJoinId.text.toString()
            if(!StringUtil.isNotEmpty(id)) {
                Toast.makeText(this, "ID를 입력하세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            jObj.put("id", id)
            val idStatus = upload.httpJsonUpload("idCheck", "", jObj)

            Log.d("JoinMemberActivity", "idStatus values ===$idStatus")

            if("success".equals(idStatus)){
                idCheck = true
            }else{
                Toast.makeText(this, "중복된 ID입니다. 새로운 ID를 입력하세요", Toast.LENGTH_LONG).show()
            }

        }

        joinMemBtn.setOnClickListener {
            val id = inputJoinId.text.toString()
            val pw = inputJoinPw.text.toString()
            val pwCheck = inputJoinPwCheck.text.toString()
            val email = inputJoinEmail.text.toString()


            if(!StringUtil.isNotEmpty(id)) {
                Toast.makeText(this, "ID를 입력하세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!idCheck) {
                Toast.makeText(this, "ID중복체크를 해주세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!StringUtil.isNotEmpty(pw)) {
                Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!StringUtil.isNotEmpty(pwCheck)) {
                Toast.makeText(this, "비밀번호확인을 입력하세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!StringUtil.isNotEmpty(email)) {
                Toast.makeText(this, "이메일을 입력하세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!pw.equals(pwCheck)) {
                Toast.makeText(this, "비밀번호와 비밀번호확인이 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            jObj.put("id", id)
            jObj.put("pw", pw)
            jObj.put("email", email)

            upload.httpJsonUpload("joinMember", "", jObj)

        }

    }
}
