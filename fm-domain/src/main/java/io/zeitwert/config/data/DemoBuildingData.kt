package io.zeitwert.config.data

import io.zeitwert.config.dsl.AccountContext

/*
 * Building demo data for DemoDataSetup.
 * This file contains building definitions for each account, organized by account key.
 *
 * Generated from SQL files - do not edit manually.
 */

/** Buildings for account 3032 - Hinterkappelen */
fun AccountContext.buildings3032() {
	building("Wohn- und Geschäftshaus", "Bergfeldstrasse 8", "3032", "Hinterkappelen") {
		buildingNr = "08.01"
		buildingType = "T01"
		buildingSubType = "ST02-11"
		buildingYear = 1834
		volume = 1840
		insuredValue = 1573
		insuredValueYear = 2009
		description =
			"Ehemalige Primarschule\nIm EG Praxis mit Mieterausbau (Oberflächen, Klimagerät, Beleuchtung, Einbauten)\nWärmepumpe wurde ersetzt, Oel-Heizung wird nur im Bedarfsfall genutzt\nWC in Praxis: muss mehrmals spülen bis ok.\nLehrerwohnung: B: PVC, Linol, Platten"
		rating(2012, "C7", "NW") {
			element("P48", 37, 80, "Senkungen vorh. z.Z. stabil, tw. Risse, rep.")
			element("P49", 0, 0)
			element("P2", 8, 85, "Ziegeldach, isol., im Estrich Abteile für Wg.")
			element("P3", 0, 0)
			element(
				"P4",
				7,
				90,
				"Fachwerk, verp. Ausfachungen / Holzschindeln, Sockelbereich im 2011/12 san., Holztäfer, Glasanbau (Windfang), Laube Holz",
			)
			element("P5", 11, 80, "Doppelvergl. (keine IV) Holzf. m. Sprossen, Stoffstoren")
			element("P9", 4, 70, "tw. alte Keramiksicherungen")
			element(
				"P6",
				2,
				90,
				"Luft-/Luft-WP (wird 2012 ersetzt), Oelbrenner als Notheizung (lief in den letzten 2 Jahren permanent!), WW über dezentr. Boiler in Wohnungen (3. Stk.), Kachelofen in Betrieb",
			)
			element("P7", 3, 85, "Radiatoren mit Thermostaten")
			element("P54", 3, 90, "WM/WT neu, App. i.O.")
			element(
				"P55",
				4,
				85,
				"ev. Undichtigkeit vorh. (Wasserschaden; Ursache nicht abgeklärt)",
			)
			element("P56", 0, 0)
			element("P57", 9, 85, "W: Gips gestr., MW verp.\nim DG D: Täfer, W: Täfer")
			element(
				"P58",
				8,
				80,
				"B: PVC/Linol, Keramik, Teppich in DG\nW: Täfer, Gips gestr., Keramik\nD: Gips gestr.",
			)
			element("P59", 4, 90, "Küchen wurden '13/'14 ersetzt")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Feuerwehrmagazin/3 Wohnungen", "Dorfstrasse 45", "3032", "Hinterkappelen") {
		buildingNr = "08.02"
		buildingType = "T01"
		buildingSubType = "ST02-11"
		buildingYear = 1989
		volume = 6160
		insuredValue = 4304
		insuredValueYear = 2009
		description =
			"Fenster in Wohnung auf Südseite ist im Winter beschlagen (IV noch intakt?)\nSchlauchwasch- und Trocknungsanlage, Druckluft ok.\nDie Küchen wurden 2013 in allen drei Wg. saniert.\nLüftungsanlage inkl. Heizung\nKompressor"
		rating(2012, "C7", "NW") {
			element(
				"P48",
				39,
				90,
				"Stahlbeton, im Boden Risse, wahrsch. d. Setzungen, san. KS-Wände roh",
			)
			element("P49", 0, 0)
			element("P2", 0, 0)
			element(
				"P3",
				8,
				85,
				"Kiesklebedach, san. n. Wassereintritt Oblicht in Wg. mit Glasbausteinen",
			)
			element("P4", 7, 85, "Betonelemente, Fugen i.O.")
			element("P5", 11, 70, "Metallfenster, IV, Glasbausteinen EG")
			element("P9", 4, 80, "Alte LS-Röhren (Leuchtmittel), alle Sicherungen Keramik")
			element(
				"P6",
				2,
				85,
				"Oelheizung/Brenner vor ca. 4. Jahren ersetzt, Heizlüftungen ok., Lüftung/Heizl. in Schulraum",
			)
			element("P7", 3, 85, "Radiator m. Thermostat, auch in Schutzraum, Heizlüfter ok")
			element("P54", 3, 80, "WC, Spaltanlage (f. Reinigung Abwasser), EG ok")
			element(
				"P55",
				4,
				70,
				"tw. saniert nach Leitungsbruch, tw. rostig (s. UG) -> immer noch rostig",
			)
			element("P56", 0, 0)
			element("P57", 9, 90, "Innentüren aus Holz, guter Zustand")
			element(
				"P58",
				8,
				85,
				"B: Parkett, Bad/Küche Keramikpl., in Schulungsraum Gussasphalt und Hartbeton, gestr. (wird ersetzt) W: Putz, gestr., Bad: Keramik D: Putz, gestr., Heraklitpl.",
			)
			element("P59", 2, 90, "Küche Ersatz '13")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Jugendtreff (Pavillon)", "Araweg 9", "3032", "Hinterkappelen") {
		buildingNr = "08.08"
		buildingType = "T10"
		buildingSubType = "ST08-43"
		buildingYear = 2007
		volume = 1460
		insuredValue = 800
		insuredValueYear = 2009
		description = "In Wohlen stark kalkhaltiges Wasser"
		rating(2012, "C29", "NW") {
			element("P48", 26, 90, "Holzständerbau")
			element("P49", 0, 0)
			element("P2", 17, 90, "Trapezwellblech")
			element("P3", 0, 0)
			element("P4", 10, 90, "Holz")
			element("P5", 12, 90, "Holz, IV")
			element("P9", 4, 90, "Beleuchtung")
			element("P6", 1, 90, "?")
			element("P7", 4, 90, "Radiatoren m. Thermostaten")
			element("P52", 0, 0)
			element("P53", 0, 0)
			element("P8", 5, 90, "WM, Leitungen haben etwas wenig Druck")
			element("P57", 11, 80, "Holztüren, Holzböden")
			element("P58", 4, 90, "B: OSP geölt, Teppich, PVC\nW: roh, gestr.\nD: roh")
			element("P59", 6, 90, "GSP/Backofen/GLK Abluft")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Wohnhaus (private Kindertagesstätte)", "Hofenstrasse 54", "3032", "Hinterkappelen") {
		buildingNr = "08.09"
		buildingType = "T02"
		buildingSubType = "ST01-4"
		buildingYear = 1920
		volume = 768
		insuredValue = 580
		insuredValueYear = 2009
		rating(2012, "C9", "NW") {
			element("P48", 41, 90, "Fachwerkbau")
			element("P49", 0, 0)
			element("P2", 8, 90, "Falzziegeldach m. Lukarnen und Dachfl.fenster")
			element("P3", 0, 0)
			element("P4", 7, 90, "Riegel mit verp. Ausfachungen")
			element("P5", 10, 90, "Holz, Doppelvergl., Holzläden")
			element("P50", 6, 90, "z.T. alte Keramiksicherungen, Beleuchtung")
			element("P51", 1, 90)
			element("P6", 1, 90, "Stückholzfeuerung, WW-Boiler")
			element("P7", 3, 90, "Radiatoren m. Thermostaten")
			element("P8", 4, 90, "WM/WT, Küchen, Bad im DG")
			element("P57", 11, 90, "Einbauschränke, Holzschiebetür: Bad/WC, (Holzwendeltreppe)")
			element(
				"P58",
				8,
				90,
				"B: Teppich, Linol\nD: Holztäfer\nW: Putz, Täfer, Keramik in Bad",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Kindergarten Kappelenring", "Kappelenring 34a", "3032", "Hinterkappelen") {
		buildingNr = "08.33"
		buildingType = "T02"
		buildingSubType = "ST01-4"
		buildingYear = 2015
		volume = 2763
		insuredValue = 2568
		insuredValueYear = 2015
		description =
			"Das Flachdach hängt im Bereich der Eingänge leicht durch, das Dachwasser fliesst dort vermutlich über die Dachkante (Wasserspuren am Boden und Dachrandabschluss); Dachkonstruktion vermutlich in diesem Bereich über dem Eingang zu schwach (Abklärung empfohlen, im Winter bei Schneelast beobachten)\n\nIm Sommer zu heiss."
		rating(2017, "C0", "NW") {
			element("P1", 35, 90, "Holzbau")
			element("P2", 0, 0)
			element(
				"P3",
				8,
				90,
				"Das extensiv begrünt Flachdach konnte nicht besichtigt werden (s. zus. Bemerkungen).",
			)
			element("P4", 8, 90, "Holz")
			element("P5", 8, 90, "Holz 3-fach IV")
			element("P6", 1, 90, "Fernwärme")
			element("P7", 2, 90, "Bodenheizung")
			element("P8", 6, 90)
			element("P9", 6, 90, "PV-Anlage")
			element("P10", 3, 90, "Lüftungsanlage Minergie; die Lüftung pfeift")
			element(
				"P11",
				27,
				90,
				"Innentüren mit Glasfüllungen, Oberflächen: B: Parkett, W: Holz, D: Holz (Akustik)",
			)
			element("P11", 0, 0)
			element("P12", 0, 0)
		}
	}
}

/** Buildings for account 3033 - Wohlen */
fun AccountContext.buildings3033() {
	building("Altes Schulhaus/Doppelkindergarten/Klassenzimmer/2 Whg.", "Schulgasse 14", "3033", "Wohlen") {
		buildingNr = "08.11"
		buildingType = "T02"
		buildingSubType = "ST01-4"
		buildingYear = 1890
		volume = 2500
		insuredValue = 1805
		insuredValueYear = 2009
		description =
			"Tagesschule, Basisstufe\nDachraum mit Mansardezimmer und 2-Zi-Wg.: B: Kugelgarn, W: Täfer, D: Täfer\nBretterboden in Estrich\nGewölbekeller"
		rating(2012, "C9", "NW") {
			element("P48", 40, 90, "Fachwerkbau, Holzdecken")
			element("P49", 0, 0)
			element("P2", 8, 85, "Falzziegeldach, Krüppelwalmdach")
			element("P3", 0, 0)
			element("P4", 7, 80, "Fachwerk m. verp. Ausfachungen")
			element("P5", 10, 85, "Holzsprossenfenster mit Doppelvergl.")
			element("P50", 6, 90, "Beleuchtung")
			element("P51", 1, 90, "Sonnerie, Telefon")
			element("P6", 0, 0, "Fernwärme")
			element("P7", 3, 90, "Radiatoren m. Thermostaten")
			element("P8", 4, 90, "WM/WT, Leitungen i.O.")
			element("P57", 11, 85, "Einbauschränke neu, Innentüren, Simse in KG")
			element(
				"P58",
				8,
				90,
				"B: Laminat, Gumminoppen, Parkett geölt, Linol\nW: Putz, gestr., Keramikpl.schild\nD: Täfer",
			)
			element("P60", 0, 0)
			element("P61", 2, 90, "Küche/Küche Tagesschule und KG")
			element("P62", 0, 0)
		}
	}
	building("Schulhaus, Pausenhalle, ZS-Anlage", "Schulgasse 16", "3033", "Wohlen") {
		buildingNr = "08.13"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1960
		volume = 10200
		insuredValue = 6768
		insuredValueYear = 2009
		rating(2012, "C9", "NW") {
			element("P48", 40, 90, "MW/Stahlbeton")
			element("P49", 0, 0)
			element("P2", 8, 90, "Ziegeldach")
			element("P3", 0, 0)
			element("P4", 7, 90, "Tw. neu, Metall/Eternit, hinterlüftet, Aussentüren Süd neu")
			element(
				"P5",
				10,
				90,
				"Holz-/Metallf. '91/Metallf., Metalltüre/Holzf. '11, Eingangstüre '16",
			)
			element("P50", 6, 90, "Beleuchtung, tw. alte Sicherungen")
			element("P51", 1, 90, "Uhr, Gong, Sonnerie, Tel.")
			element(
				"P6",
				1,
				50,
				"Oelheizung (noch keine NOx!) red. von 4'000 Liter auf 2'000 Liter, Luft-/Wärmepumpe: für WW Sommer",
			)
			element("P7", 3, 90, "Radiatoren m. Thermostaten")
			element("P8", 4, 70, "Leitungen rostig, Entkalkungsanlage/WC")
			element(
				"P57",
				11,
				90,
				"Glastrennwand, Einbauten, Arbeitssimse, Garderobe, Schränke in KZ",
			)
			element(
				"P58",
				8,
				85,
				"B: Linol, Keramikpl., Kunststeintreppen, Teppich, Gussasphalt in Werkräumen\nW: Putz, gestr./Täfer, Keramikschild\nD: Putz, Täfer",
			)
			element("P60", 0, 0)
			element("P61", 1, 90, "Küche, Aufzug")
			element("P62", 0, 0)
		}
	}
	building("Doppelturnhalle", "Schulgasse 18", "3033", "Wohlen") {
		buildingNr = "08.14"
		buildingType = "T12"
		buildingSubType = "ST01-9"
		buildingYear = 1991
		volume = 12800
		insuredValue = 5610
		insuredValueYear = 2009
		description = "Kontrolle der heruntergehängten Decke wird empfohlen (Aufhängungen im Hohlraum)"
		rating(2012, "C9", "NW") {
			element("P48", 44, 90, "Stahlbeton, KS-Wände, Kunststeintreppe")
			element("P49", 0, 0)
			element("P2", 0, 0)
			element("P3", 8, 90, "Flachdach mit Rasenspielplatz")
			element("P4", 7, 85, "Sichtbetonfass.")
			element("P5", 6, 90, "Holz-/Metallfenster")
			element(
				"P50",
				6,
				80,
				"Hallenbeleuchtung, Leuchten im Gang neu, Sicherungen o. FI-Schalter",
			)
			element("P51", 1, 90, "Audioanlage: Ersatz '15")
			element("P6", 0, 0, "Fernwärme")
			element("P7", 4, 90, "Lüftungsheizung")
			element("P8", 4, 90, "WC, Duschen")
			element("P57", 11, 90, "Garderobenbänke und -schränke, Innentüren")
			element(
				"P58",
				8,
				90,
				"B: Hallenboden (Ersatz '16), Keramikpl. W: Putz, Keramik D: Täfer, lasiert, Keramikpl. Gallerie neu",
			)
			element("P60", 0, 0)
			element("P61", 1, 90, "Aufzug, Lüftung tw. ersetzt, CO2-Sensor")
			element("P62", 0, 0)
		}
	}
	building("Aufbahrungsgebäude, Abdankungshalle", "Hauptstrasse 22", "3033", "Wohlen") {
		buildingNr = "08.15"
		buildingType = "T09"
		buildingSubType = "ST03-18"
		buildingYear = 1972
		volume = 875
		insuredValue = 564
		insuredValueYear = 2009
		description = "einzelne Räume wurden stillgelegt"
		rating(2012, "C0", "NW") {
			element("P1", 35, 90, "Stahlbetonstützen u. -decke gg. UG, Mauerwerk")
			element("P2", 8, 80, "Eternitschindeldach")
			element("P3", 0, 0)
			element("P4", 8, 90, "MW, verputzt, Kunststeingewände, Untersichten tw. beschädigt")
			element("P5", 5, 90, "Holzfenster gestrichen m. Doppelverglasung, Aussentor im UG")
			element("P6", 1, 80, "Elektroheizspeicherofen m. Raumfühler")
			element("P7", 1, 90, "Elektroheizspeicherofen m. Raumfühler")
			element(
				"P8",
				6,
				90,
				"WC, Ausguss, Schacht mit Pumpe für Abwasser wurde nachgerüstet",
			)
			element("P9", 6, 80, "Aussen- u. Innenbeleuchtung, alte Sicherungen")
			element("P10", 3, 80, "Kühlgerät")
			element("P11", 27, 90, "B: Natur-/Kunststeinpl., W: verputzt, D: Täfer")
			element("P11", 0, 0)
			element("P12", 0, 0)
		}
	}
	building("Verwaltungsgebäude (Gemeindehaus)", "Hauptstrasse 26", "3033", "Wohlen") {
		buildingNr = "08.16"
		buildingType = "T06"
		buildingSubType = "ST06-35"
		buildingYear = 1966
		volume = 7236
		insuredValue = 6000
		insuredValueYear = 2009
		description =
			"früher: Gemeindehaus und Wohnhaus\nGesamtsanierung '85 (Obergeschosse und Dach, therm. Isol. und Ersatz Fenster)\nErweiterung und Sanierung '07 (erw. therm. Isol.; Minergie)\nErsatz Fensterbeschläge '14"
		rating(2012, "C21", "NW") {
			element("P48", 28, 90, "Stahlbeton/Mauerwerk\nStahlbetonstützen")
			element("P49", 0, 0)
			element("P2", 5, 90, "Kaltdach, hinterlüftet")
			element("P3", 5, 90, "ext. begrünt m. Oblicht")
			element(
				"P4",
				8,
				90,
				"Verbundglas, hinterlüftet, Metallbl. hinterl., Zweisch.mauerw.",
			)
			element(
				"P5",
				14,
				80,
				"Holz-/Metallf., 3-fach vergl., Verbundgl., IV, Rafflamellenstoren, im alten Teil z. T. verzogene Rahmen (Gewicht)",
			)
			element("P50", 6, 90)
			element(
				"P51",
				2,
				80,
				"Gegensprechanlagen, Türschliesssystem, Telefonzentrale, EDV, Induktions-Audioanlage",
			)
			element("P6", 1, 80, "Pelletheizung")
			element("P7", 3, 85, "Bodenheizung im EG, Radiatoren m. Thermostaten")
			element("P8", 4, 85)
			element("P56", 2, 90, "Aufzug '07")
			element(
				"P57",
				14,
				90,
				"Schalterhalle, Schalterwände aus Holz u. Glas, Glastrennwände, Innentüren, Metalltreppe im DG,",
			)
			element(
				"P58",
				7,
				85,
				"B: Kunststein, Parkett, Keramik, Linol (OG)\nW: Putz, Keramikpl.\nD: Putz, gestr./Beton, gestr./Akustikputz, eingef., tw. heruntergeh. Gipsdecke (Caféteria)",
			)
			element("P60", 0, 0)
			element(
				"P61",
				1,
				90,
				"Lüftungsanlage sep. f. R., Sitzungszimmer, Cafeteria und Archiv, Pergola",
			)
			element("P62", 0, 0)
		}
	}
	building("Garage (Nebengebäude)", "Schulgasse 14a", "3033", "Wohlen") {
		buildingNr = "08.12"
		buildingType = "T13"
		buildingSubType = "ST01-2"
		buildingYear = 1975
		volume = 145
		insuredValue = 49
		insuredValueYear = 2009
		rating(2012, "C9", "NW") {
			element("P48", 51, 90, "Stahlbeton")
			element("P49", 0, 0)
			element("P2", 0, 0)
			element("P3", 21, 70, "Bitumen (Nacktdach)")
			element("P4", 18, 90, "Sichtbeton, gestr.")
			element("P5", 4, 80, "Garagentor")
			element("P50", 6, 90)
			element("P51", 0, 0)
			element("P6", 0, 0)
			element("P7", 0, 0)
			element("P8", 0, 0)
			element("P57", 0, 0)
			element("P58", 0, 0)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
}

/** Buildings for account 3034 - Hinterkappelen */
fun AccountContext.buildings3034() {
	building("Schulhaus, Pausenhalle , Kita", "Schulstrasse 4", "3034", "Hinterkappelen") {
		buildingNr = "08.03"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1963
		volume = 7481
		insuredValue = 5670
		insuredValueYear = 2012
		description =
			"Lüftung f. WC und Fumoir\nSanierung aussen und innen 2004\nWärmetechn. Sanierung 4.5 Mio geplant für ges. Schulanlage\nGeländehöhen zu tief\nSchiebetüren in Werkräumen\nFeuerleiter vorh.\nWindfang alt, undichte Türen\nIm Anbau Kita mit eigener Küche, Mansardendach ausgebaut, Böden Linol\nDach innen Gipskarton, gestrichen\nWC: ältere Apparate, aber funktionierend\nTreppe aus Kunststein\nEinbauschränke\nSimse Naturst.\nWände gestr./verputzt"
		rating(2012, "C9", "NW") {
			element("P48", 41, 90, "Mauerwerk und Stahlbeton")
			element("P49", 0, 0)
			element("P2", 8, 90, "Nach Brand ersetzt, im Dachboden isoliert")
			element("P3", 0, 0)
			element("P4", 7, 90, "Zweischalenmauerwerk, verputzt, neu gestr./verp.")
			element(
				"P5",
				10,
				90,
				"Holz-/Metallfenster, IV, Stoffstoren, Eingangstüren aus Metall / Windfang undicht",
			)
			element("P50", 6, 90)
			element("P51", 1, 90)
			element("P6", 1, 90, "Oelheizung '04 für ganze Schulanlage")
			element("P7", 3, 90, "Rad. m. Thermostaten")
			element("P8", 4, 90, "Leitungen: ausr. Druck")
			element("P57", 11, 90, "Wandschränke/Arbeitssimse, Glastrennw. im EG")
			element(
				"P58",
				8,
				90,
				"Klassenzimmer: B: Linol, Keramik, Putzraum:Kstst.,  Werkräume: Parkett/Linol, D: Akustik/Täfer, Akustikd. in Werkräumen Sritzputz, Zellulose, W: Putz/Täfer",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Turnhalle und Lehrschwimmbecken", "Schulstrasse 5", "3034", "Hinterkappelen") {
		buildingNr = "08.04"
		buildingType = "T12"
		buildingSubType = "ST07-38"
		buildingYear = 1981
		volume = 9627
		insuredValue = 5530
		insuredValueYear = 2012
		description =
			"Turnhallenboden neu\nHeizung erfolgt über Lüftung\nLehrschwimmbecken: Lüftung neu, W: Keramik tw. neu\nWarmwasseraufbereitung z.T. neu ('97 + '08) Wärmetauscher, Abwasser\nDruckluftsteuerung"
		rating(2012, "C9", "NW") {
			element("P48", 41, 80, "Stahlbeton, Mauerwerk, tw. Risse bei Innenwänden")
			element("P49", 0, 0)
			element("P2", 5, 85, "Ziegeldach; Dachraum konnte nicht besichtigt werden")
			element("P3", 3, 85, "Flachdachteil Foyer und Verbindungsgänge")
			element("P4", 7, 90, "Zweischalen- Mauerwerk, Eternitschindeln")
			element("P5", 10, 80, "Im Foyer: Metallf. '98, Holzf. in Turnhalle, 3-fach Vergl.")
			element("P50", 6, 90, "Beleuchtung TH neu, in Lehrschwimmbecken neu")
			element("P51", 1, 90, "Lautsprecheranlage")
			element("P6", 1, 90, "Wärmepumpe")
			element("P7", 3, 90, "Bodenheizung in Lehrschwimmbecken")
			element("P8", 4, 90, "WC z.T. älter, Druck i.O.")
			element("P57", 11, 90, "Metalldecke in WC, Holztäferdecken in Turnhalle")
			element("P58", 8, 90, "B: Keramik, Parkett (neu), Teppich\nD: Täfer")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Wohnhaus (2 Wohnungen)", "Schulstrasse 6", "3034", "Hinterkappelen") {
		buildingNr = "08.05"
		buildingType = "T01"
		buildingSubType = "ST02-11"
		buildingYear = 1963
		volume = 1150
		insuredValue = 902
		insuredValueYear = 2009
		description = "Anbau Wintergarten ca. 2006\nBadsanierung 2014"
		rating(2012, "C7", "NW") {
			element("P48", 35, 90, "Stahlbeton, Mauerwerk")
			element("P49", 0, 0)
			element("P2", 8, 90, "Ziegeldach")
			element("P3", 0, 0)
			element("P4", 7, 80, "Verp. Aussenwärmedämmung")
			element("P5", 11, 90, "Holz-/Metallf. mit IV")
			element("P9", 4, 90, "alte Sicherungen (Keramik)")
			element("P6", 2, 60, "Oelheizung, Brenner '10, Kessel '82, Boiler älter")
			element("P7", 3, 90, "Radiatoren m. Thermostaten")
			element("P54", 3, 90)
			element("P55", 4, 90)
			element("P56", 2, 90)
			element("P57", 9, 90, "Wandschränke (Garderobe)")
			element(
				"P58",
				8,
				85,
				"B: Parkett, Keramikpl., Laminat, Teppich, Linol, W: verputzt, D: verp. und Täfer",
			)
			element("P59", 4, 80, "wird tw. ersetzt")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Schulhaus", "Schulstrasse 7", "3034", "Hinterkappelen") {
		buildingNr = "08.06"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1980
		volume = 3440
		insuredValue = 2485
		insuredValueYear = 2012
		description =
			"Bibliothek vor wenigen Jahren saniert.\nEin Klassenzimmer mit Linolboden\nSolaranlage auf Steildach(?)"
		rating(2012, "C9", "NW") {
			element("P48", 41, 90, "Stahlbeton/Mauerwerk, Treppen Kunstststein")
			element("P49", 0, 0)
			element("P2", 8, 90, "keine sichtb. Mängel vorh.")
			element("P3", 0, 0)
			element("P4", 7, 90, "Zweischalen-MW, verp./Sichtbetonelem. (Fugen)")
			element("P5", 11, 90, "Im UG: Holz/IV, Metalllamellenstoren")
			element("P50", 6, 80, "Beleuchtung alt, ungenügend")
			element("P51", 1, 90, "Uhr")
			element(
				"P6",
				0,
				0,
				"Fernwärme von Schulstr. 4, Fotovotaikanlage vorh. (wird von SOKAWE finanz.)",
			)
			element("P7", 3, 90, "Radiatoren m. Thermostaten")
			element("P8", 4, 90, "Druck Leitungen normal")
			element("P57", 11, 80, "Innentüren z.T. schadhaft, Simse aus Holz")
			element("P58", 8, 90, "B: PVC, Klinker\nW: Putz, gestr.\nD: Putz, gestr., Täfer")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Schulhaus/ZS-Anlage/Verbindungsgang", "Schulstrasse 9", "3034", "Hinterkappelen") {
		buildingNr = "08.07"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		volume = 7446
		insuredValue = 4970
		insuredValueYear = 2009
		description = "Ausbau ähnlich wie Schulstr. 4\nSchulküche wird '13 saniert"
		rating(2012, "C9", "NW") {
			element("P48", 41, 90, "Stahlbeton")
			element("P49", 0, 0)
			element("P2", 8, 90, "Ziegeldach")
			element("P3", 0, 90)
			element("P4", 7, 90, "Stahlbeton")
			element("P5", 10, 80, "Holz, z.T. verzogen, Rafflamellenstoren, Dicht. spröde")
			element("P50", 6, 90, "Sicherungen, Schalter")
			element("P51", 1, 90, "Uhr, Gong")
			element("P6", 0, 90)
			element("P7", 3, 90, "Radiatoren/Konvektoren")
			element("P8", 4, 90, "Leitungen und Apparate i.O.")
			element("P57", 11, 90, "Holztäferwand in GR und K(EG)")
			element("P58", 8, 90, "B: Kunststein, Keramik, PVC")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Primarschule/Doppelturnhalle/KZ/WR", "Kappelenring 36/36a", "3034", "Hinterkappelen") {
		buildingNr = "08.10"
		buildingType = "T12"
		buildingSubType = "ST01-9"
		buildingYear = 1976
		volume = 19011
		insuredValue = 13947
		insuredValueYear = 2009
		description =
			"Dachsanierung '97; 2.4 Mio. Fr. (davon 400T für Brandschutzmassnahmen)\nFassaden: Fugen neu, neu gestrichen\nFenster: Beschläge tw. neu\nIn Vorraum TH finden Gemeindeversammlungen statt (Bühne)\n\nTw. Klassenzimmer unterteilt in Gruppenräume (Spezialräume Logopädie ok)\nTH: neue Böden 2018, Beleuchtung, Audioanlage mangelhaft, *Fenster werden nicht ersetzt"
		rating(2012, "C9", "NW") {
			element("P48", 39, 90, "Stahlbeton, MW(KS)")
			element("P49", 0, 0)
			element("P2", 0, 0)
			element("P3", 8, 90, "Kiesklebedach mit Oblichtkuppeln (neu)")
			element("P4", 7, 90, "Sichtbeton, gestr./b. Eingang verputzt")
			element(
				"P5",
				10,
				70,
				"Holz-/Metallfenster/Rafflamellenstoren (Fenster tw. blind), Fenster & Rafflamellenstoren werden ersetzt (2018-2021)*",
			)
			element("P50", 6, 90, "Beleuchtung tw. neu ('97), Bühnenbeleuchtung")
			element("P51", 1, 90, "Uhr, Gong, LS-Analge, BMA nachgerüstet")
			element("P6", 1, 90, "Boiler/Solaranlage/Fernwärme")
			element("P7", 3, 90, "Radiatoren, Heizlüftung in TH")
			element("P8", 4, 70, "In Du: KW+WW-Leitungen neu ('97), stark verrostet, LZ: Küche")
			element(
				"P57",
				11,
				90,
				"Einbauschränke, WC-Trennwände, Glastrennwände Windfang, TH-Boden wird saniert '12/ Bühne",
			)
			element(
				"P58",
				8,
				80,
				"B: PVC/Teppich, Keramikpl., Gumminoppenbel., Linol\nW: Sichtb., KS, Kermaikpl., Akustikpl. im Musikraum, Täfer in KZ\nD: Beton, gestr., Heraklit",
			)
			element("P60", 0, 0)
			element(
				"P61",
				2,
				80,
				"Lüftung Garderoben/Duschen, Heizungslüftung Turnhalle tw. saniert (Du '98)",
			)
			element("P62", 0, 0)
		}
	}
	building("Schützenhaus", "Schützenweg 50", "3034", "Murzelen") {
		buildingNr = "08.17"
		buildingType = "T12"
		buildingSubType = "ST10-45"
		buildingYear = 2004
		volume = 809
		insuredValue = 787
		insuredValueYear = 2009
		description = "Munitionsraum mit Panzertüre"
		rating(2012, "C0", "NW") {
			element("P1", 35, 90, "Holzständerbau, Stahlbeton im UG")
			element("P2", 8, 90, "Falzziegeldach")
			element("P3", 0, 0)
			element("P4", 8, 80, "Holzplatten, hinterlüftet")
			element(
				"P5",
				6,
				80,
				"Holzfenster m. IV, Holzschiebeladen und Markise, im UG: Metall-Rollläden",
			)
			element("P6", 1, 80, "Schwedenofen und Elektroofen im WC")
			element("P7", 0, 0)
			element("P8", 6, 85, "WC")
			element(
				"P9",
				6,
				85,
				"Stark-/Schwachstrom, Elektr. Trefferanzeige, Beleuchtung im UG, Sicherung mit FI-Schutzschalter",
			)
			element("P10", 3, 80, "Küche")
			element(
				"P11",
				14,
				90,
				"Innere Verglasungen, Innenwände aus Holz, Einbauschränke, Garderobe",
			)
			element(
				"P11",
				13,
				90,
				"B: Keramikpl., UG: Gummischrotbelag\nW: Spanplatten, natur, Täfer (Fass.wände), Putz, UG: Akutikverkl.\nD: Bretterb., unisol., UG: Akustikverkl.",
			)
			element("P12", 0, 0)
		}
	}
	building("Wohnhaus (3 Whg.)", "Murzelenstrasse 80", "3034", "Murzelen") {
		buildingNr = "08.18"
		buildingType = "T01"
		buildingSubType = "ST02-11"
		buildingYear = 1955
		volume = 1375
		insuredValue = 1162
		insuredValueYear = 2009
		description =
			"Urspr. Lehrerhaus\nAusgebauter Dachraum, isol.\nDecke gegen UG wurde isoliert ca. 2000"
		rating(2012, "C7", "NW") {
			element("P48", 38, 90, "Mauerwerk, Kunststeintreppe")
			element("P49", 0, 0)
			element("P2", 9, 90, "Falzziegeldach, Untersicht und Spenglerarbeiten i.O.")
			element("P3", 0, 0)
			element(
				"P4",
				7,
				80,
				"Mauerwerk, verputzt, auf Ostseite Putzschäden, Kunststeingewände i.O.",
			)
			element(
				"P5",
				11,
				90,
				"Holztüren und Holz-/Metallfenster (Glas '03) mit innenlieg. Sprossen, Holzläden und Storen, Rollladen im WZ",
			)
			element("P9", 4, 90, "Alte Sicherungen, tw. neu f. WM, Beleuchtung im UG neu")
			element("P6", 0, 90, "Fernwärme, WW: Boiler 600 Liter '78")
			element("P7", 3, 90, "Radiatoren m. Thermostaten ausser im Bad")
			element("P54", 3, 90, "Apparate älter, tw. o. Mischarmaturen, WM/WT rel. neu")
			element("P55", 4, 90, "Leitungen i.O.")
			element("P56", 0, 0)
			element("P57", 9, 90)
			element(
				"P58",
				8,
				90,
				"B: Klinker, Parkett, Teppich (Kugelgarn), PVC in Kü\nW: Plattenschild, Putz\nD: Putz",
			)
			element("P59", 4, 90, "Küche m. GSP u. Glaskeramikkochfeld, ren. '15")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Garagen/Aussengeräteraum Schule", "Murzelenstrasse 82A", "3034", "Murzelen") {
		buildingNr = "08.19"
		buildingType = "T13"
		buildingSubType = "ST01-2"
		buildingYear = 1978
		volume = 360
		insuredValue = 152
		insuredValueYear = 2009
		rating(2012, "C0", "NW") {
			element("P1", 38, 90, "Betonsteine (MW), Holzdachstuhl")
			element("P2", 12, 90, "Welleternit, unisol.")
			element("P3", 0, 0)
			element("P4", 8, 90, "Betonsteine, roh, Welleternit")
			element("P5", 8, 90, "Garagentore neu ('13)")
			element("P6", 0, 0)
			element("P7", 0, 0)
			element("P8", 3, 85, "Aussenanschl.")
			element("P9", 4, 85, "Aussenbeleuchtung")
			element("P10", 0, 0)
			element(
				"P11",
				27,
				90,
				"B: Zementüberzug, Betonverbundsteine, W: Betonsteine, roh, D: -",
			)
			element("P11", 0, 0)
			element("P12", 0, 0)
			element("P1", 38, 90, "Betonsteine (MW), Holzdachstuhl")
			element("P2", 12, 90, "Welleternit, unisol.")
			element("P3", 0, 0)
			element("P4", 8, 90, "Betonsteine, roh, Welleternit")
			element("P5", 8, 90, "Garagentore neu ('13)")
			element("P6", 0, 0)
			element("P7", 0, 0)
			element("P8", 3, 85, "Aussenanschl.")
			element("P9", 4, 85, "Aussenbeleuchtung")
			element("P10", 0, 0)
			element(
				"P11",
				27,
				90,
				"B: Zementüberzug, Betonverbundsteine, W: Betonsteine, roh, D: -",
			)
			element("P11", 0, 0)
			element("P12", 0, 0)
		}
	}
	building("Schulhaus/Kindergarten/Hauswartwohnung", "Murzelenstrasse 82", "3034", "Murzelen") {
		buildingNr = "08.20"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1955
		volume = 3920
		insuredValue = 2884
		insuredValueYear = 2009
		description =
			"Schulleiterbüro eingebaut\nIm Windfang wurden die inneren Türen demontiert\nKindergarten: Zwischengeschoss eingebaut\nHW-Wohnung: Estrich neu, isoliert mit Leitertreppe, (Boden) '14 \nSanitär: Druck fällt tw. zusammen K+W, Küche neu '14, Bad: ung. 40 Jahre alt (nur WC ausgewechselt)\nSchulzimmer: (1 Zi: neuer Boden Linol) neue Beleuchtung im EG und OG\nKindergarten: Fensterfront ist nicht dicht (Fenterflügel wurden gerichtet), Regen drückt rein, Zugluft\nFassade aussen: Holzfensterbänke/-schwellen tw. morsch\nZugseile innen ev. nachspannen, Holzverzapfungen bei Balken ev. feucht (dunkel und Rost bei Metallverbindungen)\nAufsteigende Feuchtigkeit im Treppenhaus\nBoden UG neu\nDecken und Wände neu gestrichen im EG und OG\nneu Mittagstisch\nProbleme mit Fliegen; ev. Luftzug bei Storenkästen"
		rating(2012, "C9", "NW") {
			element("P48", 40, 90, "Mauerwerk, verputzt/MW, gestr. im UG, Kunststeintreppen")
			element("P49", 0, 0)
			element(
				"P2",
				9,
				90,
				"Falzzeigel, Welleternit, Dachraum isol. (tw.), Dachflächenfenster neu in Wg., Dachgaupen/Lukarnen mit Blechdach",
			)
			element("P3", 0, 0)
			element("P4", 7, 90, "Mauerwerk, verputzt")
			element(
				"P5",
				10,
				70,
				"Holz-/Metallfenster m. IV, Holzf. IV, Stoffstoren, Haustüre ohne Gummidichtung seitl., KZ: innere Verdunklungen",
			)
			element(
				"P50",
				6,
				80,
				"Beleuchtung veraltet, dito Schalter, in KG neu, alte Sicherungen",
			)
			element(
				"P51",
				1,
				90,
				"Telefon, Funk, EDV, Uhr, Gong, neuer Server nach Hochwasser '14",
			)
			element("P6", 0, 0, "Fernwärme")
			element("P7", 3, 90, "Radiatoren m. Thermostaten, tw. o. Thermost. (im Entrée)")
			element("P8", 4, 80, "Leitungen: s. Bem., Apparate älter")
			element(
				"P57",
				11,
				90,
				"Einbauschränke, Simse aus holz, Geländer aus Metall, Vorhangbretter",
			)
			element(
				"P58",
				8,
				90,
				"B: Korkpl., Klinker, PVC, Linol, Kunststoffguss in WC, Parkett, W: Putz, Korkpl.schild, Beton, gestr., Keramikpl.schild",
			)
			element("P60", 0, 0)
			element("P61", 1, 90, "Küche in KG und GR, Wg DG (wurde '14 ersetzt)")
			element("P62", 0, 0)
		}
	}
	building("Turnhalle", "Murzelenstrasse 84", "3034", "Murzelen") {
		buildingNr = "08.21"
		buildingType = "T12"
		buildingSubType = "ST01-9"
		buildingYear = 1978
		volume = 4608
		insuredValue = 2097
		insuredValueYear = 2009
		description =
			"In Investitionsplan: Ersatz Fensterfront und Isolation Dach\nFenstersanierung und Decke ist im IP\nMalerarbeiten im '13\nDuschen: Armaturen neu ('13)\nWW-Leitung tw. neu\nUG: Beleuchtung neu (ausser Turnhalle)\nFenster im WC wurden ersetzt: Holz/Metall ('16)\nGeländer wird neu gemacht"
		rating(2012, "C9", "NW") {
			element("P48", 39, 90, "Stahlbeton, -stützen, -träger")
			element("P49", 0, 0)
			element("P2", 8, 80, "Welleternit")
			element("P3", 0, 0)
			element("P4", 6, 90, "Mauerwerk, verputzt, Welleternit")
			element("P5", 12, 50, "Metallfenster, Holzf. in Gard./WC, Windfang: Gläser '78")
			element("P50", 6, 70, "Beleuchtung alt, Notbeleuchtung, alte Sicherungen")
			element("P51", 1, 90, "Uhr, LS-Anlage")
			element("P6", 2, 75, "Holzschnitzelfeuerung '09")
			element("P7", 3, 75, "Lüftungsheizung (alt), Radiatoren m. Thermostaten")
			element(
				"P8",
				4,
				70,
				"Abwasserschacht unter Treppe schadhaft -> rep. im '13, Leitungen tw. rostig",
			)
			element("P57", 11, 90, "Tore Geräteraum neu, Garderobenschränke")
			element(
				"P58",
				8,
				80,
				"B: Hallenboden intakt, Gumminoppenbel., Teppichschleuse, Logop.: Teppich W: Putz, Keramik in WC, Putz in Logop. D: Täfer, Metalldecke, Pavatexpl. gestr.",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
}

/** Buildings for account 3043 - Uettligen */
fun AccountContext.buildings3043() {
	building("Velounterstand", "Schülerweg 13", "3043", "Uettligen") {
		buildingNr = "08.24"
		buildingType = "T13"
		buildingSubType = "ST01-2"
		buildingYear = 1996
		volume = 360
		insuredValue = 158
		insuredValueYear = 2009
		description = "*Gläser werden/wurden etappenweise ersetzt"
		rating(2012, "C9", "NW") {
			element("P48", 67, 90, "Stahlkonstruktion")
			element("P49", 0, 0)
			element("P2", 25, 90, "Glas/Metall*")
			element("P3", 0, 0)
			element("P4", 0, 0)
			element("P5", 0, 0)
			element("P50", 4, 90, "Beleuchtung")
			element("P51", 0, 0)
			element("P6", 0, 0)
			element("P7", 0, 0)
			element("P8", 4, 90, "Dachwasserablauf")
			element("P57", 0, 0)
			element("P58", 0, 0)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Turnhalle", "Schülerweg 14", "3043", "Uettligen") {
		buildingNr = "08.25"
		buildingType = "T12"
		buildingSubType = "ST01-9"
		buildingYear = 1996
		volume = 8200
		insuredValue = 3828
		insuredValueYear = 2009
		description =
			"Turnhallenboden wurde ersetzt '17\nTreppenlift\nGeräteschuppen für Sprungmatten mit Riffelblechen im Aussenbereich an Fassade angebaut\nBei Duschen Wasserschaden (Fugen müssen periodisch ersetzt werden)"
		rating(2012, "C9", "NW") {
			element("P48", 41, 90, "Stahlbetonbau")
			element("P49", 0, 0)
			element("P2", 0, 0)
			element("P3", 8, 90, "Flachdach mit Rubtanbelag")
			element("P4", 8, 90)
			element(
				"P5",
				10,
				90,
				"Pfosten-/Rigelkonstr., IV-Gläser, Metalltüren m. Glas, Metallfenster im Aussengeräteraum",
			)
			element("P50", 6, 90, "Tw. Keramiksicherungen, Beleuchtung i.O.")
			element("P51", 1, 90, "Tel., LS-Anlage, Uhr")
			element("P6", 0, 0, "Fernwärme")
			element(
				"P7",
				3,
				90,
				"Lüftungsheiz., Radiatoren m. Thermostaten ind Schiedsrichterraum und Garderoben, Bodenheizung in Garderoben und Foyer",
			)
			element("P8", 4, 90, "Duschen i.O.")
			element("P57", 11, 90, "Innere Verglasungen, Tore")
			element(
				"P58",
				8,
				90,
				"B: Keramikpl., TH-Boden, Teppich Schmutzschleuse, W: Putz, gestr., MW roh, Beton roh, D: Holz",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Primarschulhaus, ged. Pausenhalle, Anbau Nord", "Schülerweg 15", "3043", "Uettligen") {
		buildingNr = "08.26"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1962
		volume = 5319
		insuredValue = 3670
		insuredValueYear = 2009
		description =
			"In Investitionsplan: Ersatz Holzfenster\nP.. Ludothek wurde nachträglich angebaut mit Duripaneel\nInnere Verdunklungen\nTagesschule im UG\nAnbau ca. '04\nWassereintritt in Bibliothek (Deckenverkl. und Parkett beschädigt)"
		rating(2012, "C9", "NW") {
			element("P48", 41, 90, "Stahlbeton, Mauerwerk, Natursteintreppe")
			element("P49", 0, 0)
			element("P2", 6, 90, "Falzziegeldach, Dachflächenfenster")
			element("P3", 2, 90, "Kiesklebedach, ext. begrünt, Vordach: Kiesklebedach")
			element(
				"P4",
				7,
				70,
				"Eternitschindeln, Mauerwerk verputzt, Beton gestr., Holzf. bei Anbau",
			)
			element(
				"P5",
				11,
				80,
				"Entrée: Metall, Holzfenster, Betongewände, Holz-/Metall, Rafflamellenstoren",
			)
			element("P50", 6, 90, "Beleuchtung neu")
			element("P51", 1, 90, "Uhr, Gong, EDV-Leit u. -anschlüsse")
			element("P6", 0, 0, "Fernwärme")
			element("P7", 3, 90, "Radiatoren m. Thermostaten")
			element("P8", 4, 90, "Teeküche in Anbau, Küche im UG Anbau")
			element("P57", 11, 90, "Einbauschränke, Kunststeinsimse, Holzsimse, Arbeitssimse")
			element(
				"P58",
				8,
				90,
				"B: Kunststein (Riss im UG), Linol in Anbau, Parkett in SL-Büro und Werkr., Gussasphalt in Stauraum\nW: Putz, gestr., Täfer in KZ, Keramik in WC\nD: Täfer, Akustik-Holzdecken, Spritzputz in KZ",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Turnhalle/Hauswartwhg./Heizzentrale", "Schülerweg 16", "3043", "Uettligen") {
		buildingNr = "08.27"
		buildingType = "T12"
		buildingSubType = "ST01-9"
		buildingYear = 1961
		volume = 4497
		insuredValue = 2820
		insuredValueYear = 2009
		description =
			"Lüftung für Holzsschnitzel (wegen Gärung)\nTore Geräteraum TH ok\nRadiatoren in TH o. Thermostaten\nHW-Wohnung:\n B: Linol, Parkett in WZ, Teppich UG\n W: Putz, gestr.\n D: Putz, gestr., Täfer in Kü, Pavatex, gestr.\n Heizverteilung: Radiatoren m. Thermostaten\n Balkon: Untersicht Holz i.O., Bodenablauf vermoost, Stütze verputzt (Putzschäden), Risse in Fass. über Balkon\n Fenster ('00): IV, Küche Metall, Einbauschränke, Vorhangbretter, Simse"
		rating(2012, "C9", "NW") {
			element(
				"P48",
				41,
				80,
				"Mauerwerk, Stahlbeton, Risse in Mat.raum Boden, Decke und Wand",
			)
			element("P49", 0, 0)
			element(
				"P2",
				8,
				90,
				"Falzziegeldach; ca. '91 nachisoliert, Unterdach i.O.; in HW-Wohnung isoliert, Dachfenster, Estrich -> Zimmer umgebaut '17",
			)
			element("P3", 0, 0)
			element("P4", 7, 90, "Mauerwerk, verputzt, Beton, gestr., Eternitschindeln")
			element(
				"P5",
				10,
				90,
				"Holzf. IV (Gläser '00) mit Vert.storen ('00), Holzf. 2-fach im Geräteraum, Aussentor zum Geräteraum isoliert",
			)
			element("P50", 6, 90, "Beleuchtung in Garderobe wurde durch LED ersetzt")
			element("P51", 1, 90)
			element(
				"P6",
				1,
				70,
				"Lüftungsanlage f. Garderobe ('84), Holzschnitzelfeuerungsanlage ('90/'09)\nSteuerung defekt, keine Ersatzteile mehr erhältlich, wird ev. ersetzt durch Fernwärme",
			)
			element("P7", 3, 90, "Heizkörper mit Thermostaten in Geräteraum und vor Garderobe")
			element("P8", 4, 85, "Dachwasserablauf i.O., tw. alte WC")
			element("P57", 11, 90)
			element(
				"P58",
				8,
				90,
				"B: PVC-Platten, Porphyr in Garderoben, Kunststein, TH-Boden, PVC im Geräteraum\nD: TH: Heraklit, Holztäfer",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Oberstufenschulhaus/Aula", "Schülerweg 18", "3043", "Uettligen") {
		buildingNr = "08.28"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1961
		volume = 17300
		insuredValue = 9648
		insuredValueYear = 2009
		description =
			"Gebäudehülle wie Schülerweg 15\nAnbau '91 Erweiterungen\nAufzug im Anbau\nKüche bei Mittagstisch im UG, bei Mittagstisch nur GSP\nim UG neu elektrische Storen, zusätzlich innere Verdunkelungen\nLehrerzimmer; neuer Boden (Parkett), Deckenbeleuchtung bei Fenster"
		rating(2012, "C9", "NW") {
			element("P48", 39, 90, "Stahlbeton, Mauerwerk")
			element("P49", 0, 0)
			element(
				"P2",
				11,
				90,
				"Falzziegeldach, Holz, Stahl, Glasdach, Untersichten Holz i.O.",
			)
			element("P3", 0, 0)
			element("P4", 7, 85, "Eternitschindeln, MW verp., Beton sicht, tw. gestrichen")
			element("P5", 10, 85, "Holzfenster, Metalltüren/Glas, Lamellenstoren")
			element("P50", 6, 90, "Beleuchtung tw. neu, Elektrovert. neu")
			element("P51", 1, 90, "EDV, Uhr, Gong, Glocke, Brandmeleanlage")
			element("P6", 0, 0, "Fernwärme")
			element("P7", 3, 90, "Radiatoren, Konvektoren, Bodenheizung im Anbau")
			element("P8", 4, 85, "Leitungen i.O., Apparate ohne Mängel")
			element(
				"P57",
				11,
				90,
				"Einbauten, Garderobenschäfte, Arbeitssimse, Glasvitrinen, neue Küchenmöbel in Lehrerzimmer",
			)
			element(
				"P58",
				8,
				85,
				"B: Keramik, Parkett im Saal u. Werkräumen, Linol, W: Täfer, Putz, Beton roh im Anbau, Keramik in Schulkü., D: Akustikpl. (Pavatex) im DG, Beton gestr., Täfer, Heraklit",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Velounterstand", "Alpenblickweg 10", "3043", "Uettligen") {
		buildingNr = "08.29"
		buildingType = "T13"
		buildingSubType = "ST01-2"
		buildingYear = 1994
		volume = 110
		insuredValue = 102
		insuredValueYear = 2009
		rating(2012, "C9", "NW") {
			element("P48", 67, 90, "Stahlkonstruktion")
			element("P49", 0, 0)
			element("P2", 25, 90, "Glas/Metall")
			element("P3", 0, 0)
			element("P4", 0, 0)
			element("P5", 0, 0)
			element("P50", 4, 90, "Beleuchtung")
			element("P51", 0, 0)
			element("P6", 0, 0)
			element("P7", 0, 0)
			element("P8", 4, 90, "Dachwasserablauf")
			element("P57", 0, 0)
			element("P58", 0, 0)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Kulturelles Zentrum Reberhaus", "Lindenstrasse 4", "3043", "Uettligen") {
		buildingNr = "08.31"
		buildingType = "T10"
		buildingSubType = "ST06-36"
		buildingYear = 1900
		volume = 5500
		insuredValue = 4404
		insuredValueYear = 2009
		description =
			"Umnutzung Bauernhaus in Kulturzentrum 1987\nTreppen und Niveaulift\nKüche im Schrank zu Stuben und Saal (m. GSP, Kaffeemasch. u. mob. Backofen)"
		rating(2012, "C29", "NW") {
			element(
				"P48",
				21,
				90,
				"Stahlskelettbau, KS-Wämde, Bohlenständerbau, alter Teil m. Fachwerk, Gewölbekeller",
			)
			element("P49", 0, 0)
			element("P2", 14, 90, "Pfettendach m. Falzziegel, Walmdach m. Gaupen")
			element("P3", 0, 0)
			element("P4", 10, 90, "KS-Wände, Bohlen, Lauben, Betonsockel, Natursteinsockel")
			element(
				"P5",
				12,
				85,
				"Stahl-/Glasfassade, Metallf., Holzf. m. IV -> grosse Holzfenster lassen sich nicht mehr gut schliessen",
			)
			element(
				"P9",
				4,
				80,
				"Fass.beleuchtung, BMA, Tel., Bühnenbeleuchtung und -technik wird revidiert, Beamer, Audioanlage",
			)
			element(
				"P6",
				1,
				90,
				"Luftwärmepumpe wurde vor ca. 4-5 Jahren ersetzt, dez. Boiler im DG, Ölheizung",
			)
			element("P7", 4, 90, "Radiatoren o. Thermostaten, Bodenheizung")
			element("P52", 3, 90, "Saallüftung")
			element("P53", 3, 90, "Küchenabluft")
			element("P8", 5, 90, "Leitungen i.O. (rel. gross dim. im DG)")
			element("P57", 11, 90, "Einbauschränke, Garderoben, Schiebewand im Saal")
			element(
				"P58",
				4,
				90,
				"B: Keramik, Parkett, Dielen Empore\nW: KS roh, Täfer\nD: Täfer lasiert",
			)
			element("P59", 6, 90)
			element("P60", 0, 0)
			element("P61", 2, 90, "Aufzug nachgerüstet m. Innenkab.")
			element("P62", 0, 0)
		}
	}
	building("Feuerwehrmagazin/Schulungsraum", "Ahornweg 7", "3043", "Uettligen") {
		buildingNr = "08.32"
		buildingType = "T14"
		buildingSubType = "ST07-37"
		buildingYear = 1999
		volume = 1600
		insuredValue = 613
		insuredValueYear = 2009
		description = "Fluchttreppe in Stahl\nKein UG"
		rating(2012, "C0", "NW") {
			element("P1", 35, 90, "Zweischalen-KS-MW, Stahlstützen, Holzkonstr.")
			element("P2", 8, 90, "Wellblechvordach und -dach")
			element("P3", 0, 0)
			element("P4", 8, 90, "KS-MW")
			element("P5", 9, 90, "Stahltore m. IV")
			element("P6", 0, 0, "Fernwärme, Lüftungsheizung für Feuerwehr")
			element("P7", 2, 90, "Radiatoren m. Thermostaten")
			element("P8", 6, 90, "Leitungen i.O., Küche")
			element("P9", 6, 90, "Beleuchtung")
			element("P10", 3, 80, "Kompressor, etc.")
			element("P11", 23, 90, "Einbauschränke")
			element(
				"P11",
				0,
				0,
				"B: Hartbeton, PVC im OG, Spanplatten, Teppich, W: roh, D: Pavatex",
			)
			element("P12", 0, 0)
		}
	}
	building("ZS-Lager/BSA/Einstellhalle/Rasenspielfeld", "Schülerweg 11", "3043", "Uettligen") {
		buildingNr = "08.22"
		buildingType = "T14"
		buildingSubType = "ST03-19"
		buildingYear = 1996
		volume = 7300
		insuredValue = 3102
		insuredValueYear = 2009
		description =
			"4 Wartungen pro Jahr durch ZS (2 gr. u. 2 kl.)\n\nUNG: XXX bei Hartbeton (s. Foto) auf Parkplätzen"
		rating(2012, "C0", "NW") {
			element("P1", 36, 90, "Stahlbeton, KS-Wände")
			element("P2", 0, 0)
			element("P3", 8, 85, "Flachdach mit Rasenspielfeld")
			element("P4", 8, 90)
			element("P5", 8, 90, "Holzfenster, IV, Metalltor")
			element("P6", 0, 0, "Fernwärme")
			element("P7", 2, 90, "Radiatoren in zwei Räumen, m. Thermostat")
			element("P8", 6, 85, "BSA: Küche, Lavabo, WC, Rinnen Garagenboden")
			element("P9", 6, 85, "Beleuchtung ZS, Garage")
			element("P10", 3, 80, "Lüftung, Abzug Küche")
			element(
				"P11",
				23,
				90,
				"Holztrennwände, B: Linol, Zementüberzug oder Hartbeton, gestr.\nW: verp. u. Keramik, KS roh, Beton roh\nD: Heraklit",
			)
			element("P11", 0, 0)
			element("P12", 0, 0)
		}
	}
	building("Doppelkindergarten/Musikschule/Jugendtr.", "Schülerweg 12", "3043", "Uettligen") {
		buildingNr = "08.23"
		buildingType = "T02"
		buildingSubType = "ST01-4"
		buildingYear = 1987
		volume = 3000
		insuredValue = 1898
		insuredValueYear = 2009
		description =
			"Küche im Jugendtreff für Begehung nicht zugängig; gem. Hauswart ist Küche in schlechtem Zustand\nEhem. Heizraum ist neu Schlagzeugraum; neue Fenster, Teppich, Beleuchtung, Wände, Decken (Akustikpl.)\nLehrerzimmer im UG mit Küche\nEntfeuchtungsanlage Schlagzeugraum ist neu\nJugendraum: neu: nur Unterlagsboden (Parkett wurde ersetzt)"
		rating(2012, "C9", "NW") {
			element("P48", 39, 85, "Mauerwerk, Stahlbeton, leichter Riss")
			element("P49", 2, 90, "Zw.boden")
			element(
				"P2",
				8,
				90,
				"Eternitschindeldach, Dachflächenfenster, Spenglerarbeiten i.O.",
			)
			element("P3", 0, 0)
			element("P4", 7, 90, "Zweischalenmauerwerk, verputzt")
			element(
				"P5",
				10,
				90,
				"Holzfenster IV 2-fach/3-fach, Rafflamellenstoren, Metalltüre aussen, Musikraum",
			)
			element("P50", 6, 90, "Neue Sicherungen, Beleuchtung")
			element("P51", 1, 85, "Glocke, Tel.")
			element("P6", 0, 0, "Fernwärme (früher Wärmepumpe)")
			element("P7", 3, 90, "Bodenheizung, im UG Radiatoren")
			element("P8", 4, 85, "Lavabo im KG, Küche, Dachwasserablauf, Flüssiggasanlage")
			element("P57", 11, 90, "Einbauschränke, Simse")
			element(
				"P58",
				9,
				85,
				"B: Keramik, Teppich SS, Parkett/Novilon\nW: Pl.schild Küche, Putz, gestr., Täfer in Musikr., Keramik in WC\nD: Täfer, Gipskartonpl. in Materialr., Pavatexpl. in Musikr., in Jugendraum Beton, gestr.",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
}

/** Buildings for account 8253 - Diessenhofen */
fun AccountContext.buildings8253() {
	building("best. Schulhaus Zentrum", "Schulstrasse 5", "8253", "Diessenhofen") {
		buildingNr = "02.02"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1958
		volume = 13886
		insuredValue = 8497
		insuredValueYear = 2006
		rating(2010, "C0", "N") {
			element("P1", 35, 80, "Stahlbetondecken, Mauerwerk")
			element("P2", 4, 50, "Ziegeleindeckung, schadhaft")
			element("P3", 0, 0)
			element("P4", 8, 85, "Doppelschaliges Mauerwerk, verputzt")
			element("P5", 8, 75, "Holzfenster, Eingangstüren Stahl")
			element("P6", 1, 85, "Fernwärme vom Nachbargebäude (altes SH, san.)")
			element("P7", 2, 80, "Radiatoren mit Thermostaten")
			element("P8", 6, 90, "Abwasserleitungen aus Gusseisen, stellenweise saniert")
			element("P9", 6, 80, "Beleuchtung Klassenzimmer ca. 15-jährig")
			element("P10", 3, 90, "Lüftung im Saal DG")
			element("P11", 27, 75, "Üblicher Innenausbau mit Linolböden, Treppenhaus Naturstein")
			element("P11", 0, 0)
			element("P12", 0, 0)
		}
	}
	building("Kindergarten Schupfenzelg", "Schützenstrasse", "8253", "Diessenhofen") {
		buildingNr = "02.04"
		buildingType = "T02"
		buildingSubType = "ST01-4"
		buildingYear = 1973
		volume = 1624
		insuredValue = 861
		insuredValueYear = 2009
		rating(2011, "C8", "N") {
			element("P48", 24, 85, "Hohlkörperdecke, Porenbetonwände")
			element("P49", 0, 0)
			element("P2", 20, 90, "Welleternit, Dämmung über Decke über EG")
			element("P3", 0, 0)
			element("P4", 8, 80, "Porenbeton, verputzt\nAnbau Holz")
			element("P5", 14, 90, "Holz/Metall\nErsatz 2003 inkl. Türen\nWSG")
			element("P9", 4, 90, "Gemäss HW genügende Absicherungen und keine Mängel bekannt")
			element("P6", 3, 90, "Gasheizung")
			element("P7", 3, 90, "Radiatoren mit Thermostaten")
			element(
				"P54",
				1,
				85,
				"Lavabo tw. mit WW-Mischer, Küchengeräte orig. '73\nWW-Boiler in Küche eingeb.",
			)
			element("P55", 3, 90)
			element("P57", 17, 90, "Arbeitssimse, Einbauschränke, Garderoben")
			element(
				"P58",
				3,
				70,
				"Böden: PVC\nWände: verputzt o. Holztäfer\nDecke: Mineralfaserplatten",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Kindergarten Basadingen", "Schulstr. 38", "8253", "Diessenhofen") {
		buildingNr = "02.01"
		buildingType = "T02"
		buildingSubType = "ST01-4"
		buildingYear = 1979
		volume = 1049
		insuredValue = 670
		insuredValueYear = 2006
		rating(2010, "C8", "N") {
			element("P48", 24, 70, "Hourdisdecke, hinterlüftet mit Stützen")
			element("P49", 0, 0)
			element("P2", 20, 70, "Pfettendach m. 10cm Dämmung")
			element("P3", 0, 0)
			element("P4", 8, 50, "Massiv, verputzt und Holzstützen")
			element("P5", 14, 70, "Holz, IV-Verglasung, Glas 3mm")
			element("P9", 4, 90, "neuere Sicherungen")
			element("P6", 3, 80, "Holzschnitzelfeuerung und Elektroboiler")
			element("P7", 3, 80, "Radiatoren o. Thermostaten, im neuen Anbau: Bodenheizung")
			element("P54", 1, 80)
			element("P55", 3, 70)
			element("P57", 17, 70)
			element("P58", 3, 70, "Parkettböden, Holzwände und verputzte Wände")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Sanierung Schulhaus Zentrum", "Schulstrasse 5", "8253", "Diessenhofen") {
		buildingNr = "02.02.1"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1958
		volume = 13886
		insuredValue = 12000
		insuredValueYear = 2018
		rating(2010, "C0", "N") {
			element("P1", 27, 100, "Stahlbetondecken, Mauerwerk")
			element("P2", 4, 100, "Ziegeleindeckung, schadhaft")
			element("P3", 0, 0)
			element("P4", 6, 100, "Doppelschaliges Mauerwerk, verputzt")
			element("P5", 12, 100, "Holzfenster, Eingangstüren Stahl")
			element("P6", 1, 100, "Fernwärme vom Nachbargebäude (altes SH, san.)")
			element("P7", 4, 100, "Radiatoren mit Thermostaten")
			element("P8", 5, 100, "Abwasserleitungen aus Gusseisen, stellenweise saniert")
			element("P9", 10, 100, "Beleuchtung Klassenzimmer ca. 15-jährig")
			element("P10", 6, 100, "Lüftung im Saal DG")
			element("P11", 25, 100, "Üblicher Innenausbau mit Linolböden, Treppenhaus Naturstein")
			element("P11", 0, 0)
			element("P12", 0, 0)
		}
	}
	building("Janus Schulhaus Zentrum", "Schulstrasse 5", "8253", "Diessenhofen") {
		buildingNr = "02.02.2"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 2018
		volume = 11967
		insuredValue = 8984
		insuredValueYear = 2018
		rating(2018, "C0", "N") {
			element("P1", 31, 100, "Stahlbetondecken, Mauerwerk")
			element("P2", 0, 0)
			element("P3", 5, 100)
			element("P4", 4, 100)
			element("P5", 11, 100, "Holzfenster, Eingangstüren Stahl")
			element("P6", 1, 100, "Fernwärme vom Nachbargebäude (altes SH, san.)")
			element("P7", 3, 100)
			element("P8", 3, 100)
			element("P9", 11, 100)
			element("P10", 6, 100)
			element("P11", 25, 100)
			element("P11", 0, 0)
			element("P12", 0, 0)
		}
	}
}

/** Buildings for account 8266 - Steckborn */
fun AccountContext.buildings8266() {
	building("Oberstufenschulhaus Feldbach", "Feldbach", "8266", "Steckborn") {
		buildingNr = "04.01.1"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 2004
		volume = 18099
		insuredValue = 9298
		insuredValueYear = 2005
		description =
			"Bauherrschaft: Oberstufenschulgemeinde\nGV-Wert und Volumen inkl. Velounterstand\nAula (200 Plätze)\nAufzug mit Innentür gem. akt. Vorschriften"
		rating(2011, "C9", "N") {
			element("P48", 41, 100, "Stahlbeton, Stahlstützen verkleidet und gestrichen")
			element("P49", 0, 0)
			element("P2", 0, 0)
			element("P3", 8, 90, "nicht begrünt, z.T. Terrassen und Sonnenkollektoren")
			element("P4", 7, 85, "Aussenwärmedämmung, verputzt")
			element("P5", 10, 90, "Holzfenster mit zus. Glas auf Rahmen")
			element("P50", 6, 100, "Zeitgem. Beleuchtung mit genügender Ausleuchtung, dimmbar")
			element("P51", 1, 100, "Lautsprecheranlage, Beamer in KZ")
			element("P6", 1, 90, "Holzschnitzelfeuerungsanlage, 2 Öfen und WRG")
			element("P7", 3, 90, "Radiatoren mit Thermostaten")
			element("P8", 4, 90, "Fäkalienpumpe, da ARA höher gelegen")
			element(
				"P57",
				11,
				90,
				"Einbauschränke KH-beschichtet, innenliegende Oblichter bei KZ",
			)
			element("P58", 8, 90, "Holzzementböden, Wände Sichtbeton")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Hub gelb", "Frauenfelderstrasse 10", "8266", "Steckborn") {
		buildingNr = "04.02"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1954
		volume = 3558
		insuredValue = 2180
		insuredValueYear = 2009
		rating(2011, "C9", "N") {
			element("P48", 41, 90)
			element("P49", 0, 0)
			element("P2", 8, 100, "Ziegeldach neu (2011)")
			element("P3", 0, 0)
			element("P4", 7, 90, "Aussenwärmedämmung, verputzt")
			element("P5", 10, 80, "Alufenster iim UG (1988), Klapprollläden")
			element("P50", 6, 90, "Beleuchtung neu (2005)")
			element("P51", 1, 90, "EDV: Server (2005)\nTelefonanlage i.O.")
			element("P6", 1, 90, "Gastherme (2005)")
			element("P7", 3, 90, "Radiatoren mit Thermostaten")
			element("P8", 4, 90, "Schulküche")
			element("P57", 11, 90, "Einbauschränke i.O.")
			element(
				"P58",
				8,
				90,
				"Böden: Linoleum, Keramik, Kunststeintreppen und -gänge\nWände: Keramik und Putz\nDecken: Akustikplatten in KZ und Gänge",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Seeschulhaus", "Seestrasse 126", "8266", "Steckborn") {
		buildingNr = "04.03"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1863
		volume = 4193
		insuredValue = 2279
		insuredValueYear = 2009
		description =
			"Ein Baubeschrieb ist nicht vorhanden.\n1998 und 2000 erfolgte ein Sanierung innen und aussen."
		rating(2011, "C9", "N") {
			element(
				"P48",
				41,
				80,
				"Vermutlich massives (Naturstein-?)Mauerwerk, unisoliert, im UG und OG mit Rissen,  (Hourdis-?) und/oder Holzbalkendecken, Holztreppen",
			)
			element("P49", 0, 0)
			element(
				"P2",
				8,
				85,
				"Biberschwanzziegel, Unterdach wurde kürzlich erneuert. Spuren eines Wassereintritts sichtbar.",
			)
			element("P3", 0, 0)
			element("P4", 7, 90, "s. Rohbau; Natursteingewände, keine Mängel feststellbar")
			element("P5", 10, 90, "Holzfenster IV (Glas: Silverstar N)")
			element("P50", 6, 90, "Elektroleitungen im UG tw. neu")
			element("P51", 1, 90)
			element("P6", 1, 85, "Hoval Ultra Gas '04")
			element("P7", 3, 90)
			element(
				"P8",
				4,
				85,
				"Die Leitungen sind gem. LSV intakt, der Boiler ist älter, aber funktionstüchtig, WC-Anlagen neu",
			)
			element("P57", 11, 85, "Einbaukästen aus Holz")
			element("P58", 8, 90, "Böden: Linol\nWände: verputzt")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Sport- und Mehrzweckhalle", "Feldbach", "8266", "Steckborn") {
		buildingNr = "04.04"
		buildingType = "T12"
		buildingSubType = "ST01-9"
		buildingYear = 1981
		volume = 21371
		insuredValue = 7694
		insuredValueYear = 2009
		rating(2011, "C9", "N") {
			element("P48", 41, 90, "Stahlbeton")
			element("P49", 0, 0)
			element("P2", 5, 100, "Neues Blechdach")
			element("P3", 3, 100, "Kiesklebedach; saniert -> neu Schrägdach")
			element("P4", 7, 80, "Isolierte Waschbetonelemente")
			element("P5", 10, 70, "Metallfenster, Gläser tw. blind")
			element("P50", 6, 80, "Licht Halle saniert (2000)")
			element("P51", 1, 80, "ältere Lautsprecheranlage")
			element(
				"P6",
				1,
				90,
				"Fernwärme '06 via Holzschnitzelfeuerungs- anlage SH mit WRG, Garderobe und Gymnastikhalle",
			)
			element(
				"P7",
				3,
				80,
				"Radiatoren / Lüftungsheiz. m. Steuerung über Aussenfühler, keine Thermaostaten",
			)
			element("P8", 4, 80, "WW mit Elektroboiler und Fernwärme")
			element("P57", 11, 80, "Trennwände Halle neuwertig, tw. Glas-/Metall-Innenwände")
			element(
				"P58",
				8,
				80,
				"Böden: Waschbeton, Wände: Sichtbeton und -mauerwerk, Täfer, Putz, Hallenboden neuw.",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Hub rot", "Frauenfelderstrasse 8", "8266", "Steckborn") {
		buildingNr = "04.05.1"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1953
		volume = 3015
		insuredValue = 1718
		insuredValueYear = 2006
		description =
			"inkl. Pausenhalle; Bj. 1962, Vers.wert: 2'705'000.-\nSanierung wie Hub gelb\nAula: Heizung ohne Thermostaten, tw. keine Storen (aussen) nur Innenverdunklung, Lüftung vorh."
		rating(2011, "C9", "N") {
			element("P48", 41, 90)
			element("P49", 0, 0)
			element(
				"P2",
				8,
				100,
				"Ziegeldach neu, einzelne undichte Stellen noch vorhanden, Dachraum Boden isoliert",
			)
			element("P3", 0, 0)
			element("P4", 7, 90, "Aussenwäremdämmung verputzt (1998)")
			element(
				"P5",
				10,
				80,
				"Alufenster im UG (Glas: Interpane '88) mit Rafflamellenstoren",
			)
			element("P50", 6, 90, "Beleuchtung neu (2005)")
			element("P51", 1, 90, "EDV: Server neu (2005), Telefonanlage i.O")
			element("P6", 0, 90, "Heizung an Hub gelb angeschlossen")
			element("P7", 4, 90, "Radiatoren mit Thermostaten")
			element("P8", 4, 90, "z.T. neuere Armaturen")
			element("P57", 11, 80, "Geländerhöhen überprüfen!")
			element(
				"P58",
				8,
				90,
				"Böden: Linoleum, Kunststein, Keramik\nWände: verputzt\nDecken: Putz und Akustikplatten",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Doppelkindergarten", "Zelgistrasse 8", "8266", "Steckborn") {
		buildingNr = "04.06"
		buildingType = "T02"
		buildingSubType = "ST01-4"
		buildingYear = 1996
		volume = 2601
		insuredValue = 1464
		insuredValueYear = 2010
		rating(2011, "C8", "N") {
			element("P48", 24, 90, "Stahlbetonbau mit Stahlstützen")
			element("P49", 0, 0)
			element("P2", 20, 85, "Eternitschindeldach, Stirrnbretter tw. schadhaft")
			element("P3", 0, 0)
			element(
				"P4",
				8,
				85,
				"Hinterlüftete Eternitschindeln (tw. schadhaft) und Aussenwärmedämmung",
			)
			element("P5", 14, 90, "Wärmedämmglas 2-fach, Holzfenster '95")
			element("P9", 4, 90, "Keine Mängel bekannt")
			element("P6", 3, 90, "Gasheizung '95\nWarmwasser wird mit Gas erwärmt")
			element("P7", 3, 90, "Radiatoren mit Thermostaten, tw. mit Elektrosteuerung")
			element("P54", 1, 90, "neuwertige Armaturen und Apparate.")
			element("P55", 3, 90, "Leitungen i.O, Sickerleitungen neu")
			element("P57", 17, 90, "Einbauten Holz, Küche")
			element(
				"P58",
				3,
				90,
				"Wände verputzt o. Glasfaser-Tapeten\nBöden: Keramik und Holz\nDecken; Holztäfer oder verputzt",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Hub blau", "Hubstrasse", "8266", "Steckborn") {
		buildingNr = "04.07"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 2011
		volume = 3928
		insuredValue = 1500
		insuredValueYear = 2011
		description = "Kellerraum (Musikzimmer) feucht!"
		rating(2011, "C9", "N") {
			element("P48", 41, 100)
			element("P49", 0, 0)
			element("P2", 8, 100)
			element("P3", 0, 100)
			element("P4", 7, 100)
			element("P5", 10, 100, "3-fach WD-Glas")
			element("P50", 6, 100)
			element("P51", 1, 100, "Brüstungskanäle")
			element("P6", 1, 100, "Erdwärmepumpe inkl. WW")
			element("P7", 3, 100)
			element("P8", 4, 100)
			element("P57", 11, 100)
			element("P58", 8, 100)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
}

/** Buildings for account 8476 - Unterstammheim */
fun AccountContext.buildings8476() {
	building("Wohnhaus mit Schopf", "Unterdorf 10", "8476", "Unterstammheim") {
		buildingNr = "13.05"
		buildingType = "T01"
		buildingSubType = "ST05-26"
		buildingYear = 1951
		volume = 1381
		insuredValue = 970
		insuredValueYear = 2011
		description = "- auf Balkon rostige Abschlussbleche auf Brüstungen\n-z. T. Naturkeller"
		rating(2015, "C6", "N") {
			element("P48", 33, 90, "Mauerwerk, verputzt, Dachstuhl intakt")
			element("P49", 0, 0)
			element("P2", 13, 90, "Falzziegeldach, ohne sichtbare Schäden")
			element("P3", 0, 0)
			element("P4", 8, 80, "Mauerwerk, ungedämmt, Garagentor, einz. Risse")
			element("P5", 13, 90, "Holz/Metall, ('02), Holz DV alt")
			element("P9", 3, 50, "alte Keramiksicherungen kein Fl Schalter im Treppenhaus")
			element("P6", 1, 90, "Fernwärme")
			element("P7", 3, 90, "Heizkörper mit Thermostaten")
			element("P54", 2, 70, "alte Apparate, Warmwasser / WT ca. 5-jährig")
			element("P55", 4, 70, "alte Leitungen, tw. neu")
			element(
				"P57",
				10,
				80,
				"Holztüren, Einbauschränke, Fenstersimse aus Holz, Vorhangbretter, Holztreppen",
			)
			element(
				"P58",
				6,
				85,
				"B: Laminat, Teppich, Parkett, PVC W: Verputz, Keramik, D: Verputz, Holz",
			)
			element("P59", 4, 20, "alte Geräte, Einbauten aus Holz")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Turnhalle", "Bahnhofstrasse 7", "8476", "Unterstammheim") {
		buildingNr = "13.07"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1954
		volume = 9813
		insuredValue = 1954
		insuredValueYear = 2003
		description =
			"- die Duschanlagen sind im Jahr 2015 repariert worden, jedoch ist unter den Plattenbelägen hohe Feuchtigkeit vorhanden. Gemäss Angaben des Fachmannes sind sie noch max. 2 Jahre für den Betrieb geeignet.\n- gesamthaft wurden CHF 300`000 investiert für neuen Boden TH, Geräteraumtore, Fenster, Belichtung etc."
		rating(2015, "C33", "N") {
			element("P48", 38, 85, "Stahlbeton, Mauerwerk")
			element(
				"P49",
				0,
				90,
				"neue Dämmung (Zellulose) beim Dach gemacht, übrige Dachkonstruktion (Holz) in sehr gutem Zustand gem. Herrn B. Frei",
			)
			element("P2", 13, 80, "Falzziegel, das Steildach konnte nicht besichtigt werden")
			element("P3", 0, 0)
			element("P4", 6, 65, "Verputz, Schäden und Verfärbungen ersichtlich, Sockel i.O.")
			element(
				"P5",
				8,
				90,
				"Fenster wurden ersetzt, neu inkl. Sonnenschutz (Stoffstoren, über Funk gesteuert), Tore zu Geräteräumen wurden ersetzt",
			)
			element("P9", 5, 85, "Beleuchtung tw. neu (LED Lampen), Audio neu (LS)")
			element("P6", 1, 90, "Fernwärme")
			element("P7", 3, 90, "Radiatoren wurden ersetzt")
			element("P52", 0, 0)
			element("P53", 0, 0)
			element(
				"P54",
				2,
				50,
				"Du/WC: Apparate und Armaturen wurden im Jahr 2015 repariert, sind jedoch ohne Mischventile, Wasserschaden vorhanden: Sanierung in Planung",
			)
			element("P55", 3, 70, "Alte Leitungen vorhanden")
			element("P57", 10, 80, "Garderoben, Gerätehalterungen etc.")
			element(
				"P58",
				11,
				85,
				"Musikraum (Keller): B: Parkett W: Verputz, Turnhalle: B: PU Bodenbelag (neu), darunter Gussasphalt und Schaumdämmung (auch neu)",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Schulhausanlage", "Bahnhofstrasse 7", "8476", "Unterstammheim") {
		buildingNr = "13.08"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1894
		volume = 4146
		insuredValue = 2718
		insuredValueYear = 2003
		description =
			"-Sockelbereich ok\n-Unterstand II: Fassade aus Sichtmauerwerk\n-Aussentüre auf Paniktüre umgerüstet\n-Treppengeländer sehr tief (80cm)\n-im UG vermutlich Asbest-Platten\n-Pausenhalle neu gestrichen für CHF 5`000.-\n\n- Begehung mit Herrn B. Frei (Hauswart)"
		rating(2015, "C9", "N") {
			element(
				"P48",
				41,
				85,
				"Mauerwerk, ohne Schäden, Dachraum trocken mit Unterdach aus Pavatex, Hourdisdecke über dem Keller, Stahlunterzüge über EG (Brandschutz?), Feuchte im Keller, Gerätehaus: i.O., Dachstuhl",
			)
			element("P49", 0, 0)
			element(
				"P2",
				8,
				90,
				"Sparrendach mit Falzziegeleindeckung, Gerätehaus: Unterdach i.O., Falzziegel",
			)
			element("P3", 0, 0)
			element(
				"P4",
				7,
				80,
				"Mauerwerk, verputzt mit Kunststeingewänden und umlaufenden Kst.st.friesen, Sanierung ca. '07/'08, Gerätehaus: Mauerwerk, verputzt",
			)
			element(
				"P5",
				10,
				80,
				"Holz-/Metallfenster ('96), Stoffrollos, tw. Metallrollo (im OG), Aussentür Nord ('15), Gerätehaus: Holztore",
			)
			element(
				"P50",
				6,
				50,
				"Aufputzleitungen, Beleuchtung im DG alt, im OG: 2010, z.T. alte Sicherungen",
			)
			element(
				"P51",
				1,
				90,
				"Erneuerung Leitungen EDV auf neusten Stand (2016), Türklingel",
			)
			element("P6", 1, 85, "Fernwärme, alter Öltank vorhanden")
			element(
				"P7",
				3,
				80,
				"Radiatoren mit Thermostaten, Umwälzpumpen & Magnetventile wurden umgerüstet",
			)
			element(
				"P8",
				4,
				80,
				"tw. neue Leitungen, Abwasser-/Wasserleitungen alt, Gerätehaus: Wasseranschluss",
			)
			element(
				"P57",
				11,
				75,
				"Holzverkleidung an Wänden im OG, Holzrahmentüren, Metall- /Glastür ('10), Einbaukästen aus Holz im Naturkunde Zimmer, Garderobe im EG, Geländerhöhe: 80cm",
			)
			element(
				"P58",
				8,
				70,
				"Schulzimmer EG: B: Parkett, Kunststein im UG W: Glasfasertapete, Verputz, Pavatex, OG: B: Parkett, Linol W: Holz, Verputz D: Verputz, DG: B: Linol W: Verputz D: Verputz, Gerätehaus: Betonboden",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Schulhausanlage", "Bahnhofstrasse 8", "8476", "Unterstammheim") {
		buildingNr = "13.06"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1969
		volume = 13710
		insuredValue = 7728
		insuredValueYear = 2003
		description =
			"- Baujahr Anbau: ('03)\n- Anbau Süd (KZ) mit TH ('02) und Bibliothek\n- Velounterstand: Sichtbeton, Metalldach (wurde erneuert da altes Glasdach defekt)\n- überdachter Pausenbereich: soll bei Sanierung neu gemacht werden, Gläser tw. zersprungen und undicht, Gläser können nicht gut gereinigt werden, im Sommer staut sich die Hitze unter dem Dach\n- Schulküchen im '08 saniert, Boden aus Kautschuk mit Antirutschbeschichtung, muss neu gemacht werden\n- Geländerhöhe 90cm\n- Staketenabstand: 14.5 cm, zu breit!\n\nGerätehaus: Flachdach, Kiesklebdach, Untersichten aus Holz, Elektro: Licht, Steckdosen; in gutem Zustand\n\n- Begehung mit Herrn B.Frei (Hauswart)"
		rating(2015, "C9", "N") {
			element("P48", 40, 85, "Stahlbeton, Mauerwerk, Gerätehaus: Stahlbeton")
			element("P49", 0, 0)
			element(
				"P2",
				8,
				85,
				"Sparrendach mit Falzziegel und Glasdächer, Untersichten aus Holz, Unterdach mit Holzschindeln alt, Dachraum alt: Wasserfleck ist sichtbar am Boden, gemäss Hauswart Wassereintritt da Feuchteschaden über Akustikdecken im Schulhaus, wird gem. HW repariert",
			)
			element("P3", 0, 0)
			element(
				"P4",
				7,
				80,
				"Eternit/Holz (Stulpschalung), Betonsockel, Naturstein, Gerätehaus: Eternit und Sichtbeton",
			)
			element(
				"P5",
				10,
				50,
				"Holz-/Metallfenster ('02) und Holzfenster (alt) sind schlecht isoliert, innere Verdunkelungen alt und defekt, Rafflamellenstoren, Metalltür in der Bibliothek, Gerätehaus: Kunsttoffenster, Tor beschädigt",
			)
			element("P50", 6, 70, "Beleuchtung ('02, tw. '05)")
			element("P51", 1, 90, "EDV Leitungen erneuert, Gong, Sonnerie")
			element("P6", 1, 90, "Fernwärme")
			element(
				"P7",
				3,
				60,
				"Radiatoren mit Thermostaten, Steuerung und Pumpen müssen ersetzt werden",
			)
			element(
				"P8",
				4,
				65,
				"Die Sanitäranlagen im Schulhaus ('69) sind veraltet, die vier Aussenanschlüsse i.O.",
			)
			element(
				"P57",
				11,
				70,
				"Arbeitsflächen aus Holz, Wandschränke, Gerätehaus: Gestelle, WC-Trennwände: Türen klemmen tw.",
			)
			element(
				"P58",
				8,
				75,
				"B: Linol, Parkett, Keramik, Kunststeintreppen, Teppich W: Verputz D: Beton roh, Holz, Akustikdecken, Gerätehaus: B: Beton roh, W: Eternit, Sichtbeton D: Holz",
			)
			element("P60", 0, 0)
			element(
				"P61",
				1,
				80,
				"Teeküchen in der Bibliothek und im Lehrerzimmer, Lüftungsanlage im Singsaal ausser Betrieb",
			)
			element("P62", 0, 0)
		}
	}
	building("Sporthalle", "Bahnhofstrasse 8", "8476", "Unterstammheim") {
		buildingNr = "13.10"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 2003
		volume = 9813
		insuredValue = 2516
		insuredValueYear = 2003
		description =
			"- vor Haupteingang auf Platz wurden neu Rillen für Wasserabfluss gemacht (CHF 10`000), da vorher bei starkem Regen Wasser durch die Tür ins Innere floss\n\n- Begehung mit Herrn B. Frei (Hauswart)"
		rating(2015, "C33", "N") {
			element("P48", 34, 90, "Stahl, Holz")
			element("P49", 0, 0)
			element(
				"P2",
				11,
				90,
				"Steildach mit Falzziegeleindeckung (Dachraum konnte nicht besichtigt werden)",
			)
			element("P3", 2, 90, "Kiesklebedach zwischen Sporthalle und Schulhaus")
			element("P4", 6, 90, "Eternit, Holz, Sockel Stahlbeton")
			element(
				"P5",
				8,
				90,
				"Holz-/Metallfenster wurden für CHF 25`000 ersetzt, inkl. 2 Motoren pro Fenster, neue Storen im '16 für CHF 17`000, grosse Fluchttür in Sporthalle wurde an andere Fassade verschoben, da Wasser eintrat",
			)
			element("P9", 5, 90, "Zeitgemässe und genügende Sicherungen mit FI-Schutzschalter")
			element("P6", 1, 90, "Fernwärme")
			element("P7", 3, 90, "Bodenheizung und Wärmelüftung, Steuerung wurde optimiert")
			element("P52", 2, 90, "Lüftungsanlage Fa. Bösch ohne Mängel")
			element(
				"P53",
				2,
				90,
				"Lüftungsanlage funktioniert gem. Angaben HW einwandfrei, Nachtauskühlung",
			)
			element("P54", 2, 90, "Armaturen und Apparate: keine Mängel bekannt")
			element("P55", 3, 90, "Leitungen i.O.")
			element(
				"P57",
				10,
				90,
				"Gerätehalterungen, Trennwand Doppelturnhalle, Innentüren einwandfrei",
			)
			element(
				"P58",
				11,
				80,
				"B: Parkett, Keramik, Zement, W: Holz (tw. erneuert), Backstein gestrichen, Verputz gestrichen, Keramik; Pinselrenovation wurde im Innenraum gemacht, D: Holz (auch in Du/Gard: ungeeignet!), Beton gestrichen, in TH neuer Boden (höhere Dämpfung)",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Schulhaus und Kindergarten", "Unterdorf 8", "8476", "Unterstammheim") {
		buildingNr = "13.01"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1847
		volume = 5910
		insuredValue = 4500
		insuredValueYear = 2018
		description =
			"- Riss im DG Altbau sollte beobachtet werden (Risssiegel)\n- Baujahre der drei Gebäudeteile: Kindergarten 1847 / Anbau 1984 / Anbau 1995 / Zwischenbau 2017\n- im Anbau wurde eine Treppe eingebaut (brandschutztechnisch war dies erforderlich), welche die beiden Kindergärten im EG und OG verbindet\n\nGV-Wert und Kubatur gemäss neuer Schätzung vom 12.04.2018"
		rating(2015, "C9", "N") {
			element(
				"P48",
				27,
				80,
				"Mauerwerk, verputzt, Betonsockel, KS-Mauerwerk, massiv mit Holzbalkendecken, im Altbau Riss im DG, Dachstuhl i.O.",
			)
			element("P49", 0, 0)
			element(
				"P2",
				20,
				85,
				"Ziegeldach, Pergola: neue Stoffstoren (gleiche wie in Turnhalle Bahnhofstr. 7), Neubau Verbindungsgang (Dez '17)",
			)
			element("P3", 0, 0)
			element("P4", 8, 85, "RIss im Altbau")
			element(
				"P5",
				14,
				80,
				"Holzfenster, im Altbau 3-fach Verglasung mit Aluläden -> Dichtungen schlecht, im Neubau IV-Verglasung 2-fach mit Rafflamellenstoren, Metalltüre 3/89, innere Verdunkelung im Kindergarten, Aussentüre aus Metall/Glas",
			)
			element("P50", 5, 90, "Unterverteilungen mit neuen Sicherungen")
			element("P51", 1, 90, "EDV, Gong, Erneuerung IT")
			element("P6", 1, 90, "Fernwärme, Wärmeaustauscher, Boiler")
			element("P7", 1, 85, "Radiatoren mit Thermostaten, tw. ferngesteuert")
			element(
				"P8",
				3,
				85,
				"Santäreapparate tw. älter aber funktionstüchtig und gepflegt, Sanitär-Leitungen: keine Mängel bekannt, WW-Leitungen isoliert, Lavabos und Wand neu gefliest ('15)",
			)
			element(
				"P57",
				17,
				75,
				"Einbauschränke, Arbeitssimse, Kleinküche, Riss im Grundputz, DG Altbau: Dämmung im Estrich/DG mangelhaft",
			)
			element(
				"P58",
				3,
				85,
				"B: Laminat, Linol, PVC, Keramik W: Verputz D: Gips, Akustikdecken im EG, Anbau ('95): B: Linol W: Verputz D: Holz",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
}

/** Buildings for account 8556 - Wigoltingen */
fun AccountContext.buildings8556() {
	building("Kindergarten Haldengüetli", "Bernrainstr. 26", "8556", "Wigoltingen") {
		buildingNr = "06.01"
		buildingType = "T02"
		buildingSubType = "ST01-4"
		buildingYear = 1938
		volume = 1487
		insuredValue = 1187000
		insuredValueYear = 2005
		description = "Hauswart: Hr. Gubler\nNiedrige Raumhöhen in Kindergartenräumen"
		rating(2012, "C9", "N") {
			element("P48", 41, 90, "Wände Mauerwerk, Decken: Holz")
			element("P49", 0, 0)
			element("P2", 8, 80, "Ziegeldach mit Lukarnen")
			element("P3", 0, 0)
			element("P4", 7, 90, "MW, verputzt")
			element("P5", 10, 85, "Holz-/Metallfenster mit IV")
			element("P50", 6, 85, "Beleuchtung tw. ungenügend")
			element("P51", 1, 90, "Tel. / EDV")
			element("P6", 1, 90, "Gasheizung")
			element("P7", 3, 80, "Bodenheiz./Radiatoren im Treppenhaus EG")
			element("P8", 4, 90, "Leitungen i.O., App. alt")
			element("P57", 11, 85, "D: zw. Balken: Gipskarton, Einbauschränke, Simse aus Holz")
			element(
				"P58",
				8,
				85,
				"Boden KG: Holzkork, Wände verputzt, gestr., Decken: verputzt, gestr.",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Mehrzweckhalle", "Käsereistrasse 10", "8556", "Wigoltingen") {
		buildingNr = "25.02"
		buildingType = "T12"
		buildingSubType = "ST01-9"
		buildingYear = 1939
		volume = 2842
		insuredValue = 1457
		insuredValueYear = 2014
		rating(2019, "C33", "N") {
			element("P48", 37, 85, "Mauerwerk, Holzträger")
			element("P49", 0, 0)
			element("P2", 13, 85, "Gedämmt, Dachraum trocken und mängelfrei")
			element("P3", 0, 0)
			element("P4", 6, 90, "Mauerwerk, verputzte Aussenwärmedämmung")
			element("P5", 8, 85, "Fenster Westfassade: Holz-Metal")
			element("P9", 5, 80, "Beleuchtung, Turnhallenbeleuchtung, Bühnentechnik")
			element("P6", 1, 90, "Fernwärme")
			element("P7", 3, 85, "Heizkörper mit Thermostaten")
			element("P52", 2, 80, "Küchenabluft")
			element("P53", 0, 0)
			element("P54", 1, 80, "Armaturen ohne Mischventile, jedoch funktionstüchtig")
			element("P55", 3, 80, "Keine Mängel bekannt")
			element("P57", 10, 80, "Küche '10, Garderobe")
			element("P58", 11, 80, "Turnhallenboden (Bretterboden, alt), Wand: Glasfasertapeten")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Primarschule Wigoltingen", "Käsereistrasse 10", "8556", "Wigoltingen") {
		buildingNr = "25.01"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1958
		volume = 4409
		insuredValue = 2894
		insuredValueYear = 2014
		description =
			"Grund für den Wassereintritt war ein defekter Ziegel.\nKindergartenraum neu (Frühling 2019), ausser Beleuchtung."
		rating(2019, "C9", "N") {
			element("P48", 41, 90, "Mauerwerk, Stahlbeton")
			element("P49", 0, 0)
			element(
				"P2",
				8,
				85,
				"Der Dachraum ist trocken und unbeschädigt, Spuren von Wassereintritt sichtbar (Mangel wurde behoben).",
			)
			element("P3", 0, 0)
			element("P4", 7, 85, "Mauerwerk verputzt, Aussenwärmedämung")
			element(
				"P5",
				10,
				80,
				"Kunststofffenster, Windfang: Metalltüren mit Glasfüllungne, Rafflamellenstoren an der Nord-, West- und Südfassade",
			)
			element("P50", 6, 80, "Beleuchtung")
			element("P51", 1, 90, "Pausenglocke, EDV-Anlage")
			element("P6", 1, 90, "Fernwärme")
			element("P7", 3, 85, "Radiatoren mit Thermostaten")
			element("P8", 4, 80, "WC neu '09. Leitungen tw. rostig, Druck ist in Ordnung")
			element(
				"P57",
				11,
				80,
				"Garderoben, Einbauschränke, kleine Küche im KG-Raum\nGeländer (Höhe und Abstände nicht gesetzeskonform)",
			)
			element(
				"P58",
				8,
				80,
				"Boden: Kunststein, Linol, PVC. Garagenboden: Beton, gestrichen\nWand: verputzt, Glasfasertapete\nDecke: Holztäferdecke, Akustik-, Gipsdecke",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Turnhalle (Sarnahalle)", "Käsereistrasse 10", "8556", "Wigoltingen") {
		buildingNr = "25.03"
		buildingType = "T12"
		buildingSubType = "ST01-9"
		buildingYear = 1973
		volume = 4326
		insuredValue = 2462
		insuredValueYear = 2014
		rating(2019, "C33", "N") {
			element("P48", 36, 90, "Mauerwerk, Stahlbeton")
			element("P49", 0, 0)
			element(
				"P2",
				13,
				90,
				"Eternitschindeldach: keine Mängel bekannt (Dachraum konnte nicht besichtigt werden), Isolation über Hallendecke (Isofloc)",
			)
			element("P3", 0, 0)
			element("P4", 6, 85, "Eternitplatten '10, einige Risse vorhanden")
			element(
				"P5",
				8,
				80,
				"Kunststoffenster, Rafflamellenstoren (tw. beschädigt), an der Westfassade: Metallfenster (Gläser wurden ersetzt), Aussentüren aus Holz, tw. mit Glasfüllungen",
			)
			element("P9", 5, 90, "Beleuchtung '10 in Ordnung, Turnhallenbeleuchtung")
			element("P6", 1, 90, "Fernwärme")
			element("P7", 3, 85, "Radiatoren mit Thermostaten")
			element("P52", 1, 85, "Die grosse Lüftung gehört zur Mehrzeckhalle")
			element("P53", 1, 85, "Lüftung Garderoben/Duschen")
			element("P54", 2, 90, "WC neu , Dusche, Armaturen ersetzt")
			element("P55", 3, 85, "teilweise erneuert")
			element("P57", 10, 90, "Garderoben, Treppengeländer (h=92cm), Einbauschränke")
			element(
				"P58",
				11,
				80,
				"Boden: alt (noch original), mangelhaft, Klinker\nDecke: Holz, Heraklith im Werkraum, Hartbeton, gestrichen",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Werkzentrum", "Bahnhofstrasse 40", "8556", "Wigoltingen") {
		buildingNr = "25.04"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1973
		volume = 7617
		insuredValue = 4901
		insuredValueYear = 2014
		description =
			"Die Gebäudenhülle wurde ca '08 renoviert.\nDie Fernwärme für die Primarschule, Mehrzweckhalle und Turnhalle werden von diesem Gebäude (Werkzentrum) aus gesteuert."
		rating(2019, "C9", "N") {
			element("P48", 39, 90, "Stahlbeton, Mauerwerk, Kalksandstein")
			element("P49", 0, 0)
			element("P2", 8, 90, "Isoliertes Ziegeldach, Welleternit")
			element("P3", 0, 0)
			element("P4", 7, 85, "Eternitplatten, Sichtbeton")
			element("P5", 10, 80, "Holzfenster (nur Gläser ausgetauscht), Rafflamellenstoren")
			element("P50", 6, 85, "Beleuchtung")
			element("P51", 1, 90, "FI-Schutzschalter neu '19")
			element("P6", 3, 90, "Fernwärme, Gasheizung als Ergänzung (Übergangszeiten)")
			element("P7", 3, 85, "Heizkörper mit Thermostaten")
			element("P8", 4, 85, "Druck und Leitungen in Ordnung")
			element(
				"P57",
				11,
				85,
				"Innere Verglasungen, Trennwände, kleine Teeküche im Singsaal, Garderobe",
			)
			element(
				"P58",
				8,
				80,
				"Boden: Klinkersteine, PVC, Parkett, Linol, in der Teeküche: Hartbeton\nWand: Putz, Täfer\nDecke: Holztäfer",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Sekundarschulgebäude", "Kirchstrasse 12a", "8556", "Wigoltingen") {
		buildingNr = "25.05"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1995
		volume = 18276
		insuredValue = 11895
		insuredValueYear = 2014
		description =
			"-Problem der Feuchtigkeit im Chemieraum wurde behoben.\n-Keine Lüftung im Singsaal\ns.a. Bestandesaufnahmen vom März 2018"
		rating(2020, "C9", "N") {
			element("P48", 40, 85, "Stahlbeton, Mauerwerk")
			element("P49", 0, 0)
			element(
				"P2",
				4,
				80,
				"Schmetterlingsdach mit Fachwerk aus Holz und Eindeckung aus Blech, Ablauf teilweise defekt, Wassereintritt",
			)
			element("P3", 4, 85, "Kiesklebedach, neues Glas Unterdach bei der Einganstür")
			element("P4", 7, 80, "Mauerwerk verputzt, teilweise vermoost")
			element(
				"P5",
				10,
				80,
				"Eingangstür aus Metal und Glas, manuelle Rollläden, Mittagsraum: Fenster defekt",
			)
			element(
				"P50",
				6,
				85,
				"Genügende Absicherungen, Teilweise alte Beleuchtung, Notlichtanlage",
			)
			element("P51", 1, 85, "Steuerung Notlichtanlage, Server")
			element(
				"P6",
				1,
				90,
				"Fernwärme von Holzschnitzelheizung (bei Turnhalle/Werkzentrum), tw. Gasheizung Werkzentrum. Eigentumsverhältnisse unklar (Pol. Gemeinde oder Schulgemeinde?)",
			)
			element("P7", 3, 85, "Radiatoren mit Thermostaten")
			element(
				"P8",
				4,
				85,
				"Waschmaschine, Chromstahlleitungen, Entkalkungsanlage, teilweise wenig Druck beim WC",
			)
			element(
				"P57",
				11,
				85,
				"Garderobe, Innere Verglasung, Treppengeländer aus Metall, Einbauschränke, Küche beim Mittagstisch, Schulküche, Küchengeräte wurden ersetzt",
			)
			element(
				"P58",
				8,
				80,
				"Boden: Teppich im Eingang, Linol, Parkett (tw. beschädigt aufgrund Wassereintritts), \nWand: Holz, Beton, Putz, KS und Beton im UG\nDecke: Beton, Täfer",
			)
			element("P60", 0, 0)
			element("P61", 1, 90, "Personenaufzug")
			element("P62", 0, 0)
		}
	}
	building("Primarschule Raperswilen, Altbau inkl. Whg", "Schulstrasse 7", "8556", "Raperswilen") {
		buildingNr = "25.14"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1899
		volume = 2834
		insuredValue = 1839
		insuredValueYear = 2011
		rating(2020, "C9", "N") {
			element("P48", 41, 85, "Mauerwerk mit Holzbalkendecken")
			element("P49", 0, 0)
			element("P2", 8, 90, "Falzziegel, leicht vermoost, Dachraum Isoliert")
			element("P3", 0, 0)
			element("P4", 7, 85, "Mauerwerk verputzt, Sockel und Gewände aus Naturstein")
			element(
				"P5",
				10,
				85,
				"Holzfenster April '89, Gitter vor Glasfüllungen (Eingangstür), Aluläden",
			)
			element("P50", 6, 90, "EDV, FI-Schutzschalter vorh.")
			element("P51", 1, 90)
			element("P6", 1, 80, "Holzpelletheizung, Boiler")
			element("P7", 3, 85, "Radiatoren mit Thermostaten")
			element("P8", 4, 85, "Genügender Druck vorhanden")
			element(
				"P57",
				11,
				85,
				"Einbauschränke, Fenstersims aus Holz (Arbeitssims), Teeküche, Garderoben, Wohnungsküche mit Geschirrspüler, Holzrahmentüren",
			)
			element(
				"P58",
				8,
				80,
				"Boden: Klinker, Parkett, Naturstein, Keramik\nDecke: Holztäfer, Holzbalken (tw. beschädigt)\nWand: Putz, Glasfasertapeten, Rauhfasertapeten, Keramik",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Turnhallengebäude Sekundarschule", "Kirchstrasse 12a", "8556", "Wigoltingen") {
		buildingNr = "25.06"
		buildingType = "T12"
		buildingSubType = "ST01-9"
		buildingYear = 1996
		volume = 4933
		insuredValue = 1874
		insuredValueYear = 2014
		description = "-zwei Abläufe (ein Ablauf musste ersetzt werden beim Flachdach Bereich)"
		rating(2020, "C9", "N") {
			element("P48", 38, 85, "Stahlbeton, Mauerwerk")
			element("P49", 0, 0)
			element(
				"P2",
				0,
				85,
				"Blechdach, Verdacht auf Feuchtigkeit bei den Holzfachwerkträgern",
			)
			element("P3", 8, 80, "Kiesklebedach")
			element("P4", 7, 80, "Mauerwerk verputzt, teilweise vermoost")
			element(
				"P5",
				10,
				80,
				"Aussentür aus Metall und IV-Glas, Holz-/Metallfenster, Milchglasfenster, Dichtung der Fenster mangelhaft, Kondensation bei den Fenstern",
			)
			element("P50", 6, 85, "Genügende Absicherung, Beleuchtung neu")
			element("P51", 1, 85, "Audioanlage")
			element("P6", 1, 90, "Fernwärme von Holzschnitzelheizung")
			element("P7", 3, 85, "Radiatoren mit Thermostaten")
			element("P8", 4, 85, "Duschen, WC, Chromstahlleitungen")
			element(
				"P57",
				11,
				85,
				"Garderoben, Geländer aus Metall, innere Verglasungen, Einbauschränke, Schiebetür im Geräteraum",
			)
			element(
				"P58",
				8,
				85,
				"Decke: Holztäfer\nBoden: veralteter Turnhallenboden wurde tw. geflickt,\nWand: KS-Wände im Geräteraum, Beton in der Turnhalle, mangelhafte Fugen in der Frauengarderobe, Schimmelpilzbildung bei Duschen",
			)
			element("P60", 0, 0)
			element(
				"P61",
				3,
				90,
				"Turnhallen Lüftung, Lüftung musste ca. vor 1 Jahr ersetzt werden",
			)
			element("P62", 0, 0)
		}
	}
	building("Wohnhaus & Garage", "Kirchstrasse 10", "8556", "Wigoltingen") {
		buildingNr = "25.07"
		buildingType = "T01"
		buildingSubType = "ST02-11"
		volume = 697
		insuredValue = 473
		insuredValueYear = 2014
		description = "- Garage konnte nicht besichtigt werden"
		rating(2020, "C7", "N") {
			element("P48", 37, 80)
			element("P49", 0, 0)
			element("P2", 8, 85, "Falzziegeldach, hinterlüftet, tw. isoliert")
			element("P3", 0, 0)
			element("P4", 7, 85, "Mauerwerk, verputzt")
			element(
				"P5",
				11,
				70,
				"Eingangstür aus Holz, Holzläden, IV-Verglasung '87, tw. Metallgitter vor den Fenstern",
			)
			element("P9", 4, 85, "Keine Mängel bekannt")
			element("P6", 2, 90, "Gasheizung '17 und Cheminéeeofen, Secomat")
			element("P7", 3, 85, "Radiatoren mit Thermostaten")
			element("P54", 3, 80, "WC, Duschen, Waschmaschine")
			element("P55", 4, 80, "tw. wenig Druck vorhanden")
			element("P56", 0, 0)
			element("P57", 9, 80, "Einbauschränke, Holzrahmentüren")
			element(
				"P58",
				8,
				80,
				"Boden: Klinken, Parkett, Teppich\nWand: Holz, verputzt gestrichen\nDecke: Holz",
			)
			element("P59", 4, 85, "Küche mit Glaskeramikkochfeld, Backofen und Geschirrspüler")
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Primarschule Wigoltingen, Altbau", "Käsereistrasse 12", "8556", "Wigoltingen") {
		buildingNr = "25.08"
		buildingType = "T02"
		buildingSubType = "ST02-12"
		buildingYear = 1870
		volume = 2737
		insuredValue = 2289
		insuredValueYear = 2014
		description =
			"- Radon verdacht im Keller, Messungen wurden von Studenten gemacht (Messungen nicht erfahren)\n- Feuchtigkeit von Unten nach Oben"
		rating(2020, "C9", "N") {
			element(
				"P48",
				41,
				80,
				"Mauerwerk und Sandsteinsockel (tw. beschädigt), Gewölbekeller, Holzbalkendecke",
			)
			element("P49", 0, 0)
			element(
				"P2",
				8,
				80,
				"Falzziegeldach ohne Unterdach, hinterlüftet, Vordach aus Holz und Ziegeleindeckung, Decke gegen Estrich: unisoliert (Winter kalt, Sommer sehr warm), Dachflächenfenster",
			)
			element("P3", 0, 0)
			element("P4", 7, 80, "Mauerwerk verputzt, einige Risse sichtbar")
			element(
				"P5",
				10,
				80,
				"Holzfenster mit IV-Verglasung, Metalläden, tw. Holzläden, Holztüren, Aussentüre aus Metall",
			)
			element(
				"P50",
				6,
				80,
				"Genügende Absicherung mit FI-Schutzschaltern, Beleuchtung älteren Datums, jedoch knapp ausreichend",
			)
			element("P51", 1, 85, "Server und Verkabelung")
			element("P6", 1, 90, "Fernwärme")
			element("P7", 3, 85, "Radiatoren mit Thermostat")
			element(
				"P8",
				4,
				80,
				"Sanitärleitungen sind leicht rostig, genügender Druck vorhanden",
			)
			element(
				"P57",
				11,
				80,
				"Garderoben. Holzgeländer, Einbauschränke aus Holz, Küche neu 2019",
			)
			element(
				"P58",
				8,
				85,
				"Boden: Keramik, Linol\nWand: verputzt, Tapeten, gestrichen, einge Risse sichtbar (OG)\nDecke: Gips, gestrichen",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}
	building("Kindergarten Haldengüetli, m Gge", "Bernrainstrasse 26", "8556", "Wigoltingen") {
		buildingNr = "25.09"
		buildingType = "T02"
		buildingSubType = "ST01-4"
		buildingYear = 1938
		volume = 1500
		insuredValue = 1411
		insuredValueYear = 2014
		description =
			"-Feuchtigkeitsproblem bei dem Raum unter der Treppe\n-Fluchtweg nicht gewehrleistet\n-Keine Lüftung"
		rating(2020, "C9", "N") {
			element("P48", 33, 90, "Mauerwerk, Stahlbeton")
			element("P49", 8, 85, "Holzbalkendecken mit Stützen und Unterzug")
			element("P2", 8, 90, "Bieberschwanzziegel, tw. vermoost, Dachraum isoliert")
			element("P3", 0, 0)
			element(
				"P4",
				7,
				85,
				"Mauerwerk, verputzt, im OG und DG Holzschalung, Holzfensterbänke verwittert",
			)
			element(
				"P5",
				10,
				80,
				"Eingangstür  aus Holz, Fenster aus Holz und Metall, IV-Gläser ohne Beschichtung, Türschloss nicht mehr zeitgemäss",
			)
			element("P50", 6, 85, "Genügende Absicherung")
			element("P51", 1, 90)
			element("P6", 1, 90, "Gastherme, WW-Boiler")
			element("P7", 3, 90, "Radiatoren mit Thermostaten, Bodenheizungen")
			element(
				"P8",
				4,
				90,
				"Druck genügend, Wasser kann ungehindert ablaufen, Leitungen weisen keine bekannten Mängel auf",
			)
			element(
				"P57",
				11,
				80,
				"Garderoben, Küche alt, Einbauschränke, Holzgeländer, Holztreppe, Geländerhöhen nicht genügend",
			)
			element(
				"P58",
				8,
				80,
				"Boden: einge Risse sichtbar, Teppich, Fugen in den WC, Zementboden, gestrichen\nDecke: Holzbalken, Dachraum, Holz, Gipsdecke\nWand: verputzt, Abrieb, gestrichen",
			)
			element("P60", 0, 0)
			element("P61", 0, 0)
			element("P62", 0, 0)
		}
	}

}
