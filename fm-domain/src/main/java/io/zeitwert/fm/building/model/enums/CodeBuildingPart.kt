package io.zeitwert.fm.building.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase
import io.zeitwert.fm.building.service.api.dto.ProjectionPeriod
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow

enum class CodeBuildingPart(
	override val defaultName: String,
	val optimalRestoreDuration: Double,
	val optimalRestoreTimeValue: Double,
	val maximalRestoreDuration: Double,
	val maximalRestoreTimeValue: Double,
	val afterRestoreTimeValue: Double,
	val restoreCostPerc: Double,
	val newBuildCostPerc: Double,
	val linearDuration: Double,
	val linearTimeValue: Double,
	val c10: Double,
	val c9: Double,
	val c8: Double,
	val c7: Double,
	val c6: Double,
	val c5: Double,
	val c4: Double,
	val c3: Double,
	val c2: Double,
	val c1: Double,
	val c0: Double,
) : Enumerated {

	// @formatter:off
	P1("Rohbau", 77.0, 0.58, 98.0, 0.32, 0.95, 138.0, 120.0, 12.0, 0.9, 4.62507945846734E-22, -2.67816396887131E-19, 6.73184650365873E-17, -9.64388300326902E-15, 8.69517499606928E-13, -5.14322746869219E-11, 2.01720457885679E-09, -5.168626783867E-08, -7.63358252131916E-05, 0.00184451149073684, 0.888916308822751),
	P48("Massiver Rohbau", 185.0, 0.51, 211.0, 0.28, 0.95, 138.0, 120.0, 94.0, 0.851266, 1.60557440443757E-21, -1.9712723113754E-18, 1.03918194567227E-15, -3.05480217024366E-13, 5.42196841917345E-11, -5.83060703445595E-09, 3.53413635869565E-07, -9.4545383188793E-06, 0.0, 0.0, 1.01535396958285),
	P49("Übriger Rohbau", 90.0, 0.65, 109.0, 0.38, 0.95, 138.0, 120.0, 47.0, 0.90076, 8.46845472687661E-19, -5.32526779709104E-16, 1.43604276899949E-13, -2.15589222748033E-11, 1.94965836552998E-09, -1.06437157327896E-07, 3.25445277824119E-06, -4.35103562732675E-05, 0.0, 0.0, 0.986488106135212),
	P2("Steildach", 39.0, 0.58, 49.0, 0.32, 0.95, 138.0, 120.0, 6.0, 0.9, 1.3758455178683E-18, -3.99048169306709E-16, 5.03341536672919E-14, -3.62392941074749E-12, 1.64333488260036E-10, -4.88561581695543E-09, 9.6053884851703E-08, -1.22779720971387E-06, -0.000298928657685797, 0.00366107549631756, 0.888966944040106),
	P3("Flachdach", 23.0, 0.7, 28.0, 0.19, 0.98, 152.0, 120.0, 16.0, 0.854545, -3.27416912731571E-12, 5.10455032750639E-10, -3.33538753730506E-08, 1.17215804431084E-06, -2.34541106642957E-05, 0.000254840719739557, -0.00118795256174862, 0.0, 0.0, 0.0, 2.10973305291253),
	P4("Fassaden", 39.0, 0.58, 54.0, 0.2, 0.95, 138.0, 120.0, 6.0, 0.9, 3.83219833514035E-19, -1.16764638982454E-16, 1.54913768381886E-14, -1.17495510195419E-12, 5.62431670492814E-11, -1.76982424086846E-09, 3.6949178574008E-08, -5.03118797244714E-07, -0.000304396143844884, 0.00368389071145046, 0.888927088312456),
	P5("Fenster", 29.0, 0.58, 40.0, 0.2, 0.95, 138.0, 120.0, 4.0, 0.911111, 4.83751766977205E-14, -1.1437394430638E-11, 1.17968987896355E-09, -6.96715000191389E-08, 2.59939122093842E-06, -6.37448015623476E-05, 0.00103561269622253, -0.0109484102529232, 0.0711298783696792, -0.25599113597015, 1.28784799926783),
	P6("Wärmeerzeugung", 20.0, 0.5, 30.0, 0.25, 0.95, 136.0, 120.0, 30.0, 0.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
	P7("Wärmeverteilung", 56.0, 0.68, 72.0, 0.18, 0.98, 152.0, 120.0, 41.0, 0.83103, -8.23199702955801E-17, 3.15016166211386E-14, -5.02780343724014E-12, 4.28394093136339E-10, -2.05365396999992E-08, 5.24085515904556E-07, -5.56643088123968E-06, 0.0, 0.0, 0.0, 0.899324605352007),
	P52("Zentrale Lufttechn. Anlagen", 30.0, 0.58, 42.0, 0.2, 0.95, 134.0, 110.0, 4.0, 0.913043, 4.41380825060308E-14, -1.06667895765891E-11, 1.12421508006188E-09, -6.78172619847686E-08, 2.58315310510141E-06, -6.46334566221444E-05, 0.00107058852201099, -0.0115289466254356, 0.0762724555533014, -0.279220873613053, 1.32944690532483),
	P53("Verteilnetz Lufttechn. Anlagen", 45.0, 0.68, 57.0, 0.18, 0.98, 151.0, 120.0, 33.0, 0.83, 2.02437090016604E-16, -6.85183968433925E-14, 9.86431042491476E-12, -7.7813567069385E-10, 3.57870668653477E-08, -9.20376370123402E-07, 1.04271978495133E-05, 0.0, 0.0, 0.0, 0.425281122679205),
	P8("Sanitär", 34.0, 0.68, 43.0, 0.18, 0.98, 151.0, 120.0, 24.0, 0.835152, -8.74793593927328E-14, 2.02931157980258E-11, -1.97389246982166E-09, 1.03331577799845E-07, -3.08287753896747E-06, 5.00184813384276E-05, -0.00034881186976412, 0.0, 0.0, 0.0, 2.71633626592154),
	P54("Sanitär-Apparate", 35.0, 0.58, 51.0, 0.14, 0.95, 133.7, 110.0, 5.0, 0.907407, 3.93406276211811E-15, -1.15493839891931E-12, 1.4788390671996E-10, -1.0839677878607E-08, 5.01765409729407E-07, -1.52604468089709E-05, 0.000307323690074285, -0.00402490896934906, 0.0322377723858704, -0.142734543080321, 1.1662719001247),
	P55("Sanitär-Leitungen", 45.0, 0.68, 57.0, 0.18, 0.98, 151.3, 120.0, 33.0, 0.83, 2.02437090016604E-16, -6.85183968433925E-14, 9.86431042491476E-12, -7.7813567069385E-10, 3.57870668653477E-08, -9.20376370123402E-07, 1.04271978495133E-05, 0.0, 0.0, 0.0, 0.425281122679205),
	P56("Transportanlagen", 30.0, 0.58, 42.0, 0.2, 0.95, 138.0, 120.0, 4.0, 0.913043, 4.41380825060308E-14, -1.06667895765891E-11, 1.12421508006188E-09, -6.78172619847686E-08, 2.58315310510141E-06, -6.46334566221444E-05, 0.00107058852201099, -0.0115289466254356, 0.0762724555533014, -0.279220873613053, 1.32944690532483),
	P9("Elektro", 45.0, 0.68, 57.0, 0.18, 0.98, 151.0, 120.0, 33.0, 0.83, 2.02437090016604E-16, -6.85183968433925E-14, 9.86431042491476E-12, -7.7813567069385E-10, 3.57870668653477E-08, -9.20376370123402E-07, 1.04271978495133E-05, 0.0, 0.0, 0.0, 0.425281122679205),
	P50("Starkstrom-Anlagen", 45.0, 0.68, 57.0, 0.18, 0.98, 144.6, 100.0, 33.0, 0.83, 2.02437090016604E-16, -6.85183968433925E-14, 9.86431042491476E-12, -7.7813567069385E-10, 3.57870668653477E-08, -9.20376370123402E-07, 1.04271978495133E-05, 0.0, 0.0, 0.0, 0.425281122679205),
	P51("Schwachstrom-Anlagen", 20.0, 0.68, 26.0, 0.18, 0.98, 142.3, 100.0, 14.0, 0.839731, -1.99550857191179E-11, 2.7791146271368E-09, -1.62283126367839E-07, 5.10040376942076E-06, -9.13811125181664E-05, 0.00089081785663006, -0.00373545457061076, 0.0, 0.0, 0.0, 3.49185099507019),
	P10("Übrige Technik", 20.0, 0.5, 30.0, 0.25, 0.95, 135.8, 120.0, 30.0, 0.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
	P11("Innenausbau", 29.0, 0.58, 42.0, 0.14, 0.95, 138.0, 120.0, 4.0, 0.911111, 3.75141353449987E-14, -9.0660670688257E-12, 9.5551798548776E-10, -5.76414047611704E-08, 2.19558323308867E-06, -0.00005493677756612, 0.000909986435277306, -0.00979961035238529, 0.0647302924579919, -0.236513276989017, 1.26337970558787),
	P57("Innenausbau Substanz", 35.0, 0.58, 51.0, 0.14, 0.95, 138.0, 120.0, 5.0, 0.907407, 3.93406276211811E-15, -1.15493839891931E-12, 1.4788390671996E-10, -1.0839677878607E-08, 5.01765409729407E-07, -1.52604468089709E-05, 0.000307323690074285, -0.00402490896934906, 0.0322377723858704, -0.142734543080321, 1.1662719001247),
	P58("Innenausbau Oberflächen", 20.0, 0.58, 29.0, 0.14, 0.95, 138.0, 120.0, 3.0, 0.903226, 3.67809132521511E-13, -6.20959991498592E-11, 4.58014765253777E-09, -1.93784324863958E-07, 5.19082968241108E-06, -9.16384678340661E-05, 0.00107530991429933, -0.00824432041638841, 0.0381819418053874, -0.0978226824637012, 1.00740285830465),
	P59("Kücheneinrichtung", 25.0, 0.58, 37.0, 0.14, 0.95, 138.0, 120.0, 3.0, 0.923077, 2.26596675721754E-13, -4.77478575351923E-11, 4.38089372255531E-09, -2.29618999758536E-07, 7.58120183588671E-06, -0.000163938275800416, 0.00233814515363197, -0.0215794941046193, 0.12174828766168, -0.377492301584198, 1.38719349929755),
	P12("Disponibel", 30.0, 0.58, 42.0, 0.2, 0.95, 138.0, 120.0, 4.0, 0.911111, 3.75141353449987E-14, -9.0660670688257E-12, 9.5551798548776E-10, -5.76414047611704E-08, 2.19558323308867E-06, -0.00005493677756612, 0.000909986435277306, -0.00979961035238529, 0.0647302924579919, -0.236513276989017, 1.26337970558787),
	P60("Disponibel langlebig", 45.0, 0.58, 63.0, 0.2, 0.95, 138.0, 120.0, 7.0, 0.9, -3.47306399097142E-21, -8.0704146419854E-19, 4.49514437039057E-16, -6.8819142271753E-14, 5.44279698263263E-12, -2.55520876417713E-10, 7.43785659483518E-09, -1.33997705437229E-07, -0.000225325283180815, 0.0031664425670958, 0.888907612562652),
	P61("Disponibel normal", 30.0, 0.58, 42.0, 0.2, 0.95, 138.0, 120.0, 4.0, 0.913043, 4.41380825060308E-14, -1.06667895765891E-11, 1.12421508006188E-09, -6.78172619847686E-08, 2.58315310510141E-06, -6.46334566221444E-05, 0.00107058852201099, -0.0115289466254356, 0.0762724555533014, -0.279220873613053, 1.32944690532483),
	P62("Disponibel kurzlebig", 15.0, 0.58, 20.0, 0.2, 0.95, 138.0, 120.0, 2.0, 0.909091, 2.57951381426734E-11, -3.01577417354821E-09, 1.54237845272831E-07, -4.53193949554138E-06, 8.44657813610142E-05, -0.00103992370800702, 0.0085338835559936, -0.0459099013080185, 0.152020597982199, -0.28134291959734, 1.1228369597101),
	P63("Fernmelde- und Informationstechnik", 10.0, 0.5, 15.0, 0.25, 0.9, 135.8, 120.0, 15.0, 0.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
	P64("Lufttechnische Anlagen", 20.0, 0.5, 30.0, 0.25, 0.93, 135.8, 120.0, 30.0, 0.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
	P65("Kältetechnische Anlagen", 15.0, 0.5, 23.0, 0.25, 0.92, 136.1, 120.0, 23.0, 0.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
	P66("Förderanlagen / übrige Technik", 20.0, 0.5, 30.0, 0.25, 0.93, 135.8, 120.0, 30.0, 0.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
	;

	// @formatter:on

	override val enumeration get() = Enumeration

	override val id = name

	fun getNextRestoration(
		elementValue: Double,
		ratingYear: Int,
		condition: Double,
	): ProjectionPeriod? {
		var startYear = 0
		var restorationYear = 0
		val restorationCosts: Double
		if ((condition / 100) > optimalRestoreTimeValue) {
			startYear = floor(getRelativeAge(condition / 100)).toInt()
			restorationYear = floor(getRelativeAge(optimalRestoreTimeValue)).toInt() + 1
			restorationCosts = restoreCostPerc / 100 - optimalRestoreTimeValue
		} else {
			restorationCosts = (restoreCostPerc - condition) / 100
		}
		val duration = restorationYear - startYear
		return ProjectionPeriod(
			year = ratingYear + duration,
			originalValue = elementValue,
			timeValue = optimalRestoreTimeValue,
			restorationCosts = Math.round(restorationCosts * elementValue).toDouble(),
		)
	}

	fun getTimeValue(relativeAge: Double): Double {
		if (linearDuration > 0 && relativeAge <= linearDuration) {
			return 1 - relativeAge / linearDuration * (1 - linearTimeValue)
		}
		return c0 +
			c1 * relativeAge +
			c2 * relativeAge.pow(2.0) +
			c3 * relativeAge.pow(3.0) +
			c4 * relativeAge.pow(4.0) +
			c5 * relativeAge.pow(5.0) +
			c6 * relativeAge.pow(6.0) +
			c7 * relativeAge.pow(7.0) +
			c8 * relativeAge.pow(8.0) +
			c9 * relativeAge.pow(9.0) +
			c10 * relativeAge.pow(10.0)
	}

	fun getRelativeAge(timeValue: Double): Double {
		if (timeValue > linearTimeValue) {
			return (1 - timeValue) / (1 - linearTimeValue) * linearDuration
		}
		val precision = 0.0001
		var prevT: Double
		var t: Double
		var i = 0
		t = optimalRestoreDuration
		prevT = t
		while (abs(getTimeValue(t) - timeValue) > precision && i < 10) {
			t = prevT - (getTimeValue(prevT) - timeValue) / fDerivative(prevT)
			i += 1
			prevT = t
		}
		return t
	}

	fun getLifetime(timeValue: Double): Int {
		val optimalTimeValue = optimalRestoreTimeValue
		if (timeValue <= optimalTimeValue) {
			return 0
		}
		return floor(getRelativeAge(optimalTimeValue) - getRelativeAge(timeValue)).toInt() + 1
	}

	private fun fDerivative(relativeAge: Double): Double =
		c1 +
			2 * c2 * relativeAge +
			3 * c3 * relativeAge.pow(2.0) +
			4 * c4 * relativeAge.pow(3.0) +
			5 * c5 * relativeAge.pow(4.0) +
			6 * c6 * relativeAge.pow(5.0) +
			7 * c7 * relativeAge.pow(6.0) +
			8 * c8 * relativeAge.pow(7.0) +
			9 * c9 * relativeAge.pow(8.0) +
			10 * c10 * relativeAge.pow(9.0)

	fun getProjection(
		elementValue: Double,
		ratingYear: Int,
		condition: Double,
		startYear: Int,
		duration: Int,
	): List<ProjectionPeriod> {
		require(ratingYear <= startYear) { "valid start year (" + ratingYear + "<=" + startYear + ")" }
		require(!(condition < 0.0 || condition > 1.0)) { "valid condition (0 <=" + condition + " <= 1)" }
		require(duration <= 100) { "duration <= 100" }

		val maxProjectionYear = startYear + min(100, duration)
		val restorationTimeValue = optimalRestoreTimeValue
		val totalRestorationCosts = restoreCostPerc / 100
		val timeValueAfterRestoration = afterRestoreTimeValue
		val relativeAgeAfterRestoration = getRelativeAge(timeValueAfterRestoration)

		val periodList: MutableList<ProjectionPeriod> = mutableListOf()

		var relativeAge = getRelativeAge(condition)
		var timeValue = condition
		val techPart = getTechRate()
		val techRate = getTechRate(techPart)

		for (simYear in ratingYear..maxProjectionYear) {
			val needRestoration = timeValue <= restorationTimeValue
			var restorationCosts = 0.0
			if (needRestoration) {
				restorationCosts = (totalRestorationCosts - timeValue) * elementValue
				relativeAge = relativeAgeAfterRestoration
			}
			val maintenanceRate = getMaintenanceRate(timeValue) / 100.0
			if (simYear >= startYear) {
				val period = ProjectionPeriod(
					year = simYear,
					originalValue = elementValue,
					timeValue = timeValue * elementValue,
					restorationCosts = restorationCosts,
					techPart = techPart,
					techRate = techRate,
					maintenanceRate = maintenanceRate,
					maintenanceCosts = maintenanceRate * techRate * elementValue,
				)
				periodList.add(period)
			}
			relativeAge += 1.0
			timeValue = getTimeValue(relativeAge)
		}
		check(periodList.size == duration + 1) { "valid duration" }
		return periodList
	}

	fun getTechRate(): Double {
		if (FULL_TECH_RATES.contains(id)) {
			return 1.0
		}
		if (HALF_TECH_RATES.contains(id)) {
			return 0.5
		}
		return 0.0
	}

	companion object Enumeration : EnumerationBase<CodeBuildingPart>(CodeBuildingPart::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		val FULL_TECH_RATES =
			listOf(
				"P6",
				"P7",
				"P8",
				"P54",
				"P55",
				"P9",
				"P50",
				"P51",
				"P10",
				"P63",
				"P64",
				"P65",
				"P66",
			)
		val HALF_TECH_RATES = listOf("P12", "P60", "P61", "P62")

		fun getBuildingPart(itemId: String?) = if (itemId != null) getItem(itemId) else null

		fun getTechRate(techPart: Double): Double = getRatio(techPart, 0.09, 0.22, 0.5, 1.0)

		fun getMaintenanceRate(timeValue: Double): Double {
			if (timeValue >= 1.0) {
				return 0.5
			}
			if (timeValue >= 0.94) {
				return getRatio(timeValue, 0.94, 1.0, 0.64, 0.5)
			}
			if (timeValue >= 0.85) {
				return getRatio(timeValue, 0.85, 0.94, 1.1, 0.64)
			}
			if (timeValue >= 0.75) {
				return getRatio(timeValue, 0.75, 0.85, 2.0, 1.1)
			}
			if (timeValue >= 0.67) {
				return getRatio(timeValue, 0.67, 0.75, 2.0, 2.0)
			}
			if (timeValue >= 0.60) {
				return getRatio(timeValue, 0.60, 0.67, 0.5, 2.0)
			}
			return getRatio(timeValue, 0.00, 0.60, 0.75, 0.5)
		}

		private fun getRatio(
			value: Double,
			lowBound: Double,
			highBound: Double,
			lowValue: Double,
			highValue: Double,
		): Double {
			if (value <= lowBound) {
				return lowValue
			}
			if (value >= highBound) {
				return highValue
			}
			return lowValue + (value - lowBound) / (highBound - lowBound) * (highValue - lowValue)
		}
	}
}
