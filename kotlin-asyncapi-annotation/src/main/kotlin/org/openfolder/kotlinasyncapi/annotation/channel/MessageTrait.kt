package org.openfolder.kotlinasyncapi.annotation.channel

import org.openfolder.kotlinasyncapi.annotation.CorrelationID
import org.openfolder.kotlinasyncapi.annotation.ExternalDocumentation
import org.openfolder.kotlinasyncapi.annotation.Schema
import org.openfolder.kotlinasyncapi.annotation.Tag

annotation class MessageTrait(
    val isDefault: Boolean = false,
    val messageId: String = "",
    val schemaFormat: String = "",
    val contentType: String = "",
    val name: String = "",
    val title: String = "",
    val summary: String = "",
    val description: String = "",
    val headers: Schema = Schema(isDefault = true),
    val correlationId: CorrelationID = CorrelationID(isDefault = true, location = ""),
    val tags: Array<Tag> = [],
    val externalDocs: ExternalDocumentation = ExternalDocumentation(isDefault = true, url = ""),
    val examples: Array<MessageExample> = [],
    val bindings: MessageBindings = MessageBindings(isDefault = true)
)
