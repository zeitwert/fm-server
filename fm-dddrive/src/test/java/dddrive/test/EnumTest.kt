package dddrive.test

import dddrive.ddd.model.RepositoryDirectory
import dddrive.domain.household.model.enums.CodeLabel
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class EnumTest {

	@Autowired
	lateinit var directory: RepositoryDirectory

	@Test
	fun testEnumerations() {
		assertEquals(3, CodeLabel.items.size, "item count should match")
		val labelEnum = directory.getEnumeration("household", "codeLabel")
		assertEquals(CodeLabel.items.size, labelEnum.items.size, "item count should match")
	}

}
