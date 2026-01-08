const fs = require('fs');
const path = require('path');

// File paths
const buildingDataFile = path.join(__dirname, 'R__2906_building_data_01_golden.sql');
const elementRatingsFile = path.join(__dirname, 'R__2906_building_data_02_golden.sql');
const outputFile = path.join(__dirname, 'DemoBuildingData.kt');

// Read all files
const buildingDataContent = fs.readFileSync(buildingDataFile, 'utf8');
const elementRatingsContent = fs.readFileSync(elementRatingsFile, 'utf8');

// Data structures
const buildings = {};
const buildingNumbers = {};
const elementRatings = {};

/**
 * Parse building INSERT statements from file 01
 * Format: ('demo', 'martin@zeitwert.io', 'account', 'name', 'type','subtype','catalog',year,'street','zip','city','country','currency',volume,area_gross,insured_value,insured_value_year,'strategy','description')
 */
function parseBuildings(content) {
	// Split content into lines and process each INSERT statement
	const lines = content.split('\n');
	let currentInsert = '';

	for (const line of lines) {
		const trimmed = line.trim();

		// Skip empty lines and comments
		if (!trimmed || trimmed.startsWith('--')) continue;

		// Skip non-insert lines that aren't continuations
		if (!trimmed.startsWith("('demo',") && !currentInsert) continue;

		// Start or continue an insert
		if (trimmed.startsWith("('demo',")) {
			currentInsert = trimmed;
		} else if (currentInsert) {
			currentInsert += ' ' + trimmed;
		}

		// Check if insert is complete (ends with ; or ,)
		if (currentInsert && (currentInsert.endsWith('),') || currentInsert.endsWith(');'))) {
			parseOneBuildingInsert(currentInsert);
			currentInsert = '';
		}
	}
}

function parseOneBuildingInsert(insertLine) {
	// Remove trailing ), or );
	let line = insertLine.replace(/\),?;?$/, '');
	// Remove leading ('demo', 'martin@zeitwert.io', 
	line = line.replace(/^\('demo',\s*'martin@zeitwert\.io',\s*/, '');

	// Now we need to parse: 'account', 'name', 'type','subtype','catalog',year,'street','zip','city','country','currency',volume,area_gross,insured_value,insured_value_year,'strategy','description'
	// This is tricky because description can contain commas and quotes

	const values = [];
	let current = '';
	let inQuote = false;
	let i = 0;

	while (i < line.length) {
		const char = line[i];

		if (char === "'" && !inQuote) {
			inQuote = true;
			i++;
			continue;
		}

		if (char === "'" && inQuote) {
			// Check for escaped quote ''
			if (i + 1 < line.length && line[i + 1] === "'") {
				current += "'";
				i += 2;
				continue;
			}
			// End of quoted string
			inQuote = false;
			values.push(current);
			current = '';
			i++;
			// Skip comma after quote
			while (i < line.length && (line[i] === ',' || line[i] === ' ')) i++;
			continue;
		}

		if (char === ',' && !inQuote) {
			if (current.trim()) {
				values.push(current.trim());
			}
			current = '';
			i++;
			continue;
		}

		current += char;
		i++;
	}

	// Don't forget last value
	if (current.trim()) {
		values.push(current.trim());
	}

	if (values.length < 17) {
		console.error('Failed to parse building insert, got', values.length, 'values:', values);
		return;
	}

	const [account, name, buildingType, buildingSubType, partCatalog, buildingYear, street, zip, city, country, currency, volume, areaGross, insuredValue, insuredValueYear, maintenanceStrategy, description] = values;

	const key = `${zip}|${street}|${name}|${volume}`;

	buildings[key] = {
		account,
		name,
		buildingType,
		buildingSubType,
		partCatalog,
		buildingYear: parseInt(buildingYear) || 0,
		street,
		zip,
		city,
		country,
		currency,
		volume: parseInt(volume) || 0,
		areaGross: parseInt(areaGross) || 0,
		insuredValue: parseInt(insuredValue) || 0,
		insuredValueYear: parseInt(insuredValueYear) || 0,
		maintenanceStrategy,
		description: description || '',
		elements: []
	};
}

