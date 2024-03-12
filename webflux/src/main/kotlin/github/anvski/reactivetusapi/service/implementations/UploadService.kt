package github.anvski.reactivetusapi.service.implementations

import github.anvski.reactivetusapi.configuration.FileSystemConfiguration
import github.anvski.reactivetusapi.exceptions.BadRequestException
import github.anvski.reactivetusapi.exceptions.CommonException
import github.anvski.reactivetusapi.model.File
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.nio.file.Paths
import java.util.*
import kotlin.coroutines.CoroutineContext

@Service
class UploadService(
    private val fileService: FileService,
    private val localStorageService: LocalStorageService,
    private val configuration: FileSystemConfiguration
) {
    fun uploadFile(fileSize: Long, mimeType: String, contentOffset: Long, name: String, metadata: String)
            : Mono<File> {
        return Mono.fromCallable {
            val fileName = "$name${configuration.mimeTypesToExtensions[mimeType]}"
            configuration.fileTypesToDirectory[mimeType]?.let {
                localStorageService.saveFileToStorage(
                    Paths.get(it, fileName)
                )
                return@let fileName
            } ?: throw BadRequestException("Invalid mime type")
        }.flatMap {
            fileService.saveFile(
                File(
                    fileSize = fileSize,
                    mimeType = mimeType,
                    contentOffset = contentOffset,
                    name = it,
                    metadata = metadata
                )
            )
        }
    }

    fun updateFileContent(bytes: ByteArray, uuid: UUID, offset: Long, contentLength: Long): Mono<File> {
        val cachedFileMono = fileService.findFileByUuid(uuid).filter { it.contentOffset == offset }
            .switchIfEmpty(Mono.error(BadRequestException("Invalid [offset] supplied"))).cache()

        return cachedFileMono.flatMap {
            val directory = configuration.fileTypesToDirectory[it.mimeType]
                ?: return@flatMap Mono.error(CommonException())
            val path = Paths.get(directory, it.name)
            localStorageService.writeBytesToFile(
                path = path,
                bytes = bytes,
                offset = offset
            )
        }.zipWith(cachedFileMono).flatMap { fileService.updateFile(writtenBytes = it.t1.toLong(), file = it.t2) }
    }

    fun findFileByUuid(uuid: UUID): Mono<File> {
        return fileService.findFileByUuid(uuid)
    }

    suspend fun deleteFileByUuid(uuid: UUID, context: CoroutineContext): Boolean =
        withContext(context) {
            val file = fileService.findFileByUuid(uuid).awaitFirst()
            val directory = configuration.fileTypesToDirectory[file.mimeType] ?: throw CommonException()
            localStorageService.deleteFileFromStorage(Paths.get(directory, file.name))
            fileService.deleteFile(file).awaitFirst()
            true
        }

}