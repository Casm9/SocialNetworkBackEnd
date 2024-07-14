package com.casm.routes

import com.casm.service.SkillService
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.getSkills(skillService: SkillService) {
    authenticate{
        get("/api/skills/get") {
            call.respond(
                HttpStatusCode.OK,
                skillService.getSkills().map { it.toSkillDto() }
            )
        }
    }
}