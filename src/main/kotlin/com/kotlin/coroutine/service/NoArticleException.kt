package com.kotlin.coroutine.service

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NoArticleException(s: String) : Throwable(s) {

}
