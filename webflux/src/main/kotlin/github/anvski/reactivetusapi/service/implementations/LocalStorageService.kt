package github.anvski.reactivetusapi.service.implementations

import github.anvski.reactivetusapi.exceptions.CommonException
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import java.awt.image.DataBuffer
import java.awt.image.DataBufferUShort
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.Channel
import java.nio.channels.CompletionHandler
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

@Service
class LocalStorageService {
    fun saveFileToStorage(path: Path): Boolean {
        return File(path.toUri()).createNewFile()
    }

    fun writeBytesToFile(path: Path, bytes: ByteArray, offset: Long?): Mono<Int> {
        return Mono.fromCallable { (AsynchronousFileChannel.open(path, StandardOpenOption.WRITE)) }
            .flatMap { channel ->
                Mono.create { sink ->
                    val byteBuffer = ByteBuffer.wrap(bytes)
                    channel.write(byteBuffer, offset ?: 0, byteBuffer, object : CompletionHandler<Int, ByteBuffer> {
                        override fun completed(result: Int, attachment: ByteBuffer?) {
                            sink.success(result)
                        }

                        override fun failed(exc: Throwable, attachment: ByteBuffer?) {
                            sink.error(exc)
                        }
                    })
                }

            }.subscribeOn(Schedulers.boundedElastic())
    }

    fun deleteFileFromStorage(path: Path) =
        AsynchronousFileChannel.open(path, StandardOpenOption.DELETE_ON_CLOSE).closeChannel()


    fun Channel.closeChannel() {
        try {
            this.close()
        } catch (exception: IOException) {
            throw CommonException()
        }
    }

}