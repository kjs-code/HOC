package com.klnm.mygallery

import org.json.JSONArray
import org.json.JSONException

class TestJava {

    internal var json: String? = null

    fun test() {

        try {
            val jArray = JSONArray(json)
            for (i in 0 until jArray.length()) {

                val data = jArray.optString(i)

            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

}
