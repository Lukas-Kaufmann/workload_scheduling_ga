import sim.ComputeNode
import sim.WorkLoad

//TODO refactor to function that takes nodes and workloads as input and gives

fun randomNodesAndWorkloads(nrNodes: Int, nrWorkloads: Int) = Pair((1..nrNodes).map { ComputeNode(it) }, (1..nrWorkloads).map {WorkLoad.newRandom(it)})

fun main(args: Array<String>) {
    var p = randomNodesAndWorkloads(20, 200)
    var nodes = p.first
    var workloads = p.second

    findSchedule(nodes, workloads)

    //TODO get schedule as output and display it
}