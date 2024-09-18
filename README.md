# Recursive Markdown Generator

Recursive Markdown Generator is an IntelliJ-based IDE plugin that allows you to generate a single Markdown file containing the content of all files in your project directory, recursively.

## Features

- Recursively scan your project directory
- Generate a single Markdown file with the content of all files
- Customizable ignore patterns and files
- Preview generated Markdown in HTML format
- Download generated Markdown file

## Installation

1. Open your IntelliJ-based IDE (IntelliJ IDEA, PyCharm, WebStorm, etc.)
2. Go to `Settings/Preferences > Plugins > Marketplace`
3. Search for "Recursive Markdown Generator"
4. Click "Install"
5. Restart your IDE when prompted

## Usage

1. Open your project in the IDE
2. Go to `Tools > Generate Recursive Markdown` or use the tool window on the right side of the IDE
3. Click "Generate" to create the Markdown content
4. Use the "Preview" tab to see the generated content in HTML format
5. Click "Download" to save the generated Markdown file

## Configuration

You can configure the plugin in the "Settings" tab of the tool window:

- **Output File Name**: Set the name of the generated Markdown file
- **Ignore Patterns**: Add patterns to ignore specific files or directories
- **Ignore Files**: Add specific filenames to ignore

## Building from Source

To build the plugin from source:

1. Clone the repository:
   ```
   git clone https://github.com/getCurrentThread/recursive-markdown-generator-for-intellij.git
   ```
2. Open the project in IntelliJ IDEA
3. Run `./gradlew buildPlugin`
4. The built plugin will be in `build/distributions/`

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

If you encounter any problems or have any suggestions, please [open an issue](https://github.com/getCurrentThread/recursive-markdown-generator-for-intellij/issues) on GitHub.
