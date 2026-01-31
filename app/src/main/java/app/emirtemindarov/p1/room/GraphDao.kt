package app.emirtemindarov.p1.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GraphDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(graph: GraphEntity): Long      // нужно возвращать Long, но не Int

    @Query("SELECT * FROM GraphEntity WHERE graphId = :id")
    suspend fun getById(id: String): GraphEntity?

    @Query("SELECT * FROM GraphEntity ORDER BY createdAt DESC")
    fun getAll(): Flow<List<GraphEntity>>

    @Query("DELETE FROM GraphEntity WHERE graphId = :id")
    suspend fun delete(id: String): Int      // нужно возвращать хоть что-то, поэтому просто Int
}
