package github.anvski.reactivetusapi.repository

import github.anvski.reactivetusapi.model.File
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface FileRepository : ReactiveCrudRepository<File, Long> {
    fun findByUuidEquals(uuid: UUID): Mono<File>
}