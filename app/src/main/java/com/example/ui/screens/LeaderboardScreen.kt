package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserEntity
import com.example.ui.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: CommunityViewModel,
    modifier: Modifier = Modifier
) {
    val users by viewModel.leaderboardUsers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var classFilter by remember { mutableStateOf("All") }

    val classes = listOf("All", "Warrior", "Mage", "Ranger", "Rogue", "Cleric")

    val darkBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
    )
    val accentGold = Color(0xFFF59E0B)
    val neonCyan = Color(0xFF06B6D4)

    // Filter logic
    val filteredUsers = users.filter { user ->
        val matchesSearch = user.username.contains(searchQuery, ignoreCase = true)
        val matchesClass = classFilter == "All" || user.characterClass == classFilter
        matchesSearch && matchesClass
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(darkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Screen Title Section with some summary banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E293B).copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(accentGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Leaderboard Logo",
                            tint = accentGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Aetheria Hall of Fame",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Top rankings sorted by trophy points and skill",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search player name...", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = neonCyan
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = neonCyan,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.6f),
                    unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("leaderboard_search")
            )

            // Class filters
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                items(classes.size) { index ->
                    val cls = classes[index]
                    val isSelected = classFilter == cls
                    FilterChip(
                        selected = isSelected,
                        onClick = { classFilter = cls },
                        label = {
                            Text(
                                text = cls,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.LightGray
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = neonCyan,
                            containerColor = Color(0xFF1E293B)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            selected = isSelected,
                            enabled = true,
                            borderColor = Color(0xFF334155),
                            selectedBorderColor = neonCyan,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.dp
                        )
                    )
                }
            }

            // Leaderboard Items Title Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RANK & PLAYER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.weight(2.5f)
                )
                Text(
                    text = "CLASS & WR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.weight(1.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "POINTS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }

            // List of Players
            if (filteredUsers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Empty",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No players found",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    itemsIndexed(filteredUsers) { index, user ->
                        LeaderboardItem(rank = index + 1, user = user, neonCyan = neonCyan, accentGold = accentGold)
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardItem(
    rank: Int,
    user: UserEntity,
    neonCyan: Color,
    accentGold: Color
) {
    // Custom Rank highlight styling
    val rankColor = when (rank) {
        1 -> Color(0xFFF59E0B) // Gold
        2 -> Color(0xFF94A3B8) // Silver
        3 -> Color(0xFFB45309) // Bronze
        else -> Color.Transparent
    }

    val itemBgColor = if (user.isCurrentUser) {
        Color(0xFF1E293B).copy(alpha = 0.9f)
    } else {
        Color(0xFF1E293B).copy(alpha = 0.5f)
    }

    val borderColor = if (user.isCurrentUser) {
        neonCyan
    } else if (rank <= 3) {
        rankColor
    } else {
        Color(0xFF334155)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = itemBgColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (user.isCurrentUser || rank <= 3) 1.5.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_item_$rank")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Rank and User info
            Row(
                modifier = Modifier.weight(2.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank Avatar Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (rank <= 3) rankColor else Color(0xFF0F172A)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (rank <= 3) Color.Black else Color.LightGray
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Avatar and Names
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.username,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (user.isCurrentUser) neonCyan else Color.White
                            )
                        )
                        if (user.isCurrentUser) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "YOU",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    color = Color.Black
                                ),
                                modifier = Modifier
                                    .background(neonCyan, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = user.guild,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Gray
                        )
                    )
                }
            }

            // Class and WR
            Column(
                modifier = Modifier.weight(1.5f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = user.characterClass,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.LightGray,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "${user.winRate} WR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF10B981)
                    )
                )
            }

            // Points
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "points",
                    tint = accentGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = user.points.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
            }
        }
    }
}
