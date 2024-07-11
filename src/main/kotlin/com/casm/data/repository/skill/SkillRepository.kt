package com.casm.data.repository.skill

import com.casm.data.models.Skill

interface SkillRepository {

    suspend fun getSkills(): List<Skill>
}