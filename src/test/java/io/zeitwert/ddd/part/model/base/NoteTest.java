
package io.zeitwert.ddd.part.model.base;

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
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeCountry;
import io.zeitwert.ddd.oe.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.ddd.session.service.api.impl.TestSessionInfoProvider;
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

	@Autowired
	private RequestContext requestCtx;

	@Autowired
	private ObjUserRepository userRepository;

	@Autowired
	private CodeCountryEnum countryEnum;

	@Autowired
	private ObjTestRepository testRepository;

	@Autowired
	private ObjNoteRepository noteRepository;

	@Test
	public void testNoteList() throws Exception {

		assertTrue(testRepository != null, "testRepository not null");
		assertEquals("obj_test", testRepository.getAggregateType().getId());

		ObjTest test1a = testRepository.create(requestCtx.getTenant().getId(), requestCtx);
		this.initObjTest(test1a, "One", "martin@zeitwert.io", "ch");
		Integer test1Id = test1a.getId();

		assertEquals(0, test1a.getNoteList().size());

		ObjNote note1a0 = test1a.addNote(CodeNoteTypeEnum.getNoteType("note"));
		initNote(note1a0, "Subject 1", "Content 1", false);
		noteRepository.store(note1a0);
		assertEquals(1, test1a.getNoteList().size());

		ObjNote note1a1 = test1a.addNote(CodeNoteTypeEnum.getNoteType("note"));
		initNote(note1a1, "Subject 2", "Content 2", false);
		noteRepository.store(note1a1);

		ObjNote note1a2 = test1a.addNote(CodeNoteTypeEnum.getNoteType("note"));
		initNote(note1a2, "Subject 3", "Content 3", false);
		noteRepository.store(note1a2);

		assertEquals(3, test1a.getNoteList().size());

		List<ObjNoteVRecord> test1aNoteList = test1a.getNoteList();
		Set<Integer> idSet = test1aNoteList.stream().map(n -> n.getId()).collect(Collectors.toSet());
		assertEquals(Set.of(note1a0.getId(), note1a1.getId(), note1a2.getId()), idSet);
		assertEquals(note1a0.getId(), test1aNoteList.get(0).getId());
		assertEquals(note1a1.getId(), test1aNoteList.get(1).getId());
		assertEquals(note1a2.getId(), test1aNoteList.get(2).getId());
		assertEquals("Subject 1,Subject 2,Subject 3",
				String.join(",", test1a.getNoteList().stream().map(n -> n.getSubject()).toList()));
		assertEquals("Subject 2", test1a.getNoteList().get(1).getSubject());

		test1a.removeNote(note1a1.getId());
		test1aNoteList = test1a.getNoteList();
		assertEquals(2, test1aNoteList.size());
		assertEquals(note1a0.getId(), test1aNoteList.get(0).getId());
		assertEquals(note1a0.getSubject(), test1aNoteList.get(0).getSubject());
		assertEquals(note1a2.getId(), test1aNoteList.get(1).getId());
		assertEquals(note1a2.getSubject(), test1aNoteList.get(1).getSubject());

		testRepository.store(test1a);
		test1a = null;

		ObjTest test1b = testRepository.get(requestCtx, test1Id);
		List<ObjNoteVRecord> test1bNoteList = test1b.getNoteList();

		assertEquals(2, test1bNoteList.size());
		assertEquals("Subject 1,Subject 3", String.join(",", test1bNoteList.stream().map(n -> n.getSubject()).toList()));
		assertEquals("Subject 3", test1bNoteList.get(1).getSubject());

		ObjNote note1b2 = test1b.addNote(CodeNoteTypeEnum.getNoteType("note"));
		initNote(note1b2, "Subject 4", "Content 4", false);
		noteRepository.store(note1b2);

		ObjNoteVRecord note1b1v = test1b.getNoteList().get(1);
		ObjNote note1b1 = noteRepository.get(requestCtx, note1b1v.getId());
		note1b1.setIsPrivate(true);
		noteRepository.store(note1b1);

		testRepository.store(test1b);
		test1b = null;

		ObjTest test1c = testRepository.get(requestCtx, test1Id);
		List<ObjNoteVRecord> test1cNoteList = test1c.getNoteList();

		assertEquals(3, test1cNoteList.size());
		assertEquals("Subject 1,Subject 3,Subject 4",
				String.join(",", test1cNoteList.stream().map(n -> n.getSubject()).toList()));
		assertTrue(test1c.getNoteList().get(1).getIsPrivate());

		// Test privacy
		RequestContext otherSession = TestSessionInfoProvider.getOtherSession(userRepository);

		ObjTest test1d = testRepository.get(otherSession, test1Id);
		List<ObjNoteVRecord> test1dNoteList = test1d.getNoteList();

		assertEquals(2, test1dNoteList.size());
		assertEquals("Subject 1,Subject 4",
				String.join(",", test1dNoteList.stream().map(n -> n.getSubject()).toList()));

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
		ObjUser user = userRepository.getByEmail(requestCtx, userEmail).get();
		test.setOwner(user);
		CodeCountry country = countryEnum.getItem(countryId);
		test.setCountry(country);
	}

	private void initNote(ObjNote note, String subject, String content, Boolean isPrivate) {
		note.setSubject(subject);
		note.setContent(content);
		note.setIsPrivate(isPrivate);
	}

}
