package io.zeitwert.fm.building.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase
import kotlin.math.pow

enum class CodeBuildingPriceIndex(
	private val id: String,
	private val itemName: String,
	val minIndexYear: Int,
	val maxIndexYear: Int,
	val indexPerYear: Map<Int, Double>,
) : Enumerated {

	CH_ZRH(
		"ch-ZRH",
		"Baukostenindex Zürich (Basis 1939)",
		1939,
		2022,
		mapOf(
			1939 to 100.0,
			1940 to 112.4,
			1941 to 121.1,
			1942 to 139.6,
			1943 to 147.1,
			1944 to 155.4,
			1945 to 160.5,
			1946 to 165.9,
			1947 to 183.7,
			1948 to 196.6,
			1949 to 194.9,
			1950 to 183.5,
			1951 to 185.8,
			1952 to 202.4,
			1953 to 201.5,
			1954 to 194.8,
			1955 to 196.3,
			1956 to 202.5,
			1957 to 211.1,
			1958 to 212.4,
			1959 to 215.1,
			1960 to 219.7,
			1961 to 237.1,
			1962 to 257.9,
			1963 to 277.5,
			1964 to 297.6,
			1965 to 310.6,
			1966 to 319.7,
			1967 to 322.6,
			1968 to 325.4,
			1969 to 331.4,
			1970 to 374.7,
			1971 to 421.2,
			1972 to 466.0,
			1973 to 512.7,
			1974 to 557.5,
			1975 to 535.5,
			1976 to 500.4,
			1977 to 515.1,
			1978 to 531.3,
			1979 to 549.2,
			1980 to 600.0,
			1981 to 654.1,
			1982 to 698.4,
			1983 to 670.2,
			1984 to 670.3,
			1985 to 684.8,
			1986 to 705.2,
			1987 to 718.9,
			1988 to 750.3,
			1989 to 790.1,
			1990 to 858.5,
			1991 to 911.2,
			1992 to 905.2,
			1993 to 863.8,
			1994 to 853.1,
			1995 to 874.2,
			1996 to 861.1,
			1997 to 847.4,
			1998 to 843.8,
			1999 to 854.4,
			2000 to 887.1,
			2001 to 929.3,
			2002 to 928.4,
			2003 to 899.2,
			2004 to 907.6,
			2005 to 929.5,
			2006 to 944.5,
			2007 to 987.4,
			2008 to 1026.8,
			2009 to 1030.7,
			2010 to 1042.6,
			2011 to 1059.8,
			2012 to 1067.4,
			2013 to 1060.9,
			2014 to 1066.1,
			2015 to 1053.2,
			2016 to 1034.4,
			2017 to 1034.5,
			2018 to 1036.8,
			2019 to 1046.3,
			2020 to 1045.6,
			2021 to 1057.68,
			2022 to 1128.6,
		),
	),
	;

	override fun getId() = id

	override fun getName() = itemName

	override fun getEnumeration() = Enumeration

	fun indexAt(
		origYear: Int,
		targetYear: Int,
	): Double {
		val effectiveTargetYear = if (targetYear > maxIndexYear) maxIndexYear else targetYear
		val effectiveOrigYear = if (origYear < minIndexYear) minIndexYear else origYear
		val origIndex = indexPerYear[effectiveOrigYear] ?: return 1.0
		val targetIndex = indexPerYear[effectiveTargetYear] ?: return 1.0
		return targetIndex / origIndex
	}

	fun priceAt(
		origYear: Int,
		origPrice: Double,
		targetYear: Int,
		inflationRate: Double,
	): Double {
		var targetPrice = indexAt(origYear, targetYear) * origPrice
		if (targetYear > maxIndexYear && inflationRate > 0) {
			targetPrice *= (1.0 + inflationRate / 100.0).pow(targetYear - maxIndexYear)
		}
		return targetPrice
	}

	companion object Enumeration : EnumerationBase<CodeBuildingPriceIndex>(CodeBuildingPriceIndex::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getBuildingPriceIndex(itemId: String): CodeBuildingPriceIndex? = getItem(itemId)
	}
}
