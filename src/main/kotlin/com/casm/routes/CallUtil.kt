package com.casm.routes

import com.casm.plugins.userId
import io.ktor.application.ApplicationCall
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.principal


val ApplicationCall.userId: String
    get() = principal<JWTPrincipal>()?.userId.toString()