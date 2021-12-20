import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import java.util.stream.Collectors

inline fun measureTimeMillis(block: () -> Unit): Long {
    val start = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - start
}

fun readFile(name: String) : List<String> {
    val reader = BufferedReader(InputStreamReader(File(name).inputStream()))

    var line = reader.readLine()

    val list: MutableList<String> = mutableListOf()
    while (line != null) {
        list.add(line)
        line = reader.readLine()
    }
    return list
}

fun main() {
    val list = readFile("input2.txt")

    list.forEach {
        addData(it.chars().mapToObj { it.toChar().toString() }.collect(Collectors.toList()))
    }



}

fun funkcija() : Int {
    return 1
}




private val listica: MutableList<List<Int>> = ArrayList()
private val lowestPoints: MutableSet<Point> = HashSet()
private val basins: MutableSet<Set<Point>> = HashSet()

internal enum class Direction(val row: Int, val column: Int) {
    Gore(-1, 0), Dole(1, 0), Lijevo(0, -1), Desno(0, 1);
}

fun addData(splitData: List<String>) {
    listica.add(splitData.stream().map { s: String -> s.toInt() }.collect(Collectors.toList()))
}

/**
 * Neka ideja je proci kroz listu i na svakom mjestu provjeriti da li je njegov broj manji od susjeda.
 * Ako jeste, onda je to basins, ali treba svakako raditi BFS.
 */

fun findRiskLevelSum(): Int {
    val visited = Array(listica.size) {
        BooleanArray(
            listica[0].size
        )
    }
    var sum = 0
    for (rowCount in listica.indices) {
        for (columnCount in listica[0].indices) {
            val value = listica[rowCount][columnCount]
            var neighbourCount = 0
            var smallerThanNeighboursCount = 0
            for (direction in Direction.values()) {
                val newRowCount = rowCount + direction.row
                val newColumnCount = columnCount + direction.column
                if (newRowCount > -1 && newRowCount < listica.size && newColumnCount > -1 && newColumnCount < listica[0].size) {
                    val newValue = listica[newRowCount][newColumnCount]
                    neighbourCount++
                    if (value < newValue) {
                        smallerThanNeighboursCount++
                    }
                }
            }
            if (smallerThanNeighboursCount == neighbourCount) {
                val point = Point(rowCount, columnCount, value)
                val basin: MutableSet<Point> = HashSet()
                lowestPoints.add(point)
                sum += value + 1
                BFSPokusaj(point, visited, listica, basin)
                basins.add(basin)
            }
        }
    }
    return sum
}

fun getProductOf3LargestBasinNodeCounts(): Int {
    return basins.stream()
        .map { obj: Set<Point> -> obj.size }
        .sorted(Collections.reverseOrder())
        .limit(3)
        .reduce(1) { currentProduct: Int, value: Int -> currentProduct * value }
}

/**
 * Neki jadni pokusaj BFSa
 */
private fun BFSPokusaj(
    startPoint: Point,
    visited: Array<BooleanArray>,
    cavernFloor: MutableList<List<Int>>,
    basinSet: MutableSet<Point>
) {
    val toVisit: Queue<Point> = LinkedList()
    toVisit.offer(startPoint)
    // for debugging
    var depth = 0
    while (!toVisit.isEmpty()) {
        val point = toVisit.poll()
        if (visited[point.row][point.column]) {
            continue
        }
        visited[point.row][point.column] = true
        basinSet.add(point)
        for (direction in Direction.values()) {
            val newRowCount = point.row + direction.row
            val newColumnCount = point.column + direction.column
            if (newRowCount > -1 && newRowCount < cavernFloor.size && newColumnCount > -1 && newColumnCount < cavernFloor[0].size &&
                !visited[newRowCount][newColumnCount]
            ) {
                val value = cavernFloor[newRowCount][newColumnCount]
                if (value != 9) {
                    val newPoint = Point(newRowCount, newColumnCount, value)
                    toVisit.offer(newPoint)
                }
            }
        }
        depth++
    }
}

private class Point(val row: Int, val column: Int, val value: Int)