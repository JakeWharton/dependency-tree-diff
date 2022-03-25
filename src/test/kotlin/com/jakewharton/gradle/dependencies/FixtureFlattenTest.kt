package com.jakewharton.gradle.dependencies

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.io.File

@RunWith(Parameterized::class)
class FixtureFlattenTest(private val fixtureDir: File) {
	@Test fun runNew() {
		val new = fixtureDir.resolve("new.txt").readText()
		val expected = fixtureDir.resolve("expectedFlattenNew.txt").readText()
		val actual = flatDependencies(new)
		assertThat(actual.trim()).isEqualTo(expected.trim())
	}

	@Test fun runOld() {
		val old = fixtureDir.resolve("old.txt").readText()
		val expected = fixtureDir.resolve("expectedFlattenOld.txt").readText()
		val actual = flatDependencies(old)
		assertThat(actual.trim()).isEqualTo(expected.trim())
	}

	companion object {
		@JvmStatic
		@Parameters(name = "{0}")
		fun params() = File("src/test/fixtures")
			.listFiles { file -> file.isDirectory }
	}
}
