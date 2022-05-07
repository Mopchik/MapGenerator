package Generator

import CellsAndTypes.*
import kotlin.random.Random

class CellTypeGenerator {
    companion object{
        private val koefSmallCreeps = 0.4
        private val koefOfTerrainType = Random.nextDouble(0.90, 0.99)
        fun genSmallCreeps(i:Int, j:Int):Cell.TypeOfCreeps{
            if(Random.nextDouble(1.0) < koefSmallCreeps){
                return Cell.TypeOfCreeps.fromInt(Random.nextInt(1, 3))
            }
            return Cell.TypeOfCreeps.NO
        }

        fun genTerrainType():Cell.TypeOfTerrain{
            if(Random.nextDouble(1.0) < koefOfTerrainType){
                //println("OK")
                return Cell.TypeOfTerrain.fromInt(Random.nextInt(1,11))
            }
            return Cell.TypeOfTerrain.NO
        }

        fun genTypeOfRoad(): Road.TypeOfRoad{
            var typeOfRoadNum = Random.nextInt(10)
            return when(typeOfRoadNum){
                in 0 until 7 -> Road.TypeOfRoad.OK
                in 7 until 9 -> Road.TypeOfRoad.HARD
                else -> Road.TypeOfRoad.VERY_HARD
            }
        }

        fun genCastle():Castle{
            return Castle.getCastleByInt(Random.nextInt(6)*10 + Random.nextInt(3))
        }

        fun genOwnerable():Ownerable{
            return Ownerable.getOwnerableByInt(Random.nextInt(7))
        }

        fun genMapItem(): MapItem {
            return MapItem.getItemByInt(when(Random.nextInt(4)){0 -> 0; 1 -> 2; 2 -> 3; else -> 4})
        }
    }
}