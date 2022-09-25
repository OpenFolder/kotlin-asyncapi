package org.openfolder.kotlinasyncapi.springweb

import org.openfolder.kotlinasyncapi.model.AsyncApi
import org.openfolder.kotlinasyncapi.springweb.context.DefaultInfoProvider
import org.openfolder.kotlinasyncapi.springweb.context.DefaultResourceProvider
import org.openfolder.kotlinasyncapi.springweb.context.ResourceProvider
import org.openfolder.kotlinasyncapi.springweb.controller.AsyncApiController
import org.openfolder.kotlinasyncapi.springweb.service.AsyncApiExtension
import org.openfolder.kotlinasyncapi.springweb.service.AsyncApiSerializer
import org.openfolder.kotlinasyncapi.springweb.service.AsyncApiService
import org.openfolder.kotlinasyncapi.springweb.service.DefaultAsyncApiSerializer
import org.openfolder.kotlinasyncapi.springweb.service.DefaultAsyncApiService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

@Configuration
@ConditionalOnBean(AsyncApiMarkerConfiguration.Marker::class)
@Import(AsyncApiScriptAutoConfiguration::class)
internal open class AsyncApiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    open fun asyncApiSerializer(): AsyncApiSerializer =
        DefaultAsyncApiSerializer()

    @Bean
    open fun infoProvider(context: ApplicationContext) =
        DefaultInfoProvider(context)

    @Bean
    @ConfigurationProperties(prefix = "asyncapi", ignoreUnknownFields = false)
    open fun asyncApiProperties() =
        AsyncApiProperties()

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

@Configuration
@ConditionalOnProperty(name = ["asyncapi.script.enabled"], havingValue = "true", matchIfMissing = true)
@Import(AsyncApiEmbeddedScriptAutoConfiguration::class)
internal open class AsyncApiScriptAutoConfiguration {

    @Bean
    open fun resourceProvider(context: ApplicationContext) =
        DefaultResourceProvider(context)

    @Bean
    open fun asyncApiResourceExtension(resourceProvider: ResourceProvider, asyncApiProperties: AsyncApiProperties) =
        resourceProvider.resource(asyncApiProperties.script.resourcePath, AsyncApi::class)?.let {
            AsyncApiExtension.from(resource = it)
        }
}

@Configuration
@ConditionalOnClass(BasicJvmScriptingHost::class)
internal open class AsyncApiEmbeddedScriptAutoConfiguration {

    @Bean
    open fun asyncApiScriptExtension(resourceProvider: ResourceProvider, asyncApiProperties: AsyncApiProperties) =
        resourceProvider.resource(asyncApiProperties.script.sourcePath)?.let {
            AsyncApiExtension.from(
                script = it.inputStream.bufferedReader().use { it.readText() }.toScriptSource()
            )
        }
}

@Configuration
internal open class AsyncApiMarkerConfiguration {

    @Bean
    open fun asyncApiMarkerBean(): Marker {
        return Marker()
    }

    class Marker
}
