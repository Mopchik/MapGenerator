package Generator

import CellsAndTypes.Cell
import CellsAndTypes.Decoration
import Field
import kotlin.random.Random

class FieldGenerator(val mapSize:Int, val nOfPlayers: Int) {
    private val field:Field = Field(mapSize, mapSize)
    private val nOfCastles = Random.nextInt(nOfPlayers, (nOfPlayers * mapSize / 32).toInt())
    private val nOfOwnerables:Int = Random.nextInt(nOfCastles * 2.toInt(), nOfCastles * 4)
    private val nOfMapItems:Int = Random.nextInt(nOfCastles * 3.toInt(), nOfCastles * 6)
    fun generateField():Field{
        val dif = mapSize / 20
        val graphGen = GraphGenerator(mapSize)
        val matrix = graphGen.generateGraphMatr()
        val n = matrix.size
        val pointsGen = PointsGenerator(n, field.rows, field.cols, dif)
        var roadPoints = pointsGen.generatePoints()

        roadPoints = ForceAlgorithm.modifyGraph(matrix, roadPoints, field.rows, field.cols, 100, dif)
        pointsGen.changePoints(roadPoints)
        field.placeGraph(matrix,roadPoints)

        val castlePoints = pointsGen.generateCastlePoints(nOfCastles, field)
        field.placeCastles(castlePoints)
        castlePoints.forEach { field.setTerrainType(it.first, it.second, CellTypeGenerator.genTerrainType()) }
        field.putAllTerrainType()

        val ownerablePoints = pointsGen.generateOwnerablePoints(nOfOwnerables, field)
        field.placeOwnerables(ownerablePoints)
        val mapItemPoints = pointsGen.genMapItems(field,nOfMapItems)
        field.placeMapItems(mapItemPoints)

        val strongCreepsPoints = pointsGen.generateStrongCreeps()
        strongCreepsPoints.forEach { field.placeCreep(it, Cell.TypeOfCreeps.fromInt(Random.nextInt(3,5))) }
        val smallCreepsPoints = pointsGen.genSmallCreeps(field)
        smallCreepsPoints.forEach { field.placeCreep(it, Cell.TypeOfCreeps.fromInt(Random.nextInt(1,3))) }
        val insaneCreepsPoints = pointsGen.genInsaneCreeps(field)
        insaneCreepsPoints.forEach { field.placeCreep(it, Cell.TypeOfCreeps.fromInt(Random.nextInt(5, 7))) }

        val decorationPoints = pointsGen.generateSmallDecorations(field)
        field.placeDecorations(decorationPoints, Decoration.TypeOfDecoration.SMALL)

        val playersCastles = pointsGen.genPlayersCastles(nOfPlayers, field)
        field.setPlayersCastles(playersCastles)

        return field
    }



}