package com.casm.data.repository.skill

import com.casm.data.models.Skill
import org.litote.kmongo.coroutine.CoroutineDatabase

class SkillRepositoryImpl(db: CoroutineDatabase) : SkillRepository {

    private val skills = db.getCollection<Skill>()
    override suspend fun getSkills(): List<Skill> {
        return skills.find().toList()
    }
}