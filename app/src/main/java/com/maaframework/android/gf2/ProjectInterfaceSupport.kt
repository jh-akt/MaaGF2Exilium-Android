package com.maaframework.android.gf2

import com.maaframework.android.catalog.TaskOptionSupport
import com.maaframework.android.model.TaskDescriptor
import com.maaframework.android.model.TaskOptionDescriptor

object ProjectInterfaceSupport {
    fun taskSupportsResource(task: TaskDescriptor, resourceId: String?): Boolean {
        return TaskOptionSupport.taskSupportsResource(task, resourceId)
    }

    fun filterOptionsForResource(
        options: List<TaskOptionDescriptor>,
        resourceId: String?,
    ): List<TaskOptionDescriptor> {
        return TaskOptionSupport.filterOptionsForResource(options, resourceId)
    }

    fun defaultSelectionForOption(option: TaskOptionDescriptor): Set<String> {
        return TaskOptionSupport.defaultSelectionForOption(option)
    }

    fun collectInputValidationErrors(
        options: List<TaskOptionDescriptor>,
        selectedByOption: Map<String, Set<String>>,
        inputValuesByOption: Map<String, Map<String, String>>,
    ): Map<String, Map<String, String>> {
        return TaskOptionSupport.collectInputValidationErrors(
            options = options,
            selectedByOption = selectedByOption,
            inputValuesByOption = inputValuesByOption,
        )
    }
}
