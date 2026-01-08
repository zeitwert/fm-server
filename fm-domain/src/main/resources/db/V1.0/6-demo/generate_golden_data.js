/**
 * Script to generate R__2906_building_data_02_golden.sql
 * 
 * This script:
 * 1. Parses R__2906_building_data_01_golden.sql to extract building keys (zip|street|name|volume)
 * 2. Filters R__2906_building_data_02.sql to only include entries for those buildings
 * 3. Outputs R__2906_building_data_02_golden.sql with entries grouped by building
 */

const fs = require('fs');
const path = require('path');

const SCRIPT_DIR = __dirname;
const GOLDEN_FILE = path.join(SCRIPT_DIR, 'R__2906_building_data_01_golden.sql');
const DATA_FILE = path.join(SCRIPT_DIR, 'R__2906_building_data_02.sql');
const OUTPUT_FILE = path.join(SCRIPT_DIR, 'R__2906_building_data_02_golden.sql');

/**
 * Parse the golden file to extract building keys from insert statements.
 * Format: zip|street|name|volume
 */
function extractGoldenBuildingKeys(goldenContent) {
    const keys = new Set();
    
    // Match each row in the insert statement: ('value1', 'value2', ...)
    // The insert format is:
    // insert into migr_obj_building_v(tenant, owner, account, name, building_type_id, building_sub_type_id, 
    //   building_part_catalog_id, building_year, street, zip, city, country_id, currency_id, volume, ...)
    // Field indices (0-based): name=3, street=8, zip=9, volume=13
    
    const rowPattern = /\(([^)]+)\)/g;
    let match;
    
    while ((match = rowPattern.exec(goldenContent)) !== null) {
        const rowContent = match[1];
        
        // Skip if this doesn't look like a building insert row (should start with 'demo')
        if (!rowContent.trim().startsWith("'demo'")) {
            continue;
        }
        
        // Parse the fields - need to handle quoted strings with commas
        const fields = parseCSVFields(rowContent);
        
        if (fields.length >= 14) {
            // Extract: name (index 3), street (index 8), zip (index 9), volume (index 13)
            const name = unquote(fields[3]);
            const street = unquote(fields[8]);
            const zip = unquote(fields[9]);
            const volume = unquote(fields[13]);
            
            const key = `${zip}|${street}|${name}|${volume}`;
            keys.add(key);
        }
    }
    
    return keys;
}

/**
 * Parse CSV-like fields, handling quoted strings that may contain commas
 */
function parseCSVFields(content) {
    const fields = [];
    let current = '';
    let inQuote = false;
    let i = 0;
    
    while (i < content.length) {
        const char = content[i];
        
        if (char === "'" && !inQuote) {
            inQuote = true;
            current += char;
        } else if (char === "'" && inQuote) {
            // Check for escaped quote ('')
            if (i + 1 < content.length && content[i + 1] === "'") {
                current += "''";
                i++;
            } else {
                inQuote = false;
                current += char;
            }
        } else if (char === ',' && !inQuote) {
            fields.push(current.trim());
            current = '';
        } else {
            current += char;
        }
        i++;
    }
    
    if (current.trim()) {
        fields.push(current.trim());
    }
    
    return fields;
}

/**
 * Remove surrounding quotes from a string
 */
function unquote(str) {
    if (!str) return str;
    str = str.trim();
    if (str.startsWith("'") && str.endsWith("'")) {
        return str.slice(1, -1).replace(/''/g, "'");
    }
    return str;
}

/**
 * Extract building key from a data file entry line
 * Pattern: where zip||'|'||street||'|'||name||'|'||volume = 'KEY'
 */
function extractKeyFromDataLine(line) {
    const match = line.match(/where zip\|\|'\|'\|\|street\|\|'\|'\|\|name\|\|'\|'\|\|volume = '([^']+)'/);
    if (match) {
        return match[1];
    }
    return null;
}

/**
 * Parse the data file and group entries by building key
 */
