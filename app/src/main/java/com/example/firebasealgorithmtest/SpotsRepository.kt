package com.example.firebasealgorithmtest

// SpotsRepository.kt

object SpotsRepository {
    // List of spots with unique IDs, image resource ID, and description text resource ID
    val spots = listOf(
        // Central Moldova
        Spot(1, R.drawable.central1, R.string.central_1_text),
        Spot(2, R.drawable.central2, R.string.central_2_text),
        Spot(3, R.drawable.central3, R.string.central_3_text),
        Spot(4, R.drawable.central4, R.string.central_4_text),
        Spot(5, R.drawable.central5, R.string.central_5_text),

        // Southern Moldova
        Spot(6, R.drawable.southern1, R.string.southern_1_text),
        Spot(7, R.drawable.southern2, R.string.southern_2_text),
        Spot(8, R.drawable.southern3, R.string.southern_3_text),
        Spot(9, R.drawable.southern4, R.string.southern_4_text),
        Spot(10, R.drawable.southern5, R.string.southern_5_text),
        Spot(11, R.drawable.southern6, R.string.southern_6_text),
        Spot(12, R.drawable.southern7, R.string.southern_7_text),
        Spot(13, R.drawable.southern8, R.string.southern_8_text),
        Spot(14, R.drawable.southern9, R.string.southern_9_text),
        Spot(15, R.drawable.southern10, R.string.southern_10_text),
        Spot(16, R.drawable.southern11, R.string.southern_11_text),

        // Northern Moldova
        Spot(17, R.drawable.northern1, R.string.northern_1_text),
        Spot(18, R.drawable.northern2, R.string.northern_2_text),
        Spot(19, R.drawable.northern3, R.string.northern_3_text),
        Spot(20, R.drawable.northern4, R.string.northern_4_text),
        Spot(21, R.drawable.northern5, R.string.northern_5_text),
        Spot(22, R.drawable.northern6, R.string.northern_6_text),
        Spot(23, R.drawable.northern7, R.string.northern_7_text),
        Spot(24, R.drawable.northern8, R.string.northern_8_text),
        Spot(25, R.drawable.northern9, R.string.northern_9_text),
        Spot(26, R.drawable.northern10, R.string.northern_10_text),

        // Old Orhei
        Spot(27, R.drawable.orhei1, R.string.orhei_1_text),
        Spot(28, R.drawable.orhei2, R.string.orhei_2_text),
        Spot(29, R.drawable.orhei3, R.string.orhei_3_text),
        Spot(30, R.drawable.orhei4, R.string.orhei_4_text),

        // Transnistria
        Spot(31, R.drawable.transnistria1, R.string.transnistria_1_text),
        Spot(32, R.drawable.transnistria2, R.string.transnistria_2_text),
        Spot(33, R.drawable.transnistria3, R.string.transnistria_3_text),
        Spot(34, R.drawable.transnistria4, R.string.transnistria_4_text),
        Spot(35, R.drawable.transnistria5, R.string.transnistria_5_text),

        // Chisinau Parks
        Spot(36, R.drawable.chisinau_parks_1, R.string.chisinau_parks_1_text),
        Spot(37, R.drawable.chisinau_parks_2, R.string.chisinau_parks_2_text),
        Spot(38, R.drawable.chisinau_parks_3, R.string.chisinau_parks_3_text),
        Spot(39, R.drawable.chisinau_parks_4, R.string.chisinau_parks_4_text),
        Spot(40, R.drawable.chisinau_parks_5, R.string.chisinau_parks_5_text),
        Spot(41, R.drawable.chisinau_parks_6, R.string.chisinau_parks_6_text),
        Spot(42, R.drawable.chisinau_parks_7, R.string.chisinau_parks_7_text),
        Spot(43, R.drawable.chisinau_parks_8, R.string.chisinau_parks_8_text),
        Spot(44, R.drawable.chisinau_parks_9, R.string.chisinau_parks_9_text),
        Spot(45, R.drawable.chisinau_parks_10, R.string.chisinau_parks_10_text),

        // Chisinau Museums
        Spot(46, R.drawable.chisinau_museums_1, R.string.chisinau_museums_1_text),
        Spot(47, R.drawable.chisinau_museums_2, R.string.chisinau_museums_2_text),
        Spot(48, R.drawable.chisinau_museums_6, R.string.chisinau_museums_6_text),
        Spot(49, R.drawable.chisinau_museums_4, R.string.chisinau_museums_4_text),
        Spot(50, R.drawable.chisinau_museums_5, R.string.chisinau_museums_5_text),
        Spot(51, R.drawable.chisinau_museums_3, R.string.chisinau_museums_3_text),
        Spot(52, R.drawable.chisinau_museums_7, R.string.chisinau_museums_7_text),

        // Chisinau Monuments
        Spot(53, R.drawable.chisinau_monuments_1, R.string.chisinau_monuments_1_text),
        Spot(54, R.drawable.chisinau_monuments_2, R.string.chisinau_monuments_2_text),
        Spot(55, R.drawable.chisinau_monuments_3, R.string.chisinau_monuments_3_text),
        Spot(56, R.drawable.chisinau_monuments_4, R.string.chisinau_monuments_4_text),
        Spot(57, R.drawable.chisinau_monuments_5, R.string.chisinau_monuments_5_text),
        Spot(58, R.drawable.chisinau_monuments_6, R.string.chisinau_monuments_6_text),
        Spot(59, R.drawable.chisinau_monuments_7, R.string.chisinau_monuments_7_text),

        // Chisinau Churches
        Spot(60, R.drawable.chisinau_churches_1, R.string.chisinau_churches_1_text),
        Spot(61, R.drawable.chisinau_churches_2, R.string.chisinau_churches_2_text),
        Spot(62, R.drawable.chisinau_churches_3, R.string.chisinau_churches_3_text),
        Spot(63, R.drawable.chisinau_churches_4, R.string.chisinau_churches_4_text),
        Spot(64, R.drawable.chisinau_churches_5, R.string.chisinau_churches_5_text),
        Spot(65, R.drawable.chisinau_churches_6, R.string.chisinau_churches_6_text),
        Spot(66, R.drawable.chisinau_churches_7, R.string.chisinau_churches_7_text)
    )

    // Get a spot by ID
    fun getSpotById(id: Int): Spot? {
        return spots.find { it.id == id }
    }
}