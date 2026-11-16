/**
 * Contains code (messages and regex) from OdinClient 1.8.9
 * odtheking's BSD-3 Clause License applies to this file.
 *
 * BSD 3-Clause License
 *
 * Copyright (c) 2023-2025, odtheking
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package foo.starred.ionic.module.impl.general

import foo.starred.ionic.annotation.impl.IonicModuleScope
import foo.starred.ionic.annotation.impl.LoadableIonicModule
import foo.starred.ionic.config.dsl.impl.category.ConfigCategory
import foo.starred.ionic.event.impl.MessageEvent
import foo.starred.ionic.module.base.Module
import foo.starred.kontext.abstraction.chat.command.MinecraftCommands

@LoadableIonicModule
@IonicModuleScope(skyblock = true)
object EscrowFix : Module(
    "Escrow fix",
    "Fixes escrow bug closing auction house or bazaar menus",
    ConfigCategory.GENERAL
) {
    private val bazaar = Regex("Escrow refunded \\d+ coins for Bazaar Instant Buy Submit!")
    private val auction = listOf("There was an error with the auction house! (AUCTION_EXPIRED_OR_NOT_FOUND)", "There was an error with the auction house! (INVALID_BID)", "Claiming BIN auction...", "Visit the Auction House to collect your item!")

    init {
        on<MessageEvent.Chat.Receive> {
            when {
                bazaar.matches(stripped) -> MinecraftCommands.server("/bz")
                stripped in auction -> MinecraftCommands.server("/ah")
            }
        }
    }
}
