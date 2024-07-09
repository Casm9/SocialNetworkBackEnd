package com.casm.data.util

sealed class ParentType(val type: Int) {
    data object Post: ParentType(0)
    data object Comment: ParentType(1)
    data object None: ParentType(2)

    companion object {
        fun fromType(type: Int): ParentType {
            return when(type) {
                0 -> Post
                1 -> Comment
                else -> None
            }
        }
    }
}