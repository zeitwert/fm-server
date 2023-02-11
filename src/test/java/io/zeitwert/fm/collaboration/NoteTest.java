
package io.zeitwert.fm.collaboration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteTypeEnum;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.enums.CodeCountry;
import io.zeitwert.ddd.oe.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.server.Application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class NoteTest {

	private static final String USER_EMAIL = "tt@zeitwert.io";

	@Autowired
	private RequestContext requestCtx;

	@Autowired
	private ObjUserFMRepository userRepository;

	@Autowired
	private CodeCountryEnum countryEnum;

	@Autowired
	private ObjTestRepository testRepository;

	@Autowired
	private ObjNoteRepository noteRepository;

	@Test
	public void testNoteList() throws Exception {

		assertTrue(this.testRepository != null, "testRepository not null");
		assertEquals("obj_test", this.testRepository.getAggregateType().getId());

		ObjTest testA1 = this.testRepository.create(this.requestCtx.getTenantId());
		this.initObjTest(testA1, "One", USER_EMAIL, "ch");
		Integer testA_id = testA1.getId();

		assertEquals(0, testA1.getNotes().size());

		// add 3 notes [1, 2, 3]
		// remove 1 note in the middle [1, 2]
		// store
		{
			ObjNote noteA1a = testA1.addNote(CodeNoteTypeEnum.getNoteType("note"));
			this.initNote(noteA1a, "Subject 1", "Content 1", false);
			this.noteRepository.store(noteA1a);
			assertEquals(1, testA1.getNotes().size());

			ObjNote noteA1b = testA1.addNote(CodeNoteTypeEnum.getNoteType("note"));
			this.initNote(noteA1b, "Subject 2", "Content 2", false);
			this.noteRepository.store(noteA1b);
			assertEquals(2, testA1.getNotes().size());

			ObjNote noteA1c = testA1.addNote(CodeNoteTypeEnum.getNoteType("note"));
			this.initNote(noteA1c, "Subject 3", "Content 3", false);
			this.noteRepository.store(noteA1c);
			assertEquals(3, testA1.getNotes().size());

			List<ObjNoteVRecord> testA1_noteList = testA1.getNotes();
			Set<Integer> idSet = testA1_noteList.stream().map(n -> n.getId()).collect(Collectors.toSet());
			assertEquals(Set.of(noteA1a.getId(), noteA1b.getId(), noteA1c.getId()), idSet);
			assertEquals(noteA1a.getId(), testA1_noteList.get(0).getId());
			assertEquals(noteA1b.getId(), testA1_noteList.get(1).getId());
			assertEquals(noteA1c.getId(), testA1_noteList.get(2).getId());
			assertEquals("Subject 1,Subject 2,Subject 3",
					String.join(",", testA1.getNotes().stream().map(n -> n.getSubject()).toList()));
			assertEquals("Subject 2", testA1.getNotes().get(1).getSubject());

			testA1.removeNote(noteA1b.getId());
			testA1_noteList = testA1.getNotes();
			assertEquals(2, testA1_noteList.size());
			assertEquals(noteA1a.getId(), testA1_noteList.get(0).getId());
			assertEquals(noteA1a.getSubject(), testA1_noteList.get(0).getSubject());
			assertEquals(noteA1c.getId(), testA1_noteList.get(1).getId());
			assertEquals(noteA1c.getSubject(), testA1_noteList.get(1).getSubject());

			this.testRepository.store(testA1);
			testA1 = null;
		}

		ObjTest testA2 = this.testRepository.get(testA_id);

		// load test again [1, 3]
		// add 1 note [1, 3, 4]
		// change middle note to private [1, 3private, 4]
		{
			List<ObjNoteVRecord> testA2_noteList = testA2.getNotes();

			assertEquals(2, testA2_noteList.size());
			assertEquals("Subject 1,Subject 3", String.join(",", testA2_noteList.stream().map(n -> n.getSubject()).toList()));
			assertEquals("Subject 3", testA2_noteList.get(1).getSubject());

			ObjNote noteA2d1 = testA2.addNote(CodeNoteTypeEnum.getNoteType("note"));
			this.initNote(noteA2d1, "Subject 4", "Content 4", false);
			this.noteRepository.store(noteA2d1);
			assertEquals(3, testA2.getNotes().size());

			ObjNoteVRecord noteA2d2v = testA2.getNotes().get(1);
			ObjNote noteA2d2 = this.noteRepository.get(noteA2d2v.getId());
			assertEquals("Subject 3", testA2_noteList.get(1).getSubject());
			noteA2d2.setIsPrivate(true);
			this.noteRepository.store(noteA2d2);

			this.testRepository.store(testA2);
			testA2 = null;
		}

		// load test again [1, 3private, 4]
		{
			ObjTest testA3 = this.testRepository.get(testA_id);
			List<ObjNoteVRecord> testA3_noteList = testA3.getNotes();

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

	private void initObjTest(ObjTest test, String name, String userEmail, String countryId) {
		assertEquals("[, ]", test.getCaption());
		test.setShortText("Short Test " + name);
		assertEquals("[Short Test " + name + ", ]", test.getCaption());
		test.setLongText("Long Test " + name);
		assertEquals("[Short Test " + name + ", Long Test " + name + "]", test.getCaption());
		test.setInt(42);
		test.setNr(BigDecimal.valueOf(42));
		test.setIsDone(false);
		test.setDate(LocalDate.of(1966, 9, 8));
		ObjUser user = this.userRepository.getByEmail(userEmail).get();
		test.setOwner(user);
		CodeCountry country = this.countryEnum.getItem(countryId);
		test.setCountry(country);
	}

	private void initNote(ObjNote note, String subject, String content, Boolean isPrivate) {
		note.setSubject(subject);
		note.setContent(content);
		note.setIsPrivate(isPrivate);
	}

}
