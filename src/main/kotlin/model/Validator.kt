package model

import model.utils.CsvParser

interface Validator {
    val rules: Array<Rule>
    val parser: CsvParser
    fun validate(context: String): List<RuleResult> {
        return rules.map { rule -> rule.evaluate(context) }
    }
}

class RowValidator(override val rules: Array<Rule>, override val parser: CsvParser) : Validator {

}

class CellValidator(override val rules: Array<Rule>, override val parser: CsvParser) : Validator {
}