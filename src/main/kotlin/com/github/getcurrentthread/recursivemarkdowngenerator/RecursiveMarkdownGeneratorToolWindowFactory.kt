package com.github.getcurrentthread.recursivemarkdowngenerator

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


class RecursiveMarkdownGeneratorToolWindowFactory : ToolWindowFactory {
    companion object {
        val KEY = Key<RecursiveMarkdownGeneratorToolWindow>("RecursiveMarkdownGeneratorToolWindow")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val toolWindowContent = RecursiveMarkdownGeneratorToolWindow(project)
        val content = ContentFactory.getInstance().createContent(toolWindowContent.component, "", false)
        content.putUserData(KEY, toolWindowContent)
        toolWindow.contentManager.addContent(content)
    }
}