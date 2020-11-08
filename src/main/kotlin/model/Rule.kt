package model

data class RuleResult(val isFulfilled: Boolean, val errorMsg: String, val evalContext: String, val severity: String) {

    override fun toString(): String {
        return """
    isFulfilled: $isFulfilled
        * errorMsg: $errorMsg
        * evalContext: $evalContext
        * severity: $severity"""
    }
}

class Rule(
    val patternRegEx: String,
    val errMsg: String,
    val expMatches: Pair<Any?, Any?> = Pair(null, null),
    val evalMatch: (MatchResult) -> Boolean = { true },
    val evalMatches: (Sequence<MatchResult>) -> Boolean = { true },
    val evalCondition: (Rule) -> Boolean = { true },
    val severity: String = "ERROR"
) {
    lateinit var evalContext: String
    private val regex = Regex(patternRegEx, RegexOption.DOT_MATCHES_ALL)

    fun evaluate(text: String): RuleResult {
        this.evalContext = text

        if (!this.evalCondition(this))
            return RuleResult(true, "", text, severity)

        val matches = regex.findAll(text)
        var isFulfilled = true

        val (min, max) = expMatches
        if (min != null && max != null)
            isFulfilled = isFulfilled(matches)

        val isFulfilledForMatches = evalMatches(matches)
        var isFulfilledForMatch = true
        if (matches.count() > 0)
            isFulfilledForMatch = matches.map { match -> evalMatch(match) }.reduce { acc, b -> acc && b }

        isFulfilled = isFulfilled && isFulfilledForMatch && isFulfilledForMatches

        return RuleResult(isFulfilled, errMsg, text, severity)
    }


    private fun isFulfilled(matches: Sequence<MatchResult>): Boolean {
        val (min, max) = expMatches
        var minSuccess = true
        var maxSuccess = true

        if (min != null) {
            when (min) {
                is Int -> minSuccess = min <= matches.count()
            }
        }

        if (max != null) {
            when (max) {
                is Int -> maxSuccess = max >= matches.count()
            }
        }

        return minSuccess && maxSuccess
    }

}
