package dddrive.test

import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.base.ObjBase
import dddrive.ddd.core.model.AggregateSPI
import dddrive.ddd.path.setValueByPath
import dddrive.ddd.property.model.PropertyChangeListener
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class HouseholdMemTest : PropertyChangeListener {

  @Autowired private lateinit var sessionContext: SessionContext

  @Autowired private lateinit var tenantRepo: ObjTenantRepository

  @Autowired private lateinit var userRepo: ObjUserRepository

  @Autowired private lateinit var hhRepo: ObjHouseholdRepository

  private lateinit var user1: ObjUser
  private lateinit var user2: ObjUser

  override fun propertyChange(
          op: String,
          path: String,
          value: String?,
          oldValue: String?,
          isInCalc: Boolean,
  ) {
    println("{ op: $op, path: $path, value: $value, oldValue: $oldValue, isInCalc: $isInCalc }")
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
    assertEquals(0, hhRepo.getByForeignKey("objTypeId", "obj_household").size, "0 hh")

    val hhA1 = hhRepo.create()
    (hhA1 as AggregateSPI).addPropertyChangeListener(this)
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

    assertEquals(1, hhRepo.getByForeignKey("objTypeId", "objHousehold").size, "1 hh")
  }

  @Test
  fun testSetValueByPathy() {
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
}
