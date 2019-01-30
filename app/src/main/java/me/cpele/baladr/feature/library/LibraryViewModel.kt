package me.cpele.baladr.feature.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.cpele.baladr.common.business.PlaylistRepository
import me.cpele.baladr.feature.library.item.GroupLibItem
import me.cpele.baladr.feature.library.item.LibItem
import me.cpele.baladr.feature.library.item.PlaylistLibItem
import java.util.*

class LibraryViewModel(playlistRepository: PlaylistRepository) : ViewModel() {

    class Factory(private val playlistRepository: PlaylistRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(LibraryViewModel(playlistRepository)) as T
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
            val result = mutableListOf(GroupLibItem(group.name) as LibItem)
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

    private enum class Group {
        TODAY,
        EARLIER_THIS_WEEK,
        EARLIER_THIS_MONTH,
        EARLIER_THIS_YEAR,
        EARLIER
    }
}
