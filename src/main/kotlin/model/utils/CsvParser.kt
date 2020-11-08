package model.utils

import java.io.File
import java.nio.file.Paths

data class Config(var delimiter: String = ";")

open class CsvParser(
        private val csvPath: String,
        var config: Config = Config()
) {
    open fun readFile(): String {
        val csvFile = Paths.get(csvPath)

        val file = File(csvFile.toAbsolutePath().toString())
        val reader = file.reader()
        return reader.readText()
    }

}

