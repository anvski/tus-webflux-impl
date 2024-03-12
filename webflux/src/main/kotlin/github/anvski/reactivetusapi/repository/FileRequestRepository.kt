package github.anvski.reactivetusapi.repository

import github.anvski.reactivetusapi.model.FileRequest
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface FileRequestRepository : ReactiveCrudRepository<FileRequest, Long> {
    fun findByFileId(fileId: Long): Mono<FileRequest>
}