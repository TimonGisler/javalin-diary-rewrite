package entries

import helperClasses.PostgresContainerBaseTest
import org.junit.jupiter.api.Test

class GetEntriesOverviewTest: PostgresContainerBaseTest() {

    @Test
    fun `should return only the title and date NOT TEXT to save bandwidth`(){
        //TODO TGIS
    }

}