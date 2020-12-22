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

package gamesdk.impl.events

import gamesdk.api.events.Event
import gamesdk.api.events.EventBus
import gamesdk.api.events.Subscription
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CopyOnWriteArrayList

internal data class SubscriptionImpl<T>(val listener: suspend (T) -> Boolean) : Subscription

internal interface NotifiableEventBus<T : Event> : EventBus<T> {
    fun notify(event: T)
}

private abstract class NotifiableEventBusImpl<T : Event> : NotifiableEventBus<T> {
    private val listeners = CopyOnWriteArrayList<SubscriptionImpl<T>>()

    final override fun subscribeUntil(listener: suspend (T) -> Boolean): Subscription =
        SubscriptionImpl(listener).also(listeners::add)

    final override fun unsubscribe(subscription: Subscription) {
        listeners.remove(subscription)
    }

    final override fun notify(event: T) = runBlocking {
        listeners.forEach { subscription ->
            if (subscription.listener(event)) {
                listeners.remove(subscription)
            }
        }
    }
}

internal interface NativeNotifiableEventBus<T : Event, TN : NativeEvent> : EventBus<T> {
    fun notify(nativeEvent: TN)

    companion object {
        fun <T : Event, TN : NativeEvent> create(converter: (TN) -> T): NativeNotifiableEventBus<T, TN> = NativeNotifiableEventBusImpl(converter)
    }
}

private class NativeNotifiableEventBusImpl<T : Event, TN : NativeEvent>(val converter: (TN) -> T) : NotifiableEventBusImpl<T>(), NativeNotifiableEventBus<T, TN> {
    override fun notify(nativeEvent: TN) {
        println("EVENT")
    }
}
