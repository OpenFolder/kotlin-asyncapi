package org.openfolder.kotlinasyncapi.springweb

import org.openfolder.kotlinasyncapi.springweb.controller.AsyncApiController
import org.openfolder.kotlinasyncapi.springweb.service.AsyncApiExtension
import org.openfolder.kotlinasyncapi.springweb.service.AsyncApiSerializer
import org.openfolder.kotlinasyncapi.springweb.service.AsyncApiService
import org.openfolder.kotlinasyncapi.springweb.service.DefaultAsyncApiSerializer
import org.openfolder.kotlinasyncapi.springweb.service.DefaultAsyncApiService
import org.openfolder.kotlinasyncapi.springweb.service.DefaultInfoProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(AsyncApiMarkerConfiguration.Marker::class)
internal open class AsyncApiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    open fun asyncApiSerializer(): AsyncApiSerializer =
        DefaultAsyncApiSerializer()

    @Bean
    open fun infoProvider(context: ApplicationContext) =
        DefaultInfoProvider(context)

    @Bean
    open fun asyncApiDefaultInfoExtension(infoProvider: DefaultInfoProvider) =
        AsyncApiExtension.builder(order = -1) {
            info {
                infoProvider.title?.let { title(it) }
                infoProvider.version?.let { version(it) }
            }
        }

    @Bean
    open fun asyncApiDefaultChannelsExtension() =
        AsyncApiExtension.builder(order = -1) {
            channels { }
        }

    @Bean
    open fun asyncApiService(extensions: List<AsyncApiExtension>): AsyncApiService =
        DefaultAsyncApiService(extensions)

    @Bean
    open fun asyncApiController(service: AsyncApiService, serializer: AsyncApiSerializer): AsyncApiController =
        AsyncApiController(service, serializer)
}
