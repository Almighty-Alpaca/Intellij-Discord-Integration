/*
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gamesdk.impl

import gamesdk.api.Core
import gamesdk.api.DiscordObjectResult
import gamesdk.api.DiscordResult
import gamesdk.api.events.CurrentUserUpdateEvent
import gamesdk.api.events.RelationshipRefreshEvent
import gamesdk.api.events.RelationshipUpdateEvent
import gamesdk.api.managers.*
import gamesdk.api.types.DiscordClientId
import gamesdk.api.types.DiscordCreateFlags
import gamesdk.api.types.DiscordLogLevel
import gamesdk.impl.events.*
import gamesdk.impl.managers.NativeActivityManagerImpl
import gamesdk.impl.managers.NativeRelationshipManagerImpl
import gamesdk.impl.managers.NativeUserManagerImpl
import gamesdk.impl.types.NativeDiscordCreateFlags
import gamesdk.impl.types.toNativeDiscordCreateFlags

/**
 * **WARNING**: Do not make the properties in this class internal!
 * The name mangling done by the Kotlin compiler will break the native code.
 * Only way around this is using [`@JvmName`][JvmName] on all getters.
 */
internal class Events {
    val currentUserUpdates: NativeNotifiableEventBus<CurrentUserUpdateEvent, NativeCurrentUserUpdateEvent> =
        NativeNotifiableEventBus.create(NativeCurrentUserUpdateEvent::toCurrentUserUpdateEvent)

    val relationshipRefreshes: NativeNotifiableEventBus<RelationshipRefreshEvent, NativeRelationshipRefreshEvent> =
        NativeNotifiableEventBus.create(NativeRelationshipRefreshEvent::toRelationshipRefreshEvent)
    val relationshipUpdates: NativeNotifiableEventBus<RelationshipUpdateEvent, NativeRelationshipUpdateEvent> =
        NativeNotifiableEventBus.create(NativeRelationshipUpdateEvent::toRelationshipUpdateEvent)
}

internal class NativeCoreImpl private constructor(pointer: NativePointer, internal val events: Events) : NativeObjectImpl.Closeable(pointer), Core {
    override val applicationManager: ApplicationManager
        get() = TODO("Not yet implemented")

    override val userManager: UserManager
            by lazy { NativeUserManagerImpl(this) }

    override val imageManager: ImageManager
        get() = TODO("Not yet implemented")

    override val activityManager: ActivityManager
            by lazy { NativeActivityManagerImpl(this) }

    override val relationshipManager: RelationshipManager
            by lazy { NativeRelationshipManagerImpl(this) }

    override val lobbyManager: LobbyManager
        get() = TODO("Not yet implemented")
    override val networkManager: NetworkManager
        get() = TODO("Not yet implemented")
    override val overlayManager: OverlayManager
        get() = TODO("Not yet implemented")
    override val storageManager: StorageManager
        get() = TODO("Not yet implemented")
    override val storeManager: StoreManager
        get() = TODO("Not yet implemented")
    override val voiceManager: VoiceManager
        get() = TODO("Not yet implemented")
    override val achievementManager: AchievementManager
        get() = TODO("Not yet implemented")

    override fun runCallbacks(): DiscordResult = native { pointer -> runCallbacks(pointer).toDiscordResult() }

//    override val logMessages
//        get() = events.logMessages

    override fun setLogHook(minLevel: DiscordLogLevel, hook: (level: DiscordLogLevel, message: String) -> Unit): Unit =
        TODO("Not yet implemented")

    override val destructor: NativeMethod<Unit> = Native::destroy

    companion object : NativeObjectCreator() {
        internal fun create(clientId: DiscordClientId, createFlags: DiscordCreateFlags): DiscordObjectResult<Core> {
            val events = Events()
            return native { create(clientId, createFlags.toNativeDiscordCreateFlags(), events) }
                .toDiscordObjectResult { pointer -> NativeCoreImpl(pointer, events) }
        }
    }
}

private external fun Native.create(clientId: DiscordClientId, createFlags: NativeDiscordCreateFlags, events: Events): NativeDiscordObjectResult<NativePointer>

private external fun Native.destroy(pointer: NativePointer)

private external fun Native.runCallbacks(pointer: NativePointer): Int
