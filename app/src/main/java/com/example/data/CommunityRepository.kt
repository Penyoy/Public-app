package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class CommunityRepository(private val db: AppDatabase) {
    private val userDao = db.userDao()
    private val forumDao = db.forumDao()
    private val commentDao = db.commentDao()

    val currentUser: Flow<UserEntity?> = userDao.getCurrentUserFlow()
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsersSortedByPoints()
    val allPosts: Flow<List<ForumPostEntity>> = forumDao.getAllPosts()

    suspend fun getCurrentUser(): UserEntity? = userDao.getCurrentUser()

    suspend fun getUserByUsername(username: String): UserEntity? {
        return userDao.getUserByUsername(username)
    }

    suspend fun registerUser(user: UserEntity): Long {
        // If this is set as current user, clear any previous active users
        if (user.isCurrentUser) {
            userDao.clearCurrentUserStatus()
        }
        return userDao.insertUser(user)
    }

    suspend fun loginUser(username: String): Boolean {
        val user = userDao.getUserByUsername(username)
        return if (user != null) {
            userDao.clearCurrentUserStatus()
            userDao.setCurrentUser(user.id)
            true
        } else {
            false
        }
    }

    suspend fun logoutUser() {
        userDao.clearCurrentUserStatus()
    }

    suspend fun updateProfile(user: UserEntity) {
        userDao.updateUser(user)
    }

    fun getPostsByCategory(category: String): Flow<List<ForumPostEntity>> {
        return if (category == "All") {
            forumDao.getAllPosts()
        } else {
            forumDao.getPostsByCategory(category)
        }
    }

    fun getPostById(postId: Int): Flow<ForumPostEntity?> {
        return forumDao.getPostByIdFlow(postId)
    }

    suspend fun createPost(post: ForumPostEntity): Long {
        return forumDao.insertPost(post)
    }

    suspend fun toggleLikePost(postId: Int) {
        val post = forumDao.getPostById(postId) ?: return
        val updatedPost = if (post.likedByCurrentUser) {
            post.copy(
                likes = post.likes - 1,
                likedByCurrentUser = false
            )
        } else {
            post.copy(
                likes = post.likes + 1,
                likedByCurrentUser = true
            )
        }
        forumDao.updatePost(updatedPost)
    }

    fun getCommentsForPost(postId: Int): Flow<List<CommentEntity>> {
        return commentDao.getCommentsForPost(postId)
    }

    suspend fun addComment(comment: CommentEntity) {
        commentDao.insertComment(comment)
        forumDao.incrementCommentCount(comment.postId)
    }

    suspend fun prepopulateDatabaseIfEmpty() {
        val users = allUsers.firstOrNull() ?: emptyList()
        if (users.isEmpty()) {
            // 1. Add Community Users (Leaderboard Players)
            val initialUsers = listOf(
                UserEntity(
                    username = "Xenon",
                    email = "xenon@aetheria.com",
                    avatarUrl = "https://images.unsplash.com/photo-1566492031773-4f4e44671857?auto=format&fit=crop&q=80&w=200",
                    level = 82,
                    rankName = "Challenger",
                    characterClass = "Ranger",
                    points = 9850,
                    winRate = "68.2%",
                    guild = "Alpha Wolves",
                    bio = "Precision beats power. Top Ranger in Aetheria Realm.",
                    isCurrentUser = false
                ),
                UserEntity(
                    username = "Lumina",
                    email = "lumina@aetheria.com",
                    avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=200",
                    level = 74,
                    rankName = "Grandmaster III",
                    characterClass = "Mage",
                    points = 8120,
                    winRate = "61.4%",
                    guild = "Arcane Coven",
                    bio = "Spells, scrolls, and stars. Guild Leader of Arcane Coven.",
                    isCurrentUser = false
                ),
                UserEntity(
                    username = "ShadowBlade",
                    email = "shadowblade@aetheria.com",
                    avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=200",
                    level = 69,
                    rankName = "Master I",
                    characterClass = "Rogue",
                    points = 7450,
                    winRate = "59.0%",
                    guild = "Shadow Hunters",
                    bio = "Now you see me, now you're dead. Assassin mains represent!",
                    isCurrentUser = false
                ),
                UserEntity(
                    username = "IronWall",
                    email = "ironwall@aetheria.com",
                    avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200",
                    level = 65,
                    rankName = "Diamond II",
                    characterClass = "Warrior",
                    points = 6200,
                    winRate = "55.3%",
                    guild = "The Aegis",
                    bio = "The ultimate tank. Protecting my squad since Alpha phase.",
                    isCurrentUser = false
                ),
                UserEntity(
                    username = "StarGazer",
                    email = "stargazer@aetheria.com",
                    avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=200",
                    level = 52,
                    rankName = "Platinum IV",
                    characterClass = "Cleric",
                    points = 4850,
                    winRate = "53.1%",
                    guild = "Lightbringers",
                    bio = "Always looking up. Healing you when you charge in blindly.",
                    isCurrentUser = false
                ),
                UserEntity(
                    username = "NoobMaster99",
                    email = "noob@aetheria.com",
                    avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200",
                    level = 15,
                    rankName = "Bronze II",
                    characterClass = "Warrior",
                    points = 1540,
                    winRate = "42.5%",
                    guild = "Guildless",
                    bio = "I just started, how do you jump?",
                    isCurrentUser = false
                ),
                // Default logged-in user
                UserEntity(
                    username = "AetherNovice",
                    email = "novice@aetheria.com",
                    avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200",
                    level = 10,
                    rankName = "Silver I",
                    characterClass = "Mage",
                    points = 2150,
                    winRate = "50%",
                    guild = "Guildless",
                    bio = "New to Aetheria Realm, excited to join a guild! Add me for co-op.",
                    isCurrentUser = true
                )
            )

            initialUsers.forEach { userDao.insertUser(it) }

            // 2. Add Forum Posts
            val post1Id = forumDao.insertPost(
                ForumPostEntity(
                    authorName = "Lumina",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=200",
                    title = "Aetheria Realm Update 2.4 Discussion!",
                    content = "The new dungeon 'Temple of the Sun' is absolute fire! What do you guys think of the boss mechanics? The third boss has a crazy fire-wave pattern that wiped our squad twice. Let's share strategies!",
                    category = "General",
                    timestamp = System.currentTimeMillis() - 7200000, // 2h ago
                    likes = 42,
                    commentsCount = 2
                )
            ).toInt()

            val post2Id = forumDao.insertPost(
                ForumPostEntity(
                    authorName = "Xenon",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1566492031773-4f4e44671857?auto=format&fit=crop&q=80&w=200",
                    title = "Ultimate Ranger Build for patch 2.4 (Solo Arena)",
                    content = "For anyone struggling with the new Solo Arena, here is my recommended Ranger setup. Maximize your Crit Chance and focus on the Gale Wing armor set for the 15% agility boost. Pair it with the Stormbow for maximum attack speed.",
                    category = "Guides",
                    timestamp = System.currentTimeMillis() - 18000000, // 5h ago
                    likes = 128,
                    commentsCount = 1
                )
            ).toInt()

            forumDao.insertPost(
                ForumPostEntity(
                    authorName = "Lumina",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=200",
                    title = "Arcane Coven is Recruiting Active Mages & Clerics!",
                    content = "We are prepping for the upcoming Guild Raids next week. Looking for Level 50+ healers and DPS. Drop your stats below or apply directly in-game. Daily guild activity is required!",
                    category = "Recruitment",
                    timestamp = System.currentTimeMillis() - 86400000, // 1d ago
                    likes = 15,
                    commentsCount = 0
                )
            )

            val post4Id = forumDao.insertPost(
                ForumPostEntity(
                    authorName = "Admin",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&q=80&w=200",
                    title = "Dev Blog: Future of PVP in Aetheria Realm",
                    content = "Greetings Adventurers! Today we are sharing a sneak peek of our matchmaking overhaul. We are introducing seasonal ladders, cosmetic tier rewards, and a new 3v3 Arena mode. Read the full roadmap in our blog, and leave your suggestions below!",
                    category = "Updates",
                    timestamp = System.currentTimeMillis() - 172800000, // 2d ago
                    likes = 256,
                    commentsCount = 3
                )
            ).toInt()

            // 3. Add Comments
            commentDao.insertComment(
                CommentEntity(
                    postId = post1Id,
                    authorName = "IronWall",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200",
                    content = "Totally agree! We beat it by using a double shield tank setup. You have to trigger the water totems at 50% HP!",
                    timestamp = System.currentTimeMillis() - 5400000
                )
            )

            commentDao.insertComment(
                CommentEntity(
                    postId = post1Id,
                    authorName = "AetherNovice",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200",
                    content = "Wow, I need to level up fast to enter this dungeon. It looks amazing!",
                    timestamp = System.currentTimeMillis() - 3600000
                )
            )

            commentDao.insertComment(
                CommentEntity(
                    postId = post2Id,
                    authorName = "ShadowBlade",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=200",
                    content = "Tested this build in Solo Arena and got my personal record! The Gale Wing armor is indeed overpowered.",
                    timestamp = System.currentTimeMillis() - 14400000
                )
            )

            commentDao.insertComment(
                CommentEntity(
                    postId = post4Id,
                    authorName = "Xenon",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1566492031773-4f4e44671857?auto=format&fit=crop&q=80&w=200",
                    content = "Can't wait for 3v3 Arena! Finally, we can coordinate real-time team fights.",
                    timestamp = System.currentTimeMillis() - 150000000
                )
            )

            commentDao.insertComment(
                CommentEntity(
                    postId = post4Id,
                    authorName = "NoobMaster99",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200",
                    content = "Are there rewards for Bronze league? Asking for a friend.",
                    timestamp = System.currentTimeMillis() - 120000000
                )
            )

            commentDao.insertComment(
                CommentEntity(
                    postId = post4Id,
                    authorName = "Lumina",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=200",
                    content = "Awesome updates! Glad to see PVP getting some love. Matchmaking improvements were highly needed.",
                    timestamp = System.currentTimeMillis() - 90000000
                )
            )
        }
    }
}
