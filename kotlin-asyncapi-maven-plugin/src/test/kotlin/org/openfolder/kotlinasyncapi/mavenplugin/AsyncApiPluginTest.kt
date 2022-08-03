package org.openfolder.kotlinasyncapi.mavenplugin

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.apache.maven.project.MavenProject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openfolder.kotlinasyncapi.model.AsyncApi
import java.io.File

@ExtendWith(MockKExtension::class)
internal class AsyncApiPluginTest {

    @MockK
    private lateinit var scriptRunner: AsyncApiScriptHost

    @MockK
    private lateinit var fileWriter: AsyncApiJsonWriter

    @RelaxedMockK
    private lateinit var mavenProject: MavenProject

    @InjectMockKs
    private lateinit var plugin: AsyncApiPlugin

    @Test
    fun `should generate resource and write it to the target`() {
        val script = File("test/source")
        val json = File("./target/test/target/asyncapi.json")
        val asyncApiSlot = slot<AsyncApi>()
        val asyncApi = AsyncApi.asyncApi {
            info {
                title("titleValue")
                version("versionValue")
            }
            channels { }
        }

        every {
            scriptRunner.run(any())
        } returns asyncApi
        every {
            fileWriter.write(capture(asyncApiSlot), json)
        } returns Unit

        plugin.apply {
            sourcePath = "test/source"
            targetPath = "test/target"
            project = mavenProject
        }.execute()

        verify {
            scriptRunner.run(script)
            fileWriter.write(asyncApiSlot.captured, json)
            mavenProject.addResource(any())
        }
    }
}
