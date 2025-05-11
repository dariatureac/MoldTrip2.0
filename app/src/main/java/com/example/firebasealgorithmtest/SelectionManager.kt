package com.example.firebasealgorithmtest

object SelectionManager {
    private val selectedSpotsSet = mutableSetOf<Int>() // Set to track selected spot IDs

    // Add an ID to the selection
    fun selectSpot(id: Int) {
        selectedSpotsSet.add(id)
    }

    // Remove an ID from the selection
    fun unselectSpot(id: Int) {
        selectedSpotsSet.remove(id)
    }

    // Check if an ID is selected
    fun isSpotSelected(id: Int): Boolean {
        return selectedSpotsSet.contains(id)
    }

    // Get all selected spots as a set of IDs
    fun getSelectedSpots(): Set<Int> {
        return selectedSpotsSet
    }

    // Clear all selections
    fun clearAllSelections() {
        selectedSpotsSet.clear()
    }
}