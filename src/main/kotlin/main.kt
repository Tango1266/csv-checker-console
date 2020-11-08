import model.CsvValidator
import model.utils.CsvParser

fun main(args: Array<String>) {
//    var csvPath = "src\\assets\\test.csv";
    var csvPath = ""
    if (args.isNullOrEmpty() || args.size < 2) {
        println("Please provide a csv file path with: --csv-path <your/path/file.csv")
        return
    }

    if (args[0].contains("--csv-path")) {
        csvPath = args[1]
        println("\nusing csv: $csvPath")
    }

    val parser = CsvParser(csvPath)
    val csvValidator = CsvValidator(parser = parser)
    val result = csvValidator.perform()
    println(result)
}

