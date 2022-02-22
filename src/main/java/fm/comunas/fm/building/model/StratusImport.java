package fm.comunas.fm.building.model;

import static java.util.Map.entry;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class StratusImport {

	static final String BASE_DIR = "X:/Cloud/Dropbox/Daten/Hannes/comunas.FM/Stratus/Export_20210701/";

	static final String IN_SEPARATOR = "\t";
	static final String OUT_SEPARATOR = "\t";

	static final int BUILDING_NR = 0;
	static final int IDENTIFICATION = 1;
	static final int NAME = 2;
	static final int BUILDING_YEAR = 3;
	static final int STREET = 4;
	static final int ZIP = 5;
	static final int CITY = 6;
	static final int COUNTRY_ID = 7;
	static final int BUILDING_MANAGER_ID = 8;
	static final int PORTFOLIO_MANAGER_ID = 9;
	static final int CURRENCY_ID = 10;
	static final int INSURED_VALUE = 11;
	static final int INSURED_VALUE_YEAR = 12;
	static final int CORRECTION_FACTOR = 13;
	static final int NOT_INSURED_VALUE = 14;
	static final int NOT_INSURED_YEAR = 15;
	static final int THIRD_PARTY_VALUE = 16;
	static final int THIRD_PARTY_VALUE_YEAR = 17;
	static final int VOLUME = 18;
	static final int AREA = 19;
	static final int BUILDING_TYPE_ID = 20;
	static final int BUILDING_SUB_TYPE_ID = 21;
	static final int DEPARTMENT = 22;
	static final int EXTRA1 = 23;
	static final int EXTRA2 = 24;
	static final int EXTRA3 = 25;
	static final int EXTRA4 = 26;
	static final int MAINTENANCE_STRATEGY_ID = 27;
	static final int DO_CALC = 28;
	static final int DO_NOT_CALC_FROM_YEAR = 29;
	static final int DO_FORCE_RESTORATION = 30;
	static final int REGISTERED_BY_ID = 31;
	static final int REGISTERED_AT = 32;
	static final int MODIFIED_BY = 33;
	static final int MODIFIED_AT = 34;
	static final int DESCRIPTION = 35;
	static final int PART_CATALOG_ID = 36;
	static final int PART_COUNT = 37;

	//@formatter:off
	static final int PART_ID = 0;
	static final int PART_DESCRIPTION = 2;
	static final int PART_CONDITION_YEAR = 3;
	static final int PART_STRAIN = 5;
	static final int PART_STRENGTH = 6;
	static final int PART_CONDITION = 7;
	static final int PART_VALUE = 8;

	static final int[] BUILDING_EXPORT = {
		ZIP,    // account
		NAME,
		BUILDING_TYPE_ID, BUILDING_SUB_TYPE_ID,
		PART_CATALOG_ID,
		BUILDING_YEAR,
		STREET, ZIP, CITY, EXTRA1, COUNTRY_ID, // extra1 = state
		CURRENCY_ID,
		VOLUME, AREA,
		INSURED_VALUE, INSURED_VALUE_YEAR,
		MAINTENANCE_STRATEGY_ID,
		DESCRIPTION
	};

	static final int[] PART_EXPORT = {
		PART_ID,
		PART_VALUE,
		PART_CONDITION,
		PART_CONDITION_YEAR,
		//PART_STRAIN,
		//PART_STRENGTH,
		PART_DESCRIPTION
	};

	static final Map<String,String> BUILDING_TYPE_MAP = Map.ofEntries(
		entry("01 Wohngebäude","T01"),
		entry("02 Schulen","T02"),
		entry("03 Industriebauten","T03"),
		entry("04 Landw. Gebäude","T04"),
		entry("05 Techn. Betriebe","T05"),
		entry("06 Handel und Verwaltung","T06"),
		entry("09 Kultus","T09"),
		entry("10 Kultur und Geselligkeit","T10"),
		entry("11 Gastgewerbe","T11"),
		entry("12 Freizeit, Sport, Erholung","T12"),
		entry("13 Verkehrsanlagen","T13"),
		entry("14 Militär- und Schutzanlagen","T14")
	);

	static final Map<String, String> BUILDING_SUB_TYPE_MAP = Map.ofEntries(
		entry("01 Behelfswohnungen","ST01-1"),
		entry("01 Eingeschossige Garagen","ST01-2"),
		entry("01 Heizzentr., Fernwärmeanl.","ST01-3"),
		entry("01 Kinderhorte, Kindergärten","ST01-4"),
		entry("01 Kirchen, Kapellen","ST01-5"),
		entry("01 Lagerhallen","ST01-6"),
		entry("01 Restaurationsbetriebe","ST01-7"),
		entry("01 Schuppen, Hütten","ST01-8"),
		entry("01 Turn- und Sporthallen","ST01-9"),
		entry("02 Ladenbauten m. Grundausst.","ST02-10"),
		entry("02 Mehrfamilienhäuser","ST02-11"),
		entry("02 Primar- & Sekundarschulen","ST02-12"),
		entry("02 Stadien, Sportplätze","ST02-13"),
		entry("02 Tiefgaragen","ST02-14"),
		entry("02 Wasseraufber. Kläranlagen","ST02-15"),
		entry("03 Berufs-/Höhere Fachsch.","ST03-16"),
		entry("03 Einfache Bürobauten","ST03-17"),
		entry("03 Friedhofanlagen","ST03-18"),
		entry("03 Öfftl. Zivilschutzanlagen","ST03-19"),
		entry("03 Ortsmuseen, Kunstgalerien","ST03-20"),
		entry("03 Stallungen. landw. Anl.","ST03-21"),
		entry("03 Tribünen, Garderoben","ST03-22"),
		entry("04 Bürobauten mit erh. Anford.","ST04-23"),
		entry("04 Tankanlagen","ST04-24"),
		entry("04 Wochenendhäuser","ST04-25"),
		entry("05 Einfamilienhäuser","ST05-26"),
		entry("05 Heilpädagogik","ST05-27"),
		entry("05 Klöster","ST05-28"),
		entry("05 Kunsteisbahnen, Freibäder","ST05-29"),
		entry("05 Verwaltungsgeb., Banken","ST05-30"),
		entry("05 Werkhöfe","ST05-31"),
		entry("05 Wohlfahrts- & Klubhäuser","ST05-32"),
		entry("06 Alterswohnungen","ST06-33"),
		entry("06 Autobahnzollanlagen","ST06-34"),
		entry("06 Gemeindehäuser","ST06-35"),
		entry("06 Kleintheater","ST06-36"),
		entry("07 Feuerwehrgebäude","ST07-37"),
		entry("07 Hallenbäder","ST07-38"),
		entry("07 Konzert- & Theaterbauten","ST07-39"),
		entry("07 Rathäuser","ST07-40"),
		entry("07 Tankstellen, Wartehallen","ST07-41"),
		entry("08 Kinder- & Jugendheime","ST08-42"),
		entry("08 Musikpavillon","ST08-43"),
		entry("09 Campinganlagen","ST09-44"),
		entry("10 Schiessanlagen","ST10-45"),
		entry("10 Seilbahnstationen","ST10-46"),
		entry("11 Festhallen","ST11-47"),
		entry("11 Freizeitzentren","ST11-48"),
		entry("12 Hafenanlagen","ST12-49")
	);

	static final Map<String,String> MAINTENANCE_STRATEGY_MAP = Map.ofEntries(
		entry("Mininal","M"),
		entry("Normal","N"),
		entry("Normal Wohlen","NW")
	);

	static final Map<String,String> COUNTRY_MAP = Map.ofEntries(
		entry("1","ch")
	);

	static final Map<String,String> CURRENCY_MAP = Map.ofEntries(
		entry("1","chf")
	);
	//@formatter:on

	public static void main(String[] args) throws FileNotFoundException, IOException {
		List<List<String>> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(BASE_DIR + "object.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(IN_SEPARATOR);
				lines.add(Arrays.asList(values));
			}
		}
		System.out.println("Read " + (lines.size() - 1) + " buildings");
		try (FileWriter fw = new FileWriter(BASE_DIR + "comunasBuildings.csv")) {
			for (int i = 1; i < lines.size(); i++) {
				List<String> building = lines.get(i);
				System.out.print("Building [" + i + "] (" + building.size() + "): ");
				building.set(BUILDING_TYPE_ID, BUILDING_TYPE_MAP.get(building.get(BUILDING_TYPE_ID)));
				building.set(BUILDING_SUB_TYPE_ID, BUILDING_SUB_TYPE_MAP.get(building.get(BUILDING_SUB_TYPE_ID)));
				building.set(PART_CATALOG_ID, "C" + building.get(PART_CATALOG_ID));
				building.set(COUNTRY_ID, COUNTRY_MAP.get(building.get(COUNTRY_ID).toString()));
				building.set(CURRENCY_ID, CURRENCY_MAP.get(building.get(CURRENCY_ID).toString()));
				building.set(MAINTENANCE_STRATEGY_ID, MAINTENANCE_STRATEGY_MAP.get(building.get(MAINTENANCE_STRATEGY_ID)));
				building.set(DESCRIPTION, building.get(DESCRIPTION).replace("'", "''"));
				fw.write("comunas" + OUT_SEPARATOR + "martin@comunas.fm");
				System.out.print("comunas" + "," + "martin@comunas.fm");
				for (int col : BUILDING_EXPORT) {
					fw.write(OUT_SEPARATOR + building.get(col));
					System.out.print("," + building.get(col));
				}
				fw.write("\n");
				System.out.println();
			}
		}
		try (FileWriter fw = new FileWriter(BASE_DIR + "comunasParts.csv")) {
			for (int i = 1; i < lines.size(); i++) {
				List<String> building = lines.get(i);
				int partCount = Integer.parseInt(building.get(PART_COUNT));
				String buildingId = building.get(ZIP) + "|" + building.get(STREET) + "|" + building.get(NAME) + "|"
						+ building.get(VOLUME);
				System.out.print("Building [" + buildingId + "] (" + partCount + " parts): ");
				for (int p = 0; p < partCount; p++) {
					int offset = PART_COUNT + 1 + p * 9;
					fw.write(buildingId);
					System.out.print(buildingId);
					building.set(offset + PART_ID, "P" + building.get(offset + PART_ID));
					building.set(offset + PART_CONDITION,
							String.valueOf(Math.round(100 * Float.parseFloat(building.get(offset + PART_CONDITION)))));
					String strain = building.get(offset + PART_STRAIN);
					building.set(offset + PART_STRAIN, "+".equals(strain) ? "1" : "-".equals(strain) ? "-1" : "");
					String strength = building.get(offset + PART_STRENGTH);
					building.set(offset + PART_STRENGTH, "+".equals(strength) ? "1" : "-".equals(strength) ? "-1" : "");
					building.set(offset + PART_DESCRIPTION, building.get(offset + PART_DESCRIPTION).replace("'", "''"));
					for (int col : PART_EXPORT) {
						fw.write(OUT_SEPARATOR + building.get(offset + col));
						System.out.print("," + building.get(offset + col));
					}
					fw.write("\n");
					System.out.println();
				}
			}
		}
	}

}
