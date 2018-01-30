package cn.booklish.vertx.simple.util

import java.util.*

fun getUuid():String = UUID.randomUUID().toString().replace("-","")