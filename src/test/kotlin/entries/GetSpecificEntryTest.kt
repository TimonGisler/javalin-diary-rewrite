package entries

import commonFunctionality.PostgresContainerBaseTest
import commonFunctionality.UserFunctionality
import org.junit.jupiter.api.fail
import kotlin.test.Test

class GetSpecificEntryTest: PostgresContainerBaseTest(){

    @Test
    fun `I am unable to get other persons entries`() {
        fail("not implemented")
    }

    @Test
    fun `After saving entry I should be able to retrieve it and it should contain correct text and title`() {
        val user1: UserFunctionality = UserFunctionality()
        val createEntryCommand: CreateEntryCommand = CreateEntryCommand("title", "text")
        val entryId: Int = user1.saveEntry(createEntryCommand)

        val entry: GetEntryCommandResponse? = user1.getEntry(entryId)
    }

    @Test
    fun `Entry should return with correct date`() {
        fail("not implemented")
    }
}