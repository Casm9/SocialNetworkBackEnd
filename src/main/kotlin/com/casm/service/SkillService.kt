package com.casm.service

import com.casm.data.models.Skill
import com.casm.data.repository.skill.SkillRepository

class SkillService(
    private val repository: SkillRepository
) {
    suspend fun getSkills(): List<Skill> {
        return repository.getSkills()
    }
}