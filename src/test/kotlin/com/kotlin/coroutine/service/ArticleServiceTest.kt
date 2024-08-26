package com.kotlin.coroutine.service

import com.kotlin.coroutine.repository.ArticleRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Transaction
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@SpringBootTest
@ActiveProfiles("test")
class ArticleServiceTest(
    @Autowired private val articleService: ArticleService,
    @Autowired private val articleRepository: ArticleRepository,
    @Autowired private val rxtx: TransactionalOperator
) : StringSpec({

    beforeTest {
        articleRepository.deleteAll()
    }

    "create and get" {
        rxtx.rollback {
            val prevCnt = articleRepository.count()
            val created = articleService.create(ReqCreate("title1"))
            val get = articleService.get(created.id)
            get.id shouldBe created.id
            get.title shouldBe created.title
            get.authorId shouldBe created.authorId
            get.createdAt shouldNotBe null
            get.updatedAt shouldNotBe null
        }
    }

    "get all" {
        rxtx.rollback {
            val created = articleService.create(ReqCreate("title1"))
            articleService.getAll().toList().size shouldBeGreaterThan 1
        }
    }

    "update" {
        rxtx.rollback {
            val created = articleService.create(ReqCreate("title1"))
            articleService.update(created.id, ReqUpdate(title = "title2"))
            val updated = articleService.get(created.id)
            updated.title shouldBe "title2"
        }
    }
})

suspend fun<T> TransactionalOperator.rollback(cb: suspend (ReactiveTransaction) -> T) {
    return this.executeAndAwait { tx ->
        tx.setRollbackOnly()
        cb.invoke(tx)
    }
}