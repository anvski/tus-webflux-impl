package github.anvski.reactivetusapi.controller.dto

import java.util.UUID

data class FileRequestDto(
    val fileUuid: UUID,
    val fileRequestData: FileRequestData
)