import org.jetbrains.letsPlot.export.ggsave
import org.jetbrains.letsPlot.geom.geomDensity
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.letsPlot
import sim.ComputeNode
import sim.WorkLoad

//TODO refactor to function that takes nodes and workloads as input and gives

fun randomNodesAndWorkloads(nrNodes: Int, nrWorkloads: Int) = Pair((0 until nrNodes).map { ComputeNode.newRandom(it) }, (0 until nrWorkloads).map {WorkLoad.newRandom(it)})

fun main() {

    var (nodes, workloads) = randomNodesAndWorkloads(2, 100) //TODO read from file
    val distribution = findSchedule(nodes, workloads)

    println("Distribution:")
    for ((node, loads) in distribution) {
        //TODO replace id with names
        println("Node \t ${node.id}:")
        for (l in loads) {
            print(" ${l.id} ");
        }
        println()

    }

    //TODO get schedule as output and display it
}


//val rand = java.util.Random(1)
//val n = 400
//val data = mapOf (
//    "rating" to List(n/2) { rand.nextGaussian() } + List(n/2) { rand.nextGaussian() * 1.5 + 1.5 },
//    "cond" to List(n/2) { "A" } + List(n/2) { "B" }
//)
//
//var p = letsPlot(data) + geomDensity { x = "rating"; color = "cond" } + ggsize(500, 250)
//ggsave(p, "density.png")
//
//val line = mapOf(
//    "height" to List(100) {it},
//    "other" to List(100) {100 - it}
//)
//var p2 = letsPlot(line)  + geomLine {x = "height"; y = "height"} + ggsize(500, 250)
//ggsave(p2, "line.png")