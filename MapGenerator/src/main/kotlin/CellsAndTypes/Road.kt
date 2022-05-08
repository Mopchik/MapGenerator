package cellsAndTypes

class Road(var type: TypeOfRoad): Object() {
    constructor():this(TypeOfRoad.OK)
    enum class TypeOfRoad(val value:Int){
        OK(0), HARD(1), VERY_HARD(2);
        companion object {
            fun fromInt(value: Int) = TypeOfRoad.values().first { it.value == value }
        }
    }

    override fun getInt():Int {
        return type.value
    }

    companion object {
        fun getRoadByInt(n:Int) : Road {
            val type = TypeOfRoad.fromInt(n)
            return Road(type)
        }
    }
}