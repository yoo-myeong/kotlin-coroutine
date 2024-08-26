package com.kotlin.coroutine.controller

import com.kotlin.coroutine.model.Article
import com.kotlin.coroutine.repository.ArticleRepository
import com.kotlin.coroutine.service.ArticleService
import com.kotlin.coroutine.service.ReqCreate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.temporal.ChronoUnit

@SpringBootTest
@ActiveProfiles("test")
class ArticleControllerTest(
    @Autowired private val articleService: ArticleService,
    @Autowired private val articleRepository: ArticleRepository,
    @Autowired private val context: ApplicationContext
) : StringSpec({

    val logger = KotlinLogging.logger {}
    val client = WebTestClient.bindToApplicationContext(context).build()

    "create" {
        client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(ReqCreate("title1", "content1", 1234))
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("title").isEqualTo("title1")
            .jsonPath("body").isEqualTo("content1")
            .jsonPath("authorId").isEqualTo(1234)
    }

    "get" {
        val created = client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(ReqCreate("title test", "r2dbc coroutine sample", 1234))
            .exchange()
            .expectBody(Article::class.java)
            .returnResult().responseBody!!
        val read = client.get().uri("/article/${created.id}").accept(APPLICATION_JSON).exchange()
            .expectBody(Article::class.java)
            .returnResult().responseBody!!

        logger.debug { ">> res: $read" }

        read.id        shouldBe created.id
        read.title     shouldBe created.title
        read.body      shouldBe created.body
        read.authorId  shouldBe created.authorId
        read.createdAt?.truncatedTo(ChronoUnit.SECONDS) shouldBe created.createdAt?.truncatedTo(ChronoUnit.SECONDS)
        read.updatedAt?.truncatedTo(ChronoUnit.SECONDS) shouldBe created.updatedAt?.truncatedTo(ChronoUnit.SECONDS)
    }
})
