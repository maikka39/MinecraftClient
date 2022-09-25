package mixins

import mixinterfaces.IPlayerMoveC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique

@Mixin(PlayerMoveC2SPacket::class)
abstract class PlayerMoveC2SPacketMixin : IPlayerMoveC2SPacket {
    @Unique
    override var isMine = false;
}