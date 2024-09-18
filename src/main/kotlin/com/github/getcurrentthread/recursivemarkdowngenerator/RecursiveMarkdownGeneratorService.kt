package com.github.getcurrentthread.recursivemarkdowngenerator

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.util.data.MutableDataSet
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.File
import java.util.*
import com.vladsch.flexmark.html.HtmlRenderer as FlexmarkHtmlRenderer
import com.vladsch.flexmark.parser.Parser as FlexmarkParser

@Service
class RecursiveMarkdownGeneratorService(private val project: Project) {
    private val parser = Parser.builder().build()
    private val renderer = HtmlRenderer.builder().build()

    private val flexmarkOptions = MutableDataSet().apply {
        set(FlexmarkParser.EXTENSIONS, listOf(TablesExtension.create(), StrikethroughExtension.create(), AutolinkExtension.create()))
    }
    private val flexmarkParser = FlexmarkParser.builder(flexmarkOptions).build()
    private val flexmarkRenderer = FlexmarkHtmlRenderer.builder(flexmarkOptions).build()

    fun generateMarkdown(): String {
        val projectDir = project.basePath ?: return ""
        val markdownContent = StringBuilder()

        ApplicationManager.getApplication().invokeAndWait {
            ApplicationManager.getApplication().runReadAction {
                VirtualFileManager.getInstance().refreshWithoutFileWatcher(false)
                VirtualFileManager.getInstance().findFileByNioPath(File(projectDir).toPath())?.refresh(false, true)
            }
        }

        val settings = RecursiveMarkdownGeneratorSettings.getInstance(project)
        processDirectory(File(projectDir), markdownContent, settings.ignorePatterns.toSet(), settings.ignoreFiles.toSet())

        return markdownContent.toString()
    }

    fun renderMarkdownToHtml(markdown: String): String {
        val document = flexmarkParser.parse(markdown)
        return flexmarkRenderer.render(document)
    }

    private fun processDirectory(dir: File, markdownContent: StringBuilder, ignorePatterns: Set<String>, ignoreFiles: Set<String>) {
        dir.listFiles()?.forEach { file ->
            val relativePath = file.relativeTo(File(project.basePath!!)).path.replace(File.separator, "/")
            if (shouldIgnore(relativePath, ignorePatterns, ignoreFiles)) {
                return@forEach
            }

            if (file.isDirectory) {
                processDirectory(file, markdownContent, ignorePatterns, ignoreFiles)
            } else {
                markdownContent.append("### ${relativePath}\n")
                val fence = createUniqueFence(file.readText())
                markdownContent.append("$fence${file.extension.lowercase(Locale.getDefault())}\n")
                markdownContent.append(file.readText().trim())
                markdownContent.append("\n$fence\n\n")
            }
        }
    }

    private fun shouldIgnore(path: String, ignorePatterns: Set<String>, ignoreFiles: Set<String>): Boolean {
        return ignorePatterns.any { pattern ->
            path.matches(Regex(pattern.replace(".", "\\.").replace("*", ".*")))
        } || ignoreFiles.contains(path.substringAfterLast('/'))
    }

    private fun createUniqueFence(content: String): String {
        val backtickGroups = content.split("`+".toRegex())
        val maxBackticks = backtickGroups.map { it.length }.maxOrNull() ?: 0
        return "`".repeat(maxBackticks + 1)
    }
}