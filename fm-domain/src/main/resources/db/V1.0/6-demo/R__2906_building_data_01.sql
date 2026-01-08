
-- insert into migr_obj_building_v(tenant, owner, account, name, building_type_id, building_sub_type_id, building_part_catalog_id, building_year, street, zip, city, country_id, currency_id, volume, area_gross, insured_value, insured_value_year, building_maintenance_strategy_id, description) values
-- ('comunas', 'martin@comunas.ch', '5620', 'Reussbrücke-Saal, Zwischenbau', 'T12','ST01-9','C9',1938,'Wohlerstrasse 1','5620','Bremgarten','ch','chf',4816,0,2302,2000,'N','Umbau/Sanierung ''99\nDachraum gem. Hauswart i.O.');

insert into migr_obj_building_v(tenant, owner, account, name, building_type_id, building_sub_type_id, building_part_catalog_id, building_year, street, zip, city, country_id, currency_id, volume, area_gross, insured_value, insured_value_year, building_maintenance_strategy_id, description) values

('demo', 'martin@zeitwert.io', '3032', 'Wohn- und Geschäftshaus', 'T01','ST02-11','C7',1834,'Bergfeldstrasse 8','3032','Hinterkappelen','ch','chf',1840,0,1573,2009,'NW','Ehemalige Primarschule\nIm EG Praxis mit Mieterausbau (Oberflächen, Klimagerät, Beleuchtung, Einbauten)\nWärmepumpe wurde ersetzt, Oel-Heizung wird nur im Bedarfsfall genutzt\nWC in Praxis: muss mehrmals spülen bis ok.\nLehrerwohnung: B: PVC, Linol, Platten'),
('demo', 'martin@zeitwert.io', '3032', 'Feuerwehrmagazin/3 Wohnungen', 'T01','ST02-11','C7',1989,'Dorfstrasse 45','3032','Hinterkappelen','ch','chf',6160,0,4304,2009,'NW','Fenster in Wohnung auf Südseite ist im Winter beschlagen (IV noch intakt?)\nSchlauchwasch- und Trocknungsanlage, Druckluft ok.\nDie Küchen wurden 2013 in allen drei Wg. saniert.\nLüftungsanlage inkl. Heizung\nKompressor'),
('demo', 'martin@zeitwert.io', '3032', 'Jugendtreff (Pavillon)', 'T10','ST08-43','C29',2007,'Araweg 9','3032','Hinterkappelen','ch','chf',1460,0,800,2009,'NW','In Wohlen stark kalkhaltiges Wasser'),
('demo', 'martin@zeitwert.io', '3032', 'Wohnhaus (private Kindertagesstätte)', 'T02','ST01-4','C9',1920,'Hofenstrasse 54','3032','Hinterkappelen','ch','chf',768,0,580,2009,'NW',''),
('demo', 'martin@zeitwert.io', '3032', 'Kindergarten Kappelenring', 'T02','ST01-4','C0',2015,'Kappelenring 34a','3032','Hinterkappelen','ch','chf',2763,0,2568,2015,'NW','Das Flachdach hängt im Bereich der Eingänge leicht durch, das Dachwasser fliesst dort vermutlich über die Dachkante (Wasserspuren am Boden und Dachrandabschluss); Dachkonstruktion vermutlich in diesem Bereich über dem Eingang zu schwach (Abklärung empfohlen, im Winter bei Schneelast beobachten)\n\nIm Sommer zu heiss.'),

('demo', 'martin@zeitwert.io', '3033', 'Altes Schulhaus/Doppelkindergarten/Klassenzimmer/2 Whg.', 'T02','ST01-4','C9',1890,'Schulgasse 14','3033','Wohlen','ch','chf',2500,0,1805,2009,'NW','Tagesschule, Basisstufe\nDachraum mit Mansardezimmer und 2-Zi-Wg.: B: Kugelgarn, W: Täfer, D: Täfer\nBretterboden in Estrich\nGewölbekeller'),
('demo', 'martin@zeitwert.io', '3033', 'Schulhaus, Pausenhalle, ZS-Anlage', 'T02','ST02-12','C9',1960,'Schulgasse 16','3033','Wohlen','ch','chf',10200,0,6768,2009,'NW',''),
('demo', 'martin@zeitwert.io', '3033', 'Doppelturnhalle', 'T12','ST01-9','C9',1991,'Schulgasse 18','3033','Wohlen','ch','chf',12800,0,5610,2009,'NW','Kontrolle der heruntergehängten Decke wird empfohlen (Aufhängungen im Hohlraum)'),
('demo', 'martin@zeitwert.io', '3033', 'Aufbahrungsgebäude, Abdankungshalle', 'T09','ST03-18','C0',1972,'Hauptstrasse 22','3033','Wohlen','ch','chf',875,0,564,2009,'NW','einzelne Räume wurden stillgelegt'),
('demo', 'martin@zeitwert.io', '3033', 'Verwaltungsgebäude (Gemeindehaus)', 'T06','ST06-35','C21',1966,'Hauptstrasse 26','3033','Wohlen','ch','chf',7236,0,6000,2009,'NW','früher: Gemeindehaus und Wohnhaus\nGesamtsanierung ''85 (Obergeschosse und Dach, therm. Isol. und Ersatz Fenster)\nErweiterung und Sanierung ''07 (erw. therm. Isol.; Minergie)\nErsatz Fensterbeschläge ''14'),
('demo', 'martin@zeitwert.io', '3033', 'Garage (Nebengebäude)', 'T13','ST01-2','C9',1975,'Schulgasse 14a','3033','Wohlen','ch','chf',145,0,49,2009,'NW',''),

('demo', 'martin@zeitwert.io', '3034', 'Schulhaus, Pausenhalle , Kita', 'T02','ST02-12','C9',1963,'Schulstrasse 4','3034','Hinterkappelen','ch','chf',7481,0,5670,2012,'NW','Lüftung f. WC und Fumoir\nSanierung aussen und innen 2004\nWärmetechn. Sanierung 4.5 Mio geplant für ges. Schulanlage\nGeländehöhen zu tief\nSchiebetüren in Werkräumen\nFeuerleiter vorh.\nWindfang alt, undichte Türen\nIm Anbau Kita mit eigener Küche, Mansardendach ausgebaut, Böden Linol\nDach innen Gipskarton, gestrichen\nWC: ältere Apparate, aber funktionierend\nTreppe aus Kunststein\nEinbauschränke\nSimse Naturst.\nWände gestr./verputzt'),
('demo', 'martin@zeitwert.io', '3034', 'Turnhalle und Lehrschwimmbecken', 'T12','ST07-38','C9',1981,'Schulstrasse 5','3034','Hinterkappelen','ch','chf',9627,0,5530,2012,'NW','Turnhallenboden neu\nHeizung erfolgt über Lüftung\nLehrschwimmbecken: Lüftung neu, W: Keramik tw. neu\nWarmwasseraufbereitung z.T. neu (''97 + ''08) Wärmetauscher, Abwasser\nDruckluftsteuerung'),
('demo', 'martin@zeitwert.io', '3034', 'Wohnhaus (2 Wohnungen)', 'T01','ST02-11','C7',1963,'Schulstrasse 6','3034','Hinterkappelen','ch','chf',1150,0,902,2009,'NW','Anbau Wintergarten ca. 2006\nBadsanierung 2014'),
('demo', 'martin@zeitwert.io', '3034', 'Schulhaus', 'T02','ST02-12','C9',1980,'Schulstrasse 7','3034','Hinterkappelen','ch','chf',3440,0,2485,2012,'NW','Bibliothek vor wenigen Jahren saniert.\nEin Klassenzimmer mit Linolboden\nSolaranlage auf Steildach(?)'),
('demo', 'martin@zeitwert.io', '3034', 'Schulhaus/ZS-Anlage/Verbindungsgang', 'T02','ST02-12','C9',0,'Schulstrasse 9','3034','Hinterkappelen','ch','chf',7446,0,4970,2009,'NW','Ausbau ähnlich wie Schulstr. 4\nSchulküche wird ''13 saniert'),
('demo', 'martin@zeitwert.io', '3034', 'Primarschule/Doppelturnhalle/KZ/WR', 'T12','ST01-9','C9',1976,'Kappelenring 36/36a','3034','Hinterkappelen','ch','chf',19011,0,13947,2009,'NW','Dachsanierung ''97; 2.4 Mio. Fr. (davon 400T für Brandschutzmassnahmen)\nFassaden: Fugen neu, neu gestrichen\nFenster: Beschläge tw. neu\nIn Vorraum TH finden Gemeindeversammlungen statt (Bühne)\n\nTw. Klassenzimmer unterteilt in Gruppenräume (Spezialräume Logopädie ok)\nTH: neue Böden 2018, Beleuchtung, Audioanlage mangelhaft, *Fenster werden nicht ersetzt'),
('demo', 'martin@zeitwert.io', '3034', 'Schützenhaus', 'T12','ST10-45','C0',2004,'Schützenweg 50','3034','Murzelen','ch','chf',809,0,787,2009,'NW','Munitionsraum mit Panzertüre'),
('demo', 'martin@zeitwert.io', '3034', 'Wohnhaus (3 Whg.)', 'T01','ST02-11','C7',1955,'Murzelenstrasse 80','3034','Murzelen','ch','chf',1375,0,1162,2009,'NW','Urspr. Lehrerhaus\nAusgebauter Dachraum, isol.\nDecke gegen UG wurde isoliert ca. 2000'),
('demo', 'martin@zeitwert.io', '3034', 'Garagen/Aussengeräteraum Schule', 'T13','ST01-2','C0',1978,'Murzelenstrasse 82A','3034','Murzelen','ch','chf',360,0,152,2009,'NW',''),
('demo', 'martin@zeitwert.io', '3034', 'Schulhaus/Kindergarten/Hauswartwohnung', 'T02','ST02-12','C9',1955,'Murzelenstrasse 82','3034','Murzelen','ch','chf',3920,0,2884,2009,'NW','Schulleiterbüro eingebaut\nIm Windfang wurden die inneren Türen demontiert\nKindergarten: Zwischengeschoss eingebaut\nHW-Wohnung: Estrich neu, isoliert mit Leitertreppe, (Boden) ''14 \nSanitär: Druck fällt tw. zusammen K+W, Küche neu ''14, Bad: ung. 40 Jahre alt (nur WC ausgewechselt)\nSchulzimmer: (1 Zi: neuer Boden Linol) neue Beleuchtung im EG und OG\nKindergarten: Fensterfront ist nicht dicht (Fenterflügel wurden gerichtet), Regen drückt rein, Zugluft\nFassade aussen: Holzfensterbänke/-schwellen tw. morsch\nZugseile innen ev. nachspannen, Holzverzapfungen bei Balken ev. feucht (dunkel und Rost bei Metallverbindungen)\nAufsteigende Feuchtigkeit im Treppenhaus\nBoden UG neu\nDecken und Wände neu gestrichen im EG und OG\nneu Mittagstisch\nProbleme mit Fliegen; ev. Luftzug bei Storenkästen'),
('demo', 'martin@zeitwert.io', '3034', 'Turnhalle', 'T12','ST01-9','C9',1978,'Murzelenstrasse 84','3034','Murzelen','ch','chf',4608,0,2097,2009,'NW','In Investitionsplan: Ersatz Fensterfront und Isolation Dach\nFenstersanierung und Decke ist im IP\nMalerarbeiten im ''13\nDuschen: Armaturen neu (''13)\nWW-Leitung tw. neu\nUG: Beleuchtung neu (ausser Turnhalle)\nFenster im WC wurden ersetzt: Holz/Metall (''16)\nGeländer wird neu gemacht'),

('demo', 'martin@zeitwert.io', '3043', 'Velounterstand', 'T13','ST01-2','C9',1996,'Schülerweg 13','3043','Uettligen','ch','chf',360,0,158,2009,'NW','*Gläser werden/wurden etappenweise ersetzt'),
('demo', 'martin@zeitwert.io', '3043', 'Turnhalle', 'T12','ST01-9','C9',1996,'Schülerweg 14','3043','Uettligen','ch','chf',8200,0,3828,2009,'NW','Turnhallenboden wurde ersetzt ''17\nTreppenlift\nGeräteschuppen für Sprungmatten mit Riffelblechen im Aussenbereich an Fassade angebaut\nBei Duschen Wasserschaden (Fugen müssen periodisch ersetzt werden)'),
('demo', 'martin@zeitwert.io', '3043', 'Primarschulhaus, ged. Pausenhalle, Anbau Nord', 'T02','ST02-12','C9',1962,'Schülerweg 15','3043','Uettligen','ch','chf',5319,0,3670,2009,'NW','In Investitionsplan: Ersatz Holzfenster\nP.. Ludothek wurde nachträglich angebaut mit Duripaneel\nInnere Verdunklungen\nTagesschule im UG\nAnbau ca. ''04\nWassereintritt in Bibliothek (Deckenverkl. und Parkett beschädigt)'),
('demo', 'martin@zeitwert.io', '3043', 'Turnhalle/Hauswartwhg./Heizzentrale', 'T12','ST01-9','C9',1961,'Schülerweg 16','3043','Uettligen','ch','chf',4497,0,2820,2009,'NW','Lüftung für Holzsschnitzel (wegen Gärung)\nTore Geräteraum TH ok\nRadiatoren in TH o. Thermostaten\nHW-Wohnung:\n B: Linol, Parkett in WZ, Teppich UG\n W: Putz, gestr.\n D: Putz, gestr., Täfer in Kü, Pavatex, gestr.\n Heizverteilung: Radiatoren m. Thermostaten\n Balkon: Untersicht Holz i.O., Bodenablauf vermoost, Stütze verputzt (Putzschäden), Risse in Fass. über Balkon\n Fenster (''00): IV, Küche Metall, Einbauschränke, Vorhangbretter, Simse'),
('demo', 'martin@zeitwert.io', '3043', 'Oberstufenschulhaus/Aula', 'T02','ST02-12','C9',1961,'Schülerweg 18','3043','Uettligen','ch','chf',17300,0,9648,2009,'NW','Gebäudehülle wie Schülerweg 15\nAnbau ''91 Erweiterungen\nAufzug im Anbau\nKüche bei Mittagstisch im UG, bei Mittagstisch nur GSP\nim UG neu elektrische Storen, zusätzlich innere Verdunkelungen\nLehrerzimmer; neuer Boden (Parkett), Deckenbeleuchtung bei Fenster'),
('demo', 'martin@zeitwert.io', '3043', 'Velounterstand', 'T13','ST01-2','C9',1994,'Alpenblickweg 10','3043','Uettligen','ch','chf',110,0,102,2009,'NW',''),
('demo', 'martin@zeitwert.io', '3043', 'Kulturelles Zentrum Reberhaus', 'T10','ST06-36','C29',1900,'Lindenstrasse 4','3043','Uettligen','ch','chf',5500,0,4404,2009,'NW','Umnutzung Bauernhaus in Kulturzentrum 1987\nTreppen und Niveaulift\nKüche im Schrank zu Stuben und Saal (m. GSP, Kaffeemasch. u. mob. Backofen)'),
('demo', 'martin@zeitwert.io', '3043', 'Feuerwehrmagazin/Schulungsraum', 'T14','ST07-37','C0',1999,'Ahornweg 7','3043','Uettligen','ch','chf',1600,0,613,2009,'NW','Fluchttreppe in Stahl\nKein UG'),
('demo', 'martin@zeitwert.io', '3043', 'ZS-Lager/BSA/Einstellhalle/Rasenspielfeld', 'T14','ST03-19','C0',1996,'Schülerweg 11','3043','Uettligen','ch','chf',7300,0,3102,2009,'NW','4 Wartungen pro Jahr durch ZS (2 gr. u. 2 kl.)\n\nUNG: XXX bei Hartbeton (s. Foto) auf Parkplätzen'),
('demo', 'martin@zeitwert.io', '3043', 'Doppelkindergarten/Musikschule/Jugendtr.', 'T02','ST01-4','C9',1987,'Schülerweg 12','3043','Uettligen','ch','chf',3000,0,1898,2009,'NW','Küche im Jugendtreff für Begehung nicht zugängig; gem. Hauswart ist Küche in schlechtem Zustand\nEhem. Heizraum ist neu Schlagzeugraum; neue Fenster, Teppich, Beleuchtung, Wände, Decken (Akustikpl.)\nLehrerzimmer im UG mit Küche\nEntfeuchtungsanlage Schlagzeugraum ist neu\nJugendraum: neu: nur Unterlagsboden (Parkett wurde ersetzt)'),

('demo', 'martin@zeitwert.io', '8253', 'best. Schulhaus Zentrum', 'T02','ST02-12','C0',1958,'Schulstrasse 5','8253','Diessenhofen','ch','chf',13886,0,8497,2006,'N',''),
('demo', 'martin@zeitwert.io', '8253', 'Kindergarten Schupfenzelg', 'T02','ST01-4','C8',1973,'Schützenstrasse','8253','Diessenhofen','ch','chf',1624,0,861,2009,'N',''),
('demo', 'martin@zeitwert.io', '8253', 'Kindergarten Basadingen', 'T02','ST01-4','C8',1979,'Schulstr. 38','8253','Diessenhofen','ch','chf',1049,0,670,2006,'N',''),
('demo', 'martin@zeitwert.io', '8253', 'Sanierung Schulhaus Zentrum', 'T02','ST02-12','C0',1958,'Schulstrasse 5','8253','Diessenhofen','ch','chf',13886,0,12000,2018,'N',''),
('demo', 'martin@zeitwert.io', '8253', 'Janus Schulhaus Zentrum', 'T02','ST02-12','C0',2018,'Schulstrasse 5','8253','Diessenhofen','ch','chf',11967,0,8984,2018,'N',''),

('demo', 'martin@zeitwert.io', '8255', 'Schulhaus Schlattingen', 'T02','ST02-12','C0',1970,'Bahnhofstrasse 1','8255','Schlattingen','ch','chf',3539,0,2480,2006,'N',''),

('demo', 'martin@zeitwert.io', '8266', 'Oberstufenschulhaus Feldbach', 'T02','ST02-12','C9',2004,'Feldbach','8266','Steckborn','ch','chf',18099,0,9298,2005,'N','Bauherrschaft: Oberstufenschulgemeinde\nGV-Wert und Volumen inkl. Velounterstand\nAula (200 Plätze)\nAufzug mit Innentür gem. akt. Vorschriften'),
('demo', 'martin@zeitwert.io', '8266', 'Hub gelb', 'T02','ST02-12','C9',1954,'Frauenfelderstrasse 10','8266','Steckborn','ch','chf',3558,0,2180,2009,'N',''),
('demo', 'martin@zeitwert.io', '8266', 'Seeschulhaus', 'T02','ST02-12','C9',1863,'Seestrasse 126','8266','Steckborn','ch','chf',4193,0,2279,2009,'N','Ein Baubeschrieb ist nicht vorhanden.\n1998 und 2000 erfolgte ein Sanierung innen und aussen.'),
('demo', 'martin@zeitwert.io', '8266', 'Sport- und Mehrzweckhalle', 'T12','ST01-9','C9',1981,'Feldbach','8266','Steckborn','ch','chf',21371,0,7694,2009,'N',''),
('demo', 'martin@zeitwert.io', '8266', 'Hub rot', 'T02','ST02-12','C9',1953,'Frauenfelderstrasse 8','8266','Steckborn','ch','chf',3015,0,1718,2006,'N','inkl. Pausenhalle; Bj. 1962, Vers.wert: 2''705''000.-\nSanierung wie Hub gelb\nAula: Heizung ohne Thermostaten, tw. keine Storen (aussen) nur Innenverdunklung, Lüftung vorh.'),
('demo', 'martin@zeitwert.io', '8266', 'Doppelkindergarten', 'T02','ST01-4','C8',1996,'Zelgistrasse 8','8266','Steckborn','ch','chf',2601,0,1464,2010,'N',''),
('demo', 'martin@zeitwert.io', '8266', 'Hub blau', 'T02','ST02-12','C9',2011,'Hubstrasse','8266','Steckborn','ch','chf',3928,0,1500,2011,'N','Kellerraum (Musikzimmer) feucht!'),

