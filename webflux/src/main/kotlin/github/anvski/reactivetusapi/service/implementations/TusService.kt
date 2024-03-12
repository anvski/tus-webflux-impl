package github.anvski.reactivetusapi.service.implementations

import github.anvski.reactivetusapi.controller.dto.TusUploadStartRequest
import github.anvski.reactivetusapi.exceptions.BadRequestException
import github.anvski.reactivetusapi.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

@Service
class TusService(
    private val uploadService: UploadService
) {
    fun uploadFile(tusUploadStartRequest: TusUploadStartRequest): Mono<File> {
        return Mono.fromSupplier {
            takeIf { tusUploadStartRequest.fileSize < 1 }?.let { Mono.empty() }
                ?: uploadService.uploadFile(
                    tusUploadStartRequest.fileSize,
                    tusUploadStartRequest.mimeType,
                    0L,
                    UUID.randomUUID().toString(),
                    tusUploadStartRequest.metadata
                )
        }.flatMap { it }.switchIfEmpty(Mono.error(RuntimeException()))
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun patchFile(bytes: ByteArray, uuid: UUID, offset: Long, contentLength: Long): Mono<File> {
        return Mono.defer {
            (bytes.size.toLong() != contentLength).takeIf { it }?.let { return@defer Mono.empty() }
                ?: uploadService.updateFileContent(bytes, uuid, offset, contentLength)
        }.switchIfEmpty(Mono.error(BadRequestException("Supplied [Content-Length] is incorrect")))
    }

    fun fileHead(uuid: UUID): Mono<File> = uploadService.findFileByUuid(uuid)

    fun deleteFile(uuid: UUID): Mono<Boolean> = mono(Dispatchers.IO) {
        uploadService.deleteFileByUuid(uuid, this.coroutineContext)
    }

}