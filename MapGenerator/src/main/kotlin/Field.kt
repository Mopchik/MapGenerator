import CellsAndTypes.Cell
import CellsAndTypes.Decoration
import CellsAndTypes.Road
import Generator.CellTypeGenerator
import java.lang.Integer.max

class Field(rows:Int, cols:Int) {
    val rows:Int
    val cols:Int
    val matr:MutableList<MutableList<Cell>> = ArrayList()
    private val roadPoints:MutableList<Pair<Int,Int>> = ArrayList()
    private val castlePoints:MutableList<Pair<Int,Int>> = ArrayList()
    init{
        this.rows = rows
        this.cols = cols
        for(i in 0 until rows){
            matr.add(ArrayList<Cell>())
            // matr[i] = ArrayList<TypeOfCell>(cols)
            for(j in 0 until cols){
                // matr[i][j] = TypeOfCell.LAND
                matr[i].add(Cell(Pair(i,j)))
            }
        }
    }

    fun getAllPointsBetween(f:Pair<Int, Int>, s:Pair<Int, Int>,
                            notRoad:Pair<Int,Int> = Pair(-1,-1)):MutableList<Pair<Int,Int>>{
        val allPoints:MutableList<Pair<Int,Int>> = ArrayList()
        if(f.first > s.first){
            return getAllPointsBetween(s, f, notRoad)
        }
        var jOld = f.second
        if(f.first == s.first){
            if(f.second <= s.second) {
                for (j in f.second..s.second)
                    allPoints.add(Pair(f.first, j))
            } else{
                for (j in s.second..f.second)
                    allPoints.add(Pair(f.first, j))
            }
            //allPoints.removeAll{it.first == notRoad.first && it.second == notRoad.second}
            if(allPoints.contains(notRoad))
                allPoints.remove(notRoad)
            return allPoints
        }

        for(i in f.first..s.first){
            var jNew = f.second + ((s.second - f.second) * (i - f.first) / (s.first - f.first)).toInt()
            if(jOld < jNew) {
                for (j in jNew downTo jOld) {
                    allPoints.add(Pair(i, j))
                }
            } else{
                for (j in jNew..jOld) {
                    allPoints.add(Pair(i, j))
                }
            }
            jOld = jNew
        }
        //allPoints.removeAll{it.first == notRoad.first && it.second == notRoad.second}
        if(allPoints.contains(notRoad))
            allPoints.remove(notRoad)
        return allPoints
    }

    fun connectTwoPoints(f:Pair<Int, Int>, s:Pair<Int, Int>, typeOfRoad: Road.TypeOfRoad,
                         notRoad:Pair<Int,Int> = Pair(-1,-1)){
        getAllPointsBetween(f,s, notRoad).forEach {
            initRoad(it.first,it.second, typeOfRoad)
        }
    }

    fun findClosestRoad(p:Pair<Int,Int>, isDownLeft:Boolean = false):Pair<Int,Int>{
        val maxDist = distanceSQ(Pair(0,0), Pair(rows - 1, cols - 1))
        var ans = Pair(-1,-1)
        var dist = maxDist
        if(isDownLeft){
            for(i in p.first until rows){
                for(j in p.second until cols){
                    if(matr[i][j].type == Cell.TypeOfCell.ROAD) {
                        val tempDist = distanceSQ(p, Pair(i,j))
                        if(tempDist < dist){
                            dist = tempDist
                            ans = Pair(i,j)
                        }
                    }
                }
            }
            if(dist != maxDist)
                return ans
        }
        for(i in 0 until rows){
            for(j in 0 until cols){
                if(matr[i][j].type == Cell.TypeOfCell.ROAD) {
                    val tempDist = distanceSQ(p, Pair(i,j))
                    if(tempDist < dist){
                        dist = tempDist
                        ans = Pair(i,j)
                    }
                }
            }
        }
        return ans
    }

    private fun initRoad(i:Int, j:Int, typeOfRoad: Road.TypeOfRoad){
        matr[i][j].type = Cell.TypeOfCell.ROAD
        matr[i][j].obj = Road(typeOfRoad)
        matr[i][j].isTaken = true
    }

