package com.kotlinenjoyers.trackiteasy.data.handler

import com.kotlinenjoyers.trackiteasy.repository.parsers.interfaces.ParserRepository
import com.kotlinenjoyers.trackiteasy.repository.retrievers.RetrieverRepository

data class ParcelInfo(val id: String, val version: String, val parserRepository: ParserRepository, val retrieverRepository: RetrieverRepository)