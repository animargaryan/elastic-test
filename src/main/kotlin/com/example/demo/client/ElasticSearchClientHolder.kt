package com.example.demo.client

import org.elasticsearch.client.Client

/**
 * User: Ani Margaryan
 * Company: SFL LLC
 * Date: 01/29/2019
 * Time: 21:01
 */
interface ElasticSearchClientHolder {

    /**
     * Builds and returns elastic search client
     *
     * @return client
     */
    fun getClient(): Client
}