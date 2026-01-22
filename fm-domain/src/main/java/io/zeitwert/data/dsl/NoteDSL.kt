package io.zeitwert.data.dsl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import kotlin.random.Random

/**
 * DSL for creating notes using Spring repositories.
 *
 * This DSL uses the repository layer to create notes, which ensures proper domain logic
 * is executed.
 *
 * Usage:
 * ```
 * Note.init(directory)
 * Note.attachRandomNotes(building, userId, 1..5)
 * ```
 */
object Note {

	lateinit var directory: RepositoryDirectory

	val noteRepository: ObjNoteRepository
		get() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	fun init(directory: RepositoryDirectory) {
		this.directory = directory
	}

	/**
	 * Attach random notes to any item that implements ItemWithNotes.
	 *
	 * @param item The item to attach notes to (Building, Account, or Contact)
	 * @param userId The user ID for note creation
	 * @param countRange The range for random number of notes (default 1..5)
	 */
	fun attachRandomNotes(
		item: ItemWithNotes,
		userId: Any,
		countRange: IntRange = 1..5,
	) {
		val count = countRange.random()
		repeat(count) {
			val noteType = randomNoteType()
			val note = item.addNote(noteType, userId)
			initRandomNote(note, noteType)
			noteRepository.store(note)
		}
	}

	private fun initRandomNote(
		note: ObjNote,
		noteType: CodeNoteType,
	) {
		val (subject, content) = when (noteType) {
			CodeNoteType.NOTE -> randomLocalNote()
			CodeNoteType.CALL -> randomCallNote()
			else -> randomLocalNote()
		}
		note.subject = subject
		note.content = content
		note.isPrivate = Random.nextBoolean()
	}

	private fun randomNoteType(): CodeNoteType =
		if (Random.nextBoolean()) CodeNoteType.NOTE else CodeNoteType.CALL

	private fun randomLocalNote(): Pair<String, String> {
		val notes = listOf(
			"Dach-Inspektion erforderlich" to
				"Bei der letzten Begehung wurden lose Ziegel festgestellt. Eine detaillierte Inspektion durch einen Dachdecker wird empfohlen.",
			"Heizungswartung abgeschlossen" to
				"Die jährliche Heizungswartung wurde durchgeführt. Alle Komponenten funktionieren einwandfrei. Nächste Wartung in 12 Monaten.",
			"Fassadenrisse dokumentiert" to
				"An der Nordseite wurden mehrere Haarrisse in der Fassade festgestellt. Fotos wurden aufgenommen und archiviert.",
			"Energieausweis erneuert" to
				"Der Energieausweis wurde aktualisiert. Energieeffizienzklasse C. Gültig bis 2034.",
			"Parkplatzmarkierung erneuern" to
				"Die Parkplatzmarkierungen sind verblasst und sollten im Frühjahr erneuert werden.",
			"Aufzugsprüfung bestanden" to
				"Die TÜV-Prüfung des Aufzugs wurde erfolgreich abgeschlossen. Prüfplakette bis 2026 gültig.",
			"Wasserschaden im Keller" to
				"Nach starkem Regen wurde Feuchtigkeit im Kellerbereich festgestellt. Drainage überprüfen.",
			"Brandschutzübung durchgeführt" to
				"Die jährliche Brandschutzübung wurde mit allen Mietern durchgeführt. Protokoll liegt vor.",
			"Gartenarbeiten geplant" to
				"Für das Frühjahr sind umfangreiche Gartenarbeiten geplant: Hecken schneiden, Rasen vertikutieren.",
			"Schliesssystem modernisieren" to
				"Das mechanische Schliesssystem sollte auf ein elektronisches System umgestellt werden.",
		)
		return notes.random()
	}

	private fun randomCallNote(): Pair<String, String> {
		val calls = listOf(
			"Telefonat mit Hauswart" to
				"Besprochen: Reinigungsintervalle, Winterdienst-Bereitschaft, defekte Aussenbeleuchtung.",
			"Rückruf Versicherung" to
				"Schadensmeldung besprochen. Gutachter kommt nächste Woche. Referenznummer erhalten.",
			"Anfrage Handwerker" to
				"Offerte für Malerarbeiten angefordert. Termin für Besichtigung vereinbart.",
			"Mieteranfrage beantwortet" to
				"Fragen zur Nebenkostenabrechnung geklärt. Mieter zufrieden mit Erklärung.",
			"Termin Gemeinde vereinbart" to
				"Besprechung wegen Baubewilligung für Dachausbau. Termin: nächsten Montag 10:00 Uhr.",
			"Lieferant kontaktiert" to
				"Bestellung für Ersatzteile Heizung aufgegeben. Lieferzeit ca. 2 Wochen.",
			"Notfall-Hotline informiert" to
				"Neue Notfallnummer für Wochenenden mitgeteilt. Liste wird aktualisiert.",
			"Architekt konsultiert" to
				"Erste Einschätzung für Sanierungsprojekt eingeholt. Detailofferte folgt.",
		)
		return calls.random()
	}

}