    fun getMatrOfIsTaken():MutableList<MutableList<Boolean>>{
        val matrIsTaken:MutableList<MutableList<Boolean>> = ArrayList()
        for(i in 0 until rows){
            matrIsTaken.add(ArrayList())
            for(cell in matr[i]){
                matrIsTaken[i].add(cell.isTaken)
            }
        }
        return matrIsTaken
    }

    fun clear(){
        for(i in 0 until rows){
            matr.add(ArrayList<Cell>())
            // matr[i] = ArrayList<TypeOfCell>(cols)
            for(j in 0 until cols){
                // matr[i][j] = TypeOfCell.LAND
                matr[i][j] = Cell(Pair(i,j))
            }
        }
    }

    fun placeGraph(matrix:MutableList<MutableList<Int>>, roadPoints:MutableList<Pair<Int,Int>>){
        for(i in 0 until matrix.size){
            this.roadPoints.add(roadPoints[i])
            for(j in matrix[i]){
                connectTwoPoints(roadPoints[i], roadPoints[j], CellTypeGenerator.genTypeOfRoad())
            }
        }
    }

    fun setTerrainType(i:Int, j:Int, type:Cell.TypeOfTerrain){
        matr[i][j].terr = type
    }

    fun putAllTerrainType(){
        for(i in 0 until rows){
            for(j in 0 until cols){
                if(matr[i][j].terr == Cell.TypeOfTerrain.NO) {
                    matr[i][j].terr = getClosestTerrainCell(i, j).terr
                }
            }
        }
    }

    fun getClosestTerrainCell(i:Int,j:Int): Cell {
        val thisPair = Pair(i, j)
        var minDist = rows * rows + cols*cols
        var pair = Pair(i, j)
        for(p in castlePoints){
            if(matr[p.first][p.second].terr != Cell.TypeOfTerrain.NO){
                if(minDist > distanceSQ(thisPair,p)){
                    minDist = distanceSQ(thisPair, p)
                    pair = p
                }
            }
        }
        return matr[pair.first][pair.second]
    }

    fun distanceSQ(f:Pair<Int, Int>, s:Pair<Int, Int>):Int{
        val dif = Pair(s.first - f.first, s.second - f.second)
        return dif.first * dif.first + dif.second*dif.second
    }

    fun placeCastles(castlePoints:MutableList<Pair<Int,Int>>){
        for(p in castlePoints){
            val closestRoad = findClosestRoad(Pair(p.first,p.second), true)
            this.castlePoints.add(p)
            matr[p.first][p.second].type = Cell.TypeOfCell.CASTLE
            // for(i in p.first - 4..p.first + 4){
            //     for(j in p.second - 4..p.second +4){
            //         matr[i][j].isTaken = true
            //     }
            // }
            matr[p.first][p.second].obj = CellTypeGenerator.genCastle()

            connectTwoPoints(p, closestRoad, CellTypeGenerator.genTypeOfRoad(), p)
        }
    }

    fun getClosestPointToTheRoad(start:Pair<Int,Int>, closestRoad:Pair<Int,Int>, radius:Int):Pair<Int,Int>{
        var minDist = rows*rows + cols*cols
        var ans = Pair(-1,-1)
        val between = getAllPointsBetween(start, closestRoad)
        for(b in between){
            val tempDist = distanceSQ(b,closestRoad)
            if(isZoneOfPointsNotTaken(radius, b) && tempDist < minDist){
                minDist = tempDist
                ans = b
            }
        }
        return ans
    }

    fun isZoneOfPointsNotTaken(radius:Int, p:Pair<Int,Int>):Boolean{
        val x = p.first
        val y = p.second
        for (i in x - radius..x + radius) {
            for (j in y - radius..y + radius) {
                if (i < 0 || i >= rows || j < 0 || j >= cols  || matr[i][j].isTaken)
                    return false
            }
        }
        return true
    }

