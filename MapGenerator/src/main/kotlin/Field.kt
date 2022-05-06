import java.io.File
import kotlin.random.Random

class Field(rows:Int, cols:Int) {
    private val rows:Int
    private val cols:Int
    private val koefSmallCreeps = 4
    private val stepSmallCreeps = 10
    private val matr:MutableList<MutableList<Cell>> = ArrayList()
    private val roadPoints:MutableList<Pair<Int,Int>> = ArrayList()
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
    private fun connectTwoPoints(f:Pair<Int, Int>, s:Pair<Int, Int>){
        if(f.first > s.first){
            return connectTwoPoints(s, f)
        }
        var typeOfRoadNum = Random.nextInt(10)
        when(typeOfRoadNum){
            in 0 until 7 -> typeOfRoadNum = 1
            in 7 until 9 -> typeOfRoadNum = 2
            in 9 until 10 -> typeOfRoadNum = 3
        }
        val typeOfRoad = Cell.TypeOfRoad.fromInt(typeOfRoadNum)
        var jOld = f.second
        if(f.first == s.first){
            if(f.second <= s.second) {
                for (j in f.second..s.second)
                    initRoad(matr, f.first, j, typeOfRoad)
            } else{
                for (j in s.second..f.second)
                    initRoad(matr, f.first, j, typeOfRoad)
            }
            return
        }
        var stepper = 0
        for(i in f.first..s.first){
            // if(i > f.first) break
            var jNew = f.second + ((s.second - f.second) * (i - f.first) / (s.first - f.first)).toInt()
            // if(jNew < jOld) jNew++
            // else jNew--
            if(jOld < jNew) {
                for (j in jNew downTo jOld) {
                    initRoad(matr, i, j, typeOfRoad)
                    stepper++
                    if(stepper >= stepSmallCreeps){
                        stepper = 0
                        genSmallCreeps(i,j)
                    }
                }
            }else if(jOld == jNew) {
                initRoad(matr, i, jOld, typeOfRoad)
                stepper++
                if(stepper >= stepSmallCreeps){
                    stepper = 0
                    genSmallCreeps(i,jOld)
                }
            } else{
                for (j in jNew..jOld) {
                    initRoad(matr, i, j, typeOfRoad)
                    stepper++
                    if(stepper >= stepSmallCreeps){
                        stepper = 0
                        genSmallCreeps(i,j)
                    }
                }
            }
            jOld = jNew
        }
    }

    private fun initRoad(matr:MutableList<MutableList<Cell>>, i:Int, j:Int, typeOfRoad:Cell.TypeOfRoad){
        matr[i][j].type = Cell.TypeOfCell.ROAD
        matr[i][j].road = typeOfRoad
    }