function parseDataFile(dataContent, goldenKeys) {
    const lines = dataContent.split('\n');
    const entriesByBuilding = new Map();
    const filteredEntries = [];
    
    let currentInsertHeader = null;
    let inInsertBlock = false;
    
    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const trimmedLine = line.trim();
        
        // Check for insert statement header
        if (trimmedLine.startsWith('insert into obj_building_part_element_rating')) {
            currentInsertHeader = line;
            inInsertBlock = true;
            continue;
        }
        
        // Check for entry lines (start with '(')
        if (inInsertBlock && trimmedLine.startsWith('(')) {
            const key = extractKeyFromDataLine(line);
            if (key && goldenKeys.has(key)) {
                if (!entriesByBuilding.has(key)) {
                    entriesByBuilding.set(key, []);
                }
                entriesByBuilding.get(key).push(line);
            }
        }
        
        // End of insert block
        if (inInsertBlock && trimmedLine === '' || trimmedLine.startsWith('commit')) {
            inInsertBlock = false;
        }
    }
    
    return entriesByBuilding;
}

/**
 * Generate the output SQL file
 */
function generateOutputSQL(entriesByBuilding) {
    const lines = [];
    
    // Header
    lines.push('');
    lines.push('delete from obj_building_part_element_rating;');
    lines.push('');
    lines.push('commit;');
    lines.push('');
    
    // Sort buildings by key for consistent output
    const sortedKeys = Array.from(entriesByBuilding.keys()).sort();
    
    // Group entries into batches (to avoid too large INSERT statements)
    const BATCH_SIZE = 50;
    let currentBatch = [];
    let isFirstBatch = true;
    
    for (const key of sortedKeys) {
        const entries = entriesByBuilding.get(key);
        
        // Add comment for building
        if (currentBatch.length > 0 && currentBatch.length + entries.length > BATCH_SIZE) {
            // Flush current batch
            lines.push('insert into obj_building_part_element_rating(id,part_list_type_id,obj_id,building_part_id,weight,condition,condition_year,description) values');
            for (let i = 0; i < currentBatch.length; i++) {
                let entryLine = currentBatch[i];
                // Remove trailing comma if present, then add comma or semicolon
                entryLine = entryLine.replace(/,\s*$/, '');
                if (i < currentBatch.length - 1) {
                    lines.push(entryLine + ',');
                } else {
                    lines.push(entryLine + ';');
                }
            }
            lines.push('');
            currentBatch = [];
        }
        
        // Add entries for this building
        for (const entry of entries) {
            currentBatch.push(entry);
        }
    }
    
    // Flush remaining batch
    if (currentBatch.length > 0) {
        lines.push('insert into obj_building_part_element_rating(id,part_list_type_id,obj_id,building_part_id,weight,condition,condition_year,description) values');
        for (let i = 0; i < currentBatch.length; i++) {
            let entryLine = currentBatch[i];
            // Remove trailing comma if present
            entryLine = entryLine.replace(/,\s*$/, '');
            if (i < currentBatch.length - 1) {
                lines.push(entryLine + ',');
            } else {
                lines.push(entryLine + ';');
            }
        }
        lines.push('');
    }
    
    lines.push('commit;');
    lines.push('');
    
    return lines.join('\n');
}

// Main execution
function main() {
    console.log('Reading golden file:', GOLDEN_FILE);
    const goldenContent = fs.readFileSync(GOLDEN_FILE, 'utf8');
    
    console.log('Extracting building keys from golden file...');
    const goldenKeys = extractGoldenBuildingKeys(goldenContent);
    console.log(`Found ${goldenKeys.size} golden buildings:`);
    for (const key of Array.from(goldenKeys).sort()) {
        console.log(`  - ${key}`);
    }
    
    console.log('\nReading data file:', DATA_FILE);
    const dataContent = fs.readFileSync(DATA_FILE, 'utf8');
    
    console.log('Filtering entries for golden buildings...');
    const entriesByBuilding = parseDataFile(dataContent, goldenKeys);
    
    let totalEntries = 0;
    for (const [key, entries] of entriesByBuilding) {
        totalEntries += entries.length;
        console.log(`  - ${key}: ${entries.length} entries`);
    }
    console.log(`Total: ${totalEntries} entries for ${entriesByBuilding.size} buildings`);
    
    console.log('\nGenerating output file:', OUTPUT_FILE);
    const outputSQL = generateOutputSQL(entriesByBuilding);
    fs.writeFileSync(OUTPUT_FILE, outputSQL, 'utf8');
    
    console.log('Done!');
}

main();

