package com.cameleon.photo.manager.extension

fun String.formatRoute(arg: String, value: Any) = this.replace("{$arg}", value.toString())