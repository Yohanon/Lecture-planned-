package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.auth.GoogleAuthManager
import com.example.auth.GoogleLinkResult
import com.example.data.model.Subject
import com.example.ui.components.AccountDialog
import com.example.ui.components.GoogleLogoIcon
import com.example.ui.components.LoginDialog
import com.example.ui.screens.RevisionModeScreen
import com.example.ui.screens.SubjectScreen
import com.example.ui.screens.TestPlannerScreen
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateBlack
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import com.example.ui.viewmodel.LecturePlannerViewModel
import kotlinx.coroutines.launch

enum class AppNavDestination(
    val title: String,
    val icon: ImageVector,
    val activeColor: Color,
    val testTag: String
) {
    PHYSICS("Physics", Icons.Default.Speed, CyanAccent, "nav_physics"),
    CHEMISTRY("Chemistry", Icons.Default.Science, EmeraldAccent, "nav_chemistry"),
    MATHEMATICS("Mathematics", Icons.Default.Calculate, VioletAccent, "nav_mathematics"),
    TESTS("Tests", Icons.Default.Quiz, Color(0xFFFBBF24), "nav_tests"),
    REVISION("Revision", Icons.Default.AutoStories, Color(0xFF818CF8), "nav_revision")
}

class MainActivity : ComponentActivity() {

