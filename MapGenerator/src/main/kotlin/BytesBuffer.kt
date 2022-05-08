import cellsAndTypes.*
import java.io.File

class BytesBuffer(val field:Field) {
    fun writeToBytes(){
        val bytesBuff:MutableList<Byte> = ArrayList()

        // EMAP_FILE_HDR_KEY
        uIntToByteArray(BytesConstants.EMAP_FILE_HDR_KEY).forEach { bytesBuff.add(it) }
        // EMAP_FILE_VERSION
        uIntToByteArray(BytesConstants.EMAP_FILE_VERSION).forEach { bytesBuff.add(it) }
        // map.m_Siz
        bytesBuff.add(getMapSiz())
        // map.m_lngMask
        BytesConstants.m_lngMask.forEach{bytesBuff.add(it)}
        // text resources count
        BytesConstants.textRes.forEach{bytesBuff.add(it)}
        // map.m_MapVersion and map_MapAuthor
        BytesConstants.mapVersAndAuthor.forEach{bytesBuff.add(it)}

        // Players
        val players = field.getPlayersCastles()
        uInt16ToByteArray(players.size.toUInt()).forEach { bytesBuff.add(it) }
        for(i in 0 until players.size){
            bytesBuff.add(i.toByte()) // m_PlayerId
            bytesBuff.add(2) // m_PlayerTypeMask
            bytesBuff.add(1) // hasMainCastle
            uInt16ToByteArray(players[i].first.toUInt()).forEach { bytesBuff.add(it) }
            uInt16ToByteArray(players[i].second.toUInt()).forEach { bytesBuff.add(it) }
            bytesBuff.add(1) // create a hero here
        }

        val heroes_size = 0u
        uInt16ToByteArray(heroes_size).forEach { bytesBuff.add(it) }

        // Map Items
        val mapItems:MutableList<Cell> = field.getMapItemCells()
        uInt16ToByteArray(mapItems.size.toUInt()).forEach { bytesBuff.add(it) }
        mapItems.forEach { item ->
            bytesBuff.add(item.obj.getInt().toByte())
            uInt16ToByteArray(item.xy.first.toUInt()).forEach { bytesBuff.add(it) }
            uInt16ToByteArray(item.xy.second.toUInt()).forEach { bytesBuff.add(it) }
            for(i in 1..7)
                listOf<Byte>(-1, -1, 0, 0, 0, 0).forEach { bytesBuff.add(it) }
            val lastListOfByte = when(item.obj.getInt()){
                0 -> listOf<Byte>(0, 0, 0, 0, -1, 0, 0, 0, 0)
                else -> listOf<Byte>(0,0,0,0) // 2,3
            }
            lastListOfByte.forEach { bytesBuff.add(it) }
            if(item.obj.getInt() == MapItem.TypeOfItem.ARTIFACT.value){
                BytesConstants.randomArtId.forEach{bytesBuff.add(it)}
            }
        }

        // Guard
        //uInt16ToByteArray(0u).forEach { bytesBuff.add(it) }
        val guards = field.getGuardCells()
        uInt16ToByteArray(guards.size.toUInt()).forEach { bytesBuff.add(it) }
        guards.forEach{
            getGuardByte(it.creeps, it.xy).forEach{bytesBuff.add(it)}
        }

        val mapEvents_size = 0u
        uInt16ToByteArray(mapEvents_size).forEach { bytesBuff.add(it) }
        val visitables_size = 0u
        uInt16ToByteArray(visitables_size).forEach { bytesBuff.add(it) }

        // Mines
        //uInt16ToByteArray(0u).forEach { bytesBuff.add(it) }
        val ownerables = field.getOwnerableCells()
        uInt16ToByteArray(ownerables.size.toUInt()).forEach { bytesBuff.add(it) }
        ownerables.forEach {
            getOwnerableByte(it.obj.getInt(), it.xy).forEach { bytesBuff.add(it) }
        }

        // Castles
        //uInt16ToByteArray(0u).forEach { bytesBuff.add(it) }
        val castles = field.getCastleCells()
        uInt16ToByteArray(castles.size.toUInt()).forEach { bytesBuff.add(it) }
        castles.forEach{
            var owner:Byte = -1
            if(field.getPlayersCastles().contains(it.xy)) owner = field.getPlayersCastles().indexOf(it.xy).toByte()
            getCastleByte(it.obj.getInt(), getCastleIdOfType(it), it.xy, owner).forEach { bytesBuff.add(it) }
        }

        // Map Dump
        getMapDumpByteArray().forEach { bytesBuff.add(it) }

        // Decorations
        val decorations = field.getDecorationCells()
        uIntToByteArray(decorations.size.toUInt()).forEach{bytesBuff.add(it)}
        decorations.forEach { decoration ->
            uInt16ToByteArray(decoration.xy.first.toUInt()).forEach { bytesBuff.add(it) }
            uInt16ToByteArray(decoration.xy.second.toUInt()).forEach { bytesBuff.add(it) }
            genDecorationId(decoration).forEach { bytesBuff.add(it) }
        }
        // Roads
        //uIntToByteArray(0u).forEach { bytesBuff.add(it) }
        var allRoadPoints = field.getAllRoadPoints()
        uIntToByteArray(allRoadPoints.size.toUInt()).forEach { bytesBuff.add(it) }
        allRoadPoints.forEach {
            uInt16ToByteArray(it.xy.first.toUInt()).forEach { bytesBuff.add(it) }
            uInt16ToByteArray(it.xy.second.toUInt()).forEach { bytesBuff.add(it) }
            when(it.obj.getInt()){
                Road.TypeOfRoad.OK.value -> BytesConstants.roadOK_id.forEach{bytesBuff.add(it)}
                Road.TypeOfRoad.HARD.value, Road.TypeOfRoad.VERY_HARD.value ->
                    BytesConstants.roadOK_id.forEach{bytesBuff.add(it)}
                else -> BytesConstants.roadOK_id.forEach{bytesBuff.add(it)}
            }
        }
        ////showBytesBuff(bytesBuff)
        val arr:ByteArray = ByteArray(bytesBuff.size)
        for(i in 0 until bytesBuff.size){
            arr[i] = bytesBuff[i]
        }
        File("map.hmm").writeBytes(arr)
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

    fun genDecorationId(cell:Cell):List<Byte>{
        if(cell.obj.getInt() == Decoration.TypeOfDecoration.SMALL.value){
            return when(cell.terr){
                Cell.TypeOfTerrain.GRASS, Cell.TypeOfTerrain.DARK_GRASS ->
                    listOf(BytesConstants.flowersId, BytesConstants.pinesId).random()
                Cell.TypeOfTerrain.LIGHT_SAND, Cell.TypeOfTerrain.SAND ->
                    listOf(BytesConstants.palmId, BytesConstants.cactusId).random()
                Cell.TypeOfTerrain.DIRT, Cell.TypeOfTerrain.SOIL ->
                    //listOf(BytesConstants.rockId, BytesConstants.rockWithPlantId).random()
                    listOf(BytesConstants.flowersId, BytesConstants.pinesId).random()
                Cell.TypeOfTerrain.STONE, Cell.TypeOfTerrain.VOLCANO ->
                    listOf(BytesConstants.deadPlantId).random()
                else -> listOf(BytesConstants.snowTreeId).random() // snow
            }
        }
        return BytesConstants.snowTreeId
    }

    fun getOwnerableByte(numType:Int, pos:Pair<Int,Int>):MutableList<Byte>{
        val ownerableBytes:MutableList<Byte> = ArrayList()
        val thisId:List<Byte> = when(Ownerable.TypeOfOwnerable.fromInt(numType)){
            Ownerable.TypeOfOwnerable.GOLD_MINE -> BytesConstants.goldId
            Ownerable.TypeOfOwnerable.ORE_PIT -> BytesConstants.oreId
            Ownerable.TypeOfOwnerable.WOOD_SAWMILL -> BytesConstants.woodId
            Ownerable.TypeOfOwnerable.ALCHEMIST_LAB -> BytesConstants.alchemistId
            Ownerable.TypeOfOwnerable.GEMS_MINE -> BytesConstants.gemsId
            Ownerable.TypeOfOwnerable.CRYSTAL_MINE -> BytesConstants.crystalId
            Ownerable.TypeOfOwnerable.SULFUR_MINE -> BytesConstants.sulfurId
        }
        thisId.forEach{ ownerableBytes.add(it)}
        ownerableBytes.add(-1) // owner
        uInt16ToByteArray(pos.first.toUInt()).forEach { ownerableBytes.add(it) }
        uInt16ToByteArray(pos.second.toUInt()).forEach { ownerableBytes.add(it) }
        for(i in 1..7)
            listOf<Byte>(-1, -1, 0, 0, 0, 0).forEach { ownerableBytes.add(it) }
        return ownerableBytes
    }



    fun getGuardByte(force: Cell.TypeOfCreeps, pos:Pair<Int,Int>):MutableList<Byte>{
        val guardBytes:MutableList<Byte> = ArrayList()
        guardBytes.add(force.value.toByte())
        guardBytes.add(15) // random type of creatures
        listOf<Byte>(0, 0, 0, 0).forEach{guardBytes.add(it)}
        guardBytes.add(1) // disposition (true)
        guardBytes.add(0) // notGrow (false)
        uInt16ToByteArray(pos.first.toUInt()).forEach { guardBytes.add(it) }
        uInt16ToByteArray(pos.second.toUInt()).forEach { guardBytes.add(it) }
        listOf<Byte>(0, 0, 0, 0).forEach{guardBytes.add(it)} // message
        return guardBytes
    }

    fun getCastleByte(typeSize:Int, idOfType:UInt, pos:Pair<Int,Int>, owner:Byte):MutableList<Byte>{
        val castleBytes:MutableList<Byte> = ArrayList()
        val type = Castle.CastleType.fromInt(typeSize / 10)
        val size = Castle.CastleSize.fromInt(typeSize % 10)
        val thisId:List<Byte> = when(size){
            Castle.CastleSize.SMALL -> BytesConstants.castleSmallId
            Castle.CastleSize.MEDIUM -> BytesConstants.castleMediumId
            Castle.CastleSize.LARGE -> BytesConstants.castleLargeId
        }
        thisId.forEach{castleBytes.add(it)}
        uInt16ToByteArray(idOfType).forEach { castleBytes.add(it) }
        castleBytes.add(type.value.toByte())
        castleBytes.add(owner) // owner
        uInt16ToByteArray(pos.first.toUInt()).forEach { castleBytes.add(it) } // pos
        uInt16ToByteArray(pos.second.toUInt()).forEach { castleBytes.add(it) }
        for(i in 1..7){ // garrison
            listOf<Byte>(-1, -1, 0, 0, 0, 0).forEach{castleBytes.add(it)}
        }
        listOf<Byte>(0, 0, 0, 0).forEach{castleBytes.add(it)} // custom name
        uInt16ToByteArray(0u).forEach { castleBytes.add(it) } // isCustomized
        return castleBytes
    }

    fun getMapSiz():Byte{
        if(field.rows != field.cols) return -1
        when(field.rows - 1){
            32 -> return 0
            64 -> return 1
            128 -> return 2
            256 -> return 3
        }
        return -1
    }

    fun getCastleIdOfType(castle: Cell):UInt{
        val x = castle.xy.first
        val y = castle.xy.second
        if(x + 1 < field.rows && field.matr[x + 1][y].type == Cell.TypeOfCell.ROAD)
            return 52u
        else if(y + 1 < field.cols && field.matr[x][y+1].type == Cell.TypeOfCell.ROAD)
            return 51u
        else if(x - 1 >= 0 && field.matr[x-1][y].type == Cell.TypeOfCell.ROAD)
            return 52u
        else
            return 51u
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

    fun getMapDumpByteArray():MutableList<Byte>{
        val byteArr:MutableList<Byte> = ArrayList()
        for(i in 0 until field.rows){
            for(j in 0 until field.cols){
                uInt16ToByteArray(field.matr[i][j].terr.value.toUInt()).forEach { byteArr.add(it) }
            }
        }
        return byteArr
    }
}