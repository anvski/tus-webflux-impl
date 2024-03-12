package github.anvski.reactivetusapi.controller.advice

import github.anvski.reactivetusapi.common.ApplicationConstants
import github.anvski.reactivetusapi.exceptions.BadRequestException
import github.anvski.reactivetusapi.exceptions.CommonException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import reactor.core.publisher.Mono
import java.io.FileNotFoundException

@ControllerAdvice
class CommonExceptionHandler {
    @ExceptionHandler(value = [NoSuchElementException::class])
    fun noSuchElementException(exception: NoSuchElementException): Mono<ResponseEntity<Any>> =
        Mono.just(ResponseEntity(ApplicationConstants.NO_SUCH_ELEMENT, HttpStatus.NOT_FOUND))

    @ExceptionHandler(value = [NoSuchFileException::class])
    fun noSuchFileException(exception: NoSuchFileException): Mono<ResponseEntity<Any>> =
        Mono.just(ResponseEntity(ApplicationConstants.NO_SUCH_ELEMENT, HttpStatus.NOT_FOUND))

    @ExceptionHandler(value = [FileNotFoundException::class])
    fun fileNotFoundException(exception: FileNotFoundException): Mono<ResponseEntity<Any>> =
        Mono.just(ResponseEntity(exception.message, HttpStatus.NOT_FOUND))

    @ExceptionHandler(value = [BadRequestException::class])
    fun fileNotFoundException(exception: BadRequestException): Mono<ResponseEntity<Any>> =
        Mono.just(ResponseEntity(exception.message, HttpStatus.BAD_REQUEST))

    @ExceptionHandler(value = [CommonException::class])
    fun fileNotFoundException(exception: CommonException): Mono<ResponseEntity<Any>> =
        Mono.just(ResponseEntity(exception.message, HttpStatus.INTERNAL_SERVER_ERROR))
}