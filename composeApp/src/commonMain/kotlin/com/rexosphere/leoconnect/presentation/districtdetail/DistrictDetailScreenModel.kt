package com.rexosphere.leoconnect.presentation.districtdetail

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.rexosphere.leoconnect.domain.model.*
import kotlinx.coroutines.launch

sealed class DistrictDetailUiState {
    data object Loading : DistrictDetailUiState()
    data class Success(
        val district: District,
        val topClubs: List<Club>,
        val recentPosts: List<Post>,
        val upcomingEvents: List<Event>
    ) : DistrictDetailUiState()
    data class Error(val message: String) : DistrictDetailUiState()
}

class DistrictDetailScreenModel : StateScreenModel<DistrictDetailUiState>(DistrictDetailUiState.Loading) {
    val uiState = mutableState

    fun loadDistrict(districtName: String) {
        screenModelScope.launch {
            // TODO: Load district data from repository
            // For now, use mock data
            val mockDistrict = District(
                districtId = "1",
                name = districtName,
                region = "Region A",
                clubsCount = 45,
                membersCount = 1250,
                description = "This is a description of $districtName with information about the district's activities and mission.",
                logoUrl = null,
                coverImageUrl = null,
                chairman = ChairmanInfo(
                    name = "John Chairman",
                    photoUrl = null,
                    email = "chairman@leoconnect.org"
                )
            )

            val mockClubs = listOf(
                Club(
                    clubId = "1",
                    name = "Leo Club City Center",
                    district = districtName,
                    districtId = "1",
                    description = "Serving the community",
                    membersCount = 50,
                    followersCount = 120
                ),
                Club(
                    clubId = "2",
                    name = "Leo Club University",
                    district = districtName,
                    districtId = "1",
                    description = "Youth leadership",
                    membersCount = 75,
                    followersCount = 200
                )
            )

            val mockPosts = listOf(
                Post(
                    postId = "1",
                    clubId = "1",
                    clubName = "Leo Club City Center",
                    authorName = "John Doe",
                    authorLogo = null,
                    content = "Great event today! Thanks to everyone who participated.",
                    imageUrl = null,
                    likesCount = 25,
                    commentsCount = 5
                )
            )

            mutableState.value = DistrictDetailUiState.Success(
                district = mockDistrict,
                topClubs = mockClubs,
                recentPosts = mockPosts,
                upcomingEvents = emptyList()
            )
        }
    }
}
