package dddrive.test

import dddrive.ddd.path.getPropertyByPath
import dddrive.ddd.path.getValueByPath
import dddrive.ddd.path.relativePath
import dddrive.ddd.path.setValueByPath
import dddrive.ddd.property.model.BaseProperty
import dddrive.ddd.property.model.Property
import dddrive.domain.household.model.ObjHousehold
import dddrive.domain.household.model.ObjHouseholdRepository
import dddrive.domain.household.model.enums.CodeSalutation
import dddrive.domain.oe.model.ObjUser
import dddrive.domain.oe.model.ObjUserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * Tests for PathAccess extension functions using real domain entities.
 */
@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class PathAccessTest {

	@Autowired
	private lateinit var hhRepo: ObjHouseholdRepository

	@Autowired
	private lateinit var userRepo: ObjUserRepository

	private lateinit var household: ObjHousehold
	private lateinit var testUser: ObjUser

	@BeforeEach
	fun setUp() {
		// Create a test user for reference tests
		testUser = userRepo.getByEmail("pathtest@test.ch").orElseGet {
			val newUser = userRepo.create()
			newUser.name = "TestUser"
			newUser.email = "pathtest@test.ch"
			userRepo.store(newUser)
			newUser
		}

		// Create a household with populated properties
		household = createTestHousehold()
	}

	private fun createTestHousehold(): ObjHousehold {
		val hh = hhRepo.create()
		hh.name = "TestHousehold"
		hh.salutation = CodeSalutation.MR
		hh.responsibleUser = testUser

		// Add members to the list
		val member1 = hh.memberList.add()
		member1.name = "Member1"
		member1.salutation = CodeSalutation.MR

		val member2 = hh.memberList.add()
		member2.name = "Member2"
		member2.salutation = CodeSalutation.MRS

		// Set up spouse relationship
		member1.spouse = member2
		member2.spouse = member1

		// Set main member reference
		hh.mainMember = member1

		return hh
	}

	@Nested
	inner class SimplePropertyAccess {

		@Test
		fun `getValueByPath returns simple property value`() {
			val result: String? = household.getValueByPath("name")
			assertEquals("TestHousehold", result)
		}

		@Test
		fun `setValueByPath sets simple property value`() {
			household.setValueByPath("name", "UpdatedName")
			assertEquals("UpdatedName", household.getValueByPath("name"))
		}

		@Test
		fun `getPropertyByPath returns property object`() {
			val property: Property<String>? = household.getPropertyByPath("name")
			assertNotNull(property)
			assertEquals("TestHousehold", (property as? BaseProperty<*>)?.value)
		}
	}

	@Nested
	inner class EnumPropertyAccess {

		@Test
		fun `getValueByPath returns enum instance`() {
			val result: CodeSalutation? = household.getValueByPath("salutation")
			assertNotNull(result)
			assertEquals(CodeSalutation.MR, result)
		}

		@Test
		fun `getValueByPath with dot id returns enum ID string`() {
			val result: String? = household.getValueByPath("salutation.id")
			assertEquals("mr", result) // enum IDs are lowercase
		}

		@Test
		fun `getValueByPath with Id suffix returns enum ID string`() {
			val result: String? = household.getValueByPath("salutationId")
			assertEquals("mr", result) // enum IDs are lowercase
		}

		@Test
		fun `setValueByPath with Id suffix sets enum by ID`() {
			household.setValueByPath("salutationId", "mrs") // enum IDs are lowercase
			assertEquals("mrs", household.getValueByPath("salutationId"))
		}

		@Test
		fun `setValueByPath with dot id crashes`() {
			assertThrows(IllegalStateException::class.java) {
				household.setValueByPath("salutation.id", "mrs")
			}
		}
	}

	@Nested
	inner class ListPropertyAccess {

		@Test
		fun `getValueByPath with bracket syntax works`() {
			val result: String? = household.getValueByPath("memberList[0].name")
			assertEquals("Member1", result)
		}

		@Test
		fun `getValueByPath with dot syntax works`() {
			val result: String? = household.getValueByPath("memberList.0.name")
			assertEquals("Member1", result)
		}

		@Test
		fun `setValueByPath auto-expands list`() {
			household.setValueByPath("memberList[3].name", "Member4")
			val result: String? = household.getValueByPath("memberList[3].name")
			assertEquals("Member4", result)
		}

		@Test
		fun `getValueByPath returns null for missing index`() {
			val result: String? = household.getValueByPath("memberList[10].name")
			assertNull(result)
		}

		@Test
		fun `list access with enum id suffix works`() {
			household.setValueByPath("memberList[0].salutationId", "mrs") // lowercase
			assertEquals("mrs", household.getValueByPath("memberList[0].salutationId"))
		}

		@Test
		fun `list access with enum dot id works for getter`() {
			assertEquals("mr", household.getValueByPath("memberList[0].salutation.id")) // lowercase
		}
	}

	@Nested
	inner class ReferencePropertyAccess {

		@Test
		fun `reference id path`() {
			val p = household.getPropertyByPath<Any>("responsibleUser.id")!!
			assertEquals("responsibleUser.id", p.relativePath())
		}

		@Test
		fun `getValueByPath navigates through reference`() {
			val result: String? = household.getValueByPath("responsibleUser.name")
			assertEquals("TestUser", result)
		}

		@Test
		fun `getValueByPath with Id suffix returns reference ID`() {
			val result: Any? = household.getValueByPath("responsibleUserId")
			assertNotNull(result)
			assertEquals(testUser.id, result)
		}

		@Test
		fun `setValueByPath with Id suffix sets reference ID`() {
			// Create another user
			val anotherUser = userRepo.create()
			anotherUser.name = "AnotherUser"
			anotherUser.email = "another@test.ch"
			userRepo.store(anotherUser)

			household.setValueByPath("responsibleUserId", anotherUser.id)
			assertEquals(anotherUser.id, household.getValueByPath("responsibleUserId"))
		}

		@Test
		fun `getValueByPath with dot id returns reference ID`() {
			val result: Any? = household.getValueByPath("responsibleUser.id")
			assertNotNull(result)
			assertEquals(testUser.id, result)
		}

		@Test
		fun `setValueByPath with dot id crashes`() {
			assertThrows(IllegalStateException::class.java) {
				household.setValueByPath("responsibleUser.id", "new-id")
			}
		}
	}

	@Nested
	inner class PartReferencePropertyAccess {

		@Test
		fun `getValueByPath navigates through part reference`() {
			val result: String? = household.getValueByPath("mainMember.name")
			assertEquals("Member1", result)
		}

		@Test
		fun `setValueByPath navigates through part reference`() {
			household.setValueByPath("mainMember.name", "UpdatedMember")
			assertEquals("UpdatedMember", household.getValueByPath("mainMember.name"))
		}

		@Test
		fun `getValueByPath with Id suffix returns part reference ID`() {
			val result: Int? = household.getValueByPath("mainMemberId")
			assertNotNull(result)
			assertEquals(household.memberList[0].id, result)
		}

		@Test
		fun `setValueByPath with Id suffix sets part reference ID`() {
			val member2Id = household.memberList[1].id
			household.setValueByPath("mainMemberId", member2Id)
			assertEquals(member2Id, household.getValueByPath("mainMemberId"))
		}
	}

	@Nested
	inner class NullHandling {

		@Test
		fun `getValueByPath returns null for null reference`() {
			household.responsibleUser = null
			val result: String? = household.getValueByPath("responsibleUser.name")
			assertNull(result)
		}

		@Test
		fun `setValueByPath crashes for null reference`() {
			household.responsibleUser = null
			assertThrows(IllegalStateException::class.java) {
				household.setValueByPath("responsibleUser.name", "value")
			}
		}

		@Test
		fun `getValueByPath returns null for null part reference`() {
			household.mainMember = null
			val result: String? = household.getValueByPath("mainMember.name")
			assertNull(result)
		}
	}

	@Nested
	inner class ComplexPaths {

		@Test
		fun `complex nested path through list and part reference`() {
			// memberList[0].spouse is Member2
			household.setValueByPath("memberList[0].spouse.salutationId", "mr") // lowercase
			val result: String? = household.getValueByPath("memberList[0].spouse.salutationId")
			assertEquals("mr", result)
		}

		@Test
		fun `multiple list navigations work`() {
			assertEquals("Member1", household.getValueByPath("memberList[0].name"))
			assertEquals("Member2", household.getValueByPath("memberList[1].name"))
		}

		@Test
		fun `nested navigation through spouse reference`() {
			val result: String? = household.getValueByPath("memberList[0].spouse.name")
			assertEquals("Member2", result)
		}
	}

	@Nested
	inner class LiteralIdProperty {

		@Test
		fun `literal literalId property takes precedence over Id suffix`() {
			household.literalId = "literal-value"
			val result: String? = household.getValueByPath("literalId")
			assertEquals("literal-value", result)
		}
	}

	@Nested
	inner class ErrorCases {

		@Test
		fun `getValueByPath throws for invalid property`() {
			assertThrows(IllegalArgumentException::class.java) {
				household.getValueByPath<Any>("nonexistent")
			}
		}

		@Test
		fun `setValueByPath throws for invalid property`() {
			assertThrows(IllegalArgumentException::class.java) {
				household.setValueByPath("nonexistent", "value")
			}
		}

		@Test
		fun `navigation through non-reference property throws`() {
			assertThrows(IllegalStateException::class.java) {
				household.getValueByPath<Any>("name.something")
			}
		}
	}

}
