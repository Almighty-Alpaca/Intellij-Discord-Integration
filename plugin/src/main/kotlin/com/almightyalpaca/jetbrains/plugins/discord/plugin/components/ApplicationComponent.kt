package com.almightyalpaca.jetbrains.plugins.discord.plugin.components

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.ApplicationData
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.ApplicationDataBuilder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Application
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Source
import com.intellij.openapi.components.BaseComponent

interface ApplicationComponent : BaseComponent {
    val source: Source

    val data: ApplicationData

    fun app(builder: ApplicationDataBuilder.() -> Unit)

    fun update()

    companion object {
        val instance: ApplicationComponent by lazy { Application.instance.getComponent(ApplicationComponent::class.java) }
    }
}