package com.github.windsekirun.rxsociallogin.line

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.github.windsekirun.rxsociallogin.SocialLogin
import com.github.windsekirun.rxsociallogin.model.LoginResultItem
import com.github.windsekirun.rxsociallogin.model.SocialType
import com.linecorp.linesdk.LineApiResponseCode
import com.linecorp.linesdk.auth.LineLoginApi

class LineLogin(activity: Activity) : SocialLogin(activity) {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) onResultLineLogin(data)
    }

    override fun onLogin() {
        val lineConfig = getConfig(SocialType.LINE) as LineConfig
        val loginIntent = LineLoginApi.getLoginIntent(activity as Context,
                lineConfig.channelId ?: "")
        activity!!.startActivityForResult(loginIntent, REQUEST_CODE)
    }

    override fun onDestroy() {

    }

    override fun logout() {
        logout(false)
    }

    override fun logout(clearToken: Boolean) {

    }

    private fun onResultLineLogin(data: Intent?) {
        val result = LineLoginApi.getLoginResultFromIntent(data)
        when (result.responseCode) {
            LineApiResponseCode.SUCCESS -> {
                val accessToken = result.lineCredential?.accessToken?.accessToken
                val lineProfile = result.lineProfile
                if (lineProfile == null) {
                    responseFail(SocialType.LINE)
                    return
                }

                val item = LoginResultItem().apply {
                    this.type = SocialType.LINE
                    this.result = true
                    this.accessToken = accessToken ?: ""
                    this.id = lineProfile.userId
                    this.name = lineProfile.displayName
                }

                responseSuccess(item)
            }

            else -> responseFail(SocialType.LINE)
        }
    }

    companion object {
        private const val REQUEST_CODE = 8073
    }
}