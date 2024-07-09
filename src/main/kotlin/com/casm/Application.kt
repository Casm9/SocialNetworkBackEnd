package com.casm

import com.casm.di.mainModule
import com.casm.plugins.*
import io.ktor.application.*
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.modules

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}



fun Application.module() {
    install(Koin) {
        modules(mainModule)
    }
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
