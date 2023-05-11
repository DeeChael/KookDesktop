package net.deechael.kook.util

class QueryBuilder private constructor() {

    private val queries: MutableMap<String, String> = mutableMapOf()

    fun with(key: String, any: Any?): QueryBuilder {
        if (any != null) {
            this.queries[key] = any.toString()
        }
        return this
    }

    fun build(): Map<String, String> {
        return this.queries.toMap()
    }

    companion object {

        @JvmStatic
        fun of(): QueryBuilder {
            return QueryBuilder()
        }

    }

}