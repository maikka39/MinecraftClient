package mixins

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Mutable
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(PlayerMoveC2SPacket::class)
interface PlayerMoveC2SPacketAccessor {
    @set:Mutable
    @get:Accessor("y")
    @set:Accessor("y")
    var y: Double
}
