package github.anvski.reactivetusapi.controller

import github.anvski.reactivetusapi.controller.dto.FileRequestDto
import github.anvski.reactivetusapi.controller.dto.TusUploadStartRequest
import github.anvski.reactivetusapi.service.implementations.FileRequestService
import github.anvski.reactivetusapi.service.implementations.TusService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.HeadersBuilder
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration
import java.util.*

@RestController
@RequestMapping("/upload")
class UploadController(
    private val tusService: TusService,
    private val fileRequestService: FileRequestService
) {
    @PostMapping("/store-request")
    fun storeRequest(@RequestBody fileRequestDto: FileRequestDto, authentication: Authentication)
            : Mono<ResponseEntity<Any>> =
        fileRequestService.saveFileRequest(
            fileRequestDto.fileUuid,
            authentication.principal as String,
            fileRequestDto.fileRequestData
        ).map {
            ResponseEntity.ok().buildWithStaticHeaders()
        }

    @PostMapping
    fun startUpload(
        @RequestHeader(name = "Upload-Length") fileSize: Long,
        @RequestHeader(name = "Tus-Resumable", defaultValue = "1.0.0") version: String,
        @RequestHeader(name = "Upload-Metadata") metadata: String,
        @RequestHeader(name = "Mime-Type") mimeType: String
    ): Mono<ResponseEntity<Any>> {
        return tusService.uploadFile(
            TusUploadStartRequest(
                fileSize, version, metadata, mimeType
            )
        ).map {
            ResponseEntity.created(URI("upload/${it.uuid}")).buildWithStaticHeaders()
        }
    }

    @RequestMapping(
        value = ["/{uuid}"],
        method = [RequestMethod.POST, RequestMethod.PATCH],
        consumes = ["application/offset+octet-stream"]
    )
    fun patchFile(
        @PathVariable uuid: UUID,
        @RequestHeader(name = "Content-Length") contentLength: Long,
        @RequestHeader(name = "Upload-Offset") uploadOffset: Long,
        @RequestHeader(name = "Tus-Resumable", defaultValue = "1.0.0") version: String,
        @RequestBody bytes: ByteArray
    ): Mono<ResponseEntity<Any>> {
        return tusService.patchFile(bytes, uuid, uploadOffset, contentLength).map<ResponseEntity<Any>?> {
            ResponseEntity.noContent().header("Upload-Offset", it.contentOffset.toString())
                .header("Upload-Length", it.fileSize.toString())
                .buildWithStaticHeaders()
        }
    }

    @RequestMapping(method = [RequestMethod.HEAD], value = ["/{uuid}"])
    fun head(
        @PathVariable uuid: UUID,
        @RequestHeader(name = "Tus-Resumable", defaultValue = "1.0.0") version: String
    ): Mono<ResponseEntity<Any>> {
        return tusService.fileHead(uuid).map {
            ResponseEntity.ok().header("Upload-Offset", it.contentOffset.toString())
                .header("Upload-Length", it.fileSize.toString())
                .header("Upload-Metadata", it.metadata)
                .buildWithStaticHeaders()
        }
    }

    @DeleteMapping("/{uuid}")
    fun delete(
        @PathVariable uuid: UUID,
        @RequestHeader(name = "Tus-Resumable") version: String? = "1.0.0"
    ): Mono<ResponseEntity<Any>> =
        tusService.deleteFile(uuid).map { ResponseEntity.noContent().buildWithStaticHeaders() }


    fun <T : HeadersBuilder<*>, K> T.buildWithStaticHeaders(): ResponseEntity<K> =
        this.header("Tus-Resumable", "1.0.0")
            .build()

}