package data.utils

import domain.model.Profile
import kotlinx.serialization.encodeToString

interface ProfileStorage {

    fun save(profile: Profile)

    fun load() : Profile?

    fun clear()

}

class ProfileStorageImpl(
    private val contentProvider: ContentProvider,
) : ProfileStorage {

    private val json = JsonProvider.json

    override fun save(profile: Profile) {
        try {
            val profileContent = json.encodeToString<Profile>(profile)
            contentProvider.saveContent(profileContent)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun load(): Profile? {
        return try {
            val fileContent = contentProvider.provideContent()
            val profile = json.decodeFromString<Profile>(fileContent)
            profile
        } catch (e: Throwable) {
            // e.printStackTrace()
            null
        }
    }

    override fun clear() {
        try {
            val profileContent = "{}"
            contentProvider.saveContent(profileContent)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

}