package model

import model.utils.CsvParser

open class CsvValidator(val parser: CsvParser) {

    private val debug: Boolean = false
    private var rowValidator: Validator
    private var cellValidator: Validator

    init {
        val validatorFactory = ValidatorFactory(parser)
        this.cellValidator = validatorFactory.createCellValidator()
        this.rowValidator = validatorFactory.createRowValidator()
    }

    fun perform(): List<RuleResult> {
        val csvString = parser.readFile().trim()
        val rows = csvString.lines()
        val cells = rows.map {it.split(parser.config.delimiter)}.flatten()

        val results = mutableListOf<RuleResult>()
        rows.forEach { results.addAll(rowValidator.validate(it)) }
        cells.forEach { results.addAll(cellValidator.validate(it)) }

        results.forEach {
            if(it.isFulfilled && this.debug)
                println("processed: ${it.evalContext}")
            if(!it.isFulfilled)
                println("${it.severity}: ${it.errorMsg} \n\t\t ${it.evalContext}")
        }

        return results.filter { !it.isFulfilled }
    }

}
