package app.emirtemindarov.p1.mvvm.fileinfowindow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import app.emirtemindarov.p1.mvvm.data.FileInfo

class FileInfoWindowViewModel : ViewModel() {

    var selectedTab by mutableIntStateOf(0)
        private set

    var navigationStack by mutableStateOf<List<FileInfo>>(emptyList())
        private set

    var inspectedNodeId by mutableStateOf<String?>(null)
        private set

    private var graphRootUri: String? = null

    fun syncGraphRoot(rootUri: String) {
        if (graphRootUri != rootUri) {
            graphRootUri = rootUri
            reset()
        }
    }

    fun selectTab(index: Int) {
        selectedTab = index
    }

    fun openFileInfo(fileInfo: FileInfo) {
        navigationStack = navigationStack + fileInfo
    }

    fun goBack() {
        if (navigationStack.isNotEmpty()) {
            navigationStack = navigationStack.dropLast(1)
        }
    }

    fun setInspectedNode(nodeId: String) {
        inspectedNodeId = nodeId
    }

    fun inspectEdges(nodeId: String) {
        inspectedNodeId = nodeId
        selectedTab = 2
    }

    fun clearInspectedNode() {
        inspectedNodeId = null
    }

    fun reset() {
        selectedTab = 0
        navigationStack = emptyList()
        inspectedNodeId = null
    }
}