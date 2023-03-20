package com.ahr.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ahr.storyapp.data.local.database.StoryDatabase
import com.ahr.storyapp.data.local.entity.RemoteKeys
import com.ahr.storyapp.data.local.entity.StoryEntity
import com.ahr.storyapp.data.network.response.asEntity
import com.ahr.storyapp.data.network.service.StoryService
import com.haroldadmin.cnradapter.NetworkResponse

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val storyDatabase: StoryDatabase,
    private val storyService: StoryService,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try {
            when (val response = storyService.getStories(token, page)) {
                is NetworkResponse.Success -> {
                    val stories = response.body.listStory
                    storyDatabase.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            storyDatabase.remoteKeysDao().deleteRemoteKeys()
                            storyDatabase.storyDao().deleteStories()
                        }
                        val storyEntities = stories?.asEntity()
                        storyDatabase.storyDao().insertStories(storyEntities ?: emptyList())
                    }
                    val endOfPaginationReached = response.body.listStory?.isEmpty()
                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached == true) null else page + 1
                    val keys = response.body.listStory?.map { story ->
                        RemoteKeys(id = story.id, prevKey = prevKey, nextKey = nextKey)
                    } ?: emptyList()
                    storyDatabase.remoteKeysDao().insertAll(keys)
                    MediatorResult.Success(endOfPaginationReached = stories == null)
                }
                is NetworkResponse.Error -> {
                    MediatorResult.Error(Throwable(response.body?.message ?: response.error?.localizedMessage))
                }
            }
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }
}