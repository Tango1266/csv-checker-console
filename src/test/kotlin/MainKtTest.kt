import model.*
import model.utils.CsvParser
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*

internal class MainKtTest {

    private lateinit var parser: CsvParser

    @BeforeEach
    fun init() {
        parser = object : CsvParser("") {
            override fun readFile(): String {
                return csv
            }
        }
    }

    @Test
    fun wordRulesTest () {
        // word rules
        //todo: ((?<!\$!)(?<!\$))(?<!\.)(\b\w+)(?=\.\w)
        var rule = Rule(errMsg = "Expr. starts with # and uses variables without $",
            patternRegEx = "((?<!\\\$!)(?<!\\\$))(?<!\\.)(\\b\\w+)(?=\\.\\w)",
            evalCondition = {rule -> rule.evalContext.startsWith("#")},
            evalMatches = { matches -> matches.count() <= 0 })
        var result = rule.evaluate(text = "#'\$!object.attr' == 'object.attr' || '\$!object.attr1.attr2' == '\$object.attr1.attr2'")
        assertTrue(result.isFulfilled)

        // word rules
        rule = Rule(errMsg = "Value starts with / (constant) but uses variables",
            patternRegEx = "(?<=\\\$)!?\\b\\w+(?=\\.\\w)",
            evalCondition = {rule -> rule.evalContext.startsWith("/")},
            evalMatches = { matches -> matches.count() <= 0 },
            severity= "WARN")
        result = rule.evaluate(text = "/constantValue")
        assertTrue(result.isFulfilled)
    }

     @Test
    fun integrationTest() {

        // row rules
        var rule = Rule(patternRegEx = "((?<=.)${parser.config.delimiter})+",
                errMsg = "invalid delimiter - should be ${parser.config.delimiter}",
                evalMatches = { matches -> matches.count() > 0 })

        var result = rule.evaluate(text = "valid_field_name;#'fieldValue'")
        assert(result.isFulfilled)

        rule = Rule(patternRegEx = "((?<=.)${parser.config.delimiter})+",
                errMsg = "invalid number of delimiter - at least two columns required",
                evalMatches = { matches -> matches.count() >= 1 })
        result = rule.evaluate(text = "valid_field_name;#'fieldValue'")
        assertTrue(result.isFulfilled)

        rule = Rule(patternRegEx = "\"",
                errMsg = "double quotes are not allowed",
                evalMatches = { matches -> matches.count() <= 0 })
        result = rule.evaluate(text = "invalid_field_value;\"some text in double quoates\"")
        assertFalse(result.isFulfilled)

        rule = Rule(patternRegEx = "[\\n\\r]",
                errMsg = "line breaks are not allowed",
                evalMatches = { matches -> matches.count() <= 0 })
        result = rule.evaluate(text = """invalid_field_value;"some text in double quoates with linebreak
            """)
        assertFalse(result.isFulfilled, rule.errMsg)
    }
}


val csv = """valid_field_name;field value
valid_field_name;#'fieldValue'
valid_field_name;#'fieldValue', '%fieldValue1' == '1' && '%fieldValue1' != 'A' || '%fieldValue1' == '%var1', %boolVar
valid_field_name;/constantValue
invalid_field_value;"some text in double quoates"
invalid_field_value;"some text in double quoates with linebreak
"""".replace("%", "$")
