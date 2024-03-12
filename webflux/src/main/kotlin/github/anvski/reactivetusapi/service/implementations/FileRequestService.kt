package github.anvski.reactivetusapi.service.implementations

import github.anvski.reactivetusapi.controller.dto.FileRequestData
import github.anvski.reactivetusapi.exceptions.BadRequestException
import github.anvski.reactivetusapi.model.FileRequest
import github.anvski.reactivetusapi.repository.FileRequestRepository
import github.anvski.reactivetusapi.service.UserService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class FileRequestService(private val repository: FileRequestRepository,
                         private val userService: UserService,
                         private val fileService: FileService) {

    fun saveFileRequest(fileUuid: UUID, username: String, fileRequestData: FileRequestData) =
        fileService.findFileByUuid(fileUuid).zipWith(
            userService.findByUsername(username)
        ).filter { it.t1.id != null && it.t2.id != null }
            .flatMap {
                repository.save(
                    FileRequest(fileId = it.t1.id!!, requestData = fileRequestData, userId = it.t2.id!!)
                )
            }.switchIfEmpty(Mono.error(BadRequestException()))

}