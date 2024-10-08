package com.casm.data.requests

import com.casm.data.responses.SkillDto
data class UpdateProfileRequest(
    val username: String,
    val bio: String,
    val gitHubUrl: String,
    val instagramUrl: String,
    val linkedInUrl: String,
    val skills: List<SkillDto>
)
