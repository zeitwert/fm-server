package io.zeitwert.fm.building.model.enums

import dddrive.ddd.enums.model.Enumerated
import dddrive.ddd.enums.model.base.EnumerationBase

enum class CodeBuildingPartCatalog(
	override val defaultName: String,
	private val partList: String,
) : Enumerated {

	// @formatter:off
	C0("Custom", "P48:31,P49,P2:8,P3,P4:8,P5:8,P50:5,P51:1,P6:1,P7:2,P52,P53,P55:4,P54:2,P56,P10:3,P57:15,P58:12,P59,P60,P61,P62"),
	C6("Einfamilienhaus", "P48:30,P49,P2:13,P3,P4:8,P5:13,P50:2,P51:1,P6:4,P7:3,P52,P53,P55:4,P54:2,P56,P10,P57:10,P58:6,P59:4,P60,P61,P62"),
	C7("Mehrfamilienhaus", "P48:35,P49,P2:8,P3,P4:7,P5:11,P50:3,P51:1,P6:2,P7:3,P52,P53,P55:4,P54:3,P56:2,P10,P57:9,P58:8,P59:4,P60,P61,P62"),
	C8("Kindergarten", "P48:24,P49,P2:20,P3,P4:8,P5:14,P50:3,P51:1,P6:3,P7:3,P52,P53,P55:3,P54:1,P56,P10,P57:17,P58:3,P59,P60,P61,P62"),
	C9("Schule", "P48:41,P49,P2,P3:8,P4:7,P5:10,P50:6,P51:1,P6:1,P7:3,P52,P53,P55:2,P54:2,P56,P10,P57:11,P58:8,P59,P60,P61,P62"),
	C10("Schule mit Labor", "P48:32,P49,P2,P3:6,P4:10,P5:9,P50:6,P51:1,P6:1,P7:2,P52:3,P53:3,P55:2,P54:2,P56:2,P10,P57:12,P58:9,P59,P60,P61,P62"),
	C14("Lagerhalle", "P48,P49:38,P2:16,P3,P4:11,P5:7,P50:5,P51:1,P6:2,P7:5,P52,P53,P55:1,P54:1,P56,P10:2,P57:8,P58:3,P59,P60,P61,P62"),
	C15("Produktionshalle", "P48:41,P49,P2:13,P3,P4:4,P5:8,P50:6,P51:1,P6:1,P7:4,P52,P53,P55:4,P54:1,P56,P10:3,P57:10,P58:4,P59,P60,P61,P62"),
	C16("Gewerbegebäude", "P48:39,P49,P2,P3:12,P4:6,P5:11,P50:6,P51:1,P6:1,P7:3,P52,P53,P55:3,P54:2,P56:3,P10,P57:9,P58:4,P59,P60,P61,P62"),
	C19("Heizzentrale", "P48:37,P49,P2,P3:4,P4:4,P5:4,P50:4,P51:2,P6:35,P7:3,P52,P53,P55,P54:1,P56,P10:2,P57:3,P58:1,P59,P60,P61,P62"),
	C20("Ladenbau", "P48:30,P49,P2:13,P3,P4:8,P5:6,P50:8,P51:3,P6:2,P7:1,P52:8,P53:4,P55:2,P54:1,P56:1,P10,P57:8,P58:5,P59,P60,P61,P62"),
	C21("Bürogebäude einfach", "P48:28,P49,P2:10,P3,P4:8,P5:14,P50:6,P51:2,P6:1,P7:3,P52,P53,P55:3,P54:1,P56:2,P10,P57:14,P58:8,P59,P60,P61,P62"),
	C22("Bürogebäude komplex", "P48:27,P49,P2,P3:5,P4:11,P5:10,P50:10,P51:2,P6:2,P7:4,P52:2,P53:2,P55:3,P54:1,P56:2,P10,P57:13,P58:6,P59,P60,P61,P62"),
	C23("Bürogebäude differenziert", "P48:27,P49,P2,P3:5,P4:11,P5:10,P50:10,P51:2,P6:2,P7:4,P52:2,P53:2,P55:1,P54:3,P56:2,P10,P57:12,P58:7,P59,P60,P61,P62"),
	C24("Container / Baracke", "P48:35,P49,P2,P3:10,P4:7,P5:11,P50:3,P51:1,P6:2,P7:3,P52,P53,P55:4,P54:3,P56,P10,P57:9,P58:8,P59:4,P60,P61,P62"),
	C28("Pflegeheim", "P48:35,P49,P2:8,P3,P4:7,P5:11,P50:3,P51:1,P6:2,P7:4,P52,P53,P55:6,P54:2,P56,P10,P57:12,P58:9,P59,P60,P61,P62"),
	C29("Saalbau", "P48:23,P49,P2:14,P3,P4:10,P5:12,P50:3,P51:1,P6:1,P7:4,P52:3,P53:3,P55:3,P54:2,P56,P10,P57:11,P58:4,P59:6,P60,P61,P62"),
	C30("Restaurationsbetrieb", "P48:29,P49,P2,P3:9,P4:7,P5:9,P50:8,P51:1,P6:2,P7:4,P52:4,P53:4,P55:6,P54:2,P56,P10,P57:11,P58:4,P59,P60,P61,P62"),
	C31("Hotel", "P48:35,P49,P2,P3:7,P4:6,P5:8,P50:5,P51:2,P6:2,P7:4,P52:1,P53:1,P55:4,P54:5,P56,P10,P57:9,P58:11,P59,P60,P61,P62"),
	C33("Sporthalle", "P48:32,P49,P2,P3:13,P4:6,P5:8,P50:4,P51:1,P6:3,P7:3,P52:2,P53:2,P55:3,P54:2,P56,P10,P57:10,P58:11,P59,P60,P61,P62"),
	C34("Hallenbad", "P48:38,P49,P2,P3:4,P4:5,P5:6,P50:3,P51:1,P6:1,P7:4,P52:8,P53:3,P55:5,P54:3,P56:3,P10,P57:8,P58:8,P59,P60,P61,P62"),
	C35("Tiefgarage", "P48:63,P49,P2,P3:17,P4:1,P5:1,P50:3,P51:1,P6,P7,P52:3,P53:3,P55,P54:1,P56:2,P10,P57:2,P58:3,P59,P60,P61,P62"),
	C36("Parkhaus", "P48:63,P49,P2,P3:20,P4:1,P5:1,P50:3,P51:1,P6,P7,P52,P53,P55,P54:1,P56:2,P10,P57:2,P58:3,P59,P60,P61:3,P62"),
	C37("Werkhof", "P48:32,P49,P2,P3:7,P4:6,P5:15,P50:8,P51:1,P6:2,P7:4,P52:3,P53:3,P55:3,P54:1,P56:2,P10,P57:8,P58:5,P59,P60,P61,P62"),
	C38("Zivilschutzanlage", "P48:48,P49,P2,P3:20,P4,P5:2,P50:5,P51:1,P6,P7,P52:2,P53:2,P55:3,P54:3,P56,P10,P57:2,P58:10,P59:2,P60,P61,P62"),
	C99("Stratus Migration", "P1,P48,P49,P2,P3,P4,P5,P6,P7,P52,P53,P8,P54,P55,P56,P9,P50,P51,P10,P11,P57,P58,P59,P12,P60,P61,P62"),
	;

	// @formatter:on

	override val enumeration get() = Enumeration

	override val id = name

	fun getParts(): List<Pair<CodeBuildingPart, Int>> =
		partList.split(",").map { p ->
			val partWeight = if (p.contains(":")) p else "$p:0"
			val (partId, weight) = partWeight.split(":")
			val part = CodeBuildingPart.Enumeration.getBuildingPart(partId)
				?: throw IllegalArgumentException("Unknown building part: $partId")
			Pair(part, weight.toInt())
		}

	companion object Enumeration : EnumerationBase<CodeBuildingPartCatalog>(CodeBuildingPartCatalog::class.java) {

		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getPartCatalog(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
