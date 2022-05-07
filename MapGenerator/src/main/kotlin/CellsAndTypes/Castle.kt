package CellsAndTypes

class Castle(var type: CastleType, var size: CastleSize): Object(){
    constructor():this(CastleType.CITADEL, CastleSize.SMALL)
    enum class CastleType(val value:Int){
        CITADEL(0), STRONGHOLD(1), TOWER(2), DUNGEON(3), FORTRESS(4), NECROPOLIS(5);
        companion object {
            fun fromInt(value: Int) = CastleType.values().first { it.value == value }
        }
    }
    enum class CastleSize(val value:Int){
        SMALL(0), MEDIUM(1), LARGE(2);
        companion object {
            fun fromInt(value: Int) = CastleSize.values().first { it.value == value }
        }
    }

    override fun getInt():Int {
        return type.value * 10 + size.value
    }

    companion object {
        fun getCastleByInt(n:Int) : Castle {
            val size = CastleSize.fromInt(n % 10)
            val type = CastleType.fromInt(n / 10)
            return Castle(type, size)
        }
    }
}