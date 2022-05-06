import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

fun main() {
    val nOfCastles = 7
    val map_size = 129
    val dif = map_size / 20
    val rows = map_size
    val cols = map_size
    val n = Random.nextInt((map_size / 3.4).toInt(), (map_size / 1.2).toInt())
    val kOfEdges = (map_size.toDouble() / n) * (map_size.toDouble() / n) / Random.nextInt(25,50)
    val matrix = generateGraphMatr(n, kOfEdges)
    var roadPoints = generatePoints(n,rows,cols, dif)
    val field = Field(rows, cols)
    //field.placeGraph(matrix,roadPoints)
    //field.placeCastles(castlePoints)
    roadPoints = modifyGraph(matrix, roadPoints, rows, cols, 100, dif)
    val castlePoints = generateCastlePoints(matrix, roadPoints, nOfCastles)
    val strongCreepsPoints = generateStrongCreeps(roadPoints, castlePoints)
    field.placeGraph(matrix,roadPoints)
    field.placeCastles(castlePoints)
    field.placeStrongCreeps(strongCreepsPoints)
    field.putAllTerrainType()
    field.show()
    field.writeToBytes()
}

fun generatePoints(n:Int, rows:Int, cols:Int, dif:Int):MutableList<Pair<Int,Int>>{
    // var area:MutableList<MutableList<Pair<Int,Int>>> = ArrayList<MutableList<Pair<Int,Int>>>()
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
        allPoints.remove(r)
    }
    return neededPoints
}

fun generateGraphMatr(n:Int, kOfEdges:Double):MutableList<MutableList<Int>>{
    val matrix:MutableList<MutableList<Int>> = ArrayList()
    for(i in 0 until n){
        matrix.add(ArrayList())
    }
    val notConnected = (0 until n).toMutableList()
    val isBlack = MutableList(n){false}
    isBlack[0] = false
    notConnected.remove(0)
    generateEdges(matrix, notConnected, 0, isBlack, kOfEdges)
    return matrix
}

fun generateCastlePoints(matrix:MutableList<MutableList<Int>>, points:MutableList<Pair<Int,Int>>,
                    numOfCastles:Int):MutableList<Pair<Int,Int>>{
    val castles:MutableList<Pair<Int,Int>> = ArrayList()
    val pointsCopy:MutableList<Pair<Int,Int>> = ArrayList()
    for(i in 0 until points.size) {
        pointsCopy.add(points[i])
    }
    //for(i in 0 until points.size){
    //    if(matrix[i].size == 1 && castles.size < numOfCastles) {
    //        castles.add(points[i])
    //        pointsCopy.removeIf { it.first == points[i].first && it.second == points[i].second }
    //    }
    //}
    while(castles.size < numOfCastles){
        val r = pointsCopy.random()
        castles.add(r)
        pointsCopy.remove(r)
    }
    return castles
}

fun generateStrongCreeps(points: MutableList<Pair<Int, Int>>,
                         castles: MutableList<Pair<Int,Int>>):MutableList<Pair<Int,Int>>{
    val strongCreepsPoints:MutableList<Pair<Int,Int>> = ArrayList()
    for(p in points){
        if(!castles.contains(p)){
            if(Random.nextDouble(0.0, 10.0) < 5.0){
                strongCreepsPoints.add(p)
            }
        }
    }
    return strongCreepsPoints
}

fun generateEdges(matrix:MutableList<MutableList<Int>>, notConnected:MutableList<Int>, v:Int,
                  isBlack:MutableList<Boolean>, kOfEdges: Double){
    if(notConnected.isNotEmpty()) {
        val t = notConnected.random()
        matrix[v].add(t)
        matrix[t].add(v)
        notConnected.remove(t)
    }
    var i = 0
    var koef = 2.0
    while(i < notConnected.size){
        if(matrix[v].size > 2) koef /= (matrix[v].size)
        if(Random.nextDouble(matrix.size.toDouble()) < koef) {
            matrix[v].add(notConnected[i])
            matrix[notConnected[i]].add(v)
            notConnected.removeAt(i)
        } else i++
    }
    val range = (0 until matrix.size).filter{it != v}.toMutableList()
    val koefOfFirst = kOfEdges
    val koefOfNext = 0.4
    koef = koefOfFirst
    range.forEach {
        if(!matrix[v].contains(it)) {
            if(matrix[v].size > 2) koef /= (matrix[v].size - 1)
            if (Random.nextDouble(matrix.size.toDouble()) <=koef) {
                matrix[v].add(it)
                matrix[it].add(v)
                if(koef == koefOfFirst)
                    koef = koefOfNext
                if(notConnected.contains(it)){
                    notConnected.remove(it)
                }
            }
        }
    }
    val matrixCopy = ArrayList<Int>()
    matrix[v].forEach { matrixCopy.add(it) }
    matrixCopy.forEach{
        if(!isBlack[it]){
            isBlack[it] = true
            generateEdges(matrix, notConnected, it, isBlack, kOfEdges)
        }
    }
}