    private val viewModel: LecturePlannerViewModel by viewModels()
    private lateinit var googleAuthManager: GoogleAuthManager

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleAuthManager = GoogleAuthManager(this)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(
                    viewModel = viewModel,
                    googleAuthManager = googleAuthManager
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    viewModel: LecturePlannerViewModel,
    googleAuthManager: GoogleAuthManager
) {
    val context = LocalContext.current
    var currentDestination by remember { mutableStateOf(AppNavDestination.PHYSICS) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAccountDialog by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncStatusMessage.collectAsStateWithLifecycle()

    val allChapters by viewModel.allChapters.collectAsStateWithLifecycle()
    val recommendedTests by viewModel.eligibleRecommendedTests.collectAsStateWithLifecycle()
    val upcomingTests by viewModel.upcomingTests.collectAsStateWithLifecycle()
    val retakeTests by viewModel.retakeTests.collectAsStateWithLifecycle()
    val selectedRevisionChapters by viewModel.selectedRevisionChapterNames.collectAsStateWithLifecycle()
    val revisionMatches by viewModel.revisionMatches.collectAsStateWithLifecycle()

    // Show sync / auth snackbar messages
    LaunchedEffect(syncMessage) {
        syncMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSyncMessage()
        }
    }

    val triggerGoogleSignIn: () -> Unit = {
        scope.launch {
            val result = googleAuthManager.linkWithGoogle(context)
            when (result) {
                is GoogleLinkResult.Success -> {
                    viewModel.linkGoogleAccount(
                        email = result.email,
                        displayName = result.displayName,
                        googleId = result.googleId
                    )
                }
                is GoogleLinkResult.Error -> {
                    snackbarHostState.showSnackbar(result.message)
                }
                is GoogleLinkResult.Cancelled -> {
                    // Cancelled by user
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBlack),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(currentDestination.activeColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = currentDestination.icon,
                                contentDescription = null,
                                tint = currentDestination.activeColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (currentDestination) {
                                AppNavDestination.PHYSICS -> "Physics Lectures"
                                AppNavDestination.CHEMISTRY -> "Chemistry Lectures"
                                AppNavDestination.MATHEMATICS -> "Mathematics Lectures"
                                AppNavDestination.TESTS -> "Arjuna Test Planner"
                                AppNavDestination.REVISION -> "Revision Matcher"
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DeepNavy,
                    titleContentColor = TextPrimary
                ),
                actions = {
                    // Online User Account & Google Link Header Button
                    Surface(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                            .clickable {
                                if (userProfile?.isLoggedIn == true) {
                                    showAccountDialog = true
                                } else {
                                    showLoginDialog = true
                                }
                            }
                            .testTag("top_account_btn"),
                        color = CardBackground,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Online indicator dot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) Color(0xFF34D399) else Color(0xFFFBBF24))
                            )
                            Spacer(modifier = Modifier.width(6.dp))

                            if (userProfile?.isGoogleLinked == true) {
                                GoogleLogoIcon(modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                            }

                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    color = Color(0xFF38BDF8),
                                    strokeWidth = 1.5.dp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            Text(
                                text = if (userProfile?.isLoggedIn == true) {
                                    userProfile?.username ?: "Aspirant"
                                } else {
                                    "Login / Sync"
                                },
                                color = if (userProfile?.isLoggedIn == true) TextPrimary else Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DeepNavy,
                tonalElevation = 8.dp
            ) {
                AppNavDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        modifier = Modifier.testTag(destination.testTag),
                        icon = {
                            if (destination == AppNavDestination.TESTS && recommendedTests.isNotEmpty()) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = Color(0xFF10B981)) {
                                            Text(
                                                text = "${recommendedTests.size}",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.title
                                    )
                                }
                            } else if (destination == AppNavDestination.REVISION && selectedRevisionChapters.isNotEmpty()) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = Color(0xFF6366F1)) {
                                            Text(
                                                text = "${selectedRevisionChapters.size}",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title
                                )
                            }
                        },
                        label = {
                            Text(
                                text = destination.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = destination.activeColor,
                            selectedTextColor = destination.activeColor,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = destination.activeColor.copy(alpha = 0.18f)
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SlateBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppNavDestination.PHYSICS -> {
                    val physicsChapters = remember(allChapters) {
                        allChapters.filter { it.subject.equals("PHYSICS", ignoreCase = true) }
                    }
                    SubjectScreen(
                        subject = Subject.PHYSICS,
                        chapters = physicsChapters,
                        onIncrementLecture = { viewModel.incrementLecture(it) },
                        onDecrementLecture = { viewModel.decrementLecture(it) },
                        onUpdateLectures = { id, attended, total ->
                            viewModel.setAttendedLectures(id, attended, total)
                            viewModel.updateTotalLectures(id, total)
                        },
                        onToggleModule = { viewModel.toggleModuleChecklist(it) },
                        onTogglePyq = { viewModel.togglePyqChecklist(it) }
                    )
                }
                AppNavDestination.CHEMISTRY -> {
                    val chemistryChapters = remember(allChapters) {
                        allChapters.filter { it.subject.equals("CHEMISTRY", ignoreCase = true) }
                    }
                    SubjectScreen(
                        subject = Subject.CHEMISTRY,
                        chapters = chemistryChapters,
                        onIncrementLecture = { viewModel.incrementLecture(it) },
                        onDecrementLecture = { viewModel.decrementLecture(it) },
                        onUpdateLectures = { id, attended, total ->
                            viewModel.setAttendedLectures(id, attended, total)
                            viewModel.updateTotalLectures(id, total)
                        },
                        onToggleModule = { viewModel.toggleModuleChecklist(it) },
                        onTogglePyq = { viewModel.togglePyqChecklist(it) }
                    )
                }
                AppNavDestination.MATHEMATICS -> {
                    val mathChapters = remember(allChapters) {
                        allChapters.filter { it.subject.equals("MATHEMATICS", ignoreCase = true) }
                    }
                    SubjectScreen(
                        subject = Subject.MATHEMATICS,
                        chapters = mathChapters,
                        onIncrementLecture = { viewModel.incrementLecture(it) },
                        onDecrementLecture = { viewModel.decrementLecture(it) },
                        onUpdateLectures = { id, attended, total ->
                            viewModel.setAttendedLectures(id, attended, total)
                            viewModel.updateTotalLectures(id, total)
                        },
                        onToggleModule = { viewModel.toggleModuleChecklist(it) },
                        onTogglePyq = { viewModel.togglePyqChecklist(it) }
                    )
                }
                AppNavDestination.TESTS -> {
                    TestPlannerScreen(
                        recommendedTests = recommendedTests,
                        upcomingTests = upcomingTests,
                        retakeTests = retakeTests,
                        onCheckoutTest = { testId, note ->
                            viewModel.checkoutTest(testId, note)
                            scope.launch {
                                snackbarHostState.showSnackbar("Test checked out and moved to Retake Tests!")
                            }
                        },
                        onRetakeTest = { testId ->
                            viewModel.retakeTest(testId)
                            scope.launch {
                                snackbarHostState.showSnackbar("Retake recorded! Good luck on your revision.")
                            }
                        },
                        onResetTest = { testId ->
                            viewModel.resetTest(testId)
                            scope.launch {
                                snackbarHostState.showSnackbar("Test moved back to Planner.")
                            }
                        }
                    )
                }
                AppNavDestination.REVISION -> {
                    RevisionModeScreen(
                        chapters = allChapters,
                        selectedChapterNames = selectedRevisionChapters,
                        revisionMatches = revisionMatches,
                        onToggleChapter = { viewModel.toggleRevisionChapter(it) },
                        onClearAll = { viewModel.clearRevisionChapters() },
                        onSelectAllForSubject = { subName ->
                            viewModel.selectAllRevisionChapters(subName, allChapters)
                        },
                        onRetakeTest = { testId ->
                            viewModel.retakeTest(testId)
                            scope.launch {
                                snackbarHostState.showSnackbar("Starting revision for selected test!")
                            }
                        }
                    )
                }
            }
        }
    }

    // Account & Online Profile Dialog
    if (showAccountDialog) {
        AccountDialog(
            userProfile = userProfile,
            isOnline = isOnline,
            isSyncing = isSyncing,
            onDismiss = { showAccountDialog = false },
            onUpdateUsername = { newUsername ->
                viewModel.loginWithUsername(newUsername)
            },
            onLinkGoogleRequest = triggerGoogleSignIn,
            onManualLinkGoogle = { email, name ->
                viewModel.linkGoogleAccount(email, name, null)
            },
            onUnlinkGoogle = {
                viewModel.unlinkGoogleAccount()
            },
            onSyncNow = {
                viewModel.syncNow()
            },
            onRestoreFromCloud = { uname, email ->
                viewModel.restoreFromCloud(uname, email) { success ->
                    if (success) {
                        showAccountDialog = false
                    }
                }
            },
            onLogout = {
                viewModel.logout()
            }
        )
    }

    // Login Dialog
    if (showLoginDialog) {
        LoginDialog(
            currentUsername = userProfile?.username ?: "",
            onDismiss = { showLoginDialog = false },
            onLoginSuccess = { uname, gEmail, gName ->
                viewModel.loginWithUsername(uname)
                if (gEmail != null) {
                    viewModel.linkGoogleAccount(gEmail, gName, null)
                }
            },
            onTriggerGoogleSignIn = triggerGoogleSignIn
        )
    }
}
