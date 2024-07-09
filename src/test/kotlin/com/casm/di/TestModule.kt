package com.casm.di

import com.casm.repository.user.FakeUserRepository
import org.koin.dsl.module

internal val testModule = module {
    single { FakeUserRepository() }
}