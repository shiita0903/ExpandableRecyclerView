package jp.shiita.expandablerecyclerview

data class Parent(
        val name: String,
        val children: List<Child>
)

data class Child(
    val name: String,
    var visible: Boolean = false
)