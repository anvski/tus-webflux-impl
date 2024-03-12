package github.anvski.reactivetusapi.service.implementations

import github.anvski.reactivetusapi.model.User
import github.anvski.reactivetusapi.repository.UserRepository
import github.anvski.reactivetusapi.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun findByUsername(username: String): Mono<User> {
        return this.userRepository.findByUserName(username)
    }

    override fun save(username: String, email: String, password: String): Mono<User> {
        return this.userRepository.save(User(null, username, email, passwordEncoder.encode(password)))
    }
}