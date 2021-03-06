package io.legado.app.help.http.parser

import io.legado.app.utils.EncodingDetect
import io.legado.app.utils.UTF8BOMFighter
import okhttp3.Response
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.exception.HttpStatusCodeException
import java.nio.charset.Charset

@Parser(name = "Text")
class TextParser(val encode: String? = null) : rxhttp.wrapper.parse.Parser<String> {

    override fun onParse(response: Response): String {

        val responseBody = response.body() ?: throw HttpStatusCodeException(response, "内容为空")
        val responseBytes = UTF8BOMFighter.removeUTF8BOM(responseBody.bytes())
        var charsetName: String? = encode

        charsetName?.let {
            return String(responseBytes, Charset.forName(charsetName))
        }

        //根据http头判断
        responseBody.contentType()?.charset()?.let {
            return String(responseBytes, it)
        }

        //根据内容判断
        charsetName = EncodingDetect.getHtmlEncode(responseBytes)
        return String(responseBytes, Charset.forName(charsetName))
    }

}