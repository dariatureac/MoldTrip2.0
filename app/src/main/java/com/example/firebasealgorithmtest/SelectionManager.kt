package com.example.firebasealgorithmtest

object SelectionManager {
    private val selectedItemsMap = mutableMapOf<String, MutableSet<Int>>() // Region -> indices

    fun selectItem(region: String, index: Int) {
        val items = selectedItemsMap.getOrPut(region) { mutableSetOf() }
        items.add(index)
    }

    fun unselectItem(region: String, index: Int) {
        selectedItemsMap[region]?.remove(index)
    }

    fun isItemSelected(region: String, index: Int): Boolean {
        return selectedItemsMap[region]?.contains(index) == true
    }

    fun getSelectedItems(region: String): Set<Int> {
        return selectedItemsMap[region] ?: emptySet()
    }

    fun getAllSelections(): Map<String, Set<Int>> {
        return selectedItemsMap.mapValues { it.value.toSet() }
    }

    fun clearAllSelections() {
        selectedItemsMap.clear()
    }
}
