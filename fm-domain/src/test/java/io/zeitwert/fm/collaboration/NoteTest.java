
package io.zeitwert.fm.collaboration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteTypeEnum;
import io.dddrive.app.model.RequestContext;
import io.dddrive.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.fm.test.model.enums.CodeTestType;
import io.zeitwert.fm.test.model.enums.CodeTestTypeEnum;
import io.zeitwert.test.TestApplication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class NoteTest {

	private static final String USER_EMAIL = "tt@zeitwert.io";

	@Autowired
	private RequestContext requestCtx;

	@Autowired
	private ObjUserFMRepository userRepo;

	@Autowired
	private CodeTestTypeEnum testTypeEnum;

	@Autowired
	private ObjTestRepository testRepo;

	@Autowired
	private ObjNoteRepository noteRepo;

	@Test
	public void testNoteList() throws Exception {

		assertTrue(this.testRepo != null, "testRepository not null");
		assertEquals("obj_test", this.testRepo.getAggregateType().getId());

		// Get user and timestamp for new dddrive signatures
		ObjUser user = this.userRepo.getByEmail(USER_EMAIL).get();
		Object userId = user.getId();
		OffsetDateTime timestamp = OffsetDateTime.now();

		ObjTest testA1 = this.testRepo.create(this.requestCtx.getTenantId(), userId, timestamp);
		this.initObjTest(testA1, "One", user, "type_a");
		Object testA_id = testA1.getId();

		assertEquals(0, testA1.getNotes().size());

		// add 3 notes [1, 2, 3]
		// remove 1 note in the middle [1, 2]
		// store
		{
			ObjNote noteA1 = testA1.addNote(CodeNoteTypeEnum.getNoteType("note"));
			this.initNote(noteA1, "Subject 1", "Content 1", false);
			this.noteRepo.store(noteA1, userId, timestamp);
			assertEquals(1, testA1.getNotes().size());

			ObjNote noteB1 = testA1.addNote(CodeNoteTypeEnum.getNoteType("note"));
			this.initNote(noteB1, "Subject 2", "Content 2", false);
			this.noteRepo.store(noteB1, userId, timestamp);
			assertEquals(2, testA1.getNotes().size());

			ObjNote noteC1 = testA1.addNote(CodeNoteTypeEnum.getNoteType("note"));
			this.initNote(noteC1, "Subject 3", "Content 3", false);
			this.noteRepo.store(noteC1, userId, timestamp);
			assertEquals(3, testA1.getNotes().size());

			List<ObjNote> testA1_noteList = testA1.getNotes();
			Set<Object> idSet = testA1_noteList.stream().map(n -> n.getId()).collect(Collectors.toSet());
			assertEquals(Set.of(noteA1.getId(), noteB1.getId(), noteC1.getId()), idSet);
			assertEquals(noteA1.getId(), testA1_noteList.get(0).getId());
			assertEquals(noteB1.getId(), testA1_noteList.get(1).getId());
			assertEquals(noteC1.getId(), testA1_noteList.get(2).getId());
			assertEquals("Subject 1,Subject 2,Subject 3",
					String.join(",", testA1.getNotes().stream().map(n -> n.getSubject()).toList()));
			assertEquals("Subject 2", testA1.getNotes().get(1).getSubject());

			testA1.removeNote(noteB1.getId());
			testA1_noteList = testA1.getNotes();
			assertEquals(2, testA1_noteList.size());
			assertEquals(noteA1.getId(), testA1_noteList.get(0).getId());
			assertEquals(noteA1.getSubject(), testA1_noteList.get(0).getSubject());
			assertEquals(noteC1.getId(), testA1_noteList.get(1).getId());
			assertEquals(noteC1.getSubject(), testA1_noteList.get(1).getSubject());

			this.testRepo.store(testA1, userId, timestamp);
			testA1 = null;
		}

		// load test again [1, 3]
		// add one note [1, 3, 4]
		// change middle note to private [1, 3private, 4]
		ObjTest testA2 = this.testRepo.load(testA_id);

		{
			List<ObjNote> testA2_noteList = testA2.getNotes();

			assertEquals(2, testA2_noteList.size());
			assertEquals("Subject 1,Subject 3", String.join(",", testA2_noteList.stream().map(n -> n.getSubject()).toList()));
			assertEquals("Subject 3", testA2_noteList.get(1).getSubject());

			ObjNote noteD1 = testA2.addNote(CodeNoteTypeEnum.getNoteType("note"));
			this.initNote(noteD1, "Subject 4", "Content 4", false);
			this.noteRepo.store(noteD1, userId, timestamp);
			assertEquals(3, testA2.getNotes().size());

			ObjNote noteC2 = testA2.getNotes().get(1);
			assertEquals("Subject 3", testA2_noteList.get(1).getSubject());
			noteC2.setIsPrivate(true);
			this.noteRepo.store(noteC2, userId, timestamp);

			this.testRepo.store(testA2, userId, timestamp);
			testA2 = null;
		}

		// load test again [1, 3private, 4]
		ObjTest testA3 = this.testRepo.get(testA_id);

		{
			List<ObjNote> testA3_noteList = testA3.getNotes();

			assertEquals("Subject 1,Subject 3,Subject 4",
					String.join(",", testA3_noteList.stream().map(n -> n.getSubject()).toList()));
			assertEquals(3, testA3_noteList.size());
			assertTrue(testA3.getNotes().get(1).getIsPrivate());
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

	private void initObjTest(ObjTest test, String name, ObjUser user, String testTypeId) {
		assertEquals("[, ]", test.getCaption());
		test.setShortText("Short Test " + name);
		assertEquals("[Short Test " + name + ", ]", test.getCaption());
		test.setLongText("Long Test " + name);
		assertEquals("[Short Test " + name + ", Long Test " + name + "]", test.getCaption());
		test.setInt(42);
		test.setNr(BigDecimal.valueOf(42));
		test.setIsDone(false);
		test.setDate(LocalDate.of(1966, 9, 8));
//		test.setOwner(user);
		CodeTestType testType = this.testTypeEnum.getItem(testTypeId);
		test.setTestType(testType);
	}

	private void initNote(ObjNote note, String subject, String content, Boolean isPrivate) {
		note.setSubject(subject);
		note.setContent(content);
		note.setIsPrivate(isPrivate);
	}

}
