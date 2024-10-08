package com.casm.data.models

import com.casm.data.responses.SkillDto
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Skill(
    @BsonId
    val id: String = ObjectId().toString(),
    val name: String,
    val imageUrl: String
) {
    fun toSkillDto(): SkillDto {
        return SkillDto(
            name = name,
            imageUrl = imageUrl
        )
    }
}
