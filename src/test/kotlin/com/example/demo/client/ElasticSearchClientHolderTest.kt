package com.example.demo.client

import com.example.demo.IntegrationTestContextConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest





/**
 * User: Ani Margaryan
 * Company: SFL LLC
 * Date: 02/12/2019
 * Time: 17:44
 */
@SpringJUnitConfig(
    IntegrationTestContextConfiguration::class,
    initializers = [(ConfigFileApplicationContextInitializer::class)]
)
open class ElasticSearchClientHolderTest(
    @Autowired val elasticSearchClientHolder: ElasticSearchClientHolder
) {

    @Test
    fun testGetClusterIndicesMetaData() {

        val request = CreateIndexRequest("twitter1234")
        val createIndexResponse = elasticSearchClientHolder.getClient().admin().indices().create(request).actionGet()

    }
}