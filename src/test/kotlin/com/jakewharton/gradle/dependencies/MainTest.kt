package com.jakewharton.gradle.dependencies

import org.junit.Test

class MainTest {

	@Test
	fun helpDoesNotExitProcess() {
		// Bare flag.
		main("-h")
		main("--help")

		// Flag with valid arguments.
		main("-h", "diff", "old.txt", "new.txt")
		main("--help", "diff", "old.txt", "new.txt")

		// Flag with too few arguments.
		main("-h", "old.txt")
		main("--help", "old.txt")
		main("old.txt", "-h")
		main("old.txt", "--help")

		// Flag with too many arguments.
		main("-h", "old.txt", "new.txt", "wat.txt")
		main("--help", "old.txt", "new.txt", "wat.txt")
		main("old.txt", "-h", "new.txt", "wat.txt")
		main("old.txt", "--help", "new.txt", "wat.txt")
		main("old.txt", "new.txt", "-h", "wat.txt")
		main("old.txt", "new.txt", "--help", "wat.txt")
		main("old.txt", "new.txt", "wat.txt", "-h")
		main("old.txt", "new.txt", "wat.txt", "--help")
	}

	@Test
	fun correctArgsDoesNotExitProcess() {
		val new = "src/test/main/new.txt"
		val old = "src/test/main/old.txt"

		// diff commands
		main("diff", old, new)
		main("diff", "-f", old, new)
		main("diff", "--flatten", old, new)

		// diff flatten
		main("flatten", old)
	}
}
