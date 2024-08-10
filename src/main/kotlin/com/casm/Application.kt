package com.casm

import com.casm.di.mainModule
import com.casm.plugins.configureHTTP
import com.casm.plugins.configureMonitoring
import com.casm.plugins.configureRouting
import com.casm.plugins.configureSecurity
import com.casm.plugins.configureSerialization
import com.casm.plugins.configureSockets
import io.ktor.application.Application
import io.ktor.application.install
import org.koin.ktor.ext.Koin

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
    configureSockets()
    configureRouting()
}