fun showMatrix(matrix:MutableList<MutableList<Int>>){
    matrix.forEach { println(it) }
}

fun modifyGraph(matr:MutableList<MutableList<Int>>, points:MutableList<Pair<Int,Int>>, rows:Int,
                cols:Int, iterations:Int, dif:Int):MutableList<Pair<Int,Int>>{
    val n = points.size
    val l = sqrt(rows*cols.toDouble() / n)
    var changes:MutableList<Pair<Double,Double>> = ArrayList()
    var tempPoints:MutableList<Pair<Double, Double>> = ArrayList()
    var temperature = l * 2
    for(i in 0 until n){
        tempPoints.add(Pair(points[i].first.toDouble(), points[i].second.toDouble()))
    }
    for(count in 1..iterations) {
        val center = getCenter(tempPoints, n)
        for (i in 0 until n) {
            // var firstDif = 0.0
            // var secondDif = 0.0
            // for (j in 0 until n) {
            //     if (i != j) {
            //         val changeNow = getSprAndRep(tempPoints[i], tempPoints[j], n, area)
            //         firstDif += changeNow.first.first
            //         secondDif += changeNow.first.second
            //         // if(count == iterations - 1)
            //         //     println("$i $j     Rep: ${changeNow.first.first}      ${changeNow.first.second}")
            //         if (matr[i].contains(j)) {
            //             firstDif += changeNow.second.first
            //             secondDif += changeNow.second.second
            //             //println("Spr: ${changeNow.second.first}       ${changeNow.second.second}")
            //         }
            //         // val newFirstI = modifyNumInBounds(tempPoints[i].first + firstDif, 0, rows)
            //         // val newSecI = modifyNumInBounds(tempPoints[i].second + secondDif, 0, cols)
            //         // val newFirstJ = modifyNumInBounds(tempPoints[j].first - firstDif, 0, rows)
            //         // val newSecJ = modifyNumInBounds(tempPoints[j].second - secondDif, 0, cols)
            //         // tempPoints[i] = Pair(newFirstI,newSecI)
            //         // tempPoints[j] = Pair(newFirstJ,newSecJ)
            //     }
            // }
            // val newFirstI = modifyNumInBounds(tempPoints[i].first + firstDif, 0, rows)
            // val newSecI = modifyNumInBounds(tempPoints[i].second + secondDif, 0, cols)
            //val newFirstJ = modifyNumInBounds(tempPoints[j].first + firstDif, 0, rows)
            //val newSecJ = modifyNumInBounds(tempPoints[j].second + secondDif, 0, cols)
            val changeI = calculateForce(tempPoints, i, center, temperature, matr, l, n, rows, cols, dif)
            changes.add(changeI)
            //tempPoints[i] = Pair(newFirstI,newSecI)
        }
        tempPoints = changes
        changes = ArrayList()
        temperature /= 1.04

        // readLine()
        // val field = Field(rows,cols)
        // val answ:MutableList<Pair<Int,Int>> = ArrayList()
        // for(i in 0 until n){
        //     answ.add(Pair(tempPoints[i].first.toInt(), tempPoints[i].second.toInt()))
        // }
        // field.placeGraph(matr,answ)
        // field.show()
    }
    val ans:MutableList<Pair<Int,Int>> = ArrayList()
    for(i in 0 until n){
        ans.add(Pair(tempPoints[i].first.toInt(), tempPoints[i].second.toInt()))
    }
    return ans
}

