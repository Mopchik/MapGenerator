class BytesConstants {
    companion object{
        val EMAP_FILE_HDR_KEY:UInt = 0x76235278u
        val EMAP_FILE_VERSION = 0x19u
        val m_lngMask = listOf<Byte>(1, 0, 0, 0)
        val textRes = listOf<Byte>(2, 0, 0, 0)
        val mapVersAndAuthor = listOf<Byte>(15, 0, 0, 0, 77, 0, 97, 0, 112, 0, 32, 0, 68, 0, 101, 0,
            115, 0, 99, 0, 114, 0, 105, 0, 112, 0, 116, 0, 105, 0, 111, 0,
            110, 0, 2, 0, 19, 0, 0, 0, 68, 0, 101, 0, 102, 0, 97, 0,
            117, 0, 108, 0, 116, 0, 32, 0, 100, 0, 101, 0, 115, 0, 99, 0,
            116, 0, 105, 0, 112, 0, 116, 0, 105, 0, 111, 0, 110, 0, 8, 0,
            0, 0, 77, 0, 97, 0, 112, 0, 32, 0, 110, 0, 97, 0, 109, 0,
            101, 0, 1, 0, 7, 0, 0, 0, 78, 0, 101, 0, 119, 0, 32, 0,
            77, 0, 97, 0, 112, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            -1, -1, -1, -1, 0, 0, 0, 0)
        val goldId = listOf<Byte>(9, 0, 0, 0, 103, 0, 111, 0, 108, 0, 100, 0,95, 0, 109, 0, 105, 0, 110, 0, 101, 0)
        val oreId = listOf<Byte>(8, 0, 0, 0, 111, 0, 114, 0, 101, 0, 95, 0, 109, 0, 105, 0, 110, 0, 101, 0)
        val woodId = listOf<Byte>(12, 0, 0, 0, 119, 0, 111, 0, 111, 0, 100, 0, 95, 0, 115, 0,
            97, 0, 119, 0, 109, 0, 105, 0, 108, 0, 108, 0)
        val alchemistId = getBytesByStr("0b 00 00 00 6d 00 65 00 72 00 63 00 75 " +
                "00 72 00 79 00 5f 00 6c 00 61 00 62 00")
        val gemsId = getBytesByStr("09 00 00 00 67 00 65 00 6d 00 73 00 5f 00 6d 00 " +
                "69 00 6e 00 65 00")
        val crystalId = getBytesByStr("0c 00 00 00 63 00 72 00 79 00 73 00 74 00 61 00 " +
                "6c 00 5f 00 6d 00 69 00 6e 00 65 00")
        val sulfurId = getBytesByStr("0b 00 00 00 73 00 75 00 6c 00 66 00 75 00 72 00 " +
                "5f 00 6d 00 69 00 6e 00 65 00")
        val roadOK_id = listOf<Byte>(10, 0, 0, 0, 115, 0, 116, 0, 111, 0, 110, 0, 101, 0, 95, 0,
            114, 0, 111, 0, 97, 0, 100, 0)
        val roadHard_id = listOf<Byte>(9, 0, 0, 0, 100, 0, 105, 0, 114, 0, 116, 0, 95, 0, 114, 0,
            111, 0, 97, 0, 100, 0)
        val castleSmallId = listOf<Byte>(8, 0, 0, 0, 115, 0, 109, 0, 97, 0, 108, 0, 108, 0, 95, 0,
            48, 0)
        val castleMediumId = listOf<Byte>(9, 0, 0, 0, 109, 0, 101, 0, 100, 0, 105, 0, 117, 0, 109, 0,
            95, 0, 48, 0)
        val castleLargeId = listOf<Byte>(8, 0, 0, 0, 108, 0, 97, 0, 114, 0, 103, 0, 101, 0, 95, 0,
            48, 0)
        val palmId = getBytesByStr("07 00 00 00 70 00 61 00 6c 00 6d 00 73 00 5f 00 " +
                "38 00")
        val flowersId = getBytesByStr("09 00 00 00 66 00 6c 00 6f 00 77 00 65 00 72 00 " +
                "73 00 5f 00 33 00")
        val pinesId = getBytesByStr("07 00 00 00 70 00 69 00 6e 00 65 00 73 00 5f 00 " +
                "31 00")
        val rockId = getBytesByStr("08 00 00 00 72 00 6f 00 63 00 6b 00 73 00 5f 00 " +
                "32 00 38 00")
        val deadPlantId = getBytesByStr("09 00 00 00 64 00 70 00 6c 00 61 00 6e 00 74 00 73 " +
                "00 5f 00 32 00")
        val rockWithPlantId = getBytesByStr("08 00 00 00 72 00 6f 00 63 00 6b 00 73 00 5f 00 " +
                "31 00 37 00")
        val snowTreeId = getBytesByStr("09 00 00 00 64 00 70 00 6c 00 61 00 6e 00 74 00 " +
                "73 00 5f 00 39 00")
        val cactusId = getBytesByStr("08 00 00 00 63 00 61 00 63 00 74 00 75 00 73 00 5f " +
                "00 35 00")
        val randomArtId = getBytesByStr("0e 00 00 00 52 00 61 00 6e 00 64 00 6f 00 6d 00 " +
                "41 00 72 00 74 00 69 00 66 00 61 00 63 00 74 00")
        fun getBytesByStr(nums:String):MutableList<Byte>{
            val bytes:MutableList<Byte> = ArrayList()
            for(i in 0 until nums.length - 1 step 3){
                val n =nums.subSequence(i, i+2)
                val sum = getIntBySymHEX(n[1]) + getIntBySymHEX(n[0]) * 16
                bytes.add(sum.toByte())
            }
            return bytes
        }
        fun getIntBySymHEX(c:Char):Int{
            return when(c){
                'a' -> 10
                'b' -> 11
                'c' -> 12
                'd' -> 13
                'e' -> 14
                'f' -> 15
                else -> c.toString().toInt()
            }
        }
    }
}