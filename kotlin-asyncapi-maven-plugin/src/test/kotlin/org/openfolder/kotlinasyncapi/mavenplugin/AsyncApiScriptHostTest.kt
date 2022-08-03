package org.openfolder.kotlinasyncapi.mavenplugin

import org.junit.jupiter.api.Test
import org.openfolder.kotlinasyncapi.mavenplugin.TestUtils.assertJsonEquals
import org.openfolder.kotlinasyncapi.mavenplugin.TestUtils.json
import java.io.File

internal class AsyncApiScriptHostTest {

    @Test
    fun `should evaluate script`() {
        val scriptRunner = AsyncApiScriptHost()
        val script = File("src/test/resources/build.asyncapi.kts")

        val asyncApi = scriptRunner.run(script)

        val expected = json("asyncapi.json")
        val actual = json(asyncApi)

        assertJsonEquals(expected, actual)
    }
}