fun calculateForce(tempPoints:MutableList<Pair<Double,Double>>, i:Int, center:Pair<Double,Double>, temp:Double,
                   matr:MutableList<MutableList<Int>>, l:Double, n:Int, rows:Int, cols:Int, dif:Int):Pair<Double,Double>{
    var firstDif = 0.0
    var secondDif = 0.0
    val gravityK = 0.5 + matr[i].size / 2.0
    //val gravityK = matr[i].size.toDouble()
    for (j in 0 until n) {
        if (i != j) {
            var pairDist = Pair(tempPoints[j].first - tempPoints[i].first,
                tempPoints[j].second - tempPoints[i].second)
            if(abs(pairDist.first - 0.0)  <= Double.MIN_VALUE && abs(pairDist.second - 0.0) <= Double.MIN_VALUE){
                pairDist = Pair(Random.nextDouble(-0.1,0.1), Random.nextDouble(-0.1,0.1))
            }
            val distanceSQ = pairDist.first*pairDist.first + pairDist.second*pairDist.second
            val repulsion = getRep(pairDist, l, distanceSQ)
            firstDif += repulsion.first
            secondDif += repulsion.second
            if (matr[i].contains(j)) {
                val attraction:Pair<Double,Double>
                if(matr[i].size == 1){
                    // attraction = Pair(attraction.first * 2 , attraction.second * 2)
                    attraction = getAttr(pairDist, l / 20, distanceSQ, gravityK)
                } else{
                    attraction = getAttr(pairDist, l, distanceSQ, gravityK)
                }
                firstDif += attraction.first
                secondDif += attraction.second
            }
        }
    }
    val gravity = getGravity(i, matr, tempPoints, center, 1.3)
    firstDif += gravity.first
    secondDif += gravity.second
    val koef = 0.5
    firstDif *= koef
    secondDif *= koef
    val force = changeForceForTemparature(Pair(firstDif,secondDif), temp)
    val newFirstI = modifyNumInBounds(tempPoints[i].first + force.first, dif, rows - dif)
    val newSecI = modifyNumInBounds(tempPoints[i].second + force.second, dif, cols - dif)
    return Pair(newFirstI, newSecI)
}

fun changeForceForTemparature(force:Pair<Double,Double>, t:Double):Pair<Double,Double>{
    val dist = sqrt(force.first*force.first + force.second*force.second)
    if(dist <=t) return force
    return Pair(force.first / dist * t, force.second / dist * t)
}

fun modifyNumInBounds(n:Double, lowerB:Int, upperB:Int):Double{
    if(n < lowerB)
        return lowerB.toDouble()
    if(n > upperB - 1)
        return (upperB - 1).toDouble()
    return n
}

fun getRep(pairDist:Pair<Double,Double>, l:Double, distanceSq:Double):Pair<Double, Double>{
    val kOfRep = (-l * l / distanceSq)
    //val kOfRep = (-l * l / distanceSq / sqrt(distanceSq))
    return Pair((kOfRep * pairDist.first), (kOfRep * pairDist.second))
}

fun getAttr(pairDist:Pair<Double,Double>, l:Double, distanceSq:Double, gravityK:Double):Pair<Double,Double>{
    //val kOfAttr = (sqrt(distanceSq) / l )
    val kOfAttr = (distanceSq / (l * gravityK) / sqrt(distanceSq))
    return Pair((kOfAttr * pairDist.first), (kOfAttr * pairDist.second))
}

// correct
fun getCenter(tempPoints:MutableList<Pair<Double,Double>>, n:Int):Pair<Double,Double>{
    var sumCoordinates = Pair(0.0,0.0)
    for (j in 0 until n) {
        sumCoordinates = Pair(
            sumCoordinates.first + tempPoints[j].first,
            sumCoordinates.second + tempPoints[j].second
        )
    }
    return Pair(sumCoordinates.first / n, sumCoordinates.second / n)
}

fun getGravity(i:Int, matr:MutableList<MutableList<Int>>, tempPoints: MutableList<Pair<Double, Double>>,
               center:Pair<Double,Double>, koef:Double):Pair<Double,Double>{
    val degK = 0.5 + matr[i].size / 2.0
    //val degK = matr[i].size.toDouble()
    val outOfCenter = Pair(center.first - tempPoints[i].first, center.second - tempPoints[i].second)
    return Pair(degK * koef * outOfCenter.first, degK * koef * outOfCenter.second)
}
