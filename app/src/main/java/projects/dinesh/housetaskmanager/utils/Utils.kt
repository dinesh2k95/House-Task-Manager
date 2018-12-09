package projects.dinesh.housetaskmanager.utils

import org.joda.time.DateTime

/**
 * Created by Dinesh on 8/12/18.
 */

object Utils {

    const val TASK_DATE_FORMAT = "dd-MM-yyyy"

    fun formatGivenDateForPattern(milliseconds: Long?, requiredPattern: String): String {
        return DateTime(milliseconds).toString(requiredPattern)
    }
}