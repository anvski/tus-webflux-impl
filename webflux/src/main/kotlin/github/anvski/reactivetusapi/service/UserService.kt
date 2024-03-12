package github.anvski.reactivetusapi.service

import github.anvski.reactivetusapi.model.User
import reactor.core.publisher.Mono

interface UserService {

    fun findByUsername(username: String): Mono<User>

    fun save(username: String, email: String, password: String): Mono<User>

}