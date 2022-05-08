package generator

import cellsAndTypes.MapItem
import Field
import kotlin.random.Random

class PointsGenerator(val n:Int, val rows:Int, val cols:Int, val dif:Int) {
    private val points:MutableList<Pair<Int,Int>>
    private val takenPoints:MutableList<Pair<Int,Int>>
    private val koefOfGuard = 0.7
    private val koefOfRoadCreeps = 0.1
    init{
        points = ArrayList()
        takenPoints = ArrayList()
    }
    fun generatePoints():MutableList<Pair<Int,Int>>{
        // var area:MutableList<MutableList<Pair<Int,Int>>> = ArrayList<MutableList<Pair<Int,Int>>>()
        points.clear()
        takenPoints.clear()
        val allPoints:MutableList<Pair<Int,Int>> = ArrayList()
        for(i in dif until rows - dif){
            for(j in dif until cols - dif){
                allPoints.add(Pair(i,j))
            }
        }
        val neededPoints:MutableList<Pair<Int,Int>> = ArrayList()
        for(i in 0 until n){
            val r = allPoints.random()
            neededPoints.add(r)
            points.add(r)
            allPoints.remove(r)
        }
        return neededPoints
    }

    fun changePoints(newPoints:MutableList<Pair<Int,Int>>){
        points.clear()
        takenPoints.clear()
        for(p in newPoints){
            points.add(p)
        }
    }

    fun generateCastlePoints(nOfCastle:Int, field: Field):MutableList<Pair<Int,Int>>{
        return generateFreePoints(4, nOfCastle, field)
    }

    fun generateOwnerablePoints(nOfOwns:Int, field:Field):MutableList<Pair<Int,Int>>{
        return generateFreePoints(2, nOfOwns, field)
    }

    private fun generateFreePoints(radius:Int, kOfPoints:Int, field:Field, forBuild:Boolean = true):MutableList<Pair<Int,Int>>{
        val freePoints:MutableList<Pair<Int,Int>> = ArrayList()
        val allPoints:MutableList<Pair<Int,Int>> = ArrayList()
        for(i in dif until rows - dif){
            for(j in dif until cols - dif){
                allPoints.add(Pair(i,j))
            }
        }
        while(freePoints.size < kOfPoints) {
            if(allPoints.isEmpty()){
                println("Not enough place to generate all castles or ownerables or decorations")
                break
            }
            val rand = allPoints.random()
            val x = rand.first
            val y = rand.second

            if (forBuild && field.isPointCorrectForBuilding(rand, radius)
                || !forBuild && field.isZoneOfPointsNotTaken(radius, rand)){
                freePoints.add(rand)
                for(i in x - radius..x + radius){
                    for(j in y - radius..y +radius){
                        field.matr[i][j].isTaken = true
                    }
                }
            }
            allPoints.remove(rand)
        }
        return freePoints
    }

    fun generateStrongCreeps():MutableList<Pair<Int,Int>>{
        val strongCreepsPoints:MutableList<Pair<Int,Int>> = ArrayList()
        for(p in points){
            if(Random.nextDouble(0.0, 1.0) < koefOfRoadCreeps){
                strongCreepsPoints.add(p)
            }
        }
        return strongCreepsPoints
    }

    fun generateSmallDecorations(field:Field):MutableList<Pair<Int,Int>>{
        val decorations:MutableList<Pair<Int,Int>> = ArrayList()
        val radius = 0
        for(i in dif until rows - dif){
            for(j in dif until cols - dif){
                if(field.isZoneOfPointsNotTaken(radius, Pair(i,j))&&
                        field.isNotConnectedWithRoad(Pair(i,j))){
                    decorations.add(Pair(i,j))
                    field.matr[i][j].isTaken = true
                }
            }
        }
        return decorations
    }

    fun genSmallCreeps(field:Field):MutableList<Pair<Int,Int>>{
        val smallCreeps:MutableList<Pair<Int,Int>> = ArrayList()
        field.getOwnerableCells().forEach {
            if(Random.nextDouble(1.0) < koefOfGuard)
                smallCreeps.add(Pair(it.xy.first,it.xy.second))
        }
        field.getMapItemCells().forEach {
            if(it.obj.getInt() != MapItem.TypeOfItem.ARTIFACT.value &&
                Random.nextDouble(1.0) < koefOfGuard) {
                smallCreeps.add(Pair(it.xy.first, it.xy.second))
            }
        }
        return smallCreeps
    }

    fun genInsaneCreeps(field:Field):MutableList<Pair<Int,Int>>{
        val insaneCreeps:MutableList<Pair<Int,Int>> = ArrayList()
        field.getMapItemCells().forEach {
            if(it.obj.getInt() == MapItem.TypeOfItem.ARTIFACT.value &&
                Random.nextDouble(1.0) < koefOfGuard) {
                insaneCreeps.add(Pair(it.xy.first, it.xy.second))
            }
        }
        return insaneCreeps
    }

    fun genMapItems(field:Field, nOfItems:Int):MutableList<Pair<Int,Int>>{
        return generateFreePoints(1, nOfItems, field)
    }

    fun generateBigDecorations(field:Field, kOfPoints: Int):MutableList<Pair<Int,Int>>{
        return generateFreePoints(2, kOfPoints, field, false)
    }

    fun genPlayersCastles(nOfPlayers:Int, field:Field):MutableList<Pair<Int,Int>>{
        val allCastles = field.getCastleCells()
        val playersCastles:MutableList<Pair<Int,Int>> = ArrayList()
        for(i in 1..nOfPlayers){
            val rand = allCastles.random()
            playersCastles.add(rand.xy)
            allCastles.remove(rand)
        }
        return playersCastles
    }



    //fun generateCastlePoints(matrix:MutableList<MutableList<Int>>, numOfCastles:Int,
    //                         matrCell:MutableList<MutableList<Cell>>):MutableList<Pair<Int,Int>>{
    //    val castles:MutableList<Pair<Int,Int>> = ArrayList()
    //    val pointsCopy:MutableList<Pair<Int,Int>> = ArrayList()
    //    for(i in 0 until points.size) {
    //        pointsCopy.add(points[i])
    //    }
    //    for(i in 0 until points.size){
    //        if(matrix[i].size == 1 && castles.size < numOfCastles && !takenPoints.contains(points[i])) {
    //            val x = points[i].first
    //            val y = points[i].second
    //            if(x + 1 < rows && matrCell[x + 1][y].type == Cell.TypeOfCell.ROAD ||
    //                y + 1 < cols && matrCell[x][y+1].type == Cell.TypeOfCell.ROAD) {
    //                castles.add(points[i])
    //                takenPoints.add(points[i])
    //                pointsCopy.removeIf { it.first == points[i].first && it.second == points[i].second }
    //            }
    //        }
    //    }
    //    while(castles.size < numOfCastles){
    //        val r = pointsCopy.random()
    //        val x = r.first
    //        val y = r.second
    //        if(x + 1 < rows && matrCell[x + 1][y].type == Cell.TypeOfCell.ROAD ||
    //            y + 1 < cols && matrCell[x][y+1].type == Cell.TypeOfCell.ROAD) {
    //            castles.add(r)
    //            takenPoints.add(r)
    //            pointsCopy.remove(r)
    //        }
    //    }
    //    return castles
    //}
}