package foo.starred.ionic.api.command

import foo.starred.ionic.Ionic
import foo.starred.kommand.IKommand
import foo.starred.kommand.scopes.KommandBuilderScope
import foo.starred.kommand.scopes.KommandCommandScope
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

object IonicCommandSource : IKommand<FabricClientCommandSource> {
    override val loader: KommandCommandScope<FabricClientCommandSource> = KommandCommandScope()

    init {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            loader.register(dispatcher)
        }
    }
}

fun command(block: KommandBuilderScope<FabricClientCommandSource>.() -> Unit) {
    IonicCommandSource.command(Ionic.Data.id, block)
}
