package github.anvski.reactivetusapi.model

import github.anvski.reactivetusapi.controller.dto.FileRequestData
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "file_requests", schema = "main")
data class FileRequest(
    @Id
    var id: Long? = null,
    @Column(value = "file_id")
    val fileId: Long,
    @Column(value = "request_data")
    val requestData: FileRequestData,
    @Column(value = "user_id")
    val userId: Long
)