    fun isPointCorrectForBuilding(p:Pair<Int,Int>, radius:Int):Boolean{
        if(!isZoneOfPointsNotTaken(radius, p)) return false
        val closestRoad = findClosestRoad(p, true)
        val between = getAllPointsBetween(p, closestRoad, closestRoad)
        between.forEach { if(matr[it.first][it.second].isTaken) return false }
        return true
    }

    fun placeDecorations(points:MutableList<Pair<Int,Int>>, type:Decoration.TypeOfDecoration){
        points.forEach {
            matr[it.first][it.second].type = Cell.TypeOfCell.DECORATION
            matr[it.first][it.second].obj = Decoration(type)
        }
    }

    fun placeOwnerables(ownerablePoints:MutableList<Pair<Int,Int>>){
        // ownerablePoints.forEach {
        //     val x = it.first
        //     val y = it.second
        //     for (i in x - 2..x + 2) {
        //         for (j in y - 2..y + 2) {
        //             matr[i][j].isTaken = true
        //         }
        //     }
        // }
        for(o in ownerablePoints){
            for (i in o.first - 2..o.first + 2) {
                for (j in o.second - 2..o.second + 2) {
                    matr[i][j].isTaken = false
                }
            }
            val closestRoad = findClosestRoad(o, true)
            val cl = getClosestPointToTheRoad(o, closestRoad, 2)

            matr[cl.first][cl.second].type = Cell.TypeOfCell.OWNERABLE
            for(i in cl.first - 2..cl.first + 2){
                for(j in cl.second - 2..cl.second +2){
                    matr[i][j].isTaken = true
                }
            }
            matr[cl.first][cl.second].obj = CellTypeGenerator.genOwnerable()
            connectTwoPoints(cl, closestRoad, CellTypeGenerator.genTypeOfRoad(), cl)
        }
    }

    fun placeCreep(creep:Pair<Int,Int>, type: Cell.TypeOfCreeps){
        matr[creep.first][creep.second].creeps = type
    }

    fun placeMapItems(items:MutableList<Pair<Int,Int>>){
        for(item in items){
            for (i in item.first - 1..item.first + 1) {
                for (j in item.second - 1..item.second + 1) {
                    matr[i][j].isTaken = false
                }
            }
            val closestRoad = findClosestRoad(item, true)
            val cl = getClosestPointToTheRoad(item, closestRoad, 1)

            matr[cl.first][cl.second].type = Cell.TypeOfCell.MAP_ITEM
            for(i in cl.first - 1..cl.first + 1){
                for(j in cl.second - 1..cl.second +1){
                    matr[i][j].isTaken = true
                }
            }
            matr[cl.first][cl.second].obj = CellTypeGenerator.genMapItem()
            connectTwoPoints(cl, closestRoad, CellTypeGenerator.genTypeOfRoad(), cl)
        }
    }

    fun show(){
        for(i in 0 until rows){
            for(j in 0 until cols){
                when(matr[i][j].type) {
                    Cell.TypeOfCell.LAND, Cell.TypeOfCell.DECORATION -> showLandCell(matr[i][j])
                    Cell.TypeOfCell.ROAD -> showRoadCell(matr[i][j])
                    Cell.TypeOfCell.CASTLE -> print("\u001B[45;1m")
                    Cell.TypeOfCell.OWNERABLE -> print("\u001B[45;1m")
                }
                when(matr[i][j].creeps){
                    Cell.TypeOfCreeps.NO -> print("   ")
                    Cell.TypeOfCreeps.INSANE, Cell.TypeOfCreeps.VERY_STRONG, Cell.TypeOfCreeps.STRONG -> print("#  ")
                    Cell.TypeOfCreeps.WEEK, Cell.TypeOfCreeps.VERY_WEEK, Cell.TypeOfCreeps.NORMAL -> print("0  ")
                }
            }
            println()
        }
    }

