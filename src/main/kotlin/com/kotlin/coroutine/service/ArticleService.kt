package com.kotlin.coroutine.service

import com.kotlin.coroutine.model.Article
import com.kotlin.coroutine.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val repository: ArticleRepository
) {
    suspend fun create(request: ReqCreate): Article {
        return repository.save(request.toArticle())
    }

    suspend fun get(id: Long): Article {
        return repository.findById(id)?: throw NoArticleException("id $id")
    }

    suspend fun getAll(title: String? = null): Flow<Article> {
        return if (title.isNullOrBlank()) {
            repository.findAll()
        } else {
            repository.findAllByTitleContains(title)
        }
    }

    suspend fun update(id: Long, request: ReqUpdate) {
        val article = repository.findById(id) ?: throw NoArticleException("article $id not found")
        repository.save(article.apply {
            request.title?.let { this.title = it }
            request.body?.let { this.body = it }
            request.authorId?.let { this.authorId = it }
        })
    }
}

class ReqUpdate (
    val title: String? = null,
    val body: String? = null,
    val authorId: Long? = null,
)
data class ReqCreate(
    val title: String,
    val body: String? = null,
    val authorId: Long? = null,
) {
    fun toArticle() = Article(
        title = this.title,
        body = this.body,
        authorId = this.authorId
    )
}