('demo', 'martin@zeitwert.io', '8476', 'Wohnhaus mit Schopf', 'T01','ST05-26','C6',1951,'Unterdorf 10','8476','Unterstammheim','ch','chf',1381,0,970,2011,'N','- auf Balkon rostige Abschlussbleche auf Brüstungen\n-z. T. Naturkeller'),
('demo', 'martin@zeitwert.io', '8476', 'Turnhalle', 'T02','ST02-12','C33',1954,'Bahnhofstrasse 7','8476','Unterstammheim','ch','chf',9813,0,1954,2003,'N','- die Duschanlagen sind im Jahr 2015 repariert worden, jedoch ist unter den Plattenbelägen hohe Feuchtigkeit vorhanden. Gemäss Angaben des Fachmannes sind sie noch max. 2 Jahre für den Betrieb geeignet.\n- gesamthaft wurden CHF 300`000 investiert für neuen Boden TH, Geräteraumtore, Fenster, Belichtung etc.'),
('demo', 'martin@zeitwert.io', '8476', 'Schulhausanlage', 'T02','ST02-12','C9',1894,'Bahnhofstrasse 7','8476','Unterstammheim','ch','chf',4146,0,2718,2003,'N','-Sockelbereich ok\n-Unterstand II: Fassade aus Sichtmauerwerk\n-Aussentüre auf Paniktüre umgerüstet\n-Treppengeländer sehr tief (80cm)\n-im UG vermutlich Asbest-Platten\n-Pausenhalle neu gestrichen für CHF 5`000.-\n\n- Begehung mit Herrn B. Frei (Hauswart)'),
('demo', 'martin@zeitwert.io', '8476', 'Schulhausanlage', 'T02','ST02-12','C9',1969,'Bahnhofstrasse 8','8476','Unterstammheim','ch','chf',13710,0,7728,2003,'N','- Baujahr Anbau: (''03)\n- Anbau Süd (KZ) mit TH (''02) und Bibliothek\n- Velounterstand: Sichtbeton, Metalldach (wurde erneuert da altes Glasdach defekt)\n- überdachter Pausenbereich: soll bei Sanierung neu gemacht werden, Gläser tw. zersprungen und undicht, Gläser können nicht gut gereinigt werden, im Sommer staut sich die Hitze unter dem Dach\n- Schulküchen im ''08 saniert, Boden aus Kautschuk mit Antirutschbeschichtung, muss neu gemacht werden\n- Geländerhöhe 90cm\n- Staketenabstand: 14.5 cm, zu breit!\n\nGerätehaus: Flachdach, Kiesklebdach, Untersichten aus Holz, Elektro: Licht, Steckdosen; in gutem Zustand\n\n- Begehung mit Herrn B.Frei (Hauswart)'),
('demo', 'martin@zeitwert.io', '8476', 'Sporthalle', 'T02','ST02-12','C33',2003,'Bahnhofstrasse 8','8476','Unterstammheim','ch','chf',9813,0,2516,2003,'N','- vor Haupteingang auf Platz wurden neu Rillen für Wasserabfluss gemacht (CHF 10`000), da vorher bei starkem Regen Wasser durch die Tür ins Innere floss\n\n- Begehung mit Herrn B. Frei (Hauswart)'),
('demo', 'martin@zeitwert.io', '8476', 'Schulhaus und Kindergarten', 'T02','ST02-12','C9',1847,'Unterdorf 8','8476','Unterstammheim','ch','chf',5910,0,4500,2018,'N','- Riss im DG Altbau sollte beobachtet werden (Risssiegel)\n- Baujahre der drei Gebäudeteile: Kindergarten 1847 / Anbau 1984 / Anbau 1995 / Zwischenbau 2017\n- im Anbau wurde eine Treppe eingebaut (brandschutztechnisch war dies erforderlich), welche die beiden Kindergärten im EG und OG verbindet\n\nGV-Wert und Kubatur gemäss neuer Schätzung vom 12.04.2018'),

('demo', 'martin@zeitwert.io', '8556', 'Kindergarten Haldengüetli', 'T02','ST01-4','C9',1938,'Bernrainstr. 26','8556','Wigoltingen','ch','chf',1487,0,1187000,2005,'N','Hauswart: Hr. Gubler\nNiedrige Raumhöhen in Kindergartenräumen'),
('demo', 'martin@zeitwert.io', '8556', 'Mehrzweckhalle', 'T12','ST01-9','C33',1939,'Käsereistrasse 10','8556','Wigoltingen','ch','chf',2842,0,1457,2014,'N',''),
('demo', 'martin@zeitwert.io', '8556', 'Primarschule Wigoltingen', 'T02','ST02-12','C9',1958,'Käsereistrasse 10','8556','Wigoltingen','ch','chf',4409,0,2894,2014,'N','Grund für den Wassereintritt war ein defekter Ziegel.\nKindergartenraum neu (Frühling 2019), ausser Beleuchtung.'),
('demo', 'martin@zeitwert.io', '8556', 'Turnhalle (Sarnahalle)', 'T12','ST01-9','C33',1973,'Käsereistrasse 10','8556','Wigoltingen','ch','chf',4326,0,2462,2014,'N',''),
('demo', 'martin@zeitwert.io', '8556', 'Werkzentrum', 'T02','ST02-12','C9',1973,'Bahnhofstrasse 40','8556','Wigoltingen','ch','chf',7617,0,4901,2014,'N','Die Gebäudenhülle wurde ca ''08 renoviert.\nDie Fernwärme für die Primarschule, Mehrzweckhalle und Turnhalle werden von diesem Gebäude (Werkzentrum) aus gesteuert.'),
('demo', 'martin@zeitwert.io', '8556', 'Sekundarschulgebäude', 'T02','ST02-12','C9',1995,'Kirchstrasse 12a','8556','Wigoltingen','ch','chf',18276,0,11895,2014,'N','-Problem der Feuchtigkeit im Chemieraum wurde behoben.\n-Keine Lüftung im Singsaal\ns.a. Bestandesaufnahmen vom März 2018'),
('demo', 'martin@zeitwert.io', '8556', 'Primarschule Raperswilen, Altbau inkl. Whg', 'T02','ST02-12','C9',1899,'Schulstrasse 7','8556','Raperswilen','ch','chf',2834,0,1839,2011,'N',''),
('demo', 'martin@zeitwert.io', '8556', 'Turnhallengebäude Sekundarschule', 'T12','ST01-9','C9',1996,'Kirchstrasse 12a','8556','Wigoltingen','ch','chf',4933,0,1874,2014,'N','-zwei Abläufe (ein Ablauf musste ersetzt werden beim Flachdach Bereich)'),
('demo', 'martin@zeitwert.io', '8556', 'Wohnhaus & Garage', 'T01','ST02-11','C7',0,'Kirchstrasse 10','8556','Wigoltingen','ch','chf',697,0,473,2014,'N','- Garage konnte nicht besichtigt werden'),
('demo', 'martin@zeitwert.io', '8556', 'Primarschule Wigoltingen, Altbau', 'T02','ST02-12','C9',1870,'Käsereistrasse 12','8556','Wigoltingen','ch','chf',2737,0,2289,2014,'N','- Radon verdacht im Keller, Messungen wurden von Studenten gemacht (Messungen nicht erfahren)\n- Feuchtigkeit von Unten nach Oben'),
('demo', 'martin@zeitwert.io', '8556', 'Kindergarten Haldengüetli, m Gge', 'T02','ST01-4','C9',1938,'Bernrainstrasse 26','8556','Wigoltingen','ch','chf',1500,0,1411,2014,'N','-Feuchtigkeitsproblem bei dem Raum unter der Treppe\n-Fluchtweg nicht gewehrleistet\n-Keine Lüftung'),








('demo', 'martin@zeitwert.io', '1111', 'Rathaus', 'T06','ST07-40','C21',1913,'Rathausplatz 1','1111','Musterstadt','ch','chf',8051,0,5235,1995,'N','Klinkerboden und Decke haben Risse - Stadtratssaal: Holz-Metallfenster 2004 - Holztüren mit Türrahmen, Holztreppen im OG, Finanzabteilung: schlechte Isolation da Zugerscheinungen im Winter und im Sommer zu heiss, Abstellkammer, Materialraum, Glasbausteine, Teeküche'),
('demo', 'martin@zeitwert.io', '1111', 'Testgebäude1', 'T10','ST06-36','C29',1962,'Teststrasse.','1111','','ch','chf',1025,0,831,2012,'N',''),

('demo', 'martin@zeitwert.io', '3049', 'Schulhaus/Hausw.whg./Turnhalle/ZS', 'T02','ST02-12','C9',1939,'Staatsstrasse 76','3049','Säriswil','ch','chf',9300,0,6292,2009,'NW','Dachsanierung inkl. Wärmedämmung ist im Investitionsplan; Ziegel sind nicht mehr erhältlich\nGeländererhöhungen ''11\nGeländer im Altbauteil: Höhen mögl.w. zu niedrig (Überprüfen)\nHauswartwohnung im DG\nIm Gebäudeversicherungswert sind das Schulhaus sowie die Turnhalle enthalten. Die beiden Liegenschaften unterscheiden sich bezüglich des Gebäudetyps sowie des Gebäudealters. Diesem Umstand wird mit gemittelten Werten bei den entsprechenden Bauteilen Rechung getragen.'),

