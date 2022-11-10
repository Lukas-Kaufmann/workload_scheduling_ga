import sim.ComputeNode
import sim.WorkLoad
import java.io.File

fun randomNodesAndWorkloads(nrNodes: Int, nrWorkloads: Int) = Pair((0 until nrNodes).map { ComputeNode.newRandom(it) }, (0 until nrWorkloads).map {WorkLoad.newRandom(it)})

fun main() {
    val randomValues = listOf(
        Pair(20, 200),
        Pair(20, 400),
        Pair(20, 50),
        Pair(10, 20),
        Pair(20, 20),
        Pair(50, 500),
        Pair(100, 200),
        Pair(100, 1000)
    )

    for ((nrNodes, nrWork) in randomValues) {
        val (nodes, workloads) = randomNodesAndWorkloads(nrNodes, nrWork)
        val distro  = findSchedule(nodes, workloads)
        distro.writeToFile("rand_distro_${nrNodes}_x_${nrWork}")
    }

    //TODO read some from file

}

fun Distro.exportString(): String {
    var s = ""
    for ((node, loads) in this) {
        s += "Node ${node.id.toString().padStart(10, ' ')} \t"
        for (load in loads) {
            s += "Job ${load.id} "
        }
        s += "\n"
    }
    return s
}

fun Distro.writeToFile(filename: String) = File("schedules/$filename.txt").writeText(this.exportString())