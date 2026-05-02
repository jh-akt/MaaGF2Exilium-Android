@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.maaframework.android.gf2

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maaframework.android.catalog.TaskOptionSupport
import com.maaframework.android.model.MaaLogLevels
import com.maaframework.android.model.RootBinaryProbe
import com.maaframework.android.model.TaskDescriptor
import com.maaframework.android.model.TaskOptionDescriptor
import com.maaframework.android.model.TaskOptionType
import com.maaframework.android.model.canStopRun
import com.maaframework.android.model.canToggleDisplayPower
import com.maaframework.android.model.displayName
import com.maaframework.android.preview.DefaultDisplayConfig
import com.maaframework.android.runtime.summaryText
import com.maaframework.android.ui.MaaFullscreenPreviewOverlay as FrameworkFullscreenPreviewOverlay
import com.maaframework.android.ui.MaaHomeAction as FrameworkHomeAction
import com.maaframework.android.ui.MaaHomeDivider
import com.maaframework.android.ui.MaaHomeInfo as FrameworkHomeInfo
import com.maaframework.android.ui.MaaHomeActionRow as FrameworkHomeActionRow
import com.maaframework.android.ui.MaaHomeInfoRow as FrameworkHomeInfoRow
import com.maaframework.android.ui.MaaHomePanel as FrameworkHomePanel
import com.maaframework.android.ui.MaaHomeProgressBlock as FrameworkHomeProgressBlock
import com.maaframework.android.ui.MaaHomeProgress as FrameworkHomeProgress
import com.maaframework.android.ui.MaaHomeRepositoryPanel as FrameworkHomeRepositoryPanel
import com.maaframework.android.ui.MaaHomeResourcePresetPanel as FrameworkHomeResourcePresetPanel
import com.maaframework.android.ui.MaaHomeService as FrameworkHomeService
import com.maaframework.android.ui.MaaHomeStatus as FrameworkHomeStatus
import com.maaframework.android.ui.MaaHomeSupportText as FrameworkHomeSupportText
import com.maaframework.android.ui.MaaHomeTone as FrameworkHomeTone
import com.maaframework.android.ui.MaaLogMetric as FrameworkLogMetric
import com.maaframework.android.ui.MaaPreviewPanel as FrameworkPreviewPanel
import com.maaframework.android.ui.MaaPreviewSurfaceHost as FrameworkPreviewSurfaceHost
import com.maaframework.android.ui.MaaRuntimeLogsPanel as FrameworkRuntimeLogsPanel
import com.maaframework.android.ui.MaaRuntimeLogList as FrameworkRuntimeLogList
import com.maaframework.android.ui.MaaResourceRepositoryContent as FrameworkResourceRepositoryContent
import com.maaframework.android.ui.MaaSettingsChoice as FrameworkSettingsChoice
import com.maaframework.android.ui.MaaSettingsChoiceRow as FrameworkSettingsChoiceRow
import com.maaframework.android.ui.MaaSettingsPanel as FrameworkSettingsPanel
import com.maaframework.android.ui.MaaSettingsSection as FrameworkSettingsSection
import com.maaframework.android.ui.MaaTaskDetailPanel as FrameworkTaskDetailPanel
import com.maaframework.android.ui.MaaTaskListPanel as FrameworkTaskListPanel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MaaGf2Screen(
    viewModel: MainViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val visibleTasks = remember(state.catalog.tasks, state.selectedResourceId) {
        MainViewModel.visibleTasks(state.catalog.tasks, state.selectedResourceId)
    }
    val selectedTask = remember(visibleTasks, state.selectedTaskId) {
        visibleTasks.firstOrNull { it.id == state.selectedTaskId }
    }
    var isFullscreenPreview by rememberSaveable { mutableStateOf(false) }
    val previewContent = remember {
        movableContentOf {
            FrameworkPreviewSurfaceHost(
                modifier = Modifier.fillMaxSize(),
                onPreviewSurfaceChanged = viewModel::setPreviewSurface,
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                AppBottomBar(
                    activeTab = state.activeTab,
                    onTabSelected = viewModel::selectTab,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(
                        horizontal = MaaGf2DesignTokens.Spacing.sm,
                        vertical = MaaGf2DesignTokens.Spacing.sm,
                    ),
                verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
            ) {
                if (state.activeTab != MaaGf2Tab.Tasks) {
                    AppHeader(
                        projectName = state.manifest.displayName,
                        title = state.activeTab.title(),
                        subtitle = state.activeTab.subtitle(state, visibleTasks),
                        connected = state.rootConnected,
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    when (state.activeTab) {
                        MaaGf2Tab.Home -> HomeScreen(
                            state = state,
                            visibleTasks = visibleTasks,
                            onConnect = viewModel::requestRootAndConnect,
                            onPrepare = viewModel::prepareRuntime,
                            onOpenGame = viewModel::startWindowedGame,
                            onExport = viewModel::exportDiagnostics,
                            onSelectResource = viewModel::selectResource,
                            onSelectPreset = viewModel::selectPreset,
                            onRefreshResourceRepository = viewModel::refreshResourceRepository,
                            onClearResourceRepository = viewModel::requestClearResourceRepositoryConfirmation,
                        )

                        MaaGf2Tab.Tasks -> TasksScreen(
                            state = state,
                            tasks = visibleTasks,
                            selectedTask = selectedTask,
                            onSelectTask = viewModel::selectTask,
                            onToggleTaskChecked = viewModel::toggleTaskChecked,
                            onRunTask = viewModel::startSelectedTask,
                            onStop = viewModel::stopRun,
                            onToggleDisplayPower = viewModel::toggleDisplayPower,
                            onOverrideJsonChange = viewModel::updateOverrideJson,
                            onSwitchTaskOption = viewModel::updateTaskSwitchOption,
                            onToggleTaskCheckboxOption = viewModel::toggleTaskCheckboxOption,
                            onTaskInputValueChange = viewModel::updateTaskInputValue,
                            isFullscreenPreview = isFullscreenPreview,
                            onExpandPreview = { isFullscreenPreview = true },
                            previewContent = previewContent,
                        )

                        MaaGf2Tab.Settings -> SettingsScreen(
                            state = state,
                            onSelectResource = viewModel::selectResource,
                            onSelectPreset = viewModel::selectPreset,
                            onRefreshResourceRepository = viewModel::refreshResourceRepository,
                            onClearResourceRepository = viewModel::requestClearResourceRepositoryConfirmation,
                            onLogLevelChange = viewModel::updateLogLevel,
                            onExportConfig = viewModel::exportConfig,
                            onImportConfig = viewModel::importConfig,
                        )

                        MaaGf2Tab.Logs -> LogsScreen(state = state)
                    }
                }
            }
        }

        if (isFullscreenPreview) {
            FrameworkFullscreenPreviewOverlay(
                previewContent = previewContent,
                onDismissRequest = { isFullscreenPreview = false },
                onPreviewTouchDown = viewModel::onPreviewTouchDown,
                onPreviewTouchMove = viewModel::onPreviewTouchMove,
                onPreviewTouchUp = viewModel::onPreviewTouchUp,
            )
        }

        if (state.resourceRepositoryClearConfirmVisible) {
            ResourceRepositoryClearConfirmationDialog(
                onDismissRequest = viewModel::dismissClearResourceRepositoryConfirmation,
                onConfirm = viewModel::clearResourceRepository,
            )
        }
    }
}

@Composable
private fun AppHeader(
    projectName: String,
    title: String,
    subtitle: String?,
    connected: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = projectName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            StatusPill(
                text = if (connected) "Runtime 已连接" else "等待连接",
                active = connected,
            )
        }
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HomeScreen(
    state: MainUiState,
    visibleTasks: List<TaskDescriptor>,
    onConnect: () -> Unit,
    onPrepare: () -> Unit,
    onOpenGame: () -> Unit,
    onExport: () -> Unit,
    onSelectResource: (String) -> Unit,
    onSelectPreset: (String) -> Unit,
    onRefreshResourceRepository: () -> Unit,
    onClearResourceRepository: () -> Unit,
) {
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val screenSizeLabel = "${displayMetrics.widthPixels} × ${displayMetrics.heightPixels}"
    val resourceSummary = if (visibleTasks.isEmpty()) {
        "接口资源未加载"
    } else {
        "${visibleTasks.size} 个任务 / ${state.catalog.presets.size} 个预设"
    }

    FrameworkHomePanel(
        overview = buildList {
            add(FrameworkHomeInfo("屏幕分辨率", screenSizeLabel))
            add(
                FrameworkHomeInfo(
                    label = "接口资源",
                    value = resourceSummary,
                    tone = if (visibleTasks.isEmpty()) FrameworkHomeTone.Error else FrameworkHomeTone.Neutral,
                ),
            )
            add(FrameworkHomeInfo("运行阶段", state.runtimeState.phase.displayName()))
            state.runtimeState.currentTaskId?.takeIf { it.isNotBlank() }?.let { currentTaskId ->
                add(FrameworkHomeInfo("当前任务", currentTaskId))
            }
        },
        service = FrameworkHomeService(
            label = "Runtime 服务",
            value = homeServiceStatusLabel(state),
            tone = homeServiceTone(state),
            loading = state.busy && !state.rootConnected,
        ),
        statuses = listOf(
            FrameworkHomeStatus("Root 可用", state.rootReport.available),
            FrameworkHomeStatus("授权通过", state.rootReport.granted),
            FrameworkHomeStatus("服务在线", state.rootConnected),
        ),
        actions = listOf(
            FrameworkHomeAction(
                title = "准备运行时",
                actionLabel = if (state.runtimeState.runtimePrepared) "已就绪" else "执行",
                enabled = state.rootConnected && state.resourceRepository.available && !state.busy,
                onClick = onPrepare,
            ),
            FrameworkHomeAction(
                title = "打开游戏",
                actionLabel = "打开",
                enabled = state.rootConnected && !state.busy,
                onClick = onOpenGame,
            ),
            FrameworkHomeAction(
                title = if (state.rootConnected) "重新连接 Runtime" else "连接 Root / Runtime",
                actionLabel = if (state.busy && !state.rootConnected) {
                    "连接中"
                } else if (state.rootConnected) {
                    "重连"
                } else {
                    "连接"
                },
                enabled = !state.busy,
                onClick = onConnect,
            ),
            FrameworkHomeAction(
                title = "导出诊断包",
                actionLabel = if (state.runtimeState.lastDiagnosticsPath.isNullOrBlank()) "导出" else "最新",
                enabled = state.rootConnected && !state.busy,
                onClick = onExport,
            ),
        ),
        resourceContent = {
            FrameworkHomeRepositoryPanel(
                summary = resourceRepositorySummary(state),
                rootPath = state.resourceRepository.rootPath,
                error = state.resourceRepository.lastError,
                progress = state.resourceRepositoryProgress?.let {
                    FrameworkHomeProgress(
                        fraction = it.fraction,
                        label = it.label,
                    )
                },
                action = FrameworkHomeAction(
                    title = "清空 GitHub 资源",
                    actionLabel = if (state.resourceRepositoryUpdating) "处理中" else "清空",
                    enabled = !state.resourceRepositoryUpdating,
                    onClick = onClearResourceRepository,
                ),
                clearAction = FrameworkHomeAction(
                    title = if (state.resourceRepository.available) "更新 GitHub 资源" else "下载 GitHub 资源",
                    actionLabel = if (state.resourceRepositoryUpdating) "处理中" else "执行",
                    enabled = !state.resourceRepositoryUpdating,
                    onClick = onRefreshResourceRepository,
                ),
                latestCommitText = resourceRepositoryLatestCommitText(state),
            ) {
                if (state.catalog.resources.isNotEmpty()) {
                    MaaHomeDivider()
                    FrameworkHomeResourcePresetPanel(
                        resources = state.catalog.resources,
                        selectedResourceId = state.selectedResourceId,
                        onSelectResource = onSelectResource,
                        presets = state.catalog.presets,
                        selectedPresetId = state.selectedPresetId,
                        onSelectPreset = onSelectPreset,
                    )
                }
            }
        },
    )
}

@Composable
private fun SettingsScreen(
    state: MainUiState,
    onSelectResource: (String) -> Unit,
    onSelectPreset: (String) -> Unit,
    onRefreshResourceRepository: () -> Unit,
    onClearResourceRepository: () -> Unit,
    onLogLevelChange: (String) -> Unit,
    onExportConfig: (java.io.OutputStream) -> Unit,
    onImportConfig: (java.io.InputStream) -> Unit,
) {
    val context = LocalContext.current
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        context.contentResolver.openOutputStream(uri)?.let { output ->
            onExportConfig(output)
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        context.contentResolver.openInputStream(uri)?.let { input ->
            onImportConfig(input)
        }
    }

    FrameworkSettingsPanel {
        FrameworkSettingsSection(title = "资源") {
            FrameworkResourceRepositoryContent(
                title = "GitHub 资源",
                summary = resourceRepositorySummary(state),
                rootPath = state.resourceRepository.rootPath,
                error = state.resourceRepository.lastError,
                progress = state.resourceRepositoryProgress?.let { progress ->
                    FrameworkHomeProgress(
                        fraction = progress.fraction,
                        label = progress.label,
                    )
                },
                action = FrameworkHomeAction(
                    title = if (state.resourceRepository.available) "更新 GitHub 资源" else "下载 GitHub 资源",
                    description = if (state.resourceRepositoryUpdating) {
                        "正在处理 GitHub 资源缓存"
                    } else {
                        "首次下载后会缓存在本地，后续按需手动刷新。"
                    },
                    actionLabel = if (state.resourceRepositoryUpdating) "处理中" else "执行",
                    enabled = !state.resourceRepositoryUpdating,
                    onClick = onRefreshResourceRepository,
                ),
                clearAction = FrameworkHomeAction(
                    title = "清空 GitHub 资源",
                    description = "删除当前缓存和历史目录，下次更新会重新下载，适合排除旧数据干扰。",
                    actionLabel = if (state.resourceRepositoryUpdating) "处理中" else "清空",
                    enabled = !state.resourceRepositoryUpdating,
                    onClick = onClearResourceRepository,
                ),
                latestCommitText = resourceRepositoryLatestCommitText(state),
            )
            if (state.catalog.resources.isNotEmpty() || state.catalog.presets.isNotEmpty()) {
                MaaHomeDivider()
                FrameworkHomeResourcePresetPanel(
                    resources = state.catalog.resources,
                    selectedResourceId = state.selectedResourceId,
                    onSelectResource = onSelectResource,
                    presets = state.catalog.presets,
                    selectedPresetId = state.selectedPresetId,
                    onSelectPreset = onSelectPreset,
                )
            }
        }

        FrameworkSettingsSection(title = "日志") {
            FrameworkSettingsChoiceRow(
                title = "日志级别",
                description = "控制 root runtime 与日志页展示的详细程度",
                options = MaaLogLevels.choices.map { FrameworkSettingsChoice(it, it.logLevelLabel()) },
                selected = state.logLevel,
                onSelected = onLogLevelChange,
            )
        }

        FrameworkSettingsSection(title = "配置文件") {
            FrameworkHomeActionRow(
                action = FrameworkHomeAction(
                    title = "导出配置",
                    description = "导出任务勾选、资源、预设和参数配置。",
                    actionLabel = "导出",
                    onClick = { exportLauncher.launch("maagf2_android_config.json") },
                ),
            )
            MaaHomeDivider()
            FrameworkHomeActionRow(
                action = FrameworkHomeAction(
                    title = "导入配置",
                    description = "导入后会覆盖当前本地配置并立即刷新界面。",
                    actionLabel = "导入",
                    onClick = { importLauncher.launch(arrayOf("application/json")) },
                ),
            )
        }

        FrameworkSettingsSection(title = "诊断") {
            FrameworkHomeInfoRow(
                label = "Root",
                value = state.rootReport.summary.ifBlank { "No host diagnostics" },
            )
            if (state.servicePing.isNotBlank()) {
                MaaHomeDivider()
                FrameworkHomeSupportText(
                    text = state.servicePing,
                    tone = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            state.rootReport.binaryProbes
                .filter { probe -> probe.exists || probe.executableByApp }
                .ifEmpty { state.rootReport.binaryProbes.take(3) }
                .take(4)
                .forEach { probe ->
                MaaHomeDivider()
                FrameworkHomeInfoRow(
                    label = probe.path,
                    value = when {
                        probe.executableByApp -> "可执行"
                        probe.exists -> "存在但不可执行"
                        else -> "未找到"
                    },
                    valueColor = if (probe.executableByApp) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
            }
        }
    }
}

@Composable
private fun ResourceRepositoryClearConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("清空 GitHub 资源") },
        text = {
            Text("会删除当前资源缓存和历史目录，下次更新会重新下载。")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("清空")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text("取消")
            }
        },
    )
}

@Composable
private fun StatusOverviewCard(
    state: MainUiState,
    visibleTasks: List<TaskDescriptor>,
) {
    SectionCard(
        title = "运行状态",
        subtitle = state.lastMessage.ifBlank { "Root runtime ready" },
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
        ) {
            StatusPill("Root 可用", state.rootReport.available)
            StatusPill("授权通过", state.rootReport.granted)
            StatusPill("服务在线", state.rootConnected)
            StatusPill("资源就绪", state.resourceRepository.available)
            if (state.busy) {
                StatusPill(
                    text = "处理中",
                    active = true,
                    accent = MaterialTheme.colorScheme.secondaryContainer,
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
        ) {
            MetricTile(label = "任务数", value = visibleTasks.size.toString())
            MetricTile(label = "已选", value = state.checkedTaskIds.size.toString())
            MetricTile(label = "预设数", value = state.catalog.presets.size.toString())
            MetricTile(label = "阶段", value = state.runtimeState.phase.displayName())
            MetricTile(label = "资源", value = state.selectedResourceId ?: "-")
        }

        if (state.servicePing.isNotBlank()) {
            Text(
                text = state.servicePing,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun QuickActionsCard(
    state: MainUiState,
    onConnect: () -> Unit,
    onPrepare: () -> Unit,
    onOpenGame: () -> Unit,
    onRunTask: () -> Unit,
    onRunPreset: () -> Unit,
    onStop: () -> Unit,
    onExport: () -> Unit,
    onToggleDisplayPower: () -> Unit,
) {
    SectionCard(
        title = "快捷操作",
        subtitle = "页面结构参考 MaaEnd，但动作仍然走当前 MaaFrameworkSession 运行链路",
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
        ) {
            ActionButton(
                label = if (state.rootConnected) "重新连接 Runtime" else "连接 Root / Runtime",
                enabled = !state.busy,
                onClick = onConnect,
            )
            ActionButton(
                label = "准备运行时",
                enabled = state.rootConnected && state.resourceRepository.available && !state.busy,
                outlined = true,
                onClick = onPrepare,
            )
            ActionButton(
                label = "窗口打开游戏",
                enabled = state.rootConnected && !state.busy,
                onClick = onOpenGame,
            )
            ActionButton(
                label = "开始任务",
                enabled = state.rootConnected &&
                    state.resourceRepository.available &&
                    !state.busy &&
                    (state.checkedTaskIds.isNotEmpty() || state.selectedTaskId != null),
                onClick = onRunTask,
            )
            ActionButton(
                label = "运行预设",
                enabled = state.rootConnected && state.resourceRepository.available && !state.busy && state.selectedPresetId != null,
                outlined = true,
                onClick = onRunPreset,
            )
            ActionButton(
                label = if (state.runtimeState.displayPowerOffActive) "恢复亮屏" else "息屏挂机",
                enabled = state.runtimeState.canToggleDisplayPower(state.rootConnected),
                outlined = true,
                onClick = onToggleDisplayPower,
            )
            ActionButton(
                label = "停止任务",
                enabled = state.rootConnected && state.runtimeState.canStopRun(),
                outlined = true,
                onClick = onStop,
            )
            ActionButton(
                label = "导出诊断包",
                enabled = state.rootConnected && !state.busy,
                outlined = true,
                onClick = onExport,
            )
        }
    }
}

@Composable
private fun ResourcePresetCard(
    state: MainUiState,
    onSelectResource: (String) -> Unit,
    onSelectPreset: (String) -> Unit,
) {
    SectionCard(
        title = "资源与预设",
        subtitle = "当前资源选择、预设入口和任务可见性都跟随这里的配置。",
    ) {
        Text(
            text = "资源包",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        if (state.catalog.resources.isEmpty()) {
            Text(
                text = "资源同步完成后会在这里显示可选渠道。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
            ) {
                state.catalog.resources.forEach { resource ->
                    FilterChip(
                        selected = resource.id == state.selectedResourceId,
                        onClick = { onSelectResource(resource.id) },
                        label = { Text(resource.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    )
                }
            }
        }

        Text(
            text = "预设",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        if (state.catalog.presets.isEmpty()) {
            Text(
                text = "当前项目没有暴露可用预设。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
            ) {
                state.catalog.presets.forEach { preset ->
                    FilterChip(
                        selected = preset.id == state.selectedPresetId,
                        onClick = { onSelectPreset(preset.id) },
                        label = {
                            Text(
                                text = "${preset.label} (${preset.taskIds.size})",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun RepositorySettingsCard(
    state: MainUiState,
    onRefreshResourceRepository: () -> Unit,
) {
    SectionCard(
        title = "GitHub 资源",
        subtitle = "资源现在只从 GitHub 仓库同步，不再回退 bundled assets。",
    ) {
        Text(
            text = resourceRepositorySummary(state),
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        resourceRepositoryLatestCommitText(state)?.let { latestCommit ->
            Text(
                text = "最新提交：$latestCommit",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        state.resourceRepositoryProgress?.let { progress ->
            Column(
                verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.xs),
            ) {
                LinearProgressIndicator(
                    progress = { progress.fraction },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = progress.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        state.resourceRepository.lastError?.takeIf { it.isNotBlank() }?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
        ActionButton(
            label = if (state.resourceRepositoryUpdating) "同步中..." else "同步 GitHub 资源",
            enabled = !state.resourceRepositoryUpdating,
            onClick = onRefreshResourceRepository,
        )
    }
}

@Composable
private fun ConfigTransferCard(
    onExport: () -> Unit,
    onImport: () -> Unit,
) {
    SectionCard(
        title = "配置文件",
        subtitle = "导出或导入任务勾选、资源、预设和参数配置。",
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
        ) {
            ActionButton(
                label = "导出配置",
                enabled = true,
                outlined = true,
                onClick = onExport,
            )
            ActionButton(
                label = "导入配置",
                enabled = true,
                outlined = true,
                onClick = onImport,
            )
        }
    }
}

@Composable
private fun SelectedTaskCard(task: TaskDescriptor) {
    SectionCard(
        title = "当前选择",
        subtitle = task.entry,
    ) {
        Text(
            text = task.label,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        if (task.description.isNotBlank()) {
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
        ) {
            if (task.repeatable) {
                StatusPill(text = "可重复 ${task.repeatCount} 次", active = true)
            }
            task.supportedResources.take(4).forEach { resourceId ->
                StatusPill(text = resourceId, active = false)
            }
        }
    }
}

@Composable
private fun HostDiagnosticsCard(
    state: MainUiState,
) {
    SectionCard(
        title = "Host Diagnostics",
        subtitle = state.rootReport.summary.ifBlank { "No host diagnostics" },
    ) {
        if (state.rootReport.binaryProbes.isEmpty()) {
            Text(
                text = "No root binary probes were reported.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.xs),
            ) {
                state.rootReport.binaryProbes
                    .filter { probe -> probe.exists || probe.executableByApp }
                    .ifEmpty { state.rootReport.binaryProbes.take(3) }
                    .take(4)
                    .forEach { probe ->
                        RootBinaryProbeLine(probe = probe)
                    }
            }
        }
    }
}

@Composable
private fun RootBinaryProbeLine(probe: RootBinaryProbe) {
    val status = when {
        probe.executableByApp -> "app can execute"
        probe.exists -> "exists but app cannot execute"
        else -> "not found"
    }
    Text(
        text = "${probe.path} · $status",
        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun TasksScreen(
    state: MainUiState,
    tasks: List<TaskDescriptor>,
    selectedTask: TaskDescriptor?,
    onSelectTask: (String) -> Unit,
    onToggleTaskChecked: (String, Boolean) -> Unit,
    onRunTask: () -> Unit,
    onStop: () -> Unit,
    onToggleDisplayPower: () -> Unit,
    onOverrideJsonChange: (String) -> Unit,
    onSwitchTaskOption: (String, String, String) -> Unit,
    onToggleTaskCheckboxOption: (String, String, String) -> Unit,
    onTaskInputValueChange: (String, String, String, String) -> Unit,
    isFullscreenPreview: Boolean,
    onExpandPreview: () -> Unit,
    previewContent: @Composable () -> Unit,
) {
    val visibleTaskOptions = remember(selectedTask, state.selectedResourceId) {
        selectedTask?.let {
            TaskOptionSupport.filterOptionsForResource(it.options, state.selectedResourceId)
        }.orEmpty()
    }
    val taskInputErrors = remember(
        visibleTaskOptions,
        selectedTask?.id,
        state.taskOptionSelectionsByTask,
        state.taskInputValuesByTask,
    ) {
        if (selectedTask == null) {
            emptyMap()
        } else {
            TaskOptionSupport.collectInputValidationErrors(
                options = visibleTaskOptions,
                selectedByOption = state.taskOptionSelectionsByTask[selectedTask.id].orEmpty(),
                inputValuesByOption = state.taskInputValuesByTask[selectedTask.id].orEmpty(),
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
    ) {
        FrameworkPreviewPanel(
            isFullscreenPreview = isFullscreenPreview,
            onExpandPreview = onExpandPreview,
            overlayText = if (!state.rootConnected) "等待 Root / Runtime 连接" else null,
            previewContent = previewContent,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
            verticalAlignment = Alignment.Top,
        ) {
            FrameworkTaskListPanel(
                tasks = tasks,
                selectedTaskId = state.selectedTaskId,
                checkedTaskIds = state.checkedTaskIds,
                onSelectTask = onSelectTask,
                onToggleTaskChecked = onToggleTaskChecked,
                modifier = Modifier.fillMaxHeight(),
                runningTaskId = state.runtimeState.currentTaskId,
            )
            FrameworkTaskDetailPanel(
                task = selectedTask,
                options = visibleTaskOptions,
                selectedCaseNamesByOption = selectedTask
                    ?.let { state.taskOptionSelectionsByTask[it.id].orEmpty() }
                    .orEmpty(),
                inputValuesByOption = selectedTask
                    ?.let { state.taskInputValuesByTask[it.id].orEmpty() }
                    .orEmpty(),
                inputErrorsByOption = taskInputErrors,
                onSwitchOption = onSwitchTaskOption,
                onToggleCheckboxOption = onToggleTaskCheckboxOption,
                onInputValueChange = onTaskInputValueChange,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                AdvancedOverrideCard(
                    value = state.overrideJson,
                    onValueChange = onOverrideJsonChange,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
        ) {
            Button(
                onClick = onRunTask,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner),
                enabled = state.rootConnected &&
                    state.resourceRepository.available &&
                    !state.busy &&
                    (state.checkedTaskIds.isNotEmpty() || state.selectedTaskId != null),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = "开始任务",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            OutlinedButton(
                onClick = onStop,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner),
                enabled = state.runtimeState.canStopRun(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(
                    text = "停止任务",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            OutlinedButton(
                onClick = onToggleDisplayPower,
                modifier = Modifier
                    .widthIn(min = 56.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner),
                enabled = state.runtimeState.canToggleDisplayPower(state.rootConnected),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
            ) {
                Text(
                    text = if (state.runtimeState.displayPowerOffActive) "恢复\n亮屏" else "息屏\n挂机",
                    style = MaterialTheme.typography.labelMedium.copy(lineHeight = 13.sp),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun PreviewCard(
    state: MainUiState,
    isFullscreenPreview: Boolean,
    onExpandPreview: () -> Unit,
    previewContent: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(DefaultDisplayConfig.ASPECT_RATIO)
            .clip(RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.card))
            .background(Color.Black),
    ) {
        if (!isFullscreenPreview) {
            previewContent()
            if (!state.rootConnected) {
                PreviewOverlayHint("等待 Root / Runtime 连接")
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.35f),
                            ),
                        ),
                    )
                    .clickable(onClick = onExpandPreview),
            )
        } else {
            Spacer(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun PreviewSurfaceHost(
    modifier: Modifier = Modifier,
    onPreviewSurfaceChanged: (Surface?) -> Unit,
) {
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            onPreviewSurfaceChanged(null)
        }
    }

    Box(
        modifier = modifier.background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.aspectRatio(DefaultDisplayConfig.ASPECT_RATIO),
        ) {
            AndroidView(
                factory = { context ->
                    SurfaceView(context).apply {
                        var lastSurface: Surface? = null
                        holder.setFormat(PixelFormat.RGBA_8888)
                        holder.addCallback(
                            object : SurfaceHolder.Callback {
                                override fun surfaceCreated(holder: SurfaceHolder) {
                                    scope.launch {
                                        delay(50)
                                        holder.setFixedSize(DefaultDisplayConfig.WIDTH, DefaultDisplayConfig.HEIGHT)
                                    }
                                }

                                override fun surfaceChanged(
                                    holder: SurfaceHolder,
                                    format: Int,
                                    width: Int,
                                    height: Int,
                                ) {
                                    if (
                                        width == DefaultDisplayConfig.WIDTH &&
                                        height == DefaultDisplayConfig.HEIGHT &&
                                        lastSurface !== holder.surface
                                    ) {
                                        lastSurface = holder.surface
                                        onPreviewSurfaceChanged(holder.surface)
                                    }
                                }

                                override fun surfaceDestroyed(holder: SurfaceHolder) {
                                    if (lastSurface != null) {
                                        lastSurface = null
                                        onPreviewSurfaceChanged(null)
                                    }
                                }
                            },
                        )
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun PreviewOverlayHint(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.38f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FullscreenPreviewOverlay(
    viewModel: MainViewModel,
    previewContent: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val fullscreenProgress = remember { Animatable(0f) }
    val activeTouches = remember { mutableMapOf<Int, PreviewPoint>() }
    val contactIdsByPointer = remember { mutableMapOf<Long, Int>() }
    val nextContactId = remember { intArrayOf(0) }

    DisposableEffect(activity) {
        val window = activity?.window
        val controller = window?.let {
            WindowCompat.getInsetsController(it, it.decorView)
        }
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        controller?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    DisposableEffect(activity) {
        val originalOrientation = activity?.requestedOrientation
        onDispose {
            if (activity != null && originalOrientation != null) {
                activity.requestedOrientation = originalOrientation
            }
        }
    }

    DisposableEffect(viewModel) {
        onDispose {
            activeTouches.toMap().forEach { (contactId, point) ->
                viewModel.onPreviewTouchUp(contactId, point.x, point.y)
            }
            activeTouches.clear()
            contactIdsByPointer.clear()
        }
    }

    LaunchedEffect(activity) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        fullscreenProgress.snapTo(0f)
        fullscreenProgress.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
    }

    BackHandler(onBack = onDismissRequest)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val changes = event.changes
                        if (changes.isEmpty()) {
                            continue
                        }

                        changes
                            .filter { !it.previousPressed && it.pressed }
                            .forEach { change ->
                                val point = mapViewToVirtualDisplay(
                                    viewX = change.position.x,
                                    viewY = change.position.y,
                                    viewWidth = size.width,
                                    viewHeight = size.height,
                                    bufferWidth = DefaultDisplayConfig.WIDTH,
                                    bufferHeight = DefaultDisplayConfig.HEIGHT,
                                    clampToBounds = false,
                                ) ?: return@forEach
                                val pointerToken = change.id.value
                                val contactId = contactIdsByPointer.getOrPut(pointerToken) { nextContactId[0]++ }
                                if (viewModel.onPreviewTouchDown(contactId, point.x, point.y)) {
                                    activeTouches[contactId] = point
                                } else {
                                    contactIdsByPointer.remove(pointerToken)
                                }
                            }

                        changes
                            .filter { it.previousPressed && it.pressed && it.position != it.previousPosition }
                            .forEach { change ->
                                val pointerToken = change.id.value
                                val contactId = contactIdsByPointer[pointerToken] ?: return@forEach
                                val point = mapViewToVirtualDisplay(
                                    viewX = change.position.x,
                                    viewY = change.position.y,
                                    viewWidth = size.width,
                                    viewHeight = size.height,
                                    bufferWidth = DefaultDisplayConfig.WIDTH,
                                    bufferHeight = DefaultDisplayConfig.HEIGHT,
                                    clampToBounds = true,
                                ) ?: return@forEach
                                activeTouches[contactId] = point
                                viewModel.onPreviewTouchMove(contactId, point.x, point.y)
                            }

                        changes
                            .filter { it.previousPressed && !it.pressed }
                            .forEach { change ->
                                val pointerToken = change.id.value
                                val contactId = contactIdsByPointer[pointerToken] ?: return@forEach
                                val point = mapViewToVirtualDisplay(
                                    viewX = change.position.x,
                                    viewY = change.position.y,
                                    viewWidth = size.width,
                                    viewHeight = size.height,
                                    bufferWidth = DefaultDisplayConfig.WIDTH,
                                    bufferHeight = DefaultDisplayConfig.HEIGHT,
                                    clampToBounds = true,
                                ) ?: activeTouches[contactId]
                                if (point != null) {
                                    viewModel.onPreviewTouchUp(contactId, point.x, point.y)
                                }
                                activeTouches.remove(contactId)
                                contactIdsByPointer.remove(pointerToken)
                            }

                        changes.forEach { it.consume() }
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = fullscreenProgress.value
                },
        ) {
            previewContent()
        }

        IconButton(
            onClick = onDismissRequest,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = MaaGf2DesignTokens.Spacing.sm, end = MaaGf2DesignTokens.Spacing.sm),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "关闭预览",
                tint = Color.White.copy(alpha = 0.78f),
            )
        }
    }
}

@Composable
private fun TaskListPanel(
    tasks: List<TaskDescriptor>,
    selectedTaskId: String?,
    checkedTaskIds: Set<String>,
    runningTaskId: String?,
    modifier: Modifier = Modifier,
    onSelectTask: (String) -> Unit,
    onToggleTaskChecked: (String, Boolean) -> Unit,
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .widthIn(min = 96.dp, max = 112.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (tasks.isEmpty()) {
            EmptyStateBlock(
                title = "暂无任务",
                description = "资源同步完成后，任务会显示在这里。",
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    tasks.forEach { task ->
                        TaskListItem(
                            task = task,
                            selected = task.id == selectedTaskId,
                            checked = task.id in checkedTaskIds,
                            running = task.id == runningTaskId,
                            onClick = { onSelectTask(task.id) },
                            onCheckedChange = { checked -> onToggleTaskChecked(task.id, checked) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskListItem(
    task: TaskDescriptor,
    selected: Boolean,
    checked: Boolean,
    running: Boolean,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        border = if (selected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        } else {
            null
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                Checkbox(
                    checked = checked || running,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer(
                            scaleX = 0.72f,
                            scaleY = 0.72f,
                        ),
                )
            }
            Text(
                text = compactTaskLabel(task),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp, lineHeight = 21.sp),
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TaskDetailPanel(
    state: MainUiState,
    selectedTask: TaskDescriptor?,
    modifier: Modifier = Modifier,
    onOverrideJsonChange: (String) -> Unit,
    onSwitchTaskOption: (String, String, String) -> Unit,
    onToggleTaskCheckboxOption: (String, String, String) -> Unit,
    onTaskInputValueChange: (String, String, String, String) -> Unit,
) {
    val visibleTaskOptions = remember(selectedTask, state.selectedResourceId) {
        selectedTask?.let {
            TaskOptionSupport.filterOptionsForResource(it.options, state.selectedResourceId)
        }.orEmpty()
    }
    val taskInputErrors = remember(
        visibleTaskOptions,
        selectedTask?.id,
        state.taskOptionSelectionsByTask,
        state.taskInputValuesByTask,
    ) {
        if (selectedTask == null) {
            emptyMap()
        } else {
            TaskOptionSupport.collectInputValidationErrors(
                options = visibleTaskOptions,
                selectedByOption = state.taskOptionSelectionsByTask[selectedTask.id].orEmpty(),
                inputValuesByOption = state.taskInputValuesByTask[selectedTask.id].orEmpty(),
            )
        }
    }

    SectionCard(
        title = "任务配置",
        subtitle = null,
        modifier = modifier.fillMaxWidth(),
        fillHeight = true,
        contentPadding = PaddingValues(
            start = 2.dp,
            top = MaaGf2DesignTokens.Spacing.sm,
            end = 4.dp,
            bottom = MaaGf2DesignTokens.Spacing.sm,
        ),
    ) {
        if (selectedTask == null) {
            EmptyStateBlock(
                title = "还没有选中任务",
                description = "先在左侧点一个任务，再到这里查看执行参数。",
            )
        } else if (visibleTaskOptions.isEmpty()) {
            EmptyStateBlock(
                title = "这个任务暂无额外参数",
                description = "可以直接勾选并开始执行。",
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
                ) {
                    OptionConfigCard(
                        ownerId = selectedTask.id,
                        title = selectedTask.label,
                        description = selectedTask.description,
                        options = visibleTaskOptions,
                        selectedCaseNamesByOption = state.taskOptionSelectionsByTask[selectedTask.id].orEmpty(),
                        inputValuesByOption = state.taskInputValuesByTask[selectedTask.id].orEmpty(),
                        inputErrorsByOption = taskInputErrors,
                        onSwitchOption = onSwitchTaskOption,
                        onToggleCheckboxOption = onToggleTaskCheckboxOption,
                        onInputValueChange = onTaskInputValueChange,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    AdvancedOverrideCard(
                        value = state.overrideJson,
                        onValueChange = onOverrideJsonChange,
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionConfigCard(
    ownerId: String,
    title: String,
    description: String,
    options: List<TaskOptionDescriptor>,
    selectedCaseNamesByOption: Map<String, Set<String>>,
    inputValuesByOption: Map<String, Map<String, String>>,
    inputErrorsByOption: Map<String, Map<String, String>>,
    onSwitchOption: (String, String, String) -> Unit,
    onToggleCheckboxOption: (String, String, String) -> Unit,
    onInputValueChange: (String, String, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            options.forEach { option ->
                TaskOptionBlock(
                    ownerId = ownerId,
                    option = option,
                    selectedCaseNamesByOption = selectedCaseNamesByOption,
                    inputValuesByOption = inputValuesByOption,
                    inputErrorsByOption = inputErrorsByOption,
                    onSwitchOption = onSwitchOption,
                    onToggleCheckboxOption = onToggleCheckboxOption,
                    onInputValueChange = onInputValueChange,
                )
            }
        }
    }
}

@Composable
private fun TaskOptionBlock(
    ownerId: String,
    option: TaskOptionDescriptor,
    selectedCaseNamesByOption: Map<String, Set<String>>,
    inputValuesByOption: Map<String, Map<String, String>>,
    inputErrorsByOption: Map<String, Map<String, String>>,
    onSwitchOption: (String, String, String) -> Unit,
    onToggleCheckboxOption: (String, String, String) -> Unit,
    onInputValueChange: (String, String, String, String) -> Unit,
    compact: Boolean = false,
    nested: Boolean = false,
) {
    val selectedCaseNames = selectedCaseNamesByOption[option.id].takeUnless { it.isNullOrEmpty() }
        ?: TaskOptionSupport.defaultSelectionForOption(option)
    val inputValues = inputValuesByOption[option.id].orEmpty()
    val contentPadding = if (compact) 6.dp else MaaGf2DesignTokens.Spacing.sm
    val blockSpacing = if (compact) 4.dp else MaaGf2DesignTokens.Spacing.xs
    val titleStyle = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleSmall

    Surface(
        shape = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner),
        color = if (nested) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.16f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        },
        border = if (nested) {
            null
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f))
        },
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(blockSpacing),
        ) {
            Text(
                text = option.label,
                style = titleStyle,
                fontWeight = FontWeight.SemiBold,
            )
            if (option.description.isNotBlank()) {
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            when (option.type) {
                TaskOptionType.Switch,
                TaskOptionType.Select -> {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        option.cases.forEach { optionCase ->
                            val caseLabel = optionCase.label.ifBlank { optionCase.name }.trim()
                            if (caseLabel.isNotBlank()) {
                                OptionChip(
                                    label = caseLabel,
                                    selected = optionCase.name in selectedCaseNames,
                                    onClick = { onSwitchOption(ownerId, option.id, optionCase.name) },
                                )
                            }
                        }
                    }
                    option.cases.forEach { optionCase ->
                        if (optionCase.name in selectedCaseNames && optionCase.nestedOptions.isNotEmpty()) {
                            NestedTaskOptions(
                                ownerId = ownerId,
                                options = optionCase.nestedOptions,
                                selectedCaseNamesByOption = selectedCaseNamesByOption,
                                inputValuesByOption = inputValuesByOption,
                                inputErrorsByOption = inputErrorsByOption,
                                onSwitchOption = onSwitchOption,
                                onToggleCheckboxOption = onToggleCheckboxOption,
                                onInputValueChange = onInputValueChange,
                            )
                        }
                    }
                }

                TaskOptionType.Checkbox -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.xs),
                    ) {
                        option.cases.forEach { optionCase ->
                            val caseLabel = optionCase.label.ifBlank { optionCase.name }.trim()
                            if (caseLabel.isNotBlank()) {
                                OptionCheckboxRow(
                                    label = caseLabel,
                                    checked = optionCase.name in selectedCaseNames,
                                    onCheckedChange = { onToggleCheckboxOption(ownerId, option.id, optionCase.name) },
                                )
                            }
                        }
                    }
                    option.cases.forEach { optionCase ->
                        if (optionCase.name in selectedCaseNames && optionCase.nestedOptions.isNotEmpty()) {
                            NestedTaskOptions(
                                ownerId = ownerId,
                                options = optionCase.nestedOptions,
                                selectedCaseNamesByOption = selectedCaseNamesByOption,
                                inputValuesByOption = inputValuesByOption,
                                inputErrorsByOption = inputErrorsByOption,
                                onSwitchOption = onSwitchOption,
                                onToggleCheckboxOption = onToggleCheckboxOption,
                                onInputValueChange = onInputValueChange,
                            )
                        }
                    }
                }

                TaskOptionType.Input -> {
                    option.inputs.forEach { input ->
                        val error = inputErrorsByOption[option.id]?.get(input.name)
                        OutlinedTextField(
                            value = inputValues[input.name] ?: input.defaultValue,
                            onValueChange = { value ->
                                onInputValueChange(ownerId, option.id, input.name, value)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 44.dp),
                            label = { Text(input.label) },
                            isError = error != null,
                            supportingText = when {
                                error != null -> {
                                    { Text(error) }
                                }
                                input.description.isNotBlank() -> {
                                    {
                                        Text(
                                            text = input.description,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                                else -> null
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NestedTaskOptions(
    ownerId: String,
    options: List<TaskOptionDescriptor>,
    selectedCaseNamesByOption: Map<String, Set<String>>,
    inputValuesByOption: Map<String, Map<String, String>>,
    inputErrorsByOption: Map<String, Map<String, String>>,
    onSwitchOption: (String, String, String) -> Unit,
    onToggleCheckboxOption: (String, String, String) -> Unit,
    onInputValueChange: (String, String, String, String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.outlineVariant),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.28f),
                    shape = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner),
                )
                .padding(start = 4.dp, top = 1.dp, end = 0.dp, bottom = 1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            options.forEach { nestedOption ->
                TaskOptionBlock(
                    ownerId = ownerId,
                    option = nestedOption,
                    selectedCaseNamesByOption = selectedCaseNamesByOption,
                    inputValuesByOption = inputValuesByOption,
                    inputErrorsByOption = inputErrorsByOption,
                    onSwitchOption = onSwitchOption,
                    onToggleCheckboxOption = onToggleCheckboxOption,
                    onInputValueChange = onInputValueChange,
                    compact = true,
                    nested = true,
                )
            }
        }
    }
}

@Composable
private fun OptionCheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer(
                        scaleX = 0.72f,
                        scaleY = 0.72f,
                    ),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun OptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        shape = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

@Composable
private fun AdvancedOverrideCard(
    value: String,
    onValueChange: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)),
    ) {
        Column(
            modifier = Modifier.padding(MaaGf2DesignTokens.Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.xs),
        ) {
            Text(
                text = "高级 Override",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "如果需要临时覆盖 pipeline，可在这里直接传 JSON。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                label = { Text("Override JSON") },
                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            )
        }
    }
}

@Composable
private fun LogsScreen(
    state: MainUiState,
) {
    FrameworkRuntimeLogsPanel(
        lines = state.displayLogs,
        subtitle = state.runtimeState.lastMessage.ifBlank { state.lastMessage },
        modifier = Modifier.fillMaxSize(),
        metrics = listOf(
            FrameworkLogMetric(label = "阶段", value = state.runtimeState.phase.displayName()),
            FrameworkLogMetric(label = "当前任务", value = state.runtimeState.currentTaskId ?: "-"),
            FrameworkLogMetric(label = "连接", value = if (state.rootConnected) "已连接" else "未连接"),
        ),
        detailLines = buildList {
            state.runtimeState.lastDiagnosticsPath?.takeIf { it.isNotBlank() }?.let {
                add("Diagnostics: $it")
            }
            state.runtimeState.lastFailure?.screenshotPath?.takeIf { it.isNotBlank() }?.let {
                add("Last failure screenshot: $it")
            }
        },
        emptyTitle = "暂无日志",
        emptyDescription = "开始任务后，这里会显示运行日志。",
    )
}

@Composable
private fun AppBottomBar(
    activeTab: MaaGf2Tab,
    onTabSelected: (MaaGf2Tab) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp,
    ) {
        Column {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = MaaGf2DesignTokens.Spacing.xl, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MaaGf2Tab.entries.forEach { tab ->
                    val selected = activeTab == tab
                    val contentColor = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Column(
                        modifier = Modifier
                            .clickable { onTabSelected(tab) }
                            .heightIn(min = 48.dp)
                            .padding(horizontal = MaaGf2DesignTokens.Spacing.sm, vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Icon(
                            imageVector = tab.icon(),
                            contentDescription = tab.label(),
                            tint = contentColor,
                        )
                        Text(
                            text = tab.label(),
                            style = MaterialTheme.typography.labelMedium,
                            color = contentColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    fillHeight: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(MaaGf2DesignTokens.Spacing.sm),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = if (fillHeight) modifier.fillMaxSize() else modifier,
        shape = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.card),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)),
    ) {
        Column(
            modifier = (if (fillHeight) Modifier.fillMaxSize() else Modifier.fillMaxWidth())
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.sm),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            content()
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    active: Boolean,
    accent: Color? = null,
) {
    val backgroundColor = accent ?: if (active) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
    }
    val contentColor = if (accent != null) {
        MaterialTheme.colorScheme.onSurface
    } else if (active) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.pill),
        color = backgroundColor,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f),
    ) {
        Column(
            modifier = Modifier.padding(MaaGf2DesignTokens.Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    enabled: Boolean,
    outlined: Boolean = false,
    onClick: () -> Unit,
) {
    if (outlined) {
        OutlinedButton(onClick = onClick, enabled = enabled) {
            Text(label)
        }
    } else {
        Button(onClick = onClick, enabled = enabled) {
            Text(label)
        }
    }
}

private fun resourceRepositorySummary(state: MainUiState): String {
    val config = state.manifest.githubResourceRepository
    return state.resourceRepository.summaryText(
        repositoryLabel = config?.let { "${it.owner}/${it.repo}" },
        branchLabel = state.resourceRepository.branch ?: config?.branch ?: "main",
        updating = state.resourceRepositoryUpdating,
    )
}

private fun resourceRepositoryLatestCommitText(state: MainUiState): String? {
    return state.resourceRepository.latestCommitTimeText()
}

private fun homeServiceStatusLabel(state: MainUiState): String {
    return when {
        state.rootConnected -> "已连接"
        state.busy -> "连接中"
        state.rootReport.granted -> "已授权，待握手"
        state.rootReport.available -> "等待授权"
        else -> "未检测到 Root"
    }
}

private fun homeServiceTone(state: MainUiState): FrameworkHomeTone {
    return when {
        state.rootConnected -> FrameworkHomeTone.Positive
        state.busy -> FrameworkHomeTone.Warning
        state.rootReport.granted || state.rootReport.available -> FrameworkHomeTone.Warning
        else -> FrameworkHomeTone.Error
    }
}

private fun MaaGf2Tab.title(): String {
    return when (this) {
        MaaGf2Tab.Home -> "主页"
        MaaGf2Tab.Tasks -> "任务"
        MaaGf2Tab.Settings -> "设置"
        MaaGf2Tab.Logs -> "日志"
    }
}

private fun MaaGf2Tab.subtitle(
    state: MainUiState,
    visibleTasks: List<TaskDescriptor>,
): String {
    return when (this) {
        MaaGf2Tab.Home -> if (state.rootConnected) {
            "首页会优先展示设备、服务状态和快捷操作，Root / Runtime 已经连接完成。"
        } else {
            "启动后会静默尝试获取 Root 并连接 Runtime，失败时再手动重试。"
        }
        MaaGf2Tab.Tasks -> "当前共 ${visibleTasks.size} 个任务。"
        MaaGf2Tab.Settings -> "资源同步、渠道选择和预设入口都集中在这里。"
        MaaGf2Tab.Logs -> "当前日志 ${state.displayLogs.size} 条。"
    }
}

private fun MaaGf2Tab.label(): String {
    return when (this) {
        MaaGf2Tab.Home -> "主页"
        MaaGf2Tab.Tasks -> "任务"
        MaaGf2Tab.Settings -> "设置"
        MaaGf2Tab.Logs -> "日志"
    }
}

private fun MaaGf2Tab.icon(): ImageVector = when (this) {
    MaaGf2Tab.Home -> Icons.Default.Home
    MaaGf2Tab.Tasks -> Icons.AutoMirrored.Filled.ViewList
    MaaGf2Tab.Settings -> Icons.Default.Settings
    MaaGf2Tab.Logs -> Icons.AutoMirrored.Filled.Article
}

private fun String.logLevelLabel(): String {
    return when (this) {
        MaaLogLevels.ERROR -> "错误"
        MaaLogLevels.WARN -> "警告"
        MaaLogLevels.INFO -> "信息"
        MaaLogLevels.DEBUG -> "调试"
        else -> this
    }
}

private data class PreviewPoint(
    val x: Int,
    val y: Int,
)

private fun mapViewToVirtualDisplay(
    viewX: Float,
    viewY: Float,
    viewWidth: Int,
    viewHeight: Int,
    bufferWidth: Int,
    bufferHeight: Int,
    clampToBounds: Boolean,
): PreviewPoint? {
    val bufferW = bufferWidth.toFloat()
    val bufferH = bufferHeight.toFloat()
    val scale = minOf(viewWidth / bufferW, viewHeight / bufferH)
    val offsetX = (viewWidth - bufferW * scale) / 2f
    val offsetY = (viewHeight - bufferH * scale) / 2f
    var mappedX = (viewX - offsetX) / scale
    var mappedY = (viewY - offsetY) / scale

    if (!clampToBounds && (mappedX < 0f || mappedX >= bufferW || mappedY < 0f || mappedY >= bufferH)) {
        return null
    }

    mappedX = mappedX.coerceIn(0f, bufferW - 1f)
    mappedY = mappedY.coerceIn(0f, bufferH - 1f)
    return PreviewPoint(mappedX.toInt(), mappedY.toInt())
}

private fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) {
            return current
        }
        current = current.baseContext
    }
    return null
}

@Composable
private fun EmptyStateBlock(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MaaGf2DesignTokens.Spacing.xxl)
            .clip(RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f))
            .padding(MaaGf2DesignTokens.Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaaGf2DesignTokens.Spacing.xs),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun compactTaskLabel(task: TaskDescriptor): String {
    return when (task.id) {
        "AndroidOpenGame" -> "打开游戏"
        "DailyRewards" -> "日常奖励"
        "DijiangRewards" -> "基建任务"
        "CreditShoppingN2" -> "信用购物"
        "VisitFriends" -> "拜访好友"
        "SellProduct" -> "售卖产品"
        "AutoEssence" -> "基质刷取"
        "EnvironmentMonitoring" -> "环境监测"
        "Crafting" -> "工艺制造"
        "WeaponUpgrade" -> "武器强化"
        "AutoUseSpMedication" -> "体力用药"
        "SimpleProductionBatchStart" -> "批量生产"
        "ReceiveProdManual" -> "领取手册"
        "BakerEntry" -> "会话嘴替"
        "ReadAllWiki" -> "阅读图鉴"
        "DeliveryJobs" -> "转交委托"
        "GearAssembly" -> "装备制造"
        else -> task.label
            .replace(Regex("[^\\p{L}\\p{N}\\p{IsHan}]"), "")
            .take(4)
            .ifBlank { task.id.take(4) }
    }
}
