package cellsAndTypes

public class Cell(t: TypeOfCell, terr: TypeOfTerrain, creeps: TypeOfCreeps, xy:Pair<Int,Int>) {
    constructor(xy:Pair<Int,Int>) : this(TypeOfCell.LAND, TypeOfTerrain.NO, TypeOfCreeps.NO, xy)
    constructor(t: TypeOfCell, terr: TypeOfTerrain, creeps: TypeOfCreeps,
                xy:Pair<Int,Int>, enum:Int):this(t, terr, creeps, xy){
                    obj = when(t){
                        TypeOfCell.ROAD -> Road.getRoadByInt(enum)
                        TypeOfCell.CASTLE -> Castle.getCastleByInt(enum)
                        else -> Ownerable.getOwnerableByInt(enum) // TypeOfCell.VISITABLE
                    }
                }
    var type: TypeOfCell
    var terr: TypeOfTerrain
    var creeps: TypeOfCreeps
    var obj: Object
    var isTaken:Boolean
    val xy:Pair<Int,Int>
    init{
        this.type = t
        this.terr = terr
        this.creeps = creeps
        obj = when(t){
            TypeOfCell.ROAD -> Road()
            TypeOfCell.CASTLE -> Castle()
            TypeOfCell.DECORATION -> Decoration()
            TypeOfCell.MAP_ITEM -> MapItem()
            else -> Ownerable() // TypeOfCell.VISITABLE
        }
        this.xy = xy
        isTaken = type != TypeOfCell.LAND
    }

    enum class TypeOfCell{
        ROAD, CASTLE, LAND, OWNERABLE, DECORATION, MAP_ITEM
    }
    enum class TypeOfTerrain(val value:Int){
        NO(0), LIGHT_SAND(1), SOIL(2), GRASS(3), DARK_GRASS(4), VOLCANO(5),
        DIRT(6), SNOW(7), SAND(8), GOLD(9), STONE(10);
        companion object {
            fun fromInt(value: Int) = TypeOfTerrain.values().first { it.value == value }
        }
    }
    enum class TypeOfCreeps(val value:Int){
        NO(0), VERY_WEEK(1), WEEK(2), NORMAL(3),
        STRONG(4), VERY_STRONG(5), INSANE(6);
        companion object {
            fun fromInt(value: Int) = TypeOfCreeps.values().first { it.value == value }
        }
    }
}