    fun genSmallCreeps(i:Int, j:Int){
        if(Random.nextInt(10) < koefSmallCreeps){
            matr[i][j].creeps = Cell.TypeOfCreeps.SMALL
        }
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
                connectTwoPoints(roadPoints[i], roadPoints[j])
            }
        }
        val koef = Random.nextDouble(1.0/rows, 0.15)
        genTerrainType(roadPoints, koef)
    }

    fun genTerrainType(points:MutableList<Pair<Int,Int>>, koef:Double){
        for(p in points){
            if(Random.nextDouble(1.0) < koef){
                matr[p.first][p.second].terr = Cell.TypeOfTerrain.fromInt(Random.nextInt(1,11))
            }
        }
    }

    fun putAllTerrainType(){
        for(i in 0 until rows){
            for(j in 0 until cols){
                if(matr[i][j].terr == Cell.TypeOfTerrain.NO)
                    matr[i][j].terr = getClosestTerrainCell(i, j).terr
            }
        }
    }

    fun getClosestTerrainCell(i:Int,j:Int):Cell{
        val thisPair = Pair(i, j)
        var minDist = rows * rows + cols*cols
        var pair = Pair(i, j)
        for(p in roadPoints){
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
            matr[p.first][p.second].type = Cell.TypeOfCell.CASTLE
        }
        genTerrainType(castlePoints, 10.0)
    }

    fun placeStrongCreeps(strongCreepsPoints:MutableList<Pair<Int,Int>>){
        for(p in strongCreepsPoints){
            matr[p.first][p.second].creeps = Cell.TypeOfCreeps.BIG
        }
    }

    fun show(){
        for(i in 0 until rows){
            for(j in 0 until cols){
                when(matr[i][j].type) {
                    Cell.TypeOfCell.LAND -> showLandCell(matr[i][j])
                    Cell.TypeOfCell.ROAD -> showRoadCell(matr[i][j])
                    Cell.TypeOfCell.CASTLE -> print("\u001B[45;1m")
                }
                when(matr[i][j].creeps){
                    Cell.TypeOfCreeps.NO -> print("   ")
                    Cell.TypeOfCreeps.BIG -> print("#  ")
                    Cell.TypeOfCreeps.SMALL -> print("0  ")
                }
            }
            println()
        }
    }

    fun showLandCell(cell:Cell){
        when(cell.terr){
            Cell.TypeOfTerrain.GRASS, Cell.TypeOfTerrain.DARK_GRASS,
            Cell.TypeOfTerrain.VOLCANO, Cell.TypeOfTerrain.STONE -> print("\u001B[42;1m")
            Cell.TypeOfTerrain.SAND, Cell.TypeOfTerrain.LIGHT_SAND, Cell.TypeOfTerrain.DIRT,
            Cell.TypeOfTerrain.SOIL, Cell.TypeOfTerrain.GOLD -> print("\u001B[43;1m")
            Cell.TypeOfTerrain.SNOW -> print("\u001B[47;1m")
            else -> print("\u001B[44;1m")
        }
    }

    fun showRoadCell(cell:Cell){
        when(cell.road){
            Cell.TypeOfRoad.OK -> print("\u001B[40;1m")
            Cell.TypeOfRoad.HARD -> print("\u001B[44;1m")
            Cell.TypeOfRoad.VERY_HARD -> print("\u001B[41;1m")
        }
    }

    fun writeToBytes(){
        val bytesBuff:MutableList<Byte> = ArrayList()
        val EMAP_FILE_HDR_KEY:UInt = 0x76235278u
        val EMAP_FILE_VERSION = 0x19u
        // EMAP_FILE_HDR_KEY
        uIntToByteArray(EMAP_FILE_HDR_KEY).forEach { bytesBuff.add(it) }
        //listOf<Byte>(78, 52, 23, 76).forEach{bytesBuff.add(it)}
        // EMAP_FILE_VERSION
        uIntToByteArray(EMAP_FILE_VERSION).forEach { bytesBuff.add(it) }
        //listOf<Byte>(0, 0, 0, 0).forEach{bytesBuff.add(it)}
        // map.m_Siz
        bytesBuff.add(getMapSiz())
        // map.m_lngMask
        listOf<Byte>(1, 0, 0, 0).forEach{bytesBuff.add(it)}
        // text resources count
        listOf<Byte>(2, 0, 0, 0).forEach{bytesBuff.add(it)}
        // map.m_MapVersion and map_MapAuthor
        listOf<Byte>(15, 0, 0, 0, 77, 0, 97, 0, 112, 0, 32, 0, 68, 0, 101, 0,
            115, 0, 99, 0, 114, 0, 105, 0, 112, 0, 116, 0, 105, 0, 111, 0,
            110, 0, 2, 0, 19, 0, 0, 0, 68, 0, 101, 0, 102, 0, 97, 0,
            117, 0, 108, 0, 116, 0, 32, 0, 100, 0, 101, 0, 115, 0, 99, 0,
            116, 0, 105, 0, 112, 0, 116, 0, 105, 0, 111, 0, 110, 0, 8, 0,
            0, 0, 77, 0, 97, 0, 112, 0, 32, 0, 110, 0, 97, 0, 109, 0,
            101, 0, 1, 0, 7, 0, 0, 0, 78, 0, 101, 0, 119, 0, 32, 0,
            77, 0, 97, 0, 112, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    -1, -1, -1, -1, 0, 0, 0, 0, 2, 0, 0, 2, 0, 1, 2, 0
        ).forEach{bytesBuff.add(it)}
        val heroes_size = 0u
        uInt16ToByteArray(heroes_size).forEach { bytesBuff.add(it) }
        val mapItems_size = 0u
        uInt16ToByteArray(mapItems_size).forEach { bytesBuff.add(it) }
        val guards_size = 0u
        uInt16ToByteArray(guards_size).forEach { bytesBuff.add(it) }
        val mapEvents_size = 0u
        uInt16ToByteArray(mapEvents_size).forEach { bytesBuff.add(it) }
        val visitables_size = 0u
        uInt16ToByteArray(visitables_size).forEach { bytesBuff.add(it) }
        val ownerables_size = 0u
        uInt16ToByteArray(ownerables_size).forEach { bytesBuff.add(it) }

        // Castles
        val castles_size = 0u // size
        uInt16ToByteArray(castles_size).forEach { bytesBuff.add(it) }
        bytesBuff.add(32)
        bytesBuff.add(-1) // owner
        uInt16ToByteArray(1u).forEach { bytesBuff.add(it) } // pos
        uInt16ToByteArray(1u).forEach { bytesBuff.add(it) }
        for(i in 1..7){ // garrison
            listOf<Byte>(-1, -1, 0, 0, 0, 0).forEach{bytesBuff.add(it)}
        }
        // Map Dump
        getMapDumpByteArray().forEach { bytesBuff.add(it) }

        // Decorations
        listOf<Byte>(0, 0, 0, 0).forEach{bytesBuff.add(it)}

        // Roads
        val allRoadPoints = getAllRoadPoints()
        uIntToByteArray(allRoadPoints.size.toUInt()).forEach { bytesBuff.add(it) }

        val roadOK_id = listOf<Byte>(10, 0, 0, 0, 115, 0, 116, 0, 111, 0, 110, 0, 101, 0, 95, 0,
            114, 0, 111, 0, 97, 0, 100, 0)
        val roadHard_id = listOf<Byte>(9, 0, 0, 0, 100, 0, 105, 0, 114, 0, 116, 0, 95, 0, 114, 0,
            111, 0, 97, 0, 100, 0)
        allRoadPoints.forEach {
            uInt16ToByteArray(it.xy.first.toUInt()).forEach { bytesBuff.add(it) }
            uInt16ToByteArray(it.xy.second.toUInt()).forEach { bytesBuff.add(it) }
            when(it.road){
                Cell.TypeOfRoad.OK -> roadOK_id.forEach{bytesBuff.add(it)}
                Cell.TypeOfRoad.HARD, Cell.TypeOfRoad.VERY_HARD -> roadOK_id.forEach{bytesBuff.add(it)}
            }
        }

        //showBytesBuff(bytesBuff)
        var arr:ByteArray = ByteArray(bytesBuff.size)
        for(i in 0 until bytesBuff.size){
            arr[i] = bytesBuff[i]
        }
        File("C:\\Users\\Папа\\Desktop\\pph-native-initial (1)\\MyMaps\\file.hmm").writeBytes(arr)
    }

    fun showBytesBuff(bytesBuff:MutableList<Byte>){
        for(i in 0 until bytesBuff.size){
            print("${Integer.toHexString(bytesBuff[i].toInt())} ")
            if((i + 1) % 4 == 0)
                print("    ")
            if((i + 1) % 16 == 0)
                println()
        }
    }

    fun getMapSiz():Byte{
        if(rows != cols) return -1
        when(rows - 1){
            32 -> return 0
            64 -> return 1
            128 -> return 2
            256 -> return 3
        }
        return -1
    }

    fun uIntToByteArray(value:UInt):ByteArray{
        val bytes = ByteArray(4)
        bytes[0] = (value and 0xFFFFu).toByte()
        bytes[1] = (value.shr(8) and 0xFFFFu).toByte()
        bytes[2] = (value.shr(16) and 0xFFFFu).toByte()
        bytes[3] = (value.shr(24) and 0xFFFFu).toByte()
        return bytes
    }

    fun uInt16ToByteArray(value:UInt):ByteArray{
        val bytes = ByteArray(2)
        bytes[0] = (value and 0xFFFFu).toByte()
        bytes[1] = (value.shr(8) and 0xFFFFu).toByte()
        return bytes
    }

    fun getAllRoadPoints():MutableList<Cell>{
        val allRoadPoints:MutableList<Cell> = ArrayList()
        for(i in 0 until rows){
            for(j in 0 until cols){
                if(matr[i][j].type == Cell.TypeOfCell.ROAD || matr[i][j].type == Cell.TypeOfCell.CASTLE){
                    allRoadPoints.add(matr[i][j])
                }
            }
        }
        return allRoadPoints
    }

    fun getMapDumpByteArray():MutableList<Byte>{
        val byteArr:MutableList<Byte> = ArrayList()
        for(i in 0 until rows){
            for(j in 0 until cols){
                uInt16ToByteArray(matr[i][j].terr.value.toUInt()).forEach { byteArr.add(it) }
            }
        }
        return byteArr
    }
}