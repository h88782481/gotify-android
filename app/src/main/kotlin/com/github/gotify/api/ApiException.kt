package com.github.gotify.api

import java.io.IOException
import retrofit2.Response

internal class ApiException : Exception {
    var body: String = ""
        private set
    var code: Int
        private set

    constructor(response: Response<*>) : super("Api 错误", null) {
        body = try {
            if (response.errorBody() != null) response.errorBody()!!.string() else ""
        } catch (e: IOException) {
            "获取错误体时出错 :("
        }
        code = response.code()
    }

    constructor(exceptionBody: String, response: Response<*>) : super("Api 错误", null) {
        body = exceptionBody
        code = response.code()
    }

    constructor(cause: Throwable?) : super("请求失败。", cause) {
        code = 0
    }

    override fun toString() = "代码($code) 响应: ${body.take(200)}"
}
