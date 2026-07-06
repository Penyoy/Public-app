package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CommentEntity
import com.example.data.CommunityRepository
import com.example.data.ForumPostEntity
import com.example.data.UserEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CommunityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CommunityRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = CommunityRepository(db)
        
        // Prep the DB with rich sample community data on startup if empty
        viewModelScope.launch {
            repository.prepopulateDatabaseIfEmpty()
        }
    }

    // Auth State
    val currentUser: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Leaderboard/Rankings State
    val leaderboardUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Forum Filter State
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Forum Posts State (Reacts to Category Filter)
    @OptIn(ExperimentalCoroutinesApi::class)
    val forumPosts: StateFlow<List<ForumPostEntity>> = _selectedCategory
        .flatMapLatest { category ->
            repository.getPostsByCategory(category)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Post Comments State
    private val _selectedPostId = MutableStateFlow<Int?>(null)
    val selectedPostId: StateFlow<Int?> = _selectedPostId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPost: StateFlow<ForumPostEntity?> = _selectedPostId
        .flatMapLatest { id ->
            if (id != null) repository.getPostById(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPostComments: StateFlow<List<CommentEntity>> = _selectedPostId
        .flatMapLatest { id ->
            if (id != null) repository.getCommentsForPost(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Auth Actions
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    fun selectPost(postId: Int?) {
        _selectedPostId.value = postId
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun login(username: String, onSuccess: () -> Unit) {
        _authError.value = null
        if (username.isBlank()) {
            _authError.value = "Username cannot be empty!"
            return
        }
        viewModelScope.launch {
            val success = repository.loginUser(username)
            if (success) {
                onSuccess()
            } else {
                _authError.value = "User not found! Register a new account below."
            }
        }
    }

    fun register(username: String, characterClass: String, email: String, onSuccess: () -> Unit) {
        _authError.value = null
        if (username.isBlank()) {
            _authError.value = "Username cannot be empty!"
            return
        }
        viewModelScope.launch {
            val existing = repository.getUserByUsername(username)
            if (existing != null) {
                _authError.value = "Username already taken!"
                return@launch
            }

            // Create new user profile
            val newUser = UserEntity(
                username = username,
                email = email.ifBlank { "$username@aetheria.com" },
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200", // Default generic avatar
                level = 1,
                rankName = "Bronze V",
                characterClass = characterClass,
                points = 100, // Initial point
                winRate = "0%",
                guild = "Guildless",
                isCurrentUser = true
            )
            repository.registerUser(newUser)
            onSuccess()
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutUser()
        }
    }

    // Profile Actions
    fun updateProfile(bio: String, guild: String, characterClass: String) {
        val current = currentUser.value ?: return
        viewModelScope.launch {
            val updated = current.copy(
                bio = bio,
                guild = guild.ifBlank { "Guildless" },
                characterClass = characterClass
            )
            repository.updateProfile(updated)
        }
    }

    // Forum Actions
    fun createPost(title: String, content: String, category: String, onSuccess: () -> Unit) {
        val user = currentUser.value ?: return
        if (title.isBlank() || content.isBlank()) return

        viewModelScope.launch {
            val newPost = ForumPostEntity(
                authorName = user.username,
                authorAvatarUrl = user.avatarUrl,
                title = title,
                content = content,
                category = category,
                likes = 0,
                commentsCount = 0
            )
            repository.createPost(newPost)
            onSuccess()
        }
    }

    fun toggleLike(postId: Int) {
        viewModelScope.launch {
            repository.toggleLikePost(postId)
        }
    }

    fun addComment(content: String) {
        val user = currentUser.value ?: return
        val postId = _selectedPostId.value ?: return
        if (content.isBlank()) return

        viewModelScope.launch {
            val comment = CommentEntity(
                postId = postId,
                authorName = user.username,
                authorAvatarUrl = user.avatarUrl,
                content = content
            )
            repository.addComment(comment)
        }
    }
}

class CommunityViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommunityViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
