package github.anvski.reactivetusapi.controller.dto

class TusUploadStartRequest(
    val fileSize: Long,
    val version: String,
    val metadata: String,
    val mimeType: String,
)