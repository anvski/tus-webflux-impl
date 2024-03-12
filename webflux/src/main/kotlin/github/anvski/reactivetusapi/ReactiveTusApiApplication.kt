package github.anvski.reactivetusapi

import github.anvski.reactivetusapi.configuration.FileSystemConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories
@EnableConfigurationProperties(FileSystemConfiguration::class)
@ConfigurationPropertiesScan
class ReactiveTusApiApplication

fun main(args: Array<String>) {
    runApplication<ReactiveTusApiApplication>(*args)
}
