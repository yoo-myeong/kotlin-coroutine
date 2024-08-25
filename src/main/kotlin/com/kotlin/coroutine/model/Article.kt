package com.kotlin.coroutine.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("TB_ARTICLE")
class Article(
    @Id
    var id: Long = 0,
    var title: String,
    var body: String? = null,
    var authorId: Long? = null,
): BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Article
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String =
        "Article(id=$id, title='$title', body='$body', authorId=$authorId, ${super.toString()})"
}

open class BaseEntity (
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
) {
    override fun toString(): String {
        return "createdAt=$createdAt, updatedAt=$updatedAt"
    }
}