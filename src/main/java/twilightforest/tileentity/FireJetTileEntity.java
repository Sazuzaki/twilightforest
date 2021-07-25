package twilightforest.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import twilightforest.TFSounds;
import twilightforest.block.FireJetBlock;
import twilightforest.block.TFBlocks;
import twilightforest.enums.FireJetVariant;
import twilightforest.client.particle.TFParticleType;
import twilightforest.util.TFDamageSources;

import java.util.List;

public class FireJetTileEntity extends BlockEntity {

	private int counter = 0;

	public FireJetTileEntity(BlockPos pos, BlockState state) {
		super(TFTileEntities.FLAME_JET.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, FireJetTileEntity te) {
		if (state.getBlock() == TFBlocks.fire_jet.get() || state.getBlock() == TFBlocks.encased_fire_jet.get()) {
			switch (state.getValue(FireJetBlock.STATE)) {
			case POPPING: tickPopping(level, pos, state, te); break;
			case FLAME: tickFlame(level, pos, state, te); break;
			}
		}
	}

	private static void tickPopping(Level level, BlockPos pos, BlockState state, FireJetTileEntity te) {
		if (++te.counter >= 80) {
			te.counter = 0;
			// turn to flame
			if (!level.isClientSide) {
				if (state.getBlock() == TFBlocks.fire_jet.get() || state.getBlock() == TFBlocks.encased_fire_jet.get()) {
					level.setBlockAndUpdate(pos, state.setValue(FireJetBlock.STATE, FireJetVariant.FLAME));
				} else {
					level.removeBlock(pos, false);
				}
			}
		} else {
			if (te.counter % 20 == 0) {
				for (int i = 0; i < 8; i++)
				{
					level.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
				}
				level.playSound(null, pos, TFSounds.JET_POP, SoundSource.BLOCKS, 0.2F + level.random.nextFloat() * 0.2F, 0.9F + level.random.nextFloat() * 0.15F);
			}
		}
	}

	private static void tickFlame(Level level, BlockPos pos, BlockState state, FireJetTileEntity te) {
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();

		if (++te.counter > 60) {
			te.counter = 0;
			// idle again
			if (!level.isClientSide) {
				if (state.getBlock() == TFBlocks.fire_jet.get() || state.getBlock() == TFBlocks.encased_fire_jet.get()) {
					level.setBlockAndUpdate(pos, state.setValue(FireJetBlock.STATE, FireJetVariant.IDLE));
				} else {
					level.removeBlock(pos, false);
				}
			}
		}

		if (level.isClientSide) {
			if (te.counter % 2 == 0) {
				level.addParticle(ParticleTypes.LARGE_SMOKE, x + 0.5, y + 1.0, z + 0.5, 0.0D, 0.0D, 0.0D);
				level.addParticle(TFParticleType.LARGE_FLAME.get(), x + 0.5, y + 1.0, z + 0.5, 0.0D, 0.5D, 0.0D);
				level.addParticle(TFParticleType.LARGE_FLAME.get(), x - 0.5, y + 1.0, z + 0.5, 0.05D, 0.5D, 0.0D);
				level.addParticle(TFParticleType.LARGE_FLAME.get(), x + 0.5, y + 1.0, z - 0.5, 0.0D, 0.5D, 0.05D);
				level.addParticle(TFParticleType.LARGE_FLAME.get(), x + 1.5, y + 1.0, z + 0.5, -0.05D, 0.5D, 0.0D);
				level.addParticle(TFParticleType.LARGE_FLAME.get(), x + 0.5, y + 1.0, z + 1.5, 0.0D, 0.5D, -0.05D);
			}

			// sounds
			if (te.counter % 4 == 0) {
				level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, TFSounds.JET_ACTIVE, SoundSource.BLOCKS, 1.0F + level.random.nextFloat(), level.random.nextFloat() * 0.7F + 0.3F, false);

			} else if (te.counter == 1) {
				level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, TFSounds.JET_START, SoundSource.BLOCKS, 1.0F + level.random.nextFloat(), level.random.nextFloat() * 0.7F + 0.3F, false);
			}
		}

		// actual fire effects
		if (!level.isClientSide) {
			if (te.counter % 5 == 0) {
				// find entities in the area of effect
				List<Entity> entitiesInRange = level.getEntitiesOfClass(Entity.class,
						new AABB(pos.offset(-2, 0, -2), pos.offset(2, 4, 2)));
				// fire!
				for (Entity entity : entitiesInRange) {
					if (!entity.fireImmune()) {
						entity.hurt(TFDamageSources.FIRE_JET, 2);
						entity.setSecondsOnFire(15);
					}
				}
			}
		}
	}
}
