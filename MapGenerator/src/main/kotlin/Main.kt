import Generator.FieldGenerator
import java.lang.Math.pow
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

fun main() {
    var nOfPlayers:Int
    val mapSize:Int
    print("Хотите задать вручную, 1 - да, 0 - нет: ")
    val inp = readLine()?.toIntOrNull() ?: 0
    if(inp == 1) {
        print("Введите количество игроков от 1 до 6: ")
        nOfPlayers = readLine()?.toIntOrNull() ?: -1
        if(nOfPlayers < 1 || nOfPlayers > 6) nOfPlayers = -1
        print("Укажите размер карты, где 0 - это 32, 1 - 64, 2 - 128, 3 - 256: ")
        var poww = readLine()?.toIntOrNull() ?: -1
        if(poww < 0 || poww > 3) poww = -1
        mapSize = if(poww != -1) 1 + 32 * pow(2.0, poww.toDouble()).toInt() else -1
    } else {
        nOfPlayers = Random.nextInt(2, 6)
        mapSize = 65
    }
    if(nOfPlayers == -1 || mapSize == -1){
        println("Wrong number of players or map size.")
        return
    }
    val field = FieldGenerator(mapSize,nOfPlayers).generateField()
    field.show()
    BytesBuffer(field).writeToBytes(nOfPlayers)
}

