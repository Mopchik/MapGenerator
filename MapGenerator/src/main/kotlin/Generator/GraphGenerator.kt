package Generator

import kotlin.random.Random

class GraphGenerator(val mapSize:Int) {
    private var matr:MutableList<MutableList<Int>>
    private val n:Int
    private val kOfEdges:Double
    init{
        matr = ArrayList()
        n = Random.nextInt((mapSize / 3.0).toInt(), (mapSize).toInt())
        kOfEdges = (mapSize.toDouble() / n) * (mapSize.toDouble() / n) / Random.nextInt(20,30)
    }
    fun generateGraphMatr():MutableList<MutableList<Int>>{
        for(i in 0 until n){
            matr.add(ArrayList())
        }
        val notConnected = (0 until n).toMutableList()
        val isBlack = MutableList(n){false}
        isBlack[0] = false
        notConnected.remove(0)
        generateEdges(notConnected, 0, isBlack)
        return matr
    }

    private fun generateEdges(notConnected:MutableList<Int>, v:Int, isBlack:MutableList<Boolean>){
        if(notConnected.isNotEmpty()) {
            val t = notConnected.random()
            matr[v].add(t)
            matr[t].add(v)
            notConnected.remove(t)
        }
        var i = 0
        var koef = 2.0
        // while(i < notConnected.size){
        //     if(matr[v].size > 2) koef /= (matr[v].size)
        //     if(Random.nextDouble(matr.size.toDouble()) < koef) {
        //         matr[v].add(notConnected[i])
        //         matr[notConnected[i]].add(v)
        //         notConnected.removeAt(i)
        //     } else i++
        // }
        val range = (0 until matr.size).filter{it != v}.toMutableList()
        val koefOfFirst = kOfEdges
        val koefOfNext = 0.4
        koef = koefOfFirst
        range.forEach {
            if(!matr[v].contains(it)) {
                if(matr[v].size >= 2) koef /= (matr[v].size)
                if (Random.nextDouble(matr.size.toDouble()) <=koef) {
                    matr[v].add(it)
                    matr[it].add(v)
                    if(koef == koefOfFirst)
                        koef = koefOfNext
                    if(notConnected.contains(it)){
                        notConnected.remove(it)
                    }
                }
            }
        }
        val matrixCopy = ArrayList<Int>()
        matr[v].forEach { matrixCopy.add(it) }
        matrixCopy.forEach{
            if(!isBlack[it]){
                isBlack[it] = true
                generateEdges(notConnected, it, isBlack)
            }
        }
    }
}