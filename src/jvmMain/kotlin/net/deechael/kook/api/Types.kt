package net.deechael.kook.api

enum class ChannelTypes(val id: Int) {

    TEXT(1), VOICE(2), CATEGORY(0)

}

enum class SlowModeTypes(val time: Long) {

    FIVE_SEC(5000),
    TEN_SEC(10000),
    FIFTEEN_SEC(15000),
    THIRTY_SEC(30000),
    ONE_MIN(60000),
    TWO_MIN(120000),
    FIVE_MIN(300000),
    TEN_MIN(600000),
    FIFTEEN_MIN(900000),
    THIRTY_MIN(1800000),
    ONE_HOUR(3600000),
    TWO_HOUR(7200000),
    SIX_HOUR(21600000)

}