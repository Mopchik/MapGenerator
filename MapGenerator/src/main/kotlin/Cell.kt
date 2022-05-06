public class Cell(t:TypeOfCell, terr:TypeOfTerrain, creeps:TypeOfCreeps, xy:Pair<Int,Int>) {
    constructor(xy:Pair<Int,Int>) : this(TypeOfCell.LAND,TypeOfTerrain.NO,TypeOfCreeps.NO, xy)
    constructor(t:TypeOfCell, terr: TypeOfTerrain, creeps: TypeOfCreeps, xy:Pair<Int,Int>,
                road:TypeOfRoad):this(t, terr, creeps, xy){
                    this.road = road
                }
    var type:TypeOfCell
    var terr:TypeOfTerrain
    var creeps:TypeOfCreeps
    var road:TypeOfRoad
    val xy:Pair<Int,Int>
    init{
        this.type = t
        this.terr = terr
        this.creeps = creeps
        this.road = TypeOfRoad.NO
        this.xy = xy
    }
    enum class TypeOfCell{
        ROAD, CASTLE, LAND
    }
    enum class TypeOfTerrain(val value:Int){
        NO(0), LIGHT_SAND(1), SOIL(2), GRASS(3), DARK_GRASS(4), VOLCANO(5),
        DIRT(6), SNOW(7), SAND(8), GOLD(9), STONE(10);
        companion object {
            fun fromInt(value: Int) = TypeOfTerrain.values().first { it.value == value }
        }
    }
    enum class TypeOfCreeps{
        NO, BIG, SMALL
    }
    enum class TypeOfRoad(val value:Int){
        NO(0), OK(1), HARD(2), VERY_HARD(3);
        companion object {
            fun fromInt(value: Int) = TypeOfRoad.values().first { it.value == value }
        }
    }
}
