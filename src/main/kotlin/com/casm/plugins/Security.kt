package com.casm.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = "main"
    val jwtDomain = "https://0.0.0.0:8001"
    val jwtRealm = "social network"
    val jwtSecret = "secret"
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}

val JWTPrincipal.userId: String?
    get() = getClaim("userId", String::class)