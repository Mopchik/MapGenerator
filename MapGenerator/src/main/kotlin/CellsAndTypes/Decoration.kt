package cellsAndTypes

class Decoration(var type:TypeOfDecoration):Object() {
    constructor():this(TypeOfDecoration.SMALL)

    enum class TypeOfDecoration(val value:Int){
        SMALL(0), BIG(1);
        companion object {
            fun fromInt(value: Int) = TypeOfDecoration.values().first { it.value == value }
        }
    }

    override fun getInt():Int {
        return type.value
    }

    companion object {
        fun getDecorationByInt(n:Int) : Decoration {
            val type = TypeOfDecoration.fromInt(n)
            return Decoration(type)
        }
    }
}