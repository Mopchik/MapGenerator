import generator.FieldGenerator
import kotlin.math.pow
import kotlin.random.Random

fun main(args:Array<String>) {
    var nOfPlayers = -1
    var mapSize = -1
    print("Do you want to set options manually? 1 - YES, 0 - NO: ")
    val inp = readLine()?.toIntOrNull() ?: 0
    if (inp == 1) {
        print("Enter the number of players from 1 to 6: ")
        nOfPlayers = readLine()?.toIntOrNull() ?: -1
        if (nOfPlayers < 1 || nOfPlayers > 6) nOfPlayers = -1
        print("Enter the map size, 0 - is 32, 1 - 64, 2 - 128, 3 - 256: ")
        var poww = readLine()?.toIntOrNull() ?: -1
        if (poww < 0 || poww > 3) poww = -1
        mapSize = if (poww != -1) 1 + 32 * 2.0.pow(poww.toDouble()).toInt() else -1
    }
    if (nOfPlayers == -1 || mapSize == -1) {
        if (inp == 1)
            println("Wrong number of players or map size so the map was created manually.")
        nOfPlayers = Random.nextInt(2, 6)
        mapSize = 129
    }

    val field = FieldGenerator(mapSize,nOfPlayers).generateField()
    //field.show()
    BytesBuffer(field).writeToBytes()
    println("The result of this program is the file \"map.hmm\" in the project directory.")
}

