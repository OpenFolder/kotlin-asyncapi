package org.openfolder.kotlinasyncapi.springweb.context

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openfolder.kotlinasyncapi.model.AsyncApi
import org.openfolder.kotlinasyncapi.springweb.EnableAsyncApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
internal class DefaultResourceProviderTest {

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun `should provide parsed JSON resource`() {
        val resourceProvider = PackageResourceProvider(context, "classpath:async_api_resource.json")

        val expected = AsyncApi.asyncApi {
            info {
                title("titleValue")
                version("versionValue")
            }
            channels { }
        }
        val actual = resourceProvider.asyncApi

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableAsyncApi
    open class TestConfig
}
