package com.hai.idmanager.utils.ext

inline fun <reified T> getSimpleName(): String = T::class.java.simpleName
