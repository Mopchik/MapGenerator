package CellsAndTypes

class Ownerable(var type: TypeOfOwnerable): Object() {
    constructor():this(TypeOfOwnerable.GOLD_MINE)
    enum class TypeOfOwnerable(val value:Int){
        GOLD_MINE(0), ORE_PIT(1), WOOD_SAWMILL(2), ALCHEMIST_LAB(3),
        GEMS_MINE(4), CRYSTAL_MINE(5), SULFUR_MINE(6);
        companion object {
            fun fromInt(value: Int) = TypeOfOwnerable.values().first { it.value == value }
        }
    }

    override fun getInt(): Int {
        return type.value
    }

    companion object {
        fun getOwnerableByInt(n:Int) : Ownerable {
            val type = TypeOfOwnerable.fromInt(n)
            return Ownerable(type)
        }
    }
}