package CellsAndTypes

class MapItem(var type:TypeOfItem):Object() {
    constructor():this(TypeOfItem.RANDOM_RES)
    enum class TypeOfItem(val value:Int){
        RANDOM_RES(0), CAMPFIRE(2), TREASURE(3), ARTIFACT(4);
        companion object {
            fun fromInt(value: Int) = TypeOfItem.values().first { it.value == value }
        }
    }

    override fun getInt():Int {
        return type.value
    }

    companion object {
        fun getItemByInt(n:Int) : MapItem{
            val type = TypeOfItem.fromInt(n)
            return MapItem(type)
        }
    }
}