/**
 * Parse UPDATE statements for building numbers
 * Format: update obj_building b set building_nr = '08.01' where b.zip||'-'||b.street||'-'||b.name = '3032-Bergfeldstrasse 8-Wohn- und Geschäftshaus';
 */
function parseBuildingNumbers(content) {
	const regex = /update obj_building b set building_nr = '([^']+)' where b\.zip\|\|'-'\|\|b\.street\|\|'-'\|\|b\.name = '([^']+)';/g;
	let match;

	while ((match = regex.exec(content)) !== null) {
		const buildingNr = match[1];
		const whereKey = match[2]; // format: zip-street-name
		buildingNumbers[whereKey] = buildingNr;
	}
}

/**
 * Parse element rating INSERT statements
 * Format: (nextval('obj_part_id_seq'), 'building.elementRatingList', (select min(id) from obj_building_v where zip||'|'||street||'|'||name||'|'||volume = 'key'), 'part_id', weight, condition, year,'description')
 */
function parseElementRatings(content) {
	// Split by lines and process each INSERT
	const lines = content.split('\n');

	for (const line of lines) {
		const trimmed = line.trim();
		if (!trimmed.startsWith('(nextval(')) continue;

		// Extract building key from subquery - note the escaped pipes in the regex
		const keyMatch = trimmed.match(/volume = '([^']+)'\)/);
		if (!keyMatch) {
			console.log('No key match for line:', trimmed.substring(0, 100));
			continue;
		}

		const buildingKey = keyMatch[1];

		// Extract element data after the subquery
		// Format after subquery: ), 'part_id', weight, condition, year,'description'),
		const afterSubquery = trimmed.substring(trimmed.indexOf("'), '") + 4);

		// Parse: 'part_id', weight, condition, year,'description'),
		// Handle descriptions with escaped single quotes ''
		const elementMatch = afterSubquery.match(/'([^']+)',\s*(\d+),\s*(\d+),\s*(\d+),\s*'((?:[^']|'')*)'\)?[,;]?/);
		if (!elementMatch) {
			console.log('No element match for:', afterSubquery.substring(0, 100));
			continue;
		}

		const [, partId, weight, condition, year, description] = elementMatch;
		addElementRating(buildingKey, partId, parseInt(weight), parseInt(condition), parseInt(year), description.replace(/''/g, "'"));
	}
}

function addElementRating(buildingKey, partId, weight, condition, year, description) {
	if (!elementRatings[buildingKey]) {
		elementRatings[buildingKey] = [];
	}

	elementRatings[buildingKey].push({
		partId,
		weight,
		condition,
		year,
		description: description || ''
	});
}

/**
 * Escape string for Kotlin
 */
