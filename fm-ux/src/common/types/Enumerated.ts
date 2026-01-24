/**
 * Matches existing fm-ui Enumerated type for API compatibility.
 * Used for code tables and entity references.
 */
export interface Enumerated {
	id: string;
	name: string;
	itemType?: Enumerated; // Note: itemType is itself an Enumerated (id + name)
}
