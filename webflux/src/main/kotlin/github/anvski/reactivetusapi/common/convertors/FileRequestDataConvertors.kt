package github.anvski.reactivetusapi.common.convertors

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import github.anvski.reactivetusapi.controller.dto.FileRequestData
import io.r2dbc.postgresql.codec.Json
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@ReadingConverter
class FileRequestDataReadingConvertor : Converter<Json, FileRequestData> {
    override fun convert(source: Json): FileRequestData {
        return jacksonObjectMapper().readValue(source.asArray(), FileRequestData::class.java)
    }
}

@WritingConverter
class FileRequestDataWritingConvertor : Converter<FileRequestData, Json> {
    override fun convert(source: FileRequestData): Json {
        val byteArray = jacksonObjectMapper().writeValueAsBytes(source)
        return Json.of(byteArray)
    }
}