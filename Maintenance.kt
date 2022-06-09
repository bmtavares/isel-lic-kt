class Maintenance(private val hal:HAL) {
    companion object{
        const val M_KEY_MASK = 0b1000_0000
        const val LINE_TOP = "Maintenance Mode"
        const val LINE_1 = "1-Print ticket"
        const val LINE_2 = "2-Station Cnt."
        const val LINE_3 = "3-Coins Cnt."
        const val LINE_4 = "4-Reset Cnt."
        const val LINE_5 = "5-Shutdown"
    }

    private var line = 1

    fun getMenuLine() =
        when (line++){
            1 -> LINE_1
            2 -> LINE_2
            3 -> LINE_3
            4 -> LINE_4
            else -> {
                line = 1
                LINE_5
            }
        }

    fun isMaintenanceKey() = hal.isBit(M_KEY_MASK)
}