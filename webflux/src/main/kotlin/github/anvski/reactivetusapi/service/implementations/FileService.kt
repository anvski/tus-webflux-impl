package github.anvski.reactivetusapi.service.implementations

import github.anvski.reactivetusapi.model.File
import github.anvski.reactivetusapi.repository.FileRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.io.FileNotFoundException
import java.util.*

@Service
class FileService(private val repository: FileRepository) {

    fun saveFile(file: File) = repository.save(file)

    fun findFileByUuid(uuid: UUID) = repository.findByUuidEquals(uuid).switchIfEmpty {
        Mono.error(FileNotFoundException("File not found"))
    }

    fun deleteFile(file: File) = repository.delete(file).then(Mono.fromCallable { file })

    fun updateFile(
        fileSize: Long? = null, writtenBytes: Long,
        name: String? = null, mimeType: String? = null, file: File
    ): Mono<File> {
        val fileToSave = file.copy(
            fileSize = fileSize ?: file.fileSize,
            contentOffset = file.contentOffset + writtenBytes,
            name = name ?: file.name,
            mimeType = mimeType ?: file.mimeType
        )
        return repository.save(fileToSave)
    }
}