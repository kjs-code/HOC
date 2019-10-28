package com.klnm.houseofconversation.common

class StringUtil {

    companion object {

        fun isNotEmpty(target: String): Boolean {

            if (target == "" || target == null) {
                return false
            }

            return true

        }

    }

}