function escapeKotlin(str) {
	if (!str) return '';
	// First handle <br> tags -> \n
	let result = str
		.replace(/<br>/gi, '\\n')
		.replace(/<br\/>/gi, '\\n');

	// Escape double quotes
	result = result.replace(/"/g, '\\"');

	// The SQL already has \n as literal escape sequences, keep them as-is
	// Don't double-escape backslashes that are part of \n

	return result;
}

/**
 * Generate Kotlin DSL code
 */
function generateKotlin() {
	// Group buildings by account
	const buildingsByAccount = {};

	for (const [key, building] of Object.entries(buildings)) {
		const account = building.account;
		if (!buildingsByAccount[account]) {
			buildingsByAccount[account] = [];
		}

		// Find building number
		const whereKey = `${building.zip}-${building.street}-${building.name}`;
		building.buildingNr = buildingNumbers[whereKey] || null;

		// Attach element ratings
		building.elements = elementRatings[key] || [];

		buildingsByAccount[account].push(building);
	}

	// Generate Kotlin code
	let kotlin = `package io.zeitwert.config.data

import io.zeitwert.config.dsl.AccountContext

/**
 * Building demo data for DemoDataSetup.
 * This file contains building definitions for each account, organized by account key.
 * 
 * Generated from SQL files - do not edit manually.
 */

`;

	// Sort accounts
	const sortedAccounts = Object.keys(buildingsByAccount).sort();

	for (const account of sortedAccounts) {
		const accountBuildings = buildingsByAccount[account];
		const city = accountBuildings[0]?.city || account;

		kotlin += `/** Buildings for account ${account} - ${city} */\n`;
		kotlin += `fun AccountContext.buildings${account}() {\n`;

		for (const building of accountBuildings) {
			kotlin += generateBuildingDsl(building);
		}

		kotlin += `}\n\n`;
	}

	return kotlin;
}

function generateBuildingDsl(building) {
	let dsl = `\tbuilding("${escapeKotlin(building.name)}", "${escapeKotlin(building.street)}", "${building.zip}", "${escapeKotlin(building.city)}") {\n`;

	// Optional properties
	if (building.buildingNr) {
		dsl += `\t\tbuildingNr = "${building.buildingNr}"\n`;
	}
	if (building.buildingType) {
		dsl += `\t\tbuildingType = "${building.buildingType}"\n`;
	}
	if (building.buildingSubType) {
		dsl += `\t\tbuildingSubType = "${building.buildingSubType}"\n`;
	}
	if (building.buildingYear > 0) {
		dsl += `\t\tbuildingYear = ${building.buildingYear}\n`;
	}
	if (building.volume > 0) {
		dsl += `\t\tvolume = ${building.volume}\n`;
	}
	if (building.areaGross > 0) {
		dsl += `\t\tareaGross = ${building.areaGross}\n`;
	}
	if (building.insuredValue > 0) {
		dsl += `\t\tinsuredValue = ${building.insuredValue}\n`;
	}
	if (building.insuredValueYear > 0) {
		dsl += `\t\tinsuredValueYear = ${building.insuredValueYear}\n`;
	}
	if (building.description) {
		const escapedDesc = escapeKotlin(building.description);
		if (escapedDesc.length > 80) {
			dsl += `\t\tdescription =\n\t\t\t"${escapedDesc}"\n`;
		} else {
			dsl += `\t\tdescription = "${escapedDesc}"\n`;
		}
	}

	// Rating with elements
	if (building.elements.length > 0) {
		// Get rating year from first element
		const ratingYear = building.elements[0]?.year || 2012;
		dsl += `\t\trating(${ratingYear}, "${building.partCatalog}", "${building.maintenanceStrategy}") {\n`;

		for (const element of building.elements) {
			const desc = escapeKotlin(element.description);
			if (desc && desc.length > 60) {
				dsl += `\t\t\telement(\n\t\t\t\t"${element.partId}",\n\t\t\t\t${element.weight},\n\t\t\t\t${element.condition},\n\t\t\t\t"${desc}",\n\t\t\t)\n`;
			} else if (desc) {
				dsl += `\t\t\telement("${element.partId}", ${element.weight}, ${element.condition}, "${desc}")\n`;
			} else {
				dsl += `\t\t\telement("${element.partId}", ${element.weight}, ${element.condition})\n`;
			}
		}

		dsl += `\t\t}\n`;
	}

	dsl += `\t}\n`;
	return dsl;
}

// Main execution
console.log('Parsing building data...');
parseBuildings(buildingDataContent);
console.log(`Found ${Object.keys(buildings).length} buildings`);

console.log('Parsing building numbers...');
parseBuildingNumbers(buildingDataContent);
console.log(`Found ${Object.keys(buildingNumbers).length} building numbers`);

console.log('Parsing element ratings...');
parseElementRatings(elementRatingsContent);

let totalElements = 0;
for (const elements of Object.values(elementRatings)) {
	totalElements += elements.length;
}
console.log(`Found ${totalElements} element ratings for ${Object.keys(elementRatings).length} buildings`);

console.log('Generating Kotlin DSL...');
const kotlinCode = generateKotlin();

console.log(`Writing to ${outputFile}...`);
fs.writeFileSync(outputFile, kotlinCode, 'utf8');

console.log('Done!');

// Print summary by account
console.log('\nSummary by account:');
const buildingsByAccount = {};
for (const building of Object.values(buildings)) {
	if (!buildingsByAccount[building.account]) {
		buildingsByAccount[building.account] = 0;
	}
	buildingsByAccount[building.account]++;
}
for (const [account, count] of Object.entries(buildingsByAccount).sort()) {
	console.log(`  ${account}: ${count} buildings`);
}

