package com.github.getcurrentthread.recursivemarkdowngenerator

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@Service(Service.Level.PROJECT)
@State(
    name = "RecursiveMarkdownGeneratorSettings",
    storages = [Storage("RecursiveMarkdownGeneratorSettings.xml")]
)
class RecursiveMarkdownGeneratorSettings : PersistentStateComponent<RecursiveMarkdownGeneratorSettings> {
    var ignorePatterns: MutableList<String> = mutableListOf(
        "*.iml", ".idea/*", "out/*", "build/*", ".gradle/*",
        "*.class", "*.jar", "*.war", "*.ear", "*.zip", "*.tar.gz", "*.rar",
        "*.log", "*.sql", "*.sqlite", "*.db",
        "node_modules/*", "npm-debug.log", "yarn-debug.log", "yarn-error.log",
        ".DS_Store", "Thumbs.db", "generated_markdown.md"
    )
    var ignoreFiles: MutableList<String> = mutableListOf(".gitignore", ".npmignore", ".dockerignore")
    var defaultPath: String = "generated_markdown.md"

    override fun getState(): RecursiveMarkdownGeneratorSettings = this

    override fun loadState(state: RecursiveMarkdownGeneratorSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(project: Project): RecursiveMarkdownGeneratorSettings =
            project.getService(RecursiveMarkdownGeneratorSettings::class.java)
    }
}