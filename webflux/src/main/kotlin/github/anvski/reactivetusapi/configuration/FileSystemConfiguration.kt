package github.anvski.reactivetusapi.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "upload")
class FileSystemConfiguration(
    val directory: String,
    val stagingDirectory: String,
    val fileTypesToDirectory: Map<String, String>,
    val mimeTypesToExtensions: Map<String, String>
) {
}