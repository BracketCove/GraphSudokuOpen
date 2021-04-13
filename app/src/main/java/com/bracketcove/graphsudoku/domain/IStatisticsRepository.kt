package com.bracketcove.graphsudoku.domain

/**
 * Repository (Data I/O mechanism like Databases or Network Adapters)
 * Facade Pattern
 *
 * - A facade hides the details (implementation) of a sub-system from the client
 * Why?????????????
 * - If you have a significant boundary in your application,
 * it is generally advisable to use a repository
 * - By using an interface/abstract class/protocol, it makes it easy to:
 *  - Build the client in isolation of the repository implementation
 *  - Change the repository implementation in isolation of the client
 *  - Allows you to provide a test implementation without having to change the client
 *
 *
 * (MVPVM: This is the boundary between the P and M
 *
 */
interface IStatisticsRepository {
    suspend fun getStatistics(onSuccess: (UserStatistics) -> Unit,
                              onError: (Exception) -> Unit)
    suspend fun updateStatistic(
        time: Long,
        diff: Difficulty,
        boundary: Int,
        onSuccess: (isRecord: Boolean) -> Unit,
        onError: (Exception) -> Unit
    )
}