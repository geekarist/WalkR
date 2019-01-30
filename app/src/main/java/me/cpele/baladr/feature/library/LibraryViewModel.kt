package me.cpele.baladr.feature.library

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import me.cpele.baladr.R
import me.cpele.baladr.common.business.PlaylistRepository
import me.cpele.baladr.feature.library.item.GroupLibItem
import me.cpele.baladr.feature.library.item.LibItem
import me.cpele.baladr.feature.library.item.PlaylistLibItem
import java.util.*

class LibraryViewModel(
        private val app: Application,
        playlistRepository: PlaylistRepository
) : AndroidViewModel(app) {

    class Factory(
            private val app: Application,
            private val playlistRepository: PlaylistRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(LibraryViewModel(app, playlistRepository)) as T
        }
    }

    val libItems: LiveData<List<LibItem>> = Transformations.map(playlistRepository.findAll()) { playlists ->
        val startOfToday by lazy { findStartOfToday() }
        val startOfWeek by lazy { findStartOfWeek() }
        val startOfMonth by lazy { findStartOfMonth() }
        val startOfYear by lazy { findStartOfYear() }
        val groupedPlaylists = playlists.reversed().groupBy {
            when {
                it.date.after(startOfToday) -> Group.TODAY
                it.date.after(startOfWeek) -> Group.EARLIER_THIS_WEEK
                it.date.after(startOfMonth) -> Group.EARLIER_THIS_MONTH
                it.date.after(startOfYear) -> Group.EARLIER_THIS_YEAR
                else -> Group.EARLIER
            }
        }
        groupedPlaylists.keys.flatMap { group: Group ->
            val result = mutableListOf(GroupLibItem(app.getString(group.titleRes)) as LibItem)
            groupedPlaylists[group]?.let { playlistsForGroup ->
                result.addAll(playlistsForGroup.map { PlaylistLibItem(it) })
            }
            result
        }
    }

    private fun findStartOfYear(): Date? {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    private fun findStartOfMonth(): Date? {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    private fun findStartOfWeek(): Date? {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    private fun findStartOfToday(): Date {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    private enum class Group(@StringRes val titleRes: Int) {
        TODAY(R.string.lib_item_group_today),
        EARLIER_THIS_WEEK(R.string.lib_item_group_week),
        EARLIER_THIS_MONTH(R.string.lib_item_group_month),
        EARLIER_THIS_YEAR(R.string.lib_item_group_year),
        EARLIER(R.string.lib_item_group_earlier);
    }
}