    fun showLandCell(cell: Cell){
        when(cell.terr){
            Cell.TypeOfTerrain.GRASS, Cell.TypeOfTerrain.DARK_GRASS,
            Cell.TypeOfTerrain.VOLCANO, Cell.TypeOfTerrain.STONE -> print("\u001B[42;1m")
            Cell.TypeOfTerrain.SAND, Cell.TypeOfTerrain.LIGHT_SAND, Cell.TypeOfTerrain.DIRT,
            Cell.TypeOfTerrain.SOIL, Cell.TypeOfTerrain.GOLD -> print("\u001B[43;1m")
            Cell.TypeOfTerrain.SNOW -> print("\u001B[47;1m")
            else -> print("\u001B[44;1m")
        }
    }

    fun showRoadCell(cell: Cell){
        //when(cell.obj.getInt()){
        //    0 -> print("\u001B[40;1m")
        //    1 -> print("\u001B[44;1m")
        //    2 -> print("\u001B[41;1m")
        //}
        print("\u001B[40;1m")
    }

    fun isNotConnectedWithRoad(p:Pair<Int,Int>):Boolean{
        for(i in maxOf(p.first - 1, 0)..minOf(p.first + 1, rows - 1)){
            for(j in maxOf(p.second - 1, 0)..minOf(p.second + 1, cols - 1)){
                if(matr[i][j].type == Cell.TypeOfCell.ROAD)
                    return false
            }
        }
        return true
    }

    fun getCastleCells():MutableList<Cell>{
        val castleCells:MutableList<Cell> = ArrayList()
        for(i in 0 until rows){
            for(j in 0 until cols){
                if(matr[i][j].type == Cell.TypeOfCell.CASTLE){
                    castleCells.add(matr[i][j])
                }
            }
        }
        return castleCells
    }

    fun getMapItemCells():MutableList<Cell>{
        val mapItemCells:MutableList<Cell> = ArrayList()
        for(i in 0 until rows){
            for(j in 0 until cols){
                if(matr[i][j].type == Cell.TypeOfCell.MAP_ITEM){
                    mapItemCells.add(matr[i][j])
                }
            }
        }
        return mapItemCells
    }

    fun getOwnerableCells():MutableList<Cell>{
        val ownerableCells:MutableList<Cell> = ArrayList()
        for(i in 0 until rows){
            for(j in 0 until cols){
                if(matr[i][j].type == Cell.TypeOfCell.OWNERABLE){
                    ownerableCells.add(matr[i][j])
                }
            }
        }
        return ownerableCells
    }

    fun getDecorationCells():MutableList<Cell>{
        val decorationCells:MutableList<Cell> = ArrayList()
        for(i in 0 until rows){
            for(j in 0 until cols){
                if(matr[i][j].type == Cell.TypeOfCell.DECORATION){
                    decorationCells.add(matr[i][j])
                }
            }
        }
        return decorationCells
    }
    fun getGuardCells():MutableList<Cell>{
        val guardCells:MutableList<Cell> = ArrayList()
        for(i in 0 until rows){
            for(j in 0 until cols){
                if(matr[i][j].creeps != Cell.TypeOfCreeps.NO){
                    guardCells.add(matr[i][j])
                }
            }
        }
        return guardCells
    }

    fun getAllRoadPoints():MutableList<Cell>{
        val allRoadPoints:MutableList<Cell> = ArrayList()
        for(i in 0 until rows){
            for(j in 0 until cols){
                if(matr[i][j].type == Cell.TypeOfCell.ROAD ||
                    matr[i][j].type == Cell.TypeOfCell.CASTLE || matr[i][j].type == Cell.TypeOfCell.OWNERABLE){
                    allRoadPoints.add(matr[i][j])
                }
            }
        }
        return allRoadPoints
    }

    private val playersCastles:MutableList<Pair<Int,Int>> = ArrayList()
    fun setPlayersCastles(castles:MutableList<Pair<Int,Int>>){
        playersCastles.clear()
        castles.forEach { playersCastles.add(it) }
    }
    fun getPlayersCastles():MutableList<Pair<Int,Int>>{
        return playersCastles
    }

}