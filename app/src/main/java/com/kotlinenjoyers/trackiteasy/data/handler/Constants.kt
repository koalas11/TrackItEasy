package com.kotlinenjoyers.trackiteasy.data.handler

import com.kotlinenjoyers.trackiteasy.repository.parsers.AmazonParserRepository
import com.kotlinenjoyers.trackiteasy.repository.parsers.PosteItalianeParserRepository
import com.kotlinenjoyers.trackiteasy.repository.parsers.TNTParserRepository
import com.kotlinenjoyers.trackiteasy.repository.retrievers.AmazonRetrieverRepository
import com.kotlinenjoyers.trackiteasy.repository.retrievers.PosteItalianeRetrieverRepository
import com.kotlinenjoyers.trackiteasy.repository.retrievers.TNTRetrieverRepository

object Constants {
    const val AMAZON_ID = "Amazon"
    const val POSTE_ITALIANE_ID = "Poste Italiane"
    const val TNT_ID = "TNT"

    val parsersExtractorsList = mutableListOf(
        ParcelInfo(AMAZON_ID, "1.0", AmazonParserRepository, AmazonRetrieverRepository),
        ParcelInfo(POSTE_ITALIANE_ID, "1.0", PosteItalianeParserRepository, PosteItalianeRetrieverRepository),
        ParcelInfo(TNT_ID, "1.0", TNTParserRepository, TNTRetrieverRepository)
    )
}