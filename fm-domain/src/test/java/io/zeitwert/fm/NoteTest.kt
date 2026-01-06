package io.zeitwert.fm

import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.domain.test.model.ObjTest
import io.zeitwert.domain.test.model.ObjTestRepository
import io.zeitwert.domain.test.model.enums.CodeTestType
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class NoteTest {

	@Autowired
	private lateinit var sessionContext: SessionContext

	@Autowired
	private lateinit var testRepo: ObjTestRepository

	@Autowired
	private lateinit var noteRepo: ObjNoteRepository

	@Test
	@Throws(Exception::class)
	fun testNoteList() {
		assertNotNull(this.testRepo, "testRepository not null")
		assertEquals("obj_test", this.testRepo.aggregateType.id)

		val userId: Any = sessionContext.userId
		sessionContext.currentTime // kept for parity with Java even if unused

		val testA1: ObjTest = this.testRepo.create()
		initObjTest(testA1, "One", "type_a")
		val testAId: Any = testA1.id

		assertEquals(0, testA1.notes.size)

		// add 3 notes [1, 2, 3]
		// remove 1 note in the middle [1, 2]
		// store
		run {
			val noteA1: ObjNote = testA1.addNote(CodeNoteType.NOTE, userId)
			initNote(noteA1, "Subject 1", "Content 1", false)
			this.noteRepo.store(noteA1)
			assertEquals(1, testA1.notes.size)

			val noteB1: ObjNote = testA1.addNote(CodeNoteType.NOTE, userId)
			initNote(noteB1, "Subject 2", "Content 2", false)
			this.noteRepo.store(noteB1)
			assertEquals(2, testA1.notes.size)

			val noteC1: ObjNote = testA1.addNote(CodeNoteType.NOTE, userId)
			initNote(noteC1, "Subject 3", "Content 3", false)
			this.noteRepo.store(noteC1)
			assertEquals(3, testA1.notes.size)

			val idSet3: Set<Any?> = HashSet(testA1.notes)
			assertEquals(setOf(noteA1.id, noteB1.id, noteC1.id), idSet3)
			assertEquals(
				setOf("Subject 1", "Subject 2", "Subject 3"),
				testA1.notes
					.asSequence()
					.map { id -> noteRepo.get(id) }
					.map { n -> n.subject }
					.toSet(),
			)

			testA1.removeNote(noteB1.id, userId)
			assertEquals(2, testA1.notes.size)
			val idSet2: Set<Any?> = HashSet(testA1.notes)
			assertEquals(setOf(noteA1.id, noteC1.id), idSet2)
			assertEquals(
				setOf("Subject 1", "Subject 3"),
				testA1.notes
					.asSequence()
					.map { id -> noteRepo.get(id) }
					.map { n -> n.subject }
					.toSet(),
			)

			this.testRepo.store(testA1)
			// allow GC parity with Java
			// testA1 = null
		}

		// load test again [1, 3]
		// add one note [1, 3, 4]
		// change middle note to private [1, 3private, 4]
		val testA2: ObjTest = this.testRepo.load(testAId)

		var noteC2Id: Any? = null

		run {
			val testA2NoteList: List<ObjNote> = testA2.notes.map { id -> noteRepo.get(id) }

			assertEquals(2, testA2NoteList.size)
			assertEquals(setOf("Subject 1", "Subject 3"), testA2NoteList.map { it.subject }.toSet())
			assertTrue(testA2NoteList.map { it.subject }.toSet().contains("Subject 3"))

			val noteA2_1 = testA2NoteList.first()
			val relatedTo = noteA2_1.relatedTo
			kotlin.test.assertNotNull(relatedTo, "relatedTo not null")
			kotlin.test.assertEquals(testAId, relatedTo.id, "relatedTo id")

			val noteD1: ObjNote = testA2.addNote(CodeNoteType.NOTE, userId)
			initNote(noteD1, "Subject 4", "Content 4", false)
			this.noteRepo.store(noteD1)
			assertEquals(3, testA2.notes.size)

			noteC2Id = testA2.notes[1]
			val noteC2: ObjNote = noteRepo.load(noteC2Id)
			assertTrue(testA2NoteList.map { it.subject }.toSet().contains("Subject 3"))
			noteC2.isPrivate = true
			this.noteRepo.store(noteC2)

			this.testRepo.store(testA2)
		}

		// load test again [1, 3private, 4]
		val testA3: ObjTest = this.testRepo.get(testAId)

		run {
			val testA3NoteList: List<ObjNote> = testA3.notes.map { id -> noteRepo.get(id) }

			assertEquals(
				setOf("Subject 1", "Subject 3", "Subject 4"),
				testA3NoteList.map { n -> n.subject }.toSet(),
			)
			assertEquals(3, testA3NoteList.size)

			val finalNoteC2Id = noteC2Id
			assertTrue(testA3NoteList.first { finalNoteC2Id == it.id }.isPrivate == true)
		}

		// Test privacy
		// RequestContext otherSession =
		// TestRequestContextProvider.getOtherSession(userRepository);

		// ObjTest test1d = testRepository.get(test1Id);
		// List<ObjNoteVRecord> test1dNoteList = test1d.getNoteList();

		// assertEquals(2, test1dNoteList.size());
		// assertEquals("Subject 1,Subject 4",
		// String.join(",", test1dNoteList.stream().map(n -> n.getSubject()).toList()));
	}

	private fun initObjTest(
		test: ObjTest,
		name: String,
		testTypeId: String,
	) {
		assertEquals("[, ]", test.caption)
		test.shortText = "Short Test $name"
		assertEquals("[Short Test $name, ]", test.caption)
		test.longText = "Long Test $name"
		assertEquals("[Short Test $name, Long Test $name]", test.caption)
		test.int = 42
		test.nr = BigDecimal.valueOf(42)
		test.isDone = false
		test.date = LocalDate.of(1966, 9, 8)
		// test.owner = user
		val testType: CodeTestType = CodeTestType.getTestType(testTypeId)!!
		test.testType = testType
	}

	private fun initNote(
		note: ObjNote,
		subject: String,
		content: String,
		isPrivate: Boolean,
	) {
		note.subject = subject
		note.content = content
		note.isPrivate = isPrivate
	}

}
