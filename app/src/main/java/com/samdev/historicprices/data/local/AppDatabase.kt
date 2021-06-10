package com.samdev.historicprices.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.samdev.historicprices.data.ioThread
import com.samdev.historicprices.data.local.dao.PriceDao
import com.samdev.historicprices.data.local.dao.ProductDao
import com.samdev.historicprices.data.local.dao.ProductWithPricesDao
import com.samdev.historicprices.data.local.entities.Price
import com.samdev.historicprices.data.local.entities.Product
import com.samdev.historicprices.data.local.entities.ProductWithPrices
import timber.log.Timber

/**
 * @author Sam
 *
 * Database class created as singleton so we only have one open database
 * instance at a time.
 *
 */
@Database(
    entities = [
        Product::class,
        Price::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // expose the each data access object through abstract getters
    abstract fun productDao(): ProductDao
    abstract fun priceDao(): PriceDao
    abstract fun productWithPricesDao(): ProductWithPricesDao


    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpharma_database"
                )// .addCallback(prepopulateDataCallback(context))
                    .build()

                INSTANCE = instance

                instance
            }
        }


        private fun prepopulateDataCallback(context: Context): Callback =
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    ioThread {
                        // p1
                        val product = Product(id = 1,"Exforge 10mg")
                        val prices = mutableListOf(
                            Price(1, 10.99,1,"2019-01-01T17:16:32+00:00"),
                            Price(2, 9.20, 1, "2018-11-01T17:16:32+00:00"),
                        )

                        // p2
                        val product2 = Product(id = 2,"Exforge 20mg")
                        val prices2 = mutableListOf(
                            Price(3, 12.00,2,"2019-01-01T17:16:32+00:00"),
                            Price(4, 13.20, 2, "2018-11-01T17:16:32+00:00"),
                        )

                        // p3
                        val product3 = Product(id = 3,"Paracetamol 20MG")
                        val prices3 = mutableListOf(
                            Price(5, 5.00,3,"2017-01-01T17:16:32+ 00:00"),
                            Price(6, 13.20, 3, "2018-11-01T17:16:32+00:00"),
                        )

                        val tempItems = listOf(
                            ProductWithPrices(product, prices),
                            ProductWithPrices(product2, prices2),
                            ProductWithPrices(product3, prices3),
                        )

                        Timber.e("db created, attempting prepopulation")
                        for (item in tempItems) {
                            // getDatabase(context).productWithPricesDao().updateProductWithPrices(item.product, item.prices)
                        }
                    }
                }
            }

    }

}