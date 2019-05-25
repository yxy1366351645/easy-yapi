package com.itangcent.idea.plugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.itangcent.idea.plugin.api.export.DefaultDocParseHelper
import com.itangcent.idea.plugin.api.export.DocParseHelper
import com.itangcent.idea.plugin.api.export.postman.*
import com.itangcent.idea.plugin.settings.SettingBinder
import com.itangcent.idea.psi.RecommendClassRuleConfig
import com.itangcent.intellij.config.ConfigReader
import com.itangcent.intellij.context.ActionContext
import com.itangcent.intellij.extend.guice.singleton
import com.itangcent.intellij.extend.guice.with
import com.itangcent.intellij.file.DefaultLocalFileRepository
import com.itangcent.intellij.file.LocalFileRepository
import com.itangcent.intellij.psi.ClassRuleConfig
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClients

class PostmanExportAction : ApiExportAction("Export Postman") {

    override fun onBuildActionContext(builder: ActionContext.ActionContextBuilder) {
        super.onBuildActionContext(builder)

        builder.bind(LocalFileRepository::class) { it.with(DefaultLocalFileRepository::class).singleton() }
        builder.bind(SettingBinder::class) { it.toInstance(ServiceManager.getService(SettingBinder::class.java)) }
        builder.bind(PostmanApiHelper::class) { it.with(PostmanCachedApiHelper::class).singleton() }
        builder.bind(PostmanApiExporter::class) { it.singleton() }
        builder.bind(PostmanFormatter::class) { it.singleton() }
        builder.bindInstance(HttpClient::class, HttpClients.createDefault())
        builder.bind(DocParseHelper::class) { it.with(DefaultDocParseHelper::class).singleton() }
        builder.bind(ClassRuleConfig::class) { it.with(RecommendClassRuleConfig::class).singleton() }
        builder.bind(ConfigReader::class) { it.with(PostmanConfigReader::class).singleton() }

        builder.bindInstance("file.save.default", "postman.json")
        builder.bindInstance("file.save.last.location.key", "com.itangcent.postman.export.path")
    }

    override fun actionPerformed(actionContext: ActionContext, project: Project?, anActionEvent: AnActionEvent) {
        actionContext.instance(PostmanApiExporter::class).export()
    }

}