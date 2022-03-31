
package io.zeitwert.ddd.part.model.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.zeitwert.ddd.common.model.enums.CodeCountry;
import io.zeitwert.ddd.common.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.enums.CodePartListTypeEnum;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.obj.model.ObjPartNote;
import io.zeitwert.fm.obj.model.ObjPartNoteRepository;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.fm.test.model.ObjTestRepository;
import io.zeitwert.fm.test.model.base.ObjTestFields;
import io.zeitwert.server.Application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class NoteTest {

	@Autowired
	private SessionInfo sessionInfo;

	@Autowired
	private ObjUserRepository userRepository;

	@Autowired
	private CodeCountryEnum countryEnum;

	@Autowired
	private ObjTestRepository testRepository;

	@Test
	public void testNoteList() throws Exception {

		assertTrue(testRepository != null, "testRepository not null");
		assertEquals("obj_test", testRepository.getAggregateType().getId());

		ObjPartNoteRepository noteRepository = testRepository.getNoteRepository();
		// PartRepositoryBase<?, ?> asPartRepoBase = (PartRepositoryBase<?, ?>)
		// ((ObjPartNoteRepositoryImpl) noteRepository);
		assertTrue(noteRepository != null, "testNoteRepository not null");
		CodePartListType noteListType = testRepository.getNoteListType();
		assertTrue(CodePartListTypeEnum.getPartListType(ObjTestFields.NOTE_LIST).equals(noteListType), "noteListType");

		ObjTest test1a = testRepository.create(sessionInfo);
		// assertTrue(asPartRepoBase.isInitialised(test1a));
		this.initObjTest(test1a, "One", "martin@comunas.fm", "ch");
		Integer test1Id = test1a.getId();

		assertEquals(0, test1a.getNoteList().size());
		assertEquals(0, noteRepository.getPartList(test1a, noteListType).size());
		// assertEquals(0, ((PartRepositoryBase<ObjTest, ?>)
		// noteRepository).getParts(test1a).size());

		ObjPartNote note1a0 = test1a.addNote();
		initNote(note1a0, "Subject 1", "Content 1", false);
		assertEquals(1, test1a.getNoteList().size());
		assertEquals(1, noteRepository.getPartList(test1a, noteListType).size());
		// assertEquals(1, ((PartRepositoryBase<ObjTest, ?>)
		// noteRepository).getParts(test1a).size());

		ObjPartNote note1a1 = test1a.addNote();
		initNote(note1a1, "Subject 2", "Content 2", false);
		ObjPartNote note1a2 = test1a.addNote();
		initNote(note1a2, "Subject 3", "Content 3", false);

		assertEquals(3, test1a.getNoteList().size());
		assertEquals(3, noteRepository.getPartList(test1a, noteListType).size());
		// assertEquals(3, ((PartRepositoryBase<ObjTest, ?>)
		// noteRepository).getParts(test1a).size());

		List<ObjPartNote> test1aNoteList = test1a.getNoteList();
		assertEquals(note1a0, test1aNoteList.get(0));
		assertEquals(note1a1, test1aNoteList.get(1));
		assertEquals(note1a2, test1aNoteList.get(2));
		assertEquals(test1aNoteList, List.of(note1a0, note1a1, note1a2));
		assertEquals("Subject 1,Subject 2,Subject 3",
				String.join(",", test1a.getNoteList().stream().map(n -> n.getSubject()).toList()));
		assertEquals("Subject 2", test1a.getNoteList().get(1).getSubject());
		assertEquals(3, noteRepository.getPartList(test1a, noteListType).size());

		test1a.removeNote(note1a1.getId());
		assertEquals(2, test1a.getNoteCount());
		assertEquals(note1a2, test1a.getNote(1));
		assertEquals(note1a2, test1a.getNoteById(note1a2.getId()));
		assertEquals(PartStatus.DELETED, ((PartSPI<?>) note1a1).getStatus());
		assertEquals(2, test1a.getNoteList().size());
		assertEquals(3, noteRepository.getPartList(test1a, noteListType).size());
		// assertEquals(3, ((PartRepositoryBase<ObjTest, ?>)
		// noteRepository).getParts(test1a).size());
		assertEquals(note1a0.getSubject(), test1a.getNote(0).getSubject());
		assertEquals(note1a2.getSubject(), test1a.getNote(1).getSubject());

		test1aNoteList = test1a.getNoteList();
		assertEquals(note1a0.getSubject(), test1aNoteList.get(0).getSubject());
		assertEquals(note1a2.getSubject(), test1aNoteList.get(1).getSubject());
		assertEquals(test1aNoteList, List.of(note1a0, note1a2));
		assertEquals(List.of(PartStatus.UPDATED, PartStatus.DELETED, PartStatus.UPDATED),
				noteRepository.getPartList(test1a, noteListType).stream().map(p -> ((PartSPI<?>) p).getStatus()).toList());

		testRepository.store(test1a);
		// assertFalse(((PartRepositoryBase<ObjTest, ?>)
		// noteRepository).isInitialised(test1a));
		test1a = null;

		ObjTest test1b = testRepository.get(sessionInfo, test1Id);

		assertEquals(2, test1b.getNoteList().size());
		assertEquals(2, noteRepository.getPartList(test1b, noteListType).size());
		// assertEquals(2, ((PartRepositoryBase<ObjTest, ?>)
		// noteRepository).getParts(test1b).size());
		assertEquals(List.of(PartStatus.READ, PartStatus.READ),
				noteRepository.getPartList(test1b, noteListType).stream().map(p -> ((PartSPI<?>) p).getStatus()).toList());
		assertEquals("Subject 1,Subject 3",
				String.join(",", test1b.getNoteList().stream().map(n -> n.getSubject()).toList()));
		assertEquals("Subject 3", test1b.getNoteList().get(1).getSubject());

		ObjPartNote note1b2 = test1b.addNote();
		initNote(note1b2, "Subject 4", "Content 4", false);
		test1b.getNote(1).setIsPrivate(true);
		assertEquals(List.of(PartStatus.READ, PartStatus.UPDATED, PartStatus.UPDATED),
				noteRepository.getPartList(test1b, noteListType).stream().map(p -> ((PartSPI<?>) p).getStatus()).toList());

		testRepository.store(test1b);
		// assertFalse(((PartRepositoryBase<ObjTest, ?>)
		// noteRepository).isInitialised(test1b));
		test1b = null;

		ObjTest test1c = testRepository.get(sessionInfo, test1Id);

		assertEquals(3, test1c.getNoteList().size());
		assertEquals(3, noteRepository.getPartList(test1c, noteListType).size());
		// assertEquals(3, ((PartRepositoryBase<ObjTest, ?>)
		// noteRepository).getParts(test1c).size());
		assertEquals(List.of(PartStatus.READ, PartStatus.READ, PartStatus.READ),
				noteRepository.getPartList(test1c, noteListType).stream().map(p -> ((PartSPI<?>) p).getStatus()).toList());
		assertEquals("Subject 1,Subject 3,Subject 4",
				String.join(",", test1c.getNoteList().stream().map(n -> n.getSubject()).toList()));

		test1c.clearNoteList();
		assertEquals(0, test1c.getNoteCount());
		assertEquals(0, test1c.getNoteList().size());

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
		ObjUser user = userRepository.getByEmail(userEmail).get();
		test.setOwner(user);
		CodeCountry country = countryEnum.getItem(countryId);
		test.setCountry(country);
	}

	private void initNote(ObjPartNote note, String subject, String content, Boolean isPrivate) {
		note.setSubject(subject);
		note.setContent(content);
		note.setIsPrivate(isPrivate);
	}

}
