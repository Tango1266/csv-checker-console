package model

import model.utils.CsvParser

class ValidatorFactory(val parser: CsvParser) {
    fun createRowValidator(): Validator {
        val rules: Array<Rule> = createRowRules()
        return RowValidator(rules, parser)
    }

    fun createCellValidator(): Validator {
        val rules: Array<Rule> = createCellRules()
        return CellValidator(rules, parser)
    }

    private fun createCellRules(): Array<Rule> {
        return arrayOf(
            Rule(errMsg = "Expr. starts with # and uses variables without $",
                patternRegEx = "(?<!\\\$!)(?<!\\\$)\\b\\w+(?=\\.\\w)",
                evalCondition = { rule -> rule.evalContext.startsWith("#") },
                evalMatches = { matches -> matches.count() <= 0 }),
            Rule(
                errMsg = "Value starts with / (constant) but uses variables",
                patternRegEx = "(?<=\\\$)!?\\b\\w+(?=\\.\\w)",
                evalCondition = { rule -> rule.evalContext.startsWith("/") },
                evalMatches = { matches -> matches.count() <= 0 },
                severity = "WARN"
            )
        )
    }

    private fun createRowRules(): Array<Rule> {
        return arrayOf(
            Rule(patternRegEx = "((?<=.)${parser.config.delimiter})+",
                errMsg = "invalid delimiter - should be ${parser.config.delimiter}",
                evalMatches = { matches -> matches.count() > 0 }),
            Rule(patternRegEx = "((?<=.)${parser.config.delimiter})+",
                errMsg = "invalid number of delimiter - at least two columns required",
                evalMatches = { matches -> matches.count() >= 1 }),
            Rule(patternRegEx = "\"",
                errMsg = "double quotes are not allowed",
                evalMatches = { matches -> matches.count() <= 0 }),
            Rule(patternRegEx = "[\\n\\r]",
                errMsg = "line breaks are not allowed",
                evalMatches = { matches -> matches.count() <= 0 })
        )
    }
}