('demo', 'martin@zeitwert.io', '4303', 'Gemeindehaus inkl. Wohnung Hauswart und Feuerwehrmagazin Dorf', 'T06','ST06-35','C22',1968,'Dorfstrasse 17','4303','Kaiseraugst','ch','chf',10589,0,9737,1998,'N','Altbau UG, ehem. LS-Raum: Feuchteproblem, OGA-Raum: Wassereintritt (wird behoben)\nWohnung: B: Keramik, Parkett, W, D: Putz, Fenster: IV 2-fach verglast\nWestfassade: Riss in Mauerwerk, Gefahr von Eindringen von Feuchtigkeit\nSanierungsarbeiten für 2019 budgetiert'),
('demo', 'martin@zeitwert.io', '4303', 'Chlorgasgebäude', 'T11','ST09-44','C0',1996,'Lochmatt','4303','Kaiseraugst','ch','chf',45,0,25,1996,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Asylbewerberunterkunft', 'T01','ST01-1','C7',2007,'Rinaustrasse 26','4303','Kaiseraugst','ch','chf',2085,0,1655,2007,'N','Für den laufenden Unterhalt ist eine externe Firma beauftragt (Servicevertrag).\nDas Mobiliar ist im Versicherungswert nicht enthalten.'),
('demo', 'martin@zeitwert.io', '4303', 'Asylbewerberunterkunft', 'T01','ST01-1','C7',0,'Rinaustrasse 28','4303','Kaiseraugst','ch','chf',407,0,217,2007,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Wohnhaus', 'T01','ST02-11','C7',1800,'Allmendgasse 13','4303','Kaiseraugst','ch','chf',820,0,430,2008,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Lötscher-Scheune', 'T04','ST01-8','C0',1899,'Kirchgasse 21','4303','Kaiseraugst','ch','chf',1600,0,263,1992,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Fährihäuschen / Stübli', 'T13','ST10-46','C0',1898,'Kirchgasse 19','4303','Kaiseraugst','ch','chf',250,0,130,1992,'N','Konnten das Dach nicht besichtigen.'),
('demo', 'martin@zeitwert.io', '4303', 'Fährianlagestelle', 'T13','ST12-49','C0',1927,'Rheinuferweg','4303','Kaiseraugst','ch','chf',231,0,79,2007,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Zollhaus', 'T13','ST06-34','C0',1952,'Rheinuferweg','4303','Kaiseraugst','ch','chf',34,0,21,2010,'N','SItzbank aus Holz'),
('demo', 'martin@zeitwert.io', '4303', 'Pumpenhaus', 'T05','ST02-15','C0',1912,'Rheinuferweg','4303','Kaiseraugst','ch','chf',38,0,11,2010,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Wartehäuschen Fähri / Werkhaus', 'T13','ST07-41','C0',1912,'Rheinuferweg','4303','Kaiseraugst','ch','chf',29,0,6,2007,'N','Das Gebäude konnte nicht besichtigt werden. Nutzung nicht genau bekannt; das Gebäude dient möglicherweise der Wartung des Fährbetriebs.'),
('demo', 'martin@zeitwert.io', '4303', 'Pumpihaus Clubhaus Rheingenossen', 'T01','ST05-26','C6',1913,'Rheinuferweg','4303','Kaiseraugst','ch','chf',638,0,342,2006,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Bootshaus Lochmatt', 'T13','ST05-31','C37',1953,'Rheinuferweg Ergolzbucht','4303','Kaiseraugst','ch','chf',897,0,63,1992,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Landgasthof Adler + Autounterstand', 'T11','ST01-7','C30',0,'Dorfstrasse 35','4303','Kaiseraugst','ch','chf',3043,0,2726,2017,'N','Im Keller: Wasser eindringung über Platz (Garage), feucht trotz Klimaanalage\nKühlzellen sind nicht im Versicherungswert enthalten'),
('demo', 'martin@zeitwert.io', '4303', 'Kinderkrippe', 'T02','ST01-4','C8',2013,'Violahof','4303','Kaiseraugst','ch','chf',2867,0,1829,2014,'N','Küche: Gewerbliche Küche mit Lüftungsanlage, Wand: Keramikplatten Boden: Linol'),
('demo', 'martin@zeitwert.io', '4303', 'Jugendhaus inkl. ehemaliger Polizeiposten', 'T01','ST08-42','C23',1974,'Violahof','4303','Kaiseraugst','ch','chf',2542,0,1667,2008,'N','Flachdach konnte nicht besichtigt werden'),
('demo', 'martin@zeitwert.io', '4303', 'Pfadihaus', 'T10','ST05-32','C0',1932,'Violahof','4303','Kaiseraugst','ch','chf',1239,0,855,2003,'N','Wassereintritt vorhanden, ungeklärt bei Balken'),
('demo', 'martin@zeitwert.io', '4303', 'Feuerwehrmagazin', 'T14','ST07-37','C37',0,'Violahof','4303','Kaiseraugst','ch','chf',1879,0,1249,2015,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'KIGA Rosenweg', 'T02','ST01-4','C8',1998,'Rosenweg 18','4303','Kaiseraugst','ch','chf',8501,0,4898,2008,'N','Das Dach konnte nicht besichtigt werden. Für alle Elemente der Gebäudehülle u.a. Elemente, deren Unterhalt über die Miete finanziert werden, sind die Reinvestationskosten für das ganze Gebäude enthalten. In der Regel werden diese Kosten von allen Miteigentümern gemeinsam getragen.'),
('demo', 'martin@zeitwert.io', '4303', 'Turnhalle Dorf', 'T12','ST01-9','C33',1963,'Dorfstrasse 18','4303','Kaiseraugst','ch','chf',6792,0,3870,2009,'N','Hauswart: Samuel Baumann\nVerbindung zum Schulhaus mit Metallstützen und Flachdach\nUntersicht Dach: Heraklit\nBühne Parkett: Starke Abnützungsspuren\nSanierung 2007\nAudioanlage nicht mitversichert'),
('demo', 'martin@zeitwert.io', '4303', 'Schulhaus Dorf inkl. Blockheizkraftwerk', 'T02','ST02-12','C9',1901,'Dorfstrasse 20','4303','Kaiseraugst','ch','chf',4875,0,3300,2009,'N','Rennovation innen vor ca. 12-13 Jahren inkl. Beleuchtung'),
('demo', 'martin@zeitwert.io', '4303', 'Löwenparking', 'T13','ST02-14','C35',2011,'Kastellstrasse 3','4303','Kaiseraugst','ch','chf',6907,0,2372,2011,'N','Parkuhr nicht im Versicherungswert enthalten'),
('demo', 'martin@zeitwert.io', '4303', 'Kindergarten Dorf', 'T02','ST01-4','C8',2013,'Dorfstrasse 19','4303','Kaiseraugst','ch','chf',1993,0,1380,2013,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Geräteschopf', 'T02','ST01-8','C0',2013,'Dorfstrasse 19','4303','Kaiseraugst','ch','chf',23,0,10,2013,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Wohnhaus Dorfstr. 27', 'T01','ST02-11','C7',1897,'Dorfstrasse 27','4303','Kaiseraugst','ch','chf',1156,0,896,1998,'N','3 Wohnungen\nWW wurde neu gemacht\nKaltwasser-Steigleitungen sind alt'),
('demo', 'martin@zeitwert.io', '4303', 'Schulhaus Liebrüti', 'T02','ST02-12','C10',1976,'Hintere Liebrüti','4303','Kaiseraugst','ch','chf',32195,0,21393,2015,'N','HW: Udo Vici\nInteraktive Wandtafeln nicht im Versicherungswert eingerechnet'),
('demo', 'martin@zeitwert.io', '4303', 'Pavillon Liebrüti', 'T02','ST02-12','C9',2009,'Hintere Liebrüti','4303','Kaiseraugst','ch','chf',2204,0,1377,2009,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Turnhalle Liebrüti', 'T12','ST01-9','C33',1976,'Hintere Liebrüti','4303','Kaiseraugst','ch','chf',23095,0,12169,2001,'N','Erweiterung und Sanierung 1998'),
('demo', 'martin@zeitwert.io', '4303', 'Gerätehaus Liebrüti', 'T12','ST01-9','C0',1977,'Hintere Liebrüti, Böttmeweg','4303','Kaiseraugst','ch','chf',289,0,144,2007,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'KIGA Schwarzackerstrasse', 'T02','ST01-4','C8',1976,'Schwarzackerstrasse 59','4303','Kaiseraugst','ch','chf',6007,0,3484,2015,'N','Das Flachdach konnte nicht besichtigt werden. Für alle Elemente der Gebäudehülle u.a. Elemente, deren Unterhalt über die Miete finanziert werden, sind die Reinvestationskosten für das ganze Gebäude enthalten. In der Regel werden diese Kosten von allen Mietern resp. vom Vermieter getragen.'),
('demo', 'martin@zeitwert.io', '4303', 'KIGA Liebrütistrasse', 'T02','ST01-4','C8',1974,'Liebrütistrasse 14','4303','Kaiseraugst','ch','chf',6566,0,3905,1998,'N','Für alle Elemente der Gebäudehülle u.a. Elemente, deren Unterhalt über die Miete finanziert werden, sind die Reinvestationskosten für das ganze Gebäude enthalten. In der Regel werden diese Kosten von allen Mietern resp. vom Vermieter getragen.\nDas Flachdach konnte nicht besichtigt werden'),
('demo', 'martin@zeitwert.io', '4303', '(Vereinshaus Kleingärten)', 'T10','ST05-32','C0',1996,'Im Liner','4303','Kaiseraugst','ch','chf',346,0,150,1996,'N','Objekt wurde nicht aufgenommen'),
('demo', 'martin@zeitwert.io', '4303', 'Kirchturm', 'T09','ST01-5','C0',1300,'Kirchgasse','4303','Kaiseraugst','ch','chf',583,0,808,2007,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'KIGA Violaweg', 'T02','ST01-4','C8',1977,'Violaweg 75','4303','Kaiseraugst','ch','chf',7344,0,4000,2011,'N','Der Versicherungswert wurde über das Gebäudevolumen anteilmässig am Gesamtwert geschätzt. Das Flachdach konnte nicht besichtigt werden.\nFür alle Elemente der Gebäudehülle u.a. Elemente, deren Unterhalt über die Miete finanziert werden, sind die Reinvestationskosten für das ganze Gebäude enthalten. In der Regel werden diese Kosten von allen Mietern resp. vom Vermieter getragen.'),
('demo', 'martin@zeitwert.io', '4303', 'Waldhütte', 'T12','ST11-48','C0',1983,'Stelliweg','4303','Kaiseraugst','ch','chf',607,0,294,2006,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Werkhof', 'T13','ST05-31','C37',1991,'Guggeregge 1','4303','Kaiseraugst','ch','chf',4728,0,2983,2010,'N','Nicht im GV-Wert (und somit auch nicht in Stratus) eingerechnete Bauteile:\n- Steuergeräte Wasserversorgung\n- Dieseltankanlage mit Zapfsäulen\n- Autolifte\n- Druckluftanlagen\n- Salzsilo\n- Altölentsorgungsstelle'),
('demo', 'martin@zeitwert.io', '4303', 'Sportplatz im Liner', 'T12','ST02-13','C33',2009,'Im Liner, Römerweg','4303','Kaiseraugst','ch','chf',1660,0,1138,2009,'N','Wasserdruckerhöhungsanlage, Bewässerungsanlage, Druckluftanlage sind im Versicherungswert nicht enthalten'),
('demo', 'martin@zeitwert.io', '4303', 'Waldhütte', 'T12','ST11-48','C0',1936,'Rifälderhübel','4303','Kaiseraugst','ch','chf',108,0,40,1996,'N','*nicht im Versicherungswert inbegriffen'),
('demo', 'martin@zeitwert.io', '4303', 'Jugend- und Kulturzentrum Violahof', 'T10','ST07-39','C29',2007,'Violahof','4303','Kaiseraugst','ch','chf',4600,0,3542,2008,'N','- Personenlift, 2007\n- Umbau Fenster OG (Holz)\n- Unterstand gehört dazu\n- Luftschutzraum gehört Pfadi'),
('demo', 'martin@zeitwert.io', '4303', 'Alterswohnungen 6 Stk. inkl. Bürgerkeller', 'T01','ST06-33','C7',0,'Kirchgasse 19','4303','Kaiseraugst','ch','chf',3172,0,2171,1993,'N','Der Bürgerkeller ist sehr feucht, Akustikdecke und Klimagerät vorhanden. Boden aus Keramik mit Bodenheizung und Küche (gewerbliche Küche mit Geschirrspüler)'),
('demo', 'martin@zeitwert.io', '4303', 'Materialraum', 'T11','ST09-44','C16',1968,'Strandbad','4303','Kaiseraugst','ch','chf',90,0,33,2006,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Abdankungshalle', 'T09','ST03-18','C29',1977,'Friedhof','4303','Kaiseraugst','ch','chf',2090,0,1481,2010,'N','Erweiterungsbau 2011/12'),
('demo', 'martin@zeitwert.io', '4303', 'Geräteraum', 'T05','ST01-8','C0',1996,'Friedhof','4303','Kaiseraugst','ch','chf',198,0,45,1997,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Camping, Kiosk', 'T11','ST09-44','C30',1958,'Lochmatt, Strandbadweg 59','4303','Kaiseraugst','ch','chf',698,0,316,2010,'N','Kälteanlagen und mobile Kücheneinrichtungen sind nicht im Versicherungswert enthalten'),
('demo', 'martin@zeitwert.io', '4303', 'WC-Gebäude', 'T11','ST09-44','C0',1950,'Lochmatt','4303','Kaiseraugst','ch','chf',131,0,88,2006,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Garderobengebäude', 'T11','ST09-44','C37',1929,'Lochmatt','4303','Kaiseraugst','ch','chf',304,0,176,2006,'N',''),
('demo', 'martin@zeitwert.io', '4303', 'Magazin', 'T11','ST09-44','C37',1927,'Lochmatt','4303','Kaiseraugst','ch','chf',31,0,17,2006,'N','Badmeisterhäuschen'),
('demo', 'martin@zeitwert.io', '4303', 'Betriebsgebäude', 'T11','ST09-44','C37',1982,'Lochmatt','4303','Kaiseraugst','ch','chf',207,0,445,2006,'N',''),

('demo', 'martin@zeitwert.io', '4950', 'Wohn- und Geschäftshaus', 'T01','ST02-11','C7',1902,'Bahnhofstrasse 4','4950','Huttwil','ch','chf',2300,0,1354,1999,'N',''),
('demo', 'martin@zeitwert.io', '4950', 'Wohn- und Geschäftshaus', 'T01','ST02-11','C7',1876,'Bahnhofstrasse 6','4950','Huttwil','ch','chf',3600,0,2200,2015,'N','Garagen angebaut\nDie Büros und das Dachgeschoss wurden komplett saniert. Der Ausbau ist daher in einem sehr guten Zustand.'),
('demo', 'martin@zeitwert.io', '4950', 'Garagen / Tankraum / Parkplatz', 'T05','ST04-24','C35',1961,'Marktgasse 5b','4950','Huttwil','ch','chf',833,0,39,1996,'N','- Die Garage mit der Nr. 5d enthält einen Öltank.\n- Garage 5c: wesentlicher Schaden, über die Dilatationsfuge dringt Wasser ein\n- Die Garagen sind gefüllt mit verschiedenen Materialien (Stromkabel etc.)'),
('demo', 'martin@zeitwert.io', '4950', 'Turnhalle, Sportplatz', 'T12','ST01-9','C33',1958,'Dornackerweg 2','4950','Huttwil','ch','chf',8000,0,4352,1985,'N','- Fassade ist ungenügend gedämmt und wird saniert\n- Sep. Aussengebäude mit Garderoben und Duschen, Fass. aus Holzwerkstoff- oder Zementplatten'),
('demo', 'martin@zeitwert.io', '4950', 'Friedhof, Leichenhalle', 'T09','ST03-18','C29',1971,'Friedhofweg 31a/37a','4950','Huttwil','ch','chf',1085,0,643,1996,'N','bei 31a: Dach mit Ziegeleindeckung ohne Unterdach, Fassade: Mauerwerk, verputzt, Dachrinnen rostig'),
('demo', 'martin@zeitwert.io', '4950', 'Sekundarschulhaus', 'T02','ST02-12','C9',1913,'Hofmattstrasse 5/5a','4950','Huttwil','ch','chf',16065,0,10890,2011,'N','- Hauswart: Herr Hansruedi Bühler\n- Schlechte Klimatisierung der Sekretariatsräume\n- Serverraum: Die Deckendämmung ist nicht komplett\n- Beim Mittelbau (Eingangshalle) tw. noch Baumängel vorhanden (die Dämmung beim Storenkasten löst sich, der Vorplatz wird gegen die Fassade entwässert, die Blechabdeckungen bei der Glasfront aussen lösen sich, es bildet sich ausserdem Kondenswasser)'),
('demo', 'martin@zeitwert.io', '4950', 'Wasserversorgung', 'T05','ST02-15','C0',1977,'Willimatt 2a','4950','Huttwil','ch','chf',2700,0,1676,1978,'N',''),
('demo', 'martin@zeitwert.io', '4950', 'Wohnhaus und Vereinslokal', 'T01','ST02-11','C7',1842,'Spitalstrasse 14','4950','Huttwil','ch','chf',2415,0,1950,1991,'N','- Gemäss Herr Rauch, muss die Westseite des Gebäudes dringend saniert werden.'),
('demo', 'martin@zeitwert.io', '4950', 'Magazin, Museum', 'T03','ST01-6','C14',1932,'Spitalstrasse 54a','4950','Huttwil','ch','chf',360,0,384,1999,'N',''),
('demo', 'martin@zeitwert.io', '4950', 'Schulhaus Nyffel', 'T02','ST02-12','C0',1962,'Nyffel 26','4950','Huttwil','ch','chf',4142,0,2707,1993,'N','Die Innenwände bei den Fenstern gedämmt.\nDas Treppengeländer weist eine zu geringe Höhe auf (80cm).'),
('demo', 'martin@zeitwert.io', '4950', 'Schulhaus HPS Schüpbach', 'T02','ST05-27','C9',1883,'Bernstrasse 78','4950','Huttwil','ch','chf',2600,0,1636,1996,'N','- Kühlräume und gewerbliche Küche gehören nicht zum Schulhaus -> separate Rechnung.'),
('demo', 'martin@zeitwert.io', '4950', 'Mehrzweckgebäude', 'T01','ST02-11','C7',1898,'Bahnhofstrasse 2','4950','Huttwil','ch','chf',700,0,474,1982,'N','- Im Untergeschoss ist eine aufsteigende Feuchtigkeit ersichtlich. Dies kommt vermutlich von der Strassenseite.\n- Das Quellwasser kommt aus dem Untergeschoss\n- Das Unterdach wurde neu gebaut'),
('demo', 'martin@zeitwert.io', '4950', 'Verwaltungsgebäude', 'T06','ST06-35','C22',1933,'Marktgasse 2','4950','Huttwil','ch','chf',6300,0,5414,1995,'N',''),
('demo', 'martin@zeitwert.io', '4950', 'Geschäftshaus', 'T06','ST05-30','C20',1835,'Marktgasse 4','4950','Huttwil','ch','chf',2020,0,1466,1995,'N','- Metallstütze\n- Die Einbauschränke sowie der Besprechungsraum sind Mieterausbau.'),
('demo', 'martin@zeitwert.io', '4950', 'Wohnhaus / Scheune', 'T01','ST05-26','C6',1917,'Buchenweg 5','4950','Huttwil','ch','chf',3350,0,1354,1993,'N','Angebauter Schuppen, die Lukarnen sind stark verwitert'),
('demo', 'martin@zeitwert.io', '4950', 'Feuerwehrmagazin', 'T13','ST05-31','C37',1911,'Hoffmattstrasse 3','4950','Huttwil','ch','chf',3300,0,1600,2015,'N',''),
('demo', 'martin@zeitwert.io', '4950', 'Alte Turnhalle', 'T12','ST01-9','C33',1897,'Oberdorfstrasse 11c','4950','Huttwil','ch','chf',1630,0,846,1996,'N','Angebauter Schuppen'),
('demo', 'martin@zeitwert.io', '4950', 'Zivilschutzanlage', 'T14','ST03-19','C38',1974,'Lindenstrasse 21','4950','Huttwil','ch','chf',1300,0,776,1975,'N','- Der Dieseltankraum sowie der Notstromaggregat sind ausser Betrieb.'),
('demo', 'martin@zeitwert.io', '4950', 'Wohnhaus und Büros', 'T01','ST02-11','C7',1900,'Oberdorfstrasse 19','4950','Huttwil','ch','chf',880,0,575,1992,'N','- Das UG wird vom Militärdienst gemietet und wurde neu ausgebaut'),
('demo', 'martin@zeitwert.io', '4950', 'Kindergarten Heimstrasse', 'T02','ST01-4','C8',1957,'Heimstrasse 1','4950','Huttwil','ch','chf',1400,0,10715,1996,'N',''),
('demo', 'martin@zeitwert.io', '4950', 'Zivilschutzanlage', 'T14','ST03-19','C38',1991,'Dornackerweg 6','4950','Huttwil','ch','chf',6240,0,2820,1993,'N','Mobile Entfeuchter vorh.'),
('demo', 'martin@zeitwert.io', '4950', 'Pumpwerk, Elektrizität', 'T05','ST01-3','C0',1975,'Lochmühleweg 2a','4950','Huttwil','ch','chf',650,0,647,1980,'N','Ein Teil des Gebäudes konnte nicht besichtigt werden (EW)'),
('demo', 'martin@zeitwert.io', '4950', 'Restaurant', 'T06','ST05-30','C0',1933,'Marktgasse 2','4950','Huttwil','ch','chf',6300,0,3238,1995,'N','- Das Restaurant befindet sich im EG des Rathauses (Stratus-Nr. 17 05) und sollte für die Investitionsplanung mit diesem zusammen berücksichtigt werden.\n- Der Waren- und Personenaufzug in der Küche wurde letztes Jahr erneuert.\n- Die Küche ist 19-jährig.\n- Der Pizzaofen gehört dem Pächter'),
('demo', 'martin@zeitwert.io', '4950', 'Verwaltungsgebäude', 'T06','ST05-30','C0',0,'Marktgasse 4','4950','Huttwil','ch','chf',2020,0,662,1995,'N','Umbau im 2. OG ist geplant.'),
('demo', 'martin@zeitwert.io', '4950', 'Schulanlage Schwarzenbach', 'T02','ST02-12','C0',1974,'Neuhausstrasse 15','4950','Huttwil','ch','chf',20444,0,11250,2007,'N','- Garderoben wurden im 2016 ersetzt.\n- Die Gebäudehülle wurde im 1994 isoliert.\n- Ehemaliger Velokeller neben Garage'),
('demo', 'martin@zeitwert.io', '4950', 'Berufsschulhaus', 'T02','ST03-16','C9',1990,'Oberdorfstrasse 4','4950','Huttwil','ch','chf',3800,0,2820,1991,'N','Treppengeländer 90cm'),
('demo', 'martin@zeitwert.io', '4950', 'Velokeller', 'T13','ST02-14','C35',1991,'Oberdorfstrasse 5','4950','Huttwil','ch','chf',275,0,113,1992,'N','- Gehört zur Berufsschule'),
('demo', 'martin@zeitwert.io', '4950', 'Schulhaus Städtli', 'T02','ST02-12','C0',1897,'Oberdorfstrasse 11a','4950','Huttwil','ch','chf',9200,0,6204,1996,'N','- Zivilschutzraum alte Turnhalle\n- Estrichboden isoliert (''06)\n- Alter Oeltank wird im Frühling ausgebaut\n- Dach wurde in den 90er Jahren saniert'),
('demo', 'martin@zeitwert.io', '4950', 'Werkhof', 'T13','ST05-31','C37',1835,'Oberdorfstrasse 13/15/15b','4950','Huttwil','ch','chf',3750,0,1755,2015,'N','- 1999 Innenausbau\n- Neues Sektionaltor im Jahr 2017'),
('demo', 'martin@zeitwert.io', '4950', 'Wasserversorgung', 'T05','ST02-15','C0',1958,'Buchenweg 6b','4950','Huttwil','ch','chf',480,0,502,1979,'N','Pumpen sind nicht mehr in Betrieb, Nebengebäude Baujahr 1921/22'),
('demo', 'martin@zeitwert.io', '4950', 'Pumpwerk', 'T05','ST02-15','C0',2007,'Huttwilwald 1a','4950','Huttwil','ch','chf',291,0,830,2008,'N',''),
('demo', 'martin@zeitwert.io', '4950', 'Wasserversorgung', 'T05','ST02-15','C0',1977,'Dornackerweg 2a/b','4950','Huttwil','ch','chf',330,0,918,1978,'N',''),
('demo', 'martin@zeitwert.io', '4950', 'Wasserversorgung', 'T05','ST02-15','C0',1934,'innere Schlüecht 5a','4950','Huttwil','ch','chf',2540,0,1051,1979,'N',''),
('demo', 'martin@zeitwert.io', '4950', 'Wasserversorgung', 'T05','ST02-15','C0',1891,'Möhrenweid 1a','4950','Huttwil','ch','chf',1950,0,801,1979,'N','- An diesem Ort gibt es kein Quellwasser mehr.\n- Vor 5-6 Jahren wurden der Boden sowie die Wände renoviert'),
('demo', 'martin@zeitwert.io', '4950', 'Garage Feuerwehr', 'T13','ST02-14','C0',1946,'Moostrasse 2a','4950','Huttwil','ch','chf',27,0,11,1996,'N',''),

('demo', 'martin@zeitwert.io', '5318', 'EFH', 'T01','ST05-26','C6',1868,'Hauptstrasse 46','5318','Mandach','ch','chf',670,0,502,2008,'N',''),
('demo', 'martin@zeitwert.io', '5318', 'MFH', 'T01','ST02-11','C7',1923,'Hauptstrasse 108','5318','Mandach','ch','chf',842,0,646,2008,'N','*Schimmelbildung im Anschluss Decke-Aussenwand'),
('demo', 'martin@zeitwert.io', '5318', 'Scheune', 'T04','ST01-8','C14',1911,'Mitteldorf 45','5318','Mandach','ch','chf',1294,0,209,2013,'N','An-/Umbau ~1955\nWelleternit wahrscheinlich asbesthaltig'),
('demo', 'martin@zeitwert.io', '5318', 'Schopf mit Garage', 'T04','ST03-21','C0',1953,'Mitteldorf 47','5318','Mandach','ch','chf',668,0,112,2008,'N',''),

('demo', 'martin@zeitwert.io', '5620', 'Schulhaus Isenlauf', 'T02','ST02-12','C9',1973,'Badstrasse 3','5620','Bremgarten','ch','chf',21342,0,9928,1990,'N','Anbau ''87, Heizzentrale Holzschnitzel ''08, danach Setzungen und Risse\nFenstergläser in Anbau ''95, im Altbau ''95 IV (auf Südseite sind Gläser gedreht), Rafflamellen ''87/''95\nSommerlicher Wärmeschutz ist nicht genügend\nFäkalienpumpe 3-4 Jahre alt\nIn Werkräumen kein zentraler Schlüsselschalter'),
('demo', 'martin@zeitwert.io', '5620', 'Kindergarten Unterstadt', 'T02','ST01-4','C9',1927,'Klosterweg 6','5620','Bremgarten','ch','chf',2218,614,1341,2005,'N','IV-Verglasung auf Nordseite\nAkustikdecken im KG\nkein UG vorh.\nNeubau: Beschattungselemente in Holz: Schiebeläden (fkt. n. mehr)\nAltbau: Holzläden'),
('demo', 'martin@zeitwert.io', '5620', 'Sporthalle', 'T06','ST06-35','C33',2010,'Badstrasse 5','5620','Bremgarten','ch','chf',21622,0,6886,2010,'N','Hauswartbüro vorhanden, Dachraum konnte nicht besichtigt werden'),
('demo', 'martin@zeitwert.io', '5620', 'Kindergarten Kapuzinerhügel', 'T02','ST01-4','C9',1972,'Kapuzinerhügel 5','5620','Bremgarten','ch','chf',1615,0,763,1994,'N',''),
('demo', 'martin@zeitwert.io', '5620', 'Reussbrücke-Saal, Zwischenbau', 'T12','ST01-9','C33',1938,'Wohlerstrasse','5620','Bremgarten','ch','chf',4816,0,2302,2000,'N','Umbau/Sanierung ''99\nDachraum gem. Hauswart i.O.'),
('demo', 'martin@zeitwert.io', '5620', 'Feuerwehrlokal, SR und Wg.', 'T14','ST07-37','C37',1983,'Kreuzmattstrasse 8','5620','Bremgarten','ch','chf',5039,0,2959,2007,'N',''),
('demo', 'martin@zeitwert.io', '5620', 'Casino', 'T10','ST11-47','C29',1934,'Wohlerstrasse','5620','Bremgarten','ch','chf',8672,0,3584,1997,'N','energetisch und akustisch mangelhaft\nWindfang konzeptionell ungenügend'),
('demo', 'martin@zeitwert.io', '5620', 'Stadtschulhaus', 'T02','ST02-12','C9',1895,'Obertorplatz','5620','Bremgarten','ch','chf',17099,0,9834,1991,'N','Brüstung/Geländerhöhe: 89cm\nFlachdach: Versicherungsfall (Folie spröde)'),
('demo', 'martin@zeitwert.io', '5620', 'Promendenschulhaus', 'T02','ST02-12','C9',1962,'Promenade','5620','Bremgarten','ch','chf',10727,0,6279,1996,'N','Brüstungshöhe Treppenhaus, auch in Anbau mangelhaft. Doku "Gefährdungen" von ''05 vorhanden.\nLüftung Schulküche: Zuluft wird nicht mehr gebraucht seit Umbau.\nFenster Anbau z.T. 3-fach-Verglasung.'),
('demo', 'martin@zeitwert.io', '5620', 'Gartenschulhaus', 'T02','ST02-12','C9',1971,'Gartenstrasse','5620','Bremgarten','ch','chf',3929,0,2585,2001,'N',''),
('demo', 'martin@zeitwert.io', '5620', 'Turnhalle Bärenmatt', 'T12','ST01-9','C9',1962,'Sportstrasse','5620','Bremgarten','ch','chf',7866,0,3961,2007,'N','Geräteraum: Tor nicht sicherheitskonform (geht nach aussen auf).'),
('demo', 'martin@zeitwert.io', '5620', 'Dienstgebäude', 'T01','ST02-11','C7',1974,'Badstrasse 1','5620','Bremgarten','ch','chf',1132,0,775,2005,'N','-Dusche im Materialraum wird nicht mehr gebraucht  - UG: Materialraum'),
('demo', 'martin@zeitwert.io', '5620', 'Schwarzschloss', 'T01','ST02-11','C7',0,'Pfarrgasse 1','5620','Bremgarten','ch','chf',2839,0,2209,2004,'N',''),
('demo', 'martin@zeitwert.io', '5620', 'MFH', 'T01','ST02-11','C7',1966,'Promenadenstrasse 13','5620','Bremgarten','ch','chf',12994,0,9280,1996,'N','EG: Waschküchen, UG: Kellerräume, Notstromaggregat, Entsalzungsanlage, 2 Veloräume\nWasserschaden im Keller (Mai 2013)'),
('demo', 'martin@zeitwert.io', '5620', 'Kindergarten Fuchsächer', 'T02','ST01-4','C8',1967,'Promenadenstrasse 15','5620','Bremgarten','ch','chf',1763,0,1228,2006,'N','Materialraum, Vordach aus Metall-Glas, nicht unterkellert'),
('demo', 'martin@zeitwert.io', '5620', 'Gewerbe und Garage', 'T01','ST02-11','C7',1966,'Zugerstrasse 16/18/20','5620','Bremgarten','ch','chf',10035,0,6848,1996,'N','Wohnung an der Zugerstrasse 16: Anstrich an Decke im Schlafzimmer und Stube blättert tw. ab, Umbau im 1995 - Risse wegen Erdbeben 2012, EG: Metall-Windfang - UG: WM, Tumbler, Wasseraufbereitungsanlage, Fenster mit Einfachverglasung, Veloraum - Wohnung an der Zugerstrasse 18: Küche 1995, Kühlschrank, GS, Abstellraum auf dem Balkon, Böden UG: Überzug gestrichen'),
('demo', 'martin@zeitwert.io', '5620', 'Oberer Zoll ganzes Haus', 'T01','ST05-26','C6',1723,'Obertorplatz 3','5620','Bremgarten','ch','chf',1957,0,1625,2010,'N','Nutzung als Praxis im EG, im OG Verwaltung, Umbau im 2005 - Archiv Stadtmusik - Materialraum - Wohnung im OG konnte nicht besichtigt werden\nDas Gebäude wurde im Jahr 1979 saniert.'),
('demo', 'martin@zeitwert.io', '5620', 'Zeughaus', 'T10','ST11-47','C29',1641,'Schellenhausplatz','5620','Bremgarten','ch','chf',3215,0,2148,2010,'N','-grosse Sanierung im 1979 - Teppich im OG wird ersetzt - Holztreppe im Estrich: Gelände ist mit Plexiglas gesichert - Vorhänge als Innenbeschattung - Zugang Bibliothek: Holzfassade - Treppenaufgang: Fenster tw. IV (''08) \n-Berechnungen müssen erst ab 2015 gemacht werden, nicht unterkelllert'),
('demo', 'martin@zeitwert.io', '5620', 'Schellenhaus', 'T10','ST06-36','C29',1659,'Schellenhausplatz','5620','Bremgarten','ch','chf',5672,0,4189,2007,'N','Wohnung: wird jetzt für die Leitung als Büro genutzt - UG: Küche, Holzbalkendecke, Wärmelüfter, Bürgerkeller mit Holzplatten - Sanierung im 2006 - Lagerraum, Fluchtweg - Materialaufzug, Brandschutzdecken im Estrich'),
('demo', 'martin@zeitwert.io', '5620', 'Kornhaus', 'T02','ST02-12','C9',1523,'Spiegelgasse 9','5620','Bremgarten','ch','chf',4798,0,2554,2009,'N','Putzraum, Boiler, Materialraum - Risse an den Wänden im Kniestock, nicht unterkellert'),
('demo', 'martin@zeitwert.io', '5620', 'Haus an der Reuss', 'T01','ST02-11','C7',1913,'Schulgasse 8','5620','Bremgarten','ch','chf',4121,0,2987,2010,'N','Es konnte nur die Wohnung im DG besichtigt werden, Trocknungsraum, Geräteraum, Bastellraum, Veloraum, Estrich konnte nicht besichtigt werden'),
('demo', 'martin@zeitwert.io', '5620', 'Werkhof Neubau', 'T13','ST05-31','C37',2008,'Augraben 1','5620','Bremgarten','ch','chf',3024,0,1250,2009,'N','Chemieraum, Reinigungsraum im EG, Schmutzschleuse, Werkstatt'),
('demo', 'martin@zeitwert.io', '5620', 'Bauamtsmagazin, Werkhof Altbau', 'T13','ST05-31','C37',1735,'Klosterweg 2','5620','Bremgarten','ch','chf',2404,0,560,1993,'N','Garagentor aus Holz, Lagerraum, nicht wärmegedämmt\nDer Rohbau sowie die Fassade werden aus denkmalpflegerischen und nutzungsbedingten Gründen nicht besser instandgesetzt.'),
('demo', 'martin@zeitwert.io', '5620', 'Klarakloster', 'T09','ST05-28','C29',1624,'Klosterweg 8','5620','Bremgarten','ch','chf',7525,0,4888,2002,'N','-Schmutzschleuse - Lagerraum, Werkstatt, Jugendraum, tw. Risse auf der Innenseite der Fassadenwände, Ölheizung im EG Wohnteil'),
('demo', 'martin@zeitwert.io', '5620', 'Haberhaus Polizeiposten', 'T06','ST06-35','C21',1989,'Rathausplatz 4','5620','Bremgarten','ch','chf',2532,0,2192,2010,'N','Zweistöckige Wohnung: in gutem Zustand (konnte nicht besichtigt werden) - UG: Keller, Materialraum, Boiler, Lavabo, Reinigungsraum- Wassereinbruch im Keller Mai (''13)'),
('demo', 'martin@zeitwert.io', '5620', 'Rathaus', 'T06','ST07-40','C21',1913,'Rathausplatz 1','5620','Bremgarten','ch','chf',8051,0,5235,1995,'N','Klinkerboden und Decke haben Risse - Stadtratssaal: Holz-Metallfenster 2004 - Holztüren mit Türrahmen, Holztreppen im OG, Finanzabteilung: schlechte Isolation da Zugerscheinungen im Winter und im Sommer zu heiss, Abstellkammer, Materialraum, Glasbausteine, Teeküche'),
('demo', 'martin@zeitwert.io', '5620', 'Ortsmuseum', 'T10','ST03-20','C21',1640,'Reussgasse 14A','5620','Bremgarten','ch','chf',898,0,595,2010,'N','-Risse in den Platten auf dem WC, nicht unterkellert'),
('demo', 'martin@zeitwert.io', '5620', 'Parkgarage', 'T13','ST02-14','C35',1972,'Rathausplatz 3','5620','Bremgarten','ch','chf',7905,0,4874,1995,'N','-Garageneinfahrt wegen neuer Bodenheizung undicht - Materialraum, Entsorgungsraum, Archiv, Radiatoren mit Thermostaten'),
('demo', 'martin@zeitwert.io', '5620', 'Friedhofhalle', 'T01','ST02-11','C7',1977,'Friedhofstrasse','5620','Bremgarten','ch','chf',3334,0,1928,2007,'N','-Schmutzschleuse, Sargwagen, Materialraum, nicht unterkellert'),
('demo', 'martin@zeitwert.io', '5620', 'Parkgarage', 'T13','ST02-14','C36',1985,'Obertorplatz','5620','Bremgarten','ch','chf',35355,0,11231,2010,'N','Stadt ist Mehrheitsaktionärin\nIm 4. UG Raum bei der Treppe sind Spuren von Feuchtigkeitsschäden vorhanden (bereits behoben?).\nDie 6 Oblichter über der Ausfahrt sind undicht (Hagelschäden), die Öffnungen werden zugemauert.\nTreppengeländer nicht konform\nTreppe beim Brunnen verrostet wegen der für den Brunnen verwendeten Säure\nRaum Swisscom (Unterhalt durch Swisscom)\nSchutzraum mit Küche und Wassertank\nDiv. Räume sind vermietet (und konnten tw. nicht besichtigt werden)'),

('demo', 'martin@zeitwert.io', '5425', 'Schulhaus, Zwischenbau, Pausenhalle', 'T02','ST02-12','C9',1971,'Aemmert, Schladstrasse 21','5425','Schneisingen','ch','chf',8521,0,5401,2015,'N','Turnhalle (gem. Geb.-Vers.: Mehrzweckhalle) mit Mehrzweckraum (Aemmertsaal)\nZivilschutzanlage mit Küche, Porphyrplatten\n\n*siehe Foto Dachraum\n\nAnbau: 2000, 2014(?), 2017\n\nPV-Anlage gehört nicht der Gemeinde.\nUmgebung und zusätzlich versicherte Bauteile und Elemente gem. Versicherungsausweis sind nicht in Stratus enthalten.\n\nHauswart: Alois Meier'),
('demo', 'martin@zeitwert.io', '5425', 'Mehrzweckhalle', 'T12','ST01-9','C9',1971,'Aemmert, Schladstrasse 21','5425','Schneisingen','ch','chf',6029,0,2915,2001,'N','Keine Lüftung in Duschen/Garderoben: Schimmelpilzbildung an Decke über Fenster (Oblichter)\n\n*PV-Anlage nicht im GV-Wert enthalten (Eigentümerin: IG Solar Schneisingen)');
('demo', 'martin@zeitwert.io', '5425', 'Feuerwehrlokal, Bauamt, Zivilschutzgebäude', 'T14','ST07-37','C37',1982,'Widenstrasse','5425','Schneisingen','ch','chf',5948,0,2428,2006,'N',''),

('demo', 'martin@zeitwert.io', '5626', 'Schulanlage Staffeln', 'T02','ST01-4','C9',1969,'Schulhausstrasse 6','5626','Hermetschwil','ch','chf',7672,0,5074,2002,'N','-Glasvordach, Türe Gebäuderückseite: Einfachverglasung, Schiebetüre aus Holz - Materialraum, Glastüre, Lagerraum, Geräteraum - Glasfenster werden mit der Zeit blind, Werkraum'),
('demo', 'martin@zeitwert.io', '5626', 'Werkhof mit Wohnungen', 'T13','ST05-31','C37',1988,'Im Spilhof 6','5626','Hermetschwil','ch','chf',4565,0,2739,2002,'N','-Wohnung war bis 2000 eine Kanzlei \nWäscheraum, Wasserverteilung, WM, Tumbler'),

('demo', 'martin@zeitwert.io', '7304', 'Rathaus', 'T06','ST07-40','C16',1580,'','7304','Maienfeld','ch','chf',5266,553,3775600,2006,'N',''),
('demo', 'martin@zeitwert.io', '7304', 'Alte Turnhalle', 'T12','ST01-9','C0',1939,'','7304','Maienfeld','ch','chf',7409,0,2489400,2009,'N',''),

('demo', 'martin@zeitwert.io', '8006', 'MFH Sonnegg 42 + 44', 'T01','ST02-11','C7',1952,'Sonneggstrasse 42 + 44','8006','Zürich','ch','chf',5424,369,3930,1995,'N','UG: Im Putz sind einzelne Abplatzungen und Rostflecken auf dem Boden und an den Wänden vorhanden. Zudem ist ein grösserer Riss sichtbar.\n- Im Keller dringt Wasser ein.'),

('demo', 'martin@zeitwert.io', '8022', 'SNB', 'T06','ST04-23','C22',1887,'Fraumünsterstrasse 8 und Stadthausquai 3,5,7','8022','','ch','chf',30260,0,38671,2004,'N','Elektroanlagen:\n\nStarkstromanlagen:\nBeleuchtung im Innenhof nach Wasserschaden defekt (Bodenleuchten)\n\nGem. Stefan Bauer:\n- Für die Videoüberwachung sind keine Ersatzteile mehr erhältlich!\n- Max. Lebensdauer der BMA ca. 16 Jahre\n- Die Evakuationsanlage muss bald ersetzt werden.\n\nGemäss GVZ-Ausweis: Brandmeldeanlage: CHF 284''700.-\n\nGemäss IT-Abt.:\nReinvestitionsbedarf (ca. alle 5 Jahre):\n- IT Aktivkomponenten: ca. CHF 400''000.-\n- Telefonie: ca. CHF 100''000.-'),

('demo', 'martin@zeitwert.io', '8104', 'Villa', 'T01','ST05-26','C0',1963,'Haslernstrasse 20','8104','Weiningen','ch','chf',1753,340,1940,2013,'N',''),

('demo', 'martin@zeitwert.io', '8153', 'Pavillon', 'T02','ST02-12','C9',1991,'Im Hui','8153','Rümlang','ch','chf',129,0,141,2017,'N',''),
('demo', 'martin@zeitwert.io', '8153', 'Sekundarschulhaus Rümlang', 'T02','ST02-12','C10',1957,'Katzenrütistrasse 4','8153','Rümlang','ch','chf',15732,0,14110,2017,'N','-Hauswart: Herr Buchli\n-Boden (Linol) wird erneuert dieses Jahr im Trakt A alle 4 Zimmer'),
('demo', 'martin@zeitwert.io', '8153', 'Primarschulhaus Rümlang inkl. Turnhalle', 'T02','ST02-12','C9',1915,'Katzenrütistrasse bei 6','8153','Rümlang','ch','chf',14846,0,13200,2018,'N','-Normale Zuluft in dem Geimeindesaal bei der Turnhalle\n-Neue Trennwände bei Herren Dusche/Garderobe'),

('demo', 'martin@zeitwert.io', '8355', 'Kindergarten Käsernstrasse', 'T02','ST01-4','C9',1973,'Käsernstrasse 1','8355','Aadorf','ch','chf',2957,0,1556,2011,'N',''),
('demo', 'martin@zeitwert.io', '8355', 'Altes Schulhaus Kindergarten', 'T02','ST01-4','C8',1899,'Schulstrasse 9','8355','Aadorf','ch','chf',3170,0,1708,2011,'N',''),
('demo', 'martin@zeitwert.io', '8335', 'Hermetsbüel Primar', 'T02','ST02-12','C9',1989,'Hermetsbüelweg 16','8335','Hittnau','ch','chf',7330,0,4853,2017,'N','* Risse im Mauerwerk beobachten!\n**Wassereintritt bei der Dachuntersicht, möglicherweise von der darüber liegenden Rinne.'),
('demo', 'martin@zeitwert.io', '8335', 'Mehrzweckhalle', 'T12','ST01-9','C9',2010,'Jakob Stutz-Str 60','8335','Hittnau','ch','chf',17239,0,7931,2011,'N',''),
('demo', 'martin@zeitwert.io', '8335', 'Hauswartwohnung', 'T01','ST05-26','C6',1993,'Hermetsbüelweg 8','8335','Hittnau','ch','chf',1112,0,820,2006,'N',''),
('demo', 'martin@zeitwert.io', '8335', 'Turnhalle Hermetsbüel', 'T12','ST01-9','C9',1983,'Hermetsbüelweg 24','8335','Hittnau','ch','chf',7977,0,5706,2008,'N','Gebäude wurde im 2010 umfassend saniert.\nEntlüftung WC wird saniert.'),
('demo', 'martin@zeitwert.io', '8335', 'Dorfschule', 'T02','ST02-12','C9',2003,'Hermetsbüelweg 9','8335','Hittnau','ch','chf',5912,0,4407,2003,'N',''),
('demo', 'martin@zeitwert.io', '8335', 'Kiga Oberhittnau, Wohnung/Garage', 'T02','ST01-4','C7',1879,'Wetzikerstrasse 20','8335','Hittnau','ch','chf',2321,0,1742,2004,'N','Über dem Eingang sind die Jahreszahlen 1879 und 1932 angeschrieben. Möglicherweise wurde das Schulhaus 1932 umgebaut.'),
('demo', 'martin@zeitwert.io', '8335', 'Kiga Unterhittnau und Wohnung', 'T01','ST02-11','C7',1845,'Jakob Stutz-Str. 7','8335','Hittnau','ch','chf',1449,0,1200,2012,'N','Garagen mit Flachdach ext. begrünt'),
('demo', 'martin@zeitwert.io', '8335', 'Kiga Sonne', 'T02','ST01-4','C8',1998,'Jakob Stutz-Str 7A','8335','Hittnau','ch','chf',867,0,550,2012,'N',''),
('demo', 'martin@zeitwert.io', '8335', 'Altersheim und Gartenhaus', 'T01','ST06-33','C28',1892,'Oberdorfstrsse 91','8335','Hittnau','ch','chf',1804,0,1820,2019,'N','Treppenlift,\nVentilation des Wc''s im 2. Stock ist defekt.\nDer Keller ist feucht und der Verputz ist abgeplatzt\nDas Gartenhäuschen ist in einem schlechten Zustand\nNachtrag zum Zustand während der Besichtigung: Nach Entfernung der Kletterpflanzen stürzte eine Ecke des Mauerwerks (Fassade) ein.'),
('demo', 'martin@zeitwert.io', '8335', 'Schutzraum', 'T14','ST03-19','C38',1987,'Schulhausstrasse 2 bei 2','8335','Hittnau','ch','chf',478,0,359,2015,'N',''),
('demo', 'martin@zeitwert.io', '8335', 'Friedhofsgebäude', 'T09','ST03-18','C29',1986,'Ghangetrietstrasse','8335','Hittnau','ch','chf',91,0,160,2011,'N',''),
('demo', 'martin@zeitwert.io', '8335', 'Friedhofsgebäude', 'T09','ST03-18','C29',1986,'Ghangetrietstrasse','8335','Hittnau','ch','chf',705,0,530,2011,'N','Hauswart: Herr Caminada'),
('demo', 'martin@zeitwert.io', '8335', 'Zivilschutzanlage', 'T14','ST03-19','C38',1987,'Dürstellenstrasse 8 bei 8','8335','Hittnau','ch','chf',907,0,600,2015,'N',''),
('demo', 'martin@zeitwert.io', '8335', 'Zivilschutzanlage', 'T14','ST03-19','C38',1983,'Hermetsbüel','8335','Hittnau','ch','chf',1799,0,1162,2019,'N',''),
('demo', 'martin@zeitwert.io', '8335', 'Altes Sekundarschulhaus inkl. Gerätehaus und Garagengebäude', 'T02','ST02-12','C7',1843,'Jakob Stuz-Strasse 54 bei 54','8335','Hittnau','ch','chf',1903,0,1316,2012,'N','In den Garagengebäude sowie Gerätehäuschen sind keine Sanitär-Apparate noch Sanitär-Leitungen vorhanden.'),
('demo', 'martin@zeitwert.io', '8335', 'Hermetsbüel Oberstufe', 'T02','ST02-12','C9',1961,'Hermetsbüelweg 10','8335','Hittnau','ch','chf',8453,0,6067,2017,'N','Totalsanierung 1993\nSchulküche - Metalltür verzogen, undicht.\nAnbau Spez.-Trakt 1993\n\n*Wassereintritt bei der Dachuntersicht, möglicherweise von der darüber liegenden Rinne.'),
('demo', 'martin@zeitwert.io', '8335', 'Kiga Oberdorf', 'T02','ST01-4','C8',1950,'Oberdoftstrasse 21','8335','Hittnau','ch','chf',680,0,600,2012,'N','Renovation 1999'),
('demo', 'martin@zeitwert.io', '8335', 'Schutzraum', 'T14','ST03-19','C38',1989,'Loweidstrasse','8335','Hittnau','ch','chf',436,0,389,2015,'N',''),
('demo', 'martin@zeitwert.io', '8335', 'Feuerwehrgebäude', 'T14','ST07-37','C37',1936,'Dürstelen','8335','Hittnau','ch','chf',102,0,71,2004,'N',''),
('demo', 'martin@zeitwert.io', '8335', 'Wohnhaus', 'T01','ST02-11','C7',1918,'Dürstelenstrasse 73','8335','HIttnau','ch','chf',1122,0,905,2015,'N',''),
('demo', 'martin@zeitwert.io', '8335', 'Wohnhaus', 'T01','ST02-11','C7',0,'Oberdorfstrasse 95','8335','Hittnau','ch','chf',1156,0,900,2019,'N','Im Heizraum : Feuchtigkeit von der Aussenwand'),

('demo', 'martin@zeitwert.io', '8444', 'altes Postlokal', 'T06','ST02-10','C20',0,'','8444','Henggart','ch','chf',0,0,0,0,'N',''),
('demo', 'martin@zeitwert.io', '8444', 'Kindergarten', 'T02','ST01-4','C8',1870,'Dorfstrasse 39','8444','Henggart','ch','chf',2933,0,2853,2014,'N',''),

('demo', 'martin@zeitwert.io', '8468', 'Schulhaus mit Turnhalle', 'T02','ST02-12','C9',1974,'Mülibachstrasse 18','8468','Waltalingen','ch','chf',10153,0,6680,2012,'N','- Holzboden Estrich\n- Dusche TH: CHF 70`000 für Lüftung, in Betriebnahme: 28.10.2015, Lüftungsaggregat befindet sich im ehemaligen Tankraum\n- neue Veloständer'),
('demo', 'martin@zeitwert.io', '8468', 'Haus Mülibach', 'T02','ST01-4','C8',1952,'Mülibachstrasse 20','8468','Guntalingen','ch','chf',2224,0,1453,2008,'N','- Elektroboiler für EG ist im DG\n- 2 Wohnungen im OG: Küche sehr klein und alt (''02 saniert)'),

('demo', 'martin@zeitwert.io', '8477', 'Kindergarten', 'T02','ST01-4','C8',1954,'Hanffeldstrasse 18','8477','Oberstammheim','ch','chf',789,0,456,2003,'N',''),
('demo', 'martin@zeitwert.io', '8477', 'Schulhaus', 'T02','ST02-12','C9',1868,'Hauptstrasse 15','8477','Oberstammheim','ch','chf',8114,0,5587,2008,'N','- Baujahre der drei Gebäudeteile: Schulhaus 1868 / Anbau 1930 / Anbau 1993\n- Teilsanierung 1995: Lavabo in KZ: Beleuchtung, Starkstrom\n- Verbindungsgang im UG: Sichtbetonwände und Luftschutztüre, Boden wurde neu gestrichen, da Wasser eintrat, Wand im Heizkeller: Wasserschaden\n- Treppe: Stahlträger und Glasbausteine\n- in allen Räumen LED-Lampen (ausser im Werkraum & Handarbeit)'),

('demo', 'martin@zeitwert.io', '8479', 'Laubenhuus', 'T01','ST02-11','C7',1982,'Feldistrasse 6/8','8479','Altikon','ch','chf',3490,0,2080,2003,'N',''),

('demo', 'martin@zeitwert.io', '8558', 'Mittelstufe Raperswilen Neubau', 'T02','ST02-12','C10',1965,'Schulstrasse 7','8558','Raperswilen','ch','chf',3405,0,2087,2011,'N','-WC, Garderobe und Hauswartraum wurde im 2013 renoviert'),
('demo', 'martin@zeitwert.io', '8558', 'Wohnhaus- Garage', 'T01','ST02-11','C7',1965,'Schulstrasse 7','8558','Raperswilen','ch','chf',1301,0,886,2011,'N',''),

('demo', 'martin@zeitwert.io', '8564', 'Primarschule Sonterswil  Erweiterungsbau', 'T02','ST02-12','C9',1970,'Schulstrasse 1','8564','Sonterswil','ch','chf',4808,0,2582,2011,'N','-Gemäss Hauswart ist der Dachraum trocken und ohne bekannte Mängel'),
('demo', 'martin@zeitwert.io', '8564', 'Mehrzweckhalle & Turnhalle Sonterswil', 'T12','ST01-9','C9',1970,'Schulstrasse','8564','Sonterswil','ch','chf',3008,0,1430,2011,'N',''),
('demo', 'martin@zeitwert.io', '8564', 'Turnhalle', 'T12','ST01-9','C9',0,'Schulstrasse 1','8564','Sonterswil','ch','chf',4267,0,1746,2011,'N','Wassereintritt'),
('demo', 'martin@zeitwert.io', '8564', 'Wohnhaus & Garage', 'T01','ST02-11','C7',1970,'Schulstrasse 1','8564','Sonterswil','ch','chf',1307,0,773,2011,'N','Eine Wohnung wurde neu renoviert'),

('demo', 'martin@zeitwert.io', '8588', 'Primarschulanlage Schulhaus alt', 'T02','ST02-12','C9',1887,'Kirchstrasse','8588','Zihlschlacht','ch','chf',3609,0,1877,2011,'N','Aufsteigende Feuchtigkeit im Keller'),
('demo', 'martin@zeitwert.io', '8588', 'Primarschulanlage Schulhaus Mittelbau', 'T02','ST02-12','C9',1956,'Kirchstrasse','8588','Zihlschlacht','ch','chf',3462,0,2043,2011,'N',''),
('demo', 'martin@zeitwert.io', '8588', 'Primarschulanlage Mehrzweckhalle', 'T02','ST02-12','C33',1986,'Kirchstrasse','8588','Zihlschlacht','ch','chf',7217,0,3006,2011,'N','In der Fluchtröhre sammelt sich Wasser an und fliesst nicht ab. Aula beinhaltet Audioanlage und eine Beleuchtungsanlage. Ausserdem befand sich in den Bodenhülsen der Turnhalle Wasser.'),

('demo', 'martin@zeitwert.io', '8589', 'Primarschulanlage Schulhaus', 'T02','ST02-12','C9',1912,'St. Gallerstrasse 4','8589','Sitterdorf','ch','chf',3742,0,1990,2008,'N',''),
('demo', 'martin@zeitwert.io', '8589', 'Kindergarten', 'T02','ST01-4','C8',1898,'St. Gallerstrasse 6','8589','Sitterdorf','ch','chf',1824,0,1058,2007,'N',''),
('demo', 'martin@zeitwert.io', '8589', 'Schulhaus, Garage, Gerätehaus, Containerhaus, Gargengerättehaus', 'T02','ST02-12','C9',1996,'Bruggfeld/Rofenstrasse 20','8589','Sitterdorf','ch','chf',20263,0,9695,2007,'N','Dazugehörige Umgebungsgebäude: Garagengebäude aus Betonsteinen und Pultdach aus Welleternit. Containerraum aus Holz.\nHW-Wohnung: Risse in der Aussenwand, durch mehrere Wände verlaufend. Anstrich bei der Treppe der HW-Wohnung blättert ab.\nAula: Absenkung des Bodens, Innenbeschattung defekt.\nAbstand zwischen Handlauf und Traverse ist zu gross (Sicherheitsrisiko).'),
('demo', 'martin@zeitwert.io', '8589', 'Primarschulanlage Mehrzweckgebäude', 'T02','ST02-12','C33',1992,'St. Gallerstrasse','8589','Sitterdorf','ch','chf',13247,0,5138,2011,'N','Dia Aula beinhaltet eine Stereoanlage. Die Sprossenwand in der Turnhalle ist nicht versetzt'),

('demo', 'martin@zeitwert.io', '8620', 'EFH Leukens', 'T01','ST05-26','C6',0,'Eggstrasse 68','8620','Wetzikon','ch','chf',623,0,478,2005,'N','Das Flachdach und die Fassaden wurden unlängst renoviert, jedoch nicht wärmetechnisch.\nAn der Zimmerdecke unter dem Dach sind in regelmässigen Abständen Farbveränderungen vorhanden. Diese sind wahrscheinlich auf Wärmebrücken, durch die auf die Betondecke des Flachdachs aufliegenden Betonelemente, zurückzuführen. Allenfalls könnten auch Spalten, der in die Zimmerdecke integrierten Wäremdämmplatten, dafür verantwortlich sein.\n\nDer Bastelraum im Keller ist teilweise gedämmt und weist verhältnismässig grosse Fenster auf.\n\nGemeinsamer Luftschutzraum und Tiefgarage vorhanden.'),

('demo', 'martin@zeitwert.io', '9213', 'Primarschulanlage: Schulhaus, Garage, Velounterstand', 'T02','ST02-12','C9',1959,'Hauptstrasse','9213','Hauptwil','ch','chf',16841,0,7796,2006,'N','Sicherheitsrisiko Geländer abklären und allenfalls beheben. Grosse Unterscheide im Zustand zw. Neubau und Altbau vorhanden.'),

('demo', 'martin@zeitwert.io', '9216', 'Primarschulanlage Schulhaus', 'T02','ST02-12','C9',1901,'Schulstrasse 7','9216','Hohentannen','ch','chf',2515,0,1532,2008,'N',''),

('demo', 'martin@zeitwert.io', '9220', 'Schulhaus (Altbau)', 'T02','ST02-12','C9',1899,'Hoffnungsgut','9220','Bischofszell','ch','chf',4079,0,2924,2006,'N','Feuchteschäden Boden/Wände UG mit Rostflecken. Es gab in der Vergangenheit geologische Probleme, ein geologischer Bericht ist verfügbar. Der Boden im Keller hebt sich. Einige Geländer h<90 cm. DasGebäude wurde vor wenigen Jahren komplett saniert/umgebaut.'),
('demo', 'martin@zeitwert.io', '9220', 'Schulhaus (Neubau)', 'T02','ST02-12','C9',1963,'Hoffnungsgut','9220','Bischofszell','ch','chf',12106,0,6560,2006,'N','Gebäude wurde vor wenigen Jahren komplett saniert/umgebaut'),
('demo', 'martin@zeitwert.io', '9220', 'Turnhallen, Wohnung', 'T12','ST01-9','C33',1963,'Hoffnungsgut','9220','Bischofszell','ch','chf',7282,0,3363,2006,'N',''),
('demo', 'martin@zeitwert.io', '9220', 'Garagen, Abstellgebäude', 'T13','ST01-2','C35',1963,'Hoffnungsgut','9220','Bischofszell','ch','chf',838,0,324,2006,'N',''),
('demo', 'martin@zeitwert.io', '9220', 'Kindergartenpavillon, Gartenhaus', 'T02','ST01-4','C8',2002,'Hoffnungsgut/(Thurblick)','9220','Bischoffszell','ch','chf',772,0,447,2011,'N',''),
('demo', 'martin@zeitwert.io', '9220', 'Schulhaus, Velounterstand', 'T02','ST02-12','C9',1889,'Obertor 4','9220','Bischofszell','ch','chf',7446,0,5193,2006,'N','Abklärung Asbest bei Spritzputzdecken und Geläderhöhen'),
('demo', 'martin@zeitwert.io', '9220', 'Kindergarten Bitzi, Gerätehaus', 'T02','ST01-4','C8',1899,'Turnerweg 4','9220','Bischofszell','ch','chf',2229,0,1580,2006,'N',''),
('demo', 'martin@zeitwert.io', '9220', 'Schulhaus Altbau', 'T02','ST02-12','C9',1910,'Sandbänkli 4','9220','Bischofszell','ch','chf',6488,0,3520,2006,'N',''),
('demo', 'martin@zeitwert.io', '9220', 'Aula', 'T02','ST02-12','C29',1905,'Sandbänkli 4','9220','Bischofszell','ch','chf',2242,0,1121,2011,'N',''),
('demo', 'martin@zeitwert.io', '9220', 'Garage', 'T02','ST02-12','C0',1905,'Sandbänkli','9220','Bischofszell','ch','chf',341,0,171,2011,'N',''),
('demo', 'martin@zeitwert.io', '9220', 'Primarschulanlage Schulpavillon', 'T02','ST02-12','C9',1963,'Blidegg','9220','Blidegg','ch','chf',596,0,310,2007,'N',''),
('demo', 'martin@zeitwert.io', '9220', 'Primarschulanlage Schulhaus, Velounterstand', 'T02','ST02-12','C9',1880,'Blidegg 4','9220','Blidegg','ch','chf',1241,0,857,2007,'N','Der Velounterstand besteht aus einem Metallgerüst und einem Eternitwellendach.'),
('demo', 'martin@zeitwert.io', '9220', 'Kindergarten, Spielhaus und Anbau', 'T02','ST01-4','C8',1954,'Ibergstrasse','9220','Bischofszell','ch','chf',1195,0,1050,2008,'N','Das Sielhaus der Liegenschaft ist aus Holz und hat ein Kiesdach. Der Boden ist aus Rohbeton und hat eine Beleuchtung. Es sind keine Mängel sichtbar'),
('demo', 'martin@zeitwert.io', '9220', 'Schulhaus Neubau, Pausenunterstand', 'T02','ST02-12','C9',2002,'Sandbäkli 4','9220','Bischofszell','ch','chf',11476,0,6557,2006,'N','Der Pausenunterstand besteht aus Rohbeton und hat Holzeinbau mit Verglasungen.'),
('demo', 'martin@zeitwert.io', '9220', 'Velounterstand', 'T02','ST02-12','C9',2005,'Sandbänkli','9220','Bischofszell','ch','chf',744,0,127,2011,'N',''),
('demo', 'martin@zeitwert.io', '9220', 'Schulpavillon', 'T02','ST02-12','C9',1972,'Ibergstrasse','9220','Bischofszell','ch','chf',3520,3520,1802,2006,'N','Der Dachraum konnte nicht besichtigt werden.'),
('demo', 'martin@zeitwert.io', '9220', 'Pavillon', 'T02','ST01-4','C8',1993,'Sandbänkli 4','9220','Bischofszell','ch','chf',1005,0,483,2011,'N',''),
('demo', 'martin@zeitwert.io', '9220', 'Verwaltungsgebäude', 'T06','ST05-30','C21',1901,'Sandbänkli 5','9220','Bischofszell','ch','chf',2139,0,1534,2011,'N','Dieses Gebäude steht unter Denkmalschutz.'),

('demo', 'martin@zeitwert.io', '9223', 'Primarschulanlage Halden; Schulhaus, Wohnungen', 'T02','ST02-12','C9',1917,'Schulstrasse 10','9223','Halden','ch','chf',1766,0,1078,2011,'N',''),
('demo', 'martin@zeitwert.io', '9223', 'Primarschulanlage Kenzenau; Schulhaus, Wohnungen, Garage', 'T02','ST02-12','C9',1920,'Waldplatzstrasse','9223','Schweizersholz','ch','chf',2984,0,1784,2011,'N','Die Kellerräume wurden ausgebaut jedoch besteht aufsteigende Feuchtigkeit.\nLeitungen im Bad der Wohnung frieren im Winter ein.'),

('demo', 'martin@zeitwert.io', '9225', 'Primarschulanlage Schulhaus, Turnhalle', 'T02','ST02-12','C33',1968,'Hoferberg','9225','Gotthaus','ch','chf',6425,0,3219,2007,'N','neue Beleuchtung 2012'),
('demo', 'martin@zeitwert.io', '9225', 'Primarschulanlage Schulhaus, Wohnungen', 'T02','ST02-12','C9',1914,'Hoferberg','9225','Gotthaus','ch','chf',2719,0,1597,2007,'N','Die Garagen bestehen aus Mauerwerk und einem Betonboden. Das Flachdach ist bepflanzt aber vermoost.'),

('demo', 'martin@zeitwert.io', '9470', 'Turnhalle', 'T12','ST01-9','C9',1964,'Hanflandstr. 5','9470','Buchs SG','ch','chf',5663,0,2530,2003,'N','Bühnenmaterialraum, Aussengeräteraum'),
('demo', 'martin@zeitwert.io', '9470', 'Kindergarten / HW-Wohnung', 'T02','ST01-4','C8',1964,'Hanflandstr. 3','9470','Buchs SG','ch','chf',1320,0,1020,2003,'N','nicht unterkellert, Hauswartbüro vorhanden'),
('demo', 'martin@zeitwert.io', '9470', 'Kindergarten-Pavillon Birkenau', 'T02','ST01-4','C8',1966,'Birkenaustrasse 12','9470','Buchs SG','ch','chf',1812,0,997,2012,'N','Baujahr gem. Ang. Liegenschaftenverwalter 1966, jedoch eher um 2000 bis 2012\nVermutlich wurde beim Hauptgebäude die Gebäudehülle und tw. der Innenausbau erneuert, Fundament, Böden, Innenwände und Haustechnik grösstenteils neu, tw. original'),
('demo', 'martin@zeitwert.io', '9470', 'Aula OZ Flös', 'T02','ST02-12','C9',1972,'Heldaustrasse 48','9470','Buchs SG','ch','chf',2117,0,2421,2010,'N','Rauchabzug im  EG + Saal, Archiv vorhanden'),
('demo', 'martin@zeitwert.io', '9470', 'Schulanlage / Hallenbad', 'T02','ST02-12','C9',1972,'Heldaustrasse 50','9470','Buchs SG','ch','chf',57081,0,25686,2010,'N','Boden: Klinker tw. beschädigt wird aber saniert, Kochschule + Unterrichtsraum, Materialraum , Grundwasserpumpe, Kühlung der Aula\nBaujahr gem. Ang. Liegenschaftenverwalter 1972, jedoch eher 2011'),
('demo', 'martin@zeitwert.io', '9470', 'Kindergarten-Pavillon', 'T02','ST01-4','C8',1970,'Flösweg 3','9470','Buchs SG','ch','chf',832,0,399,2012,'N',''),
('demo', 'martin@zeitwert.io', '9470', 'Schulhaus Kappeli', 'T02','ST02-12','C9',1951,'Volksgartenstrasse 12','9470','Buchs SG','ch','chf',7874,0,4735,2004,'N','Materialraum, Therapieraum, Schulküche (wird nicht mehr genutzt), Gruppenraum im UG: feucht, Hauswartraum vorhanden\nBaujahr Parkgarage: 1996'),
('demo', 'martin@zeitwert.io', '9470', 'Pavillon Hilfsschule', 'T02','ST01-4','C8',1969,'Volksgartenstrasse 12','9470','Buchs SG','ch','chf',2052,0,882,2004,'N',''),
('demo', 'martin@zeitwert.io', '9470', 'Wohnhaus (Musikschule)', 'T01','ST05-26','C9',1930,'Schulhausstrasse 4','9470','Buchs SG','ch','chf',2231,0,1441,2007,'N','Holztreppen, Geländer zu niedrig'),
('demo', 'martin@zeitwert.io', '9470', 'Schulhaus Grof', 'T02','ST02-12','C9',1919,'Schulhausstrasse 10','9470','Buchs SG','ch','chf',6964,0,4039,2011,'N','Die Höhe der Geländer ist in Ordnung, Fassade renoviert ca. 2000 - SL-Büro seit 2005, Neubauteil mit Pressspanplatten, Holztreppen verkleidet mit Pavarec, EDV Zimmer im DG mit Stahltüren, Erdverlegte Öltänke (stillgelegt) unter Pausenplatz'),
('demo', 'martin@zeitwert.io', '9470', 'OZ Grof Neubau', 'T02','ST02-12','C9',1961,'Schulhausstrasse 30','9470','Buchs SG','ch','chf',12416,0,7698,2011,'N','Glas-Metallvordach auf dem Pausenplatz - Geländer nachträglich auf richtige Höhe angepasst, Fassade: Feuchtigkeit von aussen Bereich Decke UG, Ursache Rostflecken an Untersicht SO-Ecke sollte unbedingt abgeklärt werden (allenfalls Rost von Armierungseisen: Tragsicherheit!), Notausgang Singsaal: vermutlich mangelhaft'),
('demo', 'martin@zeitwert.io', '9470', 'OZ Grof Altbau', 'T02','ST02-12','C9',1919,'Schulhausstrasse 27','9470','Buchs SG','ch','chf',5497,0,3463,2009,'N','Holzfenster haben keine Verdichtungswippen (''78) , Geländehöhe wurde aufgedoppelt,  Dachgeschoss: ehemalige Wohnung, jetzt Umnutzung für Musikunterricht'),
('demo', 'martin@zeitwert.io', '9470', 'Turnhalle Buchserbach', 'T12','ST01-9','C9',1919,'Turnhallenstrasse','9470','Buchs SG','ch','chf',4419,0,2143,2012,'N','grösserer Aussengeräteraum angebaut'),
('demo', 'martin@zeitwert.io', '9470', 'Klassenzimmer Buchserbach', 'T02','ST02-12','C9',1981,'Turnhallenstrasse 2','9470','Buchs SG','ch','chf',3807,0,2094,2012,'N','Decke: Spritzputz, Asbestabklärung notwendig, Absturzsicherung im Treppenhaus über gesamte Raumhöhe'),
('demo', 'martin@zeitwert.io', '9470', 'Altes Gewerbeschulhaus Buchserbach', 'T02','ST03-16','C9',1919,'Turnhallenstrasse 4','9470','Buchs SG','ch','chf',3268,0,1879,2012,'N','Im EDV-Raum DG: Klimagerät'),
('demo', 'martin@zeitwert.io', '9470', 'Kindergarten Altendorf', 'T02','ST01-4','C9',1919,'Kreuzgasse 14','9470','Buchs SG','ch','chf',932,0,524,2004,'N',''),
('demo', 'martin@zeitwert.io', '9470', 'Schulhaus (Neubau) Räfis', 'T02','ST02-12','C9',1971,'Churerstrasse 117','9470','Buchs SG','ch','chf',7395,0,6632,2009,'N','Die offene Fuge (s. Innenausbau Oberflächen) sollte mit eingem geeigneten Fugenmaterial gefüllt werden. Computerraum, Rauchabzugssteuerung, begehbare Wandschränke, Adresse gem. Geoportal.ch: Churerstrasse 117, nicht 119, wie bei GV-Ausweis angegeben. Baujahr gem. Ang. Liegenschaftenverwalter 1971, jedoch eher 1998'),
('demo', 'martin@zeitwert.io', '9470', 'Schulhaus (Altbau) Räfis', 'T02','ST02-12','C9',1919,'Churerstrasse 119','9470','Buchs SG','ch','chf',4127,0,2883,2009,'N','Glockenturm weitgehend im Originalzustand mit Uhrwerk vorhanden, Geländer ev. zu tief, Verbindungsleitungen'),
('demo', 'martin@zeitwert.io', '9470', 'Magazin', 'T13','ST01-2','C37',1919,'Churerstrasse 119','9470','Buchs SG','ch','chf',291,0,121,2009,'N','Adresse müsste gem. Geoportal.ch sein: Stationsstrasse, nicht Churerstrasse 119, wie bei GV-Ausweis angegeben. Baujahr gem. Ang. Liegenschaftenverwalter: 1919, vermutlich 1987'),
('demo', 'martin@zeitwert.io', '9470', 'Turnhalle', 'T12','ST01-9','C9',1951,'Churerstrasse 119','9470','Buchs SG','ch','chf',1699,0,883,2009,'N',''),
('demo', 'martin@zeitwert.io', '9470', 'Kindergarten Kappeli mit Wohnung und Garage', 'T02','ST01-4','C9',1953,'Volksgartenstrasse 1','9470','Buchs SG','ch','chf',1444,0,803,2007,'N','Aussenwand Bad: KS-Wand mit Verfärbung, Waschmaschine, Malraum, Geländerhöhe des Balkons ev. zu niedrig, Geräteraum'),
('demo', 'martin@zeitwert.io', '9470', 'Schulhaus Hanfland', 'T02','ST02-12','C9',1964,'Hanflandstr. 5','9470','Buchs SG','ch','chf',8296,0,5468,2003,'N','Das oberste Geschoss ist ein Holzbau (es wurde aufgestockt), Ausbau neuwertig, Geländerhöhe muss überprüft werden, Bibliothek, Materialraum, Malkeller, Werkraum'),

('demo', 'martin@zeitwert.io', '9471', 'Clubhaus FC Buchs', 'T12','ST03-22','C0',2008,'Rheinau','9471','Buchs SG','ch','chf',4207,0,1914,2008,'N','Restaurant mit Industrieküche'),
('demo', 'martin@zeitwert.io', '9471', 'Schiessanlage', 'T12','ST10-45','C0',1997,'Buchersplatz','9471','Buchs SG','ch','chf',6121,0,2253,2008,'N','Restaurant akustisch nicht gut.'),
('demo', 'martin@zeitwert.io', '9471', 'Freibad Rheinau', 'T12','ST05-29','C0',1978,'Buchserau','9471','Buchs SG','ch','chf',3308,0,1342,2007,'N',''),
('demo', 'martin@zeitwert.io', '9471', 'Bezirksgefängnis Wohnhaus', 'T01','ST02-11','C7',1947,'Brunnenweg 1','9471','Buchs SG','ch','chf',1097,0,822,2014,'N','Das Gebäude steht wahrscheinlich unter Schutz. Erhaltenswerte Bauteile (auch Fenster und Beschläge) sind mit der Kant. Denkmalpflege abzuklären.\nBad mit Metallfenster im UG\nAngebauter Holzschuppen'),
('demo', 'martin@zeitwert.io', '9471', 'Werkhalle (Werkhof)', 'T13','ST05-31','C37',1979,'Langäulistrasse 20','9471','Buchs SG','ch','chf',7188,0,1869,2012,'N','Die Halle ist eine Metallkonstruktion.\nDach Halle: Sandwichpaneldach'),
('demo', 'martin@zeitwert.io', '9471', 'Rathaus', 'T06','ST07-40','C22',1968,'St. Gallerstrasse 2','9471','Buchs SG','ch','chf',11240,0,8121,2014,'N','Klimagerät wurde aufgehängt im Serverraum.\nGeländer 95cm.\n1.OG: 2011 Schalter Einbau'),
('demo', 'martin@zeitwert.io', '9471', 'Wohn- und Geschäftshaus', 'T01','ST02-11','C7',1920,'St. Gallerstrasse 6','9471','Buchs SG','ch','chf',3736,0,2614,2014,'N','Das Gebäude ist zumindest teilweise erhaltenswert (auch alte Beschläge und Radiatoren) und steht möglicherweise unter Denkmalschutz.\nTrafostation im UG gehört zu den Elektrizitätswerke Buchs SG\nDas Haus ist als Musikschule vermietet.\nDIe Teppiche bestehen überall aus Kugelgarn'),
('demo', 'martin@zeitwert.io', '9471', 'Bibliothek und altes Pfarrhaus', 'T06','ST03-17','C22',1920,'Kirchgasse 2','9471','Buchs SG','ch','chf',3309,0,2234,2011,'N','Bibliothek mit Anbau von 1988'),
('demo', 'martin@zeitwert.io', '9471', 'Logopädie mit Wohnung', 'T01','ST02-11','C7',1920,'Schulhausstrasse 12','9471','Buchs SG','ch','chf',1272,0,712,2012,'N','Fenster sind niedrig eingesetzt'),
('demo', 'martin@zeitwert.io', '9471', 'Feuerwehr- & Zivilschutzanlage', 'T14','ST07-37','C37',1965,'Volksgartenstrasse 38','9471','Buchs SG','ch','chf',18049,0,7894,2009,'N','Gebäude vierteilig.\nMittelteil aus dem Jahr 1983'),
('demo', 'martin@zeitwert.io', '9471', 'Kindertagesstätte und Pavillon', 'T02','ST01-4','C8',2013,'Aeulistrasse 12','9471','Buchs SG','ch','chf',4075,0,2050,2014,'N','nicht unterkellert\nRenovation Herbst 2013\nBeim alten Hausteil fehlt ein Blitzschutz'),
('demo', 'martin@zeitwert.io', '9471', 'Internationale Schule Rheintal', 'T02','ST02-12','C9',1964,'Aeulistrasse 10','9471','Buchs SG','ch','chf',4761,0,2807,2014,'N','Schulanlage mit mehrere Gebäuden\nFenster im Treppenhaus sind nicht sicherheitskonform\nEinige Holzfenster sind veraltet\nProbleme mit Grundwasser, es wurden Betoninjektionen gemacht\nKlimagerät (Mieterausbau)\nGaragenbox'),

('demo', 'martin@zeitwert.io', '9651', 'Ferienhaus', 'T01','ST04-25','C0',1983,'Obere Laui 1665','9651','Ennetbühl','ch','chf',509,0,351,2014,'N','Wohnfläche: 105m2\nGGF: 79m2\n\n*Gemäss Nachbar asbesthaltig (das Nachbarhaus wurde gleichzeitig erstellt); Dachsanierung CHF 30''000.-'),

update obj_building b set building_nr = '0.0.0' where b.zip||'-'||b.street||'-'||b.name = '1111-Teststrasse.-Testgebäude1';
update obj_building b set building_nr = '0.0.1' where b.zip||'-'||b.street||'-'||b.name = '1111-Rathausplatz 1-Rathaus';

update obj_building b set building_nr = '01.01' where b.zip||'-'||b.street||'-'||b.name = '7304--Rathaus';
update obj_building b set building_nr = '01.02' where b.zip||'-'||b.street||'-'||b.name = '7304--Alte Turnhalle';

update obj_building b set building_nr = '02.01' where b.zip||'-'||b.street||'-'||b.name = '8253-Schulstr. 38-Kindergarten Basadingen';
update obj_building b set building_nr = '02.02' where b.zip||'-'||b.street||'-'||b.name = '8253-Schulstrasse 5-best. Schulhaus Zentrum';
update obj_building b set building_nr = '02.02.1' where b.zip||'-'||b.street||'-'||b.name = '8253-Schulstrasse 5-Sanierung Schulhaus Zentrum';
update obj_building b set building_nr = '02.02.2' where b.zip||'-'||b.street||'-'||b.name = '8253-Schulstrasse 5-Janus Schulhaus Zentrum';
update obj_building b set building_nr = '02.03' where b.zip||'-'||b.street||'-'||b.name = '8255-Bahnhofstrasse 1-Schulhaus Schlattingen';
update obj_building b set building_nr = '02.04' where b.zip||'-'||b.street||'-'||b.name = '8253-Schützenstrasse-Kindergarten Schupfenzelg';

update obj_building b set building_nr = '03.01' where b.zip||'-'||b.street||'-'||b.name = '8479-Feldistrasse 6/8-Laubenhuus';

update obj_building b set building_nr = '04.01.1' where b.zip||'-'||b.street||'-'||b.name = '8266-Feldbach-Oberstufenschulhaus Feldbach';
update obj_building b set building_nr = '04.02' where b.zip||'-'||b.street||'-'||b.name = '8266-Frauenfelderstrasse 10-Hub gelb';
update obj_building b set building_nr = '04.03' where b.zip||'-'||b.street||'-'||b.name = '8266-Seestrasse 126-Seeschulhaus';
update obj_building b set building_nr = '04.04' where b.zip||'-'||b.street||'-'||b.name = '8266-Feldbach-Sport- und Mehrzweckhalle';
update obj_building b set building_nr = '04.05.1' where b.zip||'-'||b.street||'-'||b.name = '8266-Frauenfelderstrasse 8-Hub rot';
update obj_building b set building_nr = '04.06' where b.zip||'-'||b.street||'-'||b.name = '8266-Zelgistrasse 8-Doppelkindergarten';
update obj_building b set building_nr = '04.07' where b.zip||'-'||b.street||'-'||b.name = '8266-Hubstrasse-Hub blau';

update obj_building b set building_nr = '05.01' where b.zip||'-'||b.street||'-'||b.name = '5620-Badstrasse 3-Schulhaus Isenlauf';
update obj_building b set building_nr = '05.02' where b.zip||'-'||b.street||'-'||b.name = '5620-Kapuzinerhügel 5-Kindergarten Kapuzinerhügel';
update obj_building b set building_nr = '05.03' where b.zip||'-'||b.street||'-'||b.name = '5620-Wohlerstrasse-Reussbrücke-Saal, Zwischenbau';
update obj_building b set building_nr = '05.04' where b.zip||'-'||b.street||'-'||b.name = '5620-Kreuzmattstrasse 8-Feuerwehrlokal, SR und Wg.';
update obj_building b set building_nr = '05.05' where b.zip||'-'||b.street||'-'||b.name = '5620-Wohlerstrasse-Casino';
update obj_building b set building_nr = '05.06' where b.zip||'-'||b.street||'-'||b.name = '5620-Obertorplatz-Stadtschulhaus';
update obj_building b set building_nr = '05.07' where b.zip||'-'||b.street||'-'||b.name = '5620-Promenade-Promendenschulhaus';
update obj_building b set building_nr = '05.08' where b.zip||'-'||b.street||'-'||b.name = '5620-Gartenstrasse-Gartenschulhaus';
update obj_building b set building_nr = '05.09' where b.zip||'-'||b.street||'-'||b.name = '5620-Sportstrasse-Turnhalle Bärenmatt';
update obj_building b set building_nr = '05.10' where b.zip||'-'||b.street||'-'||b.name = '5620-Klosterweg 6-Kindergarten Unterstadt';

update obj_building b set building_nr = '06.01' where b.zip||'-'||b.street||'-'||b.name = '8556-Bernrainstr. 26-Kindergarten Haldengüetli';

update obj_building b set building_nr = '07.01' where b.zip||'-'||b.street||'-'||b.name = '8355-Käsernstrasse 1-Kindergarten Käsernstrasse';
update obj_building b set building_nr = '07.02' where b.zip||'-'||b.street||'-'||b.name = '8355-Schulstrasse 9-Altes Schulhaus Kindergarten';

update obj_building b set building_nr = '08.01' where b.zip||'-'||b.street||'-'||b.name = '3032-Bergfeldstrasse 8-Wohn- und Geschäftshaus';
update obj_building b set building_nr = '08.02' where b.zip||'-'||b.street||'-'||b.name = '3032-Dorfstrasse 45-Feuerwehrmagazin/3 Wohnungen';
update obj_building b set building_nr = '08.03' where b.zip||'-'||b.street||'-'||b.name = '3034-Schulstrasse 4-Schulhaus, Pausenhalle , Kita';
update obj_building b set building_nr = '08.04' where b.zip||'-'||b.street||'-'||b.name = '3034-Schulstrasse 5-Turnhalle und Lehrschwimmbecken';
update obj_building b set building_nr = '08.05' where b.zip||'-'||b.street||'-'||b.name = '3034-Schulstrasse 6-Wohnhaus (2 Wohnungen)';
update obj_building b set building_nr = '08.06' where b.zip||'-'||b.street||'-'||b.name = '3034-Schulstrasse 7-Schulhaus';
update obj_building b set building_nr = '08.07' where b.zip||'-'||b.street||'-'||b.name = '3034-Schulstrasse 9-Schulhaus/ZS-Anlage/Verbindungsgang';
update obj_building b set building_nr = '08.08' where b.zip||'-'||b.street||'-'||b.name = '3032-Araweg 9-Jugendtreff (Pavillon)';
update obj_building b set building_nr = '08.09' where b.zip||'-'||b.street||'-'||b.name = '3032-Hofenstrasse 54-Wohnhaus (private Kindertagesstätte)';
update obj_building b set building_nr = '08.10' where b.zip||'-'||b.street||'-'||b.name = '3034-Kappelenring 36/36a-Primarschule/Doppelturnhalle/KZ/WR';
update obj_building b set building_nr = '08.11' where b.zip||'-'||b.street||'-'||b.name = '3033-Schulgasse 14-Altes Schulhaus/Doppelkindergarten/Klassenzimmer/2 Whg.';
update obj_building b set building_nr = '08.12' where b.zip||'-'||b.street||'-'||b.name = '3033-Schulgasse 14a-Garage (Nebengebäude)';
update obj_building b set building_nr = '08.13' where b.zip||'-'||b.street||'-'||b.name = '3033-Schulgasse 16-Schulhaus, Pausenhalle, ZS-Anlage';
update obj_building b set building_nr = '08.14' where b.zip||'-'||b.street||'-'||b.name = '3033-Schulgasse 18-Doppelturnhalle';
update obj_building b set building_nr = '08.15' where b.zip||'-'||b.street||'-'||b.name = '3033-Hauptstrasse 22-Aufbahrungsgebäude, Abdankungshalle';
update obj_building b set building_nr = '08.16' where b.zip||'-'||b.street||'-'||b.name = '3033-Hauptstrasse 26-Verwaltungsgebäude (Gemeindehaus)';
update obj_building b set building_nr = '08.17' where b.zip||'-'||b.street||'-'||b.name = '3034-Schützenweg 50-Schützenhaus';
update obj_building b set building_nr = '08.18' where b.zip||'-'||b.street||'-'||b.name = '3034-Murzelenstrasse 80-Wohnhaus (3 Whg.)';
update obj_building b set building_nr = '08.19' where b.zip||'-'||b.street||'-'||b.name = '3034-Murzelenstrasse 82A-Garagen/Aussengeräteraum Schule';
update obj_building b set building_nr = '08.20' where b.zip||'-'||b.street||'-'||b.name = '3034-Murzelenstrasse 82-Schulhaus/Kindergarten/Hauswartwohnung';
update obj_building b set building_nr = '08.21' where b.zip||'-'||b.street||'-'||b.name = '3034-Murzelenstrasse 84-Turnhalle';
update obj_building b set building_nr = '08.22' where b.zip||'-'||b.street||'-'||b.name = '3043-Schülerweg 11-ZS-Lager/BSA/Einstellhalle/Rasenspielfeld';
update obj_building b set building_nr = '08.23' where b.zip||'-'||b.street||'-'||b.name = '3043-Schülerweg 12-Doppelkindergarten/Musikschule/Jugendtr.';
update obj_building b set building_nr = '08.24' where b.zip||'-'||b.street||'-'||b.name = '3043-Schülerweg 13-Velounterstand';
update obj_building b set building_nr = '08.25' where b.zip||'-'||b.street||'-'||b.name = '3043-Schülerweg 14-Turnhalle';
update obj_building b set building_nr = '08.26' where b.zip||'-'||b.street||'-'||b.name = '3043-Schülerweg 15-Primarschulhaus, ged. Pausenhalle, Anbau Nord';
update obj_building b set building_nr = '08.27' where b.zip||'-'||b.street||'-'||b.name = '3043-Schülerweg 16-Turnhalle/Hauswartwhg./Heizzentrale';
update obj_building b set building_nr = '08.28' where b.zip||'-'||b.street||'-'||b.name = '3043-Schülerweg 18-Oberstufenschulhaus/Aula';
update obj_building b set building_nr = '08.29' where b.zip||'-'||b.street||'-'||b.name = '3043-Alpenblickweg 10-Velounterstand';
update obj_building b set building_nr = '08.30' where b.zip||'-'||b.street||'-'||b.name = '3049-Staatsstrasse 76-Schulhaus/Hausw.whg./Turnhalle/ZS';
update obj_building b set building_nr = '08.31' where b.zip||'-'||b.street||'-'||b.name = '3043-Lindenstrasse 4-Kulturelles Zentrum Reberhaus';
update obj_building b set building_nr = '08.32' where b.zip||'-'||b.street||'-'||b.name = '3043-Ahornweg 7-Feuerwehrmagazin/Schulungsraum';
update obj_building b set building_nr = '08.33' where b.zip||'-'||b.street||'-'||b.name = '3032-Kappelenring 34a-Kindergarten Kappelenring';

update obj_building b set building_nr = '09.01' where b.zip||'-'||b.street||'-'||b.name = '9470-Schulhausstrasse 4-Wohnhaus (Musikschule)';
update obj_building b set building_nr = '09.02' where b.zip||'-'||b.street||'-'||b.name = '9470-Schulhausstrasse 10-Schulhaus Grof';
update obj_building b set building_nr = '09.03' where b.zip||'-'||b.street||'-'||b.name = '9470-Schulhausstrasse 30-OZ Grof Neubau';
update obj_building b set building_nr = '09.04' where b.zip||'-'||b.street||'-'||b.name = '9470-Schulhausstrasse 27-OZ Grof Altbau';
update obj_building b set building_nr = '09.05' where b.zip||'-'||b.street||'-'||b.name = '9470-Turnhallenstrasse-Turnhalle Buchserbach';
update obj_building b set building_nr = '09.06' where b.zip||'-'||b.street||'-'||b.name = '9470-Turnhallenstrasse 2-Klassenzimmer Buchserbach';
update obj_building b set building_nr = '09.07' where b.zip||'-'||b.street||'-'||b.name = '9470-Turnhallenstrasse 4-Altes Gewerbeschulhaus Buchserbach';
update obj_building b set building_nr = '09.08' where b.zip||'-'||b.street||'-'||b.name = '9470-Kreuzgasse 14-Kindergarten Altendorf';
update obj_building b set building_nr = '09.09' where b.zip||'-'||b.street||'-'||b.name = '9470-Churerstrasse 117-Schulhaus (Neubau) Räfis';
update obj_building b set building_nr = '09.10' where b.zip||'-'||b.street||'-'||b.name = '9470-Churerstrasse 119-Schulhaus (Altbau) Räfis';
update obj_building b set building_nr = '09.11' where b.zip||'-'||b.street||'-'||b.name = '9470-Churerstrasse 119-Magazin';
update obj_building b set building_nr = '09.12' where b.zip||'-'||b.street||'-'||b.name = '9470-Churerstrasse 119-Turnhalle';
update obj_building b set building_nr = '09.13' where b.zip||'-'||b.street||'-'||b.name = '9470-Volksgartenstrasse 1-Kindergarten Kappeli mit Wohnung und Garage';
update obj_building b set building_nr = '09.14' where b.zip||'-'||b.street||'-'||b.name = '9470-Hanflandstr. 5-Schulhaus Hanfland';
update obj_building b set building_nr = '09.15' where b.zip||'-'||b.street||'-'||b.name = '9470-Hanflandstr. 5-Turnhalle';
update obj_building b set building_nr = '09.16' where b.zip||'-'||b.street||'-'||b.name = '9470-Hanflandstr. 3-Kindergarten / HW-Wohnung';
update obj_building b set building_nr = '09.17' where b.zip||'-'||b.street||'-'||b.name = '9470-Birkenaustrasse 12-Kindergarten-Pavillon Birkenau';
update obj_building b set building_nr = '09.18' where b.zip||'-'||b.street||'-'||b.name = '9470-Heldaustrasse 48-Aula OZ Flös';
update obj_building b set building_nr = '09.19' where b.zip||'-'||b.street||'-'||b.name = '9470-Heldaustrasse 50-Schulanlage / Hallenbad';
update obj_building b set building_nr = '09.20' where b.zip||'-'||b.street||'-'||b.name = '9470-Flösweg 3-Kindergarten-Pavillon';
update obj_building b set building_nr = '09.22' where b.zip||'-'||b.street||'-'||b.name = '9470-Volksgartenstrasse 12-Schulhaus Kappeli';
update obj_building b set building_nr = '09.23' where b.zip||'-'||b.street||'-'||b.name = '9470-Volksgartenstrasse 12-Pavillon Hilfsschule';
update obj_building b set building_nr = '09.24' where b.zip||'-'||b.street||'-'||b.name = '8444--altes Postlokal';

update obj_building b set building_nr = '10.01' where b.zip||'-'||b.street||'-'||b.name = '5620-Promenadenstrasse 13-MFH';
update obj_building b set building_nr = '10.02' where b.zip||'-'||b.street||'-'||b.name = '5620-Promenadenstrasse 15-Kindergarten Fuchsächer';
update obj_building b set building_nr = '10.03' where b.zip||'-'||b.street||'-'||b.name = '5620-Zugerstrasse 16/18/20-Gewerbe und Garage';
update obj_building b set building_nr = '10.04' where b.zip||'-'||b.street||'-'||b.name = '5620-Obertorplatz 3-Oberer Zoll ganzes Haus';
update obj_building b set building_nr = '10.05' where b.zip||'-'||b.street||'-'||b.name = '5620-Schellenhausplatz-Zeughaus';
update obj_building b set building_nr = '10.06' where b.zip||'-'||b.street||'-'||b.name = '5620-Schellenhausplatz-Schellenhaus';
update obj_building b set building_nr = '10.07' where b.zip||'-'||b.street||'-'||b.name = '5620-Augraben 1-Werkhof Neubau';
update obj_building b set building_nr = '10.08' where b.zip||'-'||b.street||'-'||b.name = '5620-Klosterweg 2-Bauamtsmagazin, Werkhof Altbau';
update obj_building b set building_nr = '10.09' where b.zip||'-'||b.street||'-'||b.name = '5620-Klosterweg 8-Klarakloster';
update obj_building b set building_nr = '10.10' where b.zip||'-'||b.street||'-'||b.name = '5620-Spiegelgasse 9-Kornhaus';
update obj_building b set building_nr = '10.11' where b.zip||'-'||b.street||'-'||b.name = '5620-Reussgasse 14A-Ortsmuseum';
update obj_building b set building_nr = '10.12' where b.zip||'-'||b.street||'-'||b.name = '5620-Rathausplatz 4-Haberhaus Polizeiposten';
update obj_building b set building_nr = '10.13' where b.zip||'-'||b.street||'-'||b.name = '5620-Rathausplatz 1-Rathaus';
update obj_building b set building_nr = '10.14' where b.zip||'-'||b.street||'-'||b.name = '5620-Rathausplatz 3-Parkgarage';
update obj_building b set building_nr = '10.15' where b.zip||'-'||b.street||'-'||b.name = '5620-Friedhofstrasse-Friedhofhalle';
update obj_building b set building_nr = '10.16' where b.zip||'-'||b.street||'-'||b.name = '5620-Schulgasse 8-Haus an der Reuss';
update obj_building b set building_nr = '10.17' where b.zip||'-'||b.street||'-'||b.name = '5626-Im Spilhof 6-Werkhof mit Wohnungen';
update obj_building b set building_nr = '10.18' where b.zip||'-'||b.street||'-'||b.name = '5620-Badstrasse 5-Sporthalle';
update obj_building b set building_nr = '10.19' where b.zip||'-'||b.street||'-'||b.name = '5626-Schulhausstrasse 6-Schulanlage Staffeln';
update obj_building b set building_nr = '10.20' where b.zip||'-'||b.street||'-'||b.name = '5620-Pfarrgasse 1-Schwarzschloss';
update obj_building b set building_nr = '10.21' where b.zip||'-'||b.street||'-'||b.name = '5620-Badstrasse 1-Dienstgebäude';

update obj_building b set building_nr = '11.01' where b.zip||'-'||b.street||'-'||b.name = '9220-Sandbänkli 4-Pavillon';
update obj_building b set building_nr = '11.02' where b.zip||'-'||b.street||'-'||b.name = '9223-Waldplatzstrasse-Primarschulanlage Kenzenau; Schulhaus, Wohnungen, Garage';
update obj_building b set building_nr = '11.03' where b.zip||'-'||b.street||'-'||b.name = '9223-Schulstrasse 10-Primarschulanlage Halden; Schulhaus, Wohnungen';
update obj_building b set building_nr = '11.04' where b.zip||'-'||b.street||'-'||b.name = '9220-Hoffnungsgut-Schulhaus (Altbau)';
update obj_building b set building_nr = '11.05' where b.zip||'-'||b.street||'-'||b.name = '9220-Hoffnungsgut-Schulhaus (Neubau)';
update obj_building b set building_nr = '11.06' where b.zip||'-'||b.street||'-'||b.name = '9220-Hoffnungsgut-Turnhallen, Wohnung';
update obj_building b set building_nr = '11.07' where b.zip||'-'||b.street||'-'||b.name = '9220-Hoffnungsgut-Garagen, Abstellgebäude';
update obj_building b set building_nr = '11.08' where b.zip||'-'||b.street||'-'||b.name = '9220-Hoffnungsgut/(Thurblick)-Kindergartenpavillon, Gartenhaus';
update obj_building b set building_nr = '11.10' where b.zip||'-'||b.street||'-'||b.name = '9220-Obertor 4-Schulhaus, Velounterstand';
update obj_building b set building_nr = '11.12' where b.zip||'-'||b.street||'-'||b.name = '9220-Turnerweg 4-Kindergarten Bitzi, Gerätehaus';
update obj_building b set building_nr = '11.14' where b.zip||'-'||b.street||'-'||b.name = '9220-Sandbänkli 4-Schulhaus Altbau';
update obj_building b set building_nr = '11.16' where b.zip||'-'||b.street||'-'||b.name = '9220-Sandbänkli 4-Aula';
update obj_building b set building_nr = '11.17' where b.zip||'-'||b.street||'-'||b.name = '9220-Sandbänkli-Velounterstand';
update obj_building b set building_nr = '11.19' where b.zip||'-'||b.street||'-'||b.name = '9220-Sandbäkli 4-Schulhaus Neubau, Pausenunterstand';
update obj_building b set building_nr = '11.20' where b.zip||'-'||b.street||'-'||b.name = '9220-Sandbänkli-Garage';
update obj_building b set building_nr = '11.21' where b.zip||'-'||b.street||'-'||b.name = '8589-Bruggfeld/Rofenstrasse 20-Schulhaus, Garage, Gerätehaus, Containerhaus, Gargengerättehaus';
update obj_building b set building_nr = '11.25' where b.zip||'-'||b.street||'-'||b.name = '9220-Ibergstrasse-Schulpavillon';
update obj_building b set building_nr = '11.26' where b.zip||'-'||b.street||'-'||b.name = '9216-Schulstrasse 7-Primarschulanlage Schulhaus';
update obj_building b set building_nr = '11.27' where b.zip||'-'||b.street||'-'||b.name = '9213-Hauptstrasse-Primarschulanlage: Schulhaus, Garage, Velounterstand';
update obj_building b set building_nr = '11.30' where b.zip||'-'||b.street||'-'||b.name = '8589-St. Gallerstrasse 6-Kindergarten';
update obj_building b set building_nr = '11.31' where b.zip||'-'||b.street||'-'||b.name = '8589-St. Gallerstrasse 4-Primarschulanlage Schulhaus';
update obj_building b set building_nr = '11.32' where b.zip||'-'||b.street||'-'||b.name = '8588-Kirchstrasse-Primarschulanlage Schulhaus alt';
update obj_building b set building_nr = '11.33' where b.zip||'-'||b.street||'-'||b.name = '8588-Kirchstrasse-Primarschulanlage Mehrzweckhalle';
update obj_building b set building_nr = '11.34' where b.zip||'-'||b.street||'-'||b.name = '8588-Kirchstrasse-Primarschulanlage Schulhaus Mittelbau';
update obj_building b set building_nr = '11.35' where b.zip||'-'||b.street||'-'||b.name = '8589-St. Gallerstrasse-Primarschulanlage Mehrzweckgebäude';
update obj_building b set building_nr = '11.36' where b.zip||'-'||b.street||'-'||b.name = '9220-Ibergstrasse-Kindergarten, Spielhaus und Anbau';
update obj_building b set building_nr = '11.38' where b.zip||'-'||b.street||'-'||b.name = '9220-Blidegg 4-Primarschulanlage Schulhaus, Velounterstand';
update obj_building b set building_nr = '11.39' where b.zip||'-'||b.street||'-'||b.name = '9220-Blidegg-Primarschulanlage Schulpavillon';
update obj_building b set building_nr = '11.41' where b.zip||'-'||b.street||'-'||b.name = '9225-Hoferberg-Primarschulanlage Schulhaus, Wohnungen';
update obj_building b set building_nr = '11.42' where b.zip||'-'||b.street||'-'||b.name = '9225-Hoferberg-Primarschulanlage Schulhaus, Turnhalle';
update obj_building b set building_nr = '11.43' where b.zip||'-'||b.street||'-'||b.name = '9220-Sandbänkli 5-Verwaltungsgebäude';

update obj_building b set building_nr = '12.01' where b.zip||'-'||b.street||'-'||b.name = '8022-Fraumünsterstrasse 8 und Stadthausquai 3,5,7-SNB';

update obj_building b set building_nr = '13.01' where b.zip||'-'||b.street||'-'||b.name = '8476-Unterdorf 8-Schulhaus und Kindergarten';
update obj_building b set building_nr = '13.02' where b.zip||'-'||b.street||'-'||b.name = '8477-Hauptstrasse 15-Schulhaus';
update obj_building b set building_nr = '13.03' where b.zip||'-'||b.street||'-'||b.name = '8468-Mülibachstrasse 18-Schulhaus mit Turnhalle';
update obj_building b set building_nr = '13.04' where b.zip||'-'||b.street||'-'||b.name = '8468-Mülibachstrasse 20-Haus Mülibach';
update obj_building b set building_nr = '13.05' where b.zip||'-'||b.street||'-'||b.name = '8476-Unterdorf 10-Wohnhaus mit Schopf';
update obj_building b set building_nr = '13.06' where b.zip||'-'||b.street||'-'||b.name = '8476-Bahnhofstrasse 8-Schulhausanlage';
update obj_building b set building_nr = '13.07' where b.zip||'-'||b.street||'-'||b.name = '8476-Bahnhofstrasse 7-Turnhalle';
update obj_building b set building_nr = '13.08' where b.zip||'-'||b.street||'-'||b.name = '8476-Bahnhofstrasse 7-Schulhausanlage';
update obj_building b set building_nr = '13.09' where b.zip||'-'||b.street||'-'||b.name = '8477-Hanffeldstrasse 18-Kindergarten';
update obj_building b set building_nr = '13.10' where b.zip||'-'||b.street||'-'||b.name = '8476-Bahnhofstrasse 8-Sporthalle';

update obj_building b set building_nr = '14.01' where b.zip||'-'||b.street||'-'||b.name = '8444-Dorfstrasse 39-Kindergarten';

update obj_building b set building_nr = '15.01' where b.zip||'-'||b.street||'-'||b.name = '8620-Eggstrasse 68-EFH Leukens';

update obj_building b set building_nr = '16.01' where b.zip||'-'||b.street||'-'||b.name = '8006-Sonneggstrasse 42 + 44-MFH Sonnegg 42 + 44';

update obj_building b set building_nr = '17.01' where b.zip||'-'||b.street||'-'||b.name = '4950-Bahnhofstrasse 2-Mehrzweckgebäude';
update obj_building b set building_nr = '17.02' where b.zip||'-'||b.street||'-'||b.name = '4950-Bahnhofstrasse 4-Wohn- und Geschäftshaus';
update obj_building b set building_nr = '17.03' where b.zip||'-'||b.street||'-'||b.name = '4950-Bahnhofstrasse 6-Wohn- und Geschäftshaus';
update obj_building b set building_nr = '17.04' where b.zip||'-'||b.street||'-'||b.name = '4950-Buchenweg 5-Wohnhaus / Scheune';
update obj_building b set building_nr = '17.05' where b.zip||'-'||b.street||'-'||b.name = '4950-Marktgasse 2-Verwaltungsgebäude';
update obj_building b set building_nr = '17.06' where b.zip||'-'||b.street||'-'||b.name = '4950-Marktgasse 4-Geschäftshaus';
update obj_building b set building_nr = '17.07' where b.zip||'-'||b.street||'-'||b.name = '4950-Marktgasse 5b-Garagen / Tankraum / Parkplatz';
update obj_building b set building_nr = '17.08' where b.zip||'-'||b.street||'-'||b.name = '4950-Oberdorfstrasse 11c-Alte Turnhalle';
update obj_building b set building_nr = '17.09' where b.zip||'-'||b.street||'-'||b.name = '4950-Oberdorfstrasse 19-Wohnhaus und Büros';
update obj_building b set building_nr = '17.10' where b.zip||'-'||b.street||'-'||b.name = '4950-Spitalstrasse 14-Wohnhaus und Vereinslokal';
update obj_building b set building_nr = '17.11' where b.zip||'-'||b.street||'-'||b.name = '4950-Spitalstrasse 54a-Magazin, Museum';
update obj_building b set building_nr = '17.12' where b.zip||'-'||b.street||'-'||b.name = '4950-Nyffel 26-Schulhaus Nyffel';
update obj_building b set building_nr = '17.13' where b.zip||'-'||b.street||'-'||b.name = '4950-Bernstrasse 78-Schulhaus HPS Schüpbach';
update obj_building b set building_nr = '17.14' where b.zip||'-'||b.street||'-'||b.name = '4950-Dornackerweg 2-Turnhalle, Sportplatz';
update obj_building b set building_nr = '17.15' where b.zip||'-'||b.street||'-'||b.name = '4950-Friedhofweg 31a/37a-Friedhof, Leichenhalle';
update obj_building b set building_nr = '17.16' where b.zip||'-'||b.street||'-'||b.name = '4950-Hofmattstrasse 5/5a-Sekundarschulhaus';
update obj_building b set building_nr = '17.17' where b.zip||'-'||b.street||'-'||b.name = '4950-Lochmühleweg 2a-Pumpwerk, Elektrizität';
update obj_building b set building_nr = '17.18' where b.zip||'-'||b.street||'-'||b.name = '4950-Marktgasse 2-Restaurant';
update obj_building b set building_nr = '17.19' where b.zip||'-'||b.street||'-'||b.name = '4950-Marktgasse 4-Verwaltungsgebäude';
update obj_building b set building_nr = '17.20' where b.zip||'-'||b.street||'-'||b.name = '4950-Neuhausstrasse 15-Schulanlage Schwarzenbach';
update obj_building b set building_nr = '17.21' where b.zip||'-'||b.street||'-'||b.name = '4950-Oberdorfstrasse 4-Berufsschulhaus';
update obj_building b set building_nr = '17.22' where b.zip||'-'||b.street||'-'||b.name = '4950-Oberdorfstrasse 5-Velokeller';
update obj_building b set building_nr = '17.23' where b.zip||'-'||b.street||'-'||b.name = '4950-Oberdorfstrasse 11a-Schulhaus Städtli';
update obj_building b set building_nr = '17.24' where b.zip||'-'||b.street||'-'||b.name = '4950-Oberdorfstrasse 13/15/15b-Werkhof';
update obj_building b set building_nr = '17.25' where b.zip||'-'||b.street||'-'||b.name = '4950-Buchenweg 6b-Wasserversorgung';
update obj_building b set building_nr = '17.26' where b.zip||'-'||b.street||'-'||b.name = '4950-Huttwilwald 1a-Pumpwerk';
update obj_building b set building_nr = '17.27' where b.zip||'-'||b.street||'-'||b.name = '4950-Dornackerweg 2a/b-Wasserversorgung';
update obj_building b set building_nr = '17.28' where b.zip||'-'||b.street||'-'||b.name = '4950-innere Schlüecht 5a-Wasserversorgung';
update obj_building b set building_nr = '17.29' where b.zip||'-'||b.street||'-'||b.name = '4950-Möhrenweid 1a-Wasserversorgung';
update obj_building b set building_nr = '17.30' where b.zip||'-'||b.street||'-'||b.name = '4950-Willimatt 2a-Wasserversorgung';
update obj_building b set building_nr = '17.31' where b.zip||'-'||b.street||'-'||b.name = '4950-Dornackerweg 6-Zivilschutzanlage';
update obj_building b set building_nr = '17.32' where b.zip||'-'||b.street||'-'||b.name = '4950-Lindenstrasse 21-Zivilschutzanlage';
update obj_building b set building_nr = '17.33' where b.zip||'-'||b.street||'-'||b.name = '4950-Hoffmattstrasse 3-Feuerwehrmagazin';
update obj_building b set building_nr = '17.34' where b.zip||'-'||b.street||'-'||b.name = '4950-Moostrasse 2a-Garage Feuerwehr';
update obj_building b set building_nr = '17.35' where b.zip||'-'||b.street||'-'||b.name = '4950-Heimstrasse 1-Kindergarten Heimstrasse';

update obj_building b set building_nr = '18.01' where b.zip||'-'||b.street||'-'||b.name = '9471-St. Gallerstrasse 2-Rathaus';
update obj_building b set building_nr = '18.02' where b.zip||'-'||b.street||'-'||b.name = '9471-St. Gallerstrasse 6-Wohn- und Geschäftshaus';
update obj_building b set building_nr = '18.03' where b.zip||'-'||b.street||'-'||b.name = '9471-Brunnenweg 1-Bezirksgefängnis Wohnhaus';
update obj_building b set building_nr = '18.04' where b.zip||'-'||b.street||'-'||b.name = '9471-Kirchgasse 2-Bibliothek und altes Pfarrhaus';
update obj_building b set building_nr = '18.05' where b.zip||'-'||b.street||'-'||b.name = '9471-Schulhausstrasse 12-Logopädie mit Wohnung';
update obj_building b set building_nr = '18.06' where b.zip||'-'||b.street||'-'||b.name = '9471-Volksgartenstrasse 38-Feuerwehr- & Zivilschutzanlage';
update obj_building b set building_nr = '18.07' where b.zip||'-'||b.street||'-'||b.name = '9471-Aeulistrasse 12-Kindertagesstätte und Pavillon';
update obj_building b set building_nr = '18.08' where b.zip||'-'||b.street||'-'||b.name = '9471-Aeulistrasse 10-Internationale Schule Rheintal';
update obj_building b set building_nr = '18.09' where b.zip||'-'||b.street||'-'||b.name = '9471-Langäulistrasse 20-Werkhalle (Werkhof)';
update obj_building b set building_nr = '18.10' where b.zip||'-'||b.street||'-'||b.name = '9471-Buchersplatz-Schiessanlage';
update obj_building b set building_nr = '18.11' where b.zip||'-'||b.street||'-'||b.name = '9471-Buchserau-Freibad Rheinau';
update obj_building b set building_nr = '18.12' where b.zip||'-'||b.street||'-'||b.name = '9471-Rheinau-Clubhaus FC Buchs';

update obj_building b set building_nr = '19.01' where b.zip||'-'||b.street||'-'||b.name = '5620-Obertorplatz-Parkgarage';

update obj_building b set building_nr = '20.01' where b.zip||'-'||b.street||'-'||b.name = '8335-Hermetsbüelweg 10-Hermetsbüel Oberstufe';
update obj_building b set building_nr = '20.02' where b.zip||'-'||b.street||'-'||b.name = '8335-Hermetsbüelweg 24-Turnhalle Hermetsbüel';
update obj_building b set building_nr = '20.03' where b.zip||'-'||b.street||'-'||b.name = '8335-Jakob Stutz-Str 60-Mehrzweckhalle';
update obj_building b set building_nr = '20.04' where b.zip||'-'||b.street||'-'||b.name = '8335-Hermetsbüelweg 8-Hauswartwohnung';
update obj_building b set building_nr = '20.05' where b.zip||'-'||b.street||'-'||b.name = '8335-Hermetsbüelweg 9-Dorfschule';
update obj_building b set building_nr = '20.06' where b.zip||'-'||b.street||'-'||b.name = '8335-Wetzikerstrasse 20-Kiga Oberhittnau, Wohnung/Garage';
update obj_building b set building_nr = '20.07' where b.zip||'-'||b.street||'-'||b.name = '8335-Jakob Stutz-Str. 7-Kiga Unterhittnau und Wohnung';
update obj_building b set building_nr = '20.08' where b.zip||'-'||b.street||'-'||b.name = '8335-Jakob Stutz-Str 7A-Kiga Sonne';
update obj_building b set building_nr = '20.09' where b.zip||'-'||b.street||'-'||b.name = '8335-Oberdoftstrasse 21-Kiga Oberdorf';
update obj_building b set building_nr = '20.10' where b.zip||'-'||b.street||'-'||b.name = '8335-Hermetsbüelweg 16-Hermetsbüel Primar';

update obj_building b set building_nr = '21.01' where b.zip||'-'||b.street||'-'||b.name = '8104-Haslernstrasse 20-Villa';

update obj_building b set building_nr = '22.01' where b.zip||'-'||b.street||'-'||b.name = '9651-Obere Laui 1665-Ferienhaus';

update obj_building b set building_nr = '23.01' where b.zip||'-'||b.street||'-'||b.name = '4303-Dorfstrasse 17-Gemeindehaus inkl. Wohnung Hauswart und Feuerwehrmagazin Dorf';
update obj_building b set building_nr = '23.05' where b.zip||'-'||b.street||'-'||b.name = '4303-Dorfstrasse 18-Turnhalle Dorf';
update obj_building b set building_nr = '23.06' where b.zip||'-'||b.street||'-'||b.name = '4303-Dorfstrasse 20-Schulhaus Dorf inkl. Blockheizkraftwerk';
update obj_building b set building_nr = '23.08' where b.zip||'-'||b.street||'-'||b.name = '4303-Kastellstrasse 3-Löwenparking';
update obj_building b set building_nr = '23.09' where b.zip||'-'||b.street||'-'||b.name = '4303-Kirchgasse-Kirchturm';
update obj_building b set building_nr = '23.10' where b.zip||'-'||b.street||'-'||b.name = '4303-Dorfstrasse 19-Kindergarten Dorf';
update obj_building b set building_nr = '23.11' where b.zip||'-'||b.street||'-'||b.name = '4303-Dorfstrasse 19-Geräteschopf';
update obj_building b set building_nr = '23.12' where b.zip||'-'||b.street||'-'||b.name = '4303-Dorfstrasse 27-Wohnhaus Dorfstr. 27';
update obj_building b set building_nr = '23.13' where b.zip||'-'||b.street||'-'||b.name = '4303-Hintere Liebrüti-Schulhaus Liebrüti';
update obj_building b set building_nr = '23.14' where b.zip||'-'||b.street||'-'||b.name = '4303-Hintere Liebrüti-Pavillon Liebrüti';
update obj_building b set building_nr = '23.15' where b.zip||'-'||b.street||'-'||b.name = '4303-Hintere Liebrüti-Turnhalle Liebrüti';
update obj_building b set building_nr = '23.16' where b.zip||'-'||b.street||'-'||b.name = '4303-Hintere Liebrüti, Böttmeweg-Gerätehaus Liebrüti';
update obj_building b set building_nr = '23.17' where b.zip||'-'||b.street||'-'||b.name = '4303-Schwarzackerstrasse 59-KIGA Schwarzackerstrasse';
update obj_building b set building_nr = '23.18' where b.zip||'-'||b.street||'-'||b.name = '4303-Liebrütistrasse 14-KIGA Liebrütistrasse';
update obj_building b set building_nr = '23.19' where b.zip||'-'||b.street||'-'||b.name = '4303-Violaweg 75-KIGA Violaweg';
update obj_building b set building_nr = '23.20' where b.zip||'-'||b.street||'-'||b.name = '4303-Rosenweg 18-KIGA Rosenweg';
update obj_building b set building_nr = '23.21' where b.zip||'-'||b.street||'-'||b.name = '4303-Im Liner, Römerweg-Sportplatz im Liner';
update obj_building b set building_nr = '23.22' where b.zip||'-'||b.street||'-'||b.name = '4303-Im Liner-(Vereinshaus Kleingärten)';
update obj_building b set building_nr = '23.23' where b.zip||'-'||b.street||'-'||b.name = '4303-Friedhof-Abdankungshalle';
update obj_building b set building_nr = '23.24' where b.zip||'-'||b.street||'-'||b.name = '4303-Friedhof-Geräteraum';
update obj_building b set building_nr = '23.25' where b.zip||'-'||b.street||'-'||b.name = '4303-Lochmatt, Strandbadweg 59-Camping, Kiosk';
update obj_building b set building_nr = '23.26' where b.zip||'-'||b.street||'-'||b.name = '4303-Lochmatt-WC-Gebäude';
update obj_building b set building_nr = '23.27' where b.zip||'-'||b.street||'-'||b.name = '4303-Lochmatt-Garderobengebäude';
update obj_building b set building_nr = '23.28' where b.zip||'-'||b.street||'-'||b.name = '4303-Lochmatt-Magazin';
update obj_building b set building_nr = '23.29' where b.zip||'-'||b.street||'-'||b.name = '4303-Lochmatt-Betriebsgebäude';
update obj_building b set building_nr = '23.30' where b.zip||'-'||b.street||'-'||b.name = '4303-Lochmatt-Chlorgasgebäude';
update obj_building b set building_nr = '23.31' where b.zip||'-'||b.street||'-'||b.name = '4303-Rinaustrasse 26-Asylbewerberunterkunft';
update obj_building b set building_nr = '23.32' where b.zip||'-'||b.street||'-'||b.name = '4303-Rinaustrasse 28-Asylbewerberunterkunft';
update obj_building b set building_nr = '23.33' where b.zip||'-'||b.street||'-'||b.name = '4303-Guggeregge 1-Werkhof';
update obj_building b set building_nr = '23.34' where b.zip||'-'||b.street||'-'||b.name = '4303-Allmendgasse 13-Wohnhaus';
update obj_building b set building_nr = '23.35' where b.zip||'-'||b.street||'-'||b.name = '4303-Kirchgasse 21-Lötscher-Scheune';
update obj_building b set building_nr = '23.36' where b.zip||'-'||b.street||'-'||b.name = '4303-Kirchgasse 19-Alterswohnungen 6 Stk. inkl. Bürgerkeller';
update obj_building b set building_nr = '23.38' where b.zip||'-'||b.street||'-'||b.name = '4303-Kirchgasse 19-Fährihäuschen / Stübli';
update obj_building b set building_nr = '23.39' where b.zip||'-'||b.street||'-'||b.name = '4303-Rheinuferweg-Fährianlagestelle';
update obj_building b set building_nr = '23.40' where b.zip||'-'||b.street||'-'||b.name = '4303-Rheinuferweg-Zollhaus';
update obj_building b set building_nr = '23.41' where b.zip||'-'||b.street||'-'||b.name = '4303-Rheinuferweg-Pumpenhaus';
update obj_building b set building_nr = '23.42' where b.zip||'-'||b.street||'-'||b.name = '4303-Rheinuferweg-Wartehäuschen Fähri / Werkhaus';
update obj_building b set building_nr = '23.43' where b.zip||'-'||b.street||'-'||b.name = '4303-Rheinuferweg-Pumpihaus Clubhaus Rheingenossen';
update obj_building b set building_nr = '23.44' where b.zip||'-'||b.street||'-'||b.name = '4303-Rheinuferweg Ergolzbucht-Bootshaus Lochmatt';
update obj_building b set building_nr = '23.45' where b.zip||'-'||b.street||'-'||b.name = '4303-Dorfstrasse 35-Landgasthof Adler + Autounterstand';
update obj_building b set building_nr = '23.46' where b.zip||'-'||b.street||'-'||b.name = '4303-Violahof-Jugend- und Kulturzentrum Violahof';
update obj_building b set building_nr = '23.47' where b.zip||'-'||b.street||'-'||b.name = '4303-Violahof-Kinderkrippe';
update obj_building b set building_nr = '23.48' where b.zip||'-'||b.street||'-'||b.name = '4303-Violahof-Jugendhaus inkl. ehemaliger Polizeiposten';
update obj_building b set building_nr = '23.50' where b.zip||'-'||b.street||'-'||b.name = '4303-Violahof-Pfadihaus';
update obj_building b set building_nr = '23.51' where b.zip||'-'||b.street||'-'||b.name = '4303-Violahof-Feuerwehrmagazin';
update obj_building b set building_nr = '23.52' where b.zip||'-'||b.street||'-'||b.name = '4303-Rifälderhübel-Waldhütte';
update obj_building b set building_nr = '23.53' where b.zip||'-'||b.street||'-'||b.name = '4303-Stelliweg-Waldhütte';
update obj_building b set building_nr = '23.54' where b.zip||'-'||b.street||'-'||b.name = '4303-Strandbad-Materialraum';

update obj_building b set building_nr = '24.01' where b.zip||'-'||b.street||'-'||b.name = '8153-Katzenrütistrasse 4-Sekundarschulhaus Rümlang';
update obj_building b set building_nr = '24.02' where b.zip||'-'||b.street||'-'||b.name = '8153-Katzenrütistrasse bei 6-Primarschulhaus Rümlang inkl. Turnhalle';
update obj_building b set building_nr = '24.03' where b.zip||'-'||b.street||'-'||b.name = '8153-Im Hui-Pavillon';

update obj_building b set building_nr = '25.01' where b.zip||'-'||b.street||'-'||b.name = '8556-Käsereistrasse 10-Primarschule Wigoltingen';
update obj_building b set building_nr = '25.02' where b.zip||'-'||b.street||'-'||b.name = '8556-Käsereistrasse 10-Mehrzweckhalle';
update obj_building b set building_nr = '25.03' where b.zip||'-'||b.street||'-'||b.name = '8556-Käsereistrasse 10-Turnhalle (Sarnahalle)';
update obj_building b set building_nr = '25.04' where b.zip||'-'||b.street||'-'||b.name = '8556-Bahnhofstrasse 40-Werkzentrum';
update obj_building b set building_nr = '25.05' where b.zip||'-'||b.street||'-'||b.name = '8556-Kirchstrasse 12a-Sekundarschulgebäude';
update obj_building b set building_nr = '25.06' where b.zip||'-'||b.street||'-'||b.name = '8556-Kirchstrasse 12a-Turnhallengebäude Sekundarschule';
update obj_building b set building_nr = '25.07' where b.zip||'-'||b.street||'-'||b.name = '8556-Kirchstrasse 10-Wohnhaus & Garage';
update obj_building b set building_nr = '25.08' where b.zip||'-'||b.street||'-'||b.name = '8556-Käsereistrasse 12-Primarschule Wigoltingen, Altbau';
update obj_building b set building_nr = '25.09' where b.zip||'-'||b.street||'-'||b.name = '8556-Bernrainstrasse 26-Kindergarten Haldengüetli, m Gge';
update obj_building b set building_nr = '25.10' where b.zip||'-'||b.street||'-'||b.name = '8564-Schulstrasse 1-Primarschule Sonterswil  Erweiterungsbau';
update obj_building b set building_nr = '25.11' where b.zip||'-'||b.street||'-'||b.name = '8564-Schulstrasse-Mehrzweckhalle & Turnhalle Sonterswil';
update obj_building b set building_nr = '25.12' where b.zip||'-'||b.street||'-'||b.name = '8564-Schulstrasse 1-Turnhalle';
update obj_building b set building_nr = '25.13' where b.zip||'-'||b.street||'-'||b.name = '8564-Schulstrasse 1-Wohnhaus & Garage';
update obj_building b set building_nr = '25.14' where b.zip||'-'||b.street||'-'||b.name = '8556-Schulstrasse 7-Primarschule Raperswilen, Altbau inkl. Whg';
update obj_building b set building_nr = '25.15' where b.zip||'-'||b.street||'-'||b.name = '8558-Schulstrasse 7-Mittelstufe Raperswilen Neubau';
update obj_building b set building_nr = '25.16' where b.zip||'-'||b.street||'-'||b.name = '8558-Schulstrasse 7-Wohnhaus- Garage';

update obj_building b set building_nr = '26.01' where b.zip||'-'||b.street||'-'||b.name = '8335-Loweidstrasse-Schutzraum';
update obj_building b set building_nr = '26.02' where b.zip||'-'||b.street||'-'||b.name = '8335-Dürstelen-Feuerwehrgebäude';
update obj_building b set building_nr = '26.03' where b.zip||'-'||b.street||'-'||b.name = '8335-Dürstelenstrasse 73-Wohnhaus';
update obj_building b set building_nr = '26.04' where b.zip||'-'||b.street||'-'||b.name = '8335-Oberdorfstrasse 95-Wohnhaus';
update obj_building b set building_nr = '26.05' where b.zip||'-'||b.street||'-'||b.name = '8335-Oberdorfstrsse 91-Altersheim und Gartenhaus';
update obj_building b set building_nr = '26.06' where b.zip||'-'||b.street||'-'||b.name = '8335-Schulhausstrasse 2 bei 2-Schutzraum';
update obj_building b set building_nr = '26.07' where b.zip||'-'||b.street||'-'||b.name||'-'||b.insured_value = '8335-Ghangetrietstrasse-Friedhofsgebäude-160';
update obj_building b set building_nr = '26.08' where b.zip||'-'||b.street||'-'||b.name||'-'||b.insured_value = '8335-Ghangetrietstrasse-Friedhofsgebäude-530';
update obj_building b set building_nr = '26.09' where b.zip||'-'||b.street||'-'||b.name = '8335-Dürstellenstrasse 8 bei 8-Zivilschutzanlage';
update obj_building b set building_nr = '26.10' where b.zip||'-'||b.street||'-'||b.name = '8335-Hermetsbüel-Zivilschutzanlage';
update obj_building b set building_nr = '26.11' where b.zip||'-'||b.street||'-'||b.name = '8335-Jakob Stuz-Strasse 54 bei 54-Altes Sekundarschulhaus inkl. Gerätehaus und Garagengebäude';

update obj_building b set building_nr = '27.01' where b.zip||'-'||b.street||'-'||b.name = '5425-Widenstrasse-Feuerwehrlokal, Bauamt, Zivilschutzgebäude';
update obj_building b set building_nr = '27.02' where b.zip||'-'||b.street||'-'||b.name = '5425-Aemmert, Schladstrasse 21-Schulhaus, Zwischenbau, Pausenhalle';
update obj_building b set building_nr = '27.03' where b.zip||'-'||b.street||'-'||b.name = '5425-Aemmert, Schladstrasse 21-Mehrzweckhalle';

update obj_building b set building_nr = '28.01' where b.zip||'-'||b.street||'-'||b.name = '5318-Hauptstrasse 108-MFH';
update obj_building b set building_nr = '28.02' where b.zip||'-'||b.street||'-'||b.name = '5318-Mitteldorf 45-Scheune';
update obj_building b set building_nr = '28.03' where b.zip||'-'||b.street||'-'||b.name = '5318-Hauptstrasse 46-EFH';
update obj_building b set building_nr = '28.04' where b.zip||'-'||b.street||'-'||b.name = '5318-Mitteldorf 47-Schopf mit Garage';
