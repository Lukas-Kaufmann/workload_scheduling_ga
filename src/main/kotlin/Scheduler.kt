import sim.ComputeNode
import sim.WorkLoad
import java.util.DoubleSummaryStatistics
import kotlin.math.log
import kotlin.math.pow
import kotlin.random.Random

typealias Distro = Map<ComputeNode, List<WorkLoad>>

fun findSchedule(nodes: List<ComputeNode>, workloads: List<WorkLoad>): Distro {
    var nodeBits = 1
    while (2f.pow(nodeBits) < nodes.size) {
        nodeBits+=1
    }
    var workBits = 1
    while (2f.pow(workBits) < workloads.size) {
        workBits +=1
    }
    val pairingSize = workBits + nodeBits
    val individualSize = pairingSize * workloads.size


    val populationSize = 20
    val population = (1..populationSize).map { (1..individualSize).map { if (Math.random() < 0.5) 0 else 1 } }

    fun evaluateDistribution(distro: Distro) = distro
            .map { (node, loads) ->
                //TODO check possible parallelism
                node.run(1000, loads.toMutableList())
            }
            .map {
                when {
                    it >= 1.2 -> (1.0 / it).pow(2.0)
                    it > 1 -> (-5 * it) + 6
                    it > 0.2 -> it
                    it >= 0 ->(-5 * it) + 1
                    else -> 0.0
                }
            }.average()


    fun genomeToDistribution(genome: List<Int>, nodes: Collection<ComputeNode>, loads: Collection<WorkLoad>) : Distro {
        val mutLoads = loads.toMutableList()

        val genomeIds = genome
            .chunked(pairingSize)
            .map { Pair(it.subList(0, workBits), it.subList(workBits, pairingSize)) }
            .map { Pair(grayToBin(it.first), grayToBin(it.second)) }
            .map { Pair(binToDec(it.first) % workloads.size, binToDec(it.second) % nodes.size)}

        var distro = mutableMapOf<ComputeNode, MutableList<WorkLoad>>()

        for ((workId, nodeId) in genomeIds) {
            val node = nodes.find { it.id == nodeId } ?: throw java.lang.IllegalStateException()
            var work = mutLoads.find { it.id >= workId }
            if (work == null) {
                work = mutLoads.first()
            }
            mutLoads.remove(work)

            distro.putIfAbsent(node, mutableListOf(work))?.add(work)
        }

        return distro
    }

    val algorithm = GeneticAlgorithm(
        population,
        score = { evaluateDistribution(genomeToDistribution(it, nodes, workloads)) },
        cross = ::twoPointCrossOver,
        mutate = { it.map { if (Math.random() < 0.1) it else if (Math.random() < 0.5) 0 else 1 } },
        select = ::rouletteWheelSelection
    )

    val result = algorithm.run()

    return genomeToDistribution(result, nodes, workloads)
}

//this method was provided by the author of the kotlin genetic algorithm class, keeping it because why not, its a oneliner
fun spaghettiCrossover(parents: Pair<List<Int>, List<Int>>) = parents.first.mapIndexed { index, i -> if (Math.random() > 0.5) i else parents.second[index]}

fun onePointCrossOver(parents: Pair<List<Int>, List<Int>>): List<Int> {
    val cross = Random.nextInt(parents.first.size)
    return parents.first.mapIndexed { index, b -> if (index >= cross) b else parents.second[index] }
}

fun twoPointCrossOver(parents: Pair<List<Int>, List<Int>>): List<Int> {
    val start = Random.nextInt(parents.first.size)
    val end = Random.nextInt(parents.first.size)

    var child = parents.first.toMutableList()

    var index = start
    while (end != index) {
        child[index] = parents.second[index]

        index = (index + 1) % parents.first.size
    }
    return child
}




fun grayToBin(gray: List<Int>): List<Int> {
    var bin = (1..gray.size).map { gray[0] }.toMutableList()

    for ((index, value ) in gray.withIndex()) {
        if (index == 0) {
            bin[index] = gray[index]
        } else if (value == 0) {
            bin[index] = bin[index-1]
        } else {
            bin[index] = bin[index-1].xor(1)
        }

    }
    return bin
}

fun binToDec(bin: List<Int>) : Int {
    var n = 0
    for ((index, value) in bin.reversed().withIndex()) {
        n += value * (1 shl index)
    }
    return n
}