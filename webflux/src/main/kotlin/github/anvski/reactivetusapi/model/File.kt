package github.anvski.reactivetusapi.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table(name = "file", schema = "main")
data class File(
    @Id
    var id: Long? = null,
    @Column(value = "file_size")
    val fileSize: Long,
    @Column(value = "mime_type")
    val mimeType: String,
    @Column(value = "content_offset")
    val contentOffset: Long,
    val name: String,
    val uuid: UUID = UUID.randomUUID(),
    @Column(value = "meta_data")
    val metadata: String? = null
)