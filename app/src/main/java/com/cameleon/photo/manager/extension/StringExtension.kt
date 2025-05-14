package com.cameleon.photo.manager.extension

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun String.formatRoute(arg: String, value: Any, urlEncode: Boolean = false) =
    value
        .let { if (urlEncode) URLEncoder.encode(value.toString(), StandardCharsets.UTF_8.toString()) else value  }
        .let { this.replace("{$arg}", it.toString()) }
