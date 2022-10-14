package mixins

import modules.cheats.SpeedMine
import net.minecraft.block.BlockState
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(PlayerEntity::class)
abstract class PlayerEntityMixin(entityType: EntityType<out LivingEntity>?, world: World?) :
    LivingEntity(entityType, world) {

    @Inject(at = [At("RETURN")], method = ["getBlockBreakingSpeed"], cancellable = true)
    private fun getBlockBreakingSpeed(block: BlockState?, cir: CallbackInfoReturnable<Float>) {
        if (!SpeedMine.enabled) return

        cir.returnValue = cir.returnValue * SpeedMine.speedModifier.value.toFloat()
    }
}