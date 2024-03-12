package github.anvski.reactivetusapi.configuration

import github.anvski.reactivetusapi.common.convertors.FileRequestDataReadingConvertor
import github.anvski.reactivetusapi.common.convertors.FileRequestDataWritingConvertor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.PostgresDialect


@Configuration
class R2dbcConfiguration {
    @Bean
    fun r2dbcCustomConversions(): R2dbcCustomConversions {
        return R2dbcCustomConversions.of(
            PostgresDialect.INSTANCE,
            FileRequestDataWritingConvertor(), FileRequestDataReadingConvertor()
        )
    }
}