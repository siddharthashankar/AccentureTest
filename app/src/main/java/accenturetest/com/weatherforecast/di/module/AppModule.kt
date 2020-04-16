
package accenturetest.com.weatherforecast.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import accenturetest.com.weatherforecast.data.exceptions.AppErrorHandler
import accenturetest.com.weatherforecast.domain.ErrorHandler
import accenturetest.com.weatherforecast.framework.AppDatabase
import accenturetest.com.weatherforecast.util.Constants
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideAppContext(): Context = app

    @Provides
    @Singleton
    fun provideGlobalPreferences(): SharedPreferences = app.getSharedPreferences(
            Constants.Keys.GLOBAL_PREFS_NAME,
            Context.MODE_PRIVATE
    )

    @Provides
    @Singleton
    @Named("internal_config")
    fun provideInternalConfigPrefs(): SharedPreferences = app.getSharedPreferences(
        Constants.INTERNAL_CONFIG_PREFS,
            Context.MODE_PRIVATE
    )

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase {
        val MIGRATION_2_TO_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE weathers ADD COLUMN lastUpdate INTEGER")
            }
        }
        val MIGRATION_3_TO_4 = object : Migration(3,4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE weathers ADD COLUMN humidity INTEGER")
                database.execSQL("ALTER TABLE weathers ADD COLUMN windSpeed REAL")
            }
        }
        return Room.databaseBuilder(app, AppDatabase::class.java, Constants.DATABASE_NAME)
                .addMigrations(MIGRATION_2_TO_3)
                .addMigrations(MIGRATION_3_TO_4)
                .build()
    }


    @Provides
    @Singleton
    fun provideConnectivityManager(): ConnectivityManager {
        return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideErrorHandler(): ErrorHandler = AppErrorHandler()
}