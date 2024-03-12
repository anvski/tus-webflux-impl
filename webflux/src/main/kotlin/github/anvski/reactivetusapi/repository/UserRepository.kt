package github.anvski.reactivetusapi.repository

import github.anvski.reactivetusapi.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<User, Long> {

    fun findByUserName(username: String): Mono<User>

}

