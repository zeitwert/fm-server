package dddrive.test

import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.base.ObjBase
import dddrive.ddd.model.AggregateSPI
import dddrive.ddd.path.getValueByPath
import dddrive.ddd.path.setValueByPath
import dddrive.ddd.property.model.PropertyChangeListener
import dddrive.ddd.query.query
import dddrive.domain.household.model.ObjHousehold
import dddrive.domain.household.model.ObjHouseholdRepository
import dddrive.domain.household.model.enums.CodeLabel
import dddrive.domain.household.model.enums.CodeSalutation
import dddrive.domain.oe.model.ObjTenantRepository
import dddrive.domain.oe.model.ObjUser
import dddrive.domain.oe.model.ObjUserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class HouseholdMemTest {

	@Autowired
	private lateinit var sessionContext: SessionContext

	@Autowired
	private lateinit var tenantRepo: ObjTenantRepository

	@Autowired
	private lateinit var userRepo: ObjUserRepository

	@Autowired
	private lateinit var hhRepo: ObjHouseholdRepository

	private lateinit var user1: ObjUser
	private lateinit var user2: ObjUser

	data class PropertyChangeEvent(
		val op: String,
		val path: String,
		val value: Any?,
		val oldValue: Any?,
		val isInCalc: Boolean,
	)

	class PropertyChangeCollector : PropertyChangeListener {

		private val events: MutableList<PropertyChangeEvent> = mutableListOf()

		override fun propertyChange(
			op: String,
			path: String,
			value: Any?,
			oldValue: Any?,
			isInCalc: Boolean,
		) {
			events.add(PropertyChangeEvent(op, path, value, oldValue, isInCalc))
		}

		fun clear() {
			events.clear()
		}

		fun assertEventCount(expected: Int) {
			assertEquals(expected, events.size, "event count")
		}

		fun assertEvent(
			index: Int,
			op: String,
			pathSuffix: String,
			value: Any?,
			oldValue: Any?,
			isInCalc: Boolean = false,
		) {
			assertTrue(index < events.size) { "Expected event at index $index but only ${events.size} events" }
			val event = events[index]
			assertEquals(op, event.op, "event[$index].op")
			assertTrue(event.path.endsWith(pathSuffix)) { "event[$index].path expected to end with '$pathSuffix' but was '${event.path}'" }
			assertEquals(value, event.value, "event[$index].value")
			assertEquals(oldValue, event.oldValue, "event[$index].oldValue")
			assertEquals(isInCalc, event.isInCalc, "event[$index].isInCalc")
		}

		fun assertLastEvent(
			op: String,
			pathSuffix: String,
			value: Any?,
			oldValue: Any?,
			isInCalc: Boolean = false,
		) {
			assertEvent(events.size - 1, op, pathSuffix, value, oldValue, isInCalc)
		}
	}

	private fun createHouseholdWithCollector(): Pair<ObjHousehold, PropertyChangeCollector> {
		val hh = hhRepo.create()
		val collector = PropertyChangeCollector()
		(hh as AggregateSPI).addPropertyChangeListener(collector)
		collector.clear() // Clear creation events
		return Pair(hh, collector)
	}

	@BeforeEach
	fun setUp() {
		user1 =
			userRepo.getByEmail("user1@dfp.ch").orElseGet {
				val newUser = userRepo.create()
				newUser.name = "user1"
				newUser.email = "user1@dfp.ch"
				userRepo.store(newUser)
				newUser
			}
		assertNotNull(user1, "user1")

		user2 =
			userRepo.getByEmail("user2@dfp.ch").orElseGet {
				val newUser = userRepo.create()
				newUser.name = "user2"
				newUser.email = "user2@dfp.ch"
				userRepo.store(newUser)
				newUser
			}
		assertNotNull(user2, "user2")
	}

	@Test
	fun testHouseholdRepository() {
		assertEquals("objHousehold", hhRepo.aggregateType.id)
		assertEquals(
			0,
			hhRepo
				.find(
					query {
						filter { "objTypeId" eq "obj_household" }
					},
				).size,
			"0 hh",
		)

		val hhA1 = hhRepo.create()
		val collector = PropertyChangeCollector()
		(hhA1 as AggregateSPI).addPropertyChangeListener(collector)
		val hhA1Id = hhA1.id
		hhA1.name = "HHA"
		assertEquals(sessionContext.tenantId, hhA1.tenantId, "tenant")
		assertEquals(sessionContext.userId, hhA1.meta.createdByUserId, "createUser")

		// Labels - using new collection API
		assertEquals(0, hhA1.labelSet.size, "labelSet.1a")
		assertFalse(hhA1.labelSet.has(CodeLabel.A), "labelSet.1b")
		assertFalse(hhA1.labelSet.has(CodeLabel.B), "labelSet.1c")

		hhA1.labelSet.add(CodeLabel.A)
		assertEquals(1, hhA1.labelSet.size, "labelSet.2a")
		assertTrue(hhA1.labelSet.has(CodeLabel.A), "labelSet.2b")
		assertFalse(hhA1.labelSet.has(CodeLabel.B), "labelSet.2c")

		hhA1.labelSet.add(CodeLabel.B)
		assertEquals(2, hhA1.labelSet.size, "labelSet.3a")
		assertTrue(hhA1.labelSet.has(CodeLabel.A), "labelSet.3b")
		assertTrue(hhA1.labelSet.has(CodeLabel.B), "labelSet.3c")

		hhA1.labelSet.add(CodeLabel.A) // Adding duplicate
		assertEquals(2, hhA1.labelSet.size, "labelSet.4a")
		assertTrue(hhA1.labelSet.has(CodeLabel.A), "labelSet.4b")
		assertTrue(hhA1.labelSet.has(CodeLabel.B), "labelSet.4c")

		hhA1.labelSet.remove(CodeLabel.A)
		assertEquals(1, hhA1.labelSet.size, "labelSet.5a")
		assertFalse(hhA1.labelSet.has(CodeLabel.A), "labelSet.5b")
		assertTrue(hhA1.labelSet.has(CodeLabel.B), "labelSet.5c")

		hhA1.labelSet.clear()
		assertEquals(0, hhA1.labelSet.size, "labelSet.6a")
		assertFalse(hhA1.labelSet.has(CodeLabel.A), "labelSet.6b")
		assertFalse(hhA1.labelSet.has(CodeLabel.B), "labelSet.6c")

		hhA1.labelSet.add(CodeLabel.B)
		assertEquals(1, hhA1.labelSet.size, "labelSet.7a")
		assertFalse(hhA1.labelSet.has(CodeLabel.A), "labelSet.7b")
		assertTrue(hhA1.labelSet.has(CodeLabel.B), "labelSet.7c")

		// Users - using new collection API
		assertEquals(0, hhA1.userSet.size, "userSet.1a")
		assertFalse(hhA1.userSet.has(user1.id), "userSet.1b")
		assertFalse(hhA1.userSet.has(user2.id), "userSet.1c")

		hhA1.userSet.add(user1.id)
		assertEquals(1, hhA1.userSet.size, "userSet.2a")
		assertTrue(hhA1.userSet.has(user1.id), "userSet.2b")
		assertFalse(hhA1.userSet.has(user2.id), "userSet.2c")

		hhA1.userSet.add(user2.id)
		assertEquals(2, hhA1.userSet.size, "userSet.3a")
		assertTrue(hhA1.userSet.has(user1.id), "userSet.3b")
		assertTrue(hhA1.userSet.has(user2.id), "userSet.3c")

		hhA1.userSet.add(user1.id) // Adding duplicate
		assertEquals(2, hhA1.userSet.size, "userSet.4a")
		assertTrue(hhA1.userSet.has(user1.id), "userSet.4b")
		assertTrue(hhA1.userSet.has(user2.id), "userSet.4c")

		hhA1.userSet.remove(user1.id)
		assertEquals(1, hhA1.userSet.size, "userSet.5a")
		assertFalse(hhA1.userSet.has(user1.id), "userSet.5b")
		assertTrue(hhA1.userSet.has(user2.id), "userSet.5c")

		hhA1.userSet.clear()
		assertEquals(0, hhA1.userSet.size, "userSet.6a")
		assertFalse(hhA1.userSet.has(user1.id), "userSet.6b")
		assertFalse(hhA1.userSet.has(user2.id), "userSet.6c")

		hhA1.userSet.add(user2.id)
		assertEquals(1, hhA1.userSet.size, "userSet.7a")
		assertFalse(hhA1.userSet.has(user1.id), "userSet.7b")
		assertTrue(hhA1.userSet.has(user2.id), "userSet.7c")

		// Responsible user - single aggregate reference property
		assertNull(hhA1.responsibleUser, "responsibleUser.1a")
		assertNull(hhA1.responsibleUserId, "responsibleUser.1b")

		// Set by ID, read by value
		hhA1.responsibleUserId = user1.id
		assertEquals(user1.id, hhA1.responsibleUserId, "responsibleUser.2a")
		assertEquals(user1.id, hhA1.responsibleUser?.id, "responsibleUser.2b")

		// Set by value, read by ID
		hhA1.responsibleUser = user2
		assertEquals(user2.id, hhA1.responsibleUserId, "responsibleUser.3a")
		assertEquals(user2.id, hhA1.responsibleUser?.id, "responsibleUser.3b")

		// Change reference
		hhA1.responsibleUserId = user1.id
		assertEquals(user1.id, hhA1.responsibleUserId, "responsibleUser.4a")
		assertEquals(user1.id, hhA1.responsibleUser?.id, "responsibleUser.4b")

		// Clear reference by setting to null (by value)
		hhA1.responsibleUser = null
		assertNull(hhA1.responsibleUser, "responsibleUser.5a")
		assertNull(hhA1.responsibleUserId, "responsibleUser.5b")

		// Set again for persistence test
		hhA1.responsibleUser = user1
		assertEquals(user1.id, hhA1.responsibleUser?.id, "responsibleUser.6a")

		// Members - using new collection API
		val p1 = hhA1.memberList.add()
		p1.salutation = CodeSalutation.MR
		p1.name = "Martin"
		assertEquals(CodeSalutation.MR, p1.salutation, "p1.salutation")
		assertEquals("Martin", p1.name, "p1.name")
		val p1Id = p1.id

		val p2 = hhA1.memberList.add()
		p2.salutation = CodeSalutation.MRS
		p2.name = "Elena"
		assertEquals(CodeSalutation.MRS, p2.salutation, "p2.salutation")
		assertEquals("Elena", p2.name, "p2.name")
		val p2Id = p2.id

		p1.name = null
		assertNull(p1.name, "p1.name")
		p1.name = "Martin"
		assertEquals("Martin", p1.name, "p1.name")

		p2.salutation = null
		assertNull(p2.salutation, "p2.salutation")
		p2.salutation = CodeSalutation.MRS
		assertEquals(CodeSalutation.MRS, p2.salutation, "p2.salutation")

		assertEquals(2, hhA1.memberList.size, "members")
		assertEquals(p1, hhA1.memberList[0], "p1 by seqNr")
		assertEquals(p2, hhA1.memberList[1], "p2 by seqNr")
		assertEquals(p1, hhA1.memberList.getById(p1Id), "p1 by id")
		assertEquals(p2, hhA1.memberList.getById(p2Id), "p2 by id")

		p1.spouse = p2
		p2.spouseId = p1.id
		assertEquals(p2, p1.spouse, "p1.spouse")
		assertEquals(p1, p2.spouse, "p2.spouse")

		hhA1.mainMemberId = p1.id
		assertEquals(p1, hhA1.mainMember, "hh.mainMember")

		hhRepo.store(hhA1)

		val hhA2 = hhRepo.get(hhA1Id)
		assertNotNull(hhA2)
		assertNotSame(hhA1, hhA2, "different objs after load")
		assertEquals("HHA", hhA2.name, "name")

		assertEquals(1, hhA2.labelSet.size, "labelSet.a")
		assertFalse(hhA2.labelSet.has(CodeLabel.A), "labelSet.b")
		assertTrue(hhA2.labelSet.has(CodeLabel.B), "labelSet.c")

		assertEquals(1, hhA2.userSet.size, "userSet.a")
		assertFalse(hhA2.userSet.has(user1.id), "userSet.b")
		assertTrue(hhA2.userSet.has(user2.id), "userSet.c")

		assertEquals(2, hhA2.memberList.size, "members")

		val p12 = hhA2.memberList[0]
		assertNotNull(p12)
		assertEquals(CodeSalutation.MR, p12.salutation, "p1.salutation")
		assertEquals("Martin", p12.name, "p1.name")

		val p22 = hhA2.memberList[1]
		assertNotNull(p22)
		assertEquals(CodeSalutation.MRS, p22.salutation, "p2.salutation")
		assertEquals("Elena", p22.name, "p2.name")

		assertEquals(p12, hhA2.memberList[0], "p1 by seqNr")
		assertEquals(p22, hhA2.memberList[1], "p2 by seqNr")
		assertEquals(p12, hhA2.memberList.getById(p1Id), "p1 by id")
		assertEquals(p22, hhA2.memberList.getById(p2Id), "p2 by id")

		assertEquals(p22, p12.spouse, "p1.spouse")
		assertEquals(p12, p22.spouse, "p2.spouse")
		assertEquals(p12, hhA2.mainMember, "hh.mainMember")

		// Verify responsibleUser persisted correctly
		assertEquals(user1.id, hhA2.responsibleUserId, "responsibleUser persisted by id")
		assertEquals(user1.id, hhA2.responsibleUser?.id, "responsibleUser persisted by value")

		assertEquals(
			1,
			hhRepo
				.find(
					query {
						filter { "objTypeId" eq "objHousehold" }
					},
				).size,
			"1 hh",
		)
	}

	@Test
	fun testSetValueByPath() {
		val hhB1 = hhRepo.create()

		hhB1.setValueByPath("salutation", CodeSalutation.MR)
		assertEquals(CodeSalutation.MR, hhB1.salutation, "salutation by path")

		val name = "HHB"
		hhB1.setValueByPath("name", name)
		assertEquals(name, hhB1.name, "name by path")

		hhB1.setValueByPath("responsibleUserId", user2.id)
		assertEquals(user2.id, hhB1.responsibleUserId, "responsible user by path")
	}

	@Test
	fun testCloseLifecycle() {
		// Create and store a household
		val hh = hhRepo.create()
		hh.name = "TestCloseHousehold"
		hhRepo.store(hh)
		val hhId = hh.id

		// Verify not closed initially
		assertNull(hh.meta.closedByUserId, "closedByUserId should be null before close")
		assertNull(hh.meta.closedAt, "closedAt should be null before close")

		// Load a fresh copy for closing (to get a non-frozen instance)
		val hhToClose = hhRepo.load(hhId)

		// Capture sequence numbers before close
		val hhBase = hhToClose as ObjBase
		val beforeCloseSeqNrBefore = hhBase.doBeforeCloseSeqNr
		val closeSeqNrBefore = hhBase.doCloseSeqNr
		val afterCloseSeqNrBefore = hhBase.doAfterCloseSeqNr

		// Close the household
		hhRepo.close(hhToClose)

		// Verify all lifecycle callbacks were executed (sequence numbers incremented)
		assertEquals(beforeCloseSeqNrBefore + 1, hhBase.doBeforeCloseSeqNr, "doBeforeClose was called")
		assertEquals(closeSeqNrBefore + 1, hhBase.doCloseSeqNr, "doClose was called")
		assertEquals(afterCloseSeqNrBefore + 1, hhBase.doAfterCloseSeqNr, "doAfterClose was called")

		// Verify callbacks were executed in the correct order
		// Since each callback increments its counter by 1, and they run sequentially,
		// we verify the state after close reflects all three were called
		assertTrue(hhBase.doBeforeCloseSeqNr > beforeCloseSeqNrBefore, "doBeforeClose executed")
		assertTrue(hhBase.doCloseSeqNr > closeSeqNrBefore, "doClose executed")
		assertTrue(hhBase.doAfterCloseSeqNr > afterCloseSeqNrBefore, "doAfterClose executed")

		// Verify close state was set on the object
		assertEquals(sessionContext.userId, hhToClose.meta.closedByUserId, "closedByUserId set")
		assertNotNull(hhToClose.meta.closedAt, "closedAt set")

		// Objects are not physically deleted - reload and verify close state persisted
		val hhReloaded = hhRepo.get(hhId)
		assertNotNull(hhReloaded, "closed object can still be loaded")
		assertEquals("TestCloseHousehold", hhReloaded.name, "name persisted")
		assertEquals(sessionContext.userId, hhReloaded.meta.closedByUserId, "closedByUserId persisted")
		assertNotNull(hhReloaded.meta.closedAt, "closedAt persisted")
	}

	@Test
	fun testAggregateIdPropertyEvents() {
		// Aggregate creation fires an "add" event with the aggregate path
		val collector = PropertyChangeCollector()
		val hh = hhRepo.create()
		(hh as AggregateSPI).addPropertyChangeListener(collector)

		// The creation event was already fired before we attached the listener,
		// so let's verify the ID is set and the aggregate is functional
		assertNotNull(hh.id, "aggregate id is set")

		// Verify no events were captured (listener attached after create)
		collector.assertEventCount(0)

		// Now test that we can capture events on a fresh aggregate
		val collector2 = PropertyChangeCollector()
		val hh2 = hhRepo.create()
		// Note: The "add" event for the aggregate is fired in doAfterCreate before we can attach a listener
		// This is by design - the ID property skips doBeforeSet because the path depends on the ID
		(hh2 as AggregateSPI).addPropertyChangeListener(collector2)
		collector2.assertEventCount(0)
	}

	@Test
	fun testReferenceIdVariantsEvents() {
		val (hh, collector) = createHouseholdWithCollector()

		// Setting by ID fires an event
		hh.responsibleUserId = user1.id
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".responsibleUser", user1.id, null)

		collector.clear()

		// Setting by value fires an equivalent event
		hh.responsibleUser = user2
		collector.assertEventCount(1)
		collector.assertEvent(0, "replace", ".responsibleUser", user2.id, user1.id)

		collector.clear()

		// Setting by ID again with replace
		hh.responsibleUserId = user1.id
		collector.assertEventCount(1)
		collector.assertEvent(0, "replace", ".responsibleUser", user1.id, user2.id)

		collector.clear()

		// Same applies for PartReferenceProperty - test with mainMember
		val p1 = hh.memberList.add()
		val p2 = hh.memberList.add()
		collector.clear()

		// Set by ID
		hh.mainMemberId = p1.id
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".mainMember", p1.id, null)

		collector.clear()

		// Set by value
		hh.mainMember = p2
		collector.assertEventCount(1)
		collector.assertEvent(0, "replace", ".mainMember", p2.id, p1.id)
	}

	@Test
	fun testBasePropertyEvents() {
		val (hh, collector) = createHouseholdWithCollector()

		// Set a new value (add)
		hh.name = "TestName"
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".name", "TestName", null)

		collector.clear()

		// Replace value
		hh.name = "NewName"
		collector.assertEventCount(1)
		collector.assertEvent(0, "replace", ".name", "NewName", "TestName")

		collector.clear()

		// Set to null (this is still a "replace" because there was an old value)
		hh.name = null
		collector.assertEventCount(1)
		collector.assertEvent(0, "replace", ".name", null, "NewName")

		collector.clear()

		// Setting same value should not fire event
		hh.name = null
		collector.assertEventCount(0)

		// Test literalId property as well
		hh.literalId = "LIT-001"
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".literalId", "LIT-001", null)

		collector.clear()

		hh.literalId = "LIT-002"
		collector.assertEventCount(1)
		collector.assertEvent(0, "replace", ".literalId", "LIT-002", "LIT-001")
	}

	@Test
	fun testEnumPropertyEvents() {
		val (hh, collector) = createHouseholdWithCollector()

		// Set enum value (add) - enum IDs are lowercase
		hh.salutation = CodeSalutation.MR
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".salutation", "mr", null)

		collector.clear()

		// Replace enum value
		hh.salutation = CodeSalutation.MRS
		collector.assertEventCount(1)
		collector.assertEvent(0, "replace", ".salutation", "mrs", "mr")

		collector.clear()

		// Set to null
		hh.salutation = null
		collector.assertEventCount(1)
		collector.assertEvent(0, "replace", ".salutation", null, "mrs")

		collector.clear()

		// Setting same null value should not fire event
		hh.salutation = null
		collector.assertEventCount(0)

		// Note: Part properties do NOT fire events through the listener
		// (PartBase.doAfterSet doesn't call fireFieldSetChange)
		// This test only covers aggregate-level enum properties
	}

	@Test
	fun testAggregateReferencePropertyEvents() {
		val (hh, collector) = createHouseholdWithCollector()

		// Add reference
		hh.responsibleUserId = user1.id
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".responsibleUser", user1.id, null)

		collector.clear()

		// Replace reference
		hh.responsibleUserId = user2.id
		collector.assertEventCount(1)
		collector.assertEvent(0, "replace", ".responsibleUser", user2.id, user1.id)

		collector.clear()

		// Clear reference by setting to null
		hh.responsibleUser = null
		collector.assertEventCount(1)
		collector.assertEvent(0, "replace", ".responsibleUser", null, user2.id)

		collector.clear()

		// Setting same null should not fire event
		hh.responsibleUser = null
		collector.assertEventCount(0)
	}

	@Test
	fun testPartReferencePropertyEvents() {
		val (hh, collector) = createHouseholdWithCollector()

		// Create some members first
		val p1 = hh.memberList.add()
		val p2 = hh.memberList.add()
		collector.clear()

		// Set mainMember (add)
		hh.mainMemberId = p1.id
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".mainMember", p1.id, null)

		collector.clear()

		// Replace mainMember
		hh.mainMember = p2
		collector.assertEventCount(1)
		collector.assertEvent(0, "replace", ".mainMember", p2.id, p1.id)

		collector.clear()

		// Clear mainMember
		hh.mainMember = null
		collector.assertEventCount(1)
		collector.assertEvent(0, "replace", ".mainMember", null, p2.id)
	}

	@Test
	fun testEnumSetPropertyEvents() {
		val (hh, collector) = createHouseholdWithCollector()

		// Add first item - enum IDs are lowercase
		hh.labelSet.add(CodeLabel.A)
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".labelSet", "a", null)

		collector.clear()

		// Add second item
		hh.labelSet.add(CodeLabel.B)
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".labelSet", "b", null)

		collector.clear()

		// Adding duplicate should not fire event
		hh.labelSet.add(CodeLabel.A)
		collector.assertEventCount(0)

		// Remove item
		hh.labelSet.remove(CodeLabel.A)
		collector.assertEventCount(1)
		collector.assertEvent(0, "remove", ".labelSet", "a", null)

		collector.clear()

		// Remove non-existent item should not fire event
		hh.labelSet.remove(CodeLabel.A)
		collector.assertEventCount(0)

		hh.labelSet.add(CodeLabel.A)
		hh.labelSet.add(CodeLabel.B)
		hh.labelSet.add(CodeLabel.C)

		collector.clear()
		hh.labelSet.clear()
		collector.assertEventCount(3)

		// Note: EnumSetPropertyImpl.clear() has a ConcurrentModificationException bug
		// when iterating and removing - skipping clear() test until fixed
	}

	@Test
	fun testReferenceSetPropertyEvents() {
		val (hh, collector) = createHouseholdWithCollector()

		// Add first reference
		hh.userSet.add(user1.id)
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".userSet", user1.id, null)

		collector.clear()

		// Add second reference
		hh.userSet.add(user2.id)
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".userSet", user2.id, null)

		collector.clear()

		// Adding duplicate should not fire event
		hh.userSet.add(user1.id)
		collector.assertEventCount(0)

		// Remove reference
		hh.userSet.remove(user1.id)
		collector.assertEventCount(1)
		collector.assertEvent(0, "remove", ".userSet", user1.id, null)

		collector.clear()

		// Remove non-existent should not fire event
		hh.userSet.remove(user1.id)
		collector.assertEventCount(0)

		// Clear fires remove events
		hh.userSet.clear()
		collector.assertEventCount(1) // Only user2 was left
		collector.assertEvent(0, "remove", ".userSet", user2.id, null)
	}

	@Test
	fun testPartListPropertyEvents() {
		val (hh, collector) = createHouseholdWithCollector()

		// Add part - fireEntityAddedChange uses truncated path (aggregate path)
		val p1 = hh.memberList.add()
		collector.assertEventCount(1)
		// The path is truncated to the aggregate path (ends with ')'), value is the part ID
		collector.assertEvent(0, "add", ".memberList[0]", p1.id, null)

		collector.clear()

		// Add second part
		val p2 = hh.memberList.add()
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".memberList[1]", p2.id, null)

		collector.clear()

		p1.name = "Martin"
		collector.assertEventCount(1) // No event for part property change
		collector.assertEvent(0, "add", ".memberList[0].name", "Martin", null)

		collector.clear()
		hh.memberList.remove(p1.id)
		collector.assertEventCount(1)
		collector.assertEvent(0, "remove", ".memberList[0]", null, p1.id)

		collector.clear()
		hh.memberList.remove(p2.id)
		collector.assertEventCount(1)
		collector.assertEvent(0, "remove", ".memberList[0]", null, p2.id)

		val p3 = hh.memberList.add()
		val p4 = hh.memberList.add()
		collector.clear()
		hh.memberList.clear()
		collector.assertEventCount(2)
		collector.assertEvent(0, "remove", ".memberList[0]", null, p3.id)
		collector.assertEvent(1, "remove", ".memberList[0]", null, p4.id)
	}

	// Members by Role Tests

	@Test
	fun testMembersByRoleCRUD() {
		val hh = hhRepo.create()

		// Initially no members assigned to roles
		assertEquals(0, hh.membersByRole.size, "no roles assigned initially")
		assertTrue(hh.membersByRole.isEmpty(), "membersByRole is empty")
		assertFalse(hh.membersByRole.containsKey("husband"), "husband role not assigned")

		// Assign husband role
		val husband = hh.membersByRole.add("husband")
		husband.salutation = CodeSalutation.MR
		husband.name = "Martin"
		assertEquals(1, hh.membersByRole.size, "one role assigned")
		assertFalse(hh.membersByRole.isEmpty(), "membersByRole not empty")
		assertTrue(hh.membersByRole.containsKey("husband"), "husband role exists")
		assertEquals(husband, hh.membersByRole["husband"], "can retrieve husband by role")

		// Assign wife role
		val wife = hh.membersByRole.add("wife")
		wife.salutation = CodeSalutation.MRS
		wife.name = "Elena"
		assertEquals(2, hh.membersByRole.size, "two roles assigned")
		assertTrue(hh.membersByRole.containsKey("wife"), "wife role exists")
		assertEquals(wife, hh.membersByRole["wife"], "can retrieve wife by role")

		// roleOf returns the role for the member
		assertEquals("husband", hh.membersByRole.keyOf(husband), "husband has correct role")
		assertEquals("wife", hh.membersByRole.keyOf(wife), "wife has correct role")

		// Check member presence
		assertTrue(hh.membersByRole.contains(husband), "household contains husband")
		assertTrue(hh.membersByRole.containsValue(husband), "containsValue finds husband")
		assertTrue(hh.membersByRole.contains(wife), "household contains wife")

		// Access all roles and members
		assertTrue(hh.membersByRole.keys.contains("husband"), "roles include husband")
		assertTrue(hh.membersByRole.keys.contains("wife"), "roles include wife")
		assertTrue(hh.membersByRole.values.contains(husband), "members include husband")
		assertTrue(hh.membersByRole.values.contains(wife), "members include wife")

		// Remove husband role
		hh.membersByRole.remove("husband")
		assertEquals(1, hh.membersByRole.size, "one role remaining")
		assertFalse(hh.membersByRole.containsKey("husband"), "husband role removed")
		assertTrue(hh.membersByRole.containsKey("wife"), "wife role still exists")

		// Remove wife by member reference
		hh.membersByRole.remove(wife)
		assertEquals(0, hh.membersByRole.size, "no roles remaining")
		assertTrue(hh.membersByRole.isEmpty(), "membersByRole is empty again")

		// Add advisory roles and test clear
		hh.membersByRole.add("councillor")
		hh.membersByRole.add("advisor")
		assertEquals(2, hh.membersByRole.size, "advisory roles assigned")

		hh.membersByRole.clear()
		assertEquals(0, hh.membersByRole.size, "all roles cleared")
		assertTrue(hh.membersByRole.isEmpty(), "membersByRole empty after clear")
	}

	@Test
	fun testMembersByRoleRejectsDuplicateRole() {
		val hh = hhRepo.create()

		hh.membersByRole.add("husband")

		// Cannot assign same role twice
		assertThrows(IllegalArgumentException::class.java) {
			hh.membersByRole.add("husband")
		}
	}

	@Test
	fun testMembersByRoleRejectsUnknownRole() {
		val hh = hhRepo.create()

		// Accessing unassigned role throws
		assertThrows(IllegalArgumentException::class.java) {
			hh.membersByRole["guardian"]
		}

		// Removing unassigned role throws
		assertThrows(IllegalArgumentException::class.java) {
			hh.membersByRole.remove("guardian")
		}
	}

	@Test
	fun testMembersByRolePathAccessGet() {
		val hh = hhRepo.create()

		val husband = hh.membersByRole.add("husband")
		husband.name = "Martin"
		husband.salutation = CodeSalutation.MR

		// Access member property by path with bracket syntax
		val name: String? = hh.getValueByPath("membersByRole[\"husband\"].name")
		assertEquals("Martin", name, "husband name via bracket path")

		// Access member property by path with dot syntax
		val salutationId: String? = hh.getValueByPath("membersByRole.husband.salutationId")
		assertEquals("mr", salutationId, "husband salutation via dot path")
	}

	@Test
	fun testMembersByRolePathAccessRejectsUnknownRole() {
		val hh = hhRepo.create()

		// Accessing unassigned role via path throws
		assertThrows(IllegalArgumentException::class.java) {
			hh.getValueByPath<String>("membersByRole[\"guardian\"].name")
		}
	}

	@Test
	fun testMembersByRolePathAccessSetExisting() {
		val hh = hhRepo.create()

		val wife = hh.membersByRole.add("wife")

		// Update member property by path
		hh.setValueByPath("membersByRole[\"wife\"].name", "Elena")
		assertEquals("Elena", wife.name, "wife name updated via path")

		hh.setValueByPath("membersByRole.wife.salutationId", "mrs")
		assertEquals(CodeSalutation.MRS, wife.salutation, "wife salutation updated via path")
	}

	@Test
	fun testMembersByRolePathAccessAutoCreates() {
		val hh = hhRepo.create()

		assertEquals(0, hh.membersByRole.size, "no roles before")

		// Setting property via path auto-creates the role
		hh.setValueByPath("membersByRole[\"councillor\"].name", "Hans")

		assertEquals(1, hh.membersByRole.size, "councillor role created")
		assertTrue(hh.membersByRole.containsKey("councillor"), "councillor role exists")
		assertEquals("Hans", hh.membersByRole["councillor"].name, "councillor name set")
	}

	@Test
	fun testMembersByRoleEvents() {
		val (hh, collector) = createHouseholdWithCollector()

		// Adding member to role fires add event
		val husband = hh.membersByRole.add("husband")
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".membersByRole[\"husband\"]", husband.id, null)

		collector.clear()

		// Adding wife
		val wife = hh.membersByRole.add("wife")
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".membersByRole[\"wife\"]", wife.id, null)

		collector.clear()

		// Updating member property fires event with full path
		husband.name = "Martin"
		collector.assertEventCount(1)
		collector.assertEvent(0, "add", ".membersByRole[\"husband\"].name", "Martin", null)

		collector.clear()

		// Removing member from role fires remove event
		hh.membersByRole.remove("husband")
		collector.assertEventCount(1)
		collector.assertEvent(0, "remove", ".membersByRole[\"husband\"]", null, husband.id)

		collector.clear()

		// Clear fires remove events for each role
		val advisor = hh.membersByRole.add("advisor")
		collector.clear()
		hh.membersByRole.clear()
		collector.assertEventCount(2)
		collector.assertEvent(0, "remove", ".membersByRole[\"wife\"]", null, wife.id)
		collector.assertEvent(1, "remove", ".membersByRole[\"advisor\"]", null, advisor.id)
	}

	@Test
	fun testMembersByRolePersistence() {
		// Create household with members in roles
		val hh1 = hhRepo.create()
		hh1.name = "Smith Family"

		val husband = hh1.membersByRole.add("husband")
		husband.salutation = CodeSalutation.MR
		husband.name = "John"
		val husbandId = husband.id

		val wife = hh1.membersByRole.add("wife")
		wife.salutation = CodeSalutation.MRS
		wife.name = "Jane"
		val wifeId = wife.id

		hhRepo.store(hh1)
		val hhId = hh1.id

		// Load and verify persistence
		val hh2 = hhRepo.get(hhId)
		assertNotNull(hh2)
		assertNotSame(hh1, hh2, "different instance after load")
		assertEquals("Smith Family", hh2.name, "household name persisted")

		// Verify members by role persisted correctly
		assertEquals(2, hh2.membersByRole.size, "both roles persisted")
		assertTrue(hh2.membersByRole.containsKey("husband"), "husband role persisted")
		assertTrue(hh2.membersByRole.containsKey("wife"), "wife role persisted")

		val husbandLoaded = hh2.membersByRole["husband"]
		assertEquals(husbandId, husbandLoaded.id, "husband id matches")
		assertEquals(CodeSalutation.MR, husbandLoaded.salutation, "husband salutation persisted")
		assertEquals("John", husbandLoaded.name, "husband name persisted")

		val wifeLoaded = hh2.membersByRole["wife"]
		assertEquals(wifeId, wifeLoaded.id, "wife id matches")
		assertEquals(CodeSalutation.MRS, wifeLoaded.salutation, "wife salutation persisted")
		assertEquals("Jane", wifeLoaded.name, "wife name persisted")
	}

	// Computed Property Tests

	@Test
	fun testComputedBaseProperty() {
		val hh = hhRepo.create()

		// Initially no members, computed memberCount should be 0
		assertEquals(0, hh.memberCount, "memberCount is 0 initially")

		// Add a member
		hh.memberList.add()
		assertEquals(1, hh.memberCount, "memberCount is 1 after adding member")

		// Add more members
		hh.memberList.add()
		hh.memberList.add()
		assertEquals(3, hh.memberCount, "memberCount is 3 after adding more members")

		// Remove a member
		val firstId = hh.memberList[0].id
		hh.memberList.remove(firstId)
		assertEquals(2, hh.memberCount, "memberCount is 2 after removing member")

		// Clear all members
		hh.memberList.clear()
		assertEquals(0, hh.memberCount, "memberCount is 0 after clearing")
	}

	@Test
	fun testComputedPartReferenceProperty() {
		val hh = hhRepo.create()

		// Initially no members, computed firstMember should be null
		assertNull(hh.firstMember, "firstMember is null initially")

		// Add first member
		val p1 = hh.memberList.add()
		p1.name = "First"
		assertEquals(p1, hh.firstMember, "firstMember is p1")
		assertEquals("First", hh.firstMember?.name, "firstMember name is First")

		// Add second member - firstMember should still be p1
		val p2 = hh.memberList.add()
		p2.name = "Second"
		assertEquals(p1, hh.firstMember, "firstMember is still p1")

		// Remove first member - firstMember should now be p2
		hh.memberList.remove(p1.id)
		assertEquals(p2, hh.firstMember, "firstMember is now p2")
		assertEquals("Second", hh.firstMember?.name, "firstMember name is Second")

		// Clear all - firstMember should be null again
		hh.memberList.clear()
		assertNull(hh.firstMember, "firstMember is null after clearing")
	}

	@Test
	fun testComputedPropertyIsReadOnly() {
		val hh = hhRepo.create()
		val hhEntity = hh as dddrive.ddd.property.model.EntityWithProperties

		// Verify computed properties are read-only (isWritable = false)
		val memberCountProperty = hhEntity.getProperty("memberCount", Any::class)
		assertFalse(memberCountProperty.isWritable, "memberCount property is not writable")

		val firstMemberProperty = hhEntity.getProperty("firstMember", Any::class)
		assertFalse(firstMemberProperty.isWritable, "firstMember property is not writable")

		// Verify setting computed property throws exception
		assertThrows(IllegalArgumentException::class.java) {
			(memberCountProperty as dddrive.ddd.property.model.BaseProperty<Int>).value = 5
		}

		hh.memberList.add() // Need at least one member for firstMember to return non-null
		assertThrows(IllegalArgumentException::class.java) {
			(firstMemberProperty as dddrive.ddd.property.model.PartReferenceProperty<*, *>).id = 999
		}
	}

	@Test
	fun testComputedPropertiesAppearInPropertiesList() {
		val hh = hhRepo.create()
		val hhEntity = hh as dddrive.ddd.property.model.EntityWithProperties

		// Verify computed properties appear in the properties list
		val propertyNames = hhEntity.properties.map { it.name }
		assertTrue(propertyNames.contains("memberCount"), "properties contains memberCount")
		assertTrue(propertyNames.contains("firstMember"), "properties contains firstMember")
	}

}
