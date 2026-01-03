package io.zeitwert.fm;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.domain.test.model.ObjTest;
import io.zeitwert.domain.test.model.ObjTestRepository;
import io.zeitwert.domain.test.model.enums.CodeTestType;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import io.zeitwert.test.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class NoteTest {

	@Autowired
	private SessionContext sessionContext;

	@Autowired
	private ObjUserRepository userRepo;

	@Autowired
	private ObjTestRepository testRepo;

	@Autowired
	private ObjNoteRepository noteRepo;

	@Test
	public void testNoteList() throws Exception {

		assertNotNull(this.testRepo, "testRepository not null");
		assertEquals("obj_test", this.testRepo.getAggregateType().getId());

		Object userId = sessionContext.getUserId();
		OffsetDateTime now = sessionContext.getCurrentTime();

		ObjTest testA1 = this.testRepo.create();
		this.initObjTest(testA1, "One", "type_a");
		Object testA_id = testA1.getId();

		assertEquals(0, testA1.getNotes().size());

		// add 3 notes [1, 2, 3]
		// remove 1 note in the middle [1, 2]
		// store
		{
			ObjNote noteA1 = testA1.addNote(CodeNoteType.NOTE, userId);
			this.initNote(noteA1, "Subject 1", "Content 1", false);
			this.noteRepo.store(noteA1);
			assertEquals(1, testA1.getNotes().size());

			ObjNote noteB1 = testA1.addNote(CodeNoteType.NOTE, userId);
			this.initNote(noteB1, "Subject 2", "Content 2", false);
			this.noteRepo.store(noteB1);
			assertEquals(2, testA1.getNotes().size());

			ObjNote noteC1 = testA1.addNote(CodeNoteType.NOTE, userId);
			this.initNote(noteC1, "Subject 3", "Content 3", false);
			this.noteRepo.store(noteC1);
			assertEquals(3, testA1.getNotes().size());

			Set<Object> idSet3 = new HashSet<>(testA1.getNotes());
			assertEquals(Set.of(noteA1.getId(), noteB1.getId(), noteC1.getId()), idSet3);
			assertEquals(Set.of("Subject 1", "Subject 2", "Subject 3"), testA1.getNotes().stream().map(id -> noteRepo.get(id)).map(n -> n.getSubject()).collect(Collectors.toSet()));

			testA1.removeNote(noteB1.getId(), userId);
			assertEquals(2, testA1.getNotes().size());
			Set<Object> idSet2 = new HashSet<>(testA1.getNotes());
			assertEquals(Set.of(noteA1.getId(), noteC1.getId()), idSet2);
			assertEquals(Set.of("Subject 1", "Subject 3"), testA1.getNotes().stream().map(id -> noteRepo.get(id)).map(n -> n.getSubject()).collect(Collectors.toSet()));

			this.testRepo.store(testA1);
			testA1 = null;
		}

		// load test again [1, 3]
		// add one note [1, 3, 4]
		// change middle note to private [1, 3private, 4]
		ObjTest testA2 = this.testRepo.load(testA_id);

		Object noteC2Id = null;

		{
			List<ObjNote> testA2_noteList = testA2.getNotes().stream().map(id -> noteRepo.get(id)).toList();

			assertEquals(2, testA2_noteList.size());
			assertEquals("Subject 1,Subject 3", String.join(",", testA2_noteList.stream().map(n -> n.getSubject()).toList()));
			assertEquals("Subject 3", testA2_noteList.get(1).getSubject());

			ObjNote noteD1 = testA2.addNote(CodeNoteType.getNoteType("note"), userId);
			this.initNote(noteD1, "Subject 4", "Content 4", false);
			this.noteRepo.store(noteD1);
			assertEquals(3, testA2.getNotes().size());

			noteC2Id = testA2.getNotes().get(1);
			ObjNote noteC2 = noteRepo.load(noteC2Id);
			assertEquals("Subject 3", testA2_noteList.get(1).getSubject());
			noteC2.setPrivate(true);
			this.noteRepo.store(noteC2);

			this.testRepo.store(testA2);
			testA2 = null;
		}

		// load test again [1, 3private, 4]
		ObjTest testA3 = this.testRepo.get(testA_id);

		{
			List<ObjNote> testA3_noteList = testA3.getNotes().stream().map(id -> noteRepo.get(id)).toList();

			assertEquals(Set.of("Subject 1", "Subject 3", "Subject 4"), testA3_noteList.stream().map(n -> n.getSubject()).collect(Collectors.toSet()));
			assertEquals(3, testA3_noteList.size());
			Object finalNoteC2Id = noteC2Id;
			assertTrue(testA3_noteList.stream().filter(n -> finalNoteC2Id.equals(n.getId())).findFirst().get().isPrivate());
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

	private void initObjTest(ObjTest test, String name, String testTypeId) {
		assertEquals("[, ]", test.getCaption());
		test.setShortText("Short Test " + name);
		assertEquals("[Short Test " + name + ", ]", test.getCaption());
		test.setLongText("Long Test " + name);
		assertEquals("[Short Test " + name + ", Long Test " + name + "]", test.getCaption());
		test.setInt(42);
		test.setNr(BigDecimal.valueOf(42));
		test.setDone(false);
		test.setDate(LocalDate.of(1966, 9, 8));
//		test.setOwner(user);
		CodeTestType testType = CodeTestType.Enumeration.getTestType(testTypeId);
		test.setTestType(testType);
	}

	private void initNote(ObjNote note, String subject, String content, Boolean isPrivate) {
		note.setSubject(subject);
		note.setContent(content);
		note.setPrivate(isPrivate);
	}

}
