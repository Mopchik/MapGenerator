import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

class ForceAlgorithm {
    companion object{
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
        val degK = 2 + matr[i].size / 2.0
        //val degK = matr[i].size.toDouble()
        val outOfCenter = Pair(center.first - tempPoints[i].first, center.second - tempPoints[i].second)
        return Pair(degK * koef * outOfCenter.first, degK * koef * outOfCenter.second)
    }
    }

}