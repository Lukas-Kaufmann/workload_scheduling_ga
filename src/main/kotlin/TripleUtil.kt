public operator fun Triple<Int, Int, Int>.plus(other: Triple<Int, Int, Int>) = Triple(this.first + other.first, this.second + other.second, this.third + other.third)

public operator fun Triple<Int, Int, Int>.minus(other: Triple<Int, Int, Int>) = Triple(this.first - other.first, this.second - other.second, this.third - other.third)

public operator fun Triple<Int, Int, Int>.div(other: Triple<Int, Int, Int>) = Triple(this.first.toDouble() / other.first, this.second.toDouble() / other.second, this.third.toDouble() / other.third)

public operator fun Triple<Int, Int, Int>.times(n: Int) = Triple(this.first * n, this.second * n, this.third * n)