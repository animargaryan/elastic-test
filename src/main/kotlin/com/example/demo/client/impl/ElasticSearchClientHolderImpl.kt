package com.example.demo.client.impl

import com.example.demo.client.ElasticSearchClientHolder
import org.elasticsearch.client.Client
import org.elasticsearch.common.logging.LogConfigurator
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.InternalSettingsPreparer
import org.elasticsearch.node.Node
import org.elasticsearch.transport.Netty4Plugin
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File

/**
 * User: Ani Margaryan
 * Company: SFL LLC
 * Date: 02/12/2019
 * Time: 17:41
 */
@Component
class ElasticSearchClientHolderImpl : ElasticSearchClientHolder {


    @Volatile private lateinit var node: Node

    @Volatile private lateinit var dataFolder: File

    override fun getClient() = nodeClient

    //region Properties
    private val nodeClient: Client by lazy {
        LOGGER.debug("Initializing ElasticSearch local node client")
        // Create temporary data directory
        try {
            dataFolder = createTemporaryDataFolder()
        } catch (ex: Exception) {
            LOGGER.error(
                "Error occurred while creating temporary directory for ElasticSearch. Continuing without temp directory.",
                ex
            )
        }
        val settingsBuilder = Settings.builder()
            .put(CLIENT_SETTINGS_CLUSTER_NAME, "testCluster")
            .put("transport.type", "netty4")
            .put("http.type", "netty4")
            .put("http.enabled", "true")
            .put("path.home", ELASTIC_SEARCH_DATA_FOLDER)

        LogConfigurator.configureWithoutConfig(settingsBuilder.build())
        // Create new node
        node = ElasticSearchNode(settingsBuilder.build(), listOf(Netty4Plugin::class.java))
        LOGGER.debug("Starting ElasticSearch local node")
        node.start()
        LOGGER.debug("Successfully started ElasticSearch local node, acquiring client")
        // Initialize ElasticSearch client
        val localNodeClient = node.client()
        LOGGER.debug("Successfully created ElasticSearch local node and its client - {}")
        localNodeClient
    }
    //endregion

    private fun createTemporaryDataFolder(): File {
        val tempFile = File.createTempFile("elastic", "tempFile")
        LOGGER.debug(
            "Created temporary file whose parent will act as parent directory for ElasticSearch data folder. File - {}",
            tempFile
        )
        val elasticDataFolder = File(tempFile.parentFile, ELASTIC_SEARCH_DATA_FOLDER + "_" + System.currentTimeMillis())
        if (!elasticDataFolder.exists() && !elasticDataFolder.mkdirs()) {
            val message =
                "Was not able to create temporary ElasticSearch data directory using path - " + elasticDataFolder.absolutePath
            LOGGER.error(message)
            throw RuntimeException(message)
        }
        LOGGER.debug("Successfully created ElasticSearch temporary data folder - {}", elasticDataFolder.absolutePath)
        return elasticDataFolder
    }

    private class ElasticSearchNode(preparedSettings: Settings, classpathPlugins: List<Class<Netty4Plugin>>) :
        Node(InternalSettingsPreparer.prepareEnvironment(preparedSettings, null), classpathPlugins)

    //region Companion object
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ElasticSearchClientHolderImpl::class.java)

        private val ELASTIC_SEARCH_DATA_FOLDER = "elasticsearchdata"

        private val NODE_SETTINGS_ELASTIC_SEARCH_DATA_FOLDER_PATH = "path.data"

        private val CLIENT_SETTINGS_CLUSTER_NAME = "cluster.name"
    }
    //endregion
}