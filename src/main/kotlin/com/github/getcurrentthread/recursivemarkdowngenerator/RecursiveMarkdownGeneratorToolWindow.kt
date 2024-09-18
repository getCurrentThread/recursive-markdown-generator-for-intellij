package com.github.getcurrentthread.recursivemarkdowngenerator

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.io.File
import javax.swing.*

class RecursiveMarkdownGeneratorToolWindow(private val project: Project) {
    private var htmlBrowser: JBCefBrowser = JBCefBrowser()
    private val service: RecursiveMarkdownGeneratorService = project.getService(RecursiveMarkdownGeneratorService::class.java)
    private val settings: RecursiveMarkdownGeneratorSettings? = project.getService(RecursiveMarkdownGeneratorSettings::class.java)

    private lateinit var fileNameField: Cell<JTextField>
    private lateinit var ignorePatternsArea: Cell<JTextArea>
    private lateinit var ignoreFilesArea: Cell<JTextArea>
    private lateinit var tabbedPane: JTabbedPane

    val component: JBPanel<*> = JBPanel<JBPanel<*>>(BorderLayout()).apply {
        tabbedPane = JTabbedPane()

        val previewPanel = JPanel(BorderLayout()).apply {
            val controlPanel = panel {
                row {
                    button("Generate") {
                        updateContent()
                    }
                    button("Download") {
                        downloadMarkdown()
                    }
                }
            }
            add(controlPanel, BorderLayout.NORTH)
            add(htmlBrowser.component, BorderLayout.CENTER)
        }

        val settingsPanel = panel {
            group("General Settings") {
                row("Output File Name:") {
                    fileNameField = textField()
                        .text(settings?.defaultPath ?: "generated_markdown.md")
                        .align(AlignX.FILL)
                }
            }

            group("Ignore Patterns") {
                row {
                    ignorePatternsArea = textArea()
                        .rows(10)
                        .align(AlignX.FILL)
                        .apply {
                            component.text = settings?.ignorePatterns?.joinToString("\n") ?: ""
                        }
                }
                row {
                    label("Enter patterns to ignore, one per line")
                        .applyToComponent { foreground = JBUI.CurrentTheme.ContextHelp.FOREGROUND }
                }
            }

            group("Ignore Files") {
                row {
                    ignoreFilesArea = textArea()
                        .rows(10)
                        .align(AlignX.FILL)
                        .apply {
                            component.text = settings?.ignoreFiles?.joinToString("\n") ?: ""
                        }
                }
                row {
                    label("Enter filenames to ignore, one per line")
                        .applyToComponent { foreground = JBUI.CurrentTheme.ContextHelp.FOREGROUND }
                }
            }

            row {
                button("Save Settings") {
                    saveSettings()
                }.align(AlignX.RIGHT)
            }
        }.apply {
            border = JBUI.Borders.empty(10)
        }

        tabbedPane.addTab("Preview", previewPanel)
        tabbedPane.addTab("Settings", JBScrollPane(settingsPanel))

        add(tabbedPane, BorderLayout.CENTER)

        // Initialize content when the tool window is created
        ApplicationManager.getApplication().invokeLater {
            initializeContent()
        }
    }

    private fun initializeContent() {
        updateContent()
    }

    private fun updateContent() {
        ApplicationManager.getApplication().executeOnPooledThread {
            val content = service.generateMarkdown()
            val htmlContent = service.renderMarkdownToHtml(content)
            ApplicationManager.getApplication().invokeLater {
                htmlBrowser.loadHTML(getStyledHtml(htmlContent))
            }
        }
    }

    private fun downloadMarkdown() {
        val fileName = fileNameField.component.text
        if (fileName.isBlank()) {
            JOptionPane.showMessageDialog(component, "Please enter a file name", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        ApplicationManager.getApplication().executeOnPooledThread {
            val content = service.generateMarkdown()
            ApplicationManager.getApplication().invokeLater {
                val file = File(project.basePath, fileName)
                file.writeText(content)
                JOptionPane.showMessageDialog(component, "Markdown saved to ${file.absolutePath}", "Save Successful", JOptionPane.INFORMATION_MESSAGE)
            }
        }
    }

    private fun saveSettings() {
        settings?.let { s ->
            s.defaultPath = fileNameField.component.text
            s.ignorePatterns = ignorePatternsArea.component.text.lines().filter { it.isNotBlank() }.toMutableList()
            s.ignoreFiles = ignoreFilesArea.component.text.lines().filter { it.isNotBlank() }.toMutableList()
            JOptionPane.showMessageDialog(component, "Settings saved successfully", "Save Successful", JOptionPane.INFORMATION_MESSAGE)
            updateContent()  // Update content after saving settings
        } ?: run {
            JOptionPane.showMessageDialog(component, "Failed to save settings", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun getStyledHtml(content: String): String {
        // HTML 스타일링 코드는 이전과 동일하게 유지
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Recursive Markdown Preview</title>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.10.0/styles/atom-one-dark.min.css"/>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.10.0/highlight.min.js"></script>
            <script>hljs.highlightAll();</script>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    line-height: 1.6;
                    color: #abb2bf;
                    background-color: #282c34;
                    max-width: 800px;
                    margin: 0 auto;
                    padding: 20px;
                }
                pre {
                    background-color: #1e2227;
                    border-radius: 5px;
                    padding: 10px;
                    overflow-x: auto;
                }
                code {
                    font-family: 'Courier New', Courier, monospace;
                }
                h3 {
                    color: #ffffff;
                    border-bottom: 1px solid #3e4451;
                    padding-bottom: 5px;
                }
            </style>
        </head>
        <body>
            $content
        </body>
        </html>
        """
    }
}