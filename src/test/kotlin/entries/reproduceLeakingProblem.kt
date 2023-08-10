package entries

import commonFunctionality.PostgresContainerBaseTest
import commonFunctionality.UserFunctionality
import kotlin.test.Test


class reproduceLeakingProblem: PostgresContainerBaseTest() { //extending PostgresContainerBaseTest fixes the problem somehow..

    @Test
    fun `A connection was leaked`() {
        val user1 = UserFunctionality()
        //more info in my obsidian notes under "A connection was leaked"

        //save entry and count how many entries now exist
        val newEntryId2 = user1.saveEntry(CreateEntryCommand("title", "text"))
        val newEntryId = user1.saveEntry(CreateEntryCommand("title", "text"))

    }

}