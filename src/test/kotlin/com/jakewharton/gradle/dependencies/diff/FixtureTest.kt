package com.jakewharton.gradle.dependencies.diff

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.io.File

@RunWith(Parameterized::class)
class FixtureTest(private val fixtureDir: File) {
	@Test fun run() {
		val old = fixtureDir.resolve("old.txt").readText()
		val new = fixtureDir.resolve("new.txt").readText()
		val expected = fixtureDir.resolve("expected.txt").readText()
		val actual = dependencyTreeDiff(old, new)
		assertThat(actual).isEqualTo(expected)
	}

	companion object {
		@JvmStatic
		@Parameters(name = "{0}")
		fun params() = File("src/test/fixtures")
			.listFiles { file -> file.isDirectory }
	}
}
