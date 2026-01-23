package io.zeitwert.data.dsl

import dddrive.app.doc.model.enums.CodeCaseStageEnum.Companion.getCaseStage
import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.fm.task.model.ItemWithTasks
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import java.time.OffsetDateTime
import kotlin.random.Random

/**
 * DSL for creating tasks using Spring repositories.
 *
 * This DSL uses the repository layer to create tasks, which ensures proper domain logic
 * is executed.
 *
 * Usage:
 * ```
 * Task.init(directory)
 * Task.attachRandomTasks(building, userId, timestamp, 1..5)
 * ```
 */
object Task {

	lateinit var directory: RepositoryDirectory

	val taskRepository: DocTaskRepository
		get() = directory.getRepository(DocTask::class.java) as DocTaskRepository

	fun init(directory: RepositoryDirectory) {
		this.directory = directory
	}

	/**
	 * Attach random tasks to any item that implements ItemWithTasks.
	 *
	 * @param item The item to attach tasks to (Building, Account, or Contact)
	 * @param userId The user ID for task creation
	 * @param timestamp The timestamp for task creation
	 * @param countRange The range for random number of tasks (default 1..5)
	 */
	fun attachRandomTasks(
		item: ItemWithTasks,
		userId: Any,
		timestamp: OffsetDateTime,
		countRange: IntRange = 1..5,
	) {
		val count = countRange.random()
		repeat(count) {
			val task = item.addTask(userId, timestamp)
			initRandomTask(task, userId, timestamp)
			taskRepository.store(task)
		}
	}

	private fun initRandomTask(
		task: DocTask,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		// Set random case stage
		task.meta.setCaseStage(getCaseStage(randomStage()), userId, timestamp)

		// Set random task data
		val (subject, content) = randomTaskContent()
		task.subject = subject
		task.content = content
		task.isPrivate = Random.nextBoolean()
		task.priority = randomPriority()
		task.dueAt = randomDueDate(timestamp)
	}

	private fun randomStage(): String =
		listOf("task.new", "task.open", "task.progress", "task.done").random()

	private fun randomPriority(): CodeTaskPriority =
		listOf(CodeTaskPriority.LOW, CodeTaskPriority.NORMAL, CodeTaskPriority.HIGH).random()

	private fun randomDueDate(timestamp: OffsetDateTime): OffsetDateTime {
		val daysToAdd = (30..90).random().toLong()
		return timestamp.plusDays(daysToAdd)
	}

	private fun randomTaskContent(): Pair<String, String> {
		val tasks = listOf(
			"Heizungsanlage warten" to
				"Jährliche Wartung der Heizungsanlage durchführen. Brenner reinigen, Filter wechseln und Effizienz prüfen.",
			"Dachinspektion durchführen" to
				"Kontrolle der Dacheindeckung auf Beschädigungen. Dachrinnen reinigen und Abdichtungen überprüfen.",
			"Fassade überprüfen" to
				"Visuelle Inspektion der Fassade auf Risse und Beschädigungen. Dokumentation mit Fotos erstellen.",
			"Aufzugswartung koordinieren" to
				"Termin mit Aufzugsfirma für die gesetzlich vorgeschriebene Wartung vereinbaren.",
			"Brandschutzprüfung" to
				"Feuerlöscher und Notbeleuchtung kontrollieren. Fluchtwege auf Hindernisse prüfen.",
			"Elektroinstallation prüfen" to
				"Sicherungen und Schutzschalter testen. Veraltete Komponenten identifizieren.",
			"Sanitäranlagen kontrollieren" to
				"Wasserleitungen auf Lecks prüfen. Wasserdruck messen und Armaturen überprüfen.",
			"Aussenanlagen pflegen" to
				"Grünflächen und Parkplätze inspizieren. Notwendige Reparaturen dokumentieren.",
			"Kellerräume inspizieren" to
				"Kellerräume auf Feuchtigkeit und Schimmelbefall kontrollieren. Belüftung prüfen.",
			"Fenster und Türen warten" to
				"Dichtungen kontrollieren und bei Bedarf ersetzen. Scharniere ölen und Schlösser prüfen.",
			"Energieausweis erneuern" to
				"Termin mit Energieberater für die Erneuerung des Energieausweises vereinbaren.",
			"Versicherungsunterlagen prüfen" to
				"Aktuelle Versicherungspolicen überprüfen und bei Bedarf anpassen.",
			"Mieterversammlung organisieren" to
				"Einladungen versenden und Tagesordnung vorbereiten für die jährliche Versammlung.",
			"Nebenkostenabrechnung erstellen" to
				"Daten sammeln und Nebenkostenabrechnung für das vergangene Jahr vorbereiten.",
			"Handwerker beauftragen" to
				"Offerten für anstehende Reparaturarbeiten einholen und vergleichen.",
			"Schliesssystem modernisieren" to
				"Angebote für elektronisches Schliesssystem einholen. Kosten-Nutzen-Analyse erstellen.",
			"Parkplatzmarkierung erneuern" to
				"Bodenbeschriftung auf dem Parkplatz erneuern lassen. Angebote einholen.",
			"Winterdienst vorbereiten" to
				"Streugut bestellen und Vertrag mit Winterdienst überprüfen oder erneuern.",
		)
		return tasks.random()
	}

}
