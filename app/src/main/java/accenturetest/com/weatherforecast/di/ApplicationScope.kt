package accenturetest.com.weatherforecast.di

import javax.inject.Qualifier
import javax.inject.Scope

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Scope
annotation class ApplicationScope