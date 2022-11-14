import sim.ComputeNode
import sim.WorkLoad
import sim.WorkLoadLevel
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

typealias Distro = Map<ComputeNode, List<WorkLoad>>

fun findSchedule(nodes: List<ComputeNode>, workloads: List<WorkLoad>, plotName: String = "fitness_${nodes.size}_nodes_${workloads.size}_workloads.png"): Distro {

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
                val available = node.maxPerStepPerCore * node.cores
                val highEffortUtil = loads.filter { it.level == WorkLoadLevel.ALWAYS_RUNNING }.fold(Triple(0, 0, 0)) {
                        sum, elem ->
                    sum + elem.maxRequiredResources
                }

                val lowEffortUtil = loads.filter { it.level == WorkLoadLevel.BEST_EFFORT }.fold(Triple(0, 0, 0)) {
                        sum, elem ->
                    sum + elem.requiredResources
                }


                val highRate = (highEffortUtil / available).toList().average()
                val lowRate = (lowEffortUtil / available).toList().average()

                if (highRate > 1.0) -1.0 else lowRate

            }
            .map {
                when {
                    it >= 1 -> (1.0 / it).pow(2.0)
                    it > 0.2 -> it.pow(5.0)
                    it >= 0 -> -sqrt(5*it) + 1
                    else -> 0.0
                }
            }
        .average()


    fun genomeToDistribution(genome: List<Int>, nodes: Collection<ComputeNode>, loads: Collection<WorkLoad>) : Distro {

        val genomeIds = genome
            .chunked(pairingSize)
            .map { Pair(it.subList(0, workBits), it.subList(workBits, pairingSize)) }
            .map { Pair(grayToBin(it.first), grayToBin(it.second)) }
            .map { Pair(binToDec(it.first) % workloads.size, binToDec(it.second) % nodes.size)}

        val distro = mutableMapOf<ComputeNode, MutableList<WorkLoad>>()

        val mutLoads = loads.toMutableList()

        for ((workId, nodeId) in genomeIds) {
            val node = nodes.find { it.id == nodeId } ?: throw java.lang.IllegalStateException()
            var work = mutLoads.find { it.id >= workId }
            if (work == null) {
                work = mutLoads.first()
            }
            mutLoads.remove(work)

            distro.putIfAbsent(node, mutableListOf(work))?.add(work)
        }

        for (node in nodes) {
            distro.putIfAbsent(node, mutableListOf())
        }

        return distro
    }

    fun switchNodeIds(genome: List<Int>): List<Int> {
        if (Math.random() < 0.9) {
               return genome
        }
        val mutGenome = genome.toMutableList()

        repeat(10) {
            val nodePos1 = Random.nextInt(0, nodes.size)
            val nodePos2 = Random.nextInt(0, nodes.size)

            val nodeRange1 = (nodePos1 * pairingSize)+workBits+1 until (nodePos1+1)*pairingSize
            val nodeRange2 = (nodePos2 * pairingSize)+workBits+1 until (nodePos2+1)*pairingSize

            val list1 = nodeRange1.toList()
            val list2 = nodeRange2.toList()
            for (i in 0 until (nodeRange1.last - nodeRange1.first)) {
                val temp = mutGenome[list1[i]]
                mutGenome[list1[i]] = mutGenome[list2[i]]
                mutGenome[list2[i]] = temp
            }
        }
        return mutGenome
    }

    val algorithm = GeneticAlgorithm(
        population,
        score = { evaluateDistribution(genomeToDistribution(it, nodes, workloads)) },
        cross = {switchNodeIds(twoPointCrossOver(it))},
        mutate = { individual -> individual.map { if (Math.random() < 0.1) it else it.xor(1) } },
        select = ::selection
    )

    val result = algorithm.run()

    plotFitness(result.second, plotName)

    return genomeToDistribution(result.first, nodes, workloads)
}

