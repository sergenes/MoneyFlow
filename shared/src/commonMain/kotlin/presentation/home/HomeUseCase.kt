package presentation.home

import kotlinx.coroutines.flow.StateFlow

interface HomeUseCase {
    fun observeHomeModel(): StateFlow<HomeModel>
    fun computeData()
    suspend fun computeHomeDataSuspendable()
}