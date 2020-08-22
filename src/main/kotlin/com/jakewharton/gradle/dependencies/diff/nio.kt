package com.jakewharton.gradle.dependencies.diff

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

internal fun Path.readText(charset: Charset = StandardCharsets.UTF_8): String {
	return Files.readAllBytes(this).toString(charset)
}
