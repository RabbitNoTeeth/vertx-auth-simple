package cn.booklish.vertx.auth.simple.util

import java.util.*

fun getUuid():String = UUID.randomUUID().toString().replace("-","")